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
import android.content.res.AssetManager;
import android.graphics.Rect;
import android.os.Handler;
import android.view.View;

import com.junglee.data.IntentData;
import com.junglee.utils.FileSystemUtility;

public class FeatureHelpScreensHandler {
	private static FeatureHelpScreensHandler instance = null;
	
	private Handler handler = new Handler();

	Map<String, JSONObject> helpScreenParamsDataMap = new HashMap<String, JSONObject>();
	
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
	
	public JSONObject getHelpScreenData(String scrId) {
		if(helpScreenParamsDataMap.containsKey(scrId)) {
			return helpScreenParamsDataMap.get(scrId);
		}
		
		return null;
	}
	
	public void checkForHelpScreen(final String scrId, final Context c) {
		final JSONObject helpJson = getHelpScreenData(scrId);
		if(helpJson != null) {
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					invokeHelpScreen(scrId, helpJson, c);
				}
			}, 10);
			
		}
	}
	
	private void invokeHelpScreen(String scrId, JSONObject helpJson, final Context c) {
		try {
			String KEY_HELP_VERSION = scrId+"_"+"HELP_VERSION";
			int helpVersion = helpJson.getInt("HELP_VERSION");
			SharedPreferences prefs = c.getSharedPreferences("HELP_SCREENS", Context.MODE_PRIVATE);
			int lastHelpVersion = prefs.getInt(KEY_HELP_VERSION, 0);
			if(helpVersion > lastHelpVersion) {
				SharedPreferences.Editor editor = prefs.edit();
				editor.putInt(KEY_HELP_VERSION, helpVersion);
				editor.commit();
				
				JSONArray controlsJson = helpJson.getJSONArray("CONTROLS");
				List<HelpScreenUIControlParams> controls = new ArrayList<HelpScreenUIControlParams>();
				for(int idx = 0; idx < controlsJson.length(); ++idx) {
					JSONObject controlJson = controlsJson.getJSONObject(idx);
					HelpScreenUIControlParams control = new HelpScreenUIControlParams(controlJson);
					control.viewRect = getViewLocationOnScreen((Activity) c, controlJson.getString("VIEW_ID"));
					if(control.viewRect != null) {
						controls.add(control);
					}
				}

				if(controls.size() > 0) {
					IntentData.getInstance().setHelpScreenData(controls, "TEST_ID");
					final Intent i = new Intent(c.getApplicationContext(), FeatureHelpScreenActivity.class);
					i.putExtra("HELP_ID", "TEST_ID");
					c.startActivity(i);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void parsePreloadedJson(Context c) {
		AssetManager assetManager = c.getAssets();

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
	
	private Rect getViewLocationOnScreen(Activity activity, String viewName) {
		int viewId = activity.getResources().getIdentifier(viewName, "id", activity.getPackageName());
		View view= activity.findViewById(viewId);

		if(view != null) {
			int[] loc = new int[2];
			view.getLocationOnScreen(loc);
			int left = loc[0];
			int top = loc[1] - 50;
			int right = left + view.getWidth();
			int bottom = top + view.getHeight();

			Rect r = new Rect(left, top, right, bottom);

			return r;
		}

		return null;
	}
}
