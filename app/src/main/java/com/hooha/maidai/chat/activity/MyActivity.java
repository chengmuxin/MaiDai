package com.hooha.maidai.chat.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.base.BaseActivity;
import com.hooha.maidai.chat.model.FriendshipInfo;
import com.hooha.maidai.chat.model.GroupInfo;
import com.hooha.maidai.chat.model.UserInfo;
import com.hooha.maidai.chat.presenters.OKhttpHelper;
import com.hooha.maidai.chat.service.TlsBusiness;
import com.hooha.maidai.chat.view.LineControllerView;
import com.hooha.maidai.chat.view.ListPickerDialog;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendAllowType;
import com.tencent.TIMUserProfile;
import com.tencent.qcloud.presentation.business.LoginBusiness;
import com.tencent.qcloud.presentation.event.MessageEvent;
import com.tencent.qcloud.presentation.presenter.FriendshipManagerPresenter;
import com.tencent.qcloud.presentation.viewfeatures.FriendInfoView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Public on 2016/7/21.
 */
public class MyActivity extends BaseActivity implements FriendInfoView, View.OnClickListener {

    private FriendshipManagerPresenter friendshipManagerPresenter;
    private TextView id, name;
    private LineControllerView nickName, friendConfirm;
    private final int REQ_CHANGE_NICK = 1000;
    private Map<String, TIMFriendAllowType> allowTypeContent;
    private RelativeLayout wodeguanzhu, gerenshezhi, xitongshezhi, wodezhuye;//我的关注，个人设置，系统设置，我的主页

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        /**
         * 进来就改头像
         */
//        FriendshipManagerPresenter.setMyFace("https://dn-yesiemqt.qbox.me/NKISfvUXSKm8vTJBaTZNpkR7e6OM2idU0x6q1rgM", new TIMCallBack() {
//            @Override
//            public void onError(int i, String s) {
//
//            }
//
//            @Override
//            public void onSuccess() {
//
//            }
//        });
        id = (TextView) findViewById(R.id.idtext);
        name = (TextView) findViewById(R.id.name);
        wodeguanzhu = (RelativeLayout) findViewById(R.id.st_guanzhu);
        wodezhuye = (RelativeLayout) findViewById(R.id.st_homepage);
        gerenshezhi = (RelativeLayout) findViewById(R.id.st_personal);
        xitongshezhi = (RelativeLayout) findViewById(R.id.st_sys);
        wodezhuye.setOnClickListener(this);
        wodeguanzhu.setOnClickListener(this);
        gerenshezhi.setOnClickListener(this);
        xitongshezhi.setOnClickListener(this);
        friendshipManagerPresenter = new FriendshipManagerPresenter(this);
        friendshipManagerPresenter.getMyProfile();
        TextView logout = (TextView) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginBusiness.logout(new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(MyActivity.this, getResources().getString(R.string.setting_logout_fail), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() {
                        TlsBusiness.logout(UserInfo.getInstance().getId());
                        UserInfo.getInstance().setId(null);
                        MessageEvent.getInstance().clear();
                        FriendshipInfo.getInstance().clear();
                        GroupInfo.getInstance().clear();
                        Intent intent = new Intent(MyActivity.this, SplashActivity.class);
                        finish();
                        startActivity(intent);
                    }
                });
            }
        });
        nickName = (LineControllerView) findViewById(R.id.nickName);
        nickName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditActivity.navToEdit(MyActivity.this, getResources().getString(R.string.setting_nick_name_change), name.getText().toString(), REQ_CHANGE_NICK, new EditActivity.EditInterface() {
                    @Override
                    public void onEdit(String text, TIMCallBack callBack) {
                        FriendshipManagerPresenter.setMyNick(text, callBack);
                    }
                }, 20);

            }
        });

        allowTypeContent = new HashMap<>();
        allowTypeContent.put(getString(R.string.friend_allow_all), TIMFriendAllowType.TIM_FRIEND_ALLOW_ANY);
        allowTypeContent.put(getString(R.string.friend_need_confirm), TIMFriendAllowType.TIM_FRIEND_NEED_CONFIRM);
        allowTypeContent.put(getString(R.string.friend_refuse_all), TIMFriendAllowType.TIM_FRIEND_DENY_ANY);
        final String[] stringList = allowTypeContent.keySet().toArray(new String[allowTypeContent.size()]);
        friendConfirm = (LineControllerView) findViewById(R.id.friendConfirm);
        friendConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new ListPickerDialog().show(stringList, getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, final int which) {
                        FriendshipManagerPresenter.setFriendAllowType(allowTypeContent.get(stringList[which]), new TIMCallBack() {
                            @Override
                            public void onError(int i, String s) {
                                Toast.makeText(MyActivity.this, getString(R.string.setting_friend_confirm_change_err), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess() {
                                friendConfirm.setContent(stringList[which]);
                            }
                        });
                    }
                });
            }
        });
        LineControllerView blackList = (LineControllerView) findViewById(R.id.blackList);
        blackList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyActivity.this, BlackListActivity.class);
                startActivity(intent);
            }
        });
        LineControllerView about = (LineControllerView) findViewById(R.id.about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                OKhttpHelper.getInstance().about();
                OKhttpHelper.getInstance().statement();
                OKhttpHelper.getInstance().myMain(UserInfo.getInstance().getId());
            }
        }).start();
    }







    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CHANGE_NICK){
            if (resultCode == this.RESULT_OK){
                setNickName(data.getStringExtra(EditActivity.RETURN_EXTRA));
            }
        }

    }

    private void setNickName(String name){
        if (name == null) return;
        this.name.setText(name);
        nickName.setContent(name);
    }


    /**
     * 显示用户信息
     *
     * @param users 资料列表
     */
    @Override
    public void showUserInfo(List<TIMUserProfile> users) {
        id.setText(users.get(0).getIdentifier());
        setNickName(users.get(0).getNickName());
        for (String item : allowTypeContent.keySet()){
            if (allowTypeContent.get(item) == users.get(0).getAllowType()){
                friendConfirm.setContent(item);
                break;
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.st_guanzhu://我的关注
                startActivity(new Intent(MyActivity.this,MyFollowActivity.class));
                break;
            case R.id.st_sys://系统设置
//                startActivity(new Intent(getActivity(),SearchFriendActivity.class));
                startActivity(new Intent(MyActivity.this,SettingActivity.class));
                // TODO: 2016/7/19 跳转系统设置界面
                break;
            case R.id.st_personal://个人设置
                startActivity(new Intent(MyActivity.this,PersonalSettingsActivity.class));
                break;
            case R.id.st_homepage://我的主页
               startActivity(new Intent(MyActivity.this,MyHomePageActivity.class));
                break;
        }
    }
}
