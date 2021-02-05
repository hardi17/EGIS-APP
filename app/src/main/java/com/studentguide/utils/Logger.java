package com.studentguide.utils;

import android.util.Log;

public class Logger {

    public static String LOG_TAG = "Logger";
    public static boolean isDebug = true;

    public static void setIsDebug(boolean isdebug) {
        isDebug = isdebug;
    }

    public static boolean getIsDebuge() {
        return isDebug;
    }

    public static void i(String i) {
      if (getIsDebuge()) {
            Log.i(LOG_TAG, "" + i);
        }
    }

    public static void d(String d) {
        if (getIsDebuge()) {
            Log.d(LOG_TAG, "" + d);
        }
    }

    public static void v(String v) {
        if (getIsDebuge()) {
            Log.v(LOG_TAG, "" + v);
        }
    }

    public static void w(String w) {
        if (getIsDebuge()) {
            Log.w(LOG_TAG, "" + w);
        }
    }

    public static void e(String e) {
        if (getIsDebuge()) {
            Log.e(LOG_TAG, "" + e);
        }
    }
}
