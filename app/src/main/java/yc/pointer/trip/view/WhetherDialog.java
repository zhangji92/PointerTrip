package yc.pointer.trip.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import yc.pointer.trip.R;

/**
 * Created by 张继
 * 2018/7/3
 * 15:55
 * 公司：
 * 描述：出发吧弹窗  ---导游证 需要 不需要
 * 该类用法
 * new WhetherDialog(Context)
 * .setBntText("不能为空")
 * .setBntMsg("不能为空")
 * .setWhetherCallBack(接口)
 * .show();
 */

public class WhetherDialog extends Dialog implements View.OnClickListener {


    private String mBntText;
    private String mBntMsg;
    private Button bntPhoto;
    private Button bntPic;
    private Button btnNoChoose;//不限
    private TextView noChooseLine;
    private boolean isNochoose=false;

    public WhetherDialog(@NonNull Context context) {
        this(context, R.style.user_default_dialog);
    }
    public WhetherDialog(@NonNull Context context,boolean haveNoChoose) {
        this(context, R.style.user_default_dialog);
        this.isNochoose=haveNoChoose;
    }

    public WhetherDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_choose_dialog);
        setCancelable(false);//点击外部dismiss
        setCanceledOnTouchOutside(true);//点击返回键dismiss
        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        init();
    }

    private void init() {
        bntPic = findViewById(R.id.btn_picture);
        bntPic.setText(mBntText);
        bntPic.setTextColor(Color.BLACK);
        bntPic.setOnClickListener(this);

        bntPhoto = findViewById(R.id.btn_photo);
        bntPhoto.setText(mBntMsg);
        bntPhoto.setTextColor(Color.BLACK);
        bntPhoto.setOnClickListener(this);

        noChooseLine=findViewById(R.id.no_choose_line);

        btnNoChoose=findViewById(R.id.btn_no_choose);
        btnNoChoose.setTextColor(Color.BLACK);
        btnNoChoose.setOnClickListener(this);
        if (isNochoose){
            noChooseLine.setVisibility(View.VISIBLE);
            btnNoChoose.setVisibility(View.VISIBLE);
        }else {
            noChooseLine.setVisibility(View.GONE);
            btnNoChoose.setVisibility(View.GONE);
        }
        Button bntCancel = findViewById(R.id.btn_cancle);
        bntCancel.setTextColor(Color.BLACK);
        bntCancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancle:
                dismiss();
                break;
            case R.id.btn_picture:
                String trimPic = bntPic.getText().toString().trim();
                if (mWhetherCallBack != null) {
                    mWhetherCallBack.callBack(trimPic);
                }
                dismiss();
                break;
            case R.id.btn_photo:
                String trimPhoto = bntPhoto.getText().toString().trim();
                if (mWhetherCallBack != null) {
                    mWhetherCallBack.callBack(trimPhoto);
                }
                dismiss();
                break;
                case R.id.btn_no_choose:
                    String noChoose = btnNoChoose.getText().toString().trim();
                    if (mWhetherCallBack != null) {
                    mWhetherCallBack.callBack(noChoose);
                }
                dismiss();
                break;

        }
    }

    public WhetherDialog setBntText(String mBntText) {
        this.mBntText = mBntText;
        return this;
    }

    public WhetherDialog setBntMsg(String mmBntMsg) {
        this.mBntMsg = mmBntMsg;
        return this;
    }
    public WhetherDialog setBtnNoChooseVisable(int Visable){
        if (Visable==0){
           noChooseLine.setVisibility(View.VISIBLE);
            btnNoChoose.setVisibility(View.VISIBLE);
        }else {
           noChooseLine.setVisibility(View.GONE);
           btnNoChoose.setVisibility(View.GONE);
        }

        return this;
    }

    private WhetherCallBack mWhetherCallBack;

    public void setWhetherCallBack(WhetherCallBack mWhetherCallBack) {
        this.mWhetherCallBack = mWhetherCallBack;
    }

    public interface WhetherCallBack {
        void callBack(String msg);

    }
}
