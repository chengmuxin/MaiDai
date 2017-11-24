package com.hooha.maidai.chat.presenters;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hooha.maidai.chat.adapters.TucaoShowAdapter;
import com.hooha.maidai.chat.model.MySelfInfo;
import com.hooha.maidai.chat.model.Tucao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MG on 2016/10/17.
 */
public class TucaoHelper {
    private PullToRefreshListView mListView;
    private Context mContext;
    private List<Tucao> mList = new ArrayList<Tucao>();
    private String TYPE;
    public static int page = 0;
    private int pagesize = 20;
    private int mSelect = 0;

    public TucaoHelper(PullToRefreshListView listView, Context context, String type, int select) {
        mListView = listView;
        mContext = context;
        TYPE = type;
        mSelect = select;
    }

    public void start() {
        new VisitnoteAsyncTask().execute();
    }

    class VisitnoteAsyncTask extends AsyncTask<Void, Void, List<Tucao>> {
        @Override
        protected List<Tucao> doInBackground(Void... params) {
            return OKhttpHelper.getInstance().visitnote(MySelfInfo.getInstance().getId(), TYPE, page, pagesize);
        }

        @Override
        protected void onPostExecute(List<Tucao> tucaos) {
            super.onPostExecute(tucaos);
            if (tucaos == null) {
                return;
            }
            mList.addAll(tucaos);
            final TucaoShowAdapter adapter = new TucaoShowAdapter(mContext, mList, mListView, TYPE);
            mListView.setAdapter(adapter);
//            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Intent cIntent = new Intent(mContext, CommentActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("tucao", mList.get(position));
//                    cIntent.putExtras(bundle);
//                    mContext.startActivity(cIntent);
//                }
//            });
//            mListView.setSelection(mSelect);
//            mListView.getRefreshableView().setSelection(mSelect);
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
                        new AsyncTask<Void, Void, List<Tucao>>() {
                            @Override
                            protected List<Tucao> doInBackground(Void... params) {
                                page = 0;
                                return OKhttpHelper.getInstance().visitnote(MySelfInfo.getInstance().getId(), TYPE, page, pagesize);
                            }

                            @Override
                            protected void onPostExecute(List<Tucao> tucaos) {
                                super.onPostExecute(tucaos);
                                mList.clear();
                                mList.addAll(tucaos);
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
                        new AsyncTask<Void, Void, List<Tucao>>() {

                            @Override
                            protected List<Tucao> doInBackground(Void... params) {
                                page++;
                                return OKhttpHelper.getInstance().visitnote(MySelfInfo.getInstance().getId(), TYPE, page, pagesize);
                            }

                            @Override
                            protected void onPostExecute(List<Tucao> tucaos) {
                                super.onPostExecute(tucaos);
                                mList.addAll(tucaos);
                                adapter.notifyDataSetChanged();
                                mListView.onRefreshComplete();
                            }
                        }.execute();
                    }
                }
            });
        }
    }
}
