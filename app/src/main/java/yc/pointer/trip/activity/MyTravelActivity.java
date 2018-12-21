package yc.pointer.trip.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.*;
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
import yc.pointer.trip.adapter.MyTravelAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.MyTravelBean;
import yc.pointer.trip.event.MyTravelEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.view.LoadDialog;
import yc.pointer.trip.view.ToolbarWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 刘佳伟
 * 2017/7/21
 * 18:04
 * 我的旅行
 */
public class MyTravelActivity extends BaseActivity {
    @BindView(R.id.check_myorder)
    CheckBox checkMyorder;
    @BindView(R.id.refresh_recycler)
    RecyclerView refreshRecycler;
    @BindView(R.id.refresh_smart)
    SmartRefreshLayout refreshSmart;
    @BindView(R.id.adapter_empty)
    TextView adapterEmpty;
    @BindView(R.id.empty_img)
    ImageView travelEmpty;
    @BindView(R.id.empty)
    LinearLayout empty;
    private String mDevcode;//设备识别码
    private String mUserId;//用户id
    private long mTimestamp;//时间戳
    private int mProfitPage = 0;//收益分页
    private int mPayPage = 0;//开支分页
    private boolean checkFlag = true;//true收益 false支出
    private boolean mProfitFlag = true;//收益 true刷新 false加载
    private boolean mPayFlag = true;//支出 true刷新 false加载
    private List<MyTravelBean.DataBean> mProfitList = new ArrayList<>();//数据源
    private List<MyTravelBean.DataBean> mPayList = new ArrayList<>();//数据源
    private MyTravelAdapter mProfitAdapter;
    private MyTravelAdapter mPayAdapter;
    private LoadDialog mLoadDialog;

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_my_travel;
    }

    @Override
    protected void initView() {
        //开启动画
        mLoadDialog = new LoadDialog(this, "正在加载...", R.drawable.load_animation);
        mLoadDialog.show();
        new ToolbarWrapper(this).setCustomTitle(R.string.my_travel);
        mDevcode = ((MyApplication) getApplication()).getDevcode();
        mUserId = ((MyApplication) getApplication()).getUserId();
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();

        getTravelMsg(mProfitPage, "0");
        checkMyorder.setChecked(true);
        checkMyorder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    refreshSmart.setLoadmoreFinished(false);
                    mProfitFlag = true;
                    checkMyorder.setClickable(false);
                    checkFlag = true;
                    mProfitPage = 0;
                    getTravelMsg(mProfitPage, "0");
                } else {
                    refreshSmart.setLoadmoreFinished(false);
                    mPayFlag = true;
                    checkMyorder.setClickable(false);
                    checkFlag = false;
                    mPayPage = 0;
                    getTravelMsg(mPayPage, "1");
                }
            }
        });


        refreshRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        refreshSmart.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                if (checkFlag) {
                    ++mProfitPage;
                    mProfitFlag = false;
                    getTravelMsg(mProfitPage, "0");
                } else {
                    ++mPayPage;
                    mPayFlag = false;
                    getTravelMsg(mPayPage, "1");
                }
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshSmart.setLoadmoreFinished(false);
                if (checkFlag) {
                    mProfitPage = 0;
                    mProfitFlag = true;
                    getTravelMsg(mProfitPage, "0");
                } else {
                    mPayPage = 0;
                    mPayFlag = true;
                    getTravelMsg(mPayPage, "1");
                }
            }
        });
        mProfitAdapter = new MyTravelAdapter(mProfitList);
        refreshRecycler.setAdapter(mProfitAdapter);
        mPayAdapter = new MyTravelAdapter(mPayList);
        refreshRecycler.setAdapter(mPayAdapter);


    }

    /**
     * 获取网络数据
     *
     * @param page 分页
     * @param type 类型
     */
    private void getTravelMsg(int page, String type) {
        boolean flag = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
        if (flag) {
            String sign = Md5Utils.createMD5("devcode=" + mDevcode + "p=" + page + "timestamp=" + mTimestamp + "type=" + type + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", mDevcode)
                    .add("p", String.valueOf(page))
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("type", type)
                    .add("uid", mUserId)
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.MY_TRAVEL, requestBody, new HttpCallBack(new MyTravelEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getTravelMsg(MyTravelEvent event) {
        if (event.isTimeOut()) {
            mLoadDialog.dismiss();//取消动画
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        MyTravelBean bean = event.getData();
        if (bean.getStatus() == 0) {

            if (checkFlag) {
                if (mProfitFlag) {
                    if (bean.getData().size() == 0) {
                        refreshRecycler.setVisibility(View.GONE);
                        empty.setVisibility(View.VISIBLE);
                        adapterEmpty.setText("暂无旅行信息");
                        travelEmpty.setImageResource(R.mipmap.no_travel);
                        checkMyorder.setClickable(true);
                    } else {
                        refreshRecycler.setVisibility(View.VISIBLE);
                        empty.setVisibility(View.GONE);
                        mProfitList.clear();
                        mProfitList.addAll(bean.getData());
                        refreshRecycler.setAdapter(mProfitAdapter);
                        mProfitAdapter.notifyDataSetChanged();
                        refreshSmart.finishRefresh();
                        checkMyorder.setClickable(true);
                    }
                } else {
                    if (bean.getData().size() == 0) {
                        refreshSmart.setLoadmoreFinished(true);
                    } else {
                        mProfitList.addAll(bean.getData());
                        mProfitAdapter.notifyDataSetChanged();
                        checkMyorder.setClickable(true);
                    }
                    refreshSmart.finishLoadmore();
                }
            } else {
                if (mPayFlag) {
                    if (bean.getData().size() == 0) {
                        refreshRecycler.setVisibility(View.GONE);
                        empty.setVisibility(View.VISIBLE);
                        adapterEmpty.setText("暂无旅行信息");
                        travelEmpty.setImageResource(R.mipmap.no_travel);
                        checkMyorder.setClickable(true);
                    } else {
                        refreshRecycler.setVisibility(View.VISIBLE);
                        empty.setVisibility(View.GONE);
                        mPayList.clear();
                        mPayList.addAll(bean.getData());
                        refreshRecycler.setAdapter(mPayAdapter);
                        mPayAdapter.notifyDataSetChanged();
                        refreshSmart.finishRefresh();
                        checkMyorder.setClickable(true);
                    }
                } else {
                    if (bean.getData().size() == 0) {
                        refreshSmart.setLoadmoreFinished(true);
                    } else {
                        mPayList.addAll(bean.getData());
                        mPayAdapter.notifyDataSetChanged();
                        checkMyorder.setClickable(true);
                    }
                    refreshSmart.finishLoadmore();
                }
            }
            mLoadDialog.dismiss();//取消动画
        } else {
            mLoadDialog.dismiss();//取消动画
            APPUtils.intentLogin(this, bean.getStatus());
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected boolean needEventBus() {
        return true;
    }


}
