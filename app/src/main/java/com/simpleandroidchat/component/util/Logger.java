package com.simpleandroidchat.component.util;

import android.util.Log;

import com.simpleandroidchat.BuildConfig;


/**
 * Created By
 * Author   : Aung Thaw Aye
 * Email    : ata.aungthawaye@gmail.com
 * Date     : 11/10/16
 */

public class Logger {

    private final static String TAG = "simpleandroidchat";

    public final static void log(String message) {
        if (BuildConfig.DEBUG)
            Log.d(Logger.TAG, "thread [" + Thread.currentThread().getName() + "] - " + message);
    }
}