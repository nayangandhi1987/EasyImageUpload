package com.junglee.commonlib.network;

import android.content.Context;

public class FileUploadRequest {
	public FileUploadRequest(Context c, String uploadUrl, String fileBodyAttribute, String localFilePath) {
		this.context = c;
		this.uploadUrl = uploadUrl;
		this.fileBodyAttribute = fileBodyAttribute;
		this.localFilePath = localFilePath;
	}
	
	public Context getContext() {
		return context;
	}
	public String getUploadUrl() {
		return uploadUrl;
	}
	public String getFileBodyAttribute() {
		return fileBodyAttribute;
	}
	public String getLocalFilePath() {
		return localFilePath;
	}
	
	public String getRequestId() {
		return String.format("%s__%s__%s", uploadUrl, fileBodyAttribute, localFilePath);
	}
	
	public void start(FileTransferResponseListener transferListener) {
		NetworkClient.getInstance().startUpload(this, transferListener);
	}
	
	public int cancel() {
		return NetworkClient.getInstance().cancelUpload(this.getRequestId());
	}
	


	private Context context;
	private String uploadUrl;
	private String fileBodyAttribute;
	private String localFilePath;
}
