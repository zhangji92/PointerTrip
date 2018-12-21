package yc.pointer.trip.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.eventbean.UseCouponBean;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.ToolbarWrapper;

/**
 * Created by moyan on 2017/9/12.
 */
public class UseCouponAvtivity extends BaseActivity {

    @BindView(R.id.book_details_pro)
    ProgressBar bookDetailsPro;
    @BindView(R.id.use_coupon_web)
    WebView useCouponWeb;
    @BindView(R.id.standard_toolbar_title)
    TextView standardToolbarTitle;
    @BindView(R.id.standard_toolbar)
    Toolbar standardToolbar;

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_use_conpon;
    }

    @Override
    protected void initView() {
        ToolbarWrapper toolbarWrapper = new ToolbarWrapper(this);

        final String url = getIntent().getStringExtra("url");
        final String title = getIntent().getStringExtra("title");
        standardToolbarTitle.setText(title);
        standardToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent();
                //intent.setAction("useCoupon");
                //sendBroadcast(intent);
                EventBus.getDefault().post(new UseCouponBean("useCoupon"));
                finish();
            }
        });

        WebSettings webSettings = useCouponWeb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.supportMultipleWindows();
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDomStorageEnabled(true);

        webSettings.setDomStorageEnabled(true);//设置适应HTML5的一些方法
        webSettings.setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        if (!StringUtil.isEmpty(url)) {
            useCouponWeb.loadUrl(url);
        }
        useCouponWeb.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                view.loadUrl(url);
                return true;
            }
        });
        useCouponWeb.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (useCouponWeb != null) {
                    if (newProgress == 100) {
                        bookDetailsPro.setVisibility(View.GONE);
                    } else {
                        bookDetailsPro.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                        bookDetailsPro.setProgress(newProgress);//设置进度值
                    }
                }

            }
        });
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (useCouponWeb != null) {
            bookDetailsPro.setVisibility(View.GONE);
            useCouponWeb.setWebChromeClient(null);
            useCouponWeb.setWebViewClient(null);
            useCouponWeb.getSettings().setJavaScriptEnabled(false);
            useCouponWeb.clearCache(true);
            useCouponWeb.removeAllViews();
            useCouponWeb.destroy();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            //Intent intent = new Intent();
            //intent.setAction("useCoupon");
            //sendBroadcast(intent);
            EventBus.getDefault().post(new UseCouponBean("useCoupon"));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
