package com.hooha.maidai.chat.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.fragment.ContactFragment;
import com.hooha.maidai.chat.fragment.ConversationFragment;
import com.hooha.maidai.chat.fragment.MaidaiFragment;
import com.hooha.maidai.chat.fragment.WuliaobaFragment;
import com.hooha.maidai.chat.model.FriendshipInfo;
import com.hooha.maidai.chat.model.GroupInfo;
import com.hooha.maidai.chat.model.MySelfInfo;
import com.hooha.maidai.chat.model.UserInfo;
import com.hooha.maidai.chat.presenters.LoginHelper;
import com.hooha.maidai.chat.presenters.ProfileInfoHelper;
import com.hooha.maidai.chat.presenters.viewinface.ProfileView;
import com.hooha.maidai.chat.service.TlsBusiness;
import com.hooha.maidai.chat.view.DialogActivity;
import com.hooha.maidai.chat.view.NotifyDialog;
import com.tencent.TIMManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMUserStatusListener;
import com.tencent.qcloud.presentation.event.MessageEvent;

import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Tab页主界面
 */
public class HomeActivity extends FragmentActivity implements ProfileView {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private LayoutInflater layoutInflater;
    private FragmentTabHost mTabHost;
    private final Class fragmentArray[] = {ConversationFragment.class, ContactFragment.class, MaidaiFragment.class, WuliaobaFragment.class};
    private int mTitleArray[] = {R.string.home_conversation_tab, R.string.home_contact_tab, R.string.home_maidai, R.string.home_wuliao, R.string.home_setting_tab};
    private int mImageViewArray[] = {R.drawable.tab_message, R.drawable.tab_conversation, R.drawable.tab_contact, R.drawable.tab_wuliao,};
    private String mTextviewArray[] = {"conversation", "contact", "wuliaoba", "maidai"};
    private ImageView msgUnread;
    private LoginHelper mLoginHelper;
    private ProfileInfoHelper infoHelper;
    private LoginHelper mLoginHeloper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        try {
            Jpushinit(); //极光推送
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mLoginHeloper = new LoginHelper(this);
        initView();
        Log.d("cmx", "getId: " + MySelfInfo.getInstance().getId());
        Log.d("cmx", "getUserSig: " + MySelfInfo.getInstance().getUserSig());
        mLoginHeloper.imLogin(MySelfInfo.getInstance().getId(), MySelfInfo.getInstance().getUserSig());
        //互踢下线逻辑
        TIMManager.getInstance().setUserStatusListener(new TIMUserStatusListener() {
            @Override
            public void onForceOffline() {
                Log.d(TAG, "receive force offline message");
                Intent intent = new Intent(HomeActivity.this, DialogActivity.class);
                startActivity(intent);
            }

            @Override
            public void onUserSigExpired() {
                //票据过期，需要重新登录
                new NotifyDialog().show(getString(R.string.tls_expire), getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                });
            }
        });
    }

    private void Jpushinit() throws InterruptedException {
        JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);
        Thread.sleep(1000);
        String alias = MySelfInfo.getInstance().getId();
        alias = alias.replace("86-", "");
        Log.d("cmx", "Jpushinit: " + alias);
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias)); // 调用 Handler 来异步设置别名
    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(TAG, logs);
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(TAG, logs);
                    // 延迟 60 秒来调用 Handler 设置别名
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
            }
        }
    };
    private static final int MSG_SET_ALIAS = 1001;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.d(TAG, "Set alias in handler.");
                    // 调用 JPush 接口来设置别名。
                    JPushInterface.setAliasAndTags(getApplicationContext(),
                            (String) msg.obj,
                            null,
                            mAliasCallback);
                    break;
                default:
                    Log.i(TAG, "Unhandled msg - " + msg.what);
            }
        }
    };

    private void initView() {
        layoutInflater = LayoutInflater.from(this);
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.contentPanel);
        int fragmentCount = fragmentArray.length;
        for (int i = 0; i < fragmentCount; ++i) {
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
            mTabHost.getTabWidget().setDividerDrawable(null);

        }
        // 检测是否需要获取头像
        if (TextUtils.isEmpty(MySelfInfo.getInstance().getAvatar())) {
            infoHelper = new ProfileInfoHelper(this);
            infoHelper.getMyProfile();
        }
    }

    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.home_tab, null);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        icon.setImageResource(mImageViewArray[index]);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(mTitleArray[index]);
        if (index == 0) {
            msgUnread = (ImageView) view.findViewById(R.id.tabUnread);
        }
        return view;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void logout() {
        TlsBusiness.logout(UserInfo.getInstance().getId());
        UserInfo.getInstance().setId(null);
        MessageEvent.getInstance().clear();
        FriendshipInfo.getInstance().clear();
        GroupInfo.getInstance().clear();
        Intent intent = new Intent(HomeActivity.this, SplashActivity.class);
        finish();
        startActivity(intent);

    }


    /**
     * 设置未读tab显示
     */
    public void setMsgUnread(boolean noUnread) {
        msgUnread.setVisibility(noUnread ? View.GONE : View.VISIBLE);
    }

    @Override
    public void updateProfileInfo(TIMUserProfile profile) {
        if (null != profile) {
            MySelfInfo.getInstance().setAvatar(profile.getFaceUrl());
            if (!TextUtils.isEmpty(profile.getNickName())) {
                MySelfInfo.getInstance().setNickName(profile.getNickName());
            } else {
                MySelfInfo.getInstance().setNickName(profile.getNickName());
            }
        }
    }

    @Override
    public void updateUserInfo(int requestCode, List<TIMUserProfile> profiles) {

    }

//    @Override
//    protected void onStart() {
//        SxbLog.i(TAG, "HomeActivity onStart");
//        super.onStart();
//        if (QavsdkControl.getInstance().getAVContext() == null) {//retry
//            InitBusinessHelper.initApp(getApplicationContext());
//            SxbLog.i(TAG, "HomeActivity retry login");
//            mLoginHelper = new LoginHelper(this);
//            mLoginHelper.imLogin(MySelfInfo.getInstance().getId(), MySelfInfo.getInstance().getUserSig());
//        }
//    }
//    @Override
//    protected void onDestroy() {
//        if (mLoginHelper != null)
//            mLoginHelper.onDestory();
//        SxbLog.i(TAG, "HomeActivity onDestroy");
//        QavsdkControl.getInstance().stopContext();
//        super.onDestroy();
//    }
}
