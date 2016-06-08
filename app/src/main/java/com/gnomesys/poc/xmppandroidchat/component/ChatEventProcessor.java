package com.gnomesys.poc.xmppandroidchat.component;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.chatstates.ChatState;

/**
 * Created By
 * Author   : Aung Thaw Aye
 * Email    : ata.aungthawaye@gmail.com (aungthawaye@gnomesys.com)
 * Date     : 8/6/16
 */
public class ChatEventProcessor implements ChatEventListener {

    public final static String ACTION_CHAT_EVENT = "ChatEventProcessor.CHAT_EVENT";

    private Context context;

    public ChatEventProcessor(Context context) {
        this.context = context;
    }

    @Override
    public void onStateChanged(ChatState state) {
        Log.d("XMPP", "onStateChanged : " + state.toString());

        Intent intent = new Intent(ACTION_CHAT_EVENT);
        intent.putExtra("event", "onStateChanged");
        intent.putExtra("state", state.toString());
        LocalBroadcastManager.getInstance(this.context).sendBroadcast(intent);
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
        Log.d("XMPP",
                "onReceiptReceived : fromJid : [" + fromJid + "] toJid : [" + toJid + "] receiptId : [" + receiptId + "] stanzaId : [" + receipt
                        .getStanzaId() + "].");
    }
}
