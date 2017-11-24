package com.hooha.maidai.chat.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

import com.hooha.maidai.chat.R;
import com.loopj.android.image.SmartImageView;

/**
 * Created by MG on 2016/10/17.
 */
public class ImageShowActivity extends Activity {
    private SmartImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageshow);

        imageView = (SmartImageView) findViewById(R.id.iv_imageshow);
        imageView.setImageUrl(getIntent().getStringExtra("url"));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return false;
    }
}
