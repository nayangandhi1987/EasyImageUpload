package com.junglee.commonlib.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;

import android.content.Context;
import android.content.res.AssetManager;

import com.junglee.commonlib.logging.Logger;

/**
 * FileSystemUtility provides common methods to deal with the file system.
 * <p> 
 * It provides commonly used functions that need to deal with file system, like reading file, copying file, reading 
 * file size in a readable form, or extracting file name, or accessing asset directory, etc. All such common methods 
 * should go in this class, rather than every class implementing same thing over and over again.
 * 
 * @author      Nayan Gandhi <nggandhi@amazon.com>
 * @since       1.0
 */
public class FileSystemUtility {
	private static final String TAG = "FileSystemUtility";
	
	private static final int KB = 1024;
	private static final int MB = 1024 * 1024;
	private static final int GB = 1024 * 1024 *1024;
	
	
	/**
	 * Checks if a file is present or not.
	 * @param filepath the path of the file.
	 * @return true if the file exists, else false.
	 */
	public static boolean exists(String filepath) {
		if(StringUtility.isBlank(filepath)) return false;
		
		File file = new File(filepath);
		return file.exists();     
	}
	
	/**
	 * Extracts file name with extension from the given file path.
	 * @param filepath the path of the file.
	 * @return file name with extension.
	 */
	public static String extractFilenameWithExtn(String filepath) {
		return extractFilename(filepath, true);
	}
	/**
	 * Extracts file name without extension from the given file path.
	 * @param filepath the path of the file.
	 * @return file name without extension.
	 */
	public static String extractFilename(String filepath) {
		return extractFilename(filepath, false);
	}
	/**
	 * Extracts file name with/without extension from the given file path.
	 * @param filepath the path of the file.
	 * @return file name with/without extension.
	 */
	public static String extractFilename(String filepath, boolean keepExtn) {
		if(StringUtility.isBlank(filepath)) {
			return filepath;
		}
		
		if(!keepExtn && filepath.contains(".")) {
			int dotIdx = filepath.lastIndexOf(".");
			filepath = filepath.substring(0, dotIdx);
		}
		
		String[] parts = filepath.split("/");
		return parts[parts.length-1];
	}
	
	/**
	 * Generates a url for a local file.
	 * @param filepath the path of the file.
	 * @return the local url for the file.
	 */
	public static String filepathToUrl(String filepath) {
		return "file://"+filepath;
	}
	
	/**
	 * Copies source file to destination.
	 * @param fileSrc the source file path
	 * @param fileDst the destination file path
	 * @throws IOException
	 */
	public static void copyFile(String fileSrc, String fileDst) throws IOException {
    	Logger.debug(TAG, "Copying File: " + fileSrc + " => " + fileDst);
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
        Logger.verbose(TAG, "Copied To => " + fileDst);
    }
	
	/**
	 * Read content of a text file as a string.
	 * @param filePath the path of the file.
	 * @return file content as string.
	 */
	String readFileContent(String filePath) {
	    try {
	        StringBuilder sb = new StringBuilder();
	        BufferedReader br = new BufferedReader(new FileReader(filePath));
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        
	        br.close();
	        return sb.toString();
	    } catch(Exception e) {
	    	
	    }
	    
	    return null;
	}
	
	/**
	 * Read content of a text file in the assets directory as a string.
	 * @param relativeFilepath the relative file path in the assets directory.
	 * @param c the context
	 * @return fiel content as string
	 */
	public static String readAssetFileContent(String relativeFilepath, Context c) {
		AssetManager assetManager = c.getAssets();

		String content = null;
		try {
			InputStream fileIStream = assetManager.open(relativeFilepath);
			if (fileIStream != null) {
				StringWriter writer = new StringWriter();

				char[] buffer = new char[1024];

				BufferedReader reader = new BufferedReader(new InputStreamReader(fileIStream, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}

				content = writer.toString();
				fileIStream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return content;
	}
	
	/**
	 * Deletes a single file or a directory recursively with all its contents.
	 * @param fileOrDirectory File object for the given file or directory.
	 */
	public static void deleteRecursive(File fileOrDirectory) {
	    if (fileOrDirectory.isDirectory())
	        for (File child : fileOrDirectory.listFiles())
	        	deleteRecursive(child);

	    fileOrDirectory.delete();
	}
	/**
	 * Deletes all the contents from a directory without deleting the given directory itself.
	 * @param directory the File object for the directory whose contents are to be deleted.
	 */
	public static void deleteDirectoryContents(File directory) {
	    if (directory.isDirectory())
	        for (File child : directory.listFiles())
	        	deleteRecursive(child);
	}
	
	/**
	 * Checks if an sd card is present or not.
	 * @return true if present, else false.
	 */
	public static boolean isSdPresent() {
	    return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}
	
	/**
	 * Returns the size in a more readable form with GB/MB/KB/Bytes etc given the size as number of bytes.
	 * @param size the size in bytes.
	 * @return size in the more readable form
	 */
	public static String getFileSizeAsString(long size) {
		String sizeAsString = null;
		
		float gb_s = 0, mb_s = 0, kb_s = 0, bytes = 0;
		if(size > GB) {
			gb_s = size / (float)GB;
			sizeAsString = String.format("%.2f GB", gb_s);
		} else if(size > MB) {
			mb_s = size / (float)MB;
			sizeAsString = String.format("%.2f MB", mb_s);
		} else if(size > KB) {
			kb_s = size / (float)KB;
			sizeAsString = String.format("%.2f KB", kb_s);
		} else {
			bytes = size;
			sizeAsString = String.format("%.2f Bytes", bytes);
		}
		
		return sizeAsString;
	}
}
