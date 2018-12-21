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
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMWeb;

import yc.pointer.trip.R;
import yc.pointer.trip.activity.ShareWebActivity;

/**
 * Created by 张继
 * 2018/4/10
 * 13:59
 * 公司:天津悦程嘉和网络科技有限公司
 * 描述: 旅游达人web分享
 */
public class TravelSharingDialog extends Dialog implements View.OnClickListener {
    public static final int SHARE_QQ = 0;//qq
    public static final int SHARE_QZONE = 1;//空间
    public static final int SHARE_WECHAT = 2;//微信
    public static final int SHARE_WXCIRCLE = 3;//wxcircle
    public static final int SHARE_SINA = 4;//新浪

    private Activity context;  //上下文环境

    private UMWeb mWeb;
    private ShareAction shareAction;
    private UMShareAPI mShareAPI;
    private TextView mTvWechat;
    private TextView mTvWxcircle;
    private TextView mTvQQ;
    private TextView mTvQZone;
    private TextView mTvSina;
    private TextView mTvCancel;
    public ShareSuccessListener listener;

    public TravelSharingDialog(Activity context, Activity activity) {
        this(activity, R.style.user_default_dialog);
        shareAction = new ShareAction(activity);//分享
        mShareAPI = UMShareAPI.get(context);
        initView();
    }

    public TravelSharingDialog(Context context, Fragment activity) {
        this(activity.getActivity(), R.style.user_default_dialog);
        shareAction = new ShareAction(activity.getActivity());//分享
        mShareAPI = UMShareAPI.get(context);
        initView();
    }
    public void setListener(ShareSuccessListener listener) {
        this.listener = listener;
    }
    private TravelSharingDialog(@NonNull Activity context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    public TravelSharingDialog setWeb(UMWeb mWeb) {
        this.mWeb = mWeb;
        return this;
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
        window.setContentView(R.layout.travel_sharing_dialog);
        //微信
        mTvWechat = (TextView) findViewById(R.id.travel_wechat);
        //朋友圈
        mTvWxcircle = (TextView) findViewById(R.id.travel_wxcircle);
        //qq
        mTvQQ = (TextView) findViewById(R.id.travel_qq);
        //空间
        mTvQZone = (TextView) findViewById(R.id.travel_qzone);
        //新浪
        mTvSina = (TextView) findViewById(R.id.travel_sina);
        mTvCancel = (TextView) findViewById(R.id.travel_cancel);
        initData();
    }

    private void initData() {
        mTvWechat.setOnClickListener(this);
        mTvWxcircle.setOnClickListener(this);
        mTvQQ.setOnClickListener(this);
        mTvQZone.setOnClickListener(this);
        mTvSina.setOnClickListener(this);
        mTvCancel.setOnClickListener(this);
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
            case R.id.travel_wechat://微信
                boolean WECHAT = mShareAPI.isInstall(context, SHARE_MEDIA.WEIXIN);
                if (WECHAT) {
                    shareOperation(SHARE_WECHAT);
                } else {
                    Toast.makeText(context, "亲，您尚未安装微信客户端，无法分享", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.travel_wxcircle://微信朋友圈
                boolean WXCIRCLE = mShareAPI.isInstall(context, SHARE_MEDIA.WEIXIN_CIRCLE);
                if (WXCIRCLE) {
                    shareOperation(SHARE_WXCIRCLE);
                } else {
                    Toast.makeText(context, "亲，您尚未安装微信客户端，无法分享", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.travel_qq://qq
                boolean isHaveQQ = mShareAPI.isInstall(context, SHARE_MEDIA.QQ);
                if (isHaveQQ) {
                    shareOperation(SHARE_QQ);
                } else {
                    Toast.makeText(context, "亲，您尚未安装QQ客户端，无法分享", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.travel_qzone://空间
                boolean QZONE = mShareAPI.isInstall(context, SHARE_MEDIA.QQ);
                if (QZONE) {
                    shareOperation(SHARE_QZONE);
                } else {
                    Toast.makeText(context, "亲，您尚未安装QQ客户端，无法登录", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.travel_sina://新浪
                shareOperation(SHARE_SINA);
                break;
            case R.id.travel_cancel:
                dismiss();
                break;
        }

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
            if (listener!=null){
                listener.shareSuccess();
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
    public interface ShareSuccessListener{
        void shareSuccess();
    }
}
