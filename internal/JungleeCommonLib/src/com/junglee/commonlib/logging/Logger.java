package com.junglee.commonlib.logging;

import android.util.Log;

public class Logger
{
	public static final int LEVEL_VERBOSE = 0;
	public static final int LEVEL_DEBUG = 1;
	public static final int LEVEL_INFO = 2;
	public static final int LEVEL_WARNING = 3;
	public static final int LEVEL_ERROR = 4;
	public static final int LEVEL_EXCEPTION = 5;


    /**
     * Only log statements that are higher than LOGGER_LEVEL.
     *
     * Recommended settings: Debugging: Set this to Log.VERBOSE Internal builds:
     * Set this to Log.DEBUG Release builds: Set this to Log.INFO
     *
     */
    private static int LOGGER_LEVEL;

    /**
     * Convenience, for when caller doesn't want to set a tag
     */
    private static String DEFAULT_TAG = "JungleeNativeApp";
    
    public static void initLogger(int logLevel) {
    	LOGGER_LEVEL = logLevel;
    }

    public static int verbose(String message) {
    	return verbose(DEFAULT_TAG, message);
    }
    public static int verbose(String tag, String message) {
    	if (LOGGER_LEVEL <= LEVEL_VERBOSE) {
            Log.v(tag, message);
            return 0;
        } else {
            return -1;
        }
    }
    public static int verbose(String tag, String secondaryTag, String message) {
    	return verbose(tag, addSecTagToLogMessage(message, secondaryTag));
    }

    public static int debug(String message) {
    	return debug(DEFAULT_TAG, message);
    }
    public static int debug(String tag, String message) {
    	if (LOGGER_LEVEL <= LEVEL_DEBUG) {
            Log.d(tag, message);
            return 0;
        } else {
            return -1;
        }
    }
    public static int debug(String tag, String secondaryTag, String message) {
    	return debug(tag, addSecTagToLogMessage(message, secondaryTag));
    }

    public static int info(String message) {
    	return info(DEFAULT_TAG, message);
    }
    public static int info(String tag, String message) {
    	if (LOGGER_LEVEL <= LEVEL_INFO) {
            Log.i(tag, message);
            return 0;
        } else {
            return -1;
        }
    }
    public static int info(String tag, String secondaryTag, String message) {
    	return info(tag, addSecTagToLogMessage(message, secondaryTag));
    }

    public static int warn(String message) {
    	return warn(DEFAULT_TAG, message);
    }
    public static int warn(String tag, String message) {
    	if (LOGGER_LEVEL <= LEVEL_WARNING) {
            Log.w(tag, message);
            return 0;
        } else {
            return -1;
        }
    }
    public static int warn(String tag, String secondaryTag, String message) {
    	return warn(tag, addSecTagToLogMessage(message, secondaryTag));
    }

    public static int error(String message) {
    	return error(DEFAULT_TAG, message);
    }
    public static int error(String tag, String message) {
    	if (LOGGER_LEVEL <= LEVEL_ERROR) {
            Log.e(tag, message);
            return 0;
        } else {
            return -1;
        }
    }
    public static int error(String tag, String secondaryTag, String message) {
    	return error(tag, addSecTagToLogMessage(message, secondaryTag));
    }

    public static int exception(String message) {
    	return exception(DEFAULT_TAG, message);
    }
    public static int exception(String tag, String message) {
    	if (LOGGER_LEVEL <= LEVEL_EXCEPTION) {
            Log.e(tag, message);
            return 0;
        } else {
            return -1;
        }
    	
    	//also report exception
    }
    public static int exception(String tag, String secondaryTag, String message) {
    	return exception(tag, addSecTagToLogMessage(message, secondaryTag));
    }
    
    
    
    
    public static void sendLogs() {
    	// upload logs to the server
    }
    
    public static void leaveBreadCrumb(String breadcrumb) {
    	
    }

    
    
    
    private static String addSecTagToLogMessage(String message, String tag) {
    	String logMsg = String.format("[%s] %s", tag, message);
    	return logMsg;
    }
};
