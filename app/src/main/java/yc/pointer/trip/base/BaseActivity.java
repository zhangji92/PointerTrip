package yc.pointer.trip.base;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.Unbinder;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;

import yc.pointer.trip.R;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.AndroidBug5497Workaround;
import yc.pointer.trip.untils.AppDavikActivityMgr;
import yc.pointer.trip.untils.StatusBarUtils;
import yc.pointer.trip.untils.UtilHelpers;

/**
 * Created by Administrator on 2017/6/21.
 * 基础Activity，带动画切换效果
 */
public abstract class BaseActivity extends AppCompatActivity {
    //绑定布局
    private Unbinder mUnBinder;

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getContentViewLayout());
        AppDavikActivityMgr.getScreenManager().addActivity(this);//当前的activity加入栈中
        if (Build.VERSION.SDK_INT >= 21) {
            //状态了白底黑字
            this.getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        mUnBinder = ButterKnife.bind(this);//绑定注解
        if (needEventBus()) {
            try {
                if (!EventBus.getDefault().isRegistered(BaseActivity.this)) {
                    EventBus.getDefault().register(BaseActivity.this);
                }
            } catch (EventBusException e) {
                Log.e("LogInterceptor-->", "该类中没有 接收 eventbus 回调的方法");
            }
        }
        initView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnBinder.unbind();
        mUnBinder = null;
        if (needEventBus()) {
            try {
                if (EventBus.getDefault().isRegistered(BaseActivity.this)) {
                    EventBus.getDefault().unregister(BaseActivity.this);
                }
            } catch (EventBusException e) {
                Log.e("LogInterceptor-->", "该类中没有 接收 eventbus 回调的方法");
            }
        }
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        setTransitionAnimation(true);
    }


    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        setTransitionAnimation(true);
    }

    @Override
    public void finish() {
        super.finish();
        setTransitionAnimation(false);
    }

    //toolBar的返回按钮，返回上一级页面
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void finishWithDefaultTransition() {
        super.finish();
    }

    private void setTransitionAnimation(boolean newActivityIn) {
        if (newActivityIn) {
            // 新页面从右边进入
            overridePendingTransition(R.anim.push_right_in,
                    R.anim.push_right_out);
        } else {
            // 上一个页面从左边进入
            overridePendingTransition(R.anim.push_left_in,
                    R.anim.push_left_out);
        }
    }

    /**
     * 点击空白处，隐藏软键盘
     *
     * @param
     * @return
     */
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                View view = getCurrentFocus();
//                UtilHelpers.hideKeyboard(ev, view, BaseActivity.this);//调用方法判断是否需要隐藏键盘
//                break;
//            default:
//                break;
//        }
//        return super.dispatchTouchEvent(ev);
//    }
    @LayoutRes
    protected abstract int getContentViewLayout();

    protected abstract void initView();

    protected abstract boolean needEventBus();


}
