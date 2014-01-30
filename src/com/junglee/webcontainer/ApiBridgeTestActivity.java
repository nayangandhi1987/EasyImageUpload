package com.junglee.webcontainer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.CalendarContract.EventsEntity;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.jungleeclick.R;
import com.junglee.commonlib.apibridge.ApiBridge;
import com.junglee.commonlib.apibridge.ApiBridgeHelper;
import com.junglee.commonlib.apibridge.INameSpace;
import com.junglee.commonlib.eventengine.ASyncEventHandler;
import com.junglee.commonlib.eventengine.EventEngine;
import com.junglee.commonlib.eventengine.SyncEventHandler;
import com.junglee.commonlib.utils.StringUtility;
import com.junglee.events.GlobalEventID;
import com.junglee.commonlib.location.LocationTracker;
import com.junglee.utils.GlobalStrings;

@SuppressLint("NewApi")
public class ApiBridgeTestActivity extends Activity implements INameSpace {
	static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 101;
	static final int SELECT_IMAGE_ACTIVITY_REQUEST_CODE = 102;	
	
	static final String MESSAGE_ID_PARAM = "messageId";
	static final String REQUEST_ID_PARAM = "requestId";
	static final String IMAGE_URI_PARAM = "imageUri";
	static final String LOCATION_PARAM = "location";
	
	private String fileNameTemplate = "junglee_cam_picture_<time-stamp>";
	private String fileName = null;
	private Uri imageUri = null;
	
	private WebView webview = null;
	
	private Map<Integer, String> requestCodeToId = null;
		
	private String wvLocalResourceSuffix = "_JungleeLocalRscSfx";
	
	ApiBridge apiController = null;
	private Handler handler = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_api_bridge_test);
		
		webview = (WebView) findViewById(R.id.wv);	
		
		webview.setWebViewClient(new WebViewClient() {

        	@Override
        	public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        		if(url != null && url.endsWith(wvLocalResourceSuffix)) {
        			url = StringUtility.getPathfromUrl(url);
        			String filePath = StringUtility.chopFromEnd(url, wvLocalResourceSuffix.length());
        			try {
        				WebResourceResponse response = new WebResourceResponse(
        						"application/javascript",
        						"UTF8",
        						new FileInputStream(filePath)
        						);
        				
        				return response;
        			} catch (IOException e) {
        				e.printStackTrace(); // Failed to load asset file
        			}
        		}
        		return super.shouldInterceptRequest(view, url);
        	}
        });		
		
		apiController = new ApiBridge(webview);
		apiController.attachNamespaceHandler(this, "PictureNamespace");
		apiController.attachNamespaceHandler(this, "LocationNamespace");
		
		String anEventType = "Type_X";
		SyncEventHandler anEventHandler = new SyncEventHandler() {
			@Override
			public boolean handle(JSONObject eventData) {
				Log.i("JungleeClick", "Received an Event 'Type_X' from EventEngine!");
				return true;
			}
		};
		EventEngine.getInstance().register(anEventType, anEventHandler);
		EventEngine.getInstance().register(anEventType, anEventHandler);
		EventEngine.getInstance().register(anEventType, anEventHandler);
		EventEngine.getInstance().register(anEventType, anEventHandler);
		EventEngine.getInstance().register(anEventType, anEventHandler);
		
		requestCodeToId = new HashMap<Integer, String>();
		
		handler  = new Handler(){
    	    @Override
    	    public void handleMessage(Message msg){

    	    	int msgId = msg.getData().getInt(MESSAGE_ID_PARAM);
    	        switch(msgId){
    	        case GlobalEventID.LOCATION_FETCHED: {
    	        	String requestId = msg.getData().getString(REQUEST_ID_PARAM);
    	        	String location = msg.getData().getString(LOCATION_PARAM);    	            	
    	        	try {
    	        		JSONObject responseJson = new JSONObject();
    	        		responseJson.put(LOCATION_PARAM, location==null?"Unknown":location);
    	        		apiController.requestCompleted(requestId, responseJson.toString());
    	        	} catch (JSONException e) {
    	        		e.printStackTrace();
    	        	}   
    	        }
    	        break;

    	        case GlobalEventID.PICTURE_RECEIVED: {
    	        	String requestId = msg.getData().getString(REQUEST_ID_PARAM);
    	        	String imageUri = msg.getData().getString(IMAGE_URI_PARAM);    	            	
    	        	try {
    	        		JSONObject responseJson = new JSONObject();
    	        		responseJson.put(IMAGE_URI_PARAM, imageUri);
    	        		apiController.requestCompleted(requestId, responseJson.toString());
    	        	} catch (JSONException e) {
    	        		e.printStackTrace();
    	        	}    
    	        }
    	        break;
    	        }
    	    }
    	};
    	
    	loadImgUploadView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.api_bridge_test, menu);
		return true;
	}
	
	private void loadImgUploadView() {
		webview.loadUrl("file:///android_asset/ApiBridgeTest.html");
		//webview.loadUrl("http://192.168.0.225:8888/ImgUpload.html");
	}
	
	@Override
	public JSONObject processRequest(String namespace, String apiName,
			JSONObject request, String requestId,
			ApiBridge controller) {
		if(apiName.equalsIgnoreCase("takePicture")) {
			takePicture(requestId);
			return ApiBridgeHelper.jsonWithReqInProcess();
		} else if(apiName.equalsIgnoreCase("pickFromGallery")) {
			pickFromGallery(requestId);
			return ApiBridgeHelper.jsonWithReqInProcess();
		} else if(apiName.equalsIgnoreCase("fetchLocation")) {
			fetchLocation(requestId);
			return ApiBridgeHelper.jsonWithReqInProcess();
		}
		
		int errCode = ApiBridgeHelper.ERROR_CODE_METHOD_NOT_SUPPORTED_BY_NAMESPACE;
		String errMsg = String.format(ApiBridgeHelper.ERROR_MSG_METHOD_NOT_SUPPORTED_BY_NAMESPACE, apiName, namespace);
		return ApiBridgeHelper.jsonWithError(errMsg, errCode);
	}

	@Override
	public String getDefaultName() {
		return "NativeAPI";
	}

	private void takePicture(String requestId) {
		//define the file-name to save photo taken by Camera activity
    	fileName = fileNameTemplate.replaceFirst("<time-stamp>", String.valueOf(System.currentTimeMillis()));
    	//create parameters for Intent with filename
    	ContentValues values = new ContentValues();
    	values.put(MediaStore.Images.Media.TITLE, fileName);
    	values.put(MediaStore.Images.Media.DESCRIPTION, GlobalStrings.CAPTURED_IMG_DESC);
    	//imageUri is the current activity attribute, define and save it for later usage (also in onSaveInstanceState)
    	
    	imageUri = getContentResolver().insert(
    	        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    	
    	requestCodeToId.put(CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE, requestId);
    	
    	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    	intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
    	intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
    	startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}
	private void pickFromGallery(String requestId) {
		requestCodeToId.put(SELECT_IMAGE_ACTIVITY_REQUEST_CODE, requestId);
		
		imageUri = null;
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
    	startActivityForResult(intent, SELECT_IMAGE_ACTIVITY_REQUEST_CODE);
	}
	
	private void fetchLocation(final String requestId) {
		Runnable runnable = new Runnable()
		{
		    @Override
		    public void run()
		    {
		    	LocationTracker locationTracker = new LocationTracker(ApiBridgeTestActivity.this, false);
		    	String locationString = locationTracker.getReadableLocation();

		    	Message msgObj = handler.obtainMessage();
		    	Bundle b = new Bundle();
		    	b.putString(REQUEST_ID_PARAM, requestId);
		    	b.putString(LOCATION_PARAM, locationString);
		    	b.putInt(MESSAGE_ID_PARAM, GlobalEventID.LOCATION_FETCHED);
		    	msgObj.setData(b);
		    	handler.sendMessage(msgObj);		           
		    }
		};
		Thread thread = new Thread(runnable);
        thread.start();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE 
				|| requestCode == SELECT_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {

				if(requestCode == SELECT_IMAGE_ACTIVITY_REQUEST_CODE) {
					if(data != null) {
						imageUri = data.getData();
					}
				}			
			} else {
				imageUri = null;
			}
			
			Message msgObj = handler.obtainMessage();
			Bundle b = new Bundle();
			b.putString(REQUEST_ID_PARAM, requestCodeToId.get(requestCode));
			b.putString(IMAGE_URI_PARAM, (imageUri!=null)?imageUri.toString():null);
			b.putInt(MESSAGE_ID_PARAM, GlobalEventID.PICTURE_RECEIVED);
			msgObj.setData(b);
			handler.sendMessage(msgObj);
		}
	}
}
