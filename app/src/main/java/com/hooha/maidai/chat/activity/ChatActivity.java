package com.hooha.maidai.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hooha.maidai.chat.adapters.ChatAdapter;
import com.hooha.maidai.chat.model.ChatEntity;
import com.hooha.maidai.chat.model.CustomMessage;
import com.hooha.maidai.chat.model.FileMessage;
import com.hooha.maidai.chat.model.FriendProfile;
import com.hooha.maidai.chat.model.FriendshipInfo;
import com.hooha.maidai.chat.model.GroupInfo;
import com.hooha.maidai.chat.model.ImageMessage;
import com.hooha.maidai.chat.model.Message;
import com.hooha.maidai.chat.model.MessageFactory;
import com.hooha.maidai.chat.model.TextMessage;
import com.hooha.maidai.chat.model.UserInfo;
import com.hooha.maidai.chat.model.VideoMessage;
import com.hooha.maidai.chat.model.VoiceMessage;
import com.hooha.maidai.chat.presenters.OKhttpHelper;
import com.hooha.maidai.chat.view.ChatInput;
import com.hooha.maidai.chat.view.VoiceSendingView;
import com.hooha.maidai.chat.utils.FileUtil;
import com.hooha.maidai.chat.utils.MediaUtil;
import com.hooha.maidai.chat.utils.RecorderUtil;
import com.hooha.maidai.chat.utils.SendTime;
import com.tencent.TIMConversationType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageStatus;
import com.tencent.qcloud.presentation.event.FriendshipEvent;
import com.tencent.qcloud.presentation.presenter.ChatPresenter;
import com.tencent.qcloud.presentation.presenter.ConversationPresenter;
import com.tencent.qcloud.presentation.viewfeatures.ChatView;
import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.view.TemplateTitle;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends FragmentActivity implements ChatView {

    private static final String TAG = "ChatActivity";

    private List<Message> messageList = new ArrayList<>();
    private List<String> sendTimeList = new ArrayList<>();
    private ChatAdapter adapter;
    private ListView listView;
    private List<String> getSenderName = new ArrayList<>();
    private ChatPresenter presenter;

    private ChatInput input;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int IMAGE_STORE = 200;
    private static final int FILE_CODE = 300;
    private static final int IMAGE_PREVIEW = 400;
    private Uri fileUri;
    private VoiceSendingView voiceSendingView;
    private String identify;
    private RecorderUtil recorder = new RecorderUtil();
    private TIMConversationType type;
    private String titleStr;
    private Handler handler = new Handler();
    private boolean jiejiuwo;//匹配聊天的标识-30秒删除聊天信息-退出聊天室清除聊天记录

    TemplateTitle title;
    FriendProfile profile;

    public static void navToChat(Context context, String identify, TIMConversationType type) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("identify", identify);
        intent.putExtra("type", type);
        context.startActivity(intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.stop();
        if (jiejiuwo) {
            TIMManager.getInstance().deleteConversationAndLocalMsgs(TIMConversationType.C2C, identify);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        title = (TemplateTitle) findViewById(R.id.chat_title);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        identify = getIntent().getStringExtra("identify");
        jiejiuwo = getIntent().getBooleanExtra("jiejiuwo", false);
        type = (TIMConversationType) getIntent().getSerializableExtra("type");
        presenter = new ChatPresenter(this, identify, type);
        input = (ChatInput) findViewById(R.id.input_panel);
        input.setChatView(this);
        adapter = new ChatAdapter(this, R.layout.item_message, messageList);
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        input.setInputMode(ChatInput.InputMode.NONE);
                        break;
                }
                return false;
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            private int firstItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && firstItem == 0) {
                    //如果拉到顶端读取更多消息
                    presenter.getMessage(messageList.size() > 0 ? messageList.get(0).getMessage() : null);

                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                firstItem = firstVisibleItem;
            }
        });
        registerForContextMenu(listView);

        switch (type) {
            case C2C:
                title.setMoreImg(R.drawable.btn_person);
                if (FriendshipInfo.getInstance().isFriend(identify)) {
                    title.setMoreImgAction(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ChatActivity.this, ProfileActivity.class);
                            intent.putExtra("identify", identify);
                            startActivityForResult(intent, 1000);
                        }
                    });
                    profile = FriendshipInfo.getInstance().getProfile(identify);
                    title.setTitleText(titleStr = profile == null ? identify : profile.getName());
                } else {
                    title.setMoreImgAction(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent person = new Intent(ChatActivity.this, AddFriendActivity.class);
                            person.putExtra("id", identify);
                            person.putExtra("name", identify);
                            startActivity(person);
                        }
                    });
                    title.setTitleText(titleStr = identify);
                }
                break;
            case Group:
                title.setMoreImg(R.drawable.btn_group);
                title.setMoreImgAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ChatActivity.this, GroupProfileActivity.class);
                        intent.putExtra("identify", identify);
                        startActivity(intent);
                    }
                });
                title.setTitleText(GroupInfo.getInstance().getGroupName(identify));
                break;

        }
        voiceSendingView = (VoiceSendingView) findViewById(R.id.voice_sending);
        presenter.start();
    }


    @Override
    protected void onPause() {
        super.onPause();
        presenter.readMessages();
        MediaUtil.getInstance().stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        profile = FriendshipInfo.getInstance().getProfile(identify);

//   getSenderName=ChatEntity.getSenderName();
//        FriendshipEvent.getInstance().OnDelFriends(getSenderName);
        if (profile != null) {
            if (type == TIMConversationType.C2C) {
                if (!profile.getName().isEmpty()) {
                    title.setTitleText(profile.getName());
                }
            }
        } else {
            // ConversationPresenter.getInstance().delConversation(type,profile.getIdentify());
            Log.i(TAG, "onResume: 此人不在列表中");


        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<GroupListActivity> jiejiuwo = new ArrayList<GroupListActivity>();
                OKhttpHelper.getInstance().setLogin(UserInfo.getInstance().getId());
            }
        }).start();
    }

    /**
     * 显示消息
     *
     * @param message
     */
    @Override
    public void showMessage(TIMMessage message) {

        if (message == null) {
            adapter.notifyDataSetChanged();
        } else {

            Log.e("showMessageSingle", "showMessage");
            Log.e("getMsgId", "" + message.getMsgId());

            if (jiejiuwo) {
                if (!sendTimeList.contains(message.getMsgId())) {
                    sendTimeList.add(message.getMsgId());
                    SendTime sendTime = new SendTime(30000, 1000, messageList, message, adapter);
                    sendTime.start();
                }
            }

            Message mMessage = MessageFactory.getMessage(message);
            if (mMessage != null) {
                if (mMessage instanceof CustomMessage && !message.isSelf()) {
                    TemplateTitle title = (TemplateTitle) findViewById(R.id.chat_title);
                    title.setTitleText(getString(R.string.chat_typing));
                    handler.removeCallbacks(resetTitle);
                    handler.postDelayed(resetTitle, 3000);
                } else {
                    if (messageList.size() == 0) {
                        mMessage.setHasTime(null);
                    } else {
                        mMessage.setHasTime(messageList.get(messageList.size() - 1).getMessage());
                    }
                    messageList.add(mMessage);
                    adapter.notifyDataSetChanged();
                    listView.setSelection(adapter.getCount() - 1);
                }

            }
        }

    }

    /**
     * 显示消息
     *
     * @param messages
     */
    @Override
    public void showMessage(List<TIMMessage> messages) {
        Log.e("showMessssList", "showMessagessssssssss");

        int newMsgNum = 0;
        for (int i = 0; i < messages.size(); ++i) {
            Message mMessage = MessageFactory.getMessage(messages.get(i));
            if (mMessage == null || messages.get(i).status() == TIMMessageStatus.HasDeleted)
                continue;
            ++newMsgNum;

            if (i != messages.size() - 1) {
                mMessage.setHasTime(messages.get(i + 1));
                messageList.add(0, mMessage);
            } else {
                messageList.add(0, mMessage);
            }
        }
        adapter.notifyDataSetChanged();
        listView.setSelection(newMsgNum);
    }


    /**
     * 发送消息失败
     *
     * @param code 返回码
     * @param desc 返回描述
     */
    @Override
    public void onSendMessageFail(int code, String desc) {

    }

    /**
     * 发送图片消息
     */
    @Override
    public void sendImage() {
        Intent intent_album = new Intent("android.intent.action.GET_CONTENT");
        intent_album.setType("image/*");
        startActivityForResult(intent_album, IMAGE_STORE);
    }

    /**
     * 发送照片消息
     */
    @Override
    public void sendPhoto() {
        Intent intent_photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent_photo.resolveActivity(getPackageManager()) != null) {
            File tempFile = FileUtil.getTempFile(FileUtil.FileType.IMG);
            if (tempFile != null) {
                fileUri = Uri.fromFile(tempFile);
            }
            intent_photo.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent_photo, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    /**
     * 发送文本消息
     */
    @Override
    public void sendText() {
        Message message = new TextMessage(input.getText());
        presenter.sendMessage(message.getMessage());
        input.setText("");

    }

    /**
     * 发送文件
     */
    @Override
    public void sendFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, FILE_CODE);
    }


    /**
     * 开始发送语音消息
     */
    @Override
    public void startSendVoice() {
        voiceSendingView.setVisibility(View.VISIBLE);
        voiceSendingView.showRecording();
        recorder.startRecording();

    }

    /**
     * 结束发送语音消息
     */
    @Override
    public void endSendVoice() {
        voiceSendingView.release();
        voiceSendingView.showCancel();
        voiceSendingView.setVisibility(View.GONE);
        recorder.stopRecording();
        if (recorder.getTimeInterval() < 1) {
            Toast.makeText(this, getResources().getString(R.string.chat_audio_too_short), Toast.LENGTH_SHORT).show();
        } else {
            Message message = new VoiceMessage(recorder.getTimeInterval(), recorder.getFilePath());
            presenter.sendMessage(message.getMessage());
        }
    }

    /**
     * 发送小视频消息
     *
     * @param fileName 文件名
     */
    @Override
    public void sendVideo(String fileName) {
        Message message = new VideoMessage(fileName);
        presenter.sendMessage(message.getMessage());
    }


    /**
     * 结束发送语音消息
     */
    @Override
    public void cancelSendVoice() {

    }

    /**
     * 正在发送
     */
    @Override
    public void sending() {
        if (type == TIMConversationType.C2C) {
            Message message = new CustomMessage(CustomMessage.Type.TYPING);
            presenter.sendOnlineMessage(message.getMessage());
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Message message = messageList.get(info.position);
        menu.add(0, 1, Menu.NONE, getString(R.string.chat_del));
        if (message.isSendFail()) {
            menu.add(0, 2, Menu.NONE, getString(R.string.chat_resend));
        }
        if (message instanceof ImageMessage || message instanceof FileMessage) {
            menu.add(0, 3, Menu.NONE, getString(R.string.chat_save));
        }
    }


    /**
     * 发送消息成功
     *
     * @param message 返回的消息
     */
    @Override
    public void onSendMessageSuccess(TIMMessage message) {
//        showMessage(message);

    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Message message = messageList.get(info.position);
        switch (item.getItemId()) {
            case 1:
                Log.e("message", "remove");

                message.remove();
                messageList.remove(info.position);
                adapter.notifyDataSetChanged();
                break;
            case 2:
                messageList.remove(message);
                presenter.sendMessage(message.getMessage());
                break;
            case 3:
                message.save();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && data.getStringExtra("nickname") != null) {
            Log.i(TAG, "onActivityResult: ");
            title.setTitleText(data.getStringExtra("nickname"));
        }
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK && fileUri != null) {
                showImagePreview(fileUri.getPath());
            }
        } else if (requestCode == IMAGE_STORE) {//UI+UE+Web=3000
            if (resultCode == RESULT_OK && data != null) {
                showImagePreview(FileUtil.getImageFilePath(this, data.getData()));
            }

        } else if (requestCode == FILE_CODE) {
            if (resultCode == RESULT_OK) {

                sendFile(FileUtil.getFilePath(this, data.getData()));
            }
        } else if (requestCode == IMAGE_PREVIEW) {
            if (resultCode == RESULT_OK) {
                boolean isOri = data.getBooleanExtra("isOri", false);
                String path = data.getStringExtra("path");
                File file = new File(path);
                if (file.exists() && file.length() > 0) {
                    if (file.length() > 1024 * 1024 * 10) {
                        Toast.makeText(this, getString(R.string.chat_file_too_large), Toast.LENGTH_SHORT).show();
                    } else {
                        Message message = new ImageMessage(path, isOri);
                        presenter.sendMessage(message.getMessage());
                    }
                } else {
                    Toast.makeText(this, getString(R.string.chat_file_not_exist), Toast.LENGTH_SHORT).show();
                }
            }
        }

    }


    private void showImagePreview(String path) {
        if (path == null) return;
        Intent intent = new Intent(this, ImagePreviewActivity.class);

        intent.putExtra("path", path);

        startActivityForResult(intent, IMAGE_PREVIEW);
    }

    private void sendFile(String path) {
        if (path == null) return;
        File file = new File(path);


        if (file.exists()) {
            if (file.length() > 1024 * 1024 * 10) {
                Toast.makeText(this, getString(R.string.chat_file_too_large), Toast.LENGTH_SHORT).show();
            } else {
                Message message = new FileMessage(path);

                presenter.sendMessage(message.getMessage());
            }
        } else {
            Toast.makeText(this, getString(R.string.chat_file_not_exist), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 将标题设置为对象名称
     */
    private Runnable resetTitle = new Runnable() {
        @Override
        public void run() {
            TemplateTitle title = (TemplateTitle) findViewById(R.id.chat_title);
            title.setTitleText(titleStr);
        }
    };


}
