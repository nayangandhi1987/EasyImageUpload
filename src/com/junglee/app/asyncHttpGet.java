package com.junglee.app;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

public class asyncHttpGet extends AsyncTask<asyncHttpRequestTpe, Integer, Double>{
	  private String serverResponse;
	  private asyncHttpRequestTpe currentRequest;
	  private boolean done;
	  
	  public boolean isDone() {
		return done;
	}

	public void setIsDone(boolean mIsDone) {
		this.done = mIsDone;
	}

	public String getServerResponse() {
		return serverResponse;
	}

	public void setServerResponse(String serverResponse) {
		this.serverResponse = serverResponse;
	}

	@Override
	  protected Double doInBackground(asyncHttpRequestTpe... params) {
		// TODO Auto-generated method stub
		this.currentRequest = params[0];
		callUrl(this.currentRequest.urlToCall);
		return null;
	  }
	
	  public static String getServerResponseString(String sUrl){
		  	HttpClient httpclient = new DefaultHttpClient();
		    HttpResponse response;
		    String sOutput = "";
			try {
				response = httpclient.execute(new HttpGet(sUrl));
				StatusLine statusLine = response.getStatusLine();
			    if(statusLine.getStatusCode() == HttpStatus.SC_OK){
			        ByteArrayOutputStream out = new ByteArrayOutputStream();
			        response.getEntity().writeTo(out);
			        out.close();
			        sOutput = out.toString();
			    } else{
			        //Closes the connection.
			        response.getEntity().getContent().close();
			    }
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			return sOutput;
	  }

	  public void callUrl(String sUrl) {
		  	this.serverResponse = "";
		  	this.done = false;
			
	        this.serverResponse = getServerResponseString(sUrl);
	        this.done = true;
	        if(this.currentRequest.callBackToUse != null){
	        	this.currentRequest.callBackToUse.onSuccess(this.serverResponse);
	        }

		}
	  
	
}


