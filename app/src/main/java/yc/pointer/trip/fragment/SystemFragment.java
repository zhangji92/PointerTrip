package yc.pointer.trip.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import yc.pointer.trip.R;
import yc.pointer.trip.adapter.SystemFragmentListAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseViewPageFragment;
import yc.pointer.trip.bean.SystemMessageBean;
import yc.pointer.trip.bean.eventbean.NotifyMsgBean;
import yc.pointer.trip.event.SystemMessageListEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;

/**
 * Created by moyan on 2018/6/12.
 * 系统消息的页面
 */

public class SystemFragment extends BaseViewPageFragment {


    @BindView(R.id.refresh_recycler)
    RecyclerView refreshRecycler;
    @BindView(R.id.empty_img)
    ImageView emptyImg;
    @BindView(R.id.adapter_empty)
    TextView adapterEmpty;
    @BindView(R.id.empty)
    LinearLayout empty;
    @BindView(R.id.refresh_smart)
    SmartRefreshLayout refreshSmart;


    private String mUserId;
    private long mTimestamp;
    private String mDevcode;

    private int page = 0;
    private boolean mRefreshLoadFlag = true;//刷新加载的标志 true刷新 flase加载
    private boolean isLoad = true;//

    private List<SystemMessageBean.DataBean> mData = new ArrayList<>();
    private SystemFragmentListAdapter adapter;

    @Override
    protected int getContentViewLayout() {
        return R.layout.fragment_system_message;
    }

    @Override
    protected void initView() {

        mUserId = ((MyApplication) getActivity().getApplication()).getUserId();
        mTimestamp = ((MyApplication) getActivity().getApplication()).getTimestamp();
        mDevcode = ((MyApplication) getActivity().getApplication()).getDevcode();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        refreshRecycler.setLayoutManager(layoutManager);

        if (mData.size() == 0) {
            refreshRecycler.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
            adapterEmpty.setText("暂无消息通知");
            emptyImg.setImageResource(R.mipmap.no_oreder);
        }

        adapter = new SystemFragmentListAdapter(mData);
        refreshRecycler.setAdapter(adapter);


        refreshSmart.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                //加载
                mRefreshLoadFlag = false;
                ++page;
                getSystemMessage();
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                //刷新
                refreshSmart.setLoadmoreFinished(false);
                mRefreshLoadFlag = true;
                page = 0;
                getSystemMessage();
            }
        });

        isPrepared = true;
        loadData();

    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isVisble = true;
            onVisible();
        } else {
            isVisble = false;
            onInVisible();
        }
    }

    protected void onInVisible() {

    }

    protected void onVisible() {
        //加载数据
        loadData();
    }

    /**
     * 加载网络数据
     */
    protected void loadData() {
        if (!isVisble || !isPrepared) {
            return;
        }
//        mLoadDialog = new LoadDialog(getActivity(), "正在加载...", R.drawable.load_animation);
//        mLoadDialog.show();

        if (isLoad) {
            mRefreshLoadFlag = true;
            //请求数据
            getSystemMessage();
        }

    }

    ;


    private void getSystemMessage() {
        boolean timeDev = APPUtils.judgeTimeDev(getActivity(), mDevcode, mTimestamp);
        if (timeDev) {
            String signature = Md5Utils.createMD5("devcode=" + mDevcode + "p=" + page + "timestamp=" + mTimestamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", mDevcode)
                    .add("p", String.valueOf(page))
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", mUserId)
                    .add("signature", signature)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.SYSTEM_MESSAGE, requestBody, new HttpCallBack(new SystemMessageListEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void systemMessageData(SystemMessageListEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(getActivity(), "网络状态异常", Toast.LENGTH_SHORT).show();
            return;
        }
        SystemMessageBean data = event.getData();

        if (isVisble) {
            SystemMessageBean.UnMsgBean unMsg = data.getUnMsg();
            //Intent intent = new Intent();
            //intent.setAction("notifiMessage");
            //intent.putExtra("UnMsgBean", unMsg);
            //getActivity().sendBroadcast(intent);
            EventBus.getDefault().post(new NotifyMsgBean(unMsg,"notifiMessage"));
        }

        if (data.getStatus() == 0) {
            isLoad = false;
            if (mRefreshLoadFlag) {
                //刷新
                if (data.getData().size() == 0) {
                    refreshRecycler.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                    adapterEmpty.setText("暂无消息通知");
                    emptyImg.setImageResource(R.mipmap.no_oreder);
                } else {
                    refreshRecycler.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                    mData.clear();
                    mData.addAll(data.getData());
                    adapter.notifyDataSetChanged();
                }
                refreshSmart.finishRefresh();
            } else {
                //加载
                if (data.getData().size() == 0) {
                    refreshSmart.setLoadmoreFinished(true);
                } else {
                    mData.addAll(data.getData());
                    adapter.notifyDataSetChanged();
                }
                refreshSmart.finishLoadmore();
            }
        } else {
            Toast.makeText(getActivity(), data.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(getActivity(), data.getStatus());
        }
    }


}
