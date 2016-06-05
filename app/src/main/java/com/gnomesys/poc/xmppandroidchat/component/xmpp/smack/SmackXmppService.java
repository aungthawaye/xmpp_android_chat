package com.gnomesys.poc.xmppandroidchat.component.xmpp.smack;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

public class SmackXmppService extends Service implements ConnectionListener {

    public final static String EXTRA_HOST = "SmackXmppService.HOST";
    public final static String EXTRA_PORT = "SmackXmppService.PORT";
    public final static String EXTRA_SERVICE_NAME = "SmackXmppService.SERVICE_NAME";

    private final static SmackXmppManager SMACK_XMPP_MANAGER = new SmackXmppManager();

    private final SmackXmppServiceBinder smackXmppServiceBinder = new SmackXmppServiceBinder();

    private boolean connected = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Once service has been started, it will initialize XMPP connection configuration.
        final String host = intent.getStringExtra(EXTRA_HOST);
        final int port = intent.getIntExtra(EXTRA_PORT, 5222);
        final String serviceName = intent.getStringExtra(EXTRA_SERVICE_NAME);

        SMACK_XMPP_MANAGER.initialize(host, port, serviceName);
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
        return this.smackXmppServiceBinder;
    }

    public void connect() {
        SMACK_XMPP_MANAGER.connect(this,
                new SmackXmppManager.SuccessOrFailureCallback() {
                    @Override
                    public void onSuccess() {
                        SmackXmppService.this.connected = true;
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        SmackXmppService.this.connected = false;
                    }
                });
    }

    public void login(final String username,
                      final String password,
                      final String resourceName,
                      final SmackXmppManager.SuccessOrFailureCallback callback) {
        SMACK_XMPP_MANAGER.login(username, password, resourceName, callback);
    }

    public void disconnect() {
        SMACK_XMPP_MANAGER.disconnect();
    }

    public boolean isConnected() {
        return SMACK_XMPP_MANAGER.isConnected();
    }

    @Override
    public void connected(XMPPConnection connection) {

    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {

    }

    @Override
    public void connectionClosed() {

    }

    @Override
    public void connectionClosedOnError(Exception e) {

    }

    @Override
    public void reconnectionSuccessful() {

    }

    @Override
    public void reconnectingIn(int seconds) {

    }

    @Override
    public void reconnectionFailed(Exception e) {

    }

    /**
     * Helper for Binder
     */
    public class SmackXmppServiceBinder extends Binder {
        public SmackXmppService getService() {
            return SmackXmppService.this;
        }
    }

}
