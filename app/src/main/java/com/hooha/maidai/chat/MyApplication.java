package com.hooha.maidai.chat;

import android.app.Application;
import android.content.Context;

import com.hooha.maidai.chat.presenters.InitBusinessHelper;
import com.hooha.maidai.chat.utils.SxbLogImpl;
import com.tencent.TIMGroupReceiveMessageOpt;
import com.tencent.TIMManager;
import com.tencent.TIMOfflinePushListener;
import com.tencent.TIMOfflinePushNotification;
import com.tencent.qalsdk.sdk.MsfSdkUtils;
import com.hooha.maidai.chat.utils.Foreground;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.https.HttpsUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;


/**
 * 全局Application
 */
public class MyApplication extends Application {
    private static Context context;
    public static MyApplication app;
    @Override
    public void onCreate() {
        super.onCreate();
        Foreground.init(this);
        app = this;
        context = getApplicationContext();
        SxbLogImpl.init(getApplicationContext());
        InitBusinessHelper.initApp(context);
        if (MsfSdkUtils.isMainProcess(this)) {
            TIMManager.getInstance().setOfflinePushListener(new TIMOfflinePushListener() {
                @Override
                public void handleNotification(TIMOfflinePushNotification notification) {
                    if (notification.getGroupReceiveMsgOpt() == TIMGroupReceiveMessageOpt.ReceiveAndNotify) {
                        //消息被设置为需要提醒
                        notification.doNotify(getApplicationContext(), R.mipmap.ic_launcher);
                    }
                   else if (notification.getGroupReceiveMsgOpt() == TIMGroupReceiveMessageOpt.NotReceive) {
                        //消息被设置为不需要提醒
                        notification.doNotify(getApplicationContext(), R.mipmap.ic_launcher);

                }}
            });
        }
//        ClearableCookieJar cookieJar1 = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getApplicationContext()));

        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);

//        CookieJarImpl cookieJar1 = new CookieJarImpl(new MemoryCookieStore());
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .addInterceptor(new LoggerInterceptor("TAG"))
//                .cookieJar(cookieJar1)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    public static Context getContext() {
        return context;
    }
    public static MyApplication getInstance(){
        return app;
    }
}
