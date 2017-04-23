package com.simpleandroidchat.chat.core;

/**
 * Created by aungthawaye on 23/4/17.
 */

// Callback for various result
public interface ResultCallback {
    void onSuccess();

    void onFailure(Exception e);
}