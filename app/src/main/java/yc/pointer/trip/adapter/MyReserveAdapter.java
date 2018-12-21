package yc.pointer.trip.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import yc.pointer.trip.R;
import yc.pointer.trip.bean.MyReserveBean;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.CustomCircleImage;

import java.util.List;

/**
 * Created by moyan on 2017/9/7.
 */
public class MyReserveAdapter extends RecyclerView.Adapter<MyReserveAdapter.ViewHolder> {


    private List<MyReserveBean.DataBean> mDataList;

    private MyOrderCallBack mMyOrderCallBack;
    private boolean moneyFlag;

    public MyReserveAdapter(List<MyReserveBean.DataBean> mDataList,boolean moneyFlag) {
        this.mDataList = mDataList;
        this.moneyFlag = moneyFlag;
    }

    public void setmMyOrderCallBack(MyOrderCallBack mMyOrderCallBack) {
        this.mMyOrderCallBack = mMyOrderCallBack;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_my_reserve, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        return holder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mDataList.size() > 0) {
            holder.bindHolder(mDataList.get(position));
        }

    }

    @Override
    public int getItemCount() {
        return mDataList.size() == 0 ? 0 : mDataList.size();

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.order_status_note)
        ImageView orderStatusNote;//订单状态标签
        @BindView(R.id.order_refund)
        ImageView orderRefund;//待退款标签
        @BindView(R.id.star_city)
        TextView starCity;//出发城市
        @BindView(R.id.destination_city)
        TextView destinationCity;//目的城市
        @BindView(R.id.order_money)
        TextView orderMoney;//金额
        @BindView(R.id.order_head)
        CustomCircleImage orderHead;//头像
        @BindView(R.id.order_sex)
        ImageView orderSex;//性别标签
        @BindView(R.id.reserve_people)
        TextView reservePeople;//被预约人昵称
        @BindView(R.id.order_date)
        TextView orderDate;//订单日期
        @BindView(R.id.order_number)
        TextView orderNumber;//订单编号
        @BindView(R.id.reservr_relative)
        RelativeLayout reservrRelative;//总布局

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindHolder(final MyReserveBean.DataBean dataBean) {
            reservrRelative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMyOrderCallBack.onClickBack(getLayoutPosition());
                }
            });

            String nickname = dataBean.getNickname();//昵称
            String startAddress = dataBean.getSaddress();//出发城市
            String dendesteAddress = dataBean.getMaddress();//目的城市
            String money = dataBean.getMoney();//金额
            String money_y = dataBean.getMoney_y();//金额
            String pic = dataBean.getPic();//头像
            String number = dataBean.getNumber();//订单编号
            String ord_status = dataBean.getOrd_status();//订单状态
            String send_time = dataBean.getSend_time();//出发日期
            String tui_status = dataBean.getTui_status();//退款状态
            String sex = dataBean.getSex();

            if (!StringUtil.isEmpty(nickname)) {
                reservePeople.setText(nickname);
            }
            if (!StringUtil.isEmpty(startAddress)) {
                starCity.setText(startAddress);
            }
            if (!StringUtil.isEmpty(dendesteAddress)) {
                destinationCity.setText(dendesteAddress);
            }
            if (!StringUtil.isEmpty(money)) {
                if (moneyFlag){
                    orderMoney.setText(money);
                }
            }
            if (!StringUtil.isEmpty(money_y)) {
                if (!moneyFlag){
                    orderMoney.setText(money_y);
                }
            }


            if (!StringUtil.isEmpty(pic)) {
                OkHttpUtils.displayImg(orderHead, pic);
            }
            if (!StringUtil.isEmpty(number)) {
                orderNumber.setText("No." + number);
            }
            if (!StringUtil.isEmpty(send_time)) {
                orderDate.setText(send_time);
            }
            if (!StringUtil.isEmpty(tui_status)) {
                if (tui_status.equals("0")) {
                    orderRefund.setVisibility(View.GONE);
                } else if (tui_status.equals("1")) {
                    orderRefund.setVisibility(View.VISIBLE);
                    orderRefund.setImageResource(R.mipmap.order_refundable);
                } else if (tui_status.equals("2")) {
                    orderRefund.setVisibility(View.VISIBLE);
                    orderRefund.setImageResource(R.mipmap.order_refunded);
                }
            }
            if (!StringUtil.isEmpty(sex)) {
                if (sex.equals("男")) {
                    orderSex.setImageResource(R.mipmap.make_man);
                } else if (sex.equals("女")) {
                    orderSex.setImageResource(R.mipmap.make_woman);
                }
            }
            if (!StringUtil.isEmpty(ord_status)) {
                if (ord_status.equals("0")) {
                    orderStatusNote.setImageResource(R.mipmap.order_waiting);
                } else if (ord_status.equals("1")) {
                    orderStatusNote.setImageResource(R.mipmap.orderr_not_begin);
                } else if (ord_status.equals("2")) {
                    orderStatusNote.setImageResource(R.mipmap.order_have);
                } else if (ord_status.equals("3")) {
                    orderStatusNote.setImageResource(R.mipmap.order_completed);
                } else if (ord_status.equals("4")) {
                    orderStatusNote.setImageResource(R.mipmap.order_overdue);
                } else if (ord_status.equals("5")) {
                    orderStatusNote.setImageResource(R.mipmap.order_cancel);
                } else if (ord_status.equals("6")) {
                    orderStatusNote.setImageResource(R.mipmap.order_completed);
                } else if (ord_status.equals("7")) {
                    orderStatusNote.setImageResource(R.mipmap.order_completed);
                }
            }

        }
    }

    public interface MyOrderCallBack {
        void onClickBack(int position);
    }
}
