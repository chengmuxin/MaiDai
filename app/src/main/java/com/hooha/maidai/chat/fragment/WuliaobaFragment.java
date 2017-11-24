package com.hooha.maidai.chat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.activity.MyActivity;
import com.hooha.maidai.chat.adapters.WuliaobaAdapter;
import com.hooha.maidai.chat.bean.WuliaobaHuoDong;
import com.hooha.maidai.chat.presenters.OKhttpHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Public on 2016/7/28.
 * 无聊吧
 */
public class WuliaobaFragment extends Fragment {

    private View view;
    private RecyclerView mRecyclerView;//直播列表
    private List<WuliaobaHuoDong> list = new ArrayList<WuliaobaHuoDong>();
    private ImageView myImageView;//跳转到我的界面

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_wuliaoba, container, false);
            initData();
            initView();
            myImageView = (ImageView) view.findViewById(R.id.fragment_my);
            myImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().startActivity(new Intent(getActivity(), MyActivity.class));
                }
            });
        }
        return view;
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (OKhttpHelper.getInstance().getActivityList() != null) {
                    list.addAll(OKhttpHelper.getInstance().getActivityList());
                }
            }
        }).start();
    }

    private void initView() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mRecyclerView = (RecyclerView) view.findViewById(R.id.wuliaoba_rv);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(manager);
        WuliaobaAdapter adapter = new WuliaobaAdapter(list, getActivity());
        mRecyclerView.setAdapter(adapter);
    }
}
