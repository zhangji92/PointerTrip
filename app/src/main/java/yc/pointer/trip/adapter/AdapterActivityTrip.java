package yc.pointer.trip.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerClickListener;
import com.youth.banner.loader.ImageLoader;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.activity.LoginActivity;
import yc.pointer.trip.activity.ThemeActionActivity;
import yc.pointer.trip.base.BaseViewHolder;
import yc.pointer.trip.bean.ActivityTripBean;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.untils.SharedPreferencesUtils;
import yc.pointer.trip.untils.StringUtil;


/**
 * Created by 张继 on 2017/11/21.
 * 活动适配器
 */

public class AdapterActivityTrip extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int TYPE_ONE = 1;//头布局
    private static final int TYPE_ALL = 3;//全部活动
    private static final int TYPE_NEWLIST = 2;//本期精彩

    private ActivityTripBean.DataBean.DataGoodBean dataGoodBean;
    private List<ActivityTripBean.DataBean.DataAdBean> mListAd;
    private List<ActivityTripBean.DataBean.DataAllBean> mListAll;
    private Context mContext;

    public AdapterActivityTrip(ActivityTripBean.DataBean.DataGoodBean dataGoodBean,
                               List<ActivityTripBean.DataBean.DataAdBean> mListAd,
                               List<ActivityTripBean.DataBean.DataAllBean> mListAll) {
        this.dataGoodBean = dataGoodBean;
        this.mListAd = mListAd;
        this.mListAll = mListAll;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        if (viewType == TYPE_ONE) {
            View view = View.inflate(parent.getContext(), R.layout.activity_trip_head, null);
            TripHolderHead tripHolderHead = new TripHolderHead(view, parent.getContext());
            return tripHolderHead;
        } else if (viewType == TYPE_NEWLIST) {
            View view = View.inflate(parent.getContext(), R.layout.activity_trip_period, null);
            TripHolderPeriod tripHolderPeriod = new TripHolderPeriod(view, parent.getContext());
            return tripHolderPeriod;
        } else {
            View view = View.inflate(parent.getContext(), R.layout.activity_trip_all, null);
            TripHolderAll tripHolderAll = new TripHolderAll(view, parent.getContext());
            return tripHolderAll;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_ONE) {
            if (mListAd.size() > 0) {
                ((TripHolderHead) holder).headHolder(mListAd);
            }
        } else if (getItemViewType(position) == TYPE_NEWLIST) {
            if (dataGoodBean != null) {
                if (position == 1) {
                    ((TripHolderPeriod) holder).linearLayout.setVisibility(View.VISIBLE);
                } else {
                    ((TripHolderPeriod) holder).linearLayout.setVisibility(View.GONE);
                }
                ((TripHolderPeriod) holder).holder(dataGoodBean);
            }
        } else {
            if (mListAll.size() > 0) {
                if (position == 2) {
                    ((TripHolderAll) holder).all_linear.setVisibility(View.VISIBLE);
                } else {
                    ((TripHolderAll) holder).all_linear.setVisibility(View.GONE);
                }
                ((TripHolderAll) holder).holder(mListAll.get(position - 2));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mListAll.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_ONE;
        } else if (position == 1) {
            return TYPE_NEWLIST;
        } else {
            return TYPE_ALL;
        }

    }


    class TripHolderHead extends BaseViewHolder {
        @BindView(R.id.activity_trip_head_auto)
        LinearLayout autoLinearLayout;
        @BindView(R.id.activity_trip_head)
        Banner banner;
        List<String> listPic = new ArrayList<>();
        List<String> listTitle = new ArrayList<>();

        TripHolderHead(View itemView, Context context) {
            super(itemView, context);
            ButterKnife.bind(this, itemView);
        }

        public void headHolder(final List<ActivityTripBean.DataBean.DataAdBean> listAd) {
            listPic.clear();
            listTitle.clear();
            for (int i = 0; i < listAd.size(); i++) {
                listPic.add(listAd.get(i).getPic());
                listTitle.add(listAd.get(i).getTitle());
            }
            banner.setOnBannerClickListener(new OnBannerClickListener() {
                @Override
                public void OnBannerClick(int position) {
                    //TODO 跳转活动详情
                    String mUserId = SharedPreferencesUtils.getInstance().getString(mContext, "useId");
                    if (!StringUtil.isEmpty(mUserId) && !mUserId.equals("not find")) {
                        Intent intent = new Intent(mContext, ThemeActionActivity.class);
                        String aid = listAd.get(position - 1).getAid();
                        String title = listAd.get(position - 1).getTitle();
                        if (!StringUtil.isEmpty(aid)) {
                            intent.putExtra("aid",aid);
                            intent.putExtra("title",title);
                        }
                        mContext.startActivity(intent);
                    } else {
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        if (!StringUtil.isEmpty(listAd.get(position - 1).getAid())) {
                            intent.putExtra("aid", listAd.get(position - 1).getAid());
                            intent.putExtra("title",listAd.get(position - 1).getTitle());
                        }
                        intent.putExtra("logFlag", "activityTrip");
                        mContext.startActivity(intent);
                    }
                }
            });
            banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
            banner.setBannerTitles(listTitle);
            banner.setImages(listPic).setImageLoader(new ImageLoader() {
                @Override
                public void displayImage(Context context, Object path, ImageView imageView) {
                    OkHttpUtils.displayImg(imageView, "/" + path);
                }
            }).start();
            banner.setBannerAnimation(Transformer.Accordion);
        }
    }

    class TripHolderPeriod extends BaseViewHolder {
        @BindView(R.id.activity_trip_period_auto)
        LinearLayout autoLinearLayout;//点击事件
        @BindView(R.id.activity_trip_all_linear)
        LinearLayout linearLayout;//小标题背景
        @BindView(R.id.activity_trip_period_period)
        TextView textPeriod;//小标题
        @BindView(R.id.trip_day)
        TextView dayText;
        @BindView(R.id.activity_trip_period_month)
        TextView textMonth;//月份
        @BindView(R.id.activity_trip_period_title)
        TextView textTitle;//标题
        @BindView(R.id.activity_trip_period_content)
        TextView textContent;//内容

        TripHolderPeriod(View itemView, Context context) {
            super(itemView, context);
            ButterKnife.bind(this, itemView);
            //屏幕适配
        }

        public void holder(final ActivityTripBean.DataBean.DataGoodBean dataGoodBean) {
            autoLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mUserId = SharedPreferencesUtils.getInstance().getString(mContext, "useId");
                    if (!StringUtil.isEmpty(mUserId) && !mUserId.equals("not find")) {
                        Intent intent = new Intent(mContext, ThemeActionActivity.class);
                        if (!StringUtil.isEmpty(dataGoodBean.getAid())) {
                            intent.putExtra("aid", dataGoodBean.getAid());
                            intent.putExtra("title", dataGoodBean.getTitle());

                        }
                        mContext.startActivity(intent);
                    } else {
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        if (!StringUtil.isEmpty(dataGoodBean.getAid())) {
                            intent.putExtra("aid", dataGoodBean.getAid());
                            intent.putExtra("title", dataGoodBean.getTitle());
                        }
                        intent.putExtra("logFlag", "activityTrip");
                        mContext.startActivity(intent);
                    }
                }
            });

            String addtimeMonth = dataGoodBean.getAddtime();
            if (!StringUtil.isEmpty(addtimeMonth)) {
                String month = addtimeMonth.substring(5, 7);
                textMonth.setText("/" + month + "月");
            }
            String addtimeDay = dataGoodBean.getAddtime();
            if (!StringUtil.isEmpty(addtimeDay)) {
                String day = addtimeDay.substring(8, 10);
                dayText.setText(day);
            }

            String title = dataGoodBean.getTitle();
            if (!StringUtil.isEmpty(title)) {
                textTitle.setText(title);
            }
            String brief = dataGoodBean.getBrief();
            if (!StringUtil.isEmpty(brief)) {
                textContent.setText(brief);
            }
        }
    }

    class TripHolderAll extends BaseViewHolder {

        @BindView(R.id.activity_trip_all_auto)
        LinearLayout autoLinearLayout;
        @BindView(R.id.activity_trip_all_linear)
        LinearLayout all_linear;

        @BindView(R.id.activity_trip_all_img)
        ImageView all_img;
        @BindView(R.id.activity_trip_all_one)
        ImageView all_imgOne;
        @BindView(R.id.activity_trip_all_oneTitle)
        TextView all_textOneTitle;
        @BindView(R.id.activity_trip_all_two)
        ImageView all_imgTwo;
        @BindView(R.id.activity_trip_all_twoTitle)
        TextView all_textTwoTitle;
        @BindView(R.id.activity_trip_all_three)
        ImageView all_imgThree;
        @BindView(R.id.activity_trip_all_threeTitle)
        TextView all_textThreeTitle;

        TripHolderAll(View itemView, Context context) {
            super(itemView, context);
            ButterKnife.bind(this, itemView);
        }

        public void holder(final ActivityTripBean.DataBean.DataAllBean allBean) {
            autoLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mUserId = SharedPreferencesUtils.getInstance().getString(mContext, "useId");
                    if (!StringUtil.isEmpty(mUserId) && !mUserId.equals("not find")) {
                        Intent intent = new Intent(mContext, ThemeActionActivity.class);
                        if (!StringUtil.isEmpty(allBean.getAid())) {
                            intent.putExtra("aid", allBean.getAid());
                            intent.putExtra("title", allBean.getTitle());
                        }
                        mContext.startActivity(intent);
                    } else {
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        if (!StringUtil.isEmpty(allBean.getAid())) {
                            intent.putExtra("aid", allBean.getAid());
                            intent.putExtra("title", allBean.getTitle());
                        }
                        intent.putExtra("logFlag", "activityTrip");
                        mContext.startActivity(intent);
                    }
                }
            });

            OkHttpUtils.displayImg(all_img, "/" + allBean.getPic());
            if (allBean.getInfo().size() > 0) {
                OkHttpUtils.displayImg(all_imgOne, "/" + allBean.getInfo().get(0).getB_pic());
                all_textOneTitle.setText(allBean.getInfo().get(0).getTitle());
                OkHttpUtils.displayImg(all_imgTwo, "/" + allBean.getInfo().get(1).getB_pic());
                all_textTwoTitle.setText(allBean.getInfo().get(1).getTitle());
                OkHttpUtils.displayImg(all_imgThree, "/" + allBean.getInfo().get(2).getB_pic());
                all_textThreeTitle.setText(allBean.getInfo().get(2).getTitle());
            }
        }
    }
}
