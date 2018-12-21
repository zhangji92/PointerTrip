package yc.pointer.trip.base;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;





public class BaseViewHolder extends RecyclerView.ViewHolder {

    public View itemView;
    private Context context;

    public BaseViewHolder(View itemView, Context context) {
        super(itemView);
        this.itemView = itemView;
        this.context = context;
    }



    /**
     * 通过id找View
     * @param viewID
     * @param <T>
     * @return
     */
    public <T extends View> T getViewById(@IdRes int viewID){
        return (T) itemView.findViewById(viewID);
    }

    /**
     * 给TextView设置文本
     * @param viewId
     * @param text
     */
    public void setText(@IdRes int viewId,String text){
        TextView tv = getViewById(viewId);
        tv.setText(text);
    }

    /**
     * ImageView设置网络图片
     * @param viewId
     * @param resid
     */
    public void setImageUrl(@IdRes int viewId,int resid){
        ImageView img  = getViewById(viewId);
        img.setBackgroundResource(resid);
    }


    public void setVisible(@IdRes int viewId){
        View view = getViewById(viewId);
        view.setVisibility(View.GONE);
    }

    public static BaseViewHolder get(Context context, ViewGroup parent, int layoutId) {

        View itemView = LayoutInflater.from(context).inflate(layoutId, parent,
                false);
        BaseViewHolder holder = new BaseViewHolder(itemView,context);
        return holder;
    }


}
