package com.hooha.maidai.chat.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.adapters.ActionTabLayoutAdapter;
import com.hooha.maidai.chat.base.BaseActivity;
import com.hooha.maidai.chat.fragment.MyAttentionFragment;
import com.hooha.maidai.chat.fragment.MyFansFragment;
import com.hooha.maidai.chat.fragment.MyFollowFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MG on 2016/7/20.
 */
public class MyFollowActivity extends BaseActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<String> titleList;
    private List<Fragment> fragmentList;
    private MyFollowFragment myFollowFragment;
    private MyAttentionFragment myAttentionFragment;
    private MyFansFragment myFansFragment;
    private List<String> title;
    private FragmentStatePagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_follow);
        setTitle("我的关注");

        initView();

    }


    private void initView() {
        viewPager= (ViewPager) findViewById(R.id.fragment_information_ViewPager);
        tabLayout = (TabLayout) findViewById(R.id.fragment_information_tabLayout);
        titleList = new ArrayList<>();
        titleList.add("关注列表");
        titleList.add("粉丝");
        titleList.add("礼物排行");

        fragmentList=new ArrayList<>();
        title=new ArrayList<>();

        myAttentionFragment = MyAttentionFragment.newInstance(titleList.get(0));//把标题传人给fragment，作为关键词搜索相应文章内容
        fragmentList.add(myAttentionFragment);
        myFansFragment = MyFansFragment.newInstance(titleList.get(1));//把标题传人给fragment，作为关键词搜索相应文章内容
        fragmentList.add(myFansFragment);
        myFollowFragment = MyFollowFragment.newInstance(titleList.get(2));//把标题传人给fragment，作为关键词搜索相应文章内容
        fragmentList.add(myFollowFragment);
        for (int i = 0; i <titleList.size() ; i++) {
            title.add(titleList.get(0));
            tabLayout.addTab(tabLayout.newTab().setText(title.get(0)));
        }

        adapter = new ActionTabLayoutAdapter(this.getSupportFragmentManager(), fragmentList, title);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(adapter);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);//设置指示器均分
    }
}
