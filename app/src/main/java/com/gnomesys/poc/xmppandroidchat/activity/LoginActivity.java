package com.gnomesys.poc.xmppandroidchat.activity;

import android.content.ComponentName;
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
import com.gnomesys.poc.xmppandroidchat.component.ResultCallback;
import com.gnomesys.poc.xmppandroidchat.component.SmackXMPPManager;
import com.gnomesys.poc.xmppandroidchat.service.XMPPService;

public class LoginActivity extends AppCompatActivity {

    private XMPPService xmppService = null;
    private ServiceConnection xmppServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("XMPP", "Android Xmpp service connected...");
            XMPPService.XmppServiceBinder binder = (XMPPService.XmppServiceBinder) iBinder;
            LoginActivity.this.xmppService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("XMPP", "Xmpp service disconnected...");
            LoginActivity.this.xmppService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText txtUsername = (EditText) this.findViewById(R.id.txtUsername);
        assert txtUsername != null;

        final EditText txtPassword = (EditText) this.findViewById(R.id.txtPassword);
        assert txtPassword != null;

        Button btnLogin = (Button) this.findViewById(R.id.btnLogin);
        assert btnLogin != null;
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (LoginActivity.this.xmppService != null) {
                    try {
                        LoginActivity.this.xmppService
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
                        Toast.makeText(LoginActivity.this, "Xmpp not available...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

}
