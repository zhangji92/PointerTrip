package yc.pointer.trip.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.view.ToolbarWrapper;

/**
 * Created by moyan on 2018/10/16.
 */

public class RuleTextWebActivity extends BaseActivity {
    @BindView(R.id.book_details_pro)
    ProgressBar bookDetailsPro;
    @BindView(R.id.agreement_web)
    WebView agreementWeb;
    @BindView(R.id.agreement_relative)
    RelativeLayout mRelativeLayout;//加载失败页面
    private String webUrl;

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_rule_text;
    }

    @Override
    protected void initView() {
        ToolbarWrapper toolbarWrapper = new ToolbarWrapper(this);
        toolbarWrapper.setCustomTitle("奖励规则");
        webUrl = getIntent().getStringExtra("webUrl");
        if (OkHttpUtils.isNetworkAvailable(this)){
            agreementWeb.loadUrl(URLUtils.BASE_URL+webUrl);
        }else {
            agreementWeb.setVisibility(View.GONE);
            mRelativeLayout.setVisibility(View.VISIBLE);
        }
        agreementWeb.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }

        });

        agreementWeb.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    bookDetailsPro.setVisibility(View.GONE);//加载完网页进度条消失
                } else {
                    bookDetailsPro.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    bookDetailsPro.setProgress(newProgress);//设置进度值
                }
            }
        });


    }

    @Override
    protected boolean needEventBus() {
        return false;
    }

    @Override
    protected void onStop() {

        super.onStop();
        bookDetailsPro.setVisibility(View.GONE);
        agreementWeb.clearCache(true);
        agreementWeb.clearHistory();
        agreementWeb.destroy();

    }

}
