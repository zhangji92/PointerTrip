package yc.pointer.trip.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.bean.HotLabelBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/3.
 * 热门搜索适配器
 */
public class GridViewAdapter extends BaseAdapter {



    private LayoutInflater inflater;
    private List<String> data;

    public GridViewAdapter(Context context,List<String> data ) {
        inflater = LayoutInflater.from(context);
        //添加列表数据进构造方法
        this.data = data;
    }

    @Override
    public int getCount() {
//        if (data.size()>0&&data.size()<9){
//            return data.size();
//        }
       return data.size();
    }

    @Override
    public Object getItem(int position) {

        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (holder == null) {
            convertView = inflater.inflate(R.layout.gridview_adapter_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (data!=null){
            holder.hotSearchTv.setText(data.get(position));
        }else {
            holder.hotSearchTv.setText("");
        }

        return convertView;
    }



    public static class ViewHolder  {

        protected View mItemView;
        @BindView(R.id.hot_search_tv)
        TextView hotSearchTv;

        public ViewHolder(View mItemView) {
            ButterKnife.bind(this, mItemView);
            this.mItemView = mItemView;
        }
    }


}
