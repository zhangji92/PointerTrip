package yc.pointer.trip.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.activity.VideoDetailsActivity;
import yc.pointer.trip.base.BaseViewHolder;
import yc.pointer.trip.bean.SystemMessageBean;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.untils.StringUtil;

/**
 * Created by moyan on 2018/6/12.
 * 系统消息适配器
 */

public class SystemFragmentListAdapter extends RecyclerView.Adapter<SystemFragmentListAdapter.SystemMessageViewHolder> {

    private Context mContext;
    private List<SystemMessageBean.DataBean> mData = new ArrayList<>();

    public SystemFragmentListAdapter(List<SystemMessageBean.DataBean> mData) {
        this.mData = mData;
    }

    @Override
    public SystemMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = null;
        SystemMessageViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.system_message_item, parent, false);
            holder = new SystemMessageViewHolder(view, mContext);
            view.setTag(holder);
        } else {
            holder = (SystemMessageViewHolder) view.getTag();
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(SystemMessageViewHolder holder, int position) {
        if (mData.size()>0){
            holder.setMessageDataInfo(mContext,mData,position);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size() == 0 ? 0 : mData.size();
    }

    public static class SystemMessageViewHolder extends BaseViewHolder {
        @BindView(R.id.message_info)
        TextView messageInfo;
        @BindView(R.id.msg_adapter_img)
        ImageView msgAdapterImg;
        @BindView(R.id.book_title)
        TextView bookTitle;
        @BindView(R.id.book_message)
        LinearLayout bookMessage;
        @BindView(R.id.message_date)
        TextView messageDate;

        public SystemMessageViewHolder(View itemView, Context context) {
            super(itemView, context);
            ButterKnife.bind(this, itemView);
        }

        private void setMessageDataInfo(final Context context, final List<SystemMessageBean.DataBean> mData, int position) {

            String n_info = mData.get(position).getN_info();
            String addtime = mData.get(position).getAddtime();
            String title = mData.get(position).getTitle();
            String b_pic = mData.get(position).getB_pic();

            final SystemMessageBean.DataBean dataBean = mData.get(position);

            if (!StringUtil.isEmpty(n_info)) {
                messageInfo.setText(n_info);
            }
            if (!StringUtil.isEmpty(addtime)) {
                messageDate.setText(addtime);
            }
            if (!StringUtil.isEmpty(title)) {
                bookMessage.setVisibility(View.VISIBLE);
                bookTitle.setText(title);
                OkHttpUtils.displayGlide(context,msgAdapterImg,b_pic);
            }else {
                bookMessage.setVisibility(View.GONE);
            }

            bookMessage.setOnClickListener(new View.OnClickListener() {//跳转游记详情
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, VideoDetailsActivity.class);
                    intent.putExtra("dataGoodBean", dataBean);
                    context.startActivity(intent);

                }
            });

        }

    }

}
