package com.hooha.maidai.chat.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hooha.maidai.chat.activity.GroupMemberActivity;
import com.hooha.maidai.chat.activity.GroupMemberProfileActivity;
import com.hooha.maidai.chat.model.GroupMemberProfile;
import com.hooha.maidai.chat.model.ProfileSummary;
import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.view.CircleImageView;

import java.util.List;

/**
 * 好友或群等资料摘要列表的adapter
 */
public class ProfileSummaryAdapter extends ArrayAdapter<ProfileSummary> {

private List<ProfileSummary> list;

    private int resourceId;
    private View view;
    private ViewHolder viewHolder;


    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public ProfileSummaryAdapter(Context context, int resource, List<ProfileSummary> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView != null) {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.avatar = (ImageView) view.findViewById(R.id.avatar);
            viewHolder.name = (TextView) view.findViewById(R.id.name);
            viewHolder.des = (TextView) view.findViewById(R.id.description);
            view.setTag(viewHolder);


        }



        ProfileSummary data = getItem(position);
//        viewHolder.avatar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        viewHolder.avatar.setImageResource(data.getAvatarRes());
//        viewHolder.avatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
        viewHolder.name.setText(data.getName());





        return view;
    }


    public class ViewHolder{
        public ImageView avatar;
        public TextView name;
        public TextView des;
    }
}
