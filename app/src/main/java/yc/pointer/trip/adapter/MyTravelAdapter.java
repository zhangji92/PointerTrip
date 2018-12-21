package yc.pointer.trip.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.bean.MyTravelBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 刘佳伟
 * 2017/7/21
 * 18:29
 */
public class MyTravelAdapter extends RecyclerView.Adapter<MyTravelAdapter.ViewHolder> {


    private List<MyTravelBean.DataBean> beanList = new ArrayList<>();

    public MyTravelAdapter(List<MyTravelBean.DataBean> beanList) {
        this.beanList = beanList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_my_travel, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (beanList.size()>0){
            holder.textScenic.setText(beanList.get(position).getMaddress());
            holder.textDate.setText(beanList.get(position).getTime());
            holder.textNum.setText(beanList.get(position).getOrd_amount());
            holder.textMoney.setText(beanList.get(position).getMoney());
        }
    }

    @Override
    public int getItemCount() {
        return beanList.size() == 0 ? 0 : beanList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.my_travel_scenic)
        TextView textScenic;
        @BindView(R.id.my_travel_date)
        TextView textDate;
        @BindView(R.id.my_travel_num)
        TextView textNum;
        @BindView(R.id.my_travel_money)
        TextView textMoney;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
