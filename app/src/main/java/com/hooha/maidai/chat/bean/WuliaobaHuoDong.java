package com.hooha.maidai.chat.bean;

/**
 * Created by Public on 2016/7/28.
 */
public class WuliaobaHuoDong {
    private String peopleCount;//人数
    private String liveness;//热度
    private String activeId;
    private String activeInfo;
    private String detail;
    private String type;//类别
    private String picture;

    public String getPeopleCount() {
        return peopleCount;
    }

    public void setPeopleCount(String peopleCount) {
        this.peopleCount = peopleCount;
    }

    public String getLiveness() {
        return liveness;
    }

    public void setLiveness(String liveness) {
        this.liveness = liveness;
    }

    public String getActiveId() {
        return activeId;
    }

    public void setActiveId(String activeId) {
        this.activeId = activeId;
    }

    public String getActiveInfo() {
        return activeInfo;
    }

    public void setActiveInfo(String activeInfo) {
        this.activeInfo = activeInfo;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
