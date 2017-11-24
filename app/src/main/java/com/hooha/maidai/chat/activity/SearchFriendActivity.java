package com.hooha.maidai.chat.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.hooha.maidai.chat.adapters.ProfileSummaryAdapter;
import com.hooha.maidai.chat.model.FriendProfile;
import com.hooha.maidai.chat.model.ProfileSummary;
import com.tencent.TIMUserProfile;
import com.tencent.qcloud.presentation.presenter.FriendshipManagerPresenter;
import com.tencent.qcloud.presentation.viewfeatures.FriendInfoView;
import com.hooha.maidai.chat.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 查找添加新朋友
 */
public class SearchFriendActivity extends Activity implements FriendInfoView, AdapterView.OnItemClickListener, View.OnKeyListener {

    private final static String TAG = "SearchFriendActivity";

    private FriendshipManagerPresenter presenter;
    ListView mSearchList;
    ProfileSummaryAdapter adapter;
    List<ProfileSummary> list = new ArrayList<>();
    EditText mSearchInput;
    TextView tvNoResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnew);
        mSearchInput = (EditText) findViewById(R.id.inputSearch);
        tvNoResult = (TextView) findViewById(R.id.noResult);
        mSearchList =(ListView) findViewById(R.id.list);
        adapter = new ProfileSummaryAdapter(this, R.layout.item_profile_summary, list);
        mSearchList.setAdapter(adapter);
        mSearchList.setOnItemClickListener(this);
        presenter = new FriendshipManagerPresenter(this);
        TextView tvCancel = (TextView) findViewById(R.id.cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSearchInput.setOnKeyListener(this);
    }



    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        list.get(i).onClick(this);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_UP){   // 忽略其它事件
            return false;
        }

        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
                list.clear();
                adapter.notifyDataSetChanged();
                String key = mSearchInput.getText().toString();
                if (key.equals("")) return true;
                presenter.searchFriendByName(key,true);
                //给手机号加上86-
                if (maybePhone(key)){
                    key = "86-" + key;
                }
                presenter.searchFriendById(key);
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    /**
     * 显示好友信息
     *
     * @param users 好友资料列表
     */
    @Override
    public void showUserInfo(List<TIMUserProfile> users) {
        if (users == null) return;
        for (TIMUserProfile item : users){
            if (needAdd(item.getIdentifier()))
                list.add(new FriendProfile(item));
        }
        adapter.notifyDataSetChanged();
        if (list.size() == 0){
            tvNoResult.setVisibility(View.VISIBLE);
        }else{
            tvNoResult.setVisibility(View.GONE);
        }
    }

    private boolean needAdd(String id){
        for (ProfileSummary item : list){
            if (item.getIdentify().equals(id)) return false;
        }
        return true;
    }

    private boolean maybePhone(String str){
        if (str.length() != 11) return false;
        for (int i = 0 ; i < str.length() ; ++i){
            if(!Character.isDigit(str.charAt(i))) return false;
        }
        return true;
    }

}
