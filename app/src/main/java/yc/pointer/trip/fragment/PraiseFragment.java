package yc.pointer.trip.fragment;

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
import yc.pointer.trip.adapter.PraiseMessageListViewAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseViewPageFragment;
import yc.pointer.trip.bean.PrasieMessageBean;
import yc.pointer.trip.bean.eventbean.NotifyMsgBean;
import yc.pointer.trip.event.PraiseMessageListEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.view.MyListView;

/**
 * Created by moyan on 2018/6/12.
 * 点赞消息的页面
 */

public class PraiseFragment extends BaseViewPageFragment {

    @BindView(R.id.msg_activity_list)
    MyListView refreshRecycler;
    @BindView(R.id.msg_activity_smart)
    SmartRefreshLayout refreshSmart;
    @BindView(R.id.msg_activity_root)
    LinearLayout linearRoot;
    @BindView(R.id.empty_img)
    ImageView emptyImg;
    @BindView(R.id.adapter_empty)
    TextView adapterEmpty;
    @BindView(R.id.empty)
    LinearLayout empty;


    private String mUserId;
    private long mTimestamp;
    private String mDevcode;

    private int page = 0;
    private PraiseMessageListViewAdapter adapter;
    private boolean isLoad = true;

    private boolean mRefreshLoadFlag = true;//刷新加载的标志 true刷新 flase加载
    private List<PrasieMessageBean.DataBean> mList = new ArrayList<>();

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_msg_list;
    }

    @Override
    protected void initView() {

        mUserId = ((MyApplication) getActivity().getApplication()).getUserId();
        mTimestamp = ((MyApplication) getActivity().getApplication()).getTimestamp();
        mDevcode = ((MyApplication) getActivity().getApplication()).getDevcode();
        if (mList.size() == 0) {
            refreshRecycler.setVisibility(View.GONE);
            emptyImg.setImageResource(R.mipmap.img_empty_messagelist);
            adapterEmpty.setText("暂无点赞消息");
            empty.setVisibility(View.VISIBLE);
        }
        adapter = new PraiseMessageListViewAdapter(mList, getActivity());
        refreshRecycler.setAdapter(adapter);

        refreshSmart.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                //加载
                mRefreshLoadFlag = false;
                ++page;
                getServiceMsg(mUserId, mTimestamp, mDevcode, page);

            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                //刷新
                refreshSmart.setLoadmoreFinished(false);
                mRefreshLoadFlag = true;
                page = 0;
                getServiceMsg(mUserId, mTimestamp, mDevcode, page);

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
            getServiceMsg(mUserId, mTimestamp, mDevcode, 0);
        }

    }

    ;

    /**
     * 获取网络信息
     *
     * @param uid       用户id
     * @param timestamp 时间戳
     * @param devcode   手机标识码
     * @param page      分页页数
     */
    public void getServiceMsg(String uid, long timestamp, String devcode, int page) {
        boolean timeDev = APPUtils.judgeTimeDev(getActivity(), devcode, timestamp);
        if (timeDev) {
            String signature = Md5Utils.createMD5("devcode=" + devcode + "p=" + page + "timestamp=" + timestamp + "uid=" + uid + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", devcode)
                    .add("p", String.valueOf(page))
                    .add("timestamp", String.valueOf(timestamp))
                    .add("uid", uid)
                    .add("signature", signature)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.PRAISE_MESSAGE, requestBody, new HttpCallBack(new PraiseMessageListEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getServiceMsg(PraiseMessageListEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(getActivity(), "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        PrasieMessageBean bean = event.getData();
        if (isVisble){
            PrasieMessageBean.UnMsgBean unMsg = bean.getUnMsg();
            //Intent intent = new Intent();
            //intent.setAction("notifiMessage");
            //intent.putExtra("UnMsgBean", unMsg);
            //getActivity().sendBroadcast(intent);
            EventBus.getDefault().post(new NotifyMsgBean(unMsg,"notifiMessage"));
        }

        if (bean.getStatus() == 0) {
            isLoad = false;
            if (mRefreshLoadFlag) {
                if (bean.getData().size() == 0) {
                    refreshRecycler.setVisibility(View.GONE);
                    emptyImg.setImageResource(R.mipmap.img_empty_messagelist);
                    adapterEmpty.setText("暂无点赞消息");
                    empty.setVisibility(View.VISIBLE);
                } else {
                    refreshRecycler.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                    mList.clear();
                    mList.addAll(bean.getData());
                    adapter = new PraiseMessageListViewAdapter(mList, getActivity());
                    refreshRecycler.setAdapter(adapter);
                }
                refreshSmart.finishRefresh();
            } else {
                if (bean.getData().size() == 0) {
                    refreshSmart.setLoadmoreFinished(true);
                } else {
                    mList.addAll(bean.getData());
                }
                adapter.notifyDataSetChanged();
                refreshSmart.finishLoadmore();
            }
        } else {
            Toast.makeText(getActivity(), bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(getActivity(), bean.getStatus());
        }
    }

}
