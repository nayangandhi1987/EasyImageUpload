package com.junglee.jungleeclick;

import java.io.File;

import com.example.jungleeclick.R;
import com.junglee.network.AsyncHttpClientFileUploader;
import com.junglee.utils.FileSystemUtility;
import com.junglee.utils.ImageUtility;
import com.junglee.utils.LocationUtility;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class JungleeClickActivity extends Activity {
	
	static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 101;
	static final int SELECT_IMAGE_ACTIVITY_REQUEST_CODE = 102;
	
	static final String JUNGLEE_SERVER = "http://192.168.1.3:8888";
	static final String UPLOAD_ACTION = "upload";	
	
	private String fileNameTemplate = "junglee_cam_picture_<time-stamp>";
	private String fileName = null;
	private Uri imageUri = null;
	
	private String fileToUpload = null;
	private StringBuilder urls = new StringBuilder();
	
	private File picture = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button locBtn = (Button) findViewById(R.id.btn_loc) ;
        locBtn.setOnClickListener(new Button.OnClickListener() {           

        	  @Override
        	  public void onClick(View v) 
        	  {
        		  showLocation();
        	  }
        });
        
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
        
        Button compressBtn = (Button) findViewById(R.id.btn_compress) ;
        compressBtn.setOnClickListener(new Button.OnClickListener() {           

        	  @Override
        	  public void onClick(View v) 
        	  {
        		  compressRecentPicture();
        	  }    
        });
        
        Button uploadBtn = (Button) findViewById(R.id.btn_upload) ;
        uploadBtn.setOnClickListener(new Button.OnClickListener() {           

        	  @Override
        	  public void onClick(View v) 
        	  {
        		  uploadRecentPicture();
        	  }    
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    
    private void showLocation() {
    	LocationUtility locationTracker = new LocationUtility(this, false);
    	String locationString = locationTracker.getReadableLocation();
    	Toast.makeText(this, locationString, Toast.LENGTH_LONG).show();
    }
    
    private void takePicture() {
    	//define the file-name to save photo taken by Camera activity
    	fileName = fileNameTemplate.replaceFirst("<time-stamp>", String.valueOf(System.currentTimeMillis()));
    	//create parameters for Intent with filename
    	ContentValues values = new ContentValues();
    	values.put(MediaStore.Images.Media.TITLE, fileName);
    	values.put(MediaStore.Images.Media.DESCRIPTION, "Image captured by camera using JungleeClick app!");
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

    private void compressRecentPicture() {
    	if(picture==null) {
    		 Toast.makeText(this, "No picture selected for compression.", Toast.LENGTH_LONG).show();
    		 return;
    	}
    	
    	String imgSrc = picture.getAbsolutePath();
    	
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
    	fileToUpload = compressedFilepath;
    	urls.append(FileSystemUtility.filepathToUrl(compressedFilepath));
    	
    	compressedFilepath = ImageUtility.compressImage(imgSrc
    			, ImageUtility.CompressionQuality.MEDIUM);
    	urls.append("#");
    	urls.append(FileSystemUtility.filepathToUrl(compressedFilepath));
    	
    	compressedFilepath = ImageUtility.compressImage(imgSrc
    			, ImageUtility.CompressionQuality.LOW);
    	urls.append("#");
    	urls.append(FileSystemUtility.filepathToUrl(compressedFilepath));


    	AlertDialog.Builder builder = new AlertDialog.Builder(JungleeClickActivity.this);
    	builder.setTitle("Compression completed!").setMessage("Do you want to view the compressed images?")
    	.setCancelable(true)
    	.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int id) {
    			showCompressedImgs();
    		}
    	})
    	.setNegativeButton("No", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int id) {
    			dialog.dismiss();
    		}
    	});
    	AlertDialog alert = builder.create();
    	alert.show();
    }
    
    private void showCompressedImgs() {
    	Intent i = new Intent(getApplicationContext(), ImgViewerActivity.class);
    	i.putExtra("urls",urls.toString());
    	startActivity(i);
    }
    
    private void uploadRecentPicture() {
    	if(fileToUpload != null) {
    		Runnable runnable = new Runnable()
    		{
    		    @Override
    		    public void run()
    		    {
    		    	AsyncHttpClientFileUploader fileUploader = new AsyncHttpClientFileUploader();
    	    		fileUploader.uploadFile(String.format("%s/%s", JUNGLEE_SERVER, UPLOAD_ACTION), new File(fileToUpload));

    		    }
    		};
    		Thread thread = new Thread(runnable);
            thread.start();
    	}
    }


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		urls.setLength(0);
		fileToUpload = null;
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE 
				|| requestCode == SELECT_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				if(requestCode == SELECT_IMAGE_ACTIVITY_REQUEST_CODE) {
					imageUri = data.getData();
				}
				picture = ImageUtility.convertImageUriToFile (imageUri, this);
			} else {
				picture = null;
				fileName = null;
			}
		}
	}
    
}
