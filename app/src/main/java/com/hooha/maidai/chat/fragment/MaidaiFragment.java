package com.hooha.maidai.chat.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.activity.InfoActivity;
import com.hooha.maidai.chat.activity.PostActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 卖呆界面
 */
public class MaidaiFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = MaidaiFragment.class.getSimpleName();
    private View view;
    private ImageView infoImageView, post;//跳转到我的界面
    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private List<Fragment> mFragments;
    private TextView mTabAll, mTabVideo, mTabText, mTabImage, mTabWall;//tab标签

    public MaidaiFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_maidai, container, false);
            initViews();//初始化控件
            initEvents();//初始化事件
            initDatas();//初始化数据
            post = (ImageView) view.findViewById(R.id.post);
            infoImageView = (ImageView) view.findViewById(R.id.fragment_info);
            post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent postIntent = new Intent(getActivity(), PostActivity.class);
                    getActivity(). startActivity(postIntent);

                }
            });
            infoImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().startActivity(new Intent(getActivity(), InfoActivity.class));
                }
            });
        }
        return view;
    }

    private void initDatas() {
        mFragments = new ArrayList<>();
        mFragments.add(new TucaoAllFragment());
        mFragments.add(new TucaoVideoFragment());
        mFragments.add(new TucaoTextFragment());
        mFragments.add(new TucaoImageFragment());
//        mFragments.add(new TucaoWallFragment());

        mAdapter = new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }
        };
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            //页面滚动事件
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            //页面选中事件
            public void onPageSelected(int position) {
                mViewPager.setCurrentItem(position);
                resetImgs();
                selectTab(position);
            }

            @Override
            //页面滚动状态改变事件
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //设置tab点击事件
    private void initEvents() {
        mTabAll.setOnClickListener(this);
        mTabVideo.setOnClickListener(this);
        mTabText.setOnClickListener(this);
        mTabImage.setOnClickListener(this);
//        mTabWall.setOnClickListener(this);
    }

    //初始化控件
    private void initViews() {
        mViewPager = (ViewPager) view.findViewById(R.id.id_viewpager);
        mTabAll = (TextView) view.findViewById(R.id.id_tab_all);
        mTabVideo = (TextView) view.findViewById(R.id.id_tab_video);
        mTabText = (TextView) view.findViewById(R.id.id_tab_text);
        mTabImage = (TextView) view.findViewById(R.id.id_tab_image);
//        mTabWall = (TextView) view.findViewById(R.id.id_tab_wall);
    }

    @Override
    public void onClick(View v) {
        resetImgs();//设置按钮为灰色

        switch (v.getId()) {
            case R.id.id_tab_all:
                selectTab(0);
                break;
            case R.id.id_tab_video:
                selectTab(1);
                break;
            case R.id.id_tab_text:
                selectTab(2);
                break;
            case R.id.id_tab_image:
                selectTab(3);
                break;
            /*case R.id.id_tab_wall:
                selectTab(4);
                break;*/
        }
    }

    private void selectTab(int i) {
        switch (i) {
            case 0:
                mTabAll.setTextColor(Color.BLUE);
                break;
            case 1:
                mTabVideo.setTextColor(Color.BLUE);
                break;
            case 2:
                mTabText.setTextColor(Color.BLUE);
                break;
            case 3:
                mTabImage.setTextColor(Color.BLUE);
                break;
            /*case 4:
                mTabWall.setTextColor(Color.BLUE);
                break;*/
        }
        mViewPager.setCurrentItem(i);
    }

    private void resetImgs() {
        mTabAll.setTextColor(Color.BLACK);
        mTabVideo.setTextColor(Color.BLACK);
        mTabText.setTextColor(Color.BLACK);
        mTabImage.setTextColor(Color.BLACK);
//        mTabWall.setTextColor(Color.BLACK);
    }
}
