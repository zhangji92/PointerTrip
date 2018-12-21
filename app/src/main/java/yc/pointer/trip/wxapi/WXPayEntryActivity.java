package yc.pointer.trip.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import yc.pointer.trip.R;
import yc.pointer.trip.activity.BillActivity;
import yc.pointer.trip.activity.CouponActivity;
import yc.pointer.trip.activity.DepositedActivity;
import yc.pointer.trip.activity.MemberUpgradeActivity;
import yc.pointer.trip.activity.NewDepositedActivity;
import yc.pointer.trip.activity.NewUnDepositActivity;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.bean.SaveMesgBean;
import yc.pointer.trip.bean.eventbean.FinishBean;
import yc.pointer.trip.bean.eventbean.PayBean;
import yc.pointer.trip.bean.eventbean.UpDataBean;
import yc.pointer.trip.event.DepositWXPayEntryEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.SharedPreferencesUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.DialogKnow;
import yc.pointer.trip.view.DialogSure;
import yc.pointer.trip.view.LoadDialog;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

    private IWXAPI api;
    private Timer timer;
    private Task task;

    private LoadDialog mLoadDialog;

    private String mUserId;//用户id
    private long mTimestamp;//时间戳
    private String mDevcode;//手机识别码

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_pay);//加载一个透明布局
        api = WXAPIFactory.createWXAPI(this, "wxe44b2ed1e16f131c");
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(BaseResp resp) {//微信的回调，
//		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {//支付的回调 根据errCode判断支付回调是否成功
            // 0：成功  -1：大多数为签名问题 其他的就去看微信官方文档了
//            Log.d(TAG, "onPayFinish,errCode=" + resp.errCode);
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("提示");
//            builder.setMessage(String.valueOf(resp.errCode));
//            builder.show();
            switch (resp.errCode) {
                case 0:
//                    timer = new Timer();
//                    task = new Task();
//                    //schedule 计划安排，时间表
////                    timer.schedule(task, 5 * 1000, 5 * 1000);
//                    timer.schedule(task, 5 * 1000);
                    String oid = ((MyApplication) getApplication()).getOid();
                    String wxPayType = ((MyApplication) getApplication()).getWxPayType();

                    if (!StringUtil.isEmpty(oid) && !wxPayType.equals("2")) {
                        //Intent intentBroadcast = new Intent();
                        //intentBroadcast.setAction("更新数据");
                        //sendBroadcast(intentBroadcast);
                        EventBus.getDefault().post(new UpDataBean("更新数据"));
                        Intent intent = new Intent(WXPayEntryActivity.this, BillActivity.class);
                        intent.putExtra("oid", oid);
                        startActivity(intent);
                        ((MyApplication) getApplication()).setOid("");
                        PayOrderActivity.payOrderActivity.finish();
                        finish();
//                        Timer timer = new Timer();
//                        timer.schedule(new TimerTask() {
//                            @Override
//                            public void run() {
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        mLoadDialog.dismiss();
//                                        Intent intent = new Intent(WXPayEntryActivity.this, BillActivity.class);
//                                        intent.putExtra("oid", oid);
//                                        startActivity(intent);
//                                        PayOrderActivity.payOrderActivity.finish();
//                                        finish();
//                                    }
//                                });
//                            }
//                        },2000);
//
//                        Toast.makeText(WXPayEntryActivity.this, "成功", Toast.LENGTH_SHORT).show();
                    } else if (!StringUtil.isEmpty(wxPayType) && wxPayType.equals("2")) {
                        //请求最新的个人信息
                        ((MyApplication) getApplication()).setWxPayType("");
                        EventBus.getDefault().register(WXPayEntryActivity.this);
                        mUserId = ((MyApplication) getApplication()).getUserId();
                        mTimestamp = ((MyApplication) getApplication()).getTimestamp();
                        mDevcode = ((MyApplication) getApplication()).getDevcode();
                        getServiceMine();
                    } else {
                        Intent intent = new Intent(WXPayEntryActivity.this, CouponActivity.class);
                        startActivity(intent);
                        //Intent intentBroadcast = new Intent();
                        //intentBroadcast.setAction("finish");
                        //sendBroadcast(intentBroadcast);
                        EventBus.getDefault().post(new FinishBean("finish"));
                        finish();
                    }

                    break;
                case -1:
                    Toast.makeText(this, resp.errStr, Toast.LENGTH_SHORT).show();
                    break;
                case -2:
                    String orderOid = ((MyApplication) getApplication()).getOid();
                    String payType = ((MyApplication) getApplication()).getWxPayType();
                    if (!StringUtil.isEmpty(orderOid) && !payType.equals("2")) {
//                        Intent intent = new Intent(WXPayEntryActivity.this, UnpaidActivity.class);
////                  intent.putExtra("errorMsg", resultError);
//                        PayOrderActivity.payOrderActivity.finish();
//                        startActivity(intent);
//                        finish();
                    } else {
                        finish();
                    }
                    Toast.makeText(this, "取消了", Toast.LENGTH_SHORT).show();
                    break;
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
            OkHttpUtils.getInstance().post(URLUtils.MY_PERSON_SETTING, requestBody, new HttpCallBack(new DepositWXPayEntryEvent()));
            //开启动画
            mLoadDialog = new LoadDialog(this, "正在加载...", R.drawable.load_animation);//自己设定的加载动画（根据需求可省略）
            mLoadDialog.show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void personSetBean(DepositWXPayEntryEvent event) {
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
            final boolean activityType = SharedPreferencesUtils.getInstance().getBoolean(this, "ActivityType");
            final String is_vip = bean.getData().getIs_vip();
            if (!StringUtil.isEmpty(is_jie)) {
                if (is_jie.equals("2")) {
                    new DialogKnow(WXPayEntryActivity.this, R.style.user_default_dialog, new DialogKnow.CallBackListener() {
                        @Override
                        public void onClickListener() {
                            //Intent intent1 = new Intent();
                            //intent1.setAction("刷新支付");
                            //sendBroadcast(intent1);
                            EventBus.getDefault().post(new PayBean("刷新支付"));
                            if (activityType) {
                                NewUnDepositActivity.unDepositActivity.finish();
                            } else {
                                MemberUpgradeActivity.mMember.finish();
                            }
                            finish();
                        }
                    }).setTitle("温馨提示")
                            .setMsg("恭喜您注册成为指针会员，享有发、接单以及全类型提现收益的权益，感谢您对指针自由行的信任")
                            .setPositiveButton("好的")
                            .show();
                } else if (is_jie.equals("1")) {

                    //已提交信息，审核中
                    new DialogKnow(WXPayEntryActivity.this, R.style.user_default_dialog, new DialogKnow.CallBackListener() {
                        @Override
                        public void onClickListener() {
                            //Intent intent1 = new Intent();
                            //intent1.setAction("刷新支付");
                            //sendBroadcast(intent1);
                            EventBus.getDefault().post(new PayBean("刷新支付"));
                            if (activityType) {
                                NewUnDepositActivity.unDepositActivity.finish();
                            } else {
                                MemberUpgradeActivity.mMember.finish();
                            }
                            finish();
                        }
                    }).setTitle("温馨提示")
                            .setMsg("恭喜您离指针会员又近了一步，您的会员身份认证信息会在1~2个工作日审核完成，请耐心等待")
                            .setPositiveButton("好的")
                            .show();
                } else {
                    new DialogSure(WXPayEntryActivity.this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                        @Override
                        public void onClickListener(DialogSure dialogSure, boolean trueEnable) {
                            if (trueEnable) {
                                //跳转指针会员信息提交
                                //startActivity(new Intent(WXPayEntryActivity.this, VerifyActivity.class));
                                //Intent intent1 = new Intent();
                                //intent1.setAction("刷新支付");
                                //sendBroadcast(intent1);
                                if ("1".equals(is_vip)) {//99权益
                                    startActivity(new Intent(WXPayEntryActivity.this, NewDepositedActivity.class));
                                } else if ("2".equals(is_vip)) {//399权益
                                    startActivity(new Intent(WXPayEntryActivity.this, NewDepositedActivity.class));
                                }
                                EventBus.getDefault().post(new PayBean("刷新支付"));
                                if (activityType) {
                                    NewUnDepositActivity.unDepositActivity.finish();
                                } else {
                                    MemberUpgradeActivity.mMember.finish();
                                }
                                finish();
                            } else {
                                //Intent intent1 = new Intent();
                                //intent1.setAction("刷新支付");
                                //sendBroadcast(intent1);
                                EventBus.getDefault().post(new PayBean("刷新支付"));
                                if (activityType) {
                                    NewUnDepositActivity.unDepositActivity.finish();
                                } else {
                                    MemberUpgradeActivity.mMember.finish();
                                }
                                finish();
                            }
                        }
                    }).setTitle("温馨提示")
                            //.setMsg("您的会员身份信息尚未完成实名审核认证，请先前往【指针会员】完善实名信息，您离指针会员只有一步之遥。")
                            .setMsg("您已完成会员认证，是否查看已持有的权益!")
                            .setPositiveButton("查看")
                            .setNegativeButton("取消")
                            .show();
                }
            }
        } else {
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * TimerTask对象，主要用于定时拉去服务器信息
     */
    public class Task extends TimerTask {
        @Override
        public void run() {
            Log.e("AAA", "开始执行执行timer定时任务...");
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //获取网络状态
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        EventBus.getDefault().unregister(this);
    }


}