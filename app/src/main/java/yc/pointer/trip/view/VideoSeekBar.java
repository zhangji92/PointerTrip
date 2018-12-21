package yc.pointer.trip.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import yc.pointer.trip.R;
import yc.pointer.trip.base.PressUpInterface;
import yc.pointer.trip.untils.DensityUtil;

/**
 * Created by 张继
 * 2018/3/13
 * 14:48
 * 公司:天津悦程嘉和网络科技有限公司
 * 描述:
 */

public class VideoSeekBar extends View {

    private PressUpInterface mPressUpInterface;

    public void setPressUpInterface(PressUpInterface pressUpInterface) {
        this.mPressUpInterface = pressUpInterface;
    }

    private Bitmap leftBitmap, rightBitmap;
    /**
     * dip转换px
     */
    private int dip;
    // -- 画笔 --
    /**
     * 绘制缩略图画笔
     */
    private Paint thumbPaint;
    /**
     * 播放进度画笔
     */
    private Paint progressPaint;
    /**
     * 播放进度背景(阴影层)画笔
     */
    private Paint progressBgPaint;
    private float vWidth;
    private boolean isCutMode = true;
    private float xTime = 0;
    private float videoFrame = 0;
    private float vHeight = 0;
    private float width;
    private int mScreenWidth;

    public void setTime(float xTime, float videoFrame, float vHeight, int imgWidth) {
        this.xTime = xTime;
        this.videoFrame = videoFrame;
        this.vHeight = vHeight;
        this.width = imgWidth;
        if (vWidth > imgWidth) {
            this.rightSX = imgWidth;

            postInvalidate();
        } else {
            moveFlag=false;
            rightSX = vWidth - sliderIgWidth;
            postInvalidate();
        }
    }


    public VideoSeekBar(Context context) {
        this(context, null);
    }

    public VideoSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public VideoSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScreenWidth = DensityUtil.getScreenWidth(context) / 5;
        init();
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private boolean flag;

            @Override
            public void onGlobalLayout() {
                vWidth = getWidth();
                if (!flag) {
                    rightSX = vWidth - sliderIgWidth;
                    flag = true;
                }

            }
        });
    }

    /**
     * 初始化操作
     */
    private void init() {
        // 防止不进行绘画 触发onDraw
        setWillNotDraw(false);
        Bitmap bitmapLeft = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.img_clip_left);
        Bitmap bitmapRight = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.img_clip_right);
        // 先保存滑动图片宽度
        sliderIgWidth = bitmapLeft.getWidth();
        // 获取左右两个滑动的图片
        leftBitmap = Bitmap.createScaledBitmap(bitmapLeft, sliderIgWidth, (int) (mScreenWidth*1.2f), true);
        rightBitmap = Bitmap.createScaledBitmap(bitmapRight, sliderIgWidth, (int) (mScreenWidth*1.2f), true);
        // 1 dip 对应的px
        dip = DensityUtil.dipConvertPx(getContext(), 1.0f);
        // 初始化画笔
        initPaint();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        // 初始化画笔
        thumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // 缩略图
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // 播放进度 白色竖直线条
        progressBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // 播放进度背景,半透明(画布遮挡层)
        // 画笔颜色
        progressPaint.setColor(Color.rgb(255, 255, 255)); // 字体颜色 - 白色
        progressBgPaint.setColor(Color.rgb(0, 0, 0)); // 背景进度颜色(画布遮挡层)
        // 设置透明度
        progressBgPaint.setAlpha(60); // 画布遮挡层
        // 设置画笔大小
        progressPaint.setStrokeWidth(dip * 2); // 线条
        // 设置画笔样式
        progressPaint.setStyle(Paint.Style.STROKE); // 设置粗线 - 线条
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        rightMarginX=rightSX;
        // 判断是否裁剪模式, 并且高度不等于0,防止计算出现问题
        if (isCutMode && vHeight != 0) {
            // 计算右边边距值
            reckonRightSX();
            // 绘制左边滑动的X轴位置
            canvas.drawBitmap(leftBitmap, leftSX, 0, thumbPaint);
            // 绘制右边滑动的X轴位置
            canvas.drawBitmap(rightBitmap, rightSX, 0, thumbPaint);
            // === 绘制左边拖动阴影图层 ===
            if (leftSX != 0f) {
                // 绘制一个矩形
                canvas.drawRect(0, 0, leftSX, vHeight, progressBgPaint);
            }


            // === 绘制右边拖动阴影图层 ===
            if (rightSX != rightMarginX && !moveFlag) {
                // 绘制一个矩形
                canvas.drawRect(rightSX, 0, width, vHeight, progressBgPaint);
            }
        }
    }
    // =================================
    /**
     * 右边的边距（间距图片宽度）
     */
    private float rightMarginX = 0f;
    /**
     * 左边滑动的X轴
     */
    private float leftSX = 0f;
    /**
     * 右边滑动的X轴
     */
    private float rightSX = 0f;
    /**
     * 滑动的图片宽度
     */
    private int sliderIgWidth = 0;
    // ===
    /**
     * 上次滑动的值
     */
    private float oTouchX = -1f;
    /**
     * 旧的中间值
     */
    private float lrMiddleX = -1f;
    /**
     * 滑动的View
     */
    private int touchView = -1;
    /**
     * 滑动左边的View
     */
    private final int TOUCH_LEFT_VIEW = 1;
    /**
     * 滑动右边的View
     */
    private final int TOUCH_RIGHT_VIEW = 2;
    /**
     * 滑动左右两边中间空白部分
     */
    private final int TOUCH_MIDST_VIEW = 3;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        // 属于裁剪模式才进行处理
        if (isCutMode) {
            // 滑动中的X轴位置
            float xMove = event.getX();
            // --
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: // 按下时
                    if (mPressUpInterface!=null){
                        mPressUpInterface.press();
                    }
                    // 这样判断是刚好在之间,为了增加触摸体验,增加多一般的边距触摸优化
                    //if (xMove >= leftSX && xMove <= (leftSX + sliderIgWidth))
                    // --
                    if (xMove >= (leftSX - sliderIgWidth / 2) && xMove <= (leftSX + ((float) sliderIgWidth) * 1.5)) {
                        touchView = TOUCH_LEFT_VIEW;
                        // 计算滑动距离
                        reckonSlide(xMove);
                    } else if (xMove >= (rightSX - sliderIgWidth / 2) && xMove <= (rightSX + ((float) sliderIgWidth) * 1.5)) {
                        touchView = TOUCH_RIGHT_VIEW;
                        // 计算滑动距离
                        reckonSlide(xMove);
                    } else if (xMove >= (leftSX + sliderIgWidth) && xMove <= (rightSX + sliderIgWidth)) { // 属于滑动两个View中间模块
                        touchView = TOUCH_MIDST_VIEW;
                        // 计算滑动距离
                        return false;
//                        reckonSlide(xMove);
                    } else {  // 表示都没操作
                        lrMiddleX = oTouchX = touchView = -1;
                    }
                    break;
                case MotionEvent.ACTION_MOVE: // 滑动中
                    // 计算滑动距离
                    reckonSlide(xMove);
                    break;
                case MotionEvent.ACTION_UP: // 抬起时
                    if (mPressUpInterface!=null){
                        mPressUpInterface.up();
                    }
                    lrMiddleX = oTouchX = touchView = -1;
                    break;
            }
        }
        return true;
    }

    /**
     * 计算右边的值
     */
    private void reckonRightSX() {

        if (rightMarginX == 0f) {
            rightMarginX = vWidth - sliderIgWidth;
        }
        if (rightSX == 0f) { // 默认值为0则表示为最尾端
            rightSX = rightMarginX;
        }
    }

    private boolean moveFlag = true;

    /**
     * 计算滑动
     *
     * @param xMove 滑动的X轴
     */
    private void reckonSlide(float xMove) {
        // 计算右边边距值
        reckonRightSX();
        // 如果都不属于滑动,则不处理
        if (!(touchView == TOUCH_LEFT_VIEW || touchView == TOUCH_RIGHT_VIEW
                || touchView == TOUCH_MIDST_VIEW)) {
            return;
        }
        // 转换关键帧相差的X轴位置(关键帧时间 / 每个X轴对应的时间)
        float convX = videoFrame / xTime;
        // 计算间隔宽度(判断是滑动图片宽度大还是关键帧宽度大)
        float spacing = (convX > sliderIgWidth) ? convX : sliderIgWidth;
        // --
        if (touchView == 1) { // 属于滑动左边图片
            // 虚拟位置 = 滑动位置 + 间距宽度
            float vX = xMove + spacing;
            // 判断是否滑动会推动到右边
            if (vX > rightSX) {
                // 如果已经给推到边缘了,则进行控制
                if (rightSX >= rightMarginX) {
                    // 设置右边到边缘
                    rightSX = rightMarginX;
                    // 左边 = 右边 - 间距宽度(防止重叠)
                    leftSX = rightSX - spacing;
                } else { // 如果不在边缘,则进行推
                    leftSX = xMove;
                    rightSX = xMove + spacing;
                }
            } else { // 如果小于则表示没有触碰到
                leftSX = xMove;
                // 如果边距小于一半则滑动到底部 + 3分之1的边距
                if (xMove <= sliderIgWidth / 2 + sliderIgWidth / 3) {
                    leftSX = 0f;
                }
            }
            // 最后再进行判断多一次,防止出现意外（快速滑动）
            adjustLoc(TOUCH_LEFT_VIEW, spacing);
        } else if (touchView == 2) { // 属于滑动右边图片
            //如果不是满屏的时候设置右边边距
            if (xMove >= rightSX && moveFlag) {
                rightMarginX = width;
                moveFlag = false;
            } else
                // 判断是否滑动到边缘(右侧边缘)
                if (xMove >= rightMarginX) { // 滑动到边缘则直接设置边缘
                    rightSX = rightMarginX;
                } else {
//                moveFlag=true;
                    // 判断是否触碰到左边 -> 滑动的距离 - 左边的位置 > 边距，表示没触碰
                    if (xMove - leftSX > spacing) {
                        rightSX = xMove;
                    } else { // 如果触碰了
                        if (leftSX <= 0) { // 如果左边已经到了边缘
                            // 设置左边到边缘
                            leftSX = 0f;
                            // 右边 = 间距宽度
                            rightSX = spacing;
                        } else { // 左边没到边缘,则进行推
                            rightSX = xMove;
                            leftSX = rightSX - spacing;
                        }
                    }
                }
            // 最后再进行判断多一次,防止出现意外（快速滑动）
            adjustLoc(TOUCH_RIGHT_VIEW, spacing);
        } else if (touchView == 3) { // 属于滑动两个View 中间空白的
            // 左右两个的间隔 = 右边减去左边（左边坐标 + 图片宽度）
            float lrSpace = rightSX - leftSX;
            if (lrMiddleX == -1f) {
                // 获取中间值
                lrMiddleX = lrSpace;
            }
            // 判断滑动方向
            if (oTouchX == -1f) {
                // 记录上次的滑动值
                oTouchX = xMove;
                return;
            }
            if (lrMiddleX > 0) {
                // 判断左边是否已经到达最右边
                if (rightSX > rightMarginX) { // 如果已经给推到边缘了,则进行控制
                    adjustLoc(TOUCH_MIDST_VIEW, lrMiddleX); // 调整位置
                } else if (leftSX < 0) { // 如果左边的距离等于0
                    adjustLoc(TOUCH_MIDST_VIEW, lrMiddleX); // 调整位置
                } else { // 同步位移
                    // 判断滑动方向
                    if (xMove > oTouchX) { // 往右边滑动
                        if (rightSX < rightMarginX) {
                            rightSX = rightSX + (xMove - oTouchX);
                            // --
                            leftSX = rightSX - lrMiddleX;
                        }
                    } else if (xMove < oTouchX) { // 往左边滑动
                        if (leftSX > 0) {
                            leftSX = leftSX - (oTouchX - xMove);
                            // --
                            rightSX = leftSX + lrMiddleX;
                        }
                    }
                    // 记录上次的滑动值
                    oTouchX = xMove;
                    // 调整位置
                    adjustLoc(TOUCH_MIDST_VIEW, lrMiddleX);
                }
            }
        }
        // 进行绘制
        invalidate();
    }

    /**
     * 调整位置(防止左右超出边缘边距)
     *
     * @param touchView 滑动的View
     * @param spacing   两个View间隔的边距
     */
    private void adjustLoc(int touchView, float spacing) {
        // 判断左边是否到达边缘
        if (leftSX <= 0) {
            // 设置左边到边缘
            leftSX = 0f;
            // 右边 = 间距宽度
            float tRightSX = spacing;
            // 判断当前位置是否大于边距
            if (rightSX < tRightSX) {
                rightSX = tRightSX;
            }
        }
        // --
        // 判断右边是否到达边缘
        if (rightSX >= rightMarginX) {
            // 设置右边到边缘
            rightSX = rightMarginX;
            // 左边 = 右边 - 间距宽度(防止重叠)
            float tLeftSX = rightSX - spacing;
            // 判断当前位置是否大于计算出来的位置
            if (leftSX > tLeftSX) {
                leftSX = tLeftSX;
            }
        }
    }

    /**
     * 获取开始时间(左边X轴转换时间) - 毫秒
     *
     * @return
     */
    public float getStartTime() {
        if (xTime != -1) {
            return leftSX * xTime;
        }
        return -1f;
    }

    /**
     * 获取结束时间(右边X轴转换时间) - 毫秒
     *
     * @return
     */
    public float getEndTime() {
        if (xTime != -1) {
            return rightSX * xTime;
        }
        return -1f;
    }
}
