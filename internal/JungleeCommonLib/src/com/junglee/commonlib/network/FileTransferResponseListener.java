package com.junglee.commonlib.network;

/**
 * FileTransferResponseListener is an interface any class can implement to handle callbacks for file upload/download 
 * requests.
 * <p> 
 * Any class that does a file upload/download can implement this interface to listen to the progress or completion 
 * callbacks and do appropriate handling. The classes that actually implement file uploads/downloads have the 
 * responsibility to fire these callbacks at the right times.
 * 
 * @author      Nayan Gandhi <nggandhi@amazon.com>
 * @since       1.0
 */
public interface FileTransferResponseListener {
	/**
	 * This will be called whenever more bytes are transferred.
	 * @param bytesTransferred number of bytes transferred(uploaded/downloades) so far.
	 * @param bytesTotal total number of bytes to be transferred(uploaded/downloaded)
	 */
	public void transferProgress(int bytesTransferred, int bytesTotal);
	
	/**
	 * This will be called when the request completes, whether it's a success or failure.
	 * @param response the network response indicating success/failure, and other details.
	 */
	public void transferredCompleted(NetworkResponse response);
}
