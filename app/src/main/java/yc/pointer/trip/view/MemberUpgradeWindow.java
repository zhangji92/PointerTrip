package yc.pointer.trip.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import yc.pointer.trip.R;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.bean.CityMsg;
import yc.pointer.trip.untils.AppFileMgr;
import yc.pointer.trip.untils.StringUtil;

/**
 * Create by 张继
 * 2017/8/17
 * 15:09
 * 出行规则选择器
 */
public class MemberUpgradeWindow extends PopupWindow implements View.OnClickListener {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private View mRootView;
    private MemberUpgradeCallBack mMemberUpgradeCallBack;
    private CheckBox mAliPay;
    private CheckBox mWeChat;
    private CheckBox mSelect;


    public MemberUpgradeWindow(Context context, final Activity activity, MemberUpgradeCallBack callBackListener) {
        super(context);
        this.mContext = context;
        this.mMemberUpgradeCallBack = callBackListener;
        //打气筒
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //打气
        mRootView = mLayoutInflater.inflate(R.layout.member_upgrade, null);
        //设置View
        setContentView(mRootView);
        //设置宽与高
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        /**
         * 设置进出动画
         */
        setAnimationStyle(R.style.main_menu_animstyle);

        /**
         * 设置背景只有设置了这个才可以点击外边和BACK消失
         */
        setBackgroundDrawable(mContext.getResources().getDrawable(R.color.colorTitle));

        /**
         * 设置可以获取集点
         */
        setFocusable(true);
        /**
         * 设置点击外边可以消失
         */
        setOutsideTouchable(true);
        /**
         *设置可以触摸
         */
        setTouchable(true);
        /**
         * 设置点击外部可以消失
         */
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /**
                 * 判断是不是点击了外部
                 */
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    return true;
                }
                //不是点击外部
                return false;
            }
        });
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams layoutParams = activity.getWindow().getAttributes();
                layoutParams.alpha = 1.0f;
                activity.getWindow().setAttributes(layoutParams);
            }
        });
        /**
         * 初始化View与监听器
         */
        initView(mRootView);
    }


    /**
     * 初始化
     *
     * @param view 视图
     */
    private void initView(View view) {
        ImageView imgClose = view.findViewById(R.id.member_close);
        imgClose.setOnClickListener(this);
        mWeChat = view.findViewById(R.id.member_wechat);
        mWeChat.setOnClickListener(this);
        mAliPay = view.findViewById(R.id.member_alipay);
        mAliPay.setOnClickListener(this);
        mSelect = view.findViewById(R.id.member_select);

        String is_yj = MyApplication.mApp.getUserBean().getIs_yj();//判断是否读过押金协议
        if (!StringUtil.isEmpty(is_yj)) {
            if (is_yj.equals("1")) {
                mSelect.setChecked(true);//允许点击
            } else {
                mSelect.setChecked(false);//不允许点击
            }
        }
        //不允许点击
        mSelect.setClickable(false);

        TextView explanation = view.findViewById(R.id.member_explanation);
        explanation.setOnClickListener(this);
        Button pay = view.findViewById(R.id.member_pay);
        pay.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.member_close://取消付款
                dismiss();
                break;
            case R.id.member_explanation://阅读服务协议
                if (mMemberUpgradeCallBack != null) {
                    mMemberUpgradeCallBack.memberSelect(mSelect);
                }
                break;
            case R.id.member_alipay://支付宝支付
                if (mWeChat.isChecked()) {
                    mWeChat.setChecked(false);
                }
                break;
            case R.id.member_wechat://微信支付
                if (mAliPay.isChecked()) {
                    mAliPay.setChecked(false);
                }
                break;
            case R.id.member_pay://支付
                dismiss();
                if (mMemberUpgradeCallBack != null) {
                    if (mAliPay.isChecked()) {
                        mMemberUpgradeCallBack.memberCallBack("0");
                    } else if (mWeChat.isChecked()) {
                        mMemberUpgradeCallBack.memberCallBack("1");
                    }
                }
                break;
            default:
                break;
        }
    }

    public interface MemberUpgradeCallBack {
        /**
         * 根据类型付款
         *
         * @param type 0支付宝 1微信
         */
        void memberCallBack(String type);

        void memberSelect(CheckBox select);
    }

}
