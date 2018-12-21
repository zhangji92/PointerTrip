package yc.pointer.trip.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import yc.pointer.trip.BuildConfig;
import yc.pointer.trip.R;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.ApkUpdateBean;
import yc.pointer.trip.bean.BillBean;
import yc.pointer.trip.bean.SaveMesgBean;
import yc.pointer.trip.bean.ScanQRCodeBean;
import yc.pointer.trip.bean.eventbean.OrderNotifyBean;
import yc.pointer.trip.bean.eventbean.PushMsgBen;
import yc.pointer.trip.bean.eventbean.UpdateRedDotBean;
import yc.pointer.trip.event.ApkUpdateEvent;
import yc.pointer.trip.event.BillEvent;
import yc.pointer.trip.event.HomeMoveLoginEvent;
import yc.pointer.trip.event.HomeThirdLoginEvent;
import yc.pointer.trip.event.LoginEventRefresh;
import yc.pointer.trip.event.MainFragmentRefreshEvent;
import yc.pointer.trip.fragment.HomeMoveFragment;
import yc.pointer.trip.fragment.HomeScreenFragment;
import yc.pointer.trip.fragment.HomeVideoFragment;
import yc.pointer.trip.fragment.MakeMoneyFragment;
import yc.pointer.trip.fragment.MineFragment;
import yc.pointer.trip.fragment.NewGoTravelFragment;
import yc.pointer.trip.fragment.NewMakeMoneyFragment;
import yc.pointer.trip.fragment.UnLoginGoTravel;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.ApkDownUtils;
import yc.pointer.trip.untils.AppApplicationMgr;
import yc.pointer.trip.untils.AppExit2Back;
import yc.pointer.trip.untils.DensityUtil;
import yc.pointer.trip.untils.LocationUtil;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.PermissionHelper;
import yc.pointer.trip.untils.ReceiverIntentUtils;
import yc.pointer.trip.untils.SharedPreferencesUtils;
import yc.pointer.trip.untils.StatusBarUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.untils.SystemUtil;
import yc.pointer.trip.view.DialogSure;
import yc.pointer.trip.view.LocationDialog;

public class MainActivity extends BaseActivity implements PermissionHelper.OnAlterApplyPermission {
    @BindView(R.id.frame_main)
    FrameLayout frameMain;//首页四个Fragment
    @BindView(R.id.road_book)
    RadioButton roadBook;//旅游圈
    @BindView(R.id.goto_travel)
    RadioButton gotoTravel;//出发吧
    @BindView(R.id.make_monkey)
    RadioButton makeMonkey;//赚一赚
    @BindView(R.id.mine)
    RadioButton mine;//我的

    @BindView(R.id.mine_relat)
    RelativeLayout mineRelative;
    @BindView(R.id.look_world)
    RadioButton lookWorld;//拍世界
    @BindView(R.id.rb_main)
    LinearLayout rbMain;
    @BindView(R.id.action_result)
    ImageView actionResult;


    // private HomeVideoFragment mHomeFragment;
    private HomeScreenFragment mHomeFragment;

    //        private GoTravelFragment mGoTraFragment;
    private NewGoTravelFragment mGoTraFragment;
    private NewMakeMoneyFragment mMkeMoneyFrarment;
    private MineFragment mMineFragment;
    private UnLoginGoTravel mUnLoginGoTravel;
    private Fragment mCurrentFragment;//当前的Fragment;
    private boolean islogin;//判断是否为登录状态
    private String logFlag = "";
    //    private String  mainFlag;//跳转至首页后判断显示Fragment的标志
    private boolean payFlag;//判断是否提交信息
    private long exitTime = 0;//退出系统的时间
    private String mReceiverType;//广播传递过来的标志
    private String mOid;
    private String mUserId;
    private long mTimestamp;//时间戳
    private String mDevcode;//手机识别码
    //private MyBroadcastReciver receiver;
    private final int UpdateFlag = 172800;//取消更新后两天不弹框提醒
    private PermissionHelper permissionHelper;

    private PermissionHelper.PermissionModel[] permissionModels = {
            new PermissionHelper.PermissionModel(1, Manifest.permission.CAMERA, "需要打开您的相机"),
            new PermissionHelper.PermissionModel(2, Manifest.permission.WRITE_EXTERNAL_STORAGE, "获取您的读写权限"),
            new PermissionHelper.PermissionModel(3, Manifest.permission.RECORD_AUDIO, "获取您的麦克风权限")
    };

    private boolean isPermission = false;
    private String durl;
    private boolean statusFlag;//状态栏颜色的标志
    private String uuid;
    private String oidQRCode;//二维码扫描出的 oid
    private LocationUtil locationUtil;
    private String mLocalCity;
    private String registrationID;
    private String mPhonetype;
    private boolean isNotLogin = true;//判断是否需要自动登录
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler();


    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        StatusBarUtils.with(this).init();
        String photoIMEI = APPUtils.getPhotoIMEI(this);
        mDevcode = Md5Utils.createMD5(photoIMEI);
        ((MyApplication) getApplication()).setDevcode(mDevcode);
        mUserId = ((MyApplication) getApplication()).getUserId();
        mDevcode = ((MyApplication) getApplication()).getDevcode();
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();
        logFlag = getIntent().getStringExtra("logFlag");
        registrationID = JPushInterface.getRegistrationID(this);
        String deviceBrand = SystemUtil.getDeviceBrand();//获取手机厂商
        String systemModel = SystemUtil.getSystemModel();//获取手机型号

        if (!StringUtil.isEmpty(deviceBrand) && !StringUtil.isEmpty(systemModel)) {
            mPhonetype = deviceBrand + " " + systemModel;
        }

        mLocalCity = MyApplication.mApp.getLocationCity();
        if (!StringUtil.isEmpty(mLocalCity)) {

            if (isNotLogin) {
                isNotLogin = false;
                automaticLogin();
            }

        } else {
            mLocalCity = "";
            if (isNotLogin) {
                isNotLogin = false;
                automaticLogin();
            }
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (StringUtil.isEmpty(mLocalCity)) {
                        getLocation();//获取位置信息
                    }
                }
            }, 3000);
        }
        // "内存重启"时(例如修改手机字体大小), 恢复之前的Fragment.
        // 注意此方法只有在父类的onCreate(Bundle)调用之后才有效.
        retrieveFragments();
        String mBid = getIntent().getStringExtra("bid");//预约所用bid
        //极光推送 广播页面传递过来的
        mOid = getIntent().getStringExtra("oid");
        //推送过来的用户ID
        uuid = getIntent().getStringExtra("uid");
        mReceiverType = getIntent().getStringExtra("type");
        getApkUpdateMsg();//获取版本信息

        permissionHelper = new PermissionHelper(MainActivity.this, this, permissionModels);

        if (!StringUtil.isEmpty(logFlag) && logFlag.equals("gotoTravel")) {//登陆或者路书详情预约页面跳转过来
            gotoTravel.setChecked(true);
            roadBook.setChecked(false);
            if (mGoTraFragment == null) {
                //mGoTraFragment = new GoTravelFragment();
                mGoTraFragment = new NewGoTravelFragment();
                if (!StringUtil.isEmpty(mBid)) {
                    // mGoTraFragment.setFlag(mBid);
                }
            }
            switchFragment(mGoTraFragment);
        } else if (!StringUtil.isEmpty(logFlag) && logFlag.equals("unGotoTravel")) {
            gotoTravel.setChecked(true);
            roadBook.setChecked(false);
            if (mUnLoginGoTravel == null) {
                mUnLoginGoTravel = new UnLoginGoTravel();
            }
            switchFragment(mUnLoginGoTravel);
        } else {
            if (mHomeFragment == null) {
                mHomeFragment = new HomeScreenFragment();
                //statusFlag = mHomeFragment.isStatusFlag();
            }
            switchFragment(mHomeFragment);

            //跳转订单状态页面（推送相关）
            if (!StringUtil.isEmpty(mReceiverType)) {
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    private Intent intent;

                    @Override
                    public void run() {
                        if (!StringUtil.isEmpty(mUserId) && !mUserId.equals("0")) {
                            if (mReceiverType.equals("11") || mReceiverType.equals("12")) {//身份证认证已通过
                                //Toast.makeText(MainActivity.this, "您的身份认证已通过,快去寻找适合的订单吧!", Toast.LENGTH_SHORT).show();
                                makeMonkey.setChecked(true);
                                roadBook.setChecked(false);
//                                mMkeMoneyFrarment = new MakeMoneyFragment();
                                mMkeMoneyFrarment = new NewMakeMoneyFragment();
                                switchFragment(mMkeMoneyFrarment);
                            } else {
                                ReceiverIntentUtils.getInstance().ReceiverType(MainActivity.this, mUserId, uuid, mOid, mReceiverType);
                            }
                        } else {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);//logFlag
                            intent.putExtra("logFlag", "about");
                            startActivityForResult(intent, 0);
                        }
                    }
                }, 1000);
            }
        }
        //根据用户信息状态，显示或隐藏小红点
        setRedVisiable();

    }

    /**
     * 判断红点是否显示
     */
    private void setRedVisiable() {
        SaveMesgBean.DataBean userBean = ((MyApplication) getApplication()).getUserBean();
        if (userBean != null) {
            int msg_num = userBean.getMsg_num();
            if (msg_num <= 0) {
                actionResult.setVisibility(View.GONE);
            } else {
                actionResult.setVisibility(View.VISIBLE);
            }
        } else {
            actionResult.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected boolean needEventBus() {
        return true;
    }

    @OnClick({R.id.road_book, R.id.goto_travel, R.id.make_monkey, R.id.mine_relat, R.id.mine, R.id.look_world})
    public void onViewClicked(View view) {
        islogin = ((MyApplication) getApplication()).isIslogin();
        payFlag = ((MyApplication) getApplication()).isPayFlag();
        if (mHandler != null) {
            //如果token为null，则将删除所有回调和消息。
            mHandler.removeCallbacksAndMessages(null);
        }
        switch (view.getId()) {
            case R.id.road_book:
                roadBook.setChecked(true);
                //                lookWorld.setChecked(false);
                mine.setChecked(false);
                gotoTravel.setChecked(false);
                makeMonkey.setChecked(false);
                if (mCurrentFragment == mHomeFragment) {
                    if (System.currentTimeMillis() - exitTime > 2000) {
//                        Toast.makeText(this, "双击刷新", Toast.LENGTH_SHORT).show();
                        exitTime = System.currentTimeMillis();
                    } else {
                        EventBus.getDefault().post(new MainFragmentRefreshEvent(true));
                    }

                } else {
                    if (mHomeFragment == null) {
                        //                        mHomeFragment = new HomeVideoFragment();
                        mHomeFragment = new HomeScreenFragment();
                    }
                    switchFragment(mHomeFragment);
                }

                break;
            case R.id.goto_travel:
                gotoTravel.setChecked(true);
                //                lookWorld.setChecked(false);
                roadBook.setChecked(false);
                makeMonkey.setChecked(false);
                mine.setChecked(false);
                if (mUnLoginGoTravel == null) {
                    mUnLoginGoTravel = new UnLoginGoTravel();
                }
                if (islogin == true) {
                    if (payFlag) {
                        payFlag = false;
                        //mGoTraFragment = new GoTravelFragment();
                        mGoTraFragment = new NewGoTravelFragment();
                    } else {
                        if (mGoTraFragment == null) {
                            //mGoTraFragment = new GoTravelFragment();
                            mGoTraFragment = new NewGoTravelFragment();
                        }
                    }
                    switchFragment(mGoTraFragment);
                } else {
                    //                    //跳转登录
                    //                    Intent intent = new Intent(this, LoginActivity.class);
                    //                    intent.putExtra("logFlag", "gotoTravel");
                    //                    startActivityForResult(intent, 0);
                    switchFragment(mUnLoginGoTravel);
                }
                break;
            case R.id.make_monkey:
                makeMonkey.setChecked(true);
                roadBook.setChecked(false);
                gotoTravel.setChecked(false);
                //                lookWorld.setChecked(false);
                mine.setChecked(false);
//                mMkeMoneyFrarment = new MakeMoneyFragment();
                mMkeMoneyFrarment = new NewMakeMoneyFragment();
                switchFragment(mMkeMoneyFrarment);

                break;
            case R.id.look_world:
                GSYVideoManager.releaseAllVideos();
                lookWorld.setChecked(false);
                //                roadBook.setChecked(false);
                //                gotoTravel.setChecked(false);
                //                makeMonkey.setChecked(false);
                //                mine.setChecked(false);
                if (Build.VERSION.SDK_INT < 23) {
                    IntentRecord();
                } else {
                    //申请权限
                    isPermission = true;
                    permissionHelper.setOnAlterApplyPermission(this);
                    permissionHelper.applyPermission();//申请权限
                }
                break;

            case R.id.mine_relat://个人设置
                mine.setChecked(true);
                roadBook.setChecked(false);
                gotoTravel.setChecked(false);
                makeMonkey.setChecked(false);
                lookWorld.setChecked(false);
                mMineFragment = new MineFragment();
                switchFragment(mMineFragment);
                break;
            case R.id.mine://个人设置
                mine.setChecked(true);
                roadBook.setChecked(false);
                gotoTravel.setChecked(false);
                makeMonkey.setChecked(false);
                lookWorld.setChecked(false);
                mMineFragment = new MineFragment();
                switchFragment(mMineFragment);
                break;

        }
    }

    /**
     * 跳转视频录制
     */
    private void IntentRecord() {
        isPermission = false;
        if (islogin == true) {
            Intent intent = new Intent(MainActivity.this, RecordVideoActivity.class);
            intent.putExtra("logFlag", "lookWorld");
            startActivity(intent);
        } else {
            //跳转登录
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("logFlag", "lookWorld");
            startActivity(intent);
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (isPermission) {
            permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
        } else if (requestCode == 1111) {//HomeFragment存储的权限
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];
                if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        ApkDownUtils.getInstance().downLoadApk(MainActivity.this, durl);
                        //showDownLoadAppDialog();
                    } else {
                        Toast.makeText(this, "权限未通过无法更新", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else if (requestCode == 2222) {
            //请求安装未知权限的APK
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //下载Apk并安装
                if (!StringUtil.isEmpty(durl)) {
                    ApkDownUtils.getInstance().downLoadApk(MainActivity.this, durl);
                }
            } else {
                //跳转设置页面
                new DialogSure(this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClickListener(DialogSure dialogSure, boolean trueEnable) {
                        if (trueEnable) {
                            //兼容8.0
                            startInstallPermissionSettingActivity();
                        } else {
                            Toast.makeText(MainActivity.this, "无法进行下载安装，请开启权限后重试", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setTitle("温馨提示")
                        .setMsg("安装应用需要打开未知来源权限，请去设置中开启权限")
                        .setNegativeButton("取消")
                        .setPositiveButton("设置")
                        .show();
            }
        }
    }

    /**
     * 切换首页四个Fragment，采用Hide和show,不采用replace
     *
     * @param fragment
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void switchFragment(Fragment fragment) {
        //if (mHomeFragment != null) {
        //    statusFlag = mHomeFragment.isStatusFlag();
        //}
        if (fragment instanceof HomeMoveFragment && !statusFlag) {
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                DensityUtil.setStatusBarColor(MainActivity.this, R.color.colorTitle);
            }
        } else {

            if (android.os.Build.VERSION.SDK_INT >= 21) {
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                getWindow().getDecorView().setSystemUiVisibility(option);
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        }

        if (mCurrentFragment == fragment) {//当前fragment==传入fragment
            return;
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mCurrentFragment != null) {//如果当前存在fragment对象，先隐藏该对象
            transaction.hide(mCurrentFragment);
        }
        if (fragment.isAdded()) {//如果传入的fragment已经添加过，就显示它
            transaction.show(fragment);
        } else {
            transaction.add(frameMain.getId(), fragment, fragment.getClass().getName());
        }
        transaction.commitAllowingStateLoss();
        mCurrentFragment = fragment;
    }

    // 找回FragmentManager中存储的Fragment
    private void retrieveFragments() {
        FragmentManager manager = getSupportFragmentManager();
        //mHomeFragment = (HomeVideoFragment) manager.findFragmentByTag(HomeVideoFragment.class.getName());
        mHomeFragment = (HomeScreenFragment) manager.findFragmentByTag(HomeScreenFragment.class.getName());
        //mGoTraFragment = (GoTravelFragment) manager.findFragmentByTag(GoTravelFragment.class.getName());
        mGoTraFragment = (NewGoTravelFragment) manager.findFragmentByTag(NewGoTravelFragment.class.getName());
//        mMkeMoneyFrarment = (MakeMoneyFragment) manager.findFragmentByTag(MakeMoneyFragment.class.getName());
        mMkeMoneyFrarment = (NewMakeMoneyFragment) manager.findFragmentByTag(NewMakeMoneyFragment.class.getName());
        mMineFragment = (MineFragment) manager.findFragmentByTag(MineFragment.class.getName());
        mUnLoginGoTravel = (UnLoginGoTravel) manager.findFragmentByTag(UnLoginGoTravel.class.getName());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isPermission) {
            permissionHelper.onActivityResult(requestCode, resultCode, data);
        }
        switch (requestCode) {
            case 0:
                if (resultCode == 3) {
                    if (mGoTraFragment == null) {
                        //mGoTraFragment = new GoTravelFragment();
                        mGoTraFragment = new NewGoTravelFragment();
                    }
                    switchFragment(mGoTraFragment);
                } else if (resultCode == 4) {
                    if (mMkeMoneyFrarment == null) {
//                        mMkeMoneyFrarment = new MakeMoneyFragment();
                        mMkeMoneyFrarment = new NewMakeMoneyFragment();
                    }
                    switchFragment(mMkeMoneyFrarment);
                } else if (resultCode == 10) {
                    //推送后未登录，登陆后返回弹框跳转
                    mUserId = ((MyApplication) getApplication()).getUserId();
                    pushMsg();
                }
                break;
            case 10086://android8.0 未知来源应用安装权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    boolean hasInstallPermission = MainActivity.this.getPackageManager().canRequestPackageInstalls();
                    //再次执行安装流程，包含权限判等
                    //if (!hasInstallPermission && !TextUtils.isEmpty(durl)) {
                    if (!TextUtils.isEmpty(durl)) {
                        //再次启动安装流程
                        ApkDownUtils.getInstance().downLoadApk(MainActivity.this, durl);
                    }
                }
                break;
            case 147://GPS回调
                if (APPUtils.isOPen(this)) {
                    getLocation();
                } else {
                    if (isNotLogin) {
                        isNotLogin = false;
                        mLocalCity = "";
                        automaticLogin();
                    }
                }
                break;
        }

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            String contents = result.getContents();
            if (contents == null) {
//                Toast.makeText(MainActivity.this, "取消扫描", Toast.LENGTH_LONG).show();
            } else {

                boolean isJson = StringUtil.isJson(contents);
                if (isJson) {
                    //扫描成功，开始订单
                    mUserId = SharedPreferencesUtils.getInstance().getString(this, "useId");
                    Gson gson = new Gson();
                    ScanQRCodeBean scanQRCodeBean = gson.fromJson(contents, ScanQRCodeBean.class);
                    oidQRCode = scanQRCodeBean.getOid();
                    String uid = scanQRCodeBean.getUid();
                    String order_status = scanQRCodeBean.getOrder_status();
                    if (uid.equals(mUserId)) {
                        if (order_status.equals("1")) {
                            getBillStartOrder(oidQRCode);
                        } else {
                            Toast.makeText(this, "二维码已失效", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "接单方身份匹配失败，不能开始订单", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "格式错误", Toast.LENGTH_SHORT).show();
                }
                Log.e("content", result.getContents());
//                Toast.makeText(MainActivity.this, "扫描内容:" + contents, Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * 扫码开始订单
     */
    private void getBillStartOrder(String oid) {
        boolean flag = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
        if (flag) {
            String sign = Md5Utils.createMD5("devcode=" + mDevcode + "oid=" + oid + "timestamp=" + mTimestamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody build = new FormBody.Builder()
                    .add("devcode", mDevcode)
                    .add("oid", oid)
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", mUserId)
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.NEW_BILL_START_ORDER, build, new HttpCallBack(new BillEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getBillStartOrder(BillEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        BillBean bean = event.getData();
        if (bean.getStatus() == 0) {

            Intent intent = new Intent(this, GrabOrderActivity.class);
            intent.putExtra("oid", oidQRCode);
            startActivity(intent);
            EventBus.getDefault().post(new OrderNotifyBean("grabOrder"));
        } else {
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }


    /**
     * 推送跳转相关逻辑
     */
    private void pushMsg() {
        ReceiverIntentUtils.getInstance().pushMsg(MainActivity.this, mReceiverType, mUserId, uuid);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mCurrentFragment != mHomeFragment) {
                roadBook.setChecked(true);
                mine.setChecked(false);
                gotoTravel.setChecked(false);
                makeMonkey.setChecked(false);
                if (mHomeFragment == null) {
                    mHomeFragment = new HomeScreenFragment();
                }
                switchFragment(mHomeFragment);
            } else {


                if (System.currentTimeMillis() - exitTime > 2000) {
                    Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                } else {
                    AppExit2Back.exitApp(this);


                }
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void OnAlterApplyPermission() {
        IntentRecord();
    }

    @Override
    public void cancelListener() {

    }

    ////广播接收内部类
    //private class MyBroadcastReciver extends BroadcastReceiver {
    //    @Override
    //    public void onReceive(Context context, Intent intent) {
    //        String action = intent.getAction();
    //        String stringExtra = intent.getStringExtra("action");
    //        if (action.equals("更新活动标识")) {
    //            if (!StringUtil.isEmpty(stringExtra) && stringExtra.equals("visible")) {
    //                actionResult.setVisibility(View.VISIBLE);
    //            } else {
    //                actionResult.setVisibility(View.GONE);
    //            }
    //        } else if (action.equals("更新推送页面信息")) {
    //            if (!StringUtil.isEmpty(stringExtra) && stringExtra.equals("pushMsg")) {
    //                mUserId = ((MyApplication) getApplication()).getUserId();
    //                pushMsg();
    //            }
    //        }
    //    }
    //}

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            //如果token为null，则将删除所有回调和消息。
            mHandler.removeCallbacksAndMessages(null);
        }
        //if (receiver != null) {
        //    //在结束时可取消广播
        //    unregisterReceiver(receiver);
        //}
    }

    /**
     * 获取版本更新的信息
     */
    private void getApkUpdateMsg() {
        if (StringUtil.isEmpty(mUserId)) {

            mUserId = "0";

        }
        String versionCode = AppApplicationMgr.getVersionName(this);
        boolean timeFlag = APPUtils.judgeTimeDev(MainActivity.this, mDevcode, mTimestamp);
        if (timeFlag) {
            String str = Md5Utils.createMD5("devcode=" + mDevcode + "timestamp=" + mTimestamp + "type=" + 1 + "uid=" + mUserId + "version=" + versionCode + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", String.valueOf(mDevcode))
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("type", String.valueOf(1))
                    .add("version", versionCode)
                    .add("signature", str)
                    .add("uid", mUserId)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.APK_UPDATE, requestBody, new HttpCallBack(new ApkUpdateEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getApkUpdateMsg(ApkUpdateEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        final ApkUpdateBean bean = event.getData();
        if (bean.getStatus() == 0) {
            durl = bean.getData().getDurl();
            if (bean.getData().getMastupdate() == 1) {//强制更新
                //                Intent intent = new Intent();
                //                intent.setAction("强制更新");
                //                intent.putExtra("mastUpdate", "1");
                //                getActivity().sendBroadcast(intent);
                ApkDownUtils.getInstance().upDate(MainActivity.this, bean, true);
            } else {
                // 延时弹框，点击取消后两天内不显示弹框
                long nowTimeStamp = System.currentTimeMillis();
                String cancelUpdateTime = SharedPreferencesUtils.getInstance().getString(MainActivity.this, "CancelUpdateTime");
                String s = String.valueOf(nowTimeStamp);
                String substring = s.substring(0, 10);
                if (!cancelUpdateTime.equals("not find")) {
                    String substring1 = cancelUpdateTime.substring(0, 10);
                    Integer nowTime = Integer.valueOf(substring);
                    Integer cancelTime = Integer.valueOf(substring1);
                    if (nowTime - cancelTime >= UpdateFlag) {
                        ApkDownUtils.getInstance().upDate(MainActivity.this, bean, false);
                    }
                } else {
                    ApkDownUtils.getInstance().upDate(MainActivity.this, bean, false);
                }
            }
        } else {
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }

    /**
     * 自动登录方法
     */
    private void automaticLogin() {
        String phone = SharedPreferencesUtils.getInstance().getString(this, "loginPhone");
        String pad = SharedPreferencesUtils.getInstance().getString(this, "pad");
        String loginType = SharedPreferencesUtils.getInstance().getString(this, "loginType");
        String loginID = SharedPreferencesUtils.getInstance().getString(this, "loginId");
        if (!StringUtil.isEmpty(loginType) && !loginType.equals("not find")) {//登录类型为第三方登录
            if (!loginID.equals("not find")) {
                thirdLogin(loginID, loginType);
            }
        } else {
            if (!phone.equals("not find")) {
                if (!pad.equals("not find")) {
                    String padMd5 = Md5Utils.createMD5(pad + URLUtils.WK_PWD_KEY);
                    Login(phone, padMd5, mDevcode, mTimestamp);
                }
            }
        }
    }

    /**
     * 第三方登录
     */
    private void thirdLogin(String loginId, String loginType) {
        boolean timeFlag = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
        if (timeFlag) {
            String sign = Md5Utils.createMD5("devcode=" + mDevcode + "location=" + mLocalCity + "login_id=" + loginId + "login_type=" + loginType + "phone_type=" + mPhonetype + "registration_id=" + registrationID + "timestamp=" + mTimestamp + URLUtils.WK_APP_KEY);
            RequestBody params = new FormBody.Builder()
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("login_id", loginId)
                    .add("location", mLocalCity)
                    .add("login_type", loginType)
                    .add("phone_type", mPhonetype)
                    .add("registration_id", registrationID)
                    .add("signature", sign)
                    .add("devcode", mDevcode)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.THIRD_LOGIN, params, new HttpCallBack(new HomeThirdLoginEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void thirdLoginMsg(HomeThirdLoginEvent thirdLoginEvent) {
        if (thirdLoginEvent.isTimeOut()) {
            Toast.makeText(this, "网络状态异常", Toast.LENGTH_SHORT).show();
            return;
        }
        SaveMesgBean data = thirdLoginEvent.getData();
        if (data.getStatus() == 0) {
            String uid = data.getData().getUid();
            MyApplication.mApp.setUserId(uid);
            savePersonMsg(data);
        } else {
            Toast.makeText(this, data.getMsg(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 登陆
     *
     * @param phone 用户名
     * @param pad   密码
     */
    private void Login(String phone, String pad, String devcode, long timestamp) {
        boolean timeFlag = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
        if (timeFlag) {
            String sign = Md5Utils.createMD5("devcode=" + devcode + "location=" + mLocalCity + "phone=" + phone + "phone_type=" + mPhonetype + "pwd=" + pad + "registration_id=" + registrationID + "timestamp=" + timestamp + URLUtils.WK_APP_KEY);
            RequestBody params = new FormBody.Builder()
                    .add("timestamp", String.valueOf(timestamp))
                    .add("phone", phone)
                    .add("pwd", pad)
                    .add("location", mLocalCity)
                    .add("phone_type", mPhonetype)
                    .add("registration_id", registrationID)
                    .add("signature", sign)
                    .add("devcode", devcode)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.LOGIN, params, new HttpCallBack(new HomeMoveLoginEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginMsg(HomeMoveLoginEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        SaveMesgBean loginBean = event.getData();
        if (loginBean.getStatus() == 0) {
            String uid = loginBean.getData().getUid();
            MyApplication.mApp.setUserId(uid);
            savePersonMsg(loginBean);
        } else {
            Toast.makeText(this, loginBean.getMsg(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 保存用户信息
     *
     * @param loginBean
     */
    private void savePersonMsg(SaveMesgBean loginBean) {
        EventBus.getDefault().post(new LoginEventRefresh(true));
        String uid = loginBean.getData().getUid();
        String loginPhone = loginBean.getData().getPhone();
        String is_order = loginBean.getData().getIs_order();
        String login_type = loginBean.getData().getLogin_type();
        String is_bd = loginBean.getData().getIs_bd();
        String bank_num = loginBean.getData().getBank_num();
        String alipay_num = loginBean.getData().getAlipay();

        int msg_num = loginBean.getData().getMsg_num();
        SharedPreferencesUtils.getInstance().putString(this, "loginPhone", loginPhone);
        SharedPreferencesUtils.getInstance().putString(this, "useId", uid);
        SharedPreferencesUtils.getInstance().putString(this, "loginType", login_type);
        SharedPreferencesUtils.getInstance().putString(this, "isOrder", is_order);
        String pic = loginBean.getData().getPic();
        if (!StringUtil.isEmpty(pic)) {
            SharedPreferencesUtils.getInstance().putString(this, "headImg", pic);
        }
        if (!StringUtil.isEmpty(is_bd) && is_bd.equals("1")) {
            MyApplication.mApp.setUserIsBinding(true);
            SharedPreferencesUtils.getInstance().putBoolean(this, "isBinding", true);
        } else if (is_bd.equals("0")) {
            MyApplication.mApp.setUserIsBinding(false);
            SharedPreferencesUtils.getInstance().putBoolean(this, "isBinding", false);
        }

        if (!StringUtil.isEmpty(bank_num)) {
            MyApplication.mApp.setUserIsVerify(true);
        } else {
            MyApplication.mApp.setUserIsVerify(false);
        }
        if (!StringUtil.isEmpty(is_order) && is_order.equals("1")) {
//            ((MyApplication) getApplication()).setUserIsBinding(true);
            SharedPreferencesUtils.getInstance().putBoolean(this, "isPayDeposit", true);
        } else if (is_order.equals("0")) {
//            ((MyApplication) getApplication()).setUserIsBinding(false);
            SharedPreferencesUtils.getInstance().putBoolean(this, "isPayDeposit", false);
        }
        if (!StringUtil.isEmpty(alipay_num)) {
            MyApplication.mApp.setAlipayNumber(alipay_num);
        } else {
            MyApplication.mApp.setAlipayNumber("");
        }

        MyApplication.mApp.setIslogin(true);
        MyApplication.mApp.setUserBean(loginBean.getData());
        //更新活动标识
        //Intent intent1 = new Intent();
        //intent1.setAction("更新活动标识");
        if (msg_num <= 0) {
            actionResult.setVisibility(View.GONE);
        } else {
            actionResult.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 定位
     */
    private void getLocation() {
        if (APPUtils.isOPen(this)) {
            locationUtil = LocationUtil.getLocationUtilInstance().initLocation(this);
            locationUtil.setmICallLocation(new LocationUtil.ICallLocation() {
                @Override
                public void locationMsg(AMapLocation aMapLocation) {
                    String locationCity = aMapLocation.getCity();
                    String country = aMapLocation.getCountry();
                    if (!StringUtil.isEmpty(locationCity)) {
                        MyApplication.mApp.setLocationCity(locationCity);
                        mLocalCity = locationCity;

                    }
                    if (!StringUtil.isEmpty(country)) {
                        MyApplication.mApp.setLocationCountry(country);
                    }
//                    if (isNotLogin) {
//                        isNotLogin = false;
//                    }
                    automaticLogin();
                    //在此处停止定位会影响自动登录的接口回调
                    locationUtil.stopLocation();//停止定位服务
                }
            });
        } else {
            LocationDialog locationDialog = new LocationDialog(this, R.style.user_default_dialog, this, 147);
            locationDialog.setMsg("检测到GPS未开启,请开启GPS获取当前城市信息");
            locationDialog.setLocationCallBack(new LocationDialog.LocationCallBack() {
                @Override
                public void closeDialog() {
                    mLocalCity = "";
                    if (isNotLogin) {
                        isNotLogin = false;
                        automaticLogin();
                    }
                }
            });
            locationDialog.show();
        }
    }

    /**
     * 适配8.0安装权限
     * 跳转到设置-允许安装未知来源-页面
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity() {
        Uri packageURI = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
        // 注意这个是8.0新API
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
        startActivityForResult(intent, 10086);

    }

    /**
     * MineFragment + BindPhone 界面传递过来是否显示小红点
     *
     * @param bean true 显示小红点 false 不显示小红点
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateRedDot(UpdateRedDotBean bean) {
        if (bean != null && bean.isDot()) {
            actionResult.setVisibility(View.VISIBLE);
        } else {
            actionResult.setVisibility(View.GONE);
        }
    }


    /**
     * BindPhone 更新推送页面信息
     *
     * @param pushMsgBen
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updatePush(PushMsgBen pushMsgBen) {
        if (pushMsgBen.isPushFlag()) {
            mUserId = MyApplication.mApp.getUserId();
            pushMsg();
        }
    }


}
