package com.hooha.maidai.chat.utils;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;

import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.adapters.ChatAdapter;
import com.hooha.maidai.chat.model.Message;
import com.tencent.TIMMessage;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 邱世君
 * 2015/11/19.
 * SendTime 
 * 计时器
 */
public class SendTime extends CountDownTimer {
    private List<Message> messageList ;
    public TIMMessage message     ;
    private ChatAdapter adapter;

    public SendTime(long millisInFuture, long countDownInterval, List<Message> messageList, TIMMessage message, ChatAdapter adapter ) {
        super(millisInFuture, countDownInterval);
        this.messageList = messageList;
        this.message = message;
        this.adapter = adapter;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        Log.e("onTick","onTick");

    }

    @Override
    public void onFinish() {
        Log.e("onFinish","onFinish");

        message.remove();
        for (int i = 0; i < messageList.size(); i++) {
            Log.e("getMsgUniqueId",messageList.get(i).getMessage().getMsgUniqueId()+"");
            if (messageList.get(i).getMessage().getMsgId().equals(message.getMsgId())) {
                messageList.remove(i);
                break;
            }
        }
        adapter.notifyData();

    }

}
