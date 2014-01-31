package com.junglee.network;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.http.Header;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.junglee.events.GlobalEventID;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import com.junglee.commonlib.logging.Logger;

public class AsyncHttpClientFileUploader {
	private static final String TAG = "AsyncHttpClientFileUploader";
	private static AsyncHttpClient client = new AsyncHttpClient();
	
	private Handler eventHandler = null;
	
	public void setHandler(Handler handler) {
		this.eventHandler = handler;
	}
	
	private static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.post(url, params, responseHandler);
	}
	
	public void uploadFile(String url, File file) {
		RequestParams params = new RequestParams();
		try {
		    params.put("file", file);
		} catch(FileNotFoundException e) {
			// TODO
		}
		
		post(url, params,  new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				Logger.info(TAG, "Upload Started !!");
				sendMsgToHandler(GlobalEventID.UPLOAD_STARTED);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				Logger.info(TAG, "Upload Succeeded : StatusCode="+statusCode);
				sendMsgToHandler(GlobalEventID.UPLOAD_SUCCEEDED);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
			{
				Logger.info(TAG, "Upload Failed : StatusCode="+statusCode+", ResponseBody="+responseBody);
				sendMsgToHandler(GlobalEventID.UPLOAD_FAILED);
			}

			@Override
			public void onRetry() {
				Logger.info(TAG, "Upload Retried !!");
			}

			@Override
			public void onProgress(int bytesWritten, int totalSize) {
				Logger.info(TAG, "Upload In Progress !!");
			}

			@Override
			public void onFinish() {
				Logger.info(TAG, "Upload Finished !!");
			}
			
			private void sendMsgToHandler(int msgId) {
				Message msgObj = eventHandler.obtainMessage();
		    	Bundle b = new Bundle();
		    	b.putInt("message_id", msgId);
		    	msgObj.setData(b);
		    	eventHandler.sendMessage(msgObj);
			}
		});
    }
}
