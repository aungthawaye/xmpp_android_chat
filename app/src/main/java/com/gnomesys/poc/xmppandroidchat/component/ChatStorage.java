package com.gnomesys.poc.xmppandroidchat.component;

import io.realm.Realm;

/**
 * Created By
 * Author   : Aung Thaw Aye
 * Email    : ata.aungthawaye@gmail.com (aungthawaye@gnomesys.com)
 * Date     : 8/6/16
 */
public class ChatStorage {
    private final static void save(final ChatMessage message){
        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();
        realm.copyToRealm(message);
        realm.commitTransaction();
    }
}
