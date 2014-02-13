package com.junglee.commonlib.network;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

/**
 * NetworkClient provides the abstraction for all sorts of network communication from the app.
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
public class NetworkClient {
	private static NetworkClient instance = null;
	
	
	public static final int REQUEST_CANCELLED = 0;
	public static final int REQUEST_NOT_ACTIVE = 1;
	public static final int REQUEST_NOT_CANCELLED = 2;	
	
	private Map<String, VolleyResponseFetcher> responseFetchers = null;
	private Map<String, AsyncHttpClientFileUploader> fileUploaders = null;
	
	protected NetworkClient() {
		// Exists only to defeat instantiation.
	}	
	public static NetworkClient getInstance() {
		if(instance == null) {
			instance = new NetworkClient();
		}
		
		return instance;
	}	
	
	/**
	 * Does any initialization for NetworkClient. Currently it creates the instance of the VolleyClient, and initializes 
	 * the maps that would contain the mapping for the request_id to response_fetcher/file_uploaders/file_downloaders for 
	 * that request.
	 * @param c the application context
	 */
	public void initialize(Context c) {
		VolleyClient.createInstance(c);
		
		responseFetchers = new HashMap<String, VolleyResponseFetcher>();
		fileUploaders = new HashMap<String, AsyncHttpClientFileUploader>();
		
	}
	
	
	
	/**
	 * Executes the network request synchronously, and fetches the response when the request completes. It should be called 
	 * on a separate thread or through a runnable to avoid blocking the UI.
	 * @param request the network request object.
	 * @return the network response that indicates success/failure, and other details.
	 */
	public NetworkResponse fetchResponseSync(NetworkRequest request) {
		VolleyResponseFetcher responseFetcher = new VolleyResponseFetcher();
		return responseFetcher.fetchResponseSync(request);
	}	
	/**
	 * Executes the network request asynchronously, and fetches the response when the request completes. The listener 
	 * receives the callback on the completion of the request.
	 * @param request the network request object.
	 * @param listener the response listener that handles the response callbacks.
	 */
	public void fetchResponseAsync(NetworkRequest request, NetworkResponseListener listener) {
		VolleyResponseFetcher responseFetcher = new VolleyResponseFetcher();
		responseFetchers.put(request.getRequestId(), responseFetcher);
		responseFetcher.fetchResponseAsync(request, listener);
	}
	
	/**
	 * Executes the upload request asynchronously. The listener receives the callback on the progress of the request.
	 * @param request the file upload request object.
	 * @param transferListener the tarnsfer listener that handles the progress callbacks.
	 */
	public void startUpload(FileUploadRequest request, FileTransferResponseListener transferListener) {
		AsyncHttpClientFileUploader uploader = new AsyncHttpClientFileUploader();
		fileUploaders.put(request.getRequestId(), uploader);
		uploader.uploadFile(request, transferListener);
	}
	/**
	 * Not yet implemented!
	 * @param request the file upload request object.
	 * @param transferListener the tarnsfer listener that handles the progress callbacks.
	 */
	public void startDownload(FileUploadRequest request, FileTransferResponseListener transferListener) {
		
	}
	
	
	
	/**
	 * Cancels the request, given the request id.
	 * @param reqId the id of the request that is to be cancelled.
	 * @return the status whether the request has been cancelled, or it wasn't even in progress.
	 */
	public int cancelRequest(String reqId) {
		if(responseFetchers.containsKey(reqId)) {
			VolleyResponseFetcher responseFetcher = responseFetchers.get(reqId);
			responseFetcher.cancelRequest();
			return REQUEST_CANCELLED;
		} else {
			return REQUEST_NOT_ACTIVE;
		}
	}
	/**
	 * Cancels the upload, given the request id.
	 * @param reqId the id of the request that is to be cancelled.
	 * @return the status whether the request has been cancelled.
	 */
	public int cancelUpload(String reqId) {
		if(fileUploaders.containsKey(reqId)) {
			AsyncHttpClientFileUploader uploader = fileUploaders.get(reqId);
			uploader.cancel();
			return REQUEST_CANCELLED;
		} else {
			return REQUEST_NOT_ACTIVE;
		}
	}
	/**
	 * Cancels the download, given the request id.
	 * 
	 * @param reqId the id of the request that is to be cancelled.
	 * @return the status whether the request has been cancelled.
	 */
	public int cancelDownload(String reqId) {
		return REQUEST_CANCELLED;
	}
	
	
	
	/**
	 * This is called when a normal network request (GET/POST/PUT/DELETE) completes. It may have succeeded or failed.
	 * @param reqId the id of the request that has completed.
	 * @param success the flag that indicates whether the request succeeded or failed.
	 */
	public void onRequestCompleted(String reqId, boolean success) {
		responseFetchers.remove(reqId);
	}
	/**
	 * This is called when a file upload request completes. It may have succeeded or failed.
	 * @param reqId the id of the upload request that has completed.
	 * @param success the flag that indicates whether the upload succeeded or failed.
	 */
	public void onUploadFinished(String reqId, boolean success) {
		fileUploaders.remove(reqId);
	}
	/**
	 * Not yet implemented!
	 * @param reqId the id of the upload request that has completed.
	 * @param success the flag that indicates whether the download succeeded or failed.
	 */
	public void onDownloadFinished(String reqId, boolean success) {
		
	}
}
