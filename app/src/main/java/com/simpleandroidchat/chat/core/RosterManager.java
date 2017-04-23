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

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;

/**
 * Created By
 * Author   : Aung Thaw Aye
 * Email    : ata.aungthawaye@gmail.com
 * Date     : 27/10/16
 */

public class RosterManager {

    private AbstractXMPPConnection xmppConnection;
    private Roster roster;

    public RosterManager(AbstractXMPPConnection xmppConnection) {
        this.xmppConnection = xmppConnection;
    }

    public void initialize() {
        this.roster = Roster.getInstanceFor(this.xmppConnection);
    }

    /**
     * @param jabberId
     * @return
     */
    public Presence checkPresence(String jabberId) {
        final String fixedJabberId = XMPPUtil.fixJabberId(jabberId);
        return this.roster.getPresence(fixedJabberId);
    }

    /**
     * @param presence
     */
    public void notifyPresence(Presence presence) throws SmackException.NotConnectedException {
        this.xmppConnection.sendStanza(presence);
    }
}
