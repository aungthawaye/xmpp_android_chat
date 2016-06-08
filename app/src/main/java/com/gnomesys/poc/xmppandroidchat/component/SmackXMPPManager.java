package com.gnomesys.poc.xmppandroidchat.component;

import android.os.AsyncTask;
import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateListener;
import org.jivesoftware.smackx.chatstates.ChatStateManager;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created By
 * Author   : Aung Thaw Aye
 * Email    : ata.aungthawaye@gmail.com (aungthawaye@gnomesys.com)
 * Date     : 5/6/16
 */
public final class SmackXMPPManager
        implements ChatManagerListener, ChatMessageListener, ChatStateListener, ConnectionListener,
        ReceiptReceivedListener, FileTransferListener {

    private final static SmackXMPPManager INSTANCE = new SmackXMPPManager();

    private AbstractXMPPConnection xmppConnection;
    private ChatEventListener delegate;
    private ChatManager chatManager;
    private ChatStateManager chatStateManager;
    private DeliveryReceiptManager deliveryReceiptManager;
    private FileTransferManager fileTransferManager;

    // To keep the list of Chat instances.
    private Map<String, Chat> chatBoxes = new HashMap<>();

    private SmackXMPPManager() {

    }

    public final static SmackXMPPManager getInstance() {
        return INSTANCE;
    }

    /**
     * The delegator which will actually handle these Smack's XMPP related events.
     *
     * @param delegate
     */
    public void setListenerDelegate(ChatEventListener delegate) {
        this.delegate = delegate;
    }

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
     */
    public void connect() {

        if (this.xmppConnection == null) {
            throw new IllegalStateException("Initialization hasn't been done yet.");
        }

        AsyncTask<Void, Integer, Boolean> asyncTask = new AsyncTask<Void, Integer, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... objects) {

                try {

                    SmackXMPPManager.this.xmppConnection
                            .addConnectionListener(SmackXMPPManager.this);

                    Log.d("XMPP", "Connecting to XMPP Server...");
                    SmackXMPPManager.this.xmppConnection.connect();
                    Log.d("XMPP", "Connected to XMPP Server...");

                } catch (XMPPException | IOException | SmackException e) {
                    Log.e("XMPP", Log.getStackTraceString(e));
                    return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean success) {

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
     */
    public void login(final String username,
                      final String password,
                      final String resourceName,
                      final ResultCallback callback) throws ServiceUnavailableException {

        if (this.xmppConnection == null || !this.xmppConnection.isConnected())
            throw new ServiceUnavailableException();

        AsyncTask<Void, Integer, Boolean> asyncTask = new AsyncTask<Void, Integer, Boolean>() {

            private Throwable exception;

            @Override
            protected Boolean doInBackground(Void... voids) {

                try {

                    if (SmackXMPPManager.this.xmppConnection == null ||
                            SmackXMPPManager.this.xmppConnection.isConnected() == false) {
                        throw new IllegalStateException("Either of initialization or connection hasn't been done yet.");
                    }

                    Log.d("XMPP", "Logging in XMPP Server...");
                    SmackXMPPManager.this.xmppConnection.login(username, password, resourceName);
                    Log.d("XMPP", "Logged in XMPP Server...");

                    Log.d("XMPP", "Getting SmackXMPPManager...");
                    SmackXMPPManager.this.chatManager =
                            org.jivesoftware.smack.chat.ChatManager
                                    .getInstanceFor(SmackXMPPManager.this.xmppConnection);
                    SmackXMPPManager.this.chatManager.addChatListener(SmackXMPPManager.this);

                    Log.d("XMPP", "Getting FileTransferManager...");
                    SmackXMPPManager.this.fileTransferManager =
                            FileTransferManager.getInstanceFor(SmackXMPPManager.this.xmppConnection);
                    SmackXMPPManager.this.fileTransferManager.addFileTransferListener(SmackXMPPManager.this);

                    Log.d("XMPP", "Getting ChatStateManager...");
                    SmackXMPPManager.this.chatStateManager =
                            ChatStateManager.getInstance(SmackXMPPManager.this.xmppConnection);

                    Log.d("XMPP", "Getting DeliveryReceiptManager...");
                    SmackXMPPManager.this.deliveryReceiptManager =
                            DeliveryReceiptManager.getInstanceFor(SmackXMPPManager.this.xmppConnection);
                    // We enable auto delivery receipt requests. So, we will know whether the recipient
                    // get our message or not.
                    SmackXMPPManager.this.deliveryReceiptManager.autoAddDeliveryReceiptRequests();
                    SmackXMPPManager.this.deliveryReceiptManager.addReceiptReceivedListener(SmackXMPPManager.this);

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
                try {
                    Log.d("XMPP", "Disconnecting XMPP...");
                    SmackXMPPManager.this.xmppConnection.disconnect(new Presence(Presence.Type.unavailable));
                    Log.d("XMPP", "Disconnected XMPP...");
                } catch (SmackException.NotConnectedException e) {
                    Log.e("XMPP", "XMPP not connected : " + Log.getStackTraceString(e));
                    return false;
                }
                return true;
            }

        };
        asyncTask.execute();
    }

    public boolean isConnected() {
        if (this.xmppConnection != null)
            return this.xmppConnection.isConnected();
        return false;
    }

    /**
     * To send chat message to recipient.
     *
     * @param to      : the recipient's JabberID (e.g yourname@yourservicename.com)
     * @param message : the chat message
     * @return : Delivery receipt Id
     */
    public String sendMessage(String to, Message message)
            throws SmackException.NotConnectedException, ServiceUnavailableException {

        if (this.xmppConnection == null || !this.xmppConnection
                .isConnected() || this.chatManager == null || !this.xmppConnection.isAuthenticated())
            throw new ServiceUnavailableException();

        // Check whether the recipient's chat instance is already exist in chatBoxes map.
        if (!this.chatBoxes.containsKey(to)) {
            Log.d("XMPP", "New chat for this recipient : [" + to + "].");
            // We create chat and listen messaging on this chat.
            this.chatBoxes.put(to, this.chatManager.createChat(to, this));
        } else {
            Log.d("XMPP", "Existing chat for this recipient : [" + to + "].");
        }


        Chat chat = this.chatBoxes.get(to);
        String deliverReceiptId = DeliveryReceiptRequest.addTo(message);
        chat.sendMessage(message);
        Log.d("XMPP",
                "sendMessage : from : [" + message.getFrom() + "] to : [" + message.getTo() + "] stanzaId : [" + message
                        .getStanzaId() + "] body : [" + message.getBody() + "].");
        return deliverReceiptId;
    }

    /**
     * To send chat state to recipient. You can use this to inform the recipient that you are typing
     * when you click on chat textbox. (Just like in Facebook, WhatsApp, Telegram...)
     *
     * @param to
     * @param state
     */
    public void sendState(String to, ChatState state) {
        // TODO: Implementation
    }

    /**
     * To read all the offline messages sent to you while you were offline.
     *
     * @return
     */
    public List<Message> readOfflineMessages() {
        // TODO: Implementation
        return null;
    }

    @Override
    public void chatCreated(Chat chat, boolean createdLocally) {

        Log.d("XMPP", "chatCreated : participant : [" + chat.getParticipant() + "].");
        if (!createdLocally) {
            // It's incoming chat. Then we need to listen its messaging.
            chat.addMessageListener(this);
        }
    }

    @Override
    public void stateChanged(Chat chat, ChatState state) {
        this.delegate.onStateChanged(state);
    }

    @Override
    public void processMessage(Chat chat, Message message) {

        Log.d("XMPP",
                "processMessage : from : [" + message.getFrom() + "] to : [" + message.getTo() + "] body : [" + message
                        .getBody() + "]. stanzaId : [" + message.getStanzaId() + "]. XML [" + message.toXML()
                        .toString() + "].");
        // Check whether the message got extension
        if (message.getBody() == null || message.getBody().isEmpty()) {
            // If there is no message body, then it could be message for chat states.
            for (ExtensionElement ee : message.getExtensions()) {
                if (ee instanceof ChatStateExtension) {
                    // Do something.
                    ChatStateExtension state = (ChatStateExtension) ee;
                    this.delegate.onStateChanged(state.getChatState());
                }
            }
        } else {

        }
    }

    @Override
    public void connected(XMPPConnection connection) {
        this.delegate.onConnected(connection.isConnected());
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        // Do nothing...
    }

    @Override
    public void connectionClosed() {
        this.delegate.onConnectionClosed();
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        this.delegate.onConnectionClosedOnError(e);
    }

    @Override
    public void reconnectionSuccessful() {
        this.delegate.onReconnectionSuccessful();
    }

    @Override
    public void reconnectingIn(int seconds) {
        this.delegate.onReconnectingIn(seconds);
    }

    @Override
    public void reconnectionFailed(Exception e) {
        this.delegate.onReconnectionFailed(e);
    }

    @Override
    public void onReceiptReceived(String fromJid, String toJid, String receiptId, Stanza receipt) {
        this.delegate.onReceiptReceived(fromJid, toJid, receiptId, receipt);
    }

    @Override
    public void fileTransferRequest(FileTransferRequest request) {

    }

    public class ServiceUnavailableException extends Exception {
        public ServiceUnavailableException() {
            super("Xmpp service is not available.. Make sure it has connected.");
        }
    }
}
