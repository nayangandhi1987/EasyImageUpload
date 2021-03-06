package com.junglee.commonlib.apibridge;

import org.json.JSONObject;

/**
 * INamespace is an interface any api provider must implement to work with ApiBridge.
 * <p> 
 * Whenever javascript requests ApiBridge to execute a native api, it will specify the namespace, the api name, 
 * and the required parameters. The specified namespace will then be required to actually process the request. 
 * Once the request has been processed, the Api Bridge will take the responsibility to submit the result back 
 * to the javascript.
 * 
 * @author      Nayan Gandhi <nggandhi@amazon.com>
 * @since       1.0
 */
public interface INameSpace {
	/**
	 * Process the request made by the javascript running in the webview.
	 * @param namespace name of the namespace
	 * @param apiName the api or the method name
	 * @param request the request params
	 * @param requestId the request identifier
	 * @param controller the api bridge controller
	 * @return json object as response of the request
	 */
	JSONObject processRequest(String namespace, String apiName, JSONObject request, String requestId, ApiBridge controller);
	
	/**
	 * Gets the default name for this namespace api provider.
	 * @return the default name for namespace
	 */
	String getDefaultName();
}

