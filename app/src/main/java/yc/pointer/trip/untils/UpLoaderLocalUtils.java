package yc.pointer.trip.untils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import yc.pointer.trip.R;
import yc.pointer.trip.bean.EntityVideo;
import yc.pointer.trip.network.OkHttpUtils;


/**
 * Created by Lenovo
 * 2018/3/20
 * 20:40
 */

public class UpLoaderLocalUtils {
    private static UpLoaderLocalUtils utils = null;


    public static UpLoaderLocalUtils getInstance() {
        if (utils == null) {
            synchronized (OkHttpUtils.class) {
                if (utils == null) {
                    utils = new UpLoaderLocalUtils();
                }
            }
        }
        return utils;
    }


    /**
     * 创建cache
     */
    private LruCache<String, Bitmap> mCache;

    private UpLoaderLocalUtils() {
        //获取最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 4;//缓存的大小
        mCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //在每次存入缓存的时候调用
                return value.getByteCount();//bitmap实际大小
            }
        };
         }

    /**
     * 增加到缓存
     *
     * @param url
     * @param bitmap
     */

    public void addBitmapToCache(String url, Bitmap bitmap) {

        if (getBitmapFormCache(url) == null) {//当前缓存是否存在
            mCache.put(url, bitmap);
        }
    }

    /**
     * 从缓存中获取数据
     *
     * @param url
     * @return
     */
    public Bitmap getBitmapFormCache(String url) {
        return mCache.get(url);
    }


    private EntityVideo getBitmapFormUrl(String urlString) {
        EntityVideo entityVideo = new EntityVideo();
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(urlString);
        int metadata = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        Bitmap frameAtTime = retriever.getFrameAtTime();
        entityVideo.setDuration(metadata);
        entityVideo.setFrameAtTime(frameAtTime);
        return entityVideo;
    }

    public void showImageByAsyncTask(ImageView imageView,  String url, int position) {
        //从缓存中取出对应的图片
        Bitmap bitmap = getBitmapFormCache(url);
        if (bitmap == null) {
            //如果缓存中没有去网络加载图片
            new NewsAsyncTask(imageView, url).execute(url);
//            imageView.setImageResource(R.mipmap.ic_launcher);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

//    public void cancelAllTasks(){
//        if (mTask!=null){
//            for (NewsAsyncTask task:mTask) {
//                task.cancel(false);
//            }
//        }
//    }


    private class NewsAsyncTask extends AsyncTask<String, Void, EntityVideo> {
        private ImageView mImageView;

        private String mUrl;

        public NewsAsyncTask(ImageView mImageView,  String mUrl) {
            this.mImageView = mImageView;

            this.mUrl = mUrl;
        }


        @Override
        protected EntityVideo doInBackground(String... strings) {
            String url = strings[0];

            EntityVideo entityVideo = getBitmapFormUrl(url);

            if (entityVideo.getFrameAtTime() != null) {
                addBitmapToCache(url, entityVideo.getFrameAtTime());
            }
            return entityVideo;
        }

        @Override
        protected void onPostExecute(EntityVideo video) {
            super.onPostExecute(video);
            if (mImageView.getTag().equals(mUrl)) {
                mImageView.setImageBitmap(video.getFrameAtTime());
            }
              }

    }


}
