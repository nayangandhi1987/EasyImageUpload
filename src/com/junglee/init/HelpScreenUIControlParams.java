package com.junglee.init;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Rect;
import android.view.Gravity;

public class HelpScreenUIControlParams {
	
	public HelpScreenUIControlParams() {
		imgType = HelpScreenUIControlParams.IMAGE_TYPE.SINGLE_TAP;
    	imgPlacement = HelpScreenUIControlParams.IMG_PLACEMENT.CENTER;
    	txtGravity = Gravity.RIGHT;
    	txtPlacement = HelpScreenUIControlParams.TXT_RELATIVE_PLACEMENT.BELOW;   	
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
	public Rect viewRect;
	public IMG_PLACEMENT imgPlacement;
	public TXT_RELATIVE_PLACEMENT txtPlacement;
	public int txtGravity;
	
	public HelpScreenUIControlParams(JSONObject json) {
		try {
			imgType = IMAGE_TYPE.values()[json.getInt("IMG_TYPE")];
			customImgId = json.getString("CUSTOM_IMG_ID");
			useImgCentreAsRef = json.getBoolean("IMG_CENTRE_AS_REF");
			imgPlacement = IMG_PLACEMENT.values()[json.getInt("IMG_PLACEMENT")];
			text = json.getString("TEXT");
			txtPlacement = TXT_RELATIVE_PLACEMENT.values()[json.getInt("TXT_RELATIVE_PLACEMENT")];
			txtGravity = json.getInt("TEXT_GRAVITY");
		} catch (JSONException e) {
			e.printStackTrace();
		}   	
	}

	@Override
	public String toString() {
		return "HelpScreenUIControlParams [imgType=" + imgType
				+ ", customImgId=" + customImgId + ", useImgCentreArRef="
				+ useImgCentreAsRef + ", text=" + text + ", viewRect="
				+ viewRect + ", imgPlacement=" + imgPlacement
				+ ", txtPlacement=" + txtPlacement + ", txtGravity="
				+ txtGravity + "]";
	}
}
