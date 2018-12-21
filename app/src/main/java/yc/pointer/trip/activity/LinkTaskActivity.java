package yc.pointer.trip.activity;

import android.content.Intent;
import android.os.Bundle;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import yc.pointer.trip.R;
import yc.pointer.trip.adapter.LinkRecyclerAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.TaskBean;
import yc.pointer.trip.bean.eventbean.RefreshEarningAProfitBean;
import yc.pointer.trip.event.TaskEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.view.CountDownView;
import yc.pointer.trip.view.ToolbarWrapper;

/**
 * Created by 张继
 * 2018/9/11
 * 10:05
 * 公司：
 * 描述：点赞任务
 */
public class LinkTaskActivity extends BaseActivity {

    @BindView(R.id.like_banner)
    ImageView likeBanner;//banner图片
    @BindView(R.id.like_like_icon)
    TextView likeLikeIcon;
    @BindView(R.id.like_like_num)
    TextView likeLikeNum;
    @BindView(R.id.like_like_unit)
    TextView likeLikeUnit;
    @BindView(R.id.like_income_icon)
    TextView likeIncomeIcon;
    @BindView(R.id.like_income_num)
    TextView likeIncomeNum;
    @BindView(R.id.like_income_unit)
    TextView likeIncomeUnit;
    @BindView(R.id.like_countdown)
    CountDownView likeCountdown;//倒计时
    @BindView(R.id.like_leaderboard_list)
    RecyclerView recyclerView;//收益列表
    @BindView(R.id.like_legend)
    TextView likeLegend;//说明
    @BindView(R.id.like_start)
    Button likeStart;//去做任务
    @BindView(R.id.standard_toolbar)
    Toolbar standardToolbar;
    @BindView(R.id.like_leaderboard)
    TextView likeLeaderboard;
    @BindView(R.id.like_message_group)
    LinearLayout likeMessageGroup;
    private String mUserId;
    private String mDevcode;
    private long mTimestamp;
    private int mType;
    private RefreshEarningAProfitBean refreshEarningAProfitBean;

    private ToolbarWrapper toolbarWrapper;

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_like_task;
    }

    @Override
    protected void initView() {
        toolbarWrapper = new ToolbarWrapper(this);
        toolbarWrapper.setCustomTitle("点赞任务");
        mUserId = MyApplication.mApp.getUserId();
        mDevcode = MyApplication.mApp.getDevcode();
        mTimestamp = MyApplication.mApp.getTimestamp();
        refreshEarningAProfitBean = new RefreshEarningAProfitBean("刷新赚一赚");
        getTaskData(mUserId, mTimestamp, mDevcode);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        standardToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(refreshEarningAProfitBean);
                finish();
            }
        });

    }

    /**
     * 请求后台获取任务数据
     *
     * @param userId    用户id
     * @param timestamp 时间戳
     * @param devcode   设备识别码
     */
    private void getTaskData(String userId, long timestamp, String devcode) {
        if (APPUtils.judgeTimeDev(this, devcode, timestamp)) {//判断设备识别码是否为空
            String str = Md5Utils.createMD5("devcode=" + devcode + "timestamp=" + timestamp + "uid=" + userId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", String.valueOf(devcode))
                    .add("timestamp", String.valueOf(timestamp))
                    .add("signature", str)
                    .add("uid", userId)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.TASK_DETAILS, requestBody, new HttpCallBack(new TaskEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getTaskData(TaskEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
        }
        TaskBean data = event.getData();
        if (data.getStatus() == 0) {
            mType = data.getUser().getType();
            if (mType == 0) {
                toolbarWrapper.setCustomTitle("点赞任务");
                Drawable drawableLeft = getResources().getDrawable(R.mipmap.icon_zan_td);
                likeLikeIcon.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
                        null, null, null);
                likeLikeIcon.setText("点赞视频 :");
            } else if (mType == 1) {
                toolbarWrapper.setCustomTitle("转发任务");
                Drawable drawableLeft = getResources().getDrawable(R.mipmap.icon_share_td);
                likeLikeIcon.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
                        null, null, null);
                likeLikeIcon.setText("转发视频 :");
            } else if (mType == 2) {
                toolbarWrapper.setCustomTitle("评论任务");
                Drawable drawableLeft = getResources().getDrawable(R.mipmap.icon_comments_td);
                likeLikeIcon.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
                        null, null, null);
                likeLikeIcon.setText("评论视频 :");
            }

            String pic = data.getUser().getPic();
            OkHttpUtils.displayGlide(this, likeBanner, pic);
            String num = data.getUser().getNum();
            likeLikeNum.setText(num);
            String money = data.getUser().getMoney();
            likeIncomeNum.setText(money);


            int end_time = data.getUser().getEnd_time();
            likeCountdown.initTime(end_time);
            likeCountdown.reStart();
            likeCountdown.setOnTimeCompleteListener(new CountDownView.OnTimeCompleteListener() {//倒计时结束
                @Override
                public void onTimeComplete() {
                    finish();
                }
            });


            String tips = data.getUser().getTips();
            likeLegend.setText(tips);
            recyclerView.setAdapter(new LinkRecyclerAdapter(data.getData()));
        } else {
            Toast.makeText(this, data.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, data.getStatus());
        }
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

    @OnClick({R.id.like_start})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.like_start:
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            EventBus.getDefault().post(refreshEarningAProfitBean);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getTaskData(mUserId, mTimestamp, mDevcode);
    }

}
