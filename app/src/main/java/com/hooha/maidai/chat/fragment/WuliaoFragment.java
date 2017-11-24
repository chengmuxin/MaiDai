package com.hooha.maidai.chat.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.activity.LiveActivity;
import com.hooha.maidai.chat.model.CurLiveInfo;
import com.hooha.maidai.chat.model.LiveInfoJson;
import com.hooha.maidai.chat.model.MySelfInfo;
import com.hooha.maidai.chat.presenters.LiveListViewHelper;
import com.hooha.maidai.chat.presenters.OKhttpHelper;
import com.hooha.maidai.chat.presenters.viewinface.LiveListView;
import com.hooha.maidai.chat.utils.Constant;
import com.hooha.maidai.chat.utils.SxbLog;

import java.util.ArrayList;


/**
 * 直播列表页面
 */
public class WuliaoFragment extends Fragment implements View.OnClickListener, LiveListView, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "FragmentLiveList";
    private GridView mLiveList;
    private ArrayList<LiveInfoJson> liveList = new ArrayList<LiveInfoJson>();
    //    private LiveShowAdapter adapter;
    private GridViewAdapter adapter;
    private LiveListViewHelper mLiveListViewHelper;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView createRoom;
    private int type;

    public WuliaoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        type = MySelfInfo.getInstance().getRoomType();
        mLiveListViewHelper = new LiveListViewHelper(this);
        View view = inflater.inflate(R.layout.liveframent_layout, container, false);
        mLiveList = (GridView) view.findViewById(R.id.live_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_list);
        mSwipeRefreshLayout.setColorSchemeColors(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(this);

//        adapter = new LiveShowAdapter(getActivity(), R.layout.item_room_list, liveList);
        adapter = new GridViewAdapter();
        mLiveList.setAdapter(adapter);

        mLiveList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LiveInfoJson item = liveList.get(i);
                Intent intent = new Intent(getActivity(), LiveActivity.class);
                //如果是自己
                if (item.getHost().getUid().equals(MySelfInfo.getInstance().getId())) {
                    intent.putExtra(Constant.ID_STATUS, Constant.HOST);
                    MySelfInfo.getInstance().setIdStatus(Constant.HOST);
                    MySelfInfo.getInstance().setJoinRoomWay(false);
                    CurLiveInfo.setHostID(item.getHost().getUid());
                    CurLiveInfo.setHostName(item.getHost().getUsername());
                    CurLiveInfo.setHostAvator(item.getHost().getAvatar());
                    CurLiveInfo.setRoomNum(item.getAvRoomId());
                    CurLiveInfo.setMembers(item.getWatchCount() + 1); // 添加自己
                    CurLiveInfo.setAdmires(item.getAdmireCount());
//                    CurLiveInfo.setAddress(item.getLbs().getAddress());
                } else {
                    intent.putExtra(Constant.ID_STATUS, Constant.MEMBER);
                    MySelfInfo.getInstance().setIdStatus(Constant.MEMBER);
                    MySelfInfo.getInstance().setJoinRoomWay(false);
                    CurLiveInfo.setHostID(item.getHost().getUid());
                    CurLiveInfo.setHostName(item.getHost().getUsername());
                    CurLiveInfo.setHostAvator(item.getHost().getAvatar());
                    CurLiveInfo.setRoomNum(item.getAvRoomId());
                    CurLiveInfo.setMembers(item.getWatchCount() + 1); // 添加自己
                    CurLiveInfo.setAdmires(item.getAdmireCount());
//                    CurLiveInfo.setAddress(item.getLbs().getAddress());
                    Log.i(TAG, "onItemClick: " + item.getAvRoomId());
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OKhttpHelper.getInstance().getRoomId(MySelfInfo.getInstance().getId(), "" + CurLiveInfo.getRoomNum(), "" + type);
                    }
                }).start();
                startActivity(intent);

                SxbLog.i(TAG, "PerformanceTest  join Live     " + SxbLog.getTime());
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        mLiveListViewHelper.getPageData(type);
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mLiveListViewHelper.onDestory();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
    }


    @Override
    public void showFirstPage(ArrayList<LiveInfoJson> result) {
        mSwipeRefreshLayout.setRefreshing(false);
        liveList.clear();
        if (null != result) {
            for (LiveInfoJson item : result) {
                liveList.add(item);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        mLiveListViewHelper.getPageData(type);
    }

    private class GridViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return liveList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_room_list, null);
            }
            Log.d(TAG, "liveList: " + liveList.toString());
            ImageView iv_image = (ImageView) convertView.findViewById(R.id.item_room_list_bg);
            if (liveList.get(position).getCover().trim() == null || liveList.get(position).getCover().trim().equals("")) {
                iv_image.setBackgroundResource(R.drawable.h78);
            } else {
                iv_image.setBackgroundResource(Integer.parseInt(liveList.get(position).getCover()));
            }
            TextView tv_title = (TextView) convertView.findViewById(R.id.room_list_id);
//            tv_title.setText("" + liveList.get(position).getChatRoomId());
            tv_title.setText("房间" + (position + 1) + "：");
            TextView tv_detail = (TextView) convertView.findViewById(R.id.room_list_count);
            tv_detail.setText("人数：" + liveList.get(position).getWatchCount());
            TextView tv_point = (TextView) convertView.findViewById(R.id.room_list_time);
            tv_point.setText("已开始：" + liveList.get(position).getTimeSpan() + "分钟");
            return convertView;
        }
    }
}
