package com.hooha.maidai.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.base.BaseActivity;
import com.hooha.maidai.chat.view.VideoInputDialog;

/**
 * Created by MG on 2016/10/14.
 */
public class PostActivity extends FragmentActivity implements View.OnClickListener {

    private ImageView wenzi, photo, video;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_activity);
        wenzi = (ImageView) findViewById(R.id.shangchuanwenzi);
        photo = (ImageView) findViewById(R.id.shangchuanphoto);
        video = (ImageView) findViewById(R.id.shangchuanvideo);
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        wenzi.setOnClickListener(this);
        photo.setOnClickListener(this);
        video.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shangchuanwenzi:
                Toast.makeText(this, "上传文字", Toast.LENGTH_SHORT).show();
                Intent wenzi = new Intent(this, duanziActivity.class);
                startActivity(wenzi);
                finish();
                break;
            case R.id.shangchuanphoto:
                Intent pic = new Intent(this, TestPicActivity.class);
                startActivity(pic);
                Toast.makeText(this, "上传图片", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.shangchuanvideo:
                Toast.makeText(this, "上传视频", Toast.LENGTH_SHORT).show();
                Intent video = new Intent(this, PostvideoActivity.class);
                startActivity(video);
                finish();
//                FragmentActivity fragmentActivity = (FragmentActivity) getApplicationContext();
////                if (requestVideo(fragmentActivity)){
//                    VideoInputDialog.show(fragmentActivity.getSupportFragmentManager());
////                }

                break;
            case R.id.back:
                finish();
                break;
        }
    }
}
