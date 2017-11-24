package com.hooha.maidai.chat.view;

/**
 * Created by MG on 2016/10/13.
 */
public class conversat {
    public String identify;
    public String avatar;



    public String LastMessageSummary;
    public String nickname;
    /**
     * 获取最后一条消息的时间
     */
   public long LastMessageTime;

    /**
     * 获取未读消息数量
     */
  public long UnreadNum;

    public String getLastMessageSummary() {
        return LastMessageSummary;
    }

    public void setLastMessageSummary(String lastMessageSummary) {
        LastMessageSummary = lastMessageSummary;
    }

    public String getIdentify() {
        return identify;
    }

    public void setIdentify(String identify) {
        this.identify = identify;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public long getLastMessageTime() {
        return LastMessageTime;
    }

    public void setLastMessageTime(long lastMessageTime) {
        LastMessageTime = lastMessageTime;
    }

    public long getUnreadNum() {
        return UnreadNum;
    }

    public void setUnreadNum(long unreadNum) {
        UnreadNum = unreadNum;
    }


}
