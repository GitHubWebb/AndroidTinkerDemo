package com.wwx.androidtinkerdemo.tinkersdk.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

/**
* <pre>
*      author:         chenjx
*      date:           2016/7/12.
*      description:    日志类
*      upUser:         wangwx
*      upDate:         2022-07-05 11:30:54
*      upRemark:       支持在子模块获取Debug模式
*      version:        v1.0
* </pre>
*/
public class LogFactory {
    private static Boolean isDebug = null;

    public static boolean isDebug() {
        return isDebug == null ? false : isDebug.booleanValue();
    }

    // public final static boolean DEBUG = TinkerProxyApplicationLike.isDebug;

    public final static String TAG = "LTTJ";

    public static void i(String msg) {
        if (isDebug())
            Log.i(TAG, msg);
    }

    public static void i(String tag, String msg) {
        if (isDebug())
            Log.i(tag, msg);
    }

    public static void d(String msg) {
        if (isDebug())
            Log.d(TAG, msg);
    }

    public static void d(String tag, String msg) {
        if (isDebug())
            Log.d(tag, msg);
    }

    public static void v(String msg) {
        if (isDebug())
            Log.v(TAG, msg);
    }

    public static void v(String tag, String msg) {
        if (isDebug())
            Log.v(tag, msg);
    }

    public static void w(String msg) {
        if (isDebug())
            Log.w(TAG, msg);
    }

    public static void w(String tag, String msg) {
        if (isDebug())
            Log.w(tag, msg);
    }

    public static void e(String msg) {
        if (isDebug())
            Log.e(TAG, msg);
    }

    public static void e(String tag, String msg) {
        if (isDebug())
            Log.e(tag, msg);
    }


    public static void println(Exception e) {
        if (isDebug() && e != null) {
            e.printStackTrace();
        }

    }

    /**
     * Sync lib debug with app's debug value. Should be called in module Application
     *
     * @param context
     */
    public static void syncIsDebug(Context context, boolean isDebugBuild) {
        if (isDebug == null) {
            isDebug = isDebugBuild && (context.getApplicationInfo() != null &&
                    (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);
        }
    }
}
