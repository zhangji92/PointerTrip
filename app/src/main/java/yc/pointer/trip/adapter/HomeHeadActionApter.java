package yc.pointer.trip.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import yc.pointer.trip.R;
import yc.pointer.trip.activity.LoginActivity;
import yc.pointer.trip.activity.RecentTopicActivity;
import yc.pointer.trip.activity.ThemeActionActivity;
import yc.pointer.trip.bean.HomeVideoDataBean;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.untils.SharedPreferencesUtils;
import yc.pointer.trip.untils.StringUtil;

/**
 * Created by moyan on 2017/11/21.
 * 首页指针疯狂旅游年数据适配器
 */

class HomeHeadActionApter extends RecyclerView.Adapter<HomeHeadActionApter.ViewHolder> {


    private List<HomeVideoDataBean.DataBean.DataAdIndexBean> mListAction = new ArrayList<>();//精彩活动

    private Context mContext;



    public HomeHeadActionApter(List<HomeVideoDataBean.DataBean.DataAdIndexBean> mListAction) {
        this.mListAction = mListAction;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_video_head_adapter_action, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mListAction.size()>0){
            holder.bingHolder(position);
        }

    }

    @Override
    public int getItemCount() {
        return mListAction.size() == 0 ? 0 : mListAction.size();

    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.action_pic)
        ImageView actionPic;
        @BindView(R.id.action_name)
        TextView actionName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bingHolder(int position) {
            final String title = mListAction.get(position).getTitle();
            actionName.setText(title);
            OkHttpUtils.displayImg(actionPic,mListAction.get(position).getPic());
            final String aid = mListAction.get(position).getAid();
            actionPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //TODO 跳转主题介绍页面
                    Intent intent = new Intent(mContext,  ThemeActionActivity.class);
                    if (!StringUtil.isEmpty(aid)){
                        intent.putExtra("aid",aid);
                    }
                    intent.putExtra("title",title);
                    mContext.startActivity(intent);

                }
            });
        }
    }
}
