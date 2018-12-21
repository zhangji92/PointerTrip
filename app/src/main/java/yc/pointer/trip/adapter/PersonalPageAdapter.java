package yc.pointer.trip.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.activity.FansActivity;
import yc.pointer.trip.activity.FollowActivity;
import yc.pointer.trip.bean.PersonalPageBean;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.untils.DensityUtil;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.CustomCircleImage;

/**
 * Created by 张继
 * 2018/1/3
 * 14:52
 */

public class PersonalPageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int TYPE_ONE = 0;
    private final static int TYPE_ALL = 1;

    private List<PersonalPageBean.DataBean> mList;
    private PersonalPageBean.UserBean mUserBean;
    private String mUserId;
    private PersonalPageOnCallBack mPersonalPageOnCallBack;
    private List<Integer> mHeight = new ArrayList<>();
    private final int screenWidth;

    /**
     * @param mList
     */
    public void setList(List<PersonalPageBean.DataBean> mList, boolean flagClear) {
        this.mList = mList;
        if (flagClear) {
            mHeight.clear();
            CalculationHeight(0, mList);
        } else {
            CalculationHeight(mHeight.size(), mList);
        }
    }

    private void CalculationHeight(int startIndex, List<PersonalPageBean.DataBean> mList) {
        for (int i = startIndex; i < mList.size(); i++) {
            double bicWidth = Double.valueOf(mList.get(i).getWidth());
            double bicHeight = Double.valueOf(mList.get(i).getHeight());
            double than = bicHeight / bicWidth;
            double height = screenWidth / 2 * than - 10;
            mHeight.add((int) height);
            Log.e("aa","height:"+mHeight.get(i));
        }
    }

    public void setUserBean(PersonalPageBean.UserBean mUserBean) {
        this.mUserBean = mUserBean;
    }

    public void setUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public PersonalPageAdapter(PersonalPageOnCallBack mPersonalPageOnCallBack, Context context) {
        this.mPersonalPageOnCallBack = mPersonalPageOnCallBack;
        screenWidth = DensityUtil.getScreenWidth(context);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ONE) {
            View view = View.inflate(parent.getContext(), R.layout.adapter_personal_page_head, null);
            return new HeadViewHolder(view, parent.getContext());
        } else {
            View view = View.inflate(parent.getContext(), R.layout.adapter_personal_page_all, null);
            return new ALLViewHolder(view, parent.getContext());
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == TYPE_ONE) {
            if (mUserBean != null) {
                ((HeadViewHolder) holder).bindView(mUserBean);
            }
        } else if (getItemViewType(position) == TYPE_ALL) {
            if (mList.size() > 0) {
                ((ALLViewHolder) holder).bindViewHolder(mList.get(position - 1), mHeight.get(position - 1));
            }
        }
    }


    @Override
    public int getItemCount() {
        return mList.size() == 0 ? 1 : mList.size() + 1;

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_ONE;
        } else {
            return TYPE_ALL;
        }
    }


    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams != null
                && layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
            p.setFullSpan(holder.getLayoutPosition() == 0);
        }
    }


    public class HeadViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.personal_head_back)
        ImageView personalHeadBack;//背景
        @BindView(R.id.personal_head_head)
        CustomCircleImage personalHeadHead;//头像
        @BindView(R.id.personal_head_sex)
        ImageView personalHeadSex;//性别
        @BindView(R.id.personal_head_edit)
        ImageView personalHeadEdit;//编辑
        @BindView(R.id.personal_head_nick)
        TextView personalHeadNick;
        @BindView(R.id.personal_head_autograph)
        TextView personalHeadAutograph;
        @BindView(R.id.personal_head_local)
        TextView personalHeadLocal;
        @BindView(R.id.personal_head_follow)
        TextView personalHeadFollow;//关注
        @BindView(R.id.personal_head_fans)
        TextView personalHeadFans;//粉丝
        private final Context mContext;
        private int height = 0;

        public HeadViewHolder(View itemView, Context context) {
            super(itemView);
            mContext = context;
            ButterKnife.bind(this, itemView);


        }

        void bindView(final PersonalPageBean.UserBean userBean) {
            if (!StringUtil.isEmpty(mUserId) && mUserId.equals(userBean.getUid())) {
                personalHeadEdit.setImageResource(R.mipmap.personal_page_edit);
            } else {
                if (userBean.getC_status().equals("1")) {//1关注 0取消关注
                    personalHeadEdit.setImageResource(R.mipmap.personal_page_follow_yes);
                } else {
                    personalHeadEdit.setImageResource(R.mipmap.personal_page_follow_no);
                }
            }
            personalHeadEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPersonalPageOnCallBack != null) {
                        mPersonalPageOnCallBack.onClickFollow(userBean.getUid(), personalHeadEdit, personalHeadFans);
                    }
                }
            });
            //头像
            OkHttpUtils.displayAdvertImg(personalHeadHead, userBean.getPic());
            if (userBean.getSex().equals("男")) {
                personalHeadSex.setImageResource(R.mipmap.make_man);
            } else if (userBean.getSex().equals("女")) {
                personalHeadSex.setImageResource(R.mipmap.make_woman);
            }
            personalHeadAutograph.setText(userBean.getSig());//个性签名
            String text = personalHeadAutograph.getText().toString();
            height = personalHeadAutograph.getMeasuredHeight();//获文本高度
            if (StringUtil.isEmpty(text)) {
                personalHeadAutograph.setVisibility(View.GONE);
            }

            ViewTreeObserver observer = personalHeadAutograph.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    personalHeadAutograph.getViewTreeObserver().removeGlobalOnLayoutListener(this);//避免重复监听
                    height = personalHeadAutograph.getMeasuredHeight();//获文本高度
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) personalHeadBack.getLayoutParams();
                    int heightBack = 0;
                    if (screenWidth <= 480) {
                        heightBack = 480;
                        heightBack += height;
                    } else if (screenWidth <= 720) {
                        heightBack = 600;
                        heightBack += height;
                    } else if(screenWidth>720&&screenWidth<=1080) {
                        heightBack = 980;
                        heightBack += height;
                    }else {
                        heightBack = 1080;
                        heightBack += height;
                    }
                    layoutParams.height = heightBack;
                    layoutParams.width = screenWidth;
                    personalHeadBack.setLayoutParams(layoutParams);
                    OkHttpUtils.displayGlideVague(mContext, personalHeadBack, userBean.getPic());
                }
            });
            //昵称
            if (!StringUtil.isEmpty(userBean.getNickname())) {
                personalHeadNick.setText(userBean.getNickname());
            } else {
                personalHeadNick.setText("");
            }
            //地址
            if (!StringUtil.isEmpty(userBean.getLocation())) {
                personalHeadLocal.setText(userBean.getLocation());
            } else {
                personalHeadLocal.setText("火星");
            }
            //关注数
            if (!StringUtil.isEmpty(userBean.getAtt())) {
                personalHeadFollow.setText("关注  " + userBean.getAtt());
            } else {
                personalHeadFollow.setText("关注  0");
            }
            //粉丝数
            if (!StringUtil.isEmpty(userBean.getFans())) {
                personalHeadFans.setText("粉丝  " + userBean.getFans());
            } else {
                personalHeadFans.setText("粉丝  0");
            }
            //粉丝
            personalHeadFans.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, FansActivity.class);
                    intent.putExtra("uuid", userBean.getUid());
                    mContext.startActivity(intent);
                }
            });
            //关注
            personalHeadFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, FollowActivity.class);
                    intent.putExtra("uuid", userBean.getUid());
                    mContext.startActivity(intent);

                }
            });
        }
    }

    public class ALLViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.personal_all_img)
        ImageView imImg;
        @BindView(R.id.personal_all_fabulous)
        TextView tvFabulous;
        @BindView(R.id.personal_all_red)
        ImageView imRed;
//        @BindView(R.id.personal_all_rl)
//        RelativeLayout rlHeight;

        public ALLViewHolder(View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindViewHolder(final PersonalPageBean.DataBean dataBean, int height) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imImg.getLayoutParams();
            layoutParams.height = height;
            layoutParams.width = screenWidth;
            imImg.setLayoutParams(layoutParams);
            OkHttpUtils.displayImg(imImg, dataBean.getB_pic());
            //if (imImg.getTag() == dataBean.getB_pic()) {
            //    OkHttpUtils.displayImg(imImg, dataBean.getB_pic());
            //} else {
            //    imImg.setTag(dataBean.getB_pic());
            //    OkHttpUtils.displayImg(imImg, dataBean.getB_pic());
            //}

            if (!StringUtil.isEmpty(dataBean.getZan_num())) {
                tvFabulous.setText(dataBean.getZan_num());
            } else {
                tvFabulous.setText("");
            }
            if (!StringUtil.isEmpty(mUserId) && mUserId.equals(mUserBean.getUid())) {
                if (dataBean.getIs_hb().equals("1")) {
                    imRed.setVisibility(View.VISIBLE);
                    imRed.setImageResource(R.mipmap.img_linghongbao);
                } else if (dataBean.getIs_hb().equals("3")) {
                    imRed.setVisibility(View.VISIBLE);
                    imRed.setImageResource(R.mipmap.img_weitongguo);
                }else {
                    imRed.setVisibility(View.GONE);
                }
            } else {
                imRed.setVisibility(View.GONE);
            }

            imImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPersonalPageOnCallBack != null) {
                        mPersonalPageOnCallBack.onClickVideo(dataBean);
                    }
                }
            });
            imImg.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!StringUtil.isEmpty(mUserId) && mUserId.equals(mUserBean.getUid())) {
                        if (mPersonalPageOnCallBack != null) {
                            mPersonalPageOnCallBack.onLongClick(dataBean.getBid());
                        }
                    }
                    return true;
                }
            });
        }


    }

    public interface PersonalPageOnCallBack {
        void onClickVideo(PersonalPageBean.DataBean dataBean);//跳转详情

        void onClickFollow(String uuid, ImageView imageView, TextView textView);

        void onLongClick(String bid);


    }
}
