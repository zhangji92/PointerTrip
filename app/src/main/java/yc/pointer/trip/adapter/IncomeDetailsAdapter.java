package yc.pointer.trip.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.base.BaseViewHolder;
import yc.pointer.trip.bean.IncomeDetailsBean;
import yc.pointer.trip.untils.StringUtil;

/**
 * Created by moyan on 2018/4/24
 */

public class IncomeDetailsAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private final int VIEWTYPEHEAD = 0;
    private final int VIEWTYPDEFAULT = 1;


    private Context context;
    private List<IncomeDetailsBean.DataBean> dataList = new ArrayList<>();
    private IncomeDetailsBean.EarningsBean earningsBean;//累计金额  剩余金额的对象

    public IncomeDetailsAdapter(List<IncomeDetailsBean.DataBean> dataList, IncomeDetailsBean.EarningsBean earningsBean) {
        this.dataList = dataList;
        this.earningsBean = earningsBean;
    }


    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        ViewHeadholder headholder = null;
        ViewDefaultHolder defaultHolder = null;
        context = parent.getContext();
        if (viewType == VIEWTYPEHEAD) {
            view = LayoutInflater.from(context).inflate(R.layout.incomedetails_head, parent, false);
            headholder = new ViewHeadholder(view, context);
            return headholder;
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.incomedetails_default, parent, false);
            defaultHolder = new ViewDefaultHolder(view, context);
            return defaultHolder;
        }

    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {

        if (getItemViewType(position) == VIEWTYPEHEAD) {
            ((ViewHeadholder)holder).onBindViewHeadHolder(earningsBean);
        } else {
            ((ViewDefaultHolder)holder).onBindViewDefaultHolder(dataList,position-1);
        }

    }

    @Override
    public int getItemCount() {
        return dataList.size() == 0 ? 0 : dataList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEWTYPEHEAD;
        } else {
            return VIEWTYPDEFAULT;
        }
    }

    public static class ViewHeadholder extends BaseViewHolder {
        @BindView(R.id.total_income)
        TextView totalIncome;
        @BindView(R.id.surplus_income)
        TextView surplusIncome;

        public ViewHeadholder(View itemView, Context context) {
            super(itemView, context);
            ButterKnife.bind(this, itemView);
        }

        public void onBindViewHeadHolder(IncomeDetailsBean.EarningsBean earningsBean) {
            String u_all_money = earningsBean.getU_all_money();
            if (!StringUtil.isEmpty(u_all_money)) {
                totalIncome.setText(u_all_money);
            }
            String u_money = earningsBean.getU_money();
            if (!StringUtil.isEmpty(u_money)) {
                surplusIncome.setText(u_money);
            }
        }
    }

    public static class ViewDefaultHolder extends BaseViewHolder {

        @BindView(R.id.income_info)
        TextView incomeInfo;
        @BindView(R.id.income_times)
        TextView incomeTimes;
        @BindView(R.id.income_money)
        TextView incomeMoney;

        public ViewDefaultHolder(View itemView, Context context) {
            super(itemView, context);
            ButterKnife.bind(this, itemView);
        }

        public void onBindViewDefaultHolder(List<IncomeDetailsBean.DataBean> dataList, int position) {
            if (dataList.size()>0){
                incomeInfo.setText(dataList.get(position).getInfo());
                String is_tx = dataList.get(position).getIs_tx();

                if (!StringUtil.isEmpty(is_tx)&&is_tx.equals("1")){

                    incomeMoney.setText("-"+dataList.get(position).getMoney());

                }else {
                    incomeMoney.setText("+"+dataList.get(position).getMoney());
                }

                String addtime = dataList.get(position).getAddtime();
                if (!StringUtil.isEmpty(addtime)){
                    String strTimeTomm = StringUtil.getStrTimeTomm(addtime);
                    Date strTimeDate = StringUtil.getStrTimeDate(strTimeTomm);
                    String format = StringUtil.format(strTimeDate);
                    incomeTimes.setText(format);
                }
            }
        }

    }
}

