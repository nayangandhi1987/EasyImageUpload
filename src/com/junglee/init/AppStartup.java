package com.junglee.init;


import android.content.Context;
import android.content.SharedPreferences;

import com.junglee.commonlib.logging.Logger;

public class AppStartup {
	public static void performTasks(Context c) {
		onEachLaunchPre(c); // perform each launch tasks
		
		SharedPreferences prefs = c.getSharedPreferences("APP_STARTUP", Context.MODE_PRIVATE);
		boolean isFirstLaunch = prefs.getBoolean("IS_FIRST_LAUNCH", true);

		if (isFirstLaunch) {
			onFirstLaunch(c); // now perform first launch tasks

			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean("IS_FIRST_LAUNCH", false);
			editor.commit();
		}

		onEachLaunchPost(c);
	}

	public static void onEachLaunchPre(final Context c) {		
		// these will run on each launch of the app
		Logger.initLogger(2);
	}
	public static void onEachLaunchPost(final Context c) {		
		// these will run on each launch of the app
		FeatureHelpScreensHandler.getInstance().initData(c);
	}
	
	public static void onFirstLaunch(final Context c) {
		// these will only run first time
	}

	private static void initailzeDatabaseTables() {
		// Initialize the database tables here
	}
}

