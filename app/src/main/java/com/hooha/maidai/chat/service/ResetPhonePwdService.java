package com.hooha.maidai.chat.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hooha.maidai.chat.activity.PhonePwdLoginActivity;
import com.hooha.maidai.chat.helper.Util;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSPwdResetListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * Created by dgy on 15/8/14.
 */
public class ResetPhonePwdService {
    private final static String TAG = "ResetPhonePwdService";

    private Context context;
    private EditText txt_countryCode;
    private EditText txt_phoneNumber;
    private EditText txt_checkCode;
    private Button btn_requireCheckCode;
    private Button btn_verify;

    private String countryCode;
    private String phoneNumber;
    private String checkCode;

    private PwdResetListener pwdResetListener;
    private TLSService tlsService;

    public ResetPhonePwdService(Context context,
                                EditText txt_countryCode,
                                EditText txt_phoneNumber,
                                EditText txt_checkCode,
                                Button btn_requireCheckCode,
                                Button btn_verify) {
        this.context = context;
        this.txt_countryCode = txt_countryCode;
        this.txt_phoneNumber = txt_phoneNumber;
        this.txt_checkCode = txt_checkCode;
        this.btn_requireCheckCode = btn_requireCheckCode;
        this.btn_verify = btn_verify;

        tlsService = TLSService.getInstance();
        pwdResetListener = new PwdResetListener();

        btn_requireCheckCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryCode = ResetPhonePwdService.this.txt_countryCode.getText().toString();
                countryCode = countryCode.substring(countryCode.indexOf('+') + 1);  // 解析国家码
                phoneNumber = ResetPhonePwdService.this.txt_phoneNumber.getText().toString();

                if (!Util.validPhoneNumber(countryCode, phoneNumber)) {
                    Util.showToast(ResetPhonePwdService.this.context, "请输入有效的手机号");
                    return;
                }

                Log.e(TAG, Util.getWellFormatMobile(countryCode, phoneNumber));

                tlsService.TLSPwdResetAskCode(countryCode, phoneNumber, pwdResetListener);
            }
        });

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryCode = ResetPhonePwdService.this.txt_countryCode.getText().toString();
                countryCode = countryCode.substring(countryCode.indexOf('+') + 1);  // 解析国家码
                phoneNumber = ResetPhonePwdService.this.txt_phoneNumber.getText().toString();
                checkCode = ResetPhonePwdService.this.txt_checkCode.getText().toString();

                if (!Util.validPhoneNumber(countryCode, phoneNumber)) {
                    Util.showToast(ResetPhonePwdService.this.context, "请输入有效的手机号");
                    return;
                }

                if (checkCode.length() == 0) {
                    Util.showToast(ResetPhonePwdService.this.context, "请输入验证码");
                    return;
                }

                Log.e(TAG, Util.getWellFormatMobile(countryCode, phoneNumber));

                tlsService.TLSPwdResetVerifyCode(checkCode, pwdResetListener);
            }
        });
    }

    class PwdResetListener implements TLSPwdResetListener {
        @Override
        public void OnPwdResetAskCodeSuccess(int reaskDuration, int expireDuration) {
            /*请求下发短信成功，可以跳转到输入验证码进行校验的界面，同时可以开始倒计时, (reaskDuration 秒内
            不可以重发短信，如果在expireDuration 秒之后仍然没有进行短信验证，则应该回到上一步，重新开始流程)
            ；在用户输入收到的短信验证码之后，可以调用PwdResetVerifyCode 进行验证。*/
            Util.showToast(context, "请求下发短信成功,验证码" + expireDuration / 60 + "分钟内有效");

            // 在获取验证码按钮上显示重新获取验证码的时间间隔
            Util.startTimer(btn_requireCheckCode, "获取验证码", "重新获取", reaskDuration, 1);
        }

        @Override
        public void OnPwdResetReaskCodeSuccess(int reaskDuration, int expireDuration) {
            /*重新请求下发短信成功，可以跳转到输入验证码进行校验的界面，并开始倒计时，(reaskDuration 秒内
            不可以再次请求重发，在expireDuration 秒之后仍然没有进行短信验证，则应该回到第一步，重新开始流程)；
            在用户输入收到的短信验证码之后，可以调用PwdResetVerifyCode 进行验证。*/
            Util.showToast(context, "注册短信重新下发,验证码" + expireDuration / 60 + "分钟内有效");
            Util.startTimer(btn_requireCheckCode, "获取验证码", "重新获取", reaskDuration, 1);
        }

        @Override
        public void OnPwdResetVerifyCodeSuccess() {
            /*短信验证成功，接下来可以引导用户输入密码，然后调用PwdResetCommit 完成重置密码流程*/
            Util.showToast(context, "改密验证通过");
            Intent intent = new Intent(context, PhonePwdLoginActivity.class);
            intent.putExtra(Constants.EXTRA_PHONEPWD_REG_RST, Constants.PHONEPWD_RESET);
            intent.putExtra(Constants.COUNTRY_CODE, txt_countryCode.getText().toString());
            intent.putExtra(Constants.PHONE_NUMBER, txt_phoneNumber.getText().toString());
            context.startActivity(intent);
            ((Activity)context).finish();
        }

        @Override
        public void OnPwdResetCommitSuccess(TLSUserInfo userInfo) {}
        /*重置密码成功，接下来可以引导用户进行新密码登录*/
        @Override
        public void OnPwdResetFail(TLSErrInfo errInfo) {
            /*重置密码过程中任意一步都可以到达这里，可以根据他来说tlsErrInfo中ErrCode  ,TitleMsg给用户
            * 弹提示语，引导相关操作*/
            Util.notOK(context, errInfo);
        }

        @Override
        public void OnPwdResetTimeout(TLSErrInfo errInfo) {
            /*重置密码过程中任意一步都可以到达这里，网络超时。
            *环境不稳定，一般让用户重试*/
            Util.notOK(context, errInfo);
        }
    }
}
