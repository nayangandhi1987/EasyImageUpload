package com.junglee.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

public class FileSystemUtility {
	
	private static final int KB = 1024;
	private static final int MB = 1024 * 1024;
	private static final int GB = 1024 * 1024 *1024;
	
	public static String extractFilenameWithExtn(String filepath) {
		return extractFilename(filepath, true);
	}
	public static String extractFilename(String filepath) {
		return extractFilename(filepath, false);
	}
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
	
	public static String filepathToUrl(String filepath) {
		return "file://"+filepath;
	}
	
	public static void copyFile(String fileSrc, String fileDst) throws IOException {
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
	
	public static void deleteRecursive(File fileOrDirectory) {
	    if (fileOrDirectory.isDirectory())
	        for (File child : fileOrDirectory.listFiles())
	        	deleteRecursive(child);

	    fileOrDirectory.delete();
	}
	public static void deleteDirectoryContents(File fileOrDirectory) {
	    if (fileOrDirectory.isDirectory())
	        for (File child : fileOrDirectory.listFiles())
	        	deleteRecursive(child);
	}
	
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
