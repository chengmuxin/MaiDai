package com.hooha.maidai.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.hooha.maidai.chat.adapters.ProfileSummaryAdapter;
import com.hooha.maidai.chat.model.ProfileSummary;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMValueCallBack;
import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.model.GroupMemberProfile;

import java.util.ArrayList;
import java.util.List;

public class GroupMemberActivity extends Activity implements TIMValueCallBack<List<TIMGroupMemberInfo>> {

    ProfileSummaryAdapter adapter;
    List<ProfileSummary> list = new ArrayList<>();
    private GridView gridView;
    String groupId, type;
    private final int MEM_REQ = 100;
    private int memIndex;
    private View footer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_member);
        groupId = getIntent().getStringExtra("id");
        type = getIntent().getStringExtra("type");
        gridView = (GridView) findViewById(R.id.list);
        footer = getLayoutInflater().inflate(R.layout.footer_view, null);


        ImageView addImageView= (ImageView) footer.findViewById(R.id.footer_view_ImageView);
        addImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GroupMemberActivity.this, "添加新好友", Toast.LENGTH_SHORT).show();
            }
        });


        adapter = new ProfileSummaryAdapter(this, R.layout.item_profile_summary, list);
        gridView.setAdapter(adapter);
        TIMGroupManager.getInstance().getGroupMembers(groupId, this);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                memIndex = position;
                Intent intent = new Intent(GroupMemberActivity.this, GroupMemberProfileActivity.class);
                GroupMemberProfile profile = (GroupMemberProfile) list.get(position);
                intent.putExtra("data", profile);
                intent.putExtra("groupId", groupId);
                intent.putExtra("type", type);
                startActivityForResult(intent, MEM_REQ);
            }
        });
    }


    @Override
    public void onError(int i, String s) {

    }

    @Override
    public void onSuccess(List<TIMGroupMemberInfo> timGroupMemberInfos) {
        list.clear();
        if (timGroupMemberInfos == null) return;
        for (TIMGroupMemberInfo item : timGroupMemberInfos) {
            list.add(new GroupMemberProfile(item));
        }
        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (MEM_REQ == requestCode) {
            if (resultCode == RESULT_OK) {
                boolean isKick = data.getBooleanExtra("isKick", false);
                if (isKick) {
                    list.remove(memIndex);
                    adapter.notifyDataSetChanged();
                } else {
                    GroupMemberProfile profile = (GroupMemberProfile) data.getSerializableExtra("data");
                    if (memIndex < list.size() && list.get(memIndex).getIdentify().equals(profile.getIdentify())) {
                        GroupMemberProfile mMemberProfile = (GroupMemberProfile) list.get(memIndex);
                        mMemberProfile.setRoleType(profile.getRole());
                        mMemberProfile.setQuietTime(profile.getQuietTime());
                        mMemberProfile.setName(profile.getNameCard());
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }


}
