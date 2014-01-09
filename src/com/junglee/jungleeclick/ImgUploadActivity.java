package com.junglee.jungleeclick;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import com.example.jungleeclick.R;
import com.example.jungleeclick.R.layout;
import com.example.jungleeclick.R.menu;
import com.junglee.utils.FileSystemUtility;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class ImgUploadActivity extends Activity {
	
	private ImageView imgView = null;
	
	private RadioButton radioHigh = null;
	private RadioButton radioMedium = null;
	private RadioButton radioLow = null;
	
	private TextView txtSize = null;
	
	private Button uploadBtn = null;
	
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
		
		txtSize = (TextView) findViewById(R.id.file_size);
		
		uploadBtn = (Button) findViewById(R.id.btn_upload);
		uploadBtn.setOnClickListener(new Button.OnClickListener() {           

        	  @Override
        	  public void onClick(View v) 
        	  {
        		  uploadPicture();
        	  }
        });
		
		reloadPicture();
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
		    
			txtSize.setText(FileSystemUtility.getFileSizeAsString(file.length()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
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
		
		
	}

}
