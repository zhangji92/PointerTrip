package yc.pointer.trip.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Transition;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSAuthCredentialsProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
//import com.iceteck.silicompressorr.SiliCompressor;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import yc.pointer.trip.R;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.SetServiceWrateBean;
import yc.pointer.trip.bean.eventbean.VideoFinishBean;
import yc.pointer.trip.event.SetServiceWrateEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.AppFileMgr;
import yc.pointer.trip.untils.ImageUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.PermissionHelper;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.CityPopupWindow;
import yc.pointer.trip.view.DialogKnow;
import yc.pointer.trip.view.DialogSure;
import yc.pointer.trip.view.EmptyControlVideo;
import yc.pointer.trip.view.LoadDialog;
import yc.pointer.trip.view.RoundProgressBar;
import yc.pointer.trip.view.UpLoadFileProgress;

//import VideoHandle.EpDraw;
//import VideoHandle.EpEditor;
//import VideoHandle.EpVideo;
//import VideoHandle.OnEditorListener;

/**
 * Created by moyan on 2017/11/16.
 * 发布路书视频页面
 */

public class WrateVideoActivity extends BaseActivity {

    //    @BindView(R.id.back_button)
//    ImageView backButton;
    @BindView(R.id.standard_toolbar_title)
    TextView standardToolbarTitle;
    @BindView(R.id.satandard_toolbar_right)
    TextView satandardToolbarRight;
    @BindView(R.id.write_city)
    TextView writeCity;//城市
    @BindView(R.id.wrate_video_title)
    EditText wrateVideoTitle;//标题
    @BindView(R.id.introduce_video)
    EditText introduceVideo;//详情
    @BindView(R.id.video_location)
    TextView videoLocation;//定位地点
    @BindView(R.id.text_count)
    TextView textCount;
    @BindView(R.id.videoView_show)
    EmptyControlVideo surface;
    @BindView(R.id.standard_toolbar)
    Toolbar standardToolbar;
    @BindView(R.id.total_text_count)
    TextView totalTextCount;
    private CityPopupWindow cityPopupWindow;
    private String videoPath;//视频地址
    private String mUserId;
    private long mTimestamp;
    private String mDevcode;
    private Bitmap bitmap;
    private String location;
    private String city;
    private String title;
    private String info;
    //private LoadDialog loadDialog;
    private UpLoadFileProgress mLoadDialog;
    private String base64;
    private String saveDir;
    private int videoLength;

    private boolean isPermission = false;//权限申请标志，防止一直TOAST
    private boolean isTransition;
    private Transition transition;
    private PermissionHelper permissionHelper;
    private PermissionHelper.PermissionModel[] permissionModels = {
            new PermissionHelper.PermissionModel(1, Manifest.permission.ACCESS_FINE_LOCATION, "读取您的位置"),

    };
    private String fileName;


    @Override
    protected int getContentViewLayout() {
        return R.layout.wrate_video_layout;
    }

    @Override
    protected void initView() {
        isTransition = getIntent().getBooleanExtra("TRANSITION", false);
        standardToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                AppFileMgr.deleteFiles(videoPath);
//                startActivity(new Intent(WrateVideoActivity.this, RecordVideoActivity.class));

                finish();
            }
        });
        mLoadDialog = new UpLoadFileProgress(this, R.style.user_default_dialog);

        //loadDialog = new LoadDialog(this, "处理上传中...", R.drawable.load_animation);
        mUserId = ((MyApplication) getApplication()).getUserId();
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();
        mDevcode = ((MyApplication) getApplication()).getDevcode();
        videoPath = getIntent().getStringExtra("videoPath");//视频地址
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(videoPath);

        bitmap = media.getFrameAtTime();
        String length = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);//视频长度
        videoLength = Integer.valueOf(length) / 1000;//秒


        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(bitmap);
        surface.setThumbImageView(imageView);
        surface.getBackButton().setVisibility(View.GONE);
        surface.setUp(new File(videoPath).toURI().toString(), false, "");
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_DEFAULT);
//        GSYVideoType.setShowType(GSYVideoType.SCREEN_MATCH_FULL);
        //过渡动画
        initTransition();
        cityPopupWindow = new CityPopupWindow(this, WrateVideoActivity.this, new CityPopupWindow.onCallBackListener() {
            @Override
            public void callBackString(String msg, boolean sureStatus) {
                if (sureStatus) {
                    writeCity.setText(msg);
                }
            }
        });
        introduceVideo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = introduceVideo.getText().toString().trim();
                int length = text.length();
                if (length < 140) {
                    textCount.setText(String.valueOf(length));
                    textCount.setTextColor(Color.parseColor("#1FBB07"));
                } else if (length >= 140) {
                    textCount.setText("140");
                    textCount.setTextColor(Color.parseColor("#d7013a"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


    }

    /**
     * 上传视频到云服务器
     */
    private void putViedosToOSS(String filePath) {
        mLoadDialog.show();
        String endpoint = "oss-cn-shanghai.aliyuncs.com";
        //推荐使用OSSAuthCredentialsProvider。token过期可以及时更新
        OSSCredentialProvider credentialProvider = new OSSAuthCredentialsProvider("http://139.196.106.89:92/");
        //该配置类如果不设置，会有默认配置，具体可看该类
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSS oss = new OSSClient(getApplicationContext(), endpoint, credentialProvider);
        long timeMillis = System.currentTimeMillis() + ((int) (Math.random() * 5000));
        fileName = "android_" + timeMillis + ".mp4";
        PutObjectRequest request = new PutObjectRequest("zztrip", "video/" + fileName, filePath);
        request.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {

            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                long remainingBytes = totalSize - currentSize;
                mLoadDialog.setNum((int) ((totalSize - remainingBytes) * 80 / totalSize));
            }
        });
        oss.asyncPutObject(request, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                //Log.d("LogInterceptor-->", "PutObject+UploadSuccess");
                //Log.d("LogInterceptor", "ETag:" + result.getETag());
                //Log.d("LogInterceptor", "RequestId:" + result.getRequestId());
                //Log.d("LogInterceptor", "getServerCallbackReturnBody" + result.getServerCallbackReturnBody());
                setServiceVideo(fileName, bitmap, title, city, info, location, mUserId);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                // 请求异常
                if (clientException != null) {
                    mLoadDialog.dismiss();
                    // 本地异常如网络异常等
                    //clientException.printStackTrace();
                    Toast.makeText(WrateVideoActivity.this, "网络出小差", Toast.LENGTH_SHORT).show();
                }
                if (serviceException != null) {
                    mLoadDialog.dismiss();
                    Toast.makeText(WrateVideoActivity.this, "服务器异常", Toast.LENGTH_SHORT).show();
                    // 服务异常
                    //Log.e("ErrorCode", serviceException.getErrorCode());
                    //Log.e("RequestId", serviceException.getRequestId());
                    //Log.e("HostId", serviceException.getHostId());
                    //Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });

    }

    /**
     * 添加水印
     */

//    private void addWatermark(String videoPath) {
//        int statusHeight = DensityUtil.getStatusHeight(this);
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//        String bitmapPath = ImageUtils.BitmapPath(bitmap);
//        EpVideo video = new EpVideo(videoPath);
//        EpDraw draw = new EpDraw(bitmapPath, 0, statusHeight, 100, 100, false);
//        video.addDraw(draw);
//        saveDir = Environment.getExternalStorageDirectory() + "/trip/" + System.currentTimeMillis() + ".mp4";
//        EpEditor.exec(video, new EpEditor.OutputOption(saveDir), new OnEditorListener() {
//            @Override
//            public void onSuccess() {
//                flag = 1;
//                mHandler.post(runnable);
//            }
//
//            @Override
//            public void onFailure() {
//                mHandler.post(runnable);
//                flag = 2;
//                Log.e("aaa", "onFailure");
//            }
//
//            @Override
//            public void onProgress(float v) {
//                flag = 0;
////                mHandler.post(runnable);
//
//            }
//        });
//    }

//    int count = 0;
//    private int flag = 0;
//    Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            if (flag == 0) {
//                ++count;
//                Log.e("aaa", "count:" + count);
//            } else if (flag == 1) {
////                flag=0;
//                MediaMetadataRetriever media = new MediaMetadataRetriever();
//                media.setDataSource(saveDir);
//                bitmap = media.getFrameAtTime();
//                base64 = ImageUtils.Bitmap2StrByBase64(bitmap);
//                loadDialog.dismiss();
//                setServiceVideo(saveDir, base64, title, city, info, location, mUserId);//上传视频
//                Log.e("aaa", "完成:" + count);
//            } else if (flag == 2) {
//                Toast.makeText(WrateVideoActivity.this, "处理视频失败", Toast.LENGTH_SHORT).show();
//                Log.e("aaa", "失败:" + count);
//            }
//        }
//    };
    @Override
    protected boolean needEventBus() {
        return true;
    }

    int position = 0;

    @OnClick({R.id.write_city, R.id.video_location, R.id.satandard_toolbar_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
//            case R.id.back_button:
//                AppFileMgr.deleteFiles(videoPath);
//                startActivity(new Intent(this, RecordVideoActivity.class));
//                finish();
//                break;
            case R.id.write_city:
                WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
                layoutParams.alpha = 0.3f;
                getWindow().setAttributes(layoutParams);

                cityPopupWindow.showAtLocation(WrateVideoActivity.this.findViewById(R.id.write_city), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.video_location:
                //TODO 检查定位权限，跳转位置选择列表
                //判断是否为android6.0系统版本，如果是，需要动态添加权限
                if (Build.VERSION.SDK_INT >= 23) {
                    isPermission = true;
                    permissionHelper = new PermissionHelper(this, new PermissionHelper.OnAlterApplyPermission() {
                        @Override
                        public void OnAlterApplyPermission() {
                            isPermission = false;
                            Intent intent = new Intent(WrateVideoActivity.this, DetialLocationActivity.class);
                            startActivityForResult(intent, 0);
                        }

                        @Override
                        public void cancelListener() {

                        }
                    }, permissionModels);
                    permissionHelper.applyPermission();
                } else {
                    Intent intent = new Intent(WrateVideoActivity.this, DetialLocationActivity.class);
                    startActivityForResult(intent, 0);
                }
                break;
            case R.id.satandard_toolbar_right://发布

                //定位
                location = videoLocation.getText().toString();
                if (location.equals("所在位置")) {
                    location = "";
                } else {
                    location = videoLocation.getText().toString();
                }
                //城市
                city = writeCity.getText().toString();
                if (city.equals("请选择城市")) {
                    city = "";
                } else {
                    city = writeCity.getText().toString();
                }
                //标题
                title = wrateVideoTitle.getText().toString();
                info = introduceVideo.getText().toString();
                if (!StringUtil.isEmpty(mUserId)) {
                    if (bitmap != null) {
                        base64 = ImageUtils.Bitmap2StrByBase64(bitmap);
                        if (!StringUtil.isEmpty(title)) {
                            satandardToolbarRight.setClickable(false);
                            satandardToolbarRight.setEnabled(false);
                            if (StringUtil.isEmpty(city) || city.equals("请选择城市")) {
                                new DialogSure(WrateVideoActivity.this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                                    @Override
                                    public void onClickListener(DialogSure dialogSure, boolean cancelable) {
                                        if (cancelable) {
                                            city = "";
                                            putViedosToOSS(videoPath);
                                        } else {
                                            satandardToolbarRight.setClickable(true);
                                            satandardToolbarRight.setEnabled(true);
                                        }
                                    }
                                }).setTitle("温馨提示")
                                        .setMsg("您未填写城市，该游记将不能被预约,是否继续发布？")
                                        .setPositiveButton("继续")
                                        .setNegativeButton("取消")
                                        .show();
                            } else {
                                putViedosToOSS(videoPath);
                            }
                        } else {
                            Toast.makeText(this, "请先填写标题", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "图片未获取成功", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "ID获取失败,请重新登陆", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    /**
     * 上传文件，根据文件大小是否压缩
     */
//    private void isCompress() {
//        long fileSize = 0;
//        try {
//            fileSize = AppFileMgr.getFilesSize(new File(videoPath));
//            long size = 50 * 1048576;
//            if (fileSize < size) {//文件小于10MB不压缩
//                setServiceVideo(videoPath, base64, title, city, info, location, mUserId);
//            } else {//压缩视频
//                CrompresAsyncTask crompresAsyncTask = new CrompresAsyncTask(videoPath, base64, title, city, info, location, mUserId);
//                crompresAsyncTask.execute(videoPath);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isPermission) {
            permissionHelper.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == 0) {
            if (resultCode == 1) {
                String location = data.getStringExtra("location");
                if (!StringUtil.isEmpty(location)) {
                    videoLocation.setText(location);
                }
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        GSYVideoManager.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //释放视频播放器所有
        GSYVideoManager.releaseAllVideos();
    }

    /**
     * 视频发给后台
     */
    private void setServiceVideo(String fileName, Bitmap bitmap, String title, String city, String info, String location, String uid) {
        boolean timeDev = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
        if (timeDev) {
            String pathname = ImageUtils.BitmapToPath(bitmap);
            if (!StringUtil.isEmpty(pathname)) {
                File file = new File(pathname);
                //city=%@devcode=%@info=%@location=%@pic=%@timestamp=%@title=%@uid=%@%@
                String signature = Md5Utils.createMD5("city=" + city + "devcode=" + mDevcode + "info=" + info + "length=" + videoLength +
                        "location=" + location + "timestamp=" + mTimestamp + "title=" + title + "uid=" + uid + "video=" + fileName
                        + URLUtils.WK_APP_KEY);
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                builder.addFormDataPart("city", city);
                builder.addFormDataPart("devcode", mDevcode);
                builder.addFormDataPart("length", String.valueOf(videoLength));
                builder.addFormDataPart("info", info);
                builder.addFormDataPart("location", location);
                builder.addFormDataPart("timestamp", String.valueOf(mTimestamp));
                builder.addFormDataPart("title", title);
                builder.addFormDataPart("uid", uid);
                builder.addFormDataPart("video", fileName);
                builder.addFormDataPart("signature", signature);
                builder.addFormDataPart("img", file.getName(), OkHttpUtils.createCustomRequestBody(MediaType.parse("image/png"), file, new OkHttpUtils.OkProgressListener() {
                    @Override
                    public void onProgress(long totalBytes, long remainingBytes, boolean done) {
                        int num = 80 + (int) ((totalBytes - remainingBytes) * 20 / totalBytes);
                        mLoadDialog.setNum(num);
                    }
                }));
                OkHttpUtils.getInstance().post(URLUtils.WRATE_VIDEO_NEW, builder.build(), new HttpCallBack(new SetServiceWrateEvent()));
            } else {
                Toast.makeText(this, "为获取到第一帧图片", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setServiceVideo(SetServiceWrateEvent event) {
        satandardToolbarRight.setClickable(true);
        satandardToolbarRight.setEnabled(true);
        if (event.isTimeOut()) {
            mLoadDialog.dismiss();
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        SetServiceWrateBean bean = event.getData();
        if (bean.getStatus() == 0) {
            mLoadDialog.dismiss();
            String is_hb = bean.getHb().getIs_hb();
            String hb_info = bean.getHb().getTitle();
            //Intent intentBroadcast = new Intent();
            //intentBroadcast.setAction("videoFinish");
            //sendBroadcast(intentBroadcast);
            EventBus.getDefault().post(new VideoFinishBean("videoFinish"));
            if (!StringUtil.isEmpty(is_hb) && is_hb.equals("1")) {
                new DialogKnow(this, R.style.user_default_dialog, new DialogKnow.CallBackListener() {
                    @Override
                    public void onClickListener() {
                        Intent intent = new Intent(WrateVideoActivity.this, MyMoneyActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).setTitle("温馨提示")
                        .setMsg(hb_info)
                        .setPositiveButton("查看")
                        .show();
            } else {
                Intent intent = new Intent(WrateVideoActivity.this, MainActivity.class);
                startActivity(intent);
            }
        } else {
            mLoadDialog.dismiss();
            APPUtils.intentLogin(WrateVideoActivity.this, bean.getStatus());
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
        }
    }


    private static final int BAIDU_READ_PHONE_STATE = 100;

    //Android6.0申请权限的回调方法
    @Override
    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

//            AppFileMgr.deleteFiles(videoPath);
//            startActivity(new Intent(WrateVideoActivity.this, RecordVideoActivity.class));
            finish();

        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 压缩之后上传视频
     */
//    private class CrompresAsyncTask extends AsyncTask<String, Integer, String> {
//
//        private String path, imgbase64, title, city, info, location, uid;
//        private String compressVideo;
//
//        public CrompresAsyncTask(String path, String imgbase64, String title, String city, String info, String location, String uid) {
//            this.path = path;
//            this.imgbase64 = imgbase64;
//            this.title = title;
//            this.city = city;
//            this.info = info;
//            this.location = location;
//            this.uid = uid;
//        }
//
//        @Override
//        protected String doInBackground(String... strings) {
//            String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
//            try {
//                compressVideo = SiliCompressor.with(WrateVideoActivity.this)
//                        .compressVideo(strings[0], absolutePath, bitmap.getHeight(), bitmap.getWidth(), 2);
//            } catch (URISyntaxException e) {
//                e.printStackTrace();
//            }
//            return compressVideo;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            setServiceVideo(s, imgbase64, title, city, info, location, uid);
//        }
//    }
    private void initTransition() {
        if (isTransition && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
            ViewCompat.setTransitionName(surface, "IMG_TRANSITION");
            addTransitionListener();
            startPostponedEnterTransition();
        } else {
            surface.startPlayLogic();
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
                    surface.startPlayLogic();
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
