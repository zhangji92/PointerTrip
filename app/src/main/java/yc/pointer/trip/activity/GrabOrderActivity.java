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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
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
import yc.pointer.trip.bean.ScanQRCodeBean;
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
 * Created by 刘佳伟
 * 2017/7/18
 * 16:09
 * 抢单状态
 */
public class GrabOrderActivity extends BaseActivity {
    @BindView(R.id.scan_qrcode)
    ImageView sacnQRCode;//扫描按钮
    @BindView(R.id.grad_head)
    CustomCircleImage gradHead;//头像
    @BindView(R.id.grab_nick)
    TextView grabNick;//昵称
    @BindView(R.id.grab_phone)
    TextView grabPhone;//电话号
    @BindView(R.id.grab_call_phone)
    ImageView grabCallPhone;//打电话
    @BindView(R.id.grab_order_status)
    TextView grabOrderStatus;//订单状态
    @BindView(R.id.grab_refresh)
    SmartRefreshLayout grabRefresh;//刷新
    @BindView(R.id.grab_cancel_order)
    Button bntCancelOrder;//取消订单
    @BindView(R.id.grab_msg)
    TextView textMsg;//特别提醒
    @BindView(R.id.bill_order_number)
    TextView orderNumber;//订单编号
    @BindView(R.id.grab_surplus_time)
    TextView orderEndingtime;//订单结束时间
   @BindView(R.id.grab_make_money)
    TextView grabMoney;//预计收入


    private String mDevCode;//设备识别码
    private String mOid;//订单编号
    private String mUserId;//用户的id
    private long mTimestamp;//时间戳
    private MyPopWindow popWindow;//评价
    private boolean flag = true;
    private PermissionHelper mHelper;
    private PermissionHelper.PermissionModel[] permissionModels = {new PermissionHelper.PermissionModel(1, Manifest.permission.CALL_PHONE, "拨打电话")
    };
    private boolean isCall = true;
    private LoadDialog mLoadDialog;
    private boolean isPermission = false;

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_grab_order;
    }

    @Override
    protected void initView() {
        //开启动画
        mLoadDialog = new LoadDialog(this, "正在加载...", R.drawable.load_animation);
        mLoadDialog.show();
        new ToolbarWrapper(this).setCustomTitle(R.string.grab_order_title);
        grabRefresh.setEnableLoadmore(false);//禁止加载
        mOid = getIntent().getStringExtra("oid");
        mDevCode = ((MyApplication) getApplication()).getDevcode();
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();
        mUserId = ((MyApplication) getApplication()).getUserId();
        getBillOrderPersonMsg();//获取发单人的数据
        grabRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getBillOrderPersonMsg();//刷新
            }
        });

        //评价
        popWindow = new MyPopWindow(this, GrabOrderActivity.this);
        popWindow.setOnCallBackListener(new MyPopWindow.onCallBackListener() {
            @Override
            public void onClickRating(int num) {
                if (num == 1) {
                    if (!flag) {
                        flag = true;
                        getCommentMsg(0);
                    } else {
                        flag = false;
                        getCommentMsg(num);
                    }
                } else {
                    getCommentMsg(num);
                }
            }

            @Override
            public void onClickCommit(int num, String title, String info) {
                commitComment(num, title, info);
            }
        });

    }

    @OnClick({R.id.scan_qrcode, R.id.grab_call_phone, R.id.grab_cancel_order})
    public void onClickButton(View view) {
        switch (view.getId()) {
            case R.id.scan_qrcode://扫描二维码
                // 创建IntentIntegrator对象
                IntentIntegrator intentIntegrator = new IntentIntegrator(this);
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
            case R.id.grab_call_phone:
                if (Build.VERSION.SDK_INT < 23) {
                    call();
                } else {
                    isPermission = true;
                    mHelper = new PermissionHelper(this, new PermissionHelper.OnAlterApplyPermission() {
                        @Override
                        public void OnAlterApplyPermission() {
                            call();
                        }

                        @Override
                        public void cancelListener() {

                        }
                    }, permissionModels);
                    //申请权限
                    //Applypermission();
                }
                break;
            case R.id.grab_cancel_order:
                new DialogSure(GrabOrderActivity.this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                    @Override
                    public void onClickListener(DialogSure dialogSure, boolean cancelable) {
                        if (cancelable) {
                            cancelOrder();//取消订单
                        }
                    }
                }).setTitle("温馨提示").setMsg("取消订单？").setPositiveButton("确定").setNegativeButton("取消").show();
                break;
            default:
                break;
        }
    }

    /**
     * 打电话
     */
    private void call() {
        isPermission = false;
        final String phoneNum = grabPhone.getText().toString().trim();
        if (!StringUtil.isEmpty(phoneNum)) {
            new DialogKnow(GrabOrderActivity.this, R.style.user_default_dialog, new DialogKnow.CallBackListener() {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isPermission) {
            mHelper.onActivityResult(requestCode, resultCode, data);
        }
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            String contents = result.getContents();
            if (contents == null) {
                //扫描失败
//                Toast.makeText(GrabOrderActivity.this, "取消扫描", Toast.LENGTH_LONG).show();
            } else {
                //扫描成功，开始订单
                boolean isJson = StringUtil.isJson(contents);
                if (isJson){
                    Gson gson = new Gson();
                    ScanQRCodeBean scanQRCodeBean = gson.fromJson(contents, ScanQRCodeBean.class);
                    String oid = scanQRCodeBean.getOid();
                    String uid = scanQRCodeBean.getUid();
                    String order_status = scanQRCodeBean.getOrder_status();
                    if (oid.equals(mOid)){
                        if (uid.equals(mUserId)){
                            if (order_status.equals("1")){
                                getBillStartOrder();
                            }else {
                                Toast.makeText(this, "二维码已失效", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(this, "接单方身份匹配失败，不能开始订单", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(this, "订单Id错误，请认证核对订单编号", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(this, "格式错误", Toast.LENGTH_SHORT).show();
                }
                Log.e("content", contents);
//                Toast.makeText(GrabOrderActivity.this, "扫描内容:" + contents, Toast.LENGTH_LONG).show();
            }
        }


    }

    @Override
    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    /**
     * 获取发单人信息 开始 共用RequestBody
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
     * 请求发单人的信息
     */
    private void getBillOrderPersonMsg() {
        boolean flag = APPUtils.judgeTimeDev(this, mDevCode, mTimestamp);
        if (flag) {
            RequestBody builder =getRequestBody();
            OkHttpUtils.getInstance().post(URLUtils.ORDER_DETAIL, builder, new HttpCallBack(new GrabOrderPersonEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void orderBean(GrabOrderPersonEvent event) {
        if (event.isTimeOut()) {
            mLoadDialog.dismiss();//取消动画
            bntCancelOrder.setEnabled(true);//取消订单
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        OrderDetailBean bean = event.getData();
        if (bean.getStatus() == 0) {

//            Intent intent = new Intent();
//            intent.setAction("orderNotify");
//            intent.putExtra("notifyFlag","grabOrder");
//            sendBroadcast(intent);
            String ord_status = bean.getData().getOrd_status();
            OkHttpUtils.displayImg(gradHead, bean.getData().getPic());
            grabNick.setText(bean.getData().getNickname());
            grabPhone.setText(bean.getData().getPhone());
            String end_time = bean.getData().getOrd_end();
            String number = bean.getData().getNumber();
            String money_y = bean.getData().getMoney_y();

            if (!StringUtil.isEmpty(number)) {
                orderNumber.setText("No." + number);
            }

            if (!StringUtil.isEmpty(end_time)) {
                orderEndingtime.setText(end_time);
            }

            if (!StringUtil.isEmpty(money_y)){
                grabMoney.setText("￥ "+money_y+"元");
            }

            //设置订单状态
            setOrderStatus(ord_status);

            textMsg.setText(bean.getData().getTe());//特别提醒
            grabRefresh.finishRefresh();//结束刷新
            mLoadDialog.dismiss();//取消动画
        } else {
            mLoadDialog.dismiss();//取消动画
            bntCancelOrder.setEnabled(true);//取消订单
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }

    /**
     * 设置订单状态
     * @param ord_status
     */
    private void setOrderStatus(String ord_status) {
        if (ord_status.equals("0")) {
            grabOrderStatus.setText("未接单");
            bntCancelOrder.setEnabled(true);//取消订单
            bntCancelOrder.setVisibility(View.VISIBLE);
        } else if (ord_status.equals("1")) {
            grabOrderStatus.setText("未开始");
            bntCancelOrder.setEnabled(true);
            bntCancelOrder.setVisibility(View.VISIBLE);
        } else if (ord_status.equals("2")) {
            grabOrderStatus.setText("游玩中");
            bntCancelOrder.setVisibility(View.INVISIBLE);
        } else if (ord_status.equals("3")) {
            grabRefresh.finishRefresh();//结束刷新
            grabOrderStatus.setText("已结束");
            bntCancelOrder.setVisibility(View.INVISIBLE);
            lightOff();//亮暗
            getCommentMsg(0);
            popWindow.showAtLocation(GrabOrderActivity.this.findViewById(R.id.grab_call_phone), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        } else if (ord_status.equals("4")) {
            grabOrderStatus.setText("已过期");
            bntCancelOrder.setVisibility(View.INVISIBLE);
        } else if (ord_status.equals("5")) {
            grabOrderStatus.setText("已取消");
            bntCancelOrder.setVisibility(View.INVISIBLE);
        } else if (ord_status.equals("6")) {
            grabOrderStatus.setText("发单人已评价");
            bntCancelOrder.setVisibility(View.INVISIBLE);
        } else if (ord_status.equals("7")) {
            grabOrderStatus.setText("接单人已评价");
            bntCancelOrder.setVisibility(View.INVISIBLE);
        } else if (ord_status.equals("8")) {
            grabOrderStatus.setText("等待发单方付款");
            bntCancelOrder.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 开始订单
     */
    private void getBillStartOrder() {
        boolean flag = APPUtils.judgeTimeDev(this, mDevCode, mTimestamp);
        if (flag) {
            RequestBody builder = getRequestBody();
            OkHttpUtils.getInstance().post(URLUtils.NEW_BILL_START_ORDER, builder, new HttpCallBack(new BillEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getBillStartOrder(BillEvent event) {
        if (event.isTimeOut()) {
//            previewBnt.setClickable(true);//能开始
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        BillBean bean = event.getData();
        if (bean.getStatus() == 0) {
            //Intent intentStart = new Intent();
            //intentStart.setAction("orderNotify");
            //intentStart.putExtra("notifyFlag", "bill");
            //sendBroadcast(intentStart);
            //previewBnt.setEnabled(true);
            String ord_status = bean.getData().getOrd_status();
            EventBus.getDefault().post(new OrderNotifyBean("grabOrder"));
            setOrderStatus(ord_status);
        } else {
//          previewBnt.setClickable(true);//能开始
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
            String sign = Md5Utils.createMD5("devcode=" + mDevCode + "oid=" + mOid + "timestamp=" + mTimestamp + "type=" + "1" + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody builder = new FormBody.Builder().add("devcode", mDevCode)
                    .add("oid", mOid)
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("type", "1")
                    .add("uid", mUserId)
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.CANCEL_ORDER, builder, new HttpCallBack(new GrabOrderEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void cancelOrderBean(GrabOrderEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        BaseBean bean = event.getData();
        if (bean.getStatus() == 0) {
            //Intent intentCancel = new Intent();
            //intentCancel.setAction("orderNotify");
            //intentCancel.putExtra("notifyFlag","grabOrder");
            //sendBroadcast(intentCancel);
            EventBus.getDefault().post(new OrderNotifyBean("grabOrder"));
            finish();
        } else {
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }
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
            OkHttpUtils.getInstance().post(URLUtils.BILL_COMMENT_MSG, requestBody, new HttpCallBack(new GrabCommentEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getCommentMsg(GrabCommentEvent event) {
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
     *
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
            OkHttpUtils.getInstance().post(URLUtils.BILL_COMMIT_COMMENT_MSG, requestBody, new HttpCallBack(new GrabCommitCommentEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void commitComment(GrabCommitCommentEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        BaseBean bean = event.getData();
        if (bean.getStatus() == 0) {
            popWindow.dismiss();
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
