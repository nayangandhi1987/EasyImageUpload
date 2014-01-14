package com.junglee.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map.Entry;
 
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class HttpClientFileUploader {
    /**
     * A generic method to execute any type of Http Request and constructs a response object
     * @param requestBase the request that needs to be exeuted
     * @return server response as <code>String</code>
     */
    private static String executeRequest(HttpRequestBase requestBase){
        String responseString = "" ;
 
        InputStream responseStream = null ;
        HttpClient client = new DefaultHttpClient () ;
        try{
        	Log.i("JungleeClick", "Executing request ..");
            HttpResponse response = client.execute(requestBase) ;
            if (response != null){
                HttpEntity responseEntity = response.getEntity() ;
 
                if (responseEntity != null){
                    responseStream = responseEntity.getContent() ;
                    if (responseStream != null){
                        BufferedReader br = new BufferedReader (new InputStreamReader (responseStream)) ;
                        String responseLine = br.readLine() ;
                        String tempResponseString = "" ;
                        while (responseLine != null){
                            tempResponseString = tempResponseString + responseLine + System.getProperty("line.separator") ;
                            responseLine = br.readLine() ;
                        }
                        br.close() ;
                        if (tempResponseString.length() > 0){
                            responseString = tempResponseString ;
                        }
                    }
                }
            }
            Log.i("JungleeClick", "Request completed !!");
        } catch (UnsupportedEncodingException e) {
        	Log.i("JungleeClick", "UnsupportedEncodingException");
            e.printStackTrace();
        } catch (ClientProtocolException e) {
        	Log.i("JungleeClick", "ClientProtocolException");
            e.printStackTrace();
        } catch (IllegalStateException e) {
        	Log.i("JungleeClick", "IllegalStateException");
            e.printStackTrace();
        } catch (IOException e) {
        	Log.i("JungleeClick", "IOException");
            e.printStackTrace();
        }finally{
            if (responseStream != null){
                try {
                    responseStream.close() ;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        client.getConnectionManager().shutdown() ;
        
        Log.i("JungleeClick", "RESPONSE: "+responseString);
 
        return responseString ;
    }
 
    /**
     * Method that builds the multi-part form data request
     * @param urlString the urlString to which the file needs to be uploaded
     * @param file the actual file instance that needs to be uploaded
     * @param fileName name of the file, just to show how to add the usual form parameters
     * @param fileDescription some description for the file, just to show how to add the usual form parameters
     * @return server response as <code>String</code>
     */
    private String executeMultiPartRequest(String urlString, File file, String mimeType, HashMap<String, String> formParams) {
 
        HttpPost postRequest = new HttpPost (urlString);
        try{
        	Log.i("JungleeClick", "Creating Post Request for FileUpload ..");
            MultipartEntity multiPartEntity = new MultipartEntity () ;
 
            //The usual form parameters can be added this way
            for (Entry<String, String> entry : formParams.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                multiPartEntity.addPart(key, new StringBody(value)) ;
            }
 
            /*Need to construct a FileBody with the file that needs to be attached and specify the mime type of the file. Add the fileBody to the request as an another part.
            This part will be considered as file part and the rest of them as usual form-data parts*/
            FileBody fileBody = new FileBody(file, mimeType) ;
            multiPartEntity.addPart("file", fileBody);
 
            postRequest.setEntity(multiPartEntity) ;
            Log.i("JungleeClick", "Created Post Request for FileUpload !!");
        }catch (UnsupportedEncodingException ex){
            ex.printStackTrace() ;
        }
 
        return executeRequest (postRequest) ;
    }
    
    private String getMimeTypeForFile(File file) {
    	if(file!=null) {
    		String path = file.getAbsolutePath();
    		Log.i("JungleeClick", "Filepath: "+path);
    		String[] parts = path.split("/");
    		if(parts.length > 0) {
    			String filename = parts[parts.length-1];
    			Log.i("JungleeClick", "Filename: "+filename);
    			filename = filename.toLowerCase();
    			if(filename.endsWith("jpg") || filename.endsWith("jpeg"))
    				return "image/jpeg";
    		}
    	}
    	
    	return null;
    }
    public String uploadFile(String url, File file) {
    	HashMap<String, String> params = new HashMap<String, String>();
    	return executeMultiPartRequest(url, file, getMimeTypeForFile(file), params);
    }
}
