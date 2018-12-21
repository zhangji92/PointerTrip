package yc.pointer.trip.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import yc.pointer.trip.R;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.untils.StringUtil;

/**
 * Created by 张继
 * 2017/9/3
 * 14:49
 * 自定义邀请码弹窗 带确定 取消按钮
 */
public class InvitationDialog extends Dialog implements View.OnClickListener {

    private Button mPositiveButton;//确定按钮
    private CallBackListener mCallBackListener;

    private CustomCircleImage circleImage;//头像
    private ImageView verifyResult;//VIP的标识图
    private EditText mEtCode;//朋友邀请码
    private TextView mTvCode;//本人邀请码
    private Context context;
    private String mCode;
    private String mHeadUrl;
    private String mWhoCode;
    private String mVerifyResult;//VIP认证的标识 verifyResult=2标识是VIP
    private String isVip;//VIP认证的标识 1:黄金会员  2：白金会员



    public InvitationDialog(Context context, int themeResId, CallBackListener callBackListener) {
        super(context, themeResId);
        this.context=context;
        this.mCallBackListener = callBackListener;
        dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_layout_invitation);
        setCanceledOnTouchOutside(false);
        initView();
    }

    //头像
    public InvitationDialog setHeaderUrl(String url) {
        mHeadUrl =url;
        return this;
    }

    //输入邀请码
    public InvitationDialog setCode(String code) {
        mCode =code;
        return this;
    }

    public InvitationDialog setWhoCode(String whoCode) {
        mWhoCode =whoCode;
        return this;
    }

    public InvitationDialog setVerifyResult(String verifyResult){
        mVerifyResult=verifyResult;
        return this;
    }
    public InvitationDialog setVipType(String is_Vip){
        isVip=is_Vip;
        return this;
    }




    private void initView() {
        circleImage = (CustomCircleImage) findViewById(R.id.invitation_head);//头像
        verifyResult = (ImageView) findViewById(R.id.verify_result);//VIP认证标识图
        ImageView mImgClose = (ImageView) findViewById(R.id.invitation_close);//关闭
        mImgClose.setOnClickListener(this);
        mEtCode = (EditText) findViewById(R.id.invitation_code);//别人的邀请码
        mTvCode = (TextView) findViewById(R.id.invitation_who_code);//我的邀请码
        mPositiveButton = (Button) findViewById(R.id.invitation_commit);//确定
        mPositiveButton.setOnClickListener(this);
        mTvCode.setText(mWhoCode);

//        OkHttpUtils.displayImg(circleImage, mHeadUrl);
        if (!StringUtil.isEmpty(isVip)){
            OkHttpUtils.displayGlideCircular(context,circleImage,mHeadUrl,verifyResult,isVip);
        }


//        if (!StringUtil.isEmpty(mVerifyResult)){
//            if (mVerifyResult.equals("2")){
//                verifyResult.setVisibility(View.VISIBLE);
//            }else {
//                verifyResult.setVisibility(View.GONE);
//            }
//        }else {
//            verifyResult.setVisibility(View.GONE);
//        }

        if (!StringUtil.isEmpty(mCode)) {
            mEtCode.setText(mCode);
            mEtCode.setFocusable(false);
            mEtCode.setFocusableInTouchMode(false);
        } else {
            mEtCode.setHint("请输入邀请码");
            mEtCode.setText("");
            mEtCode.setFocusableInTouchMode(true);
            mEtCode.setFocusable(true);
            mEtCode.requestFocus();
        }
        if (!StringUtil.isEmpty(mCode)) {//有邀请码
            mPositiveButton.setText("已邀请");
            mPositiveButton.setBackgroundResource(R.drawable.invitation_dialog_not);
            mPositiveButton.setClickable(false);
            mPositiveButton.setEnabled(false);
        } else {//无邀请码
            mPositiveButton.setText("确定");
            mPositiveButton.setBackgroundResource(R.drawable.comment_button);
            mPositiveButton.setClickable(true);
            mPositiveButton.setEnabled(true);
        }
    }

    @Override
    public void onClick(View view) {
        String code = mEtCode.getText().toString();
        switch (view.getId()) {
            case R.id.invitation_commit:
                if (mCallBackListener != null) {
                    if (!StringUtil.isEmpty(code)) {
                        mCallBackListener.onClickListener(this, true, code,mPositiveButton);
                        dismiss();
                    } else {
                        Toast.makeText(view.getContext(), "邀请码为空", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.invitation_close:
                if (mCallBackListener != null) {
                    mCallBackListener.onClickListener(this, false, "",mPositiveButton);
                }
                dismiss();
                break;
        }
    }

    public interface CallBackListener {
        void onClickListener(InvitationDialog dialogSure, boolean trueEnable, String code,Button positiveButton);
    }
}
