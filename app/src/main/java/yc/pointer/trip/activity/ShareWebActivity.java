package yc.pointer.trip.activity;

import android.content.Intent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.net.ssl.HttpsURLConnection;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import yc.pointer.trip.R;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.SaveMesgBean;
import yc.pointer.trip.bean.eventbean.PayBean;
import yc.pointer.trip.bean.eventbean.UpdateRedDotBean;
import yc.pointer.trip.event.MineSaveMesgEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.SharedPreferencesUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.DialogKnow;
import yc.pointer.trip.view.DialogSure;
import yc.pointer.trip.view.ToolbarWrapper;
import yc.pointer.trip.view.TravelSharingDialog;
import yc.pointer.trip.wxapi.PayOrderActivity;

/**
 * Created by 张继
 * 2018/10/10
 * 14:06
 * 公司：
 * 描述：分享出去的web
 */

public class ShareWebActivity extends BaseActivity {
    @BindView(R.id.share_progress)
    ProgressBar shareProgress;
    @BindView(R.id.share_web)
    WebView shareWeb;
    @BindView(R.id.share_participate)
    Button shareParticipate;
    @BindView(R.id.share_relative)
    RelativeLayout shareRelative;
    private String mUserId;
    private String url;
    private String mIsJie;
    private long mTimestamp;
    private String mDevcode;

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_share_web;
    }

    @Override
    protected void initView() {
        new ToolbarWrapper(this).setCustomTitle("旅赚训练营");
        mUserId = MyApplication.mApp.getUserId();
        mTimestamp = MyApplication.mApp.getTimestamp();
        mDevcode = MyApplication.mApp.getDevcode();
        //是否可以发单
        mIsJie = MyApplication.mApp.getUserBean().getIs_jie();
        if (mIsJie.equals("2") && !StringUtil.isEmpty(mIsJie)) {
            shareParticipate.setText("分享集赞");
        } else {
            shareParticipate.setText("立即参加");
        }
        url = URLUtils.BASE_URL + "/Home/Work/lvzhuan?uid=" + mUserId;
        netWork(url);//加载网页
        //可见时加载数据相当于Fragment的onResume
        WebSettings webSettings = shareWeb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.supportMultipleWindows();
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDomStorageEnabled(true);//设置适应HTML5的一些方法
        webSettings.setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        getServiceMine();
    }

    /**
     * 加载网页
     * @param url
     */
    private void netWork(String url) {
        if (OkHttpUtils.isNetworkAvailable(this)) {
            shareWeb.setVisibility(View.VISIBLE);
            shareRelative.setVisibility(View.GONE);
            shareWeb.loadUrl(url);// 加载本地的html布局文件
            shareWeb.setWebChromeClient(new MyWebChromeClient());
        } else {
            shareWeb.setVisibility(View.GONE);
            shareRelative.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

    @OnClick({R.id.share_participate, R.id.share_relative})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.share_participate:

                if (!StringUtil.isEmpty(mIsJie)) {
                    if (mIsJie.equals("2")) {//是会员
                        String shareUrl = URLUtils.BASE_URL + "/Home/Work/lvzhuan?uid=" + mUserId + "&share=1";
                        //链接
                        UMWeb web = new UMWeb(shareUrl);
                        web.setTitle("「指针自由行-APP」" + " 我要参加旅赚达人训练营，快来为我点赞");//标题
                        web.setThumb(new UMImage(this, R.mipmap.share));  //缩略图
                        web.setDescription("近距离接触大咖，开启两天一夜神秘之旅");//描述
                        TravelSharingDialog travelSharingDialog = new TravelSharingDialog(this, this);
                        travelSharingDialog.setWeb(web).show();
                        travelSharingDialog.setListener(new TravelSharingDialog.ShareSuccessListener() {
                            @Override
                            public void shareSuccess() {
                                Toast.makeText(ShareWebActivity.this, "分享成功,火速积攒中...", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else if (mIsJie.equals("1")) {
                        boolean isPayDeposit = SharedPreferencesUtils.getInstance().getBoolean(ShareWebActivity.this, "isPayDeposit");
                        if (!isPayDeposit) {
                            //认证中
                            new DialogKnow(ShareWebActivity.this, R.style.user_default_dialog, new DialogKnow.CallBackListener() {
                                @Override
                                public void onClickListener() {
                                    Intent intent = new Intent(ShareWebActivity.this, NewUnDepositActivity.class);
                                    startActivity(intent);
                                }
                            }).setTitle("温馨提示")
                                    .setMsg("您尚未充值，请前去充值")
                                    .setPositiveButton("去充值")
                                    .show();
                        }else {
                            //认证中
                            new DialogKnow(ShareWebActivity.this, R.style.user_default_dialog, new DialogKnow.CallBackListener() {
                                @Override
                                public void onClickListener() {

                                }
                            }).setTitle("温馨提示")
                                    .setMsg("您提交的会员认证信息正在火速审核中,暂不可分享，请耐心等待")
                                    .setPositiveButton("我知道了")
                                    .show();
                        }
                    } else if (mIsJie.equals("3")) {//不是会员
                        //认证失败
                        new DialogSure(ShareWebActivity.this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                            @Override
                            public void onClickListener(DialogSure dialogSure, boolean cancelable) {
                                if (cancelable) {
                                    Intent intent = new Intent(ShareWebActivity.this, VerifyActivity.class);
                                    startActivity(intent);
                                }
                            }
                        }).setTitle("非常抱歉")
                                .setMsg("您的会员认证申请失败，请重新完善会员身份信息")
                                .setPositiveButton("前往")
                                .setNegativeButton("放弃")
                                .show();
                    } else {//新用户不是会员没提交信息没提交押金
                        //未认证
                        new DialogSure(ShareWebActivity.this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                            @Override
                            public void onClickListener(DialogSure dialogSure, boolean cancelable) {
                                if (cancelable) {
                                    Intent intent = new Intent(ShareWebActivity.this, VerifyActivity.class);
                                    startActivity(intent);
                                }
                            }
                        }).setTitle("温馨提示")
                                .setMsg("您尚未升级成为指针会员，请先完成指针会员认证，方可进行分享")
                                .setPositiveButton("确定")
                                .setNegativeButton("取消")
                                .show();
                    }
                }
                break;
            case R.id.share_relative:
                netWork(url);
                break;
        }
    }

    /**
     * unDeposit ver 等activity发送过来
     *
     * @param payBean 刷新支付
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void pay(PayBean payBean) {
        if (payBean != null && payBean.getPay().equals("刷新支付")) {
            initView();
            getServiceMine();
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (shareWeb != null) {
                if (newProgress == 100) {
                    shareProgress.setVisibility(View.GONE);//加载完网页进度条消失
                } else {
                    shareProgress.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    shareProgress.setProgress(newProgress);//设置进度值
                }
            }
        }
    }

    /**
     * 请求个人信息
     */
    private void getServiceMine() {
        boolean flag = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
        if (flag) {
            String sign = Md5Utils.createMD5("devcode=" + mDevcode + "timestamp=" + mTimestamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", mDevcode)
                    .add("signature", sign)
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", mUserId)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.MY_PERSON_SETTING, requestBody, new HttpCallBack(new MineSaveMesgEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void personSetBean(MineSaveMesgEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(ShareWebActivity.this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        SaveMesgBean bean = event.getData();
        if (bean.getStatus() == 0) {
            MyApplication.mApp.setUserBean(bean.getData());
            mIsJie = bean.getData().getIs_jie();
            String is_order = bean.getData().getIs_order();
            if (!StringUtil.isEmpty(is_order) && is_order.equals("1")) {
                SharedPreferencesUtils.getInstance().putBoolean(ShareWebActivity.this, "isPayDeposit", true);
            } else if (is_order.equals("0")) {
                SharedPreferencesUtils.getInstance().putBoolean(ShareWebActivity.this, "isPayDeposit", false);
            }
        } else {
            Toast.makeText(ShareWebActivity.this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(ShareWebActivity.this, bean.getStatus());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (shareWeb != null) {
            shareWeb.setVisibility(View.GONE);
            shareWeb.setWebChromeClient(null);
            shareWeb.setWebViewClient(null);
            shareWeb.getSettings().setJavaScriptEnabled(false);
            shareWeb.clearCache(true);
            shareWeb.removeAllViews();
            shareWeb.destroy();
        }
    }
}
