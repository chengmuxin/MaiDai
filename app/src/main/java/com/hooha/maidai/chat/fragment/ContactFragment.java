package com.hooha.maidai.chat.fragment;


import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.activity.ChatActivity;
import com.hooha.maidai.chat.activity.FriendshipManageMessageActivity;
import com.hooha.maidai.chat.activity.GroupListActivity;
import com.hooha.maidai.chat.activity.ManageFriendGroupActivity;
import com.hooha.maidai.chat.activity.MyActivity;
import com.hooha.maidai.chat.activity.SearchAddFriendActivity;
import com.hooha.maidai.chat.activity.SearchFriendActivity;
import com.hooha.maidai.chat.activity.SearchGroupActivity;
import com.hooha.maidai.chat.adapters.ExpandGroupListAdapter;
import com.hooha.maidai.chat.model.FriendProfile;
import com.hooha.maidai.chat.model.FriendshipInfo;
import com.hooha.maidai.chat.model.GroupInfo;
import com.hooha.maidai.chat.model.MySelfInfo;
import com.hooha.maidai.chat.model.UserInfo;
import com.hooha.maidai.chat.model.otherInfo;
import com.hooha.maidai.chat.presenters.OKhttpHelper;
import com.hooha.maidai.chat.view.TemplateTitle;
import com.tencent.TIMConversationType;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 联系人界面
 */
public class ContactFragment extends Fragment implements View.OnClickListener {
    private String Uid;
    private View view;
    private ExpandGroupListAdapter mGroupListAdapter, goodListAdapter;
    private ExpandableListView mGroupListView, goodfriListView;
    private LinearLayout mNewFriBtn, mPublicGroupBtn, mChatRoomBtn, mPrivateGroupBtn;
    private ImageView myImageView;//跳转到我的界面
    private List<FriendProfile> friendProfileList;
    Map<String, List<FriendProfile>> friends;
    private String TAG = "ContactFragment联系人界面";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_contact, container, false);
            mGroupListView = (ExpandableListView) view.findViewById(R.id.groupList);//联系人列表


            mNewFriBtn = (LinearLayout) view.findViewById(R.id.btnNewFriend);
            mNewFriBtn.setOnClickListener(this);
            mPublicGroupBtn = (LinearLayout) view.findViewById(R.id.btnPublicGroup);
            mPublicGroupBtn.setOnClickListener(this);
            mChatRoomBtn = (LinearLayout) view.findViewById(R.id.btnChatroom);//解救我
            mChatRoomBtn.setOnClickListener(this);
            mPrivateGroupBtn = (LinearLayout) view.findViewById(R.id.btnPrivateGroup);
            mPrivateGroupBtn.setOnClickListener(this);
            TemplateTitle title = (TemplateTitle) view.findViewById(R.id.contact_antionbar);
            title.setMoreImgAction(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMoveDialog();
                }
            });
            friends = FriendshipInfo.getInstance().getFriends();
            mGroupListAdapter = new ExpandGroupListAdapter(getActivity(), FriendshipInfo.getInstance().getGroups(), friends);
            mGroupListView.setAdapter(mGroupListAdapter);
//            friendProfileList.equals(friends.get(FriendshipInfo.getInstance().getGroups()));

            mGroupListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                    friends.get(FriendshipInfo.getInstance().getGroups().get(groupPosition)).get(childPosition).onClick(getActivity());
//                    friendProfileList.add(friends.get(FriendshipInfo.getInstance().getGroups().get(groupPosition)).get(childPosition));
                    return false;

                }
            });
            mGroupListAdapter.notifyDataSetChanged();
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


    @Override
    public void onResume() {
        super.onResume();
//        updateFace();
        mGroupListAdapter.notifyDataSetChanged();
    }


    private void updateFace() {
        TIMManager.getInstance().init(getActivity());
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "doInBackground: friends"+friendProfileList.size());
                for (int i = 0; i < friendProfileList.size(); i++) {
                    List<String> users = new ArrayList<>();
                    users.add(friendProfileList.get(i).getIdentify());
                    Log.i(TAG, "doInBackground: friends"+friendProfileList.get(i).getIdentify());
                    final int finalI = i;
                    TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>() {
                        @Override
                        public void onError(int i, String s) {
                            Log.e(TAG, "getUsersProfile failed: " + i + " desc:" + s);
                        }

                        @Override
                        public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                            for (TIMUserProfile res : timUserProfiles) {
                                friendProfileList.get(finalI).getAvatarUrl();
                                Log.d("cmx", "onSuccess: " +     friendProfileList.get(finalI).getAvatarUrl());
                                mGroupListAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
                return null;
            }
        }.execute();
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btnNewFriend) {
            Intent intent = new Intent(getActivity(), FriendshipManageMessageActivity.class);
            getActivity().startActivity(intent);
        }
        if (view.getId() == R.id.btnPublicGroup) {
            showGroups(GroupInfo.publicGroup);

        }
        if (view.getId() == R.id.btnChatroom) {
            showGroups(GroupInfo.chatRoom);
            getList();
//   new otherinfoAsyncTask().execute();
        }
        if (view.getId() == R.id.btnPrivateGroup) {
            Intent intent = new Intent(getActivity(), SearchFriendActivity.class);
            getActivity().startActivity(intent);
        }
    }

    private Dialog inviteDialog;
    private TextView addFriend, managerGroup, addGroup;

    private void showMoveDialog() {
        inviteDialog = new Dialog(getActivity(), R.style.dialog);
        inviteDialog.setContentView(R.layout.contact_more);
        addFriend = (TextView) inviteDialog.findViewById(R.id.add_friend);
        managerGroup = (TextView) inviteDialog.findViewById(R.id.manager_group);
        addGroup = (TextView) inviteDialog.findViewById(R.id.add_group);
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), SearchFriendActivity.class);
                Intent intent = new Intent(getActivity(), SearchAddFriendActivity.class);

                getActivity().startActivity(intent);
                inviteDialog.dismiss();
            }
        });
        managerGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ManageFriendGroupActivity.class);
                getActivity().startActivity(intent);
                inviteDialog.dismiss();
            }
        });
        addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchGroupActivity.class);
                getActivity().startActivity(intent);
                inviteDialog.dismiss();
            }
        });
        Window window = inviteDialog.getWindow();
        window.setGravity(Gravity.TOP | Gravity.RIGHT);
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setAttributes(lp);
        inviteDialog.show();
    }

    class otherinfoAsyncTask extends AsyncTask<Void, Void, otherInfo> {
        @Override
        protected otherInfo doInBackground(Void... params) {
//            Uid = MySelfInfo.getInstance().getId();
            if (Uid == null || "".equals(Uid)) {
                Uid = MySelfInfo.getInstance().getId();
            }
            return OKhttpHelper.getInstance().GET_JIEJIUWO(Uid);
        }

        @Override
        protected void onPostExecute(otherInfo otherInfo) {
            super.onPostExecute(otherInfo);
//            initView();
            Log.i(TAG, "onPostExecute: userInfo" + otherInfo.getId());
            if (otherInfo != null) {

//                nickname = otherInfo.getName();
//                str = Integer.valueOf(otherInfo.getAge());
//                sexArry[num] = otherInfo.getSex();
//                speciality_str = otherInfo.getAvatar();
//                topic_str = otherInfo.getId();
////                hobby_str = otherInfo.getHobby();
//                updateViewtext(otherInfo);
                Log.i(TAG, "onPostExecute: sz" + otherInfo);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("identify", otherInfo.getId());
                intent.putExtra("type", TIMConversationType.C2C);
                intent.putExtra("jiejiuwo", true);
                startActivity(intent);
            }


        }

    }


    private void getList() {
        List<String> users = new ArrayList<>();
        users.add("86-15734009232");
        users.add("86-18840917312");
        users.add("86-18624471423");
        new Thread(new Runnable() {
            @Override
            public void run() {
                OKhttpHelper.getInstance().GET_JIEJIUWO(UserInfo.getInstance().getId());
            }
        }).start();
    }

    //待获取用户资料的用户列表
//        List<String> users = new ArrayList<>();
//        JSONObject object=new JSONObject();

    //  //TODO 假的

//获取用户资料
//        TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>() {
//            @Override
//            public void onError(int code, String desc) {
//                //错误码code和错误描述desc，可用于定位请求失败原因
//                //错误码code列表请参见错误码表
//                Log.e("", "getUsersProfile failed: " + code + " desc");
//            }
//
//            @Override
//            public void onSuccess(List<TIMUserProfile> result) {
//                Log.e("", "getUsersProfile succ");
////                for(TIMUserProfile res : result){
////                    Log.e("", "identifier: " + res.getIdentifier() + " nickName: " + res.getNickName()
////                            + " remark: " + res.getRemark());
////
////
////
////                }
//                int i = (int) (Math.random() * result.size());
//        Intent intent = new Intent(getActivity(), ChatActivity.class);
//        intent.putExtra("identify",otherInfo.getId());
//        intent.putExtra("type", TIMConversationType.C2C);
//        intent.putExtra("jiejiuwo", true);
//        startActivity(intent);
//            }
//        });

    private void showGroups(String type) {
        Intent intent = new Intent(getActivity(), GroupListActivity.class);
        intent.putExtra("type", type);
        getActivity().startActivity(intent);
    }
}
