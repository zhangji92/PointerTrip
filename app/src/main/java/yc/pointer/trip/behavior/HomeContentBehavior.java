package yc.pointer.trip.behavior;

import android.content.Context;
import android.support.design.BuildConfig;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.List;

import yc.pointer.trip.R;
import yc.pointer.trip.application.MyApplication;


/**
 * 可滚动的新闻列表Behavior
 * <p/>
 * Created by chensuilun on 16/7/24.
 */
public class HomeContentBehavior extends HeaderScrollingViewBehavior {
    private static final String TAG = "HomeContentBehavior";

    public HomeContentBehavior() {

    }

    public HomeContentBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return isDependOn(dependency);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onDependentViewChanged");
        }
        offsetChildAsNeeded(parent, child, dependency);
        return false;
    }

    private void offsetChildAsNeeded(CoordinatorLayout parent, View child, View dependency) {
        int translationY = (int) ((-dependency.getTranslationY() / (getHeaderOffsetRange() * 1.0f) * getScrollRange(dependency))*3.1);
        child.setTranslationY(translationY);
    }


    @Override
    protected View findFirstDependency(List<View> views) {
        for (int i = 0, z = views.size(); i < z; i++) {
            View view = views.get(i);
            if (isDependOn(view))
                return view;
        }
        return null;
    }

    @Override
    protected int getScrollRange(View v) {
        if (isDependOn(v)) {
            return Math.max(0, v.getMeasuredHeight() - getFinalHeight());
        } else {
            return super.getScrollRange(v);
        }
    }

    private int getHeaderOffsetRange() {
        return MyApplication.mApp.getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin_90);
    }

    private int getFinalHeight() {
        return MyApplication.mApp.getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin45)
                + MyApplication.mApp.getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin45);
    }

    private boolean isDependOn(View dependency) {
        return dependency != null && dependency.getId() == R.id.home_header_pager;
    }
}
