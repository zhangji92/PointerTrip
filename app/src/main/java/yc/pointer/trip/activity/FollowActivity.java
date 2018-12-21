package yc.pointer.trip.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
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
import yc.pointer.trip.adapter.FansAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.FansBean;
import yc.pointer.trip.bean.eventbean.ReceiverBean;
import yc.pointer.trip.event.FollowEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.SharedPreferencesUtils;
import yc.pointer.trip.untils.StringUtil;

/**
 * Created by 张继
 * 2018/1/12
 * 17:22
 * 关注
 */

public class FollowActivity extends BaseActivity implements FansAdapter.FansCallBack{

    @BindView(R.id.fans_recycler)
    RecyclerView refreshRecycler;
    @BindView(R.id.fans_smart)
    SmartRefreshLayout refreshSmart;
    @BindView(R.id.standard_toolbar)
    Toolbar standardToolbar;
    @BindView(R.id.standard_toolbar_title)
    TextView standardToolbarTitle;
    @BindView(R.id.empty_img)
    ImageView emptyImg;
    @BindView(R.id.adapter_empty)
    TextView adapterEmpty;
    @BindView(R.id.empty)
    LinearLayout empty;
    private String mUserId;
    private String mDevcode;
    private long mTimestamp;
    private int page = 0;
    private FansAdapter adapter;
    private boolean mRefreshFlag = true;
    private String mUuid;
    private List<FansBean.DataBean> mList;

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_fans;
    }

    @Override
    protected void initView() {
        standardToolbarTitle.setText("关注");
        standardToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent1 = new Intent();
                //intent1.setAction("刷新");
                //sendBroadcast(intent1);
                EventBus.getDefault().post(new ReceiverBean("1"));
                finish();
            }
        });


        mList = new ArrayList<>();
        mUserId = SharedPreferencesUtils.getInstance().getString(this, "useId");
        if (StringUtil.isEmpty(mUserId) || mUserId.equals("not find")) {
            mUserId = "0";
        }
        mUuid = getIntent().getStringExtra("uuid");
        mDevcode = ((MyApplication) getApplication()).getDevcode();
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();
        getMsg(mUserId, mDevcode, mTimestamp, page, mUuid);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        refreshRecycler.setLayoutManager(linearLayoutManager);
        adapter = new FansAdapter(mList,this);
        refreshRecycler.setAdapter(adapter);
        refreshSmart.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                ++page;
                mRefreshFlag = false;
                getMsg(mUserId, mDevcode, mTimestamp, page, mUuid);

            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 0;
                mRefreshFlag = true;
                getMsg(mUserId, mDevcode, mTimestamp, page, mUuid);
            }
        });
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

    private void getMsg(String uid, String devcode, long timestamp, int page, String uuid) {
        if (APPUtils.judgeTimeDev(this, devcode, timestamp)) {
            String sign = Md5Utils.createMD5("devcode=" + devcode + "p=" + page + "timestamp=" + timestamp + "type=" + 1 + "uid=" + uid + "uuid=" + uuid + URLUtils.WK_APP_KEY);
            RequestBody params = new FormBody.Builder()
                    .add("devcode", devcode)
                    .add("p", String.valueOf(page))
                    .add("timestamp", String.valueOf(timestamp))
                    .add("type", String.valueOf(1))
                    .add("uid", uid)
                    .add("uuid", uuid)
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.PERSONAL_FOLLOW_LIST, params, new HttpCallBack(new FollowEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMsg(FollowEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        FansBean bean = event.getData();
        if (bean.getStatus() == 0) {
            if (mRefreshFlag) {
                if (bean.getData().size() == 0) {
                    refreshRecycler.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                    emptyImg.setImageResource(R.mipmap.fans_no_data);
                    adapterEmpty.setText("这个人很懒，没有关注");
                } else {
                    empty.setVisibility(View.GONE);
                    refreshRecycler.setVisibility(View.VISIBLE);
                }
                mList.clear();
                mList.addAll(bean.getData());
                adapter.notifyDataSetChanged();
                refreshSmart.finishRefresh();
            } else {
                if (bean.getData().size() > 0) {
                    refreshSmart.setLoadmoreFinished(true);
                } else {
                    mList.addAll(bean.getData());
                    adapter.notifyDataSetChanged();
                }
                refreshSmart.finishLoadmore();
            }
        } else {
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //Intent intent1 = new Intent();
            //intent1.setAction("刷新");
            //sendBroadcast(intent1);
            EventBus.getDefault().post(new ReceiverBean("1"));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void fans(String uid) {
//        Intent intent = new Intent(this, PersonalPageActivity.class);
//        intent.putExtra("uid", uid);
//        startActivity(intent);
    }
}
