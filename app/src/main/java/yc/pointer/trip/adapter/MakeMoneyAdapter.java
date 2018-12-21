package yc.pointer.trip.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.bean.MakeMoneyBean;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.CustomCircleImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 刘佳伟
 * 2017/7/18
 * 10:26
 * 赚一赚列表适配器
 */
public class MakeMoneyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int TYP_ONE = 1;
    private static final int TYP_ALL = 2;
    private itemViewOnClickListener itemViewOnClickListener;
    private List<MakeMoneyBean.DataBean> mBeanList;

    public MakeMoneyAdapter(List<MakeMoneyBean.DataBean> mBeanList) {
        this.mBeanList = mBeanList;
    }

    public void setItemViewOnClickListener(itemViewOnClickListener itemViewOnClickListener) {
        this.itemViewOnClickListener = itemViewOnClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYP_ONE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragemtn_make_head, parent, false);
            ViewHolderHead holder = new ViewHolderHead(view);
            return holder;
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_makemoney, parent, false);
            ViewHolderMsg holder = new ViewHolderMsg(view);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        int viewType = getItemViewType(position);
        if (viewType == TYP_ONE) {

            //((ViewHolderHead) holder).imgHead
        } else {
            if (mBeanList.size() > 0) {
                ((ViewHolderMsg) holder).itemview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemViewOnClickListener.OnClickBack(mBeanList.get(position - 1).getOid());
                    }
                });
                ((ViewHolderMsg) holder).grabOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemViewOnClickListener.OnClickBack(mBeanList.get(position - 1).getOid());
                    }
                });
                ((ViewHolderMsg) holder).bindHolder(mBeanList.get(position - 1));
            }
        }
    }


    @Override
    public int getItemCount() {
        return mBeanList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYP_ONE;
        } else {
            return TYP_ALL;
        }
    }

    public class ViewHolderHead extends RecyclerView.ViewHolder {
        @BindView(R.id.make_head_back)
        ImageView imgHead;

        public ViewHolderHead(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class ViewHolderMsg extends RecyclerView.ViewHolder {

        @BindView(R.id.publish_nickname)
        TextView publishNickname;//发单人昵称
        @BindView(R.id.itemview)
        CardView itemview;
        @BindView(R.id.grab_order)
        Button grabOrder;
        @BindView(R.id.grad_sex)
        ImageView imageViewSex;//是否是男女
        @BindView(R.id.grad_header_img)
        CustomCircleImage mHeadImg;//头像
        @BindView(R.id.make_end_city)
        TextView mEndCity;//目的地
        @BindView(R.id.make_date)
        TextView mDate;//出行时间
        @BindView(R.id.grab_scenic_text)
        TextView mScenic;//景点
        @BindView(R.id.make_money)
        TextView mMoney;//价钱

        public ViewHolderMsg(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
//            AutoUtils.auto(itemView);
        }

        private void bindHolder(MakeMoneyBean.DataBean bean) {
            if (bean != null) {
                String sex = bean.getSex();
                if (!StringUtil.isEmpty(sex)) {
                    if (sex.equals("男")) {
                        imageViewSex.setImageResource(R.mipmap.make_man);
                    } else if (sex.equals("女")) {
                        imageViewSex.setImageResource(R.mipmap.make_woman);
                    }
                } else {
                    imageViewSex.setImageResource(R.mipmap.make_man);
                }
                String nickname = bean.getNickname();
                if (!StringUtil.isEmpty(nickname)){
                    publishNickname.setText(nickname);
                }else {
                    publishNickname.setText("");
                }
                OkHttpUtils.displayImg(mHeadImg, bean.getPic());
                mEndCity.setText(bean.getMaddress());
                mDate.setText(bean.getSend_time());
                String spot = bean.getSpot();
                if(spot.equals("不限")){
                    mScenic.setText("景点："+spot);
                }else {
                    mScenic.setText(spot);
                }

                mMoney.setText(bean.getMoney_y());
            }
        }
    }

    public interface itemViewOnClickListener {
        void OnClickBack(String oid);
    }
}
