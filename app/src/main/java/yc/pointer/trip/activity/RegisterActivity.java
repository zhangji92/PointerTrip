package yc.pointer.trip.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import yc.pointer.trip.bean.CodeBean;
import yc.pointer.trip.bean.RegisterBean;
import yc.pointer.trip.event.CodeEvent;
import yc.pointer.trip.event.RegisterEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.StatusBarUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.untils.TimerCount;
import yc.pointer.trip.view.ClearEditText;
import yc.pointer.trip.view.ToolbarWrapper;

/**
 * 注册页面
 */
public class RegisterActivity extends BaseActivity {

    @BindView(R.id.register_phone)
    ClearEditText mRegisterPhone;//手机号
    @BindView(R.id.regiter_password)
    ClearEditText mRegisterPassword;//密码
    @BindView(R.id.regiter_psw_again)
    ClearEditText mRegisterPswAgain;//确认密码
    @BindView(R.id.invitation_code)
    ClearEditText invitationCode;//邀请码
    @BindView(R.id.regiter_verify)
    EditText mRegisterVerify;//验证码
    @BindView(R.id.verify_button)
    Button mVerifyButton;//验证码
    @BindView(R.id.button)
    Button mButton;//注册
    @BindView(R.id.agrement_text)
    TextView registerTestAgrement;//服务协议
    @BindView(R.id.standard_toolbar)
    Toolbar standardToolbar;
    @BindView(R.id.standard_toolbar_title)
    TextView toolbarTltle;


    private String mPhone;
    private String mPad;
    private String mPadAgain;
    private String mDevcode;
    private long mTimestamp;
    private TimerCount mTimerCount;
    private String flag;

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_register;
    }


    @Override
    protected void initView() {
        StatusBarUtils.with(this).init();//设置沉浸式
        int statusBarHeight = StatusBarUtils.getStatusBarHeight(this);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) standardToolbar.getLayoutParams();
        layoutParams.topMargin=statusBarHeight;
        standardToolbar.setLayoutParams(layoutParams);
        new ToolbarWrapper(this);
        standardToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(1);
                finish();
            }
        });
        toolbarTltle.setText("快速注册");
        mTimerCount = new TimerCount(60000, 1000);
        mTimerCount.setButton(mVerifyButton);
        mDevcode = ((MyApplication) getApplication()).getDevcode();
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();
        flag = getIntent().getStringExtra("falg");
    }

    @OnClick({R.id.button, R.id.verify_button, R.id.agrement_text})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.button://注册
                register(this);
//                Intent intent = new Intent();
//                intent.putExtra("isRegister", "true");
//                setResult(2,intent);
//                finish();
                break;
            case R.id.verify_button://验证码按钮
                obtainCode(this);
                break;
            case R.id.agrement_text:
                startActivity(new Intent(RegisterActivity.this, AgreementActivity.class));
                break;
            default:
                break;
        }
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
        mPhone = mRegisterPhone.getText().toString();//手机号
        mPad = mRegisterPassword.getText().toString();//密码
        mPadAgain = mRegisterPswAgain.getText().toString();//确认密码
        if (!StringUtil.isEmpty(mPhone)) {
            if (mPad.length() < 6 && !StringUtil.isEmpty(mPad)) {
                Toast.makeText(this, "密码不得小于6位数", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                if (mPad.equals(mPadAgain) && !mPad.equals("")) {
                    return true;
                } else {
                    Toast.makeText(this, "密码为空或者密码不一致", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    /**
     * 注册
     *
     * @param context 上下文
     */
    private void register(Context context) {
        boolean msgFlag = judgePhonePad(context);
        boolean timeFlag = APPUtils.judgeTimeDev(context, mDevcode, mTimestamp);
        String code = mRegisterVerify.getText().toString();
        String invitCode = invitationCode.getText().toString();
        if (msgFlag) {
            if (timeFlag) {
                mPad = Md5Utils.createMD5(mPad + URLUtils.WK_PWD_KEY);
                String str = Md5Utils.createMD5("code=" + code + "devcode=" + mDevcode + "invitation_code=" + invitCode + "phone=" + mPhone + "pwd=" + mPad + "timestamp=" + mTimestamp + URLUtils.WK_APP_KEY);
                RequestBody requestBody = new FormBody.Builder()
                        .add("timestamp", String.valueOf(mTimestamp))
                        .add("phone", mPhone)
                        .add("pwd", mPad)
                        .add("devcode", mDevcode)
                        .add("signature", str)
                        .add("code", code)
                        .add("invitation_code", invitCode)
                        .build();
                OkHttpUtils.getInstance().post(URLUtils.REGISTER, requestBody, new HttpCallBack(new RegisterEvent()));

            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void registerEvent(RegisterEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        RegisterBean bean = event.getData();
        if (bean.getStatus() == 0) {
//            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
//            SharedPreferencesUtils.getInstance().putString(RegisterActivity.this, "loginPhone", mPhone);
            Intent intent = new Intent();
            intent.putExtra("logFlag", flag);
            intent.putExtra("phone", mPhone);
            setResult(2, intent);
            finish();
        } else {
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
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
                OkHttpUtils.getInstance().post(URLUtils.PHONE_CODE, requestBody, new HttpCallBack(new CodeEvent()));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void obtainCodeEvent(CodeEvent codeEvent) {
        if (codeEvent.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        CodeBean bean = codeEvent.getData();
        if (bean.getStatus() == 0) {
            Log.e("LogInterceptor", bean.getMsg());
        } else {
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
        }
    }


}
