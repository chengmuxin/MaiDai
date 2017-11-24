package com.hooha.maidai.chat.utils;


import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 *
 * 上传工具类
 */
public class UploadUtil {
	public  void uploadMethod(final RequestParams params,final String uploadHost) {
		HttpUtils http = new HttpUtils();
		http.send(HttpRequest.HttpMethod.POST, uploadHost, params,new RequestCallBack<String>() {
			@Override
			public void onStart() {
				//                      msgTextview.setText("conn...");
			}
			@Override
			public void onLoading(long total, long current,boolean isUploading) {
				if (isUploading) {
					//                          msgTextview.setText("upload: " + current + "/"+ total);
				} else {
					//                          msgTextview.setText("reply: " + current + "/"+ total);
				}
			}
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				//                      msgTextview.setText("reply: " + responseInfo.result);
			}
			@Override
			public void onFailure(HttpException error, String msg) {
				//                      msgTextview.setText(error.getExceptionCode() + ":" + msg);
			}
		});
	}
}