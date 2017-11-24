package com.hooha.maidai.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.hooha.maidai.chat.MyApplication;
import com.hooha.maidai.chat.adapters.ProfileSummaryAdapter;
import com.hooha.maidai.chat.model.GroupProfile;
import com.hooha.maidai.chat.model.ProfileSummary;
import com.tencent.TIMGroupCacheInfo;
import com.tencent.qcloud.presentation.event.GroupEvent;
import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.model.GroupInfo;
import com.hooha.maidai.chat.view.TemplateTitle;

import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class GroupListActivity extends Activity implements Observer {

    private ProfileSummaryAdapter adapter;
    private ListView listView;
    private String type;

    TextView group_num,jiejiuwo_text;
    private List<ProfileSummary> list;
    private final int CREATE_GROUP_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        type = getIntent().getStringExtra("type");
        setTitle();
        listView =(ListView) findViewById(R.id.list);
        list = GroupInfo.getInstance().getGroupListByType(type);
        adapter = new ProfileSummaryAdapter(this, R.layout.item_profile_summary, list);
        listView.setAdapter(adapter);
        group_num= new TextView(this);
        group_num.setTextSize(16);
        //  jiejiuwo_text=new TextView(this);
        group_num.setGravity(CONTEXT_INCLUDE_CODE);
        if (type.equals(GroupInfo.chatRoom)){
            group_num.setVisibility(group_num.GONE);
            //    listView.addFooterView(jiejiuwo_text);

            MyApplication.getContext().getString(R.string.jiejiuwo_text);
        }
        else if (type.equals(GroupInfo.publicGroup)){
            group_num.setText( adapter.getCount() + "个群组");

        }
        listView.addFooterView(group_num);
        group_num.setClickable(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                list.get(position).onClick(GroupListActivity.this);
            }
        });
        TemplateTitle title = (TemplateTitle) findViewById(R.id.groupListTitle);
        if (type.equals(GroupInfo.chatRoom)){
            title.setVisibility(title.GONE);

            title.setMoreTextAction(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(GroupListActivity.this, CreateGroupActivity.class);
                    intent.putExtra("type", type);
                    startActivityForResult(intent, CREATE_GROUP_CODE);
                }
            });
            GroupEvent.getInstance().addObserver(this);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        GroupEvent.getInstance().deleteObserver(this);
    }

    private void setTitle(){
        TemplateTitle title = (TemplateTitle) findViewById(R.id.groupListTitle);
        title.setTitleText(GroupInfo.getTypeName(type));
        title.setMoreImgAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupListActivity.this, CreateGroupActivity.class);
                intent.putExtra("type", type);
                startActivity(intent);
            }
        });
    }


    /**
     * This method is called if the specified {@code Observable} object's
     * {@code notifyObservers} method is called (because the {@code Observable}
     * object has been updated.
     *
     * @param observable the {@link Observable} object.
     * @param data       the data passed to {@link Observable#notifyObservers(Object)}.
     */
    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof GroupEvent){
            if (data instanceof GroupEvent.NotifyCmd){
                GroupEvent.NotifyCmd cmd = (GroupEvent.NotifyCmd) data;
                switch (cmd.type){
                    case DEL:
                        delGroup((String) cmd.data);
                        break;
                    case ADD:
                        addGroup((TIMGroupCacheInfo) cmd.data);
                        break;
                    case UPDATE:
                        updateGroup((TIMGroupCacheInfo) cmd.data);
                        break;
                }
            }
        }
    }

    private void delGroup(String groupId){
        Iterator<ProfileSummary> it = list.iterator();
        while (it.hasNext()){
            ProfileSummary item = it.next();
            if (item.getIdentify().equals(groupId)){
                it.remove();
                adapter.notifyDataSetChanged();
                return;
            }
        }
    }


    private void addGroup(TIMGroupCacheInfo info){
        if (info!=null && info.getGroupInfo().getGroupType().equals(type)){
            GroupProfile profile = new GroupProfile(info);
            list.add(profile);
            adapter.notifyDataSetChanged();
        }

    }

    private void updateGroup(TIMGroupCacheInfo info){
        delGroup(info.getGroupInfo().getGroupId());
        addGroup(info);
    }
}
