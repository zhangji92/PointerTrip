package yc.pointer.trip.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import yc.pointer.trip.R;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.base.PressUpInterface;
import yc.pointer.trip.bean.eventbean.VideoFinishBean;
import yc.pointer.trip.untils.AppFileMgr;
import yc.pointer.trip.untils.DensityUtil;
import yc.pointer.trip.untils.TrimVideoUtils;
import yc.pointer.trip.view.EmptyControlVideo;
import yc.pointer.trip.view.HorizontalView;
import yc.pointer.trip.view.LoadDialog;
import yc.pointer.trip.view.ToolbarWrapper;
import yc.pointer.trip.view.VideoSeekBar;

/**
 * Created by 张继
 * 2018/3/19
 * 12:04
 * 公司:天津悦程嘉和网络科技有限公司
 * 描述: 剪切视频类
 */

public class ClipVideoActivity extends BaseActivity implements PressUpInterface {
    @BindView(R.id.clip_video_linear)
    LinearLayout clipVideoLinear;
    @BindView(R.id.clip_video_view)
    HorizontalView clipVideoView;
    @BindView(R.id.clip_video_seekBar)
    VideoSeekBar clipVideoSeekBar;
    @BindView(R.id.satandard_toolbar_right)
    TextView satandardToolbarRight;
    @BindView(R.id.clip_video_control)
    EmptyControlVideo clipVideoControl;
    private int sTime;
    private MediaMetadataRetriever mMetadataRetriever;
    /**
     * 图片的时间间隔
     */
    private int mTimeInterval = 6;
    private int width;//总宽度
    private int imgWidth;//图片的宽度
    private int mScrollTime = 0;
    private float videoFrame;
    private String path;
    private int startS;
    private int endS;
    private String absolutePath;
    //private MyBroadcastReciver myBroadcastReciver;
    private boolean isTransition;
    private Transition transition;
    private int imgCount;
    private LoadDialog loadDialog;

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_clip_video;
    }

    @Override
    protected void initView() {

        isTransition = getIntent().getBooleanExtra("TRANSITION", false);
        new ToolbarWrapper(this).setCustomTitle(R.string.clip_video_title);
        satandardToolbarRight.setText("下一步");
        imgWidth = DensityUtil.getScreenWidth(this) / 5;//图片的宽度
        //视频的路径
//        path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Honor.mp4";
        path = getIntent().getStringExtra("videoPath");
//        path = AppFileMgr.getSDCartPath() + "/Honor.mp4";
        //获取视频关键帧间隔 - 如果获取失败,则默认最少需要裁剪3秒长度的视频1
        videoFrame = 3000;
        //创建MediaMetadataRetriever对象
        mMetadataRetriever = new MediaMetadataRetriever();
        //为MediaMetadataRetriever设置视频资源
        mMetadataRetriever.setDataSource(path);
        //获取视频的总时长（以毫秒为单位）
        sTime = Integer.parseInt(mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        File file = new File(path);
        // 判断是否本地文件
        if (file.exists()) {
            new VideoAsyncTask().execute();
        }
        clipVideoLinear.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private boolean playFlag = false;
            @Override
            public void onGlobalLayout() {
                width = clipVideoLinear.getWidth();
                if (width == 0) {
                    width = 1;
                }
                if (width > 1 && !playFlag) {
                    int xTime = sTime / width;
                    clipVideoSeekBar.setTime(xTime, videoFrame, imgWidth * 2, width);
                    playFlag = true;
                }
            }
        });
        clipVideoView.setScrollViewListener(new HorizontalView.ScrollViewListener() {
            @Override
            public void onScrollChanged(HorizontalView scrollView, int newX, int newY, int oldx, int oldy) {
                int xTime = sTime / width;
                mScrollTime = newX * xTime;
            }
        });
        clipVideoSeekBar.setPressUpInterface(this);
        clipVideoView.setPressUpInterface(this);
        Bitmap frameAtTime = mMetadataRetriever.getFrameAtTime();
        ImageView imageView = new ImageButton(this);
        imageView.setImageBitmap(frameAtTime);
        clipVideoControl.setThumbImageView(imageView);
        clipVideoControl.getBackButton().setVisibility(View.GONE);
        clipVideoControl.setUp(new File(path).toURI().toString(), false, "");
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_DEFAULT);
        initTransition();//播放视频

        //注册广播
        //IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction("videoFinish");
        //myBroadcastReciver = new MyBroadcastReciver();
        //registerReceiver(myBroadcastReciver, intentFilter);

    }


    @Override
    protected boolean needEventBus() {
        return true;
    }


    @OnClick(R.id.satandard_toolbar_right)
    public void onViewClicked() {
        loadDialog = new LoadDialog(ClipVideoActivity.this, "裁剪中...", R.drawable.load_animation);
        loadDialog.show();
        TrimVideoUtils trimVideoUtils = TrimVideoUtils.getInstance();
        trimVideoUtils.setTrimCallBack(new TrimVideoUtils.TrimFileCallBack() {
            @Override
            public void trimError(int eType) {
                Message msg = new Message();
                msg.what = TrimVideoUtils.TRIM_FAIL;
                switch (eType) {
                    case TrimVideoUtils.FILE_NOT_EXISTS: // 文件不存在
                        msg.obj = "视频文件不存在";
                        break;
                    case TrimVideoUtils.TRIM_STOP: // 手动停止裁剪
                        msg.obj = "停止裁剪";
                        break;
                    case TrimVideoUtils.TRIM_FAIL:
                    default: // 裁剪失败
                        msg.obj = "裁剪失败";
                        break;
                }
                cutHandler.sendMessage(msg);
            }

            @Override
            public void trimCallback(boolean isNew, double startS, double endS, double vTotal, File file, File trimFile) {
                /**
                 * 裁剪回调
                 * @param isNew 是否新剪辑
                 * @param starts 开始时间(秒)
                 * @param ends 结束时间(秒)
                 * @param vTime 视频长度
                 * @param file 需要裁剪的文件路径
                 * @param trimFile 裁剪后保存的文件路径
                 */
                // ===========
                System.out.println("isNew : " + isNew);
                System.out.println("startS : " + startS);
                System.out.println("endS : " + endS);
                System.out.println("vTotal : " + vTotal);
                System.out.println("file : " + file.getAbsolutePath());
                absolutePath = trimFile.getAbsolutePath();
                System.out.println("trimFile : " + absolutePath);
                // --
                cutHandler.sendEmptyMessage(TrimVideoUtils.TRIM_SUCCESS);
            }
        });
        // 需要裁剪的视频路径
        String videoPath = path;
// 保存的路径
        String savePath = AppFileMgr.getSDCartPath() + File.separator + "trip" + File.separator + System.currentTimeMillis() + "_cut.mp4";
// ==
        final File file = new File(videoPath); // 视频文件地址
        final File trimFile = new File(savePath);// 裁剪文件保存地址
// 获取开始时间
        startS = (int) (clipVideoSeekBar.getStartTime() + mScrollTime) / 1000;
// 获取结束时间
        endS = ((int) (clipVideoSeekBar.getEndTime() + mScrollTime) / 1000);
        if (endS - startS > 30) {
            endS = startS + 29;
        }
        // 进行裁剪
        new Thread(new Runnable() {
            @Override
            public void run() {
                try { // 开始裁剪
                    TrimVideoUtils.getInstance().startTrim(true, startS, endS, file, trimFile);

                } catch (Exception e) {
                    e.printStackTrace();
                    // 设置回调为null
                    TrimVideoUtils.getInstance().setTrimCallBack(null);
                }
            }
        }).start();
//        // --
//        Toast.makeText(MainActivity.this, "开始裁剪 - 开始: " + startS  + "秒, 结束: " + endS + "秒", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void press() {
        clipVideoControl.onVideoPause();
    }

    @Override
    public void up() {
        float startTime = clipVideoSeekBar.getStartTime() + mScrollTime;//开始的帧
        clipVideoControl.setSeekOnStart((long) startTime);
        clipVideoControl.startPlayLogic();
    }


    /**
     * 获取视频图片集合的异步线程
     */
    class VideoAsyncTask extends AsyncTask<Void, Integer, List<Bitmap>> {
        private ProgressDialog mProgressDialog;

        @Override
        protected List<Bitmap> doInBackground(Void... voids) {
            return getBitmap();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(ClipVideoActivity.this,
                        "加载中...", "请稍等...", true);
            } else {
                mProgressDialog.show();
            }
        }

        @Override
        protected void onPostExecute(List<Bitmap> bitmaps) {
            super.onPostExecute(bitmaps);
            for (int i = 0; i < bitmaps.size(); i++) {
                ImageView imageView = new ImageView(ClipVideoActivity.this);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setImageBitmap(bitmaps.get(i));
                clipVideoLinear.addView(imageView);
            }
            mProgressDialog.dismiss();
            mProgressDialog.cancel();
//            mProgressDialog = null;
        }
    }

    /**
     * 获取图片集合
     *
     * @return 返回图片集合
     */
    public List<Bitmap> getBitmap() {
        List<Bitmap> bitmaps = new ArrayList<>();
        try {
            int width = imgWidth;
            int height = imgWidth * 2;
            // 取得视频的长度(单位为毫秒)
            imgCount = sTime / (mTimeInterval * 1000);
            for (int i = 0; i < imgCount; i++) {
                int timeUs = i * mTimeInterval * 1000 * 1000;
                Bitmap frameAtTime = mMetadataRetriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

                if (frameAtTime != null) {
                    Bitmap bitmap = ThumbnailUtils.extractThumbnail(frameAtTime, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                    bitmaps.add(bitmap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放构造器资源
                mMetadataRetriever.release();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return bitmaps;
    }


    /**
     * 裁剪处理 Handler
     */

    private Handler cutHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case TrimVideoUtils.TRIM_FAIL: // 裁剪失败
                    loadDialog.dismiss();
                    Toast.makeText(ClipVideoActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    // --
                    clipVideoSeekBar.setEnabled(true);
                    break;
                case TrimVideoUtils.TRIM_SUCCESS: // 裁剪成功
                    loadDialog.dismiss();
                    Toast.makeText(ClipVideoActivity.this, "裁剪成功", Toast.LENGTH_SHORT).show();
                    // --
                    clipVideoSeekBar.setEnabled(true);
                    Intent intent = new Intent(ClipVideoActivity.this, WrateVideoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("videoPath", absolutePath);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };


    @Override
    protected void onPause() {
        super.onPause();
        GSYVideoManager.onPause();
    }

    ////广播接收内部类
    //private class MyBroadcastReciver extends BroadcastReceiver {
    //    @Override
    //    public void onReceive(Context context, Intent intent) {
    //        String action = intent.getAction();
    //        if (action.equals("videoFinish")) {
    //            finish();
    //        }
    //    }
    //}

    /**
     * 发布路数界面传递过来消息
     * @param bean bean
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void videoFinish(VideoFinishBean bean){
        if (bean.getVideoFinish().equals("videoFinish")){
            finish();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(myBroadcastReciver);
        //释放视频播放器所有
//        clipVideoControl.setStandardVideoAllCallBack(null);
//        GSYVideoPlayer.releaseAllVideos();
    }

    private void initTransition() {
        if (isTransition && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
            ViewCompat.setTransitionName(clipVideoControl, "IMG_TRANSITION");
            addTransitionListener();
            startPostponedEnterTransition();
        } else {
            clipVideoControl.startPlayLogic();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean addTransitionListener() {
        transition = getWindow().getSharedElementEnterTransition();
        if (transition != null) {
            transition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                    Log.e("aaa", "onTransitionStart");
                }
                @Override
                public void onTransitionEnd(Transition transition) {
                    Log.e("aaa", "onTransitionEnd");
                    clipVideoControl.startPlayLogic();
                    transition.removeListener(this);
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                    Log.e("aaa", "onTransitionCancel");
                }

                @Override
                public void onTransitionPause(Transition transition) {
                    Log.e("aaa", "onTransitionPause");
                }

                @Override
                public void onTransitionResume(Transition transition) {
                    Log.e("aaa", "onTransitionResume");
                }
            });
            return true;
        }
        return false;
    }
}
