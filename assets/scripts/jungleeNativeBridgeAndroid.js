// NOTE: JungleeNativeBridge.js should be included before this js
jungleeNative.__nativeCall = function(requestId, jsonStringParam) {
    // Android specific native api invocation
    console.log('Calling window.jungleeNativeApp.processRequest('+requestId+', '+jsonStringParam+')');
    window.jungleeNativeApp.processRequest(requestId, jsonStringParam);
    console.log('Sent Request: window.jungleeNativeApp.processRequest()');
}