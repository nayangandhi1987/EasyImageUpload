package com.junglee.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class ImageUtility {
	
	private static String TAG = "JungleeClick";	
	
	private static final int TEMP_STORAGE_SIZE_FOR_COMPRESSION = 16 * 1024;
	
	private static final int HIGH_COMPRESSION_QUALITY_VALUE = 100;
	private static final int MEDIUM_COMPRESSION_QUALITY_VALUE = 60;
	private static final int LOW_COMPRESSION_QUALITY_VALUE = 20;
	
	private static String COMPRESSED_IMG_SUFFIX_TEMPLATE = "reduced_<quality>";
	
	private static String COMPRESSED_IMGS_TARGET_DIR = "JungleeClick/CompressedImages";
	
	public enum CompressionQuality {
		HIGH,
		MEDIUM,
		LOW
	}
	
	
	
	
	public static File convertImageUriToFile (Uri imageUri, Activity activity)  {
		if(imageUri == null) return null;
		
		Cursor cursor = null;

		String [] proj={MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.ImageColumns.ORIENTATION};
		cursor = activity.managedQuery( imageUri,
				proj, 		// Which columns to return
				null,       // WHERE clause; which rows to return (all rows)
				null,       // WHERE clause selection arguments (none)
				null); 		// Order-by clause (ascending by name)
		int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		if (cursor.moveToFirst()) {
			return new File(cursor.getString(file_ColumnIndex));
		}
		return null;
	}
	
	public static String compressImage(String filepath) {
		return compressImage(filepath, CompressionQuality.HIGH);
	}
	
	public static String compressImage(String filepath, CompressionQuality quality) {
		Log.i(TAG, "Compress Image => " + filepath);
		 
        Bitmap scaledBitmap = null;
 
        BitmapFactory.Options options = new BitmapFactory.Options();
 
//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filepath, options);
 
        int height = options.outHeight;
		int width = options.outWidth;
		float maxHeight = 816.0f;
		float maxWidth = 612.0f;
		float imgRatio = (float)width / (float)height;
		float maxRatio = maxWidth / maxHeight;

		if (height > maxHeight || width > maxWidth) {
			if (imgRatio < maxRatio) {
				width = (int) (imgRatio * maxHeight);
				height = (int) maxHeight;
			} else if (imgRatio > maxRatio) {
				height = (int) (maxWidth / imgRatio);
				width = (int) maxWidth;
			} else {
				height = (int) maxHeight;
				width = (int) maxWidth;     
				
			}
		}
				
		options.inSampleSize = calculateInSampleSize(options.outWidth, options.outHeight, width, height);
		Log.i(TAG,  "SampleSize: "+options.inSampleSize);
 
//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;
 
//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[TEMP_STORAGE_SIZE_FOR_COMPRESSION];
 
        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filepath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
 
        }
        try {
            scaledBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
 
        float ratioX = width / (float) options.outWidth;
		float ratioY = height / (float)options.outHeight;
		float middleX = width / 2.0f;
		float middleY = height / 2.0f;
 
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
 
        
        int compressionQualityValue = HIGH_COMPRESSION_QUALITY_VALUE;
        String suffix = getCompressionSuffix(quality);
        switch(quality) {
            case HIGH:
            	compressionQualityValue = HIGH_COMPRESSION_QUALITY_VALUE;
            	break;
            case MEDIUM:
            	compressionQualityValue = MEDIUM_COMPRESSION_QUALITY_VALUE;
            	break;
            case LOW:
            	compressionQualityValue = LOW_COMPRESSION_QUALITY_VALUE;
            	break;            	
        }
        
        String targetFilename = FileSystemUtility.extractFilename(filepath);
        String targetFilepath = getCompressedImgTargetPath(targetFilename, suffix);
        
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
	
	public static String getCompressionTargetDir()
	{
		StringBuilder pathBuilder = new StringBuilder();
		String sd_card = Environment.getExternalStorageDirectory().getPath();
		pathBuilder.append(sd_card);
		if(!sd_card.endsWith("/")) {
			pathBuilder.append("/");
		}
		pathBuilder.append(COMPRESSED_IMGS_TARGET_DIR);
		
		return pathBuilder.toString();
	}
	private static String getCompressionSuffix(CompressionQuality quality) {
		String suffixParam = "unknown";

		switch(quality) {
		case HIGH:
			suffixParam = "high";
			break;
		case MEDIUM:
			suffixParam = "medium";
			break;
		case LOW:
			suffixParam = "low";
			break;
		}
		
		return COMPRESSED_IMG_SUFFIX_TEMPLATE.replace("<quality>", suffixParam);
	}
	private static String getCompressedImgTargetPath(String name) {
	    return getCompressedImgTargetPath(name, null);	 
	}
	private static String getCompressedImgTargetPath(String name, String suffix) {
	    File dir = new File(getCompressionTargetDir());
	    if (!dir.exists()) {
	    	dir.mkdirs();
	    }
	    	    
	    String targetDir = dir.getAbsolutePath();
	    String targetName;
	    if(StringUtility.isBlank(name)) {
	    	targetName = String.valueOf(System.currentTimeMillis());
	    } else {
	    	if(StringUtility.isBlank(suffix)) {
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

	public static void clearCompressedImages() {
		File dir = new File(getCompressionTargetDir());
	    if (dir.exists()) {
	    	FileSystemUtility.deleteDirectoryContents(dir);
	    }
	}
}
