package yc.pointer.trip.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import yc.pointer.trip.R;
import yc.pointer.trip.adapter.IncomeDetailsAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.IncomeDetailsBean;
import yc.pointer.trip.event.IncomeDetailsEvent;
import yc.pointer.trip.event.MyOrderGrabEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.view.ToolbarWrapper;

/**
 * Created by moyan on 2018/4/24.
 * 收益分类详情
 */

public class IncomeDetailsActivity extends BaseActivity {
    @BindView(R.id.standard_toolbar_title)
    TextView standardToolbarTitle;
    @BindView(R.id.satandard_toolbar_right)
    TextView satandardToolbarRight;
    @BindView(R.id.standard_toolbar)
    Toolbar standardToolbar;
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


    private int type;
    private int page = 0;
    private boolean isRefresh = true;
    private String mDevcode;//设备识别码
    private String mUserId;//用户id
    private long mTimestamp;//时间戳

    private List<IncomeDetailsBean.DataBean> dataList = new ArrayList<>();
    private IncomeDetailsBean.EarningsBean earningsBean = new IncomeDetailsBean.EarningsBean();//累计金额  剩余金额的对象
    private IncomeDetailsAdapter adapter;

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_income_details_layout;
    }

    @Override
    protected void initView() {

        ToolbarWrapper toolbarWrapper = new ToolbarWrapper(this);
        String title = getIntent().getStringExtra("title");
        type = getIntent().getIntExtra("type", 0);
        standardToolbarTitle.setText(title);

        mDevcode = ((MyApplication) getApplication()).getDevcode();
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();
        mUserId = ((MyApplication) getApplication()).getUserId();
        //获取网络数据
        getDataFormNet(page);

        refreshRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        refreshSmart.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                //加载
                isRefresh = false;
                ++page;
                getDataFormNet(page);
                refreshSmart.finishLoadmore();

            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                isRefresh=true;
                page=0;
                refreshSmart.setLoadmoreFinished(false);
                getDataFormNet(page);
                refreshSmart.finishRefresh();
            }
        });


    }

    @Override
    protected boolean needEventBus() {
        return true;
    }


    private void getDataFormNet(int page) {
        boolean timeFlag = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
        if (timeFlag) {
            String sign = Md5Utils.createMD5("devcode=" + mDevcode + "p=" + page + "timestamp=" + mTimestamp + "type=" + type + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", mDevcode)
                    .add("p", String.valueOf(page))
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("type", String.valueOf(type))
                    .add("uid", mUserId)
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.INCOME_DETAILS, requestBody, new HttpCallBack(new IncomeDetailsEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getData(IncomeDetailsEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络错误", Toast.LENGTH_SHORT).show();
            return;
        }
        IncomeDetailsBean data = event.getData();
        if (data.getStatus() == 0) {
            earningsBean = data.getEarnings();
            if (isRefresh) {
                //刷新
                if (data.getEarnings() == null || data.getData().size() == 0) {
                    refreshRecycler.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                    adapterEmpty.setText("哎呀~您还没有收益入账");
                    emptyImg.setImageResource(R.mipmap.no_income);
                } else {
                    refreshRecycler.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                    dataList.clear();
                    dataList.addAll(data.getData());
                }
                adapter = new IncomeDetailsAdapter(dataList, earningsBean);
                refreshRecycler.setAdapter(adapter);
            } else {
                //加载
                if (data.getData().size() == 0) {
                    refreshSmart.setLoadmoreFinished(true);
                } else {
                    dataList.addAll(data.getData());
                }
            }
            refreshSmart.finishLoadmore();
            adapter.notifyDataSetChanged();

        }
    }
}
