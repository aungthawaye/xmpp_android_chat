package com.simpleandroidchat.component.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.simpleandroidchat.BuildConfig;
import com.simpleandroidchat.R;
import com.simpleandroidchat.component.util.AndroidUtil;
import com.simpleandroidchat.component.util.Logger;

import io.github.rockerhieu.emojiconize.Emojiconize;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created By
 * Author   : Aung Thaw Aye
 * Email    : ata.aungthawaye@gmail.com
 * Date     : 27/9/16
 */

public abstract class BasicActivity extends AppCompatActivity {

    public abstract int getLayoutXmlId();

    public abstract Class<? extends BasicActivity> getDefaultBackScreen();

    public abstract Bundle getDefaultBackScreenExtra();

    public abstract int getRootLayoutId();

    private RelativeLayout progressBarParentLayout = null;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (this.isTaskRoot()) {
            if (this.getDefaultBackScreen() != null) {
                Intent intent = new Intent(this, this.getDefaultBackScreen());
                if (this.getDefaultBackScreenExtra() != null)
                    intent.putExtras(this.getDefaultBackScreenExtra());
                intent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK);
                this.startActivity(intent);
                this.finish();
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        Emojiconize.activity(this).go();

        Logger.log("Loading basic activity...");

        super.onCreate(savedInstanceState);

        // Check google play services are available on device.
        if (!BuildConfig.DEBUG)
            AndroidUtil.checkGooglePlayServices(this);

        this.setContentView(this.getLayoutXmlId());
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        ViewGroup rootLayout = (ViewGroup) this.findViewById(this.getRootLayoutId());
        Logger.log("Adding progressbar in basic activity...");
        this.addProgressBar(rootLayout);
    }

    /**
     * show progress bar spinner on UI.
     */
    protected void showProgressBar() {
        this.progressBarParentLayout.setVisibility(View.VISIBLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    /**
     * hide progress bar spinner on UI.
     */
    protected void hideProgressBar() {
        this.progressBarParentLayout.setVisibility(View.INVISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    /**
     * To dynamically build and add progress bar to all activities.
     *
     * @param rootLayout
     */
    private void addProgressBar(ViewGroup rootLayout) {

        RelativeLayout.LayoutParams parentLayoutParams =
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        parentLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        this.progressBarParentLayout = new RelativeLayout(this);
        this.progressBarParentLayout.setVisibility(View.INVISIBLE);
        this.progressBarParentLayout.setBackgroundResource(R.color.color_000000);
        this.progressBarParentLayout.setAlpha(0.75f);
        this.progressBarParentLayout.setLayoutParams(parentLayoutParams);
        this.progressBarParentLayout.setClickable(false);
        this.progressBarParentLayout.setZ(1000.0f);
        rootLayout.addView(this.progressBarParentLayout);

        RelativeLayout.LayoutParams pbarLayoutParams =
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        pbarLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyle);
        progressBar.setLayoutParams(pbarLayoutParams);
        progressBar.getIndeterminateDrawable().setColorFilter(
                Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);
        this.progressBarParentLayout.addView(progressBar);
    }
}