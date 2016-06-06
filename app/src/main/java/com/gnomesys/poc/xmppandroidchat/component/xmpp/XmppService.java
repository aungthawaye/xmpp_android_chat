package com.gnomesys.poc.xmppandroidchat.component.xmpp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;

public class XmppService extends Service implements ConnectionListener, ChatManagerListener, ChatMessageListener,
        FileTransferListener {

    public final static String EXTRA_HOST = "XmppService.HOST";
    public final static String EXTRA_PORT = "XmppService.PORT";
    public final static String EXTRA_SERVICE_NAME = "XmppService.SERVICE_NAME";

    private XmppEventManager xmppEventManager;
    private XmppManager xmppManager;

    private final XmppServiceBinder xmppServiceBinder = new XmppServiceBinder();

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("XMPP", "Creating XmppService...");
        this.xmppManager = new XmppManager();
        this.xmppEventManager = new XmppEventManager(this.getApplicationContext());
        Log.d("XMPP", "Created XmppService. It's ready for initialization.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Once service has been started, it will initialize XMPP connection configuration.
        final String host = intent.getStringExtra(EXTRA_HOST);
        final int port = intent.getIntExtra(EXTRA_PORT, 5222);
        final String serviceName = intent.getStringExtra(EXTRA_SERVICE_NAME);

        this.xmppManager.initialize(host, port, serviceName);
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
        xmppManager.connect(this);
    }

    public void login(final String username,
                      final String password,
                      final String resourceName,
                      final SuccessOrFailureCallback callback) {
        this.xmppManager.login(username, password, resourceName,
                new SuccessOrFailureCallback() {
                    @Override
                    public void onSuccess() {

                        XmppService.this.xmppManager.getChatManager().addChatListener(XmppService.this);
                        XmppService.this.xmppManager.getFileTransferManager().addFileTransferListener(XmppService.this);

                        if (callback != null)
                            callback.onSuccess();
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        if (callback != null)
                            callback.onFailure(e);
                    }
                });
    }

    public void disconnect() {
        Log.d("XMPP", "Disconnecting XMPP...");
        this.xmppManager.disconnect();
        Log.d("XMPP", "Disconnected XMPP...");
    }

    public boolean isConnected() {
        return this.xmppManager.isConnected();
    }

    public void sendMessage(String to, Message message, SuccessOrFailureCallback callback) {
        try {
            this.xmppManager.sendMessage(to, message);

            if (callback != null) {
                callback.onSuccess();
            }
        } catch (SmackException.NotConnectedException e) {
            Log.e("XMPP", "Unable to send message : " + Log.getStackTraceString(e));
            if (callback != null) {
                callback.onFailure(e);
            }
        }
    }

    @Override
    public void connected(XMPPConnection connection) {
        Log.d("XMPP", "XMPP connected...");
        this.xmppEventManager.fireConnected();
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        Log.d("XMPP", "XMPP authenticated...");
        this.xmppEventManager.fireAuthenticated();
    }

    @Override
    public void connectionClosed() {
        Log.d("XMPP", "Connection closed...");
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

    @Override
    public void chatCreated(Chat chat, boolean createdLocally) {
        Log.d("XMPP", "Chat From : [" + chat.getParticipant() + "] Created [" + createdLocally + "].");
        chat.addMessageListener(this);
    }

    @Override
    public void processMessage(Chat chat, Message message) {
        Log.d("XMPP", "Message From : [" + message.getFrom() + "] Body : [" + message.getBody() +
                "] Stanza Id : [" + message.getStanzaId() + "].");

    }

    @Override
    public void fileTransferRequest(FileTransferRequest request) {
        Log.d("XMPP", "File From : [" + request.getRequestor() + "] Size : [" + request
                .getFileSize() + "] Mime Type :[" + request.getMimeType() + "].");
    }


    /**
     * Helper for Binder
     */
    public class XmppServiceBinder extends Binder {
        public XmppService getService() {
            return XmppService.this;
        }
    }

}
