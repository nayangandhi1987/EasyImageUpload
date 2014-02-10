package com.junglee.commonlib.utils;


/**
 * StringUtility provides common methods to deal with strings.
 * <p> 
 * It provides commonly used functions that need to deal with strings, like checking if a string is populated or not, chopping characters from it, etc. 
 * All such common methods should go in this class, rather than every class implementing same thing over and over again.
 * 
 * @author      Nayan Gandhi <nggandhi@amazon.com>
 * @since       1.0
 */
public class StringUtility {
	/**
	 * Checks if the string is blank or not. Blank mean either null or empty.
	 * @param str the input string.
	 * @return true if the string is either null or empty, else false.
	 */
	public static boolean isBlank(String str) {
		return (str==null || str.length()==0);
	}
	/**
	 * Checks if the string is populated or not. Populated mean neither null nor empty.
	 * @param str the input string.
	 * @return true if the string is neither null nor empty, else false.
	 */
	public static boolean isPopulated(String str) {
		return !isBlank(str);
	}
	
	/**
	 * Chops or removes the given number of characters from the start of the given string.
	 * @param str the input string.
	 * @param len number of characters to chop.
	 * @return the string with characters chopped from front.
	 */
	public static String chopFromStart(String str, int len) {
		if(str.length() >= len) {
			return str.substring(len, str.length());
		}
		
		return str;
	}	
	/**
	 * Chops or removes the given number of characters from the end of the given string.
	 * @param str the input string.
	 * @param len number of characters to chop.
	 * @return the string with characters chopped from end.
	 */
	public static String chopFromEnd(String str, int len) {
		if(str.length() >= len) {
			return str.substring(0, str.length()-len);
		}
		
		return str;
	}
}
