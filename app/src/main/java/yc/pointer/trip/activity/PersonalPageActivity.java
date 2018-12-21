package yc.pointer.trip.activity;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Button;
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
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import yc.pointer.trip.R;
import yc.pointer.trip.adapter.PersonalPageAdapter;
import yc.pointer.trip.adapter.PersonalPageItemDecoration;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.base.BaseBean;
import yc.pointer.trip.bean.PersonalPageBean;
import yc.pointer.trip.bean.PersonalPageFollowBean;
import yc.pointer.trip.bean.eventbean.ReceiverBean;
import yc.pointer.trip.event.PersonalPageEvent;
import yc.pointer.trip.event.PersonalPageFollowEvent;
import yc.pointer.trip.event.RoadDeleteDataEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.SharedPreferencesUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.CustomCircleImage;
import yc.pointer.trip.view.DialogSure;
import yc.pointer.trip.view.LoadDialog;
import yc.pointer.trip.view.ToolbarWrapper;

/**
 * Created by 张继
 * 2018/1/3
 * 14:42
 * 个人主页
 */

public class PersonalPageActivity extends BaseActivity implements PersonalPageAdapter.PersonalPageOnCallBack {
    @BindView(R.id.personal_recycler)
    RecyclerView refreshRecycler;
    @BindView(R.id.personal_smart)
    SmartRefreshLayout refreshSmart;
    @BindView(R.id.personal_head_back)
    ImageView personalHeadBack;
    @BindView(R.id.personal_head_head)
    CustomCircleImage personalHeadHead;
    @BindView(R.id.personal_head_edit)
    ImageView personalHeadEdit;
    @BindView(R.id.personal_head_nick)
    TextView personalHeadNick;
    @BindView(R.id.personal_head_autograph)
    TextView personalHeadAutograph;
    @BindView(R.id.personal_head_local)
    TextView personalHeadLocal;
    @BindView(R.id.personal_page_linear)
    LinearLayout personalPageLinear;
    @BindView(R.id.personal_page_refresh)
    Button personalPageRefresh;
    @BindView(R.id.personal_head_follow)
    TextView personalHeadFollow;
    @BindView(R.id.personal_head_fans)
    TextView personalHeadFans;
    @BindView(R.id.personal_page_road)
    ImageView personalPageRoad;
    @BindView(R.id.adapter_empty)
    TextView tvEmpty;
    @BindView(R.id.empty_img)
    ImageView imEmpty;
    @BindView(R.id.empty)
    LinearLayout llEmpty;
    private ImageView adapterImage;

    private PersonalPageAdapter adapter;
    private String mUserId;
    private String mUUid;
    private String mDevcode;
    private long mTimestamp;
    private int page = 0;
    private PersonalPageBean.UserBean userBean;
    private List<PersonalPageBean.DataBean> mList;
    private boolean refreshLoadFlag = true;
    private LoadDialog mLoadDialog;
    private String mFollowStatus;//关注状态
    private TextView adapterFanText;//粉丝数
    private int fans;
    private String nickName;
    private String pic;

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_personal_page;
    }

    @Override
    protected void initView() {
        new ToolbarWrapper(this);
        mLoadDialog = new LoadDialog(this, "正在加载...", R.drawable.load_animation);
        mLoadDialog.show();

        //IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction("刷新");
        //registerReceiver(new MyBroadcastReceiver(), intentFilter);

        mList = new ArrayList<>();
        mUserId = SharedPreferencesUtils.getInstance().getString(this, "useId");
        mUUid = getIntent().getStringExtra("uid");
        nickName = getIntent().getStringExtra("nick");
        pic = getIntent().getStringExtra("pic");

        if (!StringUtil.isEmpty(mUserId) && mUserId.equals(mUUid)) {//是否显示拍摄视频按钮
            personalPageRoad.setVisibility(View.VISIBLE);
        } else {
            personalPageRoad.setVisibility(View.GONE);
        }
        if (StringUtil.isEmpty(mUserId) || mUserId.equals("not find")) {//未登录状态
            mUserId = "0";
        }
        mDevcode = ((MyApplication) getApplication()).getDevcode();
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();
        if (!OkHttpUtils.isNetworkAvailable(this)) {//没网
            mLoadDialog.dismiss();
            refreshRecycler.setVisibility(View.GONE);
            personalPageLinear.setVisibility(View.VISIBLE);
            personalHeadNick.setText(nickName);
            personalHeadAutograph.setText("加载中");
            personalHeadLocal.setText("火星");
            personalHeadEdit.setEnabled(false);
//            OkHttpUtils.displayGlideCircular(this, personalHeadHead, pic);
            OkHttpUtils.displayGlideVague(this, personalHeadBack, pic);
            personalHeadFollow.setText("关注  0");
            personalHeadFans.setText("粉丝  0");
        } else {//有网络
            refreshRecycler.setVisibility(View.VISIBLE);
            personalPageLinear.setVisibility(View.GONE);
            getServiceMsg(mUserId, mDevcode, mTimestamp, page);
        }
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        //layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        //layoutManager.setAutoMeasureEnabled(true);
        RecyclerView.RecycledViewPool pool=new RecyclerView.RecycledViewPool();
        pool.setMaxRecycledViews(0,2);
        refreshRecycler.setRecycledViewPool(pool);
        refreshRecycler.setPadding(0, 0, 0, 0);
        layoutManager.invalidateSpanAssignments();
        refreshRecycler.setLayoutManager(layoutManager);
        refreshRecycler.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                layoutManager.invalidateSpanAssignments();
                recyclerView.invalidateItemDecorations();//强制刷新下item的间隙
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                recyclerView.invalidateItemDecorations();//强制刷新下item的间隙
            }
        });
        adapter = new PersonalPageAdapter(this, this);
        adapter.setUserBean(userBean);
        adapter.setList(mList, true);
        adapter.setUserId(mUserId);
        refreshRecycler.setAdapter(adapter);
        refreshRecycler.addItemDecoration(new PersonalPageItemDecoration(10));
        refreshRecycler.setItemAnimator(null);


        refreshSmart.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                //加载
                refreshLoadFlag = false;
                ++page;
                getServiceMsg(mUserId, mDevcode, mTimestamp, page);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {

                //刷新
                refreshLoadFlag = true;
                refreshSmart.setLoadmoreFinished(false);//设置之后，将不会再触发加载事件
                page = 0;
                getServiceMsg(mUserId, mDevcode, mTimestamp, page);

            }
        });
    }

    private void getServiceMsg(String userId, String devcode, long timestamp, int page) {
        if (APPUtils.judgeTimeDev(this, devcode, timestamp)) {
            String sign = Md5Utils.createMD5("devcode=" + devcode + "p=" + page + "timestamp=" + timestamp + "uid=" + userId + "uuid=" + mUUid + URLUtils.WK_APP_KEY);
            RequestBody params = new FormBody.Builder()
                    .add("devcode", mDevcode)
                    .add("p", String.valueOf(page))
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", userId)
                    .add("uuid", mUUid)
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.PERSONAL_PAGE, params, new HttpCallBack(new PersonalPageEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getServiceMsg(PersonalPageEvent event) {
        if (event.isTimeOut()) {
            refreshSmart.finishRefresh();
            refreshSmart.finishLoadmore();
            mLoadDialog.dismiss();
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        PersonalPageBean bean = event.getData();
        if (bean.getStatus() == 0) {
            personalHeadEdit.setEnabled(true);
            refreshRecycler.setVisibility(View.VISIBLE);
            personalPageLinear.setVisibility(View.GONE);
            if (refreshLoadFlag) {
                if (bean.getData().size() == 0) {
                    llEmpty.setVisibility(View.VISIBLE);
                    tvEmpty.setText("您还没有发表游记，赶快去发布你的眼中的美景吧");
                    imEmpty.setImageResource(R.mipmap.no_book);
                } else {
                    llEmpty.setVisibility(View.GONE);
                }
                mList.clear();
                userBean = null;
                userBean = bean.getUser();
                mList.addAll(bean.getData());
                adapter.setUserBean(userBean);
                this.mFollowStatus = userBean.getC_status();
                fans = Integer.valueOf(userBean.getFans());
                adapter.setList(mList, true);
                adapter.notifyDataSetChanged();
                refreshSmart.finishRefresh();
            } else {
                if (bean.getData().size() == 0) {
                    refreshSmart.finishLoadmore();//停止加载
                    refreshSmart.setLoadmoreFinished(true);//设置之后，将不会再触发加载事件
                } else {
                    //添加适配器
                    mList.addAll(bean.getData());
                    adapter.setList(mList, false);
                    //添加适配器
                    adapter.notifyDataSetChanged();
//                    adapter.notifyItemInserted(1);
                    refreshSmart.finishLoadmore();//停止加载
                }
            }

            mLoadDialog.dismiss();
        } else {
            refreshSmart.finishRefresh();
            refreshSmart.finishLoadmore();
            mLoadDialog.dismiss();
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

    @Override
    public void onClickVideo(PersonalPageBean.DataBean dataBean) {
        Intent intent = new Intent(PersonalPageActivity.this, VideoDetailsActivity.class);
        intent.putExtra("dataGoodBean", dataBean);
        startActivity(intent);
    }


    @Override
    public void onClickFollow(String uuid, ImageView imageView, TextView textView) {
        this.adapterImage = imageView;
        this.adapterFanText = textView;
        imageView.setEnabled(false);
        //this.mFansStatus = status;
        if (!StringUtil.isEmpty(mUserId) && mUserId.equals(uuid)) {
            startActivity(new Intent(PersonalPageActivity.this, PersonMessageActivity.class));
        } else {
            if (!StringUtil.isEmpty(mUserId) && !mUserId.equals("0")) {
                if (this.mFollowStatus.equals("0")) {//已关注
                    postFollow(mUserId, mDevcode, mTimestamp, uuid, 1);
                } else {//为关注
                    postFollow(mUserId, mDevcode, mTimestamp, uuid, 0);
                }
            } else {
                imageView.setEnabled(true);
                Intent intent = new Intent(PersonalPageActivity.this, LoginActivity.class);
                intent.putExtra("logFlag", "personal");
                startActivityForResult(intent, 1);
            }

        }
    }

    @Override
    public void onLongClick(final String bid) {//长按删除路数
        new DialogSure(PersonalPageActivity.this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
            @Override
            public void onClickListener(DialogSure dialogSure, boolean cancelable) {
                if (cancelable) {
                    deleteBook(bid);
                }
            }
        }).setTitle("温馨提示").setMsg("删除路书？").setPositiveButton("确定").setNegativeButton("取消").show();
    }

    /**
     * 删除路数
     *
     * @param bid 路数的id
     */
    private void deleteBook(String bid) {
        boolean timeFlag = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
        if (timeFlag) {
            String sign = Md5Utils.createMD5("bid=" + bid + "devcode=" + mDevcode + "timestamp=" + mTimestamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", String.valueOf(mUserId))
                    .add("bid", String.valueOf(bid))
                    .add("signature", sign)
                    .add("devcode", mDevcode)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.DELETE_BOOK, requestBody, new HttpCallBack(new RoadDeleteDataEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void cancelCollect(RoadDeleteDataEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络异常，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }
        BaseBean bean = event.getData();
        if (bean.getStatus() == 0) {
            page = 0;
            refreshLoadFlag = true;
            getServiceMsg(mUserId, mDevcode, mTimestamp, page);
        } else {
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == 12) {
                mUserId = ((MyApplication) getApplication()).getUserId();
            }
        }
    }

    private void postFollow(String userId, String devcode, long timestamp, String uuid, int status) {
        if (APPUtils.judgeTimeDev(this, devcode, timestamp)) {
            String sign = Md5Utils.createMD5("devcode=" + mDevcode + "status=" + status + "timestamp=" + mTimestamp + "uid=" + userId + "uuid=" + uuid + URLUtils.WK_APP_KEY);
            RequestBody params = new FormBody.Builder()
                    .add("devcode", mDevcode)
                    .add("status", String.valueOf(status))
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", userId)
                    .add("uuid", uuid)
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.PERSONAL_FOLLOW, params, new HttpCallBack(new PersonalPageFollowEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void postFollow(PersonalPageFollowEvent event) {
        if (event.isTimeOut()) {
            adapterImage.setEnabled(true);
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        PersonalPageFollowBean data = event.getData();
        if (data.getStatus() == 0) {
            adapterImage.setEnabled(true);
            if (data.getC_status() == 1) {
                ++fans;
                adapterFanText.setText("粉丝  " + fans);
                adapterImage.setImageResource(R.mipmap.personal_page_follow_yes);
            } else {
                --fans;
                adapterFanText.setText("粉丝  " + fans);
                adapterImage.setImageResource(R.mipmap.personal_page_follow_no);
            }
            this.mFollowStatus = String.valueOf(data.getC_status());
        } else {
            adapterImage.setEnabled(true);
            Toast.makeText(this, data.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, data.getStatus());
        }
    }

    @OnClick({R.id.personal_page_refresh, R.id.personal_page_road})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.personal_page_refresh:
                page = 0;
                refreshLoadFlag = true;
                getServiceMsg(mUserId, mDevcode, mTimestamp, page);
                break;
            case R.id.personal_page_road:
                startActivity(new Intent(PersonalPageActivity.this, RecordVideoActivity.class));
                break;
        }

    }

    //private class MyBroadcastReceiver extends BroadcastReceiver {
    //    @Override
    //    public void onReceive(Context context, Intent intent) {
    //        String action = intent.getAction();
    //        String receiver = intent.getStringExtra("receiver");
    //        if (action.equals("刷新")) {
    //            if (!StringUtil.isEmpty(receiver) && receiver.equals("1")) {
    //                mUserId = ((MyApplication) getApplication()).getUserId();
    //            } else {
    //                initView();
    //            }
    //        }
    //        unregisterReceiver(this);
    //    }
    //}

    /**
     * bind change collection coupon fans follow login newPerson system ver 等activity发送过来
     * @param receiverBean receiver 是1
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(ReceiverBean receiverBean) {
        if (receiverBean != null && receiverBean.equals("1")) {
            initView();
        } else {
            finish();
        }
    }
}

