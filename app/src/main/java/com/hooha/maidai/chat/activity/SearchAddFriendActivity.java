package com.hooha.maidai.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.Toast;
import com.hooha.maidai.chat.R;
import com.hooha.maidai.chat.adapters.ProfileSummaryAdapter;
import com.hooha.maidai.chat.base.BaseActivity;
import com.hooha.maidai.chat.model.FriendProfile;
import com.hooha.maidai.chat.model.ProfileSummary;
import com.tencent.TIMUserProfile;
import com.tencent.qcloud.presentation.presenter.FriendshipManagerPresenter;
import com.tencent.qcloud.presentation.viewfeatures.FriendInfoView;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import java.util.ArrayList;
import java.util.List;

/**扫一扫
 * Created by MG on 2016/7/19.
 */
public class SearchAddFriendActivity extends BaseActivity implements FriendInfoView, View.OnClickListener, AdapterView.OnItemClickListener{
    private FriendshipManagerPresenter presenter_sys;
    private EditText input;
    private  TextView textView,text_add;
    private RelativeLayout searchRelativeLayout;//扫一扫
    private RelativeLayout cellPhoneContactRelativeLayout;//手机通讯录
    public static final int DISTRIBUTION_REQUEST_CODE = 0;
    ListView mSearchList_sys;
    //ProfileSummaryAdapter adapter_sys;
    List<ProfileSummary> list_sys = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activivty_search_add_friend);
//         textView=(TextView)findViewById(R.id.tv_scan_result);
   //     adapter_sys = new ProfileSummaryAdapter(this, R.layout.activity_add_friend, list_sys);
      //  text_add=(TextView)findViewById(R.id.id_phone);
   //     mSearchList_sys =(ListView) findViewById(R.id.list_add_sys);
   //     mSearchList_sys.setAdapter(adapter_sys);
        setTitle("添加呆友");
        input = (EditText) findViewById(R.id.input_phone);
        searchRelativeLayout= (RelativeLayout) findViewById(R.id.activity_search_friend_search_RelativeLayout);
        searchRelativeLayout.setOnClickListener(this);
        presenter_sys = new FriendshipManagerPresenter(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        list_sys.get(position).onClick(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_search_friend_search_RelativeLayout:
                Toast.makeText(SearchAddFriendActivity.this, "扫一扫", Toast.LENGTH_SHORT).show();
                startActivityForResult(new Intent(this, CaptureActivity.class), DISTRIBUTION_REQUEST_CODE);
                break;
            case R.id.activity_search_friend_cell_phone_contact_RelativeLayout:
                Toast.makeText(SearchAddFriendActivity.this, "手机通讯录", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==RESULT_OK){

            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            presenter_sys.searchFriendById(scanResult);

            Intent intent=new Intent(this,AddFriendActivity.class);
          startActivityForResult(intent, resultCode, bundle);
           // Bundle bundle1 = new Bundle();
            bundle.putString("id", scanResult.toString());
            intent.putExtras(bundle);
            startActivity(intent);
            finish();

     //       textView.setText(scanResult);

        }
    }

private boolean needAdd(String id){
    for (ProfileSummary item : list_sys){
        if (item.getIdentify().equals(id)) return false;
    }
    return true;
}
    @Override
    public void showUserInfo(List<TIMUserProfile> users) {
        // TODO: 2016/7/29 这里可以获取一个好友列表，参考SearchFriendActivity，感觉demo比这个界面设计的好，就没全部替换，点击添加呆友跳转到demo中的搜索界面，点击对话框中的添加呆友，跳转到本界面

        if (users == null) return;
        for (TIMUserProfile item : users){
            if (needAdd(item.getIdentifier()))
                list_sys.add(new FriendProfile(item));
        }
   //     adapter_sys.notifyDataSetChanged();
        if (list_sys.size() == 0){
      Toast.makeText(getApplication(),"咋没有呢？",Toast.LENGTH_SHORT).show();
    } }
}

//    @Override
//    public boolean onKey(View v, int keyCode, KeyEvent event) {
//        if (event.getAction() != KeyEvent.ACTION_UP){   // 忽略其它事件
//            return false;
//        }
//
//        switch (keyCode) {
//            case KeyEvent.KEYCODE_ENTER:
//                String key = input.getText().toString();
//                if (key.equals("")) return true;
//                presenter.searchFriendByName(key,true);
//                //给手机号加上86-
//                if (maybePhone(key)){
//                    key = "86-" + key;
//                }
//                presenter.searchFriendById(key);
//                return true;
//            default:
//                return super.onKeyUp(keyCode, event);
//        }
//    }
//    private boolean maybePhone(String str){
//        if (str.length() != 11) return false;
//        for (int i = 0 ; i < str.length() ; ++i){
//            if(!Character.isDigit(str.charAt(i))) return false;
//        }
//        return true;
//    }