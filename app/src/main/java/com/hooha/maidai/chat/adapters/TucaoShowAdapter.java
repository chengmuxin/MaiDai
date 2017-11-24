package com.hooha.maidai.chat.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.activity.CommentActivity;
import com.hooha.maidai.chat.activity.ImageShowActivity;
import com.hooha.maidai.chat.activity.InfoActivity;
import com.hooha.maidai.chat.activity.VideoShowActivity;
import com.hooha.maidai.chat.model.MySelfInfo;
import com.hooha.maidai.chat.model.Tucao;
import com.hooha.maidai.chat.presenters.OKhttpHelper;
import com.hooha.maidai.chat.view.CircleImageView;
import com.loopj.android.image.SmartImageView;

import java.net.URL;
import java.util.List;

/**
 * Created by MG on 2016/10/6.
 */
public class TucaoShowAdapter extends BaseAdapter implements View.OnClickListener {

    private List<Tucao> mList;
    private LayoutInflater mInflater;
    private Context mContext;
    private PullToRefreshListView mListView;
    private String TYPE;

    public TucaoShowAdapter(Context context, List<Tucao> data, PullToRefreshListView listView, String type) {
        mContext = context;
        mList = data;
        mInflater = LayoutInflater.from(context);
        mListView = listView;
        TYPE = type;
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
            convertView = mInflater.inflate(R.layout.item_tucao_show, null);
            viewHolder.nameTv = (TextView) convertView.findViewById(R.id.tv_tucao_show_name);
            viewHolder.timeTv = (TextView) convertView.findViewById(R.id.tv_tucao_show_time);
            viewHolder.infoTextTv = (TextView) convertView.findViewById(R.id.tv_tucao_show_info_text);
            viewHolder.labelTv = (TextView) convertView.findViewById(R.id.tv_item_tucao_show_label);
            viewHolder.labelLay = (LinearLayout) convertView.findViewById(R.id.lay_tucao_show_label);
            viewHolder.upLay = (LinearLayout) convertView.findViewById(R.id.lay_tucao_show_up);
            viewHolder.downLay = (LinearLayout) convertView.findViewById(R.id.lay_tucao_show_down);
            viewHolder.shareLay = (LinearLayout) convertView.findViewById(R.id.lay_tucao_show_share);
            viewHolder.commentLay = (LinearLayout) convertView.findViewById(R.id.lay_tucao_show_comment);
            viewHolder.upImg = (ImageView) convertView.findViewById(R.id.iv_tucao_show_up);
            viewHolder.downImg = (ImageView) convertView.findViewById(R.id.iv_tucao_show_down);
            viewHolder.shareImg = (ImageView) convertView.findViewById(R.id.iv_tucao_show_share);
            viewHolder.commentImg = (ImageView) convertView.findViewById(R.id.iv_tucao_show_comment);
            viewHolder.upTv = (TextView) convertView.findViewById(R.id.tv_tucao_show_up);
            viewHolder.downTv = (TextView) convertView.findViewById(R.id.tv_tucao_show_down);
            viewHolder.shareTv = (TextView) convertView.findViewById(R.id.tv_tucao_show_share);
            viewHolder.commentTv = (TextView) convertView.findViewById(R.id.tv_tucao_show_comment);
            viewHolder.avatarImg = (CircleImageView) convertView.findViewById(R.id.iv_tucao_show_avatar);
            viewHolder.infoImageImg = (SmartImageView) convertView.findViewById(R.id.iv_tucao_show_info_image);
            viewHolder.infoPic1Img = (SmartImageView) convertView.findViewById(R.id.iv_tucao_show_info_pic1);
            viewHolder.infoPic2Img = (SmartImageView) convertView.findViewById(R.id.iv_tucao_show_info_pic2);
            viewHolder.infoPic3Img = (SmartImageView) convertView.findViewById(R.id.iv_tucao_show_info_pic3);
            viewHolder.infoPic4Img = (SmartImageView) convertView.findViewById(R.id.iv_tucao_show_info_pic4);
            viewHolder.infoPic5Img = (SmartImageView) convertView.findViewById(R.id.iv_tucao_show_info_pic5);
            viewHolder.infoPic6Img = (SmartImageView) convertView.findViewById(R.id.iv_tucao_show_info_pic6);
            viewHolder.infoPic7Img = (SmartImageView) convertView.findViewById(R.id.iv_tucao_show_info_pic7);
            viewHolder.infoPic8Img = (SmartImageView) convertView.findViewById(R.id.iv_tucao_show_info_pic8);
            viewHolder.infoPic9Img = (SmartImageView) convertView.findViewById(R.id.iv_tucao_show_info_pic9);
            viewHolder.lay1Lay = (LinearLayout) convertView.findViewById(R.id.lay_tucao_show_lay1);
            viewHolder.lay2Lay = (LinearLayout) convertView.findViewById(R.id.lay_tucao_show_lay2);
            viewHolder.lay3Lay = (LinearLayout) convertView.findViewById(R.id.lay_tucao_show_lay3);
            viewHolder.infoVideo = (ImageView) convertView.findViewById(R.id.iv_tucao_show_info_video);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.nameTv.setText(mList.get(position).getUsername());
        viewHolder.nameTv.setTag(mList.get(position).getUid());
        viewHolder.nameTv.setOnClickListener(this);
        viewHolder.timeTv.setText(mList.get(position).getTime());
        viewHolder.infoTextTv.setText(mList.get(position).getNoteInfo());
        viewHolder.labelLay.setVisibility(View.GONE);
        if (mList.get(position).getLabel().trim().length() > 0) {
            viewHolder.labelTv.setText(mList.get(position).getLabel());
            viewHolder.labelLay.setVisibility(View.VISIBLE);
        }
        //操作区域
        viewHolder.upTv.setText("" + mList.get(position).getNoteAdmire());
        viewHolder.upTv.setTag(position);
        viewHolder.downTv.setText("" + mList.get(position).getNoteBad());
        viewHolder.downTv.setTag(position);
        viewHolder.shareTv.setText("" + mList.get(position).getNoteShare());
        viewHolder.commentTv.setText("" + mList.get(position).getNoteComment());
        viewHolder.upImg.setImageResource(R.drawable.h23);
        viewHolder.upImg.setTag(position);
        viewHolder.downImg.setImageResource(R.drawable.h24);
        viewHolder.downImg.setTag(position);
        if (mList.get(position).getIsAdmire() == 1) {
            viewHolder.upImg.setImageResource(R.drawable.tucao_up1);
            viewHolder.upLay.setClickable(false);
            viewHolder.downLay.setClickable(false);
        } else if (mList.get(position).getIsBad() == 1) {
            viewHolder.downImg.setImageResource(R.drawable.tucao_down1);
            viewHolder.upLay.setClickable(false);
            viewHolder.downLay.setClickable(false);
        } else {
            viewHolder.upLay.setClickable(true);
            viewHolder.upLay.setTag(position);
            viewHolder.upLay.setOnClickListener(this);
            viewHolder.downLay.setClickable(true);
            viewHolder.downLay.setTag(position);
            viewHolder.downLay.setOnClickListener(this);
        }
        viewHolder.shareLay.setTag(position);
        viewHolder.shareLay.setOnClickListener(this);
        viewHolder.commentLay.setTag(position);
        viewHolder.commentLay.setOnClickListener(this);
        //加载头像
        viewHolder.avatarImg.setImageUrl(mList.get(position).getAvatar());
        viewHolder.avatarImg.setTag(mList.get(position).getUid());
        viewHolder.avatarImg.setOnClickListener(this);
        //清空图片缓存
        viewHolder.lay1Lay.setVisibility(View.GONE);
        viewHolder.lay2Lay.setVisibility(View.GONE);
        viewHolder.lay3Lay.setVisibility(View.GONE);
        viewHolder.infoImageImg.setVisibility(View.GONE);
        viewHolder.infoPic1Img.setVisibility(View.GONE);
        viewHolder.infoPic2Img.setVisibility(View.GONE);
        viewHolder.infoPic3Img.setVisibility(View.GONE);
        viewHolder.infoPic4Img.setVisibility(View.GONE);
        viewHolder.infoPic5Img.setVisibility(View.GONE);
        viewHolder.infoPic6Img.setVisibility(View.GONE);
        viewHolder.infoPic7Img.setVisibility(View.GONE);
        viewHolder.infoPic8Img.setVisibility(View.GONE);
        viewHolder.infoPic9Img.setVisibility(View.GONE);
        viewHolder.infoVideo.setVisibility(View.GONE);
        viewHolder.infoImageImg.setImageResource(R.color.background_gray3);
        viewHolder.infoPic1Img.setImageResource(R.color.background_gray3);
        viewHolder.infoPic2Img.setImageResource(R.color.background_gray3);
        viewHolder.infoPic3Img.setImageResource(R.color.background_gray3);
        viewHolder.infoPic4Img.setImageResource(R.color.background_gray3);
        viewHolder.infoPic5Img.setImageResource(R.color.background_gray3);
        viewHolder.infoPic6Img.setImageResource(R.color.background_gray3);
        viewHolder.infoPic7Img.setImageResource(R.color.background_gray3);
        viewHolder.infoPic8Img.setImageResource(R.color.background_gray3);
        viewHolder.infoPic9Img.setImageResource(R.color.background_gray3);
        //加载图片
        String picture = mList.get(position).getPicture();
        if (!picture.isEmpty()) {
            String[] pic = picture.split(",");
            if (pic.length == 1) { //单张图片
                viewHolder.infoImageImg.setVisibility(View.VISIBLE);
                viewHolder.infoImageImg.setImageUrl(picture);
                if (mList.get(position).getVideo() == null || "".equals(mList.get(position).getVideo())) {
                    viewHolder.infoImageImg.setTag(pic[0]);
                    viewHolder.infoImageImg.setOnClickListener(this);
                }
            } else { //多张图片
                int len = pic.length;
                viewHolder.lay1Lay.setVisibility(View.VISIBLE);
                if (len > 3) {
                    viewHolder.lay2Lay.setVisibility(View.VISIBLE);
                }
                if (len > 6) {
                    viewHolder.lay3Lay.setVisibility(View.VISIBLE);
                }
                switch (len) {
                    case 9:
                        viewHolder.infoPic9Img.setVisibility(View.VISIBLE);
                        viewHolder.infoPic9Img.setImageUrl(pic[8]);
                        viewHolder.infoPic9Img.setTag(pic[8]);
                        viewHolder.infoPic9Img.setOnClickListener(this);
                    case 8:
                        viewHolder.infoPic8Img.setVisibility(View.VISIBLE);
                        viewHolder.infoPic8Img.setImageUrl(pic[7]);
                        viewHolder.infoPic8Img.setTag(pic[7]);
                        viewHolder.infoPic8Img.setOnClickListener(this);
                    case 7:
                        viewHolder.infoPic7Img.setVisibility(View.VISIBLE);
                        viewHolder.infoPic7Img.setImageUrl(pic[6]);
                        viewHolder.infoPic7Img.setTag(pic[6]);
                        viewHolder.infoPic7Img.setOnClickListener(this);
                    case 6:
                        viewHolder.infoPic6Img.setVisibility(View.VISIBLE);
                        viewHolder.infoPic6Img.setImageUrl(pic[5]);
                        viewHolder.infoPic6Img.setTag(pic[5]);
                        viewHolder.infoPic6Img.setOnClickListener(this);
                    case 5:
                        viewHolder.infoPic5Img.setVisibility(View.VISIBLE);
                        viewHolder.infoPic5Img.setImageUrl(pic[4]);
                        viewHolder.infoPic5Img.setTag(pic[4]);
                        viewHolder.infoPic5Img.setOnClickListener(this);
                        viewHolder.infoPic4Img.setVisibility(View.VISIBLE);
                        viewHolder.infoPic4Img.setImageUrl(pic[3]);
                        viewHolder.infoPic4Img.setTag(pic[3]);
                        viewHolder.infoPic4Img.setOnClickListener(this);
                    case 3:
                        viewHolder.infoPic3Img.setVisibility(View.VISIBLE);
                        viewHolder.infoPic3Img.setImageUrl(pic[2]);
                        viewHolder.infoPic3Img.setTag(pic[2]);
                        viewHolder.infoPic3Img.setOnClickListener(this);
                    case 2:
                        viewHolder.infoPic2Img.setVisibility(View.VISIBLE);
                        viewHolder.infoPic2Img.setImageUrl(pic[1]);
                        viewHolder.infoPic2Img.setTag(pic[1]);
                        viewHolder.infoPic2Img.setOnClickListener(this);
                        viewHolder.infoPic1Img.setVisibility(View.VISIBLE);
                        viewHolder.infoPic1Img.setImageUrl(pic[0]);
                        viewHolder.infoPic1Img.setTag(pic[0]);
                        viewHolder.infoPic1Img.setOnClickListener(this);
                        break;
                    case 4:
                        viewHolder.infoPic5Img.setVisibility(View.VISIBLE);
                        viewHolder.infoPic5Img.setImageUrl(pic[3]);
                        viewHolder.infoPic5Img.setTag(pic[3]);
                        viewHolder.infoPic5Img.setOnClickListener(this);
                        viewHolder.infoPic4Img.setVisibility(View.VISIBLE);
                        viewHolder.infoPic4Img.setImageUrl(pic[2]);
                        viewHolder.infoPic4Img.setTag(pic[2]);
                        viewHolder.infoPic4Img.setOnClickListener(this);
                        viewHolder.infoPic2Img.setVisibility(View.VISIBLE);
                        viewHolder.infoPic2Img.setImageUrl(pic[1]);
                        viewHolder.infoPic2Img.setTag(pic[1]);
                        viewHolder.infoPic2Img.setOnClickListener(this);
                        viewHolder.infoPic1Img.setVisibility(View.VISIBLE);
                        viewHolder.infoPic1Img.setImageUrl(pic[0]);
                        viewHolder.infoPic1Img.setTag(pic[0]);
                        viewHolder.infoPic1Img.setOnClickListener(this);
                        break;
                }
            }
        }
        if (mList.get(position).getVideo() == null || "".equals(mList.get(position).getVideo())) {

        } else {
            viewHolder.infoVideo.setVisibility(View.VISIBLE);
            viewHolder.infoVideo.setTag(position);
            viewHolder.infoVideo.setOnClickListener(this);
        }

        return convertView;
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.iv_tucao_show_avatar:
                Intent aIntent = new Intent(mContext, InfoActivity.class);
                aIntent.putExtra("uid", (String) v.getTag());
                mContext.startActivity(aIntent);
                break;
            case R.id.tv_tucao_show_name:
                Intent nIntent = new Intent(mContext, InfoActivity.class);
                nIntent.putExtra("uid", (String) v.getTag());
                mContext.startActivity(nIntent);
                break;
            case R.id.iv_tucao_show_info_image:
                Intent intenti = new Intent(mContext, ImageShowActivity.class);
                intenti.putExtra("url", (String) v.getTag());
                mContext.startActivity(intenti);
                break;
            case R.id.iv_tucao_show_info_pic1:
                Intent intent1 = new Intent(mContext, ImageShowActivity.class);
                intent1.putExtra("url", (String) v.getTag());
                mContext.startActivity(intent1);
                break;
            case R.id.iv_tucao_show_info_pic2:
                Intent intent2 = new Intent(mContext, ImageShowActivity.class);
                intent2.putExtra("url", (String) v.getTag());
                mContext.startActivity(intent2);
                break;
            case R.id.iv_tucao_show_info_pic3:
                Intent intent3 = new Intent(mContext, ImageShowActivity.class);
                intent3.putExtra("url", (String) v.getTag());
                mContext.startActivity(intent3);
                break;
            case R.id.iv_tucao_show_info_pic4:
                Intent intent4 = new Intent(mContext, ImageShowActivity.class);
                intent4.putExtra("url", (String) v.getTag());
                mContext.startActivity(intent4);
                break;
            case R.id.iv_tucao_show_info_pic5:
                Intent intent5 = new Intent(mContext, ImageShowActivity.class);
                intent5.putExtra("url", (String) v.getTag());
                mContext.startActivity(intent5);
                break;
            case R.id.iv_tucao_show_info_pic6:
                Intent intent6 = new Intent(mContext, ImageShowActivity.class);
                intent6.putExtra("url", (String) v.getTag());
                mContext.startActivity(intent6);
                break;
            case R.id.iv_tucao_show_info_pic7:
                Intent intent7 = new Intent(mContext, ImageShowActivity.class);
                intent7.putExtra("url", (String) v.getTag());
                mContext.startActivity(intent7);
                break;
            case R.id.iv_tucao_show_info_pic8:
                Intent intent8 = new Intent(mContext, ImageShowActivity.class);
                intent8.putExtra("url", (String) v.getTag());
                mContext.startActivity(intent8);
                break;
            case R.id.iv_tucao_show_info_pic9:
                Intent intent9 = new Intent(mContext, ImageShowActivity.class);
                intent9.putExtra("url", (String) v.getTag());
                mContext.startActivity(intent9);
                break;
            case R.id.iv_tucao_show_info_video:
                Intent vIntent = new Intent(mContext, VideoShowActivity.class);
                vIntent.putExtra("url", mList.get((int) v.getTag()).getVideo());
                mContext.startActivity(vIntent);
                break;
            case R.id.lay_tucao_show_up:
                new UpAsyncTask().execute((Integer) v.getTag());
                break;
            case R.id.lay_tucao_show_down:
                new DownAsyncTask().execute((Integer) v.getTag());
                break;
            case R.id.lay_tucao_show_share:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, mList.get((int) v.getTag()).getNoteInfo() + " http://www.aihuha.com/");
                shareIntent.setType("text/plain");
                //设置分享列表的标题，并且每次都显示分享列表
                mContext.startActivity(Intent.createChooser(shareIntent, "分享到"));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OKhttpHelper.getInstance().noteshare(mList.get((int) v.getTag()).getNoteId(), MySelfInfo.getInstance().getId());
                    }
                }).start();
                break;
            case R.id.lay_tucao_show_comment:
                Intent cIntent = new Intent(mContext, CommentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("tucao", mList.get((Integer) v.getTag()));
                cIntent.putExtras(bundle);
                mContext.startActivity(cIntent);
                break;
        }
    }

    class UpAsyncTask extends AsyncTask<Integer, Void, Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            int r = OKhttpHelper.getInstance().noteadmire(mList.get(params[0].intValue()).getNoteId(), MySelfInfo.getInstance().getId());
            if (r == 0) {
                return params[0];
            }
            return r;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer.intValue() < 0) {
                Toast.makeText(mContext, "点赞操作未成功", Toast.LENGTH_SHORT).show();
            } else {
                mList.get(integer).setIsAdmire(1);
                mList.get(integer).setNoteAdmire(mList.get(integer).getNoteAdmire() + 1);
                notifyDataSetChanged();
                Toast.makeText(mContext, "点赞操作成功", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class DownAsyncTask extends AsyncTask<Integer, Void, Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            int r = OKhttpHelper.getInstance().notebad(mList.get(params[0].intValue()).getNoteId(), MySelfInfo.getInstance().getId());
            if (r == 0) {
                return params[0];
            }
            return r;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer.intValue() < 0) {
                Toast.makeText(mContext, "差评操作未成功", Toast.LENGTH_SHORT).show();
            } else {
                mList.get(integer).setIsBad(1);
                mList.get(integer).setNoteBad(mList.get(integer).getNoteBad() + 1);
                notifyDataSetChanged();
                Toast.makeText(mContext, "差评操作成功", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void myImageUrl(SmartImageView smartImageView, String s) {
        try {
            if (new URL(s).openConnection().getContentLength() / 1024 < 100) {
                smartImageView.setImageUrl(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ViewHolder {
        public TextView nameTv, timeTv, infoTextTv, labelTv, upTv, downTv, shareTv, commentTv;
        public CircleImageView avatarImg;
        public SmartImageView infoImageImg, infoPic1Img, infoPic2Img, infoPic3Img, infoPic4Img, infoPic5Img, infoPic6Img, infoPic7Img, infoPic8Img, infoPic9Img;
        public ImageView upImg, downImg, shareImg, commentImg, infoVideo;
        public LinearLayout lay1Lay, lay2Lay, lay3Lay, upLay, downLay, shareLay, commentLay, labelLay;
    }
}
