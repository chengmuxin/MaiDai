package com.hooha.maidai.chat.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.model.UserInfo;

/**
 * Created by gaom on 2016/5/30.
 * 免责声明
 */
public class DeclareActivity extends Activity {

    private TextView textView;
    ImageView head_back;
//免责声明中的返回图标
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_declare);
        setTitle("免责声明");
        head_back = (ImageView) findViewById(R.id.head_back);
        head_back.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View view) {
        finish();
        }
        });
        textView = (TextView) findViewById(R.id.alignTextViewTest);
        textView.setText(UserInfo.getInstance().getStatement());
    }
}
