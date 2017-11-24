package com.hooha.maidai.chat.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.activity.LiveActivity;
import com.hooha.maidai.chat.bean.RoomBean;
import com.hooha.maidai.chat.model.LiveInfoJson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Public on 2016/7/28.
 */
public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.WuliaobaHolder> {

    private LayoutInflater inflater;
    private List<RoomBean> mList;
    private Context mContext;
    private ArrayList<LiveInfoJson> liveList = new ArrayList<LiveInfoJson>();

    public RoomListAdapter(List<RoomBean> mList, Context context) {
        this.mList = mList;
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public WuliaobaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        WuliaobaHolder holder = new WuliaobaHolder(inflater.inflate(R.layout.item_room_list, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(WuliaobaHolder holder, int position) {
        final int i = position + 1;
        holder.roomListIdTextView.setText("房间" + i + ":");
        holder.countTextView.setText("人数：" + mList.get(position).getCount());
        holder.timeTextView.setText("已开始：：" + mList.get(position).getTime());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, LiveActivity.class);
                intent.putExtra("type", i-1);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class WuliaobaHolder extends RecyclerView.ViewHolder {
        TextView countTextView;//参与人数
        TextView timeTextView;//活动类别
        TextView roomListIdTextView;//房间ID

        public WuliaobaHolder(View itemView) {
            super(itemView);
            countTextView = (TextView) itemView.findViewById(R.id.room_list_count);
            timeTextView = (TextView) itemView.findViewById(R.id.room_list_time);
            roomListIdTextView = (TextView) itemView.findViewById(R.id.room_list_id);
        }
    }
}
