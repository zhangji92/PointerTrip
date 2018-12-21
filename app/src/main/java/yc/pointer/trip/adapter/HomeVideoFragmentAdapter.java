package yc.pointer.trip.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
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
import yc.pointer.trip.activity.ActivityTrip;
import yc.pointer.trip.activity.CollectionActivity;
import yc.pointer.trip.activity.CouponActivity;

import yc.pointer.trip.activity.ExplainWebActivity;
import yc.pointer.trip.activity.LoginActivity;
import yc.pointer.trip.activity.MyMoneyActivity;
import yc.pointer.trip.activity.MyReserveActivity;
//import yc.pointer.trip.activity.PersonalPageActivity;
import yc.pointer.trip.activity.ReadingTravelsActivity;
import yc.pointer.trip.activity.ThemeActionActivity;
import yc.pointer.trip.activity.VerifyActivity;

import yc.pointer.trip.activity.VideoDetailsActivity;
import yc.pointer.trip.base.BaseViewHolder;
import yc.pointer.trip.bean.BookBean;
import yc.pointer.trip.bean.HomeVideoDataBean;
import yc.pointer.trip.bean.HomeVideoNewDataBean;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.SharedPreferencesUtils;
import yc.pointer.trip.untils.StringUtil;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by moyan on 2017/11/20.
 * 首页的适配器
 */

public class HomeVideoFragmentAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int TYPE_ONE = 1;//头布局
    private static final int TYPE_NEWLIST = 2;//精选路书


    private boolean islogin;


    private List<HomeVideoNewDataBean.DataBean.DataAdBean> mList = new ArrayList();//banner数据集合
    //    private List<HomeVideoDataBean.DataBean.DataCityBean> mListSameCity = new ArrayList();//存取同城数据
    private List<HomeVideoNewDataBean.DataBean.DataGoodBean> mListAll = new ArrayList<>();//精选路书
    //    private List<HomeVideoDataBean.DataBean.DataAdIndexBean> mListAction = new ArrayList<>();//精彩活动
    private Context mContext;
    List<String> mListAdPage = new ArrayList<>();
    private String mUserId;


    private HomeVideoNewDataBean.DataBean.HbBean hbInstance;


//    public SameCityOnClickListener sameCityOnClickListener;

    public HomeVideoFragmentAdapter(
            List<HomeVideoNewDataBean.DataBean.DataAdBean> mList,
//            List<HomeVideoDataBean.DataBean.DataCityBean> mListSameCity,
            List<HomeVideoNewDataBean.DataBean.DataGoodBean> mListAll
//            List<HomeVideoDataBean.DataBean.DataAdIndexBean> mListAction
    ) {

        this.mList = mList;
//        this.mListSameCity = mListSameCity;
        this.mListAll = mListAll;
//        this.mListAction = mListAction;


    }

//    public void setSameCityOnClickListener(SameCityOnClickListener sameCityOnClickListener) {
//        this.sameCityOnClickListener = sameCityOnClickListener;
//    }

    public void setHb(HomeVideoNewDataBean.DataBean.HbBean hb) {
        this.hbInstance = hb;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        if (viewType == TYPE_ONE) {//头布局
            View viewheader = LayoutInflater.from(mContext).inflate(R.layout.home_fragment_video_head, parent, false);
            HomeVideoHeadViewHolder headerViewHolder = new HomeVideoHeadViewHolder(viewheader, mContext);
            return headerViewHolder;
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.home_fragment_video_list, parent, false);
            HomeVideoViewHolder viewHolderNew = new HomeVideoViewHolder(view, mContext);
            return viewHolderNew;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {

        if (getItemViewType(position) == TYPE_ONE) {
            ((HomeVideoHeadViewHolder) holder).onBindHeadViewHolder(position);
        } else {

            if (position == 1) {
                ((HomeVideoViewHolder) holder).linerGoodVideo.setVisibility(View.VISIBLE);
            } else {
                ((HomeVideoViewHolder) holder).linerGoodVideo.setVisibility(View.GONE);
            }
            if (mListAll.size() > 0) {
                ((HomeVideoViewHolder) holder).onBindNormalViewHolder(position - 1);
            }
        }

    }

    @Override
    public int getItemCount() {
        return mListAll.size() == 0 ? 1 : mListAll.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_ONE;
        } else {
            return TYPE_NEWLIST;
        }
    }

    /**
     * 基础布局ViewHolder
     */
    public class HomeVideoViewHolder extends BaseViewHolder {
        @BindView(R.id.more_good)
        TextView moreGood;//更多路书
        @BindView(R.id.good_itemview)
        LinearLayout goodItemView;//最外围布局
        @BindView(R.id.home_video_page)
        LinearLayout homeVideoPage;//最外围布局

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
        @BindView(R.id.home_video_img_back)
        ImageView homeVideoImgBack;//视频图片
        @BindView(R.id.home_video_img)
        ImageView homeVideoImg;//视频图片
        @BindView(R.id.home_video_title)
        TextView homeVideoTitle;//视频标题
         @BindView(R.id.video_length)
        TextView length;//视频时长
//        @BindView(R.id.home_video_introduce_text)
//        TextView homeVideoIntroduceText;//视频介绍

        @BindView(R.id.home_play_num)
        TextView homePlayNum;//播放数

        @BindView(R.id.home_zan_num)
        TextView homeZanNum;//点赞数


        BookBean dataGoodBean;
        private int imgHeight = 300;
        private int widthPixels;

        public HomeVideoViewHolder(View itemView, Context context) {
            super(itemView, context);
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

        private void onBindNormalViewHolder(final int position) {
            //OkHttpUtils.displayImg(homeVideoFragmentHeadimg, mListAll.get(position).getPic());
            double width = Double.valueOf(mListAll.get(position).getWidth());
            double height = Double.valueOf(mListAll.get(position).getHeight());
            double than = height / width;
            double thanH = width / height;
            double paramsWidth = imgHeight * thanH;//paramsWidth
            double paramsHeight = widthPixels * than;
            FrameLayout.LayoutParams layoutParamsImg = (FrameLayout.LayoutParams) homeVideoImg.getLayoutParams();
            FrameLayout.LayoutParams layoutParamsBack = (FrameLayout.LayoutParams) homeVideoImgBack.getLayoutParams();
            if (than > 1) {//竖屏
//                //原图
                layoutParamsImg.width = (int) paramsWidth;
                layoutParamsImg.height = imgHeight;
                homeVideoImg.setLayoutParams(layoutParamsImg);
//                //背景
                layoutParamsBack.width = MATCH_PARENT;
                layoutParamsBack.height = imgHeight;
                homeVideoImgBack.setLayoutParams(layoutParamsBack);
                OkHttpUtils.displayGlide(mContext, homeVideoImg, mListAll.get(position).getB_pic());
                OkHttpUtils.displayGlideVague(mContext, homeVideoImgBack, mListAll.get(position).getB_pic());
//                OkHttpUtils.displayImg(homeVideoImg, mListAll.get(position).getB_pic());
            } else {//横屏

                layoutParamsImg.height = (int) paramsHeight;
                layoutParamsImg.width = MATCH_PARENT;
                homeVideoImg.setLayoutParams(layoutParamsImg);
//                homeVideoImgBack.setLayoutParams(layoutParamsImg);

                layoutParamsBack.width = MATCH_PARENT;
                layoutParamsBack.height = (int) paramsHeight;
                homeVideoImgBack.setLayoutParams(layoutParamsBack);

                OkHttpUtils.displayGlideVague(mContext, homeVideoImgBack, mListAll.get(position).getB_pic());
                OkHttpUtils.displayImg(homeVideoImg, mListAll.get(position).getB_pic());
            }

            homeVideoFragmentNick.setText(mListAll.get(position).getNickname());
            homeVideoTitle.setText(mListAll.get(position).getTitle());
//            homeVideoIntroduceText.setText(mListAll.get(position).getInfo());
            videoCollectionCount.setText(mListAll.get(position).getCol_num() + "收藏");
            homePlayNum.setText(mListAll.get(position).getLook_num() + " 播放");
            String zan = String.format(mContext.getResources().getString(R.string.zan_num), mListAll.get(position).getZan_num());
//            homeZanNum.setText(mListAll.get(position).getZan_num());
            homeZanNum.setText(zan);
            length.setText(mListAll.get(position).getLength());
            dataGoodBean = mListAll.get(position);
            goodItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //TODO 跳转视频播放
                    Intent intent = new Intent(mContext, VideoDetailsActivity.class);
                    intent.putExtra("dataGoodBean", dataGoodBean);
                    mContext.startActivity(intent);

                }
            });
            moreGood.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO　跳转看游记
                    Intent intent = new Intent(mContext, ReadingTravelsActivity.class);
                    mContext.startActivity(intent);
                }
            });
            //加载头像
//            OkHttpUtils.displayGlideCircular(mContext, homeVideoFragmentHeadimg, mListAll.get(position).getPic());
//            homeVideoFragmentHeadimg.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(mContext, PersonalPageActivity.class);
//                    intent.putExtra("uid", dataGoodBean.getUid());
//                    intent.putExtra("nick", mListAll.get(position).getNickname());
//                    intent.putExtra("pic", mListAll.get(position).getPic());
//                    mContext.startActivity(intent);
//                }
//            });
        }
    }

    /**
     * 头布局ViewHolder
     */
    public class HomeVideoHeadViewHolder extends BaseViewHolder implements View.OnClickListener {

        @BindView(R.id.header_img)
        ImageView headerIMg;//顶图
        @BindView(R.id.header_viewpager)
        Banner banner;
        @BindView(R.id.home_header_look)
        TextView homeHeaderLook;//看游记
        @BindView(R.id.home_header_record)
        TextView homeHeaderRecord;//优惠券
        @BindView(R.id.home_header_book)
        TextView homeHeaderBook;//预约订单
        @BindView(R.id.home_header_mybook)
        TextView homeHeaderMybook;//我的收藏

//        @BindView(R.id.hb)
//        ImageView hb;//红包
//        @BindView(R.id.verify)
//        ImageView verify;//验证

//        @BindView(R.id.action_title)
//        TextView actionTitle;//指针疯狂旅游年
//        @BindView(R.id.more_action)
//        TextView moreAction;//更多活动

//        @BindView(R.id.more_same_city)
//        TextView moreSameCity;//更多同城数据
//        @BindView(R.id.same_city_liner)
//        LinearLayout sameCityLiner;//更多同城数据
//        @BindView(R.id.same_city_title)
//        TextView sameCity;//同城

        public HomeVideoHeadViewHolder(View itemView, Context context) {
            super(itemView, context);
            ButterKnife.bind(this, itemView);
        }

        private void onBindHeadViewHolder(final int position) {

//            if (hbInstance != null) {
//                String pic = hbInstance.getPic();
//                String pic1 = hbInstance.getPic1();
////                String is_hb = hbInstance.getIs_hb();
////                if (!StringUtil.isEmpty(is_hb) && is_hb.equals("1")) {
////                    if (!StringUtil.isEmpty(pic)) {
////                        hb.setVisibility(View.VISIBLE);
////                        OkHttpUtils.displayImg(hb, pic1);
//////                    OkHttpUtils.displayGlide(mContext,hb,pic);
////                    } else {
////                        hb.setVisibility(View.GONE);
////                    }
////                } else {
////                    hb.setVisibility(View.GONE);
////                }
//
//                if (!StringUtil.isEmpty(pic1)) {
//                    verify.setVisibility(View.VISIBLE);
//                    OkHttpUtils.displayImg(verify, pic);
////                    OkHttpUtils.displayGlide(mContext,verify,pic1);
//                } else {
//                    verify.setVisibility(View.GONE);
//                }
//            }


//            RecyclerView headHorizontalView = (RecyclerView) itemView.findViewById(R.id.same_city_home);//同城列表
//            RecyclerView headActionView = (RecyclerView) itemView.findViewById(R.id.home_video_action_list);//指针疯狂旅游年
//            RecyclerView headRecentActionView = (RecyclerView) itemView.findViewById(R.id.home_video_recent_action);//近期活动
//            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);//同城列表
//            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//            headHorizontalView.setLayoutManager(layoutManager);
//            if (mListSameCity.size() > 0) {
//                sameCity.setVisibility(View.VISIBLE);
//                moreSameCity.setVisibility(View.VISIBLE);
//                headHorizontalView.setVisibility(View.VISIBLE);
//                headHorizontalView.setAdapter(new HomeHeadHizontalApter(mListSameCity));
//                moreSameCity.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //TODO 跳转看游记列表
//                        sameCityOnClickListener.sameCityMoreDateClick();
//                    }
//                });
//
//            } else {
//                sameCityLiner.setVisibility(View.GONE);
////                sameCity.setVisibility(View.GONE);
////                moreSameCity.setVisibility(View.GONE);
////                headHorizontalView.setVisibility(View.GONE);
//            }


//            LinearLayoutManager layoutManagerAction = new LinearLayoutManager(mContext);//指针疯狂旅游年
//            layoutManagerAction.setOrientation(LinearLayoutManager.HORIZONTAL);
//            headActionView.setLayoutManager(layoutManagerAction);

//            if (mListAction.size() > 0) {
//                actionTitle.setVisibility(View.VISIBLE);
////                moreAction.setVisibility(View.VISIBLE);
//                headActionView.setVisibility(View.VISIBLE);
//                headActionView.setAdapter(new HomeHeadActionApter(mListAction));
////                moreAction.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        //TODO 跳转活动列表页面
////                        Intent intent = new Intent(mContext, ActivityTrip.class);
////                        mContext.startActivity(intent);
////                    }
////                });
//
//            } else {
//                actionTitle.setVisibility(View.GONE);
////                moreAction.setVisibility(View.GONE);
//                headActionView.setVisibility(View.GONE);
//            }


//            LinearLayoutManager layoutManagerRecentAction = new LinearLayoutManager(mContext);//近期主题
//            layoutManagerRecentAction.setOrientation(LinearLayoutManager.HORIZONTAL);
//            headRecentActionView.setLayoutManager(layoutManagerRecentAction);
//            headRecentActionView.setAdapter(new HomeHeadRecentActionApter(mListAction));

//            mListAdPage.clear();
//            for (int i = 0; i < mList.size(); i++) {
//                String b_pic = mList.get(i).getPic();
//                mListAdPage.add(b_pic);
//            }

//            headerViewpager.setImages(mListAdPage).setImageLoader(new UpLoaderLocalUtils() {
//                @Override
//                public void displayImage(Context context, Object path, ImageView imageView) {
//                    OkHttpUtils.displayImg(imageView, "/" + (String) path);
//                }
//            }).start();
//            headerViewpager.setOnBannerClickListener(new OnBannerClickListener() {
//                @Override
//                public void OnBannerClick(int position) {
////                    String mUserId = SharedPreferencesUtils.getInstance().getString(mContext, "useId");
////                    if (!StringUtil.isEmpty(mUserId)&&!mUserId.equals("not find")){
////                        Intent intent = new Intent(mContext, ThemeActionActivity.class);
////                        mContext.startActivity(intent);
////                    }else {
////                        Intent intent = new Intent(mContext, LoginActivity.class);
////                        intent.putExtra("logFlag","actionMine");
////                        mContext.startActivity(intent);
////                    }
//                    //TODO 跳转视频播放
//                    BookBean dataAdBean = mList.get(position - 1);
//                    Intent intent = new Intent(mContext, VideoDetailsActivity.class);
//                    intent.putExtra("dataGoodBean", dataAdBean);
//                    mContext.startActivity(intent);
//                }
//            });

            if (mList.size() > 0) {
                banner.setVisibility(View.GONE);
                headerIMg.setVisibility(View.VISIBLE);
                String b_pic = mList.get(position).getPic();
                final String aid = mList.get(position).getAid();
                final String title = mList.get(position).getTitle();
                OkHttpUtils.displayGlide(mContext, headerIMg, b_pic);
                headerIMg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO 跳转活动详情
                        String mUserId = SharedPreferencesUtils.getInstance().getString(mContext, "useId");
                        if (!StringUtil.isEmpty(mUserId) && !mUserId.equals("not find")) {
                            Intent intent = new Intent(mContext, ThemeActionActivity.class);
                            if (!StringUtil.isEmpty(aid)) {
                                intent.putExtra("aid", aid);
                                intent.putExtra("title", title);
                            }
                            mContext.startActivity(intent);
                        } else {
                            Intent intent = new Intent(mContext, LoginActivity.class);
                            if (!StringUtil.isEmpty(aid)) {
                                intent.putExtra("aid", aid);
                                intent.putExtra("title", title);
                            }
                            intent.putExtra("logFlag", "actionHome");
                            mContext.startActivity(intent);
                        }
                    }
                });
            }

            homeHeaderMybook.setOnClickListener(this);
            homeHeaderBook.setOnClickListener(this);
            homeHeaderRecord.setOnClickListener(this);
            homeHeaderLook.setOnClickListener(this);
//            hb.setOnClickListener(this);
//            verify.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            mUserId = SharedPreferencesUtils.getInstance().getString(mContext, "useId");
            if (!StringUtil.isEmpty(mUserId) && !mUserId.equals("not find")) {
                islogin = true;
            } else {
                islogin = false;
            }
            switch (v.getId()) {
                case R.id.home_header_look:
//                    Intent intent = new Intent(mContext, ReadingTravelsActivity.class);
//                    mContext.startActivity(intent);

                    if (islogin == true) {

                        mContext.startActivity(new Intent(mContext, MyMoneyActivity.class));

                    } else {
                        //跳转登录
                        Intent intentlogin = new Intent(mContext, LoginActivity.class);
                        intentlogin.putExtra("logFlag", "myMoney");
                        mContext.startActivity(intentlogin);
                    }

                    break;
                case R.id.home_header_record:

                    if (!islogin) {
                        mUserId = "0";
                    }
                    Intent intentVerify = new Intent(mContext, ExplainWebActivity.class);
                    intentVerify.putExtra("title", "认证说明");
                    intentVerify.putExtra("url", URLUtils.BASE_URL + "/Home/Book/agreement?uid=" + mUserId);
                    mContext.startActivity(intentVerify);

//                    if (islogin == true) {
//
//                        mContext.startActivity(new Intent(mContext, VerifyActivity.class));
//                    } else {
//                        //跳转登录
//                        Intent intentlogin = new Intent(mContext, LoginActivity.class);
//                        intentlogin.putExtra("logFlag", "verify");
//                        mContext.startActivity(intentlogin);
//                    }
                    break;
                case R.id.home_header_book:
                    if (islogin == true) {

                        mContext.startActivity(new Intent(mContext, MyReserveActivity.class));

                    } else {
                        //跳转登录
                        Intent intentlogin = new Intent(mContext, LoginActivity.class);
                        intentlogin.putExtra("logFlag", "myReservation");
                        mContext.startActivity(intentlogin);
                    }
                    break;
                case R.id.home_header_mybook://跳转我的收藏
                    if (islogin == true) {
                        mContext.startActivity(new Intent(mContext, CollectionActivity.class));
                    } else {
                        //跳转登录
                        Intent intentlogin = new Intent(mContext, LoginActivity.class);
                        intentlogin.putExtra("logFlag", "myCollection");
                        mContext.startActivity(intentlogin);
                    }
                    break;
//                case R.id.hb://红包跳转
//                    Intent intentRed = new Intent(mContext, ExplainWebActivity.class);
//                    intentRed.putExtra("title", "红包说明");
//                    intentRed.putExtra("url", URLUtils.BASE_URL + "/Home/Book/hb?id=1");
//                    mContext.startActivity(intentRed);
//                    break;
//                case R.id.verify://认证说明
//                    if (!islogin) {
//                        mUserId = "0";
//                    }
//                    Intent intentVerify = new Intent(mContext, ExplainWebActivity.class);
//                    intentVerify.putExtra("title", "认证说明");
//                    intentVerify.putExtra("url", URLUtils.BASE_URL + "/Home/Book/agreement?uid=" + mUserId);
//                    mContext.startActivity(intentVerify);
//
//
//                    break;
            }
        }
    }

//    public interface SameCityOnClickListener {
//        void sameCityMoreDateClick();
//    }
}
