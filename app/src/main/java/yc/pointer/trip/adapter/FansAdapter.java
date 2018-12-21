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
 */

public class FansAdapter extends RecyclerView.Adapter<BaseViewHolder> {


    private List<FansBean.DataBean> mList;
    private FansCallBack mFansCallBack;

    public FansAdapter(List<FansBean.DataBean> mList, FansCallBack mFansCallBack) {
        this.mList = mList;
        this.mFansCallBack = mFansCallBack;
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



    class FansViewHolder extends BaseViewHolder {
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

        public FansViewHolder(View itemView, Context context) {
            super(itemView, context);
            this.mContext = context;
            ButterKnife.bind(this, itemView);
        }

        public void bindHolder(final FansBean.DataBean dataBean) {
            String is_vip = dataBean.getIs_vip();
            String pic = dataBean.getPic();
            OkHttpUtils.displayGlideCircular(mContext, ivHead, pic,verifyResult,is_vip);
            tvNick.setText(dataBean.getNickname());

            String location = dataBean.getLocation();
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
            if (!StringUtil.isEmpty(location)){
                tvLocal.setVisibility(View.VISIBLE);
                tvLocal.setText(location);
            }else {
                tvLocal.setVisibility(View.GONE);
            }

            rlClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mFansCallBack!=null){
                        mFansCallBack.fans(dataBean.getUid());
                    }

                }
            });
        }
    }


    public interface FansCallBack{
        void fans(String uid);
    }
}
