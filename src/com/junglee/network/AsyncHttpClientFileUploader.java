package com.junglee.network;

import java.io.File;
import java.io.FileNotFoundException;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

public class AsyncHttpClientFileUploader {
	private static AsyncHttpClient client = new AsyncHttpClient();
	
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
				Log.i("JungleeClick", "Upload Started !!");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				Log.i("JungleeClick", "Upload Succeeded : StatusCode="+statusCode);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
			{
				Log.i("JungleeClick", "Upload Failed : StatusCode="+statusCode);
			}

			@Override
			public void onRetry() {
				Log.i("JungleeClick", "Upload Retried !!");
			}

			@Override
			public void onProgress(int bytesWritten, int totalSize) {
				Log.i("JungleeClick", "Upload In Progress !!");
			}

			@Override
			public void onFinish() {
				Log.i("JungleeClick", "Upload Finished !!");
			}
		});
    }
}
