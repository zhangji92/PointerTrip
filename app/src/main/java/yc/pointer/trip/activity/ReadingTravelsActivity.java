package yc.pointer.trip.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.OnClick;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import yc.pointer.trip.R;
import yc.pointer.trip.adapter.ReadingTravelsAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.ReadBookBean;
import yc.pointer.trip.event.ReadBookEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.PermissionHelper;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.LoadDialog;
import yc.pointer.trip.view.ToolbarWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * 看游记
 * Created by Lenovo
 * 2017/6/29
 * 17:30
 */
public class ReadingTravelsActivity extends BaseActivity {

    @BindView(R.id.refresh_recycler)
    RecyclerView travelsRecycler;
    @BindView(R.id.refresh_smart)
    SmartRefreshLayout travelsSmart;
    @BindView(R.id.travels_read_book)
    ImageView travelsReadBook;

    private List<ReadBookBean.DataBean.DataQBean> mListAll;
    private List<ReadBookBean> mList;

    private String mDevcode;//设备识别码
    private String mUserId;//用户id
    private String mLocationCity;//城市信息
    private long mTimestamp;//时间戳

    private int paging = 0;//分页
    private boolean flag = true;//true-刷新  false-加载
    private ReadingTravelsAdapter travelsAdapter;
    private LoadDialog mLoadDialog;

    private PermissionHelper permissionHelper;
    private PermissionHelper.PermissionModel[] permissionModels = {
            new PermissionHelper.PermissionModel(1, Manifest.permission.CAMERA, "打开你的相机"),
            new PermissionHelper.PermissionModel(2, Manifest.permission.WRITE_EXTERNAL_STORAGE, "获取您的读写权限"),
            new PermissionHelper.PermissionModel(3, Manifest.permission.RECORD_AUDIO, "获取您的麦克风权限")
    };


    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_travels_read;
    }

    @Override
    protected void initView() {
        //开启动画
        mLoadDialog = new LoadDialog(this, "正在加载...", R.drawable.load_animation);
        mLoadDialog.show();
        new ToolbarWrapper(this).setCustomTitle(R.string.read_travel_title);

        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("notify");
        registerReceiver(new MyBroadcastReciver(), intentFilter);

        mListAll = new ArrayList<>();
        mList = new ArrayList<>();
        mDevcode = ((MyApplication) getApplication()).getDevcode();//设备识别码
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();//时间戳
        mUserId = ((MyApplication) getApplication()).getUserId();//用户id

        mLocationCity=getIntent().getStringExtra("city");
        if (StringUtil.isEmpty(mLocationCity)){
            mLocationCity="";
        }

        if (StringUtil.isEmpty(mUserId)){
            mUserId="0";
        }
        sendMsg(paging,mLocationCity);
        travelsSmart.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                flag = false;
                ++paging;
                sendMsg(paging,mLocationCity);
                travelsSmart.finishLoadmore();
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                travelsSmart.setLoadmoreFinished(false);//设置之后，将不会再触发加载事件
                mUserId = ((MyApplication) getApplication()).getUserId();//用户id
                if (StringUtil.isEmpty(mUserId)){
                    mUserId="0";
                }
                flag = true;
                paging = 0;
                sendMsg(paging,mLocationCity);
                travelsSmart.finishRefresh();
            }
        });


        travelsRecycler.setLayoutManager(new LinearLayoutManager(ReadingTravelsActivity.this, LinearLayoutManager.VERTICAL, false));
        travelsAdapter = new ReadingTravelsAdapter(this, mListAll, mList);
        travelsRecycler.setAdapter(travelsAdapter);
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }


    @OnClick(R.id.travels_read_book)
    public void onViewClicked() {
        if (Build.VERSION.SDK_INT < 23) {
            startActivity(new Intent(ReadingTravelsActivity.this,RecordVideoActivity.class));
        } else {
            //申请权限
            permissionHelper = new PermissionHelper(this, new PermissionHelper.OnAlterApplyPermission() {
                @Override
                public void OnAlterApplyPermission() {
                    startActivity(new Intent(ReadingTravelsActivity.this,RecordVideoActivity.class));
                }

                @Override
                public void cancelListener() {

                }
            }, permissionModels);
            permissionHelper.applyPermission();
        }
    }
    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private void sendMsg(int pag,String city) {
        boolean timeFlag = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
        if (timeFlag) {
            String sign = Md5Utils.createMD5("city="+city+"devcode=" + mDevcode + "p=" + pag + "timestamp=" + mTimestamp+"uid="+mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("signature", sign)
                    .add("devcode", mDevcode)
                    .add("uid", mUserId)
                    .add("city", city)
                    .add("p", String.valueOf(pag))
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.READ_VIDEO_BOOK, requestBody, new HttpCallBack(new ReadBookEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void writeBootBean(ReadBookEvent event) {
        if (event.isTimeOut()) {
            mLoadDialog.dismiss();//取消动画
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        ReadBookBean bean = event.getData();
        if (bean.getStatus() == 0) {
            if (flag) {
                mList.clear();
                mListAll.clear();
                mList.add(bean);
                mListAll.addAll(bean.getData().getData_q());
                travelsRecycler.setAdapter(travelsAdapter);
                travelsAdapter.notifyDataSetChanged();
                travelsSmart.finishRefresh();//停止刷新
            } else {
                if (bean.getData().getData_q().size() == 0) {
                    travelsSmart.setLoadmoreFinished(true);//设置之后，将不会再触发加载事件
                } else {
                    //添加适配器
                    mListAll.addAll(bean.getData().getData_q());
                    //添加适配器
                    travelsAdapter.notifyDataSetChanged();
                    travelsSmart.finishLoadmore();//停止加载
                }
            }
            mLoadDialog.dismiss();//取消动画
        } else {
            mLoadDialog.dismiss();//取消动画
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this,bean.getStatus());
        }
    }
    //广播接收内部类
    private class MyBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("notify")) {
                initView();
//            //在android端显示接收到的广播内容
//            Toast.makeText(getActivity(), author, 1).show();
                //在结束时可取消广播
               unregisterReceiver(this);
            }
        }
    }

}
