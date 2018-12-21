package yc.pointer.trip.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import yc.pointer.trip.R;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.CodeBean;
import yc.pointer.trip.bean.RegisterBean;
import yc.pointer.trip.event.CodeEvent;
import yc.pointer.trip.event.RegisterEvent;
import yc.pointer.trip.event.RetrieveRegisterEvent;
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
 * Created by 张继
 * 2017/7/17
 * 10:56
 * 找回密码
 */
public class RetrievePasswordActivity extends BaseActivity {
    @BindView(R.id.standard_toolbar)
    Toolbar standardToolbar;
    @BindView(R.id.register_phone)
    EditText mRegisterPhone;//手机号
    @BindView(R.id.regiter_password)
    EditText mRegisterPassword;//密码
    @BindView(R.id.regiter_psw_again)
    EditText mRegisterPswAgain;//确认密码
    @BindView(R.id.regiter_verify)
    EditText mRegisterVerify;//验证码
    @BindView(R.id.invitation_code)
    ClearEditText invitationCode;//邀请码

    @BindView(R.id.line_invitation_code)
    ImageView lineInvitationCode;//邀请码底部线
    @BindView(R.id.verify_button)
    Button mVerifyButton;//获取验证码
    @BindView(R.id.button)
    Button mButton;//注册
    @BindView(R.id.register_test_agrement)
    TextView registerTestAgrement;//点击注册即同意
    @BindView(R.id.agrement_text)
    TextView registerAgrement;//《指针注册服务协议》

    private String mPhone;
    private String mPad;
    private String mPadAgain;
    private String mDevcode;
    private long mTimestamp;
    private TimerCount mTimerCount;

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
        registerTestAgrement.setVisibility(View.GONE);
        registerAgrement.setVisibility(View.GONE);
        invitationCode.setVisibility(View.GONE);
        lineInvitationCode.setVisibility(View.GONE);
        new ToolbarWrapper(this).setCustomTitle(R.string.retrieve_password);

        mButton.setText("重置密码");
        mTimerCount = new TimerCount(60000, 1000);
        mTimerCount.setButton(mVerifyButton);
        mDevcode = ((MyApplication) getApplication()).getDevcode();
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

    @OnClick({R.id.button, R.id.verify_button,})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.button://重置密码
                register(this);
                break;
            case R.id.verify_button://验证码
                obtainCode(this);
                break;
            default:
                break;
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
        if (msgFlag) {
            if (timeFlag) {
                mPad = Md5Utils.createMD5(mPad + URLUtils.WK_PWD_KEY);
                String str = Md5Utils.createMD5("code=" + code + "devcode=" + mDevcode + "phone=" + mPhone + "pwd=" + mPad + "timestamp=" + mTimestamp + URLUtils.WK_APP_KEY);
                RequestBody requestBody = new FormBody.Builder()
                        .add("timestamp", String.valueOf(mTimestamp))
                        .add("phone", mPhone)
                        .add("pwd", mPad)
                        .add("devcode", mDevcode)
                        .add("signature", str)
                        .add("code", code)
                        .build();
                OkHttpUtils.getInstance().post(URLUtils.RETRIEVE, requestBody, new HttpCallBack(new RetrieveRegisterEvent()));

            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void registerEvent(RetrieveRegisterEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        RegisterBean bean = event.getData();
        if (bean.getStatus() == 0) {
            startActivity(new Intent(RetrievePasswordActivity.this,LoginActivity.class));
        } else {
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 判断手机号密码
     *
     * @param context 上下文菜单
     * @return true正确 false 错误
     */
    private boolean judgePhonePad(Context context) {
        mPhone = mRegisterPhone.getText().toString();//手机号
        mPad = mRegisterPassword.getText().toString();//密码
        mPadAgain = mRegisterPswAgain.getText().toString();//确认密码
        if (StringUtil.isMobileNo(context, mPhone)&&!StringUtil.isEmpty(mPhone)) {
            if (mPad.length()<6&&!StringUtil.isEmpty(mPad)){
                Toast.makeText(this, "密码不得小于6位数", Toast.LENGTH_SHORT).show();
                return false;
            }else {
                if (mPad.equals(mPadAgain) && !mPad.equals("")) {
                    return true;
                } else {
                    Toast.makeText(this, "密码为空或密码不一致", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    /**
     * 请求验证码
     *
     * @param context 上下文菜单
     */
    private void obtainCode(Context context) {
        boolean flag = judgePhonePad(context);
        boolean timeFlag = APPUtils.judgeTimeDev(context, mDevcode, mTimestamp);
        if (flag) {
            if (timeFlag) {
                mTimerCount.start();
                String str = Md5Utils.createMD5("devcode=" + mDevcode + "phone=" + mPhone + "timestamp=" + mTimestamp + "type=2" + URLUtils.WK_APP_KEY);
                RequestBody requestBody = new FormBody.Builder()
                        .add("timestamp", String.valueOf(mTimestamp))
                        .add("phone", mPhone)
                        .add("devcode", mDevcode)
                        .add("signature", str)
                        .add("type", "2")
                        .build();
                OkHttpUtils.getInstance().post(URLUtils.PHONE_CODE, requestBody, new HttpCallBack(new CodeEvent()));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    private void obtainCodeEvent(CodeEvent codeEvent) {
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
