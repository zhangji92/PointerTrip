package yc.pointer.trip.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import yc.pointer.trip.R;
import yc.pointer.trip.activity.LoginActivity;
import yc.pointer.trip.activity.VideoDetailsActivity;
import yc.pointer.trip.adapter.CommentAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.bean.CommentsListBean;
import yc.pointer.trip.untils.DensityUtil;
import yc.pointer.trip.untils.StringUtil;

/**
 * Created by 张继
 * 2018/12/20
 * 15:54
 * 公司：
 * 描述：
 */

public class CommentPop extends PopupWindow {

    private final LayoutInflater mLayoutInflater;
    private final View mRootView;
    //private List<CommentsListBean.ListBean> mComments;//主评论列表
    private CommentAdapter commentsAdapter;
    private EditText mReply;
    private Fragment mFragment;
    private int mPid;
    private String mPnickName;
    private int mIndex;
    private int mReplyIndex;
    private boolean isComments = true;
    private CommentPopCallBack mCommentPopCallBack;
    private RecyclerView mainReview;


    public CommentPop(Context context, Fragment fragment, CommentPopCallBack callBack) {
        super(context);
        this.mCommentPopCallBack = callBack;
        this.mFragment = fragment;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = mLayoutInflater.inflate(R.layout.pop_comment, null);
        initBasis(context, fragment.getActivity());
        /**
         * 初始化View与监听器
         */
        initView(mRootView);
    }

    private void initView(final View rootView) {
        mainReview = rootView.findViewById(R.id.pop_recycler);
    }

    public void setData(final Context context, final List<CommentsListBean.ListBean> mComments) {
        LinearLayoutManager layout = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        mainReview.setLayoutManager(layout);
        commentsAdapter = new CommentAdapter(context);
        commentsAdapter.setMainList(mComments);
        mainReview.setAdapter(commentsAdapter);
        commentsAdapter.notifyDataSetChanged();
        commentsAdapter.setListener(new CommentAdapter.onCommentClickListener() {
            @Override
            public void onClickComment(int position) {
                String userNickname = MyApplication.mApp.getUserBean().getNickname();
                mPid = Integer.parseInt(mComments.get(position).getCid());
                mIndex = position;//记录列表位置
                mPnickName = mComments.get(position).getNickname();
                if (!StringUtil.isEmpty(userNickname) && !userNickname.equals(mPnickName)) {
                    //showSoftKey("回复：" + mPnickName);
                } else if (userNickname.equals(mPnickName)) {
                    //删除该评论
                    new DialogSure(context, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                        @Override
                        public void onClickListener(DialogSure dialogSure, boolean trueEnable) {
                            if (trueEnable) {
                                isComments = true;
                                if (mCommentPopCallBack != null) {
                                    mCommentPopCallBack.deleteComments(mPid, mIndex);
                                }
                            }

                        }
                    }).setTitle("确认删除该评论").setMsg("该评论被删除后相应回复也会被一并删除，确认要删除？")
                            .setNegativeButton("取消")
                            .setPositiveButton("确认")
                            .show();
                }
            }

            @Override
            public void getPid(int pid, String nickName, final int index, int replyIndex) {
                String userNickname = MyApplication.mApp.getUserBean().getNickname();
                mPid = pid;
                mPnickName = nickName;
                mIndex = index;
                mReplyIndex = replyIndex;
                if (!StringUtil.isEmpty(userNickname) && !userNickname.equals(mPnickName)) {
                    //showSoftKey("回复：" + mPnickName);
                } else if (userNickname.equals(mPnickName)) {
                    //删除该评论
                    new DialogSure(mRootView.getContext(), R.style.user_default_dialog, new DialogSure.CallBackListener() {
                        @Override
                        public void onClickListener(DialogSure dialogSure, boolean trueEnable) {
                            if (trueEnable) {
                                isComments = false;
                                if (mCommentPopCallBack != null) {
                                    mCommentPopCallBack.deleteComments(mPid, mIndex, mReplyIndex);
                                }
                            }
                        }
                    }).setTitle("确认删除").setMsg("您是否确认删除您的评论？")
                            .setNegativeButton("取消")
                            .setPositiveButton("确认")
                            .show();
                }
            }
        });
    }



    private void initBasis(Context context, final Activity activity) {
        //设置View
        setContentView(mRootView);
        int screenHeight = DensityUtil.getScreenHeight(context) - DensityUtil.getScreenHeight(context) / 3;
        //设置宽与高
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(screenHeight);
        /**
         * 设置进出动画
         */
        setAnimationStyle(R.style.main_menu_animstyle);
        /**
         * 设置背景只有设置了这个才可以点击外边和BACK消失
         */
        setBackgroundDrawable(context.getResources().getDrawable(R.color.colorTitle));

        /**
         * 设置可以获取集点
         */
        setFocusable(true);
        /**
         * 设置点击外边可以消失
         */
        setOutsideTouchable(true);
        /**
         *设置可以触摸
         */
        setTouchable(true);
        /**
         * 设置点击外部可以消失
         */
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /**
                 * 判断是不是点击了外部
                 */
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    return true;
                }
                //不是点击外部
                return false;
            }
        });
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams layoutParams = activity.getWindow().getAttributes();
                layoutParams.alpha = 1.0f;
                activity.getWindow().setAttributes(layoutParams);
            }
        });
    }

    public interface CommentPopCallBack {
        void publishComments(int pid, String info);

        void deleteComments(int pid, int index);

        void deleteComments(int pid, int index, int ReplyIndex);
    }
}
