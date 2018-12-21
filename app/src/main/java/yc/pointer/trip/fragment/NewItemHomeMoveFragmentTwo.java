package yc.pointer.trip.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import yc.pointer.trip.R;
import yc.pointer.trip.activity.LoginActivity;
import yc.pointer.trip.activity.PersonMessageActivity;
import yc.pointer.trip.adapter.ItemHomeMoveFragmentRecyclerAdapterTwo;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseBean;
import yc.pointer.trip.base.BaseViewPageFragment;
import yc.pointer.trip.bean.BookBean;
import yc.pointer.trip.bean.HomeVideoNewDataBean;
import yc.pointer.trip.bean.PersonalPageFollowBean;
import yc.pointer.trip.bean.eventbean.FollowDotEvent;
import yc.pointer.trip.event.BaseEvent;
import yc.pointer.trip.event.ItemHomeEventTwo;
import yc.pointer.trip.event.ItemRecycleToTopEvent;
import yc.pointer.trip.event.ItemTwoRefresh;
import yc.pointer.trip.event.LoginEventRefresh;
import yc.pointer.trip.event.PersonalPageFollowEvent;
import yc.pointer.trip.event.ReportSendEvent;
import yc.pointer.trip.event.VideoDetailsToHomePagerEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.ScrollCalculatorHelper;
import yc.pointer.trip.untils.SharedPreferencesUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.HomeShareBoardDialog;
import yc.pointer.trip.view.VideoReportingDialog;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
import static yc.pointer.trip.untils.ImageUtils.getBitMBitmap;

/**
 * Created by moyan on 2018/7/23.
 * 关注用户的游记数据
 */

public class NewItemHomeMoveFragmentTwo extends BaseViewPageFragment implements HomeShareBoardDialog.ShareCallBack, ItemHomeMoveFragmentRecyclerAdapterTwo.ItemHomeMoveRecycler {

    @BindView(R.id.item_fragment_recycler_two)
    RecyclerView mRecyclerView;
    @BindView(R.id.item_fragment_smart_two)
    SmartRefreshLayout mSmartRefresh;


    private final static String TYPE = "KEY";
    private String mUserId;
    private String mDevcode;
    private long mTimestamp;
    private HomeShareBoardDialog dialog;//分享的Dialog
    private List<HomeVideoNewDataBean.DataBean.DataGoodBean> mItemTwo = new ArrayList<>();
    private ItemHomeMoveFragmentRecyclerAdapterTwo itemHomeMoveFragmentRecyclerAdapter;
    private int mPage = 0;
    private boolean mRefreshSign = true;
    private LinearLayoutManager layoutManager;
    private int mCurrentData;//当前数据标志
    private int mPosition;
    private ImageView mAttentionImg;
    private String sharePic;


    public static Fragment newInstance(int key) {

        NewItemHomeMoveFragmentTwo fragment = new NewItemHomeMoveFragmentTwo();
        Bundle bundle = new Bundle();
        bundle.putInt(TYPE, key);
        fragment.setArguments(bundle);
        return fragment;

    }


    @Override
    protected int getContentViewLayout() {

        return R.layout.fragment_item_home_move_two;

    }

    @Override
    protected void initView() {
        mCurrentData = getArguments().getInt(TYPE, 0);

        mUserId = SharedPreferencesUtils.getInstance().getString(getActivity(),"useId");
        mDevcode = ((MyApplication) getActivity().getApplication()).getDevcode();
        mTimestamp = ((MyApplication) getActivity().getApplication()).getTimestamp();

        if (StringUtil.isEmpty(mUserId)||"not find".equals(mUserId)) {
            mUserId = "0";
        }
        //分享的dialog
        dialog = new HomeShareBoardDialog(MyApplication.mApp, this, this);

        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

//        itemHomeMoveFragmentRecyclerAdapter = new ItemHomeMoveFragmentRecyclerAdapterTwo(mItemTwo);
//        itemHomeMoveFragmentRecyclerAdapter.setItemHomeMoveRecycler(this);
//        mRecyclerView.setAdapter(itemHomeMoveFragmentRecyclerAdapter);


        //滑动监听，处理滑动播放逻辑
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int firstVisibleItem, lastVisibleItem, visibleCount;
            boolean scrollState = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case SCROLL_STATE_IDLE: //滚动停止
                        scrollState = false;
                        ScrollCalculatorHelper.getInstance().playVideo(recyclerView, visibleCount, R.id.item_sample_video);
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                visibleCount = lastVisibleItem - firstVisibleItem;
                //大于0说明有播放
                if (GSYVideoManager.instance().getPlayPosition() >= 0) {
                    //当前播放的位置
                    int position = GSYVideoManager.instance().getPlayPosition();
                    //对应的播放列表TAG
                    if (GSYVideoManager.instance().getPlayTag().equals("RecyclerView2List") && (position < firstVisibleItem || position > lastVisibleItem)) {
                        //GSYVideoManager.onPause();
                        GSYVideoManager.releaseAllVideos();
                    }
                }
            }
        });

        //刷新加载逻辑
        mSmartRefresh.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                ++mPage;
                mRefreshSign = false;
                requestHomePageData(mUserId, mDevcode, mTimestamp, mPage, mCurrentData);
                mSmartRefresh.finishLoadmore();
                GSYVideoManager.releaseAllVideos();
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mItemTwo.clear();
                mRefreshSign = true;
                mPage = 0;
                mSmartRefresh.setLoadmoreFinished(false);
                requestHomePageData(mUserId, mDevcode, mTimestamp, mPage, mCurrentData);
                mSmartRefresh.finishRefresh();
                GSYVideoManager.releaseAllVideos();
            }
        });

        isPrepared = true;
        loadData();

    }

    private void loadData() {
        if (!isVisble || !isPrepared) {
            return;
        }
        requestHomePageData(mUserId, mDevcode, mTimestamp, mPage, mCurrentData);

    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

    /**
     * 判断当前页面是否处于显示状态
     *
     * @param isVisibleToUser
     */
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

    //隐藏状态
    protected void onInVisible() {

    }

    //显示状态
    protected void onVisible() {
        //加载数据
        mUserId = MyApplication.mApp.getUserId();
        if (StringUtil.isEmpty(mUserId)){
            mUserId="0";
        }
        loadData();
    }


    /**
     * 从网络上获取关注用户的游记数据
     *
     * @param userId    用户id
     * @param devcode   设备识别码
     * @param timestamp 时间戳
     * @param page      分页页数
     */
    private void requestHomePageData(String userId, String devcode, Long timestamp, int page, int type) {
        boolean timeFlag = APPUtils.judgeTimeDev(getActivity(), devcode, timestamp);
        if (timeFlag) {
            String str = Md5Utils.createMD5("devcode=" + devcode+"is_att="+"1" + "p=" + page + "timestamp=" + timestamp + "type=" + type + "uid=" + userId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", String.valueOf(devcode))
                    .add("p", String.valueOf(page))
                    .add("timestamp", String.valueOf(timestamp))
                    .add("type", String.valueOf(type))
                    .add("is_att", "1")
                    .add("signature", str)
                    .add("uid", userId)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.HOME_VIDEO_NEW_DATA, requestBody, new HttpCallBack(new ItemHomeEventTwo()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void requestHomePageData(ItemHomeEventTwo event) {
        if (event.isTimeOut()) {
            Toast.makeText(getActivity(), "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        HomeVideoNewDataBean data = event.getData();
        if (data.getStatus() == 0) {
            if (mRefreshSign) {

                EventBus.getDefault().post(new FollowDotEvent("0"));//更新关注小红点广播
                mItemTwo.clear();
                mItemTwo.addAll(data.getData().getData_good());
                itemHomeMoveFragmentRecyclerAdapter = new ItemHomeMoveFragmentRecyclerAdapterTwo(mItemTwo);
                itemHomeMoveFragmentRecyclerAdapter.setItemHomeMoveRecycler(this);
                mRecyclerView.setAdapter(itemHomeMoveFragmentRecyclerAdapter);
                itemHomeMoveFragmentRecyclerAdapter.notifyDataSetChanged();
                mSmartRefresh.finishRefresh();
            } else {
                if (data.getData().getData_good().size() == 0) {
                    mSmartRefresh.setLoadmoreFinished(true);
                }
                mItemTwo.addAll(data.getData().getData_good());
                itemHomeMoveFragmentRecyclerAdapter.notifyDataSetChanged();
                mSmartRefresh.finishLoadmore();
            }

        } else {
            Toast.makeText(getActivity(), data.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(getActivity(), data.getStatus());
        }
    }


    /**
     * 分享的回调
     *
     * @param bid
     */
    @Override
    public void shareSuccess(String bid) {
        //分享成功
        shareRequestBackground(mUserId, mDevcode, mTimestamp, bid);

    }

    @Override
    public void shareReport(final String bid) {
        //分享失败
        dialog.dismiss();
        new VideoReportingDialog(getActivity()).setVideoReportingCallBack(new VideoReportingDialog.VideoReportingCallBack() {
            @Override
            public void VideoReport(String info) {
                reportSendingServer(mUserId, mDevcode, mTimestamp, bid, info);
            }
        }).show();
    }

    /**
     * Recycleview的Item点击回调
     *
     * @param textView
     * @param position
     * @param uuid
     */
    @Override
    public void attention(ImageView textView, int position, String uuid) {
        //进行关注相关操作
        mPosition = position;
        mAttentionImg = textView;
        mAttentionImg.setEnabled(false);
        String userId = MyApplication.mApp.getUserId();
        if (!StringUtil.isEmpty(userId) && !userId.equals("0")) {
            if (mUserId.equals(uuid)) {
                startActivity(new Intent(getActivity(), PersonMessageActivity.class));
            } else {
                if (mItemTwo.get(position).getF_status().equals("0")) {//已关注
                    postFollow(mUserId, mDevcode, mTimestamp, uuid, 1);
                } else {//取消关注
                    postFollow(mUserId, mDevcode, mTimestamp, uuid, 0);
                }
            }
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }

    @Override
    public void share(BookBean dataGoodBean) {
        //进行分享相关操作
        final String bid = dataGoodBean.getBid();
        final String new_url = dataGoodBean.getNew_url() + "?form=android";
        final String shareTitle = dataGoodBean.getTitle();
        final String shareContent = dataGoodBean.getInfo();
        sharePic = URLUtils.BASE_URL + dataGoodBean.getB_pic();
        final String ord_num = "预约:" + dataGoodBean.getOrd_num();//预约
        final String col_num = "收藏:" + dataGoodBean.getCol_num();//收藏
        final String s_num = "转发:" + dataGoodBean.getS_num();//转发数
        final String zan_num = "点赞:" + dataGoodBean.getZan_num();
        //new Thread(new Runnable() {
        //    @Override
        //    public void run() {
        //        bitMBitmap = getBitMBitmap(sharePic);
        //    }
        //}).start();
        if (OkHttpUtils.isNetworkAvailable(getActivity())) {
            SimpleTarget<Bitmap> simpleTarget = new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    //链接
                    UMWeb web = new UMWeb(new_url);
                    web.setTitle("「指针自由行-APP」" + shareTitle + " : " + shareContent);//标题
                    if (resource == null) {
                        web.setThumb(new UMImage(getActivity(), R.mipmap.ic_launcher));  //缩略图
                    } else {
                        web.setThumb(new UMImage(getActivity(), resource));  //缩略图
                    }
                    web.setDescription("这是一款全新的旅游软件，改变旅行新方式");//描述
                    dialog.setmWeb(web);
                    dialog.setmCollections(col_num);
                    dialog.setmForwardingNumber(s_num);
                    dialog.setmReservationNumber(ord_num);
                    dialog.setFavorNumber(zan_num);
                    dialog.setBid(bid);
                    dialog.show();
                }
            };
            getBitMBitmap(sharePic, getActivity(), simpleTarget);
        } else {
            Toast.makeText(getActivity(), "网络延迟,请稍等...", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 分享成功后通知后台增加转发数
     *
     * @param userId    用户id
     * @param devcode   设备识别码
     * @param timestamp 时间戳
     * @param bid       路书id
     */
    private void shareRequestBackground(String userId, String devcode, long timestamp, String bid) {
        if (APPUtils.judgeTimeDev(getActivity(), devcode, timestamp)) {
            String sign = Md5Utils.createMD5("bid=" + bid + "devcode=" + devcode + "timestamp=" + timestamp + "uid=" + userId + URLUtils.WK_APP_KEY);
            RequestBody params = new FormBody.Builder()
                    .add("bid", bid)
                    .add("devcode", devcode)
                    .add("timestamp", String.valueOf(timestamp))
                    .add("uid", userId)
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.TRAVLE_SHARE_SUCCESS, params, new HttpCallBack(new BaseEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void shareRequestBackground(BaseEvent event) {
        if (event.isTimeOut()) {
            mAttentionImg.setEnabled(true);
            Toast.makeText(getActivity(), "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        BaseBean bean = event.getData();
        if (bean.getStatus() == 0) {

        } else {
            Toast.makeText(getActivity(), bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(getActivity(), bean.getStatus());
        }
    }
    /**
     * 关注接口
     *
     * @param userId    用户id
     * @param devcode   设备识别码
     * @param timestamp 时间戳
     * @param uuid      路数id
     * @param status    关注状态
     */
    private void postFollow(String userId, String devcode, long timestamp, String uuid, int status) {
        if (APPUtils.judgeTimeDev(getActivity(), devcode, timestamp)) {
            String sign = Md5Utils.createMD5("devcode=" + devcode + "status=" + status + "timestamp=" + timestamp + "uid=" + userId + "uuid=" + uuid + URLUtils.WK_APP_KEY);
            RequestBody params = new FormBody.Builder()
                    .add("devcode", devcode)
                    .add("status", String.valueOf(status))
                    .add("timestamp", String.valueOf(timestamp))
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
            mAttentionImg.setEnabled(true);
            Toast.makeText(getActivity(), "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        PersonalPageFollowBean data = event.getData();
        if (data.getStatus() == 0) {
            mAttentionImg.setEnabled(true);
            if (data.getC_status() == 1) {
                mAttentionImg.setImageResource(R.mipmap.icon_focus_s);
            } else {
                mAttentionImg.setImageResource(R.mipmap.icon_add_g);
            }
            mItemTwo.get(mPosition).setF_status(String.valueOf(data.getC_status()));
        } else {
            mAttentionImg.setEnabled(true);
            Toast.makeText(getActivity(), data.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(getActivity(), data.getStatus());
        }
    }
    /**
     * 举报信息发送后台
     *
     * @param userId
     * @param devcode
     * @param timestamp
     * @param bid
     * @param info
     */
    private void reportSendingServer(String userId, String devcode, long timestamp, String bid, String info) {
        if (APPUtils.judgeTimeDev(getActivity(), devcode, timestamp)) {
            String sign = Md5Utils.createMD5("bid=" + bid + "devcode=" + devcode + "info=" + info + "timestamp=" + timestamp + "uid=" + userId + URLUtils.WK_APP_KEY);
            RequestBody params = new FormBody.Builder()
                    .add("bid", bid)
                    .add("devcode", devcode)
                    .add("info", info)
                    .add("timestamp", String.valueOf(timestamp))
                    .add("uid", userId)
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.REPORT_INTERFACE, params, new HttpCallBack(new ReportSendEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void reportSendingServer(ReportSendEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(getActivity(), "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        BaseBean bean = event.getData();
        if (bean.getStatus() == 0) {
            Toast.makeText(getActivity(), bean.getMsg(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(getActivity(), bean.getStatus());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void mainFragment(LoginEventRefresh event) {
        if (event.isFlag()) {
            initView();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void mainFragment(ItemRecycleToTopEvent event) {
        if (event.isFlag()) {
            mRecyclerView.scrollToPosition(0);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void mainFragment(ItemTwoRefresh event) {
        if (event.isFlag()) {
            initView();
        }
    }




    @Subscribe(threadMode = ThreadMode.MAIN)
    public void numberOfComments(final VideoDetailsToHomePagerEvent event){//详情页面评论之后数据改变，首页相应改变评论数据
        mItemTwo.get(event.getPosition()).setC_num(event.getNum());
        itemHomeMoveFragmentRecyclerAdapter.notifyItemChanged(event.getPosition());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 147:
                if (resultCode == 14) {
                    mUserId = MyApplication.mApp.getUserId();
                }
                break;
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        GSYVideoManager.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        GSYVideoManager.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
    }
}

