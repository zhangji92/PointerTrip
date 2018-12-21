package yc.pointer.trip.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.*;
import butterknife.BindView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import yc.pointer.trip.R;
import yc.pointer.trip.adapter.MyOrderAdapter;
import yc.pointer.trip.adapter.MyReserveAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.MyReserveBean;
import yc.pointer.trip.bean.UnpaidBean;
import yc.pointer.trip.bean.eventbean.OrderNotifyBean;
import yc.pointer.trip.bean.eventbean.RefreshEarningAProfitBean;
import yc.pointer.trip.event.MyRserveEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.LoadDialog;
import yc.pointer.trip.view.ToolbarWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moyan on 2017/9/4.
 * 我对预约
 */
public class MyReserveActivity extends BaseActivity {
    @BindView(R.id.check_my_reserve)
    CheckBox checkMyReserve;
    @BindView(R.id.refresh_recycler)
    RecyclerView refreshRecycler;
    @BindView(R.id.adapter_empty)
    TextView adapterEmpty;
    @BindView(R.id.empty_img)
    ImageView orderEmpty;
    @BindView(R.id.empty)
    LinearLayout empty;
    @BindView(R.id.refresh_smart)
    SmartRefreshLayout refreshSmart;

    @BindView(R.id.verify_title)
    TextView verifyTitle;
    @BindView(R.id.button_go_verify)
    Button buttonGoVerify;
    @BindView(R.id.note_go_verify)
    TextView noteGoVerify;
    @BindView(R.id.go_verify)
    LinearLayout goVerify;



    private String mDevcode;//设备识别码
    private String mUserId;//用户id
    private long mTimestamp;//时间戳
    private boolean judgeTimeDev;

    private boolean isMyReserve = true;//判断是我预约别人的还是别人预约我的  true：我预约别人的
    private boolean myReserveFlag = true;//我的预约 true刷新 false加载
    private boolean reserveMeFlag = true;//预约我的 true刷新 false加载

    private int myReservePage = 0;//我的预约分页
    private int reserveMePage = 0;//预约我的分页
    private int type;//类型 1=>我的预约 2=>预约我的

    private List<MyReserveBean.DataBean> myReserveDataList = new ArrayList<>();
    private List<MyReserveBean.DataBean> reserveMeDataList = new ArrayList<>();
    private MyReserveAdapter myReservedapter;
    private MyReserveAdapter reserveMeAdapter;
    private LoadDialog mLoadDialog;
    //private MyBroadcastReciver receiver;


    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_my_reserve;
    }

    @Override
    protected void initView() {
        new ToolbarWrapper(this).setCustomTitle(R.string.my_reservation);

        mDevcode = ((MyApplication) getApplication()).getDevcode();
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();
        mUserId = ((MyApplication) getApplication()).getUserId();
        judgeTimeDev = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
        boolean orderPushFlag = getIntent().getBooleanExtra("orderPushFlag", true);//订单详情页面，当拒绝预约时跳转过来显示预约我的界面
        //注册广播
        //IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction("orderNotify");
        //intentFilter.addAction("刷新赚一赚");
        //receiver = new MyBroadcastReciver();
        //registerReceiver(receiver, intentFilter);

        String is_jie = ((MyApplication) getApplication()).getUserBean().getIs_jie();

        if (is_jie.equals("2")) {
            //开启动画
            mLoadDialog = new LoadDialog(this, "正在加载...", R.drawable.load_animation);
            mLoadDialog.show();
            refreshSmart.setVisibility(View.VISIBLE);
            checkMyReserve.setVisibility(View.VISIBLE);
            goVerify.setVisibility(View.GONE);
        } else if (is_jie.equals("1")) {
            //审核中
            showVierfy("您的会员实名认证信息正在审核中", "点击查看", "我的预约");
        } else if (is_jie.equals("3")) {
            //审核失败
            showVierfy("您的会员实名认证信息审核未通过", "重新认证", "我的预约");
        } else {
            showVierfy("您尚未开通指针会员", "马上开通", "我的预约");
        }


        if (orderPushFlag) {
            checkMyReserve.setChecked(true);
            //请求我的预约信息
            getMyReserve(myReservePage, 1);
        } else {
            checkMyReserve.setChecked(false);
            //请求我的预约信息
            getMyReserve(myReservePage, 2);
        }


        checkMyReserve.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {//我预约的
                    isMyReserve = true;
                    myReserveFlag = true;//我的预约 数据加载
                    //请求网络获取信息
                    myReservePage = 0;
                    getMyReserve(myReservePage, 1);
                } else {//预约我的
                    reserveMeFlag = true;//预约我的 数据加载
                    isMyReserve = false;
                    //请求网络获取信息
                    reserveMePage = 0;
                    getMyReserve(reserveMePage, 2);
                }
                checkMyReserve.setClickable(false);//网络加载完成前不可点击
                refreshSmart.setLoadmoreFinished(false);//可以加载
            }
        });

        refreshRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        //我的预约 数据适配器
        myReservedapter = new MyReserveAdapter(myReserveDataList,true);
        //预约我的 数据适配器
        reserveMeAdapter = new MyReserveAdapter(reserveMeDataList,false);

        myReservedapter.setmMyOrderCallBack(new MyReserveAdapter.MyOrderCallBack() {//我的预约点击回调
            @Override
            public void onClickBack(int position) {
                String ordStatus = myReserveDataList.get(position).getOrd_status();
                String oid = myReserveDataList.get(position).getOid();
                if (ordStatus.equals("0") || ordStatus.equals("1") || ordStatus.equals("2")) {//未完成跳转发单页面
                    Intent intent = new Intent(MyReserveActivity.this, BillActivity.class);
                    intent.putExtra("oid", oid);
                    intent.putExtra("intentFlag", "myOrder");
                    startActivity(intent);
                } else {//已完成跳转订单详情页面
                    Intent intent = new Intent(MyReserveActivity.this, OrderDetailActivity.class);
                    intent.putExtra("oid", oid);
                    intent.putExtra("flag", "status");
                    startActivity(intent);

                }
            }
        });
        reserveMeAdapter.setmMyOrderCallBack(new MyReserveAdapter.MyOrderCallBack() {//预约我的item点击回调
            @Override
            public void onClickBack(int position) {
                String oid = reserveMeDataList.get(position).getOid();
                Intent intent = new Intent(MyReserveActivity.this, OrderDetailActivity.class);
                intent.putExtra("oid", oid);
                intent.putExtra("flag", "appointment");
                intent.putExtra("tui", false);
                startActivity(intent);
            }
        });

        refreshRecycler.setAdapter(myReservedapter);

        refreshSmart.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                if (isMyReserve) {//我预约的
                    myReserveFlag = false;//我的预约 数据加载
                    ++myReservePage;
                    getMyReserve(myReservePage, 1);
                } else {//预约我的
                    reserveMeFlag = false;//预约我的 数据加载
                    ++reserveMePage;
                    getMyReserve(reserveMePage, 2);
                }
                refreshSmart.finishLoadmore();
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshSmart.setLoadmoreFinished(false);
                if (isMyReserve) {//我预约的
                    myReserveFlag = true;//我的预约 数据刷新
                    myReservePage = 0;
                    getMyReserve(myReservePage, 1);

                } else {//预约我的
                    reserveMeFlag = true;//预约我的 数据刷新
                    reserveMePage = 0;
                    getMyReserve(reserveMePage, 2);
                }
                refreshSmart.finishRefresh();
            }
        });
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

    /**
     * 显示认证的方法
     */
    private void showVierfy(String title, String buttontext, String note) {
        refreshSmart.setVisibility(View.GONE);
        checkMyReserve.setVisibility(View.GONE);
        goVerify.setVisibility(View.VISIBLE);
        verifyTitle.setText(title);
        buttonGoVerify.setText(buttontext);
        String text = String.format(getResources().getString(R.string.note_verify), note);
        noteGoVerify.setText(text);
        buttonGoVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyReserveActivity.this, VerifyActivity.class);
                intent.putExtra("goVerify", "myReserve");
                startActivity(intent);
            }
        });
    }


    /**
     * 获取我预约的订单信息
     *
     * @param page
     */
    private void getMyReserve(int page, int type) {

        if (!StringUtil.isEmpty(mUserId)) {
            if (judgeTimeDev) {
                String sign = Md5Utils.createMD5("devcode=" + mDevcode + "p=" + page + "timestamp=" + mTimestamp + "type=" + type + "uid=" + mUserId + URLUtils.WK_APP_KEY);
                RequestBody requestBody = new FormBody.Builder()
                        .add("devcode", mDevcode)
                        .add("p", String.valueOf(page))
                        .add("timestamp", String.valueOf(mTimestamp))
                        .add("type", String.valueOf(type))
                        .add("uid", mUserId)
                        .add("signature", sign)
                        .build();
                OkHttpUtils.getInstance().post(URLUtils.MY_RESERVE, requestBody, new HttpCallBack(new MyRserveEvent()));
            }
        } else {
            return;
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getReserveMsg(MyRserveEvent myRserveEvent) {
        if (myRserveEvent.isTimeOut()) {
            mLoadDialog.dismiss();
            checkMyReserve.setClickable(true);
            Toast.makeText(this, "网络状态异常，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }
        MyReserveBean reserveBean = myRserveEvent.getData();
        if (reserveBean.getStatus() == 0) {
            mLoadDialog.dismiss();
            if (isMyReserve) {//我的预约数据
                if (myReserveFlag) {//刷新的数据
                    if (reserveBean.getData().size() == 0) {
                        refreshRecycler.setVisibility(View.GONE);
                        empty.setVisibility(View.VISIBLE);
                        adapterEmpty.setText("哎呀~您还没有预约订单");
                        orderEmpty.setImageResource(R.mipmap.no_oreder);
                    } else {
                        refreshRecycler.setVisibility(View.VISIBLE);
                        empty.setVisibility(View.GONE);
                        myReserveDataList.clear();
                        myReserveDataList.addAll(reserveBean.getData());
                        refreshRecycler.setAdapter(myReservedapter);
                        refreshSmart.finishRefresh();
                    }
                } else {//加载的数据
                    if (reserveBean.getData().size() == 0) {
                        refreshSmart.setLoadmoreFinished(true);
                    } else {
                        myReserveDataList.addAll(reserveBean.getData());
                    }
                }
                checkMyReserve.setClickable(true);
                myReservedapter.notifyDataSetChanged();
                refreshSmart.finishLoadmore();
            } else {//预约我的数据
                if (reserveMeFlag) {//刷新
                    if (reserveBean.getData().size() == 0) {
                        refreshRecycler.setVisibility(View.GONE);
                        empty.setVisibility(View.VISIBLE);
                        adapterEmpty.setText("哎呀~您还没有预约订单");
                        orderEmpty.setImageResource(R.mipmap.no_oreder);
                    } else {
                        refreshRecycler.setVisibility(View.VISIBLE);
                        empty.setVisibility(View.GONE);
                        reserveMeDataList.clear();
                        reserveMeDataList.addAll(reserveBean.getData());
                        refreshRecycler.setAdapter(reserveMeAdapter);
                        refreshSmart.finishRefresh();
                    }
                } else {//加载
                    if (reserveBean.getData().size() == 0) {
                        refreshSmart.setLoadmoreFinished(true);
                    } else {
                        reserveMeDataList.addAll(reserveBean.getData());
                    }
                }
                checkMyReserve.setClickable(true);
                reserveMeAdapter.notifyDataSetChanged();
                refreshSmart.finishLoadmore();
            }
        } else {
            mLoadDialog.dismiss();
            checkMyReserve.setClickable(true);
            Toast.makeText(this, reserveBean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(MyReserveActivity.this, reserveBean.getStatus());
        }


    }


//    //广播接收内部类
//    private class MyBroadcastReciver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals("orderNotify")) {
//                String notifyFlag = intent.getStringExtra("notifyFlag");
//                if (!StringUtil.isEmpty(notifyFlag)){
//                    //刷新已发数据
//                    if (notifyFlag.equals("bill")) {
//                        checkMyReserve.setChecked(true);
//                        isMyReserve = true;
//                        myReserveFlag = true;//我的预约 数据加载
//                        //请求网络获取信息
//                        myReservePage = 0;
//                        getMyReserve(myReservePage, 1);
//                    } else if (notifyFlag.equals("grabOrder")) {
//                        reserveMeFlag = true;//预约我的 数据加载
//                        checkMyReserve.setChecked(false);
//                        isMyReserve = false;
//                        //请求网络获取信息
//                        reserveMePage = 0;
//                        getMyReserve(reserveMePage, 2);
//                    }
//                    refreshSmart.setLoadmoreFinished(false);//可以加载
//                }
//
////            //在android端显示接收到的广播内容
////            Toast.makeText(getActivity(), author, 1).show();
//
//                //在结束时可取消广播
//                unregisterReceiver(this);
//            }else  if (action.equals("刷新赚一赚")){
//                initView();
//            }
//        }
//    }

    /**
     * bill grab等activity发送
     * @param bean
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void orderNotify(OrderNotifyBean bean) {
        if (bean != null) {
            //刷新已发数据
            if (bean.getOrderNotify().equals("bill")) {
                checkMyReserve.setChecked(true);
                isMyReserve = true;
                myReserveFlag = true;//我的预约 数据加载
                //请求网络获取信息
                myReservePage = 0;
                getMyReserve(myReservePage, 1);
            } else if (bean.getOrderNotify().equals("grabOrder")) {
                reserveMeFlag = true;//预约我的 数据加载
                checkMyReserve.setChecked(false);
                isMyReserve = false;
                //请求网络获取信息
                reserveMePage = 0;
                getMyReserve(reserveMePage, 2);
            }
            refreshSmart.setLoadmoreFinished(false);//可以加载
        }
    }
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
