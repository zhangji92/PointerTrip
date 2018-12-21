package yc.pointer.trip.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.example.xlhratingbar_lib.XLHRatingBar;
import yc.pointer.trip.R;
import yc.pointer.trip.untils.DensityUtil;
import yc.pointer.trip.untils.StringUtil;

/**
 * Create by 张继
 * 2017/8/17
 * 15:09
 */
public class MyPopWindow extends PopupWindow implements View.OnClickListener, View.OnLayoutChangeListener {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private View mRootView;
    private onCallBackListener mOnCallBackListener;
    private EditText editTextComment;
    private RelativeLayout relativeLayout;
    private TextView textKwame;
    private TextView textSummary;
    boolean flag = true;
    private ImageView cancel;
    private TextView mTextHave;
    private TextView mTextKind;
    private TextView mTextTime;
    private TextView mTextVivid;
    private boolean haveFlag = true;
    private boolean kindFlag = true;
    private boolean timeFlag = true;
    private boolean vividFlag = true;
    private Button bntCommit;
    private XLHRatingBar xlhRatingBar;
    private String strHave;
    private String strKind;
    private String strTime;
    private String strVivid;
    private StringBuffer append;

    public void setOnCallBackListener(onCallBackListener mOnCallBackListener) {
        this.mOnCallBackListener = mOnCallBackListener;
    }

    public MyPopWindow(Context context, final Activity activity) {
        super(context);
        this.mContext = context;
        //打气筒
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //打气
        mRootView = mLayoutInflater.inflate(R.layout.view_popwindow, null);
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
        setBackgroundDrawable(mContext.getResources().getDrawable(R.color.colorTitle));

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
        initListener();//初始化监听
    }

    private void initListener() {
        cancel.setOnClickListener(this);
        mTextHave.setOnClickListener(this);
        mTextKind.setOnClickListener(this);
        mTextTime.setOnClickListener(this);
        mTextVivid.setOnClickListener(this);
        bntCommit.setOnClickListener(this);
    }

    public interface onCallBackListener {
        void onClickRating(int num);
        void onClickCommit(int num, String title, String info);
    }

    /**
     * 初始化
     *
     * @param view 视图
     */
    private void initView(View view) {
        append = new StringBuffer();//字符串拼接
        cancel = (ImageView) view.findViewById(R.id.comment_cancel);//取消Popupwindow
        xlhRatingBar = (XLHRatingBar) view.findViewById(R.id.ratingBar);//星级评分
        textSummary = (TextView) view.findViewById(R.id.comment_summary);
        textKwame = (TextView) view.findViewById(R.id.comment_kwame);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.comment_relative);
        editTextComment = (EditText) view.findViewById(R.id.comment_opinion);//输入框
        mTextHave = (TextView) view.findViewById(R.id.comment_have);
        mTextKind = (TextView) view.findViewById(R.id.comment_kind);
        mTextTime = (TextView) view.findViewById(R.id.comment_time);
        mTextVivid = (TextView) view.findViewById(R.id.comment_vivid);
        bntCommit = (Button) view.findViewById(R.id.comment_commit);//提交
        /**
         * 星级评价
         */
        xlhRatingBar.setOnRatingChangeListener(new XLHRatingBar.OnRatingChangeListener() {
            @Override
            public void onChange(int countSelected) {
                if (mOnCallBackListener!=null){
                    mOnCallBackListener.onClickRating(countSelected);
                }
            }
        });
        //限制字符长度
        editTextComment.setFilters(new InputFilter[]{new InputFilter.LengthFilter(60)});
    }

    /**
     * setText数据
     *
     * @param summary
     * @param have
     * @param kind
     * @param time
     * @param vivid
     */
    public void setTextString(String summary, String have, String kind, String time, String vivid) {
        textSummary.setText(summary);
        mTextHave.setText(have);
        mTextKind.setText(kind);
        mTextTime.setText(time);
        mTextVivid.setText(vivid);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.comment_opinion:
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mRootView.getLayoutParams();
                params.width = LinearLayout.LayoutParams.MATCH_PARENT;
                params.height = DensityUtil.getScreenHeight(mContext) - DensityUtil.getStatusHeight(mContext);
                mRootView.setLayoutParams(params);
                break;
            case R.id.comment_have:
                if (haveFlag) {
                    strHave = mTextHave.getText().toString().trim();
                    mTextHave.setSelected(true);
                    mTextHave.setTextColor(Color.parseColor("#d60337"));
                    haveFlag = false;
                } else {
                    strHave = "";
                    mTextHave.setSelected(false);
                    mTextHave.setTextColor(Color.parseColor("#979696"));
                    haveFlag = true;
                }
                break;
            case R.id.comment_kind:
                if (kindFlag) {
                    strKind = mTextKind.getText().toString().trim();
                    mTextKind.setSelected(true);
                    mTextKind.setTextColor(Color.parseColor("#d60337"));
                    kindFlag = false;
                } else {
                    strKind = "";
                    mTextKind.setSelected(false);
                    mTextKind.setTextColor(Color.parseColor("#979696"));
                    kindFlag = true;
                }
                break;
            case R.id.comment_time:
                if (timeFlag) {
                    strTime = "";
                    mTextTime.setSelected(true);
                    mTextTime.setTextColor(Color.parseColor("#d60337"));
                    timeFlag = false;
                } else {
                    mTextTime.setSelected(false);
                    mTextTime.setTextColor(Color.parseColor("#979696"));
                    timeFlag = true;
                }
                break;
            case R.id.comment_vivid:
                if (vividFlag) {
                    strVivid = mTextVivid.getText().toString().trim();//选中时的数据
                    mTextVivid.setSelected(true);
                    mTextVivid.setTextColor(Color.parseColor("#d60337"));
                    vividFlag = false;
                } else {
                    strVivid = "";//未选中
                    mTextVivid.setSelected(false);
                    mTextVivid.setTextColor(Color.parseColor("#979696"));
                    vividFlag = true;
                }
                break;
            case R.id.comment_cancel:
                dismiss();
                break;
            case R.id.comment_commit:
                int num = xlhRatingBar.getCountSelected();
                String summary=textSummary.getText().toString().trim();
                if (!StringUtil.isEmpty(summary)) {
                    append.append(summary + ",");
                }
                if (!StringUtil.isEmpty(strHave)) {
                    append.append(strHave + ",");
                }
                if (!StringUtil.isEmpty(strKind)) {
                    append.append(strKind + ",");
                }
                if (!StringUtil.isEmpty(strTime)) {
                    append.append(strTime + ",");
                }
                if (!StringUtil.isEmpty(strVivid)) {
                    append.append(strVivid + ",");
                }
                String title = append.toString().trim();
                String info = editTextComment.getText().toString();
                if (mOnCallBackListener != null) {
                    mOnCallBackListener.onClickCommit(num, title, info);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        //现在认为只要控件将Activity向上推的高度超过了1/3屏幕高，就认为软键盘弹起
        if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > 10)) {
        } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > 10)) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mRootView.getLayoutParams();
            params.height = FrameLayout.LayoutParams.WRAP_CONTENT;
            mRootView.setLayoutParams(params);
        }
    }
}
