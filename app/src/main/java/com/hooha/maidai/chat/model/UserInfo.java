package com.hooha.maidai.chat.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户数据
 */
public class UserInfo {

    private String id;
    private String userSig;
    private boolean switch_btn = false;
    private String avatar;
    private String name;
    private String sex;
    private String age;
    private String hobby;
    private String topic;
    private String special;
    private String number;
    private String introduce;
    private String statement;
    private int isAttention = 0;
    private String best;
    private String maidaiNum;
    private List<UserInfo> info = new ArrayList<UserInfo>();

    public boolean getSwitch_btn() {
        return switch_btn;
    }

    public void setSwitch_btn(boolean switch_btn) {
        this.switch_btn = switch_btn;
    }

    private static UserInfo ourInstance = new UserInfo();

    public static UserInfo getInstance() {
        return ourInstance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserSig() {
        return userSig;
    }

    public void setUserSig(String userSig) {
        this.userSig = userSig;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isSwitch_btn() {
        return switch_btn;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getSpecial() {
        return special;
    }

    public void setSpecial(String special) {
        this.special = special;
    }

    public static UserInfo getOurInstance() {
        return ourInstance;
    }

    public static void setOurInstance(UserInfo ourInstance) {
        UserInfo.ourInstance = ourInstance;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public int getIsAttention() {
        return isAttention;
    }

    public void setIsAttention(int isAttention) {
        this.isAttention = isAttention;
    }

    public String getBest() {
        return best;
    }

    public void setBest(String best) {
        this.best = best;
    }

    public String getMaidaiNum() {
        return maidaiNum;
    }

    public void setMaidaiNum(String maidaiNum) {
        this.maidaiNum = maidaiNum;
    }

    public List<UserInfo> getInfo() {
        return info;
    }

    public void setInfo(List<UserInfo> info) {
        this.info = info;
    }
}