package yc.pointer.trip.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import okhttp3.FormBody;
import okhttp3.RequestBody;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import yc.pointer.trip.R;
import yc.pointer.trip.adapter.CouponAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.base.BaseBean;
import yc.pointer.trip.bean.CouponBean;
import yc.pointer.trip.bean.eventbean.ReceiverBean;
import yc.pointer.trip.bean.eventbean.UseCouponBean;
import yc.pointer.trip.event.CouponEvent;
import yc.pointer.trip.event.CouponUseEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.DialogSure;
import yc.pointer.trip.view.LoadDialog;
import yc.pointer.trip.view.ToolbarWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 刘佳伟
 * 2017/7/21
 * 11:06
 * 指针大礼包
 */
public class CouponActivity extends BaseActivity {

    @BindView(R.id.refresh_recycler)
    RecyclerView refreshRecycler;
    @BindView(R.id.refresh_smart)
    SmartRefreshLayout refreshSmart;//刷新
    @BindView(R.id.adapter_empty)
    TextView adapterEmpty;
    @BindView(R.id.empty_img)
    ImageView couponEmpty;
    @BindView(R.id.empty)
    LinearLayout empty;
    @BindView(R.id.standard_toolbar)
    Toolbar standardToolbar;

    private List<String> data;
    private long mTimestamp;//时间戳
    private String mDevcode;//手机识别码
    private int page = 0;
    private boolean flag = true;//true-刷新  false-加载
    private String mUserId;
    private CouponAdapter couponAdapter;
    private List<CouponBean.DataBean> mListBean = new ArrayList<>();
    private LoadDialog mLoadDialog;
    private ToolbarWrapper toolbarWrapper;
    //private MyBroadcastReciver myBroadcastReciver;


    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_my_coupon;
    }

    @Override
    protected void initView() {

        //开启动画
        mLoadDialog = new LoadDialog(this, "正在加载...", R.drawable.load_animation);
        mLoadDialog.show();
        toolbarWrapper = new ToolbarWrapper(this).setCustomTitle(R.string.my_coupon);
        standardToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent1 = new Intent();
                //intent1.setAction("刷新");
                //intent1.putExtra("receiver", "1");
                //sendBroadcast(intent1);
                EventBus.getDefault().post(new ReceiverBean("1"));
                finish();
            }
        });
        mUserId = ((MyApplication) getApplication()).getUserId();
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();
        mDevcode = ((MyApplication) getApplication()).getDevcode();

        getCouponMsg(page);//请求优惠券信息
        refreshRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        refreshSmart.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                flag = false;
                ++page;
                getCouponMsg(page);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshSmart.setLoadmoreFinished(false);
                flag = true;
                page = 0;
                getCouponMsg(page);
            }
        });
        //TODO 优惠券使用逻辑没做
        couponAdapter = new CouponAdapter(mListBean, new CouponAdapter.CallBackCoupon() {
            @Override
            public void onClick(int position) {
                String url = mListBean.get(position).getUrl();
                String title = mListBean.get(position).getTitle();
                if (!StringUtil.isEmpty(url) && !StringUtil.isEmpty(title)) {
                    Intent intent = new Intent(CouponActivity.this, UseCouponAvtivity.class);
                    intent.putExtra("title", title);
                    intent.putExtra("url", url);
                    startActivity(intent);
                } else {
                    final String id = mListBean.get(position).getId();
                    if (!StringUtil.isEmpty(id)) {
                        new DialogSure(CouponActivity.this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                            @Override
                            public void onClickListener(DialogSure dialogSure, boolean cancelable) {
                                if (cancelable) {
                                    //使用优惠券，请求网络
                                    useCoupon(id);
                                }
                            }
                        }).setTitle("温馨提示")
                                .setMsg("确认使用该礼券吗？")
                                .setPositiveButton("确定")
                                .setNegativeButton("取消").show();
                    }

                }

            }
        });
        refreshRecycler.setAdapter(couponAdapter);

        //注册广播
        //IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction("useCoupon");
        //myBroadcastReciver = new MyBroadcastReciver();
        //registerReceiver(myBroadcastReciver, intentFilter);

    }

    /**
     * 获取所有优惠券信息
     *
     * @param page 分页
     */
    private void getCouponMsg(int page) {
        boolean timeDev = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
        if (timeDev) {
            String signature = Md5Utils.createMD5("devcode=" + mDevcode + "p=" + page + "timestamp=" + mTimestamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", mDevcode)
                    .add("p", String.valueOf(page))
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", mUserId)
                    .add("signature", signature)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.GET_COUPON, requestBody, new HttpCallBack(new CouponEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getCouponMsg(CouponEvent event) {
        if (event.isTimeOut()) {
            mLoadDialog.dismiss();//取消动画
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        CouponBean bean = event.getData();
        if (bean.getStatus() == 0) {

            if (flag) {
                if (bean.getData().size() == 0) {
                    refreshRecycler.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                    adapterEmpty.setText("暂无优惠礼券可用");
                    couponEmpty.setImageResource(R.mipmap.no_coupon);
                } else {
                    mListBean.clear();
                    refreshRecycler.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                    mListBean.addAll(bean.getData());
                    refreshRecycler.setAdapter(couponAdapter);
                }
                refreshSmart.finishRefresh();
            } else {

                if (bean.getData().size() == 0) {
                    refreshSmart.setLoadmoreFinished(true);
                } else {
                    mListBean.addAll(bean.getData());
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
     * 使用优惠券
     *
     * @param id
     */
    private void useCoupon(String id) {
        boolean timeDev = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
        if (timeDev) {
            String signature = Md5Utils.createMD5("devcode=" + mDevcode + "mid=" + id + "timestamp=" + mTimestamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", mDevcode)
                    .add("mid", id)
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", mUserId)
                    .add("signature", signature)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.USE_COUPON, requestBody, new HttpCallBack(new CouponUseEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void useCoupon(CouponUseEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        BaseBean bean = event.getData();
        if (bean.getStatus() == 0) {
            flag = true;
            getCouponMsg(0);
        } else {
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
//            if (action.equals("useCoupon")) {
//                initView();
//
////            //在android端显示接收到的广播内容
////            Toast.makeText(getActivity(), author, 1).show();
//            }
//        }
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void useCoupon(UseCouponBean bean) {
        if (bean!=null&&bean.getUseCoupon().equals("useCoupon")) {
            initView();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            //Intent intent1 = new Intent();
            //intent1.setAction("刷新");
            //intent1.putExtra("receiver", "1");
            //sendBroadcast(intent1);
            EventBus.getDefault().post(new ReceiverBean("1"));
            finish();

            return true;

        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在结束时可取消广播
        //unregisterReceiver(myBroadcastReciver);
    }
}
