package com.hooha.maidai.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.adapters.TucaoCommentAdapter;
import com.hooha.maidai.chat.base.BaseActivity;
import com.hooha.maidai.chat.model.Comment;
import com.hooha.maidai.chat.model.MySelfInfo;
import com.hooha.maidai.chat.model.Tucao;
import com.hooha.maidai.chat.presenters.OKhttpHelper;
import com.hooha.maidai.chat.view.CircleImageView;
import com.loopj.android.image.SmartImageView;

import java.util.ArrayList;
import java.util.List;

import static com.hooha.maidai.chat.R.id.tv_tucao_show_info_text;

/**
 * Created by Public on 2016/7/21.
 */
public class CommentActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private Tucao tucao;
    private CircleImageView avatarIv;
    private TextView nameTv, timeTv, infoTextTv, labelTv, upTv, downTv, shareTv, commentTv;
    private SmartImageView imageImg, pic1Img, pic2Img, pic3Img, pic4Img, pic5Img, pic6Img, pic7Img, pic8Img, pic9Img;
    private ImageView upImg, downImg, shareImg, commentImg, infoVideo;
    private LinearLayout lay1Lay, lay2Lay, lay3Lay, upLay, downLay, shareLay, commentLay;
    private PullToRefreshListView mListView;
    private List<Comment> list = new ArrayList<Comment>();
    private EditText inputEt;
    private Button sendBtn;
    public static int page = 0;
    private int pagesize = 20;
    private String replyId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        initView();
        new VisitcommentAsyncTask().execute();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void initView() {
        mListView = (PullToRefreshListView) findViewById(R.id.lv_tucao_comment);
        View headerView = getLayoutInflater().inflate(R.layout.item_tucao_show, null);
        ListView lv = mListView.getRefreshableView();
        lv.addHeaderView(headerView);

        tucao = (Tucao) getIntent().getSerializableExtra("tucao");
        avatarIv = (CircleImageView) findViewById(R.id.iv_tucao_show_avatar);
        nameTv = (TextView) findViewById(R.id.tv_tucao_show_name);
        timeTv = (TextView) findViewById(R.id.tv_tucao_show_time);
        infoTextTv = (TextView) findViewById(tv_tucao_show_info_text);
        labelTv = (TextView) findViewById(R.id.tv_item_tucao_show_label);
        upTv = (TextView) findViewById(R.id.tv_tucao_show_up);
        downTv = (TextView) findViewById(R.id.tv_tucao_show_down);
        shareTv = (TextView) findViewById(R.id.tv_tucao_show_share);
        commentTv = (TextView) findViewById(R.id.tv_tucao_show_comment);
        upImg = (ImageView) findViewById(R.id.iv_tucao_show_up);
        downImg = (ImageView) findViewById(R.id.iv_tucao_show_down);
        shareImg = (ImageView) findViewById(R.id.iv_tucao_show_share);
        commentImg = (ImageView) findViewById(R.id.iv_tucao_show_comment);
        upLay = (LinearLayout) findViewById(R.id.lay_tucao_show_up);
        downLay = (LinearLayout) findViewById(R.id.lay_tucao_show_down);
        shareLay = (LinearLayout) findViewById(R.id.lay_tucao_show_share);
        commentLay = (LinearLayout) findViewById(R.id.lay_tucao_show_comment);
        imageImg = (SmartImageView) findViewById(R.id.iv_tucao_show_info_image);
        pic1Img = (SmartImageView) findViewById(R.id.iv_tucao_show_info_pic1);
        pic2Img = (SmartImageView) findViewById(R.id.iv_tucao_show_info_pic2);
        pic3Img = (SmartImageView) findViewById(R.id.iv_tucao_show_info_pic3);
        pic4Img = (SmartImageView) findViewById(R.id.iv_tucao_show_info_pic4);
        pic5Img = (SmartImageView) findViewById(R.id.iv_tucao_show_info_pic5);
        pic6Img = (SmartImageView) findViewById(R.id.iv_tucao_show_info_pic6);
        pic7Img = (SmartImageView) findViewById(R.id.iv_tucao_show_info_pic7);
        pic8Img = (SmartImageView) findViewById(R.id.iv_tucao_show_info_pic8);
        pic9Img = (SmartImageView) findViewById(R.id.iv_tucao_show_info_pic9);
        lay1Lay = (LinearLayout) findViewById(R.id.lay_tucao_show_lay1);
        lay2Lay = (LinearLayout) findViewById(R.id.lay_tucao_show_lay2);
        lay3Lay = (LinearLayout) findViewById(R.id.lay_tucao_show_lay3);
        infoVideo = (ImageView) findViewById(R.id.iv_tucao_show_info_video);

        inputEt = (EditText) findViewById(R.id.et_input);
        sendBtn = (Button) findViewById(R.id.btn_send);

        avatarIv.setImageUrl(tucao.getAvatar());
        nameTv.setText(tucao.getUsername());
        timeTv.setText(tucao.getTime());
        infoTextTv.setText(tucao.getNoteInfo());
        labelTv.setText(tucao.getLabel());
        upTv.setText("" + tucao.getNoteAdmire());
        downTv.setText("" + tucao.getNoteBad());
        shareTv.setText("" + tucao.getNoteShare());
        commentTv.setText("" + tucao.getNoteComment());
        if (tucao.getIsAdmire() == 1) {
            upImg.setImageResource(R.drawable.tucao_up1);
        } else if (tucao.getIsBad() == 1) {
            downImg.setImageResource(R.drawable.tucao_down1);
        } else {
//            upLay.setTag(position);
            upLay.setOnClickListener(this);
//            downLay.setTag(position);
            downLay.setOnClickListener(this);
        }
        shareLay.setOnClickListener(this);
        String picture = tucao.getPicture();
        imageImg.setVisibility(View.GONE);
        if (!picture.isEmpty()) {
            String[] pic = picture.split(",");
            if (pic.length == 1) { //单张图片
                imageImg.setVisibility(View.VISIBLE);
                imageImg.setImageUrl(picture);
            } else { //多张图片
                int len = pic.length;
                lay1Lay.setVisibility(View.VISIBLE);
                if (len > 3) {
                    lay2Lay.setVisibility(View.VISIBLE);
                }
                if (len > 6) {
                    lay3Lay.setVisibility(View.VISIBLE);
                }
                switch (len) {
                    case 2:
                        pic3Img.setVisibility(View.GONE);
                        break;
                    case 4:
                        pic3Img.setVisibility(View.GONE);
                        pic6Img.setVisibility(View.GONE);
                        break;
                    case 5:
                        pic6Img.setVisibility(View.GONE);
                        break;
                    case 7:
                        pic8Img.setVisibility(View.GONE);
                        pic9Img.setVisibility(View.GONE);
                        break;
                    case 8:
                        pic9Img.setVisibility(View.GONE);
                        break;
                }
                switch (len) {
                    case 9:
                        pic9Img.setImageUrl(pic[8]);
                    case 8:
                        pic8Img.setImageUrl(pic[7]);
                    case 7:
                        pic7Img.setImageUrl(pic[6]);
                    case 6:
                        pic6Img.setImageUrl(pic[5]);
                    case 5:
                        pic5Img.setImageUrl(pic[4]);
                        pic4Img.setImageUrl(pic[3]);
                    case 3:
                        pic3Img.setImageUrl(pic[2]);
                    case 2:
                        pic2Img.setImageUrl(pic[1]);
                        pic1Img.setImageUrl(pic[0]);
                        break;
                    case 4:
                        pic5Img.setImageUrl(pic[3]);
                        pic4Img.setImageUrl(pic[2]);
                        pic2Img.setImageUrl(pic[1]);
                        pic1Img.setImageUrl(pic[0]);
                        break;
                }
            }
        }
        if (tucao.getVideo() == null || "".equals(tucao.getVideo())) {
            infoVideo.setVisibility(View.GONE);
        } else {
            imageImg.setVisibility(View.VISIBLE);
            infoVideo.setVisibility(View.VISIBLE);
            infoVideo.setOnClickListener(this);
        }
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(inputEt.getWindowToken(), 0);
                inputEt.setHint("在这里输入评论的内容");
                replyId = "";
            }
        });
        mListView.setOnItemClickListener(this);
        sendBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_tucao_show_info_video:
                Intent vIntent = new Intent(this, VideoShowActivity.class);
                vIntent.putExtra("url", tucao.getVideo());
                this.startActivity(vIntent);
                break;
            case R.id.lay_tucao_show_up:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OKhttpHelper.getInstance().noteadmire(tucao.getNoteId(), MySelfInfo.getInstance().getId());
                    }
                }).start();
                upImg.setImageResource(R.drawable.tucao_up1);
                upTv.setText("" + (tucao.getNoteAdmire() + 1));
                upLay.setClickable(false);
                downLay.setClickable(false);
                break;
            case R.id.lay_tucao_show_down:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OKhttpHelper.getInstance().notebad(tucao.getNoteId(), MySelfInfo.getInstance().getId());
                    }
                }).start();
                downImg.setImageResource(R.drawable.tucao_down1);
                downTv.setText("" + (tucao.getNoteBad() + 1));
                upLay.setClickable(false);
                downLay.setClickable(false);
                break;
            case R.id.lay_tucao_show_share:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, tucao.getNoteInfo() + " http://www.aihuha.com/");
                shareIntent.setType("text/plain");
                //设置分享列表的标题，并且每次都显示分享列表
                startActivity(Intent.createChooser(shareIntent, "分享到"));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OKhttpHelper.getInstance().noteshare(tucao.getNoteId(), MySelfInfo.getInstance().getId());
                    }
                }).start();
                break;
            case R.id.btn_send:
                String s = inputEt.getText().toString();
                if (s == null || "".equals(s)) {
                } else {
                    new SendAsyncTask().execute(s);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(inputEt.getWindowToken(), 0);
                    inputEt.setHint("在这里输入评论的内容");
                    replyId = "";
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("cmx", "position: " + position);
        Log.d("cmx", "list: " + list.size());
        Log.d("cmx", "getCommentName: " + list.get(position - 2).getCommentName());
        inputEt.setHint("回复" + list.get(position - 2).getCommentName() + "的评论：");
        replyId = list.get(position - 2).getCommentId();
    }

    class VisitcommentAsyncTask extends AsyncTask<Void, Void, List<Comment>> {
        @Override
        protected List<Comment> doInBackground(Void... params) {
            return OKhttpHelper.getInstance().visitcomment(tucao.getNoteId(), page, pagesize);
        }

        @Override
        protected void onPostExecute(List<Comment> comments) {
            super.onPostExecute(comments);
            if (comments == null) {
                return;
            }
            list.clear();
            list.addAll(comments);
            final TucaoCommentAdapter adapter = new TucaoCommentAdapter(CommentActivity.this, comments);
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
                        new AsyncTask<Void, Void, List<Comment>>() {
                            @Override
                            protected List<Comment> doInBackground(Void... params) {
                                page = 0;
                                return OKhttpHelper.getInstance().visitcomment(tucao.getNoteId(), page, pagesize);
                            }

                            @Override
                            protected void onPostExecute(List<Comment> tucaos) {
                                super.onPostExecute(tucaos);
                                list.clear();
                                list.addAll(tucaos);
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
                        new AsyncTask<Void, Void, List<Comment>>() {

                            @Override
                            protected List<Comment> doInBackground(Void... params) {
                                page++;
                                return OKhttpHelper.getInstance().visitcomment(tucao.getNoteId(), page, pagesize);
                            }

                            @Override
                            protected void onPostExecute(List<Comment> tucaos) {
                                super.onPostExecute(tucaos);
                                list.addAll(tucaos);
                                adapter.notifyDataSetChanged();
                                mListView.onRefreshComplete();
                            }
                        }.execute();
                    }
                }
            });
        }
    }

    class SendAsyncTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            return new Integer(OKhttpHelper.getInstance().comment(MySelfInfo.getInstance().getId(), tucao.getNoteId(), replyId, params[0]));
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer.intValue() == 0) {
                inputEt.setText("");
                new VisitcommentAsyncTask().execute();
            } else {
                Toast.makeText(CommentActivity.this, "消息未发送成功", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
