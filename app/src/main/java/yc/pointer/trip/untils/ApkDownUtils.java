package yc.pointer.trip.untils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.widget.Toast;

import yc.pointer.trip.R;
import yc.pointer.trip.bean.ApkUpdateBean;
import yc.pointer.trip.receiver.DownloadService;
import yc.pointer.trip.view.DialogApk;
import yc.pointer.trip.view.DialogMustUpteApk;


/**
 * Created by 张继
 * 2018/7/17
 * 17:03
 * 公司：
 * 描述：Apk下载更新
 */

public class ApkDownUtils {
    private static ApkDownUtils mInstance=null;
    public static ApkDownUtils getInstance(){
        if (mInstance==null){
            synchronized (ApkDownUtils.class){
                if (mInstance==null){
                    mInstance=new ApkDownUtils();
                }
            }
        }
        return mInstance;
    }

    private ApkDownUtils() {
    }

    /**
     * 更新版本
     */
    public void upDate(final Activity activity, final ApkUpdateBean bean, boolean isMust) {
        if (isMust) {
            new DialogMustUpteApk(activity, R.style.user_default_dialog, new DialogApk.CallBackListener() {
                @Override
                public void onClickButton(boolean enableStatus) {
                    if (enableStatus) {
                        if (Build.VERSION.SDK_INT < 23) {
                            //正常运行
                            downLoadApk(activity,bean.getData().getDurl());
                        } else {
                            //申请权限
                            if (!isGrantExternalRW(activity)) {
                                return;
                            }
                            showDownLoadAppDialog(activity,bean.getData().getDurl());
                        }
                    } else {
                        activity.finish();
                    }
                }
            }).setMsg(bean.getData().getData()).setPositiveButton("立即更新").show();
        } else {
            new DialogApk(activity, R.style.user_default_dialog, new DialogApk.CallBackListener() {
                @Override
                public void onClickButton(boolean enableStatus) {
                    if (enableStatus) {
                        if (Build.VERSION.SDK_INT < 23) {
                            //正常运行
                            downLoadApk(activity,bean.getData().getDurl());
                        } else {
                            //申请权限
                            if (!isGrantExternalRW(activity)) {
                                return;
                            }
                            downLoadApk(activity,bean.getData().getDurl());
                        }
                    } else {
                        long cancelUpteTimeMillis = System.currentTimeMillis();
                        SharedPreferencesUtils.getInstance().putString(activity, "CancelUpdateTime", String.valueOf(cancelUpteTimeMillis));
                    }
                }
            }).setMsg(bean.getData().getData()).setPositiveButton("立即更新").setNegativeButton("稍后再说").show();
        }
    }
    /**
     * 下载APK
     */
    public void downLoadApk(Activity activity,String url) {
        Intent serviceIntent = new Intent(activity, DownloadService.class);
        //将下载地址url放入intent中
        //        "https://www.zhizhentrip.com/apk/app-debug.apk"
        serviceIntent.putExtra("URL", url);
        activity.startService(serviceIntent);
    }



    /**
     * 下载申请权限（读写存储权限）
     *
     * @param activity
     * @return
     */
    public boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1111);

            return false;
        }
        return true;
    }
    /**
     * 申请未知来源应用安装的权限
     */
    private void showDownLoadAppDialog(Activity activity,String durl) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean hasInstallPermission = activity.getPackageManager().canRequestPackageInstalls();
            if (!hasInstallPermission) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, 2222);
            } else {
                if (!TextUtils.isEmpty(durl)) {
                    //再次启动安装流程
                    downLoadApk(activity,durl);
                }
            }
        } else {
            downLoadApk(activity,durl);
            Toast.makeText(activity, "APk下载中", Toast.LENGTH_SHORT).show();
        }
    }


}
