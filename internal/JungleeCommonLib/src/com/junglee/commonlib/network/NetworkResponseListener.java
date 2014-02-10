package com.junglee.commonlib.network;

public abstract class NetworkResponseListener {
	public abstract void onRequestCompleted(String reqId, NetworkResponse response);
}
