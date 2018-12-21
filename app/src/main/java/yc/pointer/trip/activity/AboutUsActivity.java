package yc.pointer.trip.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import yc.pointer.trip.R;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.ApkUpdateBean;
import yc.pointer.trip.event.ApkUpdateEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.receiver.DownloadService;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.AppApplicationMgr;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.PermissionHelper;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.DialogApk;
import yc.pointer.trip.view.DialogKnow;
import yc.pointer.trip.view.ToolbarWrapper;

/**
 * Created by 刘佳伟
 * 2017/7/21
 * 10:33
 * 关于我们
 */
public class AboutUsActivity extends BaseActivity {
    @BindView(R.id.current_version)
    TextView currentVersion;//当前版本号
    @BindView(R.id.check_version)
    TextView checkText;//版本图标
    @BindView(R.id.version_updating)
    RelativeLayout versionUpdating;//点击更新版本
    @BindView(R.id.service_phone)
    TextView servicePhone;//客服电话
    private String versionName;//版本号
    private long mTimestamp;//时间戳
    private String mDevcode;//手机识别码
    //    private String mUserId;
    private List<ApkUpdateBean.DataBeanX.DataBean> mListData = new ArrayList<>();
    private PermissionHelper mHelper;
    private PermissionHelper.PermissionModel[] permissionModels = {new PermissionHelper.PermissionModel(1, Manifest.permission.CALL_PHONE, "拨打电话")};
    private boolean isCall = true;
    private boolean isUpdate = false;
    private UMShareAPI mShareAPI;


    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_about_us;
    }

    @Override
    protected void initView() {


        new ToolbarWrapper(this).setCustomTitle(R.string.about_us);
        versionName = AppApplicationMgr.getVersionName(this);
        currentVersion.setText("V" + versionName);
        mDevcode = ((MyApplication) getApplication()).getDevcode();
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();
//        mUserId = ((MyApplication) getApplication()).getUserId();
//        if (!StringUtil.isEmpty(mUserId)){
//            getApkUpdateMsg();
//        }
        mShareAPI = UMShareAPI.get(this);

        getApkUpdateMsg();
    }

    /**
     * 获取版本更新的信息
     */
    private void getApkUpdateMsg() {
        boolean timeFlag = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
        if (timeFlag) {
            String str = Md5Utils.createMD5("devcode=" + mDevcode + "timestamp=" + mTimestamp + "type=" + 1 + "version=" + versionName + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", String.valueOf(mDevcode))
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("type", String.valueOf(1))
                    .add("version", versionName)
                    .add("signature", str)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.APK_UPDATE, requestBody, new HttpCallBack(new ApkUpdateEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getApkUpdateMsg(ApkUpdateEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        final ApkUpdateBean bean = event.getData();

        if (bean.getStatus() == 0) {
            isUpdate = true;
            mListData.addAll(bean.getData().getData());
            Drawable drawable = getResources().getDrawable(R.mipmap.about_version_back);
            // 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            checkText.setCompoundDrawables(null, null, drawable, null);
        } else {
//            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }

    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

    @OnClick({R.id.current_version, R.id.version_updating, R.id.service_phone, R.id.weixin_gongzhong})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.current_version:

                break;
            case R.id.weixin_gongzhong:
                //跳转微信
                boolean isHaveWeiXin = mShareAPI.isInstall(this, SHARE_MEDIA.WEIXIN);
                if (isHaveWeiXin) {
                    IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
                    msgApi.openWXApp();
                } else {
                    Toast.makeText(this, "亲，您尚未安装微信客户端,请先前往应用商店下载", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.version_updating:
                //获取版本信息
//                getApkUpdateMsg();
                if (isUpdate) {
                    new DialogApk(this, R.style.user_default_dialog, new DialogApk.CallBackListener() {
                        @Override
                        public void onClickButton(boolean enableStatus) {
                            if (enableStatus) {
                                //Todo 动态存储权限
                                Intent serviceIntent = new Intent(AboutUsActivity.this, DownloadService.class);
                                //将下载地址url放入intent中
                                serviceIntent.putExtra("URL", "https://www.zhizhentrip.com/apk/app-debug.apk");
                                startService(serviceIntent);
                            } else {

                            }
                        }
                    }).setMsg(mListData).setPositiveButton("立即更新").setNegativeButton("稍后再说").show();
                } else {

                    Toast.makeText(this, "已是最新版本", Toast.LENGTH_SHORT).show();

                }
                break;
            case R.id.service_phone:
                if (Build.VERSION.SDK_INT < 23) {
                    call();
                } else {
                    mHelper=new PermissionHelper(AboutUsActivity.this, new PermissionHelper.OnAlterApplyPermission() {
                        @Override
                        public void OnAlterApplyPermission() {
                            call();
                        }

                        @Override
                        public void cancelListener() {

                        }
                    },permissionModels);
                    mHelper.applyPermission();//申请权限
                    //申请权限
                    //Applypermission();
                }
                break;
        }
    }

    /**
     * 打电话
     */
    private void call() {
        final String phoneNum = servicePhone.getText().toString().trim();
        if (!StringUtil.isEmpty(phoneNum)) {
            new DialogKnow(AboutUsActivity.this, R.style.user_default_dialog, new DialogKnow.CallBackListener() {
                @SuppressLint("MissingPermission")
                @Override
                public void onClickListener() {
                    if (!StringUtil.isEmpty(phoneNum)) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        Uri data = Uri.parse("tel:" + phoneNum);
                        intent.setData(data);
                        startActivity(intent);
                    }
                }
            }).setTitle("联系我们").setMsg(phoneNum).setPositiveButton("呼叫").show();
        } else {
            return;
        }


    }


    @Override
    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mHelper.onRequestPermissionsResult(requestCode,permissions,grantResults);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        mHelper.onActivityResult(requestCode, resultCode, data);

    }
}
