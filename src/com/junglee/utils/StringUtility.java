package com.junglee.utils;

public class StringUtility {
	public static boolean isBlank(String str) {
		return (str==null || str.length()==0);
	}
	public static boolean isPopulated(String str) {
		return !isBlank(str);
	}
}
