package yc.pointer.trip.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.base.BaseViewHolder;
import yc.pointer.trip.bean.ZanBean;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.view.CustomCircleImage;

/**
 * Created by moyan on 2018/8/10.
 */

public class LikeHomeAdapter extends RecyclerView.Adapter<LikeHomeAdapter.HomeLikeViewHolder> {

    private Context mContext;
    private List<ZanBean> zanList=new ArrayList<>();
    public LikeHomeAdapter(List<ZanBean> zan) {
        this.zanList=zan;
    }

    @Override
    public HomeLikeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View itemView = null;
        HomeLikeViewHolder holder = null;
        if (itemView == null) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.home_like_item, parent, false);
            holder = new HomeLikeViewHolder(itemView, mContext);
            itemView.setTag(holder);
        } else {
            holder = (HomeLikeViewHolder) itemView.getTag();
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(HomeLikeViewHolder holder, int position) {



//            if (position == 0) {
////                holder.zanA.setVisibility(View.VISIBLE);
////                holder.likeHeadImg.setVisibility(View.GONE);
//            } else
                if (position == 4) {
//                holder.zanA.setVisibility(View.GONE);
                holder.likeHeadImg.setImageResource(R.mipmap.icon_more_zan);
            }else {
//                holder.zanA.setVisibility(View.GONE);
                String z_u_pic = zanList.get(position).getZ_u_pic();
                OkHttpUtils.displayImg(holder.likeHeadImg, z_u_pic);
            }



    }

    @Override
    public int getItemCount() {
        int size = zanList.size();
        int i = size == 0 ? 0 : size >= 4 ? 5 : size;
        return i;
    }

    static class HomeLikeViewHolder extends BaseViewHolder{
        @BindView(R.id.like_head)
        CustomCircleImage likeHeadImg;//点赞人头像
//        @BindView(R.id.zan_a)
//        ImageView zanA;//点赞人显示标识
        public HomeLikeViewHolder(View itemView, Context context) {
            super(itemView, context);
            ButterKnife.bind(this,itemView);
        }
    }
}
