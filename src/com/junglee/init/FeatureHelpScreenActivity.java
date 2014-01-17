package com.junglee.init;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jungleeclick.R;
import com.junglee.data.IntentData;
import com.junglee.init.HelpScreenUIControlParams.IMG_PLACEMENT;
import com.nineoldandroids.view.ViewHelper;

public class FeatureHelpScreenActivity extends Activity {
	
	private static final int TXT_VIEW_ID_BASE = 100;
	private static final int IMG_VIEW_ID_BASE = 200;
	
	private RelativeLayout containerLayout = null;
	
	private List<HelpScreenUIControlParams> controls = new ArrayList<HelpScreenUIControlParams>();
	

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feature_help_screen);
		
		Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	String helpId = extras.getString("HELP_ID");
        	controls = IntentData.getInstance().getHelpScreenData(helpId);
        }
		
		containerLayout = (RelativeLayout)findViewById(R.id.help_container);
		
		populateViews();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.feature_help_screen, menu);
		return true;
	}
	
	private void populateViews() {
		DisplayMetrics metrics=new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

	    int screenHeight=metrics.heightPixels;
	    int screenWidth=metrics.widthPixels;
	    Log.i("JungleeClick", "screenWidth="+screenWidth+", screenHeight="+screenHeight);
	    
		for(int i = 0; i < controls.size(); ++i) {
			HelpScreenUIControlParams control = controls.get(i);
			Log.i("JungleeClick", control.toString());
			
			TextView txtView = createTxtView(control.text);
			ImageView imgView = createImgView(getRscIdForGesture(control.imgType, control.customImgId));
			
			addViewToLayout(imgView);
			addViewToLayout(txtView);				
			
			txtView.measure(screenWidth, screenHeight);
			imgView.measure(screenWidth, screenHeight);
			
	    	
	    	Point posImg = getPositionInRect(control.viewRect, control.imgPlacement);
	    	int posImgX = posImg.x, posImgY = posImg.y;
	    	if(control.useImgCentreAsRef) {
	    		posImgX -= imgView.getMeasuredWidth()/2;
	    		posImgY -= imgView.getMeasuredHeight()/2;
	    	}
	    	
	    	ViewHelper.setX(imgView, posImgX);	    	
	    	ViewHelper.setY(imgView, posImgY);
	    	
	    	int imgMidX = posImgX+imgView.getMeasuredWidth()/2;
	    	int imgMidY = posImgY+imgView.getMeasuredHeight()/2;
	    	int posTxtX = 0, posTxtY = 0;
	    	int widthtxtView = screenWidth;
	    	if(control.txtPlacement == HelpScreenUIControlParams.TXT_RELATIVE_PLACEMENT.ABOVE
	    			|| control.txtPlacement == HelpScreenUIControlParams.TXT_RELATIVE_PLACEMENT.BELOW) {
	    		if(control.txtGravity == Gravity.CENTER) {
	    			if(imgMidX > screenWidth/2) {
	    				widthtxtView = 2 * (screenWidth-imgMidX);
	    				posTxtX = screenWidth - widthtxtView;
	    			} else {
	    				widthtxtView = 2 * imgMidX;
	    				posTxtX = 0;
	    			}
	    		} else {
	    			if(control.txtGravity == Gravity.RIGHT) {
	    				widthtxtView = posImgX + imgView.getMeasuredHeight();
	    				posTxtX = 0;
	    			} else if(control.txtGravity == Gravity.LEFT) {
	    				widthtxtView = screenWidth - posImgX;
	    				posTxtX = posImgX;
	    			}
	    		}
	    		
	    		if(control.txtPlacement == HelpScreenUIControlParams.TXT_RELATIVE_PLACEMENT.ABOVE) {
	    			posTxtY = posImgY - txtView.getMeasuredHeight();
	    		} else {
	    			posTxtY = posImgY + imgView.getMeasuredHeight();
	    		}
	    	} else {
	    		if(control.txtPlacement == HelpScreenUIControlParams.TXT_RELATIVE_PLACEMENT.RIGHT) {
	    			posTxtX = posImgX + imgView.getMeasuredWidth();
	    			widthtxtView = screenWidth - posTxtX;
	    		} else if(control.txtPlacement == HelpScreenUIControlParams.TXT_RELATIVE_PLACEMENT.LEFT) {
	    			posTxtX = 0;
	    			widthtxtView = posImgX;
	    		}
	    		
	    		posTxtY = imgMidY - txtView.getMeasuredHeight()/2;
	    	}
	    	txtView.getLayoutParams().width = widthtxtView;
	    	ViewHelper.setX(txtView, posTxtX);
	    	ViewHelper.setY(txtView, posTxtY);
	    	
	    	txtView.setGravity(control.txtGravity);
		}
	}
	
	private void addViewToLayout(View v) {		
		containerLayout.addView(v);
	}
	
	private ImageView createImgView(int resourceId) {
		ImageView imgview = new ImageView(this);
		if(resourceId > 0) {
			imgview.setImageResource( resourceId );
		}
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT );
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		imgview.setLayoutParams( params );
		return imgview;
	}
	private TextView createTxtView(String text) {
		TextView txtview = new TextView(this);
		txtview.setTextColor(Color.WHITE);
		txtview.setText(text);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT );
		txtview.setLayoutParams( params );
		return txtview;
	}
	
	private int getRscIdForGesture(HelpScreenUIControlParams.IMAGE_TYPE type, String customId) {
		int rscId = R.drawable.single_tap;
		
		switch(type) {
		case NONE:
			rscId = 0;
			break;
			
		case CUSTOM:
			rscId = getResources().getIdentifier(customId, "drawable", getPackageName());
			break;
			
		case SINGLE_TAP:
			rscId = R.drawable.single_tap;
			break;

		case DOUBLE_TAP:
			//rscId = R.drawable.double_tap;
			break;

		case PRESS_AND_HOLD:
			//rscId = R.drawable.press_and_hold;
			break;

		case SWIPE_UP:
			//rscId = R.drawable.swipe_up;
			break;

		case SWIPE_DOWN:
			//rscId = R.drawable.swipe_down;
			break;

		case SWIPE_LEFT:
			//rscId = R.drawable.swipe_left;
			break;

		case SWIPE_RIGHT:
			//rscId = R.drawable.swipe_right;
			break;

		case SPREAD:
			//rscId = R.drawable.spread;
			break;

		case PINCH:
			//rscId = R.drawable.pinch;
			break;
		}
		
		
		return rscId;
	}
	
	private Point getPositionInRect(Rect rect, IMG_PLACEMENT imgPlacement) {
		int posX = 0, posY = 0;
		switch(imgPlacement) {
    	case CENTER:
    		posX = rect.centerX();
	    	posY = rect.centerY();
    		break;
    	case LEFT:
    		posX = rect.left;
	    	posY = rect.centerY();
    		break;
    	case RIGHT:
    		posX = rect.right;
	    	posY = rect.centerY();
    		break;
    	case TOP:
    		posX = rect.centerX();
	    	posY = rect.top;
    		break;
    	case BOTTOM:
    		posX = rect.centerX();
	    	posY = rect.bottom;
    		break;
    	case TOP_LEFT:
    		posX = rect.left;
	    	posY = rect.top;
    		break;
    	case BOTTOM_LEFT:
    		posX = rect.left;
	    	posY = rect.bottom;
    		break;
    	case TOP_RIGHT:
    		posX = rect.right;
	    	posY = rect.top;
    		break;
    	case BOTTOM_RIGHT:
    		posX = rect.right;
	    	posY = rect.bottom;
    		break;
    	}
		
		return new Point(posX, posY);
	}
	
	private int getTxtViewIdForArrIdx(int idx) {
		return TXT_VIEW_ID_BASE + idx;
	}
	private int getImgViewIdForArrIdx(int idx) {
		return IMG_VIEW_ID_BASE + idx;
	}

	
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				killActivity();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
       
		if(action==MotionEvent.ACTION_UP) {        	
			killActivity();
			return true;
        }
        
        return true;
	}
	
	private void killActivity() {
		Runnable r = new Runnable() {					
			@Override
			public void run() {
				onDone();
			}
		};
		Handler h = new Handler();
		h.postDelayed(r, 10);
	}
	
	private void onDone() {
    	finish();
	}
}
