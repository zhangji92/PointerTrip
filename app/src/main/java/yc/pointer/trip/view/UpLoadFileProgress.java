package yc.pointer.trip.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import yc.pointer.trip.R;

/**
 * Created by 张继
 * 2018/6/25
 * 13:41
 * 公司：
 * 描述：
 */

public class UpLoadFileProgress extends Dialog {

    private RoundProgressBar mProgressBar;


    public UpLoadFileProgress(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customize_up_load_progress);
        mProgressBar = findViewById(R.id.customize_up_load);
        mProgressBar.setMax(100);
        //触摸动画之外的地方是否取消动画，这里设置为false不取消
        setCanceledOnTouchOutside(false);
    }

    public void setNum(int num) {
        mProgressBar.setProgress(num);
    }
}
