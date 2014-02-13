package com.junglee.commonlib.network;

import android.content.Context;

/**
 * FileUploadRequest is used to do any file uploads. You can create an object of this class, and then start the file upload. 
 * The uploads that are in progress can be cancelled too.
 * 
 * @author      Nayan Gandhi <nggandhi@amazon.com>
 * @since       1.0
 *
 */
public class FileUploadRequest {
	private Context context;
	private String uploadUrl;
	private String fileBodyAttribute;
	private String localFilePath;
	
	
	/**
	 * The constructor that initializes the various data members of the class.
	 * @param c the context from which the object is created, or upload is to be started.
	 * @param uploadUrl the url that is to be hit to do the file upload.
	 * @param fileBodyAttribute the name of the parameter in which the server is expecting the file content.
	 * @param localFilePath the path of the local file which is to be uploaded.
	 */
	public FileUploadRequest(Context c, String uploadUrl, String fileBodyAttribute, String localFilePath) {
		this.context = c;
		this.uploadUrl = uploadUrl;
		this.fileBodyAttribute = fileBodyAttribute;
		this.localFilePath = localFilePath;
	}
	
	/**
	 * Returns the context with which the request was created.
	 * @return the context.
	 */
	public Context getContext() {
		return context;
	}
	/**
	 * Returns the url with which the request was created.
	 * @return the url.
	 */
	public String getUploadUrl() {
		return uploadUrl;
	}
	/**
	 * Returns the file param with which the request was created.
	 * @return the file param.
	 */
	public String getFileBodyAttribute() {
		return fileBodyAttribute;
	}
	/**
	 * Returns the local file path with which the request was created.
	 * @return the path of file to be uploaded.
	 */
	public String getLocalFilePath() {
		return localFilePath;
	}
	
	/**
	 * Returns the unique request id, currently generated using: uploadUrl, fileBodyAttribute, and localFilePath
	 * @return the request id.
	 */
	public String getRequestId() {
		return String.format("%s__%s__%s", uploadUrl, fileBodyAttribute, localFilePath);
	}
	
	/**
	 * Starts the file upload, and registers the handler for file transfer callbacks.
	 * @param transferListener the handler for file transfer callbacks.
	 */
	public void start(FileTransferResponseListener transferListener) {
		NetworkClient.getInstance().startUpload(this, transferListener);
	}
	
	/**
	 * Cancels the request if it is in progress, else does nothing.
	 * @return the cancellation status - CANCELLED or NOT_IN_PROGRESS, etc.
	 */
	public int cancel() {
		return NetworkClient.getInstance().cancelUpload(this.getRequestId());
	}
}
