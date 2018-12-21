package yc.pointer.trip.activity;

import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;



import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import yc.pointer.trip.R;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.ImageUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.ToolbarWrapper;

/**
 * Created by 张继
 * 2017/8/3 0003
 * 下午 2:17
 * 景点详情
 */

public class ScenicDetailsActivity extends BaseActivity {

    @BindView(R.id.scenic_details_progress_bar)
    ProgressBar scenicDetailsProgressBar;//进度条
    @BindView(R.id.scenic_details_web)
    WebView scenicDetailsWeb;//webView
    @BindView(R.id.scenic_details_linear)
    LinearLayout scenicDetailsLinear;//webView外围布局 加载失败时隐藏
    @BindView(R.id.scenic_details_reload)
    Button scenicDetailsReload;//重新加载d
    @BindView(R.id.scenic_details_relative)
    RelativeLayout scenicDetailsRelative;//加载失败外围布局 加载成功隐藏
    private String sid;


    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_scenic_details;
    }

    @Override
    protected void initView() {

        new ToolbarWrapper(this).setCustomTitle(R.string.scenic_details_title);
        sid = getIntent().getStringExtra("sid");
        if (StringUtil.isEmpty(sid)) {
            sid = "0";
        }
        //可见时加载数据相当于Fragment的onResume
        WebSettings webSettings = scenicDetailsWeb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.supportMultipleWindows();
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        String url = URLUtils.SCENIC_DETAILS + sid;
        scenicDetailsWeb.loadUrl(url);// 加载本地的html布局文件
//        scenicDetailsWeb.setWebViewClient(new MyWebViewClient());
        scenicDetailsWeb.setWebChromeClient(new MyWebChromeClient());
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

    /**
     * 监听 所有点击的链接，如果拦截到我们需要的，就跳转到相对应的页面。 shouldOverrideUrlLoading
     */
//    private class MyWebViewClient extends WebViewClient {
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//
//            arrayList.clear();
//            try {
//                if (url.contains("size")) {
//                    String strSub = url.substring(url.indexOf("*"), url.indexOf("json"));
//                    int position = Integer.valueOf(strSub.substring(6, strSub.length() - 1));
//                    int i = url.indexOf("[");
//                    String ImageUrl = url.substring(i + 1, url.length() - 1);
//                    String strArr = ImageUrl.replaceAll("%22", "");
//                    String strArr1 = strArr.replaceAll("\\\\", "");
//                    String[] strSuccess = strArr1.split(",");
//                    for (String str : strSuccess) {
//                        arrayList.add(str);
//                    }
//                    Uri uri = ImageUtils.createImageFile();
//                    new PhotoPagerConfig.Builder(ScenicDetailsActivity.this)
//                            .setBigImageUrls(arrayList)
//                            .setSavaImage(true)
//                            .setPosition(position)
//                            .setSaveImageLocalPath(uri.getPath())
//                            .build();
//                } else {
//                    Toast.makeText(ScenicDetailsActivity.this, "格式不正确", Toast.LENGTH_SHORT).show();
//                }
//                return true;
//            } catch (Exception e) {
//                return true;
//            }
//        }
//
//        @Override
//        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//            scenicDetailsRelative.setVisibility(View.VISIBLE);//显示加载失败的布局
//            scenicDetailsLinear.setVisibility(View.GONE);//webView隐藏
//        }
//    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                scenicDetailsProgressBar.setVisibility(View.GONE);//加载完网页进度条消失
            } else {
                scenicDetailsProgressBar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                scenicDetailsProgressBar.setProgress(newProgress);//设置进度值
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        scenicDetailsWeb.removeAllViews();
        scenicDetailsWeb.clearHistory();
        scenicDetailsWeb.clearCache(true);
        scenicDetailsWeb.clearView();
        scenicDetailsWeb.destroy();
    }
}
