package yc.pointer.trip.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.google.zxing.integration.android.IntentIntegrator;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import yc.pointer.trip.R;
import yc.pointer.trip.activity.AgreementActivity;
import yc.pointer.trip.activity.CityActivity;
import yc.pointer.trip.activity.NewOrderPreviewActivity;
import yc.pointer.trip.activity.ScanQRCodeActivity;
import yc.pointer.trip.activity.ScenicActivity;
import yc.pointer.trip.activity.VerifyActivity;
import yc.pointer.trip.adapter.NewGoTravelHotSenicAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseFragment;
import yc.pointer.trip.bean.BookReserveBean;
import yc.pointer.trip.bean.GoTravelMesgBeanNew;
import yc.pointer.trip.bean.GoTravelPriceBean;
import yc.pointer.trip.bean.NewGotravelBean;
import yc.pointer.trip.bean.eventbean.ClearBean;
import yc.pointer.trip.event.BookReserveEvent;
import yc.pointer.trip.event.GoTravelEvent;
import yc.pointer.trip.event.NewGoTravelEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.LocationUtil;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.CalendarPopup;
import yc.pointer.trip.view.DialogKnow;
import yc.pointer.trip.view.DialogSure;
import yc.pointer.trip.view.FadingScrollView;
import yc.pointer.trip.view.LocationDialog;
import yc.pointer.trip.view.PersonalNumberPopWindow;
import yc.pointer.trip.view.RulesPopupWindow;
import yc.pointer.trip.view.WhetherDialog;

/**
 * Created by moyan on 2018/6/1.
 * 出发吧2018.7.3版本
 */

public class NewGoTravelFragment extends BaseFragment implements CalendarPopup.CalendarCallBack,
        PersonalNumberPopWindow.PersonCallBack, RulesPopupWindow.onCallBackListener,
        WhetherDialog.WhetherCallBack {

    @BindView(R.id.nac_layout)
    View nacLayout;
    @BindView(R.id.scroll_view)
    FadingScrollView nacRoot;
    @BindView(R.id.tool_bar_line)
    ImageView line;//导航栏底部线
    private View decorView;//状态栏布局


    @BindView(R.id.scenic_search)
    ImageView search;//搜索框
    @BindView(R.id.scan_gotravel)
    ImageView scanCode;//扫描按钮
    @BindView(R.id.location_city)
    TextView TextlocationCity;//当前目的地


    @BindView(R.id.hot_title)
    TextView hotSenic;//热门景点
    @BindView(R.id.hot_senic_recycle)
    RecyclerView hotSenicRecycle;//热门景点的列表
    @BindView(R.id.city_back_img)
    ImageView cityBackImg;//城市背景图（原图）
    @BindView(R.id.city_back_blurry)
    ImageView cityBackBiurry;//城市背景图（模糊层）
    @BindView(R.id.history_go_num)
    TextView historyGoNum;//有多少人去过这里
    @BindView(R.id.city_price)
    TextView cityGuidePrice;//当前城市导游价格


    @BindView(R.id.go_date)
    TextView goDate;//出发日期
    @BindView(R.id.linear_go_date)
    LinearLayout linearGoDate;//选择出发日期
    @BindView(R.id.go_people_num)
    TextView goPeopleNum;//出行人数
    @BindView(R.id.liner_go_people_num)
    LinearLayout linerGoPeopleNum;//选择出行人数
    @BindView(R.id.linear_go_duration)
    LinearLayout linearGoDuration;//选择游玩时长
    @BindView(R.id.go_duration)
    TextView goDuration;//游玩时长
    @BindView(R.id.choose_guideCar)
    TextView chooseGuideCar;//是否需要导游证
    @BindView(R.id.liner_choose_guidecard)
    LinearLayout linerChooseGuidecard;//选择是否需要导游证
    @BindView(R.id.choose_sex)
    TextView chooseSex;//接单人性别
    @BindView(R.id.linear_choose_sex)
    LinearLayout linearChooseSex;//选择接单性别
    @BindView(R.id.choose_car)
    TextView chooseCar;//是否车接
    @BindView(R.id.linear_choose_car)
    LinearLayout linearChooseCar;//选择是否车接
//    @BindView(R.id.choose_scenic)
//    TextView chooseScenic;//选择景点
    @BindView(R.id.specific)
    EditText specific;//具体要求


    @BindView(R.id.check_rules)
    CheckBox checkRules;//是否阅读出行须知的checkBox
    @BindView(R.id.rules)
    TextView rules;//阅读出行须知
    @BindView(R.id.button_go_travel_next)
    Button nextGoTravel;//下一步，跳转订单预览页面

    private String mDevcode;//设备识别码
    private long mTimestamp;//时间戳
    private String userID;//用户ID
    private boolean judgeTimeDev;//判断是否可以请求网络
    private boolean islogin;//判断是否为登录状态
    private String isReaded;//判断是否已经阅读《出行须知》
    private String currentCity;//目的城市
    private LocationUtil locationUtil;//定位操作的对象类

    private String cityid = "";//城市ID
    private String travelDate = "";//出行日期
    private String travelTime = "";//游玩时长
    private String travelType = "";//出行类型  1代表小时 2代表天 0代表取消
    private String needGuide = "";//导游证需求
    private String needCar = "";//用车需求
    private String pickupSex = "";//接单人性别
    private String demandType = "";//需求选择框  0：是否需要导游证件  1：接单人下性别选择   2：是否需要用车服务
    private String currenMonth = "";//当前月份
    private String priceMonth = "";//请求的月份

    private String noGuideByHourPrice = "";//无导游证按小时价格
    private String GuideByHourPrice = "";//有导游证按小时价格
    private String noGuideByDayPrice = "";//无导游证按天价格
    private String GuideByDayPrice = "";//有导游证按天价格
    private String carByHourPrice = "";//租车价格按小时
    private String carByDayPrice = "";//租车价格按天


    private List<NewGotravelBean.DataBean> mHotScenicList = new ArrayList<>();//热门景点的列表
    private NewGoTravelHotSenicAdapter adapter;//热门景点的适配
    private CalendarPopup calendarPopup;//出行日期的popupWindow
    private PersonalNumberPopWindow personalNumberPopWindow;//出行人数选择的popupWindow
    private WindowManager.LayoutParams layoutParams;
    private RulesPopupWindow rulesPopupWindow;//出行时长选择的popupWindow
    private WhetherDialog whetherDialog;//导游证件、接单人性别、用车服务的选择框

    private static GoTravelMesgBeanNew goTravelMesgBeanNew;//传递的出发吧信息对象
    private String bid;//判断是否从预约跳转至发单
    //    private MyBroadcastReciver myBroadcastReciver;
    private String mIsOrder;//判断是否可以发单  // 0：未提交信息  1：提交审核中  2：审核通过  3：审核未通过
    private LocationDialog locationUtil1;


    public void setFlag(String bid) {
        this.bid = bid;
    }

    public static GoTravelMesgBeanNew getGoTravelMesgBean() {
        return goTravelMesgBeanNew;
    }


    @Override
    protected int getContentViewLayout() {
        return R.layout.fragment_go_travel_new;
    }

    @Override
    protected void initView() {

//        //注册广播
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("clear");
//        myBroadcastReciver = new MyBroadcastReciver();
//        getActivity().registerReceiver(myBroadcastReciver, intentFilter);
       getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        nacLayout.setAlpha(0);
        nacRoot.setFadingView(getActivity(), nacLayout);//绑定渐变的View视图
        nacRoot.setFadingHeightView(getActivity(), cityBackImg);//设定滑动距离为背景图片的距离
        nacRoot.setListener(new FadingScrollView.AlphaListener() {
            @Override
            public void statusBarIsWhite(boolean isWhite) {
                if (isWhite) {
                    //状态栏白色
                    if (Build.VERSION.SDK_INT >= 21) {
                        decorView = getActivity().getWindow().getDecorView();
                        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    }
                    line.setVisibility(View.VISIBLE);
                    search.setImageResource(R.mipmap.img_search_gray);
                    scanCode.setImageResource(R.mipmap.icon_flicking_gray);
                } else {
                    //沉浸式
                    if (Build.VERSION.SDK_INT >= 21) {
                        decorView = getActivity().getWindow().getDecorView();
                        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                    }
                    line.setVisibility(View.GONE);
                    search.setImageResource(R.mipmap.img_search_start);
                    scanCode.setImageResource(R.mipmap.icon_flicking_white);
                }
            }
        });
        //初始化数据
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        islogin = MyApplication.mApp.isIslogin();
        mDevcode = MyApplication.mApp.getDevcode();
        mTimestamp = MyApplication.mApp.getTimestamp();
        //判断是否获取时间戳和设备识别码
        judgeTimeDev = APPUtils.judgeTimeDev(getActivity(), mDevcode, mTimestamp);
        //获取定位城市
        String locationCity = MyApplication.mApp.getLocationCity();
        String locationCountry = MyApplication.mApp.getLocationCountry();
        locationUtil = LocationUtil.getLocationUtilInstance().initLocation(getActivity());
//        if (StringUtil.isEmpty(locationCity) || StringUtil.isEmpty(locationCountry)) {
            if (isLocationEnabled()) {
                //检查GPS开关
                getLocationCity();
            } else {
                locationUtil1 = new LocationDialog(getActivity(), R.style.user_default_dialog, NewGoTravelFragment.this, 789);
                locationUtil1.setMsg("检测到你的GPS定位服务已关闭，请检查开启");
                locationUtil1.setLocationCallBack(new LocationDialog.LocationCallBack() {
                    @Override
                    public void closeDialog() {
                        getCityMessageForNet("中国", "天津市");
                    }
                });
                locationUtil1.show();
            }
//        } else {
//            getCityMessageForNet(locationCountry, locationCity);
//        }

    }

    private void getLocationCity() {
        //定位
        locationUtil.setmICallLocation(new LocationUtil.ICallLocation() {
            @Override
            public void locationMsg(AMapLocation aMapLocation) {
                String country = aMapLocation.getCountry();
                String city = aMapLocation.getCity();
                if (!StringUtil.isEmpty(country)) {
                    MyApplication.mApp.setLocationCountry(country);
                }
                if (!StringUtil.isEmpty(city)) {
                    MyApplication.mApp.setLocationCity(city);
                }
                //获取网络数据
                getCityMessageForNet(country, city);
                locationUtil.stopLocation();//停止定位服务
            }
        });
    }

    /**
     * 根据定位请求网络数据
     */
    private void getCityMessageForNet(String country, String city) {

        currentCity = city;
        TextlocationCity.setText(country + "· " + city);

        if (islogin) {
            //判断是否已阅读发单规则
            isReaded = MyApplication.mApp.getUserBean().getIs_mz();
            userID = MyApplication.mApp.getUserId();
            if (!StringUtil.isEmpty(isReaded) && isReaded.equals("1")) {
                checkRules.setChecked(true);
            } else {
                checkRules.setChecked(false);
            }
            //获取城市信息
            if (StringUtil.isEmpty(currentCity)) {
                currentCity = "天津市";
            }
            getCityMessage(currentCity);

            if (!StringUtil.isEmpty(bid) && !bid.equals("0")) {
                // TODO 请求被预约人信息
//                getReserveMsg(bid);

            } else {
                bid = "0";

            }
        }
        String nowTime = StringUtil.getNowTime();//当前日期
        if (!StringUtil.isEmpty(nowTime)) {
            goDate.setText(nowTime);//设置默认出发日期为当天
            int month = Integer.valueOf(nowTime.substring(5, 7));
            currenMonth = String.valueOf(month);
            //请求当季价格
            getPriceMsgForNet(currenMonth);
        }
        layoutParams = getActivity().getWindow().getAttributes();
        //出行日期选择得popupWindow
        calendarPopup = new CalendarPopup(getActivity(), getActivity(), this);
        //出行人数选择的popupWindow
        personalNumberPopWindow = new PersonalNumberPopWindow(getActivity(), getActivity(), this);
        //出行时长的popupwindow
        rulesPopupWindow = new RulesPopupWindow(getActivity(), getActivity(), this);
        mHotScenicList = new ArrayList<>();
        //设置热门景点数据列表
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        hotSenicRecycle.setLayoutManager(layoutManager);
        adapter = new NewGoTravelHotSenicAdapter(mHotScenicList);
        hotSenicRecycle.setAdapter(adapter);

    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

    /**
     * 获取城市信息
     *
     * @param city 定位城市(定位失败，默认天津)
     */
    private void getCityMessage(String city) {

        if (judgeTimeDev && !StringUtil.isEmpty(userID)) {
            String sign = Md5Utils.createMD5("cityname=" + city + "devcode=" + mDevcode + "timestamp=" + mTimestamp + "uid=" + userID + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", mDevcode)
                    .add("cityname", city)
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", userID)
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.NEW_GO_TRAVEL, requestBody, new HttpCallBack(new NewGoTravelEvent()));
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getGoTravelCityMessage(NewGoTravelEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(getActivity(), "网络异常", Toast.LENGTH_SHORT).show();
            return;
        }
        NewGotravelBean data = event.getData();
        if (data.getStatus() == 0) {
            //请求成功
            cityid = data.getCity().getCityid();
            String pic = data.getCity().getPic();
            String fa_num = data.getCity().getFa_num();
            if (!StringUtil.isEmpty(pic)) {
                OkHttpUtils.displayGlide(getActivity(), cityBackImg, pic);
            }
            if (!StringUtil.isEmpty(fa_num)) {
                historyGoNum.setVisibility(View.VISIBLE);
                String format = String.format(getResources().getString(R.string.alread_publish_num), fa_num);
                historyGoNum.setText(format);
            } else {
                historyGoNum.setVisibility(View.INVISIBLE);
            }

            //热门景点数据列表
            mHotScenicList.addAll(data.getData());
            if (mHotScenicList.size() != 0) {
                hotSenic.setVisibility(View.VISIBLE);
                hotSenicRecycle.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            } else {
                hotSenic.setVisibility(View.GONE);
                hotSenicRecycle.setVisibility(View.GONE);
            }

        } else {
            //返回失败
            Toast.makeText(getActivity(), data.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(getActivity(), data.getStatus());

        }
    }


    /**
     * 获取当季价格信息
     */
    public void getPriceMsgForNet(String month) {
        if (judgeTimeDev) {
            if (!month.equals(priceMonth)) {
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

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getPriceMsg(GoTravelEvent goTravelEvent) {
        if (goTravelEvent.isTimeOut()) {
            Toast.makeText(getActivity(), "网络状态异常，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }
        GoTravelPriceBean priceBean = goTravelEvent.getData();
        if (priceBean.getStatus() == 0) {
            priceMonth = currenMonth;

            noGuideByHourPrice = priceBean.getData().getWu_h_price();//无导游证按小时价格
            noGuideByDayPrice = priceBean.getData().getWu_d_price();//无导游证按天价格
            GuideByDayPrice = priceBean.getData().getYou_d_price();//有导游证的导游一天的价格
            GuideByHourPrice = priceBean.getData().getYou_h_price();//有导游证的导游一个小时的价格

            //租车的价格
            carByHourPrice = priceBean.getData().getCar_price();//按小时的车价
            carByDayPrice = priceBean.getData().getCar_d_price();//按天的车价
            if (!StringUtil.isEmpty(GuideByHourPrice) && !StringUtil.isEmpty(GuideByDayPrice)) {
                cityGuidePrice.setVisibility(View.VISIBLE);
                @SuppressLint("StringFormatMatches") String format = String.format(getActivity().getResources().getString(R.string.city_price), GuideByHourPrice, GuideByDayPrice);
                cityGuidePrice.setText(format);
            }

        } else {
            Toast.makeText(getActivity(), priceBean.getMsg(), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 判断是否打开GPS服务
     *
     * @return
     */
    public boolean isLocationEnabled() {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(getActivity().getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationUtil.stopLocation();
        //在结束时可取消广播
//        getActivity().unregisterReceiver(myBroadcastReciver);
    }

    /**
     * 点击事件
     *
     * @param view
     */
    @OnClick({R.id.linear_go_date, R.id.liner_go_people_num, R.id.linear_go_duration,
            R.id.liner_choose_guidecard, R.id.linear_choose_sex, R.id.linear_choose_car,
            R.id.scenic_search, R.id.scan_gotravel, R.id.location_city,
            R.id.rules, R.id.button_go_travel_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.location_city:
                Intent intentdestination = new Intent(getActivity(), CityActivity.class);
                startActivityForResult(intentdestination, 6);//要用forResult跳转
                break;
            case R.id.scenic_search://搜索，跳转景点页面，传递cityId
                Intent intentHotCity = new Intent(getActivity(), CityActivity.class);
                startActivityForResult(intentHotCity, 6);//要用forResult跳转
//                if (!StringUtil.isEmpty(cityid)) {
//                    Intent intent = new Intent(getActivity(), ScenicActivity.class);
//                    intent.putExtra("cityId", cityid);
//                    startActivityForResult(intent, 7);
//                }
                break;
//            case R.id.choose_scenic://搜索，跳转景点页面，传递cityId
//                if (!StringUtil.isEmpty(cityid)) {
//                    Intent intent = new Intent(getActivity(), ScenicActivity.class);
//                    intent.putExtra("cityId", cityid);
//                    startActivityForResult(intent, 7);
//                }
//                break;
            case R.id.scan_gotravel://扫面开始订单
                // 创建IntentIntegrator对象
                IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity());
                intentIntegrator.setCaptureActivity(ScanQRCodeActivity.class);//设置扫描二维码的界面
                /**
                 * 设置扫描的样式
                 *  IntentIntegrator.ALL_CODE_TYPES//全部类型
                 IntentIntegrator.PRODUCT_CODE_TYPES//商品码类型
                 IntentIntegrator.ONE_D_CODE_TYPES//一维码类型
                 IntentIntegrator.QR_CODE_TYPES//二维码类型
                 IntentIntegrator.DATA_MATRIX_TYPES//数据矩阵类型
                 *
                 */
//                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
//                intentIntegrator.setPrompt("请将二维码放入扫描框，即可完成扫描");//设置提示信息
                intentIntegrator.setOrientationLocked(true);//设置方向锁（默认为true）

                intentIntegrator.setCameraId(0);//设置摄像头Id  0：后置  1：前置
                intentIntegrator.setBeepEnabled(false);//设置是否有声音
//                 开始扫描
                intentIntegrator.initiateScan();


                break;
            case R.id.linear_go_date://选择出行日期
                layoutParams.alpha = 0.5f;
                getActivity().getWindow().setAttributes(layoutParams);
                calendarPopup.showAtLocation(linearGoDate, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.liner_go_people_num://选择出行人数
                layoutParams.alpha = 0.5f;
                getActivity().getWindow().setAttributes(layoutParams);
                personalNumberPopWindow.showAtLocation(linerGoPeopleNum, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.linear_go_duration://选择出行时长（按小时/按天）
                layoutParams.alpha = 0.5f;
                getActivity().getWindow().setAttributes(layoutParams);
                rulesPopupWindow.showAtLocation(linearGoDuration, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.liner_choose_guidecard://选择是否需要导游证
                demandType = "0";
                //导游证件
                whetherDialog = new WhetherDialog(getActivity());
                whetherDialog.setWhetherCallBack(this);
                whetherDialog.setBntText("需要")
                        .setBntMsg("不需要")
                        .show();
                break;
            case R.id.linear_choose_sex://选择接单人性别
                demandType = "1";
                //接单性别
                whetherDialog = new WhetherDialog(getActivity(), true);
                whetherDialog.setWhetherCallBack(this);
                whetherDialog.setBntText("男")
                        .setBntMsg("女")
                        .show();
                break;
            case R.id.linear_choose_car://选择是否需要车接
                demandType = "2";
                //是否需要车接的回调
                whetherDialog = new WhetherDialog(getActivity());
                whetherDialog.setWhetherCallBack(this);
                whetherDialog.setBntText("需要")
                        .setBntMsg("不需要")
                        .show();
                break;
            case R.id.rules:
                Intent intent = new Intent(getActivity(), AgreementActivity.class);
                intent.putExtra("logFlag", "gotoTravel");
                intent.putExtra("isReade", isReaded);
                startActivityForResult(intent, 2);
                break;
            case R.id.button_go_travel_next://下一步
                if (checkRules.isChecked()) {
                    mIsOrder = MyApplication.mApp.getUserBean().getIs_jie();//是否可以发单
                    if (!StringUtil.isEmpty(mIsOrder)) {
                        if (mIsOrder.equals("2")) {
                            getGOTravelInfo();//获取出发吧订单信息
                            if (!StringUtil.isEmpty(travelTime)) {
                                //跳转订单预览
                                Intent intentOrderPreview = new Intent(getActivity(), NewOrderPreviewActivity.class);
                                intentOrderPreview.putExtra("bid", bid);
                                startActivityForResult(intentOrderPreview, 0);
                            } else {
                                Toast.makeText(getActivity(), "请选择出行时长", Toast.LENGTH_SHORT).show();
                            }
                        } else if (mIsOrder.equals("1")) {
                            //认证中
                            new DialogKnow(getActivity(), R.style.user_default_dialog, new DialogKnow.CallBackListener() {
                                @Override
                                public void onClickListener() {
                                    //清除界面数据
                                    clearMessage();
                                }
                            }).setTitle("温馨提示")
                                    .setMsg("您提交的会员认证信息正在火速审核中,暂不可发布订单，请耐心等待")
                                    .setPositiveButton("我知道了")
                                    .show();
//                            Toast.makeText(getActivity(), "认证中", Toast.LENGTH_SHORT).show();
                        } else if (mIsOrder.equals("3")) {
                            //认证失败
                            new DialogSure(getActivity(), R.style.user_default_dialog, new DialogSure.CallBackListener() {
                                @Override
                                public void onClickListener(DialogSure dialogSure, boolean cancelable) {
                                    if (cancelable) {

                                        Intent intent = new Intent(getActivity(), VerifyActivity.class);
                                        startActivity(intent);


                                    } else {

                                    }
                                    //清除界面数据
                                    clearMessage();
                                }
                            }).setTitle("非常抱歉")
                                    .setMsg("您的会员认证申请失败，请重新完善会员身份信息")
                                    .setPositiveButton("前往")
                                    .setNegativeButton("放弃")
                                    .show();

//                            Toast.makeText(getActivity(), "认证失败", Toast.LENGTH_SHORT).show();

                        } else {
                            //未认证
                            new DialogSure(getActivity(), R.style.user_default_dialog, new DialogSure.CallBackListener() {
                                @Override
                                public void onClickListener(DialogSure dialogSure, boolean cancelable) {
                                    if (cancelable) {
                                        Intent intent = new Intent(getActivity(), VerifyActivity.class);
                                        startActivity(intent);
                                    } else {

                                    }
                                    //清除界面数据
                                    clearMessage();
                                }
                            }).setTitle("温馨提示")
                                    .setMsg("您尚未升级成为指针会员，请先完成指针会员认证，方可进行发单操作")
                                    .setPositiveButton("确定")
                                    .setNegativeButton("取消")
                                    .show();
//                            Toast.makeText(getActivity(), "未认证", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), "出发前请仔细阅读《出行须知》", Toast.LENGTH_SHORT).show();
                }
                break;


        }
    }

    /**
     * 保存信息为一个对象
     *
     * @return
     */
    private GoTravelMesgBeanNew getGOTravelInfo() {


        goTravelMesgBeanNew = new GoTravelMesgBeanNew();

        TextlocationCity.getText().toString().trim();//目的城市
        travelDate = goDate.getText().toString().trim();//出行日期
        String travelPeopleNum = goPeopleNum.getText().toString().trim();//出行人数
        String trim2 = goDuration.getText().toString().trim();//游玩时长
        if (trim2.contains("/")) {
            travelTime = "";
        } else {
            travelTime = trim2;

        }
        String trim = chooseGuideCar.getText().toString().trim();//导游证需求
        if (trim.contains("/")) {
            needGuide = "不需要";
        } else {
            needGuide = trim;
        }

        String trim3 = chooseSex.getText().toString().trim();//接单人性别
        if (trim3.contains("/")) {
            pickupSex = "不限";
        } else {
            pickupSex = trim3;
        }

        String trim1 = chooseCar.getText().toString().trim();//用车需求
        if (trim1.contains("/")) {
            needCar = "不需要";
        } else {
            needCar = trim1;
        }

//        String scenics = chooseScenic.getText().toString();//所选景点
//        if (scenics.equals("景点选择")) {
//            scenics = "不限";
//        }
        String scenics = "不限";
        String demandDetials = specific.getText().toString().trim();//具体要求
        if (StringUtil.isEmpty(demandDetials)) {
            demandDetials = "无";
        }
        goTravelMesgBeanNew.setDestinCity(currentCity);
        goTravelMesgBeanNew.setScenic(scenics);
        goTravelMesgBeanNew.setTravelDateTime(travelDate);
        goTravelMesgBeanNew.setTravelperson(travelPeopleNum);

        goTravelMesgBeanNew.setTravelTime(travelTime);
        goTravelMesgBeanNew.setTravelType(travelType);
        goTravelMesgBeanNew.setGuideCar(needGuide);
        goTravelMesgBeanNew.setPickupSex(pickupSex);
        goTravelMesgBeanNew.setNeedCar(needCar);

        goTravelMesgBeanNew.setDemand(demandDetials);

        goTravelMesgBeanNew.setNoGuideByDayPrice(noGuideByDayPrice);
        goTravelMesgBeanNew.setNoGuideByHourPrice(noGuideByHourPrice);
        goTravelMesgBeanNew.setGuideByDayPrice(GuideByDayPrice);
        goTravelMesgBeanNew.setGuideByHourPrice(GuideByHourPrice);
        goTravelMesgBeanNew.setCarByHourPrice(carByHourPrice);
        goTravelMesgBeanNew.setCarByDayPrice(carByDayPrice);

        return goTravelMesgBeanNew;

    }


    /**
     * 跳转回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
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
                    cityid = data.getStringExtra("cityId");
                    if (!city.equals(currentCity)) {
                        currentCity = city;
//                        chooseScenic.setText("景点选择");
                        mHotScenicList.clear();
                        //请求城市信息
                        getCityMessage(city);
                    }
                    TextlocationCity.setText("中国·" + city);
                }
                break;
//            case 7:
//                if (resultCode == 7) {
//                    ArrayList<String> list = data.getStringArrayListExtra("scenicNameList");
//                    StringBuffer buffer = new StringBuffer();
//                    for (int i = 0; i < list.size(); i++) {
//                        if (i == list.size() - 1) {
//                            buffer.append(list.get(i));
//                        } else {
//                            buffer.append(list.get(i) + "、");
//                        }
//                    }
//                    chooseScenic.setText(buffer.toString());
//                    if (chooseScenic.getText().equals("")) {
//                        chooseScenic.setText("景点选择");
//                    }
//                }
//                break;
            case 789://开启GPS回调
                locationUtil1.dismiss();
                if (APPUtils.isOPen(getActivity())){
                    getLocationCity();
                }else {
                    getCityMessageForNet("中国", "天津市");
                }

                break;
        }

    }


    /**
     * 选择出行人数的回调
     *
     * @param num
     */
    @Override
    public void personalCall(String num) {
        goPeopleNum.setText(num);
//        Toast.makeText(getActivity(), num, Toast.LENGTH_SHORT).show();
    }

    /**
     * 出行日期点击选择的回调
     *
     * @param num
     */
    @Override
    public void calendarCallBack(String num) {
        goDate.setText(num);
        //请求网络获取当季价格信息
        int month = Integer.valueOf(num.substring(5, 7));
        currenMonth = String.valueOf(month);
        //请求当季价格
        getPriceMsgForNet(currenMonth);

        Toast.makeText(getActivity(), num, Toast.LENGTH_SHORT).show();
    }


    /**
     * 出行时长选择的回调
     *
     * @param msg    msg
     * @param status 1代表小时 2代表天 0代表取消
     */
    @Override
    public void callBackString(String msg, int status) {

        if (!StringUtil.isEmpty(msg)) {
            travelTime = msg;//出游时长
            goDuration.setText(travelTime);
            travelType = String.valueOf(status);
        }
//        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 需求选择框（是否需要导游证、接单人性别要求、是否需要用车服务）的回调
     *
     * @param msg
     */
    @Override
    public void callBack(String msg) {
        if ("0".equals(demandType)) {
            //是否需要导游证件
            needGuide = msg;
            chooseGuideCar.setText(needGuide);
        } else if ("1".equals(demandType)) {
            //接单人性别回调
            pickupSex = msg;
            chooseSex.setText(pickupSex);
        } else if ("2".equals(demandType)) {
            //是否需要用车服务的回调
            needCar = msg;
            chooseCar.setText(needCar);
        }
//        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

//    //广播接收内部类
//    private class MyBroadcastReciver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals("clear")) {
//                //清除界面数据
//                clearMessage();
//
//            }
//        }
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void clear(ClearBean bean) {
        if (bean != null && bean.getClear().equals("clear")) {
            //清除界面数据
            clearMessage();
        }
    }

    /**
     * 清除界面信息
     */
    private void clearMessage() {
        initData();

        goPeopleNum.setText("1人");//出行人数

        goDuration.setText("小时/天");//游玩时长

        chooseGuideCar.setText("需要/否");//是否需要导游证

        chooseSex.setText("男/女");//接单人性别

        chooseCar.setText("需要/否");//是否车接

//        chooseScenic.setText("景点选择");//选择景点

        specific.setText("");//具体要求
    }


}
