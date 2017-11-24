package com.hooha.maidai.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.utils.PushUtil;
import com.hooha.maidai.chat.view.CheckableGroup;
import com.hooha.maidai.chat.view.CheckableView;
import com.tencent.qcloud.presentation.event.MessageEvent;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/7/7.
 */
public class SettingUserInfoActivity extends Activity implements CheckableGroup.OnCheckedChangeListener, CheckableView.OnCheckedChangeListener {
    private static final String TAG = "SettingUserInfoActivity";
//    String url = "http://101.201.36.74/php/mysqlapi.php?a=insert&table=tb_userinfo&column=UserInfo_name&column1=UserInfo_age&column2=UserInfo_favorite&column3=UserInfo_topic&column4=UserInfo_sex&column5=UserInfo_tencentname";

    String url="http://101.201.36.74:8080/maidai/userInfo?svc=user&cmd=infoInsert";
            String gender = "";
    String hobby = "";
    String topic = "";
    private EditText ageEditText;
    private List<String> huati;//存放话题的集合
    private List<String> aihao;
    private CheckBox cb_1, cb_2;
    private Button save;
    String age;
    CheckableView checkableView_1, checkableView_2, checkableView_3, checkableView_4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_info);
        huati = new ArrayList<String>();
        aihao = new ArrayList<String>();
        ((CheckableGroup) findViewById(R.id.checkable_group)).setOnCheckedChangeListener(this);
        checkableView_1 = (CheckableView) findViewById(R.id.checkable_view_1);
        checkableView_2 = (CheckableView) findViewById(R.id.checkable_view_2);
        checkableView_3 = (CheckableView) findViewById(R.id.checkable_view_3);
        checkableView_4 = (CheckableView) findViewById(R.id.checkable_view_4);
        cb_1 = (CheckBox) findViewById(R.id.cb_1);
        cb_2 = (CheckBox) findViewById(R.id.cb_2);
        save = (Button) findViewById(R.id.save);
        ageEditText = (EditText) findViewById(R.id.et_age);
        checkableView_1.setOnCheckedChangeListener(this);
        checkableView_2.setOnCheckedChangeListener(this);
        checkableView_3.setOnCheckedChangeListener(this);
        checkableView_4.setOnCheckedChangeListener(this);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_1.isChecked()) {
                    huati.add("是否被驴踢过");
                }
                if (cb_2.isChecked()) {
                    huati.add("是否是大舌头");
                }
                for (int i = 0; i < huati.size(); i++) {
                    if (i == 0) {
                        topic += huati.get(i);
                    } else {
                        topic += ":" + huati.get(i);
                    }
                    Log.i(TAG, "onClick: " + topic);
                }
                if (ageEditText.getText().toString().equals("")) {
                    Toast.makeText(SettingUserInfoActivity.this, "请输入年龄！", Toast.LENGTH_SHORT).show();
                } else {
                    age = ageEditText.getText().toString();
                }

                if (checkableView_1.isChecked()) {
                    aihao.add("集邮");
                }
                if (checkableView_2.isChecked()) {
                    aihao.add("篮球");
                }
                if (checkableView_3.isChecked()) {
                    aihao.add("自行车");
                }
                if (checkableView_4.isChecked()) {
                    aihao.add("跑步");
                }
                for (int i = 0; i < aihao.size(); i++) {
                    if (i == 0) {
                        hobby += aihao.get(i);
                    } else {
                        hobby += ":" + aihao.get(i);
                    }
                }
                Log.i(TAG, "onClick: " + hobby);
                Log.i(TAG, "onClick: " + age);
                Log.i(TAG, "onClick: " + gender);
                String p = getIntent().getStringExtra("p");
                OkHttpUtils
                        .get()//
                        .url(url)//
                        /**
                         *&value=李某&value1=24&value2=摄影&value3=话题1&value4=男
                         */
                        .addParams("value", "aa")
                        .addParams("value1", age)
                        .addParams("value2", hobby)
                        .addParams("value3", topic)
                        .addParams("value4", gender)
                        .addParams("value5", p)
                        .build()//
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {

                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Log.e(TAG, ":" + response);
                                //初始化程序后台后消息推送
                                PushUtil.getInstance();
                                //初始化消息监听
                                MessageEvent.getInstance();
                                Intent intent = new Intent(SettingUserInfoActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });


            }
        });


    }

    @Override
    public void onCheckedChanged(CheckableGroup checkableGroup, CheckableView checkableView, boolean isChecked) {
        Log.i(TAG, "onCheckedChanged: " + checkableGroup.getCheckedCheckableViewPosition());
        if (checkableGroup.getCheckedCheckableViewPosition() == 0) {
            gender = "男";
        } else if (checkableGroup.getCheckedCheckableViewPosition() == 1) {
            gender = "女";
        }
    }

    @Override
    public void onCheckedChanged(CheckableView checkableView, boolean isChecked) {

//        switch (checkableView.getId()) {
//            case R.id.checkable_view_1:
//                if (isChecked) {
//                    hobby.add("集邮");
//                }
//                break;
//            case R.id.checkable_view_2:
//                if (isChecked) {
//                    hobby.add("篮球");
//                }
//                break;
//            case R.id.checkable_view_3:
//                if (isChecked) {
//                    hobby.add("自行车");
//                }
//                break;
//            case R.id.checkable_view_4:
//                if (isChecked) {
//                    hobby.add("跑步");
//                }
//                break;
//
//        }
    }
}
