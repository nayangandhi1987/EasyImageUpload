package com.junglee.commonlib.network;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.http.Header;

import android.content.Context;

import com.junglee.commonlib.logging.Logger;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class AsyncHttpClientFileUploader {
	private static final String TAG = "AsyncHttpClientFileUploader";
	
	private AsyncHttpClient client = null;
	private FileUploadRequest uploadRequest = null;
	private FileTransferResponseListener transferListener = null;
	
	public AsyncHttpClientFileUploader() {
		client = new AsyncHttpClient();
	}
	
	
	public void uploadFile(FileUploadRequest request, FileTransferResponseListener listener) {
		uploadRequest = request;
		transferListener = listener;
		
		
		RequestParams params = new RequestParams();
		try {
			File file = new File(uploadRequest.getLocalFilePath());
		    params.put(uploadRequest.getFileBodyAttribute(), file);
		} catch(FileNotFoundException e) {
			// TODO
		}
		
		doUpload(uploadRequest.getContext(), uploadRequest.getUploadUrl(), params,  new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				Logger.info(TAG, "Upload Started !!");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				Logger.info(TAG, "Upload Succeeded : StatusCode="+statusCode);
				
				if(transferListener != null) {
					NetworkResponse resp = new NetworkResponse();
					resp.setStatusCode(statusCode);
					resp.setByteArrayResponse(responseBody);
					
					transferListener.transferredCompleted(resp);
				}
				NetworkClient.getInstance().onUploadFinished(uploadRequest.getRequestId(), true);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
			{
				Logger.info(TAG, "Upload Failed : StatusCode="+statusCode+", ResponseBody="+responseBody);
				
				if(transferListener != null) {
					NetworkResponse resp = new NetworkResponse();
					resp.setStatusCode(statusCode);
					resp.setByteArrayResponse(responseBody);
					resp.setErrorMsg((error!=null)?error.getMessage():null);
					
					transferListener.transferredCompleted(resp);
				}
				NetworkClient.getInstance().onUploadFinished(uploadRequest.getRequestId(), false);
			}

			@Override
			public void onRetry() {
				Logger.info(TAG, "Upload Retried !!");
			}

			@Override
			public void onProgress(int bytesWritten, int totalSize) {
				Logger.info(TAG, "Upload In Progress !!");

				if(transferListener != null) {
					transferListener.transferProgress(bytesWritten, totalSize);
				}
			}

			@Override
			public void onFinish() {
				Logger.info(TAG, "Upload Finished !!");
			}
		});
    }
	
	public void cancel() {
		client.cancelRequests(uploadRequest.getContext(), true);
	}
	
	private void doUpload(Context c, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.post(c, url, params, responseHandler);
	}
}
