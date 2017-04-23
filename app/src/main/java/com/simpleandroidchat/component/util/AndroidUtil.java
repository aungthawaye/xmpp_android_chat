package com.simpleandroidchat.component.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Process;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Created By
 * Author   : Aung Thaw Aye
 * Email    : ata.aungthawaye@gmail.com
 * Date     : 4/12/16
 */

public class AndroidUtil {

    /**
     * To kill app for some reason.
     *
     * @param activity
     */
    public static void quit(Activity activity) {
        activity.finish();
        Process.killProcess(Process.myPid());
    }

    /**
     * To check whether the device has Google Play services or not. If device doesn't have it will quit.
     */
    public static void checkGooglePlayServices(final Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                Dialog dialog = googleApiAvailability.getErrorDialog(activity, status, 2404);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        AndroidUtil.quit(activity);
                    }
                });
                dialog.show();
            }
        }
    }
}
