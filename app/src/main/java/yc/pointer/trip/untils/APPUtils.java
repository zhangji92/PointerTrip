package yc.pointer.trip.untils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import yc.pointer.trip.R;
import yc.pointer.trip.activity.LoginActivity;
import yc.pointer.trip.activity.MainActivity;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.bean.eventbean.ReceiverBean;
import yc.pointer.trip.bean.eventbean.UpdateRedDotBean;
import yc.pointer.trip.view.DialogSure;
import yc.pointer.trip.view.DialogSureLogin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 张继
 * 2017/7/13
 * 18:02
 */
public class APPUtils {

    private static PermissionHelper permissionHelper;
    private PermissionHelper.PermissionModel[] permissionModels = {
            new PermissionHelper.PermissionModel(1, Manifest.permission.READ_PHONE_STATE, "获取手机识别码"),
            new PermissionHelper.PermissionModel(2, Manifest.permission.ACCESS_FINE_LOCATION, "定位")
    };

    public static String getPhoneMsg(Context context) {
        //获取手机管理器对象
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //获取本机MIEI号码
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            permissionHelper.applyPermission();

        }
        return telephonyManager.getDeviceId();
    }

    /**
     * 获取手机唯一识别码
     *
     * @param context
     * @return
     */
    public static String getPhotoIMEI(Context context) {
        TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            permissionHelper.applyPermission();
        }
        String szImei = TelephonyMgr.getDeviceId();
        if (szImei == null) {
            szImei = "没有获取到IMEI";
        }
        return szImei;
    }

    /**
     * 判断设备识别码与时间戳
     *
     * @return
     */
    public static boolean judgeTimeDev(Context context, String devCode, long timeStamp) {
        if (!StringUtil.isEmpty(devCode)) {
            if (!StringUtil.isEmpty(String.valueOf(timeStamp))) {
                return true;
            } else {
                Toast.makeText(context, "时间戳为空", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(context, "设备识别码为空", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    /**
     * 单点登录判断
     *
     * @param context 上下文菜单
     * @param status  状态
     */
    public static void intentLogin(final Activity context, int status) {
        if (status == 201) {
            DialogSureLogin.getInstance(context, R.style.user_default_dialog, new DialogSureLogin.CallBackListener() {
                @Override
                public void onClickListener(DialogSureLogin dialogSure, boolean trueEnable) {
                    MyApplication.mApp.setUserBean(null);
                    MyApplication.mApp.setIslogin(false);
                    MyApplication.mApp.setUserId("");
                    SharedPreferencesUtils.getInstance().remove(context, "phone");
                    SharedPreferencesUtils.getInstance().remove(context, "pad");
                    SharedPreferencesUtils.getInstance().remove(context, "useId");
                    if (trueEnable) {
                        context.startActivity(new Intent(context, LoginActivity.class));
                    } else {
                        //EventBus代替广播action 为刷新字符receiver为1的广播
                        context.startActivity(new Intent(context, MainActivity.class));
                    }
                    DialogSureLogin.mDialogSureLogin=null;
                    EventBus.getDefault().post(new ReceiverBean("1"));
                    EventBus.getDefault().post(new UpdateRedDotBean(true));
                }
            })
                    .setTitle("登录异常")
                    .setMsg("您的账号在其他设备登录，如不是本人操作，请尽快前去修改密码")
                    .setPositiveButton("去登录")
                    .setNegativeButton("不用了")
                    .show();

        }
    }


    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     *
     * @param context
     * @return true 表示开启
     */
    public static final boolean isOPen(Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

}
