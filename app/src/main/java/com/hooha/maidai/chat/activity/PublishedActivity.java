package com.hooha.maidai.chat.activity;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.base.BaseActivity;
import com.hooha.maidai.chat.model.Bimp;
import com.hooha.maidai.chat.model.FileUtils;
import com.hooha.maidai.chat.model.MySelfInfo;
import com.hooha.maidai.chat.presenters.OKhttpHelper;

public class PublishedActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    private GridView noScrollgridview;
    private GridAdapter adapter;
    private TextView activity_selectimg_send;
    private String type = "图片";
    private String wenzi = "", time1 = "", wuliao1 = "", gaoxiao1 = "", zidingyi1 = "";
    private TextView time, wuliao, gaoxiao, zidingyi;
    private Button back;
    private EditText photo_weizi;
    private String width = "", height = "";
    private String TAG = "PublishedActivity上传图片";
    private ImageView send_duanzi;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectimg);
        Init();
    }

    public void Init() {
        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        time = (TextView) findViewById(R.id.time);
        wuliao = (TextView) findViewById(R.id.wuliao);
        gaoxiao = (TextView) findViewById(R.id.gaoxiao);
        photo_weizi = (EditText) findViewById(R.id.photo_weizi);
        zidingyi = (TextView) findViewById(R.id.zidingyi);
        back = (Button) findViewById(R.id.back);
        zidingyi.addTextChangedListener(this);
        back.setOnClickListener(this);

        time.setOnClickListener(this);
        gaoxiao.setOnClickListener(this);
        wuliao.setOnClickListener(this);
        zidingyi.setOnClickListener(this);
        photo_weizi.setOnClickListener(this);
        photo_weizi.addTextChangedListener(this);

        adapter = new GridAdapter(this);
        adapter.update();
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (arg2 == Bimp.bmp.size()) {
                    new PopupWindows(PublishedActivity.this, noScrollgridview);
                } else {
                    Intent intent = new Intent(PublishedActivity.this,
                            PhotoActivity.class);
                    intent.putExtra("ID", arg2);
                    startActivity(intent);
                }
            }
        });
        activity_selectimg_send = (TextView) findViewById(R.id.activity_selectimg_send);
        activity_selectimg_send.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                final List<String> list = new ArrayList<String>();
                final ArrayList<String> pathbyte = new ArrayList<String>();
//                final ArrayList<Bitmap> pa=new ArrayList<Bitmap>();
                for (int i = 0; i < Bimp.drr.size(); i++) {
                    String Str = Bimp.drr.get(i).substring(
                            Bimp.drr.get(i).lastIndexOf("/") + 1,
                            Bimp.drr.get(i).lastIndexOf("."));
                    list.add(FileUtils.SDPATH + Str + ".PNG");
                    path = list.get(i).toString();
                    Log.d(TAG, "onClick: path" + path);
                    String enToStr = null;
                    Bitmap bitmap = Bimp.bmp.get(i);
                    enToStr = bitmapToBase64(bitmap);
                    Log.d(TAG, "onClick: Bimp" + i + bitmap);
                    Log.d(TAG, "onClick: list" + Bimp.bmp);
                    pathbyte.add(enToStr);
                    Log.d(TAG, "onClick: pathbype" + pathbyte.get(i));
                }

                if (list.size() < 2) {

                    height = String.valueOf(Bimp.bmp.get(0).getHeight());
                    width = String.valueOf(Bimp.bmp.get(0).getWidth());
                    Log.d(TAG, "onClick: height" + "width" + width + "height" + height);


                }
//                if (pathbyte.size() < 2) {
//                    try {
//                        width = String.valueOf((Bimp.revitionImageSize(pathbyte.get(0)).getWidth()));
//                        height = String.valueOf(Bimp.revitionImageSize(pathbyte.get(0)).getHeight());
//                        Log.d(TAG, "onClick: width"+width+height);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
                Thread iThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "onClick: width" + width + height);
                        OKhttpHelper.getInstance().Postphoto(type, wenzi, pathbyte.toString().trim(), width, height,
                                MySelfInfo.getInstance().getId(), time1 + " " + wuliao1 + " " + zidingyi1 + " " + gaoxiao1);
                        Log.d(TAG, "run: 卖呆上传图片" + type + "," + wenzi + "," + pathbyte.toString().trim() + ","
                                + width + "," + height + "," + MySelfInfo.getInstance().getId() + "," + time1 + ","
                                + wuliao1 + "," + zidingyi1 + "," + gaoxiao1);
                        Log.d(TAG, "pathbyte.size:    " + pathbyte.size());
                    }
                });
                iThread.start();
                try {
                    iThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplication(), "图片发送成功", Toast.LENGTH_SHORT).show();
                finish();
                // 高清的压缩图片全部就在  list 路径里面了
                // 高清的压缩过的 bmp 对象  都在 Bimp.bmp里面
                // 完成上传服务器后 .........
                FileUtils.deleteDir();
            }

        });
    }

    //bitmap转String用base64加密
    public String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        if (bitmap != null) {
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

            //baos.flush();
            //baos.close();

            byte[] bitmapBytes = baos.toByteArray();
            result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
        }

        try {
            if (baos != null) {
                baos.flush();
                baos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.duanzi:


                Log.d(TAG, "onClick: " + wenzi);
                break;

            case R.id.time:
                time1 = time.getText().toString();


                break;
            case R.id.wuliao:
                wuliao1 = wuliao.getText().toString();
                break;
            case R.id.gaoxiao:
                gaoxiao1 = gaoxiao.getText().toString();
                break;
            case R.id.zidingyi:
                showspecialityDialog();


                break;
            case R.id.back:
                finish();
                break;
            case R.id.send_duanzi:
                break;
        }
    }

    public void showspecialityDialog() {
        AlertDialog.Builder sbuilder = new AlertDialog.Builder(this);
        final View item_person = LayoutInflater.from(this).inflate(R.layout.item_person, null);
        sbuilder.setView(item_person);
        sbuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //事件处理

                EditText input_setting = (EditText) item_person.findViewById(R.id.input_setting);
                zidingyi.setText(input_setting.getText().toString().trim());


            }
        });
        sbuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        sbuilder.create().show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        wenzi = photo_weizi.getText().toString();

        zidingyi1 = zidingyi.getText().toString();
        Log.d(TAG, "afterTextChanged: " + wenzi);
    }

    @SuppressLint("HandlerLeak")

    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater; // 视图容器
        private int selectedPosition = -1;// 选中的位置
        private boolean shape;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update() {
            loading();
        }

        public int getCount() {
            return (Bimp.bmp.size() + 1);
        }

        public Object getItem(int arg0) {

            return null;
        }

        public long getItemId(int arg0) {

            return 0;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        /**
         * ListView Item设置
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            final int coord = position;
            ViewHolder holder = null;
            if (convertView == null) {

                convertView = inflater.inflate(R.layout.item_published_grida,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position == Bimp.bmp.size()) {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.icon_addpic_unfocused));
                if (position == 9) {
                    holder.image.setVisibility(View.GONE);
                }
            } else {
                holder.image.setImageBitmap(Bimp.bmp.get(position));
            }

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        adapter.notifyDataSetChanged();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        public void loading() {
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        if (Bimp.max == Bimp.drr.size()) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            break;
                        } else {
                            try {
                                String path = Bimp.drr.get(Bimp.max);
                                System.out.println(path);
                                Bitmap bm = Bimp.revitionImageSize(path);
                                Bimp.bmp.add(bm);
                                String newStr = path.substring(
                                        path.lastIndexOf("/") + 1,
                                        path.lastIndexOf("."));
                                FileUtils.saveBitmap(bm, "" + newStr);
                                Bimp.max += 1;
                                Message message = new Message();
                                message.what = 1;
                                handler.sendMessage(message);
                            } catch (IOException e) {

                                e.printStackTrace();
                            }
                        }
                    }
                }
            }).start();
        }
    }

    public String getString(String s) {
        String path = null;
        if (s == null)
            return "";
        for (int i = s.length() - 1; i > 0; i++) {
            s.charAt(i);
        }
        return path;
    }

    protected void onRestart() {
        adapter.update();
        super.onRestart();
    }

    public class PopupWindows extends PopupWindow {

        public PopupWindows(Context mContext, View parent) {

            View view = View
                    .inflate(mContext, R.layout.item_popupwindows, null);
            view.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.fade_ins));
            LinearLayout ll_popup = (LinearLayout) view
                    .findViewById(R.id.ll_popup);
            ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.push_bottom_in_2));

            setWidth(LayoutParams.FILL_PARENT);
            setHeight(LayoutParams.FILL_PARENT);
            setBackgroundDrawable(new BitmapDrawable());
            setFocusable(true);
            setOutsideTouchable(true);
            setContentView(view);
            showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            update();

            Button bt1 = (Button) view
                    .findViewById(R.id.item_popupwindows_camera);
            Button bt2 = (Button) view
                    .findViewById(R.id.item_popupwindows_Photo);
            Button bt3 = (Button) view
                    .findViewById(R.id.item_popupwindows_cancel);
            bt1.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    photo();
                    dismiss();
                }
            });
            bt2.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(PublishedActivity.this,
                            TestPicActivity.class);
                    startActivity(intent);
                    dismiss();
                }
            });
            bt3.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    dismiss();
                }
            });

        }
    }

    private static final int TAKE_PICTURE = 0x000000;
    private String path = "";

    public void photo() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory()
                + "/myimage/", String.valueOf(System.currentTimeMillis())
                + ".jpg");
        path = file.getPath();
        Uri imageUri = Uri.fromFile(file);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (Bimp.drr.size() < 9 && resultCode == -1) {
                    Bimp.drr.add(path);
                }
                break;
        }
    }

}
