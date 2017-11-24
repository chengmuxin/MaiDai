package com.hooha.maidai.chat.model;

import com.tencent.TIMFriendFutureItem;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMFutureFriendType;

/**
 * 好友关系链消息的界面绑定数据
 * 可用于本地操作后界面修改
 */
public class FriendFuture {
    TIMFriendshipManager s;
    TIMFriendFutureItem futureItem;

    private TIMFutureFriendType type;

    public FriendFuture(TIMFriendFutureItem item){
        futureItem = item;
        type = futureItem.getType();
    }


    public TIMFutureFriendType getType() {
        return type;
    }

    public void setType(TIMFutureFriendType type) {
        this.type = type;
    }

    public String getName(){
        return futureItem.getProfile().getNickName().equals("") ? futureItem.getIdentifier() : futureItem.getProfile().getNickName();
    }

    public String getMessage(){
        return futureItem.getAddWording();
    }


    public String getIdentify(){
        return futureItem.getIdentifier();
    }



}
