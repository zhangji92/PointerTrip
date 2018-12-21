package yc.pointer.trip.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import yc.pointer.trip.R;
import yc.pointer.trip.adapter.NewOrderPreviewAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.GoTravelMesgBeanNew;
import yc.pointer.trip.bean.NewOrderpreviewMode;
import yc.pointer.trip.bean.OrderPreviewBean;
import yc.pointer.trip.bean.eventbean.ClearBean;
import yc.pointer.trip.event.OrderPreviewEvent;
import yc.pointer.trip.fragment.NewGoTravelFragment;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.FullyLinearLayoutManager;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.ToolbarWrapper;

/**
 * Created by moyan on 2018/7/9.
 * 2.0.2版本订单预览界面
 */

public class NewOrderPreviewActivity extends BaseActivity {


    @BindView(R.id.order_create_time)
    TextView orderCreateTime;//下单时间
    @BindView(R.id.destination)
    TextView destination;//目的城市
    @BindView(R.id.order_preview_recycle)
    RecyclerView orderPreviewRecycle;//订单信息列表
    @BindView(R.id.oreder_preview_commit)
    Button orederPreviewCommit;//提交按钮


    private String mUserId;//用户的id
    private String mDevCode;//设备识别码
    private long mTimeStamp;//时间戳


    private GoTravelMesgBeanNew goTravelMesgBean;//出发吧页面提交信息的对象
    private List<NewOrderpreviewMode> mListData = new ArrayList<>();//信息列表
    private NewOrderPreviewAdapter newOrderPreviewAdapter;
    private String[] orderPreviewTitle = {"景点名称", "出行人数", "出行时长", "出行日期", "导游证件",
            "用车服务", "接单性别", "具体要求", "计价规则", "订单总价"};
    private NewOrderpreviewMode newOrderpreviewMode;//列表对象
    private String orderContent = "";//订单信息具体内容
    private String orderpriceDestial = "";//分项价格
    private String travelType;// 出行类型  1代表小时 2代表天 0代表取消
    private String destinCity;//目的城市
    private String travelTime;//出行时长
    private String bid;


    @Override
    protected int getContentViewLayout() {

        return R.layout.activity_new_order_preview;
    }

    @Override
    protected void initView() {
        new ToolbarWrapper(this).setCustomTitle(R.string.preview_activity);

        //用户id
        mUserId = MyApplication.mApp.getUserId();
        //设备识别码
        mDevCode = MyApplication.mApp.getDevcode();
        //时间戳
        mTimeStamp = MyApplication.mApp.getTimestamp();


        //获取出发吧提交的信息
        goTravelMesgBean = NewGoTravelFragment.getGoTravelMesgBean();

        bid = getIntent().getStringExtra("bid");//预约订单需要的bid
        //设置数据
        initData();

        FullyLinearLayoutManager layoutManager = new FullyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        orderPreviewRecycle.setLayoutManager(layoutManager);
        newOrderPreviewAdapter = new NewOrderPreviewAdapter(mListData);
        orderPreviewRecycle.setAdapter(newOrderPreviewAdapter);


    }

    /**
     * 初始化数据
     */
    private void initData() {
        //获取出行类型
        travelType = goTravelMesgBean.getTravelType();
        //获取目的城市
        destinCity = goTravelMesgBean.getDestinCity();
        //获取出行时长
        travelTime = goTravelMesgBean.getTravelTime();
        String substring = StringUtil.RegNum(travelTime);//出行时长（数字）

        String nowTime = StringUtil.getNowTime();
        String format = String.format(getResources().getString(R.string.order_create_time), nowTime);
        if (!StringUtil.isEmpty(nowTime)) {
            orderCreateTime.setText(format);
        } else {
            orderCreateTime.setVisibility(View.GONE);
        }
        if (!StringUtil.isEmpty(destinCity)) {
            destination.setText(destinCity);
        }
        for (int i = 0; i < 10; i++) {
            newOrderpreviewMode = new NewOrderpreviewMode();
            newOrderpreviewMode.setOrderPreviewItemTitle(orderPreviewTitle[i]);//设置标题
            switch (i) {
                case 0:
                    orderContent = goTravelMesgBean.getScenic();//景点
                    orderpriceDestial = "";
                    break;
                case 1:
                    orderContent = goTravelMesgBean.getTravelperson();//出行人数
                    orderpriceDestial = "";
                    break;
                case 2:
                    orderContent = goTravelMesgBean.getTravelTime();//出行时长
                    orderpriceDestial = "";
                    break;

                case 3:
                    orderContent = goTravelMesgBean.getTravelDateTime();//出行日期
                    orderpriceDestial = "";
                    break;
                case 4:
                    orderContent = goTravelMesgBean.getGuideCar();//导游证件

                    if (travelType.equals("1")) {
                        //按小时
                        if (orderContent.equals("需要")) {
                            orderpriceDestial = goTravelMesgBean.getGuideByHourPrice(); //需要导游证的价格
                        } else {
                            orderpriceDestial = goTravelMesgBean.getNoGuideByHourPrice();//不需要导游证的价格
                        }
                    } else {
                        //按天结算
                        if (orderContent.equals("需要")) {
                            orderpriceDestial = goTravelMesgBean.getGuideByDayPrice(); //需要导游证的价格
                        } else {
                            orderpriceDestial = goTravelMesgBean.getNoGuideByDayPrice();//不需要导游证的价格
                        }
                    }
                    break;
                case 5:
                    orderContent = goTravelMesgBean.getNeedCar();//用车服务


                    if (orderContent.equals("需要")) {

                        if (travelType.equals("1")) {
                            //按小时
                            orderpriceDestial = goTravelMesgBean.getCarByHourPrice(); //需要用车的价格（小时）
                        }else if (travelType.equals("2")){
                            //按天
                            orderpriceDestial = goTravelMesgBean.getCarByDayPrice(); //需要用车的价格（天）
                        }
                    } else {
                        orderpriceDestial = "";
                    }


                    break;
                case 6:
                    orderContent = goTravelMesgBean.getPickupSex();//接单性别
                    orderpriceDestial = "";
                    break;
                case 7:
                    orderContent = goTravelMesgBean.getDemand();//具体要求
                    orderpriceDestial = "";
                    break;
                case 8:
                    orderContent = "";
                    orderpriceDestial = "";
                    break;
                case 9:
                    orderContent = "";
                    orderpriceDestial = "";
                    break;

            }
            newOrderpreviewMode.setOrderContent(orderContent);
            newOrderpreviewMode.setPrice(orderpriceDestial);
            newOrderpreviewMode.setTravelType(travelType);
            newOrderpreviewMode.setTimeLength(Integer.parseInt(substring));
            mListData.add(newOrderpreviewMode);
        }
    }

    @OnClick({R.id.oreder_preview_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.oreder_preview_commit:
                //提交订单信息到后台
                commitOrderMsg();
                orederPreviewCommit.setEnabled(false);
                orederPreviewCommit.setClickable(false);
                break;
        }
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

    /**
     * 提交订单
     */

    private void commitOrderMsg() {

        String ask = goTravelMesgBean.getDemand();//具体要求
        String travelperson = goTravelMesgBean.getTravelperson();//出行人数
        String guideCar = goTravelMesgBean.getGuideCar();//是否需要导游证
        String pickupSex = goTravelMesgBean.getPickupSex();
        if (!StringUtil.isEmpty(guideCar)) {
            if (guideCar.equals("需要")) {
                guideCar = "1";
            } else {
                guideCar = "0";
            }
        }

        String needCar = goTravelMesgBean.getNeedCar();//是否需要用车
        if (!StringUtil.isEmpty(needCar)) {
            if (needCar.equals("需要")) {
                needCar = "1";
            } else {
                needCar = "0";
            }
        }
        String travelDateTime = goTravelMesgBean.getTravelDateTime();//出发日期
        String scenic = goTravelMesgBean.getScenic();//景点


        boolean dtFlag = APPUtils.judgeTimeDev(this, mDevCode, mTimeStamp);
        if (dtFlag) {
            //参数名                类型              说明                        是否必填
            //ask                 String            具体要求                          是
            //amount                Int             出行人数                          是
            //bid                   Int             路数id（默认0）                   是
            //devcode              String         手机唯一识别码                      是
            //is_dao               int           是否要求导游证1是0否                 是
            //is_car：             int          是否需要用车1是0否                   是
            //jie_way               Int         接站属性：0=>无 1=>接机，2=>接站      是
            //jsex               String          接单人性别                           是
            //jtime              String          接站/机时间                          是
            //maddress           String             目的地                            是
            //saddress           String            出发地                             是
            //send_time          String         出行时间Ymd格式                       是
            //spot               String              景点                             是
            //time_num             int          玩多久。n天或n小时                    是
            //time_way             Int          计价规则1按小时2按天                  是
            //timestamp            String           当前时间戳                        是
            //uid                   Int               用户id                          是
            //signature            String             签名                            是

            String sign = Md5Utils.createMD5("amount=" + travelperson + "ask=" + ask + "bid=" + bid + "devcode="
                    + mDevCode + "is_car=" + needCar + "is_dao=" + guideCar + "jsex=" + pickupSex + "maddress=" + destinCity
                    + "send_time=" + travelDateTime + "spot=" + scenic + "time_num=" + travelTime + "time_way=" + travelType
                    + "timestamp=" + mTimeStamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("amount", travelperson)
                    .add("ask", ask)
                    .add("bid", bid)
                    .add("devcode", mDevCode)
                    .add("is_dao", guideCar)
                    .add("is_car", needCar)
                    .add("jsex", pickupSex)
                    .add("maddress", destinCity)
                    .add("send_time", travelDateTime)
                    .add("spot", scenic)
                    .add("time_num", travelTime)
                    .add("time_way", travelType)
                    .add("timestamp", String.valueOf(mTimeStamp))
                    .add("uid", mUserId)
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.COMMIT_ORDER_NEW, requestBody, new HttpCallBack(new OrderPreviewEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void commitOrderMsgBean(OrderPreviewEvent event) {
        if (event.isTimeOut()) {
            orederPreviewCommit.setEnabled(true);
            orederPreviewCommit.setClickable(true);
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        OrderPreviewBean bean = event.getData();
        if (bean.getStatus() == 0) {
            orederPreviewCommit.setEnabled(true);
            orederPreviewCommit.setClickable(true);
            ((MyApplication) getApplication()).setPayFlag(true);
            Intent intent = new Intent(this, MyOrderNewActivity.class);
            intent.putExtra("isGrab",false);
            intent.putExtra("oid", bean.getOid());
            intent.putExtra("flag", "orderPay");
//            Intent intent1 = new Intent();
//            intent1.setAction("clear");
//            sendBroadcast(intent1);
            EventBus.getDefault().post(new ClearBean("clear"));
            startActivity(intent);
            finish();
        } else {
            orederPreviewCommit.setEnabled(true);
            orederPreviewCommit.setClickable(true);
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }

}
