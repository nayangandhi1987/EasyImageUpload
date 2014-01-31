package com.junglee.init;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.junglee.commonlib.utils.FileSystemUtility;
import com.junglee.commonlib.utils.StringUtility;
import com.junglee.data.IntentData;
import com.junglee.utils.UIUtility;

public class FeatureHelpScreensHandler {
	private static FeatureHelpScreensHandler instance = null;
	
	private Handler handler = new Handler();

	Map<String, JSONObject> helpScreenParamsDataMap = new HashMap<String, JSONObject>();

	private boolean active;
	
	private enum UI_OBJ_TYPE {
		VIEW,
		FRAGMENT,
		INDEX
	}
	
	protected FeatureHelpScreensHandler() {
		// Exists only to defeat instantiation.
	}	
	public static FeatureHelpScreensHandler getInstance() {
		if(instance == null) {
			instance = new FeatureHelpScreensHandler();
		}
		
		return instance;
	}
	
	public void initData(Context c) {
		parsePreloadedJson(c);
	}
	
	public JSONObject getHelpScreenData(String scrIdWithState) {
		if(StringUtility.isPopulated(scrIdWithState) && helpScreenParamsDataMap.containsKey(scrIdWithState)) {
			return helpScreenParamsDataMap.get(scrIdWithState);
		}
		
		return null;
	}
	
	public void checkForHelpScreen(final String scrId, final String uiState, final Context c) {
		if(active) return;
		
		final JSONObject helpJson = getHelpScreenData(generateScreeIdWithState(scrId, uiState));
		if(helpJson != null) {
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					invokeHelpScreen(scrId, uiState, helpJson, c);
				}
			}, 10);
			
		}
	}
	
	
	
	public void killedHelpScreen(String helpId) {
		active = false;
	}
	
	private synchronized void invokeHelpScreen(final String scrId, final String uiState, final JSONObject helpJson, final Context c) {
		if(active) return;
		
		try {
			String KEY_HELP_VERSION = generateScreeIdWithState(scrId, uiState)+"_HELP_VERSION";
			int helpVersion = helpJson.getInt("HELP_VERSION");
			SharedPreferences prefs = c.getSharedPreferences("HELP_SCREENS", Context.MODE_PRIVATE);
			int lastHelpVersion = prefs.getInt(KEY_HELP_VERSION, 0);
			if(helpVersion > lastHelpVersion) {
				boolean isDependencyDone = isDependencyResolved(helpJson.getJSONArray("DEPENDENCY"), c);
				if(!isDependencyDone) {
					return;
				}
				
				JSONArray controlsJson = helpJson.getJSONArray("CONTROLS");
				List<HelpScreenUIControlParams> controls = new ArrayList<HelpScreenUIControlParams>();
				for(int idx = 0; idx < controlsJson.length(); ++idx) {
					JSONObject controlJson = controlsJson.getJSONObject(idx);
					HelpScreenUIControlParams control = new HelpScreenUIControlParams(controlJson);
					control.viewRect = getViewLocationOnScreen((Activity) c, controlJson.getString("VIEW_ID"));
					if(control.viewRect != null) {
						controls.add(control);
					} else {
						Log.i("JungleeClick", "View not found: "+controlJson.getString("VIEW_ID"));
					}
				}

				if(controls.size() > 0) {
					int delay = 0;
					if(helpJson.has("DELAY")) {
						delay = helpJson.getInt("DELAY");
					}
					
					final String helpId = getHelpId(scrId, helpVersion);
					IntentData.getInstance().setHelpScreenData(controls, helpId);
					
					Handler h = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {							
							final Intent i = new Intent(c.getApplicationContext(), FeatureHelpScreenActivity.class);
							i.putExtra("HELP_ID", helpId);
							c.startActivity(i);
						}
					}, delay);
					
					active = true;
				}
				
				SharedPreferences.Editor editor = prefs.edit();
				editor.putInt(KEY_HELP_VERSION, helpVersion);
				editor.commit();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isDependencyResolved(JSONArray jsonArray, Context c) {
		if(jsonArray == null || jsonArray.length() == 0) {
			return true;
		}
		
		for(int i = 0; i < jsonArray.length(); ++i) {
			JSONObject dependencyJson = null;
			String scrId = null;
			int version = 0;
			try {
				dependencyJson = jsonArray.getJSONObject(i);
				scrId = dependencyJson.getString("SCREEN_ID");
				version = dependencyJson.getInt("VERSION");
				String KEY_HELP_VERSION = scrId+"_"+"HELP_VERSION";
				
				SharedPreferences prefs = c.getSharedPreferences("HELP_SCREENS", Context.MODE_PRIVATE);
				int screenVersion = prefs.getInt(KEY_HELP_VERSION, 0);
				
				if(screenVersion < version) {
					return false;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}		
		}
		
		return true;
	}
	private void parsePreloadedJson(Context c) {
		String help_json = FileSystemUtility.readAssetFileContent("HelpScreenJson", c);

		if (help_json != null) {
			try {
				JSONObject helpScreensObj = new JSONObject(help_json);
				JSONArray screens = helpScreensObj.getJSONArray("SCREENS");
				for (int i = 0; i < screens.length(); ++i) {
					JSONObject screen = screens.getJSONObject(i);
					helpScreenParamsDataMap.put(screen.getString("SCREEN_ID"), screen.getJSONObject("DATA"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private String generateScreeIdWithState(String scrId, String uiState) {
		String state = StringUtility.isBlank(uiState)?"":("_"+uiState);
		return scrId+state;
	}
	
	private Rect getViewLocationOnScreen(Activity activity, String viewName) {
		if(viewName.equalsIgnoreCase("actionbar_home")) {
			int ACTION_BAR_HEIGHT = UIUtility.getActionbarHeight(activity);
			return new Rect(0,0,ACTION_BAR_HEIGHT,ACTION_BAR_HEIGHT);
		} else if(viewName.equalsIgnoreCase("actionbar_overflow")) {
			int ACTION_BAR_HEIGHT = UIUtility.getActionbarHeight(activity);
			int SCREEN_WIDTH = UIUtility.getScreenWidth(activity);
			return new Rect(SCREEN_WIDTH-ACTION_BAR_HEIGHT,0,SCREEN_WIDTH,ACTION_BAR_HEIGHT);
		}
		
		
		if(activity != null) {
			if(StringUtility.isPopulated(viewName)) {
				UI_OBJ_TYPE objType = UI_OBJ_TYPE.VIEW;
				View view = null;
				String[] id_parts = viewName.split("##");
				for(int i = 0; i < id_parts.length; ++i) {
					String id_part = id_parts[i];
					int childIdx = -1;
					
					if(id_part.startsWith("I:")) {
						id_part = StringUtility.chopFromStart(id_part, 2);
						childIdx = Integer.parseInt(id_part);	
						objType = UI_OBJ_TYPE.INDEX;
					} else if(id_part.startsWith("F:")) {
						id_part = StringUtility.chopFromStart(id_part, 2);
						objType = UI_OBJ_TYPE.FRAGMENT;
					} else if(id_part.startsWith("V:")) {
						id_part = StringUtility.chopFromStart(id_part, 2);
						objType = UI_OBJ_TYPE.VIEW;
					}

					if(objType == UI_OBJ_TYPE.INDEX) {
						view = ((ListView)view).getChildAt(childIdx);
					} else if(objType == UI_OBJ_TYPE.VIEW) {
						int viewId = 0;
						if(view == null) {
							viewId = activity.getResources().getIdentifier(id_part, "id", activity.getPackageName());
						} else {
							viewId = view.getResources().getIdentifier(id_part, "id", activity.getPackageName());
						}
						view= activity.findViewById(viewId);
					} else if(objType == UI_OBJ_TYPE.FRAGMENT) {
						int viewId = activity.getResources().getIdentifier(id_part, "id", activity.getPackageName());
						Fragment fragment = ((ActionBarActivity)activity).getSupportFragmentManager().findFragmentById(viewId);
						Object obj = fragment.getView();
						if(obj instanceof FrameLayout) {
							FrameLayout layout = (FrameLayout) obj;
							if(layout.getChildCount() == 1) {
								view = layout.getChildAt(0);
							} else {
								view = null;
							}
						} else if(obj instanceof View) {
							view = (View) obj;
						}
					}
					
					if(view == null) return null;
				}

				if(view != null) {
					int[] loc = new int[2];
					view.getLocationOnScreen(loc);
					int left = loc[0];
					int top = loc[1] - UIUtility.getStatusbarHeight(activity);
					int right = left + view.getWidth();
					int bottom = top + view.getHeight();

					Rect r = new Rect(left, top, right, bottom);

					return r;
				} else {
					return null;
				}
			} else {
				DisplayMetrics metrics=new DisplayMetrics();
				activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
				return new Rect(0, 0, metrics.widthPixels, metrics.heightPixels);
			}
		} else {
			return null;
		}
	}
	
	private String getHelpId(String scrId, int helpVersion) {
		return String.format("%s_V_%d", scrId, helpVersion);
	}
}
