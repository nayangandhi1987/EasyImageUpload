package com.junglee.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.Toast;

import com.junglee.commonlib.logging.Logger;
import com.junglee.utils.GlobalStrings;

/**
 * UIUtility provides common methods to deal with the basic UI components like showing progres dialog, or a toast, or 
 * alert box, etc. It can also be used to other common methods like calculating screen size , or actionbar size , or 
 * the statusbar size, etc.
 * 
 * @author      Nayan Gandhi <nggandhi@amazon.com>
 * @since       1.0
 */
public class UIUtility {
	private static final String TAG = "UIUtility";
	
	private static int ACTION_BAR_HEIGHT = 0;
	private static int STATUS_BAR_HEIGHT = 0;
	
	private static int SCREEN_WIDTH = 0;
	private static int SCREEN_HEIGHT = 0;

	public static void showProgressIndicator(ProgressDialog dlg, String msg, boolean showDlg) {
		if(dlg != null) {
			if(showDlg && !dlg.isShowing()){
				dlg.setMessage(msg==null?GlobalStrings.PLS_WAIT:msg);
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
	
	public static void errorBox(Context c, String title, String mymessage) {
        errorBox(c, title, mymessage, null, null);
    }

    public static void errorBox(Context c, String title, String mymessage,
            DialogInterface.OnClickListener positiveButtonListener) {
        errorBox(c, title, mymessage, positiveButtonListener, null);
    }

    public static void errorBox(Context c, String title, String mymessage,
            DialogInterface.OnClickListener positiveButtonListener,
            DialogInterface.OnClickListener negativeButtonListener) {
        errorBox(c, title, mymessage, positiveButtonListener, c.getResources().getString(android.R.string.ok),
                negativeButtonListener, c.getResources().getString(android.R.string.cancel));
    }
    
    public static void errorBox(Context c, String title, String mymessage,
            DialogInterface.OnClickListener positiveButtonListener, String positiveButtonText,
            DialogInterface.OnClickListener negativeButtonListener, String negativeButtonText) {
    	try	{
	        Builder alert = new AlertDialog.Builder(c)
	                .setMessage(mymessage)
	                .setIcon(android.R.drawable.ic_dialog_alert)
	                .setTitle(title)
	                .setCancelable(true)
	                .setPositiveButton(
	                        positiveButtonText != null ? positiveButtonText : c.getResources().getString(android.R.string.ok),
	                        positiveButtonListener != null ? positiveButtonListener
	                                : new DialogInterface.OnClickListener()
	                                {
	                                    public void onClick(DialogInterface dialog,
	                                            int whichButton)
	                                    {
	                                    }
	                                });
	        if (negativeButtonListener != null) {
	            alert.setNegativeButton(negativeButtonText != null ? negativeButtonText : c.getResources().getString(android.R.string.cancel),
	                    negativeButtonListener);
	        }
	
	        alert.show();
    	} catch (WindowManager.BadTokenException e) {
    		// This occurs when the activity for which the dialog needs to be displayed
			// is stopped or destroyed before the dialog is displayed. We don't need to 
			// do anything specific in this case.
    	}
    }
	
	public static int getActionbarHeight(Activity activity) {
		if(ACTION_BAR_HEIGHT > 0) return ACTION_BAR_HEIGHT;
		
		if(activity != null) {
			TypedValue tv = new TypedValue();
			if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
				ACTION_BAR_HEIGHT = TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources().getDisplayMetrics());
			}
			if (ACTION_BAR_HEIGHT == 0 && activity.getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, tv, true)) {
				ACTION_BAR_HEIGHT = TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources().getDisplayMetrics());
			}
		}
		
		return ACTION_BAR_HEIGHT;
	}	
	public static int getCalculatedActionbarHeight() {
		return ACTION_BAR_HEIGHT;
	}
	
	public static int getStatusbarHeight(Activity activity) {
		if(STATUS_BAR_HEIGHT > 0) return STATUS_BAR_HEIGHT;
		
		if(activity != null) {
			Rect rectangle = new Rect();
			android.view.Window window = activity.getWindow();
			window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
			STATUS_BAR_HEIGHT = rectangle.top;
		}		
		Logger.info(TAG, "STATUS_BAR_HEIGHT="+STATUS_BAR_HEIGHT);
		return STATUS_BAR_HEIGHT;		
	}
	public static int getCalculatedStatusbarHeight() {
		return STATUS_BAR_HEIGHT;
	}
	
	public static int getScreenWidth(Activity activity) {
		if(activity != null) {
			DisplayMetrics metrics=new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

			SCREEN_HEIGHT = metrics.heightPixels;
		    SCREEN_WIDTH = metrics.widthPixels;
		}
		
		return SCREEN_WIDTH;
	}	
	public static int getCalculatedScreenWidth() {
		return SCREEN_WIDTH;
	}
	public static int getScreenHeight(Activity activity) {
		if(activity != null) {
			DisplayMetrics metrics=new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

			SCREEN_HEIGHT = metrics.heightPixels;
		    SCREEN_WIDTH = metrics.widthPixels;
		}
		
		return SCREEN_HEIGHT;
	}	
	public static int getCalculatedScreenHeight() {
		return SCREEN_HEIGHT;
	}
}
