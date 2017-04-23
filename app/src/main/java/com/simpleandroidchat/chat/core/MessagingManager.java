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


import com.simpleandroidchat.chat.util.XMPPUtil;
import com.simpleandroidchat.component.util.Logger;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created By
 * Author   : Aung Thaw Aye
 * Email    : ata.aungthawaye@gmail.com
 * Date     : 26/10/16
 */

public class MessagingManager {

    private AbstractXMPPConnection xmppConnection = null;
    private ChatManager chatManager = null;
    private DeliveryReceiptManager deliveryReceiptManager = null;
    private ChatStateManager chatStateManager = null;
    private ChatEventListener chatEventListener = null;


    // chatRepo keeps all the chat instances which are created by sendMessage method.
    // Key is the jabber Id of the receiver.
    private Map<String, Chat> chatRepo = new HashMap<>();

    public MessagingManager(AbstractXMPPConnection xmppConnection, ChatEventListener chatEventListener) {
        this.xmppConnection = xmppConnection;
        this.chatEventListener = chatEventListener;
    }

    /**
     * To start XMPP messaging. Once started this XMPP Messaging Manager will handle incoming chat,
     * chat state changes and message delivery status.
     */
    public void startMessaging() {
        this.chatManager = ChatManager.getInstanceFor(this.xmppConnection);
        this.chatManager.setMatchMode(ChatManager.MatchMode.SUPPLIED_JID);
        this.chatManager.addChatListener(this.chatEventListener);

        this.deliveryReceiptManager = DeliveryReceiptManager.getInstanceFor(this.xmppConnection);
        this.deliveryReceiptManager.autoAddDeliveryReceiptRequests();
        this.deliveryReceiptManager.addReceiptReceivedListener(this.chatEventListener);

        this.chatStateManager = ChatStateManager.getInstance(this.xmppConnection);

    }

    /**
     *
     * @param to
     * @param from
     * @param body
     * @throws SmackException.NotConnectedException
     */
    public void sendMessage(final String to, final String from, final String body) throws SmackException.NotConnectedException {

        ChatMessage chatMessage = new ChatMessage();
        // We make sure jabber IDs are in correct format.
        chatMessage.senderId = XMPPUtil.fixJabberId(from);
        chatMessage.receiverId = XMPPUtil.fixJabberId(to);
        chatMessage.stanzaId = XMPPUtil.generateStanzaId(chatMessage.senderId);
        chatMessage.body = body;
        chatMessage.createdLocalTime = new Date();

        Chat chat = this.buildChatIfRequired(chatMessage.receiverId);

        // Create XMPP message and send
        Message message = new Message();
        message.setBody(chatMessage.body);
        message.setType(Message.Type.chat);
        message.setFrom(chatMessage.senderId);
        message.setTo(chatMessage.receiverId);
        message.setStanzaId(chatMessage.stanzaId);

        // We need delivery status notification
        chatMessage.deliveryReceiptId = DeliveryReceiptRequest.addTo(message);

        Logger.log(
                "MessagingManager - sendMessage : chatMessage.deliveryReceiptId : " + chatMessage.deliveryReceiptId);

        // If you want, you can save your Chat messages in your local storage here.

        Logger.log("MessagingManager - sendMessage : sending chat message...");
        chat.sendMessage(message);

        Logger.log("MessagingManager - sendMessage : ++++++++++++++++++++++++++++++++++++++");
        Logger.log("MessagingManager - sendMessage : message.from : " + message.getFrom());
        Logger.log("MessagingManager - sendMessage : message.to : " + message.getTo());
        Logger.log("MessagingManager - sendMessage : message.threadId : " + message.getThread());
        Logger.log("MessagingManager - sendMessage : message.generateStanzaId : " + message.getStanzaId());
        Logger.log("MessagingManager - sendMessage : message.body : " + message.getBody());
        Logger.log("MessagingManager - sendMessage : message.type : " + message.getType());
        Logger.log("MessagingManager - sendMessage : done sending message...");
        Logger.log("\n");
    }

    /**
     * @param receiverId
     * @param chatState
     */
    public void sendState(String receiverId, ChatState chatState) throws SmackException.NotConnectedException {
        Chat chat = this.buildChatIfRequired(receiverId);
        this.chatStateManager.setCurrentState(chatState, chat);
    }

    /**
     * It will build Chat instance based on receiver id if the chat doesn't exist in Chat repo.
     *
     * @param receiverId
     * @return
     */
    private Chat buildChatIfRequired(String receiverId) {
        Chat chat = this.chatRepo.get(receiverId);

        if (chat == null) {
            chat = this.chatManager.createChat(receiverId, this.chatEventListener);
            this.chatRepo.put(receiverId, chat);
        }

        return chat;
    }
}
