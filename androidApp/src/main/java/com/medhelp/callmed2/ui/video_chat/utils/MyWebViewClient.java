package com.medhelp.callmed2.ui.video_chat.utils;

import android.net.http.SslError;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyWebViewClient extends WebViewClient {
    WrbClientListener listener;

    public MyWebViewClient( WrbClientListener listener) {
        this.listener=listener;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        toLog("shouldOverrideUrlLoading "+request.toString());
        return super.shouldOverrideUrlLoading(view, request);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        toLog("onPageFinished "+url);

        viewport: view.loadUrl("javascript:(function(){var m=document.createElement('META'); m.name='viewport'; m.content='width=device-width, user-scalable=yes'; document.body.appendChild(m);})()");

        listener.ready();
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        //Toast.makeText(mContext, "onReceivedError", Toast.LENGTH_SHORT).show();

        toLog("onReceivedError "+error.toString());
        super.onReceivedError(view, request, error);
    }

    @SuppressWarnings("deprecation")
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        if(description.equals("net::ERR_CONNECTION_REFUSED"))
        {
            //Toast.makeText(mContext, "onReceivedError "+description, Toast.LENGTH_SHORT).show();
            listener.error("ERR CONNECTION REFUSED");
        }

        super.onReceivedError(view, errorCode, description, failingUrl);

    }


    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

        if (error.toString() == "piglet")
            handler.cancel();
        else
            handler.proceed(); // Ignore SSL certificate errors
    }

    private void toLog(String msg)
    {
        Log.wtf("mLog","MyWebViewClient msg= "+msg);
    }


    public interface WrbClientListener {
        void ready();
        void error(String msg);
    }
}
