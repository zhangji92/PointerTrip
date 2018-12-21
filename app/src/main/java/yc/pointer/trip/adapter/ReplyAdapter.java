package yc.pointer.trip.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.base.BaseViewHolder;
import yc.pointer.trip.bean.CommentsListBean;

/**
 * Created by moyan on 2018/3/29.
 * 回复列表适配器
 */

class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder> {


    private Context context;
    private List<CommentsListBean.ListBean.ChildBean> replyList;
    private SpannableString spannableString;

    private OnReplyClickListener listener;

    public void setListener(OnReplyClickListener listener) {
        this.listener = listener;
    }

    public ReplyAdapter(Context context, List<CommentsListBean.ListBean.ChildBean> replyList) {

        this.context = context;
        this.replyList = replyList;

    }


    @Override
    public ReplyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        ReplyViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.reply_item_layout, parent, false);
            holder = new ReplyViewHolder(view, context);
            view.setTag(holder);
        } else {
            holder = (ReplyViewHolder) view.getTag();
        }


        return holder;

    }

    @Override
    public void onBindViewHolder(ReplyViewHolder holder, final int position) {
        CommentsListBean.ListBean.ChildBean childBean = replyList.get(position);
        String nickname = childBean.getNickname();
        String b_nickname = childBean.getB_nickname();
        String info = childBean.getInfo();
        //用来标识在 Span 范围内的文本前后输入新的字符时是否把它们也应用这个效果
        //Spanned.SPAN_EXCLUSIVE_EXCLUSIVE(前后都不包括)
        //Spanned.SPAN_INCLUSIVE_EXCLUSIVE(前面包括，后面不包括)
        //Spanned.SPAN_EXCLUSIVE_INCLUSIVE(前面不包括，后面包括)
        //Spanned.SPAN_INCLUSIVE_INCLUSIVE(前后都包括)
        spannableString = new SpannableString(nickname + "回复" + b_nickname + ":" + info);
        //为回复的人昵称添加点击事件
        spannableString.setSpan(new TextSpanClick("1"), 0,
                nickname.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //为评论的人的添加点击事件
        spannableString.setSpan(new TextSpanClick("2"), nickname.length() + 2,
                nickname.length() + b_nickname.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //设置回复人昵称颜色
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#0cd4f6")),0,nickname.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        设置被回复人昵称颜色
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#0cd4f6")), nickname.length() + 2, nickname.length() + b_nickname.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.replyContent.setText(spannableString);

        //添加点击事件时，必须设置
        holder.replyContent.setMovementMethod(LinkMovementMethod.getInstance());
        holder.replyContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null){
                    int cid =Integer.valueOf(replyList.get(position).getCid());
                    String nickname1 = replyList.get(position).getNickname();
                    listener.onReplyClick(cid,nickname1,position);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return replyList.size() == 0 ? 0 : replyList.size();
    }

    private final class TextSpanClick extends ClickableSpan {
        private String status;

        public TextSpanClick(String status) {
            this.status = status;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);//取消下划线
        }

        @Override
        public void onClick(View v) {
            String msgStr = "";
            if (status.equals("1")) {
                //TODO 跳转登录用户的个人主页
//                msgStr = "跳转登录用户的个人主页";
            } else if (status.equals("2")) {
                //TODO 跳转被回复的个人主页
//                msgStr ="跳转被回复的个人主页";
            }

//            Toast.makeText(context, msgStr, Toast.LENGTH_SHORT).show();
        }
    }

    public static class ReplyViewHolder extends BaseViewHolder {
        @BindView(R.id.reply_content)
        TextView replyContent;

        public ReplyViewHolder(View itemView, Context context) {
            super(itemView, context);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnReplyClickListener {
        void onReplyClick(int pid,String nickName,int replyIndex);
    }

}
