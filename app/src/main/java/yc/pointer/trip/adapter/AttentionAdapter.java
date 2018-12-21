package yc.pointer.trip.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import yc.pointer.trip.R;
import yc.pointer.trip.base.BaseViewHolder;
import yc.pointer.trip.bean.FansBean;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.CustomCircleImage;

/**
 * Created by 张继
 * 2018/1/12
 * 15:08
 * 关注的adapter
 */

public class AttentionAdapter extends RecyclerView.Adapter<BaseViewHolder> {


    private List<FansBean.DataBean> mList;
    private AttentionAdapterCallBack mAttentionAdapterCallBack;

    public AttentionAdapter(List<FansBean.DataBean> mList, AttentionAdapterCallBack mAttentionAdapterCallBack) {
        this.mList = mList;
        this.mAttentionAdapterCallBack = mAttentionAdapterCallBack;
    }


    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.adapter_fans, null);
        return new FansViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (mList.size() > 0) {
            ((FansViewHolder) holder).bindHolder(mList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class FansViewHolder extends BaseViewHolder {
        private Context mContext;
        @BindView(R.id.fans_head)
        CustomCircleImage ivHead;
        @BindView(R.id.verify_result)
        ImageView verifyResult;//VIP标识
        @BindView(R.id.fans_nick)
        TextView tvNick;
        @BindView(R.id.fans_local)
        TextView tvLocal;
        @BindView(R.id.fans_relative)
        RelativeLayout rlClick;

        FansViewHolder(View itemView, Context context) {
            super(itemView, context);
            this.mContext = context;
            ButterKnife.bind(this, itemView);
        }

        void bindHolder(final FansBean.DataBean dataBean) {
            String is_vip = dataBean.getIs_vip();
            String pic = dataBean.getPic();
            OkHttpUtils.displayGlideCircular(mContext, ivHead, pic,verifyResult,is_vip);
//            String is_jie = dataBean.getIs_jie();
//            if (!StringUtil.isEmpty(is_jie)){
//                if (is_jie.equals("2")){
//                    verifyResult.setVisibility(View.VISIBLE);
//                }else {
//                    verifyResult.setVisibility(View.GONE);
//                }
//            }else {
//                verifyResult.setVisibility(View.GONE);
//            }

            tvNick.setText(dataBean.getNickname());
            String location = dataBean.getLocation();
            if (!StringUtil.isEmpty(location)) {
                tvLocal.setVisibility(View.VISIBLE);
                tvLocal.setText(location);
            } else {
                tvLocal.setVisibility(View.GONE);
            }
            rlClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mAttentionAdapterCallBack != null) {
                        mAttentionAdapterCallBack.attention(dataBean.getUid());
                    }
                }
            });
        }
    }

    public interface AttentionAdapterCallBack {
        void attention(String uid);
    }
}
