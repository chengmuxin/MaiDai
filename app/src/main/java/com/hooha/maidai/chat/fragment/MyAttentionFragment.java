package com.hooha.maidai.chat.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.adapters.MyFollowAdapter;
import com.hooha.maidai.chat.model.Fans;
import com.hooha.maidai.chat.model.UserInfo;
import com.hooha.maidai.chat.presenters.OKhttpHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MG on 2016/7/20.
 */
public class MyAttentionFragment extends Fragment {

    private RecyclerView recyclerView;
    private View view;
    private List<Fans> myAttentionList;

    public static MyAttentionFragment newInstance(String keyWord) {
        MyAttentionFragment fragment = new MyAttentionFragment();
        Bundle args = new Bundle();
        args.putString("keyWord", keyWord);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_my_follow, container, false);
            initData();
//            initView();
//            initList();
        }
        return view;
    }

    private void initView() {
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_my_follow_RecyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        MyFollowAdapter adapter = new MyFollowAdapter(myAttentionList, getActivity(), "MyAttentionFragment");
        recyclerView.setAdapter(adapter);
    }

    private void initData() {
        myAttentionList = new ArrayList<>();
        new AsyncTask<Void, Void, ArrayList<Fans>>() {
            @Override
            protected ArrayList<Fans> doInBackground(Void... params) {
                return OKhttpHelper.getInstance().getAttention(UserInfo.getInstance().getId());
            }

            @Override
            protected void onPostExecute(ArrayList<Fans> fanses) {
                super.onPostExecute(fanses);
                if (fanses != null) {
                    myAttentionList.addAll(fanses);
                    initView();
                }
            }
        }.execute();
    }
}
