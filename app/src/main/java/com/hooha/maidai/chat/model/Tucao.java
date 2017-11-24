package com.hooha.maidai.chat.model;

import java.io.Serializable;

/**
 * Created by MG on 2016/9/13.
 */
public class Tucao implements Serializable {
    private String noteId;
    private String uid;
    private String username;
    private String avatar;
    private String time;
    private String noteInfo;
    private String picture;
    private String pictureWidth;
    private String pictureHight;
    private String video;
    private String label;
    private String type;
    private int noteAdmire;
    private int noteBad;
    private int noteShare;
    private int noteComment;
    private int isAdmire;
    private int isBad;

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNoteInfo() {
        return noteInfo;
    }

    public void setNoteInfo(String noteInfo) {
        this.noteInfo = noteInfo;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getPictureWidth() {
        return pictureWidth;
    }

    public void setPictureWidth(String pictureWidth) {
        this.pictureWidth = pictureWidth;
    }

    public String getPictureHight() {
        return pictureHight;
    }

    public void setPictureHight(String pictureHight) {
        this.pictureHight = pictureHight;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getNoteAdmire() {
        return noteAdmire;
    }

    public void setNoteAdmire(int noteAdmire) {
        this.noteAdmire = noteAdmire;
    }

    public int getNoteBad() {
        return noteBad;
    }

    public void setNoteBad(int noteBad) {
        this.noteBad = noteBad;
    }

    public int getNoteShare() {
        return noteShare;
    }

    public void setNoteShare(int noteShare) {
        this.noteShare = noteShare;
    }

    public int getNoteComment() {
        return noteComment;
    }

    public void setNoteComment(int noteComment) {
        this.noteComment = noteComment;
    }

    public int getIsAdmire() {
        return isAdmire;
    }

    public void setIsAdmire(int isAdmire) {
        this.isAdmire = isAdmire;
    }

    public int getIsBad() {
        return isBad;
    }

    public void setIsBad(int isBad) {
        this.isBad = isBad;
    }
}
