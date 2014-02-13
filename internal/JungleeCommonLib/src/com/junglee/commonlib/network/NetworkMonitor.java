package com.junglee.commonlib.network;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.junglee.commonlib.eventengine.EventEngine;
import com.junglee.commonlib.logging.Logger;
import com.junglee.commonlib.utils.LibraryGlobalConstants;


/**
 * Monitors the network connection state. Whenever it receives a CONNECTIVITY_CHANGE broadcast, it checks network 
 * connectivity by going through all the available networks to see if internet is available. So, it will always have an 
 * updated status of network connectivity, which any part of the app can query from it.
 * 
 * Make sure that the app using this class has requested the following permission:
 * <uses-permission android:name="android.permission.INTERNET" />    
 * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
 * 
 * @author      Nayan Gandhi <nggandhi@amazon.com>
 * @since       1.0
 *
 */
public class NetworkMonitor extends BroadcastReceiver {
	private static Context applicationContext = null;
	private static boolean isNetworkAvailable = false;
	private static final String TAG = "NetworkMonitor";
	
	/**
	 * This constructoe is only for handling the broadcast.
	 */
	public NetworkMonitor() {
	}
	
	/**
	 * Checks the network connectivity on the new session of the app. After this point, connectivity will be checked in 
	 * response to the CONNECTIVITY_CHANGE broadcasts received. 
	 * @param appContext the application context
	 */
	public static void init(Context appContext) {
		applicationContext = appContext;
		checkNetworkConnectivity(applicationContext);
	}

	/**
	 * Returns the cached value of the flag corresponding to the internet availability. The flag is initialized on start 
	 * of the app, and then updated when CONNECTIVITY_CHANGE broadcasts are received.
	 * @return the flag indicating whether internet is available or not.
	 */
	public static boolean isNetworkAvailable() {
		return isNetworkAvailable;
	}
	
	/**
	 * Gets the information regarding the active network.
	 * @param context the activity/application context that will be used to access the ConnectivityManager service. 
	 * @return the NetworkInfo object for the active network, or null if none is active.
	 */
	public static NetworkInfo getActiveNetworkType(Context context) {
		NetworkInfo aNetInfo = null;
		if (context != null) {
			try {
				ConnectivityManager aConMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
				aNetInfo = aConMgr.getActiveNetworkInfo();
			} catch (Exception e) {
				aNetInfo = null;
			}
		}
		return aNetInfo;
	}

	/**
	 * Checks the network connectivity, and updates the isNetworkAvailable flag to be used later when anyone queries for 
	 * the network connectivity status. It is called once on app start, and subsequently every time CONNECTIVITY_CHANGE 
	 * broadcasts are received. It doesn't consider the network type TYPE_MOBILE_MMS.
	 * <p>
	 * It also fires the NETWORK_STATUS_CHANGE_EVENT, whenever the connectivity state changes, so that all the handlers 
	 * registered with EventEngine for such event can be executed.
	 * @param context the activity/application context that will be used to access the ConnectivityManager service.
	 */
	private static void checkNetworkConnectivity(Context context) {
		if (context == null) {
			return;
		}

		try {

			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo[] netInfo = cm.getAllNetworkInfo();
			boolean isNetAvailable = false;
			for (NetworkInfo ni : netInfo) {
				if (ni.isConnected()) {
					// Ignore MMS as a usable connection type
					// Note: There are other types in CM like WIMAX, MOBILE_DUN, MOBILE_SUPL that we don't know about. Assume they are usable.
					if (ni.getType() != ConnectivityManager.TYPE_MOBILE_MMS) {
						isNetAvailable = true;
						break;
					}
				}
			}

			Logger.verbose(TAG, "Network Availability: " + isNetAvailable);
			if ((isNetAvailable != isNetworkAvailable)) {
				Logger.verbose(TAG, "Network Status Changed: CONNECTED=" + isNetAvailable);
				isNetworkAvailable = isNetAvailable;
				JSONObject networkStatus = new JSONObject();
				networkStatus.put(LibraryGlobalConstants.NETWORK_CONNECTION_AVAILABLE, isNetworkAvailable);
				EventEngine.getInstance().fireEvent(LibraryGlobalConstants.NETWORK_STATUS_CHANGE_EVENT, networkStatus);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Take action when CONNECTIVITY_CHANGE broadcast is received..
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Logger.verbose(TAG, "Broadcast Received --- CONNECTIVITY_CHANGE");
		checkNetworkConnectivity(context);
	}

}
