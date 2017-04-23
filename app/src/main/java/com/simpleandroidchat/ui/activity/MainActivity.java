package com.simpleandroidchat.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.simpleandroidchat.R;
import com.simpleandroidchat.chat.core.ResultCallback;
import com.simpleandroidchat.chat.service.ChatService;
import com.simpleandroidchat.chat.util.UserInfoStorage;
import com.simpleandroidchat.component.util.Logger;
import com.simpleandroidchat.component.view.BasicActivity;


/**
 * Created By
 * Author   : Aung Thaw Aye
 * Email    : ata.aungthawaye@gmail.com
 * Date     : 23/04/17
 */

public class MainActivity extends BasicActivity {

    /**
     * Here, we have already started ChatService in SimpleAndroidChatApp. Now, we will get
     * ChatService instance, so that we can perform Login action if user hasn't logged in yet.
     * So, we have to create ServiceConnection and get the binder and then get the service instance.
     */
    private ChatService chatService = null;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Logger.log("MessagingSupportedActivity - onServiceConnected...");
            ChatService.ChatServiceBinder binder = (ChatService.ChatServiceBinder) iBinder;
            MainActivity.this.chatService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            // Do nothing
            MainActivity.this.chatService = null;
        }
    };

    @Override
    public int getLayoutXmlId() {
        return R.layout.activity_main;
    }

    @Override
    public Class<? extends BasicActivity> getDefaultBackScreen() {
        return null;
    }

    @Override
    public Bundle getDefaultBackScreenExtra() {
        return null;
    }

    @Override
    public int getRootLayoutId() {
        return R.id.activity_login;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (this.chatService != null) {
            this.unbindService(this.serviceConnection);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, ChatService.class);
        this.bindService(intent, this.serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // As soon as we launch this screen, we will check whether we have correct credentials
        // in our local storage. If we have, we are sure we can start the chatting. We won't need
        // to ask user to do login.
        if (UserInfoStorage.getInstance().isCredentialsAvailable()) {
            // We go to next screen.
            this.finish();
            return;
        }

        final EditText txtUsername = (EditText) this.findViewById(R.id.txtUsername);
        final EditText txtPassword = (EditText) this.findViewById(R.id.txtPassword);
        final Button btnLogin = (Button) this.findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = txtUsername.getText().toString();
                String password = txtPassword.getText().toString();

                Logger.log("MainActivity - onClick : username : " + username);
                Logger.log("MainActivity - onClick : password : " + password);
                Logger.log("MainActivity - onClick : trying to login...");

                if(MainActivity.this.chatService == null)
                    throw new RuntimeException("Unexpected system failure...");

                MainActivity.this.chatService.login(username, password, new ResultCallback() {
                    @Override
                    public void onSuccess() {
                        // Everything was fine. We go to next screen.
                        Logger.log("MainActivity - onSuccess : everything is fine. Logged in successfully...");
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // We got some issue here.
                        Logger.log("MainActivity - onFailure : error is --> " + Log.getStackTraceString(e));
                    }
                });
            }
        });

    }
}
