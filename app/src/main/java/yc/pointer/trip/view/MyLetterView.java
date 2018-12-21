package yc.pointer.trip.view;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Administrator on 2017/1/10.
 * 自定义字母控件
 */
public class MyLetterView extends View {

    private int arcHeight;
    private int singleHeight;
    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;

    private String[] letter = {"定", "最", "热", "全", "A", "B", "C", "D",
            "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q",
            "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
//    private String[] letter = {"定","近"};

    private int choose = -1;//用于标记当前所选中的位置
    private Paint mTypefacePaint;//字体画笔
    private Paint mBackPaint;//背景画笔


    private Boolean showBkg = false;//用于区分是否显示view的背景

    public MyLetterView(Context context) {
        this(context, null);
    }

    public MyLetterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyLetterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTypefacePaint = new Paint();//获取画笔的实例对象
        mBackPaint = new Paint();//背景画笔的实例
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int height = getHeight();//高度
        int width = getWidth();//宽度

        arcHeight = width / 2;

        //画上半圆
        mBackPaint.setColor(Color.parseColor("#0ed3f7"));//画笔颜色
        mBackPaint.setAntiAlias(true);//去掉锯齿
        mBackPaint.setStyle(Paint.Style.FILL);//填满
        RectF rectFUpper = new RectF(arcHeight, 0, width, arcHeight);
        canvas.drawArc(rectFUpper, 180, 180, true, mBackPaint);

        //画矩形
        mBackPaint.setColor(Color.parseColor("#0ed3f7"));//画笔颜色
        mBackPaint.setAntiAlias(true);//去掉锯齿
        mBackPaint.setStyle(Paint.Style.FILL);//填满
        canvas.drawRect(arcHeight, arcHeight / 2, width, height - arcHeight / 2, mBackPaint);
        //画白点
        mBackPaint.setColor(Color.parseColor("#ffffff"));
        mBackPaint.setAntiAlias(true);//去掉锯齿
        mBackPaint.setStyle(Paint.Style.FILL);//填满
        int circleWidth = width - width / 4;//靠右居中 2代表半径
        canvas.drawCircle(circleWidth, arcHeight / 2, 6, mBackPaint);

        //画下半圆
        mBackPaint.setColor(Color.parseColor("#0ed3f7"));//画笔颜色
        mBackPaint.setAntiAlias(true);//去掉锯齿
        mBackPaint.setStyle(Paint.Style.FILL);//填满
        RectF rectFLower = new RectF(arcHeight, height - arcHeight, width, height);
        canvas.drawArc(rectFLower, 360, 180, true, mBackPaint);


        //计算字符的高度
        singleHeight = (height - arcHeight) / letter.length;

        //遍历字符
        for (int i = 0; i < letter.length; i++) {

            int left = arcHeight;
            int top = arcHeight + singleHeight * i;
            int right = width;
            int bottom = arcHeight / 2 + singleHeight * (i + 1);
            Rect targetRect = new Rect(left, top, right, bottom);
            mTypefacePaint.setTextSize(26);
            Paint.FontMetricsInt fontMetrics = mTypefacePaint.getFontMetricsInt();
            // 转载请注明出处：http://blog.csdn.net/hursing
            int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
            // 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
            mTypefacePaint.setTextAlign(Paint.Align.CENTER);
            mTypefacePaint.setColor(Color.parseColor("#FFFFFF"));
            mTypefacePaint.setAntiAlias(true);
            mTypefacePaint.setStyle(Paint.Style.FILL);
            mTypefacePaint.setTypeface(Typeface.DEFAULT_BOLD);
            //判断是不是我们按下的当前字母
            if (choose == i) {
                //绘制文字圆形背景
                mTypefacePaint.setColor(Color.parseColor("#0ed3f7"));
                mBackPaint.setColor(Color.parseColor("#ffffff"));
                mBackPaint.setStyle(Paint.Style.FILL);
                mBackPaint.setAntiAlias(true);
                canvas.drawCircle(targetRect.centerX(), baseline - singleHeight / 4.7f, singleHeight / 2.5f, mBackPaint);
            }
            canvas.drawText(letter[i], targetRect.centerX(), baseline, mTypefacePaint);
            mTypefacePaint.reset();//重置画笔
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();
        final int oldChoose = choose;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        final int c = (int) ((y - arcHeight / 2) / singleHeight);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                showBkg = true;
                if (oldChoose != c && listener != null) {
                    if (c >= 0 && c < letter.length) {
                        listener.onTouchingLetterChanged(letter[c]);
                        choose = c;
                        invalidate();
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                showBkg = false;
                choose = -1;
                invalidate();
                break;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    public interface OnTouchingLetterChangedListener {
        void onTouchingLetterChanged(String s);
    }

}
