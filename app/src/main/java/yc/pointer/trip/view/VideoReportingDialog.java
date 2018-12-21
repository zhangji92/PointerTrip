package yc.pointer.trip.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.media.UMWeb;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.activity.LoginActivity;
import yc.pointer.trip.untils.StringUtil;

/**
 * Created by 张继
 * 2018/4/10
 * 13:59
 * 公司:天津悦程嘉和网络科技有限公司
 * 描述: 视频举报dialog
 */

public class VideoReportingDialog extends Dialog implements View.OnClickListener {


    private RecyclerView recyclerView;
    private TextView tv_Cancel;
    private Activity context;  //上下文环境
    private List<OperationBean> operationBeanList = new ArrayList<>();

    public VideoReportingDialog(Activity activity) {
        this(activity, R.style.user_default_dialog);
    }

    private VideoReportingDialog(@NonNull Activity context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.context = context;
        initView();
        initData();
    }

    private void initView() {
        Window window = this.getWindow();
        //设置dialog在屏幕底部
        window.setGravity(Gravity.BOTTOM);
        //设置dialog弹出时的动画效果，从屏幕底部向上弹出
        window.setWindowAnimations(R.style.main_menu_animstyle);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.setContentView(R.layout.share_dialog_layout);
        recyclerView = (RecyclerView) findViewById(R.id.share_recyclerView);
        tv_Cancel = (TextView) window.findViewById(R.id.tv_shareCancel);
        tv_Cancel.setText("完成");
        tv_Cancel.setOnClickListener(this);
    }

    private void initData() {
        operationBeanList.clear();
        operationBeanList.add(new OperationBean("1.\t低俗色情"));
        operationBeanList.add(new OperationBean("2.\t侵权原因（抄袭等问题）"));
        operationBeanList.add(new OperationBean("3.\t标题与内容不符"));
        operationBeanList.add(new OperationBean("4.\t播放问题"));
        operationBeanList.add(new OperationBean("5.\t内容不实"));
        operationBeanList.add(new OperationBean("6.\t涉嫌违法犯罪"));
        operationBeanList.add(new OperationBean("7.\t其他问题"));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        VideoReportingAdapter adapter = new VideoReportingAdapter(context, operationBeanList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_shareCancel) {
            StringBuffer stringBuffer = new StringBuffer();
            for (OperationBean bean : operationBeanList) {
                if (bean.isFlag()) {
                    stringBuffer.append(bean.getTitle() + ",");
                }
            }
            if (!StringUtil.isEmpty(stringBuffer.toString())) {
                String substring = stringBuffer.substring(0, stringBuffer.length() - 1);
                if (mVideoReportingCallBack != null) {
                    mVideoReportingCallBack.VideoReport(substring);
                }
            } else {
                Toast.makeText(context, "尚未选择举报信息", Toast.LENGTH_SHORT).show();
            }

            dismiss();
        }
    }

    class OperationBean {
        String title;
        boolean flag = false;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public boolean isFlag() {
            return flag;
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }

        OperationBean(String title) {
            this.title = title;
        }
    }

    class VideoReportingAdapter extends RecyclerView.Adapter<VideoReportingAdapter.ViewHolder> {
        private List<OperationBean> mOperationBeanList;
        private LayoutInflater mLayoutInflater;


        public VideoReportingAdapter(Context context, List<OperationBean> operationBeanList) {
            this.mOperationBeanList = operationBeanList;
            this.mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public VideoReportingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mLayoutInflater.inflate(R.layout.view_reporting_adapter, null);
            return new VideoReportingAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(VideoReportingAdapter.ViewHolder holder, int position) {
            holder.bingHolder(mOperationBeanList.get(position));
        }

        @Override
        public int getItemCount() {
            return mOperationBeanList.size() == 0 ? 0 : mOperationBeanList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.view_repoting_title)
            TextView mTvTitle;//内容
            @BindView(R.id.view_repoting_img)
            ImageView mImgReport;//选则图标
            @BindView(R.id.view_repoting_relative)
            RelativeLayout mRlReport;//点击事件

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            void bingHolder(final OperationBean bean) {
                if (bean != null) {
                    if (!bean.isFlag()) {//未选中
                        mImgReport.setImageResource(R.mipmap.icon_uncheck_sy);
                    } else {//选中
                        mImgReport.setImageResource(R.mipmap.icon_selected_sy);
                    }
                    mTvTitle.setText(bean.title);

                    mRlReport.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!bean.isFlag()) {//选中
                                bean.setFlag(true);
                                mImgReport.setImageResource(R.mipmap.icon_selected_sy);
                            } else {//未选中
                                bean.setFlag(false);
                                mImgReport.setImageResource(R.mipmap.icon_uncheck_sy);
                            }
                        }
                    });
                }
            }
        }
    }

    private VideoReportingCallBack mVideoReportingCallBack;

    public VideoReportingDialog setVideoReportingCallBack(VideoReportingCallBack mVideoReportingCallBack) {
        this.mVideoReportingCallBack = mVideoReportingCallBack;
        return this;
    }

    public interface VideoReportingCallBack {
        void VideoReport(String info);
    }
}
