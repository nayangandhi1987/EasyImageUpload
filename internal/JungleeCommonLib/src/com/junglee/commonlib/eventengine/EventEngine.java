package com.junglee.commonlib.eventengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

public class EventEngine {
	private static EventEngine instance = null;
	
	private Map<String, List<EventHandler>> eventHandlers = new HashMap<String, List<EventHandler>>();
	
	protected EventEngine() {
		// Exists only to defeat instantiation.
	}	
	public static EventEngine getInstance() {
		if(instance == null) {
			instance = new EventEngine();
		}
		
		return instance;
	}
	
	public void register(String eventType, EventHandler handler) {
		List<EventHandler> handlers = null;
		if(eventHandlers.containsKey(eventType)) {
			handlers = eventHandlers.get(eventType);
		} else {
			handlers = new ArrayList<EventHandler>();
		}
		
		handlers.add(handler);
		eventHandlers.put(eventType, handlers);
	}
	public void unregister(String eventType, EventHandler handler) {
		List<EventHandler> handlers = null;
		if(eventHandlers.containsKey(eventType)) {
			handlers = eventHandlers.get(eventType);
			handlers.remove(handler);
			eventHandlers.put(eventType, handlers);
		}
	}
	
	public void fireEvent(String eventType, JSONObject eventData) {
		if(eventHandlers.containsKey(eventType)) {
			List<EventHandler> handlers = eventHandlers.get(eventType);
			for(int i = 0; i < handlers.size(); ++i) {
				EventHandler handler = handlers.get(i);
				if(handler != null) {
					handler.execute(eventData);
				}
			}
		}
	}
}
