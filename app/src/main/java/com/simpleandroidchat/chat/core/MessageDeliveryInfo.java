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

import java.io.Serializable;

/**
 * Created By
 * Author   : Aung Thaw Aye
 * Email    : ata.aungthawaye@gmail.com
 * Date     : 29/10/16
 */

public class MessageDeliveryInfo implements Serializable {
    // It could be the recipient's Jabber ID
    public String participantId;
    // The receipt ID (stanza ID) which is generated while sending message.
    public String receiptId;
}
