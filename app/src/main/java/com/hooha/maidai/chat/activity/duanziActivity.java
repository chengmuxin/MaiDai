package com.hooha.maidai.chat.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.base.BaseActivity;
import com.hooha.maidai.chat.model.CurLiveInfo;
import com.hooha.maidai.chat.model.MySelfInfo;
import com.hooha.maidai.chat.presenters.OKhttpHelper;

import java.util.List;

/**
 * Created by MG on 2016/10/14.
 */
public class duanziActivity extends BaseActivity implements View.OnClickListener, TextWatcher {
    private EditText duanzi;
    private String TAG = "duanzi";
    private String tag;
    private List<String> tag1;
    Editable wenzi1;
    String type = "段子";
    private String wenzi = "", time1 = "", wuliao1 = "", gaoxiao1 = "", zidingyi1 = "";
    private ImageView back;
    private TextView time, wuliao, gaoxiao, zidingyi;
    private  TextView send_duanzi;
Button a;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_wenzi);
        duanzi = (EditText) findViewById(R.id.duanzi);

        send_duanzi = (TextView) findViewById(R.id.send_duanzi);
        time = (TextView) findViewById(R.id.time);
        wuliao = (TextView) findViewById(R.id.wuliao);
        gaoxiao = (TextView) findViewById(R.id.gaoxiao);
        zidingyi = (TextView) findViewById(R.id.zidingyi);
        back = (ImageView) findViewById(R.id.back);
        duanzi.addTextChangedListener(this);
        zidingyi.addTextChangedListener(this);
        back.setOnClickListener(this);

        time.setOnClickListener(this);
        gaoxiao.setOnClickListener(this);
        wuliao.setOnClickListener(this);
        zidingyi.setOnClickListener(this);
        duanzi.setOnClickListener(this);
        send_duanzi.setOnClickListener(this);
        gaoxiao.setSelected(true);
        wuliao.setSelected(true);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.duanzi:


                Log.d(TAG, "onClick: " + wenzi);
                break;

            case R.id.time:

                time1 = time.getText().toString();


                break;
            case R.id.wuliao:
                wuliao1 = wuliao.getText().toString();
                break;
            case R.id.gaoxiao:
                gaoxiao1 = gaoxiao.getText().toString();
                break;
            case R.id.zidingyi:
                showspecialityDialog();


                break;
            case R.id.back:
                finish();
                break;
            case R.id.send_duanzi:

                Thread iThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OKhttpHelper.getInstance().Posttext(type, wenzi, MySelfInfo.getInstance().getId(), time1 + " " + wuliao1 + " " + zidingyi1 + " " + gaoxiao1);
                    }
                });
                iThread.start();
                try {
                    iThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplication(), "段子发送", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    public void showspecialityDialog() {
        AlertDialog.Builder sbuilder = new AlertDialog.Builder(this);
        final View item_person = LayoutInflater.from(this).inflate(R.layout.item_person, null);
        sbuilder.setView(item_person);
        sbuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //事件处理

                EditText input_setting = (EditText) item_person.findViewById(R.id.input_setting);
                zidingyi.setText(input_setting.getText().toString().trim());


            }
        });
        sbuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        sbuilder.create().show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        wenzi = duanzi.getText().toString();

        zidingyi1 = zidingyi.getText().toString();
        Log.d(TAG, "afterTextChanged: " + wenzi);
    }
}
