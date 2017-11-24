package com.hooha.maidai.chat.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.model.Fans;
import com.hooha.maidai.chat.view.CircleImageView;

import java.util.List;

/**
 * Created by MG on 2016/7/20.
 */
public class MyFollowAdapter extends RecyclerView.Adapter<MyFollowAdapter.ThisHolder> {
    private List<Fans> list;
    private LayoutInflater layoutInflater;
    private Context context;
    private String fragment;

    public MyFollowAdapter(List<Fans> list, Context context, String fragment) {
        this.list = list;
        this.context = context;
        this.fragment = fragment;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ThisHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyFollowAdapter.ThisHolder holder = new MyFollowAdapter.ThisHolder(layoutInflater.inflate(R.layout.item_my_follow, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(ThisHolder holder, int position) {
        holder.imageView.setImageUrl(list.get(position).getPicture());
        holder.nameTextView.setText(list.get(position).getName());
        if (fragment.equals("MyAttentionFragment")){
            holder.numberTextView.setText("好感度：" + list.get(position).getHisFeel());
        }else if (fragment.equals("MyFansFragment")){
            holder.numberTextView.setText("好友时间：" + list.get(position).getTime() + "天");
        }else if (fragment.equals("MyFollowFragment")){
            holder.numberTextView.setText("Top:" + (position+1));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ThisHolder extends RecyclerView.ViewHolder {
        CircleImageView imageView;
        TextView nameTextView;
        TextView numberTextView;

        public ThisHolder(View itemView) {
            super(itemView);
            imageView = (CircleImageView) itemView.findViewById(R.id.item_my_follow_ImageView);
            nameTextView = (TextView) itemView.findViewById(R.id.item_my_follow_name_tv);
            numberTextView = (TextView) itemView.findViewById(R.id.item_my_follow_number_tv);
        }
    }
}
