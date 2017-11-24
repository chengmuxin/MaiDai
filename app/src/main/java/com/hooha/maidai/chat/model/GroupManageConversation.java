package com.hooha.maidai.chat.model;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hooha.maidai.chat.MyApplication;
import com.hooha.maidai.chat.activity.GroupManageMessageActivity;
import com.tencent.TIMCallBack;
import com.tencent.TIMGroupPendencyItem;
import com.tencent.qcloud.presentation.presenter.GroupManagerPresenter;
import com.hooha.maidai.chat.R;

import java.util.Calendar;

/**
 * 群管理会话
 */
public class GroupManageConversation extends Conversation {

    private final String TAG = "GroupManageConversation";

    private TIMGroupPendencyItem lastMessage;

    private long unreadCount;


    private String avatar;

    public GroupManageConversation(TIMGroupPendencyItem message){
        lastMessage = message;
        name = MyApplication.getContext().getString(R.string.conversation_system_group);
    }


    /**
     * 获取最后一条消息的时间
     */
    @Override
    public long getLastMessageTime() {
        return lastMessage.getAddTime();
    }

    /**
     * 获取未读消息数量
     */
    @Override
    public long getUnreadNum() {
        return unreadCount;
    }

    /**
     * 将所有消息标记为已读
     */
    @Override
    public void readAllMessage() {
        //不能传入最后一条消息时间，由于消息时间戳的单位是秒
        GroupManagerPresenter.readGroupManageMessage(Calendar.getInstance().getTimeInMillis(), new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Log.i(TAG, "read all message error,code " + i);
            }

            @Override
            public void onSuccess() {
                Log.i(TAG, "read all message succeed");
            }
        });
    }

    /**
     * 获取头像
     */
    @Override
    public String getAvatar() {
        return avatar;
    }

    @Override
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    /**
     * 跳转到聊天界面或会话详情
     *
     * @param context 跳转上下文
     */
    @Override
    public void navToDetail(Context context) {
        readAllMessage();
        Intent intent = new Intent(context, GroupManageMessageActivity.class);
        context.startActivity(intent);
    }

    /**
     * 获取最后一条消息摘要
     */
    @Override
    public String getLastMessageSummary() {
        if (lastMessage == null) return "";
        String from = lastMessage.getFromUser();
        String to = lastMessage.getToUser();

        boolean isSelf = from.equals(UserInfo.getInstance().getId());
        switch (lastMessage.getPendencyType()){
            case INVITED_BY_OTHER:
                if (isSelf){
                    return MyApplication.getContext().getResources().getString(R.string.summary_me)+
                            MyApplication.getContext().getResources().getString(R.string.summary_group_invite)+
                            to+
                            MyApplication.getContext().getResources().getString(R.string.summary_group_add);
                }else{
                    if (to.equals(UserInfo.getInstance().getId())){
                        return from+
                                MyApplication.getContext().getResources().getString(R.string.summary_group_invite)+
                                MyApplication.getContext().getResources().getString(R.string.summary_me)+
                                MyApplication.getContext().getResources().getString(R.string.summary_group_add);
                    }else{
                        return from+
                                MyApplication.getContext().getResources().getString(R.string.summary_group_invite)+
                                to+
                                MyApplication.getContext().getResources().getString(R.string.summary_group_add);
                    }

                }
            case APPLY_BY_SELF:
                if (isSelf){
                    return MyApplication.getContext().getResources().getString(R.string.summary_me)+
                            MyApplication.getContext().getResources().getString(R.string.summary_group_apply)+
                            GroupInfo.getInstance().getGroupName(lastMessage.getGroupId());
                }else{
                    return from+MyApplication.getContext().getResources().getString(R.string.summary_group_apply)+GroupInfo.getInstance().getGroupName(lastMessage.getGroupId());
                }

            default:
                return "";
        }
    }


    /**
     * 设置最后一条消息
     */
    public void setLastMessage(TIMGroupPendencyItem message){
        lastMessage = message;
    }


    /**
     * 设置未读数量
     *
     * @param count 未读数量
     */
    public void setUnreadCount(long count){
        unreadCount = count;
    }


}
