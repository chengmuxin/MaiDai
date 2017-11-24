package com.hooha.maidai.chat.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hooha.maidai.chat.activity.ImgCodeActivity;
import com.hooha.maidai.chat.helper.Util;
import com.hooha.maidai.chat.presenters.LoginHelper;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSPwdLoginListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * Created by dgy on 15/8/12.
 * 登录service
 */
public class AccountLoginService {

    private final static String TAG = "AccountLoginService";

    private Context context;
    private EditText txt_username;
    private EditText txt_password;

    private String username;
    private String password;

    private TLSService tlsService;
    public static PwdLoginListener pwdLoginListener;
    private LoginHelper mLoginHeloper;


    public AccountLoginService(Context context,
                               EditText txt_username,
                               EditText txt_password,
                               Button btn_login) {
        this.context = context;
        this.txt_username = txt_username;
        this.txt_password = txt_password;

        tlsService = TLSService.getInstance();
        pwdLoginListener = new PwdLoginListener();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = AccountLoginService.this.txt_username.getText().toString();
                password = AccountLoginService.this.txt_password.getText().toString();

                // 验证用户名和密码的有效性
                if (username.length() == 0 || password.length() == 0) {
                    Util.showToast(AccountLoginService.this.context, "用户名密码不能为空");
                    return;
                }

                tlsService.TLSPwdLogin("86-" + username, password, pwdLoginListener);
                Log.e("username", "" + username);
                Log.e("password", "" + password);
            }
        });
    }

    class PwdLoginListener implements TLSPwdLoginListener {
        @Override
        public void OnPwdLoginSuccess(TLSUserInfo userInfo) {
            Util.showToast(context, "登录成功!");
            TLSService.getInstance().setLastErrno(0);//这里设置0主要是在本页finish后返回splash页的onActivityResult方法调用.getLastErrno()判断
            AccountLoginService.this.jumpToSuccActivity();
        }

        @Override
        public void OnPwdLoginReaskImgcodeSuccess(byte[] picData) {
            ImgCodeActivity.fillImageview(picData);
        }

        @Override
        public void OnPwdLoginNeedImgcode(byte[] picData, TLSErrInfo errInfo) {
            Intent intent = new Intent(context, ImgCodeActivity.class);
            intent.putExtra(Constants.EXTRA_IMG_CHECKCODE, picData);
            intent.putExtra(Constants.EXTRA_LOGIN_WAY, Constants.USRPWD_LOGIN);
            context.startActivity(intent);
        }

        @Override
        public void OnPwdLoginFail(TLSErrInfo errInfo) {
            TLSService.getInstance().setLastErrno(-1);
            Util.notOK(context, errInfo);
        }

        @Override
        public void OnPwdLoginTimeout(TLSErrInfo errInfo) {
            TLSService.getInstance().setLastErrno(-1);
            Util.notOK(context, errInfo);
        }
    }

    void jumpToSuccActivity() {
        String thirdappPackageNameSucc = Constants.thirdappPackageNameSucc;
        String thirdappClassNameSucc = Constants.thirdappClassNameSucc;

        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_LOGIN_WAY, Constants.USRPWD_LOGIN);
        intent.putExtra(Constants.EXTRA_USRPWD_LOGIN, Constants.USRPWD_LOGIN_SUCCESS);
        if (thirdappPackageNameSucc != null && thirdappClassNameSucc != null) {
            intent.setClassName(thirdappPackageNameSucc, thirdappClassNameSucc);
            context.startActivity(intent);
        } else {
            ((Activity) context).setResult(Activity.RESULT_OK, intent);
            ((Activity) context).finish();
        }
    }
}
