package com.junglee.init;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Rect;
import android.view.Gravity;

public class HelpScreenUIControlParams {
	
	public HelpScreenUIControlParams() {
		initDefaultValues();
	}

	public enum IMAGE_TYPE {
		NONE,
		CUSTOM,
		SINGLE_TAP,
		DOUBLE_TAP,
		PRESS_AND_HOLD,
		SWIPE_UP,
		SWIPE_DOWN,
		SWIPE_LEFT,
		SWIPE_RIGHT,
		SPREAD,
		PINCH
	}
	
	public enum IMG_PLACEMENT {
		CENTER,
		LEFT,
		RIGHT,
		TOP,
		BOTTOM,
		TOP_LEFT,
		BOTTOM_LEFT,
		TOP_RIGHT,
		BOTTOM_RIGHT
	}
	
	public enum TXT_RELATIVE_PLACEMENT {
		BELOW,
		ABOVE,
		LEFT,
		RIGHT
	}

	public IMAGE_TYPE imgType;
	public String customImgId;
	public boolean useImgCentreAsRef;
	public String text;
	public String textColor;
	public float textSize;
	public float minXFactor;
	public float maxXFactor;
	public Rect viewRect;
	public IMG_PLACEMENT imgPlacement;
	public TXT_RELATIVE_PLACEMENT txtPlacement;
	public int txtGravity;
	
	public HelpScreenUIControlParams(JSONObject json) {
		initDefaultValues();
		
		try {
			if(json.has("IMG_TYPE"))
				imgType = IMAGE_TYPE.values()[json.getInt("IMG_TYPE")];
			if(json.has("CUSTOM_IMG_ID"))
				customImgId = json.getString("CUSTOM_IMG_ID");
			if(json.has("IMG_CENTRE_AS_REF"))
				useImgCentreAsRef = json.getBoolean("IMG_CENTRE_AS_REF");
			if(json.has("IMG_PLACEMENT"))
				imgPlacement = IMG_PLACEMENT.values()[json.getInt("IMG_PLACEMENT")];
			if(json.has("TXT"))
				text = json.getString("TXT");
			if(json.has("TXT_CLR"))
				textColor = json.getString("TXT_CLR");
			if(json.has("TXT_SZ"))
				textSize = (float) json.getDouble("TXT_SZ");
			if(json.has("TXT_MIN_X_FACTOR"))
				minXFactor = (float) json.getDouble("TXT_MIN_X_FACTOR");
			if(json.has("TXT_MAX_X_FACTOR"))
				maxXFactor = (float) json.getDouble("TXT_MAX_X_FACTOR");
			if(json.has("TXT_RELATIVE_PLACEMENT"))
				txtPlacement = TXT_RELATIVE_PLACEMENT.values()[json.getInt("TXT_RELATIVE_PLACEMENT")];
			if(json.has("TEXT_GRAVITY"))
				txtGravity = json.getInt("TEXT_GRAVITY");
		} catch (JSONException e) {
			e.printStackTrace();
		}   	
	}
	
	private void initDefaultValues() {
		imgType = HelpScreenUIControlParams.IMAGE_TYPE.SINGLE_TAP;
    	imgPlacement = HelpScreenUIControlParams.IMG_PLACEMENT.CENTER;
    	useImgCentreAsRef = false;
    	textColor = "#FFFFFF";
    	textSize = 16.0f;
    	txtGravity = Gravity.CENTER;
    	txtPlacement = HelpScreenUIControlParams.TXT_RELATIVE_PLACEMENT.BELOW; 
    	minXFactor = 0.0f;
    	maxXFactor = 1.0f;
	}

	@Override
	public String toString() {
		return "HelpScreenUIControlParams [imgType=" + imgType
				+ ", customImgId=" + customImgId + ", useImgCentreAsRef="
				+ useImgCentreAsRef + ", text=" + text + ", textColor="
				+ textColor + ", textSize=" + textSize + ", minXFactor="
				+ minXFactor + ", maxXFactor=" + maxXFactor + ", viewRect="
				+ viewRect + ", imgPlacement=" + imgPlacement
				+ ", txtPlacement=" + txtPlacement + ", txtGravity="
				+ txtGravity + "]";
	}
}
