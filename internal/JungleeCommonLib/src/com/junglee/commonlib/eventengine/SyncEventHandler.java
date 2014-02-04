package com.junglee.commonlib.eventengine;

import org.json.JSONObject;

/**
 * SyncEventHandler is an implementation of IEventHandler such that the handling logic will be executed synchronously.
 * <p> 
 * When an event occurs, the EventEngine loops through all the registered event handlers and invokes their execute() 
 * method. If an event handlers is an instance of SyncEventHandler, the EventEngine won't be able to move to the next 
 * handler until the processing of this handler is finished.
 * 
 * @author      Nayan Gandhi <nggandhi@amazon.com>
 * @since       1.0
 */
public class SyncEventHandler implements IEventHandler {
	@Override
	public boolean execute(JSONObject eventData) {
		return handle(eventData);
	}

	@Override
	public boolean handle(JSONObject eventData) {
		return false;
	}
}
