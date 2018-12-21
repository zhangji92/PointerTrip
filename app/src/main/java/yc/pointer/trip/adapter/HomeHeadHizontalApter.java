package yc.pointer.trip.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.umeng.socialize.utils.Log;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.activity.VideoDetailsActivity;
import yc.pointer.trip.bean.BookBean;
import yc.pointer.trip.bean.HomeVideoDataBean;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.GlideCircleTransform;
import yc.pointer.trip.untils.GlideRoundTransform;
import yc.pointer.trip.view.MarqueeTextView;

import static com.umeng.socialize.utils.DeviceConfig.context;

/**
 * Created by moyan on 2017/11/21.
 * 首页头布局同城横向列表适配器
 */

class HomeHeadHizontalApter extends RecyclerView.Adapter<HomeHeadHizontalApter.ViewHolder> {




    private List<HomeVideoDataBean.DataBean.DataCityBean> mListSameCity = new ArrayList();//存取同城数据

    private Context mContext;

    public HomeHeadHizontalApter(List<HomeVideoDataBean.DataBean.DataCityBean> mListSameCity) {
        this.mListSameCity = mListSameCity;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_video_head_adapter_horizontal_, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mListSameCity.size() > 0) {
           holder.bingHolder(position);
        }
    }

    @Override
    public int getItemCount() {
        return mListSameCity.size() == 0 ? 0 : mListSameCity.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.horizontal_img)
        ImageView horizontalImg;
        @BindView(R.id.horizontal_title)
        MarqueeTextView horizontalTitle;
        @BindView(R.id.horizontal_nick)
        TextView horizontalNick;
        @BindView(R.id.horizontal_card)
        CardView horizontalCard;//最外围布局

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bingHolder(int position) {
//            OkHttpUtils.displayImg(horizontalImg, mListSameCity.get(position).getB_pic());
//            Glide.with(mContext)
//                    .load(URLUtils.BASE_URL + mListSameCity.get(position).getB_pic())
//                    .transform(new CenterCrop(mContext), new GlideCircleTransform(mContext))
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .crossFade()
//                    .into(horizontalImg);
            Log.e("aa",URLUtils.BASE_URL + mListSameCity.get(position).getB_pic());
            //Glide.with(mContext)
            //        .load(URLUtils.BASE_URL + mListSameCity.get(position).getB_pic())
            //        .placeholder(R.mipmap.gray_picture)
            //        .error(R.mipmap.gray_picture)
            //        .transform(new CenterCrop(mContext),new GlideRoundTransform(mContext,5))
            //        .into(horizontalImg);
            OkHttpUtils.displayGlide(mContext,horizontalImg,mListSameCity.get(position).getB_pic());
            horizontalTitle.setText(mListSameCity.get(position).getTitle());
            horizontalNick.setText(mListSameCity.get(position).getInfo());

            final BookBean dataCityBean = mListSameCity.get(position);

            horizontalCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO 跳转播放页面
                    Intent intent=new Intent(mContext, VideoDetailsActivity.class);
                    intent.putExtra("dataGoodBean",dataCityBean);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
