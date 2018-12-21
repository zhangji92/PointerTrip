package yc.pointer.trip.base;

import android.content.Context;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by 刘佳伟
 * 2017/7/24
 * 16:31
 * 带多布局类型加载的RecycleView适配器
 */
public class MultiItemCommonAdapter<T> extends BaseAdapter<T> {

    protected MultiItemTypeSupport<T> mMultiItemTypeSupport;

    public MultiItemCommonAdapter(Context context, List<T> data,MultiItemTypeSupport<T> multiItemTypeSupport) {
        super(context, data, -1);
        mMultiItemTypeSupport = multiItemTypeSupport;
    }

    @Override
    public int getItemViewType(int position) {
        return mMultiItemTypeSupport.getItemViewType(position, data.get(position));
    }
    @Override

    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = mMultiItemTypeSupport.getLayoutId(viewType);
        BaseViewHolder holder = BaseViewHolder.get(context, parent, layoutId);
        return holder;
    }



    @Override
    public void setData(BaseViewHolder holder, T data) {

    }

    public interface MultiItemTypeSupport<T> {
        int getLayoutId(int itemType);

        int getItemViewType(int position, T t);
    }
}

