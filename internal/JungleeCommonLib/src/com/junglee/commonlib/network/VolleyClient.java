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

public class VolleyClient {
	private static VolleyClient _instance = null;
	private static final String TAG = "VolleyClient";
	
	private RequestQueue _requestQueue;
	
	public static void createInstance(Context c) {
		_instance = new VolleyClient(c);
	}
	public static VolleyClient getInstance() {
		return _instance;
	}
	
	private VolleyClient(Context c) {
		DefaultHttpClient httpclient = new DefaultHttpClient();

		CookieStore cookieStore = new BasicCookieStore();
		httpclient.setCookieStore( cookieStore );

		HttpStack httpStack = new HttpClientStack( httpclient );
		
		_requestQueue = Volley.newRequestQueue(c.getApplicationContext(), httpStack);
	}
	
	
	private RequestQueue getRequestQueue() {
        return _requestQueue;
    }
	public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(StringUtility.isBlank(tag) ? TAG : tag);

        Logger.verbose(TAG, String.format("Adding request to queue: %s", req.getUrl()));

        getRequestQueue().add(req);
    }
	public <T> void addToRequestQueue(Request<T> req) {
        // set the default tag if tag is empty
        req.setTag(TAG);
        
        Logger.verbose(TAG, String.format("Adding request to queue: %s", req.getUrl()));

        getRequestQueue().add(req);
    }
	public void cancelPendingRequests(Object tag) {
        getRequestQueue().cancelAll(tag);
    }
}
