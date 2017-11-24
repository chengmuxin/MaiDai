package com.hooha.maidai.chat.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.hooha.maidai.chat.R;

/**
 * Created by MG on 2016/10/17.
 */
public class VideoShowActivity extends Activity {
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoshow);

        webView = (WebView) findViewById(R.id.wv_videoshow);
        webView.loadUrl(getIntent().getStringExtra("url"));
    }
}
