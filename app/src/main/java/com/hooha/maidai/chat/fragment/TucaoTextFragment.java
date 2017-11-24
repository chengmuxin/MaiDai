package com.hooha.maidai.chat.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.presenters.TucaoHelper;

/**
 * 吐槽墙段子页
 */
public class TucaoTextFragment extends Fragment {
    private static String TYPE = "段子";
    private PullToRefreshListView mListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_tucao_tab_text, container, false);
        mListView = (PullToRefreshListView) view.findViewById(R.id.lv_tucao_text);
        new TucaoHelper(mListView, getActivity(), TYPE, 0).start();
        return view;
    }

}
