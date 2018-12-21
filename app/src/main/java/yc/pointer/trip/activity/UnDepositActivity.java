package yc.pointer.trip.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import yc.pointer.trip.R;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.eventbean.PayBean;
import yc.pointer.trip.bean.PayOrderPayTypeBean;
import yc.pointer.trip.bean.SaveMesgBean;
import yc.pointer.trip.bean.WXPayBean;
import yc.pointer.trip.event.DepositAlipayEntryEvent;
import yc.pointer.trip.event.UnDepositWXPayEvent;
import yc.pointer.trip.event.UndepositAlipayEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.PayResult;
import yc.pointer.trip.untils.SharedPreferencesUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.DialogKnow;
import yc.pointer.trip.view.DialogSure;
import yc.pointer.trip.view.LoadDialog;
import yc.pointer.trip.view.ToolbarWrapper;

/**
 * Created by moyan on 2018/1/24.
 * 交押金界面
 */

public class UnDepositActivity extends BaseActivity {

    private static final int SDK_PAY_FLAG = 1;
    @BindView(R.id.pay_commit)
    Button payCommit;
    @BindView(R.id.cancel_order)
    Button cancelOrder;
    @BindView(R.id.bottom_linear)
    RelativeLayout bottomLinear;
    @BindView(R.id.radioB_alipay)
    RadioButton radioBAlipay;
    @BindView(R.id.radioB_weixin)
    RadioButton radioBWeixin;


    @BindView(R.id.check_rules)
    CheckBox checkRules;//判断是否阅读规则
    @BindView(R.id.rules)
    TextView readRules;//点击阅读《押金协议》

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

                        getServiceMine();

                        Toast.makeText(UnDepositActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {

                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(UnDepositActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    private boolean dtFlag;
    private String mDevCode;//设备识别码
    private long mTimeStamp;//时间戳
    private String mUserId;

    private LoadDialog mLoadDialog;
    private String isReadedDepositRules = "";

    public static UnDepositActivity unDepositActivity;

    private final int READDEPOSITRULES = 1;

    @Override
    protected int getContentViewLayout() {
        return R.layout.deposit_layout;
    }


    @Override
    protected void initView() {
        ToolbarWrapper toolbarWrapper = new ToolbarWrapper(this);
        toolbarWrapper.setCustomTitle(R.string.undeposit);
        unDepositActivity = this;

        //设备识别码
        mDevCode = ((MyApplication) getApplication()).getDevcode();
        //时间戳
        mTimeStamp = ((MyApplication) getApplication()).getTimestamp();
        mUserId = SharedPreferencesUtils.getInstance().getString(this, "useId");

        dtFlag = APPUtils.judgeTimeDev(this, mDevCode, mTimeStamp);
        //开启动画
        mLoadDialog = new LoadDialog(this, "正在加载...", R.drawable.load_animation);

        isReadedDepositRules = MyApplication.mApp.getUserBean().getIs_yj();//判断是否读过押金协议
        if (!StringUtil.isEmpty(isReadedDepositRules)) {
            if (isReadedDepositRules.equals("1")) {
                checkRules.setChecked(true);
            } else {
                checkRules.setChecked(false);
            }
        }
        checkRules.setClickable(false);

    }


    @Override
    protected boolean needEventBus() {
        return true;
    }


    /**
     * 支付宝支付请求接口
     *
     * @param url 接口地址
     */
    private void payService(String url) {
        if (dtFlag) {
            OkHttpUtils.getInstance().post(url, getRequestbody(), new HttpCallBack(new UndepositAlipayEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void payBean(UndepositAlipayEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        final PayOrderPayTypeBean bean = event.getData();
        if (bean.getStatus() == 0) {
            Runnable payRunnable = new Runnable() {
                @Override
                public void run() {

                    PayTask alipay = new PayTask(UnDepositActivity.this);
                    //String orderPay="app_id=2017010304822574&biz_content=%7b%22body%22%3a%22%e8%ae%a2%e5%8d%95%e6%94%af%e4%bb%98%22%2c%22out_trade_no%22%3a%22201704171305304101207%22%2c%22product_code%22%3a%22QUICK_MSECURITY_PAY%22%2c%22subject%22%3a%22%e8%ae%a2%e5%8d%95%e9%a2%84%e4%bb%98%e6%ac%be%22%2c%22timeout_express%22%3a%2230m%22%2c%22total_amount%22%3a%22200%22%7d&charset=UTF-8&format=json&method=alipay.trade.app.pay&notify_url=http%3a%2f%2f139.196.106.89%3a8888%2fAlipay_Notify.aspx&sign_type=RSA&timestamp=2017-04-17+16%3a12%3a40&version=1.0&sign=FrYQVae5tw55E6eFkl2BAOmsGUI8xYycdc4DooSBTKRi0NDwDsoae913AeDwVQfkG%2bzgOAEpileJLVeYV5%2b%2buXI01VK9Rcf2vGYdA0n9wMqEnXm7I4hilyO28cW0%2b5OJzMOnom%2fhJSU9F9lX9faectWht%2fp%2f4LmgLPGRKgEEwBRa74Sw7KaB8K6oKagwHv1y77jD4ta%2fslrKRws%2fk5hJz8MG%2f8mJD%2fAKQ62b7aihEwBf71LrDsJYmVKH0DmvzAaSsqb1M4fAKclSw5CWvYly3%2bMclefKlSX%2fjck3lFRu4w51zHUQ1mil1Ws91tqsTLFa0r%2fKSZpjPAcZ4XBzpagGQg%3d%3d";
                    Map<String, String> result = alipay.payV2(bean.getOrderString(), true);
//                    Map<String, String> result = alipay.payV2("app_id=2017010304822574&biz_content=%7b%22body%22%3a%22%e8%ae%a2%e5%8d%95%e6%94%af%e4%bb%98%22%2c%22out_trade_no%22%3a%22201704171305304101207%22%2c%22product_code%22%3a%22QUICK_MSECURITY_PAY%22%2c%22subject%22%3a%22%e8%ae%a2%e5%8d%95%e9%a2%84%e4%bb%98%e6%ac%be%22%2c%22timeout_express%22%3a%2230m%22%2c%22total_amount%22%3a%22200%22%7d&charset=UTF-8&format=json&method=alipay.trade.app.pay&notify_url=http%3a%2f%2f139.196.106.89%3a8888%2fAlipay_Notify.aspx&sign_type=RSA&timestamp=2017-04-17+16%3a12%3a40&version=1.0&sign=FrYQVae5tw55E6eFkl2BAOmsGUI8xYycdc4DooSBTKRi0NDwDsoae913AeDwVQfkG%2bzgOAEpileJLVeYV5%2b%2buXI01VK9Rcf2vGYdA0n9wMqEnXm7I4hilyO28cW0%2b5OJzMOnom%2fhJSU9F9lX9faectWht%2fp%2f4LmgLPGRKgEEwBRa74Sw7KaB8K6oKagwHv1y77jD4ta%2fslrKRws%2fk5hJz8MG%2f8mJD%2fAKQ62b7aihEwBf71LrDsJYmVKH0DmvzAaSsqb1M4fAKclSw5CWvYly3%2bMclefKlSX%2fjck3lFRu4w51zHUQ1mil1Ws91tqsTLFa0r%2fKSZpjPAcZ4XBzpagGQg%3d%3d", true);
                    Message msg = new Message();
                    msg.what = SDK_PAY_FLAG;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }
            };

            Thread payThread = new Thread(payRunnable);
            payThread.start();

        } else {
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }

    /**
     * 微信支付
     *
     * @param url 地址
     */
    private void WXpayService(String url) {
        if (dtFlag) {
            OkHttpUtils.getInstance().post(url, getRequestbody(), new HttpCallBack(new UnDepositWXPayEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void WXBean(UnDepositWXPayEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络状态异常，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }
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
     * 公共请求参数
     *
     * @return
     */
    private RequestBody getRequestbody() {

        String sign = Md5Utils.createMD5("devcode=" + mDevCode + "timestamp=" + mTimeStamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
        RequestBody requestBody = new FormBody.Builder()
                .add("devcode", mDevCode)
                .add("timestamp", String.valueOf(mTimeStamp))
                .add("uid", mUserId)
                .add("signature", sign)
                .build();
        return requestBody;
    }

    @OnClick({R.id.pay_commit, R.id.cancel_order, R.id.rules})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.pay_commit:
                if (checkRules.isChecked()) {
                    if (radioBAlipay.isChecked()) {
                        //请求支付宝
                        payService(URLUtils.DEPOSIT_AILPAY);
                    } else if (radioBWeixin.isChecked()) {
                        //请求微信
                        ((MyApplication) getApplication()).setWxPayType("2");
                        WXpayService(URLUtils.DEPOSIT_WXPAY);
                    }
                } else {
                    Toast.makeText(this, "付款之前请仔细阅读《指针增值服务协议》并同意", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.cancel_order:
                finish();
                break;
            case R.id.rules://阅读《押金服务协议》的结果
                Intent intent = new Intent(UnDepositActivity.this, AgreementActivity.class);
                intent.putExtra("logFlag", "deposit");
                intent.putExtra("isReade", isReadedDepositRules);
                startActivityForResult(intent, READDEPOSITRULES);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READDEPOSITRULES) {
            if (resultCode == 2) {
                isReadedDepositRules = data.getStringExtra("readedStatus");
                if (!StringUtil.isEmpty(isReadedDepositRules) && isReadedDepositRules.equals("1")) {
                    checkRules.setChecked(true);
                } else {
                    checkRules.setChecked(false);
                }
            }
        }
    }

    /**
     * 请求个人信息
     */
    private void getServiceMine() {
        if (dtFlag) {
            String sign = Md5Utils.createMD5("devcode=" + mDevCode + "timestamp=" + mTimeStamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", mDevCode)
                    .add("signature", sign)
                    .add("timestamp", String.valueOf(mTimeStamp))
                    .add("uid", mUserId)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.MY_PERSON_SETTING, requestBody, new HttpCallBack(new DepositAlipayEntryEvent()));
            mLoadDialog.show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void personSetBean(DepositAlipayEntryEvent event) {
        if (event.isTimeOut()) {
            mLoadDialog.dismiss();
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        SaveMesgBean bean = event.getData();
        if (bean.getStatus() == 0) {

            ((MyApplication) getApplication()).setUserBean(bean.getData());
            String is_order = bean.getData().getIs_order();
            String bank_num = bean.getData().getBank_num();
            String is_jie = bean.getData().getIs_jie();
            SharedPreferencesUtils.getInstance().putString(this, "isOrder", is_order);
            if (!StringUtil.isEmpty(is_order) && is_order.equals("1")) {
//            ((MyApplication) getApplication()).setUserIsBinding(true);
                SharedPreferencesUtils.getInstance().putBoolean(this, "isPayDeposit", true);
            } else if (is_order.equals("0")) {
//            ((MyApplication) getApplication()).setUserIsBinding(false);
                SharedPreferencesUtils.getInstance().putBoolean(this, "isPayDeposit", false);
            }

            if (!StringUtil.isEmpty(bank_num)) {
                ((MyApplication) getApplication()).setUserIsVerify(true);
            } else {
                ((MyApplication) getApplication()).setUserIsVerify(false);
            }
            mLoadDialog.dismiss();
            if (!StringUtil.isEmpty(is_jie)) {
                if (is_jie.equals("2")) {
                    new DialogKnow(UnDepositActivity.this, R.style.user_default_dialog, new DialogKnow.CallBackListener() {
                        @Override
                        public void onClickListener() {
                            //Intent intent1 = new Intent();
                            //intent1.setAction("刷新支付");
                            //sendBroadcast(intent1);
                            EventBus.getDefault().post(new PayBean("刷新支付"));
                            finish();
                        }
                    }).setTitle("温馨提示")
                            .setMsg("恭喜您注册成为指针会员，享有发、接单以及全类型提现收益的权益，感谢您对指针自由行的信任")
                            .setPositiveButton("好的")
                            .show();
                } else if (is_jie.equals("1")) {
                    //已提交信息，审核中
                    new DialogKnow(UnDepositActivity.this, R.style.user_default_dialog, new DialogKnow.CallBackListener() {
                        @Override
                        public void onClickListener() {
                            //Intent intent1 = new Intent();
                            //intent1.setAction("刷新支付");
                            //sendBroadcast(intent1);
                            EventBus.getDefault().post(new PayBean("刷新支付"));
                            finish();
                        }
                    })      .setTitle("温馨提示")
                            .setMsg("恭喜您离指针会员又近了一步，您的会员身份认证信息会在1~2个工作日审核完成，请耐心等待")
                            .setPositiveButton("好的")
                            .show();
                } else {
                    new DialogSure(UnDepositActivity.this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                        @Override
                        public void onClickListener(DialogSure dialogSure, boolean trueEnable) {
                            if (trueEnable) {
                                //跳转指针会员信息提交
                                startActivity(new Intent(UnDepositActivity.this, VerifyActivity.class));
                                //Intent intent1 = new Intent();
                                //intent1.setAction("刷新支付");
                                //sendBroadcast(intent1);
                                EventBus.getDefault().post(new PayBean("刷新支付"));
                                finish();

                            } else {
                                //Intent intent1 = new Intent();
                                //intent1.setAction("刷新支付");
                                //sendBroadcast(intent1);
                                EventBus.getDefault().post(new PayBean("刷新支付"));
                                finish();
                            }

                        }
                    })      .setTitle("温馨提示")
                            .setMsg("您的会员身份信息尚未完成实名审核认证，请先前往【指针会员】完善实名信息，您离指针会员只有一步之遥。")
                            .setPositiveButton("前往")
                            .setNegativeButton("取消")
                            .show();
                }
            }

        } else {
            mLoadDialog.dismiss();
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
        }
    }
}
