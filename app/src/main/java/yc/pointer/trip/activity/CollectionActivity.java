package yc.pointer.trip.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import okhttp3.FormBody;
import okhttp3.RequestBody;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import yc.pointer.trip.R;
import yc.pointer.trip.adapter.CollectionAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.BookDetailsCollectionBean;
import yc.pointer.trip.bean.CollectionBean;
import yc.pointer.trip.bean.eventbean.ReceiverBean;
import yc.pointer.trip.event.CollectionCancelFabulousEvent;
import yc.pointer.trip.event.CollectionEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.view.DialogSure;
import yc.pointer.trip.view.LoadDialog;
import yc.pointer.trip.view.ToolbarWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 张继
 * 2017/6/30
 * 17:42
 * 收藏
 */
public class CollectionActivity extends BaseActivity {
    @BindView(R.id.refresh_recycler)
    RecyclerView refreshRecycler;
    @BindView(R.id.refresh_smart)
    SmartRefreshLayout refreshSmart;//刷新
    @BindView(R.id.adapter_empty)
    TextView adapterEmpty;//文字
    @BindView(R.id.empty)
    LinearLayout collectEmpty;//布局
   @BindView(R.id.empty_img)
   ImageView imgEmpty;


    private long mTimestamp;//时间戳
    private String mDevcode;//手机识别码
    private int page = 0;
    private boolean flag = true;//true-刷新  false-加载
    private String userId;
    private List<CollectionBean.DataBean> list = new ArrayList<>();
    private CollectionAdapter collectionAdapter;
    private LoadDialog mLoadDialog;

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_collection;
    }

    @Override
    protected void initView() {
        //开启动画
        mLoadDialog = new LoadDialog(this, "正在加载...", R.drawable.load_animation);
        mLoadDialog.show();

//初始化toolbar
        new ToolbarWrapper(this).setCustomTitle(R.string.collection_title);
//获取手机识别码
        mDevcode = ((MyApplication) getApplication()).getDevcode();
//获取时间戳
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();
//获取Uid
        userId = ((MyApplication) getApplication()).getUserId();
        //获取网络数据
        getMseForNet(page);

        refreshRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        collectionAdapter = new CollectionAdapter(this, list);
        //长按点击取消收藏
        collectionAdapter.setOnLongClickListener(new CollectionAdapter.OnLongClickListener() {
            @Override
            public void itemOnLongClick(final int position) {
                //取消收藏
                String msg = "是否取消对该游记的收藏";
                new DialogSure(CollectionActivity.this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                    @Override
                    public void onClickListener(DialogSure dialogSure, boolean cancelable) {
                        if (cancelable) {
                            //网络请求方法
                            cancelCollect(list.get(position).getBid());
                        }
                    }
                }).setMsg("取消收藏？").setPositiveButton("确定").setNegativeButton("取消").show();
            }
        });
        refreshRecycler.setAdapter(collectionAdapter);
        refreshSmart.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
//                Toast.makeText(CollectionActivity.this, "加载", Toast.LENGTH_SHORT).show();
                flag = false;
                ++page;
                getMseForNet(page);
                refreshSmart.finishLoadmore();
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
//                Toast.makeText(CollectionActivity.this, "刷新", Toast.LENGTH_SHORT).show();
                refreshSmart.setLoadmoreFinished(false);
                flag = true;
                page = 0;
                getMseForNet(page);
                refreshSmart.finishRefresh();
            }
        });

    }

    //取消点赞网络接口
    private void cancelCollect(String bid) {
        boolean timeFlag = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
        RequestBody requestBody = null;
        if (timeFlag) {
            String sign = Md5Utils.createMD5("bid=" + bid + "devcode=" + mDevcode + "status=" + 0 + "timestamp=" + mTimestamp + "uid=" + userId + URLUtils.WK_APP_KEY);
            requestBody = new FormBody.Builder()
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", String.valueOf(userId))
                    .add("bid", String.valueOf(bid))
                    .add("signature", sign)
                    .add("devcode", mDevcode)
                    .add("status", String.valueOf(0))
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.BOOK_COLLECTION, requestBody, new HttpCallBack(new CollectionCancelFabulousEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void canaelCollect(CollectionCancelFabulousEvent event) {
        if (event.isTimeOut()) {
                            Toast.makeText(this, "网络异常，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }
        BookDetailsCollectionBean bean = event.getData();
        if (bean.getStatus() == 0) {
            if (bean.getC_status().equals("0")) {
                list.clear();
                page = 0;
                getMseForNet(page);
            } else {
                Toast.makeText(this, "您未能取消对该游记的收藏", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

    //获取网络数据
    public void getMseForNet(int page) {
        String signture = Md5Utils.createMD5("devcode=" + mDevcode + "p=" + page + "timestamp=" + mTimestamp + "uid=" + userId + URLUtils.WK_APP_KEY);
        RequestBody requestBody = new FormBody.Builder()
                .add("devcode", mDevcode)
                .add("timestamp", String.valueOf(mTimestamp))
                .add("p", String.valueOf(page))
                .add("uid", userId)
                .add("signature", signture)
                .build();
        OkHttpUtils.getInstance().post(URLUtils.MY_VIDEO_COLLECTION, requestBody, new HttpCallBack(new CollectionEvent()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void collectionBean(CollectionEvent collectionEvent) {
        if (collectionEvent.isTimeOut()) {
            mLoadDialog.dismiss();//取消动画
            Toast.makeText(this, "网络状态异常", Toast.LENGTH_SHORT).show();
            return;
        }
        CollectionBean collectionBean = collectionEvent.getData();
        if (collectionBean.getStatus() == 0) {
            //Intent intent = new Intent();
            //intent.setAction("刷新");
            //intent.putExtra("receiver","1");
            //sendBroadcast(intent);
            EventBus.getDefault().post(new ReceiverBean("1"));
            if (flag) {
                if (collectionBean.getData().size() == 0) {
                    adapterEmpty.setText("暂无收藏游记");
                    imgEmpty.setImageResource(R.mipmap.no_book);
                    collectEmpty.setVisibility(View.VISIBLE);
                    refreshRecycler.setVisibility(View.GONE);
                } else {
                    collectEmpty.setVisibility(View.GONE);
                    refreshRecycler.setVisibility(View.VISIBLE);
                    list.clear();
                    list.addAll(collectionBean.getData());
                    refreshRecycler.setAdapter(collectionAdapter);
                }
            } else {
                if (collectionBean.getData().size() == 0) {
                    refreshSmart.setLoadmoreFinished(true);
                } else {
                    list.addAll(collectionBean.getData());
                    collectionAdapter.notifyDataSetChanged();
                    refreshSmart.finishLoadmore();
                }
            }
            mLoadDialog.dismiss();//取消动画
        } else {
            mLoadDialog.dismiss();//取消动画
            Toast.makeText(this, collectionBean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this,collectionBean.getStatus());
        }
    }
}
