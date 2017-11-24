package com.hooha.maidai.chat.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.fragment.ConversationFragment;
import com.hooha.maidai.chat.model.FriendProfile;
import com.hooha.maidai.chat.model.FriendshipInfo;
import com.hooha.maidai.chat.model.UserInfo;
import com.hooha.maidai.chat.presenters.OKhttpHelper;
import com.hooha.maidai.chat.view.LineControllerView;
import com.hooha.maidai.chat.view.ListPickerDialog;
import com.tencent.TIMAddFriendRequest;
import com.tencent.TIMCallBack;
import com.tencent.TIMConversationType;
import com.tencent.TIMDelFriendType;
import com.tencent.TIMFriendResult;
import com.tencent.TIMFriendStatus;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMManager;
import com.tencent.TIMValueCallBack;
import com.tencent.qcloud.presentation.event.FriendshipEvent;
import com.tencent.qcloud.presentation.presenter.ConversationPresenter;
import com.tencent.qcloud.presentation.presenter.FriendshipManagerPresenter;
import com.tencent.qcloud.presentation.viewfeatures.FriendshipManageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileActivity extends FragmentActivity implements FriendshipManageView, View.OnClickListener {


    private static final String TAG = ProfileActivity.class.getSimpleName();
    private static UserInfo otherinfo;

    private final int CHANGE_CATEGORY_CODE = 100;
    private final int CHANGE_REMARK_CODE = 200;
    private String tag = "profileActivity";

    private FriendshipManagerPresenter friendshipManagerPresenter;
    private ConversationPresenter contentsationPresenter;
    private String identify, categoryStr;
    TextView name;
    private Button bestBtn, attentionBtn;
    private TextView sexTv, ageTv, hobbyTv, specialTv;

    public static void navToProfile(Context context, final String identify) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra("identify", identify);
        context.startActivity(intent);
        new Thread(new Runnable() {
            @Override
            public void run() {
                otherinfo = OKhttpHelper.getInstance().otherinfoget(UserInfo.getInstance().getId(), identify);
            }
        }).start();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        identify = getIntent().getStringExtra("identify");

        friendshipManagerPresenter = new FriendshipManagerPresenter(this);
        showProfile(identify, otherinfo);
    }

    /**
     * 显示用户信息
     *
     * @param identify
     */
    public void showProfile(final String identify, UserInfo otherinfo) {
        final FriendProfile profile = FriendshipInfo.getInstance().getProfile(identify);
        Log.d(TAG, "show profile isFriend " + (profile != null));
        if (profile == null) return;
        name = (TextView) findViewById(R.id.name);
        name.setText(profile.getName());
        TextView phone = (TextView) findViewById(R.id.id_phone);
//        phone.setText(profile.getIdentify());
        phone.setText(otherinfo.getMaidaiNum());
        final TextView remark = (TextView) findViewById(R.id.remark);
//        remark.setText(profile.getRemark());
        remark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditActivity.navToEdit(ProfileActivity.this, "修改备注", "", CHANGE_REMARK_CODE, new EditActivity.EditInterface() {
                    @Override
                    public void onEdit(String text, TIMCallBack callBack) {
                        FriendshipManagerPresenter.setRemarkName(profile.getIdentify(), text, callBack);
                    }
                }, 20);

            }
        });
        LineControllerView category = (LineControllerView) findViewById(R.id.group);
        //一个用户可以在多个分组内，客户端逻辑保证一个人只存在于一个分组
        category.setContent(categoryStr = profile.getGroupName());
        LineControllerView black = (LineControllerView) findViewById(R.id.blackList);
        black.setCheckListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    FriendshipManagerPresenter.addBlackList(Collections.singletonList(identify), new TIMValueCallBack<List<TIMFriendResult>>() {
                        @Override
                        public void onError(int i, String s) {
                            Log.e(TAG, "add black list error " + s);
                        }

                        @Override
                        public void onSuccess(List<TIMFriendResult> timFriendResults) {
                            if (timFriendResults.get(0).getStatus() == TIMFriendStatus.TIM_FRIEND_STATUS_SUCC) {
                                Toast.makeText(ProfileActivity.this, getString(R.string.profile_black_succ), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
                }
            }
        });

        bestBtn = (Button) findViewById(R.id.btn_profile_best);
        if ("1".equals(otherinfo.getBest())) {
            bestBtn.setText("最好呆友▼");
        } else {
            bestBtn.setText("普通呆友▲");
        }
        bestBtn.setOnClickListener(this);
        attentionBtn = (Button) findViewById(R.id.btn_profile_attention);
        if (1 == otherinfo.getIsAttention()) {
            attentionBtn.setText("已关注");
        } else {
            attentionBtn.setText("关注");
        }
        attentionBtn.setOnClickListener(this);

        sexTv = (TextView) findViewById(R.id.tv_profile_sex);
        ageTv = (TextView) findViewById(R.id.tv_profile_age);
        hobbyTv = (TextView) findViewById(R.id.tv_profile_hobby);
        specialTv = (TextView) findViewById(R.id.tv_profile_special);
        sexTv.setText(sexTv.getText() + otherinfo.getSex());
        ageTv.setText(ageTv.getText() + otherinfo.getAge());
        hobbyTv.setText(hobbyTv.getText() + otherinfo.getHobby());
        specialTv.setText(specialTv.getText() + otherinfo.getSpecial());
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnChat:
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("identify", identify);
                intent.putExtra("type", TIMConversationType.C2C);
                startActivity(intent);
                finish();
                break;
            case R.id.btnDel:
                AlertDialog Dialog = new AlertDialog.Builder(this).create();
                Dialog.setTitle("删除联系人");
                Dialog.setMessage("删除联系人将不再接收到他的信息");
                Dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                        new DialogInterface.OnClickListener() {


                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(ProfileActivity.this, "您单击了取消按钮", Toast.LENGTH_SHORT).show();
                            }
                        });
                //添加“确定”按钮
                Dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                        new DialogInterface.OnClickListener() {


                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                friendshipManagerPresenter.delFriend(identify);

                                Intent zhuan = new Intent(getApplication(), HomeActivity.class);
                                zhuan.setAction(identify);
                                //       TIMManager.getInstance().deleteConversation(TIMConversationType.C2C, "identify");
                                TIMManager.getInstance().deleteConversationAndLocalMsgs(TIMConversationType.C2C, identify);
                                Toast.makeText(ProfileActivity.this, "删除好友成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                Dialog.show();//显示对话框
//      List<TIMAddFriendRequest> reqList = new ArrayList<TIMAddFriendRequest>();
//                TIMAddFriendRequest req = new TIMAddFriendRequest();
//                req.setIdentifier("sample_user_1");
//
//                reqList.add(req);
                final List<TIMAddFriendRequest> reqList = new ArrayList<TIMAddFriendRequest>();
                TIMFriendshipManager.getInstance().delFriend(TIMDelFriendType.TIM_FRIEND_DEL_BOTH, reqList, new TIMValueCallBack<List<TIMFriendResult>>() {

                    @Override
                    public void onSuccess(List result) {
                        Log.i(TAG, "onSuccess: " + result.size());
                        //   for (TIMFriendResult  res : result) {
                        //         Log.e(tag, "identifier: " + res.getIdentifer() + " status: " + res.getStatus());
                        //    }
                    }

                    @Override
                    public void onError(int code, String desc) {
                        //错误码code和错误描述desc，可用于定位请求失败原因
                        //错误码code列表请参见错误码表
                        Log.e(tag, "delFriend failed: " + code + " desc");
                    }


                });
                break;
            case R.id.group:
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
                        if (groups[which].equals(categoryStr)) return;
                        friendshipManagerPresenter.changeFriendGroup(identify,
                                categoryStr.equals(getString(R.string.default_group_name)) ? null : categoryStr,
                                groups[which].equals(getString(R.string.default_group_name)) ? null : groups[which]);
                    }
                });
                break;
            case R.id.btn_profile_best:
                if ("1".equals(otherinfo.getBest())) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            OKhttpHelper.getInstance().cencelBest(UserInfo.getInstance().getId(), identify);
                        }
                    }).start();
                    otherinfo.setBest("0");
                    bestBtn.setText("普通呆友▲");
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            OKhttpHelper.getInstance().addBest(UserInfo.getInstance().getId(), identify);
                        }
                    }).start();
                    otherinfo.setBest("1");
                    bestBtn.setText("最好呆友▼");
                }
                break;
            case R.id.btn_profile_attention:
                if (otherinfo.getIsAttention() == 1) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            OKhttpHelper.getInstance().cencelAttention(UserInfo.getInstance().getId(), identify);
                        }
                    }).start();
                    otherinfo.setIsAttention(0);
                    attentionBtn.setText("关注");
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            OKhttpHelper.getInstance().addAttention(UserInfo.getInstance().getId(), identify);
                        }
                    }).start();
                    otherinfo.setIsAttention(1);
                    attentionBtn.setText("已关注");
                }
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHANGE_CATEGORY_CODE) {
            if (resultCode == RESULT_OK) {
                LineControllerView category = (LineControllerView) findViewById(R.id.group);
                category.setContent(categoryStr = data.getStringExtra("category"));
            }
        } else if (requestCode == CHANGE_REMARK_CODE) {
            if (resultCode == RESULT_OK) {
//                TextView remark = (TextView) findViewById(R.id.remark);
//                remark.setText(data.getStringExtra(EditActivity.RETURN_EXTRA));
                name.setText(data.getStringExtra(EditActivity.RETURN_EXTRA));
            }
        }

    }

    /**
     * 添加好友结果回调
     *
     * @param status 返回状态
     */
    @Override
    public void onAddFriend(TIMFriendStatus status) {

    }

    /**
     * 删除好友结果回调
     *
     * @param status 返回状态
     */
    @Override
    public void onDelFriend(TIMFriendStatus status) {
        switch (status) {
            case TIM_FRIEND_STATUS_SUCC:

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OKhttpHelper.getInstance().delFriend(UserInfo.getInstance().getId(), identify);
                    }
                }).start();
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                ConversationFragment.getInstance().removeConversation(identify);
                Toast.makeText(this, getResources().getString(R.string.profile_del_succeed), Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onDelFriend: " + "删除成功");
                finish();
                break;
            case TIM_FRIEND_STATUS_UNKNOWN:
                Toast.makeText(this, getResources().getString(R.string.profile_del_fail), Toast.LENGTH_SHORT).show();
                break;
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
        LineControllerView category = (LineControllerView) findViewById(R.id.group);
        if (groupName == null) {
            groupName = getString(R.string.default_group_name);
        }
        switch (status) {
            case TIM_FRIEND_STATUS_UNKNOWN:
                Toast.makeText(this, getString(R.string.change_group_error), Toast.LENGTH_SHORT).show();
            case TIM_FRIEND_STATUS_SUCC:
                category.setContent(groupName);
                FriendshipEvent.getInstance().OnFriendGroupChange();
                break;
            default:
                Toast.makeText(this, getString(R.string.change_group_error), Toast.LENGTH_SHORT).show();
                category.setContent(getString(R.string.default_group_name));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!name.getText().toString().isEmpty()) {
            Log.i(TAG, "onDestroy: " + name.getText().toString());
            Intent intent = new Intent();
            intent.putExtra("nickname", name.getText().toString());
            setResult(10000, intent);
        }
    }
}
