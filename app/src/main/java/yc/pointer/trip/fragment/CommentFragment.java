package yc.pointer.trip.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import yc.pointer.trip.activity.SystemMessageActivity;
import yc.pointer.trip.adapter.MessageListViewAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseViewPageFragment;
import yc.pointer.trip.bean.MessageListBean;
import yc.pointer.trip.bean.ReplyBean;
import yc.pointer.trip.bean.eventbean.NotifyMsgBean;
import yc.pointer.trip.event.MessageListEvent;
import yc.pointer.trip.event.ReplyCommentEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.SoftKeyboardStateWatcher;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.MyListView;

/**
 * Created by moyan on 2018/6/12.
 * 评论消息的页面
 */

public class CommentFragment extends BaseViewPageFragment implements MessageListViewAdapter.MsgCallBack, MyListView.OnSizeChangedListener {

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
    @BindView(R.id.msg_activity_edit)
    EditText msgActivityEdit;
    @BindView(R.id.msg_activity_commit)
    Button msgActivityCommit;
    @BindView(R.id.msg_activity_scroll)
    RelativeLayout msgActivityScroll;//回复评论的输入框外层布局


    private String mUserId;
    private long mTimestamp;
    private String mDevcode;

    private int page = 0;
    private int viewHeight;
    private int position;
    private String mBid;
    private String mPid;
    private boolean isLoad = true;


    private MessageListViewAdapter adapter;
//    private MessageRecycleViewAdapter adapter;

    private boolean mRefreshLoadFlag = true;//刷新加载的标志 true刷新 flase加载
    private List<MessageListBean.DataBean> mList = new ArrayList<>();
    private boolean isOpenSoftKey = false;
    private int softKeyHeight;
    private SystemMessageActivity.MyOnTouchListener myOnTouchListener;

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_msg_list;
    }

    @Override
    protected void initView() {
        //EditText与沉浸式状态栏的冲突适配解决
//        AndroidBug5497Workaround.assistActivity(View.inflate(getActivity(),getContentViewLayout(),null).findViewById(android.R.id.content));
        displayHid(linearRoot);
        mUserId = ((MyApplication) getActivity().getApplication()).getUserId();
        mTimestamp = ((MyApplication) getActivity().getApplication()).getTimestamp();
        mDevcode = ((MyApplication) getActivity().getApplication()).getDevcode();

        if (mList.size() == 0) {
            refreshRecycler.setVisibility(View.GONE);
            emptyImg.setImageResource(R.mipmap.img_empty_messagelist);
            adapterEmpty.setText("暂无评论消息");
            empty.setVisibility(View.VISIBLE);
        }

        adapter = new MessageListViewAdapter(getActivity(), mList, this);
        refreshRecycler.setOnSizeChangedListener(this);
//       adapter = new MessageRecycleViewAdapter();
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

        //回复信息
        msgActivityCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = msgActivityEdit.getText().toString();
                if (!StringUtil.isEmpty(str)) {
                    //发表评论回复
                    publishComments(mBid, Integer.parseInt(mPid), str);
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(msgActivityEdit.getWindowToken(), 0);
                } else {
                    Toast.makeText(MyApplication.mApp, "评论信息不能为空哦", Toast.LENGTH_SHORT).show();
                }
            }
        });

        isPrepared = true;
        loadData();
        //Touch监听
        myOnTouchListener = new SystemMessageActivity.MyOnTouchListener() {
            @Override
            public boolean onTouch(MotionEvent ev) {
                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN://按下的时候
                        if (refreshSmart != null) {
                            refreshSmart.setEnableRefresh(true);
                            refreshSmart.setEnableLoadmore(true);
                        }
                        break;
                }
                return false;
            }
        };
        //注册监听
        ((SystemMessageActivity) getActivity()).registerMyOnTouchListener(myOnTouchListener);

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
     * 软键盘谈起和关闭的监听
     *
     * @param view
     */
    private void displayHid(View view) {
        SoftKeyboardStateWatcher watcher = new SoftKeyboardStateWatcher(view, getActivity());
        watcher.addSoftKeyboardStateListener(new SoftKeyboardStateWatcher.SoftKeyboardStateListener() {
            @Override
            public void onSoftKeyboardOpened(int keyboardHeightInPx) {
                //软键盘打开状态
                isOpenSoftKey = true;
                softKeyHeight = keyboardHeightInPx;
//                refreshSmart.setEnableLoadmore(false);
                msgActivityScroll.setVisibility(View.VISIBLE);

            }

            @Override
            public void onSoftKeyboardClosed() {
                //软键盘关闭状态
                isOpenSoftKey = false;
                msgActivityEdit.setHint("写评论(最多150字)");
//                refreshSmart.setEnableLoadmore(true);
                msgActivityScroll.setVisibility(View.GONE);

            }
        });
    }

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
            OkHttpUtils.getInstance().post(URLUtils.MESSAGE_LIST_ACTIVITY, requestBody, new HttpCallBack(new MessageListEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getServiceMsg(MessageListEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(getActivity(), "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        MessageListBean bean = event.getData();

        if (isVisble) {
            MessageListBean.UnMsgBean unMsg = bean.getUnMsg();
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
                    adapterEmpty.setText("暂无评论消息");
                    empty.setVisibility(View.VISIBLE);
                } else {
                    refreshRecycler.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                    mList.clear();
                    mList.addAll(bean.getData());
                    adapter = new MessageListViewAdapter(getActivity(), mList, this);
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

    /**
     * 发表评论以及回复
     *
     * @param pid        （消息Id，对该游记的评论，pid默认传0）
     * @param bid        （路书Id）
     * @param info(评论内容)
     */
    private void publishComments(String bid, int pid, String info) {
        boolean timeFlag = APPUtils.judgeTimeDev(getActivity(), mDevcode, mTimestamp);
        if (timeFlag) {
            String sign = Md5Utils.createMD5("bid=" + bid + "devcode=" + mDevcode + "info=" + info + "pid=" + pid + "timestamp=" + mTimestamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", String.valueOf(mUserId))
                    .add("bid", String.valueOf(bid))
                    .add("signature", sign)
                    .add("devcode", mDevcode)
                    .add("pid", String.valueOf(pid))
                    .add("info", info)
                    .build();
            //回复列表数据
            OkHttpUtils.getInstance().post(URLUtils.PUBLISH_COMMENTS, requestBody, new HttpCallBack(new ReplyCommentEvent()));
        }
    }

    /**
     * 获取回复
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getCommentsMessage(ReplyCommentEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(getActivity(), "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        ReplyBean data = event.getData();
        if (data.getStatus() == 0) {
//            refreshSmart.autoRefresh();
            msgActivityEdit.setText("");
            Toast.makeText(getActivity(), "回复成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), data.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(getActivity(), data.getStatus());
        }
    }

    /**
     * 尺寸变化的监听
     */
    @Override
    public void onClickComment(int i, String name, String cid, String bid, LinearLayout linearLayout) {
        refreshSmart.setEnableRefresh(false);
        refreshSmart.setEnableLoadmore(false);
        refreshSmart.setEnableLoadmore(false);
        refreshSmart.setEnableRefresh(false);
        mPid = cid;
        mBid = bid;
        String is_del = mList.get(i).getIs_del();
        if (!is_del.equals("1")) {
            if (!MyApplication.mApp.getUserBean().getNickname().equals(name)) {

                msgActivityEdit.setHint("回复:" + name);
                msgActivityScroll.setVisibility(View.VISIBLE);
                position = (int) linearLayout.getTag();
                //获取点击的那个Item的高度
                viewHeight = linearLayout.getHeight();
                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                // 如果软键盘已经显示，则隐藏，反之则显示
                inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                msgActivityEdit.requestFocus();
                inputManager.showSoftInput(msgActivityEdit, 0);
//                refreshRecycler.setOnSizeChangedListener(this);
            } else {
                Toast.makeText(getActivity(), "当前的评论是您自己的", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "该消息已删除，无法回复", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onSizeChanged() {
        int formTop = refreshRecycler.getBottom();
        refreshRecycler.setSelectionFromTop(position, formTop - viewHeight);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((SystemMessageActivity) getActivity()).unregisterMyOnTouchListener(myOnTouchListener);
    }
}
