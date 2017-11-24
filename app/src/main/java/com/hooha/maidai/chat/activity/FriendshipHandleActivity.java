package com.hooha.maidai.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hooha.maidai.chat.model.FriendProfile;
import com.hooha.maidai.chat.model.MySelfInfo;
import com.hooha.maidai.chat.presenters.OKhttpHelper;
import com.tencent.TIMFriendResult;
import com.tencent.TIMValueCallBack;
import com.tencent.qcloud.presentation.presenter.FriendshipManagerPresenter;
import com.hooha.maidai.chat.R;

public class FriendshipHandleActivity extends Activity implements View.OnClickListener {

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendship_handle);
        id = getIntent().getStringExtra("id");
        TextView tvName = (TextView) findViewById(R.id.name);
        tvName.setText(id);
        String word = getIntent().getStringExtra("word");
        TextView tvWord = (TextView) findViewById(R.id.word);
        tvWord.setText(word);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnReject:
                FriendshipManagerPresenter.refuseFriendRequest(id, new TIMValueCallBack<TIMFriendResult>() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(FriendshipHandleActivity.this, getString(R.string.operate_fail), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(TIMFriendResult timFriendResult) {
                        Intent intent = new Intent();
                        intent.putExtra("operate", false);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
                break;
            case R.id.btnAgree:
                FriendshipManagerPresenter.acceptFriendRequest(id, new TIMValueCallBack<TIMFriendResult>() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(FriendshipHandleActivity.this, getString(R.string.operate_fail), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(TIMFriendResult timFriendResult) {
                        Intent intent = new Intent();
                        intent.putExtra("operate", true);
                        setResult(RESULT_OK, intent);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                OKhttpHelper.getInstance().addFriend(MySelfInfo.getInstance().getId(), id);
                            }
                        }).start();
                        Log.i( "onAddFriend: " , "添加成功" + MySelfInfo.getInstance().getId() +id);
                        finish();
                    }
                });
                break;
        }
    }
}
