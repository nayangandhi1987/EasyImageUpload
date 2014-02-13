package com.junglee.commonlib.network;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;

import android.util.Base64;
import android.util.Pair;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.junglee.commonlib.logging.Logger;
import com.junglee.commonlib.utils.LibraryGlobalConstants;

public class VolleyResponseFetcher {	
	private static final int VOLLEY_DEFAULT_ERROR_CODE = 0;
	
	private NetworkResponseListener		responseListener = null;
	private String       				id;
    private NetworkRequest 				req;
    private String       				response;
    private String						errorMsg;
	private int 						DEFAULT_CONN_TIMEOUT = 30*1000;
	private int 						DEFAULT_SOCK_TIMEOUT = 30*1000;
	

	/**
     * It makes an asynchronous network request using Volley, and  the response obtained as a result of that request is 
     * communicated back to the listener through the callback.
     * @param req the network request object.
     * @param responseListener the response listener that would handle the response callback.
     * @return the network response.
     */
    public void fetchResponseAsync(NetworkRequest req, NetworkResponseListener responseListener)
    {
    	this.req = req;
		this.responseListener = responseListener;
		this.id = req.getRequestId();
		
		int reqMethod = getVolleyRequestMethod(req);
    	
    	StringRequest request = new StringRequest(reqMethod, req.getRequestUrl(), new Response.Listener<String>() {
		    @Override
		    public void onResponse(String response) {
		    	Logger.verbose(String.format("VolleyClient", "%s Completed!", id));
		    	VolleyResponseFetcher.this.response = response;	
		    	
		        requestCompleted();
		    }
		}, new Response.ErrorListener() {
		    @Override
		    public void onErrorResponse(VolleyError error) {
		    	try {
		    		if(error!=null && error.networkResponse!=null && error.networkResponse.data!=null) {
		    			String responseBody = new String( error.networkResponse.data, "utf-8" );
		    			Logger.error("VolleyClient", String.format("%s Failed -- ResponseBody: %s", id, responseBody));
		    		}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
		    	String errorMsg = error.getMessage();
		    	Logger.error("VolleyClient", String.format("%s Failed -- Error: %s", id, error.getMessage()));
		    	VolleyResponseFetcher.this.errorMsg = errorMsg;	
		    		    	
		    	requestFailed();
		    }
		}) {

			@Override
		    public Map<String, String> getHeaders() throws AuthFailureError {
		    	return getRequestHeaders();
		    }
		    
		    @Override
		    public byte[] getBody() throws AuthFailureError {
		    	return getPostRequestBody();
		    }
		};
			
		sendVolleyRequest(request, req.getTimeouts());        
    }
    
    /**
     * It makes a synchronous network request using Volley and returns the response obtained as a result of that request.
     * @param req the network request object.
     * @return the network response.
     */
    public NetworkResponse fetchResponseSync(NetworkRequest req) {
    	this.req = req;
    	this.id = req.getRequestId();
    	
    	NetworkResponse response = new NetworkResponse();
    	response.setRequestFailed(true);
    	
    	RequestFuture<String> future = RequestFuture.newFuture();
    	
    	int reqMethod = getVolleyRequestMethod(req);
    	
    	StringRequest request = new StringRequest(reqMethod, req.getRequestUrl(), future, future) {
    		
    		@Override
		    public Map<String, String> getHeaders() throws AuthFailureError {
		    	return getRequestHeaders();
		    }
		    
		    @Override
		    public byte[] getBody() throws AuthFailureError {
		    	return getPostRequestBody();
		    }
		};		
		
		
		sendVolleyRequest(request, req.getTimeouts());

		String responseData = null;
		try {
			responseData = future.get(); // this will block
			Logger.verbose("VolleyClient", "Synchronous Request Completed!");
			response.setRequestFailed(false);
			response.setStringResponse(responseData);
		} catch (InterruptedException e) {    	    
			Logger.info("VolleyClient", "Got an InterruptedException while executing the request: "+e.getMessage());
			response.setErrorMsg(e.getMessage());
		} catch (ExecutionException e) {
			Logger.info("VolleyClient", "Got an ExecutionException while executing the request: "+e.getMessage());
			response.setErrorMsg(e.getMessage());
		} catch (Exception e) {
			Logger.info("VolleyClient", "Got an Exception while executing the request: "+e.getMessage());
			response.setErrorMsg(e.getMessage());
		}
    	
    	return response;
    }
    
    /**
     * This is used to actually fire off a Volley request through the VolleyClient. It also specifies the timeout and 
     * retry policy before firing off the request.
     * @param request
     * @param timeouts
     */
    private void sendVolleyRequest(StringRequest request, Pair<Integer, Integer> timeouts) {
    	Pair<Integer, Integer> reqTimeouts = timeouts;
		int connTimeout = reqTimeouts.first;
		int sockTimeout = reqTimeouts.second;
		int timeoutConnection = (connTimeout > 0) ? connTimeout
				: DEFAULT_CONN_TIMEOUT;
		int timeoutSocket = (sockTimeout > 0) ? sockTimeout
				: DEFAULT_SOCK_TIMEOUT;
		
		Logger.verbose("VolleyClient", String.format("Creating VolleyRequest with Timeout=%d", timeoutSocket));
		
		
		request.setRetryPolicy(new DefaultRetryPolicy(
				timeoutSocket , 
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, 
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    	VolleyClient.getInstance().addToRequestQueue(request, id);
    }
    
    /**
     * It can be used to cancel a request that is in progress.
     * @return true on success, else false.
     */
    public boolean cancelRequest() {
    	VolleyClient.getInstance().cancelPendingRequests(id);
    	NetworkClient.getInstance().onRequestCompleted(id, false);
    	return true;
    }
    
    /**
     * This gets called when the request execution succeeds. The callback is made along with the successful response.
     */
    private void requestCompleted() {
    	if(responseListener != null) {
    		NetworkResponse resp = new NetworkResponse();
    		resp.setStringResponse(response);
    		responseListener.onRequestCompleted(id, resp);
    	}
    	
    	NetworkClient.getInstance().onRequestCompleted(id, true);
    }
    /**
     * This gets called when the request execution fails. The callback is made along with the failure response.
     */
    private void requestFailed() {
    	if(responseListener != null) {
    		NetworkResponse resp = new NetworkResponse();
    		resp.setRequestFailed(true);
    		resp.setErrorMsg(errorMsg);
    		responseListener.onRequestCompleted(id, resp);
    	}
    	
    	NetworkClient.getInstance().onRequestCompleted(id, false);
    }
    
    /**
     * Gets the request headers. This includes the headers that were explicitly specified for a request, as well as the 
     * default headers that are needed for every request.
     * @return the request headers to be sent with the request being executed.
     * @throws AuthFailureError
     */
    public Map<String, String> getRequestHeaders() throws AuthFailureError {
    	String userAgent = LibraryGlobalConstants.APP_USER_AGENT;
    	
    	Map<String, String> params = new HashMap<String, String>();
        params.put("User-Agent", userAgent);
        
        if(req.needsBasicAuthCredentials()) {
        	String username = req.getUsername();
        	String password = req.getPassword();        	
        	String creds = String.format("%s:%s",username,password);
            String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
            params.put("Authorization", auth);
        }
        
        List<NameValuePair> headers = req.getHeaders();
        if(headers != null) {
        	for(NameValuePair header : headers) {
        		params.put(header.getName(), header.getValue());
        	}
        }
        return params;
    }
    
    
    
    /**
     * Gets the request body as a byte array for a POST/PUT request types.
     * @return the request body data for a POST/PUt request, otherwise null.
     * @throws AuthFailureError
     */
    private byte[] getPostRequestBody() throws AuthFailureError {
    	if (req.getRequestMethod() == NetworkRequest.Method.POST
    			|| req.getRequestMethod() == NetworkRequest.Method.PUT) {
			return req.getRequestData();
		}
    	
        return null;
    }
    
    /**
     * Gets the Volley's request method value corresponding to the NetworkRequest object's requestMethod field.
     * @param req
     * @return
     */
    private int getVolleyRequestMethod(NetworkRequest req) {
    	if(req == null) {
    		return -1;
    	}
    	
    	int reqMethod = 0; //GET
    	if (req.getRequestMethod() == NetworkRequest.Method.GET) {
    		reqMethod = 0; //GET
    	} else if (req.getRequestMethod() == NetworkRequest.Method.POST) {
    		reqMethod = 1; //POST
    	} else if (req.getRequestMethod() == NetworkRequest.Method.PUT) {
    		reqMethod = 2; //PUT
    	} else if (req.getRequestMethod() == NetworkRequest.Method.DELETE) {
    		reqMethod = 3; //DELETE
    	}
    	
    	return reqMethod;
    }
}
