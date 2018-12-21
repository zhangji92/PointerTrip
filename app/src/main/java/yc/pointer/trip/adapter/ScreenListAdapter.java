package yc.pointer.trip.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shuyu.gsyvideoplayer.utils.GSYVideoType;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.bean.BookBean;
import yc.pointer.trip.bean.DataGoodBean;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.CustomCircleImage;
import yc.pointer.trip.view.EmptyHomeVideo;

/**
 * Created by 张继
 * 2018/12/11
 * 13:33
 * 公司：
 * 描述：滑动一屏首页item
 */

public class ScreenListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private int widthPixels;
    LayoutInflater mInflater;
    private List<DataGoodBean> mList = new ArrayList<>();
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    //上拉加载更多
    public static final int PULLUP_LOAD_MORE = 0;
    //正在加载中
    public static final int LOADING_MORE = 1;
    //没有加载更多 隐藏
    public static final int NO_LOAD_MORE = 2;
    //上拉加载更多状态-默认为0
    private int mLoadMoreStatus = 0;
    private Context mContext;
    private int heightPixels;
    private ScreenCallBack mScreenCallBack;


    public ScreenListAdapter(Context context, List<DataGoodBean> datas, ScreenCallBack screenCallBack) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mList.addAll(datas);
        this.mScreenCallBack = screenCallBack;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View itemView = mInflater.inflate(R.layout.adapter_screen_list, parent, false);

            return new ItemViewHolder(itemView);
        } else if (viewType == TYPE_FOOTER) {
            View itemView = mInflater.inflate(R.layout.load_more_footview_layout, parent, false);

            return new FooterViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ItemViewHolder) {
            if (mList.size() > 0) {
                ((ItemViewHolder) holder).setData(position);
            }
        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            switch (mLoadMoreStatus) {
                case PULLUP_LOAD_MORE:
                    //隐藏加载更多
                    footerViewHolder.mTvLoadText.setText("上拉加载...");
                    break;
                case LOADING_MORE:
                    //隐藏加载更多
                    footerViewHolder.mTvLoadText.setText("正加载中...");
                    break;
                case NO_LOAD_MORE://隐藏加载更多
                    footerViewHolder.mTvLoadText.setText("没有更多了...");
                    break;
            }
        }

    }

    @Override
    public int getItemCount() {
        //RecyclerView的count设置为数据总条数+ 1（footerView）
        return mList.size() == 0 ? 1 : mList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            //最后一个item设置为footerView
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.screen_video)
        public EmptyHomeVideo mEmptyHomeVideo;//视频
        @BindView(R.id.screen_head)
        CustomCircleImage mCustomCircleImage;//头像
        @BindView(R.id.screen_vip)
        ImageView mVip;//会员
        @BindView(R.id.screen_nick)
        TextView mNickName;//昵称
        @BindView(R.id.screen_follow)
        TextView mFollow;//加关注
        @BindView(R.id.screen_trip)
        TextView mTrip;//旅游数
        @BindView(R.id.screen_money)
        TextView mMoney;//钱数
        @BindView(R.id.screen_title)
        TextView mTitle;//标题
        @BindView(R.id.screen_info)
        TextView mInfo;//内容
        @BindView(R.id.screen_local)
        TextView mLocal;//地址
        @BindView(R.id.screen_review)
        TextView mReview;//评论
        @BindView(R.id.screen_like)
        TextView mLike;//点赞
        @BindView(R.id.screen_forward)
        TextView mForward;//转发
        ImageView mCoverImg;
        private DataGoodBean dataGoodBean;
        private String mUserId;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mCoverImg = new ImageView(mContext);
            ButterKnife.bind(this, itemView);
            DisplayMetrics dm = mContext.getApplicationContext().getResources().getDisplayMetrics();
            widthPixels = dm.widthPixels;
            heightPixels = dm.heightPixels;
            initListener();
        }

        private void initListener() {
            //隐藏增加title
            mEmptyHomeVideo.getTitleTextView().setVisibility(View.GONE);
            //隐藏设置返回键
            mEmptyHomeVideo.getBackButton().setVisibility(View.GONE);
            mEmptyHomeVideo.setThumbImageView(mCoverImg);
            mCustomCircleImage.setOnClickListener(this);//头像点击
            mFollow.setOnClickListener(this);//关注
            mForward.setOnClickListener(this);//转发
            mReview.setOnClickListener(this);//评论
            mLike.setOnClickListener(this);//点赞
        }

        private void setData(final int position) {
            double width = Double.valueOf(mList.get(position).getWidth());
            double height = Double.valueOf(mList.get(position).getHeight());
            double than = height / width;
            final double paramsHeight = widthPixels * than;
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mEmptyHomeVideo.getLayoutParams();
            if (than > 1) {//竖屏
                mCoverImg.setScaleType(ImageView.ScaleType.FIT_XY);
                layoutParams.height = heightPixels;
                mEmptyHomeVideo.setLayoutParams(layoutParams);
            } else {//横屏
                mCoverImg.setScaleType(ImageView.ScaleType.FIT_START);
                layoutParams.height = (int) paramsHeight;
                mEmptyHomeVideo.setLayoutParams(layoutParams);
            }
            GSYVideoType.setShowType(GSYVideoType.SCREEN_MATCH_FULL);
            String nickname = mList.get(position).getNickname();
            String is_vip = mList.get(position).getIs_vip();
            String zan_num = mList.get(position).getZan_num();//点赞
            String s_num = mList.get(position).getS_num();//转发数
            String c_num = mList.get(position).getC_num();//评论
            String title = mList.get(position).getTitle();
            String info = mList.get(position).getInfo();
            String location = mList.get(position).getLocation();
            String book_num = mList.get(position).getBook_num();
            String u_money = mList.get(position).getU_money();
            mUserId = MyApplication.mApp.getUserId();
            OkHttpUtils.displayGlide(mContext, mCoverImg, mList.get(position).getB_pic());
            OkHttpUtils.displayGlideCircular(mContext, mCustomCircleImage, mList.get(position).getPic(), mVip, is_vip);
            mNickName.setText(nickname);
            mTitle.setText(title);
            mInfo.setText(info);
            if (StringUtil.isEmpty(location)) {
                mLocal.setVisibility(View.INVISIBLE);
            } else {
                mLocal.setVisibility(View.VISIBLE);
                mLocal.setText(location);
            }
            String tripNum = StringUtil.conversion(Integer.valueOf(book_num), 2);//如果超过一万，转化成1.24w
            mTrip.setText("游记 " + tripNum);
            mMoney.setText("收益 ￥" + u_money);
            //转发
            String forward = StringUtil.conversion(Integer.valueOf(s_num), 2);//如果超过一万，转化成1.24w
            mForward.setText(forward);
            //评论
            String review = StringUtil.conversion(Integer.valueOf(c_num), 2);//如果超过一万，转化成1.24w
            mReview.setText(review);
            //点赞
            String like = StringUtil.conversion(Integer.valueOf(zan_num), 2);//如果超过一万，转化成1.24w
            mLike.setText(like);
            mEmptyHomeVideo.setUp(mList.get(position).getVideo(), true, "");
            if (position == 0) {
                mEmptyHomeVideo.startPlayLogic();
            }
            if (!StringUtil.isEmpty(mUserId)) {
                if (mList.get(position).getUid().equals(mUserId)) {
                    mFollow.setVisibility(View.GONE);
                } else {
                    mFollow.setVisibility(View.VISIBLE);
                    if (mList.get(position).getF_status().equals("1")) {//已关注
                        mFollow.setText("已关注");
                    } else {//未关注
                        mFollow.setText("+关注");
                    }
                }
            } else {
                mFollow.setVisibility(View.VISIBLE);
                mFollow.setText("+关注");
            }
            mFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mScreenCallBack.clickAttention(mUserId, position, mFollow);
                }
            });
            mLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mScreenCallBack.clickLike(mUserId, position, mLike);
                }
            });
            dataGoodBean = mList.get(position);
        }


        @Override
        public void onClick(View v) {
            if (mScreenCallBack != null) {
                switch (v.getId()) {
                    case R.id.screen_head://头像点击
                        mScreenCallBack.clickHead(mUserId);
                        break;
                    case R.id.screen_review://评论
                        mScreenCallBack.clickComment(dataGoodBean);
                        break;
                    case R.id.screen_forward://转发
                        mScreenCallBack.clickForward(dataGoodBean);
                        break;
                }
            }
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.pbLoad)
        ProgressBar mPbLoad;
        @BindView(R.id.tvLoadText)
        TextView mTvLoadText;
        @BindView(R.id.loadLayout)
        LinearLayout mLoadLayout;

        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void AddHeaderItem(List<DataGoodBean> items) {
        mList.clear();
        mList.addAll(0, items);
        notifyDataSetChanged();
    }

    public void AddFooterItem(List<DataGoodBean> items) {
        int size = mList.size();
        mList.addAll(items);
        notifyItemRangeChanged(size, mList.size());
    }

    /**
     * 更新加载更多状态
     *
     * @param status
     */
    public void changeMoreStatus(int status) {
        mLoadMoreStatus = status;
        notifyDataSetChanged();
    }

    public interface ScreenCallBack {
        /**
         * 点击头像进个人主页
         *
         * @param userId 用户id
         */
        void clickHead(String userId);

        /**
         * 加关注
         *
         * @param userId 用户id
         */
        void clickAttention(String userId, int position, TextView textView);

        /**
         * 评论
         *
         * @param
         */
        void clickComment(BookBean bookBean);

        /**
         * 点赞
         *
         * @param userId 用户id
         */
        void clickLike(String userId, int position, TextView textView);

        /**
         * 转发
         *
         * @param bookBean 路数信息
         */
        void clickForward(BookBean bookBean);


    }

}
