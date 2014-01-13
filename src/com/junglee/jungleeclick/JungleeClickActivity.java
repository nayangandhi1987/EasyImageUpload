package com.junglee.jungleeclick;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;


import com.example.jungleeclick.R;
import com.junglee.events.GlobalEventID;
import com.junglee.utils.FileSystemUtility;
import com.junglee.utils.GlobalStrings;
import com.junglee.utils.ImageUtility;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Files.FileColumns;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class JungleeClickActivity extends Activity {
	
	static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 101;
	static final int SELECT_IMAGE_ACTIVITY_REQUEST_CODE = 102;	
	
	private String fileNameTemplate = "junglee_cam_picture_<time-stamp>";
	private String fileName = null;
	private Uri imageUri = null;
	
	private StringBuilder urls = new StringBuilder();
	private HashMap<String, String> qualityToImgPath = new HashMap<String, String>();
	
	private File picture = null;
	
	ProgressDialog progressDlg = null;
	

	
	Handler handler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
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
        
        progressDlg = new ProgressDialog(this);
        progressDlg.setMessage(GlobalStrings.REDUCING_IMG_SZ);
        
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
    	
    	ImageUtility.clearCompressedImages();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    private void takePicture() {
    	//define the file-name to save photo taken by Camera activity
    	fileName = fileNameTemplate.replaceFirst("<time-stamp>", String.valueOf(System.currentTimeMillis()));
    	//create parameters for Intent with filename
    	ContentValues values = new ContentValues();
    	values.put(MediaStore.Images.Media.TITLE, fileName);
    	values.put(MediaStore.Images.Media.DESCRIPTION, GlobalStrings.CAPTURED_IMG_DESC);
    	//imageUri is the current activity attribute, define and save it for later usage (also in onSaveInstanceState)
    	
    	imageUri = getContentResolver().insert(
    	        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    	
    	//create new Intent
    	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    	intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
    	intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
    	startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }
    
    private void selectPicture() {
    	Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
    	photoPickerIntent.setType("image/*");
    	startActivityForResult(photoPickerIntent, SELECT_IMAGE_ACTIVITY_REQUEST_CODE);
    }
    
    private void onPictureSelected() {
    	ImageUtility.clearCompressedImages();
    	compressSelectedPicture();
    }

    private void compressSelectedPicture() {
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
		    	String imgDst = String.format("%s/%s", ImageUtility.getCompressionTargetDir(), FileSystemUtility.extractFilenameWithExtn(imgSrc));
		    	urls.append(FileSystemUtility.filepathToUrl(imgDst));
		    	try {
		    		FileSystemUtility.copyFile(imgSrc, imgDst);
		    	} catch (IOException e) {
		    		e.printStackTrace();
		    	}
		    	*/
		    	
		    	String compressedFilepath = ImageUtility.compressImage(imgSrc
		    			, ImageUtility.CompressionQuality.HIGH);
		    	urls.append(FileSystemUtility.filepathToUrl(compressedFilepath));
		    	qualityToImgPath.put("HIGH", compressedFilepath);
		    	
		    	compressedFilepath = ImageUtility.compressImage(imgSrc
		    			, ImageUtility.CompressionQuality.MEDIUM);
		    	urls.append("#");
		    	urls.append(FileSystemUtility.filepathToUrl(compressedFilepath));
		    	qualityToImgPath.put("MEDIUM", compressedFilepath);
		    	
		    	compressedFilepath = ImageUtility.compressImage(imgSrc
		    			, ImageUtility.CompressionQuality.LOW);
		    	urls.append("#");
		    	urls.append(FileSystemUtility.filepathToUrl(compressedFilepath));
		    	qualityToImgPath.put("LOW", compressedFilepath);

		    	handler.sendMessage(handler.obtainMessage(GlobalEventID.COMPRESSION_COMPLETED));
		    }
		};
		Thread thread = new Thread(runnable);
        thread.start();
    }
    
    private void showCompressedImgs() {
    	Intent i = new Intent(getApplicationContext(), ImgViewerActivity.class);
    	i.putExtra("urls",urls.toString());
    	startActivity(i);
    }
    private void showUploadScreen() {
    	Intent i = new Intent(getApplicationContext(), ImgUploadActivity.class);
    	for (Entry<String, String> entry : qualityToImgPath.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            i.putExtra(key, value);
        }
    	
    	startActivity(i);
    }


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		urls.setLength(0);
		qualityToImgPath.clear();
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE 
				|| requestCode == SELECT_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				if(requestCode == SELECT_IMAGE_ACTIVITY_REQUEST_CODE) {
					imageUri = data.getData();
				}
				picture = ImageUtility.convertImageUriToFile (imageUri, this);
				
				onPictureSelected();
			} else {
				picture = null;
				fileName = null;
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
