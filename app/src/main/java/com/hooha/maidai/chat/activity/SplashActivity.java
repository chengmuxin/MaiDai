package com.hooha.maidai.chat.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.avcontrollers.QavsdkControl;
import com.hooha.maidai.chat.model.MySelfInfo;
import com.hooha.maidai.chat.model.UserInfo;
import com.hooha.maidai.chat.presenters.LoginHelper;
import com.hooha.maidai.chat.presenters.OKhttpHelper;
import com.hooha.maidai.chat.service.TLSService;
import com.hooha.maidai.chat.service.TlsBusiness;
import com.hooha.maidai.chat.utils.Constants;
import com.hooha.maidai.chat.utils.LogConstants;
import com.hooha.maidai.chat.utils.PushUtil;
import com.hooha.maidai.chat.utils.SxbLog;
import com.hooha.maidai.chat.view.CustomSwitch;
import com.hooha.maidai.chat.view.NotifyDialog;
import com.tencent.TIMCallBack;
import com.tencent.TIMLogLevel;
import com.tencent.qcloud.presentation.business.InitBusiness;
import com.tencent.qcloud.presentation.business.LoginBusiness;
import com.tencent.qcloud.presentation.event.FriendshipEvent;
import com.tencent.qcloud.presentation.event.GroupEvent;
import com.tencent.qcloud.presentation.event.MessageEvent;
import com.tencent.qcloud.presentation.event.RefreshEvent;
import com.tencent.qcloud.presentation.presenter.SplashPresenter;
import com.tencent.qcloud.presentation.viewfeatures.SplashView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class SplashActivity extends FragmentActivity implements SplashView, TIMCallBack {

    SplashPresenter presenter;
    private int LOGIN_RESULT_CODE = 100;
    private final int REQUEST_PHONE_PERMISSIONS = 0;
    private static final String TAG = "SplashActivity";
    private LoginHelper mLoginHeloper;
    CustomSwitch info;

    String url = "http://101.201.36.74:8080/maidai/userInfo?svc=user&cmd=register";
    //    String url = "http://101.201.36.74/php/mysqlapi.php";
    private String filePath = "/storage/emulated/0/testimg.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        mLoginHeloper = new LoginHelper(this);

        // TODO: 2016/7/21 上传文件（现在是错误405）
//        File file = new File(Environment.getExternalStorageDirectory(), "testimg.jpg");
//        if (!file.exists())
//        {
//            Toast.makeText(SplashActivity.this, "文件不存在，请修改文件路径", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        OkHttpUtils.post().addFile("mFile", filePath, file)
//                .url("http://101.201.36.74/php/uploadphp.php")
//             //   .params(params)
//             //   .headers(headers)
//                .build()//
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        Toast.makeText(SplashActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
//                        Log.i(TAG, "onError: "+e.getMessage());
//                    }
//
//                    @Override
//                    public void onResponse(String response, int id) {
//                        Log.i(TAG, "onResponse: " + response.trim());
//                        Toast.makeText(SplashActivity.this, response.trim(), Toast.LENGTH_LONG).show();
//                    }
//                });

        final List<String> permissionsList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.READ_PHONE_STATE);
            if ((checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionsList.size() == 0) {
                init();
            } else {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_PHONE_PERMISSIONS);
            }
        } else {
            init();
        }
    }

    /**
     * 跳转到主界面
     */
    @Override
    public void navToHome() {
        //直播登录
        mLoginHeloper.imLogin(MySelfInfo.getInstance().getId(), MySelfInfo.getInstance().getUserSig());
        //登录之前要初始化群和好友关系链缓存
        FriendshipEvent.getInstance().init();
        GroupEvent.getInstance().init();
        LoginBusiness.loginIm(UserInfo.getInstance().getId(), UserInfo.getInstance().getUserSig(), this);//this既是callback的实现类，登录成功后回调onSuccess()方法
    }

    /**
     * 跳转到登录界面
     */
    @Override
    public void navToLogin() {
        Intent intent = new Intent(getApplicationContext(), IndependentLoginActivity.class);
        startActivityForResult(intent, LOGIN_RESULT_CODE);
    }

    /**
     * 是否已有用户登录
     */
    @Override
    public boolean isUserLogin() {
        return UserInfo.getInstance().getId() != null && (!TLSService.getInstance().needLogin(UserInfo.getInstance().getId()));
    }

    /**
     * imsdk登录失败后回调
     */
    @Override
    public void onError(int i, String s) {
        Log.e(TAG, "login error : code " + i + " " + s);
        switch (i) {
            case 6208:
                //离线状态下被其他终端踢下线
                NotifyDialog dialog = new NotifyDialog();
                dialog.show(getString(R.string.kick_logout), getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        navToHome();
                    }
                });
                break;
            default:
                Toast.makeText(this, getString(R.string.login_error), Toast.LENGTH_SHORT).show();
                navToLogin();
                break;
        }
    }

    /**
     * imsdk登录成功后回调
     */
    @Override
    public void onSuccess() {
        clearNotification();
        if (0 == TLSService.getInstance().getLastErrno()) {//重新登陆时走这
            OkHttpUtils.get().url(url)
                    .addParams("a", "selectby")
                    .addParams("table", "tb_userinfo")
                    .addParams("selectcol", "UserInfo_tencentname")
                    .addParams("selectvalue", UserInfo.getInstance().getId())
                    .build()
                    .execute(
                            new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    Toast.makeText(SplashActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    Log.e(TAG, "onResponse: " + response);
                                    response = response.substring(response.indexOf(">") + 1);
                                    response = response.trim();
                                    Log.i(TAG, "onResponse: " + response);
                                    if (response.equals("[]")) {
                                        Intent intent = new Intent(SplashActivity.this, SettingUserInfoActivity.class);
                                        intent.putExtra("p", "" + UserInfo.getInstance().getId());
                                        startActivity(intent);
                                        finish();
                                    } else {
//                                Toast.makeText(SplashActivity.this, "else", Toast.LENGTH_SHORT).show();
                                        if (MySelfInfo.getInstance().getLogin() == 0) {
                                            MySelfInfo.getInstance().setLogin(1);
                                            Intent intent = getIntent();
                                            overridePendingTransition(0, 0);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                            finish();

                                            overridePendingTransition(0, 0);
                                            startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }

                                }
                            }
                    );
        } else {
            //初始化程序后台后消息推送
            PushUtil.getInstance();
            //初始化消息监听
            MessageEvent.getInstance();
            shouldMiInit();
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (LOGIN_RESULT_CODE == requestCode) {
            if (0 == TLSService.getInstance().getLastErrno()) {
                String id = TLSService.getInstance().getLastUserIdentifier();
                UserInfo.getInstance().setId(id);
                UserInfo.getInstance().setUserSig(TLSService.getInstance().getUserSig(id));
                navToHome();
                Log.e("onActivityResult", "navToHome");
                finish();
            } else if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                } else {
                    Toast.makeText(this, getString(R.string.need_permission), Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void init() {
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        int loglvl = pref.getInt("loglvl", TIMLogLevel.DEBUG.ordinal());
        //初始化IMSDK
        InitBusiness.start(getApplicationContext(), loglvl);
        //初始化TLS
        TlsBusiness.init(getApplicationContext());
        //设置刷新监听
        RefreshEvent.getInstance();
        String id = TLSService.getInstance().getLastUserIdentifier();
        UserInfo.getInstance().setId(id);
        UserInfo.getInstance().setUserSig(TLSService.getInstance().getUserSig(id));
        MySelfInfo.getInstance().setId(id);
        MySelfInfo.getInstance().setUserSig(TLSService.getInstance().getUserSig(id));
        presenter = new SplashPresenter(this);
        presenter.start();
    }

    /**
     * 向用户服务器获取自己房间号
     */
    private void getMyRoomNum() {
        MySelfInfo.getInstance().setMyRoomNum(64856);
        if (MySelfInfo.getInstance().getMyRoomNum() == -1) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    OKhttpHelper.getInstance().getMyRoomId(SplashActivity.this);
                }
            }).start();
        } else {
            SxbLog.d(TAG, LogConstants.ACTION_HOST_CREATE_ROOM + LogConstants.DIV + MySelfInfo.getInstance().getId() + LogConstants.DIV + "request room id"
                    + LogConstants.DIV + LogConstants.STATUS.SUCCEED + LogConstants.DIV + "get room id from local " + MySelfInfo.getInstance().getMyRoomNum());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 判断小米推送是否已经初始化
     */
    private boolean shouldMiInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 初始化AVSDK
     */
    private void startAVSDK() {
        QavsdkControl.getInstance().setAvConfig(Constants.SDK_APPID, "" + Constants.ACCOUNT_TYPE, MySelfInfo.getInstance().getId(), MySelfInfo.getInstance().getUserSig());
        QavsdkControl.getInstance().startContext();

    }

    private void clearNotification() {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
}