package com.junglee.init;


import android.content.Context;
import android.content.SharedPreferences;

import com.junglee.commonlib.logging.Logger;
import com.junglee.commonlib.network.NetworkClient;
import com.junglee.commonlib.network.NetworkMonitor;

/**
 * AppStartup is the starting point for the app. All the initialization should happen here.
 * <p> 
 * When the app starts, there will be few things which need to be initialized. All such initializations go here. Assuming 
 * that the app starts by showing a splash screen, then performTasks() method is called which will be responsible for all 
 * initialization or registeration related tasks. It also has logic to detect if it's a first time launch or a subsequent 
 * launch, and based on that it can take some special actions for the first time launch. 
 * 
 * @author      Nayan Gandhi <nggandhi@amazon.com>
 * @since       1.0
 */
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
		Logger.initLogger(0);
		NetworkClient.getInstance().initialize(c);
		NetworkMonitor.init(c);
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

