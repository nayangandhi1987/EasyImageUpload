package com.junglee.commonlib.eventengine;

import org.json.JSONObject;

public interface EventHandler {
	public boolean execute(JSONObject eventData);
	
	public boolean handle(JSONObject eventData);
}
