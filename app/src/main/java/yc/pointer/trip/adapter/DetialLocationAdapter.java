package yc.pointer.trip.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.amap.api.fence.PoiItem;

import java.util.List;

import yc.pointer.trip.R;
import yc.pointer.trip.bean.LocationInfo;

/**
 * Created by moyan on 2017/11/17.
 */

public class DetialLocationAdapter extends BaseAdapter {

    private List<LocationInfo> adressList;//详细地址简称
    private Context context;

    public DetialLocationAdapter(List<LocationInfo> locationTitle, Context context) {
        this.adressList = locationTitle;
        this.context = context;
    }

    @Override
    public int getCount() {
        return adressList.size() == 0 ? 0 : adressList.size();
    }

    @Override
    public LocationInfo getItem(int position) {
        return adressList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.detial_location_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (adressList.size()>0){
            holder.textViewAdress.setText(adressList.get(position).getLocationDetial());
            holder.textViewTitle.setText(adressList.get(position).getLocationTitle());
        }
        return convertView;
    }

    private class ViewHolder {
        private TextView textViewTitle;
        private TextView textViewAdress;

        public ViewHolder(View itemView) {
            textViewTitle = (TextView) itemView.findViewById(R.id.location_title);
            textViewAdress = (TextView) itemView.findViewById(R.id.location_detial);
        }
    }

}
