package yc.pointer.trip.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import yc.pointer.trip.R;
import yc.pointer.trip.adapter.UploadLoactionVideoAdapter;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.EntityVideo;
import yc.pointer.trip.bean.eventbean.VideoFinishBean;
import yc.pointer.trip.untils.AppFileMgr;
import yc.pointer.trip.view.EmptyControlVideo;
import yc.pointer.trip.view.SampleCoverVideo;
import yc.pointer.trip.view.ToolbarWrapper;

/**
 * Created by moyan on 2018/3/16.
 * 选择本地视频
 */


public class UploadLoactionVideoActivity extends BaseActivity {

    @BindView(R.id.satandard_toolbar_right)
    TextView satandardToolbarRight;
    @BindView(R.id.clip_video)
    ImageView clipVideo;
    @BindView(R.id.video_play)
    EmptyControlVideo mSampleCoverVideo;
    @BindView(R.id.recycleview_locate_videos)
    RecyclerView recycleviewLocateVideos;
    @BindView(R.id.no_videos)
    TextView noVideos;
    private List<EntityVideo> videoList = new ArrayList<>();

    private String saveVideoPath;
    private int mDuration = 0;
    //private MyBroadcastReciver myBroadcastReciver;
    private boolean isTransition;
    private Transition transition;

    @Override
    protected int getContentViewLayout() {
        return R.layout.upload_location_video;
    }

    @Override
    protected void initView() {
        isTransition = getIntent().getBooleanExtra("TRANSITION", false);
        ToolbarWrapper toolbarWrapper = new ToolbarWrapper(this);
        toolbarWrapper.setCustomTitle(R.string.locate_video);
        toolbarWrapper.setRightText(R.string.next);
        //获取本地视频信息
        videoList = getVideoList(this);
        if (videoList.size() > 0) {
            noVideos.setVisibility(View.GONE);
            recycleviewLocateVideos.setVisibility(View.VISIBLE);
            recycleviewLocateVideos.setLayoutManager(new GridLayoutManager(this, 4));
            UploadLoactionVideoAdapter adapter = new UploadLoactionVideoAdapter(UploadLoactionVideoActivity.this, videoList);
            recycleviewLocateVideos.setAdapter(adapter);

            videoPlay(0);
            saveVideoPath = videoList.get(0).getPath();
            adapter.setListener(new UploadLoactionVideoAdapter.onClickListener() {
                @Override
                public void OnClick(int position) {
                    videoPlay(position);
                    saveVideoPath = videoList.get(position).getPath();
                }
            });
        } else {
            noVideos.setVisibility(View.VISIBLE);
            recycleviewLocateVideos.setVisibility(View.GONE);
        }


        //注册广播
        //IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction("videoFinish");
        //myBroadcastReciver = new MyBroadcastReciver();
        //registerReceiver(myBroadcastReciver, intentFilter);
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_DEFAULT);

    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

    @OnClick({R.id.satandard_toolbar_right, R.id.clip_video})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.satandard_toolbar_right:
                long fileSize = 0;
                try {
                    fileSize = AppFileMgr.getFilesSize(new File(saveVideoPath));
                    long size = 300 * 1048576;
                    if (fileSize < size) {//不能大于300MB
                        Intent intent = new Intent(UploadLoactionVideoActivity.this, WrateVideoActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("videoPath", saveVideoPath);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "文件过大", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.clip_video:
                if (mDuration < 600) {
                    Intent intentClip = new Intent(UploadLoactionVideoActivity.this, ClipVideoActivity.class);
                    intentClip.putExtra("videoPath", saveVideoPath);
                    startActivity(intentClip);
                } else {
                    Toast.makeText(this, "文件时长过长", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 选择视频播放
     *
     * @param position
     */
    private void videoPlay(int position) {
        String path = videoList.get(position).getPath();
        ImageView imageView = new ImageView(UploadLoactionVideoActivity.this);
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(path);
        Bitmap frameAtTime1 = mediaMetadataRetriever.getFrameAtTime();
        imageView.setImageBitmap(frameAtTime1);
        mDuration = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) / 1000;
        mSampleCoverVideo.setThumbImageView(imageView);
        mSampleCoverVideo.getBackButton().setVisibility(View.GONE);
        mSampleCoverVideo.setUp(new File(path).toURI().toString(), false, "");
        initTransition();
        mSampleCoverVideo.setLooping(true);

    }

    /**
     * 查询本地所有视频的方法
     *
     * @param context 上下问菜单
     * @return 返回所有视频信息的集合
     */
    public List<EntityVideo> getVideoList(Context context) {
        List<EntityVideo> sysVideoList = new ArrayList<>();
        String[] projection = {MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATA
                , MediaStore.Audio.Media.DURATION};
        String orderBy = MediaStore.Video.Media.DISPLAY_NAME;
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        List<HashMap<String, String>> contentProvider = getContentProvider(uri, projection, orderBy);
        for (Map<String, String> map : contentProvider) {
            if (map.get("duration") != null) {
                int time = Integer.parseInt(map.get("duration")) / 1000;
                if (time > 2) {
                    String path = map.get("_data");
                    if (new File(path).exists()) {
                        EntityVideo entityVideo = new EntityVideo();
                        entityVideo.setDuration(time);
                        entityVideo.setPath(path);
                        sysVideoList.add(entityVideo);
                    }
                }
            }
        }

        return sysVideoList;
    }


    /**
     * 获取ContentProvider
     *
     * @param projection
     * @param orderBy
     */
    public List<HashMap<String, String>> getContentProvider(Uri uri, String[] projection, String orderBy) {
        // TODO Auto-generated method stub
        List<HashMap<String, String>> listImage = new ArrayList<HashMap<String, String>>();
        Cursor cursor = getContentResolver().query(uri, projection, null,
                null, orderBy);
        if (null == cursor) {
            return null;
        }
        while (cursor.moveToNext()) {
            HashMap<String, String> map = new HashMap<String, String>();
            for (int i = 0; i < projection.length; i++) {
                map.put(projection[i], cursor.getString(i));
                System.out.println(projection[i] + ":" + cursor.getString(i));
            }
            listImage.add(map);
        }
        return listImage;
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
     *
     * @param bean bean
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void videoFinish(VideoFinishBean bean) {
        if (bean != null && bean.getVideoFinish().equals("videoFinish")) {
            finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(myBroadcastReciver);
        GSYVideoManager.releaseAllVideos();
    }

    @Override
    protected void onPause() {
        super.onPause();
        GSYVideoManager.onPause();
    }

    private void initTransition() {
        if (isTransition && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
            ViewCompat.setTransitionName(mSampleCoverVideo, "IMG_TRANSITION");
            addTransitionListener();
            startPostponedEnterTransition();
        } else {
            mSampleCoverVideo.startPlayLogic();
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
                    mSampleCoverVideo.startPlayLogic();
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
