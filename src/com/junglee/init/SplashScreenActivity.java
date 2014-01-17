package com.junglee.init;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.jungleeclick.R;
import com.junglee.app.MainActivity;
/**
 * 
 */

public class SplashScreenActivity extends Activity {
	private Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		handler = new Handler();
		gotoNextStep();
		
		AppStartup.performTasks(this.getApplicationContext());
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void gotoNextStep() {
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent intent = null;
				intent = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(intent);
				finish();
			}
		}, 2000);
	}

}

