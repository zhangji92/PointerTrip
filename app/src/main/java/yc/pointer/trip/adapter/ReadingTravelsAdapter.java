package yc.pointer.trip.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerClickListener;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
//import yc.pointer.trip.activity.PersonalPageActivity;
import yc.pointer.trip.activity.VideoDetailsActivity;
import yc.pointer.trip.bean.BookBean;
import yc.pointer.trip.bean.ReadBookBean;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.untils.StringUtil;

/**
 * Created by 张继
 * 2017/6/29
 * 17:49
 * 全部路书适配器
 */
public class ReadingTravelsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ONE = 1;
    private static final int TYPE_ALL = 2;
    private List<ReadBookBean.DataBean.DataQBean> mList;//数据集合
    private List<ReadBookBean> mReadBook;//数据集合
    private Context mContext;


    public ReadingTravelsAdapter(Context context, List<ReadBookBean.DataBean.DataQBean> List, List<ReadBookBean> ListAd) {
        this.mContext = context;
        this.mList = List;
        this.mReadBook = ListAd;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ONE) {
            return new ViewHolderHead(LayoutInflater.from(mContext).inflate(R.layout.read_travels_header, parent, false));
        } else {
            return new ViewHolderAll(LayoutInflater.from(mContext).inflate(R.layout.home_fragment_video_list, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == TYPE_ONE) {
            if (mReadBook.size() > 0) {
                ((ViewHolderHead) holder).bindHolderHead(mReadBook.get(position));
            }

        } else {
            if (mList.size() > 0) {
                ((ViewHolderAll) holder).bindHolder(position - 1);
            }

        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_ONE;
        } else {
            return TYPE_ALL;
        }
    }

    public class ViewHolderHead extends RecyclerView.ViewHolder {
        @BindView(R.id.header_img)
        ImageView headerIMg;//顶图
        @BindView(R.id.header_viewpager)
        Banner banner;
        @BindView(R.id.read_travel_horizontal)
        RecyclerView mRecyclerView;
        List<String> mListAdPage = new ArrayList<>();

        public ViewHolderHead(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bindHolderHead(final ReadBookBean bookBean) {
            if (bookBean != null) {

                headerIMg.setVisibility(View.GONE);
                banner.setVisibility(View.VISIBLE);
                mListAdPage.clear();
                for (int i = 0; i < bookBean.getData().getData_ad().size(); i++) {
                    mListAdPage.add(bookBean.getData().getData_ad().get(i).getB_pic());
                }
                banner.setImages(mListAdPage).setImageLoader(new ImageLoader() {
                    @Override
                    public void displayImage(Context context, Object path, ImageView imageView) {
                        OkHttpUtils.displayImg(imageView, "/" + (String) path);
                    }
                }).start();
                banner.setOnBannerClickListener(new OnBannerClickListener() {
                    @Override
                    public void OnBannerClick(int position) {
                        BookBean dataAdBean = bookBean.getData().getData_ad().get(position - 1);
                        Intent intent = new Intent(mContext, VideoDetailsActivity.class);
                        intent.putExtra("dataGoodBean", dataAdBean);
                        mContext.startActivity(intent);

//                        String bid = bookBean.getData().getData_ad().get(position - 1).getBid();
//                        Intent intent = new Intent(mContext, BookDetailsActivity.class);
//                        intent.putExtra("bid", bid);
//                        mContext.startActivity(intent);
                    }
                });

                mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                mRecyclerView.setAdapter(new HorizontalAdapter(bookBean.getData().getData_r()));
            }
        }
    }

    public class ViewHolderAll extends RecyclerView.ViewHolder {

        @BindView(R.id.good_itemview)
        LinearLayout goodItemView;//路书详情
        @BindView(R.id.home_video_page)
        LinearLayout homeVideoPage;//个人主页


        @BindView(R.id.liner_good_video)
        LinearLayout linerGoodVideo;//精选及更多布局
        @BindView(R.id.home_video_fragment_headimg)
        ImageView homeVideoFragmentHeadimg;//头像
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
        @BindView(R.id.home_video_title)
        TextView homeVideoTitle;//视频标题
//        @BindView(R.id.home_video_introduce_text)
//        TextView homeVideoIntroduceText;//视频介绍

        @BindView(R.id.home_play_num)
        TextView homePlayNum;//播放数

        @BindView(R.id.home_zan_num)
        TextView homeZanNum;//点赞数

        @BindView(R.id.video_length)
        TextView length;//视频时长

        ReadBookBean.DataBean.DataQBean dataGoodBean;
        private int imgHeight = 300;
        private final int widthPixels;

        public ViewHolderAll(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            DisplayMetrics dm = mContext.getApplicationContext().getResources().getDisplayMetrics();
            widthPixels = dm.widthPixels;
            if (widthPixels <= 480) {
                imgHeight = 300;
            } else if (widthPixels <= 720) {
                imgHeight = 600;

            } else if (widthPixels <= 1080) {
                imgHeight = 800;
            } else {
                imgHeight = 800;
            }
        }

        private void bindHolder(final int position) {
            linerGoodVideo.setVisibility(View.GONE);
            String pic = mList.get(position).getPic();
            if (!StringUtil.isEmpty(pic)){
//                OkHttpUtils.displayGlideCircular(mContext,homeVideoFragmentHeadimg, pic);//加载头像
            }

            //OkHttpUtils.displayImg(homeVideoFragmentHeadimg, mList.get(position).getPic());
            //OkHttpUtils.displayImg(homeVideoImg,mList.get(position).getB_pic());
            double width = Double.valueOf(mList.get(position).getWidth());
            double height = Double.valueOf(mList.get(position).getHeight());
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
                OkHttpUtils.displayGlideVague(mContext,homeVideoImgBack,mList.get(position).getB_pic());


                OkHttpUtils.displayImg(homeVideoImg, mList.get(position).getB_pic());
            } else {//横屏
                layoutParamsImg.height = (int) paramsHeight;
                layoutParamsImg.width = widthPixels;
                homeVideoImg.setLayoutParams(layoutParamsImg);
                //背景
                layoutParamsBack.width = widthPixels;
                layoutParamsBack.height = (int) paramsHeight;
                homeVideoImgBack.setLayoutParams(layoutParamsBack);
                OkHttpUtils.displayGlideVague(mContext,homeVideoImgBack,mList.get(position).getB_pic());
                OkHttpUtils.displayImg(homeVideoImg, mList.get(position).getB_pic());
            }


            homeVideoFragmentNick.setText(mList.get(position).getNickname());
            homeVideoTitle.setText(mList.get(position).getTitle());
//            homeVideoIntroduceText.setText(mList.get(position).getInfo());
            videoCollectionCount.setText(mList.get(position).getCol_num() + "收藏");

            homePlayNum.setText(mList.get(position).getLook_num()+" 播放");
            length.setText(mList.get(position).getLength());
            String zan = String.format(mContext.getResources().getString(R.string.zan_num), mList.get(position).getZan_num());
            homeZanNum.setText(zan);

            dataGoodBean = mList.get(position);
            goodItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO 跳转视频播放
                    Intent intent = new Intent(mContext, VideoDetailsActivity.class);
                    intent.putExtra("dataGoodBean", dataGoodBean);
                    mContext.startActivity(intent);
                }
            });

            homeVideoPage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(mContext, PersonalPageActivity.class);
//                    intent.putExtra("uid", dataGoodBean.getUid());
//                    intent.putExtra("nick", mList.get(position).getNickname());
//                    intent.putExtra("pic", mList.get(position).getPic());
//                    mContext.startActivity(intent);
                }
            });
//                moreGood.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //TODO　跳转看游记
//                        Intent intent=new Intent(mContext, ReadingTravelsActivity.class);
//                        mContext.startActivity(intent);
//                    }
//                });


//                autoRelativeLayout.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        String uid = dataQBean.getBid();
//                        Intent intent = new Intent(mContext, BookDetailsActivity.class);
//                        intent.putExtra("bid", uid);
//                        mContext.startActivity(intent);
//                    }
//                });
//
//                String path = dataQBean.getPic();
//                if (!StringUtil.isEmpty(path)) {
//                    OkHttpUtils.displayImg(customCircleImage, path);//加载头像
//                } else {
//                    customCircleImage.setImageResource(R.mipmap.head);
//                }
//                if (dataQBean.getType().equals("美食")) {
//                    mImg_label.setImageResource(R.mipmap.delicious_food);
//                } else if (dataQBean.getType().equals("人文")) {
//                    mImg_label.setImageResource(R.mipmap.humanity);
//                } else if (dataQBean.getType().equals("深度")) {
//                    mImg_label.setImageResource(R.mipmap.depth);
//                } else if (dataQBean.getType().equals("自驾")) {
//                    mImg_label.setImageResource(R.mipmap.self_driving);
//                } else if (dataQBean.getType().equals("骑行")) {
//                    mImg_label.setImageResource(R.mipmap.riding);
//                } else if (dataQBean.getType().equals("购物")) {
//                    mImg_label.setImageResource(R.mipmap.shopping);
//                }
//                OkHttpUtils.displayImg(mImg, dataQBean.getB_pic());
//                mText_title.setText(dataQBean.getSpot());
//                mText_author.setText(dataQBean.getCp());
//                mText_nick.setText(dataQBean.getNickname());
//                mText_browse.setText(dataQBean.getLook_num());
//                mText_fabulous.setText(dataQBean.getZan_num());
//                mText_phone.setText(dataQBean.getOrd_num());


        }
    }
}
