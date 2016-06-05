package com.gnomesys.poc.xmppandroidchat;

import android.app.Application;
import android.content.Intent;

import com.gnomesys.poc.xmppandroidchat.component.xmpp.smack.SmackXmppService;

/**
 * Created by fendra on 5/6/16.
 */
public class XmppAndroidChatApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Start XMPP service
        Intent xmppIntent = new Intent(this.getApplicationContext(), SmackXmppService.class);
        xmppIntent.putExtra(SmackXmppService.EXTRA_HOST, "192.168.0.101");
        xmppIntent.putExtra(SmackXmppService.EXTRA_PORT, 5222);
        xmppIntent.putExtra(SmackXmppService.EXTRA_SERVICE_NAME, "im.gnomesys.com");
        this.startService(xmppIntent);
    }
}
