package com.hooha.maidai.chat.model;

import android.content.Context;
import android.content.Intent;

import com.hooha.maidai.chat.MyApplication;
import com.hooha.maidai.chat.activity.FriendshipManageMessageActivity;
import com.tencent.TIMFriendFutureItem;
import com.hooha.maidai.chat.R;

/**
 * 新朋友会话
 */
public class FriendshipConversation extends Conversation {

    private TIMFriendFutureItem lastMessage;

    private long unreadCount;


    private String avatar;
    public FriendshipConversation(TIMFriendFutureItem message){
        lastMessage = message;
        name = MyApplication.getContext().getString(R.string.conversation_system_friend);
    }


    /**
     * 获取最后一条消息的时间
     */
    @Override
    public long getLastMessageTime() {
        if (lastMessage == null) return 0;
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
        Intent intent = new Intent(context, FriendshipManageMessageActivity.class);
        context.startActivity(intent);
    }

    /**
     * 获取最后一条消息摘要
     */
    @Override
    public String getLastMessageSummary() {
        if (lastMessage == null) return "";
        String name = lastMessage.getProfile().getNickName();
        if (name.equals("")) name = lastMessage.getIdentifier();
        switch (lastMessage.getType()){
            case TIM_FUTURE_FRIEND_PENDENCY_IN_TYPE://我收到的好友申请的未决消息
                return name + MyApplication.getContext().getString(R.string.summary_friend_add);
            case TIM_FUTURE_FRIEND_PENDENCY_OUT_TYPE://我发出的好友申请的未决消息
                return MyApplication.getContext().getString(R.string.summary_me) + MyApplication.getContext().getString(R.string.summary_friend_add_me) + name;
            case TIM_FUTURE_FRIEND_DECIDE_TYPE://已决消息
                return MyApplication.getContext().getString(R.string.summary_friend_added) + name;
            case TIM_FUTURE_FRIEND_RECOMMEND_TYPE://好友推荐
                return MyApplication.getContext().getString(R.string.summary_friend_recommend) + name;
            default:
                return "";
        }
    }


    /**
     * 设置最后一条消息
     */
    public void setLastMessage(TIMFriendFutureItem message){
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
