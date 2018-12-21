package yc.pointer.trip.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.bean.ComplainTagBean;

import java.util.List;

/**
 * Created by moyan on 2017/9/8.
 *
 */
public class ComplainAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<ComplainTagBean.DataBean> data;

    public ComplainAdapter(Context context, List<ComplainTagBean.DataBean> data ) {
        inflater = LayoutInflater.from(context);
        //添加列表数据进构造方法
        this.data = data;
    }
    @Override
    public int getCount() {
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
            convertView = inflater.inflate(R.layout.complain_adapter_gridview, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (data!=null){
            holder.tag.setText(data.get(position).getTitle());
        }else {
            holder.tag.setText("");
        }

        return convertView;
    }
    public static class ViewHolder  {

        protected View mItemView;
        @BindView(R.id.complain_choose_tag)
        TextView tag;

        public ViewHolder(View mItemView) {
            ButterKnife.bind(this, mItemView);
            this.mItemView = mItemView;
        }
    }
}
