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
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMWeb;

import yc.pointer.trip.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 张继
 * 2018/4/10
 * 13:59
 * 公司:天津悦程嘉和网络科技有限公司
 * 描述: 分享的dialog
 */

public class ShareBoardDialog extends Dialog implements View.OnClickListener {
    public static final int SHARE_QQ = 0;//qq
    public static final int SHARE_QZONE = 1;//空间
    public static final int SHARE_WECHAT = 2;//微信
    public static final int SHARE_WXCIRCLE = 3;//wxcircle
    public static final int SHARE_SINA = 4;//新浪


    private RecyclerView recyclerView;
    private TextView tv_Cancel;
    private Activity context;  //上下文环境
    private List<OperationBean> operationBeanList = new ArrayList<>();

    private UMWeb mWeb;
    private ShareAction shareAction;
    private UMShareAPI mShareAPI;


    public ShareBoardDialog(Context _context, Activity activity, UMWeb web) {
        this(activity, R.style.user_default_dialog);
        shareAction = new ShareAction(activity);//分享
        mShareAPI = UMShareAPI.get(_context);
        this.mWeb = web;
    }

    private ShareBoardDialog(@NonNull Activity context, @StyleRes int themeResId) {
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
        tv_Cancel.setText("取消");
        tv_Cancel.setOnClickListener(this);
    }

    private void initData() {

        operationBeanList.clear();
        operationBeanList.add(new OperationBean("QQ", R.mipmap.qq, 0));
        operationBeanList.add(new OperationBean("空间", R.mipmap.qzone, 1));
        operationBeanList.add(new OperationBean("微信", R.mipmap.wechat, 2));
        operationBeanList.add(new OperationBean("朋友圈", R.mipmap.wxcircle, 3));
        operationBeanList.add(new OperationBean("新浪", R.mipmap.sina, 4));
        GridLayoutManager layout = new GridLayoutManager(context, 5);
        recyclerView.setLayoutManager(layout);
        GridViewAdapter adapter = new GridViewAdapter(context, operationBeanList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_shareCancel) {
            this.dismiss();
        }
    }

    class OperationBean {
        String title;
        int iconResId;
        int type;

        public OperationBean(String title, int iconResId, int type) {
            this.title = title;
            this.iconResId = iconResId;
            this.type = type;
        }
    }

    class GridViewAdapter extends RecyclerView.Adapter<GridViewAdapter.ViewHolder> {
        private List<OperationBean> mOperationBeanList;
        private LayoutInflater mLayoutInflater;


        public GridViewAdapter(Context context, List<OperationBean> operationBeanList) {
            this.mOperationBeanList = operationBeanList;
            this.mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mLayoutInflater.inflate(R.layout.share_item_girdview, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bingHolder(mOperationBeanList.get(position));
        }

        @Override
        public int getItemCount() {
            return mOperationBeanList.size() == 0 ? 0 : mOperationBeanList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tv_shareGridItem)
            TextView textView;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            private void bingHolder(final OperationBean bean) {

                if (bean != null) {
                    textView.setText(bean.title);
                    Drawable dwTop = context.getResources().getDrawable(bean.iconResId);
                    dwTop.setBounds(0, 0, dwTop.getMinimumWidth(), dwTop.getMinimumHeight());
                    textView.setCompoundDrawables(null, dwTop, null, null);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switch (bean.type) {
                                case SHARE_QQ:
                                    boolean isHaveQQ = mShareAPI.isInstall(context, SHARE_MEDIA.QQ);
                                    if (isHaveQQ) {
                                        shareOperation(bean.type);
                                    } else {
                                        Toast.makeText(context, "亲，您尚未安装QQ客户端，无法分享", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case SHARE_QZONE:
                                    boolean QZONE = mShareAPI.isInstall(context, SHARE_MEDIA.QQ);
                                    if (QZONE) {
                                        shareOperation(bean.type);
                                    } else {
                                        Toast.makeText(context, "亲，您尚未安装QQ客户端，无法登录", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case SHARE_WECHAT:
                                    boolean WECHAT = mShareAPI.isInstall(context, SHARE_MEDIA.WEIXIN);
                                    if (WECHAT) {
                                        shareOperation(bean.type);
                                    } else {
                                        Toast.makeText(context, "亲，您尚未安装微信客户端，无法分享", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case SHARE_WXCIRCLE:
                                    boolean WXCIRCLE = mShareAPI.isInstall(context, SHARE_MEDIA.WEIXIN_CIRCLE);
                                    if (WXCIRCLE) {
                                        shareOperation(bean.type);
                                    } else {
                                        Toast.makeText(context, "亲，您尚未安装微信客户端，无法分享", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case SHARE_SINA:
                                    shareOperation(bean.type);
                                    break;
                                default:
                                    break;
                            }

                        }
                    });
                }
            }
        }
    }

    //后面这个方法的实现 为分享到对应的平台
    private void shareOperation(int shareType) {
        switch (shareType) {
            case SHARE_QQ:
                shareAction.setPlatform(SHARE_MEDIA.QQ)
                        .setCallback(shareListener)
                        .withMedia(mWeb)
                        .share();

                break;
            case SHARE_QZONE:
                shareAction.setPlatform(SHARE_MEDIA.QZONE)
                        .setCallback(shareListener)
                        .withMedia(mWeb)
                        .share();
                break;
            case SHARE_WECHAT:
                shareAction.setPlatform(SHARE_MEDIA.WEIXIN)
                        .setCallback(shareListener)
                        .withMedia(mWeb)
                        .share();

                break;
            case SHARE_WXCIRCLE:
                //mWeb.getMediaType().getu
                shareAction.setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                        .setCallback(shareListener)
                        .withMedia(mWeb)
                        .share();
                break;
            case SHARE_SINA:
                shareAction.setPlatform(SHARE_MEDIA.SINA)
                        .setCallback(shareListener)
                        .withMedia(mWeb)
                        .share();
                break;
            default:
                break;
        }
    }

    public interface ShareCallBack {
        void shareBook(int shareType);
    }

    private UMShareListener shareListener = new UMShareListener() {
        /**
         * @descrption 分享开始的回调
         * @param platform 平台类型
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {
            dismiss();
        }

        /**
         * @descrption 分享成功的回调
         * @param platform 平台类型
         */
        @Override
        public void onResult(SHARE_MEDIA platform) {
            dismiss();
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            show();
            Toast.makeText(context, "失败" + t.getMessage(), Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            show();

        }
    };
}
