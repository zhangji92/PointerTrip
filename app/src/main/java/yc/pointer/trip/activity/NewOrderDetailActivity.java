package yc.pointer.trip.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import yc.pointer.trip.R;
import yc.pointer.trip.adapter.NewOrderDetailAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.base.BaseBean;
import yc.pointer.trip.bean.NewOrderpreviewMode;
import yc.pointer.trip.bean.OrderDetailBean;
import yc.pointer.trip.bean.eventbean.RefreshEarningAProfitBean;
import yc.pointer.trip.event.AppointmentEvent;
import yc.pointer.trip.event.OrderDetailEvent;
import yc.pointer.trip.event.OrderDetailGrabEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.FullyLinearLayoutManager;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.SharedPreferencesUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.DialogKnow;
import yc.pointer.trip.view.DialogSure;
import yc.pointer.trip.view.ToolbarWrapper;
import yc.pointer.trip.wxapi.PayOrderActivity;

/**
 * Created by moyan on 2018/7/10.
 * 2.0.2版本订单详情
 */

public class NewOrderDetailActivity extends BaseActivity {


    @BindView(R.id.satandard_toolbar_right)
    TextView satandardToolbarRight;
    @BindView(R.id.standard_toolbar)
    Toolbar standardToolbar;

    @BindView(R.id.order_create_time)
    TextView orderCode;//订单编号
    @BindView(R.id.destination)
    TextView destination;//目的城市
    @BindView(R.id.order_preview_recycle)
    RecyclerView orderDetailRecycle;//信息列表
    @BindView(R.id.oreder_preview_commit)
    Button orederDetailCommit;//按钮


    @BindView(R.id.order_person_message)
    TextView orderPersonMessage;// 发/接单人信息
    @BindView(R.id.order_nickname_content)
    TextView orderNicknameContent;//订单（发单方/接单方）的昵称
    @BindView(R.id.order_phone_content)
    TextView orderPhoneContent;//订单（发单方/接单方）的联系方式
    @BindView(R.id.star_qrcode)
    ImageView startQrcode;//开始订单的二维码
    @BindView(R.id.text_star_order)
    TextView textStartOrder;//扫描二维码开始订单的提示语，控制显隐


    private long mTimestamp;//时间戳
    private String mDevCode;//设备识别码
    private String mUserId;//用户Id
    private String mOid;//订单Id
    private String flag;//判断是从哪个页面进入的订单详情
    private boolean receiverFalg = true;//true 进抢单状态 false 进发单状态
    private boolean isTui = false;

    private List<NewOrderpreviewMode> mListData = new ArrayList<>();//信息列表
    private NewOrderDetailAdapter newOrderDetailAdapter;
    private String[] orderPreviewTitle = {"景点名称", "出行日期", "出行人数", "出行时长", "下单时间", "导游证件",
            "用车服务", "接单性别", "具体要求", "计价规则", "订单总价"};
    private NewOrderpreviewMode newOrderpreviewMode;//列表对象
    private boolean orderIsJieStatus;//判断该订单 是否 已经接单
    private String pbulishUid;
    private String ordStatus;


    @Override
    protected int getContentViewLayout() {

        orderIsJieStatus = getIntent().getBooleanExtra("orderIsJieStatus", false);

        if (orderIsJieStatus) {
            return R.layout.activity_new_order_preview_isjie;
        } else {
            return R.layout.activity_new_order_preview;
        }


    }

    @Override
    protected void initView() {

        new ToolbarWrapper(this).setCustomTitle(R.string.order_detail);
        satandardToolbarRight.setText("投诉");
        String format = String.format(getResources().getString(R.string.order_detail_number_new), "");
        orderCode.setText(format);

//        orederDetailCommit.setText("查看订单状态");


        mDevCode = MyApplication.mApp.getDevcode();
        mTimestamp = MyApplication.mApp.getTimestamp();
        mUserId = MyApplication.mApp.getUserId();
        //判断进入详情页面数据类型
        flag = getIntent().getStringExtra("flag");
        mOid = getIntent().getStringExtra("oid");
        isTui = getIntent().getBooleanExtra("tui", false);
//获取订单数据
        getOrderMsg();
        FullyLinearLayoutManager layoutManager = new FullyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        orderDetailRecycle.setLayoutManager(layoutManager);
        newOrderDetailAdapter = new NewOrderDetailAdapter(mListData);
        orderDetailRecycle.setAdapter(newOrderDetailAdapter);


    }

    @Override
    protected boolean needEventBus() {
        return true;
    }


    /**
     * 初始化数据
     */
    private void initData(OrderDetailBean orderDetailBean) {


        String orderContent = "";//订单内容信息
        String orderpriceDestial = "";//价格信息

        //获取出行类型
        String travelType = orderDetailBean.getData().getTime_way();//1
        //获取目的城市
        String destinCity = orderDetailBean.getData().getMaddress();
        //获取出行时长
        String travelTime = orderDetailBean.getData().getTime_num();

        String substring = StringUtil.RegNum(travelTime);//出行时长（数字）

        String addtime = orderDetailBean.getData().getNumber();
        String format = String.format(getResources().getString(R.string.order_detail_number_new), addtime);
        if (!StringUtil.isEmpty(addtime)) {
            orderCode.setText(format);
        } else {
            orderCode.setVisibility(View.GONE);
        }

        if (!StringUtil.isEmpty(destinCity)) {
            destination.setText(destinCity);
        }

        for (int i = 0; i < 11; i++) {
            newOrderpreviewMode = new NewOrderpreviewMode();
            newOrderpreviewMode.setOrderPreviewItemTitle(orderPreviewTitle[i]);//设置标题
            switch (i) {
                case 0:
                    orderContent = orderDetailBean.getData().getSpot();//景点
                    orderpriceDestial = "";
                    break;
                case 1:
                    orderContent = orderDetailBean.getData().getSend_time();//出行日期
                    orderpriceDestial = "";
                    break;
                case 2:
                    orderContent = orderDetailBean.getData().getAmount() + "人";//出行人数
                    orderpriceDestial = "";
                    break;
                case 3:
                    String time_num = orderDetailBean.getData().getTime_num();//出行时长
                    if (travelType.equals("1")) {
                        orderContent = time_num + "小时";
                    } else if (travelType.equals("2")) {
                        orderContent = time_num + "天";
                    }
                    orderpriceDestial = "";
                    break;

                case 4:
                    orderContent = orderDetailBean.getData().getAddtime();//下单时间
                    orderpriceDestial = "";
                    break;
                case 5:
                    String is_dao = orderDetailBean.getData().getIs_dao();//导游证件 1:需要 0：不需要
                    if (is_dao.equals("1")) {
                        orderContent = "需要";
                    } else if (is_dao.equals("0")) {
                        orderContent = "不需要";
                    }
                    orderpriceDestial = orderDetailBean.getData().getPrice();//导游证价格

                    break;
                case 6:
                    String is_car = orderDetailBean.getData().getIs_car();//用车服务
                    if (is_car.equals("1")) {
                        orderContent = "需要";
                        orderpriceDestial = orderDetailBean.getData().getCar_price(); //需要用车的价格
                    } else if (is_car.equals("0")) {
                        orderContent = "不需要";
                        orderpriceDestial = "";
                    }

                    break;
                case 7:
                    orderContent = orderDetailBean.getData().getJsex();//接单性别
                    orderpriceDestial = "";
                    break;
                case 8:
                    orderContent = orderDetailBean.getData().getAsk();//具体要求
                    orderpriceDestial = "";
                    break;
                case 9:
                    orderContent = "";
                    orderpriceDestial = "";
                    break;
                case 10:
                    orderContent = orderDetailBean.getData().getMoney();
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

    /**
     * 抢单 订单详情共用RequestBody
     *
     * @return
     */
    private RequestBody getRequestBody() {
        String sign = Md5Utils.createMD5("devcode=" + mDevCode + "oid=" + mOid + "timestamp=" + mTimestamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
        RequestBody builder = new FormBody.Builder().add("devcode", mDevCode)
                .add("oid", mOid)
                .add("timestamp", String.valueOf(mTimestamp))
                .add("uid", mUserId)
                .add("signature", sign)
                .build();
        return builder;
    }

    /**
     * 获取订单详情数据
     */
    private void getOrderMsg() {
        boolean judgeTimeDev = APPUtils.judgeTimeDev(this, mDevCode, mTimestamp);
        if (judgeTimeDev) {
            RequestBody requestBody = getRequestBody();
            OkHttpUtils.getInstance().post(URLUtils.ORDER_DETAIL, requestBody, new HttpCallBack(new OrderDetailEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void orderBean(OrderDetailEvent event) {
        if (event.isTimeOut()) {
//            mLoadDialog.dismiss();//取消动画
            orederDetailCommit.setClickable(true);
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        OrderDetailBean bean = event.getData();
        if (bean.getStatus() == 0) {
            //设置数据
            initData(bean);
            newOrderDetailAdapter.notifyDataSetChanged();

            //发单人Id
            pbulishUid = bean.getData().getUid();
            //订单状态
            ordStatus = bean.getData().getOrd_status();
            String pbulishNickname = bean.getData().getNickname();//发单人昵称
            String PbulishPhone = bean.getData().getPhone();//发单人联系方式
            String qNickname = bean.getData().getQ_nickname();//接单人昵称
            String qPhone = bean.getData().getQ_phone();//接单人联系方式
            String qrCodePic = bean.getData().getErwm_pic();

            if (mUserId.equals(pbulishUid)) {
                //从已发订单进入
                orderPersonMessage.setText("接单人信息");
                if (!StringUtil.isEmpty(qNickname)) {
                    orderNicknameContent.setText(qNickname);
                } else {
                    orderNicknameContent.setText("尚未接单，订单已过期");
                }
                if (!StringUtil.isEmpty(qPhone)) {
                    orderPhoneContent.setText(qPhone);
                } else {
                    orderNicknameContent.setText("订单过期，无法获取");
                }

                if (!StringUtil.isEmpty(qrCodePic)) {
                    OkHttpUtils.displayGlide(this, startQrcode, qrCodePic);//二维码图片
                    textStartOrder.setVisibility(View.VISIBLE);
                    if (ordStatus.equals("1")) {
                        textStartOrder.setText(getResources().getString(R.string.order_QRcode));
                    } else if (ordStatus.equals("2")) {
                        textStartOrder.setText(getResources().getString(R.string.order_stared));
                    } else {
                        textStartOrder.setText(getResources().getString(R.string.order_ed));
                    }
                } else {
                    textStartOrder.setVisibility(View.GONE);
                    startQrcode.setVisibility(View.GONE);
                }
            } else {
                //从已接订单进入
                orderPersonMessage.setText("发单人信息");
                orderNicknameContent.setText(pbulishNickname);
                orderPhoneContent.setText(PbulishPhone);
                textStartOrder.setVisibility(View.GONE);
                startQrcode.setVisibility(View.GONE);
            }

//            if (flag.equals("pay")) {
//                orederDetailCommit.setText("支付");
//            } else
            if (flag.equals("grabOrder")) {
                orederDetailCommit.setText("抢单");
            } else if (flag.equals("status")) {//从 我的订单页面进入的
                if (mUserId.equals(pbulishUid)) {
                    //从已发订单进入
                    setOrderCommitText(bean, ordStatus, "待付款");
                    receiverFalg = false;
                } else {
                    //从已接订单进入
                    setOrderCommitText(bean, ordStatus, "查看订单状态");
                    receiverFalg = true;
                }

            } else if (flag.equals("receiverGrabCancel")) {//推送页面 发单人取消后接受推送进抢单状态
                orederDetailCommit.setText("查看订单状态");
//                orederDetailCommit.setClickable(true);
                receiverFalg = true;
            } else if (flag.equals("receiverBill")) {//推送页面传递过来的进去发单状态（被抢订单的推送）
                orederDetailCommit.setText("待付款");
//                orederDetailCommit.setClickable(true);
                receiverFalg = false;
            } else if (flag.equals("receiverBillCancel")) {//推送页面 接单人取消进发单状态
                orederDetailCommit.setText("查看订单状态");
//                orederDetailCommit.setClickable(true);
                receiverFalg = false;
            } else if (flag.equals("appointment")) {//推送页面 预约进抢单状态
                orederDetailCommit.setText("查看订单状态");
//                orederDetailCommit.setClickable(true);
                receiverFalg = true;
            } else if (flag.equals("Gograb")) {//发单方已付款，推送给接单方查看状态
                orederDetailCommit.setText("查看订单状态");
//                orederDetailCommit.setClickable(true);
                receiverFalg = true;
            } else if (flag.equals("agree")) {//推送页面 预约进发单状态
                orederDetailCommit.setText("查看订单状态");
//                orederDetailCommit.setClickable(true);
                receiverFalg = false;
            } else if (flag.equals("no")) {//推送页面 预约进发单状态
                orederDetailCommit.setText("已取消");
//                orederDetailCommit.setClickable(false);
                receiverFalg = false;
            } else if (flag.equals("auto")) {//推送页面  订单自动开始/结束
                orederDetailCommit.setText("查看订单状态");
//                orederDetailCommit.setClickable(true);
                receiverFalg = false;
            }
//            mLoadDialog.dismiss();//取消动画
        } else {
//            mLoadDialog.dismiss();//取消动画
            orederDetailCommit.setClickable(true);
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }

    /**
     * 从我的订单进入的订单详情，设定按钮的提示语
     *
     * @param bean
     * @param ordStatus
     */
    private void setOrderCommitText(OrderDetailBean bean, String ordStatus, String textContent) {

        if (ordStatus.equals("0")) {//未接单
            orederDetailCommit.setText("待接单");
            orederDetailCommit.setClickable(false);
            orederDetailCommit.setEnabled(false);
//            orederDetailCommit.setBackgroundColor(getResources().getColor(R.color.verify_view));
        } else if (ordStatus.equals("1") || ordStatus.equals("2")) {//1未开始  2进行中
            orederDetailCommit.setText("查看订单状态");
            orederDetailCommit.setClickable(true);
            orederDetailCommit.setEnabled(true);
        } else if (ordStatus.equals("3")) { //3已结束待评价
            orederDetailCommit.setText("订单已结束");
            orederDetailCommit.setClickable(false);
            orederDetailCommit.setEnabled(false);
        } else if (ordStatus.equals("4")) {//3已过期
            orederDetailCommit.setText("订单已过期");
            orederDetailCommit.setClickable(false);
            orederDetailCommit.setEnabled(false);
        } else if (ordStatus.equals("5")) {//已取消
            String tui_status = bean.getData().getTui_status();
            if (isTui) {
                if (tui_status.equals("1")) {
                    orederDetailCommit.setText("待退款");
                } else if (tui_status.equals("2")) {
                    orederDetailCommit.setText("商家已退款");
                }
            } else {
                orederDetailCommit.setText("已取消");
            }
            orederDetailCommit.setClickable(false);
            orederDetailCommit.setEnabled(false);
        } else if (ordStatus.equals("6") || ordStatus.equals("7")) {
            orederDetailCommit.setText("已完成");
            orederDetailCommit.setClickable(false);
            orederDetailCommit.setEnabled(false);
        } else if (ordStatus.equals("8")) {//待付款
            orederDetailCommit.setText(textContent);
            orederDetailCommit.setClickable(true);
            orederDetailCommit.setEnabled(true);
        }
    }


    /**
     * 投诉 抢单 查看状态按钮的点击事件
     *
     * @param view
     */
    @OnClick({R.id.oreder_preview_commit, R.id.satandard_toolbar_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.oreder_preview_commit:
                //抢单按钮点击
                commitButtonClick();
                break;
            case R.id.satandard_toolbar_right:
                //投诉提交
                if (StringUtil.isEmpty(ordStatus) || ordStatus.equals("0") || ordStatus.equals("4")) {
                    //订单状态：0：未接单  4：过期（未接单）不可提交投诉
                    new DialogKnow(NewOrderDetailActivity.this, R.style.user_default_dialog, new DialogKnow.CallBackListener() {
                        @Override
                        public void onClickListener() {

                        }
                    })
                            .setTitle("温馨提示")
                            .setMsg("该订单尚未有人接单，无法对其进行投诉！")
                            .setPositiveButton("我知道了")
                            .show();
                } else {
                    Intent intent = new Intent(NewOrderDetailActivity.this, ComplainActivity.class);
                    intent.putExtra("oid", mOid);
                    startActivity(intent);
                }
                break;
        }

    }

    /**
     * 抢单 查看订单状态的点击逻辑
     */
    private void commitButtonClick() {
        String bntDetail = orederDetailCommit.getText().toString();
        if (bntDetail.equals("抢单")) {//从赚一赚页面过来的
            //TODo 避免多次请求
            grabOrder();//抢单状态
            orederDetailCommit.setClickable(false);
            orederDetailCommit.setEnabled(false);

        } else if (bntDetail.equals("待付款")) {//从已发订单页面过来的
            Intent intent = new Intent(NewOrderDetailActivity.this, PayOrderActivity.class);
            intent.putExtra("oid", mOid);
            startActivity(intent);
            finish();
        } else if (bntDetail.equals("查看订单状态")) {
            if (receiverFalg) {//进入到抢单状态页面
                if (!StringUtil.isEmpty(flag) && flag.equals("appointment")) {//预约的推送
                    if (!StringUtil.isEmpty(ordStatus) && ordStatus.equals("0")) {
                        new DialogSure(NewOrderDetailActivity.this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                            @Override
                            public void onClickListener(DialogSure dialogSure, boolean cancelable) {
                                if (cancelable) {//同意
                                    appointmentInterface(1);
                                } else {//拒绝
                                    appointmentInterface(2);
                                }
                            }
                        }).setTitle("恭喜您！").setMsg("有人对您发布的游记内容非常感兴趣，特预约与您一起畅游！").setPositiveButton("同意").setNegativeButton("拒绝").show();
                    } else {
                        Intent intent = new Intent(NewOrderDetailActivity.this, GrabOrderActivity.class);
                        intent.putExtra("oid", mOid);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Intent intent = new Intent(NewOrderDetailActivity.this, GrabOrderActivity.class);
                    intent.putExtra("oid", mOid);
                    startActivity(intent);
                    finish();
                }
            } else {//进入到发单状态页面
                if (!StringUtil.isEmpty(flag) && flag.equals("auto")) {//订单自动开始/结束的推送
                    if (!StringUtil.isEmpty(pbulishUid) && !StringUtil.isEmpty(mUserId)) {
                        if (pbulishUid.equals(mUserId)) {
                            Intent intent = new Intent(NewOrderDetailActivity.this, BillActivity.class);
                            intent.putExtra("oid", mOid);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(NewOrderDetailActivity.this, GrabOrderActivity.class);
                            intent.putExtra("oid", mOid);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        return;
                    }
                } else {
                    Intent intent = new Intent(NewOrderDetailActivity.this, BillActivity.class);
                    intent.putExtra("oid", mOid);
                    startActivity(intent);
                    finish();
                }

            }
        }
    }


    /**
     * 抢单接口
     */
    private void grabOrder() {
        boolean judgeTimeDev = APPUtils.judgeTimeDev(this, mDevCode, mTimestamp);
        if (judgeTimeDev) {
            RequestBody requestBody = getRequestBody();
            OkHttpUtils.getInstance().post(URLUtils.NEW_MAKE_MONEY_GRAD, requestBody, new HttpCallBack(new OrderDetailGrabEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void grabOrderBean(OrderDetailGrabEvent event) {
        if (event.isTimeOut()) {
            orederDetailCommit.setClickable(true);
            orederDetailCommit.setEnabled(true);
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        BaseBean bean = event.getData();
        if (bean.getStatus() == 0) {
            orederDetailCommit.setClickable(true);
            orederDetailCommit.setEnabled(true);
            Intent intent = new Intent(NewOrderDetailActivity.this, GrabOrderActivity.class);
            intent.putExtra("oid", mOid);
            EventBus.getDefault().post(new RefreshEarningAProfitBean("刷新赚一赚"));
            //Intent intentBroadcast = new Intent();
            //intentBroadcast.setAction("刷新赚一赚");
            //sendBroadcast(intentBroadcast);
            startActivity(intent);
            finish();
        } else {
            orederDetailCommit.setClickable(true);
            orederDetailCommit.setEnabled(true);
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }

    }


    /**
     * 预约同意接口
     *
     * @param type
     */
    private void appointmentInterface(int type) {
        boolean judgeTimeDev = APPUtils.judgeTimeDev(this, mDevCode, mTimestamp);
        if (judgeTimeDev) {
            String sign = Md5Utils.createMD5("devcode=" + mDevCode + "oid=" + mOid + "timestamp=" + mTimestamp + "type=" + type + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody builder = new FormBody.Builder().add("devcode", mDevCode)
                    .add("oid", mOid)
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("type", String.valueOf(type))
                    .add("uid", mUserId)
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.APPOINTMENT_URL, builder, new HttpCallBack(new AppointmentEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void appointmentInterface(AppointmentEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        BaseBean bean = event.getData();
        if (bean.getStatus() == 0) {
            if (bean.getMsg().equals("同意")) {//同意跳转的界面
                Intent intent = new Intent(NewOrderDetailActivity.this, GrabOrderActivity.class);
                intent.putExtra("oid", mOid);
                startActivity(intent);
                finish();
            } else {//拒绝跳转的界面
                Intent intent = new Intent(NewOrderDetailActivity.this, MyReserveActivity.class);
                intent.putExtra("orderPushFlag", false);//预约订单拒绝之后显示预约我的界面
                startActivity(intent);
                finish();
            }
        } else {
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }


}
