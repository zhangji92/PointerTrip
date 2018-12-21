package yc.pointer.trip.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
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
import butterknife.ButterKnife;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import yc.pointer.trip.R;
import yc.pointer.trip.adapter.MessageListViewAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.MessageListBean;
import yc.pointer.trip.bean.ReplyBean;
import yc.pointer.trip.event.MessageListEvent;
import yc.pointer.trip.event.ReplyEvent;
import yc.pointer.trip.event.ReplyMessageEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.AndroidBug5497Workaround;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.SoftKeyboardStateWatcher;
import yc.pointer.trip.untils.StatusBarUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.EditDialog;
import yc.pointer.trip.view.MyListView;

/**
 * Created by 张继
 * 2018/4/2
 * 15:24
 * 公司:天津悦程嘉和网络科技有限公司
 * 描述: 个人消息评论列表
 */

public class MessageListActivity extends BaseActivity implements MessageListViewAdapter.MsgCallBack, MyListView.OnSizeChangedListener {
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
    RelativeLayout msgActivityScroll;
//    @BindView(R.id.standard_toolbar_title)
//    TextView standardToolbarTitle;
//    @BindView(R.id.standard_toolbar)
//    Toolbar standardToolbar;


    private MessageListViewAdapter adapter;
    private String mUserId;
    private long mTimestamp;
    private String mDevcode;
    private List<MessageListBean.DataBean> mList = new ArrayList<>();
    private boolean mRefreshLoadFlag = true;//刷新加载的标志 true刷新 flase加载
    private int page = 0;
    private String mBid;
    private String mPid;
    private EditDialog editDialog;
    private int viewHeight;
    private int position;
    private boolean softKeyboardFlag = false;


    @Override
    protected int getContentViewLayout() {

        return R.layout.activity_msg_list;
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

    @Override
    protected void initView() {


        //EditText与沉浸式状态栏的冲突适配解决
        AndroidBug5497Workaround.assistActivity(findViewById(android.R.id.content));
//        standardToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
        displayHid(linearRoot);
//        standardToolbarTitle.setText("消息列表");
        mUserId = ((MyApplication) getApplication()).getUserId();
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();
        mDevcode = ((MyApplication) getApplication()).getDevcode();

        getServiceMsg(mUserId, mTimestamp, mDevcode, 0);
        adapter = new MessageListViewAdapter(this, mList, this);
        refreshRecycler.setOnSizeChangedListener(this);
        refreshRecycler.setAdapter(adapter);

        refreshSmart.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                mRefreshLoadFlag = false;
                ++page;
                getServiceMsg(mUserId, mTimestamp, mDevcode, page);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mList.clear();
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
                    publishComments(mBid, Integer.parseInt(mPid), str);
                } else {
                    Toast.makeText(MyApplication.mApp, "评论信息不能为空哦", Toast.LENGTH_SHORT).show();
                }
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
        boolean timeDev = APPUtils.judgeTimeDev(this, devcode, timestamp);
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
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        MessageListBean bean = event.getData();
        if (bean.getStatus() == 0) {
            if (mRefreshLoadFlag) {

                if (bean.getData().size() == 0) {
                    refreshRecycler.setVisibility(View.GONE);
                    emptyImg.setImageResource(R.mipmap.img_empty_messagelist);
                    adapterEmpty.setText("你还没有消息");
                    empty.setVisibility(View.VISIBLE);
                } else {
                    refreshRecycler.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                    mList.clear();
                    mList.addAll(bean.getData());
                    adapter = new MessageListViewAdapter(this, mList, this);
                    refreshRecycler.setOnSizeChangedListener(this);
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
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }



    @Override
    public void onClickComment(int position,String name, String cid, String bid, LinearLayout linearLayout) {
        mPid = cid;
        mBid = bid;
        if (!MyApplication.mApp.getUserBean().getNickname().equals(name)) {
            msgActivityEdit.setHint("回复:" + name);
            msgActivityScroll.setVisibility(View.VISIBLE);
            position = (int) linearLayout.getTag();
            //获取点击的那个Item的高度
            viewHeight = linearLayout.getHeight();
            InputMethodManager inputManager =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            // 如果软键盘已经显示，则隐藏，反之则显示
            inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            msgActivityEdit.requestFocus();
            inputManager.showSoftInput(msgActivityEdit, 0);
        } else {
            Toast.makeText(this, "当前的评论是您自己的", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 软键盘谈起和关闭的监听
     *
     * @param view
     */
    private void displayHid(View view) {
        SoftKeyboardStateWatcher watcher = new SoftKeyboardStateWatcher(view, this);
        watcher.addSoftKeyboardStateListener(new SoftKeyboardStateWatcher.SoftKeyboardStateListener() {
            @Override
            public void onSoftKeyboardOpened(int keyboardHeightInPx) {
                refreshSmart.setEnableLoadmore(false);
                msgActivityScroll.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSoftKeyboardClosed() {
                msgActivityEdit.setHint("写评论(最多150字)");
                refreshSmart.setEnableLoadmore(true);
                msgActivityScroll.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 发表评论以及回复
     *
     * @param pid        （消息Id，对该游记的评论，pid默认传0）
     * @param bid        （路书Id）
     * @param info(评论内容)
     */
    private void publishComments(String bid, int pid, String info) {
        boolean timeFlag = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
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
            OkHttpUtils.getInstance().post(URLUtils.PUBLISH_COMMENTS, requestBody, new HttpCallBack(new ReplyMessageEvent()));
        }
    }

    /**
     * 获取回复
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getCommentsMessage(ReplyMessageEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        ReplyBean data = event.getData();
        if (data.getStatus() == 0) {
            refreshSmart.autoRefresh();
            Toast.makeText(this, "回复成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, data.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, data.getStatus());
        }
    }


    @Override
    public void onSizeChanged() {
        refreshRecycler.setSelectionFromTop(position, refreshRecycler.getBottom() - viewHeight);
    }
}
