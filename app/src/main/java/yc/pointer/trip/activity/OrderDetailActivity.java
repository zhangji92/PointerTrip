package yc.pointer.trip.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import yc.pointer.trip.R;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.base.BaseBean;
import yc.pointer.trip.bean.OrderDetailBean;
import yc.pointer.trip.bean.eventbean.RefreshEarningAProfitBean;
import yc.pointer.trip.event.AppointmentEvent;
import yc.pointer.trip.event.DeleteBookEvent;
import yc.pointer.trip.event.OrderDetailEvent;
import yc.pointer.trip.event.OrderDetailGrabEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.SharedPreferencesUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.DialogKnow;
import yc.pointer.trip.view.DialogSure;
import yc.pointer.trip.view.LoadDialog;
import yc.pointer.trip.view.ToolbarWrapper;
import yc.pointer.trip.wxapi.PayOrderActivity;

/**
 * Created by 刘佳伟
 * 2017/7/18
 * 16:43
 * 订单详情
 */
public class OrderDetailActivity extends BaseActivity {
    @BindView(R.id.grab_order_detail)
    Button grabOrderDetail;//提交订单
    @BindView(R.id.order_detail_num)
    TextView orderDetailNum;//订单编号
    @BindView(R.id.order_detail_start)
    TextView orderDetailStart;//出发地
    @BindView(R.id.order_detail_start_english)
    TextView orderDetailStartEnglish;//出发地拼音
    @BindView(R.id.order_detail_end_english)
    TextView orderDetailEndEnglish;//目的地拼音
    @BindView(R.id.order_detail_end)
    TextView orderDetailEnd;//目的地
    @BindView(R.id.order_detail_nick)
    TextView orderDetailNick;//昵称
    @BindView(R.id.order_detail_phone)
    TextView orderDetailPhone;//手机号
    @BindView(R.id.order_detail_date)
    TextView orderDetailDate;//出行时间
    @BindView(R.id.order_detail_person)
    TextView orderDetailPerson;//人数
    @BindView(R.id.order_detail_time)
    TextView orderDetailTime;//游玩时间
    @BindView(R.id.order_detail_place_date)
    TextView orderDetailPlaceDate;//下单时间
    @BindView(R.id.order_detail_sex)
    TextView orderDetailSex;//接单人性别
    @BindView(R.id.order_detail_guide)
    TextView orderDetailGuide;//导游证是否需求
    @BindView(R.id.order_detail_total)
    TextView orderDetailTotal;//总额
    @BindView(R.id.satandard_toolbar_right)
    TextView satandardToolbarRight;
    @BindView(R.id.bottom_img_order)
    ImageView bottomImgOrder;
    @BindView(R.id.requirement_text)
    TextView requirement;//具体要求
    private long mTimestamp;
    private String mDevCode;
    private String mUserId;
    private String mOid;
    private String flag;
    private boolean isTui = false;
    private boolean receiverFalg = true;//true进抢单状态 false进发单状态
    private LoadDialog mLoadDialog;
    private String ordStatus;
    private String pbulishUid;//发单人ID
    private boolean isDeposited;
    private String bankNum;


    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_oeder_detail;
    }

    @Override
    protected void initView() {
        //开启动画
        mLoadDialog = new LoadDialog(this, "正在加载...", R.drawable.load_animation);
        mLoadDialog.show();
        ToolbarWrapper toolbarWrapper = new ToolbarWrapper(this);
        toolbarWrapper.setCustomTitle(R.string.order_detail);
        //TODO 投诉页面没做，点击也没做
        toolbarWrapper.setRightText(R.string.bill_order_title_complaint);

        mDevCode = ((MyApplication) getApplication()).getDevcode();
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();
        mUserId = ((MyApplication) getApplication()).getUserId();
        //判断进入详情页面数据类型
        flag = getIntent().getStringExtra("flag");
        mOid = getIntent().getStringExtra("oid");
        isTui = getIntent().getBooleanExtra("tui", false);
        getOrderMsg();//请求网络数据
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
            mLoadDialog.dismiss();//取消动画
            grabOrderDetail.setClickable(true);
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        OrderDetailBean bean = event.getData();
        if (bean.getStatus() == 0) {
            String scenic = bean.getData().getSpot();//景点
            orderDetailEnd.setText(bean.getData().getMaddress());
            orderDetailStart.setText(bean.getData().getSaddress());
            orderDetailNum.setText(bean.getData().getNumber());
            orderDetailNick.setText(bean.getData().getReal_name());
            orderDetailPhone.setText(bean.getData().getPhone());
            orderDetailDate.setText(bean.getData().getSend_time());
            orderDetailPerson.setText(bean.getData().getAmount());
            pbulishUid = bean.getData().getUid();
            ordStatus = bean.getData().getOrd_status();
            String ask = bean.getData().getAsk();
            if (!StringUtil.isEmpty(ask)) {
                requirement.setText(ask);
            } else {
                requirement.setText("无");
            }
            if (bean.getData().getTime_way().equals("1")) {
                orderDetailTime.setText(bean.getData().getTime_num() + "小时");
            } else if (bean.getData().getTime_way().equals("2")) {
                orderDetailTime.setText(bean.getData().getTime_num() + "天");
            }
            orderDetailPlaceDate.setText(bean.getData().getAddtime());
            //出发地英文
            orderDetailStartEnglish.setText(bean.getData().getS_pinyin().toUpperCase());
            //目的地英文
            orderDetailEndEnglish.setText(bean.getData().getM_pinyin().toUpperCase());
            //if (bean.getData().getJie_way().equals("0")){
            //    orderDetailType.setText("无");
            //}else if(bean.getData().getJie_way().equals("1")){
            //    orderDetailType.setText("接机");
            //}else if (bean.getData().getJie_way().equals("2")){
            //    orderDetailType.setText("接站");
            //}
            orderDetailSex.setText(bean.getData().getJsex());
            if (bean.getData().getIs_dao().equals("0")) {
                orderDetailGuide.setText("不需要");
            } else if (bean.getData().getIs_dao().equals("1")) {
                orderDetailGuide.setText("需要");
            }
            orderDetailTotal.setText(bean.getData().getMoney_y());
            if (flag.equals("pay")) {
                grabOrderDetail.setText("支付");
            } else if (flag.equals("grabOrder")) {
                grabOrderDetail.setText("抢单");
            } else if (flag.equals("status")) {
                if (ordStatus.equals("0") || ordStatus.equals("1") || ordStatus.equals("2")) {
                    receiverFalg = true;
                    grabOrderDetail.setText("查看订单状态");
                    grabOrderDetail.setClickable(true);
                } else {
                    if (ordStatus.equals(3)) {
                        grabOrderDetail.setText("订单已结束");
                    } else if (ordStatus.equals("4")) {
                        grabOrderDetail.setText("订单已过期");
                    } else if (ordStatus.equals("5")) {
                        String tui_status = bean.getData().getTui_status();
                        if (isTui) {
                            if (tui_status.equals("1")) {
                                grabOrderDetail.setText("待退款");
                            } else if (tui_status.equals("2")) {
                                grabOrderDetail.setText("商家已退款");
                            }
                        } else {
                            grabOrderDetail.setText("已取消");
                        }
                    } else {
                        grabOrderDetail.setText("已完成");
                    }
                    grabOrderDetail.setClickable(false);
                }
            } else if (flag.equals("receiverGrabCancel")) {//推送页面 发单人取消后接受推送进抢单状态
                grabOrderDetail.setText("查看订单状态");
                grabOrderDetail.setClickable(true);
                receiverFalg = true;
            } else if (flag.equals("receiverBill")) {//推送页面传递过来的进去发单状态（被抢订单的推送）
                grabOrderDetail.setText("查看订单状态");
                grabOrderDetail.setClickable(true);
                receiverFalg = false;
            } else if (flag.equals("receiverBillCancel")) {//推送页面 接单人取消进发单状态
                grabOrderDetail.setText("查看订单状态");
                grabOrderDetail.setClickable(true);
                receiverFalg = false;
            } else if (flag.equals("appointment")) {//推送页面 预约进抢单状态
                grabOrderDetail.setText("查看订单状态");
                grabOrderDetail.setClickable(true);
                receiverFalg = true;
            } else if (flag.equals("agree")) {//推送页面 预约进发单状态
                grabOrderDetail.setText("查看订单状态");
                grabOrderDetail.setClickable(true);
                receiverFalg = false;
            } else if (flag.equals("no")) {//推送页面 预约进发单状态
                grabOrderDetail.setText("已取消");
                grabOrderDetail.setClickable(false);
                receiverFalg = false;
            } else if (flag.equals("auto")) {//推送页面  订单自动开始/结束
                grabOrderDetail.setText("查看订单状态");
                grabOrderDetail.setClickable(true);
                receiverFalg = false;
            }
            mLoadDialog.dismiss();//取消动画
        } else {
            mLoadDialog.dismiss();//取消动画
            grabOrderDetail.setClickable(true);
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }

    /**
     * 抢单接口
     */
    private void grabOrder() {
        boolean judgeTimeDev = APPUtils.judgeTimeDev(this, mDevCode, mTimestamp);
        if (judgeTimeDev) {
            RequestBody requestBody = getRequestBody();
            OkHttpUtils.getInstance().post(URLUtils.MAKE_MONEY_GRAD, requestBody, new HttpCallBack(new OrderDetailGrabEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void grabOrderBean(OrderDetailGrabEvent event) {
        if (event.isTimeOut()) {
            grabOrderDetail.setClickable(true);
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        BaseBean bean = event.getData();
        if (bean.getStatus() == 0) {
            grabOrderDetail.setClickable(true);
            Intent intent = new Intent(OrderDetailActivity.this, GrabOrderActivity.class);
            intent.putExtra("oid", mOid);
            EventBus.getDefault().post(new RefreshEarningAProfitBean("刷新赚一赚"));
            //Intent intentBroadcast = new Intent();
            //intentBroadcast.setAction("刷新赚一赚");
            //sendBroadcast(intentBroadcast);
            startActivity(intent);
            finish();
        } else {
            grabOrderDetail.setClickable(true);
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }

    }


    /**
     * 抢单 订单详情共用RequestBody
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
                Intent intent = new Intent(OrderDetailActivity.this, GrabOrderActivity.class);
                intent.putExtra("oid", mOid);
                startActivity(intent);
                finish();
            } else {//拒绝跳转的界面
                Intent intent = new Intent(OrderDetailActivity.this, MyReserveActivity.class);
                intent.putExtra("orderPushFlag", false);//预约订单拒绝之后显示预约我的界面
                startActivity(intent);
                finish();
            }
        } else {
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

    @OnClick({R.id.grab_order_detail, R.id.satandard_toolbar_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.grab_order_detail:
                String bntDetail = grabOrderDetail.getText().toString();
                if (bntDetail.equals("抢单")) {
                    //TODo 避免多次请求
                    grabOrderDetail.setClickable(false);
                    String jie = ((MyApplication) getApplication()).getUserBean().getIs_jie();
                    isDeposited = SharedPreferencesUtils.getInstance().getBoolean(OrderDetailActivity.this, "isPayDeposit");
//                    //是否提交过银行卡
//                    bankNum = ((MyApplication) getApplication()).getUserBean().getBank_num();

                    if (!StringUtil.isEmpty(jie)&&jie.equals("2")){
                        if (isDeposited){
                            //交过押金
                            grabOrder();//抢单状态
                        }else {
                            //未交押金
                            //已经认证，未缴纳押金，弹框跳转押金
                            new DialogSure(OrderDetailActivity.this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                                @Override
                                public void onClickListener(DialogSure dialogSure, boolean cancelable) {
                                    if (cancelable) {
                                        Intent intent = new Intent(OrderDetailActivity.this, NewUnDepositActivity.class);
                                        startActivity(intent);
                                    } else {
                                    }
                                }
                            }).setTitle("温馨提示").setMsg("您尚未充值年费,不能完成抢单操作").setPositiveButton("去充值").setNegativeButton("那算了").show();

                        }

                    }else if (jie.equals("1")){
                        //审核中
                        new DialogKnow(OrderDetailActivity.this, R.style.user_default_dialog, new DialogKnow.CallBackListener() {
                            @Override
                            public void onClickListener() {

                            }
                        }).setTitle("温馨提示").setMsg("您提交的会员认证信息正在火速审核中,暂不可进行抢单操作，请耐心等待").setPositiveButton("我知道了").show();

                    }else if (jie.equals("3")){
                        //审核失败
                        new DialogSure(OrderDetailActivity.this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                            @Override
                            public void onClickListener(DialogSure dialogSure, boolean cancelable) {
                                if (cancelable) {

                                    Intent intent = new Intent(OrderDetailActivity.this, VerifyActivity.class);
                                    startActivity(intent);

                                } else {

                                }
                            }
                        }).setTitle("非常抱歉").setMsg("您的会员升级申请失败，请重新完成会员身份认证").setPositiveButton("前往").setNegativeButton("放弃").show();

                    }else {
                        //未提交信息
                        new DialogSure(OrderDetailActivity.this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                            @Override
                            public void onClickListener(DialogSure dialogSure, boolean cancelable) {
                                if (cancelable) {
                                    //去认证
                                    startActivity(new Intent(OrderDetailActivity.this, VerifyActivity.class));
                                }
                            }
                        }).setTitle("温馨提示").setMsg("您尚未升级成为指针会员，请先去认证会员身份，方可完成抢单操作").setPositiveButton("前往").setNegativeButton("取消").show();

                    }


//                    if (isDeposited&&!StringUtil.isEmpty(bankNum)) {
//                        //交过押金，判断身份信息是否通过
//                        if (!StringUtil.isEmpty(jie) && jie.equals("2")) {
//                            grabOrder();//抢单状态
//                        } else {
//                            new DialogKnow(OrderDetailActivity.this, R.style.user_default_dialog, new DialogKnow.CallBackListener() {
//                                @Override
//                                public void onClickListener() {
//
//                                }
//                            }).setTitle("温馨提示").setMsg("您的身份信息正在加急审核当中,请您耐心等待").setPositiveButton("我知道了").show();
//                        }
//
//                    } else {
//                        //未缴押金
//                        if (!StringUtil.isEmpty(bankNum)) {
//                            //已提交身份
//                            //已经认证，未缴纳押金，弹框跳转押金
//                            new DialogSure(OrderDetailActivity.this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
//                                @Override
//                                public void onClickListener(DialogSure dialogSure, boolean cancelable) {
//                                    if (cancelable) {
//
//                                        Intent intent = new Intent(OrderDetailActivity.this, UnDepositActivity.class);
//                                        startActivity(intent);
//
//                                    } else {
//
//
//                                    }
//                                }
//                            }).setTitle("温馨提示").setMsg("您尚未缴纳押金,不能完成抢单操作").setPositiveButton("交押金").setNegativeButton("那算了").show();
//
//                        } else {
//                            //尚未提交身份信息
//                            new DialogSure(OrderDetailActivity.this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
//                                @Override
//                                public void onClickListener(DialogSure dialogSure, boolean cancelable) {
//                                    if (cancelable) {
//                                        //去认证
//                                        startActivity(new Intent(OrderDetailActivity.this, VerifyActivity.class));
//                                    }
//                                }
//                            }).setTitle("温馨提示").setMsg("您尚未完成实名认证，请前往【指针认证】完善认证信息").setPositiveButton("前往").setNegativeButton("取消").show();
//
//                        }
//                    }


//                    if (jie.equals("2")) {
//                        grabOrder();//抢单状态
//                    } else {
//                        // 身份审核未通过，调到认证界面
//                        new DialogSure(OrderDetailActivity.this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
//                            @Override
//                            public void onClickListener(DialogSure dialogSure, boolean cancelable) {
//                                if (cancelable) {
//                                    //去认证
//                                    startActivity(new Intent(OrderDetailActivity.this, VerifyActivity.class));
//                                }
//                            }
//                        }).setMsg("您尚未完成实名认证，请前往【指针认证】完善认证信息,如已提交，请耐心等待认证结果").setPositiveButton("前往").setNegativeButton("取消").show();
//                    }


                } else if (bntDetail.equals("支付")) {
                    Intent intent = new Intent(OrderDetailActivity.this, PayOrderActivity.class);
                    intent.putExtra("oid", mOid);
                    startActivity(intent);
                    finish();
                } else if (bntDetail.equals("查看订单状态")) {
                    if (receiverFalg) {
                        if (!StringUtil.isEmpty(flag) && flag.equals("appointment")) {
                            if (!StringUtil.isEmpty(ordStatus) && ordStatus.equals("0")) {
                                new DialogSure(OrderDetailActivity.this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                                    @Override
                                    public void onClickListener(DialogSure dialogSure, boolean cancelable) {
                                        if (cancelable) {//同意
                                            appointmentInterface(1);
                                        } else {//拒绝
                                            appointmentInterface(2);
                                        }
                                    }
                                }).setTitle("恭喜您！").setMsg("您的发布的游记已被预约").setPositiveButton("同意").setNegativeButton("拒绝").show();
                            } else {
                                Intent intent = new Intent(OrderDetailActivity.this, GrabOrderActivity.class);
                                intent.putExtra("oid", mOid);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Intent intent = new Intent(OrderDetailActivity.this, GrabOrderActivity.class);
                            intent.putExtra("oid", mOid);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        if (!StringUtil.isEmpty(flag) && flag.equals("auto")) {
                            if (!StringUtil.isEmpty(pbulishUid) && !StringUtil.isEmpty(mUserId)) {
                                if (pbulishUid.equals(mUserId)) {
                                    Intent intent = new Intent(OrderDetailActivity.this, BillActivity.class);
                                    intent.putExtra("oid", mOid);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Intent intent = new Intent(OrderDetailActivity.this, GrabOrderActivity.class);
                                    intent.putExtra("oid", mOid);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                return;
                            }
                        } else {
                            Intent intent = new Intent(OrderDetailActivity.this, BillActivity.class);
                            intent.putExtra("oid", mOid);
                            startActivity(intent);
                            finish();
                        }

                    }
                }
                break;
            case R.id.satandard_toolbar_right:
                if (StringUtil.isEmpty(ordStatus) || ordStatus.equals("0") || ordStatus.equals("4")) {
                    //订单状态：0：未接单  4：过期（未接单）不可提交投诉
                    new DialogKnow(OrderDetailActivity.this, R.style.user_default_dialog, new DialogKnow.CallBackListener() {
                        @Override
                        public void onClickListener() {

                        }
                    }).setMsg("该订单还未接单，无法对其进行投诉！").setPositiveButton("我知道了").show();
                } else {

                    Intent intent = new Intent(OrderDetailActivity.this, ComplainActivity.class);
                    intent.putExtra("oid", mOid);
                    startActivity(intent);

                }
                break;
        }

    }


}
