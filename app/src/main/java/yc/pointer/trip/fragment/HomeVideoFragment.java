package yc.pointer.trip.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.youth.banner.Banner;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.jpush.android.api.JPushInterface;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import yc.pointer.trip.R;
import yc.pointer.trip.activity.LoginActivity;
import yc.pointer.trip.activity.MainActivity;
import yc.pointer.trip.activity.ReadingTravelsActivity;
import yc.pointer.trip.activity.SearchActivity;
import yc.pointer.trip.adapter.HomeVideoFragmentAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseFragment;
import yc.pointer.trip.bean.ApkUpdateBean;
import yc.pointer.trip.bean.BookBean;
import yc.pointer.trip.bean.HomeVideoDataBean;
import yc.pointer.trip.bean.HomeVideoNewDataBean;
import yc.pointer.trip.bean.SaveMesgBean;
import yc.pointer.trip.event.ApkUpdateEvent;
import yc.pointer.trip.event.HomeLoginEvent;
import yc.pointer.trip.event.HomeThirdLoginEvent;
import yc.pointer.trip.event.HomeVideoEvent;
import yc.pointer.trip.event.MainFragmentRefreshEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.receiver.DownloadService;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.AppApplicationMgr;
import yc.pointer.trip.untils.LocationUtil;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.MySQLiteDataBase;
import yc.pointer.trip.untils.PermissionHelper;
import yc.pointer.trip.untils.SharedPreferencesUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.DialogApk;
import yc.pointer.trip.view.DialogMustUpteApk;
import yc.pointer.trip.view.FadingScrollView;
import yc.pointer.trip.view.LoadDialog;

/**
 * Created by moyan on 2017/11/20.
 */

public class HomeVideoFragment extends BaseFragment {
    @BindView(R.id.refresh_recycler)
    RecyclerView homeVideoRecycler;
    @BindView(R.id.refresh_smart)
    SmartRefreshLayout refreshSmart;
    //    @BindView(R.id.refresh_linear)
//    LinearLayout refreshLinear;
    @BindView(R.id.nac_layout)
    View nacLayout;
    @BindView(R.id.video_search)
    ImageView videoSearch;
//    @BindView(R.id.nac_root_home)
//    FadingScrollView nacRoot;


    private HomeVideoFragmentAdapter adapter;
    private LoadDialog mLoadDialog;
    private long mTimestamp;//时间戳
    private String mDevcode;//手机识别码
    private String mUserId;//用户id
    private String durl;//APK下载地址
    private String mLocalCity = "";//定位城市
    private int page = 0;
    boolean isNotLogin = true;//判断是否需要自动登录
    private boolean flag = true;//true-刷新  false-加载
    private List<HomeVideoNewDataBean.DataBean.DataAdBean> mList = new ArrayList();//banner数据集合
    private List<HomeVideoNewDataBean.DataBean.DataGoodBean> mListAll = new ArrayList<>();//精选路书
    private HomeVideoNewDataBean.DataBean.HbBean hbInstance;

    //    private PermissionHelper permissionHelper;
//
//    private PermissionHelper.PermissionModel[] permissionModels = {
//            new PermissionHelper.PermissionModel(1111, Manifest.permission.ACCESS_FINE_LOCATION, "读取您的位置"),
//
//    };
//    private PermissionHelper.PermissionModel[] permissionModelStorage = {
//            new PermissionHelper.PermissionModel(2222, Manifest.permission.WRITE_EXTERNAL_STORAGE, "读取您的存储权限"),
//
//    };
    private boolean timeFlag;



    //    private int fadingHeight = 300;
    private String registrationID;
    private boolean isUp;
    private LinearLayoutManager layoutManager;

    @Override
    protected int getContentViewLayout() {
//        nacRoot.setFadingHeightView();
        return R.layout.home_fragment_video_layout;
    }

    @Override
    protected void initView() {

//        nacLayout.setAlpha(0);
//        videoSearch.setAlpha((float) 1.0);
//        nacRoot.setFadingView(getActivity(), nacLayout);
//        nacRoot.setShowHidenView(getActivity(), videoSearch);


        //开启动画
        mLoadDialog = new LoadDialog(getActivity(), "正在加载...", R.drawable.load_animation);
        mLoadDialog.show();

        mDevcode = ((MyApplication) getActivity().getApplication()).getDevcode();
        mTimestamp = ((MyApplication) getActivity().getApplication()).getTimestamp();
        mUserId = ((MyApplication) getActivity().getApplication()).getUserId();
        mLocalCity = ((MyApplication) getActivity().getApplication()).getLocationCity();

        timeFlag = APPUtils.judgeTimeDev(getActivity(), mDevcode, mTimestamp);
        registrationID = JPushInterface.getRegistrationID(getActivity());

        if (StringUtil.isEmpty(mUserId)) {
            mUserId = "0";
        }
//        permissionHelper=new PermissionHelper(getActivity(),this,permissionModels);
        /**
         * 判断是否联网
         */
        if (!OkHttpUtils.isNetworkAvailable(getActivity())) {
            //读取缓存首页数据
            getCatchData();
        } else {
            //获取首页数据
            if (!StringUtil.isEmpty(mLocalCity)) {
                getMesForNet(page, mLocalCity);//获取网络接口
            } else {

                getLocation();
                getMesForNet(page, mLocalCity);//获取网络接口
            }
//                if (Build.VERSION.SDK_INT < 23) {
//                    getLocation();
//                    getMesForNet(page, mLocalCity);//获取网络接口
//                } else {
//                    //申请权限
//                    getLocation();
//                    getMesForNet(page, mLocalCity);
////                    permissionHelper = new PermissionHelper(getActivity(), new PermissionHelper.OnAlterApplyPermission() {
////                        @Override
////                        public void OnAlterApplyPermission() {
//
////                        }
////                    }, permissionModels);
////                    permissionHelper.applyPermission();
//                }
//            }
        }


        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        homeVideoRecycler.setLayoutManager(layoutManager);


        adapter = new HomeVideoFragmentAdapter(mList, mListAll);
        //RecycleView滑动监听
        homeVideoRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.e("newState", "" + newState);


                if (newState == recyclerView.SCROLL_STATE_DRAGGING) {
                    //拖动过程种


                } else if (newState == recyclerView.SCROLL_STATE_SETTLING) {
                    //滑动结束


                } else if (newState == recyclerView.SCROLL_STATE_IDLE) {
                    //滑动未开始

                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                boolean isSignificantDelta = Math.abs(dy) > fadingHeight;
//                Animation animationOut = AnimationUtils.loadAnimation(getActivity(), R.anim.actionbar_up_out);//移出动画
//                Animation animationIn = AnimationUtils.loadAnimation(getActivity(), R.anim.actionbar_up_in);//进入动画
                int pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
                if (dy > 0) {
                    //向上滑动
                    if (pastVisiblesItems == 0) {
                        nacLayout.setVisibility(View.GONE);
                        videoSearch.setVisibility(View.VISIBLE);
                    } else {
                        videoSearch.setVisibility(View.GONE);
                        nacLayout.setVisibility(View.GONE);
                    }
                } else {
                    //向下滑动
                    if (pastVisiblesItems == 0) {
                        nacLayout.setVisibility(View.GONE);
                    } else {
                        nacLayout.setVisibility(View.VISIBLE);
                    }
                    videoSearch.setVisibility(View.VISIBLE);
                }
            }
        });
        homeVideoRecycler.setAdapter(adapter);

        refreshSmart.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                flag = false;
                ++page;
                getMesForNet(page, mLocalCity);
                refreshSmart.finishLoadmore();
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {

                mUserId = ((MyApplication) getActivity().getApplication()).getUserId();
                if (StringUtil.isEmpty(mUserId)) {
                    mUserId = "0";
                }
                refreshSmart.setLoadmoreFinished(false);
                flag = true;
                page = 0;
                getMesForNet(page, mLocalCity);
                refreshSmart.finishRefresh();
            }
        });
        videoSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                getActivity().startActivity(intent);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void mainFragment(MainFragmentRefreshEvent event) {
        if (event.isFlag()) {
            refreshSmart.autoRefresh();//自动刷新
        }
    }


    @Override
    protected boolean needEventBus() {
        return true;
    }


    /**
     * 定位
     */
    private void getLocation() {

        LocationUtil.getLocationUtilInstance().initLocation(getActivity()).setmICallLocation(new LocationUtil.ICallLocation() {
            @Override
            public void locationMsg(AMapLocation aMapLocation) {
                String locationCity = aMapLocation.getCity();
                ((MyApplication) getActivity().getApplication()).setLocationCity(locationCity);
                mLocalCity = locationCity;
            }
        });

    }

    /**
     * 获取首页数据
     *
     * @param page
     */
    private void getMesForNet(int page, String city) {
        boolean timeFlag = APPUtils.judgeTimeDev(getActivity(), mDevcode, mTimestamp);
        if (timeFlag) {
//            String str = Md5Utils.createMD5("city=" + city + "devcode=" + mDevcode + "p=" + page + "timestamp=" + mTimestamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            String str = Md5Utils.createMD5("devcode=" + mDevcode + "p=" + page + "timestamp=" + mTimestamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", String.valueOf(mDevcode))
                    .add("p", String.valueOf(page))
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("signature", str)
                    .add("uid", mUserId)
//                    .add("city", city)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.HOME_VIDEO_NEW_DATA, requestBody, new HttpCallBack(new HomeVideoEvent()));
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void homeEvent(HomeVideoEvent event) {
        if (event.isTimeOut()) {
            mLoadDialog.dismiss();//取消动画
            automaticLogin();
            Toast.makeText(getActivity(), "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        final HomeVideoNewDataBean data = event.getData();
        if (data.getStatus() == 0) {
            //自动登录
            if (isNotLogin) {
                isNotLogin = false;
                automaticLogin();
            }

            mLoadDialog.dismiss();//取消动画
            HomeVideoNewDataBean.DataBean.HbBean hb = data.getData().getHb();
            String is_hb = data.getData().getHb().getIs_hb();

            if (!StringUtil.isEmpty(is_hb) && is_hb.equals("1")) {
                //有红包模式，显示模块
                SharedPreferencesUtils.getInstance().putBoolean(getActivity(), "isCash", true);
                //动态更换桌面图标
//                changeIcon("yc.pointer.trip.activity.MainAliasActivity");
            } else {
//                changeIcon("yc.pointer.trip.activity.MainActivity");
                SharedPreferencesUtils.getInstance().putBoolean(getActivity(), "isCash", false);
            }
            if (flag) {
                mList.clear();
                mListAll.clear();
                mList.addAll(data.getData().getData_ad());//轮播数据
                mListAll.addAll(data.getData().getData_good());//精选全部路书
                homeVideoRecycler.setAdapter(adapter);
//                mListSameCity.clear();
//                mListAction.clear();
//                mListSameCity.addAll(bean.getData().getData_city());//同城数据

//                mListAction.addAll(bean.getData().getData_ad_index());//精选全部路书
//                adapter = new HomeVideoFragmentAdapter(hbInstance, mList, mListSameCity, mListAll, mListAction);
//                adapter.setSameCityOnClickListener(new HomeVideoFragmentAdapter.SameCityOnClickListener() {
//                    @Override
//                    public void sameCityMoreDateClick() {
//                        Intent intent = new Intent(getActivity(), ReadingTravelsActivity.class);
//                        intent.putExtra("city", mLocalCity);
//                        getActivity().startActivity(intent);
//                    }
//                });
            } else {
                if (data.getData().getData_good().size() == 0) {
                    refreshSmart.setLoadmoreFinished(false);//设置之后，将不会再触发加载事件
                } else {
                    //添加适配器
                    mListAll.addAll(data.getData().getData_good());
                    adapter.notifyDataSetChanged();
                    refreshSmart.finishLoadmore();//停止加载
                }
            }
        } else if (data.getStatus() == 201) {
            ((MyApplication) getActivity().getApplication()).setUserBean(null);
            ((MyApplication) getActivity().getApplication()).setIslogin(false);
            ((MyApplication) getActivity().getApplication()).setUserId("");
            SharedPreferencesUtils.getInstance().remove(getActivity(), "phone");
            SharedPreferencesUtils.getInstance().remove(getActivity(), "pad");
            mLoadDialog.dismiss();//取消动画
            //读取缓存数据
//            getCatchData();
            APPUtils.intentLogin(getActivity(), data.getStatus());
        } else {
            if (isNotLogin) {
                isNotLogin = false;
                automaticLogin();
            }
            mLoadDialog.dismiss();//取消动画
            Toast.makeText(getActivity(), data.getMsg(), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 动态改变桌面图标
     *
     * @param activityPath
     */
//    public void changeIcon(String activityPath) {
//        PackageManager pm = getActivity().getPackageManager();
//        pm.setComponentEnabledSetting(getActivity().getComponentName(),
//                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
//        pm.setComponentEnabledSetting(new ComponentName(getActivity(), activityPath),
//                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
//
//
//        //重启桌面 加速显示
////        restartSystemLauncher(pm);
//    }

    /**
     * 自动登录方法
     */
    private void automaticLogin() {
        String phone = SharedPreferencesUtils.getInstance().getString(getActivity(), "loginPhone");
        String pad = SharedPreferencesUtils.getInstance().getString(getActivity(), "pad");
        String loginType = SharedPreferencesUtils.getInstance().getString(getActivity(), "loginType");
        String loginID = SharedPreferencesUtils.getInstance().getString(getActivity(), "loginId");
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
     * 登陆
     *
     * @param phone 用户名
     * @param pad   密码
     */
    private void Login(String phone, String pad, String devcode, long timestamp) {
        if (timeFlag) {
            String sign = Md5Utils.createMD5("devcode=" + devcode + "location=" + mLocalCity + "phone=" + phone + "pwd=" + pad + "registration_id=" + registrationID + "timestamp=" + timestamp + URLUtils.WK_APP_KEY);
            RequestBody params = new FormBody.Builder()
                    .add("timestamp", String.valueOf(timestamp))
                    .add("phone", phone)
                    .add("pwd", pad)
                    .add("location", mLocalCity)
                    .add("registration_id", registrationID)
                    .add("signature", sign)
                    .add("devcode", devcode)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.LOGIN, params, new HttpCallBack(new HomeLoginEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginMsg(HomeLoginEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(getActivity(), "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        SaveMesgBean loginBean = event.getData();
        if (loginBean.getStatus() == 0) {
            savePersonMsg(loginBean);
        } else {
            Toast.makeText(getActivity(), loginBean.getMsg(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 保存用户信息
     *
     * @param loginBean
     */
    private void savePersonMsg(SaveMesgBean loginBean) {
        String uid = loginBean.getData().getUid();
        String loginPhone = loginBean.getData().getPhone();
        String is_order = loginBean.getData().getIs_order();
        String login_type = loginBean.getData().getLogin_type();
        String is_bd = loginBean.getData().getIs_bd();
        String bank_num = loginBean.getData().getBank_num();
        String alipay_num = loginBean.getData().getAlipay();
        SharedPreferencesUtils.getInstance().putString(getActivity(), "loginPhone", loginPhone);
        SharedPreferencesUtils.getInstance().putString(getActivity(), "useId", uid);
        SharedPreferencesUtils.getInstance().putString(getActivity(), "loginType", login_type);
        SharedPreferencesUtils.getInstance().putString(getActivity(), "isOrder", is_order);
        String pic = loginBean.getData().getPic();
        if (!StringUtil.isEmpty(pic)) {
            SharedPreferencesUtils.getInstance().putString(getActivity(), "headImg", pic);
        }
        if (!StringUtil.isEmpty(is_bd) && is_bd.equals("1")) {
            ((MyApplication) getActivity().getApplication()).setUserIsBinding(true);
            SharedPreferencesUtils.getInstance().putBoolean(getActivity(), "isBinding", true);
        } else if (is_bd.equals("0")) {
            ((MyApplication) getActivity().getApplication()).setUserIsBinding(false);
            SharedPreferencesUtils.getInstance().putBoolean(getActivity(), "isBinding", false);
        }

        if (!StringUtil.isEmpty(bank_num)) {
            ((MyApplication) getActivity().getApplication()).setUserIsVerify(true);
        } else {
            ((MyApplication) getActivity().getApplication()).setUserIsVerify(false);
        }
        if (!StringUtil.isEmpty(is_order) && is_order.equals("1")) {
//            ((MyApplication) getApplication()).setUserIsBinding(true);
            SharedPreferencesUtils.getInstance().putBoolean(getActivity(), "isPayDeposit", true);
        } else if (is_order.equals("0")) {
//            ((MyApplication) getApplication()).setUserIsBinding(false);
            SharedPreferencesUtils.getInstance().putBoolean(getActivity(), "isPayDeposit", false);
        }
        if (!StringUtil.isEmpty(alipay_num)) {
            ((MyApplication) getActivity().getApplication()).setAlipayNumber(alipay_num);
        } else {
            ((MyApplication) getActivity().getApplication()).setAlipayNumber("");
        }

        ((MyApplication) getActivity().getApplication()).setIslogin(true);
        ((MyApplication) getActivity().getApplication()).setUserBean(loginBean.getData());
        ((MyApplication) getActivity().getApplication()).setUserId(uid);

    }

    /**
     * 第三方登录
     */
    private void thirdLogin(String loginId, String loginType) {
        if (timeFlag) {
            String sign = Md5Utils.createMD5("devcode=" + mDevcode + "location=" + mLocalCity + "login_id=" + loginId + "login_type=" + loginType + "registration_id=" + registrationID + "timestamp=" + mTimestamp + URLUtils.WK_APP_KEY);
            RequestBody params = new FormBody.Builder()
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("login_id", loginId)
                    .add("location", mLocalCity)
                    .add("login_type", loginType)
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
            mLoadDialog.dismiss();//取消动画
            Toast.makeText(getActivity(), "网络状态异常", Toast.LENGTH_SHORT).show();
            return;
        }
        SaveMesgBean data = thirdLoginEvent.getData();
        if (data.getStatus() == 0) {
            mLoadDialog.dismiss();//取消动画
            savePersonMsg(data);
        } else {
            mLoadDialog.dismiss();//取消动画
            Toast.makeText(getActivity(), data.getMsg(), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 读取缓存首页数据
     */
    private void getCatchData() {
        mList.clear();
//        mListSameCity.clear();
        mListAll.clear();
//        mListAction.clear();
        //精选

        mLoadDialog.dismiss();
    }



}
