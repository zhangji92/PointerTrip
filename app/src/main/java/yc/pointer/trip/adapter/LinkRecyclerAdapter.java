package yc.pointer.trip.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.base.BaseViewHolder;
import yc.pointer.trip.bean.TaskBean;
import yc.pointer.trip.network.OkHttpUtils;

/**
 * Created by 张继
 * 2018/9/25
 * 14:22
 * 公司：
 * 描述：任务排行榜列表
 */

public class LinkRecyclerAdapter extends RecyclerView.Adapter<LinkRecyclerAdapter.ViewHolder> {

    private List<TaskBean.DataBean> data;

    public LinkRecyclerAdapter(List<TaskBean.DataBean> data) {
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.adapter_link_recycler, null);
        return new ViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (data.size() > 0) {
            int position1 = position + 1;
            holder.bindData(data.get(position), position1);
        }
    }

    @Override
    public int getItemCount() {
        return data.size() == 0 ? 0 : data.size();
    }

    class ViewHolder extends BaseViewHolder {
        @BindView(R.id.item_link_num)
        TextView mTvNum;
        @BindView(R.id.item_link_head)
        ImageView mIvHead;
        @BindView(R.id.item_link_nick)
        TextView mTvNick;
        @BindView(R.id.item_link_money)
        TextView mTvMoney;
        private Context mContext;

        public ViewHolder(View itemView, Context context) {
            super(itemView, context);
            this.mContext = context;
            ButterKnife.bind(this, itemView);
        }

        public void bindData(TaskBean.DataBean bean, int position) {
            mTvNum.setText(position + "");
            OkHttpUtils.displayGlideCircular(mContext, mIvHead, bean.getPic());
            String nickname = bean.getNickname();
            mTvNick.setText(nickname);
            String money = bean.getMoney();
            mTvMoney.setText(money);
        }
    }
}
