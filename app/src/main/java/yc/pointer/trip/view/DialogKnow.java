package yc.pointer.trip.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import yc.pointer.trip.R;

/**
 * Created by 张继
 * 2017/9/3
 * 14:49
 * 自定义弹窗 我知道了
 */
public class DialogKnow extends Dialog implements View.OnClickListener {

    private TextView mTextViewTitle;//标题
    private TextView mTextViewMsg;//提示信息
    private Button mKnowButton;//确定按钮
    private CallBackListener mCallBackListener;

    private String mTitle;
    private String mMsg;
    private String mPositive;

    public DialogKnow(Context context, int themeResId, CallBackListener callBackListener) {
        super(context, themeResId);
        this.mCallBackListener = callBackListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_layout_know);
        setCanceledOnTouchOutside(false);
        initView();
    }

    public DialogKnow setTitle(String title) {
        this.mTitle=title;
        return this;
    }

    public DialogKnow setMsg(String msg) {
        this.mMsg=msg;
        return this;
    }

    public DialogKnow setPositiveButton(String positive) {
        this.mPositive=positive;
        return this;
    }

    private void initView() {
        mTextViewTitle = (TextView) findViewById(R.id.dialog_title);//标题
        mTextViewTitle.setText(mTitle);
        mTextViewMsg = (TextView) findViewById(R.id.dialog_text);//提示信息
        mTextViewMsg.setText(mMsg);
        mKnowButton = (Button) findViewById(R.id.dialog_sure);//确定
        mKnowButton.setText(mPositive);
        mKnowButton.setOnClickListener(this);
    }
    
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_sure:
                if (mCallBackListener!=null){
                    mCallBackListener.onClickListener();
                }
                dismiss();
                break;
        }
    }

    public interface CallBackListener{
        void onClickListener();
    }

//    @Override
//    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
//        if (keyCode==KeyEvent.KEYCODE_BACK){
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}
