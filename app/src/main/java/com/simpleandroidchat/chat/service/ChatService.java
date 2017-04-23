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

package com.simpleandroidchat.chat.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;


import com.simpleandroidchat.chat.core.ChatEventBroadcaster;
import com.simpleandroidchat.chat.core.ChatEventListener;
import com.simpleandroidchat.chat.core.ChatEventProcessor;
import com.simpleandroidchat.chat.core.ConnectionEventBroadcaster;
import com.simpleandroidchat.chat.core.ConnectionManager;
import com.simpleandroidchat.chat.core.MessagingManager;
import com.simpleandroidchat.chat.core.ResultCallback;
import com.simpleandroidchat.chat.core.RosterManager;
import com.simpleandroidchat.chat.core.XmppConstants;
import com.simpleandroidchat.chat.util.UserInfoStorage;
import com.simpleandroidchat.component.util.Logger;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.chatstates.ChatState;

/**
 * This is the main class for chat messaging function. It is composed of connectionManager, messageManager and rosterManager.
 * ConnectionManager : it is resposible for XMPP connection establishment.
 * MessagingManager : it is responsible for chat messaging. It is composed of XMPP components from Smack API.
 * RosterManager : it is to check roster related information.
 */
public class ChatService extends Service {

    private final ChatServiceBinder binder = new ChatServiceBinder();

    private ConnectionListener connectionListener = new ConnectionEventBroadcaster(this);
    private ChatEventListener chatEventListener = new ChatEventProcessor(this, new ChatEventBroadcaster(this));

    private ConnectionManager connectionManager = null;
    private MessagingManager messagingManager = null;
    private RosterManager rosterManager = null;

    public ChatService() {
    }

    /**
     *
     */
    public void startMessaging(final ResultCallback resultCallback) {
        Logger.log("ChatService - startMessaging : Initializing XMPP service...");
        this.connectionManager = new ConnectionManager(this.connectionListener);
        /**
         * At first step, we will connect to XMPP server. Once the connection was established,
         * we will login to XMPP server using the credentials saved in SharedPreference.
         */
        connectionManager.connect(
                XmppConstants.XMPP_HOST,
                XmppConstants.XMPP_PORT,
                XmppConstants.XMPP_SERVICE_NAME, new ResultCallback() {
                    @Override
                    public void onSuccess() {
                        // After connected to XMPP server, we check whether we already have jabber credentials.
                        // If user has logged in previously, we will have credentials. Then we have to login.
                        Logger.log(
                                "ChatService - startMessaging : check whether Jabber credentials available or not...");
                        if (UserInfoStorage.getInstance()
                                .isCredentialsAvailable()) {
                            Logger.log("ChatService - startMessaging : credentials available ! do auto-login ...");
                            ChatService.this.login(
                                    UserInfoStorage.getInstance().getUserId(),
                                    UserInfoStorage.getInstance().getPassword(),
                                    resultCallback);
                        } else {
                            Logger.log(
                                    "ChatService - startMessaging : credentials NOT available ! cannot auto-login ...");
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        resultCallback.onFailure(e);
                    }
                });
    }

    /**
     *
     */
    public void login(final String jabberId, final String password, final ResultCallback resultCallback) {
        /**
         * Connection to XMPP server has been established successfully. (We hope).
         */
        Logger.log("ChatService - login : Logging In XMPP service...");
        final UserInfoStorage userInfoStorage =
                UserInfoStorage.getInstance();
        Logger.log("ChatService - login : jabberId : " + jabberId);
        Logger.log("ChatService - login : password : " + password);

        ChatService.this.connectionManager
                .login(jabberId, password, new ResultCallback() {

                    public void onSuccess() {
                        /**
                         * After logged in successfully, we will start messaging.
                         */
                        Logger.log(
                                "ChatService - login - onSuccess : Logged In XMPP service successfully...");
                        Logger.log(
                                "ChatService - login - onSuccess : Starting MessagingManager...");
                        ChatService.this.messagingManager = new MessagingManager(
                                ChatService.this.connectionManager.getXmppConnection(),
                                ChatService.this.chatEventListener);
                        ChatService.this.messagingManager.startMessaging();
                        Logger.log(
                                "ChatService - login - onSuccess : Done starting MessagingManager...");
                        ChatService.this.rosterManager = new RosterManager(
                                ChatService.this.connectionManager.getXmppConnection());

                        resultCallback.onSuccess();
                    }

                    public void onFailure(Exception e) {
                        Logger.log("ChatService - login - onFailure : " + Log.getStackTraceString(e));
                        resultCallback.onFailure(e);
                    }
                });
    }

    /**
     *
     */
    public void logout() {

        Logger.log("ChatService - logout : Logging out XMPP service...");

        if (UserInfoStorage.getInstance()
                .isCredentialsAvailable()) {
            UserInfoStorage.getInstance().cleanUpCredentials();
        }

        this.stopMessaging();
    }

    /**
     *
     */
    public void stopMessaging() {
        Logger.log("ChatService - stopMessaging : Stopping XMPP service...");
        if (UserInfoStorage.getInstance()
                .isCredentialsAvailable()) {
            try {
                this.rosterManager.notifyPresence(new Presence(Presence.Type.unavailable));
            } catch (SmackException.NotConnectedException e) {
                Logger.log("ChatService - stopMessaging : error : " + Log.getStackTraceString(e));
            }
        }

        this.connectionManager.disconnect();
        Logger.log("ChatService - stopMessaging : Stopped XMPP service...");
    }

    /**
     * To stop and terminate messaging. This method must be called when user logout.
     */
    public void terminateService() {
        this.stopMessaging();
        this.stopSelf();
    }

    /**
     * @param to
     * @param from
     * @param body
     * @throws SmackException.NotConnectedException
     */
    public void sendMessage(final String to, final String from, final String body)
            throws SmackException.NotConnectedException {
        this.messagingManager.sendMessage(to, from, body);
    }

    /**
     * @param receiverId
     * @param chatState
     * @throws SmackException.NotConnectedException
     */
    public void sendState(String receiverId, ChatState chatState) throws SmackException.NotConnectedException {
        this.messagingManager.sendState(receiverId, chatState);
    }

    /**
     *
     */
    public void notifyPresence(Presence presence) throws SmackException.NotConnectedException {
        this.rosterManager.notifyPresence(presence);
    }

    @Override
    public void onDestroy() {
        Logger.log("ChatService - onDestroy : Disconnecting XMPP service...");
        super.onDestroy();
        this.stopMessaging();
        Logger.log("ChatService - onDestroy : Disconnected XMPP service...");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Logger.log("ChatService - onTaskRemoved : Disconnecting XMPP service...");
        super.onTaskRemoved(rootIntent);
        this.stopMessaging();
        Logger.log("ChatService - onTaskRemoved : Disconnected XMPP service...");
    }


    /**
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Logger.log("ChatService - onStartCommand : starting service...");
        // We need to restart this service if Android kills it because of insufficient memory.
        this.startMessaging(new ResultCallback() {
            @Override
            public void onSuccess() {
                // Everything was fine
                Logger.log("ChatService - onStartCommand : everything was fine and logged in successfully.");
            }

            @Override
            public void onFailure(Exception e) {
                // Sometimes auto-login may be failed due to incorrect credentails or whatever reason.
                // Then we clean up UserInfoStorage. So, it wont try to do auto-login again.
                UserInfoStorage.getInstance().cleanUpCredentials();
            }
        });
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return this.binder;
    }


    public class ChatServiceBinder extends Binder {
        public ChatService getService() {
            return ChatService.this;
        }
    }
}
