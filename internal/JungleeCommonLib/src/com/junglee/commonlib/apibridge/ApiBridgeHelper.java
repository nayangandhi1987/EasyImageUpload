package com.junglee.commonlib.apibridge;

import org.json.JSONException;
import org.json.JSONObject;

public class ApiBridgeHelper {
	private static final String ERROR_TAG = "error";
	private static final String ERROR_MSG_TAG = "errorMessage";
	private static final String ERROR_CODE_TAG = "errorCode";
	
	private static final String REQ_IN_PROCESS_TAG = "reqIsInProgress";
	
	public static final int ERROR_CODE_NAMESPACE_NOT_SUPPORTED = 0;
	public static final String ERROR_MSG_NAMESPACE_NOT_SUPPORTED = 
			"The namespace \'%s\' is not supported";
	public static final int ERROR_CODE_METHOD_NOT_SUPPORTED_BY_NAMESPACE = 1;
	public static final String ERROR_MSG_METHOD_NOT_SUPPORTED_BY_NAMESPACE = 
			"The method \'%s\' is not handled by the namespace \'%s\'";
	public static final int ERROR_CODE_METHOD_NOT_SUPPORTED_BY_DEFAULT_NAMESPACE = 2;
	public static final String ERROR_MSG_METHOD_NOT_SUPPORTED_BY_DEFAULT_NAMESPACE = 
			"The method \'%s\' is not supported by Default Controller";
	
	public static JSONObject jsonWithError(String errMsg, int errCode) {
		JSONObject errJson = new JSONObject();
		try {
			errJson.put(ERROR_TAG, true);
			errJson.put(ERROR_MSG_TAG, errMsg);
			errJson.put(ERROR_CODE_TAG, errCode);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return errJson;
	}
	public static boolean isRequestFailed(JSONObject json) {
		try {
			if(json != null && json.has(ERROR_TAG) && json.getBoolean(ERROR_CODE_TAG) == true) {
				return true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static JSONObject jsonWithReqInProcess() {
		JSONObject reqInProcessJson = new JSONObject();
		try {
			reqInProcessJson.put(REQ_IN_PROCESS_TAG, true);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return reqInProcessJson;
	}
	public static boolean isRequestInProcess(JSONObject json) {
		try {
			if(json != null && json.has(REQ_IN_PROCESS_TAG) && json.getBoolean(REQ_IN_PROCESS_TAG) == true) {
				return true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return false;
	}
}
