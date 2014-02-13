package com.junglee.commonlib.network;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import android.util.Pair;

import com.junglee.commonlib.utils.StringUtility;
import com.junglee.commonlib.utils.UrlUtility;

/**
 * It provides a mechanism for making GET/POST/PUT/DELETE http requests. It allows synchronous as well as asynchronous 
 * requests.
 * 
 * @author      Nayan Gandhi <nggandhi@amazon.com>
 * @since       1.0
 *
 */
public class NetworkRequest {
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
    
    
	/**
	 * The constructor for GET request.
	 * @param requestUrl the url for the request to be made.
	 */
	public NetworkRequest(String requestUrl) {
		initRequest(requestUrl, Method.GET, null);
	}
	/**
	 * The constructor for GET or DELETE requests.
	 * @param requestUrl the url for the request to be made.
	 * @param requestMethod the http request method - it could be GET or DELETE.
	 */
	public NetworkRequest(String requestUrl, Method requestMethod) {
		initRequest(requestUrl, 
				requestMethod, 
				null);
	}
	
	/**
	 * The generic constructor for GET/POST/PUT/DELETE requests with request body as byte array.
	 * @param requestUrl the url for the request to be made.
	 * @param requestMethod the http request method - it could be any of GET, POST, PUT or DELETE.
	 * @param requestData the POST/PUT request body as byte array.
	 */
	public NetworkRequest(String requestUrl, Method requestMethod, byte[] requestData) {
		initRequest(requestUrl, 
				requestMethod, 
				requestData);
	}
	/**
	 * The generic constructor for GET/POST/PUT/DELETE requests with request body as string.
	 * @param requestUrl the url for the request to be made.
	 * @param requestMethod the http request method - it could be any of GET, POST, PUT or DELETE.
	 * @param requestData the POST/PUT request body as string.
	 */
	public NetworkRequest(String requestUrl, Method requestMethod, String requestData) {
		initRequest(requestUrl, 
				requestMethod, 
				StringUtility.isPopulated(requestData)?requestData.getBytes():null);
	}
	/**
	 * The generic constructor for GET/POST/PUT/DELETE requests with request body as json object.
	 * @param requestUrl the url for the request to be made.
	 * @param requestMethod the http request method - it could be any of GET, POST, PUT or DELETE.
	 * @param requestData the POST/PUT request body as json object.
	 */
	public NetworkRequest(String requestUrl, Method requestMethod, JSONObject requestData) {
		initRequest(requestUrl, 
				requestMethod, 
				(requestData!=null)?requestData.toString().getBytes():null);
	}
	/**
	 * The generic constructor for GET/POST/PUT/DELETE requests with request body as name-value pairs.
	 * @param requestUrl the url for the request to be made.
	 * @param requestMethod the http request method - it could be GET, POST, PUT or DELETE.
	 * @param requestData the POST/PUT request body as name-value pairs.
	 */
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
	/**
	 * Initializes the NetworkRequest object. It is called from all the constructors.
	 * @param requestUrl the url for the request to be made.
	 * @param requestMethod the http request method - it could be GET, POST, PUT or DELETE.
	 * @param requestData the POST/PUT request body as byte array, or null.
	 */
	private void initRequest(String requestUrl, Method requestMethod, byte[] requestData) {
		this.requestUrl = requestUrl;
		this.requestMethod = requestMethod;
		this.requestData = requestData;
	}
	
	
	
	/**
	 * Sets the specific query params for the request.
	 * @param queryParams the query params as name-value pairs.
	 */
	public void setRequestParams(List<NameValuePair> queryParams) {
		this.requestParams = queryParams;
	}
	/**
	 * Gets the list of query params as name-value pairs.
	 * @return the request query params that were explicitly set for the request.
	 */
	public List<NameValuePair> getRequestParams() {
		return this.requestParams;
	}
	/**
	 * Sets the specific headers for the request.
	 * @param headers the request headers as name-value pairs.
	 */
	public void setHeaders(List<NameValuePair> headers) {
		this.headers = headers;
	}
	/**
	 * Gets the list of request headers as name-value pairs.
	 * @return the request headers that were explicitly set for the request.
	 */
	public List<NameValuePair> getHeaders() {
		return this.headers;
	}
	
	
	
	/**
	 * Gets the unique identifier for the request.
	 * @return the request id.
	 */
	public String getRequestId() {
		return getRequestUrl();
	}
	
	/**
	 * Generates the final url using the query params. The query params are URL Encoded to be part of the final url.
	 * @return the final url that will be hit when the request is executed.
	 */
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
	/**
	 * Gets the http request method(or verb) corresponding to this network request.
	 * @return the request method - GET/POST/PUT/DELETE
	 */
	public Method getRequestMethod() {
		return requestMethod;
	}
	/**
	 * Gets the request body for POST/PUT requests.
	 * @return the request body, or null. It will always be null for GET or DELETE requests.
	 */
	public byte[] getRequestData() {
		return requestData;
	}
	
	
	
	/**
	 * Sets the socket and connection timeout after which the request should fail. The connection timeout is the time it 
	 * will wait for the connection to be established. The socket timeout is the time it will wait for the first byte of 
	 * data to arrive from the server.
	 * @param connectionTimeout the connection timeout in milliseconds.
	 * @param socketTimeout the socket timeout in milliseconds.
	 */
	public void setTimeouts(int connectionTimeout, int socketTimeout) {
		this.connectionTimeout = connectionTimeout;
		this.socketTimeout = socketTimeout;
	}
	/**
	 * Gets the connection and socket timeouts that were explicitly specified for the request. The default values of 0/0 
	 * would mean that no timeout is explicitly specified for the given request.
	 * @return a pair containing connection and socket timeouts.
	 */
	public Pair<Integer, Integer> getTimeouts() {
		return new Pair<Integer, Integer>(connectionTimeout, socketTimeout);
	}
	
	
	
	/**
	 * Sets the basic auth credentials (i.e username and password) needed by the request to execute.
	 * @param username the username.
	 * @param password the password.
	 */
	public void setBasicAuthCredentials(String username, String password) {
		this.username = username;
		this.password = password;
		this.needBasicAuth = true;
	}
	/**
	 * Gets the username that was set as part of the basic auth credentials.
	 * @return the username.
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * Gets the password that was set as part of the basic auth credentials.
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * Gets the flag indicating whether auth credentials (username/password) were explicitly set for the given request.
	 * @return whether the request needs auth credentials.
	 */
	public boolean needsBasicAuthCredentials() {
		return needBasicAuth;
	}
	
	
	
	
	
	/**
	 * Executes the request synchronously, and fetches the response.
	 * @return the response.
	 */
	public NetworkResponse fetchResponseSync() {
		return NetworkClient.getInstance().fetchResponseSync(this);
	}
	/**
	 * Executes the request asynchronously. The response will be delivered through callbacks.
	 * @param listener the listener that will handle the response callbacks for the request.
	 */
	public void fetchResponseAsync(NetworkResponseListener listener) {
		NetworkClient.getInstance().fetchResponseAsync(this, listener);
	}
	/**
	 * Cancels a asynchronous request that is already in progress.
	 * @return the status whether the request has been cancelled, or it wasn't even in progress.
	 */
	public int cancel() {
		return NetworkClient.getInstance().cancelRequest(this.getRequestId());
	}
}
