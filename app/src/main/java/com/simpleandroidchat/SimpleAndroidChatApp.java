package com.simpleandroidchat;

import android.app.Application;
import android.content.Intent;

import com.simpleandroidchat.chat.service.ChatService;
import com.simpleandroidchat.chat.util.UserInfoStorage;
import com.simpleandroidchat.component.util.AppLifecycle;
import com.simpleandroidchat.component.util.Logger;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by aungthawaye on 23/4/17.
 */

public class SimpleAndroidChatApp extends Application {
    @Override
    public void onCreate() {
        Logger.log("Starting application ... ");

        super.onCreate();

        Logger.log("Build Config DEBUG : " + BuildConfig.DEBUG);

        // Register our AppLifecycle detector
        this.registerActivityLifecycleCallbacks(new AppLifecycle());

        // Initialize UserInfoStorage
        Logger.log("SimpleAndroidChatApp - onCreate : initializing UserInfoStorage...");
        UserInfoStorage.initialize(this.getApplicationContext());

        // Start chat service
        Logger.log("SimpleAndroidChatApp - onCreate : starting ChatService...");
        Intent intent = new Intent(this, ChatService.class);
        this.startService(intent);

        Logger.log("SimpleAndroidChatApp - onCreate : initializing CalligraphyConfig...");
        // Setup default font for app
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/NotoSansUI-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
