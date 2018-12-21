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


import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/10.
 * 搜索结果的适配器
 */

public class ResultListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<BaseCityBean> results = new ArrayList<BaseCityBean>();

    public ResultListAdapter(Context context, ArrayList<BaseCityBean> results) {
        inflater = LayoutInflater.from(context);
        this.results = results;
    }

    ICallBackResult mICallBackResult;

    public void setICallBackResult(ICallBackResult mICallBackResult) {
        this.mICallBackResult = mICallBackResult;
    }

    @Override
    public int getCount() {
        return results.size();
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
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView
                    .findViewById(R.id.name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String name=results.get(position).getCityname();

        if (!StringUtil.isEmpty(name)){
            viewHolder.name.setText(name);
        }

        viewHolder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mICallBackResult.OnClickResult(position);
            }
        });

        return convertView;
    }

    class ViewHolder {
        TextView name;
    }
    public interface ICallBackResult{
        void OnClickResult(int position);
    }
}
