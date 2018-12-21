package yc.pointer.trip.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.widget.*;
import android.util.Log;
import android.view.View;
import android.widget.*;

import butterknife.BindView;
import okhttp3.FormBody;
import okhttp3.RequestBody;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import yc.pointer.trip.R;
import yc.pointer.trip.activity.*;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseFragment;
import yc.pointer.trip.bean.BookReserveBean;
import yc.pointer.trip.bean.GoTravelMesgBean;
import yc.pointer.trip.bean.GoTravelPriceBean;
import yc.pointer.trip.bean.eventbean.ClearBean;
import yc.pointer.trip.event.BookReserveEvent;
import yc.pointer.trip.event.GoTravelEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.*;
import yc.pointer.trip.view.DialogKnow;
import yc.pointer.trip.view.MonthDateView;
import yc.pointer.trip.view.ToolbarWrapper;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 出发吧fragment
 */
public class GoTravelFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    @BindView(R.id.standard_toolbar)
    android.support.v7.widget.Toolbar standardToolbar;

    @BindView(R.id.scroll_view)
    ScrollView mScrollView;
    //    @BindView(R.id.layout_unlogin)
//    LinearLayout mUnlogin;
//    @BindView(R.id.go_login)
//    Button goLogin;//立即登录按钮
    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.tv_today)
    TextView tvToday;
    @BindView(R.id.location_city)
    EditText locationCity;//出发城市
    @BindView(R.id.date_text)
    TextView dateText;
    @BindView(R.id.week_text)
    TextView weekText;
    @BindView(R.id.monthDateView)
    MonthDateView monthDateView;
    @BindView(R.id.check_hour)
    CheckBox checkHour;//按小时
    @BindView(R.id.time_hour)
    EditText timeHour;//按小时输入
    @BindView(R.id.tv_by_hours)
    TextView tvByHours;//按小时单位
    @BindView(R.id.check_day)
    CheckBox checkDay;//按天
    @BindView(R.id.by_day_price)
    EditText byDayPrice;//按天输入
    @BindView(R.id.tv_by_day)
    TextView tvByDay;//按天单位
    //    @BindView(R.id.check_pickup_air)
//    CheckBox checkPickupAir;//选择接站属性接机
//    @BindView(R.id.pickup_plane)
//    TextView pickupPlane;//接机
//    @BindView(R.id.pickup_plane_price)
//    TextView pickupPlanePrice;//接机价格
//    @BindView(R.id.check_pickup_train)
//    CheckBox checkPickupTrain;//选择接站属性接火车
//    @BindView(R.id.pickup_train)
//    TextView pickupTrain;//接火车
//    @BindView(R.id.pickup_train_price)
//    TextView pickupTrainPrice;//接火车价钱
    @BindView(R.id.check_guide)
    CheckBox checkGuide;//选择需要导游证
    @BindView(R.id.need_guide_price)
    TextView needGuidePrice;//需要导游证的价格
    @BindView(R.id.check_unguide)
    CheckBox checkUnguide;//选择不需要导游证
    @BindView(R.id.unneed_guide_price)
    TextView unneedGuidePrice;//不需要导游证的价格
    @BindView(R.id.start_order)
    Button startOrder;//完成
    @BindView(R.id.choose_destination)
    TextView chooseDestination;
    @BindView(R.id.choose_scenic)
    TextView chooseScenic;
    @BindView(R.id.travel_count)
    EditText travelCount;
    @BindView(R.id.check_rules)
    CheckBox checkRules;//判断是否阅读规则

    @BindView(R.id.started_time)
    EditText startedTime;
    @BindView(R.id.pickup_time)
    RelativeLayout pickupTime;
    @BindView(R.id.relative_sex)
    RelativeLayout chooseSex;//选择性别
    @BindView(R.id.line_sex)
    View lineSex;//
    @BindView(R.id.check_pickup_sex)
    CheckBox checkSex;
    @BindView(R.id.check_sex_man)
    RadioButton checkSexMan;
    @BindView(R.id.check_sex_women)
    RadioButton checkSexWomen;
    @BindView(R.id.liner_sex)
    RadioGroup sex;
    @BindView(R.id.pickup_line)
    View pickupLine;
    @BindView(R.id.rules)
    TextView rules;//规则
    @BindView(R.id.specific)
    EditText specificDemand;//具体要求
    private String mDevcode;//设备识别码
    private long mTimestamp;//时间戳
    private String userID;//用户ID
    private boolean judgeTimeDev;
    private String cityId;
    private String pickupSex;//接单人性别
    private boolean islogin;//判断是否为登录状态
    private String nowTime = "";
    private int month;
    private String travelDateTime = "";//出行日期
    private String travelTime = "";//游玩时长
    private String guidePrice = "";//导游价钱
    private String traveltype = "1";//计价方式 1：按小时 2：按天
    //    private String pickupType = "";//接待方式 1：接机 2：按站
    private String isNeedGuide = "";//是否需要导游证 1:x需要  2：不需要
    private String noGuideByHourPrice = "";//无导游证按小时价格
    private String GuideByHourPrice = "";//有导游证按小时价格
    private String noGuideByDayPrice = "";//无导游证按天价格
    private String GuideByDayPrice = "";//有导游证按天价格
    //    private String PickupAir = "";//接机价格
//    private String PickupTrain = "";//接站价格
//    private String pickupPrice = "";//接待价格
//    private String startTime;
    private String currentCity;
    private String destinCity;
    private String scenic;
    private String travelperson;
    private String demand;
    private static GoTravelMesgBean goTravelMesgBean;
    private String newTripDate;
    private int day;
    private String isReaded;

    private String bid;//判断是否从预约跳转至发单
    private String is_verify;

    public void setFlag(String bid) {
        this.bid = bid;
    }

    private final int CAMERA_REQUEST_CODE = 1;
//    private LocationUtil locationUtil;

    public static GoTravelMesgBean getGoTravelMesgBean() {
        return goTravelMesgBean;
    }


    @Override
    protected int getContentViewLayout() {
        return R.layout.fragment_go_travel;
    }

    @Override
    protected void initView() {


        View decorView = getActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        DensityUtil.setStatusBarColor(getActivity(), R.color.colorTitle);

        int statusBarHeight = StatusBarUtils.getStatusBarHeight(getActivity());
        standardToolbar.setPadding(0, statusBarHeight, 0, 0);


        ToolbarWrapper toolbarWrapper = new ToolbarWrapper(this);
        toolbarWrapper.setCustomTitle(R.string.go_title);
        toolbarWrapper.setShowBack(false);

        ////注册广播
        //IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction("clear");
        //getActivity().registerReceiver(new MyBroadcastReciver(), intentFilter);

//        toolbarWrapper.setShowTitle(true);
        islogin = ((MyApplication) getActivity().getApplication()).isIslogin();
        mDevcode = ((MyApplication) getActivity().getApplication()).getDevcode();
        mTimestamp = ((MyApplication) getActivity().getApplication()).getTimestamp();
        //判断是否获取时间戳和设备识别码
        judgeTimeDev = APPUtils.judgeTimeDev(getActivity(), mDevcode, mTimestamp);
        //获取定位城市
        String city = ((MyApplication) getActivity().getApplication()).getLocationCity();
        if (islogin) {
//            mScrollView.setVisibility(View.VISIBLE);
//            mUnlogin.setVisibility(View.GONE);
            //判断是否已阅读发单规则
            isReaded = ((MyApplication) getActivity().getApplication()).getUserBean().getIs_mz();
            userID = ((MyApplication) getActivity().getApplication()).getUserId();
            if (!StringUtil.isEmpty(isReaded) && isReaded.equals("1")) {
                checkRules.setChecked(true);
            } else {
                checkRules.setChecked(false);
            }
            if (!StringUtil.isEmpty(bid) && !bid.equals("0")) {
                // TODO 请求被预约人信息
                getReserveMsg(bid);
                chooseSex.setVisibility(View.GONE);
                lineSex.setVisibility(View.GONE);
                checkSex.setClickable(false);
            } else {
                bid = "0";
                chooseSex.setVisibility(View.VISIBLE);
                lineSex.setVisibility(View.VISIBLE);
            }
        }
//        else {
//            mScrollView.setVisibility(View.GONE);
//            mUnlogin.setVisibility(View.VISIBLE);
//        }

//        locationUtil = new LocationUtil();
//        locationUtil.initLocation(getActivity());

        if (!StringUtil.isEmpty(city)) {
            locationCity.setText(city);
        } else {
            locationCity.setHint("出发城市");
        }
        // 默认出发日期为选中日期
        getDate();
        monthDateView.setTextView(dateText, weekText);
        monthDateView.setDateClick(new MonthDateView.DateClick() {
            @Override
            public void onClickOnDate() {
                getDate();
            }
        });
        checkHour.setChecked(true);
        checkRules.setClickable(false);
//        checkPickupAir.setChecked(false);
        byDayPrice.setVisibility(View.GONE);
        tvByDay.setVisibility(View.GONE);
//        pickupPlanePrice.setVisibility(View.GONE);
//        pickupTrainPrice.setVisibility(View.GONE);
//        checkPickupTrain.setChecked(false);
        checkGuide.setChecked(false);
        checkUnguide.setChecked(false);
        needGuidePrice.setVisibility(View.GONE);
//绑定各种监听
        listioner();
    }

    /**
     * 根据日期获取价格
     */
    private void getDate() {
        day = monthDateView.getmSelDay();
        month = monthDateView.getmSelMonth();
        int year = monthDateView.getmSelYear();
        if (day != 0) {
            String tripDate = String.valueOf(year) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(day);
            newTripDate = StringUtil.dateTimeFormat(tripDate);
            if (StringUtil.isEffect(newTripDate)) {
//                Toast.makeText(getActivity(), newTripDate, Toast.LENGTH_SHORT).show();
                travelDateTime = newTripDate;
                if (!nowTime.equals(String.valueOf(month + 1))) {
                    //获取价格信息
                    getPriceMsgForNet(String.valueOf(month + 1));
                }
            } else {
                travelDateTime = "";
                Toast.makeText(getActivity(), "选择日期已过期，请重新选择", Toast.LENGTH_SHORT).show();
            }
        } else {
            travelDateTime = "";
            Toast.makeText(getActivity(), "您未选择出行日期", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 各种监听
     */
    private void listioner() {
        //日历的点击监听事件
        setOnlistener();
        //checkBox点击监听事件
        checkHour.setOnCheckedChangeListener(this);
        checkDay.setOnCheckedChangeListener(this);
//        checkPickupAir.setOnCheckedChangeListener(this);
//        checkPickupTrain.setOnCheckedChangeListener(this);
        checkGuide.setOnCheckedChangeListener(this);
        checkUnguide.setOnCheckedChangeListener(this);
        checkSex.setOnCheckedChangeListener(this);
        checkRules.setOnCheckedChangeListener(this);

        //城市、景点、完成按钮的点击事件
        startOrder.setOnClickListener(this);
        chooseDestination.setOnClickListener(this);
        chooseScenic.setOnClickListener(this);
//        goLogin.setOnClickListener(this);
        rules.setOnClickListener(this);
    }

//    @TargetApi(23)
//    private void requestPermission() {
//        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
//                != PackageManager.PERMISSION_GRANTED) {
//            // 第一次请求权限时，用户如果拒绝，下一次请求shouldShowRequestPermissionRationale()返回true
//            // 向用户解释为什么需要这个权限
//            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
//                new AlertDialog.Builder(getActivity())
//                        .setMessage("申请定位权限")
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                //申请定位权限
//                                ActivityCompat.requestPermissions(getActivity(),
//                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, CAMERA_REQUEST_CODE);
//                            }
//                        })
//                        .show();
//            } else {
//                //申请相机权限
//                ActivityCompat.requestPermissions(getActivity(),
//                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, CAMERA_REQUEST_CODE);
//            }
//        } else {
////            tvPermissionStatus.setTextColor(Color.GREEN);
////            tvPermissionStatus.setText("相机权限已申请");
//            locationUtil.setmICallLocation(new LocationUtil.ICallLocation() {
//                @Override
//                public void locationMsg(AMapLocation aMapLocation) {
//                    if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
//                        String gdCity = aMapLocation.getCity();
//                        ((MyApplication)getActivity().getApplication()).setLocationCity(gdCity);
//                        locationCity.setText(gdCity);
//                    }
//                }
//            });
//
//        }
//    }
//
//    @TargetApi(23)
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == CAMERA_REQUEST_CODE) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
////                tvPermissionStatus.setTextColor(Color.GREEN);
////                tvPermissionStatus.setText("相机权限已申请");
//                locationUtil.setmICallLocation(new LocationUtil.ICallLocation() {
//                    @Override
//                    public void locationMsg(AMapLocation aMapLocation) {
//                        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
//                            String gdCity = aMapLocation.getCity();
//                            ((MyApplication)getActivity().getApplication()).setLocationCity(gdCity);
//                            locationCity.setText(gdCity);
//                        }
//                    }
//                });
//            } else {
//                //用户勾选了不再询问
//                //提示用户手动打开权限
//                if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)){
//                    Toast.makeText(getActivity(), "定位权限已被禁止", Toast.LENGTH_SHORT).show();
//                    locationCity.setHint("出发城市");
//                }
//            }
//        }
//    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

    /**
     * 日历点击事件
     */
    private void setOnlistener() {
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthDateView.onLeftClick();
                getDate();
                TimerTask task = new TimerTask() {
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (checkHour.isChecked()) {
                                    needGuidePrice.setText(GuideByHourPrice);
                                } else if (checkDay.isChecked()) {
                                    needGuidePrice.setText(GuideByDayPrice);
                                }
                                if (checkHour.isChecked()) {
                                    unneedGuidePrice.setText(noGuideByHourPrice);
                                } else if (checkDay.isChecked()) {
                                    unneedGuidePrice.setText(noGuideByDayPrice);
                                }
                            }
                        });
                    }

                };
                Timer timer = new Timer();
                timer.schedule(task, 500);

            }
        });

        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthDateView.onRightClick();
                getDate();
                TimerTask task = new TimerTask() {
                    public void run() {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (checkHour.isChecked()) {
                                    needGuidePrice.setText(GuideByHourPrice);
                                } else if (checkDay.isChecked()) {
                                    needGuidePrice.setText(GuideByDayPrice);
                                }
                                if (checkHour.isChecked()) {
                                    unneedGuidePrice.setText(noGuideByHourPrice);
                                } else if (checkDay.isChecked()) {
                                    unneedGuidePrice.setText(noGuideByDayPrice);
                                }
                            }
                        });
                    }

                };
                Timer timer = new Timer();
                timer.schedule(task, 500);

            }
        });

        tvToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthDateView.setTodayToView();
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.check_hour://按小时
                if (isChecked) {
                    checkDay.setChecked(false);
                    timeHour.setVisibility(View.VISIBLE);
                    tvByHours.setVisibility(View.VISIBLE);
                    byDayPrice.setVisibility(View.GONE);
                    tvByDay.setVisibility(View.GONE);
                    checkHour.setClickable(false);
                    traveltype = "1";
                    if (checkGuide.isChecked()) {
                        needGuidePrice.setText(GuideByHourPrice);
                    } else {
                        unneedGuidePrice.setText(noGuideByHourPrice);
                    }
                } else {
                    timeHour.setVisibility(View.GONE);
                    tvByHours.setVisibility(View.GONE);
                    checkHour.setClickable(true);
                }
                break;
            case R.id.check_day://按天
                if (isChecked) {
                    checkHour.setChecked(false);
                    timeHour.setVisibility(View.GONE);
                    tvByHours.setVisibility(View.GONE);
                    byDayPrice.setVisibility(View.VISIBLE);
                    tvByDay.setVisibility(View.VISIBLE);
                    checkDay.setClickable(false);
                    traveltype = "2";
                    if (checkGuide.isChecked()) {
                        needGuidePrice.setText(GuideByDayPrice);
                    } else {
                        unneedGuidePrice.setText(noGuideByDayPrice);
                    }
                } else {
                    byDayPrice.setVisibility(View.GONE);
                    tvByDay.setVisibility(View.GONE);
                    checkDay.setClickable(true);
                }
                break;
//            case R.id.check_pickup_air://接机
//                if (isChecked) {
//                    checkPickupTrain.setChecked(false);
//                    if (!StringUtil.isEmpty(PickupAir)) {
//                        pickupPlanePrice.setVisibility(View.VISIBLE);
//                        pickupLine.setVisibility(View.VISIBLE);
//                        pickupTime.setVisibility(View.VISIBLE);
//                        pickupPlanePrice.setText(PickupAir);
//                    } else {
//                        checkPickupAir.setChecked(false);
//                        Toast.makeText(getActivity(), "请选择出行日期", Toast.LENGTH_SHORT).show();
//                    }
//                    pickupPrice = pickupPlanePrice.getText().toString().trim();//接机价格
//
//                    startedTime.setHint(travelDateTime + " "+"8:00--23:00");
//
//                } else {
//                    pickupPrice = "";
//                    startTime = "";
//                    pickupPlanePrice.setVisibility(View.GONE);
//                    pickupLine.setVisibility(View.GONE);
//                    pickupTime.setVisibility(View.GONE);
//                }
//                break;
//            case R.id.check_pickup_train://接火车
//                if (isChecked) {
//                    checkPickupAir.setChecked(false);
//                    if (!StringUtil.isEmpty(PickupTrain)) {
//                        pickupTrainPrice.setText(PickupTrain);
//                        pickupTrainPrice.setVisibility(View.VISIBLE);
//                        pickupLine.setVisibility(View.VISIBLE);
//                        pickupTime.setVisibility(View.VISIBLE);
//                    } else {
//                        checkPickupTrain.setChecked(false);
//                        Toast.makeText(getActivity(), "请选择出行日期", Toast.LENGTH_SHORT).show();
//                    }
//                    pickupPrice = pickupTrainPrice.getText().toString().trim();//接站价钱
//                    startedTime.setHint(travelDateTime +  " "+"8:00--23:00");
//                } else {
//                    pickupPrice = "";
//                    pickupTrainPrice.setVisibility(View.GONE);
//                    pickupLine.setVisibility(View.GONE);
//                    pickupTime.setVisibility(View.GONE);
//                }
//                break;
            case R.id.check_guide://需要导游证
                if (isChecked) {
                    checkUnguide.setChecked(false);
                    isNeedGuide = "1";
                    if (!StringUtil.isEmpty(GuideByHourPrice) && !StringUtil.isEmpty(GuideByDayPrice)) {
                        if (!StringUtil.isEmpty(is_verify)) {
                            if (is_verify.equals("2")) {
                                needGuidePrice.setVisibility(View.VISIBLE);
                                unneedGuidePrice.setVisibility(View.GONE);
                                checkGuide.setClickable(false);
                                if (checkHour.isChecked()) {
                                    needGuidePrice.setText(GuideByHourPrice);
                                } else if (checkDay.isChecked()) {
                                    needGuidePrice.setText(GuideByDayPrice);
                                }
                            } else {
                                checkGuide.setChecked(false);
                                new DialogKnow(getActivity(), R.style.user_default_dialog, new DialogKnow.CallBackListener() {
                                    @Override
                                    public void onClickListener() {
                                        checkUnguide.setChecked(true);
                                    }
                                }).setMsg("抱歉！您预约的人导游身份未认证，建议您选择不需要导游证").setPositiveButton("我知道了").show();
                            }
                        } else {
                            needGuidePrice.setVisibility(View.VISIBLE);
                            unneedGuidePrice.setVisibility(View.GONE);
                            checkGuide.setClickable(false);
                            if (checkHour.isChecked()) {
                                needGuidePrice.setText(GuideByHourPrice);
                            } else if (checkDay.isChecked()) {
                                needGuidePrice.setText(GuideByDayPrice);
                            }
                        }

                    } else {
                        checkGuide.setChecked(false);
                        Toast.makeText(getActivity(), "请选择出行日期", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    needGuidePrice.setVisibility(View.GONE);
                    checkGuide.setClickable(true);
                }
                break;
            case R.id.check_unguide://不需要导游证
                if (isChecked) {
                    isNeedGuide = "2";
                    checkGuide.setChecked(false);
                    if (!StringUtil.isEmpty(noGuideByHourPrice) && !StringUtil.isEmpty(noGuideByDayPrice)) {
                        unneedGuidePrice.setVisibility(View.VISIBLE);
                        needGuidePrice.setVisibility(View.GONE);
                        checkUnguide.setClickable(false);
                        if (checkHour.isChecked()) {
                            unneedGuidePrice.setText(noGuideByHourPrice);
                        } else if (checkDay.isChecked()) {
                            unneedGuidePrice.setText(noGuideByDayPrice);
                        }
                    } else {
                        checkUnguide.setChecked(false);
                        Toast.makeText(getActivity(), "请选择出行日期", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    unneedGuidePrice.setVisibility(View.GONE);
                    checkUnguide.setClickable(true);
                }
                break;
            case R.id.check_pickup_sex://选择接单人性别
                if (isChecked) {
                    sex.setVisibility(View.VISIBLE);
                } else {
                    pickupSex = "无";
                    sex.setVisibility(View.GONE);
                }
                break;
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_order://完成
                if (checkSex.isChecked()) {
                    if (checkSexMan.isChecked()) {
                        pickupSex = "男";
                    } else if (checkSexWomen.isChecked()) {
                        pickupSex = "女";
                    }
                } else {
                    pickupSex = "无";
                }
                //出发城市
                currentCity = locationCity.getText().toString().trim();
                //目的地
                destinCity = chooseDestination.getText().toString().trim();
                //景点
                scenic = chooseScenic.getText().toString().trim();
                if (scenic == null) {
                    scenic = "";
                }

                //出行人数
                travelperson = travelCount.getText().toString().trim();
                //具体要求
                demand = specificDemand.getText().toString().trim();

                if (checkHour.isChecked()) {
                    travelTime = timeHour.getText().toString().trim();
                } else {
                    travelTime = byDayPrice.getText().toString().trim();
                }
                if (checkUnguide.isChecked()) {
                    guidePrice = unneedGuidePrice.getText().toString().trim();
                } else if (checkGuide.isChecked()) {
                    guidePrice = needGuidePrice.getText().toString().trim();
                } else {
                    guidePrice = "";
                }

//                if (checkPickupAir.isChecked() | checkPickupTrain.isChecked()) {
//                    startTime = startedTime.getText().toString().trim();//接机具体时间
//                    startTime=StringUtil.dateTimeFormat(startTime);
//                    if (startTime.length()>=10){
//                        String startMonth = startTime.substring(5, 7);
//                        String startDay = startTime.substring(8, 10);
//                        if (StringUtil.isDateTime(startTime)==false||Integer.valueOf(startMonth)>month+1||Integer.valueOf(startDay)>day){
//                            Toast.makeText(getActivity(), "请检查接待时间格式是否正确，接待时间为8:00--23:00", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                    }else {
//                        Toast.makeText(getActivity(), "请检查接待时间格式是否正确，接待时间为8:00--23:00", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
////                    else if (Integer.valueOf(startMonth)>month+1){//输入日期月份大于出发日期月份
////                        Toast.makeText(getActivity(), "请检查接待时间格式是否正确，接待时间为8:00--23:00", Toast.LENGTH_SHORT).show();
////                        return;
////                    }else if (Integer.valueOf(startDay)>day){
////                        Toast.makeText(getActivity(), "请检查接待时间格式是否正确，接待时间为8:00--23:00", Toast.LENGTH_SHORT).show();
////                        return;
////                    }
//                }else {
//                    startTime = "";
//                }

                if (!StringUtil.isEmpty(currentCity)) {
                    if (!StringUtil.isEmpty(destinCity) && !destinCity.equals("请选择目的地")) {
                        if (!scenic.equals("请选择景点")) {
                            if (!StringUtil.isEmpty(travelperson)) {
                                if (!StringUtil.isEmpty(travelTime)) {
                                    if (!StringUtil.isEmpty(isNeedGuide)) {
//                                        if (!StringUtil.isEmpty(pickupPrice)) {
//                                            if (StringUtil.isEmpty(startTime)) {
//                                                Toast.makeText(getActivity(), "请正确填写具体接站日期", Toast.LENGTH_SHORT).show();
//                                                return;
//                                            }
//                                        }
                                        if (checkRules.isChecked()) {
                                            //保存信息至Bean类，下个页面提取
                                            intentGoTravelMsg();
                                            Intent intent = new Intent(getActivity(), OrderPreviewActivity.class);
                                            intent.putExtra("bid", bid);
                                            startActivityForResult(intent, 0);
                                        } else {
                                            Toast.makeText(getActivity(), "请阅读《出行须知》", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(getActivity(), "请选择导游证是否要求", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "请填写游玩时长", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "请填写出行人数", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "请选择游玩景点", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "请选择目的城市", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "请先填写出发城市", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.choose_destination://选择城市
                Intent intentdestination = new Intent(getActivity(), CityActivity.class);
                startActivityForResult(intentdestination, 6);//要用forResult跳转
                break;
            case R.id.choose_scenic://选择景点
                Intent intentscenic = new Intent(getActivity(), ScenicActivity.class);
                intentscenic.putExtra("cityId", cityId);
                String cityName = chooseDestination.getText().toString();
                if (!StringUtil.isEmpty(cityName) && !cityName.equals("请选择目的地")) {
                    startActivityForResult(intentscenic, 7);//要用forResult跳转
                } else {
                    Toast.makeText(getActivity(), "请先选择城市", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.rules:
                Intent intent = new Intent(getActivity(), AgreementActivity.class);
                intent.putExtra("logFlag", "gotoTravel");
                intent.putExtra("isReade", isReaded);
                startActivityForResult(intent, 2);
                break;
        }
    }

    /**
     * 保存信息至实体类，下个页面提取
     */
    private void intentGoTravelMsg() {

        goTravelMesgBean = new GoTravelMesgBean();
        goTravelMesgBean.setCurrentCity(currentCity);
        goTravelMesgBean.setDestinCity(destinCity);
        goTravelMesgBean.setScenic(scenic);
        goTravelMesgBean.setTravelDateTime(travelDateTime);
        goTravelMesgBean.setTravelperson(travelperson);
        goTravelMesgBean.setTraveltype(traveltype);
        goTravelMesgBean.setTravelTime(travelTime);
        goTravelMesgBean.setIsNeedGuide(isNeedGuide);
        goTravelMesgBean.setGuidePrice(guidePrice);
//        if (!StringUtil.isEmpty(pickupType)) {
//            goTravelMesgBean.setPickupType(pickupType);
//            goTravelMesgBean.setStartTime(startTime);
//            goTravelMesgBean.setPickupPrice(pickupPrice);
//        } else {
//            goTravelMesgBean.setPickupType("");
//            goTravelMesgBean.setStartTime("");
//            goTravelMesgBean.setPickupPrice("");
//        }
        if (!StringUtil.isEmpty(pickupSex)) {
            goTravelMesgBean.setPickupSex(pickupSex);
        } else {
            goTravelMesgBean.setPickupSex("无");
        }
        if (!StringUtil.isEmpty(demand)) {
            goTravelMesgBean.setDemand(demand);
        } else {
            goTravelMesgBean.setDemand("无");
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
//            case 1:
//                if (resultCode == 3) {
//                    mScrollView.setVisibility(View.VISIBLE);
//                    mUnlogin.setVisibility(View.GONE);
//                }
//                break;
            case 2://处理是否阅读发单须知的状态
                if (resultCode == 2) {
                    isReaded = data.getStringExtra("readedStatus");
                    if (!StringUtil.isEmpty(isReaded) && isReaded.equals("1")) {
                        checkRules.setChecked(true);
                    } else {
                        checkRules.setChecked(false);
                    }
                }
                break;
            case 6:
                if (resultCode == 6) {
                    String city = data.getStringExtra("cityName");
                    cityId = data.getStringExtra("cityId");
                    if (!city.equals(chooseDestination.getText())) {
                        chooseScenic.setText("请选择景点");
                    }
                    chooseDestination.setText(city);
                }
                break;
            case 7:
                if (resultCode == 7) {
                    ArrayList<String> list = data.getStringArrayListExtra("scenicNameList");
                    StringBuffer buffer = new StringBuffer();
                    for (int i = 0; i < list.size(); i++) {
                        if (i == list.size() - 1) {
                            buffer.append(list.get(i));
                        } else {
                            buffer.append(list.get(i) + ",");
                        }
                    }
                    chooseScenic.setText(buffer.toString());
                    if (chooseScenic.getText().equals("")) {
                        chooseScenic.setText("请选择景点");
                    }
                }
                break;
        }

    }

    /**
     * 获取价格信息
     */
    public void getPriceMsgForNet(String month) {
        if (judgeTimeDev) {
            String sign = Md5Utils.createMD5("devcode=" + mDevcode + "send_time=" + month + "timestamp=" + mTimestamp + "uid=" + userID + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", mDevcode)
                    .add("signature", sign)
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", userID)
                    .add("send_time", month)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.GET_PRICEMSG, requestBody, new HttpCallBack(new GoTravelEvent()));
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getPriceMsg(GoTravelEvent goTravelEvent) {
        if (goTravelEvent.isTimeOut()) {
            Toast.makeText(getActivity(), "网络状态异常，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }
        GoTravelPriceBean priceBean = goTravelEvent.getData();
        if (priceBean.getStatus() == 0) {
            noGuideByHourPrice = "";//无导游证按小时价格
            GuideByHourPrice = "";//有导游证按小时价格
            noGuideByDayPrice = "";//无导游证按天价格
            GuideByDayPrice = "";//有导游证按天价格
//            PickupAir = "";//接机价格
//            PickupTrain = "";//接站价格
            nowTime = String.valueOf(month + 1);
//            Toast.makeText(getActivity(), "成功", Toast.LENGTH_SHORT).show();
//            String noGuideByDay = priceBean.getData().getWu_d_price();
//            String noGuideByHour = priceBean.getData().getWu_h_price();
//            String GuideByDay = priceBean.getData().getYou_d_price();
//            String GuideByHour = priceBean.getData().getYou_h_price();
//            String pickupAirPrice = priceBean.getData().getWay_jj();
//            String pickupTrainPrice = priceBean.getData().getWay_jz();
            noGuideByDayPrice = priceBean.getData().getWu_d_price();
            noGuideByHourPrice = priceBean.getData().getWu_h_price();
            GuideByDayPrice = priceBean.getData().getYou_d_price();
            GuideByHourPrice = priceBean.getData().getYou_h_price();
            //if (!StringUtil.isEmpty(noGuideByDay)) {
            //    noGuideByDayPrice = noGuideByDay;
            //}
            //if (!StringUtil.isEmpty(noGuideByHour)) {
            //    noGuideByHourPrice = noGuideByHour;
            //}
            //if (!StringUtil.isEmpty(GuideByDay)) {
            //    GuideByDayPrice = GuideByDay;
            //}
            //if (!StringUtil.isEmpty(GuideByHour)) {
            //    GuideByHourPrice = GuideByHour;
            //}
//            if (!StringUtil.isEmpty(pickupAirPrice)) {
//                PickupAir = pickupAirPrice;
//            }
//            if (!StringUtil.isEmpty(pickupTrainPrice)) {
//                PickupTrain = pickupTrainPrice;
//            }

        } else {
            Toast.makeText(getActivity(), priceBean.getMsg(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取被预约路书的及发布人的信息
     *
     * @param bid
     */
    private void getReserveMsg(String bid) {
        if (judgeTimeDev) {
            String sign = Md5Utils.createMD5("bid=" + bid + "devcode=" + mDevcode + "timestamp=" + mTimestamp + "uid=" + userID + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", mDevcode)
                    .add("signature", sign)
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", userID)
                    .add("bid", bid)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.BOOK_RESERVE, requestBody, new HttpCallBack(new BookReserveEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getReserve(BookReserveEvent bookReserveEvent) {
        if (bookReserveEvent.isTimeOut()) {
            Toast.makeText(getActivity(), "网络状态异常，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }
        BookReserveBean bookReserveBean = bookReserveEvent.getData();
        if (bookReserveBean.getStatus() == 0) {
            String city = bookReserveBean.getData().getCity();
            String spot = bookReserveBean.getData().getSpot();
            is_verify = bookReserveBean.getData().getIs_verify();
            if (!StringUtil.isEmpty(city)) {
                chooseDestination.setText(city);
                chooseDestination.setClickable(false);
            } else {
                chooseDestination.setClickable(true);
            }
            if (!StringUtil.isEmpty(spot)) {
                chooseScenic.setText(spot);
                chooseScenic.setClickable(false);
            } else {
                chooseScenic.setText("");
                chooseScenic.setClickable(false);
            }


        } else {
            Toast.makeText(getActivity(), bookReserveBean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(getActivity(), bookReserveBean.getStatus());
        }
    }

    //广播接收内部类
//    private class MyBroadcastReciver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals("clear")) {
//
//                //获取定位城市
//                String city = ((MyApplication) getActivity().getApplication()).getLocationCity();
//                if (!StringUtil.isEmpty(city)) {
//                    locationCity.setText(city);
//                } else {
//                    locationCity.setHint("出发城市");
//                }
//                checkHour.setChecked(true);
//                checkRules.setClickable(false);
//                byDayPrice.setVisibility(View.GONE);
//                tvByDay.setVisibility(View.GONE);
//                checkGuide.setChecked(false);
//                checkUnguide.setChecked(false);
//                needGuidePrice.setVisibility(View.GONE);
//                chooseDestination.setText("");
//                chooseScenic.setText("");
//                timeHour.setText("");
//                byDayPrice.setText("");
//                travelCount.setText("");
//                monthDateView.setTodayToView();
//                GuideByHourPrice = "";
//                GuideByDayPrice = "";
//                noGuideByDayPrice = "";
//                noGuideByHourPrice = "";
//                nowTime = "";
//                specificDemand.setText("");
//                chooseSex.setVisibility(View.VISIBLE);
//                lineSex.setVisibility(View.VISIBLE);
//                checkSex.setChecked(false);
//                checkSex.setClickable(true);
//                chooseDestination.setClickable(true);
//                chooseScenic.setClickable(true);
//
////            //在android端显示接收到的广播内容
////            Toast.makeText(getActivity(), author, 1).show();
//
//                //在结束时可取消广播
//                getActivity().unregisterReceiver(this);
//            }
//        }
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void clear(ClearBean bean){
        if (bean!=null&&bean.getClear().equals("clear")){
            //获取定位城市
            String city = ((MyApplication) getActivity().getApplication()).getLocationCity();
            if (!StringUtil.isEmpty(city)) {
                locationCity.setText(city);
            } else {
                locationCity.setHint("出发城市");
            }
            checkHour.setChecked(true);
            checkRules.setClickable(false);
            byDayPrice.setVisibility(View.GONE);
            tvByDay.setVisibility(View.GONE);
            checkGuide.setChecked(false);
            checkUnguide.setChecked(false);
            needGuidePrice.setVisibility(View.GONE);
            chooseDestination.setText("");
            chooseScenic.setText("");
            timeHour.setText("");
            byDayPrice.setText("");
            travelCount.setText("");
            monthDateView.setTodayToView();
            GuideByHourPrice = "";
            GuideByDayPrice = "";
            noGuideByDayPrice = "";
            noGuideByHourPrice = "";
            nowTime = "";
            specificDemand.setText("");
            chooseSex.setVisibility(View.VISIBLE);
            lineSex.setVisibility(View.VISIBLE);
            checkSex.setChecked(false);
            checkSex.setClickable(true);
            chooseDestination.setClickable(true);
            chooseScenic.setClickable(true);

        }
    }
}




