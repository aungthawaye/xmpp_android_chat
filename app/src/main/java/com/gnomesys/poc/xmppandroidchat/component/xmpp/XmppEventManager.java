package com.gnomesys.poc.xmppandroidchat.component.xmpp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.jivesoftware.smack.XMPPConnection;

/**
 * Created By
 * Author   : Aung Thaw Aye
 * Email    : ata.aungthawaye@gmail.com (aungthawaye@gnomesys.com)
 * Date     : 5/6/16
 */
public class XmppEventManager {

    public final static String ACTION_XMPP_EVENT = "XmppEventManager.XMPP_EVENT";
    public final static String EXTRA_EVENT_NAME = "XmppEventManager.EVENT_NAME";

    private Context context = null;

    public XmppEventManager(Context context) {
        this.context = context;
    }

    public void fireConnected() {
        Intent intent = new Intent(ACTION_XMPP_EVENT);
        intent.putExtra(EXTRA_EVENT_NAME, XmppEvent.CONNECTED);
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this.context);
        lbm.sendBroadcast(intent);
    }

    public void fireAuthenticated() {
        Intent intent = new Intent(ACTION_XMPP_EVENT);
        intent.putExtra(EXTRA_EVENT_NAME, XmppEvent.AUTHENTICATED);
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this.context);
        lbm.sendBroadcast(intent);
    }
}
