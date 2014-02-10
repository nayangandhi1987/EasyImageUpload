package com.junglee.commonlib.network;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

public class NetworkClient {
	private static NetworkClient instance = null;
	
	
	public static final int REQUEST_CANCELLED = 0;
	public static final int REQUEST_NOT_ACTIVE = 1;
	public static final int REQUEST_NOT_CANCELLED = 2;	
	
	private Map<String, VolleyResponseFetcher> responseFetchers = null;
	
	protected NetworkClient() {
		// Exists only to defeat instantiation.
	}	
	public static NetworkClient getInstance() {
		if(instance == null) {
			instance = new NetworkClient();
		}
		
		return instance;
	}	
	
	public void initialize(Context c) {
		VolleyClient.createInstance(c);
		
		responseFetchers = new HashMap<String, VolleyResponseFetcher>();
	}
	
	
	
	public NetworkResponse fetchResponseSync(NetworkRequest request) {
		VolleyResponseFetcher responseFetcher = new VolleyResponseFetcher();
		return responseFetcher.fetchResponseSync(request);
	}	
	public void fetchResponseAsync(NetworkRequest request, NetworkResponseListener listener) {
		VolleyResponseFetcher responseFetcher = new VolleyResponseFetcher();
		responseFetchers.put(request.getRequestId(), responseFetcher);
		responseFetcher.fetchResponseAsync(request, listener);
	}
	public void startUpload(FileUploadRequest request, FileTransferResponseListener transferListener) {
		
	}
	public void startDownload(FileUploadRequest request, FileTransferResponseListener transferListener) {
		
	}
	
	
	
	public int cancelRequest(String reqId) {
		if(responseFetchers.containsKey(reqId)) {
			VolleyResponseFetcher responseFetcher = responseFetchers.get(reqId);
			responseFetcher.cancelRequest();
			onRequestCompleted(reqId, false);
			return REQUEST_CANCELLED;
		} else {
			return REQUEST_NOT_ACTIVE;
		}
	}
	public int cancelUpload(String reqId) {
		return REQUEST_CANCELLED;
	}
	public int cancelDownload(String reqId) {
		return REQUEST_CANCELLED;
	}
	
	
	
	public void onRequestCompleted(String reqId, boolean success) {
		responseFetchers.remove(reqId);
	}
	public void onUploadFinished(String reqId, boolean success) {
		
	}
	public void onDownloadFinished(String reqId, boolean success) {
		
	}
}
