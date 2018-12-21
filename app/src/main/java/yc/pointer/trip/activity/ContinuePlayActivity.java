package yc.pointer.trip.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import yc.pointer.trip.R;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.ContinuePlayBean;
import yc.pointer.trip.bean.ContinuePlaySendBean;
import yc.pointer.trip.event.ContinuePlayEvent;
import yc.pointer.trip.event.ContinuePlaySendEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.view.CustomCircleImage;
import yc.pointer.trip.view.DialogSure;
import yc.pointer.trip.view.LoadDialog;
import yc.pointer.trip.view.ToolbarWrapper;
import yc.pointer.trip.wxapi.PayOrderActivity;

/**
 * Created by 刘佳伟
 * 2017/7/14
 * 17:40
 * 继续玩
 */
public class ContinuePlayActivity extends BaseActivity {

    @BindView(R.id.continue_sub)
    Button continueSub;//减
    @BindView(R.id.continue_time_texe)
    TextView continueHours;//续费时间
    @BindView(R.id.continue_add)
    Button continueAdd;//加
    @BindView(R.id.total_monkey)
    TextView totalMonkey;//价钱
    @BindView(R.id.continue_time)
    TextView continueTime;
    @BindView(R.id.continue_head)
    CustomCircleImage continueHead;
    @BindView(R.id.continue_nick)
    TextView continueNick;
    @BindView(R.id.continue_phone)
    TextView continuePhone;
    @BindView(R.id.continue_commint)
    Button continueCommint;
    private String mUserId;//用户id
    private long mTimestamp;//时间戳
    private String mDevCode;//设备标识码
    private String mOid;//订单编号
    private Integer mPrice;//单价
    private int mCurNum;
    private LoadDialog mLoadDialog;

    @Override
    protected int getContentViewLayout() {
        return R.layout.acitvity_continue;
    }

    @Override
    protected void initView() {
        //开启动画
        mLoadDialog = new LoadDialog(this, "正在加载...", R.drawable.load_animation);
        mLoadDialog.show();

        new ToolbarWrapper(this).setCustomTitle(R.string.continue_conmmint);
        continueHours.setText("1");
        mUserId = ((MyApplication) getApplication()).getUserId();
        mDevCode = ((MyApplication) getApplication()).getDevcode();
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();
        mOid = getIntent().getStringExtra("oid");
        getOrderMsg();//获取订单信息

    }

    /**
     * 请求网络上面的信息
     */
    private void getOrderMsg() {
        boolean flag = APPUtils.judgeTimeDev(this, mDevCode, mTimestamp);
        if (flag) {
            String sign = Md5Utils.createMD5("devcode=" + mDevCode + "oid=" + mOid + "timestamp=" + mTimestamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody build = new FormBody.Builder()
                    .add("devcode", mDevCode)
                    .add("oid", mOid)
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", mUserId)
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.GET_CONTINUE_PLAY, build, new HttpCallBack(new ContinuePlayEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getOrderMsg(ContinuePlayEvent event) {
        if (event.isTimeOut()) {
            mLoadDialog.dismiss();//取消动画
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        ContinuePlayBean bean = event.getData();
        if (bean.getStatus() == 0) {
            OkHttpUtils.displayImg(continueHead,bean.getData().getPic());
            continueTime.setText(bean.getData().getEnd_time());//时间
            continueNick.setText(bean.getData().getNickname());//昵称
            continuePhone.setText(bean.getData().getPhone());//手机号
            mPrice = Integer.valueOf(bean.getData().getPrice());
            totalMonkey.setText("￥" + mPrice);
            mLoadDialog.dismiss();//取消动画
        } else {
            mLoadDialog.dismiss();//取消动画
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }

    /**
     * 续费的时候发送给后台
     */
    private void sendOrderMsg(int timeNum){
        boolean flag = APPUtils.judgeTimeDev(this, mDevCode, mTimestamp);
        if (flag) {
            String sign = Md5Utils.createMD5("devcode=" + mDevCode + "oid=" + mOid + "time_num=" + timeNum +"timestamp=" + mTimestamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody build = new FormBody.Builder()
                    .add("devcode", mDevCode)
                    .add("oid", mOid)
                    .add("time_num", String.valueOf(timeNum))
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", mUserId)
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.SEND_CONTINUE_PLAY, build, new HttpCallBack(new ContinuePlaySendEvent()));
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sendOrderMsg(ContinuePlaySendEvent event){
        if (event.isTimeOut()){
            continueCommint.setEnabled(true);
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        ContinuePlaySendBean bean = event.getData();
        if (bean.getStatus() == 0) {
            continueCommint.setEnabled(true);
            Intent intent=new Intent(ContinuePlayActivity.this, PayOrderActivity.class);
            intent.putExtra("oid",bean.getOid());
            intent.putExtra("flag","continuePay");
            startActivity(intent);
            finish();
        } else {
            continueCommint.setEnabled(true);
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

    @OnClick({R.id.continue_sub, R.id.continue_add,R.id.continue_commint})
    public void onViewClicked(View view) {

        mCurNum = Integer.parseInt(continueHours.getText().toString());
        switch (view.getId()) {
            case R.id.continue_sub:
                if (mCurNum > 1 && mCurNum <= 8) {
                    mCurNum--;
                    continueHours.setText(String.valueOf(mCurNum));
                    totalMonkey.setText("￥" + String.valueOf(mCurNum * mPrice));
                }
                break;
            case R.id.continue_add:
                if (mCurNum >= 1 && mCurNum < 8) {
                    mCurNum++;
                    continueHours.setText(String.valueOf(mCurNum));
                    totalMonkey.setText("￥" + String.valueOf(mCurNum * mPrice));
                }
                break;
            case R.id.continue_commint:
                continueCommint.setEnabled(true);
                new DialogSure(ContinuePlayActivity.this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                    @Override
                    public void onClickListener(DialogSure dialogSure, boolean cancelable) {
                        if (cancelable){
                            continueCommint.setEnabled(false);
                            sendOrderMsg(mCurNum);
                        }
                    }
                }).setTitle("温馨提示").setMsg("确定续费吗？").setPositiveButton("确定").setNegativeButton("取消").show();
                break;
        }
    }


}
