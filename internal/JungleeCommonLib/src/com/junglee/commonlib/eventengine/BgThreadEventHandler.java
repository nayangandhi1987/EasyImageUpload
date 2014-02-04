package com.junglee.commonlib.eventengine;

import org.json.JSONObject;

import com.junglee.commonlib.utils.ThreadUtility;

/**
 * BgThreadEventHandler is an implementation of IEventHandler such that the handling logic will be executed on a 
 * background thread.
 * <p> 
 * When the handling logic does not need to access any UI control or anything that can only be done on the application's 
 * main thread, and the handling logic could be executed asynchronously, then it's better to have the event handler as an 
 * instance of BgThreadEventHandler.
 * 
 * @author      Nayan Gandhi <nggandhi@amazon.com>
 * @since       1.0
 */
public class BgThreadEventHandler extends ASyncEventHandler {
	
	@Override
	public boolean execute(final JSONObject eventData) {
		if(!ThreadUtility.isItMainThread()) {
			super.execute(eventData);
		} else {
			ThreadUtility.executeInBackground(new Runnable() {				
				@Override
				public void run() {					
					BgThreadEventHandler.super.execute(eventData);
				}
			});
		}		
		
		return true;
	}
}
