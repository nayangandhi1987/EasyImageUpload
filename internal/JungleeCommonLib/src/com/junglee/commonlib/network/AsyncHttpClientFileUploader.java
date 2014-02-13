package com.junglee.commonlib.network;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.http.Header;

import android.content.Context;

import com.junglee.commonlib.logging.Logger;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * AsyncHttpClientFileUploader provides support for file uploads asynchronously.
 * <p> 
 * AsyncHttpClientFileUploader uses android-async-http library to provide support for file uploads, and makes callbacks 
 * on start/finish, success/failure, and progress, etc. The requests which are in progress can be cancelled.
 * 
 * @author      Nayan Gandhi <nggandhi@amazon.com>
 * @since       1.0
 */
public class AsyncHttpClientFileUploader {
	private static final String TAG = "AsyncHttpClientFileUploader";
	
	private AsyncHttpClient client = null;
	private FileUploadRequest uploadRequest = null;
	private FileTransferResponseListener transferListener = null;
	
	/**
	 * The constructor that internally creates an instance of AsyncHttpClient to be used later.
	 */
	public AsyncHttpClientFileUploader() {
		client = new AsyncHttpClient();
	}
	
	
	/**
	 * It starts the file upload. The listener will get all callbacks on start/finish, success/failure, and progress, etc.
	 * @param request the file upload request object.
	 * @param listener the listener that will handle the progress.
	 */
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
	
	/**
	 * Cancels the request if it is in progress, else does nothing.
	 */
	public void cancel() {
		if(uploadRequest != null) {
			client.cancelRequests(uploadRequest.getContext(), true);
			NetworkClient.getInstance().onUploadFinished(uploadRequest.getRequestId(), false);
		}
	}
	
	/**
	 * It actually does the file upload using the POST request.
	 * @param c the context from which the request is initiated.
	 * @param url the url to be hit to do the file upload.
	 * @param params the request param containing the file attribute => the File object
	 * @param responseHandler the handler that will handle the updates on start/finish, success/failure, and progres, etc.
	 */
	private void doUpload(Context c, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.post(c, url, params, responseHandler);
	}
}
