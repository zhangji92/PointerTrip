package yc.pointer.trip.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.bean.CouponBean;
import yc.pointer.trip.network.OkHttpUtils;

import java.util.List;

/**
 * Created by 刘佳伟
 * 2017/7/21
 * 11:17
 * 优惠券适配器
 */
public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.ViewHolder> {


    private List<CouponBean.DataBean> mListBean;
    private CallBackCoupon mCallBackCoupon;
    public CouponAdapter(List<CouponBean.DataBean> mListBean,CallBackCoupon callBackCoupon) {
        this.mListBean = mListBean;
        this.mCallBackCoupon=callBackCoupon;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_coupon, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mListBean.size()>0){
            holder.bindHolder(mListBean.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mListBean.size() == 0 ? 0 : mListBean.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id._adapter_coupon_back)
        ImageView mImg;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bindHolder(final CouponBean.DataBean bean) {
            OkHttpUtils.displayImg(mImg,bean.getPic());
            mImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallBackCoupon!=null){
                        mCallBackCoupon.onClick(getLayoutPosition());
                    }
                }
            });
        }
    }

    public interface CallBackCoupon{
        void onClick(int position);
    }
}
