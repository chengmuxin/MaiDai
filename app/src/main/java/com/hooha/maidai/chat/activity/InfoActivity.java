package com.hooha.maidai.chat.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.adapters.TucaoShowAdapter;
import com.hooha.maidai.chat.base.BaseActivity;
import com.hooha.maidai.chat.model.MySelfInfo;
import com.hooha.maidai.chat.model.Tucao;
import com.hooha.maidai.chat.model.TucaoInfo;
import com.hooha.maidai.chat.presenters.OKhttpHelper;
import com.hooha.maidai.chat.view.CircleImageView;
import com.loopj.android.image.SmartImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Public on 2016/7/21.
 */
public class InfoActivity extends BaseActivity {
    private static String TYPE = "全部";
    private PullToRefreshListView mListView;
    public static int page = 0;
    private int pagesize = 20;
    private List<Tucao> mList = new ArrayList<Tucao>();
    private SmartImageView mybgIv;
    private CircleImageView avatarIv;
    private TextView nameTv, levelTv, attentionTv, fansTv, noteTv, shareTv, commentTv, titleTv;
    private String Uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        mListView = (PullToRefreshListView) findViewById(R.id.lv_tucao_info_note);
        View headerView = getLayoutInflater().inflate(R.layout.item_tucao_info, null);
        ListView lv = mListView.getRefreshableView();
        lv.addHeaderView(headerView);
        new UserinfoAsyncTask().execute();
    }

    class UserinfoAsyncTask extends AsyncTask<Void, Void, TucaoInfo> {
        @Override
        protected TucaoInfo doInBackground(Void... params) {
            Uid = getIntent().getStringExtra("uid");
            if (Uid == null || "".equals(Uid)) {
                Uid = MySelfInfo.getInstance().getId();
            }
            return OKhttpHelper.getInstance().userinfo(Uid, "" + page, "" + pagesize);
        }

        @Override
        protected void onPostExecute(TucaoInfo tucaoInfo) {
            super.onPostExecute(tucaoInfo);
            initHead();
            if (tucaoInfo != null) {
                initData(tucaoInfo);
                mList.addAll(tucaoInfo.getInfo());
                for (Tucao tc :
                        mList) {
                    tc.setAvatar(tucaoInfo.getUseravatar());
                    tc.setUsername(tucaoInfo.getUsername());
                }
            }
            final TucaoShowAdapter adapter = new TucaoShowAdapter(InfoActivity.this, mList, mListView, TYPE);
            mListView.setAdapter(adapter);

            mListView.setMode(PullToRefreshBase.Mode.BOTH);
            mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
                @Override
                public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                    if (refreshView.isShownHeader()) {
                        //判断头布局是否可见，如果可见执行下拉刷新
                        //设置尾布局样式文字
                        mListView.getLoadingLayoutProxy().setRefreshingLabel("正在刷新");
                        mListView.getLoadingLayoutProxy().setPullLabel("下拉刷新数据");
                        mListView.getLoadingLayoutProxy().setReleaseLabel("释放开始刷新");
                        new AsyncTask<Void, Void, TucaoInfo>() {
                            @Override
                            protected TucaoInfo doInBackground(Void... params) {
                                page = 0;
                                return OKhttpHelper.getInstance().userinfo(Uid, "" + page, "" + pagesize);
                            }

                            @Override
                            protected void onPostExecute(TucaoInfo tucaoInfo) {
                                super.onPostExecute(tucaoInfo);
                                if (tucaoInfo != null) {
                                    initData(tucaoInfo);
                                    mList.clear();
                                    mList.addAll(tucaoInfo.getInfo());
                                    for (Tucao tc :
                                            mList) {
                                        tc.setAvatar(tucaoInfo.getUseravatar());
                                        tc.setUsername(tucaoInfo.getUsername());
                                    }
                                }
                                adapter.notifyDataSetChanged();
                                mListView.onRefreshComplete();
                            }
                        }.execute();
                    }
                    if (refreshView.isShownFooter()) {
                        //判断尾布局是否可见，如果可见执行上拉加载更多
                        //设置尾布局样式文字
                        mListView.getLoadingLayoutProxy().setRefreshingLabel("正在加载");
                        mListView.getLoadingLayoutProxy().setPullLabel("上拉加载更多");
                        mListView.getLoadingLayoutProxy().setReleaseLabel("释放开始加载");
                        new AsyncTask<Void, Void, TucaoInfo>() {

                            @Override
                            protected TucaoInfo doInBackground(Void... params) {
                                page++;
                                return OKhttpHelper.getInstance().userinfo(Uid, "" + page, "" + pagesize);
                            }

                            @Override
                            protected void onPostExecute(TucaoInfo tucaoInfo) {
                                super.onPostExecute(tucaoInfo);
                                if (tucaoInfo != null) {
                                    initData(tucaoInfo);
                                    mList.addAll(tucaoInfo.getInfo());
                                    for (Tucao tc :
                                            mList) {
                                        tc.setAvatar(tucaoInfo.getUseravatar());
                                        tc.setUsername(tucaoInfo.getUsername());
                                    }
                                }
                                adapter.notifyDataSetChanged();
                                mListView.onRefreshComplete();
                            }
                        }.execute();
                    }
                }
            });
        }

        private void initHead() {
            titleTv = (TextView) findViewById(R.id.head_biaoti);
            mybgIv = (SmartImageView) findViewById(R.id.iv_tucao_info_mybg);
            avatarIv = (CircleImageView) findViewById(R.id.iv_tucao_info_avatar);
            nameTv = (TextView) findViewById(R.id.tv_tucao_info_name);
            levelTv = (TextView) findViewById(R.id.tv_tucao_info_level);
            attentionTv = (TextView) findViewById(R.id.tv_tucao_info_attention);
            fansTv = (TextView) findViewById(R.id.tv_tucao_info_fans);
            noteTv = (TextView) findViewById(R.id.tv_tucao_info_note);
            shareTv = (TextView) findViewById(R.id.tv_tucao_info_share);
            commentTv = (TextView) findViewById(R.id.tv_tucao_info_comment);
        }

        private void initData(TucaoInfo tucaoInfo) {
            titleTv.setText("" + tucaoInfo.getUsername());
            avatarIv.setImageUrl(tucaoInfo.getUseravatar());
            nameTv.setText(tucaoInfo.getUsername());
            attentionTv.setText("关注：" + tucaoInfo.getUserattention());
            fansTv.setText("" + tucaoInfo.getUserfans());
            noteTv.setText("" + tucaoInfo.getUsernoteCount());
            shareTv.setText("" + tucaoInfo.getUserShareCount());
            commentTv.setText("" + tucaoInfo.getUserCommentCount());
        }
    }
}
