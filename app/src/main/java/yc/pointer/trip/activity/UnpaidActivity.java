package yc.pointer.trip.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import yc.pointer.trip.R;
import yc.pointer.trip.adapter.UnpaidAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.base.BaseBean;
import yc.pointer.trip.bean.UnpaidBean;
import yc.pointer.trip.bean.eventbean.UpDataBean;
import yc.pointer.trip.event.DeleteBookEvent;
import yc.pointer.trip.event.UnPaidCancelOrderEvetn;
import yc.pointer.trip.event.UnpaidEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.view.DialogSure;
import yc.pointer.trip.view.LoadDialog;
import yc.pointer.trip.view.ToolbarWrapper;
import yc.pointer.trip.wxapi.PayOrderActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 未支付订单
 */
public class UnpaidActivity extends BaseActivity {
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
    @BindView(R.id.activity_unpaid)
    LinearLayout activityUnpaid;
    private long mTimestamp;//时间戳
    private String mDevcode;//手机识别码
    private String mUserId;//用户id
    private boolean mTimeDev;
    private int page = 0;
    private List<UnpaidBean.DataBean> mDataList = new ArrayList<>();
    private boolean refreshFlag = true;
    private UnpaidAdapter unpaidAdapter;
    private LoadDialog mLoadDialog;

    @BindView(R.id.verify_title)
    TextView verifyTitle;
    @BindView(R.id.button_go_verify)
    Button buttonGoVerify;
    @BindView(R.id.note_go_verify)
    TextView noteGoVerify;
    @BindView(R.id.go_verify)
    LinearLayout goVerify;
    //private MyBroadcastReciver receiver;


    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_unpaid;
    }

    @Override
    protected void initView() {

        new ToolbarWrapper(this).setCustomTitle(R.string.unpaid_title);

        ////注册广播
        //IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction("更新数据");
        //receiver = new MyBroadcastReciver();
        //registerReceiver(receiver, intentFilter);


        String is_jie = ((MyApplication) getApplication()).getUserBean().getIs_jie();

        if (is_jie.equals("2")) {
            //开启动画
            mLoadDialog = new LoadDialog(this, "正在加载...", R.drawable.load_animation);
            mLoadDialog.show();
            refreshSmart.setVisibility(View.VISIBLE);

            goVerify.setVisibility(View.GONE);
        } else if (is_jie.equals("1")) {
            //审核中
            showVierfy("您的认证信息正在审核中", "点击查看", "未支付订单");
        } else if (is_jie.equals("3")) {
            //审核失败
            showVierfy("您提交的认证信息审核未通过", "重新认证", "未支付订单");
        } else {
            showVierfy("您尚未完成指针认证", "马上认证", "未支付订单");
        }



        refreshRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mDevcode = ((MyApplication) getApplication()).getDevcode();
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();
        mUserId = ((MyApplication) getApplication()).getUserId();
        mTimeDev = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);

        getServiceMsg(mTimeDev, page);//请求网络数据
        unpaidAdapter = new UnpaidAdapter(mDataList);//适配器
        refreshRecycler.setAdapter(unpaidAdapter);
        unpaidAdapter.setItemViewOnClickListener(new UnpaidAdapter.itemViewOnClickListener() {
            @Override
            public void OnItemClickBack(String oid) {
                Intent intentOrderDetail = new Intent(UnpaidActivity.this, OrderDetailActivity.class);
                intentOrderDetail.putExtra("oid", oid);
                intentOrderDetail.putExtra("flag", "pay");
                startActivity(intentOrderDetail);
            }

            @Override
            public void onButtonClickBack(final String oid, int resId) {
                switch (resId) {
                    case R.id.unpaid_pay://支付
                        Intent intentPayOrder = new Intent(UnpaidActivity.this, PayOrderActivity.class);
                        intentPayOrder.putExtra("oid", oid);
                        startActivity(intentPayOrder);
                        break;
                    case R.id.cancel_order://删除订单
                        new DialogSure(UnpaidActivity.this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
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
    }

    /**
     * 显示认证的方法
     */
    private void showVierfy(String title, String buttontext, String note) {
        refreshSmart.setVisibility(View.GONE);
        goVerify.setVisibility(View.VISIBLE);
        verifyTitle.setText(title);
        buttonGoVerify.setText(buttontext);
        String text = String.format(getResources().getString(R.string.note_verify), note);
        noteGoVerify.setText(text);
        buttonGoVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UnpaidActivity.this, VerifyActivity.class);
                intent.putExtra("goVerify", "unPaid");
                startActivity(intent);
            }
        });
    }

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
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        UnpaidBean bean = event.getData();
        if (bean.getStatus() == 0) {
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
                    refreshSmart.finishRefresh();
                }
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
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
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
            mLoadDialog.show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void delOrderBean(UnPaidCancelOrderEvetn event) {

        if (event.isTimeOut()) {
            mLoadDialog.dismiss();//取消动画
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        BaseBean bean = event.getData();
        if (bean.getStatus() == 0) {

            mDataList.clear();
            page = 0;
            getServiceMsg(mTimeDev, page);
            mLoadDialog.dismiss();//取消动画
        } else {
            mLoadDialog.dismiss();//取消动画
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }

    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

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
//                unregisterReceiver(this);
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
