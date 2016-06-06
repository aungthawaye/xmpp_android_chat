package com.gnomesys.poc.xmppandroidchat.component.xmpp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created By
 * Author   : Aung Thaw Aye
 * Email    : ata.aungthawaye@gmail.com (aungthawaye@gnomesys.com)
 * Date     : 5/6/16
 */
public class XmppBroadcastReceiver extends BroadcastReceiver {

    private Context context;
    private XmppEvent handler;

    public XmppBroadcastReceiver(Context context, XmppEvent handler) {
        this.context = context;
        this.handler = handler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String event = intent.getStringExtra(XmppEventManager.EXTRA_EVENT_NAME);

        switch (event) {
            case XmppEvent.CONNECTED:
                this.handler.onConnected();
                break;
        }
    }

}
