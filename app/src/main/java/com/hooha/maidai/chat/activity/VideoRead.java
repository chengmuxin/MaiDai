package com.hooha.maidai.chat.activity;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.model.MySelfInfo;
import com.hooha.maidai.chat.presenters.OKhttpHelper;
import com.hooha.maidai.chat.utils.UploadUtil;
import com.lidroid.xutils.http.RequestParams;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class VideoRead extends Activity implements SurfaceHolder.Callback, View.OnClickListener, TextWatcher {

    /**
     * Called when the activity is first created.
     */
    private MediaPlayer player;
    private SurfaceView surface;
    private SurfaceHolder surfaceHolder;
    private LinearLayout play;
    private String path = null;
    private TextView upload;
    private String TAG = "VideoRead";
    private String type = "视频";
    private EditText photo_weizi;
    private String wenzi = "", time1 = "", wuliao1 = "", gaoxiao1 = "", zidingyi1 = "";
    private TextView time, wuliao, gaoxiao, zidingyi;
    private ImageView back;
    String basepath;
    String picHeight, picwidth;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoshang);
        play = (LinearLayout) findViewById(R.id.button1);//播放
//        pause=(Button)findViewById(R.id.button2);
//        stop=(Button)findViewById(R.id.button3);
        upload = (TextView) findViewById(R.id.activity_selectimg_send);//上传
        surface = (SurfaceView) findViewById(R.id.surface);
        time = (TextView) findViewById(R.id.time);
        wuliao = (TextView) findViewById(R.id.wuliao);
        gaoxiao = (TextView) findViewById(R.id.gaoxiao);
        photo_weizi = (EditText) findViewById(R.id.photo_weizi);
        zidingyi = (TextView) findViewById(R.id.zidingyi);
        back = (ImageView) findViewById(R.id.back);
        zidingyi.addTextChangedListener(this);
        back.setOnClickListener(this);

        time.setOnClickListener(this);
        gaoxiao.setOnClickListener(this);
        wuliao.setOnClickListener(this);
        zidingyi.setOnClickListener(this);
        surfaceHolder = surface.getHolder();   //SurfaceHolder是SurfaceView的控制接口
        surfaceHolder.addCallback(this);     //因为这个类实现了SurfaceHolder.Callback接口，所以回调参数直接this
        surfaceHolder.setFixedSize(320, 240);//显示的分辨率,不设置为视频默认
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//Surface类型
        play.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                player.start();
            }
        });
        upload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //上传

                uploadFile();

                Toast.makeText(getApplication(), "上传成功", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
    }


    //文件上传
    @SuppressLint("SdCardPath")
    public void uploadFile() {

        path = this.getIntent().getStringExtra("path");
//        String userId = this.getIntent().getStringExtra("userId");
        Log.i("视频上传", "path" + path.toString());

        final String filePath = "/sdcard/maidai/video" + path.substring(path.lastIndexOf("/"));
        try {
            basepath = encodeBase64File(filePath);
            MediaMetadataRetriever media = new MediaMetadataRetriever();
            media.setDataSource(filePath);
            Bitmap bitmap = media.getFrameAtTime();
            picHeight = bitmap.getHeight() + "";
            picwidth = bitmap.getWidth() + "";
            final String picture = bitmapToBase64(bitmap);//第一帧
            Log.i("视频上传", "filePath" + filePath.toString());
            Thread iThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.i("视频上传", "filePath" + picture.toString() + picwidth + picHeight);
                        OKhttpHelper.getInstance().Postvideo(type, wenzi, basepath.toString(), picture.toString(), picHeight.trim(), picwidth.trim(),
                                MySelfInfo.getInstance().getId(), time1 + " " + wuliao1 + " " + zidingyi1 + " " + gaoxiao1);

                        Log.d(TAG, "run: 卖呆上传图片" + type + "," + wenzi + "," + basepath.toString() + picture.toString() + picHeight.trim() + picwidth.trim() +
                                MySelfInfo.getInstance().getId() + "," + time1 + ","
                                + wuliao1 + "," + zidingyi1 + "," + gaoxiao1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            iThread.start();
            try {
                iThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
//        try {
//            String basepath=encodeBase64File(filePath);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        // RequestParams params = new RequestParams();
//        params.addBodyParameter("type", "视频");
//        params.addBodyParameter("userId", userId);
//        Log.i("视频上传", "uploadFile: params" + params.toString());
//        Toast.makeText(getApplication(), params.toString(), Toast.LENGTH_SHORT).show();
//        params.addBodyParameter(filePath.replace("/", ""), new File(filePath));
//        UploadUtil load = new UploadUtil();
//        load.uploadMethod(params, uploadHost);
    }

    @SuppressLint("SdCardPath")
    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
//必须在surface创建后才能初始化MediaPlayer,否则不会显示图像
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setDisplay(surfaceHolder);
        //设置显示视频显示在SurfaceView上
        try {
            // 新建Bundle对象
            path = this.getIntent().getStringExtra("path");
            player.setDataSource("/sdcard/im/video" + path.substring(path.lastIndexOf("/")));
            player.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//bitmap加密

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
    public void surfaceDestroyed(SurfaceHolder arg0) {

    }

    private String encodeBase64File(String path) throws Exception {
        Log.e("aaaaa", path);
        Toast.makeText(getApplicationContext(), "文件路径" + path, Toast.LENGTH_SHORT).show();
        File file = new File(path);
        Log.e("aaaaa", file.exists() + "");
        Toast.makeText(getApplicationContext(), "文件存在" + file.exists(), Toast.LENGTH_SHORT)
                .show();
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        String date = Base64.encodeToString(buffer, Base64.DEFAULT);
        Log.e("上传视频", date);
        Log.e("上传视频", "结束");
        return date;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player.isPlaying()) {
            player.stop();
        }
        player.release();
        //Activity销毁时停止播放，释放资源。不做这个操作，即使退出还是能听到视频播放的声音
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


}
