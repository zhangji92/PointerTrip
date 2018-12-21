package yc.pointer.trip.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import yc.pointer.trip.R;
import yc.pointer.trip.activity.NewOrderDetailActivity;
import yc.pointer.trip.activity.OrderDetailActivity;
import yc.pointer.trip.adapter.MyOrderAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseViewPageFragment;
import yc.pointer.trip.bean.UnpaidBean;
import yc.pointer.trip.bean.eventbean.ChangTypeBean;
import yc.pointer.trip.event.MyOrderGrabEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.view.LoadDialog;

/**
 * Created by moyan on 2018/4/20.
 * <p>
 * 已抢订单
 */

public class GrabOrderfragment extends BaseViewPageFragment {

    @BindView(R.id.refresh_recycler)
    RecyclerView refreshRecycler;
    @BindView(R.id.empty_img)
    ImageView emptyImg;
    @BindView(R.id.adapter_empty)
    TextView adapterEmpty;
    @BindView(R.id.empty)
    LinearLayout empty;
    @BindView(R.id.refresh_smart)
    SmartRefreshLayout refreshSmart;


    private String mDevcode;//设备识别码
    private String mUserId;//用户id
    private long mTimestamp;//时间戳
    private LoadDialog mLoadDialog;

    private int orderStatus = 0;//订单状态
    private int robPage = 0;//已抢分页
    private boolean robFlag = true;//已抢订单 true刷新 false加载

    private List<UnpaidBean.DataBean> mGrabDataList;//已抢数据
    private MyOrderAdapter mGrabOrderAdapter;
    //private MyBroadcastReciver receiver;
    private boolean isLoad = true;


    @Override
    protected int getContentViewLayout() {

        return R.layout.graborder_layout;
    }

    @Override
    protected void initView() {

        mDevcode = ((MyApplication) getActivity().getApplication()).getDevcode();
        mTimestamp = ((MyApplication) getActivity().getApplication()).getTimestamp();
        mUserId = ((MyApplication) getActivity().getApplication()).getUserId();

//
        refreshSmart.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                //上拉加载
                robFlag = false;
                ++robPage;
                getServiceMsg("3", robPage, orderStatus);
                refreshSmart.finishLoadmore();
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                //下拉刷新
                robFlag = true;
                refreshSmart.setLoadmoreFinished(false);
                robPage = 0;
//                orderStatus = 0;
                getServiceMsg("3", robPage, orderStatus);
                refreshSmart.finishRefresh();
            }
        });

        refreshRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mGrabDataList = new ArrayList<>();

        if (mGrabDataList.size()<=0){
            refreshRecycler.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
            adapterEmpty.setText("哎呀~您还没有订单");
            emptyImg.setImageResource(R.mipmap.no_oreder);
        }
        mGrabOrderAdapter = new MyOrderAdapter(mGrabDataList, false);
        refreshRecycler.setAdapter(mGrabOrderAdapter);//设置适配器

        //IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction("changeGrabDataType");
        //receiver = new MyBroadcastReciver();
        //getActivity().registerReceiver(receiver, intentFilter);
        mGrabOrderAdapter.setmMyOrderCallBack(new MyOrderAdapter.MyOrderCallBack() {
            @Override
            public void onClickBack(int position) {
                String ordStatus = mGrabDataList.get(position).getOrd_status();
                String oid = mGrabDataList.get(position).getOid();
//                if (ordStatus.equals("3") || ordStatus.equals("6") || ordStatus.equals("7")) {//已完成跳转订单详情页面
                Intent intent = new Intent(getActivity(), NewOrderDetailActivity.class);
                intent.putExtra("oid", oid);
                intent.putExtra("flag", "status");
                intent.putExtra("orderIsJieStatus", true);
                intent.putExtra("tui", false);
                startActivity(intent);
            }
        });
        isPrepared=true;
        loadData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isVisble = true;
            onVisible();
        } else {
            isVisble = false;
            onInVisible();
        }
    }
    protected void onInVisible() {

    }

    protected void onVisible() {
        //加载数据
        loadData();
    }

    protected  void loadData(){
        if (!isVisble||!isPrepared){
            return;
        }
        mLoadDialog = new LoadDialog(getActivity(), "正在加载...", R.drawable.load_animation);

        if (isLoad){
            robFlag=true;
            mLoadDialog.show();
            getServiceMsg("3", robPage, orderStatus);//获取网络数据
        }

    };

    @Override
    protected boolean needEventBus() {
        return true;
    }

    /**
     * 获取网络数据
     *
     * @param type
     * @param page
     */
    private void getServiceMsg(String type, int page, int status) {
        boolean timeFlag = APPUtils.judgeTimeDev(getActivity(), mDevcode, mTimestamp);
        if (timeFlag) {
            String sign = Md5Utils.createMD5("devcode=" + mDevcode + "ord_status=" + status + "p=" + page + "timestamp=" + mTimestamp + "type=" + type + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", mDevcode)
                    .add("ord_status", String.valueOf(status))
                    .add("p", String.valueOf(page))
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("type", type)
                    .add("uid", mUserId)
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.NEW_ORDER_LIST, requestBody, new HttpCallBack(new MyOrderGrabEvent()));
//
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void myOrderBean(MyOrderGrabEvent event) {
        if (event.isTimeOut()) {
            mLoadDialog.dismiss();//取消动画
            Toast.makeText(getActivity(), "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        UnpaidBean bean = event.getData();
        if (bean.getStatus() == 0) {
            isLoad=false;
            if (robFlag) {
                //刷新
                if (bean.getData().size() == 0) {
                    refreshRecycler.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                    adapterEmpty.setText("哎呀~您还没有订单");
                    emptyImg.setImageResource(R.mipmap.no_oreder);
                } else {
                    refreshRecycler.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                    mGrabDataList.clear();
                    mGrabDataList.addAll(bean.getData());
                    refreshRecycler.setAdapter(mGrabOrderAdapter);
                    mGrabOrderAdapter.notifyDataSetChanged();
                    refreshSmart.finishRefresh();
                }
            } else {
                //加载
                if (bean.getData().size() == 0) {
                    refreshSmart.setLoadmoreFinished(true);
                } else {
                    mGrabDataList.addAll(bean.getData());
                }
            }
            mGrabOrderAdapter.notifyDataSetChanged();
            refreshSmart.finishLoadmore();
            mLoadDialog.dismiss();//取消动画
        } else {
            mLoadDialog.dismiss();//取消动画
            Toast.makeText(getActivity(), bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(getActivity(), bean.getStatus());
        }
    }




    //广播接收内部类
//    private class MyBroadcastReciver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals("changeGrabDataType")) {
////            //在android端显示接收到的广播内容
////            Toast.makeText(getActivity(), author, 1).show();
////                mGrabDataList.clear();
////                mLoadDialog.show();
//                robFlag = true;
//                robPage = 0;
//                orderStatus = intent.getIntExtra("statusType", 0);
//                getServiceMsg("3", robPage, orderStatus);//获取网络数据
//
//            }
//        }
//    }

    /**
     * MyOrder发送过来
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ChangeType(ChangTypeBean bean) {
        if (bean != null && bean.getChangeType().equals("changeGrabDataType")) {
            robFlag = true;
            robPage = 0;
            if (bean.getStatusType() > 0) {
                orderStatus = bean.getStatusType();
            } else {
                orderStatus = 0;
            }
            getServiceMsg("3", robPage, orderStatus);//获取网络数据
        }
    }


}
