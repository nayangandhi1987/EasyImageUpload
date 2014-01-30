package com.junglee.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.example.jungleeclick.R;
import com.junglee.events.GlobalEventID;
import com.junglee.commonlib.location.LocationTracker;
import com.junglee.network.AsyncHttpClientFileUploader;
import com.junglee.settings.AppSettings;
import com.junglee.commonlib.utils.FileSystemUtility;
import com.junglee.utils.GlobalStrings;
import com.junglee.utils.UIUtility;

public class ImgUploadActivity extends Activity {
	
	private ImageView imgView = null;
	
	private RadioButton radioHigh = null;
	private RadioButton radioMedium = null;
	private RadioButton radioLow = null;
	
	private TextView sizeView = null;
	
	private TextView locationView = null;
	
	private Button uploadBtn = null;
	

	
	ProgressDialog progressDlg = null;
	Handler handler = null;
	
	private HashMap<String, String> qualityToImgPath = new HashMap<String, String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_img_upload);
		
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	qualityToImgPath.put("HIGH", extras.getString("HIGH"));
        	qualityToImgPath.put("MEDIUM", extras.getString("MEDIUM"));
        	qualityToImgPath.put("LOW", extras.getString("LOW"));
        }
        
		
		imgView = (ImageView) findViewById(R.id.img_to_upload);
		
		RadioGroup qualityGrp = (RadioGroup) findViewById(R.id.radio_quality);
		qualityGrp.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	        @Override
	        public void onCheckedChanged(RadioGroup group, int checkedId) {
	        	reloadPicture();
	        }
	    });
		
		radioHigh = (RadioButton) findViewById(R.id.radio_high);
		radioMedium = (RadioButton) findViewById(R.id.radio_medium);
		radioLow = (RadioButton) findViewById(R.id.radio_low);
		
		sizeView = (TextView) findViewById(R.id.file_size);
		locationView = (TextView) findViewById(R.id.location_value);
		locationView.setText("--");
		
		uploadBtn = (Button) findViewById(R.id.btn_upload);
		uploadBtn.setOnClickListener(new Button.OnClickListener() {           

        	  @Override
        	  public void onClick(View v) 
        	  {
        		  uploadPicture();
        	  }
        });
		
		progressDlg = new ProgressDialog(this);
		handler = new Handler(){
    	    @Override
    	    public void handleMessage(Message msg){

    	    	int msgId = msg.getData().getInt("message_id");
    	        switch(msgId){
    	            case GlobalEventID.LOCATION_FETCHED:
    	            	String location = msg.getData().getString("location");
    	            	locationView.setText(location==null?"Unknown":location);

    	                break;
    	                
    	            case GlobalEventID.UPLOAD_STARTED:
    	            	UIUtility.showProgressIndicator(progressDlg, null, true);

    	                break;
    	            case GlobalEventID.UPLOAD_SUCCEEDED:
    	            	UIUtility.showProgressIndicator(progressDlg, null, false);
    	            	UIUtility.showToastMsgShort(ImgUploadActivity.this, GlobalStrings.UPLOAD_COMPLETE);

    	                break;
    	            case GlobalEventID.UPLOAD_FAILED:
    	            	UIUtility.showProgressIndicator(progressDlg, null, false);
    	            	UIUtility.showToastMsgShort(ImgUploadActivity.this, GlobalStrings.UPLOAD_FAILED);

    	                break;
    	        }
    	    }
    	};
		
		reloadPicture();
		
		refreshLocation();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.img_upload, menu);
		return true;
	}
	
	private void reloadPicture() {
		String imgPath = null;
		if(radioHigh.isChecked()) {
			imgPath = qualityToImgPath.get("HIGH");
		} else if(radioMedium.isChecked()) {
			imgPath = qualityToImgPath.get("MEDIUM");
		} else if(radioLow.isChecked()) {
			imgPath = qualityToImgPath.get("LOW");
		}
		
		try {
			Bitmap bmToRender = BitmapFactory.decodeStream(new FileInputStream(imgPath));
			imgView.setImageBitmap(bmToRender);
			
			File file = new File(imgPath);
		    
			sizeView.setText(FileSystemUtility.getFileSizeAsString(file.length()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void refreshLocation() {
		
    	
    	Runnable runnable = new Runnable()
		{
		    @Override
		    public void run()
		    {
		    	LocationTracker locationTracker = new LocationTracker(ImgUploadActivity.this, false);
		    	String locationString = locationTracker.getReadableLocation();

		    	Message msgObj = handler.obtainMessage();
		    	Bundle b = new Bundle();
		    	b.putString("location", locationString);
		    	b.putInt("message_id", GlobalEventID.LOCATION_FETCHED);
		    	msgObj.setData(b);
		    	handler.sendMessage(msgObj);		           
		    }
		};
		Thread thread = new Thread(runnable);
        thread.start();
	}
	
	
	private void uploadPicture() {
		String imgPath = null;
		if(radioHigh.isChecked()) {
			imgPath = qualityToImgPath.get("HIGH");
		} else if(radioMedium.isChecked()) {
			imgPath = qualityToImgPath.get("MEDIUM");
		} else if(radioLow.isChecked()) {
			imgPath = qualityToImgPath.get("LOW");
		}
		
		final String imgToUpload = imgPath;
    	if(imgToUpload != null) {
    		Runnable runnable = new Runnable()
    		{
    		    @Override
    		    public void run()
    		    {
    		    	AsyncHttpClientFileUploader fileUploader = new AsyncHttpClientFileUploader();
    		    	fileUploader.setHandler(handler);
    	    		fileUploader.uploadFile(String.format("%s/%s", AppSettings.getInstance().getUploadServer(),  AppSettings.getInstance().getUploadAction()), new File(imgToUpload));

    		    }
    		};
    		Thread thread = new Thread(runnable);
            thread.start();
    	}
	}

}
