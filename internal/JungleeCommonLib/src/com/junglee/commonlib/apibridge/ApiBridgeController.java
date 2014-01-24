package com.junglee.commonlib.apibridge;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.junglee.commonlib.apibridge.INameSpace;
import com.junglee.commonlib.utils.StringUtility;

public class ApiBridgeController {
	class ApiBridge {
		public Map<String, INameSpace> namespaces;
		public WebView webview;
	}
	
	private static String DEFAULT_NATIVE_JS_OBJECT_NAME = "jungleeNativeApp";
	private static String DEFAULT_JS_CONTROLLER_FNNAME = "jungleeNative.__callback";
	private Handler uiHandler = null;
	
	ApiBridge apiBridge = null;	
	
	
	//----------------------------------- Message processing related definition -----------------------------------//	
	private static String MSG_TYPE_ATTRIBUTE_NAMESPACE = "namespace";
	private static String MSG_TYPE_ATTRIBUTE_METHOD = "method";
	private static String MSG_TYPE_ATTRIBUTE_PARAMAETER = "parameter";
	
	//----------------------------------------- Publish related messages ------------------------------------------//
	private static String MSG_TYPE_ATTRIBUTE_NAME = "messageName";
	private static String MSG_TYPE_ATTRIBUTE_REQUEST_ID = "requestId";
	private static String MSG_TYPE_ATTRIBUTE_JSON_PAYLOAD = "jsonPayload";
	private static String MSG_CALL_JS  = "jsCallBack"; 
	private static final Set<String> SUPPORTED_MESSAGE_SET = new HashSet<String>(
																 Arrays.asList(MSG_CALL_JS
																 ));
	
	
	public ApiBridgeController(WebView webview) {
		if(webview != null) {
			webview.getSettings().setJavaScriptEnabled(true);
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
	                Log.i("ApiBridgeController", "Console:-- " + cm.message() + " -- From line " + cm.lineNumber()
	                        + " of -- " + cm.sourceId());
	                return true;
	            }
	        });
		}
		
		apiBridge = new ApiBridge();
		apiBridge.webview = webview;
		apiBridge.webview.addJavascriptInterface(this, DEFAULT_NATIVE_JS_OBJECT_NAME);
		apiBridge.namespaces = new HashMap<String, INameSpace>();		
		
		uiHandler = new Handler() {
    	    @Override
    	    public void handleMessage(Message msg) {
    	    	if(msg != null) {
    	    		Bundle bundle = msg.getData();
    	    		String messageType = bundle.getString(MSG_TYPE_ATTRIBUTE_NAME);
    	    		if(StringUtility.isPopulated(messageType) && SUPPORTED_MESSAGE_SET.contains(messageType)){
    	    			// So it's one of the message this controller supports
    	    			if(messageType.equals(MSG_CALL_JS)){
    	    				handleCallToJS(bundle);
    	    			} /*
    						Other message handler comes here
    	    			  */    				
    	    		}
    	    	}
    	    }
    	};
	}
	
	/*public void attachNamespaceHandler(INameSpace namespace) {
		if(namespace != null) {
			attachNamespaceHandler(namespace, namespace.getDefaultName());
		}
	}*/
	public void attachNamespaceHandler(INameSpace namespace, String name) {
		
		if(namespace == null) return;
		
		if(StringUtility.isBlank(name)) {
			name = namespace.getDefaultName();
		}
		
		apiBridge.namespaces.put(name, namespace);
		
//		if(apiBridge.webview != null) {
//			apiBridge.webview.addJavascriptInterface(this, name);			
//		}
	}
	public void detachNamespaces(String name) {
		if(StringUtility.isPopulated(name) && apiBridge.namespaces.containsKey(name)) {
			apiBridge.namespaces.remove(name);
//			if(apiBridge.webview != null) {
//				apiBridge.webview.addJavascriptInterface(null, name);
//			}
		}
	}
	public boolean isNamespaceSupported(String name){
		return apiBridge.namespaces.containsKey(name);
	}
	
	@JavascriptInterface
	public void jsHello() {
		Log.i("JungleeClick", "Hello from Javascript");
	}
	@JavascriptInterface
	public void processRequest(final String requestId, final String request){
		Log.i("JungleeClick", "Process Request from Javascript");
		try {
			Log.i("JungleeClick", "Process Request: requestId="+requestId+", request="+request);
			JSONObject jsonReq = new JSONObject(request);
			String desiredNamespace = jsonReq.getString(MSG_TYPE_ATTRIBUTE_NAMESPACE);
			String desiredMethod = jsonReq.getString(MSG_TYPE_ATTRIBUTE_METHOD);
			JSONObject suppliedParam = jsonReq.getJSONObject(MSG_TYPE_ATTRIBUTE_PARAMAETER);
			if(StringUtility.isBlank(desiredNamespace)) {
				// These needs to be handled by the bridge controller
				if(SUPPORTED_MESSAGE_SET.contains(desiredMethod)){
					// TODO
				} else {
					int errCode = ApiBridgeHelper.ERROR_CODE_METHOD_NOT_SUPPORTED_BY_DEFAULT_NAMESPACE;
					String errMsg = String.format(ApiBridgeHelper.ERROR_MSG_METHOD_NOT_SUPPORTED_BY_DEFAULT_NAMESPACE, desiredMethod);
					publishResult(requestId, ApiBridgeHelper.jsonWithError(errMsg, errCode).toString());
				}
			} else if(apiBridge.namespaces.containsKey(desiredNamespace)) {
				INameSpace requestHandler = apiBridge.namespaces.get(desiredNamespace);
				JSONObject jsonResult = requestHandler.processRequest(desiredNamespace,
						desiredMethod,
						suppliedParam, requestId, ApiBridgeController.this);
				if(!ApiBridgeHelper.isRequestInProcess(jsonResult)) {
					String jsonStringResult = jsonResult.toString();
					publishResult(requestId, jsonStringResult);
				}
			} else {
				int errCode = ApiBridgeHelper.ERROR_CODE_NAMESPACE_NOT_SUPPORTED;
				String errMsg = String.format(ApiBridgeHelper.ERROR_MSG_NAMESPACE_NOT_SUPPORTED, desiredNamespace);
				publishResult(requestId, ApiBridgeHelper.jsonWithError(errMsg, errCode).toString());
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public void requestCompleted(String requestId, String response) {
		publishResult(requestId, response);
	}
	
	public void publishResult(String requestId, String response) {		
		if(uiHandler != null) {
			Message msgObj = uiHandler.obtainMessage();
	        Bundle b = new Bundle();
	        b.putString(MSG_TYPE_ATTRIBUTE_NAME, MSG_CALL_JS);
	        b.putString(MSG_TYPE_ATTRIBUTE_REQUEST_ID, requestId);
	        b.putString(MSG_TYPE_ATTRIBUTE_JSON_PAYLOAD, response);
	        msgObj.setData(b);
	        uiHandler.sendMessage(msgObj);
		}
	}
	
	public void handleCallToJS(Bundle bundle) {
		String sData = "";
		try {
			String callbackFunc = DEFAULT_JS_CONTROLLER_FNNAME;
			sData = String.format("\"%s\"", URLEncoder.encode(bundle.getString(MSG_TYPE_ATTRIBUTE_JSON_PAYLOAD), "utf-8"));
			String jsCode = String.format("%s('%s', %s)", callbackFunc
					, bundle.getString(MSG_TYPE_ATTRIBUTE_REQUEST_ID)
					, sData);
			executeJsCode(jsCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void executeJsCode(String jsCode) {
		if(apiBridge.webview != null && StringUtility.isPopulated(jsCode)) {
			String sUrlToCall = String.format("javascript: %s", jsCode);
			apiBridge.webview.loadUrl(sUrlToCall);
		} else {
			// No webview is attached
		}
	}
	
	private String convertToJsonMsg(String msg, String key) {
		return String.format("{\"%s\": \"%s\"}", key, msg);
	}
}
