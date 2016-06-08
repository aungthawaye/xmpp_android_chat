package com.gnomesys.poc.xmppandroidchat.component;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.chatstates.ChatState;

/**
 * Created By
 * Author   : Aung Thaw Aye
 * Email    : ata.aungthawaye@gmail.com (aungthawaye@gnomesys.com)
 * Date     : 5/6/16
 */
public class ChatBroadcastReceiver extends BroadcastReceiver implements ChatEventListener {

    private Context context;
    private ChatEventListener listener;

    public ChatBroadcastReceiver(Context context, ChatEventListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

    }

    @Override
    public void onStateChanged(ChatState state) {

    }

    @Override
    public void onMessageReceived(Message message) {

    }

    @Override
    public void onConnected(boolean connected) {

    }

    @Override
    public void onConnectionClosed() {

    }

    @Override
    public void onConnectionClosedOnError(Exception e) {

    }

    @Override
    public void onReconnectionSuccessful() {

    }

    @Override
    public void onReconnectingIn(int seconds) {

    }

    @Override
    public void onReconnectionFailed(Exception e) {

    }

    @Override
    public void onReceiptReceived(String fromJid, String toJid, String receiptId, Stanza receipt) {

    }
}
