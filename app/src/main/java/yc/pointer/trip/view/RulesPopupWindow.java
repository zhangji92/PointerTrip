package yc.pointer.trip.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import yc.pointer.trip.R;
import yc.pointer.trip.bean.CityMsg;
import yc.pointer.trip.untils.AppFileMgr;
import yc.pointer.trip.untils.StringUtil;

/**
 * Create by 张继
 * 2017/8/17
 * 15:09
 * 出行规则选择器
 */
public class RulesPopupWindow extends PopupWindow implements View.OnClickListener {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private View mRootView;
    private onCallBackListener mOnCallBackListener;
    private WheelView mainWheelView;
    private WheelView subWheelView;
    private Gson gson;
    private CityMsg cityMsg;


    public RulesPopupWindow(Context context, final Activity activity, onCallBackListener callBackListener) {
        super(context);
        this.mContext = context;
        this.mOnCallBackListener = callBackListener;
        gson = new Gson();
        //打气筒
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //打气
        mRootView = mLayoutInflater.inflate(R.layout.view_rules, null);
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
    }


    /**
     * 初始化
     *
     * @param view 视图
     */
    private void initView(View view) {
        TextView textCancel = (TextView) view.findViewById(R.id.city_cancel);
        textCancel.setOnClickListener(this);
        TextView textSure = (TextView) view.findViewById(R.id.city_sure);
        textSure.setOnClickListener(this);

        String assets = AppFileMgr.readAssets(mContext, "valuation.json");
        cityMsg = gson.fromJson(assets, CityMsg.class);
        mainWheelView = (WheelView) view.findViewById(R.id.wheel_main);
        mainWheelView.setWheelAdapter(new ArrayWheelAdapter(mContext));
        mainWheelView.setSkin(WheelView.Skin.None);
        mainWheelView.setWheelData(createMainDatas());

        WheelView.WheelViewStyle style = new WheelView.WheelViewStyle();
        style.selectedTextSize = 20;
        style.textSize = 16;
        style.selectedTextColor = mContext.getResources().getColor(R.color.home_bar);
        mainWheelView.setStyle(style);
        subWheelView = (WheelView) view.findViewById(R.id.wheel_sub);
        subWheelView.setWheelAdapter(new ArrayWheelAdapter(mContext));
        subWheelView.setSkin(WheelView.Skin.None);
        subWheelView.setWheelData(createSubDatas().get(createMainDatas().get(mainWheelView.getSelection())));
        subWheelView.setStyle(style);
        mainWheelView.join(subWheelView);
        mainWheelView.joinDatas(createSubDatas());
    }

    /**
     * 所有省级名称
     *
     * @return
     */
    private List<String> createMainDatas() {
        List<String> list = new ArrayList<>();
        for (CityMsg.CitylistBean city : cityMsg.getCitylist()) {
            list.add(city.getP());
        }
        return list;
    }

    /**
     * 省级一下的城市名称
     *
     * @return
     */
    private HashMap<String, List<String>> createSubDatas() {
        HashMap<String, List<String>> map = new HashMap<String, List<String>>();
        for (int i = 0; i < createMainDatas().size(); i++) {
            String[][] ss = new String[createMainDatas().size()][cityMsg.getCitylist().get(i).getC().size()];
            for (int j = 0; j < cityMsg.getCitylist().get(i).getC().size(); j++) {
                String[] s1 = new String[cityMsg.getCitylist().get(i).getC().size()];
                s1[j] = cityMsg.getCitylist().get(i).getC().get(j).getN();
                ss[i][j] = s1[j];
            }
            map.put(createMainDatas().get(i), Arrays.asList(ss[i]));
        }
        return map;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.city_cancel:
                if (mOnCallBackListener != null) {
                    mOnCallBackListener.callBackString("", 0);
                }
                dismiss();
                break;
            case R.id.city_sure:
                String main= (String) mainWheelView.getSelectionItem();
                String sub= (String) subWheelView.getSelectionItem();


                if (!StringUtil.isEmpty(sub)){
                    if (sub.contains("天")){
                        mOnCallBackListener.callBackString(sub, 2);
                    }else {
                        mOnCallBackListener.callBackString(sub, 1);
                    }
                }
                dismiss();
                break;
            default:
                break;
        }
    }

    public interface onCallBackListener {

            /**
             *
             * @param msg msg
             * @param status 0代表小时 1代表天 2代表取消
             */
            void callBackString(String msg, int status);

    }

}
