package com.hooha.maidai.chat.presenters;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hooha.maidai.chat.bean.WuliaobaHuoDong;
import com.hooha.maidai.chat.model.Attention;
import com.hooha.maidai.chat.model.Bang;
import com.hooha.maidai.chat.model.Comment;
import com.hooha.maidai.chat.model.CurLiveInfo;
import com.hooha.maidai.chat.model.Fans;
import com.hooha.maidai.chat.model.LiveInfoJson;
import com.hooha.maidai.chat.model.MyMain;
import com.hooha.maidai.chat.model.MySelfInfo;
import com.hooha.maidai.chat.model.Tucao;
import com.hooha.maidai.chat.model.TucaoInfo;
import com.hooha.maidai.chat.model.UserInfo;
import com.hooha.maidai.chat.model.otherInfo;
import com.hooha.maidai.chat.utils.SxbLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * 网络请求类
 */
public class OKhttpHelper {
    private static final String TAG = OKhttpHelper.class.getSimpleName();
    private static OKhttpHelper instance = null;
    public static final String GET_MYROOMID = "http://182.254.234.225/sxb/index.php?svc=user_av_room&cmd=get";
    public static final String GET_ROOMID = "http://101.201.36.74:8080/maidai/getRoomMsg?svc=av_room&cmd=get";//进入房间
    //    public static final String NEW_ROOM_INFO = "http://182.254.234.225/sxb/index.php?svc=live&cmd=start";
    public static final String NEW_ROOM_INFO = "http://101.201.36.74:8080/maidai/getRoomMsg?svc=live&cmd=start";//创建房间并储存房间信息
    //    public static final String STOP_ROOM = "http://182.254.234.225/sxb/index.php?svc=live&cmd=end";
    public static final String STOP_ROOM = "http://101.201.36.74:8080/maidai/getRoomMsg?svc=live&cmd=end";//退出房间
    //    public static final String GET_LIVELIST = "http://182.254.234.225/sxb/index.php?svc=live&cmd=list";
    public static final String GET_LIVELIST = "http://101.201.36.74:8080/maidai/getRoomMsg?svc=live&cmd=list";//获取正在直播的房间列表
    public static final String SEND_HEARTBEAT = "http://182.254.234.225/sxb/index.php?svc=live&cmd=host_heartbeat";
    public static final String SEND_HEART = "http://101.201.36.74:8080/maidai/getRoomMsg?svc=live&cmd=host_heartbeat";//点赞
    public static final String SEND_FLOWER = "http://101.201.36.74:8080/maidai/getRoomMsg?svc=live&cmd=flower";//献花
    public static final String SEND_GRATUITY = "http://101.201.36.74:8080/maidai/getRoomMsg?svc=live&cmd=gratuity";//打赏
    public static final String GET_COS_SIG = "http://182.254.234.225/sxb/index.php?svc=cos&cmd=get_sign";
    public static final String BANG_FLOWER = "http://101.201.36.74:8080/maidai/getRoomMsg?svc=live&cmd=contributionFlower";//献花贡献榜
    public static final String BANG_GRATUITY = "http://101.201.36.74:8080/maidai/getRoomMsg?svc=live&cmd=contributionGratuity";//打赏贡献榜
    public static final String GET_ACTIVITY = "http://101.201.36.74:8080/maidai/getRoomMsg?svc=live&cmd=getactivity";//获取活动列表
    public static final String START_GAME = "http://101.201.36.74:8080/maidai/getRoomMsg?svc=live&cmd=startGame";//开始游戏
    public static final String SETTLEMENT = "http://101.201.36.74:8080/maidai/getRoomMsg?svc=live&cmd=settlement";//结算
    public static final String ATTENTION = "http://101.201.36.74:8080/maidai/getRoomMsg?svc=live&cmd=remind";//开播时提醒
    public static final String GETATTENTION = "http://101.201.36.74:8080/maidai/getRoomMsg?svc=live&cmd=removeRemind";//开播时不提醒

    public static final String LOGIN = "http://101.201.36.74:8080/maidai/userInfo?svc=user&cmd=login";//登录
    public static final String ZHUCE = "http://101.201.36.74:8080/maidai/userInfo?svc=user&cmd=register";//注册
    public static final String UPDATE_INFO = "http://101.201.36.74:8080/maidai/userInfo?svc=user&cmd=infoInsert";//修改个人信息
    public static final String CHAXUN_INFO = "http://101.201.36.74:8080/maidai/userInfo?svc=user&cmd=infoGet";//检索个人信息
    public static final String XIUGAI_PWD = "http://101.201.36.74:8080/maidai/userInfo?svc=user&cmd=pwdChange";//修改密码
    public static final String SET_PHOTO = "http://101.201.36.74:8080/maidai/userInfo?svc=user&cmd=photo";//上传头像
    public static final String GET_PHOTO = "http://101.201.36.74:8080/maidai/userInfo?svc=user&cmd=getphoto";//获取头像
    public static final String GET_GUANZHU = "http://101.201.36.74:8080/maidai/userInfo?svc=user&cmd=getAttention";//获取关注的人
    public static final String GET_FAN = "http://101.201.36.74:8080/maidai/userInfo?svc=user&cmd=getfans";//获取粉丝，礼物
    public static final String GET_JIEJIUWO = "http://101.201.36.74:8080/maidai/userInfo?svc=user&cmd=helpme";//解救我
    public static final String TUICHU = "http://101.201.36.74:8080/maidai/userInfo?svc=user&cmd=exit";//退出
    public static final String GUANYU_WE = "http://101.201.36.74:8080/maidai/userInfo?svc=user&cmd=aboutus";//关于我们
    public static final String YIJIAN_FANGUI = "http://101.201.36.74:8080/maidai/userInfo?svc=user&cmd=opinion";//意见反馈
    public static final String CHAXUN_GROUPINFO = "http://101.201.36.74:8080/maidai/userInfo?svc=user&cmd=groupselect";//检索群信息
    public static final String GUANZHU_GROUP = "http://101.201.36.74:8080/maidai/userInfo?svc=user&cmd=groupon";//关注群
    public static final String DELETE_GROUP = "http://101.201.36.74:8080/maidai/userInfo?svc=user&cmd=groupoff";//删除群
    public static final String ZHIDING = "http://101.201.36.74:8080/maidai/userInfo?svc=user&cmd=puttop";//置顶
    public static final String NO_ZHIDING = "http://101.201.36.74:8080/maidai/userInfo?svc=user&cmd=canceltop";//取消置顶
    public static final String CHAXUN_ZHIDING_GROUP = "http://101.201.36.74:8080/maidai/userInfo?svc=user&cmd=gettop";//检索置顶群
    public static final String ADD_FRIEND = "http://101.201.36.74:8080/maidai/userInfo?svc=user&cmd=getfriend";//添加好友
    public static final String FRIEND_LIST = "http://101.201.36.74:8080/maidai/userInfo?svc=user&cmd=friendlist";//获取好友列表及在线状态
    public static final String DELETE_FRIEND = "http://101.201.36.74:8080/maidai/userInfo?svc=user&cmd=deletefriend";//删除好友
    public static final String CHAXUN_MAIDAIHAO = "http://101.201.36.74:8080/maidai/userInfo?svc=user&cmd=getUid";//根据卖呆号查uid
    public static final String ADD_GUANZHU = "http://101.201.36.74:8080/maidai/userInfo?svc=user&cmd=addAttention";//添加关注
    public static final String ADD_GOODDAIYOU = "http://101.201.36.74:8080/maidai/userInfo?svc=user&cmd=addBest";//加为最好呆友
    public static final String NO_GUANZHU = "http://101.201.36.74:8080/maidai/userInfo?svc=user&cmd=cencelAttention";//取消关注
    public static final String NO_GOODDAIYOU = "http://101.201.36.74:8080/maidai/userInfo?svc=user&cmd=cencelBest";//取消最好呆友
    public static final String STATEMENT = "http://101.201.36.74:8080/maidai/userInfo?svc=user&cmd=statement";//免责声明
    public static final String MYMAIN = "http://101.201.36.74:8080/maidai/userInfo?svc=user&cmd=myMain"; //我的主页

    public static final String VISITNOTE = "http://101.201.36.74:8080/maidai/maidai?svc=maidai&cmd=visitnote";//浏览帖子
    public static final String VISITCOMMENT = "http://101.201.36.74:8080/maidai/maidai?svc=maidai&cmd=visitComment";//浏览评论
    public static final String COMMENT = "http://101.201.36.74:8080/maidai/maidai?svc=maidai&cmd=comment";//评论
    public static final String NOTEADMIRE = "http://101.201.36.74:8080/maidai/maidai?svc=maidai&cmd=noteAdmire";//帖子点赞
    public static final String NOTEBAD = "http://101.201.36.74:8080/maidai/maidai?svc=maidai&cmd=noteBad";//差评
    public static final String NOTESHARE = "http://101.201.36.74:8080/maidai/maidai?svc=maidai&cmd=noteShare";//分享
    public static final String COMMENTADMIRE = "http://101.201.36.74:8080/maidai/maidai?svc=maidai&cmd=commentAdmire";//评论点赞
    public static final String USERINFO = "http://101.201.36.74:8080/maidai/maidai?svc=maidai&cmd=userinfo";//查看用户信息
    public static final String POST = " http://101.201.36.74:8080/maidai/maidai?svc=maidai&cmd=post";//发帖

    public static OKhttpHelper getInstance() {
        if (instance == null) {
            instance = new OKhttpHelper();
        }
        return instance;
    }


    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .build();


    public String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            return "";
        }
    }

    /**
     * 同步Server 新创建房间信息
     */
    public int notifyServerNewLiveInfo(JSONObject reg) {
        try {

            String res = post(NEW_ROOM_INFO, reg.toString());
            SxbLog.i(TAG, "notifyServer live start  liveinfo: " + res);
            JSONTokener jsonParser = new JSONTokener(res);
            JSONObject response = (JSONObject) jsonParser.nextValue();
            SxbLog.i(TAG, "notifyServerNewLiveInfo: " + response);
            int code = response.getInt("errorCode");
            if (code == 0) {
                return code;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }


    /**
     * 同步Server 关闭房间信息
     */
    public LiveInfoJson notifyServerLiveStop(String id) {
        try {
            JSONObject stopLive = new JSONObject();
            stopLive.put("uid", id);
            stopLive.put("roomId", "" + CurLiveInfo.getRoomNum());
            stopLive.put("activeId", "" + MySelfInfo.getInstance().getRoomType());
            String res = OKhttpHelper.getInstance().post(STOP_ROOM, stopLive.toString());
            Log.d("cmx", "notifyServerLiveStop: " + stopLive.toString());
            JSONTokener jsonParser = new JSONTokener(res);
            JSONObject response = (JSONObject) jsonParser.nextValue();

            int code = response.getInt("errorCode");
            if (code == 0) {
//                JSONObject data = response.getJSONObject("data");
//                JSONObject record = data.getJSONObject("record");
//                String recordS = record.toString();
//                Gson gson = new GsonBuilder().create();
//                LiveInfoJson result = gson.fromJson(recordS, LiveInfoJson.class);
//                return result;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取自己的房间
     */
    public void getMyRoomId(final Context context) {
        try {
            JSONObject myId = new JSONObject();
            myId.put("uid", MySelfInfo.getInstance().getId());
            String response = OKhttpHelper.getInstance().post(GET_MYROOMID, myId.toString());
            SxbLog.i(TAG, "getMyRoomId:" + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
//            int ret = reg_response.getInt("errorCode");
            String ret = reg_response.getString("errorCode");
//            if (ret == 0) {
            if (ret.equals("0")) {
                JSONObject data = reg_response.getJSONObject("data");
                int id = data.getInt("avRoomId");
                SxbLog.i(TAG, "getMyRoomId " + id);
                MySelfInfo.getInstance().setMyRoomNum(id);
                MySelfInfo.getInstance().writeToCache(context.getApplicationContext());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 分配房间
     */
    public void getRoomId(String uid, String roomId, String activeId) {
        int isStartGame;
        int isremind;
        int result;
        try {
            JSONObject req = new JSONObject();
            req.put("uid", uid);
            req.put("roomId", roomId);
            req.put("activeId", activeId);
            SxbLog.i("cmx", "getRoomId:" + req.toString());
            String response = OKhttpHelper.getInstance().post(GET_ROOMID, req.toString());
            SxbLog.i("cmx", "getRoomId:" + response);
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
//            int ret = reg_response.getInt("errorCode");
            String ret = reg_response.getString("errorCode");
//            if (ret == 0) {
            if (ret.equals("0")) {
                CurLiveInfo.setIsStartGame(Integer.parseInt(reg_response.getString("isStartGame")));
                CurLiveInfo.setIsremind(Integer.parseInt(reg_response.getString("isremind")));
                CurLiveInfo.setGameTime(Integer.parseInt(reg_response.getString("gameTime")));
                CurLiveInfo.setActiveInfo(reg_response.getString("activeInfo"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取直播列表
     *
     * @param page     页数
     * @param pagesize 每页个数
     * @return 返回直播列表
     */
    public ArrayList<LiveInfoJson> getLiveList(int page, int pagesize, int activeId) {
        try {
            JSONObject req = new JSONObject();
            req.put("pageIndex", page);
            req.put("pageSize", pagesize);
            req.put("activeId", "" + activeId);
            String response = OKhttpHelper.getInstance().post(GET_LIVELIST, req.toString());
            SxbLog.i(TAG, "getLiveList:" + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                JSONObject data = reg_response.getJSONObject("data");
                JSONArray record = data.getJSONArray("recordList");
                int totalItem = data.getInt("totalItem");
                Type listType = new TypeToken<ArrayList<LiveInfoJson>>() {
                }.getType();
                ArrayList<LiveInfoJson> result = new Gson().fromJson(record.toString(), listType);
                return result;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


    public void sendHeartBeat(String userid, int watchCount, int admireCount, int timeSpan) {
        try {
            JSONObject req = new JSONObject();
            req.put("uid", userid);
            req.put("watchCount", watchCount);
            req.put("admireCount", admireCount);
            req.put("timeSpan", timeSpan);
            String response = OKhttpHelper.getInstance().post(SEND_HEARTBEAT, req.toString());

            SxbLog.i(TAG, "sendHeartBeat " + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                SxbLog.i(TAG, "sendHeartBeat is Ok");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getCosSig() {
        try {
            String response = OKhttpHelper.getInstance().post(GET_COS_SIG, "");

//            SxbLog.i(TAG, "getCosSig " + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                JSONObject data = reg_response.getJSONObject("data");
                String sign = data.getString("sign");
                return sign;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String sendHeart(String uid, String roomId, String reciveUid) {
        try {
            JSONObject req = new JSONObject();
            req.put("uid", "" + uid);
            req.put("roomId", "" + roomId);
            req.put("reciveUid", "" + reciveUid);
            String response = OKhttpHelper.getInstance().post(SEND_HEART, req.toString());

            SxbLog.i(TAG, "sendHeart " + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                SxbLog.i(TAG, "sendHeart is Ok");
                return reg_response.getString("username");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addFriend(String uid, String friendId) {
        try {
            JSONObject req = new JSONObject();
            req.put("uid", "" + uid);
            req.put("friendId", "" + friendId);

            String response = OKhttpHelper.getInstance().post(ADD_FRIEND, req.toString());

            SxbLog.i(TAG, "addFriend " + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                SxbLog.i(TAG, "addFriend is Ok");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delFriend(String uid, String friendId) {
        try {
            JSONObject req = new JSONObject();
            req.put("uid", "" + uid);
            req.put("friendId", "" + friendId);

            String response = OKhttpHelper.getInstance().post(DELETE_FRIEND, req.toString());

            SxbLog.i(TAG, "delFriend " + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                SxbLog.i(TAG, "delFriend is Ok");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String sendFlower(String uid, String roomId, String reciveUid) {
        try {
            JSONObject req = new JSONObject();
            req.put("uid", "" + uid);
            req.put("roomId", "" + roomId);
            req.put("reciveUid", "" + reciveUid);
            String response = OKhttpHelper.getInstance().post(SEND_FLOWER, req.toString());

            SxbLog.i(TAG, "sendFlower " + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                SxbLog.i(TAG, "sendFlower is Ok");
                return reg_response.getString("username");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String sendGratuity(String uid, String roomId, String reciveUid) {
        try {
            JSONObject req = new JSONObject();
            req.put("uid", "" + uid);
            req.put("roomId", "" + roomId);
            req.put("reciveUid", "" + reciveUid);
            String response = OKhttpHelper.getInstance().post(SEND_GRATUITY, req.toString());

            SxbLog.i(TAG, "sendGratuity " + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                SxbLog.i(TAG, "sendGratuity is Ok");
                return reg_response.getString("username");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Bang> bangFlower(String roomId) {
        try {
            JSONObject req = new JSONObject();
            req.put("roomId", roomId);
            String response = OKhttpHelper.getInstance().post(BANG_FLOWER, req.toString());
            SxbLog.i(TAG, "bangFlower:" + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                JSONArray record = reg_response.getJSONArray("record");
                Type listType = new TypeToken<ArrayList<Bang>>() {
                }.getType();
                ArrayList<Bang> result = new Gson().fromJson(record.toString(), listType);
                return result;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Bang> bangGratuity(String roomId) {
        try {
            JSONObject req = new JSONObject();
            req.put("roomId", roomId);
            String response = OKhttpHelper.getInstance().post(BANG_GRATUITY, req.toString());
            SxbLog.i(TAG, "bangGratuity:" + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                JSONArray record = reg_response.getJSONArray("record");
                Type listType = new TypeToken<ArrayList<Bang>>() {
                }.getType();
                ArrayList<Bang> result = new Gson().fromJson(record.toString(), listType);
                return result;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取活动列表
     *
     * @return 返回活动列表
     */
    public ArrayList<WuliaobaHuoDong> getActivityList() {
        try {
            JSONObject req = new JSONObject();
            String response = OKhttpHelper.getInstance().post(GET_ACTIVITY, req.toString());
            SxbLog.i(TAG, "getActivityList:" + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                JSONArray record = reg_response.getJSONArray("record");
                Type listType = new TypeToken<ArrayList<WuliaobaHuoDong>>() {
                }.getType();
                ArrayList<WuliaobaHuoDong> result = new Gson().fromJson(record.toString(), listType);
                return result;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void startGame(String uid1, String uid2, String roomId, String activeId) {
        try {
            JSONObject req = new JSONObject();
            req.put("uid1", "" + uid1);
            req.put("uid2", "" + uid2);
            req.put("roomId", "" + roomId);
            req.put("activeId", "" + activeId);
            String response = OKhttpHelper.getInstance().post(START_GAME, req.toString());

            SxbLog.i(TAG, "startGame " + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                SxbLog.i(TAG, "startGame is Ok");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setLogin(String uid) {
        try {
            JSONObject login = new JSONObject();
            login.put("uid", "" + uid);

            String login_response = OKhttpHelper.getInstance().post(LOGIN, login.toString());

            SxbLog.i(TAG, "setLogin " + login_response.toString());
            JSONTokener jsonParser = new JSONTokener(login_response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                SxbLog.i(TAG, "setLogin is Ok");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRegister(String uid, String userId) {
        try {
            JSONObject register = new JSONObject();
            register.put("uid", "" + uid);

            String login_response = OKhttpHelper.getInstance().post(ZHUCE, register.toString());

            SxbLog.i(TAG, "setRegister " + login_response.toString());
            JSONTokener jsonParser = new JSONTokener(login_response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                SxbLog.i(TAG, "setRegister is Ok");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public otherInfo getPhoto(String uid) {
        try {
            JSONObject photo = new JSONObject();
            photo.put("uid", "" + uid);

            String photo_response = OKhttpHelper.getInstance().post(GET_PHOTO, photo.toString());

            SxbLog.i(TAG, "getPhoto " + photo_response.toString());
            JSONTokener jsonParser = new JSONTokener(photo_response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();

            String pic = reg_response.getString("picurl");
            otherInfo info = new otherInfo();
            JSONObject data = reg_response.getJSONObject("data");
            // String sign = data.getString("sign");

            info.setAvatar(data.getString("picture"));
//            info.setName(data.getString("username"));
//            info.setSex(data.getString("sex"));

            return info;

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public otherInfo GET_JIEJIUWO(String uid) {
        try {
            JSONObject jiejiuwo = new JSONObject();
            jiejiuwo.put("uid", "" + uid);
            String login_response = OKhttpHelper.getInstance().post(GET_JIEJIUWO, jiejiuwo.toString());
            SxbLog.i(TAG, "jiejiuwo " + login_response.toString());
            JSONTokener jsonParser = new JSONTokener(login_response);
            JSONObject jie_response = (JSONObject) jsonParser.nextValue();
            int ret = jie_response.getInt("errorCode");
            if (ret == 0) {
                otherInfo info = new otherInfo();
                JSONObject jie_data = jie_response.getJSONObject("data");
                JSONArray record = jie_data.getJSONArray("record");
                Log.i(TAG, "GET_JIEJIUWO: " + jie_response);
                info.setAge(jie_data.getString("age"));
                info.setName(jie_data.getString("name"));
                info.setSex(jie_data.getString("sex"));
                info.setAvatar(jie_data.getString("picurl"));
                info.setId(jie_data.getString("uid"));
                return info;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }
        return null;
    }

    public void setPhoto(String picurl, String uid) {
        try {
            JSONObject photo = new JSONObject();
            photo.put("picurl", "" + picurl);
            photo.put("uid", "" + uid);
//            photo.put("photoName", "" + photoName);
            String photo_response = OKhttpHelper.getInstance().post(SET_PHOTO, photo.toString());

            SxbLog.i(TAG, "setPhoto " + photo_response.toString());
            JSONTokener jsonParser = new JSONTokener(photo_response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                SxbLog.i(TAG, "setPhoto is Ok");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String settlement(String roomId) {
        try {
            String winuid = "";
            while (winuid.isEmpty()) {
                JSONObject req = new JSONObject();
                req.put("roomId", roomId);
                String response = OKhttpHelper.getInstance().post(SETTLEMENT, req.toString());
                SxbLog.i(TAG, "settlement:" + response.toString());
                JSONTokener jsonParser = new JSONTokener(response);
                JSONObject reg_response = (JSONObject) jsonParser.nextValue();
                int ret = reg_response.getInt("errorCode");
                if (ret == 0) {
                    winuid = reg_response.getString("winuid");
                    Log.d("cmx", "winuid: " + winuid);
                    if (winuid.isEmpty()) {
                        Thread.sleep(1000);
                        continue;
                    }
                    String result = "";
                    if (winuid.equals("draw")) {
                        result = "双方对战结果是平局！";
                    } else {
                        String username = reg_response.getString("username");
                        result = "恭喜" + username + "获得胜利！";
                    }
                    return result;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "未获取到双方对战结果";
    }

    public void attention(String uid, String roomId) {
        try {
            JSONObject req = new JSONObject();
            req.put("uid", "" + uid);
            req.put("roomId", "" + roomId);
            String response = OKhttpHelper.getInstance().post(ATTENTION, req.toString());

            SxbLog.i("cmx", "attention " + req.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                SxbLog.i(TAG, "attention is Ok");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getattention(String uid, String roomId) {
        try {
            JSONObject req = new JSONObject();
            req.put("uid", "" + uid);
            req.put("roomId", "" + roomId);
            String response = OKhttpHelper.getInstance().post(GETATTENTION, req.toString());

            SxbLog.i("cmx", "getattention " + req.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                SxbLog.i(TAG, "getattention is Ok");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Tucao> visitnote(String uid, String noteType, int page, int pagesize) {
        try {
            JSONObject req = new JSONObject();
            req.put("uid", "" + uid);
            req.put("noteType", "" + noteType);
            req.put("page", page);
            req.put("pagesize", pagesize);
            Log.d("cmx", "visitnote: " + req.toString());
            String response = OKhttpHelper.getInstance().post(VISITNOTE, req.toString());
            SxbLog.i("cmx", "visitnote:" + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            JSONArray record = reg_response.getJSONArray("record");
            Type listType = new TypeToken<ArrayList<Tucao>>() {
            }.getType();
            ArrayList<Tucao> result = new Gson().fromJson(record.toString(), listType);
            return result;

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Comment> visitcomment(String noteId, int page, int pageSize) {
        try {
            JSONObject req = new JSONObject();
            req.put("noteId", "" + noteId);
            req.put("page", page);
            req.put("pageSize", pageSize);
            Log.d("cmx", "visitcomment: " + req.toString());
            String response = OKhttpHelper.getInstance().post(VISITCOMMENT, req.toString());
            SxbLog.i("cmx", "visitcomment:" + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            JSONArray record = reg_response.getJSONArray("record");
            Type listType = new TypeToken<ArrayList<Comment>>() {
            }.getType();
            ArrayList<Comment> result = new Gson().fromJson(record.toString(), listType);
            return result;

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int comment(String commentId, String noteId, String replyId, String commentInfo) {
        int ret = -1;
        try {
            JSONObject req = new JSONObject();
            req.put("commentId", "" + commentId);
            req.put("noteId", "" + noteId);
            if (!"".equals(replyId)) {
                req.put("replyId", "" + replyId);
            }
            req.put("commentInfo", "" + commentInfo);
            Log.d("cmx", "comment: " + req.toString());
            String response = OKhttpHelper.getInstance().post(COMMENT, req.toString());
            SxbLog.i("cmx", "comment:" + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                SxbLog.i("cmx", "comment is Ok");
                return ret;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public int noteadmire(String noteId, String uid) {
        int ret = -1;
        try {
            JSONObject req = new JSONObject();
            req.put("noteId", "" + noteId);
            req.put("uid", "" + uid);
            Log.d("cmx", "noteadmire: " + req.toString());
            String response = OKhttpHelper.getInstance().post(NOTEADMIRE, req.toString());
            SxbLog.i("cmx", "noteadmire:" + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                SxbLog.i(TAG, "noteadmire is Ok");
                return ret;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public int notebad(String noteId, String uid) {
        int ret = -1;
        try {
            JSONObject req = new JSONObject();
            req.put("noteId", "" + noteId);
            req.put("uid", "" + uid);
            Log.d("cmx", "notebad: " + req.toString());
            String response = OKhttpHelper.getInstance().post(NOTEBAD, req.toString());
            SxbLog.i("cmx", "notebad:" + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                SxbLog.i(TAG, "notebad is Ok");
                return ret;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public void noteshare(String noteId, String uid) {
        try {
            JSONObject req = new JSONObject();
            req.put("noteId", "" + noteId);
            req.put("uid", "" + uid);
            Log.d("cmx", "noteshare: " + req.toString());
            String response = OKhttpHelper.getInstance().post(NOTESHARE, req.toString());
            SxbLog.i("cmx", "noteshare:" + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                SxbLog.i(TAG, "noteshare is Ok");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void commentadmire(String noteId, String commentId) {
        try {
            JSONObject req = new JSONObject();
            req.put("noteId", "" + noteId);
            req.put("commentId", "" + commentId);
            Log.d("cmx", "commentadmire: " + req.toString());
            String response = OKhttpHelper.getInstance().post(COMMENTADMIRE, req.toString());
            SxbLog.i("cmx", "commentadmire:" + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                SxbLog.i(TAG, "commentadmire is Ok");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TucaoInfo userinfo(String uid, String page, String pagesize) {
        try {
            JSONObject req = new JSONObject();
            req.put("uid", "" + uid);
            req.put("page", "" + page);
            req.put("pageSize", "" + pagesize);
            Log.d("cmx", "userinfo: " + req.toString());
            String response = OKhttpHelper.getInstance().post(USERINFO, req.toString());
            SxbLog.i("cmx", "userinfo:" + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            List<TucaoInfo> infoList = new ArrayList<TucaoInfo>();
            TucaoInfo info = new TucaoInfo();
            info.setUseravatar(reg_response.getString("useravatar"));
            info.setUsername(reg_response.getString("username"));
            info.setUserattention(reg_response.getInt("userattention"));
            info.setUserfans(reg_response.getInt("userfans"));
            info.setUsernoteCount(reg_response.getInt("usernoteCount"));
            info.setUserShareCount(reg_response.getInt("userShareCount"));
            info.setUserCommentCount(reg_response.getInt("userCommentCount"));
            JSONArray record = reg_response.getJSONArray("noteRecord");
            Type listType = new TypeToken<ArrayList<Tucao>>() {
            }.getType();
            ArrayList<Tucao> result = new Gson().fromJson(record.toString(), listType);
            info.setInfo(result);
            return info;

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //    picture 头像 name 昵称 sex 性别
    //    age 年龄 hobby 爱好 topic 话题 special 特长
    //    检索个人信息用于在个人设置里获取个人信息
    public UserInfo infoget(String uid) {
        try {
            JSONObject req = new JSONObject();
            req.put("uid", "" + uid);


            Log.d("cmx", "infoget: " + req.toString());
            String response = OKhttpHelper.getInstance().post(CHAXUN_INFO, req.toString());
            SxbLog.i("cmx", "infoget:" + response.toString());
//            String response  = new String(response.getBytes("UTF-8"), "GBK");
//            SxbLog.i("cmx", "infoget:" + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
//            List<UserInfo> infoList = new ArrayList<UserInfo>();

            UserInfo info = new UserInfo();
            JSONObject data = reg_response.getJSONObject("data");
            // String sign = data.getString("sign");

            info.setAge(data.getString("age"));
            info.setName(data.getString("username"));
            info.setSex(data.getString("sex"));
            info.setTopic(data.getString("topic"));
            info.setSpecial(data.getString("special"));
            info.setHobby(data.getString("hobby"));
//            JSONArray record = reg_response.getJSONArray("noteRecord");
//            Type listType = new TypeToken<ArrayList<UserInfo>>() {
//            }.getType();
//            ArrayList<UserInfo> result = new Gson().fromJson(record.toString(), listType);
//            info.setInfo(result);
//            infoList.add(info);
            Log.i(TAG, "infoget: userinfo" + info);
            return info;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //    picture 头像 name 昵称 sex 性别
    //    age 年龄 hobby 爱好 topic 话题 special 特长
    //    检索个人信息用于在个人设置里获取个人信息
    public UserInfo otherinfoget(String uid, String otherUid) {
        try {
            JSONObject req = new JSONObject();
            req.put("uid", "" + uid);
            req.put("otherUid", "" + otherUid);

            Log.d("cmx", "infoget: " + req.toString());
            String response = OKhttpHelper.getInstance().post(CHAXUN_INFO, req.toString());
            SxbLog.i("cmx", "infoget:" + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();

            UserInfo info = new UserInfo();
            JSONObject data = reg_response.getJSONObject("data");

            info.setAge(data.getString("age"));
            info.setName(data.getString("username"));
            info.setSex(data.getString("sex"));
            info.setTopic(data.getString("topic"));
            info.setSpecial(data.getString("special"));
            info.setHobby(data.getString("hobby"));
            info.setIsAttention(data.getInt("isAttention"));
            info.setBest(data.getString("best"));
            info.setMaidaiNum(data.getString("maidaiNum"));
            Log.i(TAG, "infoget: userinfo" + info);
            return info;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param uid     用户id
     * @param name    昵称
     * @param sex     性别
     * @param age     年龄
     * @param hobby   爱好
     * @param topic   话题
     * @param special 特长
     */
    public void set_update_info(String uid, String name, String sex, int age, String hobby, String topic, String special) {
        try {
            JSONObject update_info = new JSONObject();
            update_info.put("uid", "" + uid);
//            update_info.put("picture", "" + picture);
            update_info.put("name", "" + name);
            update_info.put("sex", "" + sex);
            update_info.put("age", "" + age);
            update_info.put("hobby", "" + hobby);
            update_info.put("topic", "" + topic);
            update_info.put("special", "" + special);
            Log.d(TAG, "set_update_info: update" + update_info.toString());
            String update_info_response = OKhttpHelper.getInstance().post(UPDATE_INFO, update_info.toString());
            SxbLog.i(TAG, "set_update_info " + update_info_response.toString());
            JSONTokener jsonParser = new JSONTokener(update_info_response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                SxbLog.i(TAG, "setLogin is Ok");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Posttext(String type, String text, String uid, String label) {
        try {
            JSONObject req = new JSONObject();
            req.put("text", "" + text);
            req.put("type", "" + type);
            req.put("uid", uid);
            req.put("label", label);
            Log.d("cmx", "post: " + req.toString());
            String response = OKhttpHelper.getInstance().post(POST, req.toString());
            SxbLog.i("Post", "post:" + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                SxbLog.i(TAG, "Post is Ok");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Postphoto(String type, String wenzi, String picture, String pictureWidth, String pictureHight, String uid, String label) {
        try {
            JSONObject req = new JSONObject();
            req.put("type", "" + type);
            req.put("text", "" + wenzi);
            req.put("picture", picture);
            req.put("pictureWidth", pictureWidth);
            req.put("pictureHight", pictureHight);
            req.put("uid", uid);
            req.put("label", label);
            Log.d("cmx", "post: " + req.toString());
            String response = OKhttpHelper.getInstance().post(POST, req.toString());
            SxbLog.i("Post", "post:" + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                SxbLog.i(TAG, "Post is Ok");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Postvideo(String type, String wenzi, String video, String picture, String pictureWidth, String pictureHight, String uid, String label) {
        try {
            JSONObject req = new JSONObject();
            req.put("type", "" + type);
            req.put("picture", picture);
            req.put("pictureWidth", pictureWidth);
            req.put("pictureHight", pictureHight);
            req.put("text", "" + wenzi);
            req.put("video", video);
            req.put("uid", uid);
            req.put("label", label);
            Log.d("cmx", "Postvideo: " + req.toString());
            String response = OKhttpHelper.getInstance().post(POST, req.toString());
            SxbLog.i("Postvideo", "post:" + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                SxbLog.i(TAG, "Postvideo is Ok");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void about() {
        try {
            JSONObject req = new JSONObject();
            String response = OKhttpHelper.getInstance().post(GUANYU_WE, req.toString());
            SxbLog.i("cmx", "about:" + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                String number = reg_response.getString("number");
                Log.d("cmx", "number: " + number);
                UserInfo.getInstance().setNumber(number);
                String introduce = reg_response.getString("introduce");
                Log.d("cmx", "introduce: " + introduce);
                UserInfo.getInstance().setIntroduce(introduce);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void feedback(String uid, String opinion) {
        try {
            JSONObject req = new JSONObject();
            req.put("uid", "" + uid);
            req.put("opinion", "" + opinion);
            SxbLog.i("cmx", "yijian_fangui:" + req.toString());
            String response = OKhttpHelper.getInstance().post(YIJIAN_FANGUI, req.toString());
            SxbLog.i("cmx", "yijian_fangui:" + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void statement() {
        try {
            JSONObject req = new JSONObject();
            String response = OKhttpHelper.getInstance().post(STATEMENT, req.toString());
            SxbLog.i("cmx", "statement:" + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                String info = reg_response.getString("info");
                UserInfo.getInstance().setStatement(info);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void myMain(String uid) {
        try {
            JSONObject req = new JSONObject();
            req.put("uid", "" + uid);
            SxbLog.i("cmx", "myMain:" + req.toString());
            String response = OKhttpHelper.getInstance().post(MYMAIN, req.toString());
            SxbLog.i("cmx", "myMain:" + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                MyMain.getInstance().setPicture(reg_response.getString("picture"));
                MyMain.getInstance().setName(reg_response.getString("name"));
                MyMain.getInstance().setContribution(reg_response.getString("contribution"));

                JSONArray record = reg_response.getJSONArray("attention");
                Type listType = new TypeToken<ArrayList<Attention>>() {
                }.getType();
                ArrayList<Attention> result = new Gson().fromJson(record.toString(), listType);
                MyMain.getInstance().setList(result);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addAttention(String uid, String attentionId) {
        try {
            JSONObject req = new JSONObject();
            req.put("uid", "" + uid);
            req.put("attentionId", "" + attentionId);
            SxbLog.i("cmx", "addAttention:" + req.toString());
            String response = OKhttpHelper.getInstance().post(ADD_GUANZHU, req.toString());
            SxbLog.i("cmx", "addAttention:" + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                Log.d("cmx", "addAttention OK");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cencelAttention(String uid, String attentionId) {
        try {
            JSONObject req = new JSONObject();
            req.put("uid", "" + uid);
            req.put("attentionId", "" + attentionId);
            SxbLog.i("cmx", "cencelAttention:" + req.toString());
            String response = OKhttpHelper.getInstance().post(NO_GUANZHU, req.toString());
            SxbLog.i("cmx", "cencelAttention:" + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                Log.d("cmx", "cencelAttention OK");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addBest(String uid, String bestId) {
        try {
            JSONObject req = new JSONObject();
            req.put("uid", "" + uid);
            req.put("bestId", "" + bestId);
            SxbLog.i("cmx", "addBest:" + req.toString());
            String response = OKhttpHelper.getInstance().post(ADD_GOODDAIYOU, req.toString());
            SxbLog.i("cmx", "addBest:" + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                Log.d("cmx", "addBest OK");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cencelBest(String uid, String bestId) {
        try {
            JSONObject req = new JSONObject();
            req.put("uid", "" + uid);
            req.put("bestId", "" + bestId);
            SxbLog.i("cmx", "cencelBest:" + req.toString());
            String response = OKhttpHelper.getInstance().post(NO_GOODDAIYOU, req.toString());
            SxbLog.i("cmx", "cencelBest:" + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            int ret = reg_response.getInt("errorCode");
            if (ret == 0) {
                Log.d("cmx", "cencelBest OK");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public ArrayList<Fans> getAttention(String uid) {
        try {
            JSONObject req = new JSONObject();
            req.put("uid", "" + uid);
            SxbLog.i("cmx", "getAttention:" + req.toString());
            String response = OKhttpHelper.getInstance().post(GET_GUANZHU, req.toString());
            SxbLog.i("cmx", "getAttention:" + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            JSONObject data = reg_response.getJSONObject("data");
            JSONArray record = data.getJSONArray("record");
            Type listType = new TypeToken<ArrayList<Fans>>() {
            }.getType();
            ArrayList<Fans> result = new Gson().fromJson(record.toString(), listType);
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Fans> getfans(String uid) {
        try {
            JSONObject req = new JSONObject();
            req.put("uid", "" + uid);
            SxbLog.i("cmx", "getfans:" + req.toString());
            String response = OKhttpHelper.getInstance().post(GET_FAN, req.toString());
            SxbLog.i("cmx", "getfans:" + response.toString());
            JSONTokener jsonParser = new JSONTokener(response);
            JSONObject reg_response = (JSONObject) jsonParser.nextValue();
            JSONObject data = reg_response.getJSONObject("data");
            JSONArray record = data.getJSONArray("record");
            Type listType = new TypeToken<ArrayList<Fans>>() {
            }.getType();
            ArrayList<Fans> result = new Gson().fromJson(record.toString(), listType);
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
