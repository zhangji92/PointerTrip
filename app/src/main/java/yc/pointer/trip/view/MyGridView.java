package yc.pointer.trip.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by Administrator on 2016/12/16.
 * 热点城市的自定义控件
 */

public class MyGridView extends GridView {
    public MyGridView(Context context) {
        this(context,null);
    }

    public MyGridView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置不滚动
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpce= MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpce);
    }
}
