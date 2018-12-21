package yc.pointer.trip.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;

import moe.codeest.enviews.ENPlayView;
import yc.pointer.trip.R;

/**
 * Created by 张继
 * 2018/5/16
 * 10:39
 * 公司：
 * 描述：
 */

public class SampleCoverVideo extends StandardGSYVideoPlayer {

    ImageView mCoverImage;

    String mCoverOriginUrl;
    int mDefaultRes;

    public SampleCoverVideo(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public SampleCoverVideo(Context context) {
        super(context);
    }

    public SampleCoverVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(final Context context) {
        super.init(context);
        mCoverImage = (ImageView) findViewById(R.id.thumbImage);
        FrameLayout relativeLayout = findViewById(R.id.surface_container);
        relativeLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click != null) {
                    click.click();
                }
            }
        });
        if (mThumbImageViewLayout != null &&
                (mCurrentState == -1 || mCurrentState == CURRENT_STATE_NORMAL || mCurrentState == CURRENT_STATE_ERROR)) {
            mThumbImageViewLayout.setVisibility(VISIBLE);
        }
    }


    @Override
    public int getLayoutId() {
        return R.layout.list_player_video;
    }

    @Override
    public void updateStartImage() {
     if (mStartButton instanceof ImageView) {
            ImageView imageView = (ImageView) mStartButton;
            if (mCurrentState == CURRENT_STATE_PLAYING) {
                //播放中
                imageView.setImageResource(R.mipmap.icon_play_on_sy);
            } else if (mCurrentState == CURRENT_STATE_ERROR) {
                //播放错误
                imageView.setImageResource(com.shuyu.gsyvideoplayer.R.drawable.video_click_error_selector);
            } else {
                imageView.setImageResource(R.mipmap.icon_play_sy_home);
            }
        }
    }

    public void loadCoverImage(String url, int res) {
        mCoverOriginUrl = url;
        mDefaultRes = res;
        Glide.with(getContext())
                .load(url)
                .error(res)
                .placeholder(res)
                .into(mCoverImage);
    }

    @Override
    public GSYBaseVideoPlayer startWindowFullscreen(Context context, boolean actionBar, boolean statusBar) {
        GSYBaseVideoPlayer gsyBaseVideoPlayer = super.startWindowFullscreen(context, actionBar, statusBar);
        SampleCoverVideo sampleCoverVideo = (SampleCoverVideo) gsyBaseVideoPlayer;
        sampleCoverVideo.loadCoverImage(mCoverOriginUrl, mDefaultRes);
        return gsyBaseVideoPlayer;
    }

    private VideoCallBackClick click;

    public void setClick(VideoCallBackClick click) {
        this.click = click;
    }

    public interface VideoCallBackClick {
        void click();
    }
}