package com.junglee.commonlib.network;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import android.util.Pair;

import com.junglee.commonlib.utils.StringUtility;
import com.junglee.commonlib.utils.UrlUtility;

public class NetworkRequest {
	public NetworkRequest(String requestUrl) {
		initRequest(requestUrl, Method.GET, null);
	}
	
	public NetworkRequest(String requestUrl, Method requestMethod, byte[] requestData) {
		initRequest(requestUrl, 
				requestMethod, 
				requestData);
	}
	public NetworkRequest(String requestUrl, Method requestMethod, String requestData) {
		initRequest(requestUrl, 
				requestMethod, 
				StringUtility.isPopulated(requestData)?requestData.getBytes():null);
	}
	public NetworkRequest(String requestUrl, Method requestMethod, JSONObject requestData) {
		initRequest(requestUrl, 
				requestMethod, 
				(requestData!=null)?requestData.toString().getBytes():null);
	}
	public NetworkRequest(String requestUrl, Method requestMethod, List<NameValuePair> requestDataParams) {
		byte[] requestData = null;
		if(requestDataParams!=null && requestDataParams.size()>0) {
			try {
				requestData = UrlUtility.getQuery(requestDataParams).getBytes();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		initRequest(requestUrl, 
				requestMethod, 
				requestData);
	}
	private void initRequest(String requestUrl, Method requestMethod, byte[] requestData) {
		this.requestUrl = requestUrl;
		this.requestMethod = requestMethod;
		this.requestData = requestData;
	}
	
	
	
	public void setRequestParams(List<NameValuePair> queryParams) {
		this.requestParams = queryParams;
	}
	public List<NameValuePair> getRequestParams() {
		return this.requestParams;
	}
	public void setHeaders(List<NameValuePair> headers) {
		this.headers = headers;
	}
	public List<NameValuePair> getHeaders() {
		return this.headers;
	}
	
	
	
	
	public String getRequestId() {
		return getRequestUrl();
	}
	
	public String getRequestUrl() {
		String requestParamsString = "";
		if(requestParams!=null && requestParams.size()>0) {
			try {
				requestParamsString = String.format("?%s", UrlUtility.getQuery(requestParams));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		return requestUrl+requestParamsString;
	}
	public Method getRequestMethod() {
		return requestMethod;
	}
	public byte[] getRequestData() {
		return requestData;
	}
	
	
	
	public void setTimeouts(int connectionTimeout, int socketTimeout) {
		this.connectionTimeout = connectionTimeout;
		this.socketTimeout = socketTimeout;
	}
	public Pair<Integer, Integer> getTimeouts() {
		return new Pair<Integer, Integer>(connectionTimeout, socketTimeout);
	}
	
	
	
	public void setBasicAuthCredentials(String username, String password) {
		this.username = username;
		this.password = password;
		this.needBasicAuth = true;
	}
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
	public boolean needsBasicAuthCredentials() {
		return needBasicAuth;
	}
	
	
	
	
	
	public NetworkResponse fetchResponseSync() {
		return NetworkClient.getInstance().fetchResponseSync(this);
	}	
	public void fetchResponseAsync(NetworkResponseListener listener) {
		NetworkClient.getInstance().fetchResponseAsync(this, listener);
	}
	public int cancel() {
		return NetworkClient.getInstance().cancelRequest(this.getRequestId());
	}


	
	
	public enum Method {
		GET,
		POST,
		PUT,
		DELETE
	}
	

	private String requestUrl;
	private List<NameValuePair> requestParams;	
	private Method requestMethod;
	private byte[] requestData;
	
	private List<NameValuePair> headers;
	
	private String username;
	private String password;
	private boolean needBasicAuth;
	
    private int connectionTimeout;
    private int socketTimeout;
    
    //TODO - Add retry option
}
