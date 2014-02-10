package com.junglee.commonlib.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.NameValuePair;

public class UrlUtility {
	/**
	 * Returns the path component from the url string.
	 * @param url the input url string.
	 * @return the path component of the url string.
	 */
	public static String getPathfromUrl(String url) {
		if(StringUtility.isPopulated(url)) {
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
	/**
	 * Returns the protocol component from the url string.For eg, "http"
	 * @param url the input url string.
	 * @return the protocol component of the url string.
	 */
	public static String getProtocolfromUrl(String url) {
		if(StringUtility.isPopulated(url)) {
			int idx = url.indexOf("://");
			if(idx > -1) {
				return url.substring(0, idx);
			}
		}
		
		return null;
	}
	/**
	 * Returns the server name component from the url string.For eg, "http"
	 * @param url the input url string.
	 * @return the server name component of the url string.
	 */
	public static String getServerNamefromUrl(String url) {
		if(StringUtility.isPopulated(url)) {
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
	
	/**
	 * Generates the url query string from the list of name-value pairs.
	 * @param params query params as name-value pairs
	 * @return query string
	 * @throws UnsupportedEncodingException
	 */
	public static String getQuery(List<NameValuePair> params)
			throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();
		boolean first = true;
		for (NameValuePair pair : params) {
			if (first)
				first = false;
			else
				result.append("&");
			result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
		}

		return result.toString();
	}
}
