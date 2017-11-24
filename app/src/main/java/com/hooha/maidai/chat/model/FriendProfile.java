package com.hooha.maidai.chat.model;

import android.content.Context;
import android.content.Intent;

import com.tencent.TIMUserProfile;
import com.hooha.maidai.chat.MyApplication;
import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.activity.AddFriendActivity;
import com.hooha.maidai.chat.activity.ProfileActivity;

/**
 * 好友资料
 */
public class FriendProfile implements ProfileSummary {


    private TIMUserProfile profile;
    private boolean isSelected;
    private static FriendProfile instance;

    public FriendProfile(TIMUserProfile profile) {
        this.profile = profile;
    }

    public synchronized static FriendProfile getInstance() {

        return instance;
    }

    /**
     * 获取头像资源
     */
    @Override
    public int getAvatarRes() {
        return R.drawable.head_other;
    }

    /**
     * 获取头像地址
     */
    @Override
    public String getAvatarUrl() {
        if (!profile.getFaceUrl().equals("")) {
            profile.getFaceUrl(); return profile.getFaceUrl();

        }
       return null;
    }

    /**
     * 获取名字
     */
    @Override
    public String getName() {
        if (!profile.getRemark().equals("")) {
            return profile.getRemark();
        } else if (!profile.getNickName().equals("")) {
            return profile.getNickName();
        }
        return profile.getIdentifier();
    }

    /**
     * 获取描述信息
     */
    @Override
    public String getDescription() {
        return null;
    }

    /**
     * 显示详情
     *
     * @param context 上下文
     */
    @Override
    public void onClick(Context context) {
        if (FriendshipInfo.getInstance().isFriend(profile.getIdentifier())) {
            ProfileActivity.navToProfile(context, profile.getIdentifier());
        } else {
            Intent person = new Intent(context, AddFriendActivity.class);
            person.putExtra("id", profile.getIdentifier());
            person.putExtra("name", getName());
            context.startActivity(person);
        }
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    /**
     * 获取用户ID
     */
    @Override
    public String getIdentify() {
        return profile.getIdentifier();
    }


    /**
     * 获取用户备注名
     */
    public String getRemark() {
        return profile.getRemark();
    }


    /**
     * 获取好友分组
     */
    public String getGroupName() {
        if (profile.getFriendGroups().size() == 0) {
            return MyApplication.getContext().getString(R.string.default_group_name);
        } else {
            return profile.getFriendGroups().get(0);
        }
    }

}