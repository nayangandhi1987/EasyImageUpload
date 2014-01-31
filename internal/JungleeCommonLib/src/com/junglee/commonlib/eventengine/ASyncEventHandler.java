package com.junglee.commonlib.eventengine;

import org.json.JSONObject;

import android.os.Handler;

public class ASyncEventHandler implements EventHandler {
	@Override
	public boolean execute(final JSONObject eventData) {
		Handler h = new Handler();
		Runnable r = new Runnable() {			
			@Override
			public void run() {
				handle(eventData);
			}
		};
		h.post(r);
		
		return false;
	}

	@Override
	public boolean handle(JSONObject eventData) {
		return false;
	}
}
