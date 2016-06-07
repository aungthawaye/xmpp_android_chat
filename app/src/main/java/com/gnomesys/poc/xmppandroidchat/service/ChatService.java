package com.gnomesys.poc.xmppandroidchat.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.gnomesys.poc.xmppandroidchat.component.chat.MessagingManager;
import com.gnomesys.poc.xmppandroidchat.component.helper.ResultCallback;
import com.gnomesys.poc.xmppandroidchat.component.chat.ChatEventManager;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;

public class ChatService extends Service  {

    public final static String EXTRA_HOST = "ChatService.HOST";
    public final static String EXTRA_PORT = "ChatService.PORT";
    public final static String EXTRA_SERVICE_NAME = "ChatService.SERVICE_NAME";

    private ChatEventManager chatEventManager;
    private MessagingManager messagingManager;

    private final XmppServiceBinder xmppServiceBinder = new XmppServiceBinder();

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("XMPP", "Creating ChatService...");
        this.messagingManager = MessagingManager.getInstance();
        this.chatEventManager = new ChatEventManager(this.getApplicationContext());
        Log.d("XMPP", "Created ChatService. It's ready for initialization.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Once service has been started, it will initialize XMPP connection configuration.
        final String host = intent.getStringExtra(EXTRA_HOST);
        final int port = intent.getIntExtra(EXTRA_PORT, 5222);
        final String serviceName = intent.getStringExtra(EXTRA_SERVICE_NAME);

        this.messagingManager.initialize(host, port, serviceName);
        // Try to connect to XMPP server after initialization.
        this.connect();

        return START_NOT_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        // In case, user terminate your app by swiping.
        this.disconnect();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return this.xmppServiceBinder;
    }

    public void connect() {
        this.messagingManager.connect();
    }

    public void login(final String username,
                      final String password,
                      final String resourceName,
                      final ResultCallback callback) throws MessagingManager.ServiceUnavailableException {

        this.messagingManager.login(username, password, resourceName, callback);
    }

    public void disconnect() {
        this.messagingManager.disconnect();
    }

    public boolean isConnected() {
        return this.messagingManager.isConnected();
    }

    public void sendMessage(String to, Message message)
            throws SmackException.NotConnectedException, MessagingManager.ServiceUnavailableException {
        this.messagingManager.sendMessage(to, message);
    }

    /**
     * Helper for Binder
     */
    public class XmppServiceBinder extends Binder {
        public ChatService getService() {
            return ChatService.this;
        }
    }

}
