package yc.pointer.trip.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import yc.pointer.trip.R;
import yc.pointer.trip.untils.StringUtil;

/**
 * Created by 张继
 * 2018/7/3
 * 10:30
 * 公司：
 * 描述：日历 Popup Window
 */

public class CalendarPopup extends PopupWindow implements View.OnClickListener {

    private final LayoutInflater mLayoutInflater;
    private final View mRootView;
    private final Context mContext;
    private MonthDateView mMonthDate;


    public CalendarPopup(Context context, final Activity activity, CalendarCallBack personCallBack) {
        super(context);
        this.mContext = context;
        this.mCalendarCallBack = personCallBack;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = mLayoutInflater.inflate(R.layout.popup_calendar, null);
        //设置View
        setContentView(mRootView);
        //设置宽与高
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        /**
         * 设置进出动画
         */
        setAnimationStyle(R.style.main_menu_animstyle);

        /**
         * 设置背景只有设置了这个才可以点击外边和BACK消失
         */
        setBackgroundDrawable(context.getResources().getDrawable(R.color.colorTitle));

        /**
         * 设置可以获取集点
         */
        setFocusable(true);
        /**
         * 设置点击外边可以消失
         */
        setOutsideTouchable(true);
        /**
         *设置可以触摸
         */
        setTouchable(true);
        /**
         * 设置点击外部可以消失
         */
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /**
                 * 判断是不是点击了外部
                 */
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    return true;
                }
                //不是点击外部
                return false;
            }
        });
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams layoutParams = activity.getWindow().getAttributes();
                layoutParams.alpha = 1.0f;
                activity.getWindow().setAttributes(layoutParams);
            }
        });
        /**
         * 初始化View与监听器
         */
        initView(mRootView);
    }

    /**
     * @param view
     */
    private void initView(View view) {
        ImageView mImgLeft = view.findViewById(R.id.iv_left);
        mImgLeft.setOnClickListener(this);
        ImageView mImgRight = view.findViewById(R.id.iv_right);
        mImgRight.setOnClickListener(this);
        TextView mTextWeek = view.findViewById(R.id.week_text);
        TextView mToady = view.findViewById(R.id.tv_today);
        mToady.setOnClickListener(this);
        TextView mTextDate = view.findViewById(R.id.date_text);
        mMonthDate = view.findViewById(R.id.monthDateView);
        mMonthDate.setTextView(mTextDate, mTextWeek);

        TextView mTextSure = view.findViewById(R.id.pop_person_sure);
        mTextSure.setOnClickListener(this);
        TextView mTextCancel = view.findViewById(R.id.pop_person_cancel);
        mTextCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                mMonthDate.onLeftClick();
                break;
            case R.id.iv_right:
                mMonthDate.onRightClick();
                break;
            case R.id.tv_today:
                mMonthDate.setTodayToView();
                break;
            case R.id.pop_person_cancel:
                dismiss();
                break;
            case R.id.pop_person_sure:
                int day = mMonthDate.getmSelDay();
                int month = mMonthDate.getmSelMonth();
                int year = mMonthDate.getmSelYear();
                //获取系统的日期
                //年
                Calendar curr = Calendar.getInstance();
                curr.set(Calendar.YEAR, curr.get(Calendar.YEAR) + 1);
                Date date = curr.getTime();
                long time = date.getTime();
                if (day != 0) {
                    String tripDate = String.valueOf(year) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(day);
                    String newTripDate = StringUtil.dateTimeFormat(tripDate);
                    long strTimeDateTom = StringUtil.getStrTimeDateTom(newTripDate);
                    if (time > strTimeDateTom) {
                        if (StringUtil.isEffect(newTripDate)) {
                            if (mCalendarCallBack != null) {
                                mCalendarCallBack.calendarCallBack(newTripDate);
                                dismiss();
                            }
                        } else {
                            Toast.makeText(mContext, "选择日期已过期，请重新选择", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mContext, "出行规划不能超过一年", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, "您未选择出行日期", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    CalendarCallBack mCalendarCallBack;

    public interface CalendarCallBack {
        void calendarCallBack(String num);
    }
}
