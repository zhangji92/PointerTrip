package yc.pointer.trip.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.GSYVideoProgressListener;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.activity.NewPersonalHomePageActivity;
import yc.pointer.trip.activity.VideoDetailsActivity;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseViewHolder;
import yc.pointer.trip.bean.BookBean;
import yc.pointer.trip.bean.HomeVideoNewDataBean;
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
 * 描述: 首页ViewPager里面Fragment的RecyclerVIew的适配器
 */

public class ItemHomeMoveFragmentRecyclerAdapterOne extends RecyclerView.Adapter<ItemHomeMoveFragmentRecyclerAdapterOne.FragmentViewHolder> {
    private List<HomeVideoNewDataBean.DataBean.DataGoodBean> mList;

    public ItemHomeMoveFragmentRecyclerAdapterOne(List<HomeVideoNewDataBean.DataBean.DataGoodBean> res) {
        this.mList = res;
    }


    @Override
    public FragmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_fragment_video_move, parent, false);
        return new FragmentViewHolder(v, parent.getContext());
    }

    @Override
    public void onBindViewHolder(FragmentViewHolder holder, int position) {
        if (mList.size() > 0) {
            holder.onBindNormalViewHolder(position);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() == 0 ? 0 : mList.size();
    }


    class FragmentViewHolder extends BaseViewHolder {
        public final static String TAG = "RecyclerView2List";
        @BindView(R.id.good_itemview)
        LinearLayout goodItemView;//最外围布局
        @BindView(R.id.home_video_page)
        LinearLayout homeVideoPage;//最外围布局
        @BindView(R.id.liner_good_video)
        LinearLayout linerGoodVideo;//精选及更多布局
        @BindView(R.id.relate_head)
        RelativeLayout relateHead;//头像的外围布局（包含VIP标识）
        @BindView(R.id.home_video_fragment_headimg)
        CustomCircleImage homeVideoFragmentHeadimg;//头像
        @BindView(R.id.verify_result)
        ImageView verifyResult;//VIP标识

        @BindView(R.id.home_video_fragment_nick)
        TextView homeVideoFragmentNick;//昵称
        @BindView(R.id.video_collection_count)
        TextView videoCollectionCount;//收藏数量
        @BindView(R.id.home_video_title)
        TextView homeVideoTitle;//视频标题
        @BindView(R.id.video_length)
        TextView length;//视频时长
        @BindView(R.id.home_play_num)
        TextView homePlayNum;//播放数
        @BindView(R.id.home_hamburger)
        ImageView homeZanNum;//三个点的点击事件

        @BindView(R.id.video_attention)
        ImageView mTvAttention;//关注

        @BindView(R.id.item_sample_video)
        SampleCoverVideo mListPlayerVideo;//视频播放

        BookBean dataGoodBean;

        private int mPosition = 0;
        private int imgHeight = 300;
        private int widthPixels;
        private Context mContext;
        ImageView mCoverImg;//封面图
        private GSYVideoOptionBuilder gsyVideoOptionBuilder;

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
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mListPlayerVideo.getLayoutParams();
            if (than > 1) {//竖屏
                //layoutParams.width = (int) paramsWidth;
                layoutParams.width = widthPixels;
                layoutParams.height = imgHeight;
                mListPlayerVideo.setLayoutParams(layoutParams);
            } else {//横屏
                layoutParams.height = (int) paramsHeight;
                layoutParams.width = widthPixels;
                mListPlayerVideo.setLayoutParams(layoutParams);
            }

            //增加封面
            mCoverImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
            OkHttpUtils.displayGlide(mContext, mCoverImg, mList.get(position).getB_pic());
            //隐藏增加title
            mListPlayerVideo.getTitleTextView().setVisibility(View.GONE);
            //隐藏设置返回键
            mListPlayerVideo.getBackButton().setVisibility(View.GONE);
            //昵称
            homeVideoFragmentNick.setText(mList.get(position).getNickname());
            //title
            homeVideoTitle.setText(mList.get(position).getTitle());
            videoCollectionCount.setText(mList.get(position).getC_num());//评论数
            homePlayNum.setText(mList.get(position).getLook_num());//播放次数
            length.setText(mList.get(position).getLength());//视频的长度
            String userId = MyApplication.mApp.getUserId();


//            String is_jie = mList.get(position).getIs_jie();
//
//            if (!StringUtil.isEmpty(is_jie) && is_jie.equals("2")) {
//                verifyResult.setVisibility(View.VISIBLE);//VIP认证标识
//            } else {
//                verifyResult.setVisibility(View.GONE);
//            }
            if (!StringUtil.isEmpty(userId)) {
                if (mList.get(position).getUid().equals(userId)) {
                    mTvAttention.setVisibility(View.GONE);
                } else {
                    if (mList.get(position).getF_status().equals("1")) {//已关注
                        mTvAttention.setImageResource(R.mipmap.icon_focus_s);
                    } else {//未关注
                        mTvAttention.setImageResource(R.mipmap.icon_add_g);
                    }
                }
            } else {
                mTvAttention.setImageResource(R.mipmap.icon_add_g);
            }

            dataGoodBean = mList.get(position);
            mCoverImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListPlayerVideo.getStartButton().getVisibility() == View.VISIBLE) {
                        mPosition = position;
                        Intent intent = new Intent(mContext, VideoDetailsActivity.class);
                        intent.putExtra("dataGoodBean", dataGoodBean);
                        intent.putExtra("position", String.valueOf(position));
                        mContext.startActivity(intent);
                    } else {
                        mListPlayerVideo.getStartButton().setVisibility(View.VISIBLE);
                    }
                }
            });
            mListPlayerVideo.setClick(new SampleCoverVideo.VideoCallBackClick() {
                @Override
                public void click() {
                    if (mListPlayerVideo.getStartButton().getVisibility() == View.VISIBLE) {
                        mPosition = position;
                        Intent intent = new Intent(mContext, VideoDetailsActivity.class);
                        intent.putExtra("dataGoodBean", dataGoodBean);
                        intent.putExtra("position", String.valueOf(position));
                        mContext.startActivity(intent);
                    } else {
                        mListPlayerVideo.getStartButton().setVisibility(View.VISIBLE);
                    }
                }
            });
            String pic = mList.get(position).getPic();
            String is_vip = mList.get(position).getIs_vip();
            OkHttpUtils.displayGlideCircular(mContext, homeVideoFragmentHeadimg, pic,verifyResult,is_vip);
            homeVideoFragmentHeadimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(mContext, NewPersonalHomePageActivity.class);
                    intent.putExtra("uid", dataGoodBean.getUid());
                    mContext.startActivity(intent);
                }
            });

            videoCollectionCount.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPosition = position;
                    //TODO 跳转视频播放
                    Intent intent = new Intent(mContext, VideoDetailsActivity.class);
                    intent.putExtra("dataGoodBean", dataGoodBean);
                    intent.putExtra("position", String.valueOf(position));
                    mContext.startActivity(intent);
                }
            });

            homeZanNum.setOnClickListener(new OnClickListener() {//汉堡包
                @Override
                public void onClick(View view) {
                    if (mItemHomeMoveRecycler != null) {
                        mItemHomeMoveRecycler.share(mList.get(position));
                    }
                }
            });


            mTvAttention.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItemHomeMoveRecycler != null) {
                        mItemHomeMoveRecycler.attention(mTvAttention, position, mList.get(position).getUid());
                    }
                }
            });
            /**
             * 播放点击判断是否时wifi状态下，如果不是显示弹框
             */
            mListPlayerVideo.getStartButton().setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentState = mListPlayerVideo.getCurrentState();
                    if (currentState == 2) {//正在播放
                        GSYVideoManager.onPause();
                        relateHead.setVisibility(View.VISIBLE);
                    } else if (currentState == 5) {
                        GSYVideoManager.onResume();
                        relateHead.setVisibility(View.INVISIBLE);
                    } else {
                        ScrollCalculatorHelper.getInstance().startPlayLogic(mListPlayerVideo, mContext);

                    }
                }
            });

            //防止错位，离开释放
            mListPlayerVideo.initUIState();
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
            mListPlayerVideo.initUIState();
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
                            if (!mListPlayerVideo.isIfCurrentIsFullscreen()) {
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
                            mListPlayerVideo.getCurrentPlayer().getTitleTextView().setText((String) objects[0]);
                        }
                    }).build(mListPlayerVideo);
        }
    }

    private ItemHomeMoveRecycler mItemHomeMoveRecycler;

    public void setItemHomeMoveRecycler(ItemHomeMoveRecycler mItemHomeMoveRecycler) {
        this.mItemHomeMoveRecycler = mItemHomeMoveRecycler;
    }

    public interface ItemHomeMoveRecycler {
        void attention(ImageView textView, int position, String uuid);//关注

        void share(BookBean dataGoodBean);//分享
    }
}
