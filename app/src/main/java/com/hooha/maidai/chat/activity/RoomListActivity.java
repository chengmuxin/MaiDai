package com.hooha.maidai.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.base.BaseActivity;
import com.hooha.maidai.chat.bean.RoomBean;
import com.hooha.maidai.chat.model.MySelfInfo;

import java.util.List;

/**
 * Created by Public on 2016/7/28.
 */
public class RoomListActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private List<RoomBean> list;
    private Button gotoPublishLive;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);
        //根据传过来的type值确定设置标题
        type = MySelfInfo.getInstance().getRoomType();
        switch (type) {
            case 0:
                setTitle("竞技类");
                break;
            case 1:
                setTitle("搞笑类");
                break;
            case 2:
                setTitle("恶搞类");
                break;
        }
        gotoPublishLive = (Button) findViewById(R.id.goto_publish_live);
        gotoPublishLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoomListActivity.this, PublishLiveActivity.class);
                startActivity(intent);
            }
        });
    }


}
