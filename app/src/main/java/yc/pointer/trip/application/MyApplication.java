package yc.pointer.trip.application;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import com.danikula.videocache.HttpProxyCacheServer;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.orm.SchemaGenerator;
import com.orm.SugarApp;
import com.orm.SugarContext;
import com.orm.SugarDb;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import cn.jpush.android.api.JPushInterface;
import yc.pointer.trip.bean.SaveMesgBean;
import yc.pointer.trip.untils.AppExit2Back;
import yc.pointer.trip.untils.AppFileMgr;


/**
 * Created by Lenovo
 * 2017/6/28
 * 13:57
 */
public class MyApplication extends Application {
    private long timestamp;//时间戳
    private long cancelUpteTimeMillis;//取消更新的时间戳


    private String devcode;//设备识别码
    public static MyApplication mApp;
    private String userId;//用户id


    private String wxPayType = "";//0:发单支付  1：活动支付  2：押金支付
    private boolean userIsBinding = false;//用户是否绑定
    private boolean userIsVerify = false;//用户是否认证
    private boolean islogin = false;//是否登录的标志，默认为false
    private String oid;//付款页面订单ID
    private String alipayNumber;//支付宝账号
    private boolean payFlag = false;//支付订单标志
    private SaveMesgBean.DataBean userBean;//个人信息实例
    //    private LocationUtil locationUtil;//初始化定位
    public String locationCity;//定位城市

    public String locationCountry;//定位国家





    public String getLocationCountry() {
        return locationCountry;
    }

    public void setLocationCountry(String locationCountry) {
        this.locationCountry = locationCountry;
    }


    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    //视频缓存
    private HttpProxyCacheServer proxy;

    //单例模式
    public static HttpProxyCacheServer getProxy(Context context) {
        mApp = (MyApplication) context.getApplicationContext();
        return mApp.proxy == null ? (mApp.proxy = mApp.newProxy()) : mApp.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .cacheDirectory(AppFileMgr.getVideoCacheDir(this))
                .build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //第三方数据库
        SugarContext.init(this);
        SchemaGenerator schemaGenerator = new SchemaGenerator(this);
        schemaGenerator.createDatabase(new SugarDb(this).getDB());
        Log.d("App","----------onCreate---------------");
        Thread.setDefaultUncaughtExceptionHandler(restartHandler); // 程序崩溃时触发线程  以下用来捕获程序崩溃异常
        //友盟
        com.umeng.socialize.Config.DEBUG = true;
        UMShareAPI.get(this);
        mApp = this;
        //默认使用的高度是设备的可用高度，也就是不包括状态栏和底部的操作栏的，如果你希望拿设备的物理高度进行百分比化
        timestamp = System.currentTimeMillis();
        //String photoIMEI = APPUtils.getPhotoIMEI(getApplicationContext());
        //devcode = Md5Utils.createMD5(photoIMEI);
        initImageLoad(this);//初始化ImageLoader
//        Awen.init(this);//图片选择器初始化
        //极光
        JPushInterface.setDebugMode(false);
        JPushInterface.init(this);

        //腾讯Bugly
        Context context = getApplicationContext();
        // 获取当前包名
        String packageName = context.getPackageName();
        // 获取当前进程名
        String processName = getProcessName(android.os.Process.myPid());
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        // 初始化Bugly
        CrashReport.initCrashReport(context, "ea0e85bb5f", true, strategy);

//        LocationUtil.getLocationUtilInstance().initLocation(this).setmICallLocation(new LocationUtil.ICallLocation() {
//            @Override
//            public void locationMsg(AMapLocation aMapLocation) {
//                if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
//                    String city = aMapLocation.getCity();
//                    String country = aMapLocation.getCountry();
//                    locationCountry=country;
//                    locationCity = city;
//
//                }
//            }
//        });
//        setLocationCity(locationCity);
        /**
         * 删除慎重，7.0系统以后往文件里面写东西的权限
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }


        //极光相关
//        String v = JPushInterface.getRegistrationID(this);
    }

    {
//        PlatformConfig.setWeixin("wxe44b2ed1e16f131c", "5bb696d9ccd75a38c8a0bfe0675559b3");
        PlatformConfig.setWeixin("wxe44b2ed1e16f131c", "10a90b557d2a65425029ff9d5ad8ae50");
        PlatformConfig.setQQZone("1105894263", "XjrGDm3neLTwE7fY");
        PlatformConfig.setSinaWeibo("1594177138", "105868616b9198b585c77b9934017b25", "http://sns.whalecloud.com/sina2/callback");
//        PlatformConfig.setSinaWeibo("1594177138", "105868616b9198b585c77b9934017b25", "");
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public boolean isPayFlag() {
        return payFlag;
    }

    public void setPayFlag(boolean payFlag) {
        this.payFlag = payFlag;
    }


    // 创建服务用于捕获崩溃异常
    private Thread.UncaughtExceptionHandler restartHandler = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread thread, Throwable ex) {
            Log.e("LogInterceptor-->",ex.getMessage());
            AppExit2Back.exitApp(mApp);
        }
    };

    public SaveMesgBean.DataBean getUserBean() {
        return userBean;
    }

    public boolean isIslogin() {
        return islogin;
    }

    public void setIslogin(boolean islogin) {
        this.islogin = islogin;
    }

    public String getLocationCity() {
        return locationCity;
    }

    public void setLocationCity(String locationCity) {
        this.locationCity = locationCity;
    }

    public void setUserBean(SaveMesgBean.DataBean userBean) {
        this.userBean = userBean;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getDevcode() {
        return devcode;
    }

    public void setDevcode(String devcode) {
        this.devcode = devcode;
    }

    public long getCancelUpteTimeMillis() {
        return cancelUpteTimeMillis;
    }

    public void setCancelUpteTimeMillis(long cancelUpteTimeMillis) {
        this.cancelUpteTimeMillis = cancelUpteTimeMillis;
    }

    public boolean isUserIsBinding() {
        return userIsBinding;
    }

    public void setUserIsBinding(boolean userIsBinding) {
        this.userIsBinding = userIsBinding;
    }

    public boolean isUserIsVerify() {
        return userIsVerify;
    }

    public void setUserIsVerify(boolean userIsVerify) {
        this.userIsVerify = userIsVerify;
    }

    public String getAlipayNumber() {
        return alipayNumber;
    }

    public void setAlipayNumber(String alipayNumber) {
        this.alipayNumber = alipayNumber;
    }

    public String getWxPayType() {
        return wxPayType;
    }

    public void setWxPayType(String wxPayType) {
        this.wxPayType = wxPayType;
    }


    /**
     * 初始化ImageLoader
     *
     * @param context 上下文
     */
    private void initImageLoad(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(480, 800) // maxwidth, max height，即保存的每个缓存文件的最大长宽
                .threadPoolSize(3)//线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
                .memoryCacheSize(2 * 1024 * 1024)
                .discCacheSize(50 * 1024 * 1024)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .discCacheFileCount(500) //缓存的文件数量
                //.discCache(new UnlimitedDiscCache(cacheDir))//自定义缓存路径
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(this, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                .writeDebugLogs() // Remove for releaseapp
                .build();
        // Initialize UpLoaderLocalUtils with configuration.
        ImageLoader.getInstance().init(config);
    }


    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }
}