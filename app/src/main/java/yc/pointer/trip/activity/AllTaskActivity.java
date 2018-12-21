package yc.pointer.trip.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import yc.pointer.trip.adapter.AllTaskAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.AllTaskDestilsBean;
import yc.pointer.trip.bean.GetAllTaskMoneyBean;
import yc.pointer.trip.bean.eventbean.RefreshEarningAProfitBean;
import yc.pointer.trip.event.AllTaskDestilsEvent;
import yc.pointer.trip.event.GetAllTaskMoneyEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.ToolbarWrapper;

/**
 * Created by moyan on 2018/9/29.
 */

public class AllTaskActivity extends BaseActivity {

    @BindView(R.id.standard_toolbar)
    Toolbar standardToolbar;
    @BindView(R.id.recycle_all_task)
    RecyclerView recycleAllTask;
    @BindView(R.id.satandard_toolbar_right)
    TextView satandardToolbarRight;


    private String mUserId;
    private long mTimestamp;
    private String mDevCode;

    private String bannerPicUrl = "";//顶部banner图片
    private List<AllTaskDestilsBean.DataBean> taskData = new ArrayList<>();//任务信息列表
    private int[] img = {R.mipmap.icon_tast_zan_zong, R.mipmap.icon_tast_share_zong, R.mipmap.icon_tast_pinglun_zong};
    private String[] taskTitle = {"点赞任务", "分享任务", "评论任务"};
    private String[] taskIntroduction = {"给您喜欢的游记点赞", "分享您喜欢的游记", "评论你感兴趣的游记"};
    private AllTaskAdapter allTaskAdapter;
    private RefreshEarningAProfitBean refreshEarningAProfitBean;
    private String webUrl="";


    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_all_task;
    }

    @Override
    protected void initView() {
        ToolbarWrapper toolbarWrapper = new ToolbarWrapper(this);
        toolbarWrapper.setCustomTitle("每日任务");
        toolbarWrapper.setRightText(R.string.all_task_introduction);


        mTimestamp = MyApplication.mApp.getTimestamp();//时间戳
        mDevCode = MyApplication.mApp.getDevcode();//设备识别码
        mUserId = MyApplication.mApp.getUserId();//用户Id
        refreshEarningAProfitBean = new RefreshEarningAProfitBean("刷新赚一赚");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycleAllTask.setLayoutManager(layoutManager);
        allTaskAdapter = new AllTaskAdapter(taskData);
        recycleAllTask.setAdapter(allTaskAdapter);
//获取总任务信息列表
        getAllTaskDestils();
        allTaskAdapter.setListener(new AllTaskAdapter.OnClickTaskMoney() {
            @Override
            public void onClickAllMoney(int taskType) {
                //全部领取
                getAllTaskMoney(taskType);
            }

            @Override
            public void onClickLinkMoney(int taskType) {
                //分类提取
                getAllTaskMoney(taskType);

            }

            @Override
            public void onTimeCompleteClick() {
                //倒计时完成后，点击跳转任务详情
                startActivity(new Intent(AllTaskActivity.this, LinkTaskActivity.class));
                finish();
            }
        });

        standardToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(refreshEarningAProfitBean);
                finish();
            }
        });
        satandardToolbarRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtil.isEmpty(webUrl)){
                    Intent intent = new Intent(AllTaskActivity.this, RuleTextWebActivity.class);
                    intent.putExtra("webUrl",webUrl);
                    startActivity(intent);
                }
            }
        });


    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

    /**
     * 获取总任务信息
     */
    private void getAllTaskDestils() {
        if (APPUtils.judgeTimeDev(this, mDevCode, mTimestamp)) {//判断设备识别码是否为空
            String s = Md5Utils.createMD5("devcode=" + mDevCode + "timestamp=" + mTimestamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", mDevCode)
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", mUserId)
                    .add("signature", s)
                    .build();
            HttpCallBack allTaskCallBack = new HttpCallBack(new AllTaskDestilsEvent());
            OkHttpUtils.getInstance().post(URLUtils.ALL_TASK_DETAILS, requestBody, allTaskCallBack);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getAllTaskInfo(AllTaskDestilsEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络状态异常", Toast.LENGTH_SHORT).show();
            return;
        }
        AllTaskDestilsBean data = event.getData();
        if (data.getStatus() == 0) {
            if (data.getData() != null) {
                taskData.clear();
                setAdapterData(data);

                bannerPicUrl = data.getPic();
                webUrl = data.getUrl();
                allTaskAdapter.setBannerPicUrl(bannerPicUrl);
                allTaskAdapter.notifyDataSetChanged();

            }

        } else {
            Toast.makeText(this, data.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, data.getStatus());
        }

    }

    /**
     * 设置适配器数据
     *
     * @param data
     */
    private void setAdapterData(AllTaskDestilsBean data) {
        for (int i = 0; i < img.length; i++) {
            AllTaskDestilsBean.DataBean datalocal = new AllTaskDestilsBean.DataBean();
            datalocal.setImg(img[i]);
            datalocal.setTaskType(i);
            datalocal.setTaskTitle(taskTitle[i]);
            datalocal.setTaskIntroduction(taskIntroduction[i]);
            datalocal.setMoney(data.getData().get(i).getMoney());
            datalocal.setBtn_status(data.getData().get(i).getBtn_status());
            datalocal.setTime(data.getData().get(i).getTime());
            taskData.add(datalocal);
        }
    }

    /**
     * 领取任务奖励
     *
     * @param type 0点赞 1:转发 2:评论 3: 全部
     */
    private void getAllTaskMoney(int type) {
        if (APPUtils.judgeTimeDev(this, mDevCode, mTimestamp)) {//判断设备识别码是否为空
            String s = Md5Utils.createMD5("devcode=" + mDevCode + "timestamp=" + mTimestamp + "type=" + type + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", mDevCode)
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", mUserId)
                    .add("type", String.valueOf(type))
                    .add("signature", s)
                    .build();
            HttpCallBack allTaskCallBack = new HttpCallBack(new GetAllTaskMoneyEvent());
            OkHttpUtils.getInstance().post(URLUtils.GET_TASK_MONEY, requestBody, allTaskCallBack);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getTaskMoney(GetAllTaskMoneyEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络状态异常", Toast.LENGTH_SHORT).show();
            return;
        }
        GetAllTaskMoneyBean data = event.getData();
        if (data.getStatus() == 0) {
            if (data.getData() != null) {
                taskData.clear();
                setAdapterData(data);
                allTaskAdapter.notifyDataSetChanged();
                EventBus.getDefault().post(refreshEarningAProfitBean);
            }


        } else {
            Toast.makeText(this, data.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, data.getStatus());
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
        getAllTaskDestils();
    }
}
