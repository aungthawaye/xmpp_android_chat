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


import com.simpleandroidchat.chat.util.UserInfoStorage;
import com.simpleandroidchat.chat.util.XMPPUtil;
import com.simpleandroidchat.component.util.Logger;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.chatstates.ChatState;

import java.util.Date;
/**
 * This class will process all the chat messages, chat state and other chat related information.
 */

/**
 * Created By
 * Author   : Aung Thaw Aye
 * Email    : ata.aungthawaye@gmail.com
 * Date     : 27/10/16
 */

public class ChatEventProcessor implements ChatEventListener {

    private UserInfoStorage userInfoStorage = null;
    private Context context = null;
    private ChatEventBroadcaster chatEventBroadcaster = null;

    public ChatEventProcessor(Context context, ChatEventBroadcaster chatEventBroadcaster) {
        this.context = context;
        this.chatEventBroadcaster = chatEventBroadcaster;
        this.userInfoStorage = UserInfoStorage.getInstance();
    }

    @Override
    public void chatCreated(Chat chat, boolean createdLocally) {
        Logger.log("ChatEventProcessor - chatCreated : " + chat + " locally : " + createdLocally);

        if (createdLocally == false) {

            //TODO:
            // check participant's info already exist in Realm DB or not.
            // If not exist, grab participant's user info from backend system and keep it in DB.

            chat.addMessageListener(this);

            // We have to check this chat is already exist in current chat box list.
            // If exist, we skip. Else, we will save this chat in chatbox.
            final String participantId = XMPPUtil.fixJabberId(chat.getParticipant());

        }
    }

    @Override
    public void stateChanged(Chat chat, ChatState state) {
        Logger.log("ChatEventProcessor - stateChanged : ++++++++++++++++++++++++++++++++++++");
        Logger.log("ChatEventProcessor - stateChanged : chat.participant : " + chat.getParticipant());
        Logger.log("ChatEventProcessor - stateChanged : state.name : " + state.name());
        Logger.log("ChatEventProcessor - stateChanged : ++++++++++++++++++++++++++++++++++++");

        ParticipantChatState participantChatState = new ParticipantChatState();
        participantChatState.chatState = state;
        participantChatState.participantId = chat.getParticipant();

        this.chatEventBroadcaster.broadcastStateChanged(participantChatState);
    }

    /**
     * Whenever there is incoming chat, we will inform to receiver through broadcast.
     *
     * @param chat
     * @param message
     */
    @Override
    public void processMessage(Chat chat, Message message) {
        Logger.log("ChatEventProcessor - processMessage : ++++++++++++++++++++++++++++++++++++");
        Logger.log("ChatEventProcessor - processMessage : message.generateStanzaId : " + message.getStanzaId());
        Logger.log("ChatEventProcessor - processMessage : message.from : " + message.getFrom());
        Logger.log("ChatEventProcessor - processMessage : message.to : " + message.getTo());
        Logger.log("ChatEventProcessor - processMessage : message.body : " + message.getBody());
        Logger.log("ChatEventProcessor - processMessage : ++++++++++++++++++++++++++++++++++++");

        if (message.getBody() == null) {
            // We dont care the message whose body is blank.
            return;
        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.senderId = XMPPUtil.fixJabberId(message.getFrom());
        chatMessage.receiverId = XMPPUtil.fixJabberId(message.getTo());
        chatMessage.stanzaId = message.getStanzaId();
        chatMessage.body = message.getBody();
        chatMessage.createdLocalTime = new Date();
        chatMessage.deliveryReceiptId = "INCOMING";

        Logger.log("ChatEventProcessor - processMessage : broadcast incoming message...");
        this.chatEventBroadcaster.broadcastIncomingMessage(chatMessage);
    }

    /**
     * When we received this event, we update the status of the message we sent as 'delivered' using the receiptId.
     *
     * @param fromJid
     * @param toJid
     * @param receiptId
     * @param receipt
     */
    @Override
    public void onReceiptReceived(String fromJid, String toJid, String receiptId, Stanza receipt) {
        Logger.log(
                "ChatEventProcessor - onReceiptReceived : fromJid : " + fromJid + " , toJid : " + toJid +
                        " , receiptId : " + receiptId + " , stanza : " + receipt);

        Logger.log(
                "ChatEventProcessor - onReceiptReceived : update message status to DELIVERED : stanzaId : " + receiptId);

        MessageDeliveryInfo messageDeliveryInfo = new MessageDeliveryInfo();
        messageDeliveryInfo.participantId = fromJid;
        messageDeliveryInfo.receiptId = receiptId;

        Logger.log("ChatEventProcessor - processMessage : broadcast message delivery info...");
        this.chatEventBroadcaster.broadcastMessageDelivered(messageDeliveryInfo);

    }
}
