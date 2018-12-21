package yc.pointer.trip.network;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.zxing.common.StringUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.*;

import org.greenrobot.eventbus.EventBus;

import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import yc.pointer.trip.R;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.untils.GlideCircleTransform;
import yc.pointer.trip.untils.StringUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by Lenovo on 2017/5/21.
 * 封装的网络请求
 */

public class OkHttpUtils {


    private static OkHttpUtils mInstance;
    private OkHttpClient mHttpClient;
    private Gson mGson;
    private static boolean LogFlag = true;//true 打印日志   false:不打印日志

    public static OkHttpUtils getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpUtils.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpUtils();
                }
            }
        }
        return mInstance;
    }

    private OkHttpUtils() {
        if (LogFlag) {
            mHttpClient = new OkHttpClient.Builder().addInterceptor(new LogInterceptor()).build();
        } else {
            mHttpClient = new OkHttpClient.Builder()
                    .readTimeout(10000, TimeUnit.SECONDS)
                    .writeTimeout(10000, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(false)
                    .build();
        }

        mGson = new Gson();
    }


    /**
     * 缓存
     *
     * @param mContext 上下文菜单
     * @return
     */
    public OkHttpUtils setCache(Context mContext) {
        File sdCache = mContext.getExternalCacheDir();
        int cacheSize = 10 * 1024 * 1024;
        mHttpClient.newBuilder().cache(new Cache(sdCache.getAbsoluteFile(), cacheSize)).build();
        return mInstance;
    }

    /**
     * get请求
     *
     * @param url      网址
     * @param callback 回调接口
     */
    public void get(String url, HttpCallBack callback) {
        Request request = buildRequest(url, HttpMethodType.GET, null);
        doRequest(request, callback);
    }


    /**
     * post请求
     *
     * @param url      网址
     * @param param    请求体
     * @param callback 回调接口
     */

    public void post(String url, RequestBody param, HttpCallBack callback) {
        Request request = buildRequest(url, HttpMethodType.POST, param);
        doRequest(request, callback);
    }


    public static void displayImg(ImageView imageView, String pathUrl) {
        //UpLoaderLocalUtils imageLoader=new UpLoaderLocalUtils().
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.gray_picture) //设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.no_photo)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.no_photo)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中
                //.considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                //.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                //.bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                //.decodingOptions(android.graphics.BitmapFactory.Options.class)//设置图片的解码配置
                .build();//构建完成
        if (pathUrl.contains("https:")) {

            ImageLoader.getInstance().displayImage(pathUrl, imageView, options);
        } else {
            ImageLoader.getInstance().displayImage(URLUtils.BASE_URL + pathUrl, imageView, options);
        }
    }

    public static void displayAdvertImg(ImageView imageView, String pathUrl) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中
                //.considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                //.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                //.bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                //.decodingOptions(android.graphics.BitmapFactory.Options.class)//设置图片的解码配置
                .build();//构建完成
        if (pathUrl.contains("https:")) {
            ImageLoader.getInstance().displayImage(pathUrl, imageView, options);
        } else {
            ImageLoader.getInstance().displayImage(URLUtils.BASE_URL + pathUrl, imageView, options);
        }
    }

    /**
     * 圆形图片
     *
     * @param context
     * @param imageView
     * @param pathUrl
     */
    public static void displayGlideCircular(Context context, ImageView imageView, String pathUrl, ImageView imgVip, String is_vip) {
        displayImg(imageView, pathUrl);
        if (!StringUtil.isEmpty(is_vip)) {
            if (is_vip.equals("1")) {
                //黄金会员
                imgVip.setVisibility(View.VISIBLE);
                imgVip.setImageResource(R.mipmap.icon_vip);
            } else if (is_vip.equals("2")) {
                //白金会员
                imgVip.setVisibility(View.VISIBLE);
                imgVip.setImageResource(R.mipmap.icon_baijin);
            } else {
                imgVip.setVisibility(View.GONE);
            }
        } else {
            imgVip.setVisibility(View.GONE);
        }
    }

    /**
     * 加载圆形图片
     *
     * @param context   上下文
     * @param imageView 图片控件
     * @param pathUrl   图片地址
     */
    public static void displayGlideCircular(Context context, ImageView imageView, String pathUrl) {
        if (pathUrl.contains("https:")) {
            Glide.with(context)
                    .load(pathUrl)
                    .asBitmap()
                    .error(R.mipmap.head) //失败图片
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .placeholder(R.mipmap.gray_picture) //占位符 也就是加载中的图片，可放个gif
//                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(imageView);
        } else {
            Glide.with(context)
                    .load(URLUtils.BASE_URL + pathUrl)
                    .asBitmap()
                    .error(R.mipmap.head) //失败图片
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    //.placeholder(R.mipmap.gray_picture) //占位符 也就是加载中的图片，可放个gif
                    //.bitmapTransform(new CropCircleTransformation(context))
                    .into(imageView);
        }
    }

    /**
     * 圆角图片
     *
     * @param context
     * @param imageView
     * @param pathUrl
     */
    public static void displayGlideRound(Context context, ImageView imageView, String pathUrl, int radius) {
        if (pathUrl.contains("https:")) {
            Glide.with(context)
                    .load(pathUrl)
                    .error(R.mipmap.no_photo) //失败图片
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .placeholder(R.mipmap.gray_picture) //占位符 也就是加载中的图片，可放个gif
                    .bitmapTransform(new RoundedCornersTransformation(context, radius, 5))
                    .into(imageView);
        } else {
            Glide.with(context)
                    .load(URLUtils.BASE_URL + pathUrl)
                    .error(R.mipmap.no_photo) //失败图片
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .placeholder(R.mipmap.gray_picture) //占位符 也就是加载中的图片，可放个gif
                    .bitmapTransform(new RoundedCornersTransformation(context, radius, 5))
                    .into(imageView);
        }
    }

    /**
     * 图片
     *
     * @param context
     * @param imageView
     * @param pathUrl
     */
    public static void displayGlide(Context context, ImageView imageView, String pathUrl) {
        if (pathUrl.contains("https:")) {
            Glide.with(context)
                    .load(pathUrl)
                    .placeholder(R.mipmap.gray_picture) //占位符 也就是加载中的图片，可放个gif
                    .error(R.mipmap.no_photo) //失败图片
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        } else {
            Glide.with(context)
                    .load(URLUtils.BASE_URL + pathUrl)
                    .placeholder(R.mipmap.gray_picture) //占位符 也就是加载中的图片，可放个gif
                    .error(R.mipmap.no_photo) //失败图片
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        }


    }

    /**
     * 加载模糊图片
     *
     * @param context   上下文
     * @param imageView 图片控件
     * @param pathUrl   url
     */
    public static void displayGlideVague(Context context, ImageView imageView, String pathUrl) {

        if (pathUrl.contains("https:")) {
            Glide.with(context)
                    .load(pathUrl)
                    .placeholder(R.mipmap.gray_picture) //占位符 也就是加载中的图片，可放个gif
                    .error(R.mipmap.no_photo) //失败图片
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .fitCenter()//缩放
                    .bitmapTransform(new BlurTransformation(context, 85))
                    .into(imageView);
        } else {
            Glide.with(context)
                    .load(URLUtils.BASE_URL + pathUrl)
                    .placeholder(R.mipmap.gray_picture) //占位符 也就是加载中的图片，可放个gif
                    .error(R.mipmap.no_photo) //失败图片
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .fitCenter()//缩放
                    .bitmapTransform(new BlurTransformation(context, 85))
                    .into(imageView);
        }

    }

    /**
     * 请求网络
     *
     * @param request  请求request
     * @param callBack 回调接口
     */
    private <T> void doRequest(final Request request, final HttpCallBack callBack) {
        mHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                /**
                 * 设置event 超时
                 */
                callBack.getEvent().setTimeOut(true);
                Log.e("LogInterceptor--> ", e.getMessage());
                /**
                 * 发送广播
                 */
                EventBus.getDefault().post(callBack.getEvent());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String resultStr = response.body().string();
                    try {
                        callBack.getEvent().setData(mGson.fromJson(resultStr, callBack.getEvent().getValueType()));
                    } catch (JsonSyntaxException e) {
                        /**
                         * 设置event 超时
                         */
                        callBack.getEvent().setTimeOut(true);
                    }
                } else {
                    /**
                     * 设置event 超时
                     */
                    callBack.getEvent().setTimeOut(true);
                }
                /**
                 * 发送event广播
                 */
                EventBus.getDefault().post(callBack.getEvent());
            }
        });
    }


    /**
     * 构建Request
     *
     * @param url        网址
     * @param methodType get请求  post请求
     * @param body       请求体
     * @return 返回构建成功的Request
     */
    private Request buildRequest(String url, HttpMethodType methodType, RequestBody body) {
        Request.Builder builder = new Request.Builder()
                .url(url);
        if (methodType == HttpMethodType.POST) {

            builder.post(body);
        } else if (methodType == HttpMethodType.GET) {
            builder.get();
        }
        return builder.build();
    }

    /**
     * 枚举类型
     */
    enum HttpMethodType {
        GET,
        POST,
    }

    /**
     * 检查当前网络是否可用
     *
     * @param activity
     * @return
     */
    public static boolean isNetworkAvailable(Context activity) {
        Context context = MyApplication.mApp;
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    System.out.println(i + "===状态===" + networkInfo[i].getState());
                    System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static RequestBody createCustomRequestBody(final MediaType contentType, final File file, final OkProgressListener listener) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return contentType;
            }

            @Override
            public long contentLength() {
                return file.length();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source;
                try {
                    source = Okio.source(file);
                    //sink.writeAll(source);
                    Buffer buf = new Buffer();
                    Long remaining = contentLength();
                    for (long readCount; (readCount = source.read(buf, 2048)) != -1; ) {
                        sink.write(buf, readCount);
                        listener.onProgress(contentLength(), remaining -= readCount, remaining == 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public interface OkProgressListener {
        void onProgress(long totalBytes, long remainingBytes, boolean done);
    }


}
