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
 * 历史城市
 */

public class HitCityAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<BaseCityBean> hitCitys;

    ICallBackHit mICallBack;

    public void setICallBack(ICallBackHit mICallBack) {
        this.mICallBack = mICallBack;
    }

    public HitCityAdapter(Context context, List<BaseCityBean> hitCitys) {
        this.context = context;
        inflater = LayoutInflater.from(this.context);
        this.hitCitys = hitCitys;
    }

    @Override
    public int getCount() {
        if (hitCitys.size()>6){
            return 6;
        }else {
            return hitCitys.size()==0?0:hitCitys.size();
        }
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
        convertView = inflater.inflate(R.layout.gridview_adapter_item, null);
        final TextView city = (TextView) convertView.findViewById(R.id.hot_search_tv);

        String strCity=hitCitys.get(position).getCityname();

         if (strCity.contains("特别行政区")) {
            String[] newCityName = strCity.split("特");
            String allName = newCityName[0];
            city.setText(allName);
        } else if (strCity.contains("地区")) {
            String[] newCityName = strCity.split("地");
            String allName = newCityName[0];
            city.setText(allName);
        } else {
            city.setText(strCity);
        }
        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mICallBack.OnClickHot(position);
            }
        });
        return convertView;
    }
    public interface ICallBackHit {
        void OnClickHot(int position);
    }
}
