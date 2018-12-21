package yc.pointer.trip.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import yc.pointer.trip.R;
import yc.pointer.trip.bean.BaseCityBean;
import yc.pointer.trip.untils.StringUtil;

import java.util.List;

/**
 * Created by Administrator on 2017/3/10.
 * 热门城市
 */

public class HotCityAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<BaseCityBean> hotCitys;
    ICallBackHot mICallBackHot;

    public void setICallBackHot(ICallBackHot mICallBackHot) {
        this.mICallBackHot = mICallBackHot;
    }

    public HotCityAdapter(Context context, List<BaseCityBean> hotCitys) {
        this.context = context;
        inflater = LayoutInflater.from(this.context);
        this.hotCitys = hotCitys;
    }

    @Override
    public int getCount() {
        return hotCitys.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if (viewHolder==null){
            viewHolder=new ViewHolder();
            convertView = inflater.inflate(R.layout.gridview_adapter_item, null);
            viewHolder.city=(TextView) convertView.findViewById(R.id.hot_search_tv);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }

        String str=hotCitys.get(position).getCityname();
        if (!StringUtil.isEmpty(str)) {
            viewHolder.city.setText(str);
        }else{
            viewHolder.city.setText(str+"");
        }
        viewHolder.city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mICallBackHot.OnClickHot(position);
            }
        });
        return convertView;
    }
    public class ViewHolder{
        private TextView city;
    }

    public interface ICallBackHot{
        void OnClickHot(int position);
    }
}
