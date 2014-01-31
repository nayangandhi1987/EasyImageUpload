package com.junglee.commonlib.eventengine;

import org.json.JSONObject;

import com.junglee.commonlib.utils.ThreadUtility;

public class BgThreadAsyncEventHandler extends ASyncEventHandler {
	
	@Override
	public boolean execute(final JSONObject eventData) {
		if(!ThreadUtility.isItMainThread()) {
			super.execute(eventData);
		} else {
			ThreadUtility.executeInBackground(new Runnable() {				
				@Override
				public void run() {					
					BgThreadAsyncEventHandler.super.execute(eventData);
				}
			});
		}		
		
		return true;
	}
}
