package com.junglee.commonlib.utils;

public class StringUtility {
	public static boolean isBlank(String str) {
		return (str==null || str.length()==0);
	}
	public static boolean isPopulated(String str) {
		return !isBlank(str);
	}
	
	public static String chopFromStart(String str, int len) {
		if(str.length() >= len) {
			return str.substring(len, str.length());
		}
		
		return str;
	}	
	public static String chopFromEnd(String str, int len) {
		if(str.length() >= len) {
			return str.substring(0, str.length()-len);
		}
		
		return str;
	}
	public static String getPathfromUrl(String url) {
		if(isPopulated(url)) {
			int idx = url.indexOf("://");
			if(idx > -1) {
				idx = url.indexOf('/', idx+3);
				if(idx > -1) {
					return url.substring(idx, url.length());
				}
			}
		}
		
		return null;
	}
	public static String getProtocolfromUrl(String url) {
		if(isPopulated(url)) {
			int idx = url.indexOf("://");
			if(idx > -1) {
				return url.substring(0, idx);
			}
		}
		
		return null;
	}
	public static String getServerNamefromUrl(String url) {
		if(isPopulated(url)) {
			int idx1 = url.indexOf("://");
			if(idx1 > -1) {
				int idx2 = url.indexOf('/', idx1+3);
				if(idx2 > -1) {
					return url.substring(idx1+3, idx2);
				}
			}
		}
		
		return null;
	}
}
