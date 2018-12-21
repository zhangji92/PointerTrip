package yc.pointer.trip.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.activity.NewPersonalHomePageActivity;
import yc.pointer.trip.base.BaseViewHolder;
import yc.pointer.trip.bean.FansBean;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.CustomCircleImage;

/**
 * Created by moyan on 2018/6/13.
 */

public class FansFragmentListAdapter extends RecyclerView.Adapter<FansFragmentListAdapter.FansMessageViewHolder> {


    private Context mContext;
    private List<FansBean.DataBean> mList = new ArrayList<>();


    private AddAttentionListener listener;


    public void setListener(AddAttentionListener listener) {
        this.listener = listener;
    }

    public FansFragmentListAdapter(Context mContext, List<FansBean.DataBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public FansMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mContext = parent.getContext();
        View view = null;
        FansMessageViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.fans_message_item, parent, false);
            holder = new FansMessageViewHolder(view, mContext);
            view.setTag(holder);
        } else {
            holder = (FansMessageViewHolder) view.getTag();
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final FansMessageViewHolder holder, final int position) {
        if (mList.size() > 0) {
            holder.setData(mContext, mList, position);
            holder.attenButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        listener.addAtten(holder.attenButton,position);
                    }
                }
            });
            holder.itemViewRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        listener.ToPersonPage(holder.attenButton,position);
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mList.size() == 0 ? 0 : mList.size();
    }


    public static class FansMessageViewHolder extends BaseViewHolder {
        @BindView(R.id.liner_fans_message)
        LinearLayout itemViewRoot;
        @BindView(R.id.fans_head)
        CustomCircleImage fansHead;//粉丝头像
        @BindView(R.id.verify_result)
        ImageView verifyResult;//VIP标识
        @BindView(R.id.fans_nickname)
        TextView fansNickname;//粉丝昵称
        @BindView(R.id.fans_date)
        TextView fansDate;//日期
        @BindView(R.id.atten_button)
        Button attenButton;//相互关注按钮

        public FansMessageViewHolder(View itemView, Context context) {
            super(itemView, context);
            ButterKnife.bind(this, itemView);
        }

        private void setData(final Context mContext, final List<FansBean.DataBean> mList, final int position) {
            String pic = mList.get(position).getPic();//头像地址
            String nickname = mList.get(position).getNickname();//昵称
            String addtime = mList.get(position).getAddtime();//时间戳
            int att_status = mList.get(position).getAtt_status();//是否相互关注  0：未相互关注  1：已相互关注
            String is_vip = mList.get(position).getIs_vip();
//            String is_jie = mList.get(position).getIs_jie();
//            if (!StringUtil.isEmpty(is_jie)){
//                if (is_jie.equals("2")){
//                    verifyResult.setVisibility(View.VISIBLE);
//                }else {
//                    verifyResult.setVisibility(View.GONE);
//                }
//            }else {
//                verifyResult.setVisibility(View.GONE);
//            }
            if (!StringUtil.isEmpty(pic)) {
                OkHttpUtils.displayGlideCircular(mContext, fansHead, pic,verifyResult,is_vip);
            }
            if (!StringUtil.isEmpty(nickname)) {
                fansNickname.setText(nickname);
            }

            if (!StringUtil.isEmpty(addtime)) {
                String strTimeDate = StringUtil.getStrTimeTomm(addtime);
                Date date = StringUtil.getStrTimeDate(strTimeDate);
                if (strTimeDate != null) {
                    String format = StringUtil.format(date);
                    fansDate.setText(format);
                }
            }
            if (att_status == 0) {
                attenButton.setText("关注");
                attenButton.setBackgroundResource(R.drawable.go_verify);
            } else if (att_status == 1) {
                attenButton.setText("互相关注");
                attenButton.setBackgroundResource(R.drawable.invitation_dialog_not);
            }
//            itemViewRoot.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//
//                    Intent intent = new Intent(mContext, NewPersonalHomePageActivity.class);
//                    intent.putExtra("uid", mList.get(position).getUid());
//                    mContext.startActivity(intent);
//                }
//            });

        }
    }

    public interface AddAttentionListener {
        void addAtten(Button attenButton,int position);
        void ToPersonPage(Button attenButton,int position);
    }
}
