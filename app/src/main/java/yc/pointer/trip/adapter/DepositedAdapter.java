package yc.pointer.trip.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import yc.pointer.trip.R;
import yc.pointer.trip.base.BaseViewHolder;
import yc.pointer.trip.bean.DepositedBean;

/**
 * Created by moyan on 2018/1/25.
 */

public class DepositedAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int TYPE_ONE = 1;//头布局
    private static final int TYPE_NORMAL = 2;//正常数据
//    private static final int TYPE_BUTTON = 3;//退押金按钮

    private Context mContext;
    private List<DepositedBean> mList;



//    public setDepositedMoney setDepositedMoneyListener;
//
//    public backDepositMoney backDepositMoneyListener;

    public DepositedAdapter(List<DepositedBean> mList) {
        this.mList = mList;
    }

//    public void setSetDepositedMoneyListener(setDepositedMoney setDepositedMoneyListener) {
//        this.setDepositedMoneyListener = setDepositedMoneyListener;
//    }
//
//    public void setBackDepositMoneyListener(backDepositMoney backDepositMoneyListener) {
//        this.backDepositMoneyListener = backDepositMoneyListener;
//    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        if (viewType == TYPE_ONE) {//头布局
            View viewHead = LayoutInflater.from(mContext).inflate(R.layout.deposited_head_layout, parent, false);
            DepsoitedHeadViewHolder depsoitedHeadViewHolder = new DepsoitedHeadViewHolder(viewHead, mContext);
            return depsoitedHeadViewHolder;
        } else {
            View viewNormal = LayoutInflater.from(mContext).inflate(R.layout.deposited_normal_layout, parent, false);
            DepsoitedNormalViewHolder depsoitedNormalViewHolder = new DepsoitedNormalViewHolder(viewNormal, mContext);
            return depsoitedNormalViewHolder;
        }
//        else {
//            View viewButton = LayoutInflater.from(mContext).inflate(R.layout.deposited_button_layout, parent, false);
//            DepsoitedButtonViewHolder depsoitedButtonViewHolder = new DepsoitedButtonViewHolder(viewButton, mContext);
//            return depsoitedButtonViewHolder;
//        }

    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_NORMAL) {
            int iconDeposit = mList.get(position - 1).getIconDeposit();
            String title = mList.get(position - 1).getTitle();
            String introduce = mList.get(position - 1).getRightsInstduce();
            ImageView depositedImg = holder.getViewById(R.id.deposited_img);
            TextView depositedTitle = holder.getViewById(R.id.deposited_title);
            TextView depositedIntroduce = holder.getViewById(R.id.deposited_introduce);

            Glide.with(mContext)
                    .load(iconDeposit)
                    .into(depositedImg);
            depositedTitle.setText(title);
            depositedIntroduce.setText(introduce);
        }
//        else if (getItemViewType(position)==TYPE_BUTTON){
//            Button button = holder.getViewById(R.id.deposited_button);
////            TextView money = holder.getViewById(R.id.deposited_money);
//     holder.setText(R.id.deposited_money,setDepositedMoneyListener.setMoney());
//     button.setOnClickListener(new View.OnClickListener() {
//         @Override
//         public void onClick(View v) {
//             backDepositMoneyListener.backMoney();
//         }
//     });
//        }


    }

    @Override
    public int getItemCount() {

        return mList.size() == 0 ? 0 : mList.size() + 1;

    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            return TYPE_ONE;
        }else {
            return TYPE_NORMAL;
        }
//        else if (position == mList.size() + 1) {
//            return TYPE_BUTTON;
//        }

    }


    public class DepsoitedHeadViewHolder extends BaseViewHolder {

        public DepsoitedHeadViewHolder(View itemView, Context context) {
            super(itemView, context);
            ButterKnife.bind(this, itemView);
        }

    }

    public class DepsoitedNormalViewHolder extends BaseViewHolder {

        @BindView(R.id.deposited_img)
        ImageView depositedImg;
        @BindView(R.id.deposited_title)
        TextView depositedTitle;
        @BindView(R.id.deposited_introduce)
        TextView depositedIntroduce;

        public DepsoitedNormalViewHolder(View itemView, Context context) {
            super(itemView, context);
            ButterKnife.bind(this, itemView);

        }

    }

//    public class DepsoitedButtonViewHolder extends BaseViewHolder {
//
//        @BindView(R.id.deposited_button)
//        Button depositedButton;
//        @BindView(R.id.deposited_money)
//        TextView depositedMoney;
//
//        public DepsoitedButtonViewHolder(View itemView, Context context) {
//            super(itemView, context);
//            ButterKnife.bind(this, itemView);
//            //屏幕适配
//            AutoUtils.autoSize(itemView);
//        }
//
//
//    }

//    public interface setDepositedMoney {
//        String setMoney();
//    }
//
//    public interface backDepositMoney {
//        void backMoney();
//    }


}
