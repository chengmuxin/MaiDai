package com.hooha.maidai.chat.model;

/**
 * Created by MG on 2016/11/4.
 */
public class Fans {
    private int hisFeel = 0;
    private int myFeel = 0;
    private String name;
    private int sendGifts = 0;
    private int reciveGifts = 0;
    private String picture;
    private String attentedId;
    private String time;

    public int getHisFeel() {
        return hisFeel;
    }

    public void setHisFeel(int hisFeel) {
        this.hisFeel = hisFeel;
    }

    public int getMyFeel() {
        return myFeel;
    }

    public void setMyFeel(int myFeel) {
        this.myFeel = myFeel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSendGifts() {
        return sendGifts;
    }

    public void setSendGifts(int sendGifts) {
        this.sendGifts = sendGifts;
    }

    public int getReciveGifts() {
        return reciveGifts;
    }

    public void setReciveGifts(int reciveGifts) {
        this.reciveGifts = reciveGifts;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getAttentedId() {
        return attentedId;
    }

    public void setAttentedId(String attentedId) {
        this.attentedId = attentedId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
