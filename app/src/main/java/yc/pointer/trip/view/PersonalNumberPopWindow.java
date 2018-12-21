package yc.pointer.trip.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import java.util.ArrayList;
import java.util.List;

import yc.pointer.trip.R;
import yc.pointer.trip.untils.StringUtil;

/**
 * Created by 张继
 * 2018/7/2
 * 15:48
 * 公司：
 * 描述：选择人数
 */

public class PersonalNumberPopWindow extends PopupWindow implements View.OnClickListener {


    private final LayoutInflater mLayoutInflater;
    private final View mRootView;
    private final Context mContext;
    private List<String> mList;
    private WheelView mainWheelView;

    public PersonalNumberPopWindow(Context context, final Activity activity,PersonCallBack personCallBack) {
        super(context);
        mList=new ArrayList<>();
        for (int i = 1; i <7 ; i++) {
            mList.add(i+"人");
        }

        this.mContext =context;
        this.mPersonCallBack=personCallBack;
        //打气筒
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //打气
        mRootView = mLayoutInflater.inflate(R.layout.pop_person_num, null);
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

    private void initView(View view) {
        TextView mTextCancel = view.findViewById(R.id.pop_person_cancel);
        mTextCancel.setOnClickListener(this);
        TextView mTextSure = view.findViewById(R.id.pop_person_sure);
        mTextSure.setOnClickListener(this);
        mainWheelView = (WheelView) view.findViewById(R.id.pop_person_wheel);
        WheelView.WheelViewStyle style = new WheelView.WheelViewStyle();
        style.selectedTextSize = 20;
        style.textSize = 16;
        style.selectedTextColor=mContext.getResources().getColor(R.color.home_bar);
        mainWheelView.setStyle(style);
        mainWheelView.setWheelAdapter(new ArrayWheelAdapter(mContext)); // 文本数据源
        mainWheelView.setSkin(WheelView.Skin.None); // common皮肤
        mainWheelView.setWheelData(mList);  // 数据集合

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pop_person_cancel:
                dismiss();
                break;
            case R.id.pop_person_sure:
                String item = (String) mainWheelView.getSelectionItem();
                if (!StringUtil.isEmpty(item)){
                    if (mPersonCallBack!=null){
                        mPersonCallBack.personalCall(item);
                    }
                }else {
                    Toast.makeText(mContext, "人数不能为空", Toast.LENGTH_SHORT).show();
                }
                dismiss();
                break;
        }
    }

    PersonCallBack mPersonCallBack;
    public interface PersonCallBack{
        void personalCall(String num);
    }
}
