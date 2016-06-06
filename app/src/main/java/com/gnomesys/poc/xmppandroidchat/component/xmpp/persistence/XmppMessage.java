package com.gnomesys.poc.xmppandroidchat.component.xmpp.persistence;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created By
 * Author   : Aung Thaw Aye
 * Email    : ata.aungthawaye@gmail.com (aungthawaye@gnomesys.com)
 * Date     : 6/6/16
 */
public class XmppMessage extends RealmObject {

    @PrimaryKey
    private String stanzaId;
    private String body;
    private String senderId;
    private String recipientId;
    private short type;
    private long senderLocalTime;
    private long recipientLocalTime;
    private boolean seen;
}
