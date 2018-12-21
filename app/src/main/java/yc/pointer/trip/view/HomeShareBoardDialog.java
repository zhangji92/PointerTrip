package yc.pointer.trip.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMWeb;

import yc.pointer.trip.R;
import yc.pointer.trip.activity.LoginActivity;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.untils.StringUtil;

/**
 * Created by 张继
 * 2018/4/10
 * 13:59
 * 公司:天津悦程嘉和网络科技有限公司
 * 描述: 首页分享的dialog
 */

public class HomeShareBoardDialog extends Dialog implements View.OnClickListener {
    public static final int SHARE_QQ = 0;//qq
    public static final int SHARE_QZONE = 1;//空间
    public static final int SHARE_WECHAT = 2;//微信
    public static final int SHARE_WXCIRCLE = 3;//wxcircle
    public static final int SHARE_SINA = 4;//新浪

    private Activity context;  //上下文环境

    private UMWeb mWeb;
    private ShareAction shareAction;
    private UMShareAPI mShareAPI;
    private String mCollections = "0";
    private String mReservationNumber = "0";
    private String mFavorNumber = "0";
    private String mForwardingNumber = "0";
    private String mBid = "0";
    private TextView mTvCollect;
    private TextView mTvReserve;
    private TextView mTvForwarding;
    private TextView mTvReport;
    private TextView mTvWechat;
    private TextView mTvWxcircle;
    private TextView mTvQQ;
    private TextView mTvQZone;
    private TextView mTvSina;
    private TextView mTvCancel;
//    private TextView mTvFavor;


    public void setBid(String bid) {
        this.mBid = bid;
        initData();
    }

    public void setFavorNumber(String favorNumber) {
        this.mFavorNumber = favorNumber;
        initData();
    }

    public void setmFavorNumber(String mFavorNumber) {
        this.mFavorNumber = mFavorNumber;
    }

    public void setmWeb(UMWeb mWeb) {
        this.mWeb = mWeb;
    }

    public void setmCollections(String mCollections) {
        this.mCollections = mCollections;
    }

    public void setmReservationNumber(String mReservationNumber) {
        this.mReservationNumber = mReservationNumber;
    }

    public void setmForwardingNumber(String mForwardingNumber) {
        this.mForwardingNumber = mForwardingNumber;

    }

    //    public HomeShareBoardDialog(Context _context, Activity activity, UMWeb web, String collections, String reservationNumber, String forwardingNumber) {
//        this(activity, R.style.user_default_dialog);
//        shareAction = new ShareAction(activity);//分享
//        mShareAPI = UMShareAPI.get(_context);
//        this.mWeb = web;
//        this.mCollections = collections;
//        this.mReservationNumber = reservationNumber;
//        this.mForwardingNumber = forwardingNumber;
//        initView();
//        initData();
//    }

    public HomeShareBoardDialog(Context _context, Activity activity, ShareCallBack shareCallBack) {
        this(activity, R.style.user_default_dialog);
        shareAction = new ShareAction(activity);//分享
        mShareAPI = UMShareAPI.get(_context);
        this.mShareCallBack = shareCallBack;
        initView();
    }
    public HomeShareBoardDialog(Context _context, Fragment activity, ShareCallBack shareCallBack) {
        this(activity.getActivity(), R.style.user_default_dialog);
        shareAction = new ShareAction(activity.getActivity());//分享
        mShareAPI = UMShareAPI.get(_context);
        this.mShareCallBack = shareCallBack;
        initView();
    }
    private HomeShareBoardDialog(@NonNull Activity context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    private void initView() {
        Window window = this.getWindow();
        //设置dialog在屏幕底部
        window.setGravity(Gravity.BOTTOM);
        //设置dialog弹出时的动画效果，从屏幕底部向上弹出
        window.setWindowAnimations(R.style.main_menu_animstyle);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.setContentView(R.layout.home_share_dialog_layout);
        //微信
        mTvWechat = (TextView) findViewById(R.id.home_share_wechat);
        //朋友圈
        mTvWxcircle = (TextView) findViewById(R.id.home_share_wxcircle);
        //qq
        mTvQQ = (TextView) findViewById(R.id.home_share_qq);
        //空间
        mTvQZone = (TextView) findViewById(R.id.home_share_qzone);
        //新浪
        mTvSina = (TextView) findViewById(R.id.home_share_sina);

        //收藏数
        mTvCollect = (TextView) findViewById(R.id.home_share_collect);
        //预约
        mTvReserve = (TextView) findViewById(R.id.home_share_reserve);
        //转发
        mTvForwarding = (TextView) findViewById(R.id.home_share_forwarding);

//        //点赞
//        mTvFavor = (TextView) findViewById(R.id.home_share_favor);


        //举报
        mTvReport = (TextView) findViewById(R.id.home_share_report);
        mTvCancel = (TextView) findViewById(R.id.home_share_cancel);

    }

    private void initData() {
        mTvWechat.setOnClickListener(this);
        mTvWxcircle.setOnClickListener(this);
        mTvQQ.setOnClickListener(this);
        mTvQZone.setOnClickListener(this);
        mTvSina.setOnClickListener(this);
        mTvCollect.setOnClickListener(this);
        mTvReserve.setOnClickListener(this);
        mTvForwarding.setOnClickListener(this);
        mTvReport.setOnClickListener(this);
//        mTvFavor.setOnClickListener(this);
        mTvCancel.setOnClickListener(this);

        mTvCollect.setText(mCollections);
        mTvReserve.setText(mReservationNumber);
        mTvForwarding.setText(mForwardingNumber);
//        mTvFavor.setText(mFavorNumber);
    }


    //后面这个方法的实现 为分享到对应的平台
    private void shareOperation(int shareType) {
        switch (shareType) {
            case SHARE_QQ:
                shareAction.setPlatform(SHARE_MEDIA.QQ)
                        .setCallback(shareListener)
                        .withMedia(mWeb)
                        .share();
                break;
            case SHARE_QZONE:
                shareAction.setPlatform(SHARE_MEDIA.QZONE)
                        .setCallback(shareListener)
                        .withMedia(mWeb)
                        .share();
                break;
            case SHARE_WECHAT:
                shareAction.setPlatform(SHARE_MEDIA.WEIXIN)
                        .setCallback(shareListener)
                        .withMedia(mWeb)
                        .share();
                break;
            case SHARE_WXCIRCLE:
                //mWeb.getMediaType().getu
                shareAction.setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                        .setCallback(shareListener)
                        .withMedia(mWeb)
                        .share();
                break;
            case SHARE_SINA:
                shareAction.setPlatform(SHARE_MEDIA.SINA)
                        .setCallback(shareListener)
                        .withMedia(mWeb)
                        .share();
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home_share_wechat://微信
                if (judgingLogin()) {
                    boolean WECHAT = mShareAPI.isInstall(context, SHARE_MEDIA.WEIXIN);
                    if (WECHAT) {
                        shareOperation(SHARE_WECHAT);
                    } else {
                        Toast.makeText(context, "亲，您尚未安装微信客户端，无法分享", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    toLoginActivity();
                }
                break;
            case R.id.home_share_wxcircle://微信朋友圈
                if (judgingLogin()) {
                    boolean WXCIRCLE = mShareAPI.isInstall(context, SHARE_MEDIA.WEIXIN_CIRCLE);
                    if (WXCIRCLE) {
                        shareOperation(SHARE_WXCIRCLE);
                    } else {
                        Toast.makeText(context, "亲，您尚未安装微信客户端，无法分享", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    toLoginActivity();
                }

                break;
            case R.id.home_share_qq://qq
                if (judgingLogin()) {
                    boolean isHaveQQ = mShareAPI.isInstall(context, SHARE_MEDIA.QQ);
                    if (isHaveQQ) {
                        shareOperation(SHARE_QQ);
                    } else {
                        Toast.makeText(context, "亲，您尚未安装QQ客户端，无法分享", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    toLoginActivity();
                }

                break;
            case R.id.home_share_qzone://空间
                if (judgingLogin()) {
                    boolean QZONE = mShareAPI.isInstall(context, SHARE_MEDIA.QQ);
                    if (QZONE) {
                        shareOperation(SHARE_QZONE);
                    } else {
                        Toast.makeText(context, "亲，您尚未安装QQ客户端，无法登录", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    toLoginActivity();
                }


                break;
            case R.id.home_share_sina://新浪
                if (judgingLogin()) {
                    shareOperation(SHARE_SINA);
                } else {
                    toLoginActivity();
                }

                break;
            case R.id.home_share_report://举报
                if (judgingLogin()) {
                    if (mShareCallBack != null) {
                        mShareCallBack.shareReport(mBid);
                    }
                } else {
                    toLoginActivity();
                }

                break;
            case R.id.home_share_cancel:
                dismiss();
                break;
        }

    }

    private void toLoginActivity() {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("logFlag", "share");
        context.startActivityForResult(intent, 147);

    }

    private boolean judgingLogin() {
        boolean flag = false;
        String userId = MyApplication.mApp.getUserId();
        if (!StringUtil.isEmpty(userId) && !userId.equals("0")) {
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

    private ShareCallBack mShareCallBack;


    public interface ShareCallBack {
        void shareSuccess(String bid);//分享成功

        void shareReport(String bid);//举报

    }


    private UMShareListener shareListener = new UMShareListener() {
        /**
         * @descrption 分享开始的回调
         * @param platform 平台类型
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {
            dismiss();
        }

        /**
         * @descrption 分享成功的回调
         * @param platform 平台类型
         */
        @Override
        public void onResult(SHARE_MEDIA platform) {
            dismiss();
            if (mShareCallBack != null) {
                mShareCallBack.shareSuccess(mBid);
            }
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            show();
            Toast.makeText(context, "失败" + t.getMessage(), Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            show();
        }
    };
}
