package com.gnomesys.poc.xmppandroidchat.component.activity;

import android.support.v7.app.AppCompatActivity;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;

/**
 * Created by fendra on 5/6/16.
 */
public class XmppAwareActivity extends AppCompatActivity implements ChatMessageListener {

    /**
     * It will be called whenever MessagingManager receive the chat message.
     *
     */
    @Override
    public void processMessage(Chat chat, Message message) {

    }
}
