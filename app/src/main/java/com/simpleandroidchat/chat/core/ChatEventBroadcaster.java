/*
 * Copyright 2016 Chummies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.simpleandroidchat.chat.core;

import android.content.Context;
import android.content.Intent;

import com.simpleandroidchat.component.util.AppLifecycle;
import com.simpleandroidchat.component.util.Logger;


/**
 * Created By
 * Author   : Aung Thaw Aye
 * Email    : ata.aungthawaye@gmail.com
 * Date     : 26/10/16
 */

public class ChatEventBroadcaster {

    public static final String ACTION_INCOMING_MESSAGE =
            "com.chummiesapp.ChatEventBroadcaster.ACTION_INCOMING_MESSAGE";
    public static final String ACTION_DELIVERED_MESSAGE =
            "com.chummiesapp.ChatEventBroadcaster.ACTION_DELIVERED_MESSAGE";
    public static final String ACTION_CHAT_STATE_CHANGED =
            "com.chummiesapp.ChatEventBroadcaster.ACTION_CHAT_STATE_CHANGED";


    public static final String EXTRA_INCOMING_MESSAGE =
            "com.chummiesapp.ChatEventBroadcaster.EXTRA_INCOMING_MESSAGE";
    public static final String EXTRA_DELIVERED_MESSAGE =
            "com.chummiesapp.ChatEventBroadcaster.EXTRA_DELIVERED_MESSAGE";
    public static final String EXTRA_CHAT_STATE_CHANGED =
            "com.chummiesapp.ChatEventBroadcaster.EXTRA_CHAT_STATE_CHANGED";

    private Context context = null;

    public ChatEventBroadcaster(Context context) {
        this.context = context;
    }


    public void broadcastIncomingMessage(ChatMessage chatMessage) {
        Logger.log("ChatEventBroadcaster - broadcastIncomingMessage : start...");
        Logger.log("ChatEventBroadcaster - broadcastIncomingMessage : message.from : " + chatMessage.senderId);
        Logger.log("ChatEventBroadcaster - broadcastIncomingMessage : message.stanzaId : " + chatMessage.stanzaId);
        Logger.log("ChatEventBroadcaster - broadcastIncomingMessage : message.body : " + chatMessage.body);

        // Here we need to check whether the app is not running, or running but in background ?
        // If app is not running or it is running but in background,
        // we will simply notify before broadcasting.
        if (AppLifecycle.isApplicationInForeground() == false || AppLifecycle.isApplicationVisible() == false) {
            Logger.log(
                    "ChatEventBroadcaster - broadcastIncomingMessage : require notification because " +
                            "app is not running, or in background...");
        } else {
            Logger.log(
                    "ChatEventBroadcaster - broadcastIncomingMessage : NOT require notification because " +
                            "app is running in foreground...");
        }

        Intent intent = new Intent(ACTION_INCOMING_MESSAGE);
        intent.putExtra(EXTRA_INCOMING_MESSAGE, chatMessage);
        this.context.sendBroadcast(intent);
        Logger.log("ChatEventBroadcaster - broadcastIncomingMessage : finish...");
    }

    public void broadcastStateChanged(ParticipantChatState participantChatState) {
        Logger.log("ChatEventBroadcaster - broadcastStateChanged : start...");
        Intent intent = new Intent(ACTION_CHAT_STATE_CHANGED);
        intent.putExtra(EXTRA_CHAT_STATE_CHANGED, participantChatState);
        this.context.sendBroadcast(intent);
        Logger.log("ChatEventBroadcaster - broadcastStateChanged : finish...");
    }

    public void broadcastMessageDelivered(MessageDeliveryInfo messageDeliveryInfo) {
        Logger.log("ChatEventBroadcaster - broadcastMessageDelivered : start...");
        Intent intent = new Intent(ACTION_DELIVERED_MESSAGE);
        intent.putExtra(EXTRA_DELIVERED_MESSAGE, messageDeliveryInfo);
        this.context.sendBroadcast(intent);
        Logger.log("ChatEventBroadcaster - broadcastMessageDelivered : finish...");
    }
}
