package yc.pointer.trip.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import com.amap.api.location.AMapLocation;

import butterknife.BindView;
import yc.pointer.trip.R;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.LocationUtil;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.PermissionHelper;
import yc.pointer.trip.untils.SharedPreferencesUtils;
import yc.pointer.trip.untils.StatusBarUtils;
import yc.pointer.trip.untils.StringUtil;

/**
 * Create by 张继
 * 2017/8/30
 * 10:20
 * 启动页
 */
public class SplashActivity extends BaseActivity {
    @BindView(R.id.splashlayout)
    LinearLayout splashlayout;
    private boolean isChangeIcon=false;
    private PermissionHelper permissionHelper;

    private PermissionHelper.PermissionModel[] permissionModels = {
            new PermissionHelper.PermissionModel(1, Manifest.permission.READ_PHONE_STATE, "获取手机识别码,否则无法开启程序"),
            new PermissionHelper.PermissionModel(2, Manifest.permission.ACCESS_FINE_LOCATION, "获取定位信息，否则无法开启程序"),
            new PermissionHelper.PermissionModel(3, Manifest.permission.WRITE_EXTERNAL_STORAGE, "获取您的读写权限,否则无法开启程序")
    };
    private String devcode;//设备识别码

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {
        StatusBarUtils.with(this).init();
        if (Build.VERSION.SDK_INT < 23) {
            //正常运行
            runApp();
        } else {
            //申请权限
            permissionHelper = new PermissionHelper(SplashActivity.this, new PermissionHelper.OnAlterApplyPermission() {
                @Override
                public void OnAlterApplyPermission() {
                    runApp();
                }
                @Override
                public void cancelListener() {
                    finish();
                }
            }, permissionModels);
            permissionHelper.applyPermission();
        }
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }


    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 定位
     */
    private void getLocation() {
        LocationUtil.getLocationUtilInstance().initLocation(this).setmICallLocation(new LocationUtil.ICallLocation() {
            @Override
            public void locationMsg(AMapLocation aMapLocation) {
                String locationCity = aMapLocation.getCity();
                String country = aMapLocation.getCountry();

                if (!StringUtil.isEmpty(locationCity)){
                    MyApplication.mApp.setLocationCity(locationCity);
                }
                if (!StringUtil.isEmpty(country)){
                    MyApplication.mApp.setLocationCountry(country);
                }

            }
        });
    }


    /**
     * APP正常运行
     */
    private void runApp() {

        //定位
        getLocation();
        String photoIMEI = APPUtils.getPhotoIMEI(getApplicationContext());
        devcode = Md5Utils.createMD5(photoIMEI);
        ((MyApplication) getApplication()).setDevcode(devcode);
//        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.5f);
//        anim.setDuration(1000);
//        anim.setFillAfter(true);
//        splashlayout.startAnimation(anim);
        //更换应用图标
        boolean isChangeIcon = SharedPreferencesUtils.getInstance().getBoolean(this, "isCash");
//        if (isChangeIcon){
//            changeIcon("yc.pointer.trip.SplashliasActivity");
//        }else {
//            changeIcon("yc.pointer.trip.activity.SplashActivity");
//        }

//        anim.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//        });
        boolean networkAvailable = OkHttpUtils.isNetworkAvailable(SplashActivity.this);
        if (networkAvailable) {
            startActivity(new Intent(SplashActivity.this, AdvertisingActivity.class));
//            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        } else {
            final boolean falg = SharedPreferencesUtils.getInstance().getBoolean(SplashActivity.this, "BootPageActivity");
            if (falg) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
//                startActivity(new Intent(SplashActivity.this, AdvertisingActivity.class));
                finish();
            } else {
                startActivity(new Intent(SplashActivity.this, BootPageActivity.class));
                finish();
            }
        }

//        boolean networkAvailable = OkHttpUtils.isNetworkAvailable(SplashActivity.this);
//        if (networkAvailable) {
//            startActivity(new Intent(SplashActivity.this, AdvertisingActivity.class));
//            finish();
//        } else {
//            final boolean falg = SharedPreferencesUtils.getInstance().getBoolean(SplashActivity.this, "BootPageActivity");
//            if (falg) {
//                startActivity(new Intent(SplashActivity.this, MainActivity.class));
//                finish();
//            } else {
//                startActivity(new Intent(SplashActivity.this, BootPageActivity.class));
//                finish();
//            }
//        }

    }

    public void changeIcon(String activityPath) {
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(getComponentName(),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(new ComponentName(this, activityPath),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);


        //重启桌面 加速显示
//        restartSystemLauncher(pm);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        permissionHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocationUtil.getLocationUtilInstance().stopLocation();
    }
}
