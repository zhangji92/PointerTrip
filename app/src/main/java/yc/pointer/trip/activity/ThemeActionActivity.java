package yc.pointer.trip.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import yc.pointer.trip.R;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.PayOrderPayTypeBean;
import yc.pointer.trip.bean.WXPayBean;
import yc.pointer.trip.bean.eventbean.FinishBean;
import yc.pointer.trip.event.ActionAlipayEvent;
import yc.pointer.trip.event.ActionWeixinPayEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.PayResult;
import yc.pointer.trip.untils.SharedPreferencesUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.DialogKnow;
import yc.pointer.trip.view.ToolbarWrapper;

/**
 * Created by moyan on 2017/10/20.
 * 主题活动页面
 */
public class ThemeActionActivity extends BaseActivity {
    @BindView(R.id.theme_action_pro)
    ProgressBar themeActionPro;
    @BindView(R.id.theme_action_web)
    WebView themeActionWeb;
    @BindView(R.id.standard_toolbar)
    Toolbar standardToolbar;
    @BindView(R.id.theme_relative)
    RelativeLayout mRelativeLayout;
    @BindView(R.id.standard_toolbar_title)
    TextView standardToolbarTitle;



    //    private String userId;//用户ID
    private String mDevCode;//设备识别码
    private long mTimeStamp;//时间戳

    private static final int SDK_PAY_FLAG = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG:
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    //String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    String resultError = payResult.getMemo();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Intent intent = new Intent(ThemeActionActivity.this, CouponActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(ThemeActionActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(ThemeActionActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    //    private String is_order;
    private String backFlag;
    private String mAid;//活动Id
    private String mTitle;//活动标题
    private String mUserId;


    @Override
    protected int getContentViewLayout() {
        return R.layout.theme_action_layout;
    }

    @Override
    protected void initView() {
        mRelativeLayout.setVisibility(View.GONE);
        themeActionWeb.setVisibility(View.VISIBLE);
        ToolbarWrapper toolbarWrapper = new ToolbarWrapper(this);
//        toolbarWrapper.setCustomTitle(R.string.action_detial);
        mTitle = getIntent().getStringExtra("title");
        if (!StringUtil.isEmpty(mTitle)) {
            standardToolbarTitle.setText(mTitle);
        } else {
            toolbarWrapper.setCustomTitle(R.string.action_detial);
        }
        backFlag = getIntent().getStringExtra("backFlag");
        mAid = getIntent().getStringExtra("aid");

        mUserId = SharedPreferencesUtils.getInstance().getString(ThemeActionActivity.this, "useId");
        standardToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回按钮重写
                overWriteBack();
            }
        });

        //设备识别码
        mDevCode = ((MyApplication) getApplication()).getDevcode();
        //时间戳
        mTimeStamp = ((MyApplication) getApplication()).getTimestamp();

//        is_order =  SharedPreferencesUtils.getInstance().getString(ThemeActionActivity.this, "isOrder");

        //注册广播
        //IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction("finish");
        //registerReceiver(new MyBroadcastReciver(), intentFilter);

        //可见时加载数据相当于Fragment的onResume
        WebSettings webSettings = themeActionWeb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.supportMultipleWindows();
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDomStorageEnabled(true);

        webSettings.setDomStorageEnabled(true);//设置适应HTML5的一些方法
        webSettings.setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        newWork();//判断是否有网


    }

    /**
     * 没网不显示
     */
    private void newWork() {
        //加载活动详情Web
        if (OkHttpUtils.isNetworkAvailable(this)) {
            if (!StringUtil.isEmpty(mUserId) && !mUserId.equals("not find")) {
                //加载活动详情
                lodingActionDietial(mUserId, mAid);
            }
        } else {
            mRelativeLayout.setVisibility(View.VISIBLE);
            themeActionWeb.setVisibility(View.GONE);
            Toast.makeText(this, "请检查网络状态", Toast.LENGTH_SHORT).show();
        }

    }

    private void lodingActionDietial(String userId, String mAid) {
        mRelativeLayout.setVisibility(View.GONE);
        themeActionWeb.setVisibility(View.VISIBLE);
        String ACTIONURL = URLUtils.BASE_URL + "/Home/Ad/details?uid=" + userId + "&aid=" + mAid;
        Log.e("LogInterceptor-->", ACTIONURL);
        themeActionWeb.loadUrl(ACTIONURL);
        themeActionWeb.setWebViewClient(new MyWebViewClient());
        themeActionWeb.setWebChromeClient(new MyWebChromeClient());
    }


    /**
     * 返回按钮点击事件逻辑
     */
    private void overWriteBack() {
        if (!StringUtil.isEmpty(backFlag) && backFlag.equals("adverting")) {
            Intent intent = new Intent(ThemeActionActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            finish();
        }
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }


    @OnClick(R.id.theme_relative)
    public void onViewClicked() {
        newWork();
        //lodingActionDietial(mUserId, mAid);
    }


    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("&&")) {
                String[] split = url.split("&&");
                String myNeed = "";
                for (int i = 0; i < split.length; i++) {
                    myNeed = split[i];
                    if (myNeed.equals("htype=1")) {
                        //网络请求支付  支付宝
                        alipay();
//                        if (!StringUtil.isEmpty(is_order) && is_order.equals("1")) {
//                            alipay();
//                        } else if (is_order.equals("0")) {
//                            //去认证
//                            verifyDialog();
//                        }else {
//                            Toast.makeText(ThemeActionActivity.this, "登录后完成付款", Toast.LENGTH_SHORT).show();
//                        }
                    } else if (myNeed.equals("htype=2")) {
                        //网络请求支付  微信
                        weixinPay();
//                        if (!StringUtil.isEmpty(is_order) && is_order.equals("1")) {
//                            weixinPay();
//                        } else if (is_order.equals("0")){
//                            //去认证
//                            verifyDialog();
//                        }else {
//                            Toast.makeText(ThemeActionActivity.this, "登录后完成付款", Toast.LENGTH_SHORT).show();
//                        }
                    }
                }
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            mRelativeLayout.setVisibility(View.VISIBLE);
            themeActionWeb.setVisibility(View.GONE);
            Toast.makeText(ThemeActionActivity.this, "网络链接不可用", Toast.LENGTH_SHORT).show();

        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (themeActionWeb != null) {
                if (newProgress == 100) {
                    themeActionPro.setVisibility(View.GONE);
                } else {
                    themeActionPro.setVisibility(View.VISIBLE);
                    themeActionPro.setProgress(newProgress);
                }
            }
        }
    }

    /**
     * 跳转认证界面
     */
//    private void verifyDialog() {
//        //支付界面使用DialogUtil
//        //身份审核未通过，调到审核界面
//        new DialogSure(ThemeActionActivity.this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
//            @Override
//            public void onClickListener(DialogSure dialogSure, boolean cancelable) {
//                if (cancelable) {
//                    Intent intent = new Intent(ThemeActionActivity.this, VerifyActivity.class);
//                    intent.putExtra("payOrder", "pay");
//                    startActivityForResult(intent, 1);
//                } else {
//
//                }
//            }
//        }).setMsg("您尚未完成身份认证,不能发布订单").setPositiveButton("去认证").setNegativeButton("不用了").show();
//    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == 1) {
                    new DialogKnow(ThemeActionActivity.this, R.style.user_default_dialog, new DialogKnow.CallBackListener() {
                        @Override
                        public void onClickListener() {

                        }
                    }).setMsg("您已完成实名认证，可继续完成付款，祝您旅途愉快").setPositiveButton("我知道了").show();
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (themeActionWeb != null) {
            themeActionPro.setVisibility(View.GONE);
            themeActionWeb.setWebChromeClient(null);
            themeActionWeb.setWebViewClient(null);
            themeActionWeb.getSettings().setJavaScriptEnabled(false);
            themeActionWeb.clearCache(true);
            themeActionWeb.removeAllViews();
            themeActionWeb.destroy();
        }
    }

    /**
     * 调起支付的请求参数
     */
    @NonNull
    private RequestBody getRequestBody() {
        //参数名	类型	说明	是否必填
        //signature	String	签名	是
        //devcode	String	手机唯一识别码	是
        //timestamp	String	当前时间戳	是
        //uid	Int	用户id	是
        //aid	活动id	是
        String sign = Md5Utils.createMD5("aid=" + mAid + "devcode=" + mDevCode + "timestamp=" + mTimeStamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
        return new FormBody.Builder()
                .add("devcode", mDevCode)
                .add("timestamp", String.valueOf(mTimeStamp))
                .add("uid", mUserId)
                .add("aid", mAid)
                .add("signature", sign)
                .build();
    }

    /**
     * 微信支付
     */
    private void weixinPay() {
        boolean dtFlag = APPUtils.judgeTimeDev(this, mDevCode, mTimeStamp);
        if (dtFlag) {
            OkHttpUtils.getInstance().post(URLUtils.ACTION_WEIXINPAY, getRequestBody(), new HttpCallBack(new ActionWeixinPayEvent()));
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void actionWeixinPay(ActionWeixinPayEvent event) {
        WXPayBean wxPayBean = event.getData();
        if (wxPayBean.getStatus() == 0) {
            try {
                IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
                msgApi.registerApp("wxe44b2ed1e16f131c");
                PayReq request = new PayReq();
                request.appId = wxPayBean.getData().getAppid();
                request.partnerId = wxPayBean.getData().getPartnerid();
                request.prepayId = wxPayBean.getData().getPrepayid();
                request.packageValue = wxPayBean.getData().getPackageX();
                request.nonceStr = wxPayBean.getData().getNoncestr();
                request.timeStamp = wxPayBean.getData().getTimestamp();
                request.sign = wxPayBean.getData().getSign();
                msgApi.sendReq(request);

            } catch (Exception e) {
                Log.e("PAY_GET", "异常：" + e.getMessage());
            }
        } else {
            Toast.makeText(this, wxPayBean.getMsg(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 支付宝支付
     */
    private void alipay() {
        boolean dtFlag = APPUtils.judgeTimeDev(this, mDevCode, mTimeStamp);
        if (dtFlag) {
            OkHttpUtils.getInstance().post(URLUtils.ACTION_ALIPAY, getRequestBody(), new HttpCallBack(new ActionAlipayEvent()));
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void actionAlipay(ActionAlipayEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        final PayOrderPayTypeBean data = event.getData();
        if (data.getStatus() == 0) {
            Runnable payRunnable = new Runnable() {
                @Override
                public void run() {
                    PayTask alipay = new PayTask(ThemeActionActivity.this);
                    Map<String, String> result = alipay.payV2(data.getOrderString(), true);
                    Message msg = new Message();
                    msg.what = SDK_PAY_FLAG;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }
            };
            Thread payThread = new Thread(payRunnable);
            payThread.start();

        } else {
            Toast.makeText(this, data.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, data.getStatus());
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

//    //广播接收内部类
//    private class MyBroadcastReciver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals("finish")) {
//                finish();
////            //在android端显示接收到的广播内容
////            Toast.makeText(getActivity(), author, 1).show();
//                //在结束时可取消广播
//                unregisterReceiver(this);
//            }
//        }
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void FinishBean(FinishBean bean){
        if (bean!=null&&bean.getFinish().equals("finish")){
            finish();
        }
    }

}
