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
import android.widget.Toast;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.activity.ScenicDetailsActivity;
import yc.pointer.trip.base.BaseViewHolder;
import yc.pointer.trip.bean.ScenicSearchBean;
import yc.pointer.trip.network.OkHttpUtils;

/**
 * Created by 张继
 * 2017/8/2 0002
 * 下午 6:46
 */

public class ScenicSearchAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<ScenicSearchBean.DataBean> mListSearch;
    private LayoutInflater mLayoutInflater;
    private searchCallBack mSearchCallBack;
    private boolean searchErrorResult=false;//true搜索结果 flase为您推荐
    private Context mContext;
    public void setSearchErrorResult(boolean searchErrorResult) {
        this.searchErrorResult = searchErrorResult;
    }

    public void setSearchCallBack(searchCallBack mSearchCallBack) {
        this.mSearchCallBack = mSearchCallBack;
    }

    public ScenicSearchAdapter(Context context, List<ScenicSearchBean.DataBean> mListSearch) {
        this.mContext=context;
        this.mListSearch = mListSearch;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.adapter_scenic, parent, false), parent.getContext());
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, final int position) {
        if (mListSearch.size() > 0 && mListSearch != null) {
            ((ViewHolder) holder).bindHolder(mListSearch.get(position));
            if (position==0){
                ((ViewHolder) holder).scenicSubtitle.setVisibility(View.VISIBLE);
            }else {
                ((ViewHolder) holder).scenicSubtitle.setVisibility(View.GONE);
            }
            ((ViewHolder) holder).mSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSearchCallBack.onClickSearch(position,((ViewHolder) holder).mSelect);
                }
            });
            ((ViewHolder) holder).linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ScenicDetailsActivity.class);
                    intent.putExtra("sid", mListSearch.get(position).getSid());
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mListSearch.size()==0?0:mListSearch.size();
    }

    class ViewHolder extends BaseViewHolder {
        @BindView(R.id.scenic_item_hook)
        ImageView mSelect;//
        @BindView(R.id.scenic_item_linear)
        LinearLayout linearLayout;
        @BindView(R.id.scenic_subtitle)
        TextView scenicSubtitle;//小标题
        @BindView(R.id.scenic_item_img)
        ImageView mImage;//展示图片
        @BindView(R.id.scenic_item_title)
        TextView mTextTitle;//标题
        @BindView(R.id.scenic_item_content)
        TextView mTextContent;//内容
        @BindView(R.id.scenic_item_money)
        TextView mTextMoney;//价钱
        @BindView(R.id.scenic_item_price)
        TextView mTextPic;//协议价

        public ViewHolder(View itemView, Context context) {
            super(itemView, context);
            ButterKnife.bind(this, itemView);
        }

        private void bindHolder(ScenicSearchBean.DataBean bean) {
            if (searchErrorResult){
                scenicSubtitle.setText("搜索结果");
            }else{
                scenicSubtitle.setText("为您推荐");
            }
            if (bean.isSelect()) {
                mSelect.setBackgroundResource(R.mipmap.selected_scenic);
            } else {
                mSelect.setBackgroundResource(R.mipmap.unselect_scenic);
            }

            OkHttpUtils.displayImg(mImage, bean.getPic());
            mTextContent.setText(bean.getBrief());
            mTextTitle.setText(bean.getTitle());
            mTextMoney.setText(bean.getPrice());
            mTextPic.setText(bean.getZz());
        }
    }
    public interface searchCallBack{
        void onClickSearch(int position, ImageView imageView);
    }
}
