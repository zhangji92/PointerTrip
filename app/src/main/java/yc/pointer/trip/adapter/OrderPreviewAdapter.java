package yc.pointer.trip.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.bean.OrderPreviewMode;

import java.util.List;

/**
 * Create by 张继
 * 2017/8/10
 * 17:39
 */
public class OrderPreviewAdapter extends BaseAdapter {
    private static final int TYPE_TOP = 0x0001;
    private static final int TYPE_NORMAL = 0x0002;
    private static final int TYPE_BOTTOM = 0x0003;

    private List<OrderPreviewMode> mList;

    public OrderPreviewAdapter(List<OrderPreviewMode> mList) {
        this.mList = mList;
    }



    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_TOP;
        } else if (position == mList.size() - 1) {
            return TYPE_BOTTOM;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getCount() {
        return mList.size() == 0 ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            viewHolder=new ViewHolder();
            convertView=LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_order_preview,null);
            viewHolder.tv_top_line= (TextView) convertView.findViewById(R.id.tv_top_line);
            viewHolder.tv_bottom_line= (TextView) convertView.findViewById(R.id.tv_bottom_line);
            viewHolder.tv_title= (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.tv_scenic= (TextView) convertView.findViewById(R.id.tv_scenic);
            viewHolder.tv_pic= (TextView) convertView.findViewById(R.id.tv_pic);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_scenic.setTextColor(Color.parseColor("#000000"));
        viewHolder.tv_title.setTextColor(Color.parseColor("#000000"));
        if (getItemViewType(position) == TYPE_TOP) {
            // 最后一行头的竖线不显示
            viewHolder.tv_top_line.setVisibility(View.GONE);
            viewHolder.tv_bottom_line.setVisibility(View.VISIBLE);
        } else if (getItemViewType(position) == TYPE_BOTTOM) {
            // 最后一行头的竖线不显示
            viewHolder.tv_bottom_line.setVisibility(View.GONE);
            viewHolder.tv_scenic.setTextColor(Color.parseColor("#d60337"));
            viewHolder.tv_title.setTextColor(Color.parseColor("#d60337"));
        } else {
            viewHolder.tv_top_line.setVisibility(View.VISIBLE);
            viewHolder.tv_bottom_line.setVisibility(View.VISIBLE);
        }
        viewHolder.tv_title.setText(mList.get(position).getOrderTitle());
        viewHolder.tv_scenic.setText(mList.get(position).getOrderContent());
       if(mList.get(position).getOrderTitle().equals("导游证件")){
            viewHolder.tv_pic.setText(mList.get(position).getOrderPic());
        }else if(mList.get(position).getOrderTitle().equals("计价规则")){
            viewHolder.tv_pic.setText("￥"+mList.get(position).getOrderPic());
        }else if(mList.get(position).getOrderTitle().equals("订单总额")){
            viewHolder.tv_pic.setText("￥"+mList.get(position).getOrderPic());
        }
            return convertView;
    }

    class ViewHolder{
        TextView tv_top_line;
        TextView tv_bottom_line;
        TextView tv_title;
        TextView tv_scenic;
        TextView tv_pic;

    }
}
