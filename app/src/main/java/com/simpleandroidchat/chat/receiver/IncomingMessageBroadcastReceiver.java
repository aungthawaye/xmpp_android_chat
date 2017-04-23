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

package com.simpleandroidchat.chat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.simpleandroidchat.chat.core.ChatEventBroadcaster;
import com.simpleandroidchat.chat.core.ChatMessage;
import com.simpleandroidchat.chat.util.XMPPUtil;
import com.simpleandroidchat.component.util.Logger;


/**
 * Created By
 * Author   : Aung Thaw Aye
 * Email    : ata.aungthawaye@gmail.com
 * Date     : 27/10/16
 */

public class IncomingMessageBroadcastReceiver extends BroadcastReceiver {

    private Context context;
    private String participantId;
    private IncomingMessageListener incomingMessageListener;

    /**
     * @param context
     * @param participantId
     * @param incomingMessageListener
     */
    public IncomingMessageBroadcastReceiver(
            Context context,
            String participantId,
            IncomingMessageListener incomingMessageListener) {
        this.context = context;
        this.participantId = participantId != null ? XMPPUtil.fixJabberId(participantId) : null;
        this.incomingMessageListener = incomingMessageListener;
    }

    /**
     * Register this broadcast receiver to listen incoming chat messages.
     */
    public void registerToContext() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ChatEventBroadcaster.ACTION_INCOMING_MESSAGE);
        this.context.registerReceiver(this, intentFilter);
    }

    /**
     * To stop receiving broadcast
     */
    public void unregisterFromContext() {
        this.context.unregisterReceiver(this);
    }

    /**
     * We will receive chat messages through broadcast which is done by ChatEventBroadcaster.
     * Then we pass these chat messages to caller (e.g your ChatActivity).
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.log("IncomingMessageBroadcastReceiver - onReceive : start...");
        Object data = intent.getExtras().get(ChatEventBroadcaster.EXTRA_INCOMING_MESSAGE);

        if (data instanceof ChatMessage == false) {
            Logger.log("IncomingMessageBroadcastReceiver - onReceive : stop continuing...");
            return;
        }

        // Upon receiving chat messages, we have to check whether current Activity is for chatting or not.
        // For example, you may be on Lading/HomeActivity which are not ChatActivity. If you are not in
        // ChatActivity, we will display incoming message in Notification bar.

        // Sometimes you are in ChatActivity, but incoming message's not for your current chat session. Then, we
        // display notification.

        // If chat message is for your current session, we will simply pass this message to IncomingMessageListener.

        ChatMessage chatMessage = (ChatMessage) data;
        String remoteSenderId = chatMessage.senderId;

        Logger.log("IncomingMessageBroadcastReceiver - onReceive : received remoteSenderId : " + remoteSenderId);
        if (participantId != null && participantId.equals(remoteSenderId)) {
            Logger.log("IncomingMessageBroadcastReceiver - onReceive : fire listener...");
            this.incomingMessageListener.onMessageReceived(chatMessage);
        } else {
            // Notify incoming message.
            Logger.log("IncomingMessageBroadcastReceiver - onReceive : notify incoming message...");
        }

    }

    public interface IncomingMessageListener {
        void onMessageReceived(ChatMessage chatMessage);
    }
}
