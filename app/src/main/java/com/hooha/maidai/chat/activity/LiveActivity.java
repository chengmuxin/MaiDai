package com.hooha.maidai.chat.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.adapters.ChatMsgListAdapter;
import com.hooha.maidai.chat.adapters.FlowerAdapter;
import com.hooha.maidai.chat.adapters.GratuityAdapter;
import com.hooha.maidai.chat.avcontrollers.QavsdkControl;
import com.hooha.maidai.chat.base.BaseActivity;
import com.hooha.maidai.chat.model.Bang;
import com.hooha.maidai.chat.model.ChatEntity;
import com.hooha.maidai.chat.model.CurLiveInfo;
import com.hooha.maidai.chat.model.LiveInfoJson;
import com.hooha.maidai.chat.model.MySelfInfo;
import com.hooha.maidai.chat.presenters.EnterLiveHelper;
import com.hooha.maidai.chat.presenters.LiveHelper;
import com.hooha.maidai.chat.presenters.OKhttpHelper;
import com.hooha.maidai.chat.presenters.ProfileInfoHelper;
import com.hooha.maidai.chat.presenters.viewinface.EnterQuiteRoomView;
import com.hooha.maidai.chat.presenters.viewinface.LiveView;
import com.hooha.maidai.chat.presenters.viewinface.ProfileView;
import com.hooha.maidai.chat.utils.Constant;
import com.hooha.maidai.chat.utils.GlideCircleTransform;
import com.hooha.maidai.chat.utils.SxbLog;
import com.hooha.maidai.chat.utils.UIUtils;
import com.hooha.maidai.chat.view.CustomSwitch;
import com.hooha.maidai.chat.view.HeartLayout;
import com.hooha.maidai.chat.view.InputTextMsgDialog;
import com.hooha.maidai.chat.view.MembersDialog;
import com.tencent.TIMMessage;
import com.tencent.TIMTextElem;
import com.tencent.TIMUserProfile;
import com.tencent.av.TIMAvManager;
import com.tencent.av.sdk.AVView;
import com.tencent.av.utils.PhoneStatusTools;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Live直播类
 */
public class LiveActivity extends BaseActivity implements EnterQuiteRoomView, LiveView, View.OnClickListener, ProfileView, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = LiveActivity.class.getSimpleName();
    private static final int GETPROFILE_JOIN = 0x200;
    private static final int REFERSH = 1;
    private static final int GAME_COUNTDOWN = 2;
    private static final int GAME_RESULT = 3;
    private static final int JOIN_NO = 4;
    private static final int JOIN_ON = 5;

    private EnterLiveHelper mEnterRoomHelper;
    private ProfileInfoHelper mUserInfoHelper;
    private LiveHelper mLiveHelper;
    private InputMethodManager imm;

    private ArrayList<ChatEntity> mArrayListChatEntity;
    private ChatMsgListAdapter mChatMsgListAdapter;
    private static final int MINFRESHINTERVAL = 500;
    private static final int UPDAT_WALL_TIME_TIMER_TASK = 1;
    private static final int TIMEOUT_INVITE = 2;
    private boolean mBoolRefreshLock = false;
    private boolean mBoolNeedRefresh = false;
    private final Timer mTimer = new Timer();
    private ArrayList<ChatEntity> mTmpChatList = new ArrayList<ChatEntity>();//缓冲队列
    private TimerTask mTimerTask = null;
    private static final int REFRESH_LISTVIEW = 5;
    private Dialog mMemberDg, closeCfmDg, inviteDg;
    private HeartLayout mHeartLayout;
    private TextView mLikeTv;
    private HeartBeatTask mHeartBeatTask;//心跳
    private ImageView mHeadIcon;
    private TextView mHostNameTv;
    private LinearLayout mHostLayout, mHostLeaveLayout;
    private TextView liaotianTv, xiangqingTv, gongxianTv, startgameTv;
    private LinearLayout liaotianLayout, xiangqingLayout, gongxianLayout, memberLayout, videoLayout;
    private EditText inputEt;
    private Button sendBtn;
    private final int REQUEST_PHONE_PERMISSIONS = 0;
    private long mSecond = 0;
    private String formatTime;
    private Timer mHearBeatTimer, mVideoTimer;
    private VideoTimerTask mVideoTimerTask;//计时器
    private TextView mVideoTime;
    private ObjectAnimator mObjAnim;
    private ImageView mRecordBall;
    private int thumbUp = 0;
    private long admireTime = 0;
    private int watchCount = 0;
    private static boolean mBeatuy = false;
    private static boolean mWhite = true;
    private boolean bCleanMode = false;
    private boolean mProfile;
    private boolean bFirstRender = true;

    private String backGroundId;

    private TextView tvMembers;
    private TextView tvAdmires;

    private Dialog mDetailDialog;

    private ArrayList<String> mRenderUserList = new ArrayList<>();
    private int type;
    private final int Lp = 1;
    private final int Rp = 2;
    private final int videoNum = 3;
    private boolean hua = false;
    boolean stopThread = false;

    /**
     * 启动一个自动线程，用来检测房间主播人数，以便控制加入对战按钮状态，该线程在退出房间时应销毁
     */
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            while (!stopThread) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!mRenderUserList.contains(MySelfInfo.getInstance().getId())) {
                    if (mRenderUserList.size() >= videoNum) {
                        Message message = new Message();
                        message.what = JOIN_NO;
                        handler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = JOIN_ON;
                        handler.sendMessage(message);
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   // 不锁屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_live);
        type = MySelfInfo.getInstance().getRoomType();
        checkPermission();
        mRenderUserList.add("system");
        //进出房间的协助类
        mEnterRoomHelper = new EnterLiveHelper(this, this);
        //房间内的交互协助类
        mLiveHelper = new LiveHelper(this, this);
        // 用户资料类
        mUserInfoHelper = new ProfileInfoHelper(this);

        initView();
        registerReceiver();
        backGroundId = CurLiveInfo.getHostID();
        //进入房间流程
        mEnterRoomHelper.startEnterRoom();

        //QavsdkControl.getInstance().setCameraPreviewChangeCallback();
        mLiveHelper.setCameraPreviewChangeCallback();
        registerOrientationListener();
        Log.d(TAG, "onCreate is OK");

        new Thread(mRunnable).start();
    }


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case UPDAT_WALL_TIME_TIMER_TASK:
                    updateWallTime();
                    break;
                case REFRESH_LISTVIEW:
                    doRefreshListView();
                    break;
                case TIMEOUT_INVITE:
                    String id = "" + msg.obj;
                    cancelInviteView(id);
                    mLiveHelper.sendGroupMessage(Constant.AVIMCMD_MULTI_HOST_CANCELINVITE, id);
                    break;
            }
            return false;
        }
    });

    /**
     * 时间格式化
     */
    private void updateWallTime() {
        String hs, ms, ss;

        long h, m, s;
        h = mSecond / 3600;
        m = (mSecond % 3600) / 60;
        s = (mSecond % 3600) % 60;
        if (h < 10) {
            hs = "0" + h;
        } else {
            hs = "" + h;
        }

        if (m < 10) {
            ms = "0" + m;
        } else {
            ms = "" + m;
        }

        if (s < 10) {
            ss = "0" + s;
        } else {
            ss = "" + s;
        }
        if (hs.equals("00")) {
            formatTime = ms + ":" + ss;
        } else {
            formatTime = hs + ":" + ms + ":" + ss;
        }

        if (Constant.HOST == MySelfInfo.getInstance().getIdStatus() && null != mVideoTime) {
            SxbLog.i(TAG, " refresh time ");
            mVideoTime.setText(formatTime);
        }
    }


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //AvSurfaceView 初始化成功
            if (action.equals(Constant.ACTION_SURFACE_CREATED)) {
                //打开摄像头
                if (MySelfInfo.getInstance().getIdStatus() == Constant.HOST) {
                    mLiveHelper.openCameraAndMic();
                }

            }

            if (action.equals(Constant.ACTION_CAMERA_OPEN_IN_LIVE)) {//有人打开摄像头
                ArrayList<String> ids = intent.getStringArrayListExtra("ids");
                //如果是自己本地直接渲染
                for (String id : ids) {
                    if (id.equals(backGroundId)) {
//                        mHostLeaveLayout.setVisibility(View.GONE);
                    }
                    if (!mRenderUserList.contains(id)) {
                        mRenderUserList.add(id);
                    }

                    if (id.equals(MySelfInfo.getInstance().getId())) {
                        showVideoView(true, id);
                        return;
//                        ids.remove(id);
                    }
                }
                //其他人一并获取
                int requestCount = CurLiveInfo.getCurrentRequestCount();
                mLiveHelper.requestViewList(ids);
                requestCount = requestCount + ids.size();
                CurLiveInfo.setCurrentRequestCount(requestCount);
//                }
            }

            if (action.equals(Constant.ACTION_CAMERA_CLOSE_IN_LIVE)) {//有人关闭摄像头
                ArrayList<String> ids = intent.getStringArrayListExtra("ids");
                //如果是自己本地直接渲染
                for (String id : ids) {
                    mRenderUserList.remove(id);
                    if (id.equals(backGroundId)) {
//                        mHostLeaveLayout.setVisibility(View.VISIBLE);
                        return;
                    }
                }
            }

            if (action.equals(Constant.ACTION_SWITCH_VIDEO)) {//点击成员回调
                backGroundId = intent.getStringExtra(Constant.EXTRA_IDENTIFIER);
                SxbLog.v(TAG, "switch video enter with id:" + backGroundId);

                if (mRenderUserList.contains(backGroundId)) {
//                    mHostLeaveLayout.setVisibility(View.GONE);
                } else {
//                    mHostLeaveLayout.setVisibility(View.VISIBLE);
                }

//                if (MySelfInfo.getInstance().getIdStatus() == Constant.HOST) {//自己是主播
//                    if (backGroundId.equals(MySelfInfo.getInstance().getId())) {//背景是自己
////                        mHostCtrView.setVisibility(View.VISIBLE);
//                        videoLayout.setVisibility(View.INVISIBLE);
//                    } else {//背景是其他成员
////                        mHostCtrView.setVisibility(View.INVISIBLE);
//                        videoLayout.setVisibility(View.VISIBLE);
//                    }
//                } else {//自己成员方式
//                    if (backGroundId.equals(MySelfInfo.getInstance().getId())) {//背景是自己
//                        videoLayout.setVisibility(View.VISIBLE);
//                        memberLayout.setVisibility(View.INVISIBLE);
//                    } else if (backGroundId.equals(CurLiveInfo.getHostID())) {//主播自己
//                        videoLayout.setVisibility(View.INVISIBLE);
//                        memberLayout.setVisibility(View.VISIBLE);
//                    } else {
//                        videoLayout.setVisibility(View.INVISIBLE);
//                        memberLayout.setVisibility(View.INVISIBLE);
//                    }
//
//                }

            }
            if (action.equals(Constant.ACTION_HOST_LEAVE)) {//主播结束
                //quiteLivePassively();
            }


        }
    };

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_SURFACE_CREATED);
        intentFilter.addAction(Constant.ACTION_HOST_ENTER);
        intentFilter.addAction(Constant.ACTION_CAMERA_OPEN_IN_LIVE);
        intentFilter.addAction(Constant.ACTION_CAMERA_CLOSE_IN_LIVE);
        intentFilter.addAction(Constant.ACTION_SWITCH_VIDEO);
        intentFilter.addAction(Constant.ACTION_HOST_LEAVE);
        registerReceiver(mBroadcastReceiver, intentFilter);

    }

    private void unregisterReceiver() {
        unregisterReceiver(mBroadcastReceiver);
    }

    /**
     * 初始化UI
     */
    private View avView;
    private TextView BtnBack, BtnInput, Btnflash, BtnSwitch, BtnBeauty, BtnWhite, BtnMic, BtnScreen, BtnHeart, BtnNormal, mVideoChat, BtnCtrlVideo, BtnCtrlMic, BtnHungup, mBeautyConfirm;
    private TextView inviteView1, inviteView2, inviteView3;
    private ListView mListViewMsgItems;
    private LinearLayout mBeautySettings;
    private FrameLayout mFullControllerUi, mBackgound;
    private SeekBar mBeautyBar;
    private int mBeautyRate, mWhiteRate;
    private TextView pushBtn, recordBtn;
    private TextView Lflower, Lgratuity, Lheart, Rflower, Rgratuity, Rheart, JoinGame;
    private TextView xianhuaTv, dashangTv;
    private LinearLayout gongXianLay;
    private ListView flowerLv, gratuityLv;
    private ImageButton backBtn, fenxiangBtn;
    private FlowerAdapter flowerAdapter;
    private GratuityAdapter gratuityAdapter;
    private List<Bang> flowerList = new ArrayList<Bang>();
    private List<Bang> gratuityList = new ArrayList<Bang>();
    private SwipeRefreshLayout swipeLayoutFlower, swipeLayoutGratuity;
    private CustomSwitch kaiboSwitch;
    private TextView activeInfoTv;

    private void showHeadIcon(ImageView view, String avatar) {
        if (TextUtils.isEmpty(avatar)) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_avatar);
            Bitmap cirBitMap = UIUtils.createCircleImage(bitmap, 0);
            view.setImageBitmap(cirBitMap);
        } else {
            SxbLog.d(TAG, "load icon: " + avatar);
            RequestManager req = Glide.with(this);
            req.load(avatar).transform(new GlideCircleTransform(this)).into(view);
        }
    }

    /**
     * 初始化界面
     */
    private void initView() {
        backBtn = (ImageButton) findViewById(R.id.head_back);
        backBtn.setOnClickListener(this);
        fenxiangBtn = (ImageButton) findViewById(R.id.head_other);
        fenxiangBtn.setOnClickListener(this);
        //聊天，详情，贡献
        liaotianTv = (TextView) findViewById(R.id.tv_liaotian);
        xiangqingTv = (TextView) findViewById(R.id.tv_xiangqing);
        gongxianTv = (TextView) findViewById(R.id.tv_gongxian);
        startgameTv = (TextView) findViewById(R.id.tv_startgame);
        liaotianLayout = (LinearLayout) findViewById(R.id.layout_liaotian);
        xiangqingLayout = (LinearLayout) findViewById(R.id.layout_xiangqing);
        gongxianLayout = (LinearLayout) findViewById(R.id.layout_gongxian);
        liaotianTv.setTextColor(Color.rgb(253, 125, 0));
        liaotianLayout.setVisibility(View.VISIBLE);
        liaotianTv.setOnClickListener(this);
        xiangqingTv.setOnClickListener(this);
        gongxianTv.setOnClickListener(this);
        startgameTv.setOnClickListener(this);
        startgameTv.setClickable(false);
        inputEt = (EditText) findViewById(R.id.et_input);
        sendBtn = (Button) findViewById(R.id.btn_send);
        imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputEt.getText().length() > 0) {
                    sendText("" + inputEt.getText());
                    imm.showSoftInput(inputEt, InputMethodManager.SHOW_FORCED);
                    imm.hideSoftInputFromWindow(inputEt.getWindowToken(), 0);
                    inputEt.setText("");
//                    dismiss();
                } else {
                    Toast.makeText(LiveActivity.this, "input can not be empty!", Toast.LENGTH_LONG).show();
                }
            }
        });
        inputEt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != KeyEvent.ACTION_UP) {   // 忽略其它事件
                    return false;
                }

                switch (keyCode) {
                    case KeyEvent.KEYCODE_ENTER:
                        if (inputEt.getText().length() > 0) {
                            sendText("" + inputEt.getText());
                            imm.showSoftInput(inputEt, InputMethodManager.SHOW_FORCED);
                            imm.hideSoftInputFromWindow(inputEt.getWindowToken(), 0);
                            inputEt.setText("");
//                            dismiss();
                        } else {
                            Toast.makeText(LiveActivity.this, "input can not be empty!", Toast.LENGTH_LONG).show();
                        }
                        return true;
                    default:
                        return false;
                }
            }
        });

//        mHostCtrView = (LinearLayout) findViewById(R.id.host_bottom_layout);
//        mNomalMemberCtrView = (LinearLayout) findViewById(R.id.member_bottom_layout);
//        mVideoMemberCtrlView = (LinearLayout) findViewById(R.id.video_member_bottom_layout);
        //送花，打赏，点赞
        memberLayout = (LinearLayout) findViewById(R.id.member_bottom_layout);
        videoLayout = (LinearLayout) findViewById(R.id.video_member_bottom_layout);
        Lflower = (TextView) findViewById(R.id.tv_lflower);
        Lgratuity = (TextView) findViewById(R.id.tv_lgratuity);
        Lheart = (TextView) findViewById(R.id.tv_lheart);
        Rflower = (TextView) findViewById(R.id.tv_rflower);
        Rgratuity = (TextView) findViewById(R.id.tv_rgratuity);
        Rheart = (TextView) findViewById(R.id.tv_rheart);
        JoinGame = (TextView) findViewById(R.id.tv_joingame);
        JoinGame.setClickable(false);
        Lflower.setOnClickListener(this);
        Lgratuity.setOnClickListener(this);
        Lheart.setOnClickListener(this);
        Rflower.setOnClickListener(this);
        Rgratuity.setOnClickListener(this);
        Rheart.setOnClickListener(this);
        JoinGame.setOnClickListener(this);

        //详情
        activeInfoTv = (TextView) findViewById(R.id.active_info);
        activeInfoTv.setText(CurLiveInfo.getActiveInfo());
        kaiboSwitch = (CustomSwitch) findViewById(R.id.switch_kaibo);
        Log.d("cmx", "getIsremind: " + CurLiveInfo.getIsremind());
        boolean kaibo = false;
        if (CurLiveInfo.getIsremind() == 1) {
            kaibo = true;
        }
        kaiboSwitch.setChecked(kaibo, false);
        kaiboSwitch.setOnClickListener(this);

        //贡献榜
        gongXianLay = (LinearLayout) findViewById(R.id.layout_gongxianbang);
        xianhuaTv = (TextView) findViewById(R.id.tv_xianhua);
        dashangTv = (TextView) findViewById(R.id.tv_dashang);
        xianhuaTv.setOnClickListener(this);
        dashangTv.setOnClickListener(this);
        swipeLayoutFlower = (SwipeRefreshLayout) findViewById(R.id.swipe_container_flower);
        swipeLayoutGratuity = (SwipeRefreshLayout) findViewById(R.id.swipe_container_gratuity);
        flowerLv = (ListView) findViewById(R.id.lv_bang_flower);
        gratuityLv = (ListView) findViewById(R.id.lv_bang_gratuity);
        swipeLayoutFlower.setOnRefreshListener(this);
        swipeLayoutGratuity.setOnRefreshListener(this);
        swipeLayoutFlower.setColorSchemeResources(android.R.color.holo_orange_dark,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeLayoutGratuity.setColorSchemeResources(android.R.color.holo_orange_dark,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        new Thread(new Runnable() {
            @Override
            public void run() {
                flowerList.addAll(OKhttpHelper.getInstance().bangFlower("" + CurLiveInfo.getRoomNum()));
                gratuityList.addAll(OKhttpHelper.getInstance().bangGratuity("" + CurLiveInfo.getRoomNum()));
            }
        }).start();
        flowerAdapter = new FlowerAdapter(this, R.layout.item_bang_flower, flowerList);
        gratuityAdapter = new GratuityAdapter(this, R.layout.item_bang_gratuity, gratuityList);
        flowerLv.setAdapter(flowerAdapter);
        gratuityLv.setAdapter(gratuityAdapter);

//        mHostLeaveLayout = (LinearLayout) findViewById(R.id.ll_host_leave);
        mVideoChat = (TextView) findViewById(R.id.video_interact);
        mVideoTime = (TextView) findViewById(R.id.broadcasting_time);
        mHeadIcon = (ImageView) findViewById(R.id.head_icon);
//        mVideoMemberCtrlView.setVisibility(View.INVISIBLE);
        mHostNameTv = (TextView) findViewById(R.id.host_name);
        tvMembers = (TextView) findViewById(R.id.member_counts);
        tvAdmires = (TextView) findViewById(R.id.heart_counts);

//        BtnCtrlVideo = (TextView) findViewById(R.id.camera_controll);
//        BtnCtrlMic = (TextView) findViewById(R.id.mic_controll);
        BtnHungup = (TextView) findViewById(R.id.close_member_video);
//        BtnCtrlVideo.setOnClickListener(this);
//        BtnCtrlMic.setOnClickListener(this);
        BtnHungup.setOnClickListener(this);

        //for 测试用
        tvTipsMsg = (TextView) findViewById(R.id.qav_tips_msg);
        tvTipsMsg.setTextColor(Color.GREEN);
        paramTimer.schedule(task, 1000, 1000);


        if (MySelfInfo.getInstance().getIdStatus() == Constant.HOST) {
            memberLayout.setVisibility(View.GONE);
            mRecordBall = (ImageView) findViewById(R.id.record_ball);
            Btnflash = (TextView) findViewById(R.id.flash_btn);
            BtnSwitch = (TextView) findViewById(R.id.switch_cam);
            BtnBeauty = (TextView) findViewById(R.id.beauty_btn);
            BtnWhite = (TextView) findViewById(R.id.white_btn);
            BtnMic = (TextView) findViewById(R.id.mic_btn);
            BtnScreen = (TextView) findViewById(R.id.fullscreen_btn);
            mVideoChat.setVisibility(View.VISIBLE);
            Btnflash.setOnClickListener(this);
            BtnSwitch.setOnClickListener(this);
            BtnBeauty.setOnClickListener(this);
            BtnWhite.setOnClickListener(this);
            BtnMic.setOnClickListener(this);
            BtnScreen.setOnClickListener(this);
            mVideoChat.setOnClickListener(this);
            inviteView1 = (TextView) findViewById(R.id.invite_view1);
            inviteView2 = (TextView) findViewById(R.id.invite_view2);
            inviteView3 = (TextView) findViewById(R.id.invite_view3);
            inviteView1.setOnClickListener(this);
            inviteView2.setOnClickListener(this);
            inviteView3.setOnClickListener(this);

//            recordBtn = (TextView) findViewById(R.id.record_btn);
//            recordBtn.setOnClickListener(this);

            initBackDialog();
            initDetailDailog();
            initPushDialog();
            initRecordDialog();


            mMemberDg = new MembersDialog(this, R.style.floag_dialog, this);
            startRecordAnimation();
            showHeadIcon(mHeadIcon, MySelfInfo.getInstance().getAvatar());
            mBeautySettings = (LinearLayout) findViewById(R.id.qav_beauty_setting);
            mBeautyConfirm = (TextView) findViewById(R.id.qav_beauty_setting_finish);
            mBeautyConfirm.setOnClickListener(this);
            mBeautyBar = (SeekBar) (findViewById(R.id.qav_beauty_progress));
            mBeautyBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    SxbLog.d("SeekBar", "onStopTrackingTouch");
                    if (mProfile == mBeatuy) {
                        Toast.makeText(LiveActivity.this, "beauty " + mBeautyRate + "%", Toast.LENGTH_SHORT).show();//美颜度
                    } else {
                        Toast.makeText(LiveActivity.this, "white " + mWhiteRate + "%", Toast.LENGTH_SHORT).show();//美白度
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    SxbLog.d("SeekBar", "onStartTrackingTouch");
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    Log.i(TAG, "onProgressChanged " + progress);
                    if (mProfile == mBeatuy) {
                        mBeautyRate = progress;
                        QavsdkControl.getInstance().getAVContext().getVideoCtrl().inputBeautyParam(getBeautyProgress(progress));//美颜
                    } else {
                        mWhiteRate = progress;
                        QavsdkControl.getInstance().getAVContext().getVideoCtrl().inputWhiteningParam(getBeautyProgress(progress));//美白
                    }
                }
            });
        } else {
            LinearLayout llRecordTip = (LinearLayout) findViewById(R.id.record_tip);
            llRecordTip.setVisibility(View.GONE);
            mHostNameTv.setVisibility(View.VISIBLE);
            initInviteDialog();
            memberLayout.setVisibility(View.VISIBLE);
//            mHostCtrView.setVisibility(View.GONE);
//            BtnInput = (TextView) findViewById(R.id.message_input);
//            BtnInput.setOnClickListener(this);
//            mLikeTv = (TextView) findViewById(R.id.member_send_good);
//            mLikeTv.setOnClickListener(this);
            mVideoChat.setVisibility(View.GONE);
//            BtnScreen = (TextView) findViewById(R.id.clean_screen);

            List<String> ids = new ArrayList<>();
            ids.add(CurLiveInfo.getHostID());
            showHeadIcon(mHeadIcon, CurLiveInfo.getHostAvator());
            mHostNameTv.setText(UIUtils.getLimitString(CurLiveInfo.getHostName(), 10));

            mHostLayout = (LinearLayout) findViewById(R.id.head_up_layout);
            mHostLayout.setOnClickListener(this);
//            BtnScreen.setOnClickListener(this);
            if (CurLiveInfo.getIsStartGame() == 1) {
                Toast.makeText(LiveActivity.this, "游戏正在进行中，请耐心等待下一局开始", Toast.LENGTH_SHORT).show();
            }
        }
        BtnNormal = (TextView) findViewById(R.id.normal_btn);
        BtnNormal.setOnClickListener(this);
        mFullControllerUi = (FrameLayout) findViewById(R.id.controll_ui);
        avView = findViewById(R.id.av_video_layer_ui);//surfaceView;

        mListViewMsgItems = (ListView) findViewById(R.id.im_msg_listview);
        mArrayListChatEntity = new ArrayList<ChatEntity>();
        mChatMsgListAdapter = new ChatMsgListAdapter(this, mListViewMsgItems, mArrayListChatEntity);
        mListViewMsgItems.setAdapter(mChatMsgListAdapter);

        tvMembers.setText("" + CurLiveInfo.getMembers());
        tvAdmires.setText("" + CurLiveInfo.getAdmires());
    }

    private void sendText(String msg) {
        if (msg.length() == 0)
            return;
        try {
            byte[] byte_num = msg.getBytes("utf8");
            if (byte_num.length > 160) {
                Toast.makeText(this, "input message too long", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }
        TIMMessage Nmsg = new TIMMessage();
        TIMTextElem elem = new TIMTextElem();
        elem.setText(msg);
        if (Nmsg.addElement(elem) != 0) {
            return;
        }
        mLiveHelper.sendGroupText(Nmsg);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mLiveHelper.resume();
        QavsdkControl.getInstance().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLiveHelper.pause();
        QavsdkControl.getInstance().onPause();
    }

    @Override
    protected void onStop() {
        Log.d("cmx", "onStop: OK");
        super.onStop();
        if (mRenderUserList.contains(MySelfInfo.getInstance().getId())) {
            cancelMemberView(MySelfInfo.getInstance().getId());
            startgameTv.setText(R.string.startgame);
            startgameTv.setBackgroundResource(R.color.colorGray4);
            startgameTv.setClickable(false);
            if (CurLiveInfo.getGameStatus() == 0) {
                //游戏过程中，主播下麦，应该做失败处理
            }
        }
    }

    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                swipeLayoutFlower.setRefreshing(false);
                swipeLayoutGratuity.setRefreshing(false);
                flowerList.clear();
                gratuityList.clear();
                flowerList.addAll(OKhttpHelper.getInstance().bangFlower("" + CurLiveInfo.getRoomNum()));
                gratuityList.addAll(OKhttpHelper.getInstance().bangGratuity("" + CurLiveInfo.getRoomNum()));
                Message message = new Message();
                message.what = REFERSH;
                handler.sendMessage(message);
            }
        }).start();
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFERSH:
                    flowerAdapter.notifyDataSetChanged();
                    gratuityAdapter.notifyDataSetChanged();
                    break;
                case GAME_COUNTDOWN:
                    startgameTv.setText(msg.getData().getString("time"));
                    break;
                case GAME_RESULT:
                    refreshTextListView("系统:", msg.getData().getString("result"), Constant.TEXT_TYPE);
                    CurLiveInfo.setGameStatus(-1);
                    hua = false;
                    if (mRenderUserList.contains(MySelfInfo.getInstance().getId())) {
                        startgameTv.setText(R.string.ready);
                        startgameTv.setBackgroundResource(R.color.theme_orange);
                        startgameTv.setClickable(true);
                    } else {
                        startgameTv.setText(R.string.startgame);
                    }
                    break;
                case JOIN_NO:
                    Log.d(TAG, "JOIN_NO: ");
                    JoinGame.setVisibility(View.INVISIBLE);
                    break;
                case JOIN_ON:
                    Log.d(TAG, "JOIN_ON: ");
                    JoinGame.setVisibility(View.VISIBLE);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }

        }
    };

    /**
     * 直播心跳
     */
    private class HeartBeatTask extends TimerTask {
        @Override
        public void run() {
            String host = CurLiveInfo.getHostID();
            SxbLog.i(TAG, "HeartBeatTask " + host);
            OKhttpHelper.getInstance().sendHeartBeat(host, CurLiveInfo.getMembers(), CurLiveInfo.getAdmires(), 0);
        }
    }

    /**
     * 记时器
     */
    private class VideoTimerTask extends TimerTask {
        public void run() {
            SxbLog.i(TAG, "timeTask ");
            ++mSecond;
            if (MySelfInfo.getInstance().getIdStatus() == Constant.HOST)
                mHandler.sendEmptyMessage(UPDAT_WALL_TIME_TIMER_TASK);
        }
    }

    @Override
    protected void onDestroy() {
        Log.d("cmx", "onDestroy: OK");
        //通知其他用户自己退出了
        mLiveHelper.perpareQuitRoom(true);

        stopThread = true;

        watchCount = 0;
        super.onDestroy();
        if (null != mHearBeatTimer) {
            mHearBeatTimer.cancel();
            mHearBeatTimer = null;
        }
        if (null != mVideoTimer) {
            mVideoTimer.cancel();
            mVideoTimer = null;
        }
        if (null != paramTimer) {
            paramTimer.cancel();
            paramTimer = null;
        }


        inviteViewCount = 0;
        thumbUp = 0;
        CurLiveInfo.setMembers(0);
        CurLiveInfo.setAdmires(0);
        CurLiveInfo.setCurrentRequestCount(0);
        unregisterReceiver();
        mLiveHelper.onDestory();
        mEnterRoomHelper.onDestory();
        QavsdkControl.getInstance().clearVideoMembers();
        QavsdkControl.getInstance().onDestroy();
    }


    /**
     * 点击Back键
     */
    @Override
    public void onBackPressed() {
        if (mRenderUserList.contains(MySelfInfo.getInstance().getId())) {
            if (CurLiveInfo.getGameStatus() >= 0) {
                new AlertDialog.Builder(LiveActivity.this).setTitle("系统提示")//设置对话框标题
                        .setMessage("游戏正在进行，退出将不会保存游戏数据，是否继续！")//设置显示的内容
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加确定按钮
                            @Override
                            public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                cancelMemberView(MySelfInfo.getInstance().getId());
                                startgameTv.setText(R.string.startgame);
                                startgameTv.setBackgroundResource(R.color.colorGray4);
                                startgameTv.setClickable(false);
                            }
                        }).setNegativeButton("返回", new DialogInterface.OnClickListener() {//添加返回按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//响应事件
                        Log.i("cmx", " 请保存数据！");
                    }
                }).show();//在按键响应事件中显示此对话框
            } else {
                cancelMemberView(MySelfInfo.getInstance().getId());
                startgameTv.setText(R.string.startgame);
                startgameTv.setBackgroundResource(R.color.colorGray4);
                startgameTv.setClickable(false);
            }
        } else {
            quiteLiveByPurpose();
        }
    }

    /**
     * 主动退出直播
     */
    private void quiteLiveByPurpose() {
        if (MySelfInfo.getInstance().getIdStatus() == Constant.HOST) {
            if (backDialog.isShowing() == false)
                backDialog.show();


        } else {
            mLiveHelper.perpareQuitRoom(true);
//            mEnterRoomHelper.quiteLive();
        }
    }


    private Dialog backDialog;

    private void initBackDialog() {
        backDialog = new Dialog(this, R.style.dialog);
        backDialog.setContentView(R.layout.dialog_end_live);
        TextView tvSure = (TextView) backDialog.findViewById(R.id.btn_sure);
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果是直播，发消息
                if (null != mLiveHelper) {
                    mLiveHelper.perpareQuitRoom(true);
                    if (isPushed) {
                        mLiveHelper.stopPushAction();
                    }
                }
                backDialog.dismiss();
            }
        });
        TextView tvCancel = (TextView) backDialog.findViewById(R.id.btn_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backDialog.cancel();
            }
        });
//        backDialog.show();
    }

    /**
     * 被动退出直播
     */
    private void quiteLivePassively() {
        Toast.makeText(this, "Host leave Live ", Toast.LENGTH_SHORT);
        mLiveHelper.perpareQuitRoom(false);
//        mEnterRoomHelper.quiteLive();
    }

    @Override
    public void readyToQuit() {
        mEnterRoomHelper.quiteLive();
    }

    /**
     * 完成进出房间流程
     *
     * @param id_status
     * @param isSucc
     */
    @Override
    public void enterRoomComplete(int id_status, boolean isSucc) {
        Log.d(TAG, "enterRoomComplete: " + "EnterRoom  [" + id_status + "] isSucc [" + isSucc + "]");
        //必须得进入房间之后才能初始化UI
        mEnterRoomHelper.initAvUILayer(avView);

        //设置预览回调，修正摄像头镜像
        mLiveHelper.setCameraPreviewChangeCallback();
        if (isSucc == true) {
            //IM初始化
            mLiveHelper.initTIMListener("" + CurLiveInfo.getRoomNum());

            if (id_status == Constant.HOST) {//主播方式加入房间成功
                //开启摄像头渲染画面
                SxbLog.i(TAG, "createlive enterRoomComplete isSucc" + isSucc);
            } else {
                //发消息通知上线
                mLiveHelper.sendGroupMessage(Constant.AVIMCMD_EnterLive, "");
            }
        }
    }

    @Override
    public void quiteRoomComplete(int id_status, boolean succ, LiveInfoJson liveinfo) {
        if (MySelfInfo.getInstance().getIdStatus() == Constant.HOST) {
            if ((getBaseContext() != null) && (null != mDetailDialog) && (mDetailDialog.isShowing() == false)) {
                mDetailTime.setText(formatTime);
                mDetailAdmires.setText("" + CurLiveInfo.getAdmires());
                mDetailWatchCount.setText("" + watchCount);
                mDetailDialog.show();
            }
        } else {
            finish();
        }
    }


    private TextView mDetailTime, mDetailAdmires, mDetailWatchCount;

    private void initDetailDailog() {
        mDetailDialog = new Dialog(this, R.style.dialog);
        mDetailDialog.setContentView(R.layout.dialog_live_detail);
        mDetailTime = (TextView) mDetailDialog.findViewById(R.id.tv_time);
        mDetailAdmires = (TextView) mDetailDialog.findViewById(R.id.tv_admires);
        mDetailWatchCount = (TextView) mDetailDialog.findViewById(R.id.tv_members);

        mDetailDialog.setCancelable(false);

        TextView tvCancel = (TextView) mDetailDialog.findViewById(R.id.btn_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDetailDialog.dismiss();
                finish();
            }
        });
//        mDetailDialog.show();
    }

    /**
     * 成员状态变更
     *
     * @param id
     * @param name
     */
    @Override
    public void memberJoin(String id, String name) {
        watchCount++;
        refreshTextListView(TextUtils.isEmpty(name) ? id : name, "进入房间", Constant.MEMBER_ENTER);

        CurLiveInfo.setMembers(CurLiveInfo.getMembers() + 1);
        tvMembers.setText("" + CurLiveInfo.getMembers());
    }

    @Override
    public void memberQuit(String id, String name) {
        refreshTextListView(TextUtils.isEmpty(name) ? id : name, "离开房间", Constant.MEMBER_EXIT);

        if (CurLiveInfo.getMembers() > 1) {
            CurLiveInfo.setMembers(CurLiveInfo.getMembers() - 1);
            tvMembers.setText("" + CurLiveInfo.getMembers());
        }

        //如果存在视频互动，取消
        QavsdkControl.getInstance().closeMemberView(id);
    }

    @Override
    public void hostLeave(String id, String name) {
        refreshTextListView(TextUtils.isEmpty(name) ? id : name, "暂离", Constant.HOST_LEAVE);
    }

    @Override
    public void hostBack(String id, String name) {
        refreshTextListView(TextUtils.isEmpty(name) ? id : name, "回来了", Constant.HOST_BACK);
    }

    /**
     * 有成员退群
     *
     * @param list 成员ID 列表
     */
    @Override
    public void memberQuiteLive(String[] list) {
        if (list == null) return;
        for (String id : list) {
            SxbLog.i(TAG, "memberQuiteLive id " + id);
            if (CurLiveInfo.getHostID().equals(id)) {
                if (MySelfInfo.getInstance().getIdStatus() == Constant.MEMBER)
                    quiteLivePassively();
            }
        }
    }


    /**
     * 有成员入群
     *
     * @param list 成员ID 列表
     */
    @Override
    public void memberJoinLive(final String[] list) {
    }

    @Override
    public void alreadyInLive(String[] list) {
        for (String id : list) {
            if (id.equals(MySelfInfo.getInstance().getId())) {
                QavsdkControl.getInstance().setSelfId(MySelfInfo.getInstance().getId());
                QavsdkControl.getInstance().setLocalHasVideo(true, MySelfInfo.getInstance().getId());
            } else {
                QavsdkControl.getInstance().setRemoteHasVideo(true, id, AVView.VIDEO_SRC_TYPE_CAMERA);
            }
        }

    }

    /**
     * 红点动画
     */
    private void startRecordAnimation() {
        mObjAnim = ObjectAnimator.ofFloat(mRecordBall, "alpha", 1f, 0f, 1f);
        mObjAnim.setDuration(1000);
        mObjAnim.setRepeatCount(-1);
        mObjAnim.start();
    }

    private static int index = 0;

    /**
     * 加载视频数据
     *
     * @param isLocal 是否是本地数据
     * @param id      身份
     */
    @Override
    public void showVideoView(boolean isLocal, String id) {
        SxbLog.i(TAG, "showVideoView " + id);

        //渲染本地Camera
        if (isLocal == true) {
            SxbLog.i(TAG, "showVideoView host :" + MySelfInfo.getInstance().getId());
            QavsdkControl.getInstance().setSelfId(MySelfInfo.getInstance().getId());
            QavsdkControl.getInstance().setLocalHasVideo(true, MySelfInfo.getInstance().getId());
            //主播通知用户服务器
            if (MySelfInfo.getInstance().getIdStatus() == Constant.HOST) {
                if (bFirstRender) {
                    mEnterRoomHelper.notifyServerCreateRoom(type);

                    //主播心跳
                    mHearBeatTimer = new Timer(true);
                    mHeartBeatTask = new HeartBeatTask();
                    mHearBeatTimer.schedule(mHeartBeatTask, 1000, 3 * 1000);

                    //直播时间
                    mVideoTimer = new Timer(true);
                    mVideoTimerTask = new VideoTimerTask();
                    mVideoTimer.schedule(mVideoTimerTask, 1000, 1000);
                    bFirstRender = false;
                }
            }
        } else {
//            QavsdkControl.getInstance().addRemoteVideoMembers(id);
            QavsdkControl.getInstance().setRemoteHasVideo(true, id, AVView.VIDEO_SRC_TYPE_CAMERA);
        }

    }


    private float getBeautyProgress(int progress) {
        SxbLog.d("shixu", "progress: " + progress);
        return (9.0f * progress / 100.0f);
    }


    @Override
    public void showInviteDialog() {
        if ((inviteDg != null) && (getBaseContext() != null) && (inviteDg.isShowing() != true)) {
            inviteDg.show();
        }
    }

    @Override
    public void hideInviteDialog() {
        if ((inviteDg != null) && (inviteDg.isShowing() == true)) {
            inviteDg.dismiss();
        }
        JoinGame.setVisibility(View.VISIBLE);
    }


    @Override
    public void refreshText(String text, String name) {
        if (text != null) {
            refreshTextListView(name, text, Constant.TEXT_TYPE);
        }
    }

    @Override
    public void refreshThumbUp(String identifier) {
        CurLiveInfo.setHearts(CurLiveInfo.getHearts() + 1);
    }

    @Override
    public void refreshThumbUp2(String identifier) {
        CurLiveInfo.setHearts2(CurLiveInfo.getHearts2() + 1);
    }

    @Override
    public void refreshFlower(String identifier) {
        CurLiveInfo.setFlowers(CurLiveInfo.getFlowers() + 1);
    }

    @Override
    public void refreshFlower2(String identifier) {
        CurLiveInfo.setFlowers2(CurLiveInfo.getFlowers2() + 1);
    }

    @Override
    public void refreshGratuity(String identifier) {
        CurLiveInfo.setGratuitys(CurLiveInfo.getGratuitys() + 1);
    }

    @Override
    public void refreshGratuity2(String identifier) {
        CurLiveInfo.setGratuitys2(CurLiveInfo.getGratuitys2() + 1);
    }

    @Override
    public void readyGame() {
        Log.d(TAG, "readyGame: " + startgameTv.getText().toString());
        if (startgameTv.getText().toString().equals("等待开始") && mRenderUserList.contains(MySelfInfo.getInstance().getId())) {
            mLiveHelper.sendGroupMessage(Constant.AVIMCMD_Start_Game, "");
            CurLiveInfo.setGameStatus(0);
            startgameTv.setText(R.string.game_start);
            Toast.makeText(this, R.string.game_start, Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    OKhttpHelper.getInstance().startGame(mRenderUserList.get(Lp), mRenderUserList.get(Rp), "" + CurLiveInfo.getRoomNum(), "" + MySelfInfo.getInstance().getRoomType()); //开始游戏通知后台
                    overGame();
                }
            }).start();
        }
    }

    @Override
    public void startGame() {
        CurLiveInfo.setGameStatus(0);
        startgameTv.setText(R.string.game_start);
        Toast.makeText(this, R.string.game_start, Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                overGame();
            }
        }).start();
    }

    public void overGame() {
        for (int i = CurLiveInfo.getGameTime() == 0 ? Constant.GAME_TIME / 1000 : CurLiveInfo.getGameTime(); i > 0; i--) { //游戏倒计时
            Message message = new Message();
            message.what = GAME_COUNTDOWN;
            Bundle bundle = new Bundle();
            bundle.putString("time", "" + i + "s");
            message.setData(bundle);
            handler.sendMessage(message);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String result = OKhttpHelper.getInstance().settlement("" + CurLiveInfo.getRoomNum()); //游戏结束取后台结果
        Log.d("cmx", "result: " + result);
        Message message = new Message();
        message.what = GAME_RESULT;
        Bundle bundle = new Bundle();
        bundle.putString("result", result);
        message.setData(bundle);
        handler.sendMessage(message);
    }


    @Override
    public void refreshUI(String id) {
        //当主播选中这个人，而他主动退出时需要恢复到正常状态
        if (MySelfInfo.getInstance().getIdStatus() == Constant.HOST)
            if (!backGroundId.equals(CurLiveInfo.getHostID()) && backGroundId.equals(id)) {
                backToNormalCtrlView();
            }
    }


    private int inviteViewCount = 0;

    @Override
    public boolean showInviteView(String id) {
        int index = QavsdkControl.getInstance().getAvailableViewIndex(1);
        if (index == -1) {
            Toast.makeText(LiveActivity.this, "the invitation's upper limit is 3", Toast.LENGTH_SHORT).show();
            return false;
        }
        int requetCount = index + inviteViewCount;
        if (requetCount > 3) {
            Toast.makeText(LiveActivity.this, "the invitation's upper limit is 3", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (hasInvited(id)) {
            Toast.makeText(LiveActivity.this, "it has already invited", Toast.LENGTH_SHORT).show();
            return false;
        }
        switch (requetCount) {
            case 1:
                inviteView1.setText(id);
                inviteView1.setVisibility(View.VISIBLE);
                inviteView1.setTag(id);

                break;
            case 2:
                inviteView2.setText(id);
                inviteView2.setVisibility(View.VISIBLE);
                inviteView2.setTag(id);
                break;
            case 3:
                inviteView3.setText(id);
                inviteView3.setVisibility(View.VISIBLE);
                inviteView3.setTag(id);
                break;
        }
        mLiveHelper.sendC2CMessage(Constant.AVIMCMD_MUlTI_HOST_INVITE, "", id);
        inviteViewCount++;
        //30s超时取消
        Message msg = new Message();
        msg.what = TIMEOUT_INVITE;
        msg.obj = id;
        mHandler.sendMessageDelayed(msg, 30 * 1000);
        return true;
    }


    /**
     * 判断是否邀请过同一个人
     *
     * @param id
     * @return
     */
    private boolean hasInvited(String id) {
        if (id.equals(inviteView1.getTag())) {
            return true;
        }
        if (id.equals(inviteView2.getTag())) {
            return true;
        }
        if (id.equals(inviteView3.getTag())) {
            return true;
        }
        return false;
    }

    @Override
    public void cancelInviteView(String id) {
        if ((inviteView1 != null) && (inviteView1.getTag() != null)) {
            if (inviteView1.getTag().equals(id)) {
            }
            if (inviteView1.getVisibility() == View.VISIBLE) {
                inviteView1.setVisibility(View.INVISIBLE);
                inviteView1.setTag("");
                inviteViewCount--;
            }
        }
    }

    @Override
    public void cancelMemberView(String id) {
        if (MySelfInfo.getInstance().getIdStatus() == Constant.HOST) {
        } else {
            //TODO 主动下麦 下麦；
            mLiveHelper.changeAuthandRole(false, Constant.NORMAL_MEMBER_AUTH, Constant.NORMAL_MEMBER_ROLE);
//            mLiveHelper.closeCameraAndMic();//是自己成员关闭
        }
        mLiveHelper.sendGroupMessage(Constant.AVIMCMD_MULTI_CANCEL_INTERACT, id);
        QavsdkControl.getInstance().closeMemberView(id);
        backToNormalCtrlView();
    }


    private void showReportDialog() {
        final Dialog reportDialog = new Dialog(this, R.style.report_dlg);
        reportDialog.setContentView(R.layout.dialog_live_report);

        TextView tvReportDirty = (TextView) reportDialog.findViewById(R.id.btn_dirty);
        TextView tvReportFalse = (TextView) reportDialog.findViewById(R.id.btn_false);
        TextView tvReportVirus = (TextView) reportDialog.findViewById(R.id.btn_virus);
        TextView tvReportIllegal = (TextView) reportDialog.findViewById(R.id.btn_illegal);
        TextView tvReportYellow = (TextView) reportDialog.findViewById(R.id.btn_yellow);
        TextView tvReportCancel = (TextView) reportDialog.findViewById(R.id.btn_cancel);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    default:
                        reportDialog.cancel();
                        break;
                }
            }
        };

        tvReportDirty.setOnClickListener(listener);
        tvReportFalse.setOnClickListener(listener);
        tvReportVirus.setOnClickListener(listener);
        tvReportIllegal.setOnClickListener(listener);
        tvReportYellow.setOnClickListener(listener);
        tvReportCancel.setOnClickListener(listener);

        reportDialog.setCanceledOnTouchOutside(true);
        reportDialog.show();
    }

    private void showHostDetail() {
        Dialog hostDlg = new Dialog(this, R.style.host_info_dlg);
        hostDlg.setContentView(R.layout.host_info_layout);

        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Window dlgwin = hostDlg.getWindow();
        WindowManager.LayoutParams lp = dlgwin.getAttributes();
        dlgwin.setGravity(Gravity.TOP);
        lp.width = (int) (display.getWidth()); //设置宽度

        hostDlg.getWindow().setAttributes(lp);
        hostDlg.show();

        TextView tvHost = (TextView) hostDlg.findViewById(R.id.tv_host_name);
        tvHost.setText(CurLiveInfo.getHostName());
        ImageView ivHostIcon = (ImageView) hostDlg.findViewById(R.id.iv_host_icon);
        showHeadIcon(ivHostIcon, CurLiveInfo.getHostAvator());
        TextView tvLbs = (TextView) hostDlg.findViewById(R.id.tv_host_lbs);
        tvLbs.setText(UIUtils.getLimitString(CurLiveInfo.getAddress(), 6));
        ImageView ivReport = (ImageView) hostDlg.findViewById(R.id.iv_report);
        ivReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReportDialog();
            }
        });
    }

    private boolean checkInterval() {
        if (0 == admireTime) {
            admireTime = System.currentTimeMillis();
            return true;
        }
        long newTime = System.currentTimeMillis();
        if (newTime >= admireTime + 1000) {
            admireTime = newTime;
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                quiteLiveByPurpose();
                mLiveHelper.sendC2CMessage(Constant.AVIMCMD_MULTI_CANCEL_INTERACT, "", CurLiveInfo.getHostID());
                break;
//              case R.id.message_input:
//                inputMsgDialog();
//                break;
//            case R.id.member_send_good:
//                // 添加飘星动画
//                mHeartLayout.addFavor();
//                if (checkInterval()) {
//                    mLiveHelper.sendC2CMessage(Constant.AVIMCMD_Praise, "", CurLiveInfo.getHostID());
//                    CurLiveInfo.setAdmires(CurLiveInfo.getAdmires() + 1);
//                    tvAdmires.setText("" + CurLiveInfo.getAdmires());
//                } else {
//                    //Toast.makeText(this, getString(R.string.text_live_admire_limit), Toast.LENGTH_SHORT).show();
//                }
//                break;
            case R.id.flash_btn:
                if (mLiveHelper.isFrontCamera() == true) {
                    Toast.makeText(LiveActivity.this, "this is front cam", Toast.LENGTH_SHORT).show();
                } else {
                    mLiveHelper.toggleFlashLight();
                }
                break;
            case R.id.switch_cam:
                mLiveHelper.switchCamera();
                break;
            case R.id.mic_btn:
                if (mLiveHelper.isMicOpen() == true) {
                    BtnMic.setBackgroundResource(R.drawable.icon_mic_close);
                    mLiveHelper.muteMic();
                } else {
                    BtnMic.setBackgroundResource(R.drawable.icon_mic_open);
                    mLiveHelper.openMic();
                }
                break;
            case R.id.head_up_layout:
                showHostDetail();
                break;
//            case R.id.clean_screen:
            case R.id.fullscreen_btn:
                bCleanMode = true;
                mFullControllerUi.setVisibility(View.INVISIBLE);
                BtnNormal.setVisibility(View.VISIBLE);
                break;
            case R.id.normal_btn:
                bCleanMode = false;
                mFullControllerUi.setVisibility(View.VISIBLE);
                BtnNormal.setVisibility(View.GONE);
                break;
            case R.id.video_interact:
                mMemberDg.setCanceledOnTouchOutside(true);
                mMemberDg.show();
                break;
/*            case R.id.camera_controll:
                Toast.makeText(LiveActivity.this, "切换" + backGroundId + "camrea 状态", Toast.LENGTH_SHORT).show();
                if (backGroundId.equals(MySelfInfo.getInstance().getId())) {//自己关闭自己
                    mLiveHelper.toggleCamera();
                } else {
                    mLiveHelper.sendC2CMessage(Constant.AVIMCMD_MULTI_HOST_CONTROLL_CAMERA, backGroundId, backGroundId);//主播关闭自己
                }
                break;
            case R.id.mic_controll:
                Toast.makeText(LiveActivity.this, "切换" + backGroundId + "mic 状态", Toast.LENGTH_SHORT).show();
                if (backGroundId.equals(MySelfInfo.getInstance().getId())) {//自己关闭自己
                    mLiveHelper.toggleMic();
                } else {
                    mLiveHelper.sendC2CMessage(Constant.AVIMCMD_MULTI_HOST_CONTROLL_MIC, backGroundId, backGroundId);//主播关闭自己
                }
                break;*/
            case R.id.close_member_video://主动关闭成员摄像头
                if (CurLiveInfo.getGameStatus() >= 0) {
                    new AlertDialog.Builder(LiveActivity.this).setTitle("系统提示")//设置对话框标题
                            .setMessage("游戏正在进行，退出将不会保存游戏数据，是否继续！")//设置显示的内容
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加确定按钮
                                @Override
                                public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                    cancelMemberView(MySelfInfo.getInstance().getId());
                                    startgameTv.setText(R.string.startgame);
                                    startgameTv.setBackgroundResource(R.color.colorGray4);
                                    startgameTv.setClickable(false);
                                }
                            }).setNegativeButton("返回", new DialogInterface.OnClickListener() {//添加返回按钮
                        @Override
                        public void onClick(DialogInterface dialog, int which) {//响应事件
                            Log.i("cmx", " 请保存数据！");
                        }
                    }).show();//在按键响应事件中显示此对话框
                } else {
                    cancelMemberView(MySelfInfo.getInstance().getId());
                    startgameTv.setText(R.string.startgame);
                    startgameTv.setBackgroundResource(R.color.colorGray4);
                    startgameTv.setClickable(false);
                }
                break;
            case R.id.head_back:
                if (mRenderUserList.contains(MySelfInfo.getInstance().getId())) {
                    if (CurLiveInfo.getGameStatus() >= 0) {
                        new AlertDialog.Builder(LiveActivity.this).setTitle("系统提示")//设置对话框标题
                                .setMessage("游戏正在进行，退出将不会保存游戏数据，是否继续！")//设置显示的内容
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加确定按钮
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                        cancelMemberView(MySelfInfo.getInstance().getId());
                                        startgameTv.setText(R.string.startgame);
                                        startgameTv.setBackgroundResource(R.color.colorGray4);
                                        startgameTv.setClickable(false);
                                    }
                                }).setNegativeButton("返回", new DialogInterface.OnClickListener() {//添加返回按钮
                            @Override
                            public void onClick(DialogInterface dialog, int which) {//响应事件
                                Log.i("cmx", " 请保存数据！");
                            }
                        }).show();//在按键响应事件中显示此对话框
                    } else {
                        cancelMemberView(MySelfInfo.getInstance().getId());
                        startgameTv.setText(R.string.startgame);
                        startgameTv.setBackgroundResource(R.color.colorGray4);
                        startgameTv.setClickable(false);
                    }
                } else {
                    quiteLiveByPurpose();
                }
                break;
            case R.id.head_other:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "我在卖呆，你也一起来吧！http://www.aihuha.com/");
                shareIntent.setType("text/plain");
                //设置分享列表的标题，并且每次都显示分享列表
                startActivity(Intent.createChooser(shareIntent, "分享到"));
                break;
            case R.id.beauty_btn:
                Log.i(TAG, "onClick " + mBeautyRate);

                mProfile = mBeatuy;
                if (mBeautySettings != null) {
                    if (mBeautySettings.getVisibility() == View.GONE) {
                        mBeautySettings.setVisibility(View.VISIBLE);
                        mFullControllerUi.setVisibility(View.INVISIBLE);
                        mBeautyBar.setProgress(mBeautyRate);
                    } else {
                        mBeautySettings.setVisibility(View.GONE);
                        mFullControllerUi.setVisibility(View.VISIBLE);
                    }
                } else {
                    SxbLog.i(TAG, "beauty_btn mTopBar  is null ");
                }
                break;

            case R.id.white_btn:
                Log.i(TAG, "onClick " + mWhiteRate);
                mProfile = mWhite;
                if (mBeautySettings != null) {
                    if (mBeautySettings.getVisibility() == View.GONE) {
                        mBeautySettings.setVisibility(View.VISIBLE);
                        mFullControllerUi.setVisibility(View.INVISIBLE);
                        mBeautyBar.setProgress(mWhiteRate);
                    } else {
                        mBeautySettings.setVisibility(View.GONE);
                        mFullControllerUi.setVisibility(View.VISIBLE);
                    }
                } else {
                    SxbLog.i(TAG, "beauty_btn mTopBar  is null ");
                }
                break;
            case R.id.qav_beauty_setting_finish:
                mBeautySettings.setVisibility(View.GONE);
                mFullControllerUi.setVisibility(View.VISIBLE);
                break;
            case R.id.invite_view1:
                inviteView1.setVisibility(View.INVISIBLE);
                mLiveHelper.sendGroupMessage(Constant.AVIMCMD_MULTI_CANCEL_INTERACT, "" + inviteView1.getTag());
                break;
            case R.id.invite_view2:
                inviteView2.setVisibility(View.INVISIBLE);
                mLiveHelper.sendGroupMessage(Constant.AVIMCMD_MULTI_CANCEL_INTERACT, "" + inviteView2.getTag());
                break;
            case R.id.invite_view3:
                inviteView3.setVisibility(View.INVISIBLE);
                mLiveHelper.sendGroupMessage(Constant.AVIMCMD_MULTI_CANCEL_INTERACT, "" + inviteView3.getTag());
                break;
            case R.id.record_btn:
                if (!mRecord) {
                    if (recordDialog != null)
                        recordDialog.show();
                } else {
                    mLiveHelper.stopRecord();
                }
                break;
            case R.id.tv_liaotian:
                liaotianTv.setTextColor(Color.rgb(253, 125, 0));
                xiangqingTv.setTextColor(Color.rgb(151, 151, 151));
                gongxianTv.setTextColor(Color.rgb(151, 151, 151));
                liaotianLayout.setVisibility(View.VISIBLE);
                xiangqingLayout.setVisibility(View.GONE);
                gongxianLayout.setVisibility(View.GONE);
                break;
            case R.id.tv_xiangqing:
                liaotianTv.setTextColor(Color.rgb(151, 151, 151));
                xiangqingTv.setTextColor(Color.rgb(253, 125, 0));
                gongxianTv.setTextColor(Color.rgb(151, 151, 151));
                liaotianLayout.setVisibility(View.GONE);
                xiangqingLayout.setVisibility(View.VISIBLE);
                gongxianLayout.setVisibility(View.GONE);
                break;
            case R.id.tv_gongxian:
                liaotianTv.setTextColor(Color.rgb(151, 151, 151));
                xiangqingTv.setTextColor(Color.rgb(151, 151, 151));
                gongxianTv.setTextColor(Color.rgb(253, 125, 0));
                liaotianLayout.setVisibility(View.GONE);
                xiangqingLayout.setVisibility(View.GONE);
                gongxianLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_lflower:
                if (checkInterval()) {
                    if (CurLiveInfo.gameStatus < 0) {
                        Toast.makeText(this, "下局游戏开始后才能送花", Toast.LENGTH_SHORT).show();
                    } else {
                        if (hua) {
                            refreshTextListView("系统:", "一局游戏只能为主播送花一次", Constant.TEXT_TYPE);
                        } else {
                            //1.发通知，2.发后台，3.改UI，4.发消息
                            mLiveHelper.sendGroupMessage(Constant.AVIMCMD_Host_Flower, MySelfInfo.getInstance().getNickName());
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    String name = OKhttpHelper.getInstance().sendFlower(MySelfInfo.getInstance().getId(), "" + CurLiveInfo.getRoomNum(), mRenderUserList.get(Lp).toString());
                                    sendText("送给了 " + (name.isEmpty() ? mRenderUserList.get(Lp).toString() : name) + " 一朵花");
                                }
                            }).start();
                            hua = true;
                        }
                    }
                }
                break;
            case R.id.tv_rflower:
                if (checkInterval()) {
                    if (CurLiveInfo.gameStatus < 0) {
                        Toast.makeText(this, "下局游戏开始后才能送花", Toast.LENGTH_SHORT).show();
                    } else {
                        if (hua) {
                            refreshTextListView("系统:", "一局游戏只能为主播送花一次", Constant.TEXT_TYPE);
                        } else {
                            mLiveHelper.sendGroupMessage(Constant.AVIMCMD_Interaction_Flower, MySelfInfo.getInstance().getNickName());
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    String name = OKhttpHelper.getInstance().sendFlower(MySelfInfo.getInstance().getId(), "" + CurLiveInfo.getRoomNum(), mRenderUserList.get(Rp).toString());
                                    sendText("送给了 " + (name.isEmpty() ? mRenderUserList.get(Rp).toString() : name) + " 一朵花");
                                }
                            }).start();
                            hua = true;
                        }
                    }
                }
                break;
            case R.id.tv_lgratuity:
                if (checkInterval()) {
                    if (CurLiveInfo.gameStatus < 0) {
                        Toast.makeText(this, "下局游戏开始后才能打赏", Toast.LENGTH_SHORT).show();
                    } else {
                        mLiveHelper.sendGroupMessage(Constant.AVIMCMD_Host_Present, MySelfInfo.getInstance().getNickName());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String name = OKhttpHelper.getInstance().sendGratuity(MySelfInfo.getInstance().getId(), "" + CurLiveInfo.getRoomNum(), mRenderUserList.get(Lp).toString());
                                sendText("送给了 " + (name.isEmpty() ? mRenderUserList.get(Lp).toString() : name) + " 一个礼物");
                            }
                        }).start();
                    }
                }
                break;
            case R.id.tv_rgratuity:
                if (checkInterval()) {
                    if (CurLiveInfo.gameStatus < 0) {
                        Toast.makeText(this, "下局游戏开始后才能打赏", Toast.LENGTH_SHORT).show();
                    } else {
                        mLiveHelper.sendGroupMessage(Constant.AVIMCMD_Interaction_Present, MySelfInfo.getInstance().getNickName());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String name = OKhttpHelper.getInstance().sendGratuity(MySelfInfo.getInstance().getId(), "" + CurLiveInfo.getRoomNum(), mRenderUserList.get(Rp).toString());
                                sendText("送给了 " + (name.isEmpty() ? mRenderUserList.get(Rp).toString() : name) + " 一个礼物");
                            }
                        }).start();
                    }
                }
                break;
            case R.id.tv_lheart:
                if (checkInterval()) {
                    if (CurLiveInfo.gameStatus < 0) {
                        Toast.makeText(this, "下局游戏开始后才能点赞", Toast.LENGTH_SHORT).show();
                    } else if (CurLiveInfo.gameStatus == 0) {
                        mLiveHelper.sendGroupMessage(Constant.AVIMCMD_Praise, MySelfInfo.getInstance().getNickName());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String name = OKhttpHelper.getInstance().sendHeart(MySelfInfo.getInstance().getId(), "" + CurLiveInfo.getRoomNum(), mRenderUserList.get(Lp).toString());
                                sendText("送给了 " + (name.isEmpty() ? mRenderUserList.get(Lp).toString() : name) + " 一个赞");
                            }
                        }).start();
                        CurLiveInfo.setGameStatus(1);
                    } else {
                        Toast.makeText(this, "已经点过赞了", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.tv_rheart:
                if (checkInterval()) {
                    if (CurLiveInfo.gameStatus < 0) {
                        Toast.makeText(this, "下局游戏开始后才能点赞", Toast.LENGTH_SHORT).show();
                    } else if (CurLiveInfo.gameStatus == 0) {
                        mLiveHelper.sendGroupMessage(Constant.AVIMCMD_Interaction_Praise, MySelfInfo.getInstance().getNickName());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String name = OKhttpHelper.getInstance().sendHeart(MySelfInfo.getInstance().getId(), "" + CurLiveInfo.getRoomNum(), mRenderUserList.get(Rp).toString());
                                sendText("送给了 " + (name.isEmpty() ? mRenderUserList.get(Rp).toString() : name) + " 一个赞");
                            }
                        }).start();
                        CurLiveInfo.setGameStatus(1);
                    } else {
                        Toast.makeText(this, "已经点过赞了", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.tv_xianhua:
                gongXianLay.setBackgroundResource(R.drawable.h63);
                swipeLayoutFlower.setVisibility(View.VISIBLE);
                swipeLayoutGratuity.setVisibility(View.GONE);
                break;
            case R.id.tv_dashang:
                gongXianLay.setBackgroundResource(R.drawable.h62);
                swipeLayoutFlower.setVisibility(View.GONE);
                swipeLayoutGratuity.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_joingame:
                mLiveHelper.sendGroupMessage(Constant.AVIMCMD_MUlTI_JOIN, MySelfInfo.getInstance().getNickName());
                mLiveHelper.changeAuthandRole(true, Constant.VIDEO_MEMBER_AUTH, Constant.VIDEO_MEMBER_ROLE);
                videoLayout.setVisibility(View.VISIBLE);
                memberLayout.setVisibility(View.GONE);
                startgameTv.setText(R.string.ready);
                startgameTv.setBackgroundResource(R.color.theme_orange);
                startgameTv.setClickable(true);
                break;
            case R.id.tv_startgame:
                if (CurLiveInfo.gameStatus < 0) {
                    mLiveHelper.sendGroupMessage(Constant.AVIMCMD_Ready_Game, "");
                    startgameTv.setText(R.string.startgame);
                    startgameTv.setBackgroundResource(R.color.colorGray4);
                    startgameTv.setClickable(false);
                }
                break;
            case R.id.switch_kaibo:
                if (kaiboSwitch.getChecked()) {
                    kaiboSwitch.setChecked(false, true);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            OKhttpHelper.getInstance().getattention(MySelfInfo.getInstance().getId(), "" + CurLiveInfo.getRoomNum());
                        }
                    }).start();
                } else {
                    kaiboSwitch.setChecked(true, true);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            OKhttpHelper.getInstance().attention(MySelfInfo.getInstance().getId(), "" + CurLiveInfo.getRoomNum());
                        }
                    }).start();
                }
                break;
        }
    }

    //for 测试获取测试参数
    private boolean showTips = false;
    private TextView tvTipsMsg;
    Timer paramTimer = new Timer();
    TimerTask task = new TimerTask() {
        public void run() {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (showTips) {
                        if (tvTipsMsg != null) {
                            String strTips = QavsdkControl.getInstance().getQualityTips();
                            strTips = praseString(strTips);
                            if (!TextUtils.isEmpty(strTips)) {
                                tvTipsMsg.setText(strTips);
                            }
                        }
                    } else {
                        tvTipsMsg.setText("");
                    }
                }
            });
        }
    };

    //for 测试 解析参数
    private String praseString(String video) {
        if (video.length() == 0) {
            return "";
        }
        String result = "";
        String splitItems[];
        String tokens[];
        splitItems = video.split("\\n");
        for (int i = 0; i < splitItems.length; ++i) {
            if (splitItems[i].length() < 2)
                continue;

            tokens = splitItems[i].split(":");
            if (tokens[0].length() == "mainVideoSendSmallViewQua".length()) {
                continue;
            }
            if (tokens[0].endsWith("BigViewQua")) {
                tokens[0] = "mainVideoSendViewQua";
            }
            if (tokens[0].endsWith("BigViewQos")) {
                tokens[0] = "mainVideoSendViewQos";
            }
            result += tokens[0] + ":\n" + "\t\t";
            for (int j = 1; j < tokens.length; ++j)
                result += tokens[j];
            result += "\n\n";
            //Log.d(TAG, "test:" + result);
        }
        //Log.d(TAG, "test:" + result);
        return result;
    }


    private void backToNormalCtrlView() {
        if (MySelfInfo.getInstance().getIdStatus() == Constant.HOST) {
            backGroundId = CurLiveInfo.getHostID();
//            mHostCtrView.setVisibility(View.VISIBLE);
            videoLayout.setVisibility(View.GONE);
        } else {
            backGroundId = CurLiveInfo.getHostID();
            memberLayout.setVisibility(View.VISIBLE);
            videoLayout.setVisibility(View.GONE);
        }
    }


    /**
     * 发消息弹出框
     */
    private void inputMsgDialog() {
        InputTextMsgDialog inputMsgDialog = new InputTextMsgDialog(this, R.style.inputdialog, mLiveHelper, this);
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = inputMsgDialog.getWindow().getAttributes();

        lp.width = (int) (display.getWidth()); //设置宽度
        inputMsgDialog.getWindow().setAttributes(lp);
        inputMsgDialog.setCancelable(true);
        inputMsgDialog.show();
    }


    /**
     * 主播邀请应答框
     */
    private void initInviteDialog() {
        inviteDg = new Dialog(this, R.style.dialog);
        inviteDg.setContentView(R.layout.invite_dialog);
        TextView hostId = (TextView) inviteDg.findViewById(R.id.host_id);
        hostId.setText(CurLiveInfo.getHostID());
        TextView agreeBtn = (TextView) inviteDg.findViewById(R.id.invite_agree);
        TextView refusebtn = (TextView) inviteDg.findViewById(R.id.invite_refuse);
        agreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoLayout.setVisibility(View.VISIBLE);
                memberLayout.setVisibility(View.GONE);

                //上麦 ；TODO 上麦 上麦 上麦 ！！！！！；
                mLiveHelper.changeAuthandRole(true, Constant.VIDEO_MEMBER_AUTH, Constant.VIDEO_MEMBER_ROLE);
                inviteDg.dismiss();
            }
        });

        refusebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLiveHelper.sendC2CMessage(Constant.AVIMCMD_MUlTI_REFUSE, "", CurLiveInfo.getHostID());
                inviteDg.dismiss();
            }
        });

        Window dialogWindow = inviteDg.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);
    }


    /**
     * 消息刷新显示
     *
     * @param name    发送者
     * @param context 内容
     * @param type    类型 （上线线消息和 聊天消息）
     */
    public void refreshTextListView(String name, String context, int type) {
        ChatEntity entity = new ChatEntity();
        entity.setSenderName(name);
        entity.setContext(context);
        entity.setType(type);
        //mArrayListChatEntity.add(entity);
        notifyRefreshListView(entity);
        //mChatMsgListAdapter.notifyDataSetChanged();

        mListViewMsgItems.setVisibility(View.VISIBLE);
        SxbLog.d(TAG, "refreshTextListView height " + mListViewMsgItems.getHeight());

        if (mListViewMsgItems.getCount() > 1) {
            if (true)
                mListViewMsgItems.setSelection(0);
            else
                mListViewMsgItems.setSelection(mListViewMsgItems.getCount() - 1);
        }
    }


    /**
     * 通知刷新消息ListView
     */
    private void notifyRefreshListView(ChatEntity entity) {
        mBoolNeedRefresh = true;
        mTmpChatList.add(entity);
        if (mBoolRefreshLock) {
            return;
        } else {
            doRefreshListView();
        }
    }


    /**
     * 刷新ListView并重置状态
     */
    private void doRefreshListView() {
        if (mBoolNeedRefresh) {
            mBoolRefreshLock = true;
            mBoolNeedRefresh = false;
            mArrayListChatEntity.addAll(mTmpChatList);
            mTmpChatList.clear();
            mChatMsgListAdapter.notifyDataSetChanged();

            if (null != mTimerTask) {
                mTimerTask.cancel();
            }
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    SxbLog.v(TAG, "doRefreshListView->task enter with need:" + mBoolNeedRefresh);
                    mHandler.sendEmptyMessage(REFRESH_LISTVIEW);
                }
            };
            //mTimer.cancel();
            mTimer.schedule(mTimerTask, MINFRESHINTERVAL);
        } else {
            mBoolRefreshLock = false;
        }
    }

    @Override
    public void updateProfileInfo(TIMUserProfile profile) {

    }

    @Override
    public void updateUserInfo(int requestCode, List<TIMUserProfile> profiles) {
        if (null != profiles) {
            switch (requestCode) {
                case GETPROFILE_JOIN:
                    for (TIMUserProfile user : profiles) {
                        tvMembers.setText("" + CurLiveInfo.getMembers());
                        SxbLog.w(TAG, "get nick name:" + user.getNickName());
                        SxbLog.w(TAG, "get remark name:" + user.getRemark());
                        SxbLog.w(TAG, "get avatar:" + user.getFaceUrl());
                        if (!TextUtils.isEmpty(user.getNickName())) {
                            refreshTextListView(user.getNickName(), "进入房间", Constant.MEMBER_ENTER);
                        } else {
                            refreshTextListView(user.getIdentifier(), "进入房间", Constant.MEMBER_ENTER);
                        }
                    }
                    break;
            }

        }
    }

    //旁路直播
    private static boolean isPushed = false;

    /**
     * 旁路直播 退出房间时必须退出推流。否则会占用后台channel。
     */
    public void pushStream() {
        if (!isPushed) {
            if (mPushDialog != null)
                mPushDialog.show();
        } else {
            mLiveHelper.stopPushAction();
        }
    }

    private Dialog mPushDialog;

    private void initPushDialog() {
        mPushDialog = new Dialog(this, R.style.dialog);
        mPushDialog.setContentView(R.layout.push_dialog_layout);
        final TIMAvManager.StreamParam mStreamParam = TIMAvManager.getInstance().new StreamParam();
        final EditText pushfileNameInput = (EditText) mPushDialog.findViewById(R.id.push_filename);
        final RadioGroup radgroup = (RadioGroup) mPushDialog.findViewById(R.id.push_type);


        Button recordOk = (Button) mPushDialog.findViewById(R.id.btn_record_ok);
        recordOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pushfileNameInput.getText().toString().equals("")) {
                    Toast.makeText(LiveActivity.this, "name can't be empty", Toast.LENGTH_SHORT);
                    return;
                } else {
                    mStreamParam.setChannelName(pushfileNameInput.getText().toString());
                }

                if (radgroup.getCheckedRadioButtonId() == R.id.hls) {
                    mStreamParam.setEncode(TIMAvManager.StreamEncode.HLS);
                } else {
                    mStreamParam.setEncode(TIMAvManager.StreamEncode.RTMP);
                }
//                mStreamParam.setEncode(TIMAvManager.StreamEncode.HLS);
                mLiveHelper.pushAction(mStreamParam);
                mPushDialog.dismiss();
            }
        });


        Button recordCancel = (Button) mPushDialog.findViewById(R.id.btn_record_cancel);
        recordCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPushDialog.dismiss();
            }
        });

        Window dialogWindow = mPushDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);
        mPushDialog.setCanceledOnTouchOutside(false);
    }


    /**
     * 推流成功
     *
     * @param streamRes
     */
    @Override
    public void pushStreamSucc(TIMAvManager.StreamRes streamRes) {
        List<TIMAvManager.LiveUrl> liveUrls = streamRes.getUrls();
        isPushed = true;
        pushBtn.setBackgroundResource(R.drawable.icon_stop_push);
        int length = liveUrls.size();
        String url = null;
        String url2 = null;
        if (length == 1) {
            TIMAvManager.LiveUrl avUrl = liveUrls.get(0);
            url = avUrl.getUrl();
        } else if (length == 2) {
            TIMAvManager.LiveUrl avUrl = liveUrls.get(0);
            url = avUrl.getUrl();
            TIMAvManager.LiveUrl avUrl2 = liveUrls.get(1);
            url2 = avUrl2.getUrl();
        }
        ClipToBoard(url, url2);
    }

    /**
     * 将地址黏贴到黏贴版
     *
     * @param url
     * @param url2
     */
    private void ClipToBoard(final String url, final String url2) {
        SxbLog.i(TAG, "ClipToBoard url " + url);
        SxbLog.i(TAG, "ClipToBoard url2 " + url2);
        if (url == null) return;
        final Dialog dialog = new Dialog(this, R.style.dialog);
        dialog.setContentView(R.layout.clip_dialog);
        TextView urlText = ((TextView) dialog.findViewById(R.id.url1));
        TextView urlText2 = ((TextView) dialog.findViewById(R.id.url2));
        Button btnClose = ((Button) dialog.findViewById(R.id.close_dialog));
        urlText.setText(url);
        urlText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clip = (ClipboardManager) getApplicationContext().getSystemService(getApplicationContext().CLIPBOARD_SERVICE);
                clip.setText(url);
                Toast.makeText(LiveActivity.this, getResources().getString(R.string.clip_tip), Toast.LENGTH_SHORT).show();
            }
        });
        if (url2 == null) {
            urlText2.setVisibility(View.GONE);
        } else {
            urlText2.setText(url2);
            urlText2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClipboardManager clip = (ClipboardManager) getApplicationContext().getSystemService(getApplicationContext().CLIPBOARD_SERVICE);
                    clip.setText(url2);
                    Toast.makeText(LiveActivity.this, getResources().getString(R.string.clip_tip), Toast.LENGTH_SHORT).show();
                }
            });
        }
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }


    private Dialog recordDialog;
    private TIMAvManager.RecordParam mRecordParam;
    private String filename = "";
    private String tags = "";
    private String classId = "";
    private boolean mRecord = false;
    private EditText filenameEditText, tagEditText, classEditText;
    private CheckBox trancodeCheckBox, screenshotCheckBox, watermarkCheckBox;

    private void initRecordDialog() {
        recordDialog = new Dialog(this, R.style.dialog);
        recordDialog.setContentView(R.layout.record_param);
        mRecordParam = TIMAvManager.getInstance().new RecordParam();

        filenameEditText = (EditText) recordDialog.findViewById(R.id.record_filename);
        tagEditText = (EditText) recordDialog.findViewById(R.id.record_tag);
        classEditText = (EditText) recordDialog.findViewById(R.id.record_class);
        trancodeCheckBox = (CheckBox) recordDialog.findViewById(R.id.record_tran_code);
        screenshotCheckBox = (CheckBox) recordDialog.findViewById(R.id.record_screen_shot);
        watermarkCheckBox = (CheckBox) recordDialog.findViewById(R.id.record_water_mark);

        if (filename.length() > 0) {
            filenameEditText.setText(filename);
        }
        filenameEditText.setText("" + CurLiveInfo.getRoomNum());

        if (tags.length() > 0) {
            tagEditText.setText(tags);
        }

        if (classId.length() > 0) {
            classEditText.setText(classId);
        }
        Button recordOk = (Button) recordDialog.findViewById(R.id.btn_record_ok);
        recordOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                filename = filenameEditText.getText().toString();
                mRecordParam.setFilename(filename);
                tags = tagEditText.getText().toString();
                classId = classEditText.getText().toString();
                Log.d(TAG, "onClick classId " + classId);
                if (classId.equals("")) {
                    Toast.makeText(getApplicationContext(), "classID can not be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                mRecordParam.setClassId(Integer.parseInt(classId));
                mRecordParam.setTransCode(trancodeCheckBox.isChecked());
                mRecordParam.setSreenShot(screenshotCheckBox.isChecked());
                mRecordParam.setWaterMark(watermarkCheckBox.isChecked());
                mLiveHelper.startRecord(mRecordParam);
                startOrientationListener();
                recordDialog.dismiss();
            }
        });
        Button recordCancel = (Button) recordDialog.findViewById(R.id.btn_record_cancel);
        recordCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startOrientationListener();
                recordDialog.dismiss();
            }
        });
        stopOrientationListener();
        Window dialogWindow = recordDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);
        recordDialog.setCanceledOnTouchOutside(false);
    }

    /**
     * 停止推流成功
     */
    @Override
    public void stopStreamSucc() {
        isPushed = false;
        pushBtn.setBackgroundResource(R.drawable.icon_push_stream);
    }

    @Override
    public void startRecordCallback(boolean isSucc) {
        mRecord = true;
        recordBtn.setBackgroundResource(R.drawable.icon_stoprecord);

    }

    @Override
    public void stopRecordCallback(boolean isSucc, List<String> files) {
        if (isSucc == true) {
            mRecord = false;
            recordBtn.setBackgroundResource(R.drawable.icon_record);
        }
    }


    private VideoOrientationEventListener mOrientationEventListener;

    void registerOrientationListener() {
        if (mOrientationEventListener == null) {
            mOrientationEventListener = new VideoOrientationEventListener(super.getApplicationContext(), SensorManager.SENSOR_DELAY_UI);
        }
    }

    void startOrientationListener() {
        if (mOrientationEventListener != null) {
            mOrientationEventListener.enable();
        }
    }

    void stopOrientationListener() {
        if (mOrientationEventListener != null) {
            mOrientationEventListener.disable();
        }
    }


    class VideoOrientationEventListener extends OrientationEventListener {
        boolean mbIsTablet = false;
        int mRotationAngle = 0;

        public VideoOrientationEventListener(Context context, int rate) {
            super(context, rate);
            mbIsTablet = PhoneStatusTools.isTablet(context);
        }

        int mLastOrientation = -25;

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                mLastOrientation = orientation;
                return;
            }

            if (mLastOrientation < 0) {
                mLastOrientation = 0;
            }

            if (((orientation - mLastOrientation) < 20)
                    && ((orientation - mLastOrientation) > -20)) {
                return;
            }

            if (mbIsTablet) {
                orientation -= 90;
                if (orientation < 0) {
                    orientation += 360;
                }
            }
            mLastOrientation = orientation;

            if (orientation > 314 || orientation < 45) {
                if (QavsdkControl.getInstance() != null) {
                    QavsdkControl.getInstance().setRotation(0);
                }
                mRotationAngle = 0;
            } else if (orientation > 44 && orientation < 135) {
                if (QavsdkControl.getInstance() != null) {
                    QavsdkControl.getInstance().setRotation(90);
                }
                mRotationAngle = 90;
            } else if (orientation > 134 && orientation < 225) {
                if (QavsdkControl.getInstance() != null) {
                    QavsdkControl.getInstance().setRotation(180);
                }
                mRotationAngle = 180;
            } else {
                if (QavsdkControl.getInstance() != null) {
                    QavsdkControl.getInstance().setRotation(270);
                }
                mRotationAngle = 270;
            }
        }

    }


    void checkPermission() {
        final List<String> permissionsList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.CAMERA);
            if ((checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.RECORD_AUDIO);
            if ((checkSelfPermission(Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.WAKE_LOCK);
            if ((checkSelfPermission(Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.MODIFY_AUDIO_SETTINGS);
            if (permissionsList.size() != 0) {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_PHONE_PERMISSIONS);
            }
        }
    }
}

