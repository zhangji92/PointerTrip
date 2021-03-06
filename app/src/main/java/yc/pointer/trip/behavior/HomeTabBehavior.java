package yc.pointer.trip.behavior;

import android.content.Context;
import android.support.design.BuildConfig;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import yc.pointer.trip.R;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.untils.DensityUtil;


/**
 * 新闻标题
 * <p/>
 * Created by chensuilun on 16/7/25.
 */
public class HomeTabBehavior extends CoordinatorLayout.Behavior<View> {
    private static final String TAG = "HomeTitleBehavior";

    public HomeTabBehavior() {
    }

    public HomeTabBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);

    }


    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
        // FIXME: 16/7/27 不知道为啥在XML设置-45dip,解析出来的topMargin少了1个px,所以这里用代码设置一遍

        //((CoordinatorLayout.LayoutParams) child.getLayoutParams()).topMargin = (int) (getHeadHeight()+getTitleHeight()*4.3);
        ((CoordinatorLayout.LayoutParams) child.getLayoutParams()).topMargin = (int) (getHeadHeight()+getTitleHeight()*3.6);
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
            //int translationY = (int) ((dependency.getTranslationY() / (headerOffsetRange * 1.0f) * titleOffsetRange) * 3.62);//2.24
            int translationY = (int) ((dependency.getTranslationY() / (headerOffsetRange * 1.0f) * titleOffsetRange) * 3.12);//2.24
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
