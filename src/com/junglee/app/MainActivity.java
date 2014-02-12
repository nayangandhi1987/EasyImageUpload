package com.junglee.app;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.jungleeclick.R;
import com.helpshift.Helpshift;
import com.junglee.commonlib.logging.Logger;
import com.junglee.commonlib.network.NetworkApiTest;
import com.junglee.commonlib.network.NetworkResponse;
import com.junglee.commonlib.network.NetworkResponseListener;
import com.junglee.commonlib.utils.StringUtility;
import com.junglee.commonlib.utils.ThreadUtility;
import com.junglee.utils.UIUtility;

@SuppressLint("NewApi")
public class MainActivity extends JungleeActionbarActivity
        implements SearchView.OnQueryTextListener, NavigationDrawerFragment.NavigationDrawerCallbacks, OnActionExpandListener
        , httpCallBack {
	
	private static String IDENTIFIER = "MAIN_ACTIVITY";
	private String UI_STATE = null;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
	private NavigationDrawerFragment mNavigationDrawerFragment;

    private String url;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private static final String URL_TO_LOAD = "http://www.junglee.com";
    private static final String SUGGESTION_URL = "http://completion.amazon.co.uk/search/complete?q=QQQ&method=completion&search-alias=aps&client=amazon-search-ui&mkt=44561&x=";
    private static final String SUBMIT_URL = "http://www.junglee.com/aw/search/junglee/ref=nav_sb_noss?url=search-alias%3Daps&field-keywords=";
    private ListView mListView;
    private MenuItem mSearchItem;
    private SearchView mSearchView;
    private EditText searchbox;
    private WebView webview1;
    private ArrayList<String> suggestionTexts;
    private ArrayList<String> suggestionLinks;
    private ArrayAdapter<String> adapter;
    private static final int MAX_SUGGESTIONS_TO_DISPLAY = 6;
    private boolean alreadyRunningWebCall = false;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        HashMap config = new HashMap();
        config.put("enableInAppNotification", true);
        Helpshift.install(getApplication(), 
        		"90a52fec54f12903c7ad3a4d05c2c357", 
        		"nggandhi.helpshift.com", 
        		"nggandhi_platform_20140128025526433-9d915c5b8d767b1",
        		config);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        mListView = (ListView) findViewById(R.id.suggestionList);
        suggestionTexts = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, suggestionTexts);
        mListView.setAdapter(adapter);
        mListView.setVisibility(View.GONE);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                String sUrl = SUBMIT_URL + item;
                PlaceholderFragment.wv.loadUrl(sUrl);
                mListView.setVisibility(View.GONE);
            }
        });
    }

	@Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

	@Override
	protected String getScreenId() {
		return IDENTIFIER;
	}

	@Override
	protected String getUiState() {
		return UI_STATE;
	}
	
	private void updateUiState(String uiState) {
		if(StringUtility.isPopulated(uiState)) {
			boolean stateChanged = !uiState.equalsIgnoreCase(UI_STATE);
			UI_STATE = uiState;

			if(stateChanged) {
				onUiStateChanged();
			}
		}
	}

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

//	public void restoreActionBar() {
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//        actionBar.setDisplayShowTitleEnabled(true);
//        actionBar.setTitle(PlaceholderFragment.text);
//    }

    @Override
    public void onSuccess(String result) {
        threadMsg(result);
        this.alreadyRunningWebCall = false;
    }

    // Define the Handler that receives messages from the thread and update the
    // progress
    private final Handler handler = new Handler() {

        public void handleMessage(Message msg) {

            String aResponse = msg.getData().getString("message");

            if ((null != aResponse)) {

                JSONArray jsonArray;
                try {
                    jsonArray = new JSONArray(aResponse);
                    if (jsonArray.length() > 1) {
                        JSONArray suggestions = jsonArray.getJSONArray(1);
                        for (int i = 0; i < suggestions.length()
                                && i < MAX_SUGGESTIONS_TO_DISPLAY; i++) {
                            suggestionTexts.add(suggestions.getString(i));
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                adapter.notifyDataSetChanged();
            }

        }
    };

    private void threadMsg(String msg) {
        if (!msg.equals(null) && !msg.equals("")) {
            Message msgObj = handler.obtainMessage();
            Bundle b = new Bundle();
            b.putString("message", msg);
            msgObj.setData(b);
            handler.sendMessage(msgObj);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            updateUiState("WITHOUT_NAV_DRAWER");
        } else {
        	updateUiState("WITH_NAV_DRAWER");
        }
    	
		createSearchMenu(menu);
		createLocationMenu(menu);
		createAccountMenu(menu);
		createOverflowMenu(menu);
		return true;
    }
    
    private void createSearchMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.item_search, menu);
//    	
//    	mSearchItem = menu.findItem(R.id.action_search);
//    	mSearchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
//    	    	
//    	mSearchView = new SearchView(getBaseContext());
//    	mSearchView.setEnabled(true);
//    	mSearchView.setOnQueryTextListener(this);    	
//    	mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean queryTextFocused) {
//                if(!queryTextFocused) {
//                    mSearchView.setQuery("", false);
//                    mSearchView.onActionViewCollapsed();
//                }
//            }
//        });
//    	mSearchItem.setActionView(mSearchView);
    	
    	getMenuInflater().inflate(R.menu.junglee_search, menu);
    	mSearchItem = menu.findItem(R.id.junglee_search);
    	MenuItemCompat.setOnActionExpandListener(mSearchItem, this);
	}
    private void createLocationMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.item_location, menu);
    	
    	MenuItem locationItem = menu.findItem(R.id.action_location);
    	locationItem.setTitle("Change Location");
    	locationItem.setTitleCondensed("Bangalore");
    	locationItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
	}
    private void createAccountMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.item_account, menu);
    	
    	MenuItem accountItem = menu.findItem(R.id.action_account);
    	accountItem.setTitle("Sign Out");
    	accountItem.setTitleCondensed("Nayan");
    	accountItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	}
    private void createOverflowMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.main, menu);
    	
//    	MenuItem localAdItem = menu.findItem(R.id.action_local_ad);
//    	localAdItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
//    	
//    	MenuItem faqsItem = menu.findItem(R.id.action_faqs);
//    	faqsItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
//    	
//    	MenuItem reportIssueItem = menu.findItem(R.id.action_report_issue);
//    	reportIssueItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
    	
    	MenuItem helpItem = menu.findItem(R.id.action_help);
    	helpItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
    	MenuItem termsItem = menu.findItem(R.id.action_terms);
    	termsItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
    	MenuItem testItem = menu.findItem(R.id.action_test_api);
    	testItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	int id =  item.getItemId();
    	if(id == R.id.junglee_search) {
    		MenuItemCompat.setActionView(item, R.layout.actionbar_search);
    		
			searchbox = (EditText) MenuItemCompat.getActionView(item)
					.findViewById(R.id.menu_search_field);
			searchbox.setHint("Type to search...");
			searchbox.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
			searchbox.setTextColor(Color.BLACK);
			searchbox.setHintTextColor(Color.GRAY);
			
			MenuItemCompat.expandActionView(mSearchItem);
    	} else if(id == R.id.action_location) {
    		UIUtility.showToastMsgShort(this, "Change Location Clicked!");
    	} else if(id == R.id.action_account) {
    		UIUtility.errorBox(this
    				, "Sign Out"
    				, "Are you sure you want to sign out of your account?"
    				, new DialogInterface.OnClickListener() {
		    			@Override
		    			public void onClick(DialogInterface dialog, int which) {
		    				UIUtility.showToastMsgShort(MainActivity.this, "Requested sign Out!");
		    			}
		    		}
		    		, new DialogInterface.OnClickListener() {
		    			@Override
		    			public void onClick(DialogInterface dialog, int which) {
		    				UIUtility.showToastMsgShort(MainActivity.this, "Cancelled sign Out!");
		    			}
		    		});
    	} else if(id == R.id.action_help) {
    		
    	} else if(id == R.id.action_terms) {
    		
    	} else if(id == R.id.action_test_api) {
    		/*
    		int TEST_GET_SYNC = 1;
    		int TEST_GET_ASYNC = 2;
    		int TEST_POST_SYNC = 3;
    		int TEST_POST_ASYNC = 4;    		
    		int TEST_PUT_SYNC = 5;
    		int TEST_PUT_ASYNC = 6;
    		int TEST_DELETE_SYNC = 7;
    		int TEST_DELETE_ASYNC = 8;
    		
    		int testApi = TEST_DELETE_ASYNC;
    		
    		if(testApi == TEST_GET_SYNC) {
    			NetworkApiTest.testParseGetRequestSync();  			
    		} else if(testApi == TEST_GET_ASYNC) {
    			NetworkApiTest.testParseGetRequestAsync();
    		} else if(testApi == TEST_POST_SYNC) {
    			NetworkApiTest.testParsePostRequestSync();    			
    		} else if(testApi == TEST_POST_ASYNC) {
    			NetworkApiTest.testParsePostRequestAsync();
    		} else if(testApi == TEST_PUT_SYNC) {
    			NetworkApiTest.testParsePutRequestSync();
    		} else if(testApi == TEST_PUT_ASYNC) {
    			NetworkApiTest.testParsePutRequestAsync();
    		} else if(testApi == TEST_DELETE_SYNC) {
    			NetworkApiTest.testParseDeleteRequestSync();
    		} else if(testApi == TEST_DELETE_ASYNC) {
    			NetworkApiTest.testParseDeleteRequestAsync();
    		}
    		*/
    		UIUtility.showToastMsgShort(this, "Unused!");
    	}
    	
//    	else if(id == R.id.action_local_ad) {
//    		Intent i = new Intent(getApplicationContext(), JungleeClickActivity.class);
//    		startActivity(i);
//    	} 
//    	else if(id == R.id.action_faqs) {
//    		Helpshift.showFAQs(this);
//    	} 
//    	else if(id == R.id.action_report_issue) {
//    		Helpshift.showConversation(this);
//    	}

        return super.onOptionsItemSelected(item);
    }
    
//    This won't work if mSearchView is added in code!
//    @Override
//    public void onBackPressed() {
//        if (mSearchView.isShown()) {
//        	collapseSearchView();
//        } else{
//            super.onBackPressed();
//        }
//    }
//    private void collapseSearchView() {
//    	mSearchView.setQuery("", false);
//    	mSearchView.onActionViewCollapsed();
//    }

    protected boolean isAlwaysExpanded() {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String userText) {
        String sUrl = SUBMIT_URL + userText;
        webview1.loadUrl(sUrl);
        mListView.setVisibility(View.GONE);
        return false;
    }


    private void updateSuggestions(final String userText) {
        String responseString = "";
        String sUrl = SUGGESTION_URL.replaceFirst("QQQ", userText);
        asyncHttpRequestTpe paramToUse = new asyncHttpRequestTpe();
        paramToUse.urlToCall = sUrl;
        paramToUse.callBackToUse = this;
        asyncHttpGet req = new asyncHttpGet();
        req.execute(paramToUse);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        suggestionTexts.clear();
        if (newText.length() > 3) {
            if (!alreadyRunningWebCall) {
                alreadyRunningWebCall = true;
                updateSuggestions(newText);
                if (mListView.getVisibility() == View.GONE) {
                    mListView.setVisibility(View.VISIBLE);
                }
            }
        } else if (mListView.getVisibility() == View.VISIBLE) {
            mListView.setVisibility(View.GONE);
        }

        adapter.notifyDataSetChanged();

        return false;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public static String url = "http://junglee.com";
        public static String text = "Home";

        public boolean isExit = true;

        public static WebView wv;

        @Override
        public void onPrepareOptionsMenu(Menu menu) {
            super.onPrepareOptionsMenu(menu);
//            MenuItem searchItem = menu.findItem(R.id.action_search);
//            searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
//            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            WebView wv = (WebView) rootView.findViewById(R.id.webView);
            wv.getSettings().setJavaScriptEnabled(true);
            wv.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url.contains("junglee.com")) return false;
                    return true;
                }
            });
            wv.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        WebView webView = (WebView) v;

                        switch (keyCode) {
                            case KeyEvent.KEYCODE_BACK:
                                if (webView.canGoBack()) {
                                    webView.goBack();
                                    return true;
                                }
                        }
                    }
                    return false;
                }
            });

            //wv.loadUrl("http://shrankur.desktop.amazon.com:9075/marketplace-override?marketplaceId=A3M22N3OY5KY7Q");
            wv.loadUrl(this.url);
            this.isExit = false;
            this.wv = wv;
            return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.main, menu);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

	@Override
	public boolean onMenuItemActionExpand(MenuItem item) {
		searchbox.post(new Runnable() {
			@Override
			public void run() {
				searchbox.requestFocus();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(searchbox, 0);
			}
		});
		return true;
	}

	@Override
	public boolean onMenuItemActionCollapse(MenuItem item) {	
		searchbox.setText("");
		InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(searchbox.getWindowToken(), 0);
		return true;
	}

}
