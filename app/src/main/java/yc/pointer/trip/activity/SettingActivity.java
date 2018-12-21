package yc.pointer.trip.activity;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import yc.pointer.trip.R;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.eventbean.PayBean;
import yc.pointer.trip.bean.eventbean.ReceiverBean;
import yc.pointer.trip.untils.AppApplicationMgr;
import yc.pointer.trip.untils.AppCleanMgr;
import yc.pointer.trip.untils.SharedPreferencesUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.DialogKnow;
import yc.pointer.trip.view.DialogSure;
import yc.pointer.trip.view.LoadDialog;
import yc.pointer.trip.view.ToolbarWrapper;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by moyan on 2017/8/31.
 */
public class SettingActivity extends BaseActivity {
    //@BindView(R.id.verify_result)
    //TextView verifyResult;//认证结果
    @BindView(R.id.binding_result)
    TextView bindingResult;//绑定结果
    @BindView(R.id.ispay_deposit)
    TextView depositResult;//押金支付结果
    @BindView(R.id.liner_recompose)
    LinearLayout linerRecompose;//修改密码
    @BindView(R.id.liner_help)
    LinearLayout linerHelp;//帮助反馈
    @BindView(R.id.cache)
    TextView cache;//当前缓存
    @BindView(R.id.liner_clear)
    LinearLayout linerClear;//清理缓存
    @BindView(R.id.liner_about_us)
    LinearLayout linerAboutUs;//关于我们
    @BindView(R.id.login_exit)
    Button loginExit;//退出登录
    @BindView(R.id.current_version)
    TextView currentVersion;//当前版本号
    private String userId;
    private boolean isBinding;
    private boolean isPayDeposit;//判断是否交过押金
    private LoadDialog mLoadDialog;
    //private MyBroadcastReciver myBroadcastReciver;


    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {
        //开启动画
        mLoadDialog = new LoadDialog(this, "正在清理...", R.drawable.load_animation);
        ToolbarWrapper toolbarWrapper = new ToolbarWrapper(this);
        toolbarWrapper.setCustomTitle(R.string.setting);
        //获取当前缓存
        cache.setText(AppCleanMgr.getAppClearSize(this));
        userId = ((MyApplication) getApplication()).getUserId();
        isBinding = ((MyApplication) getApplication()).isUserIsBinding();
        isPayDeposit = SharedPreferencesUtils.getInstance().getBoolean(this, "isPayDeposit");


        if (!StringUtil.isEmpty(userId)) {
            bindingResult.setVisibility(View.VISIBLE);
            depositResult.setVisibility(View.VISIBLE);
            loginExit.setVisibility(View.VISIBLE);
            String is_jie = ((MyApplication) getApplication()).getUserBean().getIs_jie();
            String qiTime = ((MyApplication) getApplication()).getUserBean().getQi_time();

            if (isBinding) {
                bindingResult.setText("已绑定");
                bindingResult.setTextColor(Color.parseColor("#1FBB07"));
            } else {
                bindingResult.setText("未绑定");
                bindingResult.setTextColor(Color.parseColor("#b1b1b1"));
            }
            if (!StringUtil.isEmpty(is_jie)) {
                if (is_jie.equals("1")) {
                    if (isPayDeposit) {
                        depositResult.setText("VIP认证中");
                        depositResult.setTextColor(Color.parseColor("#ffb400"));
                    } else {
                        depositResult.setText("未充值");
                        depositResult.setTextColor(Color.parseColor("#b1b1b1"));
                    }
                } else if (is_jie.equals("2")) {
                    if (!StringUtil.isEmpty(qiTime)) {
                        if (qiTime.equals("已到期")) {
                            depositResult.setText(qiTime);
                        } else {
                            depositResult.setText(qiTime + "到期");
                        }
                    }
                    depositResult.setTextColor(Color.parseColor("#1FBB07"));
                } else if (is_jie.equals("3")) {
                    depositResult.setText("VIP认证失败");
                    depositResult.setTextColor(Color.parseColor("#d7013a"));
                } else {
                    depositResult.setText("未充值");
                    depositResult.setTextColor(Color.parseColor("#b1b1b1"));
                }
            }
//            if (isPayDeposit) {
//                depositResult.setText("2019-5-1到期");
//                depositResult.setTextColor(Color.parseColor("#1FBB07"));
//            } else {
//
//            }

        } else {
            loginExit.setVisibility(View.GONE);
            bindingResult.setVisibility(View.GONE);
            depositResult.setVisibility(View.GONE);
        }
        currentVersion.setText("版本 " + AppApplicationMgr.getVersionName(SettingActivity.this));
        //IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction("刷新支付");
        //myBroadcastReciver = new MyBroadcastReciver();
        //registerReceiver(myBroadcastReciver, intentFilter);

    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

    @OnClick({R.id.liner_recompose, R.id.liner_binding_phone, R.id.liner_deposit, R.id.liner_help, R.id.liner_clear, R.id.liner_about_us, R.id.login_exit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //case R.id.liner_verify://跳转认证界面
            //    if (StringUtil.isEmpty(userId)) {
            //        Intent intentPerson = new Intent(SettingActivity.this, LoginActivity.class);
            //        intentPerson.putExtra("logFlag", "verify");
            //        startActivity(intentPerson);
            //    } else {
            //        Intent intent = new Intent(this, VerifyActivity.class);
            //        startActivity(intent);
            //    }
            //    break;
            case R.id.liner_recompose://跳转修改密码界面
                if (StringUtil.isEmpty(userId)) {
                    Intent intentPerson = new Intent(SettingActivity.this, LoginActivity.class);
                    intentPerson.putExtra("logFlag", "recompose");
                    startActivity(intentPerson);
                } else {
                    Intent intent = new Intent(this, RecomposeActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.liner_binding_phone://跳转绑定页面
                if (StringUtil.isEmpty(userId)) {
                    Intent intentPerson = new Intent(SettingActivity.this, LoginActivity.class);
                    intentPerson.putExtra("logFlag", "setting");
//                    startActivity(intentPerson);
                    startActivityForResult(intentPerson, 0);

                } else {
                    //TODO 需判断是否绑定过,跳转不同页面
                    if (isBinding) {
                        //绑定过
                        Intent intent = new Intent(this, ChangeBindingPhoneActivity.class);
                        startActivity(intent);
                    } else {
                        //没绑定过，跳转绑定页面
                        Intent intent = new Intent(this, BindingPhoneActivity.class);
                        intent.putExtra("uid", userId);
                        intent.putExtra("logFlag", "setting");
                        startActivity(intent);
                    }
                }
                break;
            case R.id.liner_deposit://跳转押金页面

                if (StringUtil.isEmpty(userId)) {

                    Intent intentPerson = new Intent(SettingActivity.this, LoginActivity.class);
                    intentPerson.putExtra("logFlag", "setting");
                    startActivityForResult(intentPerson, 0);

                } else {
                    String is_jie = ((MyApplication) getApplication()).getUserBean().getIs_jie();

//                    String is_order = ((MyApplication) getApplication()).getUserBean().getIs_order();

                    if (!StringUtil.isEmpty(is_jie)) {
                        if (is_jie.equals("0")) {
                            //未提交认证信息，去认证
                            new DialogSure(this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                                @Override
                                public void onClickListener(DialogSure dialogSure, boolean trueEnable) {
                                    if (trueEnable) {
                                        startActivity(new Intent(SettingActivity.this, VerifyActivity.class));
                                        finish();
                                    } else {

                                    }
                                }
                            }).setTitle("温馨提示")
                                    .setMsg("您尚未完成实名认证，请先去完善指针会员认证信息，方可充值会员")
                                    .setPositiveButton("前往")
                                    .setNegativeButton("取消")
                                    .show();

                        } else if (is_jie.equals("1")) {
                            //提交的信息认证中，耐心等待

                            if (isPayDeposit == false) {
                                //没缴纳押金，跳转支付押金页面
                                //未充值
                                Intent intent = new Intent(this, NewUnDepositActivity.class);
                                startActivity(intent);
                            } else {
                                new DialogKnow(this, R.style.user_default_dialog, new DialogKnow.CallBackListener() {
                                    @Override
                                    public void onClickListener() {

                                    }
                                }).setTitle("温馨提示")
                                        .setMsg("您的指针会员正在认证中，请耐心等待，无需再充值")
                                        .setPositiveButton("我知道了")
                                        .show();
                            }

                        } else if (is_jie.equals("2")) {
                            // 已缴纳押金，跳转会员权益
                            //已充值

                            Intent intent = new Intent(this, DepositedActivity.class);
                            startActivity(intent);

                        } else if (is_jie.equals("3")) {
                            new DialogSure(this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                                @Override
                                public void onClickListener(DialogSure dialogSure, boolean trueEnable) {
                                    if (trueEnable) {
                                        startActivity(new Intent(SettingActivity.this, VerifyActivity.class));
                                        finish();
                                    } else {

                                    }
                                }
                            }).setTitle("温馨提示")
                                    .setMsg("非常抱歉，您的指针会员身份信息认证失败，请重新完善身份信息")
                                    .setPositiveButton("前往")
                                    .setNegativeButton("取消")
                                    .show();
                        }
                    }


                }
                break;
            case R.id.liner_help://跳转帮助与反馈界面
                Intent intent = new Intent(this, HelpActivity.class);
                startActivity(intent);

                break;
            case R.id.liner_clear://清理缓存
                mLoadDialog.show();
                try {
                    AppCleanMgr.cleanApplicationData(SettingActivity.this);
                    TimerTask task = new TimerTask() {
                        public void run() {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cache.setText("清理完成");
                                    mLoadDialog.dismiss();
                                }
                            });
                        }

                    };
                    Timer timer = new Timer();
                    timer.schedule(task, 2000);
                } catch (Exception e) {
                    return;
                }

                break;
            case R.id.liner_about_us://跳转关于我们
                Intent intentAboutus = new Intent(SettingActivity.this, AboutUsActivity.class);
                startActivity(intentAboutus);
                break;
            case R.id.login_exit://退出登陆，退出后隐藏该按钮
                ((MyApplication) getApplication()).setUserBean(null);
                ((MyApplication) getApplication()).setIslogin(false);
                ((MyApplication) getApplication()).setUserId("");
                SharedPreferencesUtils.getInstance().remove(this, "phone");
                SharedPreferencesUtils.getInstance().remove(this, "pad");
                SharedPreferencesUtils.getInstance().remove(this, "useId");
                SharedPreferencesUtils.getInstance().remove(this, "loginType");
                SharedPreferencesUtils.getInstance().remove(this, "loginId");
                startActivity(new Intent(this, MainActivity.class));
                loginExit.setVisibility(View.GONE);
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == 13) {
                    initView();
                }
                break;
        }
    }

    ////广播接收内部类
    //private class MyBroadcastReciver extends BroadcastReceiver {
    //    @Override
    //    public void onReceive(Context context, Intent intent) {
    //        String action = intent.getAction();
    //        if (action.equals("刷新支付")) {
    //            initView();
    //        }
    //    }
    //}



    /**
     * bind change collection coupon fans follow login newPerson system ver 等activity发送过来
     * @param receiverBean receiver 是1
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(ReceiverBean receiverBean) {
        if (receiverBean != null && receiverBean.equals("1")) {
            initView();
        } else {
            finish();
        }
    }
    /**
     * unDeposit ver 等activity发送过来
     * @param payBean 刷新支付
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void pay(PayBean payBean) {
        if (payBean != null && payBean.getPay().equals("刷新支付")) {
            initView();
        } else {
            finish();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //this.unregisterReceiver(myBroadcastReciver);
    }
}
