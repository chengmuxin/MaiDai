package com.hooha.maidai.chat.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.hooha.maidai.chat.utils.Constant;
import com.hooha.maidai.chat.utils.SxbLog;

/**
 * 自己的状态数据
 */
public class MySelfInfo {
    private static final String TAG = MySelfInfo.class.getSimpleName();
    private String id;
    private String userSig;
    private String nickName;    // 呢称
    private String avatar;      // 头像地址
    private String sign;      // 签名
    private String CosSig;
    private int roomType;
    private static boolean isCreateRoom = false;
    private int login = 0;

    private boolean bLiveAnimator;  // 渐隐动画
    private SxbLog.SxbLogLevel logLevel;           // 日志等级


    private int id_status;

    private int myRoomNum = -1;

    private static MySelfInfo ourInstance = new MySelfInfo();

    public static MySelfInfo getInstance() {

        return ourInstance;
    }

    public int getLogin() {
        return login;
    }

    public void setLogin(int login) {
        this.login = login;
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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getMyRoomNum() {
        return myRoomNum;
    }

    public void setMyRoomNum(int myRoomNum) {
        this.myRoomNum = myRoomNum;
    }

    public String getCosSig() {
        return CosSig;
    }

    public void setCosSig(String cosSig) {
        CosSig = cosSig;
    }

    public boolean isbLiveAnimator() {
        return bLiveAnimator;
    }

    public void setbLiveAnimator(boolean bLiveAnimator) {
        this.bLiveAnimator = bLiveAnimator;
    }

    public SxbLog.SxbLogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(SxbLog.SxbLogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public int getRoomType() {
        return roomType;
    }

    public void setRoomType(int roomType) {
        this.roomType = roomType;
    }

    public void writeToCache(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constant.USER_INFO, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constant.USER_ID, id);
        editor.putString(Constant.USER_SIG, userSig);
        editor.putString(Constant.USER_NICK, nickName);
        editor.putString(Constant.USER_AVATAR, avatar);
        editor.putString(Constant.USER_SIGN, sign);
        editor.putInt(Constant.USER_ROOM_NUM, myRoomNum);
        editor.putBoolean(Constant.LIVE_ANIMATOR, bLiveAnimator);
        editor.putInt(Constant.LOG_LEVEL, logLevel.ordinal());
        editor.commit();
    }

    public void clearCache(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constant.USER_INFO, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();
    }

    public void getCache(Context context) {
        SharedPreferences sharedata = context.getSharedPreferences(Constant.USER_INFO, 0);
        id = sharedata.getString(Constant.USER_ID, null);
        userSig = sharedata.getString(Constant.USER_SIG, null);
        myRoomNum = sharedata.getInt(Constant.USER_ROOM_NUM, -1);
        nickName = sharedata.getString(Constant.USER_NICK, null);
        avatar = sharedata.getString(Constant.USER_AVATAR, null);
        sign = sharedata.getString(Constant.USER_SIGN, null);
        bLiveAnimator = sharedata.getBoolean(Constant.LIVE_ANIMATOR, false);
        int level = sharedata.getInt(Constant.LOG_LEVEL, SxbLog.SxbLogLevel.INFO.ordinal());
        if (level < SxbLog.SxbLogLevel.OFF.ordinal() || level > SxbLog.SxbLogLevel.INFO.ordinal()) {
            logLevel = SxbLog.SxbLogLevel.INFO;
        } else {
            logLevel = SxbLog.SxbLogLevel.values()[level];
        }
        SxbLog.setLogLevel(logLevel);
        SxbLog.i(TAG, " getCache id: " + id);
    }

    public int getIdStatus() {
        return id_status;
    }

    public void setIdStatus(int id_status) {
        this.id_status = id_status;
    }

    public boolean isCreateRoom() {
        return isCreateRoom;
    }

    public void setJoinRoomWay(boolean isCreateRoom) {
        this.isCreateRoom = isCreateRoom;
    }
}