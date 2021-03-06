package com.hooha.maidai.chat.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.hooha.maidai.chat.model.FriendProfile;
import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.model.MySelfInfo;
import com.hooha.maidai.chat.model.UserInfo;
import com.hooha.maidai.chat.model.otherInfo;
import com.hooha.maidai.chat.presenters.OKhttpHelper;
import com.hooha.maidai.chat.utils.GlideCircleTransform;
import com.hooha.maidai.chat.view.CircleImageView;

import java.util.List;
import java.util.Map;

/**
 * 分组列表Adapters
 */
public class ExpandGroupListAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private boolean selectable;
private String TAG="分组列表Adapters";
    private List<String> groups;
    private String Uid;
    private Map<String, List<FriendProfile>> mMembers;




    public ExpandGroupListAdapter(Context context, List<String> groups, Map<String, List<FriendProfile>> members){
        this(context, groups, members, false);
    }

    public ExpandGroupListAdapter(Context context, List<String> groups, Map<String, List<FriendProfile>> members, boolean selectable){
        mContext = context;
        this.groups = groups;
        mMembers = members;
        this.selectable = selectable;
    }




    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return mMembers.get(groups.get(i)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mMembers.get(groups.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * 群组
     *
     * @param groupPosition
     * @param isExpanded
     * @param convertView
     * @param viewGroup
     * @return
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup viewGroup) {
        GroupHolder groupHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_group, null);
            groupHolder = new GroupHolder();
            groupHolder.groupname = (TextView) convertView.findViewById(R.id.groupName);
            groupHolder.contentNum = (TextView) convertView.findViewById(R.id.contentNum);
            groupHolder.tag = (ImageView) convertView.findViewById(R.id.groupTag);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        if (isExpanded) {
            groupHolder.tag.setBackgroundResource(R.drawable.open);
        } else {
            groupHolder.tag.setBackgroundResource(R.drawable.close);
        }
        if (groups.get(groupPosition).equals("")) {
            groupHolder.groupname.setText(mContext.getResources().getString(R.string.default_group_name));
        } else {
            groupHolder.groupname.setText(groups.get(groupPosition));
        }
        groupHolder.contentNum.setText(String.valueOf(mMembers.get(groups.get(groupPosition)).size()) + mContext.getString(R.string.people));
        return convertView;
    }

    /**
     * 群组成员
     *
     * @param groupPosition
     * @param childPosition
     * @param isLastChild
     * @param convertView
     * @param viewGroup
     * @return
     */
    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup viewGroup) {
        ChildrenHolder itemHolder;
//        new UserinfoAsyncTask().execute();
        if (convertView == null) {
            itemHolder = new ChildrenHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_childmember, null);
            itemHolder.tag = (ImageView) convertView.findViewById(R.id.chooseTag);
            itemHolder.name = (TextView) convertView.findViewById(R.id.name);
            itemHolder.avatar=(CircleImageView) convertView.findViewById(R.id.avatar);
            convertView.setTag(itemHolder);
        } else {
            itemHolder = (ChildrenHolder) convertView.getTag();
        }
        FriendProfile data = (FriendProfile) getChild(groupPosition,childPosition);
        itemHolder.name.setText(data.getName());
        Uid=data.getIdentify();
        RequestManager req = Glide.with(mContext);

//        Log.i(TAG, "getChildView: data.getName"+data.getName());
        req.load(data.getAvatarUrl()).transform(new GlideCircleTransform(mContext)).into(itemHolder.avatar);
//        Log.i(TAG, "getChildView: data.avatar" + data.getAvatarRes());
        itemHolder.tag.setVisibility(selectable? View.VISIBLE : View.GONE);
        itemHolder.tag.setImageResource(data.isSelected()?R.drawable.selected:R.drawable.unselected);
        return convertView;
    }
//
//    class UserinfoAsyncTask extends AsyncTask<Void, Void, otherInfo> {
//        @Override
//        protected otherInfo doInBackground(Void... params) {
//
//            if (Uid == null || "".equals(Uid)) {
//                Uid = FriendProfile.getInstance().getIdentify();
//            }
//            return OKhttpHelper.getInstance().getPhoto(Uid);
//        }
//
//        @Override
//        protected void onPostExecute(otherInfo otherInfo) {
//            super.onPostExecute(otherInfo);
//
//            Log.i(TAG, "onPostExecute: userInfo" + otherInfo.getName());
//            if (otherInfo != null) {
//
//
//                Log.i(TAG, "onPostExecute: sz" + otherInfo);
//            }
//
//
//        }

//    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }


    class GroupHolder {
        public TextView groupname;
        public TextView contentNum;
        public ImageView tag;
    }

    class ChildrenHolder {
        public TextView name;
        public CircleImageView avatar;
        public ImageView tag;
    }



}
