package com.junglee.webcontainer;

import java.io.FileInputStream;
import java.io.IOException;

import com.example.jungleeclick.R;
import com.junglee.utils.GlobalStrings;
import com.junglee.utils.StringUtility;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class JungleeWebContainerActivity extends Activity {
	private WebView webview = null;
	private JavaScriptInterface jsItfObj = new JavaScriptInterface();
	
	private String srcImgHighQ = "/storage/emulated/0/DCIM/Camera/1389254975689.jpg";
	private String srcImgMediumQ = "/storage/emulated/0/DCIM/Camera/1389255792213.jpg";
	private String srcImgLowQ = "/storage/emulated/0/DCIM/Camera/1389256774382.jpg";
	
	private String wvLocalResourceSuffix = "_JungleeLocalRscSfx";
	
	class JavaScriptInterface
    {
		@JavascriptInterface
		public void invokeCamera() {
			Log.i("JungleeClick", "renderHighQualityImg() Called!");			

	    	//define the file-name to save photo taken by Camera activity
	    	String fileName = "junglee_cam_picture_" + String.valueOf(System.currentTimeMillis());
	    	//create parameters for Intent with filename
	    	ContentValues values = new ContentValues();
	    	values.put(MediaStore.Images.Media.TITLE, fileName);
	    	values.put(MediaStore.Images.Media.DESCRIPTION, GlobalStrings.CAPTURED_IMG_DESC);
	    	//imageUri is the current activity attribute, define and save it for later usage (also in onSaveInstanceState)
	    	
	    	Uri imageUri = getContentResolver().insert(
	    	        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
	    	
	    	//create new Intent
	    	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    	intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
	    	intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
	    	startActivityForResult(intent, 0);
		}
		
		@JavascriptInterface
		public void renderHighQualityImg() {
			Log.i("JungleeClick", "renderHighQualityImg() Called!");			

			JungleeWebContainerActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					webview.loadUrl("javascript: document.getElementById('imgToUpload').src='"+toLocalAccessiblePath(srcImgHighQ)+"'");
				}
			});
		}
		
		@JavascriptInterface
		public void renderMediumQualityImg() {
			Log.i("JungleeClick", "renderMediumQualityImg() Called!");

			JungleeWebContainerActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					webview.loadUrl("javascript: document.getElementById('imgToUpload').src='"+toLocalAccessiblePath(srcImgMediumQ)+"'");
				}
			});
		}
		
		@JavascriptInterface
		public void renderLowQualityImg() {
			Log.i("JungleeClick", "renderLowQualityImg() Called!");

			JungleeWebContainerActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					webview.loadUrl("javascript: document.getElementById('imgToUpload').src='"+toLocalAccessiblePath(srcImgLowQ)+"'");
				}
			});
		}
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    
		setContentView(R.layout.activity_junglee_web_container);
		
		webview = (WebView) findViewById(R.id.web_container);		
		initWebview();

		loadImgUploadView();		
	}

	@SuppressLint("NewApi")
	private void initWebview() {
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setAllowFileAccess(true);
        webview.addJavascriptInterface(jsItfObj, "Native");
        
        webview.setWebChromeClient(new WebChromeClient()
        {

            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                    JsResult result)
            {
                return super.onJsAlert(view, url, message, result);
            }

            public boolean onConsoleMessage(ConsoleMessage cm)
            {
                Log.i("JungleeClick", "Console:-- " + cm.message() + " -- From line " + cm.lineNumber()
                        + " of -- " + cm.sourceId());
                return true;
            }
        });
        
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.junglee_web_container, menu);
		return true;
	}
	
	private void loadImgUploadView() {
		//webview.loadUrl("file:///android_asset/ImgUpload.html");
		webview.loadUrl("http://192.168.0.225:8888/ImgUpload.html");
	}

	private String toLocalAccessiblePath(String path) {
		if(StringUtility.isPopulated(path))
			return path+wvLocalResourceSuffix;
		else
			return path;
	}
}
