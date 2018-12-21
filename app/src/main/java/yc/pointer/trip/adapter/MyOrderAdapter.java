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
import yc.pointer.trip.R;
import yc.pointer.trip.bean.UnpaidBean;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.CustomCircleImage;

import java.util.List;

/**
 * Created by 刘佳伟
 * 2017/7/21
 * 17:04
 */
public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.ViewHolder> {



    private List<UnpaidBean.DataBean> mDataList;
    private boolean moneyFlag;

    public MyOrderAdapter(List<UnpaidBean.DataBean> mDataList,boolean moneyFlag) {
        this.mDataList = mDataList;
        this.moneyFlag=moneyFlag;
    }

    private MyOrderCallBack mMyOrderCallBack;

    public void setmMyOrderCallBack(MyOrderCallBack mMyOrderCallBack) {
        this.mMyOrderCallBack = mMyOrderCallBack;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_my_order, parent, false);
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
            holder.bingHolder(mDataList.get(position));
        }

    }

    @Override
    public int getItemCount() {
        return mDataList.size() == 0 ? 0 : mDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.order_relative)
        RelativeLayout relativeLayout;
        @BindView(R.id.order_status_note)
        ImageView orderStatusNote;//订单状态
        @BindView(R.id.order_refund)
        ImageView orderStatusRefund;//退款状态
        @BindView(R.id.star_city)
        TextView starCity;//出发城市
        @BindView(R.id.destination_city)
        TextView destinationCity;//目的城市

        @BindView(R.id.order_head)
        CustomCircleImage customCircleImage;//头像
        @BindView(R.id.order_date)
        TextView orderDate;//时间
        @BindView(R.id.order_money)
        TextView orderMoney;//价钱
        @BindView(R.id.order_sex)
        ImageView imageSex;//价钱
        @BindView(R.id.order_number)
        TextView orderNumber;//订单编号

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
//            AutoUtils.auto(itemView);
        }

        private void bingHolder(final UnpaidBean.DataBean bean) {
            String number = bean.getNumber();

            if (!StringUtil.isEmpty(number)){
                orderNumber.setText("No."+number);
            }
            String startCity = bean.getSaddress();
            if (!StringUtil.isEmpty(startCity)){
                starCity.setText(startCity);
            }else {
                starCity.setText("");
            }
            String destinCity = bean.getMaddress();
            if (!StringUtil.isEmpty(destinCity)){
                destinationCity.setText(destinCity);
            }else {
                destinationCity.setText("");
            }
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMyOrderCallBack!=null){
                        mMyOrderCallBack.onClickBack(getLayoutPosition());
                    }

                }
            });

            String ord_status = bean.getOrd_status();
            if (ord_status.equals("0")) {
                orderStatusNote.setImageResource(R.mipmap.order_waiting);
            } else if (ord_status.equals("1")) {
                orderStatusNote.setImageResource(R.mipmap.orderr_not_begin);
            } else if (ord_status.equals("2")) {
                orderStatusNote.setImageResource(R.mipmap.order_have);
            } else if (ord_status.equals("3")||ord_status.equals("6")||ord_status.equals("7")) {
                orderStatusNote.setImageResource(R.mipmap.order_completed);
            } else if (ord_status.equals("4")) {
                orderStatusNote.setImageResource(R.mipmap.order_overdue);
            } else if (ord_status.equals("5")) {
                orderStatusNote.setImageResource(R.mipmap.order_cancel);
                if (bean.getTui_status().equals("0")) {//正常
                    orderStatusRefund.setVisibility(View.GONE);
                } else if (bean.getTui_status().equals("1")) {//待退款
                    orderStatusRefund.setVisibility(View.VISIBLE);
                    orderStatusRefund.setImageResource(R.mipmap.order_refundable);
                } else if (bean.getTui_status().equals("2")) {//已退款
                    orderStatusRefund.setVisibility(View.VISIBLE);
                    orderStatusRefund.setImageResource(R.mipmap.order_refunded);
                }
            } else if (ord_status.equals("8")){
                orderStatusNote.setImageResource(R.mipmap.order_not_pay);
            }

            if (bean.getSex().equals("男")) {
                imageSex.setImageResource(R.mipmap.make_man);
            } else if (bean.getSex().equals("女")) {
                imageSex.setImageResource(R.mipmap.make_woman);
            }
            orderDate.setText(bean.getSend_time());
            OkHttpUtils.displayImg(customCircleImage, bean.getPic());
            if (moneyFlag){
                orderMoney.setText(bean.getMoney()); //已发，显示订单全部金额
            }else {
                orderMoney.setText(bean.getMoney_y());//已抢，显示预计收入
            }
        }
    }

    public interface MyOrderCallBack {
        void onClickBack(int position);
    }
}
