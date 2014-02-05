package com.junglee.commonlib.eventengine;

import org.json.JSONObject;

import android.os.Handler;

/**
 * ASyncEventHandler is an implementation of IEventHandler such that the handling logic will be executed asynchronously.
 * <p> 
 * When an event occurs, the EventEngine loops through all the registered event handlers and invokes their execute() 
 * method. If an event handlers is an instance of ASyncEventHandler, the EventEngine will simply schedule the processing 
 * of this handler and move on to the next handler without waiting for the processing of this handler is finished.
 * 
 * @author      Nayan Gandhi <nggandhi@amazon.com>
 * @since       1.0
 */
public class ASyncEventHandler implements IEventHandler {
	/**
	 * Makes sure that the handling logic executes asynchronously.
	 */
	@Override
	public void execute(final JSONObject eventData) {
		Handler h = new Handler();
		Runnable r = new Runnable() {			
			@Override
			public void run() {
				handle(eventData);
			}
		};
		h.post(r);
	}

	@Override
	public void handle(JSONObject eventData) {
		
	}
}
