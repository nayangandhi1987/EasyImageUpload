package com.junglee.utils;

public class Utility {
	public static String extractFilenameWithExtn(String filepath) {
		return extractFilename(filepath, true);
	}
	public static String extractFilename(String filepath) {
		return extractFilename(filepath, false);
	}
	public static String extractFilename(String filepath, boolean keepExtn) {
		if(filepath==null || filepath.length()==0) {
			return filepath;
		}
		
		if(!keepExtn && filepath.contains(".")) {
			int dotIdx = filepath.lastIndexOf(".");
			filepath = filepath.substring(0, dotIdx);
		}
		
		String[] parts = filepath.split("/");
		return parts[parts.length-1];
	}
	
	public static String filepathToUrl(String filepath) {
		return "file://"+filepath;
	}
}
