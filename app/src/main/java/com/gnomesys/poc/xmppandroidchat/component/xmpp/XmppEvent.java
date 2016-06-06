package com.gnomesys.poc.xmppandroidchat.component.xmpp;

/**
 * Created By
 * Author   : Aung Thaw Aye
 * Email    : ata.aungthawaye@gmail.com (aungthawaye@gnomesys.com)
 * Date     : 5/6/16
 */
public interface XmppEvent {

    String CONNECTED = "connected";
    String AUTHENTICATED = "authenticated";

    void onLogin();

    void onConnected();

    void onDisconnect();

    void onIncomingMessage();

    void onReceiptMessageReceived();

    void onChatCreated();
}
