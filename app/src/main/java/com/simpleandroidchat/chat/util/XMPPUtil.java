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

package com.simpleandroidchat.chat.util;



import com.simpleandroidchat.chat.core.XmppConstants;
import com.simpleandroidchat.component.util.Logger;

import java.util.UUID;

/**
 * Created By
 * Author   : Aung Thaw Aye
 * Email    : ata.aungthawaye@gmail.com
 * Date     : 27/10/16
 */


public class XMPPUtil {

    /**
     * It's to build the proper XMPP Id using normal/plain XMPP ID. Proper XMPP ID means
     * <jabberId>@<service.name>/<resourceId>
     * e.g david@im.hello.com/xmppchat
     * In case plainID is already form of proper XMPP ID, it wont build.
     *
     * @param plainId
     * @return
     */
    public static final String fixJabberId(String plainId) {
        Logger.log("XMPPUtil - build : plainId (before) : " + plainId);
        final String JABBER_SUFFIX = "@" + XmppConstants.XMPP_SERVICE_NAME + "/" + XmppConstants.JABBER_RESOURCE_ID;
        plainId = !plainId.endsWith(JABBER_SUFFIX) ? plainId + JABBER_SUFFIX : plainId;
        Logger.log("XMPPUtil - build : plainId (after) : " + plainId);
        return plainId;
    }

    /**
     * @param senderId
     * @return
     */
    public static final String generateStanzaId(String senderId) {
        return new StringBuffer()
                .append(senderId.substring(0, senderId.indexOf("@")))
                .append(":")
                .append(UUID.randomUUID().toString())
                .toString();
    }
}
