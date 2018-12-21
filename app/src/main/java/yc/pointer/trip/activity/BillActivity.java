package yc.pointer.trip.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import okhttp3.FormBody;
import okhttp3.RequestBody;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import yc.pointer.trip.R;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.base.BaseBean;
import yc.pointer.trip.bean.BillBean;
import yc.pointer.trip.bean.BillCommentBean;
import yc.pointer.trip.bean.OrderDetailBean;
import yc.pointer.trip.bean.eventbean.OrderNotifyBean;
import yc.pointer.trip.event.*;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.PermissionHelper;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.*;

/**
 * Created by 张继
 * 2017/7/11
 * 16:22
 * 发单成功之后的界面
 */
public class BillActivity extends BaseActivity {

    @BindView(R.id.preview_bnt)
    Button previewBnt;//开始
    @BindView(R.id.recompose_order)
    Button recomposeOrder;//取消订单
    @BindView(R.id.bill_refresh)
    SmartRefreshLayout billRefresh;//刷新
    @BindView(R.id.bill_head)
    CustomCircleImage billHead;//头像
    @BindView(R.id.bill_nick)
    TextView billNick;//昵称
    @BindView(R.id.bill_phone)
    TextView billPhone;//电话
    @BindView(R.id.bill_call_phone)
    ImageView billCallPhone;//打电话
    @BindView(R.id.bill_order_status)
    TextView billOrderStatus;//状态
    @BindView(R.id.bill_start_date)
    TextView billStartDate;//出发日期
    @BindView(R.id.bill_guide)
    ImageView billGuide;//导游证
    @BindView(R.id.bill_person_head)
    ImageView billPersonHead;//本人照片
    @BindView(R.id.bill_surplus_time)
    TextView billSurplusTime;//所剩时间
    @BindView(R.id.bill_order_number)
    TextView orderNum;//订单编号

    //    @BindView(R.id.back_button)
//    ImageView backButton;
    @BindView(R.id.standard_toolbar_title)
    TextView standardToolbarTitle;
    @BindView(R.id.satandard_toolbar_right)
    TextView satandardToolbarRight;
    @BindView(R.id.standard_toolbar)
    Toolbar standardToolbar;


    private String mUserId;
    private String mDevCode;
    private long mTimestamp;
    private String mOid;
    private MyPopWindow popWindow;
    private String intentFlag;
    private PermissionHelper mHelper;
    private PermissionHelper.PermissionModel[] permissionModels = {new PermissionHelper.PermissionModel(1, Manifest.permission.CALL_PHONE, "拨打电话")
    };
    private boolean isCall = true;
    private boolean is_reserve = true;
    private LoadDialog mLoadDialog;

    private String ordStatus;

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_bill;
    }

    @Override
    protected void initView() {
        new ToolbarWrapper(this);
        //开启动画
        mLoadDialog = new LoadDialog(this, "正在加载...", R.drawable.load_animation);
        mLoadDialog.show();

        standardToolbarTitle.setText("发单状态");
        satandardToolbarRight.setText("投诉");

        previewBnt.setVisibility(View.GONE);
        recomposeOrder.setText("取消订单");
        billRefresh.setEnableLoadmore(false);
        mUserId = ((MyApplication) getApplication()).getUserId();
        mDevCode = ((MyApplication) getApplication()).getDevcode();
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();
        mOid = getIntent().getStringExtra("oid");
        intentFlag = getIntent().getStringExtra("intentFlag");
        getBillOrderPersonMsg();//请求接单人信息
        billRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getBillOrderPersonMsg();//刷新
            }
        });
        popWindow = new MyPopWindow(this, BillActivity.this);
        popWindow.setOnCallBackListener(new MyPopWindow.onCallBackListener() {
            @Override
            public void onClickRating(int num) {
                getCommentMsg(num);
            }

            @Override
            public void onClickCommit(int num, String title, String info) {
                commitComment(num, title, info);
            }
        });
    }

    @OnClick({R.id.preview_bnt, R.id.recompose_order, R.id.bill_call_phone, R.id.satandard_toolbar_right})
    public void onViewClicked(View view) {
        String bntStart = previewBnt.getText().toString();
        switch (view.getId()) {
//            case R.id.back_button:
//                finish();
//                break;
            case R.id.preview_bnt://开始订单
//                if (bntStart.equals("开始")) {
//                    previewBnt.setClickable(false);
//                    /**
//                     *  @param context 上下文
//                     *  @param themeResId 样式id
//                     *  @param callBackListener 点击监听
//                     */
//                    new DialogSure(BillActivity.this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
//                        @Override
//                        public void onClickListener(DialogSure dialogSure, boolean cancelable) {
//                            if (cancelable) {//确定按钮
//                                getBillStartOrder();
//                            }
//                        }
//                    }).setMsg("开始订单？").setPositiveButton("确定").setNegativeButton("取消").show();
//                } else
                if (bntStart.equals("完成")) {
                    previewBnt.setClickable(false);
                    new DialogSure(BillActivity.this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                        @Override
                        public void onClickListener(DialogSure dialogSure, boolean cancelable) {
                            if (cancelable) {//确定按钮
                                getBillEndOrder();
                            }
                        }
                    }).setTitle("温馨提示").setMsg("确认要结束该行程？").setPositiveButton("确定").setNegativeButton("取消").show();
                }
                break;
            case R.id.recompose_order://取消订单
                if (recomposeOrder.getText().toString().equals("取消订单")) {//取消订单
                    new DialogSure(BillActivity.this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                        @Override
                        public void onClickListener(DialogSure dialogSure, boolean cancelable) {
                            if (cancelable) {//确定按钮
                                recomposeOrder.setEnabled(false);
                                cancelOrder();//取消订单
                            }

                        }
                    }).setTitle("温馨提示").setMsg("取消订单？").setPositiveButton("确定").setNegativeButton("取消").show();
                } else {//续费
                    new DialogSure(BillActivity.this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                        @Override
                        public void onClickListener(DialogSure dialogSure, boolean cancelable) {
                            if (cancelable) {//确定按钮
                                Intent intent = new Intent(BillActivity.this, ContinuePlayActivity.class);
                                intent.putExtra("oid", mOid);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }).setTitle("温馨提示").setMsg("确定续费？").setPositiveButton("确定").setNegativeButton("取消").show();
                }
                break;
            case R.id.bill_call_phone://打电话

                if (Build.VERSION.SDK_INT < 23) {
                    call();
                } else {
                    mHelper = new PermissionHelper(this, new PermissionHelper.OnAlterApplyPermission() {
                        @Override
                        public void OnAlterApplyPermission() {
                            call();
                        }

                        @Override
                        public void cancelListener() {

                        }
                    }, permissionModels);
                    mHelper.applyPermission();
                    //申请权限
                    //Applypermission();
                }
                break;
            case R.id.satandard_toolbar_right://跳转投诉
                if (StringUtil.isEmpty(ordStatus) || ordStatus.equals("0") || ordStatus.equals("4")) {
                    //订单状态：0：未接单  4：过期（未接单）不可提交投诉
                    new DialogKnow(BillActivity.this, R.style.user_default_dialog, new DialogKnow.CallBackListener() {
                        @Override
                        public void onClickListener() {

                        }
                    }).setTitle("温馨提示").setMsg("该订单还未接单，无法对其进行投诉！").setPositiveButton("我知道了").show();
                } else {
                    Intent intent = new Intent(BillActivity.this, ComplainActivity.class);
                    intent.putExtra("oid", mOid);
                    startActivity(intent);
                }
                break;

            default:
                break;
        }
    }

    /**
     * 打电话
     */
    private void call() {
        final String phoneNum = billPhone.getText().toString().trim();
        if (!StringUtil.isEmpty(phoneNum)) {
            new DialogKnow(BillActivity.this, R.style.user_default_dialog, new DialogKnow.CallBackListener() {
                @Override
                public void onClickListener() {
                    if (!StringUtil.isEmpty(phoneNum)) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        Uri data = Uri.parse("tel:" + phoneNum);
                        intent.setData(data);
                        startActivity(intent);
                    }
                }
            }).setMsg(phoneNum).setPositiveButton("呼叫").show();
        } else {
            return;
        }


    }


    @Override
    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mHelper.onActivityResult(requestCode, resultCode, data);
    }

//    /**
//     * 开始订单
//     */
//    private void getBillStartOrder() {
//        boolean flag = APPUtils.judgeTimeDev(this, mDevCode, mTimestamp);
//        if (flag) {
//            RequestBody builder = getRequestBody();
//            OkHttpUtils.getInstance().post(URLUtils.BILL_START_ORDER, builder, new HttpCallBack(new BillEvent()));
//        }
//    }

    /**
     * 结束订单
     */
    private void getBillEndOrder() {
        boolean flag = APPUtils.judgeTimeDev(this, mDevCode, mTimestamp);
        if (flag) {
            RequestBody builder = getRequestBody();
            OkHttpUtils.getInstance().post(URLUtils.BILL_END_ORDER, builder, new HttpCallBack(new BillEndEvent()));
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void getBillStartOrder(BillEvent event) {
//        if (event.isTimeOut()) {
////            previewBnt.setClickable(true);//能开始
//            recomposeOrder.setClickable(true);//能取消
//            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        BillBean bean = event.getData();
//        if (bean.getStatus() == 0) {
//            //Intent intentStart = new Intent();
//            //intentStart.setAction("orderNotify");
//            //intentStart.putExtra("notifyFlag", "bill");
//            //sendBroadcast(intentStart);
//            //            previewBnt.setEnabled(true);
//
//
//            EventBus.getDefault().post(new OrderNotifyBean("bill"));
//            setOrderData(bean);
//        } else {
////            previewBnt.setClickable(true);//能开始
//            recomposeOrder.setClickable(true);//能取消
//            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
//            APPUtils.intentLogin(this, bean.getStatus());
//        }
//    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getBillEndOrder(BillEndEvent event) {
        if (event.isTimeOut()) {
//            previewBnt.setClickable(true);//能开始
            recomposeOrder.setClickable(true);//能取消
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        BillBean bean = event.getData();
        if (bean.getStatus() == 0) {
            //Intent intentEnd = new Intent();
            //intentEnd.setAction("orderNotify");
            //intentEnd.putExtra("notifyFlag", "bill");
            //sendBroadcast(intentEnd);
            //  previewBnt.setEnabled(true);


            //发送消息到我的订单，更新订单列表的订单状态信息
            EventBus.getDefault().post(new OrderNotifyBean("bill"));
            //设置订单数据
            setOrderData(bean);
        } else {
            recomposeOrder.setClickable(true);//能取消
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }

    /**
     * 设置订单数据
     *
     * @param bean
     */
    private void setOrderData(BillBean bean) {
        billSurplusTime.setText(bean.getData().getOrd_end());
        String ord_status = bean.getData().getOrd_status();
        if (ord_status.equals("0")) {
            billOrderStatus.setText("未接单");
            recomposeOrder.setText("取消订单");
            recomposeOrder.setClickable(true);//能取消
        } else if (ord_status.equals("1")) {
            recomposeOrder.setText("取消订单");
            billOrderStatus.setText("未开始");
            recomposeOrder.setClickable(true);//能取消
        } else if (ord_status.equals("2")) {
            billOrderStatus.setText("游玩中");
//            if (bean.getData().getTime_way().equals("1")) {
////                recomposeOrder.setText("续费");
//            } else {
                recomposeOrder.setText("取消订单");

            recomposeOrder.setClickable(true);//不能取消
        } else if (ord_status.equals("3")) {
            billOrderStatus.setText("已结束");
            recomposeOrder.setText("取消订单");
            lightOff();//亮暗
            getCommentMsg(0);
            popWindow.showAtLocation(BillActivity.this.findViewById(R.id.preview_bnt), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            recomposeOrder.setClickable(false);//不能取消
        } else if (ord_status.equals("4")) {
            recomposeOrder.setText("取消订单");
            billOrderStatus.setText("已过期");
            recomposeOrder.setClickable(false);//不能取消
        } else if (ord_status.equals("5")) {
            recomposeOrder.setText("取消订单");
            billOrderStatus.setText("已取消");
            recomposeOrder.setClickable(false);//不能取消
        } else if (ord_status.equals("6")) {
            recomposeOrder.setText("取消订单");
            billOrderStatus.setText("发单人已评价");
            recomposeOrder.setClickable(false);//不能取消
        } else if (ord_status.equals("7")) {
            recomposeOrder.setText("取消订单");
            billOrderStatus.setText("接单人已评价");
            recomposeOrder.setClickable(false);//不能取消
        }
    }

    /**
     * 请求接单人的信息
     */
    private void getBillOrderPersonMsg() {

        boolean flag = APPUtils.judgeTimeDev(this, mDevCode, mTimestamp);
        if (flag) {
            RequestBody builder = getRequestBody();
            OkHttpUtils.getInstance().post(URLUtils.ORDER_DETAIL, builder, new HttpCallBack(new BillOrderPersonEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getBillOrderPersonMsg(BillOrderPersonEvent event) {
        if (event.isTimeOut()) {
            mLoadDialog.dismiss();//取消动画
//            previewBnt.setClickable(true);//能开始
            recomposeOrder.setClickable(true);//能取消
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        OrderDetailBean bean = event.getData();
        if (bean.getStatus() == 0) {
//
//            Intent intentNormal = new Intent();
//            intentNormal.setAction("orderNotify");
//            intentNormal.putExtra("notifyFlag","bill");
//            sendBroadcast(intentNormal);
//发送消息到我的订单，更新订单列表的订单状态信息
            EventBus.getDefault().post(new OrderNotifyBean("bill"));
            billSurplusTime.setText(bean.getData().getOrd_end());
            String bid = bean.getData().getBid();
            ordStatus = bean.getData().getOrd_status();
            String number = bean.getData().getNumber();
            if (!StringUtil.isEmpty(number)) {
                orderNum.setText("No." + number);
            }
            if (!StringUtil.isEmpty(bid) && !bid.equals("0")) {
                if (!StringUtil.isEmpty(ordStatus) && ordStatus.equals("0")) {
                    DialogKnow dialogKnow = new DialogKnow(BillActivity.this, R.style.user_default_dialog, new DialogKnow.CallBackListener() {
                        @Override
                        public void onClickListener() {
                            is_reserve = false;
                        }
                    });
                    dialogKnow.setMsg("您的预约订单已提交，预约结果我们会第一时间通知您，请您耐心等待");
                    dialogKnow.setPositiveButton("我知道了");
                    dialogKnow.show();
                }
            }

            if (!StringUtil.isEmpty(bean.getData().getQ_pic())) {
                OkHttpUtils.displayImg(billHead, bean.getData().getQ_pic());
            } else {
                billHead.setImageResource(R.mipmap.head);
            }
//            if (bean.getData().getIs_dao().equals("0")) {//不显示导游证
//                billGuide.setImageResource(R.mipmap.no_guide);
//            }else if (bean.getData().getIs_dao().equals("1")){
            if (!StringUtil.isEmpty(bean.getData().getQ_dpic())) {//导游证照片
                OkHttpUtils.displayImg(billGuide, bean.getData().getQ_dpic());
            } else {
                billGuide.setImageResource(R.mipmap.no_guide);
            }
            if (!bean.getData().getQ_card_pic().equals("")) {//本人照片
                OkHttpUtils.displayImg(billPersonHead, bean.getData().getQ_card_pic());
            } else {
                billPersonHead.setImageResource(R.mipmap.bill_header);
            }
            billNick.setText(bean.getData().getQ_nickname());
            billPhone.setText(bean.getData().getQ_phone());
            billStartDate.setText(bean.getData().getSend_time());
            billSurplusTime.setText(bean.getData().getOrd_end());//订单结束时间

            if (ordStatus.equals("0")) {
                billOrderStatus.setText("未接单");
//                previewBnt.setText("开始");
                recomposeOrder.setText("取消订单");
//                previewBnt.setClickable(false);//不能开始
                recomposeOrder.setClickable(true);//能取消
            } else if (bean.getData().getOrd_status().equals("1")) {//已接单未开始
                billOrderStatus.setText("未开始");
//                previewBnt.setText("开始");
                recomposeOrder.setText("取消订单");
//                previewBnt.setClickable(true);//能开始
                recomposeOrder.setClickable(true);//能取消
            } else if (bean.getData().getOrd_status().equals("2")) {//游玩中
                billOrderStatus.setText("游玩中");
//                previewBnt.setText("结束");
                if (bean.getData().getTime_way().equals("1")) {
                    recomposeOrder.setText("续费");
                } else {
                    recomposeOrder.setText("取消订单");
                }
                previewBnt.setVisibility(View.VISIBLE);
                previewBnt.setText("完成");
                recomposeOrder.setClickable(true);//不能取消
//                previewBnt.setClickable(true);//不能开始
            } else if (bean.getData().getOrd_status().equals("3")) {//结束

                billOrderStatus.setText("已结束");
//                previewBnt.setText("开始");
                recomposeOrder.setText("取消订单");
                recomposeOrder.setClickable(false);//不能取消
//                previewBnt.setClickable(false);//结束
            } else if (bean.getData().getOrd_status().equals("4")) {//过期
                billOrderStatus.setText("已过期");
//                previewBnt.setText("开始");
                recomposeOrder.setText("取消订单");
//                previewBnt.setClickable(false);//不能开始
                recomposeOrder.setClickable(false);//不能取消
            } else if (bean.getData().getOrd_status().equals("5")) {//取消订单
                billOrderStatus.setText("已取消");
//                previewBnt.setText("开始");
                recomposeOrder.setText("取消订单");
//                previewBnt.setClickable(false);//不能开始
                recomposeOrder.setClickable(false);//不能取消
            } else if (bean.getData().getOrd_status().equals("6")) {//发单人已评价
//                previewBnt.setText("开始");
                recomposeOrder.setText("取消订单");
                billOrderStatus.setText("发单人已评价");
//                previewBnt.setClickable(false);//不能开始
                recomposeOrder.setClickable(false);//不能取消
            } else if (bean.getData().getOrd_status().equals("7")) {//接单人已评价
//                previewBnt.setText("开始");
                recomposeOrder.setText("取消订单");
                billOrderStatus.setText("接单人已评价");
//                previewBnt.setClickable(false);//不能开始
                recomposeOrder.setClickable(false);//不能取消
            } else if (bean.getData().getOrd_status().equals("8")) {
//                previewBnt.setText("开始");
                recomposeOrder.setText("取消订单");
                billOrderStatus.setText("待付款");
//                previewBnt.setClickable(false);//不能开始
                recomposeOrder.setClickable(false);//不能取消
            }
            billRefresh.finishRefresh();//结束刷新
            mLoadDialog.dismiss();//取消动画
        } else {
            mLoadDialog.dismiss();//取消动画
//            previewBnt.setClickable(true);//能开始
            recomposeOrder.setClickable(true);//能取消
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }

    }


    /**
     * 取消订单
     */
    private void cancelOrder() {
        boolean flag = APPUtils.judgeTimeDev(this, mDevCode, mTimestamp);
        if (flag) {
            String sign = Md5Utils.createMD5("devcode=" + mDevCode + "oid=" + mOid + "timestamp=" + mTimestamp + "type=" + "0" + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody builder = new FormBody.Builder().add("devcode", mDevCode)
                    .add("oid", mOid)
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("type", "0")
                    .add("uid", mUserId)
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.CANCEL_ORDER, builder, new HttpCallBack(new DeleteBookEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void cancelOrderBean(DeleteBookEvent event) {
        if (event.isTimeOut()) {
            recomposeOrder.setEnabled(true);
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        BaseBean bean = event.getData();
        if (bean.getStatus() == 0) {
            //Intent intent = new Intent();
            //intent.setAction("orderNotify");
            //intent.putExtra("notifyFlag", "bill");
            //sendBroadcast(intent);
            EventBus.getDefault().post(new OrderNotifyBean("bill"));
            recomposeOrder.setEnabled(true);
//            if (!StringUtil.isEmpty(intentFlag) && intentFlag.equals("myOrder")) {
//                finish();
//            } else {
//                startActivity(new Intent(BillActivity.this, MainActivity.class));
//            }
            finish();
        } else {
            recomposeOrder.setEnabled(true);
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }

    /**
     * 获取接单人信息 开始 结束共用RequestBody
     *
     * @return
     */
    private RequestBody getRequestBody() {
        String sign = Md5Utils.createMD5("devcode=" + mDevCode + "oid=" + mOid + "timestamp=" + mTimestamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
        RequestBody build = new FormBody.Builder()
                .add("devcode", mDevCode)
                .add("oid", mOid)
                .add("timestamp", String.valueOf(mTimestamp))
                .add("uid", mUserId)
                .add("signature", sign)
                .build();
        return build;
    }

    /**
     * 获取评价信息
     *
     * @param num
     */
    private void getCommentMsg(int num) {
        boolean flag = APPUtils.judgeTimeDev(this, mDevCode, mTimestamp);
        if (flag) {
            String sign = Md5Utils.createMD5("devcode=" + mDevCode + "oid=" + mOid + "starlevel=" + num + "timestamp=" + mTimestamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", mDevCode)
                    .add("oid", mOid)
                    .add("starlevel", String.valueOf(num))
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", mUserId)
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.BILL_COMMENT_MSG, requestBody, new HttpCallBack(new BillCommentEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getCommentMsg(BillCommentEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        BillCommentBean bean = event.getData();
        if (bean.getStatus() == 0) {
            popWindow.setTextString(bean.getData().getLevelmemo(), bean.getData().getLevelcomment1(), bean.getData().getLevelcomment2(), bean.getData().getLevelcomment3(), bean.getData().getLevelcomment4());
        } else {
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }

    }

    /**
     * 提交评论
     * @param num   星星
     * @param title 标题
     * @param info  内容
     */
    private void commitComment(int num, String title, String info) {
        boolean flag = APPUtils.judgeTimeDev(this, mDevCode, mTimestamp);
        if (flag) {
            String sign = Md5Utils.createMD5("devcode=" + mDevCode + "info=" + info + "oid=" + mOid + "starlevel=" + num + "timestamp=" + mTimestamp + "title=" + title
                    + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", mDevCode)
                    .add("info", info)
                    .add("oid", mOid)
                    .add("starlevel", String.valueOf(num))
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("title", title)
                    .add("uid", mUserId)
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.BILL_COMMIT_COMMENT_MSG, requestBody, new HttpCallBack(new CommitCommentEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void commitComment(CommitCommentEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        BaseBean bean = event.getData();
        if (bean.getStatus() == 0) {
            popWindow.dismiss();
            Intent intent = new Intent(BillActivity.this, MainActivity.class);
            startActivity(intent);
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }

    /**
     * 显示时屏幕变暗
     */
    private void lightOff() {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.alpha = 0.3f;
        getWindow().setAttributes(layoutParams);
    }


    @Override
    protected boolean needEventBus() {
        return true;
    }


}
