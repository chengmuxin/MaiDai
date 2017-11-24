package com.hooha.maidai.chat.activity;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.model.UserInfo;

public class AboutActivity extends FragmentActivity {
    private TextView numberTv, introduceTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        numberTv = (TextView) findViewById(R.id.tv_about_number);
        introduceTv = (TextView) findViewById(R.id.tv_about_introduce);
        numberTv.setText("当前版本：" + UserInfo.getInstance().getNumber());
        introduceTv.setText(UserInfo.getInstance().getIntroduce());

    }

//        LineControllerView imsdk = (LineControllerView) findViewById(R.id.imsdk);
//        imsdk.setContent(TIMManager.getInstance().getVersion());
//        LineControllerView qalsdk = (LineControllerView) findViewById(R.id.qalsdk);
//        qalsdk.setContent(QALSDKManager.getInstance().getSdkVersion());
//        LineControllerView tlssdk = (LineControllerView) findViewById(R.id.tlssdk);
//        tlssdk.setContent(TLSHelper.getInstance().getSDKVersion());
//        final LineControllerView log = (LineControllerView) findViewById(R.id.logLevel);
//        final TIMLogLevel[] logLevels = TIMLogLevel.values();
//        final String[] logNames = new String[logLevels.length];
//        for (int i = 0 ; i < logLevels.length ; ++i){
//            logNames[i] = logLevels[i].name();
//        }
//        log.setContent(TIMManager.getInstance().getLogLevel().name());
//        log.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new ListPickerDialog().show(logNames, getSupportFragmentManager(), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, final int which) {
//                        log.setContent(logNames[which]);
//                        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
//                        editor.putInt("loglvl", which);
//                        editor.apply();
//                    }
//                });
//            }
//        });

}
