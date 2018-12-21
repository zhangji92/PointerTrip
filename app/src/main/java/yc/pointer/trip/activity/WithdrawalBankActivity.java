package yc.pointer.trip.activity;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import yc.pointer.trip.R;
import yc.pointer.trip.adapter.ChooseWithdrawalAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.base.BaseBean;
import yc.pointer.trip.bean.ChooseWithdrawalBean;
import yc.pointer.trip.bean.WithdrawalPublishBean;
import yc.pointer.trip.bean.eventbean.RefreshEmbodyBean;
import yc.pointer.trip.event.CashMoneyEvent;
import yc.pointer.trip.event.ChooseWithdrawalEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.DialogKnow;
import yc.pointer.trip.view.DialogSure;
import yc.pointer.trip.view.LoadDialog;
import yc.pointer.trip.view.ToolbarWrapper;

/**
 * Created by moyan on 2018/1/16.
 * 提现到银行卡
 */

public class WithdrawalBankActivity extends BaseActivity {

    @BindView(R.id.this_cash)
    TextView thisCash;
    @BindView(R.id.remain_cash)
    TextView remainCash;
    //    @BindView(R.id.button)
//    Button button;
//    @BindView(R.id.alipay_number)
//    EditText alipay;
    @BindView(R.id.recycle_withdrawal)
    RecyclerView recyclerView;
    @BindView(R.id.empty_img)
    ImageView emptyImg;
    @BindView(R.id.adapter_empty)
    TextView adapterEmpty;
    @BindView(R.id.empty)
    LinearLayout empty;


    private String cash;

    private String mDevcode;
    private String mUserId;
    private long mTimestamp;
    private String alipayNumber = "";
    private String a_num;

    private double withdrawalMoney = 0.00;
    private double remainMoney = 0.00;//剩余金额

    private LoadDialog mLoadDialog;

    private boolean isDialogSure = true;//判断是否绑定支付宝

    private List<ChooseWithdrawalBean.DataBean> chooseData = new ArrayList<>();//选择提现的列表
    private List<WithdrawalPublishBean> withdrawalData = new ArrayList<>();//选中列表
    private ChooseWithdrawalAdapter adapter;
    private DecimalFormat df;

    @Override
    protected int getContentViewLayout() {
        return R.layout.withdrawal_bank_layout;
    }

    @Override
    protected void initView() {
        ToolbarWrapper toolbarWrapper = new ToolbarWrapper(this);
        toolbarWrapper.setCustomTitle(R.string.withdrawal_title);

        mLoadDialog = new LoadDialog(this, "正在加载...", R.drawable.load_animation);
//        mLoadDialog.show();
        mDevcode = ((MyApplication) getApplication()).getDevcode();
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();
        mUserId = ((MyApplication) getApplication()).getUserId();

        alipayNumber = ((MyApplication) getApplication()).getAlipayNumber();

        cash = getIntent().getStringExtra("cash");
        df = new DecimalFormat("#.##");

        getChooseWithdrawalList();

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapter = new ChooseWithdrawalAdapter(chooseData, alipayNumber);

        recyclerView.setAdapter(adapter);


        adapter.setListener(new ChooseWithdrawalAdapter.itemClickListener() {

            @Override
            public void addMoney(CheckBox checkBox, double addMoney, int position) {
                String type = chooseData.get(position).getType();
                String money = chooseData.get(position).getMoney();
                WithdrawalPublishBean withdrawalPublishBean = new WithdrawalPublishBean();
                withdrawalPublishBean.setType(type);
                if (withdrawalMoney == 0.00) {
                    if (addMoney > 50.00) {

                        withdrawalMoney = 50.00;
                        Toast.makeText(WithdrawalBankActivity.this, "单笔提现金额最高为50元", Toast.LENGTH_SHORT).show();
                        remainMoney = remainMoney - withdrawalMoney;
                        withdrawalPublishBean.setMoney("50.00");
                        withdrawalData.add(withdrawalPublishBean);

//                        df.format(withdrawalMoney);
//                        df.format(remainMoney);
//                        thisCash.setText(String.valueOf(withdrawalMoney));
//                        remainCash.setText(String.valueOf(remainMoney));
                            String textWithdrawal = df.format(Math.abs(withdrawalMoney));
                        String textremainMoney = df.format(Math.abs(remainMoney));
                        thisCash.setText(textWithdrawal);
                        remainCash.setText(textremainMoney);
                        return;
                    }
                }
                withdrawalMoney += addMoney;
                if (withdrawalMoney > 50.00) {
                    checkBox.setChecked(false);
                    withdrawalMoney = (withdrawalMoney - addMoney);
                    Toast.makeText(WithdrawalBankActivity.this, "单笔提现金额最高为50元", Toast.LENGTH_SHORT).show();
                } else {
                    remainMoney -= addMoney;
                    withdrawalPublishBean.setMoney(money);
                    withdrawalData.add(withdrawalPublishBean);
                }
                ;

                String textWithdrawal = df.format(Math.abs(withdrawalMoney));
                String textremainMoney = df.format(Math.abs(remainMoney));
                thisCash.setText(textWithdrawal);
                remainCash.setText(textremainMoney);
            }

            @Override
            public void subMoney(CheckBox checkBox, double subMoney, int position) {
                String type = chooseData.get(position).getType();
                for (int i = 0; i < withdrawalData.size(); i++) {
                    if (!StringUtil.isEmpty(type) && type.equals(withdrawalData.get(i).getType())) {
                        withdrawalData.remove(i);
                    }
                }
                if (subMoney > 50.00) {
                    withdrawalMoney = 0.00;
                    remainMoney += 50.00;

//                    df.format(withdrawalMoney);
//                    df.format(remainMoney);
//                    thisCash.setText(String.valueOf(withdrawalMoney));
//                    remainCash.setText(String.valueOf(remainMoney));
                    String textWithdrawal = df.format(Math.abs(withdrawalMoney));
                    String textremainMoney = df.format(Math.abs(remainMoney));
                    thisCash.setText(textWithdrawal);
                    remainCash.setText(textremainMoney);
                    return;
                }
                withdrawalMoney -= subMoney;
                remainMoney += subMoney;
//                df.format(withdrawalMoney);
//                df.format(remainMoney);
//                thisCash.setText(String.valueOf(withdrawalMoney));
//                remainCash.setText(String.valueOf(remainMoney));
                String textWithdrawal = df.format(Math.abs(withdrawalMoney));
                String textremainMoney = df.format(Math.abs(remainMoney));
                if (textWithdrawal.equals("0")){
                    withdrawalMoney=0.0;
                }

                thisCash.setText(textWithdrawal);
                remainCash.setText(textremainMoney);

            }

            @Override
            public void getwithdrawalMessage(final String alipayNum) {
                //申请提现
                final String money = thisCash.getText().toString().trim();
                Gson gson = new Gson();
                final String withdrawalInfo = gson.toJson(withdrawalData);

                if (!StringUtil.isEmpty(alipayNum)) {
                    if (!StringUtil.isEmpty(alipayNumber)) {
                        if (!alipayNum.equals(alipayNumber)) {
                            isDialogSure = true;
                        } else {
                            isDialogSure = false;
                        }
                    } else {
                        isDialogSure = true;
                    }
                    if (isDialogSure) {
                        if (Double.parseDouble(money) >= 5.00) {
                            new DialogSure(WithdrawalBankActivity.this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                                @Override
                                public void onClickListener(DialogSure dialogSure, boolean trueEnable) {
                                    if (trueEnable) {
                                        //请求接口
                                        getCash(WithdrawalBankActivity.this, money, withdrawalInfo, alipayNum);
                                    }
                                }
                            }).setTitle("提现账号确认")
                                    .setMsg("您绑定的支付宝账号：" + alipayNum)
                                    .setPositiveButton("确认")
                                    .setNegativeButton("修改").show();
                        } else {
                            Toast.makeText(WithdrawalBankActivity.this, "单笔金额满五元才可发起提现", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        if (Double.parseDouble(money) >= 5.00) {
                            //请求接口
                            getCash(WithdrawalBankActivity.this, money, withdrawalInfo, alipayNum);

                        } else {
                            Toast.makeText(WithdrawalBankActivity.this, "单笔金额满五元才可发起提现", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(WithdrawalBankActivity.this, "请您填写支付宝账号", Toast.LENGTH_SHORT).show();
                }

            }
        });


//        button.setClickable(false);
//        button.setBackground(getResources().getDrawable(R.drawable.unenable_background));
//
//        if (!StringUtil.isEmpty(alipayNumber)) {
//            alipay.setText(alipayNumber);
//            alipay.setCursorVisible(false);
//            isDialogSure = false;
//        } else {
//            alipay.setText("");
//            isDialogSure = true;
//        }
//
//        if (!StringUtil.isEmpty(cash)) {
//            double integer = Double.valueOf(cash);
//            if (integer < 5) {
//                //不可提现
//                thisCash.setText(cash);
//                remainCash.setText("0.00");
//                button.setClickable(false);
//                button.setBackground(getResources().getDrawable(R.drawable.unenable_background));

//            } else if (integer >= 5 && integer <= 50) {
//                thisCash.setText(cash);
//                remainCash.setText("0.00");
//                button.setClickable(true);
//                button.setBackground(getResources().getDrawable(R.drawable.adapter_order_txt));
//            } else {
//                double remain = integer - 50;
//                thisCash.setText("50.00");
//                remainCash.setText(String.valueOf(remain));
//                button.setClickable(true);
//                button.setBackground(getResources().getDrawable(R.drawable.adapter_order_txt));
//
//            }
//        }

    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

    /**
     * 获取提现类型列表
     */
    private void getChooseWithdrawalList() {
        boolean timeFlag = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
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
            OkHttpUtils.getInstance().post(URLUtils.CHOOSE_CASH, requestBody, new HttpCallBack(new ChooseWithdrawalEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getChooseCash(ChooseWithdrawalEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络状态异常", Toast.LENGTH_SHORT).show();
            return;
        }
        ChooseWithdrawalBean data = event.getData();
        if (data.getStatus() == 0) {
            List<ChooseWithdrawalBean.DataBean> data1 = data.getData();
            if (data1.size() != 0) {
                recyclerView.setVisibility(View.VISIBLE);
                empty.setVisibility(View.GONE);
                chooseData.addAll(data1);
                adapter.notifyDataSetChanged();
                for (int i = 0; i < chooseData.size(); i++) {
                    String money = chooseData.get(i).getMoney();
                    remainMoney += Double.parseDouble(money);
                }
                String remainMoneyNum = String.valueOf( df.format(remainMoney));
                remainCash.setText(remainMoneyNum);
            } else {
                recyclerView.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
                empty.setBackgroundColor(Color.parseColor("#ededed"));
                adapterEmpty.setText("哎呀~您还没有可提现的收益");
                emptyImg.setImageResource(R.mipmap.no_income);
            }
        }
    }


    /**
     * 发起提现
     *
     * @param context 上下文
     */
    private void getCash(Context context, String money, String withdrawalInfo, String alipayNumber) {
        boolean timeFlag = APPUtils.judgeTimeDev(context, mDevcode, mTimestamp);
        if (timeFlag) {
            if (StringUtil.isEmpty(mUserId)) {
                Toast.makeText(this, "无效的用户Id", Toast.LENGTH_SHORT).show();
                return;
            }
            String str = Md5Utils.createMD5("alipay=" + alipayNumber + "devcode=" + mDevcode + "list=" + withdrawalInfo + "money=" + money + "timestamp=" + mTimestamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", mUserId)
                    .add("devcode", mDevcode)
                    .add("money", money)
                    .add("list", withdrawalInfo)
                    .add("alipay", alipayNumber)
                    .add("signature", str)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.CASH, requestBody, new HttpCallBack(new CashMoneyEvent()));
        }
        mLoadDialog.show();//显示动画

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void cashWithdrawal(CashMoneyEvent event) {

        if (event.isTimeOut()) {
            mLoadDialog.dismiss();//停止动画
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        BaseBean data = event.getData();
        if (data.getStatus() == 0) {
            mLoadDialog.dismiss();//停止动画
            if (!StringUtil.isEmpty(a_num)) {
                ((MyApplication) getApplication()).setAlipayNumber(a_num);
            }
            String msg = data.getMsg();
            new DialogKnow(this, R.style.user_default_dialog, new DialogKnow.CallBackListener() {
                @Override
                public void onClickListener() {
                    //Intent intent = new Intent();
                    //intent.setAction("刷新提现");
                    //sendBroadcast(intent);
                    EventBus.getDefault().post(new RefreshEmbodyBean("刷新提现"));
                    finish();
                }
            }).setTitle("提交成功").setMsg(msg).setPositiveButton("我知道了").show();
//            Intent intent = new Intent();
//            intent.setAction("刷新提现");
//            sendBroadcast(intent);
//            finish();
        } else {
            mLoadDialog.dismiss();//停止动画
            Toast.makeText(this, data.getMsg(), Toast.LENGTH_SHORT).show();
        }
    }

//    @OnClick({R.id.button, R.id.alipay_number})
//    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.alipay_number:
//                alipay.setCursorVisible(true);
//                break;
//
//            case R.id.button:
//
//                final String money = thisCash.getText().toString().trim();
//                a_num = alipay.getText().toString().trim();
//                if (!StringUtil.isEmpty(a_num)) {
//                    if (!StringUtil.isEmpty(alipayNumber)) {
//                        if (!a_num.equals(alipayNumber)) {
//                            isDialogSure = true;
//                        } else {
//                            isDialogSure = false;
//                        }
//                    } else {
//                        isDialogSure = true;
//                    }
//                    if (isDialogSure) {
//                        new DialogSure(this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
//                            @Override
//                            public void onClickListener(DialogSure dialogSure, boolean trueEnable) {
//                                if (trueEnable) {
//                                    getCash(WithdrawalBankActivity.this, money, a_num);
//                                } else {
//                                    alipay.setCursorVisible(true);
//                                }
//                              }
//                        }).setTitle("提现账号确认").setMsg("您绑定的支付宝账号：" + a_num).setPositiveButton("确认").setNegativeButton("修改").show();
//                    } else {
//                        getCash(WithdrawalBankActivity.this, money, a_num);
//                    }
//                } else {
//                    Toast.makeText(this, "请您填写支付宝账号", Toast.LENGTH_SHORT).show();
//                }
//                break;
//        }

//    }


}
