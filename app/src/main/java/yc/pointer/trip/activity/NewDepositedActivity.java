package yc.pointer.trip.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import yc.pointer.trip.R;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.view.ToolbarWrapper;

/**
 * Created by 张继
 * 2018/8/6
 * 16:20
 * 公司：
 * 描述：新的权益界面
 */

public class NewDepositedActivity extends BaseActivity {

    @BindView(R.id.deposited_progress)
    ProgressBar depositedProgress;
    @BindView(R.id.deposited_web)
    WebView depositedWeb;
    @BindView(R.id.deposited_relative)
    RelativeLayout depositedRelative;
    @BindView(R.id.deposited_wifi)
    ImageView depositedWifi;
    private String mUserId;

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_deposite_new;
    }

    @Override
    protected void initView() {
        mUserId = MyApplication.mApp.getUserId();
        new ToolbarWrapper(this).setCustomTitle(getIntent().getStringExtra("title"));
        String url = URLUtils.BASE_URL + "/Home/Work/gold?uid=" + mUserId;
        netWork(url);
    }

    /**
     * 加载网页
     *
     * @param url
     */
    private void netWork(String url) {
        if (OkHttpUtils.isNetworkAvailable(this)) {
            depositedWeb.setVisibility(View.VISIBLE);
            depositedRelative.setVisibility(View.GONE);
            depositedWeb.loadUrl(url);// 加载本地的html布局文件
            depositedWeb.setWebChromeClient(new MyWebChromeClient());
        } else {
            depositedWeb.setVisibility(View.GONE);
            depositedRelative.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected boolean needEventBus() {
        return false;
    }


    @OnClick({R.id.deposited_wifi, R.id.deposited_relative})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.deposited_wifi:
            case R.id.deposited_relative:
                String url = URLUtils.BASE_URL + "/Home/Work/gold?uid=" + mUserId;
                netWork(url);
                break;
        }
    }


    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (depositedWeb != null) {
                if (newProgress == 100) {
                    depositedProgress.setVisibility(View.INVISIBLE);//加载完网页进度条消失
                } else {
                    depositedProgress.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    depositedProgress.setProgress(newProgress);//设置进度值
                }
            }
        }
    }
}
