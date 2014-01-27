package com.junglee.app;

import com.junglee.init.FeatureHelpScreensHandler;

import android.app.Activity;

public abstract class JungleeActivity extends Activity {
	
	protected abstract String getScreenId();
	protected abstract String getUiState();
	
	protected void onUiStateChanged() {
		FeatureHelpScreensHandler.getInstance().checkForHelpScreen(getScreenId(), getUiState(), this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		FeatureHelpScreensHandler.getInstance().checkForHelpScreen(getScreenId(), getUiState(), this);
	}
}
