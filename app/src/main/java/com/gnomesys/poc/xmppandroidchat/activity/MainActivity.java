package com.gnomesys.poc.xmppandroidchat.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.gnomesys.poc.xmppandroidchat.R;
import com.gnomesys.poc.xmppandroidchat.component.xmpp.SuccessOrFailureCallback;
import com.gnomesys.poc.xmppandroidchat.component.xmpp.XmppService;

public class MainActivity extends AppCompatActivity {

    private XmppService xmppService = null;
    private ServiceConnection xmppServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("XMPP", "Xmpp service connected...");
            XmppService.XmppServiceBinder binder = (XmppService.XmppServiceBinder) iBinder;
            MainActivity.this.xmppService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("XMPP", "Xmpp service disconnected...");
            MainActivity.this.xmppService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnLogin = (Button) this.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.this.xmppService != null) {
                    MainActivity.this.xmppService.login("aungthawaye", "password", "aungthawaye",
                            new SuccessOrFailureCallback() {
                                @Override
                                public void onSuccess() {
                                    Log.d("XAC", "Login successful...");
                                }

                                @Override
                                public void onFailure(Throwable e) {
                                    Log.d("XAC", "Login failed : " + Log.getStackTraceString(e));
                                }
                            });
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (this.xmppService != null) {
            this.unbindService(this.xmppServiceConnection);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, XmppService.class);
        this.bindService(intent, this.xmppServiceConnection, BIND_AUTO_CREATE);
    }

    private void login() {

    }
}
