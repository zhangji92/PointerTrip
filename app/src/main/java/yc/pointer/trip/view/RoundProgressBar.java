package yc.pointer.trip.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import yc.pointer.trip.R;


/**
 * Created by 张继
 * 2017/12/6
 * 10:59
 * 自定义圆形进度条
 */

public class RoundProgressBar extends View {
    private Paint mPaint;//画笔对象的引用
    private int mRoundColor;//圆环的颜色
    private int mRoundProgressColor;//圆环进度的颜色
    private int mTextColor;//中间进度百分比的字符的颜色
    private float mTextSize;//中间进度百分比的字符串的字体大小
    private float mRoundWidth;//圆环的宽度
    private int mMax;//最大进度
    private int mProgress;//当前进度
    private boolean mTextIsDisplayable;//是否显示中间的进度
    private int mStyle;//进度的风格，实心或者空心
    private int mStartAngle;//进度开始的角度数
    private int mBackColor;//圆形的颜色

    public static final int STROKE = 0;
    public static final int FILL = 1;

    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();//创建对象
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar);
        //圆环的颜色
        mRoundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor, Color.WHITE);
        //圆环进度条的颜色
        mRoundProgressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor, Color.parseColor("#5a9fff"));
        //文字的颜色
        mTextColor = mTypedArray.getColor(R.styleable.RoundProgressBar_textColor, Color.GREEN);
        //文字的大小
        mTextSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_textSize, 16);
        //圆环的宽度
        mRoundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 3);
        //最大的进度
        mMax = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 100);
        //是否显示中间的进度
        mTextIsDisplayable = mTypedArray.getBoolean(R.styleable.RoundProgressBar_textIsDisplayable, true);
        //进度的风格，实心或者空心
        mStyle = mTypedArray.getInt(R.styleable.RoundProgressBar_style, 0);
        //进度开始的角度数
        mStartAngle = mTypedArray.getInt(R.styleable.RoundProgressBar_startAngle, -90);
        // 圆形颜色
        mBackColor = mTypedArray.getColor(R.styleable.RoundProgressBar_backColor, 0);
        mTypedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**
         * 画最外层的大圆环
         */
        int centre = getWidth() / 2; //获取圆心的x坐标
        int radius = (int) (centre - mRoundWidth / 2); //圆环的半径
        mPaint.setColor(mRoundColor); //设置圆环的颜色
        mPaint.setStyle(Paint.Style.STROKE); //设置空心
        mPaint.setStrokeWidth(mRoundWidth); //设置圆环的宽度
        mPaint.setAntiAlias(true);  //消除锯齿
        canvas.drawCircle(centre, centre, radius, mPaint); //画出圆环

        if (mBackColor != 0) {
            mPaint.setAntiAlias(true);
            mPaint.setColor(mBackColor);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(centre, centre, radius, mPaint);
        }
        /**
         * 画进度百分比
         */
        mPaint.setStrokeWidth(0);
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD); //设置字体
        int percent = (int) (((float) mProgress / (float) mMax) * 100);  //中间的进度百分比，先转换成float在进行除法运算，不然都为0
        float textWidth = mPaint.measureText(percent + "%");   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间

        if (mTextIsDisplayable && percent != 0 && mStyle == STROKE) {
            canvas.drawText(percent + "%", centre - textWidth / 2, centre + mTextSize / 2, mPaint); //画出进度百分比
        }

        /**
         * 画圆弧 ，画圆环的进度
         */
        //设置进度是实心还是空心
        mPaint.setStrokeWidth(mRoundWidth); //设置圆环的宽度
        mPaint.setColor(mRoundProgressColor);  //设置进度的颜色
        RectF oval = new RectF(centre - radius, centre - radius, centre
                + radius, centre + radius);  //用于定义的圆弧的形状和大小的界限

        switch (mStyle) {
            case STROKE:{
                mPaint.setStyle(Paint.Style.STROKE);
                /*第二个参数是进度开始的角度，-90表示从12点方向开始走进度，如果是0表示从三点钟方向走进度，依次类推
                 *public void drawArc(RectF oval, float startAngle, float sweepAngle, boolean useCenter, Paint paint)
                    oval :指定圆弧的外轮廓矩形区域。
                    startAngle: 圆弧起始角度，单位为度。
                    sweepAngle: 圆弧扫过的角度，顺时针方向，单位为度。
                    useCenter: 如果为True时，在绘制圆弧时将圆心包括在内，通常用来绘制扇形。
                    paint: 绘制圆弧的画板属性，如颜色，是否填充等
                 *
                */
                canvas.drawArc(oval, mStartAngle, 360 * mProgress / mMax, false, mPaint);  //根据进度画圆弧
                break;
            }
            case FILL:{
                mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                if(mProgress !=0)
                    canvas.drawArc(oval, mStartAngle, 360 * mProgress / mMax, true, mPaint);  //根据进度画圆弧
                break;
            }
        }


    }

    public synchronized int getMax() {
        return mMax;
    }

    /**
     * 设置进度的最大值
     * @param max
     */
    public synchronized void setMax(int max) {
        if(max < 0){
            throw new IllegalArgumentException("max not less than 0");
        }
        this.mMax = max;
    }

    /**
     * 获取进度.需要同步
     * @return
     */
    public synchronized int getProgress() {
        return mProgress;
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     * @param progress
     */
    public synchronized void setProgress(int progress) {
        if(progress < 0){
            throw new IllegalArgumentException("progress not less than 0");
        }
        if(progress > mMax){
            progress = mMax;
        }
        if(progress <= mMax){
            this.mProgress = progress;
            postInvalidate();
        }
    }


    public int getCircleColor() {
        return mRoundColor;
    }

    public void setCircleColor(int CircleColor) {
        this.mRoundColor = CircleColor;
    }

    public int getCircleProgressColor() {
        return mRoundProgressColor;
    }

    public void setCircleProgressColor(int CircleProgressColor) {
        this.mRoundProgressColor = CircleProgressColor;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        this.mTextColor = textColor;
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float textSize) {
        this.mTextSize = textSize;
    }

    public float getRoundWidth() {
        return mRoundWidth;
    }

    public void setRoundWidth(float roundWidth) {
        this.mRoundWidth = roundWidth;
    }
}
