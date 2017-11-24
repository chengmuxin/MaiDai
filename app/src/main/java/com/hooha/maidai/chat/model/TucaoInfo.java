package com.hooha.maidai.chat.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MG on 2016/10/18.
 */
public class TucaoInfo {
    private String useravatar;
    private String username;
    private int userattention = 0;
    private int userfans = 0;
    private int usernoteCount = 0;
    private int userShareCount = 0;
    private int userCommentCount = 0;
    private List<Tucao> info = new ArrayList<Tucao>();

    public String getUseravatar() {
        return useravatar;
    }

    public void setUseravatar(String useravatar) {
        this.useravatar = useravatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getUserattention() {
        return userattention;
    }

    public void setUserattention(int userattention) {
        this.userattention = userattention;
    }

    public int getUserfans() {
        return userfans;
    }

    public void setUserfans(int userfans) {
        this.userfans = userfans;
    }

    public int getUsernoteCount() {
        return usernoteCount;
    }

    public void setUsernoteCount(int usernoteCount) {
        this.usernoteCount = usernoteCount;
    }

    public int getUserShareCount() {
        return userShareCount;
    }

    public void setUserShareCount(int userShareCount) {
        this.userShareCount = userShareCount;
    }

    public int getUserCommentCount() {
        return userCommentCount;
    }

    public void setUserCommentCount(int userCommentCount) {
        this.userCommentCount = userCommentCount;
    }

    public List<Tucao> getInfo() {
        return info;
    }

    public void setInfo(List<Tucao> info) {
        this.info = info;
    }
}
