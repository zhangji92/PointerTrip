package yc.pointer.trip.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
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
 * 2017/7/13
 * 12:25
 */
public class UnpaidAdapter extends RecyclerView.Adapter<UnpaidAdapter.ViewHolder> {


    private List<UnpaidBean.DataBean> mDataList;
    private itemViewOnClickListener itemViewOnClickListener;

    public UnpaidAdapter(List<UnpaidBean.DataBean> mDataList) {
        this.mDataList = mDataList;
    }

    public void setItemViewOnClickListener(itemViewOnClickListener itemViewOnClickListener) {
        this.itemViewOnClickListener = itemViewOnClickListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = null;
        ViewHolder holder = null;
        if (itemView == null) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_unpaid_item, parent, false);
            holder = new ViewHolder(itemView);
            itemView.setTag(holder);
        } else {
            holder = (ViewHolder) itemView.getTag();
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {


        if (mDataList.size() > 0) {
            holder.bingHolder(mDataList.get(position));

            holder.unpaidItemview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemViewOnClickListener.OnItemClickBack(mDataList.get(position).getOid());
                }
            });
            holder.unpaidPay.setOnClickListener(new View.OnClickListener() {//去支付按钮
                @Override
                public void onClick(View v) {
                    itemViewOnClickListener.onButtonClickBack(mDataList.get(position).getOid(), R.id.unpaid_pay);
                }
            });
            holder.cancelOrder.setOnClickListener(new View.OnClickListener() {//取消按钮
                @Override
                public void onClick(View v) {
                    itemViewOnClickListener.onButtonClickBack(mDataList.get(position).getOid(), R.id.cancel_order);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size() == 0 ? 0 : mDataList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.unpaid_send_order_time)
        TextView unpaidSendOrderTime;//发单的时间
        @BindView(R.id.unpaid_head)
        CustomCircleImage unpaidHead;//头像
        @BindView(R.id.grad_sex)
        ImageView mImageSex;//性别

        @BindView(R.id.unpaid_scenic)
        TextView unpaidScenic;//景点
        @BindView(R.id.unpaid_person)
        TextView unpaidPerson;//人数
        @BindView(R.id.unpaid_time)
        TextView unpaidTime;//游玩的时间
        @BindView(R.id.unpaid_linear)
        LinearLayout unpaidLinear;
        @BindView(R.id.unpaid_preface)
        TextView unpaidPreface;
        @BindView(R.id.unpaid_money)
        TextView unpaidMoney;
        @BindView(R.id.unpaid_cost)
        LinearLayout unpaidCost;
        @BindView(R.id.unpaid_itemview)
        RelativeLayout unpaidItemview;
        @BindView(R.id.unpaid_pay)
        Button unpaidPay;
        @BindView(R.id.cancel_order)
        Button cancelOrder;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
//            AutoUtils.auto(itemView);
        }

        private void bingHolder(UnpaidBean.DataBean bean) {
            unpaidSendOrderTime.setText(bean.getAddtime());
            OkHttpUtils.displayImg(unpaidHead, bean.getPic());
            unpaidScenic.setText(bean.getSpot());
            unpaidPerson.setText(bean.getAmount()+"人");
            String time_way = bean.getTime_way();
            if (!StringUtil.isEmpty(time_way)){
                if (time_way.equals("1")){
                    unpaidTime.setText(bean.getTime_num()+"小时");
                } else {
                    unpaidTime.setText(bean.getTime_num()+"天");
                }
            }
            unpaidMoney.setText(bean.getMoney());
            if (bean.getSex().equals("男")) {
                mImageSex.setImageResource(R.mipmap.make_man);
            }else if (bean.getSex().equals("女")){
                mImageSex.setImageResource(R.mipmap.make_woman);
            }
        }

    }

    //点击事件接口回调
    public interface itemViewOnClickListener {
        void OnItemClickBack(String oid);

        void onButtonClickBack(String oid, int resId);
    }
}
