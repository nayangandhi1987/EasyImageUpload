package com.junglee.commonlib.eventengine;

import org.json.JSONObject;

import com.junglee.commonlib.utils.ThreadUtility;

import android.app.Activity;

public class MnThreadSyncEventHandler extends SyncEventHandler {
	private Activity activity;
	
	public MnThreadSyncEventHandler(Activity activity) {
		this.activity = activity;
	}
	
	@Override
	public boolean execute(final JSONObject eventData) {
		if(ThreadUtility.isItMainThread()) {
			super.execute(eventData);
		} else {
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					MnThreadSyncEventHandler.super.execute(eventData);				
				}
			});
		}
		
		return true;
	}
}
