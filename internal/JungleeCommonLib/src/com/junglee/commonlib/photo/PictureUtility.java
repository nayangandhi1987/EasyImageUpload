package com.junglee.commonlib.photo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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

import com.junglee.commonlib.logging.Logger;
import com.junglee.commonlib.utils.FileSystemUtility;
import com.junglee.commonlib.utils.LibraryGlobalConstants;
import com.junglee.commonlib.utils.LibraryGlobalStrings;
import com.junglee.commonlib.utils.StringUtility;
/**
 * PictureUtility provides common methods to create intent for camera or gallery. It also provides methods for compressing 
 * image to different quality levels.
 * <p> 
 * Any activity that wants to use camera for clicking a picture or wants to select a picture from gallery can request to 
 * create respective intents, and then fire those intents and wait for result. Once the picture is taken/selected, it can 
 * be compressed to reduce the size (for easy upload, or for other reasons).
 * <p>
 * The current compressions logic converts the image to a jpg. If the resolution is huge, then it scales down the image to 
 * a maximum of 816x612 or 612x816 without loosing the aspect ration. And then it can compress it to change the picture 
 * quality to 100/75/50 percent. It also rotates the picture as required if the picture was not taken in the portrait mode.
 * 
 * @author      Nayan Gandhi <nggandhi@amazon.com>
 * @since       1.0
 */
public class PictureUtility {	
	private static final String TAG = "ImageUtility";	
	
	private static final int TEMP_STORAGE_SIZE_FOR_COMPRESSION = 16 * 1024;
	
	private static final int HIGH_COMPRESSION_QUALITY_VALUE = 100;
	private static final int MEDIUM_COMPRESSION_QUALITY_VALUE = 75;
	private static final int LOW_COMPRESSION_QUALITY_VALUE = 50;
	
	private static final String FILE_NAME_TEMPLATE = "junglee_cam_picture_<time-stamp>";
	private static Uri CAMERA_PICTURE_URI = null;
	
	private static String COMPRESSED_IMG_SUFFIX_TEMPLATE = "reduced_<quality>";
	
	private static String COMPRESSED_IMGS_TARGET_DIR = "JungleeClick/CompressedImages";
	
	private static final float MAX_CMPRS_WIDTH_PORTRAIT = 612.0f;
	private static final float MAX_CMPRS_WIDTH_LANDSCAPE = 816.0f;
	private static final float MAX_CMPRS_HEIGHT_PORTRAIT = 816.0f;
	private static final float MAX_CMPRS_HEIGHT_LANDSCAPE = 612.0f;
	
	private static final String COMPRESSION_SUFFIX_HIGH_QUALITY = "high";
	private static final String COMPRESSION_SUFFIX_MEDIUM_QUALITY = "medium";
	private static final String COMPRESSION_SUFFIX_LOW_QUALITY = "low";
	private static final String COMPRESSION_SUFFIX_UNKNOWN_QUALITY = "unknown";
	
	public enum CompressionQuality {
		HIGH,
		MEDIUM,
		LOW
	}
	
	
	/**
	 * Clear the directory containing the compressed images, when they are no longer needed. They should not keep accumulating and waste storage.
	 */
	public static void clearPreviousCompressedImages() {
		File dir = new File(getCompressionTargetDir());
	    if (dir.exists()) {
	    	FileSystemUtility.deleteDirectoryContents(dir);
	    }
	}
	
	/**
	 * Creates the intent for launching the camera application to click a picture. It also takes care of the file output path where the picture will be temporarily stored.
	 * @param c the context
	 * @return the intent for launching camera
	 */
	public static Intent createCameraIntent(Context c) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    	intent.putExtra(MediaStore.EXTRA_OUTPUT, generateUriForCameraPicture(c));
    	intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
    	
    	return intent;
	}
	/**
	 * Returns the picture uri that was used when the camera was launched the last time.
	 * @return the uri of last camera picture
	 */
	public static Uri getLastCameraPictureUri() {
		return CAMERA_PICTURE_URI;
	}
	
	/**
	 * Creates the intent for launching the gallery application to select a picture.
	 * @return the intent for launching gallery
	 */
	public static Intent createGalleryIntent() {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
    	
    	return intent;
	}
	
	/**
	 * Creates a File object for the given image uri.
	 * @param imageUri the image uri.
	 * @param activity the activity.
	 * @return the image as file.
	 */
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
	
	/**
	 * Compresses the image to reduce it's size. The current compressions logic converts the image to a jpg. If the resolution is huge, then it scales 
	 * down the image to a maximum of 816x612 or 612x816 without loosing the aspect ration. And then it can compress it to change the picture quality to 
	 * 100/75/50 percent. It also rotates the picture as required if the picture was not taken in the portrait mode.
	 * @param filepath the actual file path.
	 * @param quality the quality to which the image should be compressed - HIGH/MEDIUM/LOW.
	 * @return the path of the compressed file.
	 */
	public static String compressImage(String filepath, CompressionQuality quality) {
		Logger.info(TAG, "Compress Image => " + filepath);
		 
        Bitmap scaledBitmap = null;
 
        BitmapFactory.Options options = new BitmapFactory.Options();
 
//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filepath, options);
 
        int height = options.outHeight;
		int width = options.outWidth;
		float maxHeight = (height>width)?MAX_CMPRS_HEIGHT_PORTRAIT:MAX_CMPRS_HEIGHT_LANDSCAPE;
		float maxWidth = (width<height)?MAX_CMPRS_WIDTH_PORTRAIT:MAX_CMPRS_WIDTH_LANDSCAPE;
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
		Logger.info(TAG,  "SampleSize: "+options.inSampleSize);
 
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
            Logger.info(TAG, "Exif", "Orientation="+orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
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
	/**
	 * Compresses the original image to various compresion qualities, and returns a hash map of the compression quality to the compressed image file path.
	 * @param filepath the actual image file path to be compressed.
	 * @return a hashmap of the compression quality to the compressed image file path.
	 */
	public static HashMap<String, String> compressImageToVariousSizes(String filepath) {
		if(FileSystemUtility.exists(filepath)) {
			HashMap<String, String> qualityToImgPath = new HashMap<String, String>();
			qualityToImgPath.put(LibraryGlobalConstants.KEY_HIGH_COMPRESSION_QUALITY, compressImage(filepath, CompressionQuality.HIGH));
			qualityToImgPath.put(LibraryGlobalConstants.KEY_MEDIUM_COMPRESSION_QUALITY, compressImage(filepath, CompressionQuality.MEDIUM));
			qualityToImgPath.put(LibraryGlobalConstants.KEY_LOW_COMPRESSION_QUALITY, compressImage(filepath, CompressionQuality.LOW));
			return qualityToImgPath;
		}

		return null;
	}
	
	/**
	 * Returns the target directory path where compressed images get stored.
	 * @return the target directory path.
	 */
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
	
	/**
	 * Returns the suffix to be used as part of the file name based on the compression quality.
	 * @param quality the compression quality.
	 * @return the suffix corresponding to the compression quality.
	 */
	private static String getCompressionSuffix(CompressionQuality quality) {
		String suffixParam = COMPRESSION_SUFFIX_UNKNOWN_QUALITY;

		switch(quality) {
		case HIGH:
			suffixParam = COMPRESSION_SUFFIX_HIGH_QUALITY;
			break;
		case MEDIUM:
			suffixParam = COMPRESSION_SUFFIX_MEDIUM_QUALITY;
			break;
		case LOW:
			suffixParam = COMPRESSION_SUFFIX_LOW_QUALITY;
			break;
		}
		
		return COMPRESSED_IMG_SUFFIX_TEMPLATE.replace("<quality>", suffixParam);
	}
	
	/**
	 * Returns the target path of the compressed image given the original file name, and the suffix to be used
	 * @param name
	 * @param suffix
	 * @return the target file path of the compressed image.
	 */
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
	
	/**
	 * Generates the uri for the camera picture to be clicked. It uses a template file name. 
	 * @param c the activity context.
	 * @return the generated uri.
	 */
	private static Uri generateUriForCameraPicture(Context c) {
		String fileName = FILE_NAME_TEMPLATE.replaceFirst("<time-stamp>", String.valueOf(System.currentTimeMillis()));
    	//create parameters for Intent with filename
    	ContentValues values = new ContentValues();
    	values.put(MediaStore.Images.Media.TITLE, fileName);
    	values.put(MediaStore.Images.Media.DESCRIPTION, LibraryGlobalStrings.CAPTURED_IMG_DESC);
    	//imageUri is the current activity attribute, define and save it for later usage (also in onSaveInstanceState)
    	
    	CAMERA_PICTURE_URI = c.getContentResolver().insert(
    	        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    	
    	return CAMERA_PICTURE_URI;
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
