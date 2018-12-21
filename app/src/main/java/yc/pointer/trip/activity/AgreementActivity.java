package yc.pointer.trip.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import yc.pointer.trip.R;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.SaveMesgBean;
import yc.pointer.trip.event.AgreementDepositEvent;
import yc.pointer.trip.event.AgreementSetOutEvent;
import yc.pointer.trip.event.AgreementVerifyEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.ToolbarWrapper;

/**
 * Created by 张继
 * 2017/7/14
 * 17:39
 * 用户协议
 */
public class AgreementActivity extends BaseActivity {
    @BindView(R.id.agreement_web)
    WebView agreementWeb;
    @BindView(R.id.book_details_pro)
    ProgressBar bookDetailsPro;
    @BindView(R.id.accept)
    Button accept;//我知道了
    @BindView(R.id.agreement_relative)
    RelativeLayout mRelativeLayout;//加载失败页面

    private String logFlag;
    private String isReaded = "";

    private String mDevcode;//设备识别码
    private long mTimestamp;//时间戳
    private String userID;//用户ID
    private boolean judgeTimeDev;
    private ToolbarWrapper toolbarWrapper;

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_agreement;
    }

    @Override

    protected void initView() {
        accept.setVisibility(View.VISIBLE);
        agreementWeb.setVisibility(View.VISIBLE);
        mRelativeLayout.setVisibility(View.GONE);
        toolbarWrapper = new ToolbarWrapper(this);
        mDevcode = ((MyApplication) getApplication()).getDevcode();
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();
        userID = ((MyApplication) getApplication()).getUserId();
        //判断是否获取时间戳和设备识别码
        judgeTimeDev = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
        logFlag = getIntent().getStringExtra("logFlag");

        if (OkHttpUtils.isNetworkAvailable(this)) {

            if (!StringUtil.isEmpty(logFlag)) {
                String isReade = getIntent().getStringExtra("isReade");
                if (logFlag.equals("gotoTravel")) {//出行须知
                    toolbarWrapper.setCustomTitle(R.string.orderagreement);
                    agreementWeb.loadUrl(URLUtils.BASE_URL + "/Home/Index/ordAgreement");
                } else if (logFlag.equals("Verify")) {//实名认证服务协议
                    toolbarWrapper.setCustomTitle(R.string.verifyagreement);
                    agreementWeb.loadUrl(URLUtils.BASE_URL + "/Home/Work/smAgreement");
                } else if (logFlag.equals("deposit")) {//押金服务协议
                    toolbarWrapper.setCustomTitle(R.string.depositagreement);
                    agreementWeb.loadUrl(URLUtils.BASE_URL + "/Home/Work/yjAgreement");
                }
                if (!StringUtil.isEmpty(isReade) && isReade.equals("1")) {
                    accept.setText("已同意");
                    accept.setClickable(false);
                    accept.setEnabled(false);
                    accept.setBackgroundColor(Color.parseColor("#b8b8b8"));
                } else {
                    accept.setClickable(true);
                    accept.setEnabled(true);
                    accept.setText("我知道了");
                }
                accept.setVisibility(View.VISIBLE);

            } else {
                toolbarWrapper.setCustomTitle(R.string.agreement);
                agreementWeb.loadUrl(URLUtils.BASE_URL + "/Home/Index/serviceAgreement");
                accept.setVisibility(View.GONE);
            }
        } else {
            accept.setVisibility(View.GONE);
            agreementWeb.setVisibility(View.GONE);
            mRelativeLayout.setVisibility(View.VISIBLE);
        }

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtil.isEmpty(logFlag)) {
                    if (logFlag.equals("gotoTravel")) {
                        //阅读须知
                        getRulesForNet("1");
                        accept.setClickable(false);
                        accept.setEnabled(false);
                        accept.setBackgroundColor(Color.parseColor("#b8b8b8"));
                    } else if (logFlag.equals("Verify")) {
                        //阅读《实名认证服务协议》
                        getVerifyRulesForNet("1");
                        accept.setClickable(false);
                        accept.setEnabled(false);
                        accept.setBackgroundColor(Color.parseColor("#b8b8b8"));
                    } else if (logFlag.equals("deposit")) {
                        //阅读《押金服务协议》
                        getDepositRulesForNet("1");
                        accept.setClickable(false);
                        accept.setEnabled(false);
                        accept.setBackgroundColor(Color.parseColor("#b8b8b8"));
                    }

                } else {

                }


            }
        });
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
        return true;
    }

    /**
     * 获取是否阅读出发须知的状态
     *
     * @param isReaded
     */
    public void getRulesForNet(String isReaded) {
        if (judgeTimeDev) {
            String sign = Md5Utils.createMD5("devcode=" + mDevcode + "is_mz=" + isReaded + "timestamp=" + mTimestamp + "uid=" + userID + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", mDevcode)
                    .add("signature", sign)
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", userID)
                    .add("is_mz", isReaded)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.READED_RULES, requestBody, new HttpCallBack(new AgreementSetOutEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getRules(AgreementSetOutEvent saveMesgEvent) {
        if (saveMesgEvent.isTimeOut()) {
            Toast.makeText(this, "网络状态异常，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }
        SaveMesgBean saveMesgBean = saveMesgEvent.getData();
        if (saveMesgBean.getStatus() == 0) {
            accept.setClickable(true);
            isReaded = saveMesgBean.getData().getIs_mz();
            ((MyApplication) getApplication()).setUserBean(saveMesgBean.getData());
            if (!StringUtil.isEmpty(isReaded)) {
                Intent intent = new Intent();
                intent.putExtra("readedStatus", isReaded);
                setResult(2, intent);
                finish();
            }
        }
    }

    /**
     * 获取是否阅读《实名认证服务协议》的状态
     *
     * @param isReaded
     */
    public void getVerifyRulesForNet(String isReaded) {
        if (judgeTimeDev) {
            String sign = Md5Utils.createMD5("devcode=" + mDevcode + "is_sm=" + isReaded + "timestamp=" + mTimestamp + "uid=" + userID + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", mDevcode)
                    .add("signature", sign)
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", userID)
                    .add("is_sm", isReaded)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.READED_VERIFY_RULES, requestBody, new HttpCallBack(new AgreementVerifyEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getVerifyRules(AgreementVerifyEvent saveMesgEvent) {
        if (saveMesgEvent.isTimeOut()) {
            accept.setClickable(true);
            accept.setEnabled(true);
            Toast.makeText(this, "网络状态异常，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }
        SaveMesgBean saveMesgBean = saveMesgEvent.getData();
        if (saveMesgBean.getStatus() == 0) {
            accept.setClickable(true);
            accept.setEnabled(true);
            isReaded = saveMesgBean.getData().getIs_sm();
            ((MyApplication) getApplication()).setUserBean(saveMesgBean.getData());
            if (!StringUtil.isEmpty(isReaded)) {
                Intent intent = new Intent();
                intent.putExtra("readedStatus", isReaded);
                setResult(2, intent);
                finish();
            }
        }
    }


    /**
     * 获取是否阅读《押金服务协议》的状态
     *
     * @param isReaded
     */
    public void getDepositRulesForNet(String isReaded) {
        if (judgeTimeDev) {
            String sign = Md5Utils.createMD5("devcode=" + mDevcode + "is_yj=" + isReaded + "timestamp=" + mTimestamp + "uid=" + userID + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", mDevcode)
                    .add("signature", sign)
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", userID)
                    .add("is_yj", isReaded)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.READED_DEPOSIT_RULES, requestBody, new HttpCallBack(new AgreementDepositEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getDepositRules(AgreementDepositEvent saveMesgEvent) {
        if (saveMesgEvent.isTimeOut()) {
            Toast.makeText(this, "网络状态异常，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }
        SaveMesgBean saveMesgBean = saveMesgEvent.getData();
        if (saveMesgBean.getStatus() == 0) {
            accept.setClickable(true);
            accept.setEnabled(true);
            isReaded = saveMesgBean.getData().getIs_yj();
            ((MyApplication) getApplication()).setUserBean(saveMesgBean.getData());
            if (!StringUtil.isEmpty(isReaded)) {
                Intent intent = new Intent();
                intent.putExtra("readedStatus", isReaded);
                setResult(2, intent);
                finish();
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        bookDetailsPro.setVisibility(View.GONE);
        agreementWeb.clearCache(true);
        agreementWeb.clearHistory();
        agreementWeb.destroy();
    }


    @OnClick(R.id.agreement_relative)
    public void onViewClicked() {
        accept.setVisibility(View.VISIBLE);
        agreementWeb.setVisibility(View.VISIBLE);
        mRelativeLayout.setVisibility(View.GONE);
        if (OkHttpUtils.isNetworkAvailable(this)) {

            if (!StringUtil.isEmpty(logFlag)) {
                String isReade = getIntent().getStringExtra("isReade");
                if (logFlag.equals("gotoTravel")) {//出行须知
                    toolbarWrapper.setCustomTitle(R.string.orderagreement);
                    agreementWeb.loadUrl(URLUtils.BASE_URL + "/Home/Index/ordAgreement");
                } else if (logFlag.equals("Verify")) {//实名认证服务协议
                    toolbarWrapper.setCustomTitle(R.string.verifyagreement);
                    agreementWeb.loadUrl(URLUtils.BASE_URL + "/Home/Work/smAgreement");
                } else if (logFlag.equals("deposit")) {//押金服务协议
                    toolbarWrapper.setCustomTitle(R.string.depositagreement);
                    agreementWeb.loadUrl(URLUtils.BASE_URL + "/Home/Work/yjAgreement");
                }
                if (!StringUtil.isEmpty(isReade) && isReade.equals("1")) {
                    accept.setText("已同意");
                    accept.setClickable(false);
                    accept.setEnabled(false);
                    accept.setBackgroundColor(Color.parseColor("#b8b8b8"));
                } else {
                    accept.setClickable(true);
                    accept.setEnabled(true);
                    accept.setText("我知道了");
                }
                accept.setVisibility(View.VISIBLE);
            } else {
                toolbarWrapper.setCustomTitle(R.string.agreement);
                agreementWeb.loadUrl(URLUtils.BASE_URL + "/Home/Index/serviceAgreement");
                accept.setVisibility(View.GONE);
            }
        } else {
            accept.setVisibility(View.GONE);
            agreementWeb.setVisibility(View.GONE);
            mRelativeLayout.setVisibility(View.VISIBLE);
        }
    }
}
