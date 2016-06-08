package com.gnomesys.poc.xmppandroidchat.component;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created By
 * Author   : Aung Thaw Aye
 * Email    : ata.aungthawaye@gmail.com (aungthawaye@gnomesys.com)
 * Date     : 8/6/16
 */
public class ChatMessage extends RealmObject {

    @PrimaryKey
    private String stanzaId;
    private String body;
    private String sender;
    private String recipient;
    private long senderLocalTime;
    private long recipientLocalTime;
    private long persistedTime;
    private short type; // 1 : Text, 2 : Binary
    private boolean read;
    private boolean received;
}
