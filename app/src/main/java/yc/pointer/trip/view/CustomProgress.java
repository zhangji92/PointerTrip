package yc.pointer.trip.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import yc.pointer.trip.R;


/**
 * Created by 张继
 * 2018/9/14
 * 15:11
 * 公司：
 * 描述：
 */

public class CustomProgress extends ProgressBar {
    //默认值
    private static final int DEAFUALT_PROGRESS_UNREACH_HEIGHH = 10;//dp
    private static final int DEAFUALT_PROGRESS_UNREACH_CORLOR = 0xFFD3D6DA;
    private static final int DEAFUALT_PROGRESS_REACH_HEIGHH = 8;//dp
    private static final int DEAFUALT_PROGRESS_REACH_CORLOR = 0xFFD70D0D;
    private static final int DEAFUALT_PROGRESS_TEXT_SIZE = 10;//sp
    private static final int DEAFUALT_PROGRESS_TEXT_CORLOR = 0xFFD3D6DA;
    private static final int DEAFUALT_PROGRESS_TEXT_OFFSET = 10;//dp
    private static final int DEAFUALT_PROGRESS_VIEW_WIDTH = 200;//进度条默认宽度

    private int HorizontalProgresUnReachColor;//不能用static修饰,不然多个View会共用此属性
    private int HorizontalProgresUnReachHeight;
    private int HorizontalProgresReachColor;
    private int HorizontalProgresReachHeight;
    private int HorizontalProgresTextColor;
    private int HorizontalProgresTextSize;
    private int HorizontalProgresTextOffset;
    private int HorizontalProgressBack;
    private Paint mPaint;
    private int width;
    private String progressText;

    public CustomProgress(Context context) {
        this(context, null);
    }

    public CustomProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setAntiAlias(true); //消除锯齿
        geStyleAttributeSet(context, attrs);
        mPaint.setTextSize(HorizontalProgresTextSize);//设置画笔文字大小,便于后面测量文字宽高
        mPaint.setColor(HorizontalProgresTextColor);
    }

    /**
     * 获取自定义属性
     */
    private void geStyleAttributeSet(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomProgressStyle);
        HorizontalProgresUnReachColor = typedArray.getColor(R.styleable.CustomProgressStyle_ProgressUnReachColor, DEAFUALT_PROGRESS_UNREACH_CORLOR);
        HorizontalProgresReachColor = typedArray.getColor(R.styleable.CustomProgressStyle_ProgressReachColor, DEAFUALT_PROGRESS_REACH_CORLOR);
        //将sp、dp统一转换为sp
        HorizontalProgresReachHeight = (int) typedArray.getDimension(R.styleable.CustomProgressStyle_ProgressReachHeight, dp2px(getContext(), DEAFUALT_PROGRESS_REACH_HEIGHH));
        HorizontalProgresUnReachHeight = (int) typedArray.getDimension(R.styleable.CustomProgressStyle_ProgressUnReachHeight, dp2px(getContext(), DEAFUALT_PROGRESS_UNREACH_HEIGHH));
        HorizontalProgresTextColor = typedArray.getColor(R.styleable.CustomProgressStyle_ProgressTextColor, DEAFUALT_PROGRESS_TEXT_CORLOR);
        HorizontalProgresTextSize = (int) typedArray.getDimension(R.styleable.CustomProgressStyle_ProgressTextSize, sp2px(getContext(), DEAFUALT_PROGRESS_TEXT_SIZE));
        HorizontalProgresTextOffset = (int) typedArray.getDimension(R.styleable.CustomProgressStyle_ProgressTextOffset, DEAFUALT_PROGRESS_TEXT_OFFSET);
        HorizontalProgressBack = typedArray.getColor(R.styleable.CustomProgressStyle_ProgressBack, DEAFUALT_PROGRESS_TEXT_CORLOR);
        progressText = typedArray.getString(R.styleable.CustomProgressStyle_ProgressText);
        typedArray.recycle();//记得加这句
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //计算宽高
        width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);//设置宽高
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();//save、restore 图层的保存和回滚相关的方法 详见 http://blog.csdn.net/tianjian4592/article/details/45234419
        canvas.translate(0, getHeight() / 3);//移动图层到垂直居中位置
        float radio = getProgress() * 1.0f / getMax();
        int textWidth = (int) mPaint.measureText(getProgress() + "");//The width of the text
        //float realWidth = getWidth() - getPaddingLeft() - getPaddingRight() - HorizontalProgresTextOffset;//实际宽度减去文字宽度
        //float realWidth = getWidth() - HorizontalProgresTextOffset;//实际宽度减去文字宽度
        float progressX = radio * getWidth();
        mPaint.setStrokeWidth(HorizontalProgresReachHeight);
        //绘制走完的进度线 背景色
        mPaint.setColor(HorizontalProgressBack);
        canvas.drawLine(getPaddingLeft(), getPaddingTop(), getWidth(), getPaddingTop(), mPaint);//直角 垂直在同一高度 float startY, float stopY 一样
        if (getProgress() != 0) {//进度为0时不绘制文本字体
            //绘制走完的进度线
            mPaint.setColor(HorizontalProgresReachColor);
            canvas.drawLine(getPaddingLeft(), getPaddingTop(), progressX - 30, getPaddingTop(), mPaint);//直角 垂直在同一高度 float startY, float stopY 一样
            //设置画笔类型
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(8);
            //绘制圆环
            canvas.drawCircle(progressX-15, getPaddingTop(), 15, mPaint);
            if (getProgress() != getMax()) {//当进度等于最大值时文本不显示
                //设置画笔类型
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(HorizontalProgresTextColor);
                mPaint.setTextSize(HorizontalProgresTextSize);
                mPaint.setStrokeWidth(2);
                if (getProgress() < getMax() / 3) {
                    //绘制文本
                    canvas.drawText(getProgressText(), progressX, 50, mPaint);
                } else if (getProgress() > getMax() / 2) {
                    //绘制文本
                    canvas.drawText(getProgressText(), progressX - textWidth, 50, mPaint);
                } else {
                    canvas.drawText(getProgressText(), progressX - textWidth / 3, 50, mPaint);
                }
            }
        }
        canvas.restore();
    }

    /**
     * dp转px
     *
     * @param context 上下文
     * @param dpVal   dp
     * @return 返回px
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     *
     * @param context
     * @param spVal
     * @return
     */
    public static int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }

    /**
     * Determines the width of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The width of the view, honoring constraints from measureSpec
     */
    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text
            result = dp2px(getContext(), DEAFUALT_PROGRESS_VIEW_WIDTH);//
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    /**
     * Determines the height of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The height of the view, honoring constraints from measureSpec
     */
    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text (beware: ascent is a negative number)
            //此处高度为走完的进度高度和未走完的机大以及文字的高度的最大值
            int textHeight = (int) (mPaint.descent() - mPaint.ascent());//得到字的高度有二种方式,第一种是React,第二种这个
            result = Math.max(textHeight, Math.max(HorizontalProgresReachHeight, HorizontalProgresUnReachHeight)) + getPaddingTop()
                    + getPaddingBottom();
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    public String getProgressText() {
        return progressText;
    }

    public void setProgressText(String progressText) {
        this.progressText = progressText;
    }

}
