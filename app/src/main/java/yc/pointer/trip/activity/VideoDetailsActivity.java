package yc.pointer.trip.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.GSYVideoProgressListener;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import yc.pointer.trip.R;
import yc.pointer.trip.adapter.CommentAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.base.BaseBean;
import yc.pointer.trip.bean.BookBean;
import yc.pointer.trip.bean.BookDetailsBean;
import yc.pointer.trip.bean.BookDetailsCollectionBean;
import yc.pointer.trip.bean.BookDetailsFabulousBean;
import yc.pointer.trip.bean.CommentMessageBean;
import yc.pointer.trip.bean.CommentsListBean;
import yc.pointer.trip.bean.ReplyBean;
import yc.pointer.trip.bean.eventbean.ReceiverBean;
import yc.pointer.trip.event.BaseEvent;
import yc.pointer.trip.event.BookDetailsCollectionEvent;
import yc.pointer.trip.event.BookDetailsEvent;
import yc.pointer.trip.event.BookDetailsFabulousEvent;
import yc.pointer.trip.event.CommentListEvent;
import yc.pointer.trip.event.CommentsEvent;
import yc.pointer.trip.event.DeleteCommentEvent;
import yc.pointer.trip.event.ReplyEvent;
import yc.pointer.trip.event.ReportSendEvent;
import yc.pointer.trip.event.VideoDetailsToHomePagerEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.FullyLinearLayoutManager;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.PermissionHelper;
import yc.pointer.trip.untils.PermissionHelper.PermissionModel;
import yc.pointer.trip.untils.ScrollCalculatorHelper;
import yc.pointer.trip.untils.SoftKeyboardStateWatcher;
import yc.pointer.trip.untils.StatusBarUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.CustomCircleImage;
import yc.pointer.trip.view.DialogKnow;
import yc.pointer.trip.view.DialogSure;
import yc.pointer.trip.view.EmptyControlBookVideo;
import yc.pointer.trip.view.HomeShareBoardDialog;
import yc.pointer.trip.view.VideoReportingDialog;

import static com.shuyu.gsyvideoplayer.utils.GSYVideoType.SCREEN_TYPE_DEFAULT;
import static yc.pointer.trip.untils.ImageUtils.getBitMBitmap;

/**
 * Created by moyan on 2017/11/20.
 * 详情
 */

public class VideoDetailsActivity extends BaseActivity implements PermissionHelper.OnAlterApplyPermission, HomeShareBoardDialog.ShareCallBack {


    @BindView(R.id.video_details_layout)
    RelativeLayout rootView;
    @BindView(R.id.refresh_smart)
    SmartRefreshLayout refreshSmart;
    @BindView(R.id.video_details_surfaceview)
    EmptyControlBookVideo mVideoView;//视频播放
    @BindView(R.id.scroll_video)
    ScrollView mScrollVideoView;//外层滑动
    @BindView(R.id.video_details_head)
    CustomCircleImage videoDetailsHead;//发布者头像
    @BindView(R.id.video_nick_name)
    TextView videoNickName;//发布者昵称
    @BindView(R.id.video_date)
    TextView videoDate;//发布日期
    @BindView(R.id.video_city)
    TextView videoCity;//发布城市
    @BindView(R.id.video_senic)
    TextView videoSenic;//发布景点
    //    @BindView(R.id.video_reserve)
//    Button videoReserve;//预约服务
    @BindView(R.id.video_title)
    TextView videoTitle;//视频标题
    @BindView(R.id.video_introduce_content)
    TextView videoIntroduceContent;//视频介绍
    @BindView(R.id.share_video)
    ImageView shareVideo;//分享
    @BindView(R.id.exit_video)
    ImageView exitVideo;//返回同原生返回键
    @BindView(R.id.collection_video)
    ImageView collectionVideo;//收藏
    @BindView(R.id.like_video)
    ImageView likeVideo;//点赞
    @BindView(R.id.comments_num)
    TextView commentsNum;//评论数
    @BindView(R.id.comments_main_list)
    RecyclerView commentsMainList;//评论列表
    @BindView(R.id.edit_comments_bottom)
    ImageView editCommentsBottom;//点击写评论（弹出软键盘）
    @BindView(R.id.comments_bottom)
    RelativeLayout commentsBottom;//正常显示的底部栏
    @BindView(R.id.edit_comments_up)
    EditText editCommentsUp;//输入框
    @BindView(R.id.comments_publish)
    Button commentsPublish;//发送按钮
    @BindView(R.id.comments_up)
    RelativeLayout commentsUp;//软键盘弹出后的输入框布局
    @BindView(R.id.empty_img)
    ImageView emptyImg;
    @BindView(R.id.adapter_empty)
    TextView adapterEmpty;
    @BindView(R.id.empty)
    LinearLayout empty;
    @BindView(R.id.verify_result)
    ImageView isVIP;  //VIP认证标识


    private BookBean dataGoodBean;
    //    private NewHomeVideoData.DataBean.DataGoodBean dataGoodBean;
    private String mBuid;
    private String mUserId;
    private String mBid;
    private boolean islogin;
    private String mDevcode;
    private long mTimestamp;
    private int collectionStatus;
    private String isOrder;//发游记人的会员身份
    private int zStatus;
    private String mShareUrl;
    private String shareTitle;
    private String shareContent;
    private PermissionHelper mHelper;
    private PermissionModel[] permissionModels = {new PermissionModel(1, Manifest.permission.READ_EXTERNAL_STORAGE, "存储"),
            new PermissionModel(2, Manifest.permission.WRITE_EXTERNAL_STORAGE, "写出外部存储"),
    };
    private boolean isPermission = true;//权限申请标志，防止一直TOAST
    private UMWeb web;
    private String sharePic;
    private String mIsOrder;//用户可不可以发单
    private boolean flag = true;//true-刷新  false-加载
    private int page = 0;
    //private MyBroadcastReciver myBroadcastReciver;

    private boolean isDeposited;
    private List<CommentsListBean.ListBean> mCommetsList = new ArrayList<>();//主评论列表
    private CommentAdapter commetsAdapter;

    private int mPid = 0;
    private String mPnickName = "";
    private int mIndex;
    private int mReplyIndex;
    private boolean isComments = true;
    private int screenWidth;//屏幕宽度
    private HomeShareBoardDialog mHomeShareBoardDialog;
    private OrientationUtils orientationUtils;
    private boolean isPlay;
    private boolean isPause;
    private String mPosition;


    @Override
    protected int getContentViewLayout() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return R.layout.video_details_layout;
    }


    @Override
    protected boolean needEventBus() {
        return true;
    }

    @Override
    protected void initView() {
        StatusBarUtils.with(this).init();//设置沉浸式
        shareVideo.setEnabled(false);
        //是否登录
        dataGoodBean = (BookBean) getIntent().getSerializableExtra("dataGoodBean");
        mPosition = getIntent().getStringExtra("position");
        mUserId = ((MyApplication) getApplication()).getUserId();
        mBuid = dataGoodBean.getUid();
        mBid = dataGoodBean.getBid();
        mDevcode = ((MyApplication) getApplication()).getDevcode();//设备识别码
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();//时间戳
        islogin = ((MyApplication) getApplication()).isIslogin();
        mHelper = new PermissionHelper(this, this, permissionModels);
        mHomeShareBoardDialog = new HomeShareBoardDialog(MyApplication.mApp, VideoDetailsActivity.this, VideoDetailsActivity.this);
        if (!StringUtil.isEmpty(mUserId)) {
            bookDetailsBean(mBid, mUserId);//请求收藏点赞预约的状态
        } else {
            mUserId = "0";
            bookDetailsBean(mBid, mUserId);//请求收藏点赞预约的状态
        }

//        //设置本地数据
        setLocationData();
//        //获取评论列表
        getCommentsList(0);
        refreshSmart.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                flag = false;
                ++page;
                getCommentsList(page);
                refreshSmart.finishLoadmore();
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshSmart.setLoadmoreFinished(false);
                flag = true;
                page = 0;
                getCommentsList(page);
                refreshSmart.finishRefresh();
            }
        });
        refreshSmart.setEnableRefresh(false);
        commentsBottom.setVisibility(View.VISIBLE);
        commentsUp.setVisibility(View.GONE);

        //软键盘监听
        softKeyListener(rootView);
        FullyLinearLayoutManager fullyLinearLayoutManager = new FullyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        commentsMainList.setLayoutManager(fullyLinearLayoutManager);
        //处理滑动卡顿
        commentsMainList.setHasFixedSize(true);
        commentsMainList.setNestedScrollingEnabled(false);
        //主评论适配器
        commetsAdapter = new CommentAdapter(VideoDetailsActivity.this);
        commetsAdapter.setMainList(mCommetsList);
        commentsMainList.setAdapter(commetsAdapter);
        commetsAdapter.setListener(new CommentAdapter.onCommentClickListener() {
            @Override
            public void onClickComment(int position) {
                if (isLogin(islogin)) {
                    return;
                }
                String userNickname = ((MyApplication) getApplication()).getUserBean().getNickname();
                mPid = Integer.parseInt(mCommetsList.get(position).getCid());
                mIndex = position;//记录列表位置
                mPnickName = mCommetsList.get(position).getNickname();
                if (!StringUtil.isEmpty(userNickname) && !userNickname.equals(mPnickName)) {
                    showSoftKey("回复：" + mPnickName);
                } else if (userNickname.equals(mPnickName)) {
                    //删除该评论
                    new DialogSure(VideoDetailsActivity.this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                        @Override
                        public void onClickListener(DialogSure dialogSure, boolean trueEnable) {
                            if (trueEnable) {
                                isComments = true;
                                deleteComments(String.valueOf(mPid));
                            }

                        }
                    }).setTitle("确认删除该评论").setMsg("该评论被删除后相应回复也会被一并删除，确认要删除？")
                            .setNegativeButton("取消")
                            .setPositiveButton("确认")
                            .show();
                }
            }

            @Override
            public void getPid(int pid, String nickName, int index, int replyIndex) {

                if (isLogin(islogin)) {
                    return;
                }
                String userNickname = ((MyApplication) getApplication()).getUserBean().getNickname();
                mPid = pid;
                mPnickName = nickName;
                mIndex = index;
                mReplyIndex = replyIndex;
                if (!StringUtil.isEmpty(userNickname) && !userNickname.equals(mPnickName)) {
                    showSoftKey("回复：" + mPnickName);
                } else if (userNickname.equals(mPnickName)) {
                    //删除该评论
                    new DialogSure(VideoDetailsActivity.this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                        @Override
                        public void onClickListener(DialogSure dialogSure, boolean trueEnable) {
                            if (trueEnable) {
                                isComments = false;
                                deleteComments(String.valueOf(mPid));
                            }
                        }
                    }).setTitle("确认删除").setMsg("您是否确认删除您的评论？")
                            .setNegativeButton("取消")
                            .setPositiveButton("确认")
                            .show();

                }
            }
        });


        //IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction("刷新");
        //myBroadcastReciver = new MyBroadcastReciver();
        //registerReceiver(myBroadcastReciver, intentFilter);

        //播放点击判断是否时wifi状态下，如果不是显示弹框
        mVideoView.getStartButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoView.getCurrentState() == 2) {//正在播放
                    GSYVideoManager.onPause();
                } else if (mVideoView.getCurrentState() == 5) {
                    GSYVideoManager.onResume();
                } else {
                    ScrollCalculatorHelper.getInstance().startPlayLogic(mVideoView, VideoDetailsActivity.this);
                }
            }
        });
    }


    /**
     * 点击弹出软键盘
     *
     * @param hint
     */
    private void showSoftKey(String hint) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        commentsBottom.setVisibility(View.GONE);
        commentsUp.setVisibility(View.VISIBLE);
        editCommentsUp.setFocusable(true);
        editCommentsUp.setFocusableInTouchMode(true);
        editCommentsUp.requestFocus();
        editCommentsUp.setHint(hint);
        editCommentsUp.setHintTextColor(getResources().getColor(R.color.history_search));
        inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 监听软键盘弹出和隐藏的状态
     *
     * @param view
     */
    private void softKeyListener(View view) {
        final SoftKeyboardStateWatcher watcher = new SoftKeyboardStateWatcher(view, this);
        watcher.addSoftKeyboardStateListener(
                new SoftKeyboardStateWatcher.SoftKeyboardStateListener() {
                    @Override
                    public void onSoftKeyboardOpened(int keyboardHeightInPx) {
                        commentsBottom.setVisibility(View.GONE);
                        commentsUp.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onSoftKeyboardClosed() {
                        //处理一些键盘关闭的事情
                        commentsBottom.setVisibility(View.VISIBLE);
                        commentsUp.setVisibility(View.GONE);

                    }
                }
        );
    }

    /**
     * 设置上个页面传递过来的数据
     */
    private void setLocationData() {
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        double picHeight = Double.parseDouble(dataGoodBean.getHeight());
        double picWidth = Double.parseDouble(dataGoodBean.getWidth());
        double penNum = 0;
        if (picWidth != 0) {
            penNum = picHeight / picWidth;
        } else {
            penNum = 0;
        }
        double height = width * penNum;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mVideoView.getLayoutParams();
        if (penNum == 0) {
            layoutParams.height = width * (4 / 3);
        } else {
            layoutParams.height = (int) height;
        }
        mVideoView.setLayoutParams(layoutParams);

        GSYVideoType.setShowType(GSYVideoType.SCREEN_MATCH_FULL);
        String strTimeDate = StringUtil.getStrTimeTomm(dataGoodBean.getAddtime1());
        Date date = StringUtil.getStrTimeDate(strTimeDate);
        if (strTimeDate != null) {
            String format = StringUtil.format(date);
            videoDate.setText(format);
        }
        String pic = dataGoodBean.getPic();
//        isOrder = dataGoodBean.getIs_order();
        String is_vip = dataGoodBean.getIs_vip();
//        OkHttpUtils.displayImg(videoDetailsHead, pic);//头像
        OkHttpUtils.displayGlideCircular(this, videoDetailsHead, pic, isVIP, is_vip);
//        if (!StringUtil.isEmpty(isOrder)&&isOrder.equals("1")){
//          isVIP.setVisibility(View.VISIBLE);
//        }else {
//            isVIP.setVisibility(View.GONE);
//        }
        videoNickName.setText(dataGoodBean.getNickname());//发布昵称
        videoCity.setText(dataGoodBean.getCity());//发布城市
        videoSenic.setText(dataGoodBean.getLocation());//发布景点
        videoTitle.setText(dataGoodBean.getTitle());//视频标题

        String info = dataGoodBean.getInfo();
        if (!StringUtil.isEmpty(info)) {

            videoIntroduceContent.setText(info);//简介

        } else {

            videoIntroduceContent.setText("这位作者很懒，没有添加描述~");
            videoIntroduceContent.setTextColor(Color.parseColor("#b1b1b1"));

        }
        String c_num = dataGoodBean.getC_num();//评论次数
        String format = String.format(getResources().getString(R.string.comments_num), c_num);
        commentsNum.setText(format);
        resolveNormalVideoUI();
        //外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils(this, mVideoView);
        //初始化不打开外部的旋转
        orientationUtils.setEnable(false);
        GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();

        String video = dataGoodBean.getVideo();
        ImageView imageView = new ImageView(this);
        OkHttpUtils.displayImg(imageView, dataGoodBean.getB_pic());
        if (video.contains("https:")) {
            playVideoForUrl(imageView, gsyVideoOption, video);
        } else {
            playVideoForUrl(imageView, gsyVideoOption, URLUtils.BASE_URL + video);
        }

        mVideoView.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接横屏
                orientationUtils.resolveByClick();
                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                mVideoView.startWindowFullscreen(VideoDetailsActivity.this, true, true);
            }
        });
    }

    /**
     * 播放网络视频
     *
     * @param imageView
     * @param gsyVideoOption
     * @param video
     */
    private void playVideoForUrl(ImageView imageView, GSYVideoOptionBuilder gsyVideoOption, String video) {
        gsyVideoOption.setThumbImageView(imageView)
                .setIsTouchWiget(false)
                .setRotateViewAuto(true)
                .setLockLand(true)
                .setAutoFullWithSize(true)
                .setShowFullAnimation(true)
                .setNeedLockFull(true)
                .setUrl(video)
                .setCacheWithPlay(true)
                .setVideoTitle("")
                .setVideoAllCallBack(new GSYSampleCallBack() {
                    @Override
                    public void onPrepared(String url, Object... objects) {
                        super.onPrepared(url, objects);
                        //开始播放了才能旋转和全屏
                        orientationUtils.setEnable(true);
                        isPlay = true;
                    }

                    @Override
                    public void onEnterFullscreen(String url, Object... objects) {
                        super.onEnterFullscreen(url, objects);
                    }

                    @Override
                    public void onAutoComplete(String url, Object... objects) {
                        super.onAutoComplete(url, objects);
                    }

                    @Override
                    public void onClickStartError(String url, Object... objects) {
                        super.onClickStartError(url, objects);
                    }

                    @Override
                    public void onQuitFullscreen(String url, Object... objects) {
                        super.onQuitFullscreen(url, objects);
                        if (orientationUtils != null) {
                            orientationUtils.backToProtVideo();
                        }
                    }
                })
                .setLockClickListener(new LockClickListener() {
                    @Override
                    public void onClick(View view, boolean lock) {
                        if (orientationUtils != null) {
                            //配合下方的onConfigurationChanged
                            orientationUtils.setEnable(!lock);
                        }
                    }
                })
                .setGSYVideoProgressListener(new GSYVideoProgressListener() {
                    @Override
                    public void onProgress(int progress, int secProgress, int currentPosition, int duration) {
                    }
                })
                .build(mVideoView);
        ScrollCalculatorHelper.getInstance().startPlayLogic(mVideoView, this);
        mVideoView.setLooping(true);
    }


    @OnClick({R.id.share_video, R.id.video_details_head, R.id.exit_video, R.id.collection_video, R.id.like_video, R.id.comments_publish, R.id.edit_comments_bottom})
    public void onViewClicked(View view) {
        switch (view.getId()) {
//            case R.id.video_reserve://预约
//                if (isLogin(islogin)) {
//                    return;
//                }
//                mIsOrder = ((MyApplication) getApplication()).getUserBean().getIs_jie();//判断是否可以发单，否则需要认证
//                isDeposited = SharedPreferencesUtils.getInstance().getBoolean(VideoDetailsActivity.this, "isPayDeposit");
//                if (OkHttpUtils.isNetworkAvailable(VideoDetailsActivity.this)) {
//                    if (!StringUtil.isEmpty(mBuid) && !mBuid.equals(mUserId)) {
//                        if (!StringUtil.isEmpty(mIsOrder) && mIsOrder.equals("2")) {
//                            //认证成功
//                            if (isDeposited) {
//                                //已交押金
//                                //验证发游记作者是否具备接单资格
//                                authIsJie();
//                            } else {
//                                //已经认证，未缴纳押金，弹框跳转押金
//                                new DialogSure(VideoDetailsActivity.this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
//                                    @Override
//                                    public void onClickListener(DialogSure dialogSure, boolean cancelable) {
//                                        if (cancelable) {
//
//                                            Intent intent = new Intent(VideoDetailsActivity.this, UnDepositActivity.class);
//                                            startActivity(intent);
//
//                                        } else {
//
//                                        }
//                                    }
//                                }).setTitle("非常抱歉").setMsg("您尚未充值年费,会员身份认证不完善,不能完成发单操作").setPositiveButton("去充值").setNegativeButton("那算了").show();
//
//                            }
//                        } else if (mIsOrder.equals("1")) {
//                            //审核中
//                            new DialogKnow(this, R.style.user_default_dialog, new DialogKnow.CallBackListener() {
//                                @Override
//                                public void onClickListener() {
//
//                                }
//                            }).setTitle("温馨提示").setMsg("您的身份信息正在加急审核当中,请您耐心等待").setPositiveButton("我知道了").show();
//
//                        } else if (mIsOrder.equals("3")) {
//                            //审核失败
//                            new DialogSure(this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
//                                @Override
//                                public void onClickListener(DialogSure dialogSure, boolean trueEnable) {
//                                    if (trueEnable) {
//                                        Intent intent = new Intent(VideoDetailsActivity.this, VerifyActivity.class);
//                                        intent.putExtra("video", "hair");
//                                        intent.putExtra("dataBean", dataGoodBean);
//                                        startActivity(intent);
//                                    }
//                                }
//                            }).setTitle("非常抱歉").setMsg("您的会员升级申请失败，请重新完成会员身份认证")
//                                    .setPositiveButton("前往")
//                                    .setNegativeButton("放弃")
//                                    .show();
//                        } else {
//                            new DialogSure(this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
//                                @Override
//                                public void onClickListener(DialogSure dialogSure, boolean trueEnable) {
//                                    if (trueEnable) {
//                                        Intent intent = new Intent(VideoDetailsActivity.this, VerifyActivity.class);
//                                        intent.putExtra("video", "hair");
//                                        intent.putExtra("dataBean", dataGoodBean);
//                                        startActivity(intent);
//                                    }
//                                }
//                            }).setTitle("温馨提示").setMsg("您尚未升级成为指针会员，请先去认证会员身份，方可完成预约操作")
//                                    .setPositiveButton("确定")
//                                    .setNegativeButton("取消")
//                                    .show();
//                        }
//                    } else {
//                        new DialogKnow(this, R.style.user_default_dialog, new DialogKnow.CallBackListener() {
//                            @Override
//                            public void onClickListener() {
//                            }
//                        }).setTitle("温馨提示").setMsg("该游记是由您自己发布的,不能被预约").setPositiveButton("我知道了").show();
//                    }
//
//                } else {
//                    Toast.makeText(this, "网络未连接", Toast.LENGTH_SHORT).show();
//                }
//                break;
            case R.id.share_video://分享

                if (OkHttpUtils.isNetworkAvailable(VideoDetailsActivity.this)) {
                    //开启存储权限
                    if (Build.VERSION.SDK_INT < 23) {
                        //正常运行
//                        new EditDialog(this, VideoDetailsActivity.this, web).show();
                        shareDialog();
                    } else {
                        //申请权限
                        //Applypermission();
//                        mHelper=new PermissionHelper(this, new OnAlterApplyPermission() {
//                            @Override
//                            public void OnAlterApplyPermission() {
//                                new EditDialog(VideoDetailsActivity.this, VideoDetailsActivity.this, web).show();
//                            }
//                        },permissionModels);
                        isPermission = true;
                        mHelper.applyPermission();
                        mHelper.setOnAlterApplyPermission(this);
                    }
                } else {
                    Toast.makeText(this, "网络未连接", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.exit_video://返回
                finish();
                break;
            case R.id.collection_video://收藏
                if (isLogin(islogin)) {
                    return;
                }
                if (collectionStatus == 0) {
                    collection(mBid, mUserId, 1);
                } else {
                    Toast.makeText(this, "您已经收藏过该游记了", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.like_video://点赞
                if (isLogin(islogin)) {
                    return;
                }
                if (zStatus == 0) {
                    fabulous(mBid, mUserId);
                } else {
                    Toast.makeText(this, "您已经点过赞了", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.edit_comments_bottom://点击写评论
                if (isLogin(islogin)) {
                    return;
                }
                mPid = 0;
                showSoftKey("写评论!(最多150字)");
                break;
            case R.id.comments_publish://发送
                String info = editCommentsUp.getText().toString();
                if (mPid != 0) {
                    String userNickname = ((MyApplication) getApplication()).getUserBean().getNickname();
                    if (userNickname.equals(mPnickName)) {
                        //删除评论
                        Toast.makeText(this, "不可回复自己", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (!StringUtil.isEmpty(info)) {
                    publishComments(mBid, mPid, info);
                    editCommentsUp.setText("");
                    editCommentsUp.setFocusable(false);
                    editCommentsUp.setFocusableInTouchMode(false);
                    commentsBottom.setVisibility(View.VISIBLE);
                    commentsUp.setVisibility(View.GONE);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editCommentsUp.getWindowToken(), 0);
                } else {
                    Toast.makeText(this, "评论信息不能为空哦", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.video_details_head:
                Intent intent = new Intent(VideoDetailsActivity.this, NewPersonalHomePageActivity.class);
                intent.putExtra("uid", dataGoodBean.getUid());
                startActivity(intent);
                break;
        }
    }


    private void shareDialog() {
        if (OkHttpUtils.isNetworkAvailable(this)) {
            SimpleTarget<Bitmap> simpleTarget = new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    //链接
                    web = new UMWeb(mShareUrl);
                    web.setTitle("「指针自由行-APP」" + shareTitle + " : " + shareContent);//标题
                    web.setDescription("这是一款全新的旅游软件，改变旅行新方式");//描述
                    if (resource == null) {
                        web.setThumb(new UMImage(VideoDetailsActivity.this, R.mipmap.ic_launcher));  //缩略图
                    } else {
                        web.setThumb(new UMImage(VideoDetailsActivity.this, resource));  //缩略图
                    }
                    mHomeShareBoardDialog.setBid(mBid);
                    String ord_num = "预约:" + dataGoodBean.getOrd_num();//预约
                    String col_num = "收藏:" + dataGoodBean.getCol_num();//收藏
                    String s_num = "转发:" + dataGoodBean.getS_num();//转发数
                    String zan_num = "点赞:" + dataGoodBean.getZan_num();
                    mHomeShareBoardDialog.setmWeb(web);
                    mHomeShareBoardDialog.setmCollections(col_num);
                    mHomeShareBoardDialog.setmForwardingNumber(s_num);
                    mHomeShareBoardDialog.setmReservationNumber(ord_num);
                    mHomeShareBoardDialog.setFavorNumber(zan_num);
                    mHomeShareBoardDialog.setBid(mBid);
                    mHomeShareBoardDialog.show();
                }
            };
            getBitMBitmap(sharePic, VideoDetailsActivity.this, simpleTarget);
        } else {
            Toast.makeText(this, "网络延迟,请稍等...", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 判断游记作者是否具备接单资格
     */
    private void authIsJie() {
        if (!StringUtil.isEmpty(isOrder) && isOrder.equals("1")) {
            Intent intent = new Intent(VideoDetailsActivity.this, MainActivity.class);
            intent.putExtra("bid", mBid);
            intent.putExtra("logFlag", "gotoTravel");
            startActivity(intent);
            finish();
        } else {
            new DialogKnow(this, R.style.user_default_dialog, new DialogKnow.CallBackListener() {
                @Override
                public void onClickListener() {

                }
            }).setTitle("非常抱歉")
                    .setMsg("该游记作者尚未具备接单资格，再去看看其他您感兴趣的游记吧!")
                    .setPositiveButton("我知道了")
                    .show();
//                            //Toast.makeText(this, "该游记作者尚未具备接单资格，看看其他您感兴趣的路书吧", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 去登陆
     *
     * @param isFlag
     * @return
     */
    private boolean isLogin(boolean isFlag) {
        if (!isFlag) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("logFlag", "videoDetails");
            startActivityForResult(intent, 0);
            return true;
        }
        return false;
    }

    /**
     * 点赞
     *
     * @param bid 路书id
     * @param uid 用户id
     */
    private void fabulous(String bid, String uid) {
        RequestBody requestBody = sendMsg(bid, uid);
        OkHttpUtils.getInstance().post(URLUtils.BOOK_FABULOUS, requestBody, new HttpCallBack(new BookDetailsFabulousEvent()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fabulousBean(BookDetailsFabulousEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        BookDetailsFabulousBean bean = event.getData();
        if (bean.getStatus() == 0) {
            likeVideo.setImageResource(R.mipmap.icon_zan_details_x_r);
            likeVideo.setEnabled(false);

            //评论数设置在首页
            if (!StringUtil.isEmpty(mPosition)) {
                VideoDetailsToHomePagerEvent eventLike = new VideoDetailsToHomePagerEvent(dataGoodBean.getZan_num(), Integer.valueOf(mPosition));
                eventLike.setLike(true);
                EventBus.getDefault().post(eventLike);
            }

//            likeCount.setText(num);
//            Toast.makeText(this, "赞", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }


    /**
     * 游记详情的数据
     *
     * @param bid 路书id
     * @param uid 用户id
     */
    private void bookDetailsBean(String bid, String uid) {
        RequestBody requestBody = sendMsg(bid, uid);
        OkHttpUtils.getInstance().post(URLUtils.BOOK_DETAILS, requestBody, new HttpCallBack(new BookDetailsEvent()));
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void bookDetailsBean(BookDetailsEvent event) {
        if (event.isTimeOut()) {
            //shareVideo.setEnabled(false);
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        BookDetailsBean bean = event.getData();

        if (bean.getStatus() == 0) {
            shareVideo.setEnabled(true);
            //监听点击的每一个url，做自己的特殊处理用
            mShareUrl = bean.getData().getNew_url() + "?form=android";
            shareTitle = bean.getData().getTitle();
            shareContent = bean.getData().getInfo();
            sharePic = bean.getData().getPic();
            String is_vip = bean.getData().getIs_vip();
            String pic = bean.getData().getU_pic();
            OkHttpUtils.displayGlideCircular(this, videoDetailsHead, pic, isVIP, is_vip);
//            isOrder = bean.getData().getIs_order();
//            if (!StringUtil.isEmpty(isOrder)&&isOrder.equals("1")){
//                isVIP.setVisibility(View.VISIBLE);
//            }else {
//                isVIP.setVisibility(View.GONE);
//            }

            String c_num = bean.getData().getC_num();
            String format = String.format(getResources().getString(R.string.comments_num), c_num);
            commentsNum.setText(format);

            if (bean.getData().getC_status() == 1) {
                collectionVideo.setEnabled(false);
                collectionVideo.setImageResource(R.mipmap.icon_collection_details_x_o);
                collectionStatus = bean.getData().getC_status();
            } else {
                collectionVideo.setEnabled(true);
                collectionVideo.setImageResource(R.mipmap.icon_collection_details_x);
                collectionStatus = bean.getData().getC_status();
            }

            //评论数设置在首页
            if (!StringUtil.isEmpty(mPosition)) {
                EventBus.getDefault().post(new VideoDetailsToHomePagerEvent(bean.getData().getC_num(), Integer.valueOf(mPosition)));
            }
//            likeCount.setText(bean.getData().getZan_num());//赞数
//            reserveCount.setText(bean.getData().getOrd_num());//预约数
//            playCount.setText(bean.getData().getLook_num());//播放次数

//            if (bean.getData().getIs_order().equals("1")) {//预约
            //videoReserve.setVisibility(View.VISIBLE);
            //videoReserve.setEnabled(true);


            if (bean.getData().getZ_status() == 1) {
                likeVideo.setImageResource(R.mipmap.icon_zan_details_x_r);
                likeVideo.setEnabled(false);
                zStatus = bean.getData().getZ_status();
            } else {

                likeVideo.setImageResource(R.mipmap.icon_zan_details_x);
                likeVideo.setEnabled(true);
                zStatus = bean.getData().getZ_status();
            }
        } else {
            //shareVideo.setEnabled(false);
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }

    /**
     * 请求网络数据，web 点赞 评论
     *
     * @param bid
     * @param uid
     */
    private RequestBody sendMsg(String bid, String uid) {
        boolean timeFlag = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
        if (timeFlag) {
            String sign = Md5Utils.createMD5("bid=" + bid + "devcode=" + mDevcode + "timestamp=" + mTimestamp + "uid=" + uid + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", String.valueOf(mUserId))
                    .add("bid", String.valueOf(mBid))
                    .add("signature", sign)
                    .add("devcode", mDevcode)
                    .build();
            return requestBody;
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isPermission) {
            mHelper.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == 0) {
            if (resultCode == 11) {
                mUserId = ((MyApplication) getApplication()).getUserId();
                islogin = ((MyApplication) getApplication()).isIslogin();
                bookDetailsBean(mBid, mUserId);
            }
        }
        if (requestCode == 147) {//分享dialog
            if (resultCode == 14) {
                mUserId = MyApplication.mApp.getUserId();
            }
        }
    }


    /**
     * 收藏
     *
     * @param bid    路书id
     * @param uid    用户id
     * @param status 状态
     */
    private void collection(String bid, String uid, int status) {
        RequestBody requestBody = sendMsg(bid, uid, status);
        OkHttpUtils.getInstance().post(URLUtils.BOOK_COLLECTION, requestBody, new HttpCallBack(new BookDetailsCollectionEvent()));
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void collectionBean(BookDetailsCollectionEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        BookDetailsCollectionBean bean = event.getData();
        if (bean.getStatus() == 0) {
            collectionVideo.setEnabled(false);
            collectionVideo.setImageResource(R.mipmap.icon_collection_details_x_o);
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }

    /**
     * 路书收藏点赞预约公用的方法
     *
     * @param bid    路书id
     * @param uid    用户id
     * @param status 状态
     */
    private RequestBody sendMsg(String bid, String uid, int status) {
        boolean timeFlag = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
        RequestBody requestBody = null;
        if (timeFlag) {

            String sign = Md5Utils.createMD5("bid=" + bid + "devcode=" + mDevcode + "status=" + status + "timestamp=" + mTimestamp + "uid=" + uid + URLUtils.WK_APP_KEY);
            requestBody = new FormBody.Builder()
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", String.valueOf(mUserId))
                    .add("bid", String.valueOf(mBid))
                    .add("signature", sign)
                    .add("devcode", mDevcode)
                    .add("status", String.valueOf(status))
                    .build();
            return requestBody;

        }
        return requestBody;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    /**
     * 获取评论列表
     */
    private void getCommentsList(int page) {
        boolean timeFlag = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
        if (timeFlag) {
            String sign = Md5Utils.createMD5("bid=" + mBid + "devcode=" + mDevcode + "p=" + page + "timestamp=" + mTimestamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", String.valueOf(mUserId))
                    .add("bid", String.valueOf(mBid))
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
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        CommentsListBean data = event.getData();
        if (data.getStatus() == 0) {
            if (flag) {
                mCommetsList.clear();
                if (data.getData().size() == 0) {
                    commentsMainList.setVisibility(View.GONE);
                    emptyImg.setVisibility(View.INVISIBLE);
//                    emptyImg.setImageResource(R.mipmap.img_empty_messagelist);
                    adapterEmpty.setText("暂无评论");
                    empty.setVisibility(View.VISIBLE);
                } else {
                    commentsMainList.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                    mCommetsList.addAll(data.getData());
                    commentsMainList.setAdapter(commetsAdapter);
                    commetsAdapter.notifyDataSetChanged();
                }

            } else {
                if (data.getData().size() == 0) {
                    refreshSmart.setLoadmoreFinished(false);
                } else {
                    mCommetsList.addAll(data.getData());
                    commetsAdapter.notifyDataSetChanged();
                    refreshSmart.finishLoadmore();
                }
            }
        }
    }

    /**
     * 发表评论以及回复
     *
     * @param pid        （消息Id，对该游记的评论，pid默认传0）
     * @param bid        （路书Id）
     * @param info(评论内容)
     */
    private void publishComments(String bid, int pid, String info) {
        boolean timeFlag = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
        if (timeFlag) {
            String sign = Md5Utils.createMD5("bid=" + bid + "devcode=" + mDevcode + "info=" + info + "pid=" + pid + "timestamp=" + mTimestamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", String.valueOf(mUserId))
                    .add("bid", String.valueOf(mBid))
                    .add("signature", sign)
                    .add("devcode", mDevcode)
                    .add("pid", String.valueOf(pid))
                    .add("info", info)
                    .build();
            if (mPid != 0) {
                //回复列表数据
                OkHttpUtils.getInstance().post(URLUtils.PUBLISH_COMMENTS, requestBody, new HttpCallBack(new ReplyEvent()));
            } else {
                //主评论数据
                OkHttpUtils.getInstance().post(URLUtils.PUBLISH_COMMENTS, requestBody, new HttpCallBack(new CommentsEvent()));
            }

        }
    }

    /**
     * 获取主评论信息
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getCommentsMessage(CommentsEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        CommentMessageBean commentMessageBean = event.getData();
        if (commentMessageBean.getStatus() == 0) {
            empty.setVisibility(View.GONE);
            commentsMainList.setVisibility(View.VISIBLE);
            //主评论发布成功，本地添加数据进列表中
            String addtime = commentMessageBean.getData().getAddtime();
            String nickname = commentMessageBean.getData().getNickname();
            String info = commentMessageBean.getData().getInfo();
            String pic = commentMessageBean.getData().getPic();
            String bid = commentMessageBean.getData().getBid();
            String cid = commentMessageBean.getData().getCid();
            List<CommentsListBean.ListBean.ChildBean> childBeanList = commentMessageBean.getData().getChildBeanList();

            CommentsListBean.ListBean listBean = new CommentsListBean.ListBean();
            listBean.setAddtime(addtime);
            listBean.setNickname(nickname);
            listBean.setInfo(info);
            listBean.setPic(pic);
            listBean.setBid(bid);
            listBean.setCid(cid);
            listBean.setChildBeanList(childBeanList);
            mCommetsList.add(0, listBean);
            commetsAdapter.notifyDataSetChanged();

            if (!StringUtil.isEmpty(mPosition)) {

                VideoDetailsToHomePagerEvent eventComment = new VideoDetailsToHomePagerEvent(commentMessageBean.getData().getC_num(), Integer.valueOf(mPosition));
                eventComment.setLike(false);
                eventComment.setC_info(info);
                eventComment.setC_nickName(nickname);
                EventBus.getDefault().post(eventComment);
            }
            String format = String.format(getResources().getString(R.string.comments_num), commentMessageBean.getData().getC_num());
            commentsNum.setText(format);
        }


    }

    /**
     * 获取回复消息
     *
     * @param event
     */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getReplyMessage(ReplyEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
        }
        ReplyBean data = event.getData();
        if (data.getStatus() == 0) {
            String bid = data.getData().getBid();
            String cid = data.getData().getCid();
            String b_nickname = data.getData().getB_nickname();
            String nickname = data.getData().getC_nickname();
            String info = data.getData().getC_info();

            List<CommentsListBean.ListBean.ChildBean> childBeanList = mCommetsList.get(mIndex).getChildBeanList();
            CommentsListBean.ListBean.ChildBean childBean = new CommentsListBean.ListBean.ChildBean();
            childBean.setBid(bid);
            childBean.setCid(cid);
            childBean.setB_nickname(b_nickname);
            childBean.setNickname(nickname);
            childBean.setInfo(info);

            childBeanList.add(childBean);
//            mCommetsList.addAll(index,mCommetsList.get(index).getChildBeanList());
            commetsAdapter.notifyDataSetChanged();
            if (!StringUtil.isEmpty(mPosition)) {

                VideoDetailsToHomePagerEvent eventReply = new VideoDetailsToHomePagerEvent(data.getData().getC_num(), Integer.valueOf(mPosition));
                eventReply.setLike(false);
                eventReply.setC_info("");
                eventReply.setC_nickName("");
                EventBus.getDefault().post(eventReply);
            }
            String format = String.format(getResources().getString(R.string.comments_num), data.getData().getC_num());
            commentsNum.setText(format);
        } else {
            Toast.makeText(this, data.getMsg(), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 删除评论
     *
     * @param cid
     */
    private void deleteComments(String cid) {
        boolean timeFlag = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
        if (timeFlag) {
            String sign = Md5Utils.createMD5("cid=" + cid + "devcode=" + mDevcode + "timestamp=" + mTimestamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", String.valueOf(mUserId))
                    .add("signature", sign)
                    .add("devcode", mDevcode)
                    .add("cid", cid)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.DELETE_COMMENTS, requestBody, new HttpCallBack(new DeleteCommentEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void deleteCommentEvent(DeleteCommentEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        BaseBean data = event.getData();
        if (data.getStatus() == 0) {
            //请求成功
            if (isComments) {
                //删除评论
                mCommetsList.remove(mIndex);
                String comment = commentsNum.getText().toString();
                String regEx = "[^0-9]";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(comment);
                String trim = m.replaceAll("").trim();
                int commentInt = Integer.valueOf(trim) - 1;
                String format = String.format(getResources().getString(R.string.comments_num), String.valueOf(commentInt));
                commentsNum.setText(format);
                if (!StringUtil.isEmpty(mPosition)) {
                    EventBus.getDefault().post(new VideoDetailsToHomePagerEvent(String.valueOf(commentInt), Integer.valueOf(mPosition)));
                }
            } else {
                //删除回复
                List<CommentsListBean.ListBean.ChildBean> childBeanList = mCommetsList.get(mIndex).getChildBeanList();
                childBeanList.remove(mReplyIndex);
            }

            commetsAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, data.getMsg(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //释放视频播放器所有
        GSYVideoManager.releaseAllVideos();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void OnAlterApplyPermission() {
        isPermission = false;
//        new ShareBoardDialog(VideoDetailsActivity.this, VideoDetailsActivity.this, web).show();
        shareDialog();
    }

    @Override
    public void cancelListener() {

    }

    @Override
    public void shareSuccess(String bid) {//分享成功的
        shareRequestBackground(mUserId, mDevcode, mTimestamp, bid);
    }

    @Override
    public void shareReport(final String bid) {//举报的
        mHomeShareBoardDialog.dismiss();
        new VideoReportingDialog(this).setVideoReportingCallBack(new VideoReportingDialog.VideoReportingCallBack() {
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
        if (APPUtils.judgeTimeDev(this, devcode, timestamp)) {
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
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        BaseBean bean = event.getData();
        if (bean.getStatus() == 0) {

        } else {
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
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
        if (APPUtils.judgeTimeDev(this, devcode, timestamp)) {
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
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        BaseBean bean = event.getData();
        if (bean.getStatus() == 0) {
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }

    //广播接收内部类
    //private class MyBroadcastReciver extends BroadcastReceiver {
    //    @Override
    //    public void onReceive(Context context, Intent intent) {
    //        String action = intent.getAction();
    //        if (action.equals("刷新")) {
    //            String author = intent.getStringExtra("receiver");
    //            if (!StringUtil.isEmpty(author) && author.equals("1")) {
    //                mUserId = ((MyApplication) getApplication()).getUserId();
    //                islogin = ((MyApplication) getApplication()).isIslogin();
    //                bookDetailsBean(mBid, mUserId);
    //            }
    //        }
    //    }
    //}

    /**
     * bind change collection coupon fans follow login newPerson system ver 等activity发送过来
     *
     * @param receiverBean receiver 是1
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(ReceiverBean receiverBean) {
        if (receiverBean != null && receiverBean.getReceiver().equals("1")) {
            mUserId = ((MyApplication) getApplication()).getUserId();
            islogin = ((MyApplication) getApplication()).isIslogin();
            bookDetailsBean(mBid, mUserId);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在结束时可取消广播
        //unregisterReceiver(myBroadcastReciver);
        GSYVideoType.setShowType(SCREEN_TYPE_DEFAULT);
        GSYVideoManager.releaseAllVideos();
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }


    @Override
    public void onBackPressed() {
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPause = true;
        mVideoView.startPlayLogic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GSYVideoManager.onResume();
        isPause = false;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            mVideoView.onConfigurationChanged(this, newConfig, orientationUtils, true, true);
        }
    }

    private void resolveNormalVideoUI() {
        //增加title
        mVideoView.getTitleTextView().setVisibility(View.GONE);
        mVideoView.getBackButton().setVisibility(View.GONE);
    }

    private GSYVideoPlayer getCurPlay() {
        if (mVideoView.getFullWindowPlayer() != null) {
            return mVideoView.getFullWindowPlayer();
        }
        return mVideoView;
    }
}
