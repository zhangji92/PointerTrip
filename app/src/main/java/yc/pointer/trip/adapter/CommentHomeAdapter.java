package yc.pointer.trip.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.base.BaseViewHolder;
import yc.pointer.trip.bean.CommentBean;

/**
 * Created by moyan on 2018/8/10.
 * s首页评论内容列表
 */

public class CommentHomeAdapter extends RecyclerView.Adapter<CommentHomeAdapter.HomeCommentViewHolder> {

    private Context mContext;
    private List<CommentBean> mCommentList = new ArrayList<>();

    public CommentHomeAdapter(List<CommentBean> comment) {
        this.mCommentList = comment;
    }

    @Override
    public HomeCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View itemView = null;
        HomeCommentViewHolder holder = null;
        if (itemView == null) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.home_comment_item, parent, false);
            holder = new HomeCommentViewHolder(itemView, mContext);
            itemView.setTag(holder);
        } else {
            holder = (HomeCommentViewHolder) itemView.getTag();
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(HomeCommentViewHolder holder, int position) {

         if (mCommentList.size() >= 2&&position==2){
            holder.commentContent.setText("查看剩余评论");
            holder.commentContent.setTextColor(Color.parseColor("#45b0ff"));
        }else {
             String c_info = mCommentList.get(position).getC_info();
             String c_nickname = mCommentList.get(position).getC_nickname();
             holder.commentContent.setText(c_nickname+": "+c_info);
         }

    }


    @Override
    public int getItemCount() {
        int size = mCommentList.size();
        return size == 0 ? 0 : size >= 2 ? 3 : size;
    }

    static class HomeCommentViewHolder extends BaseViewHolder {
        @BindView(R.id.comment_content)
        TextView commentContent;

        public HomeCommentViewHolder(View itemView, Context context) {
            super(itemView, context);
            ButterKnife.bind(this, itemView);
        }

    }


}
