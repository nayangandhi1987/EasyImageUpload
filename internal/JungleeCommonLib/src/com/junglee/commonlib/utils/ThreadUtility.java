package com.junglee.commonlib.utils;

import android.os.Looper;

/**
 * ThreadUtility provides common methods to deal with threads.
 * <p> 
 * It provides commonly used functions that need to deal with threads, like checking if it is main thread, or to execute 
 * something in a background thread, etc.
 * 
 * @author      Nayan Gandhi <nggandhi@amazon.com>
 * @since       1.0
 */
public class ThreadUtility {
	/**
	 * Checks if the current execution unit is running on the main thread or not.
	 * @return true if it's on main thread, else false.
	 */
	public static boolean isItMainThread() {
		return (Looper.myLooper() == Looper.getMainLooper());
	}
	
	/**
	 * Executes a runnable in a background thread.
	 * @param r the given runnable object.
	 */
	public static void executeInBackground(Runnable r) {
		if(r != null) {
			Thread t = new Thread(r);
			t.start();
		}
	}
}
