package yc.pointer.trip.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shuyu.gsyvideoplayer.utils.GSYVideoType;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseViewHolder;
import yc.pointer.trip.bean.BookBean;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.CustomCircleImage;
import yc.pointer.trip.view.EmptyControlVideo;

/**
 * Created by moyan on 2018/3/27.
 */

public class VideoDetailCommentAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int TYPE_ONE = 1;//头布局
    private static final int TYPE_COMMENTS = 2;//评论数据


    private Context mContext;
    private BookBean dataGoodBean;
    private boolean isTransition;
    private Transition transition;

    public VideoDetailCommentAdapter(Context mContext, BookBean dataGoodBean,boolean isTransition) {
        this.mContext = mContext;
        this.dataGoodBean = dataGoodBean;
        this.isTransition = isTransition;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();

        if (viewType == TYPE_ONE) {
            View viewPlayVideo = LayoutInflater.from(mContext).inflate(R.layout.video_detail_play, parent, false);
            VideoDetailPlayHolder holder = new VideoDetailPlayHolder(viewPlayVideo, mContext);
            return holder;

        } else {
            View viewComments = LayoutInflater.from(mContext).inflate(R.layout.video_comments, parent, false);
            VdieoDetailCommentHolder commentHolder = new VdieoDetailCommentHolder(viewComments, mContext);
            return commentHolder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_ONE) {
            ((VideoDetailPlayHolder)holder).onBindplayHolder();
        }


    }

    @Override
    public int getItemCount() {

        return 1;

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_ONE;
        } else {
            return TYPE_COMMENTS;
        }
    }

    public static class VdieoDetailCommentHolder extends BaseViewHolder {

        public VdieoDetailCommentHolder(View itemView, Context context) {
            super(itemView, context);
        }
    }

    public class VideoDetailPlayHolder extends BaseViewHolder {

        @BindView(R.id.video_details_surfaceview)
        EmptyControlVideo mVideoView;//视频播放
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
        @BindView(R.id.video_reserve)
        Button videoReserve;//预约服务
        @BindView(R.id.video_title)
        TextView videoTitle;//视频标题
        @BindView(R.id.video_introduce_content)
        TextView videoIntroduceContent;//视频介绍
        @BindView(R.id.play_count)
        TextView playCount;//播放次数
        @BindView(R.id.like_count)
        TextView likeCount;//点赞次数
        @BindView(R.id.reserve_count)
        TextView reserveCount;//预约次数
        @BindView(R.id.comments_num)
        TextView commentsNum;

        public VideoDetailPlayHolder(View itemView, Context context) {
            super(itemView, context);
            ButterKnife.bind(this, itemView);

        }

        private void onBindplayHolder() {
            DisplayMetrics dm = mContext.getApplicationContext().getResources().getDisplayMetrics();
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
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mVideoView.getLayoutParams();
            if (penNum == 0) {
                layoutParams.height = width * (4 / 3);
            } else {
                layoutParams.height = (int) height;
            }
            mVideoView.setLayoutParams(layoutParams);
            ImageView imageView = new ImageView(mContext);
            //imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            OkHttpUtils.displayImg(imageView, dataGoodBean.getB_pic());
            mVideoView.setThumbImageView(imageView);
            mVideoView.getBackButton().setVisibility(View.GONE);
            mVideoView.setUp(URLUtils.BASE_URL + dataGoodBean.getVideo(), true, "");
            GSYVideoType.setShowType(GSYVideoType.SCREEN_MATCH_FULL);
            mVideoView.startPlayLogic();

            String strTimeDate = StringUtil.getStrTimeTomm(dataGoodBean.getAddtime1());
            Date date = StringUtil.getStrTimeDate(strTimeDate);
            if (strTimeDate != null) {
                String format = StringUtil.format(date);
                videoDate.setText(format);
            }
            OkHttpUtils.displayImg(videoDetailsHead, dataGoodBean.getPic());//头像
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
            playCount.setText(dataGoodBean.getLook_num());
            likeCount.setText(dataGoodBean.getZan_num());
            reserveCount.setText(dataGoodBean.getOrd_num());//预约次数
        }



    }


}
