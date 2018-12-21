package yc.pointer.trip.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import yc.pointer.trip.R;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.ToolbarWrapper;

/**
 * Created by moyan on 2017/12/11.
 * <p>
 * 近期主题详情页
 */

public class RecentTopicActivity extends BaseActivity {

    @BindView(R.id.theme_action_pro)
    ProgressBar themeActionPro;
    @BindView(R.id.recent_topic_web)
    WebView recentTopicWeb;
    @BindView(R.id.standard_toolbar)
    Toolbar standardToolbar;
    @BindView(R.id.theme_relative)
    RelativeLayout mRelativeLayout;
    @BindView(R.id.standard_toolbar_title)
    TextView standardToolbarTitle;
    private String aid;

    @Override
    protected int getContentViewLayout() {
        return R.layout.recent_topic_layout;

    }

    @Override
    protected void initView() {
        ToolbarWrapper toolbarWrapper = new ToolbarWrapper(this);
        String title = getIntent().getStringExtra("title");
        aid = getIntent().getStringExtra("aid");
        if (!StringUtil.isEmpty(title)){
            standardToolbarTitle.setText(title);
        }

        mRelativeLayout.setVisibility(View.GONE);
        recentTopicWeb.setVisibility(View.VISIBLE);


        //可见时加载数据相当于Fragment的onResume
        WebSettings webSettings = recentTopicWeb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.supportMultipleWindows();
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDomStorageEnabled(true);

        webSettings.setDomStorageEnabled(true);//设置适应HTML5的一些方法
        webSettings.setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        if (OkHttpUtils.isNetworkAvailable(this)) {//判断网络状态
            //加载webView
            if (!StringUtil.isEmpty(aid)){
                lodingActionDietial(aid);
            }else {
                mRelativeLayout.setVisibility(View.VISIBLE);
                recentTopicWeb.setVisibility(View.GONE);
            }
        } else {
            mRelativeLayout.setVisibility(View.VISIBLE);
            recentTopicWeb.setVisibility(View.GONE);
            Toast.makeText(this, "请检查网络状态", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 加载web
     */
    private void lodingActionDietial(String aid) {
        mRelativeLayout.setVisibility(View.GONE);
        recentTopicWeb.setVisibility(View.VISIBLE);
        String ACTIONURL = URLUtils.BASE_URL + "/Home/Ad/ztDetails?aid="+aid;
        recentTopicWeb.loadUrl(ACTIONURL);
        recentTopicWeb.setWebChromeClient(new MyWebChromeClient());//进度条监听
        recentTopicWeb.setWebViewClient(new MyWebViewClient());
    }



    /**
     * webView进度条监听
     */
    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (recentTopicWeb != null) {
                if (newProgress == 100) {
                    themeActionPro.setVisibility(View.GONE);
                } else {
                    themeActionPro.setVisibility(View.VISIBLE);
                    themeActionPro.setProgress(newProgress);
                }
            }
        }
    }

    @OnClick(R.id.theme_relative)
    public void onViewClicked() {
        lodingActionDietial(aid);
    }


    @Override
    protected boolean needEventBus() {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (recentTopicWeb != null) {
            themeActionPro.setVisibility(View.GONE);
            recentTopicWeb.setWebChromeClient(null);
            recentTopicWeb.setWebViewClient(null);
            recentTopicWeb.getSettings().setJavaScriptEnabled(false);
            recentTopicWeb.clearCache(true);
            recentTopicWeb.removeAllViews();
            recentTopicWeb.destroy();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        recentTopicWeb.onPause();
        recentTopicWeb.pauseTimers();
    }

    @Override
    public void onResume() {
        super.onResume();
        recentTopicWeb.resumeTimers();
        recentTopicWeb.onResume();
    }




    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            mRelativeLayout.setVisibility(View.VISIBLE);
            recentTopicWeb.setVisibility(View.GONE);
        }
    }
}
