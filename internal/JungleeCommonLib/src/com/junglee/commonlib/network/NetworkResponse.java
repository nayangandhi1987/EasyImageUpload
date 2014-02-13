package com.junglee.commonlib.network;

import java.util.Arrays;

import org.json.JSONObject;

import com.junglee.commonlib.utils.StringUtility;

public class NetworkResponse {
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
	
	
	/**
	 * The constructor to create a NetworkResponse object with default values.
	 */
	public NetworkResponse() {
		init();
	}
	/**
	 * Initializes the data member of the NetworkResponse to the initial/default values.
	 */
	private void init() {
		statusCode = 200;
		requestFailed = false;
		errorCode = 0;
		errorMsg = null;
		isTimedOut = false;
		responseType = NetworkResponse.Type.NONE;
	}
	
	
	
	/**
	 * Gets the status code of the response.
	 * @return status code.
	 */
	public int getStatusCode() {
		return statusCode;
	}
	/**
	 * Sets the status code in the NetworkResponse object, to be used by the consumers of the response.
	 * @param statusCode the status code value.
	 */
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	
	/**
	 * checks whether the request has failed or not.
	 * @return true if the request failed to be processed.
	 */
	public boolean isRequestFailed() {
		return requestFailed;
	}
	/**
	 * Sets the flag indicating whether request has failed or not.
	 * @param requestFailed the flag indicating the failure.
	 */
	public void setRequestFailed(boolean requestFailed) {
		this.requestFailed = requestFailed;
	}
	
	/**
	 * Gets the error code in the response.
	 * @return the error code value, or 0 if there was no error.
	 */
	public int getErrorCode() {
		return errorCode;
	}
	/**
	 * Sets the error code indicating what kind of error has happened as part of the response.
	 * @param errorCode the error code value.
	 */
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
	/**
	 * Gets the error message in the response.
	 * @return the error message.
	 */
	public String getErrorMsg() {
		return errorMsg;
	}
	/**
	 * Sets the error message indicating the details of the error happened as part of the response.
	 * @param errorMsg the meaningful error message.
	 */
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	/**
	 * Checks whether the request has timed out.
	 * @return flag indicating whether request has timed out.
	 */
	public boolean isTimedOut() {
		return isTimedOut;
	}
	/**
	 * Sets a flag to indicate whether the request has timed out or not.
	 * @param isTimedOut the timeout flag
	 */
	public void setTimedOut(boolean isTimedOut) {
		this.isTimedOut = isTimedOut;
	}	
	
	
	/**
	 * Gets the type of the response that is populated in the response object.
	 * @return the response type - string, byte array, or json object.
	 */
	public Type getResponseType() {
		return responseType;
	}
	/**
	 * Sets the type of the response that is populated in the response object.
	 * @param responseType the response type - string, byte array, or json object.
	 */
	public void setResponseType(Type responseType) {
		this.responseType = responseType;
	}
	
	/**
	 * Gets the value of the byteArrayResponse field in the response object. It will be null, if it was not previously 
	 * assigned.
	 * @return the byte array response data, or null
	 */
	public byte[] getByteArrayResponse() {
		return byteArrayResponse;
	}
	/**
	 * Sets the byte array response in the NetworkResponse object.
	 * @param byteArrayResponse the byte array response to the request.
	 */
	public void setByteArrayResponse(byte[] byteArrayResponse) {
		this.byteArrayResponse = byteArrayResponse;
		this.responseType = Type.BYTE_ARRAY;
	}
	
	/**
	 * Gets the value of the stringResponse field in the response object. It will be null, if it was not previously 
	 * assigned.
	 * @return the string response data, or null
	 */
	public String getStringResponse() {
		return stringResponse;
	}
	/**
	 * Sets the string response in the NetworkResponse object.
	 * @param stringResponse the string response to the request.
	 */
	public void setStringResponse(String stringResponse) {
		this.stringResponse = stringResponse;
		this.responseType = Type.STRING;
	}
	
	/**
	 * Gets the value of the jsonResponse field in the response object. It will be null, if it was not previously 
	 * assigned.
	 * @return the jsonobject response data, or null
	 */
	public JSONObject getJsonResponse() {
		return jsonResponse;
	}
	/**
	 * Sets the json object response in the NetworkResponse object.
	 * @param jsonResponse the jsonobject response to the request.
	 */
	public void setJsonResponse(JSONObject jsonResponse) {
		this.jsonResponse = jsonResponse;
		this.responseType = Type.JSON;
	}
	
	/**
	 * Checks if a request was successful or not. A request is considered successful if requestFailed flag is not set to 
	 * true, the status code is between 200 and 299, and errorCode field has the value 0, and the errorMsg field is null.
	 * @return
	 */
	public boolean isSuccessful() {
		boolean failure = false;
		
		if(requestFailed) {
			failure = true;
		} else {
			if(statusCode<200 || statusCode>=300) {
				failure = true;
			} else {
				if(errorCode != 0 || StringUtility.isPopulated(errorMsg)) {
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
}
