package com.junglee.jungleeclick;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.junglee.utils.Utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

public class ImgCompressionUtility {
	
	private static String TAG = "JungleeClick";
	
	private static final int TEMP_STORAGE_SIZE = 16 * 1024;
	
	private static final int HIGHEST_QUALITY_VALUE = 100;
	private static final int HIGH_QUALITY_VALUE = 80;
	private static final int MEDIUM_QUALITY_VALUE = 60;
	private static final int LOW_QUALITY_VALUE = 40;
	private static final int LOWEST_QUALITY_VALUE = 20;
	
	enum CompressionQuality {
		HIGHEST_QUALITY,
		HIGH_QUALITY,
		MEDIUM_QUALITY,
		LOW_QUALITY,
		LOWEST_QUALITY
	}
	
	public static String compressImage(String filepath) {
		return compressImage(filepath, CompressionQuality.HIGH_QUALITY);
	}
	
	public static String compressImage(String filepath, CompressionQuality quality) {
		Log.i(TAG, "Compress Image => " + filepath);
		 
        Bitmap scaledBitmap = null;
 
        BitmapFactory.Options options = new BitmapFactory.Options();
 
//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filepath, options);
 
        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
 
//      max Height and width values of the compressed image is taken as 816x612
 
        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;
 
//      width and height values are set maintaining the aspect ratio of the image
 
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {               
            	imgRatio = maxHeight / actualHeight;                
            	actualWidth = (int) (imgRatio * actualWidth);               
            	actualHeight = (int) maxHeight;             
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
 
            }
        }
 
//      setting inSampleSize value allows to load a scaled down version of the original image
 
        options.inSampleSize = calculateInSampleSize(actualWidth, actualHeight, actualWidth, actualHeight);
 
//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;
 
//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[TEMP_STORAGE_SIZE];
 
        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filepath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
 
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
 
        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;
 
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
 
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp
        		, middleX - bmp.getWidth() / 2
        		, middleY - bmp.getHeight() / 2
        		, new Paint(Paint.FILTER_BITMAP_FLAG));
 
//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filepath);
 
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.i(TAG, "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.i(TAG, "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.i(TAG, "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.i(TAG, "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }
 
        
        int compressionQualityValue = HIGH_QUALITY_VALUE;
        String suffix = "reduced";
        switch(quality) {
            case HIGHEST_QUALITY:
            	compressionQualityValue = HIGHEST_QUALITY_VALUE;
            	suffix = "reduced_highest_quality";
            	break;
            case HIGH_QUALITY:
            	compressionQualityValue = HIGH_QUALITY_VALUE;
            	suffix = "reduced_high_quality";
            	break;
            case MEDIUM_QUALITY:
            	compressionQualityValue = MEDIUM_QUALITY_VALUE;
            	suffix = "reduced_medium_quality";
            	break;
            case LOW_QUALITY:
            	compressionQualityValue = LOW_QUALITY_VALUE;
            	suffix = "reduced_low_quality";
            	break;
            case LOWEST_QUALITY:
            	compressionQualityValue = LOWEST_QUALITY_VALUE;
            	suffix = "reduced_lowest_quality";
            	break;
            	
        }
        
        String targetFilename = Utility.extractFilename(filepath);
        String targetFilepath = getTargetFilepath(targetFilename, suffix);
        
        FileOutputStream out = null;        
        try {
            out = new FileOutputStream(targetFilepath);
 
//          write the compressed bitmap at the destination specified by filename.
            
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, compressionQualityValue, out);
 
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
 
        return targetFilepath;
 
    }
	
	private static String getTargetFilepath(String name) {
	    return getTargetFilepath(name, null);	 
	}
	private static String getTargetFilepath(String name, String suffix) {
	    File file = new File(Environment.getExternalStorageDirectory().getPath(), "JungleeClick/Images");
	    if (!file.exists()) {
	        file.mkdirs();
	    }
	    	    
	    String targetDir = file.getAbsolutePath();
	    String targetName;
	    if(name==null || name.length()==0) {
	    	targetName = String.valueOf(System.currentTimeMillis());
	    } else {
	    	if(suffix==null || suffix.length()==0) {
	    		targetName = name;
	    	} else {
	    		targetName = String.format("%s_%s", name, suffix);
	    	}
	    }
	    String targetExtn = "jpg";
	    
	    String uriSting = String.format("%s/%s.%s", targetDir, targetName, targetExtn);
	    return uriSting;
	 
	}
	
	private static int calculateInSampleSize(int actualWidth, int actualHeight, int reqWidth, int reqHeight) {
	    int inSampleSize = 1;
	 
	    if (actualHeight > reqHeight || actualWidth > reqWidth) {
	        final int heightRatio = Math.round((float) actualHeight/ (float) reqHeight);
	        final int widthRatio = Math.round((float) actualWidth / (float) reqWidth);
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;      
	    }       
	    
	    final float totalPixels = actualWidth * actualHeight;       
	    final float totalReqPixelsCap = reqWidth * reqHeight * 2;       
	    while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
	        inSampleSize++;
	    }
	 
	    return inSampleSize;
	}
}
