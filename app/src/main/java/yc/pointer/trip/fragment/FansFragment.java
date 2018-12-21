package yc.pointer.trip.fragment;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
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
import yc.pointer.trip.activity.NewPersonalHomePageActivity;
import yc.pointer.trip.adapter.FansFragmentListAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseViewPageFragment;
import yc.pointer.trip.bean.FansBean;
import yc.pointer.trip.bean.PersonalPageFollowBean;
import yc.pointer.trip.bean.eventbean.NotifyMsgBean;
import yc.pointer.trip.event.FansFollowEvent;
import yc.pointer.trip.event.FansMessageEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.StringUtil;

/**
 * Created by moyan on 2018/6/12.
 * 粉丝消息的页面
 */

public class FansFragment extends BaseViewPageFragment {


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
    private boolean mRefreshLoadFlag;//true:刷新  false：记载
    private int page = 0;
    private boolean isLoad = true;
    private List<FansBean.DataBean> mList = new ArrayList<>();
    private FansFragmentListAdapter adapter;
    private Button button;
    private int index = 0;

    @Override
    protected int getContentViewLayout() {
        return R.layout.fragment_fans;
    }

    @Override
    protected void initView() {


        mUserId = ((MyApplication) getActivity().getApplication()).getUserId();
        mTimestamp = ((MyApplication) getActivity().getApplication()).getTimestamp();
        mDevcode = ((MyApplication) getActivity().getApplication()).getDevcode();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        refreshRecycler.setLayoutManager(layoutManager);

        if (mList.size() == 0) {
            refreshRecycler.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
            emptyImg.setImageResource(R.mipmap.fans_no_data);
            adapterEmpty.setText("暂无粉丝消息");
        }

        adapter = new FansFragmentListAdapter(getActivity(), mList);
        refreshRecycler.setAdapter(adapter);


        refreshSmart.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                //加载
                mRefreshLoadFlag = false;
                ++page;
                getFansMsg(mUserId, mDevcode, mTimestamp, page);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                //刷新
                refreshSmart.setLoadmoreFinished(false);
                mRefreshLoadFlag = true;
                page = 0;
                getFansMsg(mUserId, mDevcode, mTimestamp, page);

            }
        });

        adapter.setListener(new FansFragmentListAdapter.AddAttentionListener() {
            @Override
            public void addAtten(Button attenButton, int position) {
                //添加关注
                button = attenButton;
                index = position;
                int att_status = mList.get(position).getAtt_status();
                String UUid = mList.get(position).getUid();

                if (att_status == 0) {
                    postFollow(mUserId, mDevcode, mTimestamp, UUid, 1);
                } else {
                    postFollow(mUserId, mDevcode, mTimestamp, UUid, 0);
                }
                button.setEnabled(false);
                button.setClickable(false);
            }

            @Override
            public void ToPersonPage(Button attenButton, int position) {
                button = attenButton;
                index = position;
                Intent intent = new Intent(getActivity(), NewPersonalHomePageActivity.class);
                intent.putExtra("uid", mList.get(position).getUid());
                startActivityForResult(intent,1);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode!=getActivity().RESULT_OK){
//            return;
//        }
        switch (requestCode){
            case 1:
                if (resultCode==2){
                    String c_status = data.getStringExtra("c_status");
                    if (!StringUtil.isEmpty(c_status)){
                        if (c_status.equals("1")) {
                            button.setText("互相关注");
                            button.setBackgroundResource(R.drawable.invitation_dialog_not);
                        } else {
                            button.setText("关注");
                            button.setBackgroundResource(R.drawable.go_verify);
                        }
                        int status = Integer.valueOf(c_status);
                        mList.get(index).setAtt_status(status);
                    }
                }
                break;
        }
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
            getFansMsg(mUserId, mDevcode, mTimestamp, 0);
        }

    }

    ;


    /**
     * 粉丝消息接口
     *
     * @param uid       用户id
     * @param devcode   设备识别码
     * @param timestamp 时间戳
     * @param page      分享
     */
    private void getFansMsg(String uid, String devcode, long timestamp, int page) {
        if (APPUtils.judgeTimeDev(getActivity(), devcode, timestamp)) {
            String sign = Md5Utils.createMD5("devcode=" + devcode + "p=" + page + "timestamp=" + timestamp + "type=" + 2 + "uid=" + uid + "uuid=" + uid + URLUtils.WK_APP_KEY);
            RequestBody params = new FormBody.Builder()
                    .add("devcode", devcode)
                    .add("p", String.valueOf(page))
                    .add("timestamp", String.valueOf(timestamp))
                    .add("type", String.valueOf(2))
                    .add("uid", uid)
                    .add("uuid", uid)
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.PERSONAL_FOLLOW_LIST, params, new HttpCallBack(new FansMessageEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMsg(FansMessageEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(getActivity(), "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        FansBean bean = event.getData();
        if (isVisble) {
            FansBean.UnMsgBean unMsg = bean.getUnMsg();
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
                    empty.setVisibility(View.VISIBLE);
                    emptyImg.setImageResource(R.mipmap.fans_no_data);
                    adapterEmpty.setText("暂无粉丝消息");
                } else {
                    empty.setVisibility(View.GONE);
                    refreshRecycler.setVisibility(View.VISIBLE);
                }
                mList.clear();
                mList.addAll(bean.getData());
                adapter.notifyDataSetChanged();
                refreshSmart.finishRefresh();
            } else {
                if (bean.getData().size() > 0) {
                    refreshSmart.setLoadmoreFinished(true);
                } else {
                    int oldSize = mList.size();
                    mList.addAll(bean.getData());
                    adapter.notifyItemRangeChanged(oldSize, mList.size());
                }
                refreshSmart.finishLoadmore();
            }
        } else {
            Toast.makeText(getActivity(), bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(getActivity(), bean.getStatus());
        }
    }


    /**
     * 关注接口
     *
     * @param userId    用户id
     * @param devcode   设备识别码
     * @param timestamp 时间戳
     * @param uuid      路数id
     * @param status    关注状态  1：加关注  0：取消关注
     */
    private void postFollow(String userId, String devcode, long timestamp, String uuid, int status) {
        if (APPUtils.judgeTimeDev(getActivity(), devcode, timestamp)) {
            String sign = Md5Utils.createMD5("devcode=" + devcode + "status=" + status + "timestamp=" + timestamp + "uid=" + userId + "uuid=" + uuid + URLUtils.WK_APP_KEY);
            RequestBody params = new FormBody.Builder()
                    .add("devcode", devcode)
                    .add("status", String.valueOf(status))
                    .add("timestamp", String.valueOf(timestamp))
                    .add("uid", userId)
                    .add("uuid", uuid)
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.PERSONAL_FOLLOW, params, new HttpCallBack(new FansFollowEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void postFollow(FansFollowEvent event) {
        if (event.isTimeOut()) {
            button.setClickable(true);
            button.setEnabled(true);
            Toast.makeText(getActivity(), "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        PersonalPageFollowBean data = event.getData();
        if (data.getStatus() == 0) {
            button.setClickable(true);
            button.setEnabled(true);
            if (data.getC_status() == 1) {
                button.setText("互相关注");
                button.setBackgroundResource(R.drawable.invitation_dialog_not);
            } else {
                button.setText("关注");
                button.setBackgroundResource(R.drawable.go_verify);
            }
            int c_status = data.getC_status();
            mList.get(index).setAtt_status(c_status);
        } else {
            button.setClickable(true);
            button.setEnabled(true);
            Toast.makeText(getActivity(), data.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(getActivity(), data.getStatus());

        }
    }


}
