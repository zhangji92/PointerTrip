package yc.pointer.trip.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
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
import yc.pointer.trip.base.BaseBean;
import yc.pointer.trip.bean.SaveMesgBean;
import yc.pointer.trip.event.BindingPhoneEvent;
import yc.pointer.trip.event.ChangeBindingEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.SharedPreferencesUtils;
import yc.pointer.trip.untils.StatusBarUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.ClearEditText;
import yc.pointer.trip.view.ToolbarWrapper;

import static yc.pointer.trip.R.drawable.black_background;

/**
 * Created by moyan on 2018/1/11.
 * 绑定手机号第一步
 */

public class ChangeFirstActivity extends BaseActivity {
    @BindView(R.id.standard_toolbar_title)
    TextView standardToolbarTitle;
    @BindView(R.id.standard_toolbar)
    Toolbar standardToolbar;
    @BindView(R.id.register_phone)
    TextView registerPhone;
    @BindView(R.id.regiter_password)
    ClearEditText regiterPassword;
    @BindView(R.id.button)
    Button button;

    private String mDevcode;
    private String mUserId;
    private long mTimestamp;
    private String bindingNumber;
    private String psw;
    private String mPad;

    @Override
    protected int getContentViewLayout() {

        return R.layout.change_first_layout;

    }

    @Override
    protected void initView() {

        StatusBarUtils.with(this).init();
        int statusBarHeight = StatusBarUtils.getStatusBarHeight(this);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) standardToolbar.getLayoutParams();
        layoutParams.topMargin = statusBarHeight;
        standardToolbar.setLayoutParams(layoutParams);

        ToolbarWrapper toolbarWrapper = new ToolbarWrapper(this);
        toolbarWrapper.setCustomTitle(R.string.change_number);
        bindingNumber = getIntent().getStringExtra("bindingNumber");

        mDevcode = ((MyApplication) getApplication()).getDevcode();
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();
        mUserId = ((MyApplication) getApplication()).getUserId();

        if (!StringUtil.isEmpty(bindingNumber)){
            registerPhone.setText(bindingNumber);
        }
        button.setClickable(false);
        button.setBackground(getResources().getDrawable(R.drawable.unenable_background));
        regiterPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                psw = regiterPassword.getText().toString().trim();
                if (psw.length()>=6){
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
     * 绑定手机号
     *
     * @param context 上下文
     */
    private void changeBindingPhone(Context context) {

        boolean timeFlag = APPUtils.judgeTimeDev(context, mDevcode, mTimestamp);
            if (timeFlag) {
                if (StringUtil.isEmpty(mUserId)){
                    Toast.makeText(this, "无效的用户Id", Toast.LENGTH_SHORT).show();
                    return;
                }
                mPad = Md5Utils.createMD5(psw + URLUtils.WK_PWD_KEY);
                String str = Md5Utils.createMD5( "devcode=" + mDevcode + "old_phone=" + bindingNumber + "pwd=" + mPad + "timestamp=" + mTimestamp+"uid="+mUserId + URLUtils.WK_APP_KEY);
                RequestBody requestBody = new FormBody.Builder()
                        .add("timestamp", String.valueOf(mTimestamp))
                        .add("old_phone", bindingNumber)
                        .add("uid", mUserId)
                        .add("pwd", mPad)
                        .add("devcode", mDevcode)
                        .add("signature", str)
                        .build();
                OkHttpUtils.getInstance().post(URLUtils.CHANGE_BINDING_PHONE, requestBody, new HttpCallBack(new ChangeBindingEvent()));

            }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeBinding(ChangeBindingEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        BaseBean data = event.getData();
        if (data.getStatus() == 0) {
            Intent intent = new Intent(ChangeFirstActivity.this, ChangeSecondActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, data.getMsg(), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.button)
    public void onViewClicked() {
        //验证密码正确与否
        changeBindingPhone(ChangeFirstActivity.this);

    }
}
