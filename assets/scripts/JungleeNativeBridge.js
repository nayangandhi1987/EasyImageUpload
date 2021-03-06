/* maintain a callback table of 'id' -> function handler */
var __jn_callBackTable = {}, 
jungleeNative = {
"init": function(){
	// Register with native as the javascript controller
	// .registerJavascriptController
},




"execute": function(namespace, methodName, fnCallBack, jsonParameter){
	var callBackId = namespace + "_" + methodName;
	
	//if(__jn_callBackTable[callBackId] != undefined){
	//	throw "cannot make  " + callBackId + "api call as an older call for same api is in progress";
	//}
	
	__jn_callBackTable[callBackId] = fnCallBack;
	var jsonPayload = {
		"namespace" : namespace,
		"method" : methodName,
		"callbackId" : callBackId,
		"parameter": jsonParameter
	}
	var sData = JSON.stringify(jsonPayload);
    console.log('Calling this.__nativeCall('+callBackId+', '+sData+')');
	this.__nativeCall(callBackId, sData);
    console.log('Sent Request: this.__nativeCall()');
},





"raiseEvent": function(eventType, jsonParameter){
    var jsonPayload = {
		"type" : eventType,
		"parameter": jsonParameter
	}
	var sData = JSON.stringify(jsonPayload);
	window.jungleeNativeApp.sendEvent(sData);
},




// This is for cases where native needs to raise an event for the javascript layer to handle
"registerJSEventHandler": function(jsNamespace, eventName, fnEventHandler){
},




/* This method needs to be implemented per platform.
   Note: Least denominator is to keep all parameters as string
   No return value expected. 
*/ 
"__nativeCall": function(requestId, jsonStringParam){
    console.log('Calling window.jungleeNativeApp.processRequest('+requestId+', '+jsonStringParam+')');
    window.jungleeNativeApp.processRequest(requestId, jsonStringParam);
    console.log('Sent Request: window.jungleeNativeApp.processRequest()');

},




/*
 * This method is the js-controller's callback 
 */	
"__callback": function(requestId,jsonStringParam){
	console.log('Calling __callback()');
	console.log('Calling __callback('+requestId+', '+jsonStringParam+')');
	if(__jn_callBackTable[requestId] != undefined){
		var sMsg = decodeURIComponent(jsonStringParam);
		var fnCallBack = __jn_callBackTable[requestId], resultParam;
		__jn_callBackTable[requestId] = undefined;
		resultParam = JSON.parse(sMsg);
		console.log('Making the actual function call - the one found in callback table corresponding to '+requestId);
		fnCallBack(resultParam);
		console.log('Made the actual function call');
	} else {
		console.log('No function found corresponding to '+requestId+' in the callback table!');
	}
}	
};




jungleeNative.init();