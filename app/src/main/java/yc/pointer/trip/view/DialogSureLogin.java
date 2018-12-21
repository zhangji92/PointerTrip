package yc.pointer.trip.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import yc.pointer.trip.R;

/**
 * Created by 张继
 * 2017/9/3
 * 14:49
 * 自定义弹窗 带确定 取消按钮
 */
public class DialogSureLogin extends Dialog implements View.OnClickListener {

    private TextView mTextViewTitle;//标题
    private TextView mTextViewMsg;//提示信息
    private Button mPositiveButton;//确定按钮
    private Button mNegativeButton;//取消按钮
    private CallBackListener mCallBackListener;
    public static DialogSureLogin mDialogSureLogin=null;
    private String mTitle;
    private String mMsg;
    private String mPositive;
    private String mNegative;

    public static DialogSureLogin getInstance(Context context, int themeResId, CallBackListener callBackListener){
        if (mDialogSureLogin==null){
            synchronized (DialogSureLogin.class){
                if (mDialogSureLogin==null){
                    mDialogSureLogin=new DialogSureLogin(context,themeResId,callBackListener);
                }
            }
        }
        return mDialogSureLogin;
    }


    private DialogSureLogin(Context context, int themeResId, CallBackListener callBackListener) {
        super(context, themeResId);
        this.mCallBackListener = callBackListener;
//        dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_layout_sure);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        initView();
    }

    public DialogSureLogin setTitle(String title) {
        this.mTitle=title;
        return this;
    }

    public DialogSureLogin setMsg(String msg) {
        this.mMsg=msg;
        return this;
    }

    public DialogSureLogin setPositiveButton(String positive) {
        this.mPositive=positive;
        return this;
    }

    public DialogSureLogin setNegativeButton(String negative) {
        this.mNegative=negative;
        return this;
    }

    private void initView() {
        mTextViewTitle = (TextView) findViewById(R.id.dialog_title);//标题
        mTextViewTitle.setText(mTitle);
        mTextViewMsg = (TextView) findViewById(R.id.dialog_text);//提示信息
        mTextViewMsg.setText(mMsg);
        mPositiveButton = (Button) findViewById(R.id.dialog_sure);//确定
        mPositiveButton.setText(mPositive);
        mNegativeButton = (Button) findViewById(R.id.dialog_cancel);//取消
        mNegativeButton.setText(mNegative);
        mPositiveButton.setOnClickListener(this);
        mNegativeButton.setOnClickListener(this);
    }
    
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_sure:
                if (mCallBackListener!=null){
                    mCallBackListener.onClickListener(this,true);
                }
                cancel();
                break;
            case R.id.dialog_cancel:
                if (mCallBackListener!=null){
                    mCallBackListener.onClickListener(this,false);
                }
                cancel();
                break;

        }
    }

    public interface CallBackListener{
        void onClickListener(DialogSureLogin dialogSure, boolean trueEnable);
    }
}
