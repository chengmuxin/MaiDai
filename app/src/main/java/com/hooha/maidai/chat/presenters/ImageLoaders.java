package com.hooha.maidai.chat.presenters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by MG on 2016/10/7.
 */
public class ImageLoaders {
    //    private ImageView mImageView;
    private String mUrl;
    private LruCache<String, Bitmap> mCaches;
    private ListView mListView;
    private Set<TucaoAsyncTask> mTask;

    public ImageLoaders(ListView listView) {
        mListView = listView;
        mTask = new HashSet<>();
        //获取最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 4;
        mCaches = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //在每次存入缓存的时候调用
                return value.getByteCount();
            }
        };
    }

    //增加到缓存
    public void addBitmapToCache(String url, Bitmap bitmap) {
        if (getBitmapFromCache(url) == null) {
            mCaches.put(url, bitmap);
        }
    }

    //从缓存中获取数据
    public Bitmap getBitmapFromCache(String url) {
        return mCaches.get(url);
    }

    public void showImageByAsyncTask(ImageView imageView, String url) {
        //从缓存中取出对应图片
        Bitmap bitmap = getBitmapFromCache(url);
        //如果缓存中没有，去下载图片
        if (bitmap == null) {
            new TucaoAsyncTask(url).execute(url);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

//    //用来加载从start到end的所有图片
//    public void loadImages(int start, int end) {
//        for (int i = start; i < end; i++) {
//            String url = ConversationAdapter.[i];
//            //从缓存中取出对应图片
//            Bitmap bitmap = getBitmapFromCache(url);
//            //如果缓存中没有，去下载图片
//            if (bitmap == null) {
//                TucaoAsyncTask task = new TucaoAsyncTask(url);
//                task.execute(url);
//                mTask.add(task);
//            } else {
//                ImageView imageView = (ImageView) mListView.findViewWithTag(url);
//                imageView.setImageBitmap(bitmap);
//            }
//        }
//    }

    public void cancelAllTasks() {
        if (mTask != null) {
            for (TucaoAsyncTask task : mTask) {
                task.cancel(false);
            }
        }
    }

    private class TucaoAsyncTask extends AsyncTask<String, Void, Bitmap> {

        public TucaoAsyncTask(String url) {
//            mImageView = imageView;
            mUrl = url;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            //从网络上获取图片
            Bitmap bitmap = getBitmapFromURL(url);
            if (bitmap != null) {
                //将不在缓存的图片加入缓存
                addBitmapToCache(url, bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView imageView = (ImageView) mListView.findViewWithTag(mUrl);
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
            mTask.remove(this);
        }
    }

    public Bitmap getBitmapFromURL(String urlString) {
        Bitmap bitmap;
        InputStream is = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            is = new BufferedInputStream(connection.getInputStream());
            bitmap = BitmapFactory.decodeStream(is);
            connection.disconnect();
            return bitmap;
        } catch (IOException e) {
//            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
