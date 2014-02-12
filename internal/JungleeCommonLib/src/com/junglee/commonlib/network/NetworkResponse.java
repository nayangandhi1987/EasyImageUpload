package com.junglee.commonlib.network;

import java.util.Arrays;

import org.json.JSONObject;

import com.junglee.commonlib.utils.StringUtility;

public class NetworkResponse {	
	public NetworkResponse() {
		init();
	}
	private void init() {
		statusCode = 200;
		requestFailed = false;
		errorCode = 0;
		errorMsg = null;
		isTimedOut = false;
		responseType = NetworkResponse.Type.NONE;
	}
	
	
	
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	
	public boolean isRequestFailed() {
		return requestFailed;
	}
	public void setRequestFailed(boolean requestFailed) {
		this.requestFailed = requestFailed;
	}
	
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	public boolean isTimedOut() {
		return isTimedOut;
	}
	public void setTimedOut(boolean isTimedOut) {
		this.isTimedOut = isTimedOut;
	}	
	
	
	public Type getResponseType() {
		return responseType;
	}
	public void setResponseType(Type responseType) {
		this.responseType = responseType;
	}
	
	public byte[] getByteArrayResponse() {
		return byteArrayResponse;
	}
	public void setByteArrayResponse(byte[] byteArrayResponse) {
		this.byteArrayResponse = byteArrayResponse;
		this.responseType = Type.BYTE_ARRAY;
	}
	
	public String getStringResponse() {
		return stringResponse;
	}
	public void setStringResponse(String stringResponse) {
		this.stringResponse = stringResponse;
		this.responseType = Type.STRING;
	}
	
	public JSONObject getJsonResponse() {
		return jsonResponse;
	}
	public void setJsonResponse(JSONObject jsonResponse) {
		this.jsonResponse = jsonResponse;
		this.responseType = Type.JSON;
	}
	
	public boolean isSuccessful() {
		boolean failure = false;
		
		if(requestFailed) {
			failure = true;
		} else {
			if(statusCode<200 || statusCode>=300) {
				failure = true;
			} else {
				if(errorCode != -1 || StringUtility.isPopulated(errorMsg)) {
					failure = true;
				}
			}
		}
		
		return !failure;
	}
	
	
	
	@Override
	public String toString() {
		return "NetworkResponse [statusCode=" + statusCode + ", requestFailed="
				+ requestFailed + ", errorCode=" + errorCode + ", errorMsg="
				+ errorMsg + ", isTimedOut=" + isTimedOut + ", responseType="
				+ responseType + ", byteArrayResponse="
				+ Arrays.toString(byteArrayResponse) + ", stringResponse="
				+ stringResponse + ", jsonResponse=" + jsonResponse + "]";
	}
	
	
	
	
	public enum Type {
		NONE,
		BYTE_ARRAY,
		STRING,
		JSON
	}
	
	private int statusCode;
	private boolean requestFailed;
	private int errorCode;
	private String errorMsg;
	private boolean isTimedOut;
	
	private Type responseType;
	private byte[] byteArrayResponse;
	private String stringResponse;
	private JSONObject jsonResponse;
}
