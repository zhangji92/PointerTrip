package yc.pointer.trip.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.activity.LoginActivity;
import yc.pointer.trip.activity.NewPersonalHomePageActivity;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseViewHolder;
import yc.pointer.trip.bean.BookBean;
import yc.pointer.trip.bean.CommentBean;
import yc.pointer.trip.bean.DataGoodBean;
import yc.pointer.trip.bean.ZanBean;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.ScrollCalculatorHelper;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.CustomCircleImage;
import yc.pointer.trip.view.SampleCoverVideo;

/**
 * Created by 张继
 * 2018/4/10
 * 13:59
 * 公司:天津悦程嘉和网络科技有限公司
 * 描述: 新版首页 item 0809
 */

public class NewHomeFragmentMoveItemFollowAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<DataGoodBean> mList;
    private int TYPE = 1;//未登录的标志
    public NewHomeFragmentMoveItemFollowAdapter(List<DataGoodBean> res) {
        this.mList = res;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (StringUtil.isEmpty(MyApplication.mApp.getUserId())) {
            TYPE = 1;
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_fragment_video_move_head, parent, false);
            return new FragmentViewHolderHead(v, parent.getContext());
        } else if (mList.size() == 0) {
            TYPE = 2;
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_fragment_video_move_data, parent, false);
            return new FragmentViewHolderData(v, parent.getContext());
        } else {
            TYPE = 3;
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_fragment_move_item, parent, false);
            return new FragmentViewHolder(v, parent.getContext());
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (mList.size() > 0 && TYPE == 3) {
            ((FragmentViewHolder) holder).onBindNormalViewHolder(position);
        } else if (TYPE == 1) {//未登录时的界面
            ((FragmentViewHolderHead) holder).bind();
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() == 0 ? 1 : mList.size();
    }

    //未登录界面
    class FragmentViewHolderHead extends BaseViewHolder {
        @BindView(R.id.home_fragment_two)
        Button goodItemView;//最外围布局
        private Context mContext;

        public FragmentViewHolderHead(View itemView, Context context) {
            super(itemView, context);
            mContext = context;
            ButterKnife.bind(this, itemView);
        }

        public void bind() {
            goodItemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mContext.startActivity(new Intent(mContext, LoginActivity.class));
                }
            });
        }
    }

    //登陆没数据界面
    class FragmentViewHolderData extends BaseViewHolder {

        public FragmentViewHolderData(View itemView, Context context) {
            super(itemView, context);
        }
    }


    class FragmentViewHolder extends BaseViewHolder {
        public final static String TAG = "RecyclerView2List";

        @BindView(R.id.move_cover_video)
        SampleCoverVideo moveCoverVideo;
        @BindView(R.id.move_video_title)
        TextView moveVideoTitle;
        @BindView(R.id.move_play_num)
        TextView movePlayNum;
        @BindView(R.id.move_head)
        CustomCircleImage moveHead;
        @BindView(R.id.move_vip)
        ImageView moveVip;
        @BindView(R.id.move_nick)
        TextView moveNick;
        @BindView(R.id.move_time)
        TextView moveTime;
        @BindView(R.id.move_attention)
        ImageView moveAttention;
        @BindView(R.id.move_content)
        TextView moveContent;
        @BindView(R.id.move_full_text)
        TextView moveFullText;
        @BindView(R.id.move_comment_person)
        RecyclerView moveCommentPerson;
        @BindView(R.id.move_like)
        RecyclerView moveLike;
        @BindView(R.id.move_like_num)
        TextView moveLikeNum;
        @BindView(R.id.move_comment_num)
        TextView moveCommentNum;
        @BindView(R.id.move_hamburger)
        ImageView moveHamburger;
        @BindView(R.id.zan_a)
        ImageView zanImg;

        @BindView(R.id.liner_intentDetail)
        LinearLayout intenVideo;

        private BookBean dataGoodBean;
        private int mPosition = 0;
        private int imgHeight = 300;
        private int widthPixels;
        private Context mContext;
        private ImageView mCoverImg;//封面图
        private GSYVideoOptionBuilder gsyVideoOptionBuilder;
        private LikeHomeAdapter likeHomeAdapter;
        private CommentHomeAdapter commentHomeAdapter;

        public FragmentViewHolder(View itemView, Context context) {
            super(itemView, context);
            this.mContext = context;
            ButterKnife.bind(this, itemView);
            mCoverImg = new ImageView(context);
            gsyVideoOptionBuilder = new GSYVideoOptionBuilder();
            DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
            widthPixels = dm.widthPixels;
            if (widthPixels <= 480) {
                imgHeight = 300;
            } else if (widthPixels <= 720) {
                imgHeight = 600;
            } else if (widthPixels <= 1080) {
                imgHeight = 800;
            } else {
                imgHeight = 800;
            }
        }

        private void onBindNormalViewHolder(final int position) {

            double width = Double.valueOf(mList.get(position).getWidth());
            double height = Double.valueOf(mList.get(position).getHeight());
            double than = height / width;
            double thanH = width / height;
            double paramsWidth = imgHeight * thanH;//paramsWidth
            final double paramsHeight = widthPixels * than;
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) moveCoverVideo.getLayoutParams();
            if (than > 1) {//竖屏
                layoutParams.width = widthPixels;
                layoutParams.height = imgHeight;
                moveCoverVideo.setLayoutParams(layoutParams);
            } else {//横屏
                layoutParams.height = (int) paramsHeight;
                layoutParams.width = widthPixels;
                moveCoverVideo.setLayoutParams(layoutParams);
            }
            String info = mList.get(position).getInfo();

            //增加封面
            mCoverImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
            String is_vip = mList.get(position).getIs_vip();
            OkHttpUtils.displayGlide(mContext, mCoverImg, mList.get(position).getB_pic());
            OkHttpUtils.displayGlideCircular(mContext,moveHead,mList.get(position).getPic(),moveVip,is_vip);
            //隐藏增加title
            moveCoverVideo.getTitleTextView().setVisibility(View.GONE);
            //隐藏设置返回键
            moveCoverVideo.getBackButton().setVisibility(View.GONE);
            //昵称
            moveNick.setText(mList.get(position).getNickname());
            //title
            String title = mList.get(position).getTitle();
            moveVideoTitle.setText(title);//游记标题

            String userId = MyApplication.mApp.getUserId();
            String is_jie = mList.get(position).getIs_jie();
            String zan_num = mList.get(position).getZan_num();
            String addtime1 = mList.get(position).getAddtime1();
            String strTimeDate = StringUtil.getStrTimeTomm(addtime1);
            String look_num = mList.get(position).getLook_num();
            String c_num = mList.get(position).getC_num();
            String z_status = mList.get(position).getZ_status();
            List<CommentBean> comment = mList.get(position).getComment();//评论列表信息

            //简介
            if (!StringUtil.isEmpty(info)) {
                moveContent.setText(info);
                moveContent.post(new Runnable() {
                    @Override
                    public void run() {
                        //需要异步获取行数
                        int lineCount = moveContent.getLineCount();
                        if (lineCount >= 2) {
//                            moveFullText.setVisibility(View.VISIBLE);
                        } else {
                            moveFullText.setVisibility(View.GONE);
                        }
                    }
                });
            } else {
                moveFullText.setVisibility(View.GONE);
                moveContent.setText("这位作者很懒，没有添加描述~");
                moveContent.setTextColor(Color.parseColor("#b1b1b1"));
            }



            moveCommentNum.setText(c_num);//评论数

            movePlayNum.setText(look_num);//播放次数

            moveLikeNum.setText(zan_num);//点赞次数

            if (!StringUtil.isEmpty(z_status)&&z_status.equals("1")){
                Drawable drawable = mContext.getResources().getDrawable(R.mipmap
                        .icon_zan_b_red);
                moveLikeNum.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
            }else {
                Drawable drawable = mContext.getResources().getDrawable(R.mipmap
                        .icon_zan_b);
                moveLikeNum.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
            }


            Date date = StringUtil.getStrTimeDate(strTimeDate);
            if (strTimeDate != null) {
                String format = StringUtil.format(date);
                moveTime.setText(format);//日期
            }

            if (!StringUtil.isEmpty(userId)) {
                if (mList.get(position).getUid().equals(userId)) {
                    moveAttention.setVisibility(View.GONE);
                } else {
                    if (mList.get(position).getF_status().equals("1")) {//已关注
                        moveAttention.setImageResource(R.mipmap.icon_concern_had);
                    } else {//未关注
                        moveAttention.setImageResource(R.mipmap.icon_concern);
                    }
                }
            } else {
                moveAttention.setImageResource(R.mipmap.icon_concern);
            }

            LinearLayoutManager commentLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
            moveCommentPerson.setLayoutManager(commentLayoutManager);
            commentHomeAdapter = new CommentHomeAdapter(comment);
            moveCommentPerson.setAdapter(commentHomeAdapter);
            List<ZanBean> zan = mList.get(position).getZan();    //点赞人的头像
            LinearLayoutManager likeLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            moveLike.setLayoutManager(likeLayoutManager);
            if (zan.size()>0) {
                zanImg.setVisibility(View.VISIBLE);
            }else {
                zanImg.setVisibility(View.INVISIBLE);
            }
            likeHomeAdapter = new LikeHomeAdapter(zan);
            moveLike.setAdapter(likeHomeAdapter);

            dataGoodBean = mList.get(position);


            //播放器封面点击事件
            mCoverImg.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (moveCoverVideo.getStartButton().getVisibility() == View.VISIBLE) {
                        if (mItemHomeMoveRecycler != null) {
                            mItemHomeMoveRecycler.intenVieoDetial(moveLikeNum,position,likeHomeAdapter,commentHomeAdapter);
                        }
                    } else {
                        moveCoverVideo.getStartButton().setVisibility(View.VISIBLE);
                    }
                }
            });
            //播放器点击事件
            moveCoverVideo.setClick(new SampleCoverVideo.VideoCallBackClick() {
                @Override
                public void click() {
                    if (moveCoverVideo.getStartButton().getVisibility() == View.VISIBLE) {
                        if (mItemHomeMoveRecycler != null) {
                            mItemHomeMoveRecycler.intenVieoDetial(moveLikeNum,position,likeHomeAdapter,commentHomeAdapter);
                        }
                    } else {
                        moveCoverVideo.getStartButton().setVisibility(View.VISIBLE);
                    }
                }
            });

            //评论数点击的回调
            moveCommentNum.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItemHomeMoveRecycler != null) {
                        mItemHomeMoveRecycler.intenVieoDetial(moveLikeNum,position,likeHomeAdapter,commentHomeAdapter);
                    }
                }
            });

            //详情展示布局点击跳转游记详情
            intenVideo.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItemHomeMoveRecycler != null) {
                        mItemHomeMoveRecycler.intenVieoDetial(moveLikeNum,position,likeHomeAdapter,commentHomeAdapter);
                    }
                }
            });

            //头像的点击回调
            moveHead.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, NewPersonalHomePageActivity.class);
                    intent.putExtra("uid", dataGoodBean.getUid());
                    mContext.startActivity(intent);
                }
            });


            //更多的点击回调（三个点的按钮点击回调）

            moveHamburger.setOnClickListener(new OnClickListener() {//汉堡包
                @Override
                public void onClick(View view) {
                    if (mItemHomeMoveRecycler != null) {
                        mItemHomeMoveRecycler.share(mList.get(position));
                    }
                }
            });

            //关注的请求操作（接口回调）
            moveAttention.setOnClickListener(new OnClickListener() {//关注
                @Override
                public void onClick(View view) {
                    if (mItemHomeMoveRecycler != null) {
                        mItemHomeMoveRecycler.attention(moveAttention, position, mList.get(position).getUid());
                    }
                }
            });

            //点赞的请求操作
            moveLikeNum.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemHomeMoveRecycler != null) {
                        String bid = mList.get(position).getBid();
                        mItemHomeMoveRecycler.Addlike(zanImg,moveLikeNum,position,likeHomeAdapter,bid);
                    }
                }
            });



            /**
             * 播放点击判断是否时wifi状态下，如果不是显示弹框
             */
            moveCoverVideo.getStartButton().setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (moveCoverVideo.getCurrentState() == 2) {//正在播放
                        GSYVideoManager.onPause();
                    } else if (moveCoverVideo.getCurrentState() == 5) {
                        GSYVideoManager.onResume();
                    } else {
                        ScrollCalculatorHelper.getInstance().startPlayLogic(moveCoverVideo, mContext);
                    }
                }
            });
            String video = mList.get(position).getVideo();
            if (video.contains("https:")) {
                playVideoForUrl(position, video);
            } else {
                playVideoForUrl(position, URLUtils.BASE_URL + video);
            }
        }

        /**
         * 播放网络视频
         *
         * @param position
         * @param url
         */
        private void playVideoForUrl(int position, String url) {

            //防止错位，离开释放
            moveCoverVideo.initUIState();
            gsyVideoOptionBuilder
                    .setIsTouchWiget(false)
                    .setThumbImageView(mCoverImg)
                    .setUrl(url)
                    .setSetUpLazy(true)//lazy可以防止滑动卡顿
                    .setCacheWithPlay(true)
                    .setRotateViewAuto(true)
                    .setLockLand(true)
                    .setPlayTag(TAG)
                    .setShowFullAnimation(true)
                    .setNeedLockFull(true)
                    .setPlayPosition(position)
                    .setVideoAllCallBack(new GSYSampleCallBack() {
                        @Override
                        public void onPrepared(String url, Object... objects) {
                            super.onPrepared(url, objects);
                            if (!moveCoverVideo.isIfCurrentIsFullscreen()) {
                                //静音
                                GSYVideoManager.instance().setNeedMute(false);
                            }
                        }

                        @Override
                        public void onQuitFullscreen(String url, Object... objects) {
                            super.onQuitFullscreen(url, objects);
                            //全屏不静音
                            GSYVideoManager.instance().setNeedMute(true);
                        }

                        @Override
                        public void onEnterFullscreen(String url, Object... objects) {
                            super.onEnterFullscreen(url, objects);
                            GSYVideoManager.instance().setNeedMute(false);
                            moveCoverVideo.getCurrentPlayer().getTitleTextView().setText((String) objects[0]);
                        }
                    }).build(moveCoverVideo);
        }
    }

    private ItemHomeMoveRecycler mItemHomeMoveRecycler;

    public void setItemHomeMoveRecycler(ItemHomeMoveRecycler mItemHomeMoveRecycler) {
        this.mItemHomeMoveRecycler = mItemHomeMoveRecycler;
    }

    public interface ItemHomeMoveRecycler {
        void attention(ImageView textView, int position, String uuid);//关注
        void Addlike(ImageView zanImg,TextView likeTextView, int i, LikeHomeAdapter likeHomeAdapter, String bid);
        void intenVieoDetial(TextView likeTextView, int index, LikeHomeAdapter likeHomeAdapter, CommentHomeAdapter commentHomeAdapter);//跳转详情的回调
        void share(BookBean dataGoodBean);//分享
    }
}
