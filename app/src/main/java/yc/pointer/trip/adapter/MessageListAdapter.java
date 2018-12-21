package yc.pointer.trip.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.activity.VideoDetailsActivity;
import yc.pointer.trip.base.BaseRecycleViewAdapter;
import yc.pointer.trip.base.BaseViewHolder;
import yc.pointer.trip.bean.MessageListBean;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.untils.StringUtil;

/**
 * Created by 张继
 * 2018/4/2
 * 15:38
 * 公司:天津悦程嘉和网络科技有限公司
 * 描述:
 */

//public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {
public class MessageListAdapter  {
    //
    //List<MessageListBean.DataBean> mList;
    //private MsgCallBack mMsgCallBack;
    //
    //public MessageListAdapter(List<MessageListBean.DataBean> mList, MsgCallBack msgCallBack) {
    //    this.mMsgCallBack = msgCallBack;
    //    this.mList = mList;
    //}
    //
    //@Override
    //public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    //    View inflate = View.inflate(parent.getContext(), R.layout.adapter_msg_list, null);
    //    return new ViewHolder(inflate, parent.getContext());
    //}
    //
    //@Override
    //public void onBindViewHolder(ViewHolder holder, int position) {
    //    if (mList.size() > 0) {
    //        holder.bind(mList.get(position));
    //    }
    //}
    //
    //@Override
    //public int getItemCount() {
    //    return mList.size() == 0 ? 0 : mList.size();
    //}
    //
    //class ViewHolder extends BaseViewHolder {
    //    @BindView(R.id.msg_adapter_header)
    //    ImageView mIvHeader;
    //    @BindView(R.id.msg_adapter_nick)
    //    TextView mTvNick;
    //    @BindView(R.id.msg_adapter_content)
    //    TextView mTvContext;
    //    @BindView(R.id.msg_adapter_time)
    //    TextView mTvTime;
    //    @BindView(R.id.msg_adapter_img)
    //    ImageView mIvImg;
    //
    //    @BindView(R.id.msg_adapter_linear)
    //    LinearLayout mllLayout;
    //
    //
    //    private Context mContent;
    //
    //    public ViewHolder(View itemView, Context context) {
    //        super(itemView, context);
    //        this.mContent = context;
    //        ButterKnife.bind(this, itemView);
    //        //屏幕适配
    //        AutoUtils.autoSize(itemView);
    //    }
    //
    //    public void bind(final MessageListBean.DataBean bean) {
    //        //头像
    //        OkHttpUtils.displayGlideCircular(mContent, mIvHeader, bean.getC_u_pic());
    //        //视频截图
    //        OkHttpUtils.displayGlide(mContent, mIvImg, bean.getB_pic());
    //        mTvNick.setText(bean.getC_nickname());//发表评论人的昵称
    //        mTvContext.setText(bean.getC_info());
    //
    //        String strTimeDate = StringUtil.getStrTimeTomm(bean.getAddtime());
    //        Date date = StringUtil.getStrTimeDate(strTimeDate);
    //        if (strTimeDate != null) {
    //            String format = StringUtil.format(date);
    //            mTvTime.setText(format);
    //        }
    //
    //        mIvImg.setOnClickListener(new View.OnClickListener() {//跳转路数详情
    //            @Override
    //            public void onClick(View view) {
    //                Intent intent = new Intent(mContent, VideoDetailsActivity.class);
    //                intent.putExtra("dataGoodBean", bean);
    //                mContent.startActivity(intent);
    //            }
    //        });
    //        mllLayout.setOnClickListener(new View.OnClickListener() {//评论回复
    //            @Override
    //            public void onClick(View view) {
    //                if (mMsgCallBack != null) {
    //                    mMsgCallBack.onClickComment(bean.getCid(),bean.getBid());
    //                }
    //            }
    //        });
    //    }
    //}
    //
    //
    //public interface MsgCallBack {
    //    void onClickComment(String cid,String bid);
    //}
}
