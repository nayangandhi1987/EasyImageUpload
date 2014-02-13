package com.junglee.commonlib.network;

/**
 * NetworkResponseListener is an interface any class can implement to handle callbacks for network(GET/POST/PUT/DELETE) 
 * requests.
 * <p> 
 * Any class that makes a network request can implement this interface to listen to the callbacks 
 * and do appropriate handling. The classes that actually implement request execution have the responsibility to fire 
 * the callback at the right times.
 * 
 * @author      Nayan Gandhi <nggandhi@amazon.com>
 * @since       1.0
 */
public interface NetworkResponseListener {
	/**
	 * This will be called when the request completes, whether it's a success or failure.
	 * @param response the network response indicating success/failure, and other details.
	 */
	public void onRequestCompleted(String reqId, NetworkResponse response);
}
