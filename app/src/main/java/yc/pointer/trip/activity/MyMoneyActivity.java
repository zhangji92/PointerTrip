package yc.pointer.trip.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import yc.pointer.trip.R;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.WithdrawalBean;
import yc.pointer.trip.bean.eventbean.RefreshEmbodyBean;
import yc.pointer.trip.event.WithdrawalEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.StatusBarUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.ToolbarWrapper;

/**
 * Created by moyan on 2018/1/11.
 */

public class MyMoneyActivity extends BaseActivity {

    @BindView(R.id.standard_toolbar_title)
    TextView standardToolbarTitle;
    @BindView(R.id.standard_toolbar)
    Toolbar standardToolbar;
    @BindView(R.id.my_money_top)
    LinearLayout myMoneyTop;
    @BindView(R.id.can_num)
    TextView canNum;
    @BindView(R.id.can_withdrawal)
    TextView canWithdrawal;
    @BindView(R.id.total_num)
    TextView totalNum;
    @BindView(R.id.my_moeny_tex)
    LinearLayout myMoenyTex;
    @BindView(R.id.video_money)
    TextView videoMoney;
    @BindView(R.id.broadcast_money)
    TextView broadcastMoney;
    @BindView(R.id.thumbupear_money)
    TextView thumbupearMoney;
    @BindView(R.id.fans_money)
    TextView fansMoney;
    @BindView(R.id.order_money)
    TextView orderMoney;
    @BindView(R.id.invitew_money)
    TextView invitewMoney;
    @BindView(R.id.money_trip)
    TextView moneyTrip;
    @BindView(R.id.button)
    Button button;
//    @BindView(R.id.can_withdrawal)
//    TextView canWithdrawal;
//    @BindView(R.id.book_num)
//    TextView bookNum;
//    @BindView(R.id.can_num)
//    TextView canNum;
//    @BindView(R.id.total_num)
//    TextView totalNum;
//    @BindView(R.id.money_trip)
//    TextView moneyTrip;
//    @BindView(R.id.button)
//    Button button;

    private String mDevcode;
    private String mUserId;
    private long mTimestamp;
    private boolean isBinding;
    private boolean isVerify;
    private String canCash;
    private int inComeType=0;
    private String inComeTitle="";

    @Override
    protected int getContentViewLayout() {
        return R.layout.new_my_money;
    }

    @Override
    protected void initView() {

        StatusBarUtils.with(this).init();//设置沉浸式
        int statusBarHeight = StatusBarUtils.getStatusBarHeight(this);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) standardToolbar.getLayoutParams();
        layoutParams.topMargin=statusBarHeight;
        standardToolbar.setLayoutParams(layoutParams);


        ToolbarWrapper toolbarWrapper = new ToolbarWrapper(this);
        toolbarWrapper.setCustomTitle(R.string.withdrawal_title);

        mDevcode = ((MyApplication) getApplication()).getDevcode();
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();
        mUserId = ((MyApplication) getApplication()).getUserId();
        isBinding = ((MyApplication) getApplication()).isUserIsBinding();
        isVerify = ((MyApplication) getApplication()).isUserIsVerify();

        //获取网络数据
        getWithdrawalData(this);

        //IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction("刷新提现");
        //registerReceiver(new MyBroadcastReciver(), intentFilter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String withdrawalMoney = canWithdrawal.getText().toString().trim();
                Intent intent = new Intent(MyMoneyActivity.this, WithdrawalBankActivity.class);
                intent.putExtra("cash", withdrawalMoney);
                startActivity(intent);
            }
        });

    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

    /**
     * 获取红包提现数据
     *
     * @param context 上下文
     */
    private void getWithdrawalData(Context context) {

        boolean timeFlag = APPUtils.judgeTimeDev(context, mDevcode, mTimestamp);
        if (timeFlag) {
            if (StringUtil.isEmpty(mUserId)) {
                Toast.makeText(this, "无效的用户Id", Toast.LENGTH_SHORT).show();
                return;
            }
            String str = Md5Utils.createMD5("devcode=" + mDevcode + "timestamp=" + mTimestamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", mUserId)
                    .add("devcode", mDevcode)
                    .add("signature", str)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.GET_CASH, requestBody, new HttpCallBack(new WithdrawalEvent()));

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void withdrawalData(WithdrawalEvent event) {

        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }

        WithdrawalBean data = event.getData();
        if (data.getStatus() == 0) {
            canCash = data.getData().getCash();
//            String book_num = data.getData().getBook_num();
            String y_cash = data.getData().getY_cash();
            String all_cash = data.getData().getAll_cash();
            String tips = data.getData().getTips();

            moneyTrip.setText(tips);
            canWithdrawal.setText(canCash);
//            bookNum.setText(book_num);
            canNum.setText(y_cash);
            totalNum.setText(all_cash);

        } else {
            Toast.makeText(this, data.getMsg(), Toast.LENGTH_SHORT).show();
        }
    }



    @OnClick({R.id.video_money, R.id.broadcast_money, R.id.thumbupear_money, R.id.fans_money, R.id.order_money, R.id.invitew_money})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.video_money://视频收益
                inComeType=0;
                inComeTitle="视频收益";
                break;
            case R.id.broadcast_money://播放收益
                inComeType=1;
                inComeTitle="播放收益";
                break;
            case R.id.thumbupear_money://点赞收益
                inComeType=2;
                inComeTitle="点赞收益";
                break;
            case R.id.fans_money://粉丝收益
                inComeType=3;
                inComeTitle="粉丝收益";
                break;
            case R.id.order_money://任务收益
                inComeType=6;
                inComeTitle="任务收益";
                break;
            case R.id.invitew_money://邀请收益
                inComeType=5;
                inComeTitle="邀请收益";
                break;

        }
        Intent intent1 = new Intent(MyMoneyActivity.this, IncomeDetailsActivity.class);
        intent1.putExtra("type",inComeType);
        intent1.putExtra("title",inComeTitle);
        startActivity(intent1);
    }



//        if (isVerify) {
//            String withdrawalMoney = canWithdrawal.getText().toString().trim();
//            Intent intent = new Intent(MyMoneyActivity.this, WithdrawalBankActivity.class);
////            String withdrawalMoney ="100";
//            intent.putExtra("cash", withdrawalMoney);
//            startActivity(intent);
//
//        } else {
//            new DialogKnow(this, R.style.user_default_dialog, new DialogKnow.CallBackListener() {
//                @Override
//                public void onClickListener() {
//                    Intent intent = new Intent(MyMoneyActivity.this, VerifyActivity.class);
//                    intent.putExtra("money","money");
//                    startActivityForResult(intent,1);
//                }
//            }).setTitle("非常抱歉").setMsg("您需要完成实名认证，绑定您的银行卡方可提取奖励现金").setPositiveButton("立即绑定").show();
//        }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1){
            if (resultCode==2){
                isVerify = MyApplication.mApp.isUserIsVerify();
            }
        }
    }

    ////广播接收内部类
    //private class MyBroadcastReciver extends BroadcastReceiver {
    //    @Override
    //    public void onReceive(Context context, Intent intent) {
    //        String action = intent.getAction();
    //        if (action.equals("刷新提现")) {
    //            initView();
    //
    //            //在结束时可取消广播
    //            unregisterReceiver(this);
    //        }
    //    }
    //}

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshEmbody(RefreshEmbodyBean bean){
        if (bean!=null&&bean.getEmbody().equals("刷新提现")){
            initView();
        }
    }
}
