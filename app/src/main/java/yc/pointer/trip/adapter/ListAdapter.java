package yc.pointer.trip.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.activity.NewPersonalHomePageActivity;
//import yc.pointer.trip.activity.PersonalPageActivity;
import yc.pointer.trip.activity.VideoDetailsActivity;
import yc.pointer.trip.bean.SearchResultBean;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.CustomCircleImage;

/**
 * Created by Administrator on 2017/7/3.
 * 搜索结果数据列表适配器
 */
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {


    private LayoutInflater mLayoutInflater;
    private List<SearchResultBean.DataBean> mList;
    private Context mContext;
    private boolean flag = false;

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public ListAdapter(Context context, List<SearchResultBean.DataBean> list) {
        this.mContext = context;
        this.mList = list;
        this.mLayoutInflater = LayoutInflater.from(context);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.home_fragment_video_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (position == 0) {
            holder.linerGoodVideo.setVisibility(View.VISIBLE);
        } else {
            holder.linerGoodVideo.setVisibility(View.GONE);
        }
        if (mList != null) {
            holder.bindHolder(mList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() == 0 ? 0 : mList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.good_itemview)
        LinearLayout goodItemView;//最外围布局
        @BindView(R.id.home_video_page)
        LinearLayout homeVideoPage;//最外围布局

        @BindView(R.id.liner_good_video)
        LinearLayout linerGoodVideo;//精选及更多布局

        @BindView(R.id.good_text)
        TextView goodText;//小标题

        @BindView(R.id.more_good)
        TextView moreGood;//更多
        @BindView(R.id.home_video_fragment_headimg)
        CustomCircleImage homeVideoFragmentHeadimg;//头像
        @BindView(R.id.verify_result)
        ImageView verifyResult;//vip认证标识

        @BindView(R.id.home_video_fragment_nick)
        TextView homeVideoFragmentNick;//昵称
        @BindView(R.id.home_video_collection)
        ImageView homeVideoCollection;//收藏图标
        @BindView(R.id.video_collection_count)
        TextView videoCollectionCount;//收藏数量
        @BindView(R.id.home_video_img)
        ImageView homeVideoImg;//视频图片
        @BindView(R.id.home_video_img_back)
        ImageView homeVideoImgBack;//视频图片

        @BindView(R.id.home_play_num)
        TextView homePlayNum;//播放数

        @BindView(R.id.home_zan_num)
        TextView homeZanNum;//点赞数

        @BindView(R.id.video_length)
        TextView length;//视频时长

        @BindView(R.id.home_video_title)
        TextView homeVideoTitle;//视频标题
//        @BindView(R.id.home_video_introduce_text)
//        TextView homeVideoIntroduceText;//视频介绍

        SearchResultBean.DataBean dataGoodBean;
        private int imgHeight = 300;
        private final int widthPixels;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            DisplayMetrics dm = mContext.getApplicationContext().getResources().getDisplayMetrics();
            widthPixels = dm.widthPixels;
            if (widthPixels <= 480) {
                imgHeight = 300;
            } else if (widthPixels <= 720) {
                imgHeight = 600;

            } else if (widthPixels >= 1080) {
                imgHeight = 800;
            }
        }

        private void bindHolder(final SearchResultBean.DataBean bean) {
            if (bean != null) {
                if (flag) {
                    goodText.setText("为您推荐");
                } else {
                    goodText.setText("相关游记");
                }
                moreGood.setVisibility(View.GONE);
                dataGoodBean = bean;
                goodItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Intent intent=new Intent(mContext, BookDetailsActivity.class);
//                        intent.putExtra("bid",bean.getBid());
//                        mContext.startActivity(intent);

                        Intent intent = new Intent(mContext, VideoDetailsActivity.class);
                        intent.putExtra("dataGoodBean", dataGoodBean);
                        mContext.startActivity(intent);
                    }
                });

                String pic = bean.getPic();
                String is_vip = bean.getIs_vip();
                OkHttpUtils.displayGlideCircular(mContext, homeVideoFragmentHeadimg, pic,verifyResult,is_vip);

                double width = Double.valueOf(bean.getWidth());
                double height = Double.valueOf(bean.getHeight());
                double than = height / width;
                double thanH = width / height;
                double paramsWidth = imgHeight * thanH;//paramsWidth
                double paramsHeight = widthPixels * than;
                FrameLayout.LayoutParams layoutParamsImg = (FrameLayout.LayoutParams) homeVideoImg.getLayoutParams();
                FrameLayout.LayoutParams layoutParamsBack = (FrameLayout.LayoutParams) homeVideoImgBack.getLayoutParams();
                if (than > 1) {//竖屏
                    //原图
                    layoutParamsImg.width = (int) paramsWidth;
                    layoutParamsImg.height = imgHeight;
                    homeVideoImg.setLayoutParams(layoutParamsImg);
                    //背景
                    layoutParamsBack.width = widthPixels;
                    layoutParamsBack.height = imgHeight;
                    homeVideoImgBack.setLayoutParams(layoutParamsBack);
                    OkHttpUtils.displayGlideVague(mContext,homeVideoImgBack,bean.getB_pic());

                    OkHttpUtils.displayGlide(mContext,homeVideoImg, bean.getB_pic());
                } else {//横屏
                    layoutParamsImg.height = (int) paramsHeight;
                    layoutParamsImg.width = widthPixels;
                    homeVideoImg.setLayoutParams(layoutParamsImg);
                    //背景
                    layoutParamsBack.width = widthPixels;
                    layoutParamsBack.height = (int) paramsHeight;
                    homeVideoImgBack.setLayoutParams(layoutParamsBack);
                    OkHttpUtils.displayGlideVague(mContext,homeVideoImgBack,bean.getB_pic());
                    OkHttpUtils.displayImg(homeVideoImg, bean.getB_pic());
                }


                //OkHttpUtils.displayImg(homeVideoImg, "/" + bean.getB_pic());


                homeVideoFragmentNick.setText(bean.getNickname());
//                mText_author.setText(bean.getCp());

//                if (bean.getType().equals("美食")) {
//                    mLabel_image.setImageResource(R.mipmap.delicious_food);
//                } else if (bean.getType().equals("人文")) {
//                    mLabel_image.setImageResource(R.mipmap.humanity);
//                } else if (bean.getType().equals("深度")) {
//                    mLabel_image.setImageResource(R.mipmap.depth);
//                } else if (bean.getType().equals("自驾")) {
//                    mLabel_image.setImageResource(R.mipmap.self_driving);
//                } else if (bean.getType().equals("骑行")) {
//                    mLabel_image.setImageResource(R.mipmap.riding);
//                } else if (bean.getType().equals("购物")) {
//                    mLabel_image.setImageResource(R.mipmap.shopping);
//                }

                homeVideoTitle.setText(bean.getTitle());
                videoCollectionCount.setText(bean.getCol_num() + " 人收藏");
                homePlayNum.setText(bean.getLook_num()+" 播放");
                String zan = String.format(mContext.getResources().getString(R.string.zan_num), bean.getZan_num());
                homeZanNum.setText(zan);
                length.setText(bean.getLength());

//                String is_jie = bean.getIs_jie();
//                if (!StringUtil.isEmpty(is_jie)&&is_jie.equals("2")){
//                    verifyResult.setVisibility(View.VISIBLE);
//                }else {
//                    verifyResult.setVisibility(View.GONE);
//                }
//                homeVideoIntroduceText.setText(bean.getInfo());
//                mText_fabulous.setText(bean.getZan_num());
//                mText_phone.setText(bean.getOrd_num());
                homeVideoFragmentHeadimg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, NewPersonalHomePageActivity.class);
                        intent.putExtra("uid", dataGoodBean.getUid());
                        //intent.putExtra("nick", bean.getNickname());
                        //intent.putExtra("pic", bean.getPic());
                        mContext.startActivity(intent);
                    }
                });
            }

        }
    }
}
