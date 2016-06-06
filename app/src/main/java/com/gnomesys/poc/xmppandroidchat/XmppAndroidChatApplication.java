package com.gnomesys.poc.xmppandroidchat;

import android.app.Application;
import android.content.Intent;

import com.gnomesys.poc.xmppandroidchat.component.xmpp.XmppService;

/**
 * Created by fendra on 5/6/16.
 */
public class XmppAndroidChatApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Start XMPP service
        Intent xmppIntent = new Intent(this.getApplicationContext(), XmppService.class);
        xmppIntent.putExtra(XmppService.EXTRA_HOST, "192.168.0.101");
        xmppIntent.putExtra(XmppService.EXTRA_PORT, 5222);
        xmppIntent.putExtra(XmppService.EXTRA_SERVICE_NAME, "im.gnomesys.com");
        this.startService(xmppIntent);
    }
}
