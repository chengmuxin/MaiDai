package com.hooha.maidai.chat.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.TIMCallBack;
import com.tencent.qcloud.presentation.presenter.GroupManagerPresenter;
import com.hooha.maidai.chat.R;

/**
 * 申请加群发送请求
 */
public class ApplyGroupActivity extends Activity implements TIMCallBack {

    private final String TAG = "ApplyGroupActivity";

    private String identify;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_group);
        identify = getIntent().getStringExtra("identify");
        TextView des = (TextView) findViewById(R.id.description);
        des.setText("申请加入 " + identify);
        final EditText editText = (EditText) findViewById(R.id.input);
        TextView btnSend = (TextView) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupManagerPresenter.applyJoinGroup(identify, editText.getText().toString(), ApplyGroupActivity.this);
            }
        });
    }

    @Override
    public void onError(int i, String s) {

    }

    @Override
    public void onSuccess() {
        Toast.makeText(this, getResources().getString(R.string.send_success), Toast.LENGTH_SHORT).show();
        finish();
    }
}
