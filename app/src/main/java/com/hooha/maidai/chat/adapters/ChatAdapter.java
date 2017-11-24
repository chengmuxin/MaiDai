package com.hooha.maidai.chat.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.model.Message;
import com.hooha.maidai.chat.model.MySelfInfo;
import com.hooha.maidai.chat.view.CircleImageView;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天界面adapter
 */
public class ChatAdapter extends ArrayAdapter<Message> {

    private final String TAG = "ChatAdapter";

    private int resourceId;
    private View view;
    private ViewHolder viewHolder;

    public void notifyData() {
        notifyDataSetChanged();
    }

    public ChatAdapter(Context context, int resource, List<Message> objects) {
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
            viewHolder.leftAvatar = (CircleImageView) view.findViewById(R.id.leftAvatar);
            viewHolder.rightAvatar = (CircleImageView) view.findViewById(R.id.rightAvatar);
            viewHolder.leftMessage = (RelativeLayout) view.findViewById(R.id.leftMessage);
            viewHolder.rightMessage = (RelativeLayout) view.findViewById(R.id.rightMessage);
            viewHolder.leftPanel = (RelativeLayout) view.findViewById(R.id.leftPanel);
            viewHolder.rightPanel = (RelativeLayout) view.findViewById(R.id.rightPanel);
            viewHolder.sending = (ProgressBar) view.findViewById(R.id.sending);
            viewHolder.error = (ImageView) view.findViewById(R.id.sendError);
            viewHolder.sender = (TextView) view.findViewById(R.id.sender);
            viewHolder.systemMessage = (TextView) view.findViewById(R.id.systemMessage);
            view.setTag(viewHolder);
        }
        final Message data = getItem(position);
        data.showMessage(viewHolder, getContext());
        if (data.isSelf()) {
            viewHolder.rightAvatar.setImageUrl(MySelfInfo.getInstance().getAvatar());
        } else {
            Log.d("cmx", "left: " + data.getSender());
            List<String> users = new ArrayList<>();
            users.add(data.getSender());
            TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>() {
                @Override
                public void onError(int i, String s) {
                    Log.e(TAG, "getUsersProfile failed: " + i + " desc:" + s);
                }

                @Override
                public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                    for (TIMUserProfile res : timUserProfiles) {
                        Log.d("cmx", "onSuccess: " + res.getFaceUrl());
                        viewHolder.leftAvatar.setImageUrl(res.getFaceUrl());
                    }
                }
            });
        }
        return view;
    }


    public class ViewHolder {
        public CircleImageView leftAvatar;
        public CircleImageView rightAvatar;
        public RelativeLayout leftMessage;
        public RelativeLayout rightMessage;
        public RelativeLayout leftPanel;
        public RelativeLayout rightPanel;
        public ProgressBar sending;
        public ImageView error;
        public TextView sender;
        public TextView systemMessage;
    }
}
