package yc.pointer.trip.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import yc.pointer.trip.R;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.CodeBean;
import yc.pointer.trip.bean.eventbean.PushMsgBen;
import yc.pointer.trip.bean.eventbean.ReceiverBean;
import yc.pointer.trip.bean.SaveMesgBean;
import yc.pointer.trip.bean.eventbean.RefreshEarningAProfitBean;
import yc.pointer.trip.bean.eventbean.RefreshLoadBean;
import yc.pointer.trip.event.BindingCodeEvent;
import yc.pointer.trip.event.BindingPhoneEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.SharedPreferencesUtils;
import yc.pointer.trip.untils.StatusBarUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.untils.TimerCount;
import yc.pointer.trip.view.ClearEditText;
import yc.pointer.trip.view.ToolbarWrapper;

/**
 * Created by moyan on 2018/1/10.
 * 绑定手机号
 */

public class BindingPhoneActivity extends BaseActivity {
    @BindView(R.id.standard_toolbar_title)
    TextView standardToolbarTitle;
    @BindView(R.id.standard_toolbar)
    Toolbar standardToolbar;
    @BindView(R.id.register_phone)
    ClearEditText registerPhone;
    @BindView(R.id.regiter_password)
    ClearEditText regiterPassword;
    @BindView(R.id.regiter_verify)
    EditText regiterVerify;
    @BindView(R.id.verify_button)
    Button verifyButton;
    @BindView(R.id.button)
    Button button;


    private String mPhone;
    private String mPad;
    private String mDevcode;
    private long mTimestamp;
    private TimerCount mTimerCount;
    private String useUid;
    private String logFlag;

    private String mAid;//活动id
    private String mtitle;//活动标题


    @Override
    protected int getContentViewLayout() {
        return R.layout.binding_phone;
    }

    @Override
    protected void initView() {
        StatusBarUtils.with(this).init();//设置沉浸式
        new ToolbarWrapper(this).setCustomTitle(R.string.binding_title);

        mTimerCount = new TimerCount(60000, 1000);
        mTimerCount.setButton(verifyButton);

        mDevcode = ((MyApplication) getApplication()).getDevcode();
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();
        useUid = getIntent().getStringExtra("uid");
        logFlag = getIntent().getStringExtra("logFlag");//跳转至登录页面的标志（判断由哪个页面跳转至登录界面）
        standardToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentOthersActivity();
            }
        });
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

    /**
     * 匹配手机号和密码
     *
     * @param context 上下文
     * @return true成功 false 失败
     */
    private boolean judgePhonePad(Context context) {
        mPhone = registerPhone.getText().toString();//手机号
        mPad = regiterPassword.getText().toString();//密码
        if (StringUtil.isMobileNo(context, mPhone) && !StringUtil.isEmpty(mPhone)) {
            if (mPad.length() < 6 && !StringUtil.isEmpty(mPad)) {
                Toast.makeText(this, "密码不得小于6位数", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * 获取验证码
     *
     * @param context 上下文
     */
    private void obtainCode(Context context) {
        boolean flag = judgePhonePad(context);
        boolean timeFlag = APPUtils.judgeTimeDev(context, mDevcode, mTimestamp);
        if (flag) {
            if (timeFlag) {
                mTimerCount.start();
                String str = Md5Utils.createMD5("devcode=" + mDevcode + "phone=" + mPhone + "timestamp=" + mTimestamp + "type=1" + URLUtils.WK_APP_KEY);
                RequestBody requestBody = new FormBody.Builder()
                        .add("timestamp", String.valueOf(mTimestamp))
                        .add("phone", mPhone)
                        .add("devcode", mDevcode)
                        .add("signature", str)
                        .add("type", "1")
                        .build();
                OkHttpUtils.getInstance().post(URLUtils.PHONE_CODE, requestBody, new HttpCallBack(new BindingCodeEvent()));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void obtainCodeEvent(BindingCodeEvent bindingCodeEvent) {
        if (bindingCodeEvent.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        CodeBean bean = bindingCodeEvent.getData();
        if (bean.getStatus() == 0) {
            Log.e("LogInterceptor", bean.getMsg());
        } else {
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 绑定手机号
     *
     * @param context 上下文
     */
    private void bindingPhone(Context context) {
        boolean msgFlag = judgePhonePad(context);
        boolean timeFlag = APPUtils.judgeTimeDev(context, mDevcode, mTimestamp);
        String code = regiterVerify.getText().toString();
        if (msgFlag) {
            if (timeFlag) {
                if (StringUtil.isEmpty(useUid)) {
                    Toast.makeText(this, "无效的用户Id", Toast.LENGTH_SHORT).show();
                    return;
                }
                mPad = Md5Utils.createMD5(mPad + URLUtils.WK_PWD_KEY);
                String str = Md5Utils.createMD5("code=" + code + "devcode=" + mDevcode + "phone=" + mPhone + "pwd=" + mPad + "timestamp=" + mTimestamp + "uid=" + useUid + URLUtils.WK_APP_KEY);
                RequestBody requestBody = new FormBody.Builder()
                        .add("timestamp", String.valueOf(mTimestamp))
                        .add("phone", mPhone)
                        .add("uid", useUid)
                        .add("pwd", mPad)
                        .add("devcode", mDevcode)
                        .add("signature", str)
                        .add("code", code)
                        .build();
                OkHttpUtils.getInstance().post(URLUtils.BINDING_PHONE, requestBody, new HttpCallBack(new BindingPhoneEvent()));

            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void registerEvent(BindingPhoneEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        SaveMesgBean data = event.getData();
        if (data.getStatus() == 0) {
            String loginPhone = data.getData().getPhone();
            String is_bd = data.getData().getIs_bd();
            String is_order = data.getData().getIs_order();
            String uid = data.getData().getUid();

            String bank_num = data.getData().getBank_num();

            ((MyApplication) getApplication()).setIslogin(true);
            ((MyApplication) getApplication()).setUserBean(data.getData());
            ((MyApplication) getApplication()).setUserId(uid);
            SharedPreferencesUtils.getInstance().putString(BindingPhoneActivity.this, "loginPhone", loginPhone);

            if (!StringUtil.isEmpty(bank_num)) {
                ((MyApplication) getApplication()).setUserIsVerify(true);
            } else {
                ((MyApplication) getApplication()).setUserIsVerify(false);
            }

            if (!StringUtil.isEmpty(is_bd) && is_bd.equals("1")) {
                ((MyApplication) getApplication()).setUserIsBinding(true);
                SharedPreferencesUtils.getInstance().putBoolean(BindingPhoneActivity.this, "isBinding", true);
            } else if (is_bd.equals("0")) {
                ((MyApplication) getApplication()).setUserIsBinding(false);
                SharedPreferencesUtils.getInstance().putBoolean(BindingPhoneActivity.this, "isBinding", false);
            }

            if (!StringUtil.isEmpty(is_order) && is_order.equals("1")) {
//            ((MyApplication) getApplication()).setUserIsBinding(true);
                SharedPreferencesUtils.getInstance().putBoolean(BindingPhoneActivity.this, "isPayDeposit", true);
            } else if (is_order.equals("0")) {
//            ((MyApplication) getApplication()).setUserIsBinding(false);
                SharedPreferencesUtils.getInstance().putBoolean(BindingPhoneActivity.this, "isPayDeposit", false);
            }
            intentOthersActivity();
        } else {
            Toast.makeText(this, data.getMsg(), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 跳转其他页面的逻辑
     */
    private void intentOthersActivity() {

        mAid = getIntent().getStringExtra("aid");//活动Id
        mtitle = getIntent().getStringExtra("title");//活动标题

        if (!StringUtil.isEmpty(logFlag)) {
            if (logFlag.equals("gotoTravel")) {
                Intent intent = new Intent(BindingPhoneActivity.this, MainActivity.class);
                intent.putExtra("logFlag", "gotoTravel");
                startActivity(intent);
                finish();
//                setResult(3);
//                finish();
            } else if (logFlag.equals("unlogin")) {
                Intent intent = new Intent(BindingPhoneActivity.this, MainActivity.class);
                intent.putExtra("logFlag", "gotoTravel");
                startActivity(intent);
                finish();
            } else if (logFlag.equals("makemoney")) {
                //Intent intent1 = new Intent();
                //intent1.setAction("刷新赚一赚");
                //sendBroadcast(intent1);
                EventBus.getDefault().post(new RefreshEarningAProfitBean("刷新赚一赚"));
                finish();
//                setResult(4);
//                finish();
            } else if (logFlag.equals("myCollection")) {
                startActivity(new Intent(BindingPhoneActivity.this, CollectionActivity.class));
                finish();
            } else if (logFlag.equals("mine")) {

                //Intent intent1 = new Intent();
                //intent1.setAction("刷新");
                //intent1.putExtra("receiver", "1");
                //sendBroadcast(intent1);
                //finish();
                EventBus.getDefault().post(new ReceiverBean("1"));
//                Intent intent = new Intent();
//                intent.putExtra("uid", loginBean.getData().getUid());
//                setResult(8, intent);
                finish();
            }
//            else if (logFlag.equals("personMsg")) {
//                startActivity(new Intent(BindingPhoneActivity.this, PersonMessageActivity.class));
//                finish();
//            }
            else if (logFlag.equals("myOrder")) {
                startActivity(new Intent(BindingPhoneActivity.this, MyOrderNewActivity.class));
                finish();
            } else if (logFlag.equals("myReservation")) {
                startActivity(new Intent(BindingPhoneActivity.this, MyReserveActivity.class));
                finish();
            } else if (logFlag.equals("myTravel")) {
                startActivity(new Intent(BindingPhoneActivity.this, MyTravelActivity.class));
                finish();
            } else if (logFlag.equals("unPaid")) {
                startActivity(new Intent(BindingPhoneActivity.this, UnpaidActivity.class));
                finish();
            } else if (logFlag.equals("coupon")) {
                startActivity(new Intent(BindingPhoneActivity.this, CouponActivity.class));
                finish();
            } else if (logFlag.equals("verify")) {
                startActivity(new Intent(BindingPhoneActivity.this, VerifyActivity.class));
                finish();
            } else if (logFlag.equals("recompose")) {
                startActivity(new Intent(BindingPhoneActivity.this, RecomposeActivity.class));
                finish();
            } else if (logFlag.equals("explain_red")) {
                Intent intent = new Intent(BindingPhoneActivity.this, RecordVideoActivity.class);
                startActivity(intent);
                finish();
            } else if (logFlag.equals("help")) {

                //Intent intent1 = new Intent();
                //intent1.setAction("刷新");
                //intent1.putExtra("receiver", "1");
                //sendBroadcast(intent1);
                EventBus.getDefault().post(new ReceiverBean("1"));
                finish();

//                Intent intent = new Intent();
//                intent.putExtra("uid", loginBean.getData().getUid());
//                setResult(1);
//                finish();
            } else if (logFlag.equals("about")) {
                //Intent intent1 = new Intent();
                //intent1.setAction("更新推送页面信息");
                //intent1.putExtra("action", "pushMsg");
                //sendBroadcast(intent1);
                //finish();
                //action 更新页面信息
                EventBus.getDefault().post(new PushMsgBen(true));


//                setResult(10);
                finish();
            } else if (logFlag.equals("action") && !StringUtil.isEmpty(mAid)) {
                Intent intent = new Intent(BindingPhoneActivity.this, ThemeActionActivity.class);
                intent.putExtra("backFlag", "adverting");
                intent.putExtra("aid", mAid);
                startActivity(intent);
                finish();
            } else if (logFlag.equals("actionHome") && !StringUtil.isEmpty(mAid)) {
                Intent intent = new Intent(BindingPhoneActivity.this, ThemeActionActivity.class);
                intent.putExtra("aid", mAid);
                intent.putExtra("title", mtitle);
                startActivity(intent);
                finish();
            } else if (logFlag.equals("lookWorld")) {
                Intent intent = new Intent(BindingPhoneActivity.this, RecordVideoActivity.class);
                startActivity(intent);
                finish();
            } else if (logFlag.equals("myMoney")) {
                Intent intent = new Intent(BindingPhoneActivity.this, MyMoneyActivity.class);
                startActivity(intent);
                finish();
            } else if (logFlag.equals("activityTrip")) {
                Intent intent = new Intent(BindingPhoneActivity.this, ThemeActionActivity.class);
                intent.putExtra("aid", mAid);
                intent.putExtra("title", mtitle);
                startActivity(intent);
                finish();
            } else if (logFlag.equals("videoDetails")) {//

                //Intent intent1 = new Intent();
                //intent1.setAction("刷新");
                //intent1.putExtra("receiver", "1");
                //sendBroadcast(intent1);
                EventBus.getDefault().post(new ReceiverBean("1"));
                finish();
//                setResult(11);
//                finish();
            } else if (logFlag.equals("personal")) {//
                //Intent intent1 = new Intent();
                //intent1.setAction("刷新");
                //intent1.putExtra("receiver", "1");
                //sendBroadcast(intent1);
                EventBus.getDefault().post(new ReceiverBean("1"));
                finish();
            } else if (logFlag.equals("setting")) {
                //Intent intent1 = new Intent();
                //intent1.setAction("刷新");
                //intent1.putExtra("receiver", "2");
                //sendBroadcast(intent1);
                EventBus.getDefault().post(new ReceiverBean("2"));
                Intent intent = new Intent(BindingPhoneActivity.this, SettingActivity.class);
                startActivity(intent);
                finish();
            } else if (logFlag.equals("myComments")) {
                Intent intent = new Intent(BindingPhoneActivity.this, SystemMessageActivity.class);
                startActivity(intent);
                finish();
            } else if (logFlag.equals("verifyExplainWeb")) {
                //Intent intent1 = new Intent();
                //intent1.setAction("重新加载");
                //sendBroadcast(intent1);
                EventBus.getDefault().post(new RefreshLoadBean("重新加载"));
//                Intent intent = new Intent(BindingPhoneActivity.this, ExplainWebActivity.class);
//                intent.putExtra("title", "认证说明");
//                intent.putExtra("url", URLUtils.BASE_URL + "/Home/Book/agreement?uid="+useUid);
//                startActivity(intent);
                finish();

            }else if (logFlag.equals("shareweb")){
                startActivity(new Intent(this,ShareWebActivity.class));
                finish();
            }
            else if (logFlag.equals("personMessage")) {
                finish();
            }
        } else {
            startActivity(new Intent(BindingPhoneActivity.this, MainActivity.class));
            finish();
        }
    }

    @OnClick({R.id.verify_button, R.id.button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.verify_button:
                obtainCode(this);
                break;
            case R.id.button:
                bindingPhone(this);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            intentOthersActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
