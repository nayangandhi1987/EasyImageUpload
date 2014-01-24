package com.junglee.commonlib.apibridge;

import org.json.JSONObject;

public interface INameSpace {
	JSONObject processRequest(String namespace, String apiName, JSONObject request, String requestId, ApiBridgeController controller);
	String getDefaultName();
}

