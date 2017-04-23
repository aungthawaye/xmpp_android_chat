package com.simpleandroidchat.component.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Created By
 * Author   : Aung Thaw Aye
 * Email    : ata.aungthawaye@gmail.com (aungthawaye@gnomesys.com)
 * Date     : 14/11/16
 */

public class AppLifecycle implements Application.ActivityLifecycleCallbacks {

    private static int resumed = 0;
    private static int paused = 0;
    private static int started = 0;
    private static int stopped = 0;

    public static boolean isApplicationVisible() {
        return started > stopped;
    }

    public static boolean isApplicationInForeground() {
        return resumed > paused;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Logger.log("AppLifecycle - onActivityResumed : " + activity.getClass().getName());
        ++resumed;
        Logger.log("AppLifecycle - resumed : " + resumed + " paused : " + paused);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Logger.log("AppLifecycle - onActivityPaused : " + activity.getClass().getName());
        ++paused;
        Logger.log("AppLifecycle - resumed : " + resumed + " paused : " + paused);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Logger.log("AppLifecycle - onActivityStarted : " + activity.getClass().getName());
        ++started;
        Logger.log("AppLifecycle - started : " + started + " stopped : " + stopped);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Logger.log("AppLifecycle - onActivityStopped : " + activity.getClass().getName());
        ++stopped;
        Logger.log("AppLifecycle - started : " + started + " stopped : " + stopped);
    }
}
