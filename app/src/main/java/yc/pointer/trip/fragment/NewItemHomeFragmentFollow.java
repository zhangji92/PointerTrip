package yc.pointer.trip.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
import yc.pointer.trip.activity.VideoDetailsActivity;
import yc.pointer.trip.adapter.CommentHomeAdapter;
import yc.pointer.trip.adapter.LikeHomeAdapter;
import yc.pointer.trip.adapter.NewHomeFragmentMoveItemFollowAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseBean;
import yc.pointer.trip.base.BaseViewPageFragment;
import yc.pointer.trip.bean.BookBean;
import yc.pointer.trip.bean.BookDetailsFabulousBean;
import yc.pointer.trip.bean.CommentBean;
import yc.pointer.trip.bean.DataGoodBean;
import yc.pointer.trip.bean.NewHomeVideoData;
import yc.pointer.trip.bean.PersonalPageFollowBean;
import yc.pointer.trip.bean.ZanBean;
import yc.pointer.trip.bean.eventbean.FollowDotEvent;
import yc.pointer.trip.event.BaseEvent;
import yc.pointer.trip.event.ItemRecycleToTopEvent;
import yc.pointer.trip.event.NewHomeAddLikeEvent;
import yc.pointer.trip.event.NewItemHomeEventFollow;
import yc.pointer.trip.event.PersonalPageFollowEvent;
import yc.pointer.trip.event.ReportSendEvent;
import yc.pointer.trip.event.VideoDetailsToHomePagerEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.SharedPreferencesUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.HomeShareBoardDialog;
import yc.pointer.trip.view.LoadDialog;
import yc.pointer.trip.view.VideoReportingDialog;

import static yc.pointer.trip.untils.ImageUtils.getBitMBitmap;

/**
 * Created by 张继
 * 2018/4/10
 * 13:52
 * 公司:天津悦程嘉和网络科技有限公司
 * 描述:新版首页0809 关注
 */

public class NewItemHomeFragmentFollow extends BaseViewPageFragment implements NewHomeFragmentMoveItemFollowAdapter.ItemHomeMoveRecycler,
        HomeShareBoardDialog.ShareCallBack {

    @BindView(R.id.item_fragment_recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.item_fragment_smart)
    SmartRefreshLayout mSmartRefresh;

    private final static String TYPE = "KEY";
    private String mUserId;
    private String mDevcode;
    private long mTimestamp;
    private List<DataGoodBean> mItemOne = new ArrayList<>();

    private int mCurrentData;//当前数据标志
    private int mPage = 0;
    private boolean mRefreshSign = true;
    private NewHomeFragmentMoveItemFollowAdapter itemHomeMoveFragmentRecyclerAdapterOne;
    private ImageView mAttentionImg;
    private TextView mLikeNumText;
    private ImageView mZanImg;
    private int mPosition;
    private String sharePic;
    private HomeShareBoardDialog dialog;
    private LoadDialog mLoadDialog;

    private LinearLayoutManager layout;

    private LikeHomeAdapter likeHomeZanPicAdapter;//点赞人头像列表适配器
    private CommentHomeAdapter mCommentHomeAdapter;//首页游记评论列表适配器


    public static Fragment newInstance(int key) {
        NewItemHomeFragmentFollow fragment = new NewItemHomeFragmentFollow();
        Bundle bundle = new Bundle();
        bundle.putInt(TYPE, key);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    protected int getContentViewLayout() {
        return R.layout.fragment_item_home_move;
    }

    @Override
    protected void initView() {
        mCurrentData = getArguments().getInt(TYPE, 0);
        mUserId = SharedPreferencesUtils.getInstance().getString(getActivity(), "useId");
        mDevcode = ((MyApplication) getActivity().getApplication()).getDevcode();
        mTimestamp = ((MyApplication) getActivity().getApplication()).getTimestamp();

        if (StringUtil.isEmpty(mUserId) || "not find".equals(mUserId)) {
            mUserId = "0";
        }
        mItemOne.clear();

        dialog = new HomeShareBoardDialog(MyApplication.mApp, this, this);
        layout = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layout);

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
                mItemOne.clear();
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
        mLoadDialog = new LoadDialog(getActivity(), "正在加载...", R.drawable.load_animation);
        mLoadDialog.show();
        requestHomePageData(mUserId, mDevcode, mTimestamp, mPage, mCurrentData);

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
        if (StringUtil.isEmpty(mUserId)) {
            mUserId = "0";
        }
        loadData();
    }


    @Override
    protected boolean needEventBus() {
        return true;
    }


    /**
     * 从网络上获取首页信息
     *
     * @param userId    用户id
     * @param devcode   设备识别码
     * @param timestamp 时间戳
     * @param page      分页页数
     */
    private void requestHomePageData(String userId, String devcode, Long timestamp, int page, int type) {
        boolean timeFlag = APPUtils.judgeTimeDev(getActivity(), devcode, timestamp);
        if (timeFlag) {
            String str = Md5Utils.createMD5("devcode=" + devcode + "is_att=" + "1" + "p=" + page + "timestamp=" + timestamp + "type=" + type + "uid=" + userId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", String.valueOf(devcode))
                    .add("p", String.valueOf(page))
                    .add("timestamp", String.valueOf(timestamp))
                    .add("type", String.valueOf(type))
                    .add("is_att", "1")
                    .add("signature", str)
                    .add("uid", userId)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.HOME_VIDEO_NEW_DATA, requestBody, new HttpCallBack(new NewItemHomeEventFollow()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void requestHomePageData(NewItemHomeEventFollow event) {
        if (event.isTimeOut()) {
            mLoadDialog.dismiss();
            Toast.makeText(getActivity(), "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        NewHomeVideoData data = event.getData();
        if (data.getStatus() == 0) {
            if (mRefreshSign) {
                mItemOne.clear();
                NewHomeVideoData.UnmsgBean unmsg = data.getUnmsg();
                String msg_num = unmsg.getAtt_msg_num();
                EventBus.getDefault().post(new FollowDotEvent("0"));//更新关注小红点广播
                mItemOne.addAll(data.getData().getData_good());
                itemHomeMoveFragmentRecyclerAdapterOne = new NewHomeFragmentMoveItemFollowAdapter(mItemOne);
                itemHomeMoveFragmentRecyclerAdapterOne.setItemHomeMoveRecycler(this);
                mRecyclerView.setAdapter(itemHomeMoveFragmentRecyclerAdapterOne);
                mSmartRefresh.finishRefresh();
            } else {
                if (data.getData().getData_good().size() == 0) {
                    mSmartRefresh.setLoadmoreFinished(true);
                }
                int size = mItemOne.size();
                mItemOne.addAll(data.getData().getData_good());

                itemHomeMoveFragmentRecyclerAdapterOne.notifyDataSetChanged();
                mSmartRefresh.finishLoadmore();
            }
            mLoadDialog.dismiss();
        } else {
            mLoadDialog.dismiss();
            Toast.makeText(getActivity(), data.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(getActivity(), data.getStatus());
        }
    }

    @Override
    public void attention(ImageView img, int position, String uuid) {//关注
        mPosition = position;
        mAttentionImg = img;
        mAttentionImg.setEnabled(false);
        mUserId = MyApplication.mApp.getUserId();
        if (!StringUtil.isEmpty(mUserId) && !mUserId.equals("0")) {
            if (mUserId.equals(uuid)) {
//                startActivity(new Intent(getActivity(), PersonMessageActivity.class));
                Toast.makeText(getActivity(), "不能对该账户进行此操作", Toast.LENGTH_SHORT).show();
            } else {
                if (mItemOne.get(position).getF_status().equals("0")) {//已关注
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
    public void Addlike(ImageView zanImg,TextView likeTextView, int i, LikeHomeAdapter likeHomeAdapter, String bid) {
        mPosition = i;
        mLikeNumText = likeTextView;
        mZanImg=zanImg;
//        mLikeNumText.setEnabled(false);
        likeHomeZanPicAdapter = likeHomeAdapter;
        mUserId = MyApplication.mApp.getUserId();
        if (!StringUtil.isEmpty(mUserId) && !mUserId.equals("0")) {
            //请求点赞接口
            fabulous(bid);

        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }


    }

    @Override
    public void intenVieoDetial(TextView likeTextView, int index, LikeHomeAdapter likeHomeAdapter, CommentHomeAdapter commentHomeAdapter) {
        mPosition = index;
        mLikeNumText = likeTextView;
        likeHomeZanPicAdapter = likeHomeAdapter;
        mCommentHomeAdapter = commentHomeAdapter;
        DataGoodBean dataGoodBean = mItemOne.get(mPosition);
        Intent intent = new Intent(getActivity(), VideoDetailsActivity.class);
        intent.putExtra("dataGoodBean", dataGoodBean);
        intent.putExtra("position", String.valueOf(mPosition));
        startActivity(intent);
    }


    @Override
    public void share(BookBean dataGoodBean) {//分享
        final String bid = dataGoodBean.getBid();
        final String new_url = dataGoodBean.getNew_url() + "?form=android";
        final String shareTitle = dataGoodBean.getTitle();
        final String shareContent = dataGoodBean.getInfo();
        sharePic = URLUtils.BASE_URL + dataGoodBean.getB_pic();
        final String ord_num = "预约:" + dataGoodBean.getOrd_num();//预约
        final String col_num = "收藏:" + dataGoodBean.getCol_num();//收藏
        final String s_num = "转发:" + dataGoodBean.getS_num();//转发数
        final String zan_num = "点赞:" + dataGoodBean.getZan_num();

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
                mAttentionImg.setImageResource(R.mipmap.icon_concern_had);
            } else {
                mAttentionImg.setImageResource(R.mipmap.icon_concern);
            }
            mItemOne.get(mPosition).setF_status(String.valueOf(data.getC_status()));
        } else {
            mAttentionImg.setEnabled(true);
            Toast.makeText(getActivity(), data.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(getActivity(), data.getStatus());
        }
    }


    /**
     * 点赞
     *
     * @param bid 路书id
     */
    private void fabulous(String bid) {
        String sign = Md5Utils.createMD5("bid=" + bid + "devcode=" + mDevcode + "timestamp=" + mTimestamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
        RequestBody requestBody = new FormBody.Builder()
                .add("timestamp", String.valueOf(mTimestamp))
                .add("uid", String.valueOf(mUserId))
                .add("bid", String.valueOf(bid))
                .add("signature", sign)
                .add("devcode", mDevcode)
                .build();
        OkHttpUtils.getInstance().post(URLUtils.BOOK_FABULOUS, requestBody, new HttpCallBack(new NewHomeAddLikeEvent()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fabulousBean(NewHomeAddLikeEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(getActivity(), "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        BookDetailsFabulousBean bean = event.getData();
        if (bean.getStatus() == 0) {
            String zan_num = mItemOne.get(mPosition).getZan_num();
            AddLikeIsOk(zan_num, mPosition);

        } else {
            Toast.makeText(getActivity(), bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(getActivity(), bean.getStatus());
        }
    }

    /**
     * 点赞成功后的操作
     */
    private void AddLikeIsOk(String zanNum, int Position) {

        mItemOne.get(Position).setZ_status("1");
        String s = String.valueOf(Integer.valueOf(zanNum) + 1);
        if (!StringUtil.isEmpty(s)) {
            mLikeNumText.setText(s);
            mItemOne.get(Position).setZan_num(s);
        }


        Drawable drawable = getActivity().getResources().getDrawable(R.mipmap
                .icon_zan_b_red);
        mLikeNumText.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        List<ZanBean> zan = mItemOne.get(Position).getZan();
        ZanBean element = new ZanBean();
        String pic = MyApplication.mApp.getUserBean().getPic();
        element.setZ_u_pic(pic);
        zan.add(0, element);
        if (zan.size()==1){
            mZanImg.setVisibility(View.VISIBLE);
        }
        likeHomeZanPicAdapter.notifyDataSetChanged();
    }


    @Override
    public void shareSuccess(String bid) {//分享成功后回调
        //bitMBitmap.recycle();
        shareRequestBackground(mUserId, mDevcode, mTimestamp, bid);
    }

    @Override
    public void shareReport(final String bid) {//举报
        dialog.dismiss();
        new VideoReportingDialog(getActivity()).setVideoReportingCallBack(new VideoReportingDialog.VideoReportingCallBack() {
            @Override
            public void VideoReport(String info) {
                reportSendingServer(mUserId, mDevcode, mTimestamp, bid, info);
            }
        }).show();
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
    public void mainFragment(ItemRecycleToTopEvent event) {//回到顶部
        if (event.isFlag()) {
            mRecyclerView.scrollToPosition(0);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void numberOfComments(final VideoDetailsToHomePagerEvent event) {

        int position = event.getPosition();
        String num = event.getNum();
        String c_nickName = event.getC_nickName();
        String c_info = event.getC_info();

        if (event.isLike()) {
            //详情页点赞后的数据更新操作
            AddLikeIsOk(num, position);
        } else {
            //详情页面评论之后数据改变，首页相应改变评论数据
            mItemOne.get(position).setC_num(num);
            if (!StringUtil.isEmpty(c_nickName) && !StringUtil.isEmpty(c_info)) {
                List<CommentBean> commentBeansList = mItemOne.get(position).getComment();
                CommentBean commentBean = new CommentBean();
                commentBean.setC_nickname(c_nickName);
                commentBean.setC_info(c_info);
                commentBeansList.add(0, commentBean);
                mCommentHomeAdapter.notifyDataSetChanged();
            }

        }

        itemHomeMoveFragmentRecyclerAdapterOne.notifyItemChanged(position);


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