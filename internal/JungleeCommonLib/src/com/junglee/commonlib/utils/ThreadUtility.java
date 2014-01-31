package com.junglee.commonlib.utils;

import android.os.Looper;

public class ThreadUtility {
	public static boolean isItMainThread() {
		return (Looper.myLooper() == Looper.getMainLooper());
	}
	
	public static void executeInBackground(Runnable r) {
		if(r != null) {
			Thread t = new Thread(r);
			t.start();
		}
	}
}
