package yc.pointer.trip.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import yc.pointer.trip.R;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.DensityUtil;
import yc.pointer.trip.untils.PermissionHelper;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.ToolbarWrapper;
import yc.pointer.trip.view.TravelSharingDialog;

/**
 * Created by moyan on 2018/10/24.
 */

public class InvitionTaskActivity extends BaseActivity {

    private View decorView;//状态栏布局

    @BindView(R.id.book_details_pro)
    ProgressBar invitePro;
    @BindView(R.id.agreement_web)
    WebView inviteWeb;

    @BindView(R.id.book_details_wifi)
    ImageView webWifi;
    @BindView(R.id.book_details_reload)
    Button webReload;
    @BindView(R.id.agreement_relative)
    RelativeLayout reloadRelative;

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_invite_task;
    }

    @Override
    protected void initView() {
        new ToolbarWrapper(this).setCustomTitle("指针邀你一起旅行");
        inviteWeb.setVisibility(View.VISIBLE);
        reloadRelative.setVisibility(View.GONE);
//        decorView = getWindow().getDecorView();
//        //沉浸式
//        if (Build.VERSION.SDK_INT >= 21) {
//            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            DensityUtil.setStatusBarColor(this, R.color.transparent);
//        }else {
//            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        }

        WebSettings webSettings = inviteWeb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.supportMultipleWindows();
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDomStorageEnabled(true);//设置适应HTML5的一些方法
        webSettings.setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        final String loadUrl = getIntent().getStringExtra("loadUrl");
        if (OkHttpUtils.isNetworkAvailable(this)) {
            //网络正常
            if (!StringUtil.isEmpty(loadUrl)){
                inviteWeb.loadUrl(loadUrl);
            }else {
                //网络错误
                inviteWeb.setVisibility(View.GONE);
                reloadRelative.setVisibility(View.VISIBLE);
                Toast.makeText(this, "加载出错！", Toast.LENGTH_SHORT).show();
            }

        } else {
            //网络错误
            inviteWeb.setVisibility(View.GONE);
            reloadRelative.setVisibility(View.VISIBLE);
        }

        inviteWeb.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                if (url.contains("&&")) {
                    String[] split = url.split("&&");
                    for (String type : split) {
                        if (type.equals("htype=share")) {
                            String shareUrl = loadUrl+ "&share=1";
                            //链接
                            UMWeb web = new UMWeb(shareUrl);
                            web.setTitle("「指针自由行-APP」" + "邀请您一起开启随心之旅");//标题
                            web.setThumb(new UMImage(InvitionTaskActivity.this, R.mipmap.invite_share));  //缩略图
                            web.setDescription("带你一起玩赚旅游，开启旅游全新模式，我在这里等你");//描述
                            TravelSharingDialog travelSharingDialog = new TravelSharingDialog(InvitionTaskActivity.this, InvitionTaskActivity.this);
                            travelSharingDialog.setWeb(web).show();
                            travelSharingDialog.setListener(new TravelSharingDialog.ShareSuccessListener() {
                                @Override
                                public void shareSuccess() {
//                                    inviteWeb.reload();
                                }
                            });
                        }
                    }
                } else {
                    Toast.makeText(InvitionTaskActivity.this, "格式错误", Toast.LENGTH_SHORT).show();
                }
//                view.loadUrl(url);
                return true;
            }
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

                inviteWeb.setVisibility(View.GONE);
                reloadRelative.setVisibility(View.VISIBLE);

            }

        });

        inviteWeb.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    invitePro.setVisibility(View.GONE);//加载完网页进度条消失
                } else {
                    invitePro.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    invitePro.setProgress(newProgress);//设置进度值
                }
            }
        });

    }

    @Override
    protected boolean needEventBus() {
        return false;
    }


    @OnClick(R.id.book_details_reload)
    public void onViewClicked() {

    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        invitePro.setVisibility(View.GONE);
//        inviteWeb.clearCache(true);
//        inviteWeb.clearHistory();
//        inviteWeb.destroy();
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (inviteWeb != null) {
            inviteWeb.setVisibility(View.GONE);
            inviteWeb.setWebChromeClient(null);
            inviteWeb.setWebViewClient(null);
            inviteWeb.getSettings().setJavaScriptEnabled(false);
            inviteWeb.clearCache(true);
            inviteWeb.removeAllViews();
            inviteWeb.destroy();
        }
    }




}
