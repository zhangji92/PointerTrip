package yc.pointer.trip.behavior;

import android.content.Context;
import android.graphics.Rect;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import yc.pointer.trip.R;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.untils.MathUtils;

/**
 * Copy from AndroidBug5497Workaround design library
 * <p/>
 * Created by chensuilun on 16/7/24.
 */
public abstract class HeaderScrollingViewBehavior extends ViewOffsetBehavior<View> {
    private final Rect mTempRect1 = new Rect();
    private final Rect mTempRect2 = new Rect();
    //垂直的布局差距
    private int mVerticalLayoutGap = 0;
    //顶部距离
    private int mOverlayTop;

    public HeaderScrollingViewBehavior() {
    }

    public HeaderScrollingViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onMeasureChild(CoordinatorLayout parent, View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        final int childLpHeight = child.getLayoutParams().height;
        if (childLpHeight == ViewGroup.LayoutParams.MATCH_PARENT || childLpHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
            // If the menu's height is set to match_parent/wrap_content then measure it with the maximum visible height
            //如果菜单的高度设置为match_parent / wrap_content然后测量它最大可视高度
            final List<View> dependencies = parent.getDependencies(child);
            final View header = findFirstDependency(dependencies);
            if (header != null) {
                if (ViewCompat.getFitsSystemWindows(header) && !ViewCompat.getFitsSystemWindows(child)) {
                    // If the header is fitting system windows then we need to also,
                    // otherwise we'll get CoL's compatible measuring
                    //如果头是安装系统窗口，那么我们也需要，否则我们会得到CoL的兼容测量。
                    ViewCompat.setFitsSystemWindows(child, true);
                    if (ViewCompat.getFitsSystemWindows(child)) {
                        // If the set succeeded, trigger a new layout and return true
                        //如果该集合成功，则触发新布局并返回true
                        child.requestLayout();
                        return true;
                    }
                }
                if (ViewCompat.isLaidOut(header)) {
                    //当前测量的高度
                    int availableHeight = View.MeasureSpec.getSize(parentHeightMeasureSpec);
                    if (availableHeight == 0) {
                        // If the measure spec doesn't specify a size, use the current height
                        //如果测量规范没有指定尺寸，请使用当前高度。
                        availableHeight = parent.getHeight();
                    }
                    //
                    final int height = availableHeight - header.getMeasuredHeight() + getScrollRange(header);
                    final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height,
                            childLpHeight == ViewGroup.LayoutParams.MATCH_PARENT ? View.MeasureSpec.EXACTLY : View.MeasureSpec.AT_MOST);

                    // Now measure the scrolling view with the correct height
                    //现在，用正确的高度测量滚动视图。
                    parent.onMeasureChild(child, parentWidthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed);

                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void layoutChild(final CoordinatorLayout parent, final View child, final int layoutDirection) {
        final List<View> dependencies = parent.getDependencies(child);
        final View header = findFirstDependency(dependencies);

        if (header != null) {
            final CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            final Rect available = mTempRect1;
            available.set(parent.getPaddingLeft() + lp.leftMargin, header.getBottom() + lp.topMargin+getHeadHeight(),
                    parent.getWidth() - parent.getPaddingRight() - lp.rightMargin,
                    parent.getHeight() + header.getBottom() - parent.getPaddingBottom() - lp.bottomMargin);
            final Rect out = mTempRect2;
            GravityCompat.apply(resolveGravity(lp.gravity), child.getMeasuredWidth(), child.getMeasuredHeight(), available, out, layoutDirection);

            final int overlap = getOverlapPixelsForOffset(header);

            child.layout(out.left, out.top - overlap, out.right, out.bottom - overlap);
            mVerticalLayoutGap = out.top - header.getBottom();
        } else {
            // If we don't have a dependency, let super handle it
            super.layoutChild(parent, child, layoutDirection);
            mVerticalLayoutGap = 0;
        }
    }

    float getOverlapRatioForOffset(final View header) {
        return 1f;
    }

    final int getOverlapPixelsForOffset(final View header) {
        return mOverlayTop == 0
                ? 0
                : MathUtils.constrain(Math.round(getOverlapRatioForOffset(header) * mOverlayTop),
                0, mOverlayTop);

    }

    private static int resolveGravity(int gravity) {
        return gravity == Gravity.NO_GRAVITY ? GravityCompat.START | Gravity.TOP : gravity;
    }

    private int getHeadHeight() {
        return MyApplication.mApp.getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin200);
    }


    protected abstract View findFirstDependency(List<View> views);

    //滚动视图的顶部与页眉布局的底部之间的差距为像素。
    protected int getScrollRange(View v) {
        return v.getMeasuredHeight();
    }

    /**
     * The gap between the top of the scrolling view and the bottom of the header layout in pixels.
     */
    final int getVerticalLayoutGap() {
        return mVerticalLayoutGap;
    }

    /**
     * Set the distance that this view should overlap any {@link AppBarLayout}.
     *
     * @param overlayTop the distance in px
     */
    public final void setOverlayTop(int overlayTop) {
        mOverlayTop = overlayTop;
    }

    /**
     * Returns the distance that this view should overlap any {@link AppBarLayout}.
     */
    public final int getOverlayTop() {
        return mOverlayTop;
    }


}
