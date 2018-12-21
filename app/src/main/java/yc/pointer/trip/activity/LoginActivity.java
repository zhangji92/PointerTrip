package yc.pointer.trip.activity;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.bumptech.glide.Glide;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import yc.pointer.trip.R;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.eventbean.FollowDotEvent;
import yc.pointer.trip.bean.eventbean.ReceiverBean;
import yc.pointer.trip.bean.SaveMesgBean;
import yc.pointer.trip.bean.UserHeadImgBean;
import yc.pointer.trip.bean.eventbean.RefreshEarningAProfitBean;
import yc.pointer.trip.bean.eventbean.RefreshLoadBean;
import yc.pointer.trip.bean.eventbean.UpdateRedDotBean;
import yc.pointer.trip.event.ItemTwoRefresh;
import yc.pointer.trip.event.LoginEvent;
import yc.pointer.trip.event.LoginEventRefresh;
import yc.pointer.trip.event.ThirdLoginEvent;
import yc.pointer.trip.event.UserHeadImgEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.GlideCircleTransform;
import yc.pointer.trip.untils.LocationUtil;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.PermissionHelper;
import yc.pointer.trip.untils.PermissionHelper.PermissionModel;
import yc.pointer.trip.untils.SharedPreferencesUtils;
import yc.pointer.trip.untils.StatusBarUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.untils.SystemUtil;
import yc.pointer.trip.view.ClearEditText;
import yc.pointer.trip.view.CustomCircleImage;
import yc.pointer.trip.view.LoadDialog;
import yc.pointer.trip.view.ToolbarWrapper;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.login_head)
    CustomCircleImage loginHead;
    @BindView(R.id.verify_result)
    ImageView verifyResult;//VIP认证标识
    @BindView(R.id.loggin_phone)
    ClearEditText mLoginPhone;
    @BindView(R.id.loggin_password)
    ClearEditText mLoginPad;
    @BindView(R.id.loginsetting_forget)
    TextView forgetPsw;
    @BindView(R.id.quick_loginsetting_enroll)
    TextView siginUp;
    @BindView(R.id.login_commint)
    Button loginCommint;
    @BindView(R.id.standard_toolbar)
    Toolbar standardToolbar;


    private String mPhone;
    private String mPad;
    private String mDevcode;
    private long mTimestamp;
    private String logFlag;
    private String registerFlag;
    private boolean timeFlag;
    private String mBid;

    private String mAid;//活动id
    private String mtitle;//活动标题

    private String login_id;//第三方登录Id
    private String nickname;//第三方登录昵称
    private String pic;//第三方登录头像
    private String login_type;//第三方登录的类型
    private String registrationID;//极光推送的注册Id
    private UMShareAPI mShareAPI;

    private LoadDialog mLoadDialog;
    private String mLocalCity = "";
    private PermissionHelper mHelper;
    private PermissionModel[] permissionModels = {new PermissionModel(1, Manifest.permission.ACCESS_FINE_LOCATION, "定位"),
    };
    private boolean isPermission = false;//权限申请标志，防止一直TOAST

    private String mUUid;
    private String mPhonetype;//手机型号
    private LocationUtil locationUtil;

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_login;
    }


    @Override
    protected void initView() {

        StatusBarUtils.with(this).init();
        int statusBarHeight = StatusBarUtils.getStatusBarHeight(this);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) standardToolbar.getLayoutParams();
        layoutParams.topMargin = statusBarHeight;
        standardToolbar.setLayoutParams(layoutParams);

        String deviceBrand = SystemUtil.getDeviceBrand();//获取手机厂商
        String systemModel = SystemUtil.getSystemModel();//获取手机型号
        if (!StringUtil.isEmpty(deviceBrand) && !StringUtil.isEmpty(systemModel)) {
            mPhonetype = deviceBrand + " " + systemModel;
        }

        if (Build.VERSION.SDK_INT < 23) {
            getLocation();
        } else {
            //申请权限
            //Applypermission();
            isPermission = true;
            mHelper = new PermissionHelper(this, new PermissionHelper.OnAlterApplyPermission() {
                @Override
                public void OnAlterApplyPermission() {
                    isPermission = false;
                    getLocation();
                }

                @Override
                public void cancelListener() {

                }
            }, permissionModels);
            mHelper.applyPermission();//申请权限
        }
        mShareAPI = UMShareAPI.get(LoginActivity.this);
        new ToolbarWrapper(this).setCustomTitle(R.string.loggin_title);
        mDevcode = ((MyApplication) getApplication()).getDevcode();
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();
        timeFlag = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);

        registrationID = JPushInterface.getRegistrationID(this);

        mBid = getIntent().getStringExtra("bid");
        mAid = getIntent().getStringExtra("aid");//活动Id
        mtitle = getIntent().getStringExtra("title");//活动标题
        mUUid = getIntent().getStringExtra("uuid");
        logFlag = getIntent().getStringExtra("logFlag");//跳转至登录页面的标志（判断由哪个页面跳转至登录界面）
        standardToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!StringUtil.isEmpty(logFlag)) {
                    if (logFlag.equals("action")) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else if (logFlag.equals("makemoney")) {
                        setResult(4);
                        finish();
                    } else if (logFlag.equals("unlogin")) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("logFlag", "unGotoTravel");
                        startActivity(intent);
                        finish();
                    } else if (logFlag.equals("explain_red")) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        //startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                } else {
                    finish();
                }
            }
        });
        String phone = SharedPreferencesUtils.getInstance().getString(this, "loginPhone");
        String headPic = SharedPreferencesUtils.getInstance().getString(this, "headImg");
        String is_vip = SharedPreferencesUtils.getInstance().getString(this, "isVip");

        if (!StringUtil.isEmpty(phone) && !phone.equals("not find")) {
            mLoginPhone.setText(phone);
        }
        if (!StringUtil.isEmpty(headPic) && !headPic.equals("not find")) {
//            OkHttpUtils.displayImg(loginHead, headPic);
            OkHttpUtils.displayGlideCircular(this,loginHead,headPic,verifyResult,is_vip);
        } else {
            loginHead.setImageResource(R.mipmap.head);
        }

//        if (!StringUtil.isEmpty(isJie) && !isJie.equals("not find")) {
//            if (isJie.equals("2")) {
//                verifyResult.setVisibility(View.VISIBLE);
//            } else {
//                verifyResult.setVisibility(View.GONE);
//            }
//        } else {
//            verifyResult.setVisibility(View.GONE);
//        }


        mLoginPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phoneNumber = mLoginPhone.getText().toString().trim();
                if (s.toString().length() == 11) {
                    if (StringUtil.isMobileNo(LoginActivity.this, phoneNumber)) {
                        //请求用户头像
                        getUserHeadImg(phoneNumber);
                    }
                } else {
                    loginHead.setImageResource(R.mipmap.head);
                    verifyResult.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });
        mLoginPhone.setSelection(mLoginPhone.getText().length());
        mLoginPad.setSelection(mLoginPad.getText().length());
    }

    /**
     * 获取用户头像
     *
     * @param phoneNumber
     */
    private void getUserHeadImg(String phoneNumber) {
        if (timeFlag) {
            String sign = Md5Utils.createMD5("devcode=" + mDevcode + "phone=" + phoneNumber + "timestamp=" + mTimestamp + URLUtils.WK_APP_KEY);
            RequestBody params = new FormBody.Builder()
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("phone", phoneNumber)
                    .add("signature", sign)
                    .add("devcode", mDevcode)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.GET_USER_HEADIMG, params, new HttpCallBack(new UserHeadImgEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getHeadPic(UserHeadImgEvent event) {
        if (event.isTimeOut()) {
            loginHead.setImageResource(R.mipmap.head);
            return;
        }
        UserHeadImgBean data = event.getData();
        if (data.getStatus() == 0) {
            String headUrl = data.getPic();
            String is_jie = data.getIs_jie();
            if (!StringUtil.isEmpty(headUrl)) {
                OkHttpUtils.displayImg(loginHead, headUrl);
            }
            if (!StringUtil.isEmpty(is_jie) && is_jie.equals("2")) {
                verifyResult.setVisibility(View.VISIBLE);
            } else {
                verifyResult.setVisibility(View.GONE);
            }
        } else {
            loginHead.setImageResource(R.mipmap.head);
            APPUtils.intentLogin(LoginActivity.this, data.getStatus());
        }
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

    @OnClick({R.id.loginsetting_forget, R.id.quick_loginsetting_enroll, R.id.login_commint,
            R.id.qq_login, R.id.wx_login, R.id.wb_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginsetting_forget://忘记密码
                startActivity(new Intent(LoginActivity.this, RetrievePasswordActivity.class));
                break;
            case R.id.quick_loginsetting_enroll://快速注册
                Intent intent = new Intent(this, RegisterActivity.class);
                intent.putExtra("falg", logFlag);
                startActivityForResult(intent, 0);
                break;
            case R.id.login_commint://登录
                mPhone = mLoginPhone.getText().toString();
                mPad = mLoginPad.getText().toString();
                if (!StringUtil.isEmpty( mPhone)) {
                    if (!StringUtil.isEmpty(mPad)) {
                        String padMd5 = Md5Utils.createMD5(mLoginPad.getText().toString() + URLUtils.WK_PWD_KEY);
                        Login(mPhone, padMd5);
                        loginCommint.setClickable(false);
                        loginCommint.setEnabled(false);

                    } else {
                        Toast.makeText(this, "密码为空", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.qq_login://qq登录

//                login_id = "073D14D7FE94EB2437AFD503638EC906";
//                nickname = "waitting乀";
//                pic = " http://q.qlogo.cn/qqapp/1105894263/073D14D7FE94EB2437AFD503638EC906/100";
//                login_type = "qq";
//                thirdLogin(pic);

                boolean isHaveQQ = mShareAPI.isInstall(this, SHARE_MEDIA.QQ);
                if (isHaveQQ) {
                    theThirdLogin(SHARE_MEDIA.QQ);
                } else {
                    Toast.makeText(this, "亲，您尚未安装QQ客户端，无法登录", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.wx_login://微信登录
                boolean isHaveWeiXin = mShareAPI.isInstall(this, SHARE_MEDIA.WEIXIN);
                if (isHaveWeiXin) {
                    theThirdLogin(SHARE_MEDIA.WEIXIN);
                } else {
                    Toast.makeText(this, "亲，您尚未安装微信客户端，无法登录", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.wb_login://微博登录
                theThirdLogin(SHARE_MEDIA.SINA);
                break;
        }
    }

    /**
     * 第三方登录
     *
     * @param type 第三方登录平台
     */
    private void theThirdLogin(SHARE_MEDIA type) {

//        mShareAPI.doOauthVerify(LoginActivity.this, type, umAuthListener);
        mShareAPI.getPlatformInfo(LoginActivity.this, type, umAuthListener);

    }

    /**
     * 友盟第三方登录回调
     */
    private UMAuthListener umAuthListener = new UMAuthListener() {

        @Override
        /**
         * 授权开始的回调
         */
        public void onStart(SHARE_MEDIA share_media) {


        }

        @Override
        /**
         * 授权成功的回调
         */
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {

            login_id = data.get("uid");
            nickname = data.get("name");
            pic = data.get("iconurl");
//            login_id = "073D14D7FE94EB2437AFD503638EC906";
//            nickname = "waitting乀";
//            pic = " http://q.qlogo.cn/qqapp/1105894263/073D14D7FE94EB2437AFD503638EC906/100";
            String s = pic.substring(pic.indexOf(":"), pic.length());
            String intentPic = "https" + s;
            if (platform == SHARE_MEDIA.QQ) {
                login_type = "qq";
            } else if (platform == SHARE_MEDIA.WEIXIN) {
                login_type = "wechat";
            } else if (platform == SHARE_MEDIA.SINA) {
                login_type = "sina";
            }

            //第三种：推荐，尤其是容量大时
            System.out.println("通过Map.entrySet遍历key和value");
            for (Map.Entry<String, String> entry : data.entrySet()) {
                Log.e("map", "key= " + entry.getKey() + " and value= " + entry.getValue());
            }
            Log.e("authLogin", login_id.toString() + nickname + pic + login_type);
            Glide.with(LoginActivity.this)
                    .load(pic)
                    .placeholder(R.mipmap.gray_picture)
                    .error(R.mipmap.gray_picture)
                    .transform(new GlideCircleTransform(LoginActivity.this))
                    .into(loginHead);
            mLoginPhone.setText(nickname);
            thirdLogin(intentPic);
            mLoadDialog = new LoadDialog(LoginActivity.this, "正在登录...", R.drawable.load_animation);
            mLoadDialog.show();
            //删除授权-->换第三方账号时重新拉起授权换新登陆账号信息
            mShareAPI.deleteOauth(LoginActivity.this, platform, null);

        }

        @Override
        /**
         * 授权失败的回调
         */
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Log.e("authLogin", t.toString());
            //t.getMessage();
            Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_SHORT).show();
        }

        @Override
        /**
         * 授权取消的回调
         */
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText(getApplicationContext(), "您放弃本次登录", Toast.LENGTH_SHORT).show();
        }
    };

//    /**
//     * 获取平台用户信息
//     */
//    private void getUserInfo(SHARE_MEDIA type){
//        UMShareAPI  mShareAPI = UMShareAPI.get( LoginActivity.this );
//        mShareAPI.getPlatformInfo(LoginActivity.this,type, umAuthListener);
//    }

    /**
     * 登陆
     *
     * @param phone 用户名
     * @param pad   密码
     */
    private void Login(String phone, String pad) {

        if (timeFlag) {
            String sign = Md5Utils.createMD5("devcode=" + mDevcode + "location=" + mLocalCity + "phone=" + phone +
                    "phone_type=" + mPhonetype + "pwd=" + pad + "registration_id=" + registrationID + "timestamp=" +
                    mTimestamp + URLUtils.WK_APP_KEY);
            RequestBody params = new FormBody.Builder()
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("location", mLocalCity)
                    .add("phone", phone)
                    .add("pwd", pad)
                    .add("phone_type", mPhonetype)
                    .add("registration_id", registrationID)
                    .add("signature", sign)
                    .add("devcode", mDevcode)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.LOGIN, params, new HttpCallBack(new LoginEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginMsg(LoginEvent event) {
        loginCommint.setClickable(true);
        loginCommint.setEnabled(true);
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        final SaveMesgBean loginBean = event.getData();
        if (loginBean.getStatus() == 0) {

            savePersonMsg(loginBean);
            intentOthersActivity(loginBean);
            EventBus.getDefault().post(new ItemTwoRefresh(true));
        } else {
            Toast.makeText(this, loginBean.getMsg(), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 保存用户信息
     *
     * @param loginBean
     */
    private void savePersonMsg(SaveMesgBean loginBean) {
        EventBus.getDefault().post(new LoginEventRefresh(true));
        String loginPhone = loginBean.getData().getPhone();
        String uid = loginBean.getData().getUid();
        String is_order = loginBean.getData().getIs_order();
        String is_jie = loginBean.getData().getIs_jie();
        String login_type = loginBean.getData().getLogin_type();
        String login_id = loginBean.getData().getLogin_id();
        String is_bd = loginBean.getData().getIs_bd();
        String bank_num = loginBean.getData().getBank_num();
        String alipay = loginBean.getData().getAlipay();
        String is_vip = loginBean.getData().getIs_vip();

        SharedPreferencesUtils.getInstance().putString(LoginActivity.this, "phone", loginPhone);
        SharedPreferencesUtils.getInstance().putString(LoginActivity.this, "loginPhone", loginPhone);
        SharedPreferencesUtils.getInstance().putString(LoginActivity.this, "pad", mPad);
        SharedPreferencesUtils.getInstance().putString(LoginActivity.this, "useId", uid);
        SharedPreferencesUtils.getInstance().putString(LoginActivity.this, "loginType", login_type);
        SharedPreferencesUtils.getInstance().putString(LoginActivity.this, "loginId", login_id);
        SharedPreferencesUtils.getInstance().putString(LoginActivity.this, "isOrder", is_order);
        SharedPreferencesUtils.getInstance().putString(LoginActivity.this, "isJie", is_jie);
        SharedPreferencesUtils.getInstance().putString(LoginActivity.this, "isVip", is_vip);

        String pic = loginBean.getData().getPic();
        if (!StringUtil.isEmpty(pic)) {
            SharedPreferencesUtils.getInstance().putString(LoginActivity.this, "headImg", pic);
        }
        if (logFlag == null) {
            logFlag = registerFlag;
        }

        if (!StringUtil.isEmpty(is_bd) && is_bd.equals("1")) {
            ((MyApplication) getApplication()).setUserIsBinding(true);
            SharedPreferencesUtils.getInstance().putBoolean(LoginActivity.this, "isBinding", true);
        } else if (is_bd.equals("0")) {
            ((MyApplication) getApplication()).setUserIsBinding(false);
            SharedPreferencesUtils.getInstance().putBoolean(LoginActivity.this, "isBinding", false);
        }

        if (!StringUtil.isEmpty(is_order) && is_order.equals("1")) {
//            ((MyApplication) getApplication()).setUserIsBinding(true);
            SharedPreferencesUtils.getInstance().putBoolean(LoginActivity.this, "isPayDeposit", true);
        } else if (is_order.equals("0")) {
//            ((MyApplication) getApplication()).setUserIsBinding(false);
            SharedPreferencesUtils.getInstance().putBoolean(LoginActivity.this, "isPayDeposit", false);
        }

        if (!StringUtil.isEmpty(bank_num)) {
            ((MyApplication) getApplication()).setUserIsVerify(true);
        } else {
            ((MyApplication) getApplication()).setUserIsVerify(false);
        }

        if (!StringUtil.isEmpty(alipay)) {
            ((MyApplication) getApplication()).setAlipayNumber(alipay);
        } else {
            ((MyApplication) getApplication()).setAlipayNumber("");
        }

        ((MyApplication) getApplication()).setIslogin(true);
        ((MyApplication) getApplication()).setUserBean(loginBean.getData());
        ((MyApplication) getApplication()).setUserId(uid);
        //Intent intent1 = new Intent();
        //intent1.setAction("刷新");
        //intent1.putExtra("receiver", "1");
        //sendBroadcast(intent1);
        EventBus.getDefault().post(new ReceiverBean("1"));


    }

    /**
     * 跳转其他页面的逻辑
     *
     * @param loginBean
     */
    private void intentOthersActivity(SaveMesgBean loginBean) {
        //更新MainActivity 我的 RadioButton 上面的小红点
        int msg_num = loginBean.getData().getMsg_num();
        String att_msg_num = loginBean.getData().getAtt_msg_num();
        if (msg_num <= 0) {
            EventBus.getDefault().post(new UpdateRedDotBean(false));
        } else {
            EventBus.getDefault().post(new UpdateRedDotBean(true));
        }
        if (!StringUtil.isEmpty(att_msg_num)) {
            EventBus.getDefault().post(new FollowDotEvent(att_msg_num));
        } else {
            EventBus.getDefault().post(new FollowDotEvent("0"));
        }

        if (!StringUtil.isEmpty(logFlag)) {
            if (logFlag.equals("gotoTravel")) {
                setResult(3);
                finish();
            } else if (logFlag.equals("unlogin")) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("logFlag", "gotoTravel");
                startActivity(intent);
                finish();
            } else if (logFlag.equals("makemoney")) {
                setResult(4);
                finish();
            } else if (logFlag.equals("myCollection")) {
                startActivity(new Intent(LoginActivity.this, CollectionActivity.class));
                finish();
            } else if (logFlag.equals("mine")) {
                Intent intent = new Intent();
                intent.putExtra("uid", loginBean.getData().getUid());
                setResult(8, intent);
                finish();
            } else if (logFlag.equals("personMsg")) {
                startActivity(new Intent(LoginActivity.this, PersonMessageActivity.class));
                finish();
            } else if (logFlag.equals("myOrder")) {
                startActivity(new Intent(LoginActivity.this, MyOrderNewActivity.class));
                finish();
            } else if (logFlag.equals("myReservation")) {
//                startActivity(new Intent(LoginActivity.this, MyReserveActivity.class));
//                finish();
            } else if (logFlag.equals("myTravel")) {
                startActivity(new Intent(LoginActivity.this, MyTravelActivity.class));
                finish();
            } else if (logFlag.equals("unPaid")) {
                startActivity(new Intent(LoginActivity.this, UnpaidActivity.class));
                finish();
            } else if (logFlag.equals("coupon")) {
                startActivity(new Intent(LoginActivity.this, CouponActivity.class));
                finish();
            } else if (logFlag.equals("verify")) {
                startActivity(new Intent(LoginActivity.this, VerifyActivity.class));
                finish();
            } else if (logFlag.equals("recompose")) {
                startActivity(new Intent(LoginActivity.this, RecomposeActivity.class));
                finish();
            } else if (logFlag.equals("help")) {
                Intent intent = new Intent();
                intent.putExtra("uid", loginBean.getData().getUid());
                setResult(1);
                finish();
            } else if (logFlag.equals("about")) {
                setResult(10);
                finish();
            } else if (logFlag.equals("action") && !StringUtil.isEmpty(mAid)) {
                Intent intent = new Intent(LoginActivity.this, ThemeActionActivity.class);
                intent.putExtra("backFlag", "adverting");
                intent.putExtra("aid", mAid);
                startActivity(intent);
                finish();
            } else if (logFlag.equals("actionHome") && !StringUtil.isEmpty(mAid)) {
                Intent intent = new Intent(LoginActivity.this, ThemeActionActivity.class);
                intent.putExtra("aid", mAid);
                intent.putExtra("title", mtitle);
                startActivity(intent);
                finish();
            } else if (logFlag.equals("lookWorld")) {
                EventBus.getDefault().post(new RefreshEarningAProfitBean("刷新赚一赚"));
                Intent intent = new Intent(LoginActivity.this, RecordVideoActivity.class);
                startActivity(intent);
                finish();
            } else if (logFlag.equals("activityTrip")) {
                Intent intent = new Intent(LoginActivity.this, ThemeActionActivity.class);
                intent.putExtra("aid", mAid);
                intent.putExtra("title", mtitle);
                startActivity(intent);
                finish();
            } else if (logFlag.equals("videoDetails")) {
                setResult(11);
                finish();
            } else if (logFlag.equals("personal")) {
                setResult(12);
                finish();
            } else if (logFlag.equals("setting")) {
//                Intent intent = new Intent(LoginActivity.this, SettingActivity.class);
//                startActivity(intent);
                setResult(13);
                finish();
            } else if (logFlag.equals("myMoney")) {
                Intent intent = new Intent(LoginActivity.this, MyMoneyActivity.class);
                startActivity(intent);
                finish();
            } else if (logFlag.equals("explain_red")) {
                Intent intent = new Intent(LoginActivity.this, RecordVideoActivity.class);
                intent.putExtra("logFlag", "lookWorld");
                startActivity(intent);
                finish();
            } else if (logFlag.equals("verifyExplainWeb")) {
                //Intent intent1 = new Intent();
                //intent1.setAction("重新加载");
                //sendBroadcast(intent1);
                EventBus.getDefault().post(new RefreshLoadBean("重新加载"));
                finish();
            } else if (logFlag.equals("myComments")) {
                Intent intent = new Intent(LoginActivity.this, SystemMessageActivity.class);
                startActivity(intent);
                finish();
            } else if (logFlag.equals("NewPersonal")) {
                Intent intent = new Intent(LoginActivity.this, NewPersonalHomePageActivity.class);
                intent.putExtra("uid", mUUid);
                startActivity(intent);
                finish();
            }else if (logFlag.equals("share")) {
                setResult(14);
                finish();
            }else if (logFlag.equals("link")){
                startActivity(new Intent(this,LinkTaskActivity.class));
                finish();
            }else if (logFlag.equals("shareweb")){
                startActivity(new Intent(this,ShareWebActivity.class));
                finish();
            }else if (logFlag.equals("commentHome")){//防微视首页，评论未登录来登录返回
                setResult(147);
                finish();
            }
        } else {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
//            //更新MainActivity 我的 RadioButton 上面的小红点
//            int msg_num = loginBean.getData().getMsg_num();
//            if (msg_num <= 0) {
//                EventBus.getDefault().post(new UpdateRedDotBean(false));
//            } else {
//                EventBus.getDefault().post(new UpdateRedDotBean(true));
//            }
            finish();
        }
    }

    /**
     * 第三方登录
     */
    private void thirdLogin(String intenPic) {
        if (timeFlag) {
            String sign = Md5Utils.createMD5("devcode=" + mDevcode + "location=" + mLocalCity + "login_id=" + login_id +
                    "login_type=" + login_type + "nickname=" + nickname + "phone_type=" + mPhonetype + "pic=" + intenPic +
                    "registration_id=" + registrationID + "timestamp=" + mTimestamp + URLUtils.WK_APP_KEY);
            RequestBody params = new FormBody.Builder()
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("location", mLocalCity)
                    .add("login_id", login_id)
                    .add("nickname", nickname)
                    .add("login_type", login_type)
                    .add("phone_type", mPhonetype)
                    .add("pic", intenPic)
                    .add("registration_id", registrationID)
                    .add("signature", sign)
                    .add("devcode", mDevcode)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.THIRD_LOGIN, params, new HttpCallBack(new ThirdLoginEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void thirdLoginMsg(ThirdLoginEvent thirdLoginEvent) {

        if (thirdLoginEvent.isTimeOut()) {
            mLoadDialog.dismiss();//取消动画
            Toast.makeText(this, "网络状态异常", Toast.LENGTH_SHORT).show();
            return;
        }
        final SaveMesgBean data = thirdLoginEvent.getData();
        if (data.getStatus() == 0) {
            mLoadDialog.dismiss();//取消动画
            String loginPhone = data.getData().getPhone();
            final String uid = data.getData().getUid();
            String info = data.getInfo();
            savePersonMsg(data);
            if (StringUtil.isEmpty(loginPhone)) {
                //TODO 跳转绑定页面
                Intent intent = new Intent(LoginActivity.this, BindingPhoneActivity.class);
                intent.putExtra("uid", uid);
                intent.putExtra("logFlag", logFlag);
                if (!StringUtil.isEmpty(mAid)) {
                    intent.putExtra("aid", mAid);
                }
                if (!StringUtil.isEmpty(mtitle)) {
                    intent.putExtra("title", mtitle);
                }
                startActivity(intent);
                finish();
//                new DialogKnow(this, R.style.user_default_dialog, new DialogKnow.CallBackListener() {
//                    @Override
//                    public void onClickListener() {
//
//
//                    }
//                }).setMsg("  " + info).setPositiveButton("立即绑定").show();
            } else {

                intentOthersActivity(data);

            }
        } else {
            mLoadDialog.dismiss();//取消动画
            Toast.makeText(this, data.getMsg(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isPermission) {
            mHelper.onActivityResult(requestCode, resultCode, data);
        }
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == 2) {
                    registerFlag = data.getStringExtra("logFlag");
                    String loginPhone = data.getStringExtra("phone");
                    if (!StringUtil.isEmpty(loginPhone)) {
                        mLoginPhone.setText(loginPhone);
                        getUserHeadImg(loginPhone);
                    }
                }
                break;
            case 123://开启GPS之后返回继续回去定位信息
                getLocation();
                break;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!StringUtil.isEmpty(logFlag)) {
                if (logFlag.equals("action")) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else if (logFlag.equals("makemoney")) {
                    setResult(4);
                    finish();
                } else if (logFlag.equals("unlogin")) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("logFlag", "unGotoTravel");
                    startActivity(intent);
                    finish();
                } else if (logFlag.equals("explain_red")) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();

                } else {
                    //startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            } else {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


    /**
     * 定位
     */
    private void getLocation() {
        locationUtil = LocationUtil.getLocationUtilInstance().initLocation(this);
        locationUtil.setmICallLocation(new LocationUtil.ICallLocation() {
            @Override
            public void locationMsg(AMapLocation aMapLocation) {
                String locationCity = aMapLocation.getCity();
                String country = aMapLocation.getCountry();
                if (!StringUtil.isEmpty(locationCity)) {
                    MyApplication.mApp.setLocationCity(locationCity);
                }
                if (!StringUtil.isEmpty(country)) {
                    MyApplication.mApp.setLocationCountry(country);
                }
                mLocalCity = locationCity;
                locationUtil.stopLocation();
            }
        });
    }


    @Override
    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (isPermission) {
            mHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


}
