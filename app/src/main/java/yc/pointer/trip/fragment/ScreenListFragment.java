package yc.pointer.trip.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import yc.pointer.trip.R;
import yc.pointer.trip.activity.LoginActivity;
import yc.pointer.trip.activity.NewPersonalHomePageActivity;
import yc.pointer.trip.adapter.ScreenListAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseBean;
import yc.pointer.trip.base.BaseFragment;
import yc.pointer.trip.bean.BookBean;
import yc.pointer.trip.bean.BookDetailsFabulousBean;
import yc.pointer.trip.bean.CommentsListBean;
import yc.pointer.trip.bean.DataGoodBean;
import yc.pointer.trip.bean.NewHomeVideoData;
import yc.pointer.trip.bean.PersonalPageFollowBean;
import yc.pointer.trip.bean.eventbean.FollowDotEvent;
import yc.pointer.trip.event.BaseEvent;
import yc.pointer.trip.event.CommentListEvent;
import yc.pointer.trip.event.NewHomeAddLikeEvent;
import yc.pointer.trip.event.PersonalPageFollowEvent;
import yc.pointer.trip.event.ScreenListEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.MySQLiteDataBase;
import yc.pointer.trip.untils.StatusBarUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.CommentPop;
import yc.pointer.trip.view.TravelSharingDialog;

import static yc.pointer.trip.untils.ImageUtils.getBitMBitmap;

/**
 * Created by 张继
 * 2018/12/10
 * 16:28
 * 公司：
 * 描述：首页全屏播放
 */

public class ScreenListFragment extends BaseFragment implements ScreenListAdapter.ScreenCallBack {
    @BindView(R.id.screen_recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    //recyclerView滑动一屏的辅助类
    PagerSnapHelper mPagerSnapHelper;
    @BindView(R.id.screen_linear)
    LinearLayout screenLinear;
    @BindView(R.id.screen_search)
    ImageView screenSearch;
    @BindView(R.id.screen_choice)
    TextView screenChoice;
    @BindView(R.id.screen_attention)
    TextView screenAttention;
    @BindView(R.id.screen_travel_earn)
    TextView screenTravelEarn;
    @BindView(R.id.screen_scanIt)
    ImageView screenScanIt;
    @BindView(R.id.screen_edit)
    EditText screenEdit;
    @BindView(R.id.screen_publish)
    Button screenPublish;
    @BindView(R.id.screen_up)
    RelativeLayout screenUp;
    private List<DataGoodBean> mList;
    private LinearLayoutManager layoutManager;
    private ScreenListAdapter listAdapter;
    private String mUserId;
    private String mDevcode;
    private Long mTimestamp;
    private int mPage;
    private boolean mRefreshSign = true;//刷新 true 加载false
    private int mPosition;
    private TextView mAttention;
    private TextView mLikeNumText;
    private String sharePic;
    private List<CommentsListBean.ListBean> mComments;//主评论列表
    private CommentPop commentPop;
    private boolean refreshFlag = true;

    @Override
    protected int getContentViewLayout() {
        return R.layout.screen_list_fragment;
    }

    @Override
    protected void initView() {
        mUserId = MyApplication.mApp.getUserId();
        if (StringUtil.isEmpty(mUserId)) {
            mUserId = "0";
        }
        mDevcode = MyApplication.mApp.getDevcode();
        mTimestamp = MyApplication.mApp.getTimestamp();
        mPage = 0;
        //头布局
        int height = StatusBarUtils.getStatusBarHeight(getActivity());
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) screenLinear.getLayoutParams();
        layoutParams.setMargins(0, height, 0, 0);
        screenLinear.setLayoutParams(layoutParams);
        mComments = new ArrayList<>();
        mList = new ArrayList();
        mPagerSnapHelper = new PagerSnapHelper();
        mPagerSnapHelper.attachToRecyclerView(mRecyclerView);
        //刷新动态空间颜色
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        listAdapter = new ScreenListAdapter(getActivity(), mList, this);
        mRecyclerView.setAdapter(listAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 0;
                mRefreshSign = true;
                //网络请求回来添加数据
                requestHomePageData(mUserId, mDevcode, mTimestamp, mPage, 0);
                //刷新完成
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == listAdapter.getItemCount()) {
                    //设置正在加载更多
                    listAdapter.changeMoreStatus(listAdapter.LOADING_MORE);
                    ++mPage;
                    mRefreshSign = false;
                    //改为网络请求
                    requestHomePageData(mUserId, mDevcode, mTimestamp, mPage, 0);
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    View view = mPagerSnapHelper.findSnapView(layoutManager);
                    GSYVideoManager.releaseAllVideos();
                    RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
                    if (viewHolder != null && viewHolder instanceof ScreenListAdapter.ItemViewHolder) {
                        ((ScreenListAdapter.ItemViewHolder) viewHolder).mEmptyHomeVideo.startPlayLogic();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //最后一个可见的ITEM
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });
        requestHomePageData(mUserId, mDevcode, mTimestamp, mPage, 0);

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
            String str = Md5Utils.createMD5("devcode=" + devcode + "p=" + page + "timestamp=" + timestamp + "type=" + type + "uid=" + userId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", String.valueOf(devcode))
                    .add("p", String.valueOf(page))
                    .add("timestamp", String.valueOf(timestamp))
                    .add("type", String.valueOf(type))
                    .add("signature", str)
                    .add("uid", userId)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.HOME_VIDEO_NEW_DATA, requestBody, new HttpCallBack(new ScreenListEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void requestHomePageData(ScreenListEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(getActivity(), "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        NewHomeVideoData data = event.getData();
        if (data.getStatus() == 0) {
            if (mRefreshSign) {
                mList.clear();
                NewHomeVideoData.UnmsgBean unmsg = data.getUnmsg();
                String msg_num = unmsg.getAtt_msg_num();
                EventBus.getDefault().post(new FollowDotEvent(msg_num));//更新关注小红点广播
                mList.addAll(data.getData().getData_good());
                listAdapter.AddHeaderItem(mList);
                MySQLiteDataBase.deleteGoodBean();
                MySQLiteDataBase.saveGoodData(data);
            } else {
                if (data.getData().getData_good().size() == 0) {
                    listAdapter.changeMoreStatus(listAdapter.NO_LOAD_MORE);
                }
                mList.clear();
                mList.addAll(data.getData().getData_good());
                listAdapter.AddFooterItem(mList);
            }
        } else {
            Toast.makeText(getActivity(), data.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(getActivity(), data.getStatus());
        }
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

    @Override
    public void onStop() {
        super.onStop();
        GSYVideoManager.releaseAllVideos();
    }


    @Override
    public void clickHead(String userId) {//跳转个人主页
        Intent intent = new Intent(getActivity(), NewPersonalHomePageActivity.class);
        intent.putExtra("uid", userId);
        getActivity().startActivity(intent);
    }

    @Override
    public void clickAttention(String uuid, int position, TextView textView) {//关注
        mAttention = textView;
        this.mPosition = position;
        if (!StringUtil.isEmpty(mUserId) && !mUserId.equals("0")) {
            if (mUserId.equals(uuid)) {
                Toast.makeText(getActivity(), "不能对该用户进行此操作", Toast.LENGTH_SHORT).show();
            } else {
                if (mList.get(position).getF_status().equals("0")) {//已关注
                    postFollow(mUserId, mDevcode, mTimestamp, uuid, 1);
                } else {//取消关注
                    postFollow(mUserId, mDevcode, mTimestamp, uuid, 0);
                }
            }
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }

    /**
     * 关注
     *
     * @param mUserId    用户id
     * @param mDevcode   设备识别码
     * @param mTimestamp 时间戳
     * @param uuid       作者id
     * @param status     关注状态
     */
    private void postFollow(String mUserId, String mDevcode, Long mTimestamp, String uuid, int status) {
        if (APPUtils.judgeTimeDev(getActivity(), mDevcode, mTimestamp)) {
            String sign = Md5Utils.createMD5("devcode=" + mDevcode + "status=" + status + "timestamp=" + mTimestamp
                    + "uid=" + mUserId + "uuid=" + uuid + URLUtils.WK_APP_KEY);
            RequestBody params = new FormBody.Builder()
                    .add("devcode", mDevcode)
                    .add("status", String.valueOf(status))
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", mUserId)
                    .add("uuid", uuid)
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.PERSONAL_FOLLOW, params, new HttpCallBack(new PersonalPageFollowEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void postFollow(PersonalPageFollowEvent event) {
        if (event.isTimeOut()) {
            mAttention.setEnabled(true);
            Toast.makeText(getActivity(), "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        PersonalPageFollowBean data = event.getData();
        if (data.getStatus() == 0) {
            mAttention.setEnabled(true);
            if (data.getC_status() == 1) {
                mAttention.setText("已关注");
            } else {
                mAttention.setText("+关注");
            }
            mList.get(mPosition).setF_status(String.valueOf(data.getC_status()));
        } else {
            mAttention.setEnabled(true);
            Toast.makeText(getActivity(), data.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(getActivity(), data.getStatus());
        }
    }

    @Override
    public void clickComment(BookBean bean) {
        refreshFlag = true;
        String bid = bean.getBid();
        commentPop = new CommentPop(getActivity(), this, new CommentPop.CommentPopCallBack() {
            @Override
            public void publishComments(int pid, String info) {

            }

            @Override
            public void deleteComments(int pid, int index) {

            }

            @Override
            public void deleteComments(int pid, int index, int ReplyIndex) {

            }
        });
        int[] location = new int[2];
        screenUp.getLocationOnScreen(location);
        commentPop.showAtLocation(screenUp, Gravity.NO_GRAVITY,
                location[0], location[1] - commentPop.getHeight());
        getCommentsList(bid, 0);
    }

    /**
     * 获取评论列表
     */
    private void getCommentsList(String bid, int page) {
        boolean timeFlag = APPUtils.judgeTimeDev(getActivity(), mDevcode, mTimestamp);
        if (timeFlag) {
            String sign = Md5Utils.createMD5("bid=" + bid + "devcode=" + mDevcode + "p=" + page + "timestamp=" + mTimestamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", String.valueOf(mUserId))
                    .add("bid", String.valueOf(bid))
                    .add("signature", sign)
                    .add("devcode", mDevcode)
                    .add("p", String.valueOf(page))
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.COMMENTS_LIST, requestBody, new HttpCallBack(new CommentListEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void commentsList(CommentListEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(getActivity(), "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        CommentsListBean data = event.getData();
        if (data.getStatus() == 0) {
            if (refreshFlag) {
                mComments.clear();
                mComments.addAll(data.getData());
                commentPop.setData(getActivity(), mComments);
            } else {
                mComments.addAll(data.getData());
                commentPop.setData(getActivity(), mComments);
            }

        }
    }

    @Override
    public void clickLike(String userId, int position, TextView textView) {//点赞
        this.mPosition = position;
        this.mLikeNumText = textView;
        fabulous(userId);
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
            String zan_num = mList.get(mPosition).getZan_num();
            mList.get(mPosition).setZ_status("1");
            String s = String.valueOf(Integer.valueOf(zan_num) + 1);
            if (!StringUtil.isEmpty(s)) {
                mLikeNumText.setText(s);
                mList.get(mPosition).setZan_num(s);
            }
            Drawable drawable = getActivity().getResources().getDrawable(R.mipmap
                    .icon_praise_sy_r);
            mLikeNumText.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
            listAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getActivity(), bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(getActivity(), bean.getStatus());
        }
    }

    @Override
    public void clickForward(BookBean dataGoodBean) {//转发分享
        final String new_url = dataGoodBean.getNew_url() + "?form=android";
        final String bid = dataGoodBean.getBid();
        final String shareTitle = dataGoodBean.getTitle();
        final String shareContent = dataGoodBean.getInfo();
        String bPic = dataGoodBean.getB_pic();
        if (!StringUtil.isEmpty(bPic)) {
            if (bPic.contains("https:")) {
                sharePic = bPic;
            } else {
                sharePic = URLUtils.BASE_URL + bPic;
            }
        }
        if (OkHttpUtils.isNetworkAvailable(getActivity())) {
            SimpleTarget<Bitmap> simpleTarget = new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    //链接
                    //链接
                    UMWeb web = new UMWeb(new_url);
                    web.setTitle("「指针自由行-APP」" + shareTitle + " : " + shareContent);//标题
                    if (resource == null) {
                        web.setThumb(new UMImage(getActivity(), R.mipmap.ic_launcher));  //缩略图
                    } else {
                        web.setThumb(new UMImage(getActivity(), resource));  //缩略图
                    }
                    web.setDescription("这是一款全新的旅游软件，改变旅行新方式");//描述
                    TravelSharingDialog travelSharingDialog = new TravelSharingDialog(getActivity(), ScreenListFragment.this);
                    travelSharingDialog.setWeb(web).show();
                    travelSharingDialog.setListener(new TravelSharingDialog.ShareSuccessListener() {
                        @Override
                        public void shareSuccess() {
                            shareRequestBackground(mUserId, mDevcode, mTimestamp, bid);
                        }
                    });
                }
            };
            getBitMBitmap(sharePic, getActivity(), simpleTarget);
        } else {
            Toast.makeText(getActivity(), "网络异常", Toast.LENGTH_SHORT).show();
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

    @OnClick({R.id.screen_search, R.id.screen_choice, R.id.screen_attention, R.id.screen_travel_earn, R.id.screen_scanIt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.screen_search:
                break;
            case R.id.screen_choice:
                break;
            case R.id.screen_attention:
                break;
            case R.id.screen_travel_earn:
                break;
            case R.id.screen_scanIt:
                break;
            case R.id.screen_edit:
                screenEdit.setFocusable(true);
                screenEdit.setFocusableInTouchMode(true);
                break;
            case R.id.screen_publish:
                break;
        }
    }

}
