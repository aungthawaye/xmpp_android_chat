package com.gnomesys.poc.xmppandroidchat.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gnomesys.poc.xmppandroidchat.R;
import com.gnomesys.poc.xmppandroidchat.component.SmackXMPPManager;
import com.gnomesys.poc.xmppandroidchat.component.ResultCallback;
import com.gnomesys.poc.xmppandroidchat.service.XMPPService;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private XMPPService xmppService = null;
    private ServiceConnection xmppServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("XMPP", "Android Xmpp service connected...");
            XMPPService.XmppServiceBinder binder = (XMPPService.XmppServiceBinder) iBinder;
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

        final EditText txtUsername = (EditText) this.findViewById(R.id.txtUsername);
        assert txtUsername != null;

        final EditText txtPassword = (EditText) this.findViewById(R.id.txtPassword);
        assert txtPassword != null;

        Button btnLogin = (Button) this.findViewById(R.id.btnLogin);
        assert btnLogin != null;
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.this.xmppService != null) {
                    try {
                        MainActivity.this.xmppService
                                .login(txtUsername.getText().toString(), txtPassword.getText().toString(),
                                        txtUsername.getText().toString(),
                                        new ResultCallback() {
                                            @Override
                                            public void onSuccess() {
                                                Log.d("XAC", "Login successful...");
                                            }

                                            @Override
                                            public void onFailure(Throwable e) {
                                                Log.d("XAC", "Login failed : " + Log.getStackTraceString(e));
                                            }
                                        });
                    } catch (SmackXMPPManager.ServiceUnavailableException e) {
                        Toast.makeText(MainActivity.this, "Xmpp not available...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        final EditText txtTo = (EditText) this.findViewById(R.id.txtTo);
        assert txtTo != null;

        final EditText txtMessage = (EditText) this.findViewById(R.id.txtMessage);
        assert txtMessage != null;

        Button btnSendMessage = (Button) this.findViewById(R.id.btnSendMessage);
        assert btnSendMessage != null;
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message();
                message.setBody(txtMessage.getText().toString());
                message.setType(Message.Type.chat);
                message.setStanzaId(UUID.randomUUID().toString());
                try {
                    MainActivity.this.xmppService.sendMessage(txtTo.getText().toString() + "@im.gnomesys.com", message);
                } catch (SmackException.NotConnectedException e) {
                    Toast.makeText(MainActivity.this, "XMPP not connected...", Toast.LENGTH_SHORT).show();
                } catch (SmackXMPPManager.ServiceUnavailableException e) {
                    Toast.makeText(MainActivity.this, "Xmpp not available...", Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(this, XMPPService.class);
        this.bindService(intent, this.xmppServiceConnection, BIND_AUTO_CREATE);
    }

    private void login() {

    }
}
