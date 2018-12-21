package yc.pointer.trip.wxapi;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;

import butterknife.BindView;
import butterknife.OnClick;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import okhttp3.FormBody;
import okhttp3.RequestBody;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import yc.pointer.trip.R;
import yc.pointer.trip.activity.*;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.eventbean.PayBean;
import yc.pointer.trip.bean.PayOrderBean;
import yc.pointer.trip.bean.PayOrderPayTypeBean;
import yc.pointer.trip.bean.WXPayBean;
import yc.pointer.trip.bean.eventbean.UpDataBean;
import yc.pointer.trip.event.PayOrderEvent;
import yc.pointer.trip.event.PayOrderPayTypeEvent;
import yc.pointer.trip.event.WXPayEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.*;
import yc.pointer.trip.view.CustomCircleImage;
import yc.pointer.trip.view.DialogSure;
import yc.pointer.trip.view.ToolbarWrapper;

import java.util.Map;

import static yc.pointer.trip.R.*;

/**
 * 支付页面 微信支付和支付宝支付
 */
public class PayOrderActivity extends BaseActivity {
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
                        Intent intent = new Intent(PayOrderActivity.this, BillActivity.class);
                        if (!StringUtil.isEmpty(flag) && flag.equals("continuePay")) {
                            intent.putExtra("oid", mContinueOid);
                        } else {
                            intent.putExtra("oid", oid);
                        }
                        //Intent intent1 = new Intent();
                        //intent1.setAction("更新数据");
                        //sendBroadcast(intent1);
                        EventBus.getDefault().post(new UpDataBean("更新数据"));
                        startActivity(intent);
                        finish();
                        Toast.makeText(PayOrderActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
//                        Intent intent = new Intent(PayOrderActivity.this, UnpaidActivity.class);
//                        startActivity(intent);
//                        finish();
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(PayOrderActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    @BindView(id.radioB_alipay)
    RadioButton radioBAlipay;
    @BindView(id.radioB_weixin)
    RadioButton radioBWeiXin;//
    @BindView(id.radioG_pay)
    RadioGroup radioGPay;//支付宝或者微信
    @BindView(id.pay_commit)
    Button payCommit;//付款
    @BindView(id.cancel_order)
    Button cancelOrder;//取消订单
    @BindView(id.pay_price)
    TextView payPrice;//价钱
    @BindView(id.pay_head)
    CustomCircleImage payHead;//头像
    @BindView(id.pay_nick)
    TextView payNick;//昵称
    @BindView(id.pay_start_point)
    TextView payStartPoint;//出发地
    @BindView(id.pay_destination)
    TextView payDestination;//目的地
    private String oid;//订单id
    private String mContinueOid;//继续支付之后带到订单状态界面的订单id

    private String mUserId;//用户id
    private String mDevCode;//设备识别码
    private long mTimeStamp;//时间戳

    private boolean isDeposited;
    private String bankNum;
    private String mIsOrder;

    private boolean dtFlag;
    private String flag;
    public static PayOrderActivity payOrderActivity;
    //private MyBroadcastReciver myBroadcastReciver;

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_pay_order;
    }

    @Override
    protected void initView() {
        ToolbarWrapper toolbarWrapper = new ToolbarWrapper(this);
        toolbarWrapper.setCustomTitle(string.pay_title);
        toolbarWrapper.setShowBack(false);
        payOrderActivity = this;
        oid = getIntent().getStringExtra("oid");
        if (!StringUtil.isEmpty(oid)) {
            ((MyApplication) getApplication()).setOid(oid);
        }
        flag = getIntent().getStringExtra("flag");//订单预览界面传递过来 续费页面传递过来判断支付的标志flag
        //用户id
        mUserId = ((MyApplication) getApplication()).getUserId();
        //设备识别码
        mDevCode = ((MyApplication) getApplication()).getDevcode();
        //时间戳
        mTimeStamp = ((MyApplication) getApplication()).getTimestamp();
        //判断时间戳和设备识别码是否为空
        dtFlag = APPUtils.judgeTimeDev(this, mDevCode, mTimeStamp);


//        isDeposited = SharedPreferencesUtils.getInstance().getBoolean(PayOrderActivity.this,"isPayDeposit");
//        bankNum = ((MyApplication) getApplication()).getUserBean().getBank_num();//是否提交过银行卡
        if (!StringUtil.isEmpty(flag) && flag.equals("continuePay")) {
            getOrderService(0);//继续玩请求信息
        } else {
            getOrderService(1);//出发吧请求信息
        }
        //IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction("刷新支付");
        //myBroadcastReciver = new MyBroadcastReciver();
        //registerReceiver(myBroadcastReciver, intentFilter);
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

    @OnClick({id.pay_commit, id.cancel_order, id.radioB_alipay, id.radioB_weixin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case id.radioB_alipay://支付宝支付

                break;
            case id.radioB_weixin://微信支付

                break;
            case id.pay_commit://付款
                mIsOrder = MyApplication.mApp.getUserBean().getIs_jie();//是否可以发单
//                isDeposited = SharedPreferencesUtils.getInstance().getBoolean(PayOrderActivity.this,"isPayDeposit");
                if (!StringUtil.isEmpty(mIsOrder) && mIsOrder.equals("2")) {
                    //认证通过正常逻辑
                    judgeDeposit();
                } else if (mIsOrder.equals("1")) {
                    //认证审核中
                    new DialogSure(PayOrderActivity.this, style.user_default_dialog, new DialogSure.CallBackListener() {
                        @Override
                        public void onClickListener(DialogSure dialogSure, boolean cancelable) {
                            if (cancelable) {
                                Intent intent = new Intent(PayOrderActivity.this, VerifyActivity.class);
                                intent.putExtra("payOrder", "pay");
                                startActivity(intent);
                            } else {

                            }
                        }
                    }).setTitle("温馨提示").setMsg("您提交的会员认证信息正在火速审核中,暂不可发布订单，请耐心等待").setPositiveButton("前往").setNegativeButton("放弃").show();
                } else if (mIsOrder.equals("3")) {
                    //认证不成功
                    new DialogSure(PayOrderActivity.this, style.user_default_dialog, new DialogSure.CallBackListener() {
                        @Override
                        public void onClickListener(DialogSure dialogSure, boolean cancelable) {
                            if (cancelable) {

                                Intent intent = new Intent(PayOrderActivity.this, VerifyActivity.class);
                                intent.putExtra("payOrder", "pay");
                                startActivity(intent);

                            } else {

                            }
                        }
                    }).setTitle("非常抱歉").setMsg("您的会员升级申请失败，请重新完成会员身份认证").setPositiveButton("前往").setNegativeButton("放弃").show();


                } else {
                    //未提交认证信息调到审核界面
                    new DialogSure(PayOrderActivity.this, style.user_default_dialog, new DialogSure.CallBackListener() {
                        @Override
                        public void onClickListener(DialogSure dialogSure, boolean cancelable) {
                            if (cancelable) {
                                Intent intent = new Intent(PayOrderActivity.this, VerifyActivity.class);
                                intent.putExtra("payOrder", "pay");
                                startActivity(intent);
                            } else {
                            }
                        }
                    }).setTitle("温馨提示").setMsg("您尚未升级成为指针会员，请先去认证会员身份，方可完成发单操作").setPositiveButton("确定").setNegativeButton("取消").show();
                }


//
//                if (!StringUtil.isEmpty(bankNum)){
//                //已提交身份信息,判断押金
//
//                    judgeDeposit();
//
//
//                }else {
//                    //身份审核未通过，调到审核界面
//                    new DialogSure(PayOrderActivity.this, style.user_default_dialog, new DialogSure.CallBackListener() {
//                        @Override
//                        public void onClickListener(DialogSure dialogSure, boolean cancelable) {
//                            if (cancelable) {
//
//                                Intent intent = new Intent(PayOrderActivity.this, VerifyActivity.class);
//                                intent.putExtra("payOrder", "pay");
//                                startActivityForResult(intent, 1);
//
//                            } else {
//
//                            }
//                        }
//                    }).setTitle("非常抱歉").setMsg("您尚未完成身份认证,不能发布订单").setPositiveButton("去认证").setNegativeButton("不用了").show();
//
//                }


//                    if (!StringUtil.isEmpty(mIsOrder) && mIsOrder.equals("1")) {
//                        if (!StringUtil.isEmpty(flag) && flag.equals("continuePay")) {//续费页面的支付
//                            if (radioBWeiXin.isChecked()) {
//                                //请求调起微支付的参数
//                                WXpayService(URLUtils.CONTINUE_PLAY_WECHAT_PAY);
//                            }
//                            if (radioBAlipay.isChecked()) {
//                                //支付宝
//                                payService(1, URLUtils.CONTINUE_PLAY_PAY);
//                            }
//                        } else {//订单预览页面的支付
//                            if (radioBWeiXin.isChecked()) {
//                                //请求调起微支付的参数
//                                WXpayService(URLUtils.WX_PAY);
//                            }
//                            if (radioBAlipay.isChecked()) {
//                                //支付宝
//                                payService(1, URLUtils.PAY_SAVE);
//
//                            }
//                        }
//                    } else {
//                        //支付界面使用DialogUtil
//                        //身份审核未通过，调到审核界面
//                        new DialogSure(PayOrderActivity.this, style.user_default_dialog, new DialogSure.CallBackListener() {
//                            @Override
//                            public void onClickListener(DialogSure dialogSure, boolean cancelable) {
//                                if (cancelable) {
//
//                                    Intent intent = new Intent(PayOrderActivity.this, VerifyActivity.class);
//                                    intent.putExtra("payOrder", "pay");
//                                    startActivityForResult(intent, 1);
//
//                                } else {
//
//
//                                }
//                            }
//                        }).setTitle("非常抱歉").setMsg("您尚未完成实名认证,不能发布订单").setPositiveButton("去实名").setNegativeButton("不用了").show();
//
//                    }


                break;
            case id.cancel_order:
//                Intent intentMain = new Intent(PayOrderActivity.this, MainActivity.class);
//                intentMain.putExtra("mainFlag","cancelOrder");
//                startActivity(intentMain);
                // 弹框确认是否取消付款
                new DialogSure(PayOrderActivity.this, style.user_default_dialog, new DialogSure.CallBackListener() {
                    @Override
                    public void onClickListener(DialogSure dialogSure, boolean cancelable) {
                        if (cancelable) {
                            if (!StringUtil.isEmpty(flag) && flag.equals("orderPay")) {
                                Intent intent = new Intent(PayOrderActivity.this, UnpaidActivity.class);
                                startActivity(intent);
                            }
                            finish();
                        }

                    }
                }).setTitle("温馨提示").setMsg("是否取消对该订单付款？").setPositiveButton("确定").setNegativeButton("取消").show();
                break;

        }
    }

    /**
     * 判断押金缴纳与否的操作
     */
    private void judgeDeposit() {
//        if (isDeposited) {
            //已交押金
            if (!StringUtil.isEmpty(flag) && flag.equals("continuePay")) {//续费页面的支付
                if (radioBWeiXin.isChecked()) {
                    //请求调起微支付的参数
                    WXpayService(URLUtils.CONTINUE_PLAY_WECHAT_PAY);
                }
                if (radioBAlipay.isChecked()) {
                    //支付宝
                    payService(1, URLUtils.CONTINUE_PLAY_PAY);

                }
            } else {//订单预览页面的支付
                if (radioBWeiXin.isChecked()) {
                    //请求调起微支付的参数
                    WXpayService(URLUtils.WX_PAY);
                }
                if (radioBAlipay.isChecked()) {
                    //支付宝
                    payService(1, URLUtils.PAY_SAVE);

                }
            }
//        } else {
//            //未支付押金的跳转押金
//            new DialogSure(PayOrderActivity.this, style.user_default_dialog, new DialogSure.CallBackListener() {
//                @Override
//                public void onClickListener(DialogSure dialogSure, boolean cancelable) {
//                    if (cancelable) {
//
//                        Intent intent = new Intent(PayOrderActivity.this, UnDepositActivity.class);
//                        startActivity(intent);
//
//                    } else {
//
//
//                    }
//                }
//            }).setTitle("非常抱歉").setMsg("您尚未完成会员充值,不能完成发单操作").setPositiveButton("去充值").setNegativeButton("那算了").show();
//
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
//                if (resultCode == 1) {
//                    new DialogKnow(PayOrderActivity.this, style.user_default_dialog, new DialogKnow.CallBackListener() {
//                        @Override
//                        public void onClickListener() {
//
//                        }
//                    }).setMsg("您已完成实名认证，可继续完成付款，祝您旅途愉快").setPositiveButton("我知道了").show();
//                }
                break;
        }
    }

    /**
     * 请求订单信息
     */
    private void getOrderService(int type) {

        if (dtFlag) {
            //参数名	类型	说明	是否必填
            //signature	String	签名	是
            //devcode	String	手机唯一识别码	是
            //timestamp	String	当前时间戳	是
            //uid	Int	用户id	是
            //oid	Int	订单id	是
            String sign = Md5Utils.createMD5("devcode=" + mDevCode + "oid=" + oid + "timestamp=" + mTimeStamp + "type=" + type + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", mDevCode)
                    .add("oid", oid)
                    .add("timestamp", String.valueOf(mTimeStamp))
                    .add("type", String.valueOf(type))
                    .add("uid", mUserId)
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.PAY_ORDER, requestBody, new HttpCallBack(new PayOrderEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void orderServiceBean(PayOrderEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        PayOrderBean bean = event.getData();
        if (bean.getStatus() == 0) {

            mContinueOid = bean.getData().getOid();//继续玩支付之后带到订单状态的id
            payPrice.setText(bean.getData().getMoney());
//            payStartPoint.setText(bean.getData().getSaddress());
            payDestination.setText(bean.getData().getMaddress());
            payNick.setText(bean.getData().getNickname());
            OkHttpUtils.displayImg(payHead, bean.getData().getPic());
        } else {
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            payCommit.setClickable(false);
            payCommit.setBackgroundColor(Color.parseColor("#b8b8b8"));
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }

    /**
     * 支付宝支付请求接口
     *
     * @param payType 类型
     * @param url     接口地址
     */
    private void payService(int payType, String url) {

        if (dtFlag) {
            //参数名	类型	说明	是否必填
            //signature	String	签名	是
            //devcode	String	手机唯一识别码	是
            //timestamp	String	当前时间戳	是
            //uid	Int	用户id	是
            //oid	Int	订单id	是
            //pay_way	Int	支付方式1支付宝2微信	是

            String sign = Md5Utils.createMD5("devcode=" + mDevCode + "oid=" + oid + "pay_way=" + payType + "timestamp=" + mTimeStamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", mDevCode)
                    .add("oid", oid)
                    .add("pay_way", String.valueOf(payType))
                    .add("timestamp", String.valueOf(mTimeStamp))
                    .add("uid", mUserId)
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(url, requestBody, new HttpCallBack(new PayOrderPayTypeEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void payBean(PayOrderPayTypeEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        final PayOrderPayTypeBean bean = event.getData();
        if (bean.getStatus() == 0) {
            Runnable payRunnable = new Runnable() {
                @Override
                public void run() {
                    PayTask alipay = new PayTask(PayOrderActivity.this);
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
            // 参数名	类型	  说明	     是否必填
            // signature	String	  签名	         是
            // devcode	String	手机唯一识别码	 是
            // timestamp	String	当前时间戳	     是
            // uid	    Int	     用户id	         是
            // oid	    Int	     订单id	         是
            OkHttpUtils.getInstance().post(url, getRequestbody(), new HttpCallBack(new WXPayEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void WXBean(WXPayEvent event) {
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
        String sign = Md5Utils.createMD5("devcode=" + mDevCode + "oid=" + oid + "timestamp=" + mTimeStamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
        RequestBody requestBody = new FormBody.Builder()
                .add("devcode", mDevCode)
                .add("oid", oid)
                .add("timestamp", String.valueOf(mTimeStamp))
                .add("uid", mUserId)
                .add("signature", sign)
                .build();
        return requestBody;
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        return false;
//    }

    ////广播接收内部类
    //private class MyBroadcastReciver extends BroadcastReceiver {
    //    @Override
    //    public void onReceive(Context context, Intent intent) {
    //        String action = intent.getAction();
    //        if (action.equals("刷新支付")) {
    //            isDeposited = SharedPreferencesUtils.getInstance().getBoolean(PayOrderActivity.this,"isPayDeposit");//是否可以发单
    //            bankNum = ((MyApplication) getApplication()).getUserBean().getBank_num();//是否提交过银行卡
    //
    //        }
    //    }
    //}

    /**
     * unDeposit ver 等activity发送过来
     *
     * @param payBean 刷新支付
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void pay(PayBean payBean) {
        if (payBean != null && payBean.getPay().equals("刷新支付")) {
            isDeposited = SharedPreferencesUtils.getInstance().getBoolean(PayOrderActivity.this, "isPayDeposit");//是否可以发单
            bankNum = ((MyApplication) getApplication()).getUserBean().getBank_num();//是否提交过银行卡

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //this.unregisterReceiver(myBroadcastReciver);
    }
}
