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
import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.activity.VideoDetailsActivity;
import yc.pointer.trip.bean.BookBean;
import yc.pointer.trip.bean.ReadBookBean.DataBean.*;
import yc.pointer.trip.network.OkHttpUtils;

import java.util.List;

/**
 * Created by Lenovo
 * 2017/6/30
 * 10:29
 * 看游记页面水平滑动列表适配器
 */
public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.ViewHolder> {

    private List<DataRBean> mList;
    private Context mContext;

    public HorizontalAdapter(List<DataRBean> mList) {
        this.mList = mList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext=parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_horizontal, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bingHolder(position);
    }

    @Override
    public int getItemCount() {
        return mList.size() == 0 ? 0 : mList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.horizontal_card)
        CardView cardView;
        @BindView(R.id.horizontal_title)
        TextView text_title;

        @BindView(R.id.horizontal_nick)
        TextView text_nick;
        @BindView(R.id.horizontal_author)
        TextView text_author;
        @BindView(R.id.horizontal_number)
        TextView text_number;
        @BindView(R.id.horizontal_img)
        ImageView img_pic;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bingHolder(final int position) {

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    BookBean dataRBean = mList.get(position);
                    Intent intent=new Intent(mContext, VideoDetailsActivity.class);
                    intent.putExtra("dataGoodBean",dataRBean);
//                    String uid=dataRBean.getBid();
//                    Intent intent=new Intent(mContext, BookDetailsActivity.class);
//                    intent.putExtra("bid",uid);
                    mContext.startActivity(intent);
                }
            });

            text_title.setText(mList.get(position).getTitle());//主题
            text_nick.setText(mList.get(position).getNickname());//昵称
            text_number.setText(mList.get(position).getOrd_num());//预约
            text_author.setText(mList.get(position).getCp());//作者
            OkHttpUtils.displayImg(img_pic, mList.get(position).getY_pic());
        }
    }
}
