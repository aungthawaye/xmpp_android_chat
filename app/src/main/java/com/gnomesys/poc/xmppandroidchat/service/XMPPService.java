package com.gnomesys.poc.xmppandroidchat.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.gnomesys.poc.xmppandroidchat.component.ChatEventListener;
import com.gnomesys.poc.xmppandroidchat.component.ChatEventProcessor;
import com.gnomesys.poc.xmppandroidchat.component.SmackXMPPManager;
import com.gnomesys.poc.xmppandroidchat.component.ResultCallback;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;

public class XMPPService extends Service {

    public final static String EXTRA_HOST = "XMPPService.HOST";
    public final static String EXTRA_PORT = "XMPPService.PORT";
    public final static String EXTRA_SERVICE_NAME = "XMPPService.SERVICE_NAME";

    private SmackXMPPManager smackXMPPManager;
    private ChatEventListener chatEventProcessor;

    private final XmppServiceBinder xmppServiceBinder = new XmppServiceBinder();

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("XMPP", "Creating XMPPService...");
        this.smackXMPPManager = SmackXMPPManager.getInstance();
        this.smackXMPPManager.setListenerDelegate(new ChatEventProcessor(this.getApplicationContext()));
        Log.d("XMPP", "Created XMPPService. It's ready for initialization.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Once service has been started, it will initialize XMPP connection configuration.
        final String host = intent.getStringExtra(EXTRA_HOST);
        final int port = intent.getIntExtra(EXTRA_PORT, 5222);
        final String serviceName = intent.getStringExtra(EXTRA_SERVICE_NAME);

        this.smackXMPPManager.initialize(host, port, serviceName);
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
        this.smackXMPPManager.connect();
    }

    public void login(final String username,
                      final String password,
                      final String resourceName,
                      final ResultCallback callback) throws SmackXMPPManager.ServiceUnavailableException {

        this.smackXMPPManager.login(username, password, resourceName, callback);
    }

    public void disconnect() {
        this.smackXMPPManager.disconnect();
    }

    public boolean isConnected() {
        return this.smackXMPPManager.isConnected();
    }

    public String sendMessage(String to, Message message)
            throws SmackException.NotConnectedException, SmackXMPPManager.ServiceUnavailableException {
        return this.smackXMPPManager.sendMessage(to, message);
    }

    /**
     * Helper for Binder
     */
    public class XmppServiceBinder extends Binder {
        public XMPPService getService() {
            return XMPPService.this;
        }
    }

}
