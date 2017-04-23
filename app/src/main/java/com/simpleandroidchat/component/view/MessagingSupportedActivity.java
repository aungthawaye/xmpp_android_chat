package com.simpleandroidchat.component.view;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.simpleandroidchat.chat.receiver.DeliveredMessageBroadcastReceiver;
import com.simpleandroidchat.chat.receiver.IncomingMessageBroadcastReceiver;
import com.simpleandroidchat.chat.service.ChatService;
import com.simpleandroidchat.chat.util.XMPPUtil;
import com.simpleandroidchat.component.util.Logger;

public abstract class MessagingSupportedActivity extends BasicActivity
        implements IncomingMessageBroadcastReceiver.IncomingMessageListener,
        DeliveredMessageBroadcastReceiver.MessageDeliveryListener {

    public final static String EXTRA_PARTICIPANT_ID = "com.chummiesapp.MessagingSupportedActivity.EXTRA_PARTICIPANT_ID";

    private String participantId = null;
    private ChatService chatService = null;
    private IncomingMessageBroadcastReceiver incomingMessageBroadcastReceiver = null;
    private DeliveredMessageBroadcastReceiver deliveredMessageBroadcastReceiver = null;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Logger.log("MessagingSupportedActivity - onServiceConnected...");
            ChatService.ChatServiceBinder binder = (ChatService.ChatServiceBinder) iBinder;
            MessagingSupportedActivity.this.chatService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            // Do nothing
            MessagingSupportedActivity.this.chatService = null;
        }
    };

    public String getParticipantId() {
        return this.participantId;
    }

    public ChatService getChatService() {
        return this.chatService;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.participantId = this.getIntent().getExtras().getString(EXTRA_PARTICIPANT_ID, null);
        this.participantId = XMPPUtil.fixJabberId(this.participantId);
        Logger.log("MessagingSupportedActivity - onCreate : participantId : " + this.participantId);

        this.incomingMessageBroadcastReceiver = new IncomingMessageBroadcastReceiver(this, this.participantId, this);
        this.deliveredMessageBroadcastReceiver = new DeliveredMessageBroadcastReceiver(this, this.participantId, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (this.chatService != null) {
            this.unbindService(this.serviceConnection);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, ChatService.class);
        this.bindService(intent, this.serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.log("MessagingSupportedActivity - onPause : unregistering incomingMessageBroadcastReceiver ...");
        this.incomingMessageBroadcastReceiver.unregisterFromContext();
        Logger.log("MessagingSupportedActivity - onPause : unregistering incomingMessageBroadcastReceiver ...");
        this.deliveredMessageBroadcastReceiver.unregisterFromContext();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.log("MessagingSupportedActivity - onResume : registering incomingMessageBroadcastReceiver ...");
        this.incomingMessageBroadcastReceiver.registerToContext();
        Logger.log("MessagingSupportedActivity - onResume : registering deliveredMessageBroadcastReceiver ...");
        this.deliveredMessageBroadcastReceiver.registerToContext();
    }
}