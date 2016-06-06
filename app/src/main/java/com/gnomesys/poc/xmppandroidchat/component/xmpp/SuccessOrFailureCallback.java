package com.gnomesys.poc.xmppandroidchat.component.xmpp;

/**
 * Created By
 * Author   : Aung Thaw Aye
 * Email    : ata.aungthawaye@gmail.com (aungthawaye@gnomesys.com)
 * Date     : 6/6/16
 */
public interface SuccessOrFailureCallback {
    void onSuccess();

    void onFailure(Throwable e);
}
