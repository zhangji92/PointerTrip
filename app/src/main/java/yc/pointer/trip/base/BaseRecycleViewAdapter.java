package yc.pointer.trip.base;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 刘佳伟
 * 2017/7/24
 * 15:13
 * RecycleView基础适配器
 */
public  abstract  class BaseRecycleViewAdapter<T,V extends BaseRecycleViewAdapter.ViewHolder> extends RecyclerView.Adapter<V> {

    //数据集合
    private final List<T> mDataSet = new ArrayList<>();

    private itemViewOnClickListener itemViewOnClickListener;

    public void setItemViewOnClickListener(BaseRecycleViewAdapter.itemViewOnClickListener itemViewOnClickListener) {
        this.itemViewOnClickListener = itemViewOnClickListener;
    }



    @Override
    public V onCreateViewHolder(ViewGroup parent, int viewType) {
        //获取视图
        View itemView=null;
        ViewHolder holder=null;
        if (itemView==null){
            itemView=createItemViewIfNotExist(itemView,parent);
        }else {
            holder= getItemViewHolder(itemView);
        }

        return (V) holder;
    }

    @Override
    public void onBindViewHolder(V holder, final int position) {
        holder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    itemViewOnClickListener.onItemClick(position);

            }
        });

    }



    @Override
    public int getItemCount() {
        return mDataSet.size()==0?0:mDataSet.size();
    }
    @LayoutRes
    protected abstract int getItemViewLayout();

    protected abstract V getItemViewHolder(View itemView);


    private View createItemViewIfNotExist(View itemView, ViewGroup parent) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(getItemViewLayout(), parent, false);
            itemView.setTag(getItemViewHolder(itemView));
        return itemView;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        protected final View mItemView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            this.mItemView = itemView;
        }
    }
    //item点击事件
    public interface itemViewOnClickListener{
         void onItemClick(int position);
    }
}
