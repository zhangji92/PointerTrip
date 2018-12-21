package yc.pointer.trip.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.activity.ScenicDetailsActivity;
import yc.pointer.trip.base.BaseViewHolder;
import yc.pointer.trip.bean.NewGotravelBean;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.untils.StringUtil;

/**
 * Created by moyan on 2018/6/8.
 */

public class NewGoTravelHotSenicAdapter extends RecyclerView.Adapter<NewGoTravelHotSenicAdapter.GoTravelViewHolder> {

    private Context mContext;


    private List<NewGotravelBean.DataBean> mHotScenicList = new ArrayList<>();//热门景点的列表

    public NewGoTravelHotSenicAdapter(List<NewGotravelBean.DataBean> mHotScenicList) {
        this.mHotScenicList = mHotScenicList;
    }

    @Override
    public GoTravelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        GoTravelViewHolder holder = null;
        View view = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.new_go_travel_hot_senics_item, parent, false);
            holder = new GoTravelViewHolder(view, mContext);
            view.setTag(holder);
        } else {
            holder = (GoTravelViewHolder) view.getTag();
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(GoTravelViewHolder holder, int position) {
        if (mHotScenicList.size() > 0) {
            holder.setHotScenicData(position);
        }
    }

    @Override
    public int getItemCount() {
        return mHotScenicList.size() == 0 ? 0 : mHotScenicList.size();
    }

    public class GoTravelViewHolder extends BaseViewHolder {

        @BindView(R.id.hot_scenic_pic)
        ImageView hotScenicPic;
        @BindView(R.id.hot_scenic_title)
        TextView hotScenicTitle;
        @BindView(R.id.hot_scenic_oreder_num)
        TextView hotScenicOrederNum;

        public GoTravelViewHolder(View itemView, Context context) {
            super(itemView, context);
            ButterKnife.bind(this, itemView);
        }

        private void setHotScenicData(int position) {
            String pic = mHotScenicList.get(position).getPic();
            String title = mHotScenicList.get(position).getTitle();
            String fa_num = mHotScenicList.get(position).getFa_num();
            final String sid = mHotScenicList.get(position).getSid();
            if (!StringUtil.isEmpty(pic)) {
                OkHttpUtils.displayGlideRound(mContext, hotScenicPic, pic, 20);
            }
            if (!StringUtil.isEmpty(title)) {
                hotScenicTitle.setText(title);
            }
            if (!StringUtil.isEmpty(fa_num)) {
                String format = String.format(mContext.getResources().getString(R.string.hot_scenic_oreder_num), fa_num);
                hotScenicOrederNum.setText(format);
            }
            hotScenicPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!StringUtil.isEmpty(sid)) {
                        Intent intent = new Intent(mContext, ScenicDetailsActivity.class);
                        intent.putExtra("sid", sid);
                        mContext.startActivity(intent);
                    }
                }
            });

        }
    }


}
