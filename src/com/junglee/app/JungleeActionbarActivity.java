package com.junglee.app;

import com.junglee.init.FeatureHelpScreensHandler;

import android.support.v7.app.ActionBarActivity;

public abstract class JungleeActionbarActivity extends ActionBarActivity {
	
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
