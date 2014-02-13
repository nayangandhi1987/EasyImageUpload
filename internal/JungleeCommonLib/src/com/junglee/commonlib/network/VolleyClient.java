package com.junglee.commonlib.network;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.Volley;
import com.junglee.commonlib.logging.Logger;
import com.junglee.commonlib.utils.StringUtility;

/**
 * VolleyClient is a wrapper of the Volley library.
 * <p> 
 * It provides support for normal http requests including GET/POST/PUT/DELETE methods. It also supports file uploads. File 
 * downloads is currently not in scope.
 * <p>
 * For normal http requests, it provides two variants for the requests - synchronous and asynchronous. For asynchronous 
 * requests, handlers can be registered for callbacks. The asynchronous requests can also be cancelled using the request 
 * id.
 * <p>
 * It wraps different libraries for making the network requests. Currently, it uses Volley for basic http requests and 
 * android-async-client for file uploads. These underlying libraries or the classes that actually implement the mechanism 
 * to make network requests can be changed without modifying the client code.
 * 
 * @author      Nayan Gandhi <nggandhi@amazon.com>
 * @since       1.0
 */
public class VolleyClient {
	private static VolleyClient _instance = null;
	private static final String TAG = "VolleyClient";
	
	private RequestQueue _requestQueue;
	
	/**
	 * Creates the instance of the VolleyClient. It must be called on start of each app session.
	 * @param c the activity/application context.
	 */
	public static void createInstance(Context c) {
		_instance = new VolleyClient(c);
	}
	public static VolleyClient getInstance() {
		return _instance;
	}
	
	/**
	 * The constructor for the Volley Client. It does all the initialization for Volley. Creates the DefaultHttpClient 
	 * and the CookieStore instances to be used by Volley, and creates the request queue that will be used for all the 
	 * requests going through Volley.
	 * @param c
	 */
	private VolleyClient(Context c) {
		DefaultHttpClient httpclient = new DefaultHttpClient();

		CookieStore cookieStore = new BasicCookieStore();
		httpclient.setCookieStore( cookieStore );

		HttpStack httpStack = new HttpClientStack( httpclient );
		
		_requestQueue = Volley.newRequestQueue(c.getApplicationContext(), httpStack);
	}
	
	
	/**
	 * Gets the request queue being used by Volley.
	 * @return the request queue.
	 */
	private RequestQueue getRequestQueue() {
        return _requestQueue;
    }
	/**
	 * Adds a request to the Volley's request queue.
	 * @param req the Volley's request.
	 * @param tag the tag or id for the request.
	 */
	public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(StringUtility.isBlank(tag) ? TAG : tag);

        Logger.verbose(TAG, String.format("Adding request to queue: %s", req.getUrl()));

        getRequestQueue().add(req);
    }
	/**
	 * Adds a request to the Volley's request queue.
	 * @param req the Volley's request.
	 */
	public <T> void addToRequestQueue(Request<T> req) {
        // set the default tag if tag is empty
        req.setTag(TAG);
        
        Logger.verbose(TAG, String.format("Adding request to queue: %s", req.getUrl()));

        getRequestQueue().add(req);
    }
	/**
	 * Cancels all pending requests for the specified tag.
	 * @param tag the tag identifier for the request.
	 */
	public void cancelPendingRequests(Object tag) {
        getRequestQueue().cancelAll(tag);
    }
}
