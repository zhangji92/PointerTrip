package yc.pointer.trip.activity;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import yc.pointer.trip.bean.eventbean.ReceiverBean;
import yc.pointer.trip.bean.SaveMesgBean;
import yc.pointer.trip.event.ChangeBindingCodeEvent;
import yc.pointer.trip.event.NewBindingPhoneEvent;
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
 * Created by moyan on 2018/1/11.
 */

public class ChangeSecondActivity extends BaseActivity {
    @BindView(R.id.standard_toolbar_title)
    TextView standardToolbarTitle;
    @BindView(R.id.standard_toolbar)
    Toolbar standardToolbar;
    @BindView(R.id.register_phone)
    ClearEditText registerPhone;
    @BindView(R.id.regiter_verify)
    EditText regiterVerify;
    @BindView(R.id.verify_button)
    Button verifyButton;
    @BindView(R.id.button)
    Button button;


    private String newPhone;
    private String mDevcode;
    private long mTimestamp;
    private TimerCount mTimerCount;
    private String useUid;
    private String code;

    @Override
    protected int getContentViewLayout() {
        return R.layout.change_seconed_layout;
    }

    @Override
    protected void initView() {

        StatusBarUtils.with(this).init();
        int statusBarHeight = StatusBarUtils.getStatusBarHeight(this);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) standardToolbar.getLayoutParams();
        layoutParams.topMargin = statusBarHeight;
        standardToolbar.setLayoutParams(layoutParams);

        ToolbarWrapper toolbarWrapper = new ToolbarWrapper(this);
        toolbarWrapper.setCustomTitle(R.string.rename_phone);

        mDevcode = ((MyApplication) getApplication()).getDevcode();
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();
        useUid = ((MyApplication) getApplication()).getUserId();
        mTimerCount = new TimerCount(60000, 1000);
        mTimerCount.setButton(verifyButton);


        button.setClickable(false);
        button.setBackground(getResources().getDrawable(R.drawable.unenable_background));

        registerPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                newPhone = registerPhone.getText().toString().trim();
                if (newPhone.length()==11){
                    button.setClickable(true);
                    button.setBackground(getResources().getDrawable(R.drawable.adapter_order_txt));
                }else {

                    button.setClickable(false);
                    button.setBackground(getResources().getDrawable(R.drawable.unenable_background));

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected boolean needEventBus() {
        return true;

    }




    /**
     * 获取验证码
     *
     * @param context 上下文
     */
    private void obtainCode(Context context) {

        boolean timeFlag = APPUtils.judgeTimeDev(context, mDevcode, mTimestamp);
        if (StringUtil.isMobileNo(this,newPhone)) {
            if (timeFlag) {
                mTimerCount.start();
                String str = Md5Utils.createMD5("devcode=" + mDevcode + "phone=" + newPhone + "timestamp=" + mTimestamp + "type=1" + URLUtils.WK_APP_KEY);
                RequestBody requestBody = new FormBody.Builder()
                        .add("timestamp", String.valueOf(mTimestamp))
                        .add("phone", newPhone)
                        .add("devcode", mDevcode)
                        .add("signature", str)
                        .add("type", "1")
                        .build();
                OkHttpUtils.getInstance().post(URLUtils.PHONE_CODE, requestBody, new HttpCallBack(new ChangeBindingCodeEvent()));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void obtainCodeEvent(ChangeBindingCodeEvent bindingCodeEvent) {
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

        boolean timeFlag = APPUtils.judgeTimeDev(context, mDevcode, mTimestamp);

        if (StringUtil.isMobileNo(this,newPhone)) {
            if (timeFlag) {
                if (StringUtil.isEmpty(useUid)){
                    Toast.makeText(this, "无效的用户Id", Toast.LENGTH_SHORT).show();
                    return;
                }
                String str = Md5Utils.createMD5("code=" + code + "devcode=" + mDevcode + "new_phone=" + newPhone + "timestamp=" + mTimestamp+"uid="+useUid + URLUtils.WK_APP_KEY);
                RequestBody requestBody = new FormBody.Builder()
                        .add("timestamp", String.valueOf(mTimestamp))
                        .add("new_phone", newPhone)
                        .add("uid", useUid)
                        .add("devcode", mDevcode)
                        .add("signature", str)
                        .add("code", code)
                        .build();
                OkHttpUtils.getInstance().post(URLUtils.CHANGE_BINDING_PHONE_CODE, requestBody, new HttpCallBack(new NewBindingPhoneEvent()));

            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void registerEvent(NewBindingPhoneEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        SaveMesgBean data = event.getData();
        if (data.getStatus() == 0) {

            SharedPreferencesUtils.getInstance().putString(ChangeSecondActivity.this, "loginPhone", newPhone);
            //Intent intent1 = new Intent();
            //intent1.setAction("刷新");
            //intent1.putExtra("receiver", "1");
            //sendBroadcast(intent1);
            EventBus.getDefault().post(new ReceiverBean("1"));
            finish();

        } else {
            Toast.makeText(this, data.getMsg(), Toast.LENGTH_SHORT).show();
        }
    }


    @OnClick({R.id.verify_button, R.id.button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.verify_button:
                //获取验证码
                obtainCode(this);
                break;
            case R.id.button:
                //更换手机
                code = regiterVerify.getText().toString();
                if (!StringUtil.isEmpty(code)){
                    bindingPhone(this);
                }else {
                    Toast.makeText(this, "验证码为空", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }
}
