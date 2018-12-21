package yc.pointer.trip.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerClickListener;
import com.youth.banner.loader.ImageLoader;

import butterknife.OnClick;
import yc.pointer.trip.R;
import yc.pointer.trip.activity.ScenicDetailsActivity;
import yc.pointer.trip.base.BaseViewHolder;
import yc.pointer.trip.bean.ScenicBean;
import yc.pointer.trip.network.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 刘佳伟
 * 2017/7/13
 * 17:49
 * 景点页面适配器
 */
public class ScenicAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int TYPE_ONE = 1;
    private static final int TYPE_ALL = 2;
    private LayoutInflater mLayoutInflater;
    private List<ScenicBean> mList;
    private List<ScenicBean.DataBean.DataHotBean> mListHot;
    private List<ScenicBean.DataBean.DataOtherBean> mListOther;
    private Context mContext;
    private scenicCallBack mScenicCallBack;
    private ScenicBean scenicBean;

    public ScenicAdapter(List<ScenicBean> mList, List<ScenicBean.DataBean.DataHotBean> mListHot, List<ScenicBean.DataBean.DataOtherBean> mListOther, Context context) {
        this.mList = mList;
        this.mListHot = mListHot;
        this.mListOther = mListOther;
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewAllHolder viewHolder = new ViewAllHolder(mLayoutInflater.inflate(R.layout.adapter_scenic, parent, false), mContext);
        if (viewType == TYPE_ONE) {
            return new ViewHeadHolder(mLayoutInflater.inflate(R.layout.scenic_header_layout, parent, false), mContext);
        } else {
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, final int position) {

        if (getItemViewType(position) == TYPE_ONE) {
            if (mList.get(position).getData().getData_type().size() > 0) {
                scenicBean = mList.get(position);
                ((ViewHeadHolder) holder).bindHeadHolder(scenicBean);
            }
        } else {
            if (position < mListHot.size() + 1) {
                if (position == 1) {
                    ((ViewAllHolder) holder).scenicSubtitle.setText("热门景点");
                    ((ViewAllHolder) holder).scenicSubtitle.setVisibility(View.VISIBLE);
                } else {

                    ((ViewAllHolder) holder).scenicSubtitle.setVisibility(View.GONE);
                }
                if (mListHot.size() > 0) {
                    OkHttpUtils.displayImg(((ViewAllHolder) holder).mImage, "/" + mListHot.get(position - 1).getPic());
                    ((ViewAllHolder) holder).mTextContent.setText(mListHot.get(position - 1).getBrief());
                    ((ViewAllHolder) holder).mTextTitle.setText(mListHot.get(position - 1).getTitle());
                    ((ViewAllHolder) holder).mTextMoney.setText(mListHot.get(position - 1).getPrice());
                    ((ViewAllHolder) holder).mTextPic.setText(mListHot.get(position - 1).getZz());
                    ((ViewAllHolder) holder).mSelect.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {


                            mScenicCallBack.onClickSelectHot(position - 1, ((ViewAllHolder) holder).mSelect);


                        }

                    });
                    if (mListHot.get(position - 1).isSelect()) {
                        ((ViewAllHolder) holder).mSelect.setBackgroundResource(R.mipmap.selected_scenic);
                    } else {
                        ((ViewAllHolder) holder).mSelect.setBackgroundResource(R.mipmap.unselect_scenic);
                    }
                    ((ViewAllHolder) holder).linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mContext, ScenicDetailsActivity.class);
                            intent.putExtra("sid", mListHot.get(position - 1).getSid());
                            mContext.startActivity(intent);
                        }
                    });

                }

            } else {
                if (position == mListHot.size() + 1) {
                    ((ViewAllHolder) holder).scenicSubtitle.setText("全部景点");
                    ((ViewAllHolder) holder).scenicSubtitle.setVisibility(View.VISIBLE);
                } else {
                    ((ViewAllHolder) holder).scenicSubtitle.setVisibility(View.GONE);
                }
                if (mListOther.size() > 0) {
                    OkHttpUtils.displayImg(((ViewAllHolder) holder).mImage, "/" + mListOther.get(position - mListHot.size() - 1).getPic());
                    ((ViewAllHolder) holder).mTextContent.setText(mListOther.get(position - mListHot.size() - 1).getBrief());
                    ((ViewAllHolder) holder).mTextTitle.setText(mListOther.get(position - mListHot.size() - 1).getTitle());
                    ((ViewAllHolder) holder).mTextMoney.setText(mListOther.get(position - mListHot.size() - 1).getPrice());
                    ((ViewAllHolder) holder).mTextPic.setText(mListOther.get(position - mListHot.size() - 1).getZz());
                    ((ViewAllHolder) holder).mSelect.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mScenicCallBack.onClickSelectOther(position - mListHot.size() - 1, ((ViewAllHolder) holder).mSelect);
                        }
                    });
                    if (mListOther.get(position - mListHot.size() - 1).isSelect()) {
                        ((ViewAllHolder) holder).mSelect.setBackgroundResource(R.mipmap.selected_scenic);
                    } else {
                        ((ViewAllHolder) holder).mSelect.setBackgroundResource(R.mipmap.unselect_scenic);
                    }
                    //    mImage
                    ((ViewAllHolder) holder).linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mContext, ScenicDetailsActivity.class);
                            intent.putExtra("sid", mListOther.get(position - mListHot.size() - 1).getSid());
                            mContext.startActivity(intent);
                        }
                    });
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mListHot.size() + mListOther.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_ONE;
        } else {
            return TYPE_ALL;
        }
    }

    public class ViewHeadHolder extends BaseViewHolder {

        @BindView(R.id.header_viewpager)
        Banner mBanner;//轮播图
        @BindView(R.id.scenic_head_food)
        TextView mTextFood;//美食
        @BindView(R.id.scenic_head_humanity)
        TextView mTextHumanity;//人文
        @BindView(R.id.scenic_head_outdoors)
        TextView mTextOutdoors;//户外
        @BindView(R.id.scenic_head_img_food)
        ImageView mImageFood;//美食图片
        @BindView(R.id.scenic_head_img_humanity)
        ImageView mImageHumanity;//人文图片
        @BindView(R.id.scenic_head_img_outdoors)
        ImageView mImageOutDoors;//户外

        public ViewHeadHolder(View itemView, Context context) {
            super(itemView, context);
            ButterKnife.bind(this, itemView);
        }

        private void bindHeadHolder(final ScenicBean scenicBean) {
            if (scenicBean.getData().getData_type().size() > 0) {
                if (scenicBean.getData().getData_ad().size()>0){
                    mBanner.setVisibility(View.VISIBLE);
                }else {
                    mBanner.setVisibility(View.GONE);
                }
                itemView.setVisibility(View.VISIBLE);
                List<String> list = new ArrayList<>();
                for (int i = 0; i < scenicBean.getData().getData_ad().size(); i++) {
                    list.add(scenicBean.getData().getData_ad().get(i).getPic());
                }
                //图片展示
                mBanner.setImages(list).setImageLoader(new ImageLoader() {
                    @Override
                    public void displayImage(Context context, Object path, ImageView imageView) {
                        OkHttpUtils.displayImg(imageView, "" + path);
                    }
                }).start();
                //点击事件
                mBanner.setOnBannerClickListener(new OnBannerClickListener() {
                    @Override
                    public void OnBannerClick(int position) {
                        Intent intent = new Intent(mContext, ScenicDetailsActivity.class);
                        intent.putExtra("sid", scenicBean.getData().getData_ad().get(position - 1).getSid());
                        mContext.startActivity(intent);
                    }
                });
                if (scenicBean.getData().getData_type().size() > 0) {
                    mTextFood.setText(scenicBean.getData().getData_type().get(0).getTitle());
                    mTextHumanity.setText(scenicBean.getData().getData_type().get(1).getTitle());
                    mTextOutdoors.setText(scenicBean.getData().getData_type().get(2).getTitle());

                    OkHttpUtils.displayImg(mImageFood, scenicBean.getData().getData_type().get(0).getPic());
                    OkHttpUtils.displayImg(mImageHumanity, scenicBean.getData().getData_type().get(1).getPic());
                    OkHttpUtils.displayImg(mImageOutDoors, scenicBean.getData().getData_type().get(2).getPic());
                }


            } else {
                itemView.setVisibility(View.GONE);


            }
        }

        @OnClick({R.id.scenic_head_food, R.id.scenic_head_humanity, R.id.scenic_head_outdoors, R.id.scenic_head_img_food
                , R.id.scenic_head_img_humanity, R.id.scenic_head_img_outdoors})
        public void onClick(View v) {
            switch (v.getId()) {//类型搜索
                case R.id.scenic_head_food://美食
                    if (mScenicCallBack != null) {
                        mScenicCallBack.onClickType(scenicBean.getData().getData_type().get(0).getTypeid());
                    }
                    break;
                case R.id.scenic_head_humanity://人文
                    if (mScenicCallBack != null) {
                        mScenicCallBack.onClickType(scenicBean.getData().getData_type().get(1).getTypeid());
                    }
                    break;
                case R.id.scenic_head_outdoors://户外
                    if (mScenicCallBack != null) {
                        mScenicCallBack.onClickType(scenicBean.getData().getData_type().get(2).getTypeid());
                    }
                    break;
                case R.id.scenic_head_img_food://美食图片
                    if (mScenicCallBack != null) {
                        mScenicCallBack.onClickType(scenicBean.getData().getData_type().get(0).getTypeid());
                    }
                    break;
                case R.id.scenic_head_img_humanity://人文图片

                    if (mScenicCallBack != null) {
                        mScenicCallBack.onClickType(scenicBean.getData().getData_type().get(1).getTypeid());
                    }
                    break;
                case R.id.scenic_head_img_outdoors://户外图片
                    if (mScenicCallBack != null) {
                        mScenicCallBack.onClickType(scenicBean.getData().getData_type().get(2).getTypeid());
                    }
                    break;

            }
        }
    }

    class ViewAllHolder extends BaseViewHolder {
        @BindView(R.id.scenic_item_hook)
        ImageView mSelect;
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

        public ViewAllHolder(View itemView, Context context) {
            super(itemView, context);
            ButterKnife.bind(this, itemView);
        }
    }

    //构造方法
    public void setScenicCallBack(scenicCallBack scenicCallBack) {
        this.mScenicCallBack = scenicCallBack;
    }

    //接口回调，回调类型搜索的typeId
    public interface scenicCallBack {
        void onClickType(String type);

        void onClickSelectOther(int position, ImageView imageVIew);

        void onClickSelectHot(int position, ImageView imageVIew);

    }
}
