package com.junglee.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.Toast;

public class UIUtility {

	public static void showProgressIndicator(ProgressDialog dlg, String msg, boolean showDlg) {
		if(dlg != null) {
			if(showDlg && !dlg.isShowing()){
				dlg.setMessage(msg==null?"Please wait...":msg);
				dlg.show();
		    } else if(!showDlg && dlg.isShowing()) {
		    	dlg.dismiss();
		    }
		}
	}
	
	public static void showToastMsgShort(Activity activity, String msg) {
		Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
	}
	public static void showToastMsgLong(Activity activity, String msg) {
		Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
	}
}
