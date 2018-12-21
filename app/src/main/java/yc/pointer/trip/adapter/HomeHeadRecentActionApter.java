package yc.pointer.trip.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.activity.LoginActivity;
import yc.pointer.trip.activity.RecentTopicActivity;
import yc.pointer.trip.activity.ThemeActionActivity;
import yc.pointer.trip.bean.HomeVideoDataBean;
import yc.pointer.trip.untils.SharedPreferencesUtils;
import yc.pointer.trip.untils.StringUtil;

/**
 * Created by moyan on 2017/11/22.
 * 近期主题
 */

class HomeHeadRecentActionApter extends RecyclerView.Adapter<HomeHeadRecentActionApter.ViewHolder> {

    private List<HomeVideoDataBean.DataBean.DataAdIndexBean> mListAction = new ArrayList<>();//精彩活动
    private Context mContext;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_video_head_recent_action_adapter, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    public HomeHeadRecentActionApter(List<HomeVideoDataBean.DataBean.DataAdIndexBean> mListAction) {
        this.mListAction = mListAction;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mListAction.size()>0){
            holder.bingRecentHolder(position);
        }
    }

    @Override
    public int getItemCount() {
        return mListAction.size() <3 ? mListAction.size() : 3;
//        return mListAction.size() == 0 ? 0 : mListAction.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.action_name)
        TextView actionName;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bingRecentHolder(final int position) {
            final String title = mListAction.get(position).getTitle();
            final String aid = mListAction.get(position).getAid();

            if(!StringUtil.isEmpty(title)){
                actionName.setText(title);

                actionName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO 跳转活动详情
                        String mUserId = SharedPreferencesUtils.getInstance().getString(mContext, "useId");
                        if (!StringUtil.isEmpty(mUserId)&&!mUserId.equals("not find")){
                            Intent intent = new Intent(mContext, ThemeActionActivity.class);
                            if (!StringUtil.isEmpty(aid)){
                                intent.putExtra("aid",aid);
                                intent.putExtra("title",title);
                            }
                            mContext.startActivity(intent);
                        }else {
                            Intent intent = new Intent(mContext, LoginActivity.class);
                            if (!StringUtil.isEmpty(aid)){
                                intent.putExtra("aid",aid);
                                intent.putExtra("title",title);
                            }
                            intent.putExtra("logFlag","actionHome");
                            mContext.startActivity(intent);
                        }
                    }
                });

            }



        }
    }
}
