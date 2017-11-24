package com.hooha.maidai.chat.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.base.BaseActivity;
import com.hooha.maidai.chat.model.MySelfInfo;
import com.hooha.maidai.chat.model.UserInfo;
import com.hooha.maidai.chat.presenters.OKhttpHelper;
import com.hooha.maidai.chat.presenters.ProfileInfoHelper;
import com.hooha.maidai.chat.presenters.UploadHelper;
import com.hooha.maidai.chat.presenters.viewinface.ProfileView;
import com.hooha.maidai.chat.presenters.viewinface.UploadView;
import com.hooha.maidai.chat.utils.Constants;
import com.hooha.maidai.chat.utils.GlideCircleTransform;
import com.hooha.maidai.chat.utils.SxbLog;
import com.hooha.maidai.chat.utils.UIUtils;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//import android.os.AsyncTask;

/**
 * Created by Administrator on 2016/7/18.
 * 个人设置
 */
public class PersonalSettingsActivity extends BaseActivity implements View.OnClickListener, ProfileView, UploadView {
    private final static int REQ_EDIT_NICKNAME = 0x100;
    private final static int REQ_EDIT_SIGN = 0x200;
    private PullToRefreshListView userListView;
    private static final int CROP_CHOOSE = 10;
    private static final int CAPTURE_IMAGE_CAMERA = 100;
    private static final int IMAGE_STORE = 200;
    //    private UserInfo userInfo;
//    private List<UserInfo> userInfoList = new ArrayList<UserInfo>();
    private RelativeLayout changeAge;//修改年龄
    private RelativeLayout changeGender;//修改性别
    private RelativeLayout changeNickname;//修改昵称
    private RelativeLayout changeAvatar;//修改头像
    private RelativeLayout changeLike;
    private RelativeLayout change_topic;//修改话题
    private RelativeLayout changeSpeacial;//修改特长
    private Button finishButton;
    private final int REQ_CHANGE_NICK = 1000;
    private TextView sexView, oldView, SpecialtyView, topicView, likeView, nickName;
    private int num = 0;
    private int str;
    private String speciality_str = "", topic_str = "", hobby_str = "", nickname = "";
    private ImageView add_avatar;
    private String TAG = "PersonalSettingsActivity个人设置";
    Bitmap photo;
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    protected static Uri tempUri;
    private UploadHelper mUploadHelper;
    private ProfileInfoHelper mProfileInfoHelper;
    private boolean bPermission = false;
    private Uri iconUrl, iconCrop;
    private String Uid;
    private List<UserInfo> mList = new ArrayList<UserInfo>();

    private void updateView() {

        if (TextUtils.isEmpty(MySelfInfo.getInstance().getAvatar())) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_avatar);
            Bitmap cirBitMap = UIUtils.createCircleImage(bitmap, 0);
            add_avatar.setImageBitmap(cirBitMap);
        } else {

            SxbLog.d(TAG, "profile avator: " + MySelfInfo.getInstance().getAvatar());
            RequestManager req = Glide.with(this);
            req.load(MySelfInfo.getInstance().getAvatar()).transform(new GlideCircleTransform(this)).into(add_avatar);
        }

    }


    private void initView() {
        changeAge = (RelativeLayout) findViewById(R.id.change_age);
        changeGender = (RelativeLayout) findViewById(R.id.change_gender);
        changeNickname = (RelativeLayout) findViewById(R.id.change_nickname);
        changeAvatar = (RelativeLayout) findViewById(R.id.changeAvatar);
        changeSpeacial = (RelativeLayout) findViewById(R.id.change_speciality);
        finishButton = (Button) findViewById(R.id.setting_finish);
        changeLike = (RelativeLayout) findViewById(R.id.change_like);
        change_topic = (RelativeLayout) findViewById(R.id.change_topic);
        sexView = (TextView) changeGender.findViewById(R.id.sexView);
        nickName = (TextView) changeNickname.findViewById(R.id.change_nickName);
        oldView = (TextView) changeAge.findViewById(R.id.oldView);
        SpecialtyView = (TextView) changeSpeacial.findViewById(R.id.SpecialtyView);
        likeView = (TextView) changeLike.findViewById(R.id.likeView);
        add_avatar = (ImageView) changeAvatar.findViewById(R.id.add_avatar);
        topicView = (TextView) change_topic.findViewById(R.id.topicView);
        changeAge.setOnClickListener(this);
        changeGender.setOnClickListener(this);
        changeNickname.setOnClickListener(this);
        changeAvatar.setOnClickListener(this);
        changeLike.setOnClickListener(this);
        finishButton.setOnClickListener(this);
        changeSpeacial.setOnClickListener(this);
        change_topic.setOnClickListener(this);
        updateView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_settings);
        setTitle("个人设置");


        mProfileInfoHelper = new ProfileInfoHelper(this);
        mUploadHelper = new UploadHelper(this, this);

        bPermission = checkCropPermission();
        new UserinfoAsyncTask().execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUploadHelper.onDestory();
    }

    private Uri createCoverUri(String type) {
        String filename = MySelfInfo.getInstance().getId() + type + ".png";
        File outputImage = new File(Environment.getExternalStorageDirectory(), filename);
        Log.d(TAG, "createCoverUri: file" + filename);
        if (ContextCompat.checkSelfPermission(PersonalSettingsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PersonalSettingsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.WRITE_PERMISSION_REQ_CODE);
            return null;
        }
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Uri.fromFile(outputImage);
    }

    private boolean checkCropPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(PersonalSettingsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(PersonalSettingsActivity.this, Manifest.permission.READ_PHONE_STATE)) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(PersonalSettingsActivity.this,
                        (String[]) permissions.toArray(new String[0]),
                        Constants.WRITE_PERMISSION_REQ_CODE);
                return false;
            }
        }
        return true;
    }

    /**
     * 获取图片资源
     *
     * @param type
     */
    private void getPicFrom(int type) {
//        if (!bPermission){
//            Toast.makeText(this, getString(R.string.tip_no_permission), Toast.LENGTH_SHORT).show();
//            return;
//        }
        switch (type) {
            case CAPTURE_IMAGE_CAMERA:
                Intent intent_photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                iconUrl = createCoverUri("_icon");
                intent_photo.putExtra(MediaStore.EXTRA_OUTPUT, iconUrl);
                startActivityForResult(intent_photo, CAPTURE_IMAGE_CAMERA);
                break;
            case IMAGE_STORE:
                iconUrl = createCoverUri("_select_icon");
                Intent intent_album = new Intent("android.intent.action.GET_CONTENT");
                intent_album.setType("image/*");
                startActivityForResult(intent_album, IMAGE_STORE);
                break;

        }
    }

    class UserinfoAsyncTask extends AsyncTask<Void, Void, UserInfo> {
        @Override
        protected UserInfo doInBackground(Void... params) {
            Uid = getIntent().getStringExtra("uid");
            if (Uid == null || "".equals(Uid)) {
                Uid = MySelfInfo.getInstance().getId();
            }
            return OKhttpHelper.getInstance().infoget(Uid);
        }

        @Override
        protected void onPostExecute(UserInfo userInfo) {
            super.onPostExecute(userInfo);
            initView();
            Log.i(TAG, "onPostExecute: userInfo" + userInfo.getName());
            if (userInfo != null) {

                nickname = userInfo.getName();
                str = Integer.valueOf(userInfo.getAge());
                sexArry[num] = userInfo.getSex();
                speciality_str = userInfo.getSpecial();
                topic_str = userInfo.getTopic();
                hobby_str = userInfo.getHobby();
                updateViewtext(userInfo);
                Log.i(TAG, "onPostExecute: sz" + userInfo);
            }


        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_age:
                //修改年龄

                showOldDialog();

                break;
            case R.id.change_gender:
                //修改性别
                showChooseDialog();
                break;
            case R.id.change_nickname:
                //修改昵称
                shownickDialog();
                break;
            case R.id.changeAvatar:
                //修改头像
                showPhotoDialog();

                //  showChoosePicDialog();
                break;
            case R.id.change_like:
                //修改爱好
                showlikeDialog();
                break;
            case R.id.change_speciality:
                //修改特长
                showspecialityDialog();
                break;
            case R.id.change_topic:
                showTopicDialog();
                break;
            case R.id.setting_finish:
                //完成修改
                UserInfo.getInstance().setHobby(hobby_str);
                UserInfo.getInstance().setAge(str + "");
                UserInfo.getInstance().setTopic(topic_str);
                UserInfo.getInstance().setName(nickname);
                UserInfo.getInstance().setSpecial(speciality_str);
                UserInfo.getInstance().setSex(sexArry[num]);

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        OKhttpHelper.getInstance().set_update_info(UserInfo.getInstance().getId(),
                                UserInfo.getInstance().getName(), UserInfo.getInstance().getSex(), str, UserInfo.getInstance().getHobby(),
                                UserInfo.getInstance().getTopic(), UserInfo.getInstance().getSpecial());

                        Log.i(TAG, "run: finish" + UserInfo.getInstance().getId() + "," +
                                UserInfo.getInstance().getName() + "," + UserInfo.getInstance().getSex() + "," + str + "," + UserInfo.getInstance().getHobby() + "," + UserInfo.getInstance().getTopic()
                                + "," + UserInfo.getInstance().getSpecial());
                    }
                }).start();
                TIMFriendshipManager.getInstance().setNickName(nickname, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {

                    }

                    @Override
                    public void onSuccess() {
                        Log.e("nick", "setNickName succ");
                    }
                });
//                Toast.makeText(getApplication(), "修改成功", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }


    String[] sexArry = new String[]{"男", "女"};//性别选择

    public void showChooseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setSingleChoiceItems(sexArry, num, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // which是被选中的位置
                //showToast(which+"");
                sexView.setText(sexArry[which]);
                num = which;
                dialog.dismiss();
                //随便点击一个item消失对话框，不用点击确认取消
            }
        });

        builder.show();// 让弹出框显示
    }

    //创建一个在我的设置里都能用的对话框，减少代码量
    public void showOldDialog() {
        AlertDialog.Builder Obuilder = new AlertDialog.Builder(this);
        final View item_person = LayoutInflater.from(this).inflate(R.layout.item_person_old, null);
        Obuilder.setView(item_person);
        Obuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //事件处理

                EditText input_setting = (EditText) item_person.findViewById(R.id.input_setting_old);
                // input_setting.setKeyListener(DialerKeyListener.getInstance());
                input_setting.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                str = Integer.valueOf(input_setting.getText().toString());
                oldView.setText(str + "");

                input_setting.clearComposingText();

            }
        });
        Obuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        Obuilder.create().show();
    }

    public void showTopicDialog() {
        AlertDialog.Builder Obuilder = new AlertDialog.Builder(this);
        final View item_person = LayoutInflater.from(this).inflate(R.layout.item_person, null);
        Obuilder.setView(item_person);
        Obuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //事件处理

                EditText input_setting = (EditText) item_person.findViewById(R.id.input_setting);

                topic_str = input_setting.getText().toString().trim();
                topicView.setText(topic_str);

            }
        });
        Obuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        Obuilder.create().show();
    }

    public void showlikeDialog() {
        AlertDialog.Builder lbuilder = new AlertDialog.Builder(this);
        final View item_person = LayoutInflater.from(this).inflate(R.layout.item_person, null);
        lbuilder.setView(item_person);
        lbuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //事件处理

                EditText input_setting = (EditText) item_person.findViewById(R.id.input_setting);
                hobby_str = input_setting.getText().toString().trim();
                likeView.setText(hobby_str);

            }
        });
        lbuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        lbuilder.create().show();
    }

    public void showspecialityDialog() {
        AlertDialog.Builder sbuilder = new AlertDialog.Builder(this);
        final View item_person = LayoutInflater.from(this).inflate(R.layout.item_person, null);
        sbuilder.setView(item_person);
        sbuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //事件处理

                EditText input_setting = (EditText) item_person.findViewById(R.id.input_setting);
                speciality_str = input_setting.getText().toString().trim();
                SpecialtyView.setText(speciality_str);

            }
        });
        sbuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        sbuilder.create().show();
    }

    public void shownickDialog() {
        AlertDialog.Builder sbuilder = new AlertDialog.Builder(this);
        final View item_person = LayoutInflater.from(this).inflate(R.layout.item_person, null);
        sbuilder.setView(item_person);
        sbuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //事件处理

                EditText input_setting = (EditText) item_person.findViewById(R.id.input_setting);
                nickname = input_setting.getText().toString().trim();
                nickName.setText(nickname);

            }
        });
        sbuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        sbuilder.create().show();
    }


    /**
     * 图片选择对话框
     */
    private void showPhotoDialog() {
        final Dialog pickDialog = new Dialog(this, R.style.floag_dialog);
        pickDialog.setContentView(R.layout.pic_choose);

        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Window dlgwin = pickDialog.getWindow();
        WindowManager.LayoutParams lp = dlgwin.getAttributes();
        dlgwin.setGravity(Gravity.BOTTOM);
        lp.width = (int) (display.getWidth()); //设置宽度

        pickDialog.getWindow().setAttributes(lp);

        TextView camera = (TextView) pickDialog.findViewById(R.id.chos_camera);
        TextView picLib = (TextView) pickDialog.findViewById(R.id.pic_lib);
        TextView cancel = (TextView) pickDialog.findViewById(R.id.btn_cancel);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPicFrom(CAPTURE_IMAGE_CAMERA);
                pickDialog.dismiss();
            }
        });

        picLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPicFrom(IMAGE_STORE);
                pickDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDialog.dismiss();
            }
        });

        pickDialog.show();
    }

    public void startPhotoZoom(Uri uri) {
        iconCrop = createCoverUri("_icon_crop");

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 300);
        intent.putExtra("aspectY", 300);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, iconCrop);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        startActivityForResult(intent, CROP_CHOOSE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            SxbLog.e(TAG, "onActivityResult->failed for request: " + requestCode + "/" + resultCode);
            return;
        }
        switch (requestCode) {
            case CAPTURE_IMAGE_CAMERA:
                startPhotoZoom(iconUrl);
                break;
            case IMAGE_STORE:
                String path = UIUtils.getPath(this, data.getData());
                if (null != path) {
                    SxbLog.e(TAG, "startPhotoZoom->path:" + path);
                    File file = new File(path);
                    startPhotoZoom(Uri.fromFile(file));
                }
                break;
            case CROP_CHOOSE:
                mUploadHelper.uploadCover(iconCrop.getPath());
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.WRITE_PERMISSION_REQ_CODE:
                for (int ret : grantResults) {
                    if (ret != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
                bPermission = true;
                break;
            default:
                break;
        }
    }

    @Override
    public void updateProfileInfo(TIMUserProfile profile) {
        MySelfInfo.getInstance().setAvatar(profile.getFaceUrl());
        new Thread(new Runnable() {
            @Override
            public void run() {

                OKhttpHelper.getInstance().setPhoto(MySelfInfo.getInstance().getAvatar(), UserInfo.getInstance().getId()
                );

                Log.i(TAG, "run: finish" + UserInfo.getInstance().getId() + "," + MySelfInfo.getInstance().getAvatar() + ","
                );
            }
        }).start();
        updateView();
    }

    @Override
    public void updateUserInfo(int reqid, List<TIMUserProfile> profiles) {
    }

    @Override
    public void onUploadProcess(int percent) {
    }

    @Override
    public void onUploadResult(int code, String url) {
        if (0 == code) {
            mProfileInfoHelper.setMyAvator(url);
            SxbLog.e(TAG, "onUploadResult->failed: " + url);
        } else {
            SxbLog.w(TAG, "onUploadResult->failed: " + code);
        }
    }


    /***
     * @param spec 图片路径
     * @param
     * @return url请求结果
     */
    public static byte[] BufferStreamForByte(String spec) {
        byte[] tempUri = null;
        try {
            BufferedInputStream bis = null;
            ByteArrayOutputStream out = null;
            try {
                FileInputStream input = new FileInputStream(spec);
                bis = new BufferedInputStream(input, 1024);
                byte[] bytes = new byte[1024];
                int len;
                out = new ByteArrayOutputStream();
                while ((len = bis.read(bytes)) > 0) {
                    out.write(bytes, 0, len);

                }

                bis.close();
                tempUri = out.toByteArray();
            } finally {
                if (bis != null)
                    bis.close();
                if (out != null)
                    out.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return tempUri;
    }

    // 上传至服务器
    // ... 可以在这里把Bitmap转换成file，然后得到file的url，做文件上传操作
    // 注意这里得到的图片已经是圆形图片了
    // bitmap是没有做个圆形处理的，但已经被裁剪了
    private void updateViewtext(UserInfo userInfo) {
        likeView.setText(userInfo.getHobby());
        sexView.setText(userInfo.getSex());
        oldView.setText(userInfo.getAge());
        SpecialtyView.setText(userInfo.getSpecial());
        topicView.setText(userInfo.getTopic());
        nickName.setText(userInfo.getName());
        if (TextUtils.isEmpty(userInfo.getHobby())) {
            likeView.setText("修改");
        }
        if (TextUtils.isEmpty(userInfo.getTopic())) {
            topicView.setText("修改");
        }
        if (TextUtils.isEmpty(userInfo.getSpecial())) {
            SpecialtyView.setText("修改");

        }
        if (TextUtils.isEmpty(userInfo.getAge())) {
            oldView.setText("修改");

        }
        if (TextUtils.isEmpty(userInfo.getSex())) {
            sexView.setText("修改");

        }

    }

}

