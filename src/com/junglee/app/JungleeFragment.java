package com.junglee.app;

import com.junglee.init.FeatureHelpScreensHandler;

import android.support.v4.app.Fragment;

public abstract class JungleeFragment extends Fragment{

	protected abstract String getScreenId();
	protected abstract String getUiState();
	
	protected void onUiStateChanged() {
		FeatureHelpScreensHandler.getInstance().checkForHelpScreen(getScreenId(), getUiState(), this.getActivity());
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		FeatureHelpScreensHandler.getInstance().checkForHelpScreen(getScreenId(), getUiState(), this.getActivity());
	}
}
