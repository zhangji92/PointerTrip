package yc.pointer.trip.fragment;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import yc.pointer.trip.R;
import yc.pointer.trip.activity.LoginActivity;
import yc.pointer.trip.activity.NewOrderDetailActivity;
import yc.pointer.trip.activity.OrderDetailActivity;
import yc.pointer.trip.activity.VerifyActivity;
import yc.pointer.trip.adapter.MakeMoneyAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseFragment;
import yc.pointer.trip.bean.MakeMoneyBean;
import yc.pointer.trip.bean.eventbean.RefreshEarningAProfitBean;
import yc.pointer.trip.event.MakeMoneyEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.DensityUtil;
import yc.pointer.trip.untils.LocationUtil;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.PermissionHelper;
import yc.pointer.trip.untils.StatusBarUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.LoadDialog;
import yc.pointer.trip.view.LocationDialog;
import yc.pointer.trip.view.ToolbarWrapper;

/**
 * 赚一赚fragmment
 */
public class MakeMoneyFragment extends BaseFragment {

    @BindView(R.id.refresh_recycler)
    RecyclerView refreshRecycler;
    @BindView(R.id.refresh_smart)
    SmartRefreshLayout refreshSmart;
    //@BindView(R.id.refresh_linear)
    //LinearLayout listLiner;
    @BindView(R.id.go_login)
    Button goLogin;
    @BindView(R.id.layout_unlogin)
    LinearLayout layoutUnlogin;
    @BindView(R.id.standard_toolbar)
    Toolbar standardToolbar;
    @BindView(R.id.adapter_empty)
    TextView textEmpty;//当前城市还未有人发单
    @BindView(R.id.empty_img)
    ImageView makeEmpty;//无发单状态

    @BindView(R.id.empty)
    LinearLayout empty;

    @BindView(R.id.verify_title)
    TextView verifyTitle;
    @BindView(R.id.button_go_verify)
    Button buttonGoVerify;
    @BindView(R.id.note_go_verify)
    TextView noteGoVerify;
    @BindView(R.id.go_verify)
    LinearLayout goVerify;


    private boolean islogin;//判断是否为登录状态
    private String mDevCode;
    private String mUserId;
    private long mTimeStamp;
    private int page = 0;
    private String mLocalCity = "";
    private List<MakeMoneyBean.DataBean> mBeanList = new ArrayList<>();
    private boolean refreshFlag = true;
    private MakeMoneyAdapter makeMoneyAdapter;

    private boolean isPermission = false;//权限申请标志，防止一直TOAST
    private LoadDialog mLoadDialog;

    private PermissionHelper permissionHelper;
    private PermissionHelper.PermissionModel[] permissionModels = {
            new PermissionHelper.PermissionModel(1, Manifest.permission.ACCESS_FINE_LOCATION, "读取您的位置"),

    };
    private LocationUtil locationUtil;

    @Override
    protected int getContentViewLayout() {
        return R.layout.fragment_make_monkey;
    }

    @Override
    protected void initView() {
        View decorView = getActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        DensityUtil.setStatusBarColor(getActivity(), R.color.colorTitle);
//        int statusBarHeight = StatusBarUtils.getStatusBarHeight(getActivity());
//        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) standardToolbar.getLayoutParams();
//        layoutParams.topMargin = statusBarHeight;
//        standardToolbar.setLayoutParams(layoutParams);
        int statusBarHeight = StatusBarUtils.getStatusBarHeight(getActivity());
        standardToolbar.setPadding(0, statusBarHeight, 0, 0);

        ToolbarWrapper toolbarWrapper = new ToolbarWrapper(this);
        toolbarWrapper.setShowBack(false);
        toolbarWrapper.setCustomTitle(R.string.make_monkey_title);
        //IntentFilter intentFilter = new IntentFilter();
        ////注册广播，方便刷新
        //intentFilter.addAction("刷新赚一赚");
        //getActivity().registerReceiver(new MyBroadcastReciver(), intentFilter);
        islogin = ((MyApplication) getActivity().getApplication()).isIslogin();

//        permissionHelper=new PermissionHelper(getActivity(),this,permissionModels);
        if (islogin) {
            String is_jie = ((MyApplication) getActivity().getApplication()).getUserBean().getIs_jie();

            if (is_jie.equals("2")) {
                //开启动画
                mLoadDialog = new LoadDialog(getActivity(), "正在加载...", R.drawable.load_animation);
                mLoadDialog.show();
                refreshSmart.setVisibility(View.VISIBLE);
                layoutUnlogin.setVisibility(View.GONE);
                goVerify.setVisibility(View.GONE);
                mUserId = ((MyApplication) getActivity().getApplication()).getUserId();
            } else if (is_jie.equals("1")) {
                //审核中
                showVierfy("您的会员实名认证信息正在审核中", "点击查看", "赚一赚");
            } else if (is_jie.equals("3")) {
                //审核失败
                showVierfy("您的会员实名认证信息审核未通过", "重新认证", "赚一赚");
            } else {
                showVierfy("您尚未开通指针会员", "马上开通", "赚一赚");
            }


        } else {
            refreshSmart.setVisibility(View.GONE);
            goVerify.setVisibility(View.GONE);
            layoutUnlogin.setVisibility(View.VISIBLE);

            goLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //跳转登录
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.putExtra("logFlag", "makemoney");
                    startActivityForResult(intent, 0);
                }
            });
        }
        mDevCode = MyApplication.mApp.getDevcode();
        mTimeStamp = MyApplication.mApp.getTimestamp();
        mLocalCity = MyApplication.mApp.getLocationCity();

        if (!StringUtil.isEmpty(mLocalCity)) {
            getOrderList(page, mLocalCity);//获取网络接口
        } else {
            if (Build.VERSION.SDK_INT < 23) {
                getLocation();
                //getOrderList(page, mLocalCity);//获取网络接口
            } else {
                //申请权限
                isPermission = true;
                permissionHelper = new PermissionHelper(getActivity(), new PermissionHelper.OnAlterApplyPermission() {
                    @Override
                    public void OnAlterApplyPermission() {
                        isPermission = false;
                        getLocation();
                        //getOrderList(page, mLocalCity);//获取网络
                    }

                    @Override
                    public void cancelListener() {

                    }
                }, permissionModels);
                permissionHelper.applyPermission();
            }
        }
        refreshRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        refreshSmart.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                ++page;
                refreshFlag = false;
                if (!StringUtil.isEmpty(mUserId)) {
                    getOrderList(page, mLocalCity);
                }
                refreshSmart.finishLoadmore();
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshSmart.setLoadmoreFinished(false);
                page = 0;
                refreshFlag = true;
                if (!StringUtil.isEmpty(mLocalCity)) {
                    getOrderList(page, mLocalCity);//获取网络接口
                } else {
                    if (Build.VERSION.SDK_INT < 23) {
                        getLocation();
                        //getOrderList(page, mLocalCity);//获取网络接口
                    } else {
                        isPermission = true;
                        permissionHelper = new PermissionHelper(getActivity(), new PermissionHelper.OnAlterApplyPermission() {
                            @Override
                            public void OnAlterApplyPermission() {
                                isPermission = false;
                                getLocation();
                                //getOrderList(page, mLocalCity);//获取网络
                            }

                            @Override
                            public void cancelListener() {

                            }
                        }, permissionModels);
                        permissionHelper.applyPermission();

                    }
                }
                mLoadDialog.dismiss();//取消动画
                refreshSmart.finishRefresh();
            }
        });
        makeMoneyAdapter = new MakeMoneyAdapter(mBeanList);
        refreshRecycler.setAdapter(makeMoneyAdapter);
        makeMoneyAdapter.setItemViewOnClickListener(new MakeMoneyAdapter.itemViewOnClickListener() {
            @Override
            public void OnClickBack(String oid) {
                Intent intent = new Intent(getActivity(), NewOrderDetailActivity.class);
                intent.putExtra("oid", oid);
                intent.putExtra("flag", "grabOrder");
                intent.putExtra("orderIsJieStatus", false);
                startActivity(intent);
            }
        });
    }

    /**
     * 显示认证的方法
     */
    private void showVierfy(String title, String buttontext, String note) {
        refreshSmart.setVisibility(View.GONE);
        layoutUnlogin.setVisibility(View.GONE);
        goVerify.setVisibility(View.VISIBLE);
        verifyTitle.setText(title);
        buttonGoVerify.setText(buttontext);
        String text = String.format(getResources().getString(R.string.note_verify), note);
        noteGoVerify.setText(text);
        buttonGoVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VerifyActivity.class);
                intent.putExtra("goVerify", "makemoney");
                startActivityForResult(intent, 0);
            }
        });
    }


    /**
     * 定位
     */
    private void getLocation() {
        if (APPUtils.isOPen(getActivity())) {
            locationUtil = LocationUtil.getLocationUtilInstance().initLocation(getActivity());
            locationUtil.setmICallLocation(new LocationUtil.ICallLocation() {
                @Override
                public void locationMsg(AMapLocation aMapLocation) {
                    String locationCity = aMapLocation.getCity();
                    MyApplication.mApp.setLocationCity(locationCity);
                    mLocalCity = locationCity;
                    locationUtil.stopLocation();
                    page = 0;
                    getOrderList(page, mLocalCity);
                }
            });
        } else {
            LocationDialog locationDialog = new LocationDialog(getActivity(), R.style.user_default_dialog, MakeMoneyFragment.this, 123);
            locationDialog.setMsg("检测到GPS未开启,请开启GPS获取当前城市信息");
            locationDialog.setLocationCallBack(new LocationDialog.LocationCallBack() {
                @Override
                public void closeDialog() {
                    page = 0;
                    getOrderList(page, "");
                }
            });
            locationDialog.show();
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 获取订单列表
     *
     * @param page 分页
     * @param city 定位城市
     */
    private void getOrderList(int page, String city) {
        boolean flag = APPUtils.judgeTimeDev(getActivity(), mDevCode, mTimeStamp);
        if (flag && !StringUtil.isEmpty(mUserId)) {
            String sign = Md5Utils.createMD5("devcode=" + mDevCode + "maddress=" + city + "p=" + page + "timestamp=" + mTimeStamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", mDevCode)
                    .add("maddress", city)
                    .add("p", String.valueOf(page))
                    .add("timestamp", String.valueOf(mTimeStamp))
                    .add("uid", mUserId)
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.NEW_MAKE_MONEY, requestBody, new HttpCallBack(new MakeMoneyEvent()));

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void orderListBean(MakeMoneyEvent event) {
        if (event.isTimeOut()) {
            mLoadDialog.dismiss();//取消动画
            Toast.makeText(getActivity(), "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        MakeMoneyBean bean = event.getData();
        if (bean.getStatus() == 0) {
            if (refreshFlag) {
                if (bean.getData().size() == 0) {
                    refreshRecycler.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                    textEmpty.setText("哎呀~当前城市还未有人发单");
                    makeEmpty.setImageResource(R.mipmap.no_oreder);
                } else {
                    refreshRecycler.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                    mBeanList.clear();
                    mBeanList.addAll(bean.getData());
                    refreshRecycler.setAdapter(makeMoneyAdapter);
                    makeMoneyAdapter.notifyDataSetChanged();
                    refreshSmart.finishRefresh();
                }
            } else {
                if (bean.getData().size() == 0) {
                    refreshSmart.setLoadmoreFinished(true);
                } else {
                    mBeanList.addAll(bean.getData());
                    makeMoneyAdapter.notifyDataSetChanged();
                }
                refreshSmart.finishLoadmore();
            }
            mLoadDialog.dismiss();//取消动画
        } else {
            refreshSmart.finishRefresh();
            refreshSmart.finishLoadmore();
            Toast.makeText(getActivity(), bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(getActivity(), bean.getStatus());
            mLoadDialog.dismiss();//取消动画
        }
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isPermission) {
            permissionHelper.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == 0) {
            if (resultCode == 4) {
                initView();
            }
        } else if (requestCode == 123) {//开启GPS之后的回调
            if (APPUtils.isOPen(getActivity())) {
                //String locationCity = MyApplication.mApp.getLocationCity();
                //page = 0;
                //getOrderList(page, locationCity);
                getLocation();
            }else {
                getOrderList(page, "");
            }
        }
    }

    //
    ////广播接收内部类
    //private class MyBroadcastReciver extends BroadcastReceiver {
    //    @Override
    //    public void onReceive(Context context, Intent intent) {
    //        String action = intent.getAction();
    //        if (action.equals("刷新赚一赚")) {
    //            initView();
    //            getActivity().unregisterReceiver(this);
    //        }
    //    }
    //}

    /**
     * Bill order ver 发送过来接收
     *
     * @param bean
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshEarningAProfit(RefreshEarningAProfitBean bean) {
        if (bean != null) {
            if (bean.getProfit().equals("刷新赚一赚")) {
                initView();
            }
        }
    }


}
