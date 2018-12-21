package yc.pointer.trip.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.base.BaseViewHolder;
import yc.pointer.trip.bean.CommentsListBean;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.untils.FullyLinearLayoutManager;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.CustomCircleImage;

/**
 * Created by moyan on 2018/3/28.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {


    private Context context;
    private List<CommentsListBean.ListBean> mainList;//主评论信息
    private List<CommentsListBean.ListBean.ChildBean> replyList;//回复信息
    private int mPid;

    private onCommentClickListener listener;//主评论的点击事件

    public void setListener(onCommentClickListener listener) {
        this.listener = listener;
    }

    public CommentAdapter(Context context) {
        this.context = context;
    }

    public void setMainList(List<CommentsListBean.ListBean> mainList) {
        this.mainList = mainList;
        notifyDataSetChanged();
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item_layout, parent, false);
        CommentViewHolder  commentViewHolder = new CommentViewHolder(view, context);
        return commentViewHolder;
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, final int position) {
        if (mainList.size() > 0) {
            holder.onSetViewholder(context,mainList.get(position));
            replyList=new ArrayList<>();
            replyList.addAll(mainList.get(position).getChildBeanList());
            FullyLinearLayoutManager FullyLinearLayoutManager = new FullyLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            holder.recycleReply.setLayoutManager(FullyLinearLayoutManager);

            if (replyList!=null){
                ReplyAdapter adapter = new ReplyAdapter(context, replyList);
                holder.recycleReply.setAdapter(adapter);
                adapter.setListener(new ReplyAdapter.OnReplyClickListener() {
                    @Override
                    public void onReplyClick(int pid, String nickName,int replyIndex) {
                        listener.getPid(pid,nickName,position,replyIndex);
                    }
                });
            }

            holder.itemViewComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        listener.onClickComment(position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mainList.size() == 0 ? 0 : mainList.size();
    }

    static class CommentViewHolder extends BaseViewHolder {
        @BindView(R.id.itemview_comment)
        LinearLayout itemViewComment;//itemView根布局
        @BindView(R.id.video_details_comment_head)
        CustomCircleImage videoDetailsCommentHead;
        @BindView(R.id.verify_result)
        ImageView isVIP;  //VIP认证标识
        @BindView(R.id.comment_nick)
        TextView commentNick;
        @BindView(R.id.comment_info)
        TextView commentInfo;
        @BindView(R.id.recycle_reply)
        RecyclerView recycleReply;
        @BindView(R.id.comment_time)
        TextView commentTime;

        public CommentViewHolder(View itemView, Context context) {
            super(itemView, context);
            ButterKnife.bind(this, itemView);
        }

        public void onSetViewholder(Context context,CommentsListBean.ListBean commentBean) {
            String addtime = commentBean.getAddtime();
            String nickname = commentBean.getNickname();
            String pic = commentBean.getPic();
            String info = commentBean.getInfo();
            String is_vip = commentBean.getIs_vip();
            commentNick.setText(nickname);
            commentInfo.setText(info);

            OkHttpUtils.displayGlideCircular(context,videoDetailsCommentHead,pic,isVIP,is_vip);

            String strTimeDate = StringUtil.getStrTimeTomm(addtime);
            Date date = StringUtil.getStrTimeDate(strTimeDate);
            if (strTimeDate != null) {
                String format = StringUtil.format(date);
                commentTime.setText(format);
            }
        }
    }

    public interface onCommentClickListener {
        void onClickComment(int position);
        void getPid(int pid,String nickName,int index,int replyIndex);
    }
}
