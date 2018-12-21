package yc.pointer.trip.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.base.BaseViewHolder;
import yc.pointer.trip.bean.ChooseWithdrawalBean;
import yc.pointer.trip.untils.StringUtil;

/**
 * Created by moyan on 2018/4/26.
 */

public class ChooseWithdrawalAdapter extends RecyclerView.Adapter<BaseViewHolder> {


    private static final int VIEWTYPEDEFAULT = 0;
    private static final int VIEWTYPEFOOT = 1;


    private Context context;
    private List<ChooseWithdrawalBean.DataBean> chooseData = new ArrayList<>();//选择提现的列表

    private Drawable drawable;
    private String alipay="";

    private itemClickListener listener;
    private String editAlipayNum;


    public void setListener(itemClickListener listener) {
        this.listener = listener;
    }


    public ChooseWithdrawalAdapter(List<ChooseWithdrawalBean.DataBean> chooseData,String alipay) {
        this.chooseData = chooseData;
        this.alipay = alipay;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = null;
        WithdrawalViewHolder withdrawalViewHolder = null;
        if (viewType == VIEWTYPEDEFAULT) {
            view = LayoutInflater.from(context).inflate(R.layout.choose_withdrawal_item, parent, false);
            withdrawalViewHolder = new WithdrawalViewHolder(view, context);
            return withdrawalViewHolder;
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.wiehdrawal_foot, parent, false);
            WithdrawalFootViewHolder withdrawalFootViewHolder = new WithdrawalFootViewHolder(view, context);
            return withdrawalFootViewHolder;
        }


    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, final int position) {
        if (getItemViewType(position)==VIEWTYPEDEFAULT) {
            if (chooseData.size()>0) {
                final String is_order = chooseData.get(position).getIs_order();
                final String money = chooseData.get(position).getMoney();
                String remark = chooseData.get(position).getRemark();
                String type = chooseData.get(position).getType();
                if (!StringUtil.isEmpty(type)) {
                    if (type.equals("0")) {
                        //视频收益
                        drawable = context.getResources().getDrawable(R.mipmap.icon_videoreturns8);
                    } else if (type.equals("1")) {
                        //播放
                        drawable = context.getResources().getDrawable(R.mipmap.icon_broadcastrevenue);
                    } else if (type.equals("2")) {
                        //点赞
                        drawable = context.getResources().getDrawable(R.mipmap.icon_thumbupearnings);
                    } else if (type.equals("3")) {
                        //粉丝
                        drawable = context.getResources().getDrawable(R.mipmap.icon_fansearnings);
                    } else if (type.equals("4")) {
                        //订单
                        drawable = context.getResources().getDrawable(R.mipmap.icon_orderincome);
                    } else if (type.equals("5")) {
                        //邀请
                        drawable = context.getResources().getDrawable(R.mipmap.icon_inviteearnings);
                    }else if (type.equals("6")){
                        //任务
                        drawable = context.getResources().getDrawable(R.mipmap.icon_task_earnings);
                    }
                }

                ((WithdrawalViewHolder) holder).withdrawalType.setText(remark);
                ((WithdrawalViewHolder) holder).withdrawalType.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                ((WithdrawalViewHolder) holder).withdrawalMoney.setText(money);
                ((WithdrawalViewHolder) holder).checkWithdrawal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (is_order.equals("1")) {
                            CheckBox checkWithdrawal = ((WithdrawalViewHolder) holder).checkWithdrawal;
                            boolean checked = checkWithdrawal.isChecked();
                            if (checked) {
                                //++
                                if (!StringUtil.isEmpty(money)) {
                                    if (listener!=null){
                                        listener.addMoney(checkWithdrawal, Double.parseDouble(money),position);
                                    }
                                }
                            } else {
                                //--
                                if (!StringUtil.isEmpty(money)) {
                                    if (listener!=null) {
                                        listener.subMoney(checkWithdrawal, Double.parseDouble(money),position);
                                    }
                                }
                            }
                        } else {
                            ((WithdrawalViewHolder) holder).checkWithdrawal.setChecked(false);
                            Toast.makeText(context, "您尚未开通指针会员，此项收益不能提现", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

                ((WithdrawalViewHolder) holder).checkWithdrawal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            chooseData.get(position).setChoose(true);
                        } else {
                            chooseData.get(position).setChoose(false);
                        }
                    }
                });
            }else {

            }
        }else if (getItemViewType(position)==VIEWTYPEFOOT){

            if (!StringUtil.isEmpty(alipay)){
                ((WithdrawalFootViewHolder) holder).alipayNumber.setText(alipay);
//                ((WithdrawalFootViewHolder) holder).alipayNumber.setEnabled(false);
            }

            ((WithdrawalFootViewHolder)holder).button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editAlipayNum = ((WithdrawalFootViewHolder) holder).alipayNumber.getText().toString().trim();
                    if (listener!=null) {
                        listener.getwithdrawalMessage(editAlipayNum);
                    }
                }
            });

            ((WithdrawalFootViewHolder) holder).alipayNumber.setSelection(((WithdrawalFootViewHolder) holder).alipayNumber.getText().toString().length());

        }


    }

    @Override
    public int getItemCount() {
        return chooseData.size() == 0 ? 1 : chooseData.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (chooseData.size()>0){
            if (position < chooseData.size()) {
                return VIEWTYPEDEFAULT;
            } else {
                return VIEWTYPEFOOT;
            }
        }else {
            return VIEWTYPEFOOT;
        }


    }

    public static class WithdrawalViewHolder extends BaseViewHolder {

        @BindView(R.id.withdrawal_type)
        TextView withdrawalType;
        @BindView(R.id.withdrawal_money)
        TextView withdrawalMoney;
        @BindView(R.id.check_withdrawal)
        CheckBox checkWithdrawal;

        public WithdrawalViewHolder(View itemView, Context context) {
            super(itemView, context);
            ButterKnife.bind(this, itemView);
        }


    }

    public static class WithdrawalFootViewHolder extends BaseViewHolder {

        @BindView(R.id.alipay_number)
        EditText alipayNumber;
        @BindView(R.id.button)
        Button button;

        public WithdrawalFootViewHolder(View itemView, Context context) {
            super(itemView, context);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface itemClickListener{
        void addMoney(CheckBox checkBox,double addMoney,int postion);
        void subMoney(CheckBox checkBox,double subMoney,int postion);
        void getwithdrawalMessage(String alipayNum);
    }
}
