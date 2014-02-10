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

public class NetworkMonitor extends BroadcastReceiver {
	private static Context applicationContext = null;
	private static boolean isNetworkAvailable = false;
	private static final String TAG = "NetworkMonitor";
	
	public NetworkMonitor() {
	}
	
	public static void init(Context appContext) {
		applicationContext = appContext;
		checkNetworkConnectivity(applicationContext);
	}

	public static boolean isNetworkAvailable(Context context) {
		Context c = (context!=null)?context:applicationContext;
		checkNetworkConnectivity(c);

		return isNetworkAvailable;
	}
	
	public static NetworkInfo getNetworkType(Context context) {
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

			Logger.info(TAG, "Network Availability: " + isNetAvailable);
			if ((isNetAvailable != isNetworkAvailable)) {
				isNetworkAvailable = isNetAvailable;
				JSONObject networkStatus = new JSONObject();
				networkStatus.put(LibraryGlobalConstants.NETWORK_CONNECTION_AVAILABLE, isNetworkAvailable);
				EventEngine.getInstance().fireEvent(LibraryGlobalConstants.NETWORK_STATUS_CHANGE_EVENT, networkStatus);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Logger.info(TAG, "Broadcast Received --- Context is " + context + "   Intent is " + intent);
		checkNetworkConnectivity(context);
	}

}
