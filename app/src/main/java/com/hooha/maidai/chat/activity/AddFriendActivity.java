package com.hooha.maidai.chat.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hooha.maidai.chat.model.CurLiveInfo;
import com.hooha.maidai.chat.model.FriendProfile;
import com.hooha.maidai.chat.model.FriendshipInfo;
import com.hooha.maidai.chat.model.MySelfInfo;
import com.hooha.maidai.chat.model.UserInfo;
import com.hooha.maidai.chat.presenters.OKhttpHelper;
import com.hooha.maidai.chat.view.ListPickerDialog;
import com.hooha.maidai.chat.view.NotifyDialog;
import com.tencent.TIMFriendResult;
import com.tencent.TIMFriendStatus;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.qcloud.presentation.event.FriendshipEvent;
import com.tencent.qcloud.presentation.presenter.FriendshipManagerPresenter;
import com.tencent.qcloud.presentation.viewfeatures.FriendshipManageView;
import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.view.LineControllerView;

import java.util.Collections;
import java.util.List;

/**
 * 申请添加好友界面
 */
public class AddFriendActivity extends FragmentActivity implements View.OnClickListener, FriendshipManageView {


    private TextView tvName, btnAdd;
    private EditText editRemark, editMessage;
    private LineControllerView idField, groupField;
    private FriendshipManagerPresenter presenter;
    private String id,id_sys;
private String TAG="AddFriendActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        tvName = (TextView) findViewById(R.id.name);
        idField = (LineControllerView) findViewById(R.id.id_phone);
        id = getIntent().getStringExtra("id");
       // id_sys=getIntent().getStringExtra("scanResult");
        tvName.setText(getIntent().getStringExtra("name"));

        idField.setContent(id);
   //     idField.setContent(id_sys);
        groupField = (LineControllerView) findViewById(R.id.group);
        btnAdd = (TextView) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);
        editMessage = (EditText) findViewById(R.id.editMessage);
        editRemark = (EditText) findViewById(R.id.editNickname);
        presenter = new FriendshipManagerPresenter(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnAdd) {

            presenter.addFriend(id, editRemark.getText().toString(), groupField.getContent().equals(getString(R.string.default_group_name))?"":groupField.getContent(), editMessage.getText().toString());
          //  presenter.addFriend(id_sys, editRemark.getText().toString(), groupField.getContent().equals(getString(R.string.default_group_name))?"":groupField.getContent(), editMessage.getText().toString());

        }else if (view.getId() == R.id.group){
            final String[] groups = FriendshipInfo.getInstance().getGroupsArray();
            for (int i = 0; i < groups.length; ++i) {
                if (groups[i].equals("")) {
                    groups[i] = getString(R.string.default_group_name);
                    break;
                }
            }
            new ListPickerDialog().show(groups, getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    groupField.setContent(groups[which]);
                }
            });
        }
    }

    /**
     * 添加好友结果回调
     *
     * @param status 返回状态
     */
    @Override
    public void onAddFriend(TIMFriendStatus status) {
        switch (status){
            case TIM_ADD_FRIEND_STATUS_PENDING:
                Toast.makeText(this, getResources().getString(R.string.add_friend_succeed), Toast.LENGTH_SHORT).show();
                finish();
                break;
            case TIM_FRIEND_STATUS_SUCC:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OKhttpHelper.getInstance().addFriend(MySelfInfo.getInstance().getId(),FriendProfile.getInstance().getIdentify());
                    }
                }).start();
                Log.i(TAG, "onAddFriend: "+"添加成功"+MySelfInfo.getInstance().getId()+FriendProfile.getInstance().getIdentify());
                Toast.makeText(this, getResources().getString(R.string.add_friend_added), Toast.LENGTH_SHORT).show();
                finish();
                break;
            case TIM_ADD_FRIEND_STATUS_FRIEND_SIDE_FORBID_ADD:
                Toast.makeText(this, getResources().getString(R.string.add_friend_refuse_all), Toast.LENGTH_SHORT).show();
                finish();
                break;
            case TIM_ADD_FRIEND_STATUS_IN_OTHER_SIDE_BLACK_LIST:
                Toast.makeText(this, getResources().getString(R.string.add_friend_to_blacklist), Toast.LENGTH_SHORT).show();
                finish();
                break;
            case TIM_ADD_FRIEND_STATUS_IN_SELF_BLACK_LIST:
                NotifyDialog dialog = new NotifyDialog();
                dialog.show(getString(R.string.add_friend_del_black_list), getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FriendshipManagerPresenter.delBlackList(Collections.singletonList(id), new TIMValueCallBack<List<TIMFriendResult>>() {
                            @Override
                            public void onError(int i, String s) {
                                Toast.makeText(AddFriendActivity.this, getResources().getString(R.string.add_friend_del_black_err), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess(List<TIMFriendResult> timFriendResults) {
                                Toast.makeText(AddFriendActivity.this, getResources().getString(R.string.add_friend_del_black_succ), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                break;
            default:
                Toast.makeText(this, getResources().getString(R.string.add_friend_error), Toast.LENGTH_SHORT).show();
                break;
        }

    }

    /**
     * 删除好友结果回调
     *
     * @param status 返回状态
     */
    @Override
    public void onDelFriend(TIMFriendStatus status) {
switch (status){
    case TIM_FRIEND_STATUS_SUCC:

        new Thread(new Runnable() {
            @Override
            public void run() {
                OKhttpHelper.getInstance().delFriend(UserInfo.getInstance().getId(), FriendProfile.getInstance().getIdentify());
            }
        }).start();
        Intent intent=new Intent(getApplication(),HomeActivity.class);
        startActivity(intent);
        Toast.makeText(this,"删除好友",Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onDelFriend: " + "被删除成功");
    case TIM_FRIEND_STATUS_UNKNOWN:
     Toast.makeText(this,"删除好友失败",Toast.LENGTH_SHORT).show();
}

    }

    /**
     * 修改好友分组回调
     *
     * @param status    返回状态
     * @param groupName 分组名
     */
    @Override
    public void onChangeGroup(TIMFriendStatus status, String groupName) {

    }

}
