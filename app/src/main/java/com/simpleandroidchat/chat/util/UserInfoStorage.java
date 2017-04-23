package com.simpleandroidchat.chat.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created By
 * Author   : Aung Thaw Aye
 * Email    : ata.aungthawaye@gmail.com (aungthawaye@gnomesys.com)
 * Date     : 26/10/16
 */

public class UserInfoStorage {

    private final static String PREFERENCES_NAME = "com.chummiesapp.UserInfoStorage";
    private final static String KEY_USER_ID = "com.chummiesapp.UserInfoStorage.KEY_USER_ID";
    private final static String KEY_PASSWORD = "com.chummiesapp.UserInfoStorage.KEY_PASSWORD";

    private static UserInfoStorage userInfoStorage = null;

    private SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editor = null;

    private UserInfoStorage(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        this.editor = this.sharedPreferences.edit();
    }

    public synchronized static void initialize(Context context) {
        if (userInfoStorage == null) {
            userInfoStorage = new UserInfoStorage(context);
        }
    }

    public synchronized static UserInfoStorage getInstance() {

        return UserInfoStorage.userInfoStorage;
    }

    public void saveCredentials(String userId, String secretKey) {
        this.editor.putString(KEY_USER_ID, userId);
        this.editor.commit();

        this.editor.putString(KEY_PASSWORD, secretKey);
        this.editor.commit();
    }


    public String getUserId() {
        return this.sharedPreferences.getString(KEY_USER_ID, null);
    }

    public String getPassword() {
        return this.sharedPreferences.getString(KEY_PASSWORD, null);
    }

    public boolean isCredentialsAvailable() {
        return (this.getUserId() != null && this.getUserId().trim().length() > 0) && (this
                .getPassword() != null && this.getPassword().trim().length() > 0);
    }

    public void cleanUpCredentials() {
        this.saveCredentials("", "");
    }
}
