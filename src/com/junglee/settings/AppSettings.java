package com.junglee.settings;

public class AppSettings {
	private static AppSettings instance = null;
	
	private final String UPLOAD_SERVER = "http://192.168.0.225:8888";
	private final String UPLOAD_ACTION = "upload";
	
	protected AppSettings() {
		// Exists only to defeat instantiation.
	}
	
	public static AppSettings getInstance() {
		if(instance == null) {
			instance = new AppSettings();
		}
		
		return instance;
	}
	
	public String getUploadServer() {
		return UPLOAD_SERVER;
	}
	public String getUploadAction() {
		return UPLOAD_ACTION;
	}

}
