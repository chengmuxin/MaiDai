package com.hooha.maidai.chat.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hooha.maidai.chat.R;

/**
 * 吐槽墙视频页
 */
public class TucaoWallFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_tucao_tab_wall, container, false);
        return view;
    }
}
