package com.junglee.app;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.example.jungleeclick.R;
import com.junglee.commonlib.photo.PictureUtility;
import com.junglee.commonlib.utils.ThreadUtility;
import com.junglee.events.GlobalEventID;
import com.junglee.utils.GlobalStrings;
import com.junglee.webcontainer.ApiBridgeTestActivity;

public class JungleeClickActivity extends JungleeActivity {
	private static String IDENTIFIER = "JUNGLEE_CLICK_ACTIVITY";
	
	private String UI_STATE = null;
	
	static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 101;
	static final int SELECT_IMAGE_ACTIVITY_REQUEST_CODE = 102;	
		
	private HashMap<String, String> qualityToImgPath = null;
		
	ProgressDialog progressDlg = null;
	

	
	Handler handler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_ad);
        
        Button camBtn = (Button) findViewById(R.id.btn_cam) ;
        camBtn.setOnClickListener(new Button.OnClickListener() {           

        	  @Override
        	  public void onClick(View v) 
        	  {
        		  takePicture();
        	  }
        });
        
        Button galleryBtn = (Button) findViewById(R.id.btn_gallery) ;
        galleryBtn.setOnClickListener(new Button.OnClickListener() {           

        	  @Override
        	  public void onClick(View v) 
        	  {
        		  selectPicture();
        	  }
        });
        
        Button wvBtn = (Button) findViewById(R.id.btn_wv) ;
        wvBtn.setOnClickListener(new Button.OnClickListener() {           

        	  @Override
        	  public void onClick(View v) 
        	  {
        		  gotoWebview();
        	  }
        });
        
        progressDlg = new ProgressDialog(this);
        progressDlg.setMessage(GlobalStrings.REDUCING_IMG_SZ);
        
        setupMsgHandler();
    	
    	PictureUtility.clearPreviousCompressedImages();    	
    }
    
    @Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected String getScreenId() {
		return IDENTIFIER;
	}
	
	@Override
	protected String getUiState() {
		return UI_STATE;
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu);
		// return true;
		
		return super.onCreateOptionsMenu(menu);
    }



	private void setupMsgHandler() {
        handler = new Handler(){
    	    @Override
    	    public void handleMessage(Message msg){

    	        switch(msg.what){
    	            case GlobalEventID.COMPRESSION_STARTED:
    	            	showCompressionProgressIndicator(true);

    	                break;
    	            case GlobalEventID.COMPRESSION_COMPLETED:
    	            	showCompressionProgressIndicator(false);
    	            	
    	            	/*
    	            	AlertDialog.Builder builder = new AlertDialog.Builder(JungleeClickActivity.this);
    	            	builder.setTitle("Compression completed!").setMessage("Do you want to view the compressed images?")
    	            	.setCancelable(true)
    	            	.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    	            		public void onClick(DialogInterface dialog, int id) {
    	            			//showCompressedImgs();
    	            			showUploadScreen();
    	            		}
    	            	})
    	            	.setNegativeButton("No", new DialogInterface.OnClickListener() {
    	            		public void onClick(DialogInterface dialog, int id) {
    	            			dialog.dismiss();
    	            		}
    	            	});
    	            	AlertDialog alert = builder.create();
    	            	alert.show();
    	            	*/
    	            	
    	            	showUploadScreen();
    	                break;
    	        }
    	    }
    	};
	}
    
    private void takePicture() {
    	startActivityForResult(PictureUtility.createCameraIntent(this), CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }
    
    private void selectPicture() {    	
    	startActivityForResult(PictureUtility.createGalleryIntent(), SELECT_IMAGE_ACTIVITY_REQUEST_CODE);
    }
    
    private void onPictureSelected(File picture) {
    	PictureUtility.clearPreviousCompressedImages();
    	compressSelectedPicture(picture);
    }

    private void compressSelectedPicture(File picture) {
    	if(picture==null) {
    		return;
    	}
    	
    	final String imgSrc = picture.getAbsolutePath();
    	
    	Runnable runnable = new Runnable()
		{
		    @Override
		    public void run()
		    {
		    	handler.sendMessage(handler.obtainMessage(GlobalEventID.COMPRESSION_STARTED));
		    	
		    	/*
		    	String compressedFilepath = PictureUtility.compressImage(imgSrc
		    			, PictureUtility.CompressionQuality.HIGH);
		    	qualityToImgPath.put("HIGH", compressedFilepath);		    	
		    	compressedFilepath = PictureUtility.compressImage(imgSrc
		    			, PictureUtility.CompressionQuality.MEDIUM);
		    	qualityToImgPath.put("MEDIUM", compressedFilepath);		    	
		    	compressedFilepath = PictureUtility.compressImage(imgSrc
		    			, PictureUtility.CompressionQuality.LOW);
		    	qualityToImgPath.put("LOW", compressedFilepath);
		    	*/
		    	
		    	
		    	qualityToImgPath = PictureUtility.compressImageToVariousSizes(imgSrc);

		    	handler.sendMessage(handler.obtainMessage(GlobalEventID.COMPRESSION_COMPLETED));
		    }
		};
		ThreadUtility.executeInBackground(runnable);
    }
    
    private void showUploadScreen() {
    	if(qualityToImgPath!=null && qualityToImgPath.size()>0) {
    		Intent i = new Intent(getApplicationContext(), ImgUploadActivity.class);
    		for (Entry<String, String> entry : qualityToImgPath.entrySet()) {
    			String key = entry.getKey();
    			String value = entry.getValue();
    			i.putExtra(key, value);
    		}

    		startActivity(i);
    	}
    }
    
    private void gotoWebview() {
    	Intent i = new Intent(getApplicationContext(), ApiBridgeTestActivity.class /*JungleeWebContainerActivity.class*/);
    	startActivity(i);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		qualityToImgPath = null;
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE 
				|| requestCode == SELECT_IMAGE_ACTIVITY_REQUEST_CODE) {
			File picture = null;
			if (resultCode == RESULT_OK) {
				Uri imageUri = null;
				if(requestCode == SELECT_IMAGE_ACTIVITY_REQUEST_CODE) {
					imageUri = data.getData();
				} else {
					imageUri = PictureUtility.getLastCameraPictureUri();
				}
				picture = PictureUtility.convertImageUriToFile(imageUri, this);
				
				onPictureSelected(picture);
			}
		}
	}
	
	private void showCompressionProgressIndicator(boolean showDlg) {
		if(progressDlg != null) {
			if(showDlg && !progressDlg.isShowing()){
		        progressDlg.show();
		    } else if(!showDlg && progressDlg.isShowing()) {
		    	progressDlg.dismiss();
		    }
		}
	}	
}
