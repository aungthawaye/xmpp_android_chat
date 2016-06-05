package com.gnomesys.poc.xmppandroidchat.component.xmpp.smack;

import android.os.AsyncTask;
import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;

/**
 * Created By
 * Author   : Aung Thaw Aye
 * Email    : ata.aungthawaye@gmail.com (aungthawaye@gnomesys.com)
 * Date     : 5/6/16
 */
public final class SmackXmppManager {

    private AbstractXMPPConnection xmppConnection = null;
    private ChatManager chatManager = null;
    private ConnectionListener connectionListener = null;

    /**
     * To connect to XMPP server using Smack API.
     *
     * @param host        : XMPP server
     * @param port        : XMPP server port
     * @param serviceName : service name of your XMPP server.
     */
    public void initialize(final String host,
                           final int port,
                           final String serviceName) {

        // Configuring XMPP Connection
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setServiceName(serviceName)
                .setHost(host)
                .setPort(port)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .setDebuggerEnabled(true)
                .build();

        Log.d("XMPP", "Configuring XMPP connection settings...");
        Log.d("XMPP", "Host : " + host);
        Log.d("XMPP", "Port : " + port);
        Log.d("XMPP", "Service Name : " + serviceName);

        XMPPTCPConnection.setUseStreamManagementDefault(true);
        this.xmppConnection = new XMPPTCPConnection(config);
    }

    /**
     * To connect to XMPP Server
     *
     * @param callback : Callback after successful or failed connection.
     */
    public void connect(final ConnectionListener connectionListener, final SuccessOrFailureCallback callback) {

        if (this.xmppConnection == null) {
            throw new IllegalStateException("Initialization hasn't been done yet.");
        }

        this.connectionListener = connectionListener;

        AsyncTask<Void, Integer, Boolean> asyncTask = new AsyncTask<Void, Integer, Boolean>() {

            private Throwable exception = null;

            @Override
            protected Boolean doInBackground(Void... objects) {

                try {

                    Log.d("XMPP", "Connecting to XMPP Server...");
                    SmackXmppManager.this.xmppConnection.connect();
                    Log.d("XMPP", "Connected to XMPP Server...");

                    SmackXmppManager.this.xmppConnection
                            .addConnectionListener(SmackXmppManager.this.connectionListener);
                } catch (XMPPException | IOException | SmackException e) {
                    Log.e("XMPP", Log.getStackTraceString(e));
                    this.exception = e;
                    return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (callback != null) {
                    if (success) {

                        callback.onSuccess();
                    } else {
                        callback.onFailure(this.exception);
                    }
                }
            }
        };

        Log.d("XMPP", "Starting to connect XMPP Server...");
        asyncTask.execute();

    }

    /**
     * To login XMPP server
     *
     * @param username     : XMPP server username
     * @param password     : password for your username
     * @param resourceName : unique ID for yourself
     * @param callback     : Success or failure callback
     */
    public void login(final String username,
                      final String password,
                      final String resourceName,
                      final SuccessOrFailureCallback callback) {
        AsyncTask<Void, Integer, Boolean> asyncTask = new AsyncTask<Void, Integer, Boolean>() {

            private Throwable exception;

            @Override
            protected Boolean doInBackground(Void... voids) {

                try {

                    if (SmackXmppManager.this.xmppConnection == null ||
                            SmackXmppManager.this.xmppConnection.isConnected() == false) {
                        throw new IllegalStateException("Either of initialization or connection hasn't been done yet.");
                    }

                    Log.d("XMPP", "Logging in XMPP Server...");
                    SmackXmppManager.this.xmppConnection.login(username, password, resourceName);
                    Log.d("XMPP", "Logged in XMPP Server...");

                    SmackXmppManager.this.chatManager =
                            ChatManager.getInstanceFor(SmackXmppManager.this.xmppConnection);

                } catch (XMPPException | IOException | SmackException e) {
                    Log.e("XMPP", Log.getStackTraceString(e));
                    this.exception = e;
                    return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (callback != null) {
                    if (success) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure(this.exception);
                    }
                }
            }
        };

        Log.d("XMPP", "Starting to login XMPP Server...");
        asyncTask.execute();
    }

    /**
     * To disconnect from XMPP server. It will also logout you.
     */
    public void disconnect() {
        AsyncTask<Void, Integer, Boolean> asyncTask = new AsyncTask<Void, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                if (SmackXmppManager.this.xmppConnection != null) {
                    if (SmackXmppManager.this.connectionListener != null)
                        SmackXmppManager.this.xmppConnection
                                .removeConnectionListener(SmackXmppManager.this.connectionListener);
                    SmackXmppManager.this.xmppConnection.disconnect();
                    return true;
                }

                return false;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                // Do nothing...
            }
        };
        asyncTask.execute();
    }

    public boolean isConnected(){
        if(this.xmppConnection != null)
            return this.xmppConnection.isConnected();
        return false;
    }

    // Interface for callbacks
    public interface SuccessOrFailureCallback {

        void onSuccess();

        void onFailure(Throwable e);
    }

}
