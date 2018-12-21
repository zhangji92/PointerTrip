package yc.pointer.trip.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import yc.pointer.trip.R;
import yc.pointer.trip.adapter.DepositedAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.DepositedBean;
import yc.pointer.trip.bean.SaveMesgBean;
import yc.pointer.trip.event.BackDepositMoneyEvent;
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

/**
 * Created by moyan on 2018/1/25.
 */

public class DepositedActivity extends BaseActivity {
    @BindView(R.id.standard_toolbar)
    Toolbar standardToolbar;
    @BindView(R.id.recycleview_rights)
    RecyclerView recycleviewRights;

    private List<DepositedBean> mList = new ArrayList<>();

    private int[] depositImgArray = {R.mipmap.deposited_one, R.mipmap.deposited_two,
            R.mipmap.deposited_three, R.mipmap.deposited_forth,R.mipmap.deposited_five};
    private String[] depositTitleArray = {"指针大礼包", "“出发吧”发布订单",
            "“赚一赚”抢订单", "“一键预约”专属用户","现金提现"};
    private String[] depositInstroduceArray = {"指针会员认证用户可直接获得200元的大礼包",
            "定制最具有个性化的自由行旅游方式",
            "指针会员用户可获得更多收益",
            "可以与其他用户预约，相伴游玩，同时也可以作为向导，接取其他用户发出的预约订单"
            ,"指针会员用户可直接提取所获全类型现金收益"};

    private String mUserId;
//    private String yjMoney;//押金总额

    private String mDevcode;//设备识别码
    private long mTimestamp;//时间戳
    private boolean judgeTimeDev;

    private LoadDialog mLoadDialog;

    @Override
    protected int getContentViewLayout() {

        return R.layout.deposited_layout;

    }

    @Override
    protected void initView() {

        ToolbarWrapper toolbarWrapper = new ToolbarWrapper(this);
        toolbarWrapper.setCustomTitle(R.string.deposited);
        mUserId = SharedPreferencesUtils.getInstance().getString(this, "useId");
        mDevcode = ((MyApplication) getApplication()).getDevcode();
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();
        //判断是否获取时间戳和设备识别码
        judgeTimeDev = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
//        if (!mUserId.equals("not find")) {
//            yjMoney = ((MyApplication) getApplication()).getUserBean().getYj_money();
//        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycleviewRights.setLayoutManager(linearLayoutManager);
        for (int i = 0; i < depositImgArray.length; i++) {
            DepositedBean depositedBean = new DepositedBean();
            depositedBean.setIconDeposit(depositImgArray[i]);
            depositedBean.setTitle(depositTitleArray[i]);
            depositedBean.setRightsInstduce(depositInstroduceArray[i]);
            mList.add(depositedBean);
        }

        DepositedAdapter depositedAdapter = new DepositedAdapter(mList);
//        depositedAdapter.setSetDepositedMoneyListener(new DepositedAdapter.setDepositedMoney() {
//            @Override
//            public String setMoney() {
//                return yjMoney;
//            }
//        });
//        depositedAdapter.setBackDepositMoneyListener(new DepositedAdapter.backDepositMoney() {
//            @Override
//            public void backMoney() {
//                new DialogSure(DepositedActivity.this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
//                    @Override
//                    public void onClickListener(DialogSure dialogSure, boolean trueEnable) {
//                        if (trueEnable){
//                            backDepositmoney();
//                        }
//                    }
//                }).setTitle("温馨提示")
//                   .setMsg("退回押金后将不再享受以上权益，指针认证需重新进行审核，审核周期为1-3个工作日，是否继续？")
//                        .setPositiveButton("继续")
//                        .setNegativeButton("取消")
//                        .show();
//
//            }
//        });
        recycleviewRights.setAdapter(depositedAdapter);

    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

    /**
     * 请求退押金接口
     */
//    public void backDepositmoney() {
//        if (judgeTimeDev) {
//            String sign = Md5Utils.createMD5("devcode=" + mDevcode + "timestamp=" + mTimestamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
//            RequestBody requestBody = new FormBody.Builder()
//                    .add("devcode", mDevcode)
//                    .add("timestamp", String.valueOf(mTimestamp))
//                    .add("uid", mUserId)
//                    .add("signature", sign)
//                    .build();
//            OkHttpUtils.getInstance().post(URLUtils.BACK_DEPOSIT_MONEY, requestBody, new HttpCallBack(new BackDepositMoneyEvent()));
//            mLoadDialog = new LoadDialog(this, "正在处理...", R.drawable.load_animation);
//            mLoadDialog.show();
//        }
//
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void backDeposit(BackDepositMoneyEvent event) {
//        if (event.isTimeOut()) {
//            Toast.makeText(this, "网络异常，请稍后重试", Toast.LENGTH_SHORT).show();
//            mLoadDialog.dismiss();
//            return;
//
//        }
//
//        SaveMesgBean data = event.getData();
//        if (data.getStatus() == 0) {
//            mLoadDialog.dismiss();
//            ((MyApplication) getApplication()).setUserBean(data.getData());
//
//            String is_order = data.getData().getIs_order();
//
//            SharedPreferencesUtils.getInstance().putString(DepositedActivity.this, "isOrder", is_order);
//
//            if (!StringUtil.isEmpty(is_order) && is_order.equals("1")) {
////            ((MyApplication) getApplication()).setUserIsBinding(true);
//                SharedPreferencesUtils.getInstance().putBoolean(DepositedActivity.this, "isPayDeposit", true);
//            } else if (is_order.equals("0")) {
////            ((MyApplication) getApplication()).setUserIsBinding(false);
//                SharedPreferencesUtils.getInstance().putBoolean(DepositedActivity.this, "isPayDeposit", false);
//            }
//            new DialogKnow(DepositedActivity.this, R.style.user_default_dialog, new DialogKnow.CallBackListener() {
//                @Override
//                public void onClickListener() {
//                    Intent intent1 = new Intent();
//                    intent1.setAction("刷新支付");
//                    sendBroadcast(intent1);
//                    finish();
//                }
//            }).setTitle("温馨提示")
//                    .setMsg("操作成功,再次感谢您对《指针自由行》的信任，祝您生活愉快！")
//                    .setPositiveButton("我知道了")
//                    .show();
//
//        } else {
//
//            Toast.makeText(this, data.getMsg(), Toast.LENGTH_SHORT).show();
//            APPUtils.intentLogin(DepositedActivity.this, data.getStatus());
//            mLoadDialog.dismiss();
//
//        }
//
//    }

}
