package yc.pointer.trip.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

import yc.pointer.trip.base.PressUpInterface;

/**
 * Created by 张继
 * 2018/3/16
 * 16:32
 * 公司:天津悦程嘉和网络科技有限公司
 * 描述:
 */

public class HorizontalView extends HorizontalScrollView {
    private ScrollViewListener mScrollViewListener = null;

    public void setPressUpInterface(PressUpInterface mPressUpInterface) {
        this.mPressUpInterface = mPressUpInterface;
    }

    private PressUpInterface mPressUpInterface = null;


    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.mScrollViewListener = scrollViewListener;
    }

    public HorizontalView(Context context) {
        this(context, null);
    }

    public HorizontalView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mScrollViewListener != null) {
            mScrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
        }

    }

    @Override
    public void setOnScrollChangeListener(OnScrollChangeListener l) {
        super.setOnScrollChangeListener(l);
    }


    public interface ScrollViewListener {
        void onScrollChanged(HorizontalView scrollView, int x, int y, int oldx, int oldy);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                if (mPressUpInterface != null) {
                    mPressUpInterface.up();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }
}
