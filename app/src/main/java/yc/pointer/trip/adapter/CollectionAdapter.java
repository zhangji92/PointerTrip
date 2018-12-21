package yc.pointer.trip.adapter;

import android.content.Context;
import android.content.Intent;
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
import yc.pointer.trip.activity.VideoDetailsActivity;
import yc.pointer.trip.bean.CollectionBean;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.CustomCircleImage;
import yc.pointer.trip.view.DialogKnow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 张继
 * 2017/7/3
 * 14:13
 */
public class CollectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context mContext;
    private List<CollectionBean.DataBean> dataList = new ArrayList<>();

    CollectionBean.DataBean dataGoodBean;

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    private OnLongClickListener onLongClickListener;


    public CollectionAdapter(Context mContext, List<CollectionBean.DataBean> dataList) {
        this.mContext = mContext;
        this.dataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        ViewHolder viewHolder = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_collection_item, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (dataList.size() > 0) {
            String pic = dataList.get(position).getPic();
            String is_vip = dataList.get(position).getIs_vip();
            if (!StringUtil.isEmpty(pic)) {
//                OkHttpUtils.displayImg(((ViewHolder) holder).myHead, pic);
                OkHttpUtils.displayGlideCircular(mContext,((ViewHolder) holder).myHead,pic,((ViewHolder)holder).verifyResult,is_vip);
            }
//            String is_jie = dataList.get(position).getIs_jie();//判定路书作者是否为VIP
//            if(!StringUtil.isEmpty(is_jie)){
//                if (is_jie.equals("2")){
//                    ((ViewHolder)holder).verifyResult.setVisibility(View.VISIBLE);
//                }else {
//                    ((ViewHolder)holder).verifyResult.setVisibility(View.GONE);
//                }
//            }else {
//                ((ViewHolder)holder).verifyResult.setVisibility(View.GONE);
//            }
            if (!StringUtil.isEmpty(dataList.get(position).getNickname())) {
                ((ViewHolder) holder).myNick.setText(dataList.get(position).getNickname());
            }
            if (!StringUtil.isEmpty(dataList.get(position).getCp())) {
                ((ViewHolder) holder).bookAuthor.setText(dataList.get(position).getCp());
            }
            if (!StringUtil.isEmpty(dataList.get(position).getTitle())) {
                ((ViewHolder) holder).bookTitle.setText(dataList.get(position).getTitle());
            } else {
                ((ViewHolder) holder).bookTitle.setText("");
            }
            if (!StringUtil.isEmpty(dataList.get(position).getCity())) {
                ((ViewHolder) holder).bookPlace.setText(dataList.get(position).getCity());
            } else {
                ((ViewHolder) holder).bookPlace.setText("");
            }
            String addtime = dataList.get(position).getAddtime1();
            if (!StringUtil.isEmpty(addtime)) {
                String dateTime = StringUtil.getStrTime(addtime);
                ((ViewHolder) holder).collectTime.setText("收藏于 " + dateTime);
            }

            if (!StringUtil.isEmpty(dataList.get(position).getB_pic())) {
                OkHttpUtils.displayImg(((ViewHolder) holder).bookImg, dataList.get(position).getB_pic());
            }
//            if (!StringUtil.isEmpty(dataList.get(position).getType())) {
//                String bookType = dataList.get(position).getType();
//                if (bookType.equals("人文")) {
//                    ((ViewHolder)holder).bookLab.setImageResource(R.mipmap.collection_humanity);
//                }
//                if (bookType.equals("美食")) {
//                    ((ViewHolder)holder).bookLab.setImageResource(R.mipmap.coolection_food);
//                }
//                if (bookType.equals("深度")) {
//                    ((ViewHolder)holder).bookLab.setImageResource(R.mipmap.collection_depth);
//                }
//                if (bookType.equals("骑行")) {
//                    ((ViewHolder)holder).bookLab.setImageResource(R.mipmap.collection_bike);
//                }
//                if (bookType.equals("购物")) {
//                    ((ViewHolder)holder).bookLab.setImageResource(R.mipmap.collection_shoping);
//                }
//                if (bookType.equals("自驾")) {
//                    ((ViewHolder)holder).bookLab.setImageResource(R.mipmap.collection_drive);
//                }
//            }

            ((ViewHolder) holder).collectionItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataGoodBean = dataList.get(position);
                    if (dataGoodBean.getIs_del().equals("1")) {
                        new DialogKnow(mContext, R.style.user_default_dialog, new DialogKnow.CallBackListener() {
                            @Override
                            public void onClickListener() {

                            }
                        }).setMsg("该游记已被作者删除").setPositiveButton("我知道了").show();
                    } else {
                        Intent intent = new Intent(mContext, VideoDetailsActivity.class);
                        intent.putExtra("dataGoodBean", dataGoodBean);
                        mContext.startActivity(intent);

//                        Intent intent = new Intent(mContext, BookDetailsActivity.class);
//                        String bid = dataList.get(position).getBid();
//                        intent.putExtra("bid", bid);
//                        mContext.startActivity(intent);
                    }

                }
            });
            ((ViewHolder) holder).collectionItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onLongClickListener.itemOnLongClick(position);
                    return true;
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return dataList.size() == 0 ? 0 : dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.adapter_my_head)
        CustomCircleImage myHead;//头像
        @BindView(R.id.verify_result)
        ImageView verifyResult;//VIP认证标识
        @BindView(R.id.adapter_my_nick)
        TextView myNick;//昵称
        @BindView(R.id.adapter_my_author)
        TextView bookAuthor;//出品作者
        @BindView(R.id.adapter_my_img)
        ImageView bookImg;//路书图片
        @BindView(R.id.adapter_my_title)
        TextView bookTitle;//路书标题
        @BindView(R.id.adapter_my_lab)
        ImageView bookLab;//路书标签
        @BindView(R.id.adapter_my_place)
        TextView bookPlace;//路书城市
        @BindView(R.id.adapter_my_time)
        TextView collectTime;//收藏时间
        @BindView(R.id.collection_item)
        RelativeLayout collectionItem;//点击进详情页

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
//            AutoUtils.auto(itemView);
        }
    }

    public interface OnLongClickListener {
        void itemOnLongClick(int position);
    }
}
