package com.hooha.maidai.chat.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.model.UserInfo;
import com.hooha.maidai.chat.presenters.OKhttpHelper;

/**
 * Created by MG on 2016/11/1.
 */
public class FeedbackActivity extends Activity {
    private EditText editText;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        editText = (EditText) findViewById(R.id.et_feedback);
        button = (Button) findViewById(R.id.btn_feedback);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String opinion = editText.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OKhttpHelper.getInstance().feedback(UserInfo.getInstance().getId(), opinion);
                    }
                }).start();
                editText.setText("");
                finish();
            }
        });
    }
}
