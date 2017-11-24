package com.hooha.maidai.chat.fragment;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.hooha.maidai.chat.activity.AboutActivity;
import com.hooha.maidai.chat.activity.BlackListActivity;
import com.hooha.maidai.chat.activity.EditActivity;
import com.hooha.maidai.chat.activity.HomeActivity;
import com.hooha.maidai.chat.activity.MyHomePageActivity;
import com.hooha.maidai.chat.activity.PersonalSettingsActivity;
import com.hooha.maidai.chat.activity.erweima_dialog;
import com.hooha.maidai.chat.model.MySelfInfo;
import com.hooha.maidai.chat.view.CircleImageView;
import com.hooha.maidai.chat.view.LineControllerView;
import com.hooha.maidai.chat.view.ListPickerDialog;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendAllowType;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.qcloud.presentation.business.LoginBusiness;
import com.tencent.qcloud.presentation.presenter.FriendshipManagerPresenter;
import com.tencent.qcloud.presentation.viewfeatures.FriendInfoView;
import com.hooha.maidai.chat.R;
import com.xys.libzxing.zxing.encoding.EncodingUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设置页面
 */
public class SettingFragment extends Fragment implements FriendInfoView, View.OnClickListener {

    private static final String TAG = SettingFragment.class.getSimpleName();
    private View view;
    private FriendshipManagerPresenter friendshipManagerPresenter;
    private TextView id,name,Text;
    private LineControllerView nickName, friendConfirm;
    private String contentString;
    private final int REQ_CHANGE_NICK = 1000;
    private CircleImageView circleImageView;
    private Map<String, TIMFriendAllowType> allowTypeContent;
    private RelativeLayout wodeguanzhu,gerenshezhi,xitongshezhi,wodezhuye,erweima;//我的关注，个人设置，系统设置，我的主页，我的二维码
    Bitmap bmp;
    String ava;
//    String TAG="设置页面";

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
//            }
//        });
        if (view == null){
            view = inflater.inflate(R.layout.fragment_setting, container, false);
            name = (TextView) view.findViewById(R.id.name);//昵称
            id = (TextView) view.findViewById(R.id.idtext);//id号
            circleImageView=(CircleImageView)view.findViewById(R.id.head_me);//头像
            wodeguanzhu = (RelativeLayout) view.findViewById(R.id.st_guanzhu);//我的关注
            wodezhuye= (RelativeLayout) view.findViewById(R.id.st_homepage);//我的主页
            gerenshezhi = (RelativeLayout) view.findViewById(R.id.st_personal);//个人设置
            xitongshezhi = (RelativeLayout) view.findViewById(R.id.st_sys);//系统设置
            erweima = (RelativeLayout) view.findViewById(R.id.st_erweima);//我的二维码
            erweima.setOnClickListener(this);
            wodezhuye.setOnClickListener(this);
            wodeguanzhu.setOnClickListener(this);
            gerenshezhi.setOnClickListener(this);
            xitongshezhi.setOnClickListener(this);
            friendshipManagerPresenter = new FriendshipManagerPresenter(this);
            friendshipManagerPresenter.getMyProfile();
            TextView logout = (TextView) view.findViewById(R.id.logout);
//            if (!TextUtils.isEmpty(UserInfo.getInstance().getAvatar())){
//                Log.d("我的", "load cover: " + UserInfo.getInstance().getAvatar());
//                RequestManager req = Glide.with(this);
//                req.load(UserInfo.getInstance().getAvatar()).into(circleImageView);
//            }
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoginBusiness.logout(new TIMCallBack() {
                        @Override
                        public void onError(int i, String s) {
                            Toast.makeText(getActivity(), getResources().getString(R.string.setting_logout_fail), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess() {
                            ((HomeActivity) getActivity()).logout();
                        }
                    });
                }
            });
            nickName = (LineControllerView) view.findViewById(R.id.nickName);
            nickName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditActivity.navToEdit(SettingFragment.this, getResources().getString(R.string.setting_nick_name_change), name.getText().toString(), REQ_CHANGE_NICK, new EditActivity.EditInterface() {
                        @Override
                        public void onEdit(String text, TIMCallBack callBack) {
                            FriendshipManagerPresenter.setMyNick(text, callBack);
                        }
                    }, 20);

                }
            });
            ava= MySelfInfo.getInstance().getAvatar();
            if (!TextUtils.isEmpty(ava)){
                Log.d(TAG, "load cover: " + ava);
                RequestManager req = Glide.with(this);
                req.load(ava).into(circleImageView);
            }
            allowTypeContent = new HashMap<>();
            allowTypeContent.put(getString(R.string.friend_allow_all), TIMFriendAllowType.TIM_FRIEND_ALLOW_ANY);
            allowTypeContent.put(getString(R.string.friend_need_confirm), TIMFriendAllowType.TIM_FRIEND_NEED_CONFIRM);
            allowTypeContent.put(getString(R.string.friend_refuse_all), TIMFriendAllowType.TIM_FRIEND_DENY_ANY);
            final String[] stringList = allowTypeContent.keySet().toArray(new String[allowTypeContent.size()]);
            friendConfirm = (LineControllerView) view.findViewById(R.id.friendConfirm);
            friendConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new ListPickerDialog().show(stringList, getFragmentManager(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, final int which) {
                            FriendshipManagerPresenter.setFriendAllowType(allowTypeContent.get(stringList[which]), new TIMCallBack() {
                                @Override
                                public void onError(int i, String s) {
                                    Toast.makeText(getActivity(), getString(R.string.setting_friend_confirm_change_err), Toast.LENGTH_SHORT).show();
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
            LineControllerView blackList = (LineControllerView) view.findViewById(R.id.blackList);
            blackList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), BlackListActivity.class);
                    startActivity(intent);
                }
            });
            LineControllerView about = (LineControllerView) view.findViewById(R.id.about);
            about.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), AboutActivity.class);
                    startActivity(intent);
                }
            });

        }
        return view ;
    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CHANGE_NICK){
            if (resultCode == getActivity().RESULT_OK){
                setNickName(data.getStringExtra(EditActivity.RETURN_EXTRA));
            }
        }

    }

    private void setNickName(String name){
        if (name==null) return ;
        this.name.setText(name);
        nickName.setContent(name);
        Log.d(TAG, "setNickName: "+name);
    }
    private  void setAva(String ava){
        TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onSuccess(TIMUserProfile timUserProfile) {
                RequestManager req = Glide.with(getActivity());
                req.load(timUserProfile.getFaceUrl()).into(circleImageView);
                Log.i(TAG, "onSuccess: getFaceUrl"+timUserProfile.getFaceUrl());
            }
        });
//    if (ava==null)return;
//    RequestManager req = Glide.with(this);
//    req.load(ava).into(circleImageView);
//    Log.d(TAG, "setAva: "+circleImageView);
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
        setAva(users.get(0).getFaceUrl());
        Log.d(TAG, "showUserInfo: ava" + users.get(0).getFaceUrl());
        for (String item : allowTypeContent.keySet()){
            if (allowTypeContent.get(item) == users.get(0).getAllowType()){
                friendConfirm.setContent(item);
                break;
            }
        }

    }
    public Bitmap getBitmap() {
        Bitmap mBitmap = null;
        try {
            URL url = new URL(ava);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream is = conn.getInputStream();
            mBitmap = BitmapFactory.decodeStream(is);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mBitmap;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.st_guanzhu://我的关注
                Toast.makeText(getActivity(), "我的关注", Toast.LENGTH_SHORT).show();
                break;
            case R.id.st_sys://系统设置
//                startActivity(new Intent(getActivity(),SearchFriendActivity.class));
                Toast.makeText(getActivity(), "系统设置", Toast.LENGTH_SHORT).show();
                // TODO: 2016/7/19 跳转系统设置界面
                break;
            case R.id.st_personal://个人设置
                startActivity(new Intent(getActivity(), PersonalSettingsActivity.class));
                break;
            case R.id.st_homepage://我的主页
                startActivity(new Intent(getActivity(), MyHomePageActivity.class));
                break;
            case R.id.st_erweima:
                Dialog dialog=new erweima_dialog(getActivity(),R.style.erweima);
                ImageView ecode_img=(ImageView)dialog.findViewById(R.id.ecode_img);
                contentString=id.getText().toString();
                if (!contentString.equals("")) {
                    //根据字符串生成二维码图片并显示在界面上，第二个参数为图片的大小（350*350）
                    Bitmap qrCodeBitmap = EncodingUtils.createQRCode(contentString, 1350, 1350,null);
                    //  BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)
                    ecode_img.setImageBitmap(qrCodeBitmap);
                    dialog.show();
                } else {

                    Toast.makeText(getActivity(), "Text can not be empty", Toast.LENGTH_SHORT).show();
                }
        }
    }
}




