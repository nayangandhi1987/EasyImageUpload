package com.junglee.commonlib.apibridge;

import org.json.JSONObject;

import com.junglee.commonlib.eventengine.EventEngine;


public class ApiBridgeController {
	private static ApiBridgeController instance = null;
	
	protected ApiBridgeController() {
		// Exists only to defeat instantiation.
	}	
	public static ApiBridgeController getInstance() {
		if(instance == null) {
			instance = new ApiBridgeController();
		}
		
		return instance;
	}
}
