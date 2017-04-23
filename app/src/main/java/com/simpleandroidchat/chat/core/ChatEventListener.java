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

import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smackx.chatstates.ChatStateListener;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;

/**
 * Created By
 * Author   : Aung Thaw Aye
 * Email    : ata.aungthawaye@gmail.com
 * Date     : 26/10/16
 */

public interface ChatEventListener extends ChatManagerListener, ChatMessageListener, ChatStateListener
        , ReceiptReceivedListener {
    // Consolidated interface for all these methods
}
