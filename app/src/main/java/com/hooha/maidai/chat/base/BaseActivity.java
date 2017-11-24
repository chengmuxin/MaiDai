package com.hooha.maidai.chat.base;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.utils.Constant;

import java.util.Map;

/**
 * 创建者：邱世君
 * 创建日期：2015/11/30
 * 创建时间：14:14
 * 描述：
 */

public class BaseActivity extends FragmentActivity {
    private BroadcastReceiver recv;

    private Toast toast = null;

    protected    void T(String msg) {
        if (toast == null) {
            toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActivityCollector.addActivity(this);

        recv = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constant.BD_EXIT_APP)){
                    finish();
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.BD_EXIT_APP);

        registerReceiver(recv, filter);
    }


    protected void finishAll() {
        ActivityCollector.finishAll();
    }

    protected void finishOtherButMain() {
        ActivityCollector.finishOtherButMain();
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        ActivityCollector.finishActivity(cls);
    }

    @Override
    protected void onDestroy() {
        ActivityCollector.finishActivity(this);
        try {
            unregisterReceiver(recv);
        }catch (Exception e){
        }
        super.onDestroy();
    }
    private Map<String, Fragment> fragmentMap;

    /**
     * 返回
     *
     * @param v
     */
    public void onBack(View v) {

        finish();
    }

    /**
     * 设置title
     *
     * @param title
     */
    public void setTitle(String title) {
        TextView textView = (TextView) findViewById(R.id.head_biaoti);
        if (textView != null) {
            textView.setText(title == null ? getTitle() : title);
        }
    }


    /**
     * 设置右上角的文本文字
     *
     * @param text
     */


//    public void setRightText(String text) {
//        TextView textView = (TextView) findViewById(R.id.head_right_text);
//        if (textView != null) {
//            textView.setText(text);
//        }
//    }
}
/**
 * 设置左上角的文本文字
 *
 * @param text
 */
//    public void setRightText(int text) {
//        TextView textView = (TextView) findViewById(R.id.head_right_text);
//        if (textView != null) {
//            textView.setText(text == 0 ? getTitle() : getString(text));
//        }
//    }




