package com.hooha.maidai.chat.presenters.viewinface;


import com.hooha.maidai.chat.model.MemberInfo;

import java.util.ArrayList;


/**
 * 成员列表回调
 */
public interface MembersDialogView extends MvpView {

    void showMembersList(ArrayList<MemberInfo> data);

}
