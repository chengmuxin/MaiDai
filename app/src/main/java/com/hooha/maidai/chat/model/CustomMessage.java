package com.hooha.maidai.chat.model;

import android.content.Context;
import android.util.Log;

import com.hooha.maidai.chat.adapters.ChatAdapter;
import com.tencent.TIMCustomElem;
import com.tencent.TIMMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 自定义消息
 */
public class CustomMessage extends Message {


    private String TAG = getClass().getSimpleName();

    private final int TYPE_TYPING = 14;

    private Type type;
    private String desc;
    private String data;

    public CustomMessage(TIMMessage message){
        this.message = message;
        TIMCustomElem elem = (TIMCustomElem) message.getElement(0);
        parse(elem.getData());

    }

    public CustomMessage(Type type){
        message = new TIMMessage();
        String data = "";
        JSONObject dataJson = new JSONObject();
        try{
            switch (type){
                case TYPING:
                    dataJson.put("userAction",TYPE_TYPING);
                    dataJson.put("actionParam","EIMAMSG_InputStatus_Ing");
                    data = dataJson.toString();
            }
        }catch (JSONException e){
            Log.e(TAG, "generate json error");
        }
        TIMCustomElem elem = new TIMCustomElem();
        elem.setData(data.getBytes());
        message.addElement(elem);
    }


    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    private void parse(byte[] data){
//        try{
//            JsonReader reader = new JsonReader(new InputStreamReader(new ByteArrayInputStream(data),"UTF-8"));
//            reader.beginObject();
//            while(reader.hasNext()){
//                String keyName = reader.nextName();
//                if("userAction".equals(keyName)){
//                    int action = reader.nextInt();
//                    switch (action){
//                        case TYPE_TYPING:
//                            type = Type.TYPING;
//                            if (reader.hasNext()&&reader.nextName().equals("actionParam")){
//                                this.data = reader.nextString();
//                            }
//                            break;
//
//                    }
//                }
//            }
//            reader.endObject();
//            reader.close();
//        }catch (IOException e){
//            Log.e(TAG, "parse json error");
//        }
    }

    /**
     * 显示消息
     *
     * @param viewHolder 界面样式
     * @param context    显示消息的上下文
     */
    @Override
    public void showMessage(ChatAdapter.ViewHolder viewHolder, Context context) {

    }

    /**
     * 获取消息摘要
     */
    @Override
    public String getSummary() {
        return null;
    }

    /**
     * 保存消息或消息文件
     */
    @Override
    public void save() {

    }

    public enum Type{
        TYPING,
    }
}
