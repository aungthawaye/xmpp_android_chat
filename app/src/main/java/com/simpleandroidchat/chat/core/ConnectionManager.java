/*
 * Copyright 2016 Chummies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.simpleandroidchat.chat.core;

import android.os.AsyncTask;
import android.util.Log;


import com.simpleandroidchat.BuildConfig;
import com.simpleandroidchat.component.util.Logger;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.sasl.provided.SASLDigestMD5Mechanism;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


/**
 * Created By
 * Author   : Aung Thaw Aye
 * Email    : ata.aungthawaye@gmail.com
 * Date     : 25/10/16
 */

public class ConnectionManager implements ConnectionListener {

    private AbstractXMPPConnection xmppConnection = null;
    private ConnectionListener connectionListener = null;

    public ConnectionManager(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

    /**
     * @return
     */
    public AbstractXMPPConnection getXmppConnection() {
        return this.xmppConnection;
    }

    /**
     * To connect to specified XMPP server.
     *
     * @param host
     * @param port
     * @param serviceName
     */
    public void connect(
            final String host,
            final int port,
            final String serviceName,
            final ResultCallback callback) {
        Logger.log("ConnectionManager - connect : Configuring XMPP connection settings...");
        Logger.log("ConnectionManager - connect : Host : " + host);
        Logger.log("ConnectionManager - connect : Port : " + port);
        Logger.log("ConnectionManager - connect : Service Name : " + serviceName);

        // Configuring XMPP Connection
        XMPPTCPConnectionConfiguration config;

        if (BuildConfig.DEBUG) {
            config = XMPPTCPConnectionConfiguration.builder()
                    .setServiceName(serviceName)
                    .setHost(host)
                    .setPort(port)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .setDebuggerEnabled(true) // We need DEBUGGING in development
                    .build();
        } else {
            config = XMPPTCPConnectionConfiguration.builder()
                    .setServiceName(serviceName)
                    .setHost(host)
                    .setPort(port)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .build();
        }

        SASLMechanism mechanism = new SASLDigestMD5Mechanism();
        SASLAuthentication.registerSASLMechanism(mechanism);
        SASLAuthentication.blacklistSASLMechanism("SCRAM-SHA-1");
        SASLAuthentication.unBlacklistSASLMechanism("DIGEST-MD5");


        XMPPTCPConnection.setUseStreamManagementDefault(true);
        this.xmppConnection = new XMPPTCPConnection(config);

        if (this.xmppConnection == null) {
            throw new IllegalStateException("Initialization hasn't been done yet.");
        }

        AsyncTask<Void, Integer, Boolean> asyncTask = new AsyncTask<Void, Integer, Boolean>() {

            private Exception exception = null;

            @Override
            protected Boolean doInBackground(Void... objects) {

                try {

                    ConnectionManager.this.xmppConnection
                            .addConnectionListener(ConnectionManager.this);
                    Logger.log("ConnectionManager - connect : Connecting to XMPP Server...");
                    ConnectionManager.this.xmppConnection.connect();
                    Logger.log("ConnectionManager - connect : Connected to XMPP Server...");


                } catch (XMPPException | IOException | SmackException e) {
                    Logger.log(Log.getStackTraceString(e));
                    this.exception = e;
                    return false;
                } catch (Exception e) {
                    Logger.log(Log.getStackTraceString(e));
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

        Logger.log("ConnectionManager - connect : Starting to connect XMPP Server...");
        asyncTask.execute();
    }

    /**
     *
     */
    public void disconnect() {

        Logger.log("ConnectionManager - disconnect : Disconnecting from XMPP server...");
        AsyncTask<Void, Integer, Boolean> asyncTask = new AsyncTask<Void, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... objects) {
                ConnectionManager.this.xmppConnection.disconnect();
                Logger.log("ConnectionManager - disconnect : Disconnected from XMPP Server...");
                return true;
            }

            @Override
            protected void onPostExecute(Boolean success) {

            }
        };

        try {
            asyncTask.execute().get();
        } catch (InterruptedException | ExecutionException e) {
            Logger.log("ConnectionManager - disconnect : Error : " + Log.getStackTraceString(e));
        }
    }

    /**
     * @param jabberId
     * @param password
     * @param callback
     */
    public void login(final String jabberId, final String password, final ResultCallback callback) {

        AsyncTask<Void, Integer, Boolean> asyncTask = new AsyncTask<Void, Integer, Boolean>() {

            private Exception exception;

            @Override
            protected Boolean doInBackground(Void... voids) {

                try {

                    if (ConnectionManager.this.xmppConnection == null ||
                            ConnectionManager.this.xmppConnection.isConnected() == false) {
                        throw new IllegalStateException("Either of initialization or connection hasn't been done yet.");
                    }

                    Logger.log("ConnectionManager - login : Logging in XMPP Server...");
                    ConnectionManager.this.xmppConnection
                            .login(jabberId, password, XmppConstants.JABBER_RESOURCE_ID);
                    Logger.log("ConnectionManager - login : Logged in XMPP Server...");

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
     * @return
     */
    public boolean isConnectionReady() {
        if (this.xmppConnection == null || this.xmppConnection.isConnected() == false)
            return false;
        return true;
    }

    @Override
    public void connected(XMPPConnection connection) {
        this.connectionListener.connected(connection);
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        this.connectionListener.authenticated(connection, resumed);
    }

    @Override
    public void connectionClosed() {
        this.connectionListener.connectionClosed();
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        this.connectionListener.connectionClosedOnError(e);
    }

    @Override
    public void reconnectionSuccessful() {
        this.connectionListener.reconnectionSuccessful();
    }

    @Override
    public void reconnectingIn(int seconds) {
        this.connectionListener.reconnectingIn(seconds);
    }

    @Override
    public void reconnectionFailed(Exception e) {
        this.connectionListener.reconnectionFailed(e);
    }



}
