package com.gnomesys.poc.xmppandroidchat.component;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.chatstates.ChatState;

/**
 * Created By
 * Author   : Aung Thaw Aye
 * Email    : ata.aungthawaye@gmail.com (aungthawaye@gnomesys.com)
 * Date     : 8/6/16
 */
public interface ChatEventListener {

    void onStateChanged(ChatState state);

    void onMessageReceived(Message message);

    void onConnected(boolean connected);

    void onConnectionClosed();

    void onConnectionClosedOnError(Exception e);

    void onReconnectionSuccessful();

    void onReconnectingIn(int seconds);

    void onReconnectionFailed(Exception e);

    void onReceiptReceived(String fromJid, String toJid, String receiptId, Stanza receipt);
}
