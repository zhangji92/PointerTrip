package yc.pointer.trip.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import yc.pointer.trip.R;
import yc.pointer.trip.adapter.AdapterActivityTrip;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.ActivityTripBean;
import yc.pointer.trip.bean.SetServiceWrateBean;
import yc.pointer.trip.event.ActivityTripEvent;
import yc.pointer.trip.event.SetServiceWrateEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.SharedPreferencesUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.DialogSure;
import yc.pointer.trip.view.ToolbarWrapper;

/**
 * Created by 张继 on 2017/11/21.
 * 活动页面
 */

public class ActivityTrip extends BaseActivity {
    @BindView(R.id.refresh_recycler)
    RecyclerView refreshRecycler;
    @BindView(R.id.refresh_smart)
    SmartRefreshLayout refreshSmart;
    private String mUserId;
    private long mTimestamp;
    private String mDevcode;
    private AdapterActivityTrip adapter;
    private ActivityTripBean.DataBean.DataGoodBean dataGoodBean;
    private List<ActivityTripBean.DataBean.DataAdBean> mListAd = new ArrayList<>();
    private List<ActivityTripBean.DataBean.DataAllBean> mListAll = new ArrayList<>();
    private boolean refreshFlag = true;
    private int page = 0;//分页


    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_trip;
    }

    @Override
    protected void initView() {
        new ToolbarWrapper(this).setCustomTitle(R.string.activity_trip);
        mUserId = ((MyApplication) getApplication()).getUserId();
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();
        mDevcode = ((MyApplication) getApplication()).getDevcode();
        if (!StringUtil.isEmpty(mUserId)) {
            getActivityMsg(mUserId, page);
        } else {
            mUserId = "0";
            getActivityMsg(mUserId, page);
//            Toast.makeText(this, "登陆异常", Toast.LENGTH_SHORT).show();
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        refreshRecycler.setLayoutManager(layoutManager);
        adapter = new AdapterActivityTrip(dataGoodBean, mListAd, mListAll);
        refreshRecycler.setAdapter(adapter);

        refreshSmart.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshFlag = false;
                ++page;
                getActivityMsg(mUserId, page);
                refreshSmart.finishLoadmore();
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                dataGoodBean = null;
                mListAd.clear();
                mListAll.clear();
                refreshSmart.setLoadmoreFinished(false);
                refreshFlag = true;
                page = 0;
                getActivityMsg(mUserId, page);
                refreshSmart.finishRefresh();
            }
        });
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

    private void getActivityMsg(String uid, int page) {
        boolean timeDev = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
        if (timeDev) {
            String signature = Md5Utils.createMD5("devcode=" + mDevcode + "p=" + page + "timestamp=" + mTimestamp + "uid=" + uid + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", mDevcode)
                    .add("p", String.valueOf(page))
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", uid)
                    .add("signature", signature)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.ACTIVITY_TRIP, requestBody, new HttpCallBack(new ActivityTripEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setServiceVideo(ActivityTripEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityTripBean bean = event.getData();
        if (bean.getStatus() == 0) {
            if (refreshFlag) {
                dataGoodBean = bean.getData().getData_good();
                mListAd.addAll(bean.getData().getData_ad());
                mListAll.addAll(bean.getData().getData_all());
                adapter = new AdapterActivityTrip(dataGoodBean, mListAd, mListAll);
                refreshRecycler.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } else {
                if (bean.getData().getData_all().size() == 0) {
                    refreshSmart.setLoadmoreFinished(true);
                } else {
                    mListAll.addAll(bean.getData().getData_all());
                }
                adapter.notifyDataSetChanged();
                refreshSmart.finishLoadmore();
            }
        } else if (bean.getStatus() == 201) {
            new DialogSure(this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                @Override
                public void onClickListener(DialogSure dialogSure, boolean trueEnable) {
                    ((MyApplication) getApplication()).setUserBean(null);
                    ((MyApplication) getApplication()).setIslogin(false);
                    ((MyApplication) getApplication()).setUserId("");
                    SharedPreferencesUtils.getInstance().remove(ActivityTrip.this, "phone");
                    SharedPreferencesUtils.getInstance().remove(ActivityTrip.this, "pad");
                    SharedPreferencesUtils.getInstance().remove(ActivityTrip.this, "useId");
                    if (trueEnable) {
                        startActivity(new Intent(ActivityTrip.this, LoginActivity.class));
                    } else {
                        mUserId = "0";
                        page = 0;
                        getActivityMsg(mUserId, page);
                    }
                }
            }).setMsg("您的账号在其他设备登录，如不是本人操作，请尽快前去修改密码")
                    .setPositiveButton("去登录")
                    .setNegativeButton("不用了")
                    .show();
        } else {
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
        }
    }

}
