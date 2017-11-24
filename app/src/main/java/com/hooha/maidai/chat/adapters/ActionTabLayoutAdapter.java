package com.hooha.maidai.chat.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * 创建人：邱世君
 * 创建日期：2016/1/1
 * 创建时间：16:59
 */
public class ActionTabLayoutAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragments;                         //fragment列表
    private List<String> title;                              //tab名的列表

    public ActionTabLayoutAdapter(FragmentManager fm, List<Fragment> fragments, List<String> title) {
        super(fm);
        this.fragments=fragments;
        this.title=title;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return title.size();
    }

    //此方法用来显示tab上的名字
    @Override
    public CharSequence getPageTitle(int position) {
        //    return  title.get(position);
        //这么写更流畅
        return title.get(position % title.size());
    }

}
