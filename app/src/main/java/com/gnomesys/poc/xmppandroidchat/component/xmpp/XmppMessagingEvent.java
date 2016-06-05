package com.gnomesys.poc.xmppandroidchat.component.xmpp;

/**
 * Created By
 * Author   : Aung Thaw Aye
 * Email    : ata.aungthawaye@gmail.com (aungthawaye@gnomesys.com)
 * Date     : 5/6/16
 */
public interface XmppMessagingEvent {

    void onLogin();
    void onConnect();
    void onDisconnect();
    void onIncomingMessage();
    void onReceiptMessageReceived();
}
