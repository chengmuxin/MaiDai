package com.hooha.maidai.chat.model;

/**
 * Created by MG on 2016/10/13.
 */
public class Comment {
    private String commentId;
    private String commentName;
    private String commentAvatar;
    private String replyId;
    private String replyName;
    private String commentInfo;
    private int commentAdmire;

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getCommentName() {
        return commentName;
    }

    public void setCommentName(String commentName) {
        this.commentName = commentName;
    }

    public String getCommentAvatar() {
        return commentAvatar;
    }

    public void setCommentAvatar(String commentAvatar) {
        this.commentAvatar = commentAvatar;
    }

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public String getReplyName() {
        return replyName;
    }

    public void setReplyName(String replyName) {
        this.replyName = replyName;
    }

    public String getCommentInfo() {
        return commentInfo;
    }

    public void setCommentInfo(String commentInfo) {
        this.commentInfo = commentInfo;
    }

    public int getCommentAdmire() {
        return commentAdmire;
    }

    public void setCommentAdmire(int commentAdmire) {
        this.commentAdmire = commentAdmire;
    }
}
