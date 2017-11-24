package com.hooha.maidai.chat.presenters.viewinface;


import com.hooha.maidai.chat.model.LiveInfoJson;

import java.util.ArrayList;


/**
 *  列表页面回调
 */
public interface LiveListView extends MvpView{

    void showFirstPage(ArrayList<LiveInfoJson> livelist);
}
