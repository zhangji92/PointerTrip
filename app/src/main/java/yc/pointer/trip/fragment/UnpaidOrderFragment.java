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
import yc.pointer.trip.activity.OrderDetailActivity;
import yc.pointer.trip.activity.UnpaidActivity;
import yc.pointer.trip.adapter.UnpaidAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseBean;
import yc.pointer.trip.base.BaseFragment;
import yc.pointer.trip.base.BaseViewPageFragment;
import yc.pointer.trip.bean.UnpaidBean;
import yc.pointer.trip.bean.eventbean.UpDataBean;
import yc.pointer.trip.event.UnPaidCancelOrderEvetn;
import yc.pointer.trip.event.UnpaidEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.view.DialogSure;
import yc.pointer.trip.view.LoadDialog;
import yc.pointer.trip.wxapi.PayOrderActivity;

/**
 * Created by moyan on 2018/4/20.
 * <p>
 * 未支付订单
 */

public class UnpaidOrderFragment extends BaseViewPageFragment {

    @BindView(R.id.refresh_recycler)
    RecyclerView refreshRecycler;
    @BindView(R.id.refresh_smart)
    SmartRefreshLayout refreshSmart;
    @BindView(R.id.adapter_empty)
    TextView adapterEmpty;
    @BindView(R.id.empty_img)
    ImageView orderEmpty;
    @BindView(R.id.empty)
    LinearLayout empty;


    private long mTimestamp;//时间戳
    private String mDevcode;//手机识别码
    private String mUserId;//用户id
    private boolean mTimeDev;
    private int page = 0;
    private List<UnpaidBean.DataBean> mDataList;
    private boolean refreshFlag = true;
    private boolean isLoad=true;//判断是否需要加载  true：需要加载  false：不需要加载
    private UnpaidAdapter unpaidAdapter;
    private LoadDialog mLoadDialog;

    //private MyBroadcastReciver receiver;


    @Override
    protected int getContentViewLayout() {
        return R.layout.unpaid_fragment;
    }

    @Override
    protected void initView() {
        //注册广播
        //IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction("更新数据");
        //receiver = new MyBroadcastReciver();
        //getActivity().registerReceiver(receiver, intentFilter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        refreshRecycler.setLayoutManager(layoutManager);

        mDevcode = ((MyApplication) getActivity().getApplication()).getDevcode();
        mTimestamp = ((MyApplication) getActivity().getApplication()).getTimestamp();
        mUserId = ((MyApplication) getActivity().getApplication()).getUserId();
        mTimeDev = APPUtils.judgeTimeDev(getActivity(), mDevcode, mTimestamp);

        mDataList = new ArrayList<>();

        if (mDataList.size()<=0){
            refreshRecycler.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
            adapterEmpty.setText("哎呀~您没有未支付订单");
            orderEmpty.setImageResource(R.mipmap.no_oreder);
        }
        unpaidAdapter = new UnpaidAdapter(mDataList);//适配器
        refreshRecycler.setAdapter(unpaidAdapter);

//        mLoadDialog = new LoadDialog(getActivity(), "正在加载...", R.drawable.load_animation);
//        mLoadDialog.show();


        //刷新加载
        refreshSmart.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshFlag = false;
                ++page;
                getServiceMsg(mTimeDev, page);
                refreshSmart.finishLoadmore();
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshSmart.setLoadmoreFinished(false);
                refreshFlag = true;
                page = 0;
                getServiceMsg(mTimeDev, page);
                refreshSmart.finishRefresh();
            }
        });
        unpaidAdapter.setItemViewOnClickListener(new UnpaidAdapter.itemViewOnClickListener() {
            @Override
            public void OnItemClickBack(String oid) {
                Intent intentOrderDetail = new Intent(getActivity(), OrderDetailActivity.class);
                intentOrderDetail.putExtra("oid", oid);
                intentOrderDetail.putExtra("flag", "pay");
                startActivity(intentOrderDetail);
            }

            @Override
            public void onButtonClickBack(final String oid, int resId) {
                switch (resId) {
                    case R.id.unpaid_pay://支付
                        Intent intentPayOrder = new Intent(getActivity(), PayOrderActivity.class);
                        intentPayOrder.putExtra("oid", oid);
                        startActivity(intentPayOrder);
                        break;
                    case R.id.cancel_order://删除订单
                        new DialogSure(getActivity(), R.style.user_default_dialog, new DialogSure.CallBackListener() {
                            @Override
                            public void onClickListener(DialogSure dialogSure, boolean cancelable) {
                                if (cancelable) {
                                    delOrder(mTimeDev, oid);
                                }
                            }
                        }).setTitle("温馨提示").setMsg("您确认删除该订单吗？").setPositiveButton("确定").setNegativeButton("取消").show();

                        break;
                }
            }
        });
        isPrepared = true;
        loadData();
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

    protected  void loadData(){
        if (!isVisble||!isPrepared){
            return;
        }
        if (isLoad){
            refreshFlag=true;
            mLoadDialog = new LoadDialog(getActivity(), "正在加载...", R.drawable.load_animation);
            mLoadDialog.show();
            getServiceMsg(mTimeDev, page);//请求网络数据
        }

    };


    /**
     * 获取未支付订单列表
     *
     * @param flag 判断时间戳和设备识别码的标志
     * @param page 分页
     */
    private void getServiceMsg(boolean flag, int page) {
        if (flag) {
            String sign = Md5Utils.createMD5("devcode=" + mDevcode + "p=" + page + "timestamp=" + mTimestamp + "type=" + "1" + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", mDevcode)
                    .add("p", String.valueOf(page))
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("type", "1")
                    .add("uid", mUserId)
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.UNPAID_ORDER_LIST, requestBody, new HttpCallBack(new UnpaidEvent()));

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void unPaidBean(UnpaidEvent event) {
        if (event.isTimeOut()) {
            mLoadDialog.dismiss();//取消动画
            Toast.makeText(getActivity(), "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        UnpaidBean bean = event.getData();
        if (bean.getStatus() == 0) {
            isLoad=false;
            if (refreshFlag) {
                if (bean.getData().size() == 0) {
                    refreshRecycler.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                    adapterEmpty.setText("哎呀~您没有未支付订单");
                    orderEmpty.setImageResource(R.mipmap.no_oreder);
                } else {
                    refreshRecycler.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                    mDataList.clear();
                    mDataList.addAll(bean.getData());
                    refreshRecycler.setAdapter(unpaidAdapter);
                    unpaidAdapter.notifyDataSetChanged();
                }
                refreshSmart.finishRefresh();
            } else {
                if (bean.getData().size() == 0) {
                    refreshSmart.setLoadmoreFinished(true);
                } else {
                    mDataList.addAll(bean.getData());
                    unpaidAdapter.notifyDataSetChanged();
                }
                refreshSmart.finishLoadmore();
            }
            mLoadDialog.dismiss();//取消动画
        } else {
            mLoadDialog.dismiss();//取消动画
            Toast.makeText(getActivity(), bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(getActivity(), bean.getStatus());
        }
    }

    /**
     * 删除订单
     *
     * @param flag 判断时间戳和设备识别码的标志
     * @param oid  订单编号
     */
    private void delOrder(boolean flag, String oid) {
        if (flag) {//ord_status
            String sign = Md5Utils.createMD5("devcode=" + mDevcode + "oid=" + oid + "ord_status=" + "0" + "timestamp=" + mTimestamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", mDevcode)
                    .add("oid", oid)
                    .add("ord_status", "0")
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", mUserId)
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.UNPAID_DELETE_ORDER, requestBody, new HttpCallBack(new UnPaidCancelOrderEvetn()));
//            mLoadDialog.show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void delOrderBean(UnPaidCancelOrderEvetn event) {

        if (event.isTimeOut()) {
//            mLoadDialog.dismiss();//取消动画
            Toast.makeText(getActivity(), "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        BaseBean bean = event.getData();
        if (bean.getStatus() == 0) {

            mDataList.clear();
            page = 0;
            getServiceMsg(mTimeDev, page);
//            mLoadDialog.dismiss();//取消动画
        } else {
//            mLoadDialog.dismiss();//取消动画
            Toast.makeText(getActivity(), bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(getActivity(), bean.getStatus());
        }

    }

//    @Override
//    protected void loadData() {
//        if (!isVisble || isPrepared) {
//            return;
//        }
//
//        mLoadDialog = new LoadDialog(getActivity(), "正在加载...", R.drawable.load_animation);
//        mLoadDialog.show();
//        getServiceMsg(mTimeDev, page);//请求网络数据
//    }

//    //广播接收内部类
//    private class MyBroadcastReciver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals("更新数据")) {
//                initView();
////            //在android端显示接收到的广播内容
////            Toast.makeText(getActivity(), author, 1).show();
//
//                //在结束时可取消广播
//                getActivity().unregisterReceiver(this);
//            }
//        }
//    }

    /**
     * 从ver pay wx等activity发送过来
     * @param bean
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateData(UpDataBean bean){
        if (bean!=null&&bean.getData().equals("更新数据")){
            initView();
        }
    }
}
