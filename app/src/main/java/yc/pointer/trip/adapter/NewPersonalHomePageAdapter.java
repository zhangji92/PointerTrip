package yc.pointer.trip.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseViewHolder;
import yc.pointer.trip.bean.PersonalPageBean;
import yc.pointer.trip.adapter.NewPersonalHomePageAdapter.ViewHolder;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.untils.DensityUtil;
import yc.pointer.trip.untils.StringUtil;

/**
 * Created by 张继
 * 2018/4/20
 * 14:06
 * 公司:天津悦程嘉和网络科技有限公司
 * 描述:
 */

public class NewPersonalHomePageAdapter extends RecyclerView.Adapter<ViewHolder> {

    private List<PersonalPageBean.DataBean> mList;
    private LayoutInflater mLayoutInflater;
    private String mUserId;
    private PersonalPageOnCallBack mPersonalPageOnCallBack;
    private int mScreenWidth;

    public NewPersonalHomePageAdapter(Context context, List<PersonalPageBean.DataBean> mList,
                                      PersonalPageOnCallBack personalPageOnCallBack) {
        this.mPersonalPageOnCallBack = personalPageOnCallBack;
        this.mList = mList;
        mLayoutInflater = LayoutInflater.from(context);
        mUserId = MyApplication.mApp.getUserId();
        mScreenWidth=DensityUtil.getScreenWidth(context);

    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.adapter_personal_page_all, null), parent.getContext());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mList.size() > 0) {
            double bicWidth = Double.valueOf(mList.get(position).getWidth());
            double bicHeight = Double.valueOf(mList.get(position).getHeight());
            double than = bicHeight / bicWidth;
            double height = mScreenWidth / 2 * than - 10;
            holder.bind(mList.get(position), (int) height);
        }
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    class ViewHolder extends BaseViewHolder {
        @BindView(R.id.personal_all_img)
        ImageView imImg;
        @BindView(R.id.personal_all_fabulous)
        TextView tvFabulous;
        @BindView(R.id.personal_all_red)
        ImageView imRed;
        @BindView(R.id.personal_look_num)
        TextView homePlayNum;//播放数

        public ViewHolder(View itemView, Context context) {
            super(itemView, context);
            ButterKnife.bind(this, itemView);
        }

        void bind(final PersonalPageBean.DataBean dataBean, int height) {

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imImg.getLayoutParams();
            layoutParams.height = height;
            layoutParams.width = mScreenWidth;
            imImg.setLayoutParams(layoutParams);
            OkHttpUtils.displayImg(imImg, dataBean.getB_pic());
            tvFabulous.setText(dataBean.getZan_num());
            if (!StringUtil.isEmpty(mUserId) && mUserId.equals(dataBean.getUid())) {
                if (dataBean.getIs_hb().equals("1")) {
                    imRed.setVisibility(View.VISIBLE);
                    imRed.setImageResource(R.mipmap.img_linghongbao);
                } else if (dataBean.getIs_hb().equals("3")) {
                    imRed.setVisibility(View.VISIBLE);
                    imRed.setImageResource(R.mipmap.img_weitongguo);
                } else {
                    imRed.setVisibility(View.GONE);
                }
            } else {
                imRed.setVisibility(View.GONE);
            }

            if (!StringUtil.isEmpty(dataBean.getLook_num())) {
                homePlayNum.setText(dataBean.getLook_num());//播放数
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
                    if (!StringUtil.isEmpty(mUserId) && mUserId.equals(dataBean.getUid())) {
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
        void onLongClick(String bid);
    }
}
