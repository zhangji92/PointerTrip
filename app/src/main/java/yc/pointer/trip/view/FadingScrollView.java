package yc.pointer.trip.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ScrollView;

import yc.pointer.trip.R;

/**
 * Created by ZHL on 2016/12/19.
 */

public class FadingScrollView extends ScrollView {

    private static String TAG = "-----------FadingScrollView----------";
    //渐变view
    private View fadingView;
    //滑动view的高度，如果这里fadingHeightView是一张图片，
    // 那么就是这张图片上滑至完全消失时action bar 完全显示，
    // 过程中透明度不断增加，直至完全显示
    private View fadingHeightView;

//    //显隐控件
//    private ImageView showHidenView;

    private Context context;
    private boolean outAnimation=true;
    private boolean inAnimation=true;
    private boolean isWhite=false;

    private int oldY;
    //滑动距离，默认设置滑动500 时完全显示，根据实际需求自己设置
    private int fadingHeight = 250;


    private AlphaListener listener;//监听渐变试图的透明度

    /**
     * 根据渐变视图的透明度,控制页面状态栏颜色
     * @param listener
     */
    public void setListener(AlphaListener listener) {
        this.listener = listener;
    }


    public FadingScrollView(Context context) {
        super(context);
    }

    public FadingScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FadingScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setFadingView(Context context, View view) {
        this.context = context;
        this.fadingView = view;
    }

    public void setFadingHeightView(Context context, View v) {
        this.context = context;
        this.fadingHeightView = v;
    }

    public void setFadingHeight(int fadingHeight) {
        this.fadingHeight = fadingHeight;
    }


//    public void setShowHidenView(Context context, ImageView showHidenView) {
//        this.context = context;
//        this.showHidenView = showHidenView;
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (fadingHeightView != null) {
            int measuredHeight = fadingView.getMeasuredHeight();
            fadingHeight = fadingHeightView.getMeasuredHeight()-measuredHeight;
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
//        Animation animationOut = AnimationUtils.loadAnimation(context, R.anim.actionbar_up_out);//移出动画
//        Animation animationIn = AnimationUtils.loadAnimation(context, R.anim.actionbar_up_in);//进入动画

//判断竖直方向上滑动向上还是向下
//        int Y = t - oldt;
//        if (Y > 0) {//向下滑动，ActionBar隐藏
////            float fading = t < fadingHeight ? 0 : (t > fadingHeight ? fadingHeight : 0);
//            float fading = t > fadingHeight ? fadingHeight : (t > 30 ? t : 0);
//            updateActionBarAlpha(fading / fadingHeight);
////            if (t > fadingHeight) {
//////               animationOut.setAnimationListener(new Animation.AnimationListener() {
//////                   @Override
//////                   public void onAnimationStart(Animation animation) {
//////                       //动画开始
//////                       outAnimation=true;
//////                   }
//////
//////                   @Override
//////                   public void onAnimationEnd(Animation animation) {
//////                        //动画结束
//////                       outAnimation=false;
//////                       inAnimation=true;
//////                   }
//////
//////                   @Override
//////                   public void onAnimationRepeat(Animation animation) {
//////                        //动画重复
//////
//////                   }
//////               });
//////               if (outAnimation==true){
//////                   showHidenView.startAnimation(animationOut);
//////                   fadingView.startAnimation(animationOut);
//////               }
////                showHidenView.setVisibility(GONE);
////                fadingView.setVisibility(GONE);
////            }
//        } else if (Y < 0) {//向上滑动 ActionBar显示
//            float fading = t > fadingHeight ? fadingHeight : (t > 30 ? t : 0);
////            showHidenView.setVisibility(VISIBLE);
//        updateActionBarAlpha(fading / fadingHeight);
//
////            if (t < fadingHeight) {
////                    fadingView.setVisibility(GONE);
////                } else {
////                    fadingView.setVisibility(VISIBLE);
////                }
////                showHidenView.setVisibility(VISIBLE);
////            animationIn.setAnimationListener(new Animation.AnimationListener() {
////                @Override
////                public void onAnimationStart(Animation animation) {
////                    //动画开始
////                    inAnimation=true;
////                }
////
////                @Override
////                public void onAnimationEnd(Animation animation) {
////                  //动画结束
////                    inAnimation=false;
////                    outAnimation=true;
////                }
////
////                @Override
////                public void onAnimationRepeat(Animation animation) {
////                    //动画重复
////
////                }
////            });
//
//
////            showHidenView.setVisibility(VISIBLE);
////            if (inAnimation==true){
////                if (t < fadingHeight) {
////                    fadingView.setVisibility(GONE);
////                } else {
////                    fadingView.setVisibility(VISIBLE);
////                    fadingView.startAnimation(animationIn);
////                }
////                showHidenView.startAnimation(animationIn);
////
////            }
//
//        }


//        l,t  滑动后 xy位置，
//        oldl lodt 滑动前 xy 位置-----
//        float fading = t > fadingHeight ? fadingHeight : (t > 30 ? t : 0);
//        updateActionBarAlpha(fading / fadingHeight);
        float fading = t > fadingHeight ? fadingHeight : (t > 30 ? t : 0);
            updateActionBarAlpha(fading / fadingHeight);
    }


    void updateActionBarAlpha(float alpha) {
        try {
            setActionBarAlpha(alpha);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setActionBarAlpha(float alpha) throws Exception {
        if (fadingView == null) {
            throw new Exception("fadingView is null...");
        }
        fadingView.setAlpha(alpha);

        if (alpha==1){
            isWhite=true;
        }else {
            isWhite=false;
        }
        if (listener!=null){
            listener.statusBarIsWhite(isWhite);
        }



//        if (showHidenView == null) {
//            throw new Exception("showHidenView is null...");
//        }

//        showHidenView.setAlpha(alpha);
    }

    public interface AlphaListener{
        void statusBarIsWhite(boolean isWhite);
    }

}
