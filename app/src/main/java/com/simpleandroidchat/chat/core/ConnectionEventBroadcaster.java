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

import android.content.Context;
import android.content.Intent;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;


/**
 * Created By
 * Author   : Aung Thaw Aye
 * Email    : ata.aungthawaye@gmail.com
 * Date     : 26/10/16
 */

public class ConnectionEventBroadcaster implements ConnectionListener {

    public static final String ACTION_CONNECTION_INFO =
            "com.chummiesapp.ConnectionEventBroadcaster.ACTION_CONNECTION_INFO";
    public static final String EXTRA_CONNECTION_INFO =
            "com.chummiesapp.ConnectionEventBroadcaster.EXTRA_CONNECTION_INFO";

    private Context context = null;

    public ConnectionEventBroadcaster(Context context) {
        this.context = context;
    }

    @Override
    public void connected(XMPPConnection connection) {
        Intent intent = new Intent(ACTION_CONNECTION_INFO);
        intent.putExtra(EXTRA_CONNECTION_INFO, ConnectionStatus.CONNECTED);
        this.context.sendBroadcast(intent);
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        Intent intent = new Intent(ACTION_CONNECTION_INFO);
        intent.putExtra(EXTRA_CONNECTION_INFO, ConnectionStatus.AUTHENTICATED);
        this.context.sendBroadcast(intent);
    }

    @Override
    public void connectionClosed() {
        Intent intent = new Intent(ACTION_CONNECTION_INFO);
        intent.putExtra(EXTRA_CONNECTION_INFO, ConnectionStatus.CONNECION_CLOSED_ON_ERROR);
        this.context.sendBroadcast(intent);
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        Intent intent = new Intent(ACTION_CONNECTION_INFO);
        intent.putExtra(EXTRA_CONNECTION_INFO, ConnectionStatus.CONNECTION_ERROR);
        this.context.sendBroadcast(intent);
    }

    @Override
    public void reconnectionSuccessful() {
        Intent intent = new Intent(ACTION_CONNECTION_INFO);
        intent.putExtra(EXTRA_CONNECTION_INFO, ConnectionStatus.RECONNECTED);
        this.context.sendBroadcast(intent);
    }

    @Override
    public void reconnectingIn(int seconds) {
        Intent intent = new Intent(ACTION_CONNECTION_INFO);
        intent.putExtra(EXTRA_CONNECTION_INFO, ConnectionStatus.RECONNECTING);
        this.context.sendBroadcast(intent);
    }

    @Override
    public void reconnectionFailed(Exception e) {
        Intent intent = new Intent(ACTION_CONNECTION_INFO);
        intent.putExtra(EXTRA_CONNECTION_INFO, ConnectionStatus.RECONNECTION_FAILED);
        this.context.sendBroadcast(intent);
    }
}
