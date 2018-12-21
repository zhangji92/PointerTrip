package yc.pointer.trip.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.socialize.media.UMWeb;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import yc.pointer.trip.R;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.eventbean.RefreshLoadBean;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.PermissionHelper;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.TravelSharingDialog;
import yc.pointer.trip.wxapi.PayOrderActivity;

/**
 * Created by 张继
 * 2018/1/15
 * 18:14
 * 红包和认证说明界面
 */

public class ExplainWebActivity extends BaseActivity {
    @BindView(R.id.explain_web)
    WebView explainWeb;
    @BindView(R.id.explain_web_pro)
    ProgressBar explainWebPro;
    @BindView(R.id.standard_toolbar_title)
    TextView standardToolbarTitle;
    @BindView(R.id.standard_toolbar)
    Toolbar standardToolbar;
    @BindView(R.id.explain_relative)
    RelativeLayout explainRelative;
    private String mUserId;
    private String url;
    private String backFlag;
    private boolean isPermission = false;//权限申请标志，防止一直TOAST


    private PermissionHelper permissionHelper;
    private PermissionHelper.PermissionModel[] permissionModels = {
            new PermissionHelper.PermissionModel(1, Manifest.permission.CAMERA, "需要打开您的相机"),
            new PermissionHelper.PermissionModel(2, Manifest.permission.WRITE_EXTERNAL_STORAGE, "获取您的读写权限"),
            new PermissionHelper.PermissionModel(3, Manifest.permission.RECORD_AUDIO, "获取您的麦克风权限")
    };
    //private MyBroadcastReciver myBroadcastReciver;

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_explain_web;
    }

    @Override
    protected void initView() {

        mUserId = ((MyApplication) getApplication()).getUserId();
        String title = getIntent().getStringExtra("title");
        url = getIntent().getStringExtra("url");
        backFlag = getIntent().getStringExtra("backFlag");

        standardToolbarTitle.setText(title);
        standardToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回按钮重写
                overWriteBack();
            }
        });

        netWork(url);//加载网页
        //可见时加载数据相当于Fragment的onResume
        WebSettings webSettings = explainWeb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.supportMultipleWindows();
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDomStorageEnabled(true);//设置适应HTML5的一些方法
        webSettings.setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        //IntentFilter intentDeposit = new IntentFilter();
        //intentDeposit.addAction("重新加载");
        //myBroadcastReciver = new MyBroadcastReciver();
        //registerReceiver(myBroadcastReciver, intentDeposit);
    }

    /**
     * 加载网页
     *
     * @param url
     */
    private void netWork(String url) {
        if (OkHttpUtils.isNetworkAvailable(this)) {
            explainWeb.setVisibility(View.VISIBLE);
            explainRelative.setVisibility(View.GONE);
            explainWeb.loadUrl(url);// 加载本地的html布局文件
            explainWeb.setWebViewClient(new MyWebViewClient());
            explainWeb.setWebChromeClient(new MyWebChromeClient());
        } else {
            explainWeb.setVisibility(View.GONE);
            explainRelative.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (explainWeb != null) {
            explainWeb.setVisibility(View.GONE);
            explainWeb.setWebChromeClient(null);
            explainWeb.setWebViewClient(null);
            explainWeb.getSettings().setJavaScriptEnabled(false);
            explainWeb.clearCache(true);
            explainWeb.removeAllViews();
            explainWeb.destroy();
        }
    }

    @OnClick(R.id.explain_relative)
    public void onViewClicked() {
        netWork(url);
    }


    /**
     * 监听 所有点击的链接，如果拦截到我们需要的，就跳转到相对应的页面。 shouldOverrideUrlLoading
     */
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("&&")) {
                String[] split = url.split("&&");
                for (String type : split) {
                    if (type.equals("htype=hb")) {
                        if (Build.VERSION.SDK_INT < 23) {
                            intentActivity();
                        } else {
                            //申请权限
                            isPermission = true;
                                permissionHelper = new PermissionHelper(ExplainWebActivity.this, new PermissionHelper.OnAlterApplyPermission() {
                                @Override
                                public void OnAlterApplyPermission() {//授权开启
                                    isPermission = false;
                                    intentActivity();
                                }

                                @Override
                                public void cancelListener() {

                                }
                            }, permissionModels);
                            permissionHelper.applyPermission();//申请权限
                        }
                    } else if (type.equals("htype=yj*is_v=0")) {
                        //未填写信息，跳转填写认证信息页面
                        Intent intent = new Intent(ExplainWebActivity.this, VerifyActivity.class);
                        startActivity(intent);
                        finish();

                    } else if (type.equals("htype=yj*is_v=1")) {
                        //提交过信息，直接跳转支付押金页面
                        Intent intent = new Intent(ExplainWebActivity.this, NewUnDepositActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (type.equals("htype=yj*is_v=2")) {
                        //跳转登录
                        Intent intentlogin = new Intent(ExplainWebActivity.this, LoginActivity.class);
                        intentlogin.putExtra("logFlag", "verifyExplainWeb");
                        startActivity(intentlogin);
                    } else if (type.equals("htype=share")) {
                        String shareUrl = url+ "&share=1";
                        //链接
                        UMWeb web = new UMWeb(shareUrl);
                        new TravelSharingDialog(ExplainWebActivity.this,ExplainWebActivity.this).setWeb(web).show();
                    }
                }
            } else {
                Toast.makeText(ExplainWebActivity.this, "格式不正确", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

            explainWeb.setVisibility(View.GONE);
            explainRelative.setVisibility(View.VISIBLE);

        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (isPermission) {
            permissionHelper.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode){
//            case 1:
//                if (resultCode==14){
//                    mUserId = ((MyApplication) getApplication()).getUserId();
//                    url= URLUtils.BASE_URL + "/Home/Book/agreement?uid="+mUserId;
//                    netWork(url);//加载网页
//                }
//        }
    }

    private void intentActivity() {
        if (!StringUtil.isEmpty(mUserId)) {
            Intent intent = new Intent(ExplainWebActivity.this, RecordVideoActivity.class);
            intent.putExtra("logFlag", "lookWorld");
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(ExplainWebActivity.this, LoginActivity.class);
            intent.putExtra("logFlag", "explain_red");
            startActivity(intent);
            finish();
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (explainWeb != null) {
                if (newProgress == 100) {
                    explainWebPro.setVisibility(View.GONE);//加载完网页进度条消失
                } else {
                    explainWebPro.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    explainWebPro.setProgress(newProgress);//设置进度值
                }
            }

        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            overWriteBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 返回按钮点击事件逻辑
     */
    private void overWriteBack() {
        if (!StringUtil.isEmpty(backFlag) && backFlag.equals("adverting")) {
            Intent intent = new Intent(ExplainWebActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            finish();
        }
    }


    //广播接收内部类
    //private class MyBroadcastReciver extends BroadcastReceiver {
    //    @Override
    //    public void onReceive(Context context, Intent intent) {
    //        String action = intent.getAction();
    //        if (action.equals("重新加载")) {
    //
    //            mUserId = ((MyApplication) getApplication()).getUserId();
    //            url = URLUtils.BASE_URL + "/Home/Book/agreement?uid=" + mUserId;
    //            netWork(url);//加载网页
    //            unregisterReceiver(this);
    //        }
    //    }
    //}

    /**
     * 重新加载 Bind Login等activity 发送过来
     * @param bean
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshLoading(RefreshLoadBean bean) {
        if (bean!=null&&bean.getRefreshLoad().equals("重新加载")) {
            mUserId = ((MyApplication) getApplication()).getUserId();
            url = URLUtils.BASE_URL + "/Home/Book/agreement?uid=" + mUserId;
            netWork(url);//加载网页
        }
    }
}


