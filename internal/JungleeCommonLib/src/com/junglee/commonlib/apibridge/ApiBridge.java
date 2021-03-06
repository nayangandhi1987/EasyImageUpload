package com.junglee.commonlib.apibridge;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.junglee.commonlib.eventengine.EventEngine;
import com.junglee.commonlib.logging.Logger;
import com.junglee.commonlib.utils.StringUtility;

/**
 * ApiBridge is bound to a webview, and it sets up a communication bridge between the javascript and the native app.
 * <p> 
 * Different api providers need to implement the INamespace interface. Multiple namespaces can be attached to (or 
 * detached from) the webview at any time.
 * <p>
 * Javascript can request any native api to be executed by calling the processRequest() method, specifying 
 * the namespace, the api name, along with the required parameters, and a callback function for submitting 
 * the result. The specified namespace will  then execute the request, and the result will be submitted back 
 * asynchronously using publishResult() method which will internally cause the handleCallToJS() method to be 
 * invoked.
 * <p>
 * Javacript can fire various events by calling sendEvent() method, specifying the type of event, along with 
 * the event parameters. The event will then be broadcasted through EventEngine, and any registered listeners 
 * for that event will be notified of the event.
 * <p>
 * The executeJsCode() method can be called to directly execute some javascript code.
 * 
 * @author      Nayan Gandhi <nggandhi@amazon.com>
 * @since       1.0
 */
public class ApiBridge {
	private static final String TAG = "ApiBridge";
	
	private static String DEFAULT_NATIVE_JS_OBJECT_NAME = "jungleeNativeApp";
	private static String DEFAULT_JS_CONTROLLER_FNNAME = "jungleeNative.__callback";
	private Handler handler = null;
		
	public Map<String, INameSpace> namespaces;
	public WebView webview;
	
	
	//----------------------------------- Message processing related definition -----------------------------------//	
	private static String MSG_TYPE_ATTRIBUTE_NAMESPACE = "namespace";
	private static String MSG_TYPE_ATTRIBUTE_METHOD = "method";
	private static String MSG_TYPE_ATTRIBUTE_PARAMAETER = "parameter";
	
	//----------------------------------------- Publish related messages ------------------------------------------//
	private static String MSG_TYPE_ATTRIBUTE_NAME = "messageName";
	private static String MSG_TYPE_ATTRIBUTE_REQUEST_ID = "requestId";
	private static String MSG_TYPE_ATTRIBUTE_JSON_PAYLOAD = "jsonPayload";
	private static String MSG_CALL_JS  = "jsCallBack"; 
	
	//------------------------------------- Event Handling related definition -------------------------------------//
	private static String EVENT_TYPE_ATTRIBUTE_TYPE = "type";
	private static String EVENT_TYPE_ATTRIBUTE_CONTEXT = "context";
	private static String EVENT_TYPE_ATTRIBUTE_RECEIVER = "receivers";
	private static String EVENT_TYPE_ATTRIBUTE_PARAMAETER = "parameter";
	
	private static final Set<String> SUPPORTED_MESSAGE_SET = new HashSet<String>(
																 Arrays.asList(MSG_CALL_JS
																 ));
	
	/**
	 * The constructor for the api bridge.
	 * @param webview the webview that will be boud to this api bridge
	 */
	public ApiBridge(WebView webview) {
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
	            	Logger.info(TAG, "Console Log", 
	                		String.format("%s -- (from line# %d of source# %s)", 
	                				cm.message(), 
	                				cm.lineNumber(), 
	                				cm.sourceId()));
	                return true;
	            }
	        });
		}
		
		this.webview = webview;
		this.webview.addJavascriptInterface(this, DEFAULT_NATIVE_JS_OBJECT_NAME);
		this.namespaces = new HashMap<String, INameSpace>();		
		
		handler = new Handler() {
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
	
	/**
	 * Attaches a namespace api provider to the webview. Javascript can refer to this namespace for making requests to the native app.
	 * @param namespace the namespace api provider
	 * @param name the name of the namespace. Javascript will use this name when making a request to the native app.
	 */
	public void attachNamespaceHandler(INameSpace namespace, String name) {
		
		if(namespace == null) return;
		
		if(StringUtility.isBlank(name)) {
			name = namespace.getDefaultName();
		}
		
		this.namespaces.put(name, namespace);
	}
	/**
	 * Detaches a namespace api provider to the webview.
	 * @param name the name of the namespace.
	 */
	public void detachNamespaces(String name) {
		if(StringUtility.isPopulated(name) && this.namespaces.containsKey(name)) {
			this.namespaces.remove(name);
		}
	}
	public boolean isNamespaceSupported(String name){
		return this.namespaces.containsKey(name);
	}
	
	@JavascriptInterface
	public void jsHello() {
		Logger.info(TAG, "Hello from Javascript");
	}
	
	/**
	 * Javascript calls this function to make a request to the native app. When the request completes, the result will
     * be submitted back to the javascript asynchronously.
	 * @param requestId the identifier for the request. javascript maps it to a callback function
	 * @param request the actual request in the form of a json string containing namespace, api name, 
     * 				  and request params as json.
	 */
	@JavascriptInterface
	public void processRequest(final String requestId, final String request){
		try {
			Logger.info(TAG, "Process Request: requestId="+requestId+", request="+request);
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
			} else if(this.namespaces.containsKey(desiredNamespace)) {
				INameSpace requestHandler = this.namespaces.get(desiredNamespace);
				JSONObject jsonResult = requestHandler.processRequest(desiredNamespace,
						desiredMethod,
						suppliedParam, requestId, ApiBridge.this);
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
	
	/**
	 * Javascript calls this function to notify native app about an event happened inside the webview.
	 * @param event the actual event in the form of a json string containing the event type and the event params as json.
	 */
	@JavascriptInterface	
	public void sendEvent(String event) {		
		JSONObject eventJson;
		try {
			Logger.info(TAG, "sendEvent: "+event);
			eventJson = new JSONObject(event);
			String eventType = eventJson.getString(EVENT_TYPE_ATTRIBUTE_TYPE);
			JSONObject eventData = eventJson.getJSONObject(EVENT_TYPE_ATTRIBUTE_PARAMAETER);
			EventEngine.getInstance().fireEvent(eventType, eventData);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	public void requestCompleted(String requestId, String response) {
		publishResult(requestId, response);
	}
	
	/**
	 * Used to submit the result of a request initiated from the javascript. 
	 * @param requestId the identifier for the request. javascript maps it to a callback function
	 * @param response the response data as json for the given request
	 */
	public void publishResult(String requestId, String response) {		
		if(handler != null) {
			Message msgObj = handler.obtainMessage();
	        Bundle b = new Bundle();
	        b.putString(MSG_TYPE_ATTRIBUTE_NAME, MSG_CALL_JS);
	        b.putString(MSG_TYPE_ATTRIBUTE_REQUEST_ID, requestId);
	        b.putString(MSG_TYPE_ATTRIBUTE_JSON_PAYLOAD, response);
	        msgObj.setData(b);
	        handler.sendMessage(msgObj);
		}
	}
	
	/**
	 * Used to make the callback to the javascript for a request along with the result json data
	 * @param bundle the Bundle object containing the requestId and the json paylod (i.e the result data for the request: requestId)
	 */
	public void handleCallToJS(Bundle bundle) {
		try {
			String callbackFunc = DEFAULT_JS_CONTROLLER_FNNAME;
			String sData = String.format("%s", URLEncoder.encode(bundle.getString(MSG_TYPE_ATTRIBUTE_JSON_PAYLOAD), "utf-8"));
			String jsCode = String.format("%s('%s', '%s')", callbackFunc
					, bundle.getString(MSG_TYPE_ATTRIBUTE_REQUEST_ID)
					, sData);
			executeJsCode(jsCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Executes the specified javascript code inside the webview
	 * @param jsCode the javascript code (in the form of a string) to be executed in the webview
	 */
	public void executeJsCode(String jsCode) {
		if(this.webview != null && StringUtility.isPopulated(jsCode)) {
			String sUrlToCall = String.format("javascript: %s", jsCode);
			Logger.info(TAG, "ExecuteJSCode", sUrlToCall);
			this.webview.loadUrl(sUrlToCall);
		} else {
			// No webview is attached
		}
	}
}
