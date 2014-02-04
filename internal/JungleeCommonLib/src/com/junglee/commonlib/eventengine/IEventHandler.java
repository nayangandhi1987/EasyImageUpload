package com.junglee.commonlib.eventengine;

import org.json.JSONObject;

/**
 * IEventHandler is an interface that any event handler must implement to provide the handling logic for an event.
 * <p> 
 * One can register/unregister an event handler with the EventEngine to be executed on occurrence of a specific event. 
 * When an event is fired through EventEngine, all the registered event handlers for that particular event are executed.
 * 
 * @author      Nayan Gandhi <nggandhi@amazon.com>
 * @since       1.0
 */
public interface IEventHandler {
	public boolean execute(JSONObject eventData);
	
	public boolean handle(JSONObject eventData);
}
