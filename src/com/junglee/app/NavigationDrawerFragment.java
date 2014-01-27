package com.junglee.app;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.example.jungleeclick.R;


/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
@SuppressLint("NewApi")
public class NavigationDrawerFragment extends JungleeFragment {
	
	private static String IDENTIFIER = "NAVIGATION_DRAWER_FRAGMENT";
	private String UI_STATE = null;

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ExpandableListView mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
        Log.i("JungleeCLick", "Fragment ViewCreated");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mDrawerListView = (ExpandableListView) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        //mWebView = (WebView) rootView.findViewById(R.id.webView);//inflater.inflate(R.id.webView, container, false);
         /* mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        }); */

        String jsonData = "[{\"type\":\"t1\",\"title\":\"Mobiles & Electronics\",\"children\":[{\"type\":\"t2\",\"title\":\"Mobile Phones\",\"link\":\"/aw/search/junglee/ref=nav_sb_noss_m1_1?url=node%3D803073031&field-keywords=\"},{\"type\":\"t2\",\"title\":\"Computers & Tablets\",\"link\":\"/aw/search/junglee/ref=nav_sb_noss_m1_2?url=search-alias%3Dcomputers&field-keywords=\"},{\"type\":\"t2\",\"title\":\"Kindle\",\"link\":\"/aw/search/junglee/ref=nav_sb_noss_m1_3?url=node%3D1320152031&field-keywords=\"},{\"type\":\"t2\",\"title\":\"TV, Video & Audio\",\"link\":\"/aw/search/junglee/ref=nav_sb_noss_m1_4?url=node%3D802980031&field-keywords=\"},{\"type\":\"t2\",\"title\":\"Cameras\",\"link\":\"/aw/search/junglee/ref=nav_sb_noss_m1_5?url=node%3D802718031&field-keywords=\"},{\"type\":\"t2\",\"title\":\"Video Games\",\"link\":\"/aw/search/junglee/ref=nav_sb_noss_m1_6?url=search-alias%3Dvideogames&field-keywords=\"}]},{\"type\":\"t1\",\"title\":\"Clothing, Shoes & Watches\",\"children\":[{\"type\":\"t2\",\"title\":\"Clothing\",\"link\":\"/aw/search/junglee/ref=nav_sb_noss_m2_1?url=search-alias%3Dapparel&field-keywords=\"},{\"type\":\"t2\",\"title\":\"Shoes\",\"link\":\"/aw/search/junglee/ref=nav_sb_noss_m2_2?url=node%3D805169031&field-keywords=\"},{\"type\":\"t2\",\"title\":\"Watches\",\"link\":\"/aw/search/junglee/ref=nav_sb_noss_m2_3?url=search-alias%3Dwatches&field-keywords=\"},{\"type\":\"t2\",\"title\":\"Jewellery\",\"link\":\"/aw/search/junglee/ref=nav_sb_noss_m2_4?url=search-alias%3Djewelry&field-keywords=\"},{\"type\":\"t2\",\"title\":\"Men's Accessories\",\"link\":\"/aw/search/junglee/ref=nav_sb_noss_m2_5?url=node%3D792227031&field-keywords=\"},{\"type\":\"t2\",\"title\":\"Women's Accessories\",\"link\":\"/aw/search/junglee/ref=nav_sb_noss_m2_6?url=node%3D792559031&field-keywords=\"}]},{\"type\":\"t1\",\"title\":\"Books, Movies & Music\",\"children\":[{\"type\":\"t2\",\"title\":\"Books\",\"link\":\"/aw/search/junglee/ref=nav_sb_noss_m3_1?url=search-alias%3Dstripbooks&field-keywords=\"},{\"type\":\"t2\",\"title\":\"Movies & TV\",\"link\":\"/aw/search/junglee/ref=nav_sb_noss_m3_2?url=search-alias%3Ddvd&field-keywords=\"},{\"type\":\"t2\",\"title\":\"Music\",\"link\":\"/aw/search/junglee/ref=nav_sb_noss_m3_3?url=search-alias%3Dpopular&field-keywords=\"}]},{\"type\":\"t1\",\"title\":\"Kitchen & Home\",\"children\":[{\"type\":\"t2\",\"title\":\"Kitchen Appliances\",\"link\":\"/aw/search/junglee/ref=nav_sb_noss_m4_1?url=node%3D804087031&field-keywords=\"},{\"type\":\"t2\",\"title\":\"Home Appliances\",\"link\":\"/aw/search/junglee/ref=nav_sb_noss_m4_2?url=node%3D804305031&field-keywords=\"},{\"type\":\"t2\",\"title\":\"Cookware & Bakeware\",\"link\":\"/aw/search/junglee/ref=nav_sb_noss_m4_3?url=node%3D804057031&field-keywords=\"},{\"type\":\"t2\",\"title\":\"Furnishing\",\"link\":\"/aw/search/junglee/ref=nav_sb_noss_m4_4?url=node%3D836523031&field-keywords=\"},{\"type\":\"t2\",\"title\":\"Furniture\",\"link\":\"/aw/search/junglee/ref=nav_sb_noss_m4_5?url=node%3D836590031&field-keywords=\"}]},{\"type\":\"t1\",\"title\":\"Toys, Sports & Baby\",\"children\":[{\"type\":\"t2\",\"title\":\"Toys & Games\",\"link\":\"/aw/search/junglee/ref=nav_sb_noss_m5_1?url=search-alias%3Dtoys&field-keywords=\"},{\"type\":\"t2\",\"title\":\"Sports & Leisure\",\"link\":\"/aw/search/junglee/ref=nav_sb_noss_m5_2?url=search-alias%3Dsporting&field-keywords=\"},{\"type\":\"t2\",\"title\":\"Baby Products\",\"link\":\"/aw/search/junglee/ref=nav_sb_noss_m5_3?url=node%3D783364031&field-keywords=\"}]},{\"type\":\"t1\",\"title\":\"Health & Beauty\",\"children\":[{\"type\":\"t2\",\"title\":\"Health & Personal Care\",\"link\":\"/aw/search/junglee/ref=nav_sb_noss_m6_1?url=search-alias%3Dhpc&field-keywords=\"},{\"type\":\"t2\",\"title\":\"Beauty\",\"link\":\"/aw/search/junglee/ref=nav_sb_noss_m6_2?url=search-alias%3Dbeauty&field-keywords=\"}]},{\"type\":\"t1\",\"title\":\"More Categories\",\"children\":[{\"type\":\"t2\",\"title\":\"Automotive\",\"link\":\"/aw/search/junglee/ref=nav_sb_noss_m7_1?url=search-alias%3Dautomotive&field-keywords=\"},{\"type\":\"t2\",\"title\":\"Garden\",\"link\":\"/aw/search/junglee/ref=nav_sb_noss_m7_2?url=search-alias%3Dgarden&field-keywords=\"},{\"type\":\"t2\",\"title\":\"Grocery\",\"link\":\"/aw/search/junglee/ref=nav_sb_noss_m7_3?url=node%3D1367150031&field-keywords=\"},{\"type\":\"t2\",\"title\":\"Musical Instruments\",\"link\":\"/aw/search/junglee/ref=nav_sb_noss_m7_4?url=node%3D1367148031&field-keywords=\"},{\"type\":\"t2\",\"title\":\"Office Supplies\",\"link\":\"/aw/search/junglee/ref=nav_sb_noss_m7_5?url=node%3D1367149031&field-keywords=\"},{\"type\":\"t2\",\"title\":\"School Supplies\",\"link\":\"/aw/search/junglee/ref=nav_sb_noss_m7_6?url=node%3D806764031&field-keywords=\"},{\"type\":\"t2\",\"title\":\"Pet Supplies\",\"link\":\"/aw/search/junglee/ref=nav_sb_noss_m7_7?url=search-alias%3Dpets&field-keywords=\"},{\"type\":\"t2\",\"title\":\"Home Improvement\",\"link\":\"/aw/search/junglee/ref=nav_sb_noss_m7_8?url=search-alias%3Dhi&field-keywords=\"}]}]";
        ArrayList<String> groupList = new ArrayList<String>();
        groupList.add("Home");
        Map<String, List<String>> groupCollection = new LinkedHashMap<String, List<String>>();
        Map<String, String> urlMap = new LinkedHashMap<String, String>();
        groupCollection.put("Home", new ArrayList<String>());
        try{

            JSONArray jData = new JSONArray(jsonData);
            for ( int i =0 ; i < jData.length(); i++)
            {
                JSONObject menu = jData.getJSONObject(i);
                JSONArray submenu = menu.getJSONArray("children");
                String menu_title = menu.getString("title");
                groupList.add(menu_title);
                List<String> submenu_list = new ArrayList<String>();
                for ( int j =0 ; j < submenu.length(); j++)
                {
                    String submenu_title = submenu.getJSONObject(j).getString("title");
                    submenu_list.add(submenu_title);
                    urlMap.put(submenu_title, submenu.getJSONObject(j).getString("link"));
                }
                groupCollection.put(menu_title, submenu_list);
            }
        } catch (Exception e){
            // blah
        }
        final NavigationListAdaptor expListAdapter = new NavigationListAdaptor(this, groupList, groupCollection, urlMap);

        mDrawerListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long l) {
                MainActivity.PlaceholderFragment.url = "http://junglee.com" + expListAdapter.getUrl(i, i2);
                MainActivity.PlaceholderFragment.text = "" + expListAdapter.getChild(i, i2);
                selectItem((int)l);
                return true;
            }
        });
        mDrawerListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                if (i == 0)
                {
                    MainActivity.PlaceholderFragment.url = "http://junglee.com";
                    MainActivity.PlaceholderFragment.text = "Home";
                    selectItem((int)l);
                    return true;
                }
                return false;
            }
        });

        mDrawerListView.setAdapter(expListAdapter);/* new ArrayAdapter<String>(
                getActionBar().getThemedContext(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                new String[]{
                        getString(R.string.title_section1),
                        getString(R.string.title_section2),
                        getString(R.string.title_section3),
                }));*/
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        
        return mDrawerListView;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
	public void onResume() {
		super.onResume();
	}

	@Override
	protected String getScreenId() {
		return IDENTIFIER;
	}

	@Override
	protected String getUiState() {
		return UI_STATE;
	}

	private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.main, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }



        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }
}
