package com.junglee.commonlib.eventengine;

import org.json.JSONObject;

import com.junglee.commonlib.utils.ThreadUtility;

import android.app.Activity;

/**
 * MnThreadEventHandler is an implementation of IEventHandler such that the handling logic will be executed on the main 
 * thread only.
 * <p> 
 * When the handling logic needs to access any UI controls or anything that can only be done on the application's main 
 * thread, then the event handler must be an instance of MnThreadEventHandler.
 * 
 * @author      Nayan Gandhi <nggandhi@amazon.com>
 * @since       1.0
 */
public class MnThreadEventHandler extends SyncEventHandler {
	private Activity activity;
	
	public MnThreadEventHandler(Activity activity) {
		this.activity = activity;
	}
	
	/**
	 * Makes sure that the handling logic executes on the main thread.
	 */
	@Override
	public void execute(final JSONObject eventData) {
		if(ThreadUtility.isItMainThread()) {
			super.execute(eventData);
		} else {
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					MnThreadEventHandler.super.execute(eventData);				
				}
			});
		}
	}
}
