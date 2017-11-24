package com.hooha.maidai.chat.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.activity.RoomListActivity;
import com.hooha.maidai.chat.bean.WuliaobaHuoDong;
import com.hooha.maidai.chat.model.MySelfInfo;

import java.util.List;

/**
 * Created by Public on 2016/7/28.
 */
public class WuliaobaAdapter extends RecyclerView.Adapter<WuliaobaAdapter.WuliaobaHolder> {

    private LayoutInflater inflater;
    private List<WuliaobaHuoDong> mList;
    private Context mContext;

    public WuliaobaAdapter(List<WuliaobaHuoDong> mList, Context context) {
        this.mList = mList;
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public WuliaobaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        WuliaobaHolder holder = new WuliaobaHolder(inflater.inflate(R.layout.item_wuliaoba, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(WuliaobaHolder holder, int position) {
        if (!TextUtils.isEmpty(mList.get(position).getPicture())){
            Log.d("WuliaobaAdapter", "load cover: " + mList.get(position).getPicture());
            RequestManager req = Glide.with(mContext);
            req.load(mList.get(position).getPicture()).into(holder.picImageView);
        }
        holder.typeTextView.setText(mList.get(position).getType());
        holder.infoTextView.setText(mList.get(position).getActiveInfo());
        String xing = "";
        for(int i = 0;i<Integer.parseInt(mList.get(position).getLiveness());i++){
            xing = xing + "☆";
        }
        holder.liveTextView.setText(xing);
        holder.countTextView.setText(mList.get(position).getPeopleCount());
        final int i = Integer.parseInt(mList.get(position).getActiveId());
//        final int i = 0;  //测试
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RoomListActivity.class);
                MySelfInfo.getInstance().setRoomType(i);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class WuliaobaHolder extends RecyclerView.ViewHolder {
        ImageView picImageView;//活动图像
        TextView typeTextView;//活动类别
        TextView infoTextView;//活动信息
        TextView liveTextView;//活跃度
        TextView countTextView;//活动人数

        public WuliaobaHolder(View itemView) {
            super(itemView);
            picImageView = (ImageView) itemView.findViewById(R.id.wuliaoba_picture);
            typeTextView = (TextView) itemView.findViewById(R.id.wuliaoba_type);
            infoTextView = (TextView) itemView.findViewById(R.id.wuliaoba_activeInfo);
            liveTextView = (TextView) itemView.findViewById(R.id.wuliaoba_liveness);
            countTextView = (TextView) itemView.findViewById(R.id.wuliaoba_peopleCount);
        }
    }
}
