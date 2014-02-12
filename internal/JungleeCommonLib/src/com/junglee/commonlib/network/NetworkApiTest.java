package com.junglee.commonlib.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.junglee.commonlib.logging.Logger;
import com.junglee.commonlib.utils.ThreadUtility;

public class NetworkApiTest {
	private static final String PARSE_APP_ID = "r9Jmz5GDuKO0ZMcENR3X0Ygq7RThXtgzJPVR6C6r";
	private static final String PARSE_REST_API_KEY = "N6e3gyM4FJZOGyzTBj560Tik2H27u3ywCWqZUZSO";
	private static final String API_VER = "1";
	private static final String API_BASE_URL = "https://api.parse.com";

	private static final String TEST_CLASS_NAME  = "NClassDummy";
	private static final String JSON_CONTENT_TYPE  = "application/json";
	private static final String FILE_CONTENT_TYPE  = "application/octet-stream";
	
	private static final String OBJECT_ID = "dZ43K18qBh";
	
	
	public static void testParseGetRequestSync() {
	    String url = String.format("%s/%s/classes/%s/%s", API_BASE_URL, API_VER, TEST_CLASS_NAME, OBJECT_ID);
	    
	    List<NameValuePair> headers = new ArrayList<NameValuePair>();
	    headers.add(new BasicNameValuePair("X-Parse-Application-Id", PARSE_APP_ID));
	    headers.add(new BasicNameValuePair("X-Parse-REST-API-Key", PARSE_REST_API_KEY));
	    
	    final NetworkRequest request = new NetworkRequest(url);
	    request.setHeaders(headers);
	    
	    ThreadUtility.executeInBackground(new Runnable() {					
			@Override
			public void run() {
				NetworkResponse response = request.fetchResponseSync();
        		Logger.verbose("Sync Get Request completed: "+response.toString());
			}
		});
	}
	public static void testParseGetRequestAsync() {
		String url = String.format("%s/%s/classes/%s/%s", API_BASE_URL, API_VER, TEST_CLASS_NAME, OBJECT_ID);
	    
	    List<NameValuePair> headers = new ArrayList<NameValuePair>();
	    headers.add(new BasicNameValuePair("X-Parse-Application-Id", PARSE_APP_ID));
	    headers.add(new BasicNameValuePair("X-Parse-REST-API-Key", PARSE_REST_API_KEY));
	    
	    NetworkRequest request = new NetworkRequest(url);
	    request.setHeaders(headers);
	    
	    request.fetchResponseAsync(new NetworkResponseListener() {				
			@Override
			public void onRequestCompleted(String reqId, NetworkResponse response) {
				Logger.verbose("Async Get Request completed: "+response.toString());
			}
		});
	}
	
	public static void testParsePostRequestSync() {
	    String url = String.format("%s/%s/classes/%s", API_BASE_URL, API_VER, TEST_CLASS_NAME);
	    JSONObject obj = new JSONObject();
	    try {
			obj.put("fieldString", "Hello from Post Sync");
			obj.put("fieldNumber", (new Random()).nextInt());
		    obj.put("fieldBoolean", true);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	    
	    List<NameValuePair> headers = new ArrayList<NameValuePair>();
	    headers.add(new BasicNameValuePair("X-Parse-Application-Id", PARSE_APP_ID));
	    headers.add(new BasicNameValuePair("X-Parse-REST-API-Key", PARSE_REST_API_KEY));
	    headers.add(new BasicNameValuePair("Content-Type", JSON_CONTENT_TYPE));
	    
	    final NetworkRequest request = new NetworkRequest(url, NetworkRequest.Method.POST, obj.toString());
	    request.setHeaders(headers);
	    
	    ThreadUtility.executeInBackground(new Runnable() {					
			@Override
			public void run() {
				NetworkResponse response = request.fetchResponseSync();
        		Logger.verbose("Sync Post Request completed: "+response.toString());
			}
		});
	}
	public static void testParsePostRequestAsync() {
	    String url = String.format("%s/%s/classes/%s", API_BASE_URL, API_VER, TEST_CLASS_NAME);
	    JSONObject obj = new JSONObject();
	    try {
			obj.put("fieldString", "Hello from Post Async");
			obj.put("fieldNumber", (new Random()).nextInt());
		    obj.put("fieldBoolean", true);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	    
	    List<NameValuePair> headers = new ArrayList<NameValuePair>();
	    headers.add(new BasicNameValuePair("X-Parse-Application-Id", PARSE_APP_ID));
	    headers.add(new BasicNameValuePair("X-Parse-REST-API-Key", PARSE_REST_API_KEY));
	    headers.add(new BasicNameValuePair("Content-Type", JSON_CONTENT_TYPE));
	    
	    NetworkRequest request = new NetworkRequest(url, NetworkRequest.Method.POST, obj.toString());
	    request.setHeaders(headers);
	    request.fetchResponseAsync(new NetworkResponseListener() {				
			@Override
			public void onRequestCompleted(String reqId, NetworkResponse response) {
				Logger.verbose("Async Post Request completed: "+response.toString());
			}
		});
	}
	
	public static void testParsePutRequestSync() {
	    String url = String.format("%s/%s/classes/%s/%s", API_BASE_URL, API_VER, TEST_CLASS_NAME, OBJECT_ID);
	    JSONObject obj = new JSONObject();
	    try {
			obj.put("fieldString", "Hello Again from Put Sync");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	    
	    List<NameValuePair> headers = new ArrayList<NameValuePair>();
	    headers.add(new BasicNameValuePair("X-Parse-Application-Id", PARSE_APP_ID));
	    headers.add(new BasicNameValuePair("X-Parse-REST-API-Key", PARSE_REST_API_KEY));
	    headers.add(new BasicNameValuePair("Content-Type", JSON_CONTENT_TYPE));
	    
	    final NetworkRequest request = new NetworkRequest(url, NetworkRequest.Method.PUT, obj.toString());
	    request.setHeaders(headers);

	    ThreadUtility.executeInBackground(new Runnable() {					
			@Override
			public void run() {
				NetworkResponse response = request.fetchResponseSync();
        		Logger.verbose("Sync Put Request completed: "+response.toString());
			}
		});
	}
	public static void testParsePutRequestAsync() {
		String url = String.format("%s/%s/classes/%s/%s", API_BASE_URL, API_VER, TEST_CLASS_NAME, OBJECT_ID);
	    JSONObject obj = new JSONObject();
	    try {
			obj.put("fieldString", "Hello Again from Put Async");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	    
	    List<NameValuePair> headers = new ArrayList<NameValuePair>();
	    headers.add(new BasicNameValuePair("X-Parse-Application-Id", PARSE_APP_ID));
	    headers.add(new BasicNameValuePair("X-Parse-REST-API-Key", PARSE_REST_API_KEY));
	    headers.add(new BasicNameValuePair("Content-Type", JSON_CONTENT_TYPE));
	    
	    NetworkRequest request = new NetworkRequest(url, NetworkRequest.Method.PUT, obj.toString());
	    request.setHeaders(headers);
	    request.fetchResponseAsync(new NetworkResponseListener() {				
			@Override
			public void onRequestCompleted(String reqId, NetworkResponse response) {
				Logger.verbose("Async Put Request completed: "+response.toString());
			}
		});
	}
	
	public static void testParseDeleteRequestSync() {
	    String url = String.format("%s/%s/classes/%s/%s", API_BASE_URL, API_VER, TEST_CLASS_NAME, OBJECT_ID);
	    
	    List<NameValuePair> headers = new ArrayList<NameValuePair>();
	    headers.add(new BasicNameValuePair("X-Parse-Application-Id", PARSE_APP_ID));
	    headers.add(new BasicNameValuePair("X-Parse-REST-API-Key", PARSE_REST_API_KEY));
	    
	    final NetworkRequest request = new NetworkRequest(url, NetworkRequest.Method.DELETE);
	    request.setHeaders(headers);


	    ThreadUtility.executeInBackground(new Runnable() {					
			@Override
			public void run() {
				NetworkResponse response = request.fetchResponseSync();
        		Logger.verbose("Sync Delete Request completed: "+response.toString());
			}
		});
	}
	public static void testParseDeleteRequestAsync() {
		String url = String.format("%s/%s/classes/%s/%s", API_BASE_URL, API_VER, TEST_CLASS_NAME, OBJECT_ID);
	    
	    List<NameValuePair> headers = new ArrayList<NameValuePair>();
	    headers.add(new BasicNameValuePair("X-Parse-Application-Id", PARSE_APP_ID));
	    headers.add(new BasicNameValuePair("X-Parse-REST-API-Key", PARSE_REST_API_KEY));
	    
	    NetworkRequest request = new NetworkRequest(url, NetworkRequest.Method.DELETE);
	    request.setHeaders(headers);
	    request.fetchResponseAsync(new NetworkResponseListener() {				
			@Override
			public void onRequestCompleted(String reqId, NetworkResponse response) {
				Logger.verbose("Async Delete Request completed: "+response.toString());
			}
		});
	}
}
