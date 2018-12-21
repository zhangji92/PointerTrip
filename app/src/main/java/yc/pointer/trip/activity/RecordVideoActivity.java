package yc.pointer.trip.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import yc.pointer.trip.R;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.eventbean.VideoFinishBean;
import yc.pointer.trip.untils.AppFileMgr;
import yc.pointer.trip.untils.ScreenSwitchUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.untils.VideoUtils;

/**
 * Created by moyan on 2017/11/16.
 * 录制
 */

public class RecordVideoActivity extends BaseActivity implements SurfaceHolder.Callback {
    @BindView(R.id.back_record_video)
    ImageButton backRecordVideo;//返回按钮
    @BindView(R.id.camera_record_video)
    ImageButton cameraRecordVideo;//切换摄像头
    @BindView(R.id.falsh_record_video)
    ImageButton falshRecordVideo;//闪光灯开关
    @BindView(R.id.delete_record_video)
    ImageButton deleteRecordVideo;//删除当前视频
    @BindView(R.id.play_record_video)
    ImageButton playRecordVideo;//开始录制
    @BindView(R.id.complete_record_video)
    ImageButton completeRecordVideo;//完成录制
    @BindView(R.id.upload_location)
    ImageButton upload_location;//本地上传
    @BindView(R.id.load_record_video)
    ProgressBar loadRecordVideo;//进度条
    @BindView(R.id.time_record_video)
    TextView timeRecordVideo;//显示时间
    //@BindView(R.id.record_surface)
    private SurfaceView mSurfaceView;

    private SurfaceHolder mSurfaceHolder;
    private boolean isRecording = false;
    private MediaRecorder mMediaRecorder;
    private Camera mCamera;
    private Camera.Parameters mParams;
    private File mFile;
    private int mBitRate = 5;
    private int mDisplayOrientation = 90;
    private int mTime = 0 - 1;
    private int mTimeNum = 0;

    private int mRecordMaxTime = 90;
    private Handler mHandler;
    private String mVideoNeam;//视频名称
    boolean isPause = false;//暂停的标志
    private String saveVideoPath = "";
    private boolean lightFlag = true;//闪光灯的开关标志

    private ScreenSwitchUtils switchUtils;//陀螺仪工具类
    private String currentVideoFilePath;
    private int cameraPosition = 1;//0代表前置摄像头，1代表后置摄像头
    private DisplayMetrics dm;
    private String logFlag;
    private int mWidth;
    private int mHeight;

    //private MyBroadcastReciver myBroadcastReciver;

    @Override
    protected int getContentViewLayout() {

        //初始化传感器
        switchUtils = ScreenSwitchUtils.init(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.record_video_layout;
    }

    @Override
    protected void initView() {
        dm = getApplicationContext().getResources().getDisplayMetrics();
        logFlag = getIntent().getStringExtra("logFlag");//标志
        mHandler = new Handler();
        mSurfaceView = (SurfaceView) this.findViewById(R.id.record_surface);
        mSurfaceHolder = mSurfaceView.getHolder();// 取得holder
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.setKeepScreenOn(true);
        mSurfaceView.setFocusable(true);
        mSurfaceHolder.addCallback(this); // holder加入回调接口
        loadRecordVideo.setMax(mRecordMaxTime);
        timeRecordVideo.setText("00:00");
        deleteRecordVideo.setVisibility(View.GONE);//删除当前视频隐藏
        completeRecordVideo.setVisibility(View.GONE);//完成录制隐藏
        loadRecordVideo.setVisibility(View.GONE);//进度条
        timeRecordVideo.setVisibility(View.GONE);//时间
        switchUtils.setOnCallBack(new ScreenSwitchUtils.onCallBack() {
            @Override
            public void animatorRotation(int type) {
                if (type == 0) {//右转屏
                    mDisplayOrientation = 0;
                    setVideoAnimator(backRecordVideo, 0, 270).start();
                    setVideoAnimator(deleteRecordVideo, 0, 270).start();
                    setVideoAnimator(completeRecordVideo, 0, 270).start();
                    setVideoAnimator(timeRecordVideo, 0, 270).start();
                    setVideoAnimator(cameraRecordVideo, 0, 270).start();
                    setVideoAnimator(falshRecordVideo, 0, 270).start();
                    setVideoAnimator(upload_location, 0, 270).start();
                } else if (type == 1) {//左转屏
                    mDisplayOrientation = 0;
                    setVideoAnimator(backRecordVideo, 0, 90).start();
                    setVideoAnimator(timeRecordVideo, 0, 90).start();
                    setVideoAnimator(deleteRecordVideo, 0, 90).start();
                    setVideoAnimator(completeRecordVideo, 0, 90).start();
                    setVideoAnimator(cameraRecordVideo, 0, 90).start();
                    setVideoAnimator(falshRecordVideo, 0, 90).start();
                    setVideoAnimator(upload_location, 0, 90).start();
                } else {//竖屏
                    mDisplayOrientation = 90;
                    setVideoAnimator(backRecordVideo, 0, 0).start();
                    setVideoAnimator(timeRecordVideo, 0, 0).start();
                    setVideoAnimator(deleteRecordVideo, 0, 0).start();
                    setVideoAnimator(completeRecordVideo, 0, 0).start();
                    setVideoAnimator(cameraRecordVideo, 0, 0).start();
                    setVideoAnimator(falshRecordVideo, 0, 0).start();
                    setVideoAnimator(upload_location, 0, 0).start();
                }
            }
        });

        ////注册广播
        //IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction("videoFinish");
        //myBroadcastReciver = new MyBroadcastReciver();
        //registerReceiver(myBroadcastReciver,intentFilter);

    }

    private Runnable timeRun = new Runnable() {
        @Override
        public void run() {
            mTime++;
            mTimeNum++;
            loadRecordVideo.setProgress(mTime);
            timeRecordVideo.setText(StringUtil.timeFormat(mTimeNum));
            mHandler.postDelayed(timeRun, 1000);
            if (mTime == mRecordMaxTime) {
                stop();
                finishActivity();
            }
        }
    };

    @Override
    protected boolean needEventBus() {
        return true;
    }


    @OnClick({R.id.back_record_video, R.id.camera_record_video, R.id.falsh_record_video, R.id.upload_location, R.id.delete_record_video, R.id.play_record_video, R.id.complete_record_video})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_record_video://返回
                if (mCamera != null) {
                    //释放视频文件并返回
                    setVideoResourseNull();
                }
                break;
            case R.id.camera_record_video://转换摄像头
                switchCamera();
                break;
            case R.id.falsh_record_video://闪光灯
                if (lightFlag) {
                    falshRecordVideo.setImageResource(R.mipmap.flash_off);
                    lightFlag = false;
                    turnLightOn(mCamera, mParams);
                } else {
                    falshRecordVideo.setImageResource(R.mipmap.flash_lamp);
                    lightFlag = true;
                    turnLightOff(mCamera, mParams);
                }
                break;
            case R.id.upload_location://本地上传
                //跳转本地视频上传
                stop();
                mTime = 0;
                mTimeNum = 0;
                timeRecordVideo.setText("00:00");
                loadRecordVideo.setProgress(0);
                if (mFile != null) {
                    AppFileMgr.deleteFile(mFile);
                }
                Intent intent = new Intent(RecordVideoActivity.this, UploadLoactionVideoActivity.class);
                startActivity(intent);

                break;
            case R.id.delete_record_video://删除视频
                stop();
                mTime = 0;
                mTimeNum = 0;
                timeRecordVideo.setText("00:00");
                loadRecordVideo.setProgress(0);
                cameraRecordVideo.setVisibility(View.VISIBLE);//切换摄像头隐藏
                upload_location.setVisibility(View.VISIBLE);//本地上传隐藏
                if (mFile != null) {
                    AppFileMgr.deleteFile(mFile);
                }
                break;
            case R.id.play_record_video://录制视频
                deleteRecordVideo.setVisibility(View.VISIBLE);//删除当前视频显示
                completeRecordVideo.setVisibility(View.VISIBLE);//完成录制显示
                loadRecordVideo.setVisibility(View.VISIBLE);//进度条
                timeRecordVideo.setVisibility(View.VISIBLE);//时间
                cameraRecordVideo.setVisibility(View.INVISIBLE);//切换摄像头隐藏
                upload_location.setVisibility(View.INVISIBLE);//本地上传隐藏
                if (!isRecording) {
                    mTime--;
                    mTimeNum--;
                    start();
                    isPause = false;
                } else {
                    stop();
                    upload_location.setVisibility(View.VISIBLE);//本地上传显示
                    mCamera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                            if (success == true)
                                RecordVideoActivity.this.mCamera.cancelAutoFocus();
                        }
                    });
                    isPause = true;
                    if (saveVideoPath.equals("")) {
                        saveVideoPath = currentVideoFilePath;
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String[] str = new String[]{saveVideoPath, currentVideoFilePath};
                                    VideoUtils.appendVideo(RecordVideoActivity.this, AppFileMgr.getSDPath(RecordVideoActivity.this) + "append.mp4", str);
                                    File reName = new File(saveVideoPath);
                                    File f = new File(AppFileMgr.getSDPath(RecordVideoActivity.this) + "append.mp4");
                                    f.renameTo(reName);
                                    if (reName.exists()) {
                                        f.delete();
                                        new File(currentVideoFilePath).delete();
                                    }
                                } catch (IOException e) {

                                    e.printStackTrace();

                                }
                            }
                        }).start();
                    }
                }
                break;
            case R.id.complete_record_video://完成录制
                finishActivity();
                break;

        }
    }

    /**
     * 结束录制，释放视频资源
     */
    private void setVideoResourseNull() {
        stop();
        mTime = 0;
        mTimeNum = 0;
        timeRecordVideo.setText("00:00");
        loadRecordVideo.setProgress(0);
        if (mFile != null) {
            AppFileMgr.deleteFile(mFile);
        }
        if (!StringUtil.isEmpty(logFlag) && logFlag.equals("lookWorld")) {
//            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            finish();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }


    @Override
    protected void onStart() {
        super.onStart();
        switchUtils.start(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        switchUtils.stop();
    }

    /**
     * 旋转动画
     *
     * @param view 控件
     * @param x    坐标
     * @param y    坐标
     * @return ObjectAnimator
     */
    private ObjectAnimator setVideoAnimator(View view, float x, float y) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", x, y);
        animator.setDuration(500);
        return animator;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (null == mCamera) {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);//TODO 默认打开后置摄像头
            camera();
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        camera();
        //if (switchUtils.isPortrait()) {//竖屏
        mParams.set("orientation", "portrait");
        mCamera.setDisplayOrientation(90);
        //} else {//横屏
        //    mParams.set("orientation", "landscape");
        //    mCamera.setDisplayOrientation(0);
        //}
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        if (mMediaRecorder != null) {
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setPreviewDisplay(null);
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mCamera.lock();
        }
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    private void camera() {
        try {
            mCamera.stopPreview();
            mParams = mCamera.getParameters();
            setPreviewSize(mParams);
            mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            mCamera.setParameters(mParams);
            mCamera.startPreview();
            //mCamera.unlock();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据手机支持的视频分辨率，设置预览尺寸
     *
     * @param params
     */
    private void setPreviewSize(Camera.Parameters params) {
        if (mCamera == null) {
            return;
        }
        //获取手机支持的分辨率集合，并以宽度为基准降序排序
        List<Camera.Size> previewSizes = params.getSupportedPreviewSizes();
        Collections.sort(previewSizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                if (lhs.width > rhs.width) {
                    return -1;
                } else if (lhs.width == rhs.width) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
        float tmp = 0f;
        float minDiff = 100f;
        float ratio = 2.0f / 4.0f;//TODO 高宽比率3:4，且最接近屏幕宽度的分辨率，可以自己选择合适的想要的分辨率
        Camera.Size best = null;
        for (Camera.Size s : previewSizes) {
            tmp = Math.abs(((float) s.height / (float) s.width) - ratio);
            if (tmp < minDiff) {
                minDiff = tmp;
                best = s;
            }
        }


        params.setPreviewSize(best.width, best.height);//预览比率

        //TODO 大部分手机支持的预览尺寸和录制尺寸是一样的，也有特例，有些手机获取不到，那就把设置录制尺寸放到设置预览的方法里面
        if (params.getSupportedVideoSizes() == null || params.getSupportedVideoSizes().size() == 0) {
            mWidth = best.width;
            mHeight = best.height;
        } else {
            setVideoSize(params);
        }
    }

    /**
     * 根据手机支持的视频分辨率，设置录制尺寸
     *
     * @param params
     */
    private void setVideoSize(Camera.Parameters params) {
        if (mCamera == null) {
            return;
        }
        //获取手机支持的分辨率集合，并以宽度为基准降序排序
        List<Camera.Size> previewSizes = params.getSupportedVideoSizes();
        Collections.sort(previewSizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                if (lhs.width > rhs.width) {
                    return -1;
                } else if (lhs.width == rhs.width) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });

        float tmp = 0f;
        float minDiff = 100f;
        float ratio = 2.0f / 4.0f;//高宽比率3:4，且最接近屏幕宽度的分辨率
        Camera.Size best = null;
        for (Camera.Size s : previewSizes) {
            tmp = Math.abs(((float) s.height / (float) s.width) - ratio);
            if (tmp < minDiff) {
                minDiff = tmp;
                best = s;
            }
        }
        //设置录制尺寸
        mWidth = best.width;
        mHeight = best.height;
    }

    /**
     * 开始录制
     */
    protected void start() {

        playRecordVideo.setImageResource(R.mipmap.suspend);
        mVideoNeam = "VID_" + AppFileMgr.getModifyTime() + ".mp4";
        mFile = new File(AppFileMgr.getSDPath(this) + mVideoNeam);
        if (mFile.exists()) {
            // 如果文件存在，删除它，演示代码保证设备上只有一个录音文件
            mFile.delete();
        }
        mCamera.stopPreview();
        mCamera.unlock();
        mMediaRecorder = new MediaRecorder();// 创建mediarecorder对象
        mMediaRecorder.setCamera(mCamera);
        // 设置录制视频源为Camera(相机)
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        // 录像旋转90度
        mMediaRecorder.setOrientationHint(mDisplayOrientation);
        // 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        // // 设置录制的视频编码h263 h264
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setVideoSize(mWidth, mHeight);//设置分辨率
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        // 设置高质量录制,改变编码速率
        mMediaRecorder.setVideoEncodingBitRate(mBitRate * 1024 * 512);
        // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());
        // 设置视频文件输出的路径
        currentVideoFilePath = AppFileMgr.getSDPath(RecordVideoActivity.this) + mVideoNeam;
        mMediaRecorder.setOutputFile(currentVideoFilePath);
        try {
            // 准备、开始
            mMediaRecorder.prepare();
            mMediaRecorder.start();//开始刻录
            isRecording = true;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        mMediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {

            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                // 发生错误，停止录制
                mMediaRecorder.stop();
                mMediaRecorder.reset();
                mMediaRecorder.release();
                mMediaRecorder = null;
                isRecording = false;

            }
        });
        mHandler.post(timeRun);
    }

    protected void stop() {
        playRecordVideo.setImageResource(R.mipmap.play);
        if (isRecording) {
            // 如果正在录制，停止并释放资源
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setPreviewDisplay(null);
//            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            isRecording = false;
            mHandler.removeCallbacks(timeRun);
        }
    }

    /**
     * 完成录制
     */
    private void finishActivity() {
        if (isPause) {
            Intent intent = new Intent(RecordVideoActivity.this, WrateVideoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("videoPath", saveVideoPath);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        } else {
            stop();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!(saveVideoPath.equals(""))) {
                            String[] str = new String[]{saveVideoPath, currentVideoFilePath};
                            VideoUtils.appendVideo(RecordVideoActivity.this, AppFileMgr.getSDPath(RecordVideoActivity.this) + "append.mp4", str);
                            File reName = new File(saveVideoPath);
                            File f = new File(AppFileMgr.getSDPath(RecordVideoActivity.this) + "append.mp4");
                            f.renameTo(reName);
                            if (reName.exists()) {
                                f.delete();
                                new File(currentVideoFilePath).delete();
                            }

                        }
                        Intent intent = new Intent(RecordVideoActivity.this, WrateVideoActivity.class);
                        Bundle bundle = new Bundle();
                        if (saveVideoPath.equals("")) {
                            bundle.putString("videoPath", currentVideoFilePath);

                        } else {
                            bundle.putString("videoPath", saveVideoPath);
                        }
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    /**
     * 通过设置Camera打开闪光灯
     */
    public static void turnLightOn(Camera camera, Camera.Parameters parameters) {
        if (camera == null) {
            return;
        }
        if (parameters == null) {
            return;
        }
        List<String> flashModes = parameters.getSupportedFlashModes();
        if (flashModes == null) {
            return;
        }
        String flashMode = parameters.getFlashMode();
        if (!Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(parameters);
            } else {
            }
        }
    }

    /**
     * 通过设置Camera关闭闪光灯
     *
     * @param camera
     */
    public static void turnLightOff(Camera camera, Camera.Parameters parameters) {
        if (camera == null) {
            return;
        }
        if (parameters == null) {
            return;
        }
        List<String> flashModes = parameters.getSupportedFlashModes();
        String flashMode = parameters.getFlashMode();
        // Check if camera flash exists
        if (flashModes == null) {
            return;
        }
        if (!Camera.Parameters.FLASH_MODE_OFF.equals(flashMode)) {
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(parameters);
            } else {


            }
        }
    }

    /**
     * 切换摄像头
     */
    private void switchCamera() {
        //切换前后摄像头
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数

        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
            if (cameraPosition == 1) {
                //现在是后置，变更为前置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    mCamera.stopPreview();//停掉原来摄像头的预览
                    mCamera.release();//释放资源
                    mCamera = null;//取消原来摄像头
                    mCamera = Camera.open(i);//打开当前选中的摄像头
                    mCamera.setDisplayOrientation(90);
                    camera();
                    try {
                        mCamera.setPreviewDisplay(mSurfaceHolder);//通过surfaceview显示取景画面
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mCamera.startPreview();//开始预览
                    cameraPosition = 0;
                    break;
                }
            } else {
                //现在是前置， 变更为后置-->后置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    mCamera.stopPreview();//停掉原来摄像头的预览
                    mCamera.release();//释放资源
                    mCamera = null;//取消原来摄像头
                    mCamera = Camera.open(i);//打开当前选中的摄像头
                    mCamera.setDisplayOrientation(90);
                    camera();
                    try {
                        mCamera.setPreviewDisplay(mSurfaceHolder);//通过surfaceview显示取景画面
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    mCamera.startPreview();//开始预览
                    cameraPosition = 1;
                    break;
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mCamera != null) {
                setVideoResourseNull();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    ////广播接收内部类
    //private class MyBroadcastReciver extends BroadcastReceiver {
    //    @Override
    //    public void onReceive(Context context, Intent intent) {
    //        String action = intent.getAction();
    //        if (action.equals("videoFinish")) {
    //            stop();
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
            stop();
            finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(myBroadcastReciver);
    }
}
