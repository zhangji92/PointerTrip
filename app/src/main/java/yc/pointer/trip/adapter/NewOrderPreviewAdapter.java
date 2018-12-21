package yc.pointer.trip.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.base.BaseViewHolder;
import yc.pointer.trip.bean.GoTravelMesgBeanNew;
import yc.pointer.trip.bean.NewOrderpreviewMode;
import yc.pointer.trip.untils.StringUtil;

/**
 * Created by moyan on 2018/7/9.
 */

public class NewOrderPreviewAdapter extends RecyclerView.Adapter<NewOrderPreviewAdapter.OrderPreviewViewHolder> {


    private Context mContext;
//    private String[] orderPreviewTitle = {"景点名称", "出行人数", "出行时长", "出行日期", "导游证件",
//            "用车服务", "接单性别", "具体要求", "计价规则", "订单总价"};

    private List<NewOrderpreviewMode> mListData = new ArrayList<>();//信息列表

    public NewOrderPreviewAdapter(List<NewOrderpreviewMode> mListData) {
        this.mListData = mListData;
    }

    @Override
    public OrderPreviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = null;
        OrderPreviewViewHolder orderPreviewViewHolder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.new_orderpreview_adapter_item, parent, false);
            orderPreviewViewHolder = new OrderPreviewViewHolder(view, mContext);
            view.setTag(orderPreviewViewHolder);
        } else {
            orderPreviewViewHolder = (OrderPreviewViewHolder) view.getTag();
        }

        return orderPreviewViewHolder;
    }

    @Override
    public void onBindViewHolder(OrderPreviewViewHolder holder, int position) {

        if (mListData.size() > 0) {
            holder.setOrderContents(position);
        }

    }

    @Override
    public int getItemCount() {
        return mListData.size() == 0 ? 0 : mListData.size();
    }

    public class OrderPreviewViewHolder extends BaseViewHolder {

        @BindView(R.id.order_preview_item_title)
        TextView orderPreviewItemTitle;//内容标题
        @BindView(R.id.order_content)
        TextView orderContent;//订单要求
        @BindView(R.id.price)
        TextView orderPrice;//价格信息

        public OrderPreviewViewHolder(View itemView, Context context) {
            super(itemView, context);
            ButterKnife.bind(this, itemView);
        }

        private void setOrderContents(int position) {
            String title = mListData.get(position).getOrderPreviewItemTitle();
            if (!StringUtil.isEmpty(title)) {
                orderPreviewItemTitle.setText(title);
            }
            String content = mListData.get(position).getOrderContent();
            if (!StringUtil.isEmpty(content)) {
                orderContent.setText(content);
            }
            String price = mListData.get(position).getPrice();
            String travelType = mListData.get(position).getTravelType();
            if (!StringUtil.isEmpty(price)) {
                orderPrice.setVisibility(View.VISIBLE);
                if (position == 4) {
                    if (travelType.equals("1")) {
                        price = "(" + price + "元/小时" + ")";
                    } else if (travelType.equals("2")) {
                        price = "(" + price + "元/天" + ")";
                    }
                } else if (position == 5) {

                    if (travelType.equals("1")) {
                        price = "(" + price + "元/小时" + ")";
                    } else if (travelType.equals("2")) {
                        price = "(" + price + "元/天" + ")";
                    }

                }
                orderPrice.setText(price);
            } else {
                orderPrice.setVisibility(View.GONE);
            }
            if (position == 8) {
                int unitprice = getUnitprice();
                orderContent.setText("￥" + String.valueOf(unitprice) + "元");
                orderContent.setTextColor(Color.parseColor("#45b0ff"));
            }

            if (position == 9) {
                int unitprice = getUnitprice();
                int timeLength = mListData.get(position).getTimeLength();
                String s = String.valueOf(unitprice * timeLength);
                orderContent.setText("￥" + s + "元");
                orderContent.setTextColor(Color.parseColor("#45b0ff"));
            }

        }

        /**
         * 获取订单单价
         *
         * @return
         */
        private int getUnitprice() {
            int carPrice = 0;
            int unitPrice = 0;
            String price2 = mListData.get(4).getPrice();
            int guidePrice = Integer.parseInt(price2);
            String price1 = mListData.get(5).getPrice();
            if (!StringUtil.isEmpty(price1)) {
                carPrice = Integer.parseInt(price1);
            }
            unitPrice = guidePrice + carPrice;//订单单价
            return unitPrice;
        }


    }
}
