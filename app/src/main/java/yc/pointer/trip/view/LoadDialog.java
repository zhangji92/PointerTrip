package yc.pointer.trip.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import yc.pointer.trip.R;

/**
 * Created by Administrator on 2017/1/3.
 * 界面加载的dialog
 */

public class LoadDialog extends ProgressDialog {
    private AnimationDrawable mAnimation;//动画
    private ImageView mImageView;//图片集合
    private String mLoadingTip;//正在加载
    private TextView mLoadingTv;//正在加载
    private int mResid;


    public LoadDialog(Context context, int theme) {
        super(context, theme);
    }

    public LoadDialog(Context context, String content, int id) {
        this(context,R.style.user_default_dialog);
        this.mLoadingTip = content;
        this.mResid = id;
        //触摸动画之外的地方是否取消动画，这里设置为false不取消
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actiity_load_dialog);
        initView();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mImageView.setBackgroundResource(mResid);
        //通过ImageView对象拿到背景显示的AnimationDrawable
        mAnimation = (AnimationDrawable) mImageView.getBackground();
        //为了防止在onCreate方法中现实第一帧的解决方案之一
        mImageView.post(new Runnable() {
            @Override
            public void run() {
                mAnimation.start();//动画开始
            }
        });
        mLoadingTv.setText(mLoadingTip);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mLoadingTv = (TextView) findViewById(R.id.load_textView);
        mImageView = (ImageView) findViewById(R.id.load_image);
    }
}
