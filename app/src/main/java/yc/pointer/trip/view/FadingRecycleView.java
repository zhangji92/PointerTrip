package yc.pointer.trip.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by moyan on 2017/11/24.
 */

public class FadingRecycleView extends RecyclerView {

    private static String TAG = "-----------FadingScrollView----------";
    //渐变view
    private View fadingView;
    //滑动view的高度，如果这里fadingHeightView是一张图片，
    // 那么就是这张图片上滑至完全消失时action bar 完全显示，
    // 过程中透明度不断增加，直至完全显示
    private View fadingHeightView;
    private int oldY;
    //滑动距离，默认设置滑动500 时完全显示，根据实际需求自己设置
    private int fadingHeight = 300;

    public FadingRecycleView(Context context) {
        super(context);
    }

    public FadingRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FadingRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setFadingView(View view){this.fadingView = view;}
    public void setFadingHeightView(View v){this.fadingHeightView = v;}

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(fadingHeightView != null)
            fadingHeight = fadingHeightView.getMeasuredHeight();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
//        l,t  滑动后 xy位置，
//        oldl lodt 滑动前 xy 位置-----
        float fading = t>fadingHeight ? fadingHeight : (t > 30 ? t : 0);
        updateActionBarAlpha( fading / fadingHeight);

    }

    void updateActionBarAlpha(float alpha){
        try {
            setActionBarAlpha(alpha);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setActionBarAlpha(float alpha) throws Exception{
        if(fadingView==null){
            throw new Exception("fadingView is null...");
        }
        fadingView.setAlpha(alpha);
    }
}
