package yc.pointer.trip.behavior;

import android.content.Context;
import android.support.design.BuildConfig;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import yc.pointer.trip.R;
import yc.pointer.trip.application.MyApplication;

/**
 * Created by 张继
 * 2018/4/11
 * 15:09
 * 公司:天津悦程嘉和网络科技有限公司
 * 描述:
 * 首页布局四个按钮的behavior
 */

public class HomeNavigateBehavior extends CoordinatorLayout.Behavior<View>{
    private static final String TAG = "HomeTitleBehavior";

    public HomeNavigateBehavior() {
    }

    public HomeNavigateBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);

    }


    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
        // FIXME: 16/7/27 不知道为啥在XML设置-45dip,解析出来的topMargin少了1个px,所以这里用代码设置一遍

        ((CoordinatorLayout.LayoutParams) child.getLayoutParams()).topMargin = getHeadHeight()+getTitleHeight()/2+getTitleHeight()/2;
        parent.onLayoutChild(child, layoutDirection);
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "layoutChild:top" + child.getTop() + ",height" + child.getHeight());
        }
        return true;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return isDependOn(dependency);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        offsetChildAsNeeded(parent, child, dependency);
        return false;
    }

    private void offsetChildAsNeeded(CoordinatorLayout parent, View child, View dependency) {
        int headerOffsetRange = getHeaderOffsetRange();
        int titleOffsetRange = getTitleHeight();
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "offsetChildAsNeeded:" + dependency.getTranslationY());
        }
        if (dependency.getTranslationY() == headerOffsetRange) {
            child.setTranslationY(titleOffsetRange);
        } else if (dependency.getTranslationY() == 0) {
            child.setTranslationY(0);
        } else {
            //int translationY = (int) ((dependency.getTranslationY() / (headerOffsetRange * 1.0f) * titleOffsetRange) * 2.6);//2.24
            int translationY = (int) ((dependency.getTranslationY() / (headerOffsetRange * 1.0f) * titleOffsetRange) * 2.2);//2.24
            child.setTranslationY(translationY);
        }
    }

    private int getHeaderOffsetRange() {
        return MyApplication.mApp.getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin45);
    }

    private int getTitleHeight() {
        return MyApplication.mApp.getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin45);
    }

    private int getHeadHeight() {
        return MyApplication.mApp.getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin180);
    }


    private boolean isDependOn(View dependency) {
        return dependency != null && dependency.getId() == R.id.home_header_pager;
    }


}
