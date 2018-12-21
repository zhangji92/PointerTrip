package yc.pointer.trip.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import okhttp3.FormBody;
import okhttp3.RequestBody;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import yc.pointer.trip.R;
import yc.pointer.trip.adapter.OrderPreviewAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.GoTravelMesgBean;
import yc.pointer.trip.bean.OrderPreviewBean;
import yc.pointer.trip.bean.OrderPreviewMode;
import yc.pointer.trip.bean.eventbean.ClearBean;
import yc.pointer.trip.event.OrderPreviewEvent;
import yc.pointer.trip.fragment.GoTravelFragment;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.ToolbarWrapper;
import yc.pointer.trip.wxapi.PayOrderActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 张继
 * 2017/7/10
 * 14:12
 * 预览订单
 */
public class OrderPreviewActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.preview_scenic)
    ListView mListView;
    private List<OrderPreviewMode> traceList = new ArrayList<>(10);
    private OrderPreviewAdapter adapter;
    private TextView start_point;//出发地
    private TextView start_destination;//目的地
    private TextView preview_time;//发单时间
    private Button bnt_recompose;//修改订单
    private Button bnt_commit;//提交订单
    private String mUserId;//用户的id
    private String mDevCode;//设备识别码
    private long mTimeStamp;//时间戳
    private String orderScenic;//订单景点
    private String orderTime;//订单时间
    private int orderPersonNum;//人数
    private String orderRealProperty;//接站属性
    private String orderSex;//性别
    private String orderGuide;//导游证
    private String orderGuidePic;//导游证价钱
    private String orderValuation;//计价规则
    private String orderValuationPic;//计价价格
    private String orderSpecific;//具体要求
    private String orderTotal;//订单总额
    private String bid;//
    //private String orderStationTime;//接站时间
    //private String pickupPrice;
    private GoTravelMesgBean goTravelMesgBean;
    private int guideFlag;//导游证 1需要 0不需要
    private int pickupType;//接待方式 0不需要 1接机 2接站
    private int pickType;//计价规则 1小时 2天
    private GoTravelFragment goTravelFragment;


    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_order_preview;
    }

    @Override
    protected void initView() {
        new ToolbarWrapper(this).setCustomTitle(R.string.preview_activity);


        //用户id
        mUserId = ((MyApplication) getApplication()).getUserId();
        //设备识别码
        mDevCode = ((MyApplication) getApplication()).getDevcode();
        //时间戳
        mTimeStamp = ((MyApplication) getApplication()).getTimestamp();
        goTravelMesgBean = GoTravelFragment.getGoTravelMesgBean();
        bid = getIntent().getStringExtra("bid");

        String currentCity = goTravelMesgBean.getCurrentCity();//出发地
        String destinCity = goTravelMesgBean.getDestinCity();//目的地
        //景点
        orderScenic = goTravelMesgBean.getScenic();
        //出行日期
        orderTime = goTravelMesgBean.getTravelDateTime();
        //人数
        orderPersonNum = Integer.valueOf(goTravelMesgBean.getTravelperson());
        //接待方式
        //if (goTravelMesgBean.getPickupType().equals("")) {
        //    orderRealProperty = "无";
        //    pickupType = 0;
        //} else if (goTravelMesgBean.getPickupType().equals("1")) {
        //    pickupType = 1;
        //    orderRealProperty = "接机";
        //} else if (goTravelMesgBean.getPickupType().equals("2")) {
        //    pickupType = 2;
        //    orderRealProperty = "接站";
        //}
        //orderStationTime = goTravelMesgBean.getStartTime();//接待时间
        //计价方式
        if (goTravelMesgBean.getTraveltype().equals("1")) {
            //导游证价钱
            pickType = 1;
            orderGuidePic = goTravelMesgBean.getGuidePrice() + "元/小时";
            orderValuation = "按小时计费" + goTravelMesgBean.getTravelTime() + "小时";//计价规则
        } else {
            pickType = 2;
            //导游证价钱
            orderGuidePic = goTravelMesgBean.getGuidePrice() + "元/天";
            orderValuation = "按天计费" + goTravelMesgBean.getTravelTime() + "天";//计价规则
        }
        //接单人性别
        orderSex = goTravelMesgBean.getPickupSex();
        //导游证
        if (goTravelMesgBean.getIsNeedGuide().equals("1")) {
            guideFlag = 1;
            orderGuide = "需要";
        } else {
            guideFlag = 0;
            orderGuide = "不需要";
        }


        int guide = Integer.valueOf(goTravelMesgBean.getGuidePrice());
        int valuation = Integer.valueOf(goTravelMesgBean.getTravelTime());
        orderValuationPic = String.valueOf(guide * valuation) + "元";//计价价钱
        orderSpecific = goTravelMesgBean.getDemand();//具体要求
        //int price = 0;
        //if (!goTravelMesgBean.getPickupPrice().equals("")) {
        //    price = Integer.valueOf(goTravelMesgBean.getPickupPrice());//接待价格
        //}
        orderTotal = String.valueOf(guide * valuation) + "元";//订单总额

        traceList.add(new OrderPreviewMode("景点", orderScenic, ""));
        traceList.add(new OrderPreviewMode("出行日期", orderTime, ""));
        traceList.add(new OrderPreviewMode("出行人数", String.valueOf(orderPersonNum), ""));
        //if (!orderRealProperty.equals("无")) {
        //    traceList.add(new OrderPreviewMode("接待方式", orderRealProperty, ""));
        //    traceList.add(new OrderPreviewMode("接站时间", orderStationTime, ""));
        //    traceList.add(new OrderPreviewMode("接待价格", pickupPrice, ""));
        //}
        if (!orderSex.equals("")) {
            traceList.add(new OrderPreviewMode("接单人性别", orderSex, ""));
        }
        traceList.add(new OrderPreviewMode("导游证件", orderGuide, orderGuidePic));
        traceList.add(new OrderPreviewMode("计价规则", orderValuation, orderValuationPic));
        traceList.add(new OrderPreviewMode("具体要求", orderSpecific, ""));
        traceList.add(new OrderPreviewMode("订单总额", "", orderTotal));
        adapter = new OrderPreviewAdapter(traceList);
        //头布局
        View viewHead = View.inflate(this, R.layout.order_preview_head, null);
        //出发地
        start_point = (TextView) viewHead.findViewById(R.id.preview_start_point);
        //目的地
        start_destination = (TextView) viewHead.findViewById(R.id.preview_start_destination);
        //发单时间
        preview_time = (TextView) viewHead.findViewById(R.id.preview_time);
        //尾布局
        View viewFoot = View.inflate(this, R.layout.order_preview_foot, null);
        preview_time.setText(StringUtil.getNowTime());//发单时间
        start_point.setText(currentCity);//出发地
        start_destination.setText(destinCity);//目的地
        bnt_commit = (Button) viewFoot.findViewById(R.id.preview_bnt);//提交
        bnt_commit.setOnClickListener(this);
        bnt_recompose = (Button) viewFoot.findViewById(R.id.recompose_order);//修改
        bnt_recompose.setOnClickListener(this);
        mListView.addHeaderView(viewHead);
        mListView.addFooterView(viewFoot);
        mListView.setAdapter(adapter);
    }


    @Override
    protected boolean needEventBus() {
        return true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == bnt_commit.getId()) {//提交订单
            bnt_commit.setEnabled(false);
            commitOrderMsg();
        }
        if (id == bnt_recompose.getId()) {//修改订单
            setResult(3);
            finish();
        }
    }

    /**
     * 提交订单
     */
    private void commitOrderMsg() {
        String timeNum = StringUtil.RegNum(orderValuation);
        String orderStartPoint = start_point.getText().toString();
        String orderDestination = start_destination.getText().toString();
        boolean dtFlag = APPUtils.judgeTimeDev(this, mDevCode, mTimeStamp);
        if (dtFlag) {
            //参数名                类型              说明                        是否必填
            //ask                 String            具体要求                          是
            //amount                Int             出行人数                          是
            //bid                   Int             路数id（默认0）                   是
            //devcode              String         手机唯一识别码                      是
            //is_dao               int           是否要求导游证1是0否                 是
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

            String sign = Md5Utils.createMD5("amount=" + orderPersonNum + "ask=" + orderSpecific + "bid=" + bid + "devcode="
                    + mDevCode + "is_dao=" + guideFlag + "jie_way=" + pickupType + "jsex=" + orderSex + "jtime=" + "" + "maddress=" + orderDestination + "saddress=" + orderStartPoint
                    + "send_time=" + orderTime + "spot=" + orderScenic + "time_num=" + timeNum + "time_way=" + pickType + "timestamp=" + mTimeStamp
                    + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("amount", String.valueOf(orderPersonNum))
                    .add("ask", orderSpecific)
                    .add("bid", bid)
                    .add("devcode", mDevCode)
                    .add("is_dao", String.valueOf(guideFlag))
                    .add("jie_way", String.valueOf(pickupType))
                    .add("jsex", orderSex)
                    .add("jtime", "")
                    .add("maddress", orderDestination)
                    .add("saddress", orderStartPoint)
                    .add("send_time", orderTime)
                    .add("spot", orderScenic)
                    .add("time_num", timeNum)
                    .add("time_way", String.valueOf(pickType))
                    .add("timestamp", String.valueOf(mTimeStamp))
                    .add("uid", mUserId)
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.COMMIT_ORDER, requestBody, new HttpCallBack(new OrderPreviewEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void commitOrderMsgBean(OrderPreviewEvent event) {

        if (event.isTimeOut()) {
            bnt_commit.setEnabled(true);
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        OrderPreviewBean bean = event.getData();
        if (bean.getStatus() == 0) {

            bnt_commit.setEnabled(true);
            ((MyApplication) getApplication()).setPayFlag(true);
            Intent intent = new Intent(this, PayOrderActivity.class);
            intent.putExtra("oid", bean.getOid());
            intent.putExtra("flag", "orderPay");
            //Intent intent1 = new Intent();
            //intent1.setAction("clear");
            //sendBroadcast(intent1);
            EventBus.getDefault().post(new ClearBean("clear"));
            startActivity(intent);
            finish();
        } else {
            bnt_commit.setEnabled(true);
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }

}
