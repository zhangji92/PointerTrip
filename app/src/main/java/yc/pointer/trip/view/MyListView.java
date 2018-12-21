package yc.pointer.trip.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by 张继
 * 2018/5/9
 * 11:56
 * 公司：
 * 描述：
 */

public class MyListView extends ListView {

    private OnSizeChangedListener mOnSizeChangedListener;

    public void setOnSizeChangedListener(OnSizeChangedListener onSizeChangedListener) {
        this.mOnSizeChangedListener = onSizeChangedListener;
    }

    public MyListView(Context context) {
        super(context);
    }

    public MyListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mOnSizeChangedListener!=null){
            mOnSizeChangedListener.onSizeChanged();
        }
    }

    public interface OnSizeChangedListener{
        void onSizeChanged();
    }

}
