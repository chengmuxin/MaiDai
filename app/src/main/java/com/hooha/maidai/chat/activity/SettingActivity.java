package com.hooha.maidai.chat.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.base.BaseActivity;
import com.hooha.maidai.chat.model.FriendshipInfo;
import com.hooha.maidai.chat.model.GroupInfo;
import com.hooha.maidai.chat.model.MySelfInfo;
import com.hooha.maidai.chat.model.UserInfo;
import com.hooha.maidai.chat.service.TlsBusiness;
import com.hooha.maidai.chat.view.CircleImageView;
import com.hooha.maidai.chat.view.CustomSwitch;
import com.hooha.maidai.chat.view.SwitchView;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMManager;
import com.tencent.TIMOfflinePushListener;
import com.tencent.TIMOfflinePushNotification;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.qcloud.presentation.business.LoginBusiness;
import com.tencent.qcloud.presentation.event.MessageEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;


/**
 * Created by Public on 2016/7/19.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private CircleImageView circleImageView;
    private TextView nameTV;
    private TextView huancunTV;
    private TextView versionTV;
    private SwitchView switchView;
    private TextView fankuiTV;
    private AlertDialog ad1;
    private CustomSwitch info;
    private Boolean info_set = false;
    private String TAG = "系统设置";
    private int push;

    private File root;
    private String ava;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitle("系统设置");
        initViewAndEvent();
        nameTV.setText(UserInfo.getInstance().getId());
        Log.d(TAG, "onCreate: nameTV" + nameTV);

        Log.i(TAG, "onCreate: shishi" + MySelfInfo.getInstance().getAvatar());
        TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onSuccess(TIMUserProfile timUserProfile) {
                RequestManager req = Glide.with(getApplication());
                req.load(timUserProfile.getFaceUrl()).into(circleImageView);
                Log.i(TAG, "onSuccess: getFaceUrl" + timUserProfile.getFaceUrl());
            }
        });
//        ava=MySelfInfo.getInstance().getAvatar();
//        Log.d(TAG, "onCreate: touxiang"+ava);
//        if(!TextUtils.isEmpty(ava)){
//            RequestManager req = Glide.with(this);
//            req.load(ava).into(circleImageView);
//            Log.d(TAG, "onCreate: ava"+ava);
//            Log.d(TAG, "setAva: " + circleImageView);
    }

    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    private void initViewAndEvent() {
        findViewById(R.id.activity_setting_about).setOnClickListener(this);
        findViewById(R.id.activity_setting_clear_imageLoader).setOnClickListener(this);
        findViewById(R.id.activity_setting_exit).setOnClickListener(this);
        circleImageView = (CircleImageView) findViewById(R.id.activity_setting_head_me);
        circleImageView.setOnClickListener(this);
        findViewById(R.id.activity_setting_mianze).setOnClickListener(this);
        nameTV = (TextView) findViewById(R.id.activity_setting_name);
        huancunTV = (TextView) findViewById(R.id.activity_setting_huancun);
        versionTV = (TextView) findViewById(R.id.activity_setting_version);
        versionTV.setText(versionTV.getText().toString() + UserInfo.getInstance().getNumber());
        fankuiTV = (TextView) findViewById(R.id.activity_setting_fankui);

        info = (CustomSwitch) findViewById(R.id.activity_setting_switch_btn);
        info.setChecked(UserInfo.getInstance().getSwitch_btn(), false);
        info.setOnClickListener(this);
        Log.d(TAG, "info: " + info.getChecked());

        findViewById(R.id.activity_setting_reset_psd).setOnClickListener(this);
        findViewById(R.id.activity_setting_yijian).setOnClickListener(this);

        findViewById(R.id.activity_setting_switch_btn);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                List<String> jiejiuwo1=new ArrayList<String>();
//          OKhttpHelper.getInstance().GET_JIEJIUWO(MySelfInfo.getInstance().getId());
//                Log.d(TAG, "run: jiejiuwo "+OKhttpHelper.getInstance().GET_JIEJIUWO(MySelfInfo.getInstance().getId()));
//            }
//        }).start();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                ava= OKhttpHelper.getInstance().getPhoto(MySelfInfo.getInstance().getId());
//                Log.i(TAG, "SettingActivity" +ava);
//            }
//        }).start();


    }

//    private void deleteAllFiles(File root) {
//
//        root = getCacheDir();
//        File files[] = root.listFiles();
//        if (files != null)
//            for (File f : files) {
//                if (f.isDirectory()) { // 判断是否为文件夹
//                    deleteAllFiles(f);
//                    try {
//                        f.delete();
//                    } catch (Exception e) {
//                    }
//                } else {
//                    if (f.exists()) { // 判断是否存在
//                        deleteAllFiles(f);
//                        try {
//                            f.delete();
//                        } catch (Exception e) {
//                            Log.e(TAG, "deleteAllFiles: " + f);
//                        }
//                    }
//                }
//            }
//    }

    public static void cleanApplicationData(Context context, String... filepath) {
        cleanApplicationData(context, filepath);
    }

//    public static Bitmap getBitmap(String path) throws IOException {
//
//        path = (MySelfInfo.getInstance().getAvatar()).toString();
//
//        // OKhttpHelper.getInstance().getPhoto(MySelfInfo.getInstance().getId());
//        URL url = new URL(path);
//        Log.i("SettingActivity", "getbitmap: url" + path);
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setConnectTimeout(5000);
//        conn.setRequestMethod(" POST");
//        if (conn.getResponseCode() == 200) {
//            InputStream inputStream = conn.getInputStream();
//            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//
//            return bitmap;
//        }
//        return null;
//
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_setting_switch_btn://消息免打扰

                if (info.getChecked()) {
                    info.setChecked(false, true);
                    SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
                    info_set = sharedPreferences.getBoolean("info", false);
//                    Toast.makeText(SettingActivity.this, "有声", Toast.LENGTH_SHORT).show();
                    UserInfo.getInstance().setSwitch_btn(false);
                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                    editor.putBoolean("info", false);

                    editor.commit();
                    TIMManager.getInstance().setOfflinePushListener(new TIMOfflinePushListener() {
                        @Override
                        public void handleNotification(TIMOfflinePushNotification notification) {
                            notification.doNotify(getApplication(), 0);

                        }
                    });


                } else {
                    info.setChecked(true, true);
                    SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
                    info_set = sharedPreferences.getBoolean("info_set", false);
                    UserInfo.getInstance().setSwitch_btn(true);
                    NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();

//                    Toast.makeText(SettingActivity.this, "没声", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = getSharedPreferences("info_set", MODE_PRIVATE).edit();

                    editor.commit();
                    Log.d(info_set.toString(), "onClick: info_set");
                }
                break;
            case R.id.activity_setting_about: //关于
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.activity_setting_clear_imageLoader://清除缓存

                AlertDialog.Builder Obuilder = new AlertDialog.Builder(this);
                //final View item_person = LayoutInflater.from(this).inflate(R.layout.item_person, null);
                //  Obuilder.setView(item_person);
                Obuilder.setTitle("清除缓存");
                Obuilder.setMessage("确定要清除缓存");
                Obuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //事件处理
                        Toast.makeText(getApplication(), "清除成功", Toast.LENGTH_SHORT).show();
                        root = getCacheDir();
                        Log.i(TAG, "onClick: root" + root);
                        //  deleteAllFiles(root);
                    }
                });
                Obuilder.setNegativeButton("让我再想想！", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Toast.makeText(getApplication(), "消除了清除缓存", Toast.LENGTH_SHORT).show();
                    }
                });
                Obuilder.create().show();
                break;
            case R.id.activity_setting_exit://退出登录
                LoginBusiness.logout(new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(SettingActivity.this, getResources().getString(R.string.setting_logout_fail), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() {
                        TlsBusiness.logout(UserInfo.getInstance().getId());
                        UserInfo.getInstance().setId(null);
                        MessageEvent.getInstance().clear();
                        FriendshipInfo.getInstance().clear();
                        GroupInfo.getInstance().clear();
                        MySelfInfo.getInstance().setLogin(0);
                        finish();
                        Intent intent = new Intent(SettingActivity.this, SplashActivity.class);

                        startActivity(intent);
                    }
                });
                break;
            case R.id.activity_setting_head_me://头像


                break;
            case R.id.activity_setting_mianze://免责声明
                Intent intent = new Intent(SettingActivity.this, DeclareActivity.class);
                startActivity(intent);
                break;
            case R.id.activity_setting_reset_psd://修改密码
                Intent intentpwd = new Intent(this, ResetPhonePwdActivity.class);
                startActivity(intentpwd);
                break;
            case R.id.activity_setting_yijian://意见反馈
                startActivity(new Intent(this, FeedbackActivity.class));
                break;

        }


    }

//    public static Bitmap getbitmap(String picurl) throws IOException {
//        OKhttpHelper.getInstance().getPhoto(MySelfInfo.getInstance().getId());
//        URL url = new URL(picurl);
//
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setConnectTimeout(5000);
//        conn.setRequestMethod(" POST");
//        if (conn.getResponseCode() == 200) {
//            InputStream inputStream = conn.getInputStream();
//            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//
//            return bitmap;
//        }
//        return null;
//
//    }

    //将获得的bitmap放置到ImageView控件中：


    public void save(String info) {
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            out = openFileOutput("data", Context.MODE_PRIVATE);
            writer = new BufferedWriter((new OutputStreamWriter(out)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            writer.write(info);
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}






