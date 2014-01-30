package com.junglee.commonlib.eventengine;

import org.json.JSONObject;

public class SyncEventHandler implements EventHandler {
	@Override
	public boolean execute(JSONObject eventData) {
		return handle(eventData);
	}

	@Override
	public boolean handle(JSONObject eventData) {
		return false;
	}
}
