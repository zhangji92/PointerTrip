package yc.pointer.trip.base;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * RecycleView通用Adapter
 * @param <T>  数据类型bean
 */


public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {

    protected   Context context;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    protected   List<T> data;
    private  int layoutId;
    private OnItemClickListener itemClickListener;

    public BaseAdapter(Context context, List<T> data, int layoutId) {
        this.context = context;
        this.data = data;
        this.layoutId = layoutId;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(context).inflate(layoutId,parent,false);
        BaseViewHolder holder = new BaseViewHolder(itemView,context);
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, final int position) {
        if(itemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onItemClick(view,position);
                }
            });
        }
        setData(holder,data.get(position));

    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size() ;
    }

    public abstract void setData(BaseViewHolder holder,T data);

    /** item点击监听接口 */
    public interface OnItemClickListener{
        /**
         * item点击监听回调方法
         * @param view
         * @param position
         */
        void onItemClick(View view, int position);

    }

    /**
     * 设置item点击监听
     * @param itemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener itemClickListener){

        this.itemClickListener = itemClickListener;
    }

    /**
     * 更新数据
     * @param data
     */
    public void updataData(List<T> data){
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    /**
     * 添加数据
     * @param data
     */
    public void addData(List<T> data){
        this.data.addAll(data);
        notifyDataSetChanged();
    }
}
