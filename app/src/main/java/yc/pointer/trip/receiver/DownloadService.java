package yc.pointer.trip.receiver;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;



import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import yc.pointer.trip.R;

/**
 * Created by Administrator on 2017/7/6.
 */

public class DownloadService extends Service {

    private String mDownloadUrl;//APK的下载路径
    private NotificationManager mNotificationManager;
    private Notification mNotification;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private int download_precent = 0;
    private int notificationId = 1234;
    private String appName = "";
    private  File apkFile =null;
    private Boolean flag = false;
    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            notifyMsg("温馨提醒", "文件下载失败", 0);
            stopSelf();
        }
        mDownloadUrl = intent.getStringExtra("URL");//获取下载APK的链接
        downloadApkFile(mDownloadUrl);//下载APK
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void notifyMsg(String title, String content, int progress) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);//为了向下兼容，这里采用了v7包下的NotificationCompat来构造
        builder.setSmallIcon(R.mipmap.ic_launcher).setLargeIcon(BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher)).setContentTitle(title);
        if (progress > 0 && progress < 100) {
            //下载进行中
            builder.setProgress(100, progress, false);
        } else {
            builder.setProgress(0, 0, false);
        }
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentText(content);
        if (progress >= 100&&flag) {
            //下载完成
            builder.setContentIntent(getInstallIntent());
        }
        mNotification = builder.build();
        mNotificationManager.notify(0, mNotification);


    }

    /**
     * 安装apk文件
     *
     * @return
     */
    private PendingIntent getInstallIntent() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                switch (msg.what) {
                    case 1:
                        //避免频繁刷新View，这里设置每下载10%提醒更新一次进度
                        flag = false;
                        notifyMsg("温馨提醒", "文件正在下载..", download_precent-1);
                        break;
                    case 2:
                        // 下载完成后清除所有下载信息，执行安装提示
                        download_precent = 0;
                        mNotificationManager.cancel(notificationId);
                        apkFile = (File)msg.obj;
                        flag = true;
                        notifyMsg("温馨提醒", "文件下载已完成", 100);
                        stopSelf();
                        System.out.println("HomeActivity 下载的文件路径:"+apkFile);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                        startActivity(intent);
                        break;
                    default:
                        break;

                }
            }
        }
    };

    /**
     * 下载apk文件
     *
     * @param url
     */
    private  void downloadApkFile(String url){
        Request request = new Request.Builder().url(url).build();
        okHttpClient .newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("h_bl", "onFailure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    InputStream is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(Environment.getExternalStorageDirectory(), "sxlife.apk");
                    FileOutputStream fos  = new FileOutputStream(file);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    byte[] buffer = new byte[1024];
                    int len;
                    int precent = 0;
                    long sum = 0;
                    while ((len = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                        sum += len;
                        precent = (int) (sum * 1.0f / total * 100);
                        Log.d("h_bl", "precent=" + precent);
                        if (precent - download_precent >= 1||precent==100) {
                            download_precent = precent;
                            Message message = handler.obtainMessage(1, precent);
                            handler.sendMessage(message);
                        }
                    }
                    fos.flush();
                    bis.close();
                    is.close();
                    Message message = handler.obtainMessage(2, file);
                    handler.sendMessage(message);
                    Log.d("h_bl", "文件下载成功");
                } catch (Exception e) {
                    Log.d("h_bl", "文件下载失败");
                }

            }
        });
    }
}