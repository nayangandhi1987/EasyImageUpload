package com.junglee.jungleeclick;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.example.jungleeclick.R;
import com.junglee.network.AsyncHttpClientFileUploader;
import com.junglee.network.HttpClientFileUploader;
import com.junglee.utils.Utility;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 101;
	static final int SELECT_IMAGE_ACTIVITY_REQUEST_CODE = 102;
	
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
    
    public void copyFile(String fileSrc, String fileDst) throws IOException {
    	Log.i("JungleeClick", "Copy File " + fileSrc + " => " + fileDst);
    	File src = new File(fileSrc);
    	File dst = new File(fileDst);
    	
    	File dstDir = new File(dst.getParent());
	    if (!dstDir.exists()) {
	    	dstDir.mkdirs();
	    }
    	
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
        Log.i("JungleeClick", "Copied To => " + fileDst);
    }
    
    
    private void showLocation() {
    	LocationTrackerUtility locationTracker = new LocationTrackerUtility(this);
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
    		 Toast.makeText(this, "Picture not taken/selected for compression.", Toast.LENGTH_LONG).show();
    		 return;
    	}
    	
    	String imgSrc = picture.getAbsolutePath();//String.format("/storage/emulated/0/DCIM/Camera/%s.jpg", fileName);
    	String imgDst = String.format("/storage/emulated/0/JungleeClick/Images/%s", Utility.extractFilenameWithExtn(imgSrc));
    	urls.append(Utility.filepathToUrl(imgDst));
    	try {
    		MainActivity.this.copyFile(imgSrc, imgDst);
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	
    	String compressedFilepath = ImgCompressionUtility.compressImage(imgSrc
    			, ImgCompressionUtility.CompressionQuality.HIGHEST_QUALITY);
    	urls.append("#");
    	urls.append(Utility.filepathToUrl(compressedFilepath));
    	
    	compressedFilepath = ImgCompressionUtility.compressImage(imgSrc
    			, ImgCompressionUtility.CompressionQuality.HIGH_QUALITY);
    	fileToUpload = compressedFilepath;
    	urls.append("#");
    	urls.append(Utility.filepathToUrl(compressedFilepath));
    	
    	compressedFilepath = ImgCompressionUtility.compressImage(imgSrc
    			, ImgCompressionUtility.CompressionQuality.MEDIUM_QUALITY);
    	urls.append("#");
    	urls.append(Utility.filepathToUrl(compressedFilepath));
    	
    	compressedFilepath = ImgCompressionUtility.compressImage(imgSrc
    			, ImgCompressionUtility.CompressionQuality.LOW_QUALITY);
    	urls.append("#");
    	urls.append(Utility.filepathToUrl(compressedFilepath));
    	
    	compressedFilepath = ImgCompressionUtility.compressImage(imgSrc
    			, ImgCompressionUtility.CompressionQuality.LOWEST_QUALITY);
    	urls.append("#");
    	urls.append(Utility.filepathToUrl(compressedFilepath));


    	//Toast.makeText(this, "Compression completed!", Toast.LENGTH_LONG).show();    	
    	//showCompressedImgs();
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
    	builder.setMessage("Compression completed!")
    	.setCancelable(true)
    	.setPositiveButton("Show Images", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int id) {
    			showCompressedImgs();
    		}
    	})
    	.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
    	//Toast.makeText(this, "Not implemented!", Toast.LENGTH_LONG).show();
    	if(fileToUpload != null) {
    		Runnable runnable = new Runnable()
    		{
    		    @Override
    		    public void run()
    		    {
    		    	AsyncHttpClientFileUploader fileUploader = new AsyncHttpClientFileUploader();
    	    		fileUploader.uploadFile("http://192.168.1.3:8888/upload", new File(fileToUpload));

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
				picture = convertImageUriToFile (imageUri, this);
			} else {
				picture = null;
				fileName = null;
			}
		}
	}
	
	public static File convertImageUriToFile (Uri imageUri, Activity activity)  {
		if(imageUri == null) return null;
		
		Cursor cursor = null;

		String [] proj={MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.ImageColumns.ORIENTATION};
		cursor = activity.managedQuery( imageUri,
				proj, 		// Which columns to return
				null,       // WHERE clause; which rows to return (all rows)
				null,       // WHERE clause selection arguments (none)
				null); 		// Order-by clause (ascending by name)
		int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		if (cursor.moveToFirst()) {
			return new File(cursor.getString(file_ColumnIndex));
		}
		return null;
	}
    
}
