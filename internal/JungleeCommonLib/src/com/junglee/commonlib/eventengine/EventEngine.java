package com.junglee.commonlib.eventengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

/**
 * EventEngine is the central point where anyone can register to be informed when specific events occur.
 * <p> 
 * Different parts of the app can register/unregister the handlers to be executed when specific events occur. 
 * And then, whenever an event occurs anywhere in the app, the EventEngine can be requested to fire such event. 
 * As a result, the EventEngine will loop through all the handlers registered for the given event to execute each 
 * of them..
 * 
 * @author      Nayan Gandhi <nggandhi@amazon.com>
 * @since       1.0
 */
public class EventEngine {
	private static EventEngine instance = null;
	
	private Map<String, List<IEventHandler>> eventHandlers = new HashMap<String, List<IEventHandler>>();
	
	protected EventEngine() {
		// Exists only to defeat instantiation.
	}	
	public static EventEngine getInstance() {
		if(instance == null) {
			instance = new EventEngine();
		}
		
		return instance;
	}
	
	public void register(String eventType, IEventHandler handler) {
		List<IEventHandler> handlers = null;
		if(eventHandlers.containsKey(eventType)) {
			handlers = eventHandlers.get(eventType);
		} else {
			handlers = new ArrayList<IEventHandler>();
		}
		
		handlers.add(handler);
		eventHandlers.put(eventType, handlers);
	}
	public void unregister(String eventType, IEventHandler handler) {
		List<IEventHandler> handlers = null;
		if(eventHandlers.containsKey(eventType)) {
			handlers = eventHandlers.get(eventType);
			handlers.remove(handler);
			eventHandlers.put(eventType, handlers);
		}
	}
	
	public void fireEvent(String eventType, JSONObject eventData) {
		if(eventHandlers.containsKey(eventType)) {
			List<IEventHandler> handlers = eventHandlers.get(eventType);
			for(int i = 0; i < handlers.size(); ++i) {
				IEventHandler handler = handlers.get(i);
				if(handler != null) {
					handler.execute(eventData);
				}
			}
		}
	}
}
