package com.junglee.data;

import java.util.List;

import com.junglee.init.HelpScreenUIControlParams;

/**
 * IntentData provides a way to sharing data between the activities, when one activity starts another activity through 
 * an intent. It helps to avoid the need to serialize and then deserialize the data being shared between those activities.
 * 
 * @author      Nayan Gandhi <nggandhi@amazon.com>
 * @since       1.0
 */
public class IntentData {
	private static IntentData instance = null;
	
	private List<HelpScreenUIControlParams> helpScrData = null;
	private String helpScrId = null;
	
	
	protected IntentData() {
		// Exists only to defeat instantiation.
	}	
	public static IntentData getInstance() {
		if(instance == null) {
			instance = new IntentData();
		}
		
		return instance;
	}
	
	
	
	public void setHelpScreenData(List<HelpScreenUIControlParams> data, String helpId) {
		helpScrData = data;
		helpScrId = helpId;
	}
	public List<HelpScreenUIControlParams> getHelpScreenData(String id) {
		if(helpScrId != null && helpScrId.equalsIgnoreCase(id)) {
			return helpScrData;
		}
		
		return null;
	}
}
