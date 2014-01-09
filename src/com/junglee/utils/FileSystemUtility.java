package com.junglee.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

public class FileSystemUtility {
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
}
