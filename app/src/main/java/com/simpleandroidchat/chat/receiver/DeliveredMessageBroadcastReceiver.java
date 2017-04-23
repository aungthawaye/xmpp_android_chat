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
import com.simpleandroidchat.chat.core.MessageDeliveryInfo;
import com.simpleandroidchat.chat.util.XMPPUtil;


/**
 * Created By
 * Author   : Aung Thaw Aye
 * Email    : ata.aungthawaye@gmail.com
 * Date     : 27/10/16
 */

public class DeliveredMessageBroadcastReceiver extends BroadcastReceiver {

    private Context context;
    private String participantId;
    private MessageDeliveryListener messageDeliveryListener;

    /**
     * @param context
     * @param participantId
     * @param messageDeliveryListener
     */
    public DeliveredMessageBroadcastReceiver(
            Context context,
            String participantId,
            MessageDeliveryListener messageDeliveryListener) {
        this.context = context;
        this.participantId = XMPPUtil.fixJabberId(participantId);
        this.messageDeliveryListener = messageDeliveryListener;
    }

    /**
     * Register this broadcast receiver to listen incoming chat messages.
     */
    public void registerToContext() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ChatEventBroadcaster.ACTION_DELIVERED_MESSAGE);
        this.context.registerReceiver(this, intentFilter);
    }

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
        Object data = intent.getExtras().get(ChatEventBroadcaster.EXTRA_DELIVERED_MESSAGE);

        if (data instanceof MessageDeliveryInfo == false) {
            return;
        }

        MessageDeliveryInfo messageDeliveryInfo = (MessageDeliveryInfo) data;
        if (messageDeliveryInfo.participantId != null && messageDeliveryInfo.participantId.equals(this.participantId)) {
            // Invoke caller's callback
            this.messageDeliveryListener.onMessageDelivered(messageDeliveryInfo);
        }

    }

    public interface MessageDeliveryListener {
        void onMessageDelivered(MessageDeliveryInfo messageDeliveryInfo);
    }
}
