package com.gnomesys.poc.xmppandroidchat.component.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created By
 * Author   : Aung Thaw Aye
 * Email    : ata.aungthawaye@gmail.com (aungthawaye@gnomesys.com)
 * Date     : 5/6/16
 */
public class ChatEventBroadcastReceiver extends BroadcastReceiver {

    private Context context;
    private ChatEventManager.XmppEvent handler;

    public ChatEventBroadcastReceiver(Context context, ChatEventManager.XmppEvent handler) {
        this.context = context;
        this.handler = handler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String event = intent.getStringExtra(ChatEventManager.EXTRA_EVENT_NAME);

        switch (event) {
            case ChatEventManager.XmppEvent.CONNECTED:
                this.handler.onConnected();
                break;
        }
    }

}
