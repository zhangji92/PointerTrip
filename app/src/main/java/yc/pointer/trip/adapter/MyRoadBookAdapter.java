package yc.pointer.trip.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.bean.MyBookBean;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.untils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 刘佳伟
 * 2017/7/21
 * 14:38
 * 我的路书适配器
 */
public class MyRoadBookAdapter extends RecyclerView.Adapter<MyRoadBookAdapter.ViewHolder> {

    private Context mContext;
    private List<MyBookBean.DataBean> dataList = new ArrayList<>();

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener itemLongClickListener;


    public void setItemLongClickListener(OnItemLongClickListener itemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener;
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public MyRoadBookAdapter(Context mContext, List<MyBookBean.DataBean> dataList) {
        this.mContext = mContext;
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_my_roadbook, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (dataList.size() > 0) {
            String look_num = dataList.get(position).getLook_num();
            String zan_num = dataList.get(position).getZan_num();
            String bid = dataList.get(position).getBid();
            String pic = dataList.get(position).getB_pic();
            String title = dataList.get(position).getTitle();
            String updatetime = dataList.get(position).getUpdatetime();
            if (!StringUtil.isEmpty(updatetime)) {
                String updateTime = StringUtil.getStrTimeTomm(updatetime);
                holder.upteTime.setText("最新更新时间：" + updateTime);
            }
            if (StringUtil.isEmpty(look_num)) {
                holder.lookNum.setVisibility(View.GONE);
            } else {
                holder.lookNum.setVisibility(View.VISIBLE);
                holder.lookNum.setText(look_num);
            }
            if (StringUtil.isEmpty(zan_num)) {
                holder.zanNum.setVisibility(View.GONE);
            } else {
                holder.zanNum.setVisibility(View.VISIBLE);
                holder.zanNum.setText(zan_num);
            }

            OkHttpUtils.displayImg(holder.bookImg, "/" + pic);
            holder.booklTitle.setText(title);
            holder.itemMyBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(position);
                }
            });
            holder.itemMyBook.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    itemLongClickListener.OnLongClick(position);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size() == 0 ? 0 : dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_myBook)
        LinearLayout itemMyBook;//最外层布局
        @BindView(R.id.road_bill_img)
        ImageView bookImg;//路书图片
        @BindView(R.id.road_bill_title)
        TextView booklTitle;//路书标题
        @BindView(R.id.road_bill_time)
        TextView upteTime;//路书更新时间
        @BindView(R.id.road_bill_browse)
        TextView lookNum;//路书浏览数量
        @BindView(R.id.road_bill_collection)
        TextView zanNum;//路书点赞数量
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {
        void OnLongClick(int position);
    }
}
