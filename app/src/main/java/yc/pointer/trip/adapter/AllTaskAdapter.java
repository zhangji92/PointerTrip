package yc.pointer.trip.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.zxing.common.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.activity.LinkTaskActivity;
import yc.pointer.trip.base.BaseViewHolder;
import yc.pointer.trip.bean.AllTaskDestilsBean;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.CountDownView;

/**
 * Created by moyan on 2018/10/10.
 */

public class AllTaskAdapter extends RecyclerView.Adapter<BaseViewHolder> {


    private Context mContext;



    private String bannerPicUrl = "";//顶部banner图片
    private List<AllTaskDestilsBean.DataBean> mContentList = new ArrayList();

    private static int ALLTASKHEADLAYOUT = 0;
    private static int ALLTASKNORMALLAYOUT = 1;
    private static int ALLTASKLASTLAYOUT = 2;



    private OnClickTaskMoney listener;


    public void setListener(OnClickTaskMoney listener) {
        this.listener = listener;
    }

    public AllTaskAdapter( List<AllTaskDestilsBean.DataBean> mContentList) {
        this.mContentList = mContentList;
    }
    public void setBannerPicUrl(String bannerPicUrl) {
        this.bannerPicUrl = bannerPicUrl;
    }
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View itemView = null;
        AllTaskAdapterHeadViewHolder headHolder = null;
        AllTaskAdapterNormalViewHolder normalHolder = null;
        AllTaskAdapterLastViewHolder LastHolder = null;
        if (viewType == ALLTASKHEADLAYOUT) {
            //头部布局
            if (itemView == null) {
                itemView = LayoutInflater.from(mContext).inflate(R.layout.all_task_head_adapter, parent, false);
                headHolder = new AllTaskAdapterHeadViewHolder(itemView, mContext);
                itemView.setTag(headHolder);
            } else {
                headHolder = (AllTaskAdapterHeadViewHolder) itemView.getTag();
            }
            return headHolder;
        } else if (viewType == ALLTASKNORMALLAYOUT) {
            //列表布局
            if (itemView == null) {
                itemView = LayoutInflater.from(mContext).inflate(R.layout.all_task_adapter, parent, false);
                normalHolder = new AllTaskAdapterNormalViewHolder(itemView, mContext);
                itemView.setTag(normalHolder);
            } else {
                normalHolder = (AllTaskAdapterNormalViewHolder) itemView.getTag();
            }
            return normalHolder;
        } else {
            if (itemView == null) {
                itemView = LayoutInflater.from(mContext).inflate(R.layout.all_task_last_adapter, parent, false);
                LastHolder = new AllTaskAdapterLastViewHolder(itemView, mContext);
                itemView.setTag(LastHolder);
            } else {
                LastHolder = (AllTaskAdapterLastViewHolder) itemView.getTag();
            }
            return LastHolder;
        }


    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {

        switch (getItemViewType(position)) {
            case 0:
                if (!StringUtil.isEmpty(bannerPicUrl)){
                    OkHttpUtils.displayGlide(mContext,((AllTaskAdapterHeadViewHolder)holder).allTaskBannerImg,bannerPicUrl);
                }
                break;
            case 1:
                ((AllTaskAdapterNormalViewHolder)holder).setTaskInfo(mContentList,position-1,listener);
                break;
            case 2:
                int color = mContext.getResources().getColor(R.color.task_text_gray);
                ((AllTaskAdapterLastViewHolder)holder).btAllTask.setTextColor(color);
                ((AllTaskAdapterLastViewHolder)holder).btAllTask.setBackgroundResource(R.mipmap.bt_aii_tast_zong_gr);
                ((AllTaskAdapterLastViewHolder)holder).btAllTask.setEnabled(false);
                ((AllTaskAdapterLastViewHolder)holder).btAllTask.setClickable(false);
                for (int i = 0; i <mContentList.size() ; i++) {
                    String btn_status = mContentList.get(i).getBtn_status();
                    if (btn_status.equals("0")){
                        int buleColor = mContext.getResources().getColor(R.color.task_text_bule);
                        ((AllTaskAdapterLastViewHolder)holder).btAllTask.setTextColor(buleColor);
                        ((AllTaskAdapterLastViewHolder)holder).btAllTask.setBackgroundResource(R.mipmap.bt_aii_tast_zong_bl);
                        ((AllTaskAdapterLastViewHolder)holder).btAllTask.setEnabled(true);
                        ((AllTaskAdapterLastViewHolder)holder).btAllTask.setClickable(true);
                     break;
                    }
                }
                ((AllTaskAdapterLastViewHolder)holder).btAllTask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener!=null){
                            listener.onClickAllMoney(3);
                        }
                    }
                });


                break;


        }


    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            //头部
            return ALLTASKHEADLAYOUT;
        } else if (position == mContentList.size() + 1) {
            //底部全部选择
            return ALLTASKLASTLAYOUT;

        } else {
            //列表部分
            return ALLTASKNORMALLAYOUT;

        }
    }

    @Override
    public int getItemCount() {
        return mContentList.size() == 0 ? 0 : mContentList.size() + 2;

    }

    static class AllTaskAdapterHeadViewHolder extends BaseViewHolder {

        @BindView(R.id.all_task_banner_img)
        ImageView allTaskBannerImg;

        public AllTaskAdapterHeadViewHolder(View itemView, Context context) {
            super(itemView, context);
            ButterKnife.bind(this, itemView);
        }
    }

    static class AllTaskAdapterNormalViewHolder extends BaseViewHolder {

        @BindView(R.id.task_all_icon)
        ImageView taskAllIcon;
        @BindView(R.id.task_title)
        TextView taskTitle;
        @BindView(R.id.task_money)
        TextView taskMoney;
        @BindView(R.id.task_introduction)
        TextView taskIntroduction;
        @BindView(R.id.text_get_reward)
        TextView textGetReward;//领取奖励 / 已领取
        @BindView(R.id.text_time_title)
        TextView textTimeTitle;//距离开启还剩
        @BindView(R.id.text_time)
        CountDownView textTime;//倒计时
        @BindView(R.id.liner_get_money)
        LinearLayout linerGetMoney;//领取按钮背景

        public AllTaskAdapterNormalViewHolder(View itemView, Context context) {
            super(itemView, context);
            ButterKnife.bind(this, itemView);
        }

        private void setTaskInfo(List<AllTaskDestilsBean.DataBean> mContentList, int position, final OnClickTaskMoney listener){
            if (mContentList.size()>0){
                taskAllIcon.setImageResource(mContentList.get(position).getImg());
                taskTitle.setText(mContentList.get(position).getTaskTitle());
                taskIntroduction.setText(mContentList.get(position).getTaskIntroduction());
                String btn_status = mContentList.get(position).getBtn_status();
                String money = mContentList.get(position).getMoney();
                int time = mContentList.get(position).getTime();
                final int taskType = mContentList.get(position).getTaskType();
                if (!StringUtil.isEmpty(money)){
                    taskMoney.setText("+ "+money+"元");
                }else {
                    taskMoney.setText(" ");
                }

                if (!StringUtil.isEmpty(btn_status)){
                    textTimeTitle.setVisibility(View.GONE);
                    textTime.setVisibility(View.GONE);
                    textGetReward.setVisibility(View.VISIBLE);
                    if (btn_status.equals("0")){
                        //待领取
                        linerGetMoney.setEnabled(true);
                        linerGetMoney.setClickable(true);
                        linerGetMoney.setBackgroundResource(R.mipmap.bt_tast_zong_bl);
                        textGetReward.setText("领取奖励");
                        linerGetMoney.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (listener!=null){
                                    linerGetMoney.setEnabled(false);
                                    linerGetMoney.setClickable(false);
                                    listener.onClickLinkMoney(taskType);
                                }
                            }
                        });


                    }else if (btn_status.equals("1")){
                        //已领取
                        linerGetMoney.setEnabled(false);
                        linerGetMoney.setClickable(false);
                        linerGetMoney.setBackgroundResource(R.mipmap.bt_tast_zong_gr);
                        textGetReward.setText("已领取");
                    }else {

                    }
                }else {
                    //倒计时
                    linerGetMoney.setEnabled(false);
                    linerGetMoney.setClickable(false);
                    linerGetMoney.setBackgroundResource(R.mipmap.bt_tast_zong_gr);
                    textTimeTitle.setVisibility(View.VISIBLE);
                    textTime.setVisibility(View.VISIBLE);
                    textGetReward.setVisibility(View.GONE);
                    if (time!=0){
                        textTime.initTime(time);
                        textTime.reStart();
                        textTime.setOnTimeCompleteListener(new CountDownView.OnTimeCompleteListener() {
                            @Override
                            public void onTimeComplete() {
                                textTimeTitle.setVisibility(View.GONE);
                                textTime.setVisibility(View.GONE);
                                textGetReward.setVisibility(View.VISIBLE);
                                linerGetMoney.setEnabled(true);
                                linerGetMoney.setClickable(true);
                                linerGetMoney.setBackgroundResource(R.mipmap.bt_tast_zong_bl);
                                textGetReward.setText("立即开启");
                                linerGetMoney.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (listener!=null){
                                            listener.onTimeCompleteClick();
                                        }
                                    }
                                });

                            }
                        });
                    }
                }

            }


        }


    }

    static class AllTaskAdapterLastViewHolder extends BaseViewHolder {

        @BindView(R.id.bt_all_task)
        Button btAllTask;

        public AllTaskAdapterLastViewHolder(View itemView, Context context) {
            super(itemView, context);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnClickTaskMoney{
        void onClickAllMoney(int taskType);
        void onClickLinkMoney(int taskType);
        void onTimeCompleteClick();
    }

}
