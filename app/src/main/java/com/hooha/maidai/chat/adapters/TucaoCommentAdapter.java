package com.hooha.maidai.chat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.model.Comment;
import com.hooha.maidai.chat.view.CircleImageView;

import java.util.List;


/**
 * Created by MG on 2016/10/13.
 */
public class TucaoCommentAdapter extends BaseAdapter {
    private List<Comment> mList;
    private LayoutInflater mInflater;

    public TucaoCommentAdapter(Context context, List<Comment> data) {
        mList = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_tucao_comment, null);
            viewHolder.avatarIv = (CircleImageView) convertView.findViewById(R.id.iv_tucao_comment_avatar);
            viewHolder.commentNameTv = (TextView) convertView.findViewById(R.id.tv_tucao_comment_name);
            viewHolder.commentInfoTv = (TextView) convertView.findViewById(R.id.tv_tucao_comment_text);
//            viewHolder.commentAdmireTv = (TextView) convertView.findViewById(R.id.tv_tucao_comment_up);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.avatarIv.setImageUrl(mList.get(position).getCommentAvatar());
        viewHolder.commentNameTv.setText(mList.get(position).getCommentName());
        if (mList.get(position).getReplyId() == null || "".equals(mList.get(position).getReplyId())) {
            viewHolder.commentInfoTv.setText(mList.get(position).getCommentInfo());
        } else {
            viewHolder.commentInfoTv.setText("回复 " + mList.get(position).getReplyName() + " 的评论：" + mList.get(position).getCommentInfo());
        }
//        viewHolder.commentAdmireTv.setText("" + mList.get(position).getCommentAdmire());

        return convertView;
    }

    class ViewHolder {
        public CircleImageView avatarIv;
        public TextView commentIdTv, commentNameTv, replyIdTv, replyNameTv, commentInfoTv, commentAdmireTv;
    }
}
