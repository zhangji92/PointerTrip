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

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import yc.pointer.trip.R;
import yc.pointer.trip.activity.BillActivity;
import yc.pointer.trip.activity.NewOrderDetailActivity;
import yc.pointer.trip.activity.OrderDetailActivity;
import yc.pointer.trip.adapter.MyOrderAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseViewPageFragment;
import yc.pointer.trip.bean.UnpaidBean;
import yc.pointer.trip.bean.eventbean.ChangTypeBean;
import yc.pointer.trip.bean.eventbean.UpDataBean;
import yc.pointer.trip.event.MyOrderPublisEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.LoadDialog;

/**
 * Created by moyan on 2018/4/20.
 * 已发订单
 */

public class PublishOrderFragment extends BaseViewPageFragment {
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
    private int publishPage = 0;//已抢分页
    private boolean robFlag = true;//已抢订单 true刷新 false加载
    private boolean isload = true;//是否加载  true ：加载  false：不加载

    private List<UnpaidBean.DataBean> mPublishDataList;//已发数据
    private MyOrderAdapter mPublishOrderAdapter;
    //private MyBroadcastReciver receiver;


    @Override
    protected int getContentViewLayout() {
        return R.layout.graborder_layout;
    }

    @Override
    protected void initView() {

        mDevcode = MyApplication.mApp.getDevcode();
        mTimestamp = MyApplication.mApp.getTimestamp();
        mUserId = MyApplication.mApp.getUserId();


        refreshSmart.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                //上拉加载
                robFlag = false;
                ++publishPage;
                getServiceMsg("2", publishPage, orderStatus);
                refreshSmart.finishLoadmore();
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                //下拉刷新
                robFlag = true;
                refreshSmart.setLoadmoreFinished(false);
                publishPage = 0;
//                orderStatus = 0;
                getServiceMsg("2", publishPage, orderStatus);
                refreshSmart.finishRefresh();
            }
        });


        refreshRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mPublishDataList = new ArrayList<>();
        mPublishOrderAdapter = new MyOrderAdapter(mPublishDataList, true);
        refreshRecycler.setAdapter(mPublishOrderAdapter);//设置适配器

//        mLoadDialog = new LoadDialog(getActivity(), "正在加载...", R.drawable.load_animation);
//        mLoadDialog.show();
        mPublishOrderAdapter.setmMyOrderCallBack(new MyOrderAdapter.MyOrderCallBack() {
            @Override
            public void onClickBack(int position) {
                String ordStatus = mPublishDataList.get(position).getOrd_status();
                String oid = mPublishDataList.get(position).getOid();
                Intent intent = new Intent(getActivity(), NewOrderDetailActivity.class);
                intent.putExtra("oid", oid);
                intent.putExtra("flag", "status");
                if (ordStatus.equals("0")) {//未接单的时候进入订单详情
                    intent.putExtra("orderIsJieStatus", false);
                } else {//已接单的时候进入订单详情
                    intent.putExtra("orderIsJieStatus", true);
                }
                startActivity(intent);
            }
        });
        isPrepared = true;
        loadData();

        //IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction("changePublisDataType");
        //receiver = new MyBroadcastReciver();
        //getActivity().registerReceiver(receiver, intentFilter);

    }

    @Override
    protected boolean needEventBus() {
        return true;
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

    protected void loadData() {
        if (!isVisble || !isPrepared) {
            return;
        }
        mLoadDialog = new LoadDialog(getActivity(), "正在加载...", R.drawable.load_animation);

        if (isload) {
            robFlag = true;
            mLoadDialog.show();
            getServiceMsg("2", publishPage, orderStatus);//获取网络数据
        }

    }

    ;

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
            OkHttpUtils.getInstance().post(URLUtils.NEW_ORDER_LIST, requestBody, new HttpCallBack(new MyOrderPublisEvent()));

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void myOrderBean(MyOrderPublisEvent event) {
        if (event.isTimeOut()) {
            mLoadDialog.dismiss();//取消动画
            Toast.makeText(getActivity(), "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        UnpaidBean bean = event.getData();
        if (bean.getStatus() == 0) {
            isload = false;
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
                    mPublishDataList.clear();
                    mPublishDataList.addAll(bean.getData());
                    refreshRecycler.setAdapter(mPublishOrderAdapter);
                    mPublishOrderAdapter.notifyDataSetChanged();
                }
                refreshSmart.finishRefresh();
            } else {
                //加载
                if (bean.getData().size() == 0) {
                    refreshSmart.setLoadmoreFinished(true);
                } else {
                    mPublishDataList.addAll(bean.getData());
                }
                refreshSmart.finishLoadmore();
            }
            mPublishOrderAdapter.notifyDataSetChanged();
            mLoadDialog.dismiss();//取消动画
        } else {
            mLoadDialog.dismiss();//取消动画
            Toast.makeText(getActivity(), bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(getActivity(), bean.getStatus());
        }
    }

//    //广播接收内部类
//    private class MyBroadcastReciver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals("changePublisDataType")) {
////            //在android端显示接收到的广播内容
////                mLoadDialog.show();
//                robFlag = true;
//                publishPage = 0;
//                orderStatus = intent.getIntExtra("statusType", 0);
//                getServiceMsg("2", publishPage, orderStatus);//获取网络数据
//
//            }
//        }
//    }

    /**
     * MyOrder发送过来
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ChangeType(ChangTypeBean bean) {
        if (bean != null && bean.getChangeType().equals("changePublisDataType")) {
            initData(bean);
        }else if (bean != null && bean.getChangeType().equals("更新数据")){
            initData(bean);
        }
    }

    private void initData(ChangTypeBean bean) {

        robFlag = true;
        publishPage = 0;
        if (bean.getStatusType() > 0) {
            orderStatus = bean.getStatusType();
        } else {
            orderStatus = 0;
        }
        getServiceMsg("2", publishPage, orderStatus);//获取网络数据
    }

}
