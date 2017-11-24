package com.hooha.maidai.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.base.BaseActivity;
import com.hooha.maidai.chat.model.MyMain;
import com.hooha.maidai.chat.model.MySelfInfo;
import com.loopj.android.image.SmartImageView;

/**
 * Created by Administrator on 2016/7/19.
 */
public class MyHomePageActivity extends BaseActivity implements View.OnClickListener {
    private TextView gxd;//贡献度
    private RelativeLayout post, attention;
    private ImageView home_page_post;
    private SmartImageView avatarIv;
    private TextView nickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        setTitle("我的主页");
        initViewAndEvent();
    }

    private void initViewAndEvent() {
        //初始化控件
        post = (RelativeLayout) findViewById(R.id.activity_my_home_page_post);
        attention = (RelativeLayout) findViewById(R.id.lay_myhome_attention);
        gxd = (TextView) findViewById(R.id.activity_my_home_page_gxd);

        //初始化监听事件
        post.setOnClickListener(this);
        attention.setOnClickListener(this);

        avatarIv = (SmartImageView) findViewById(R.id.home_page_avatar);
        avatarIv.setImageUrl(MySelfInfo.getInstance().getAvatar());
        nickName = (TextView) findViewById(R.id.nickName);
        nickName.setText(MyMain.getInstance().getName());
        gxd.setText(MyMain.getInstance().getContribution());

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.activity_my_home_page_post:
                Intent post = new Intent(this, InfoActivity.class);
                startActivity(post);
                break;
            case R.id.lay_myhome_attention:
                Intent attention = new Intent(this, MyFollowActivity.class);
                startActivity(attention);
                break;
        }
    }
}
