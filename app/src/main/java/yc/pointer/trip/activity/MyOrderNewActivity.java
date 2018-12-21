package yc.pointer.trip.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import yc.pointer.trip.R;
import yc.pointer.trip.adapter.MyOrderNewAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.eventbean.ChangTypeBean;
import yc.pointer.trip.bean.eventbean.OrderNotifyBean;
import yc.pointer.trip.bean.eventbean.RefreshEarningAProfitBean;
import yc.pointer.trip.fragment.GrabOrderfragment;
import yc.pointer.trip.fragment.PublishOrderFragment;

/**
 * Created by moyan on 2018/4/20.
 * 我的订单（新）
 */

public class MyOrderNewActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {


    @BindView(R.id.order_back)
    ImageView orderBack;
    @BindView(R.id.order_title)
    TextView orderTitle;
    @BindView(R.id.order_bar)
    Toolbar orderBar;
    @BindView(R.id.my_order_tab)
    TabLayout myOrderTab;
    @BindView(R.id.my_order_viewpager)
    ViewPager myOrderViewpager;
    @BindView(R.id.normal_myorder)
    LinearLayout normalMyorder;
    @BindView(R.id.verify_title)
    TextView verifyTitle;
    @BindView(R.id.button_go_verify)
    Button buttonGoVerify;
    @BindView(R.id.note_go_verify)
    TextView noteGoVerify;
    @BindView(R.id.go_verify)
    LinearLayout goVerify;
    private List<Fragment> datas = new ArrayList<>();

    private GrabOrderfragment grabOrderfragment;
    private PublishOrderFragment publishOrderFragment;
//    private UnpaidOrderFragment unpaidOrderFragment;
    private MyOrderNewAdapter adapter;

    private int orderStatus = 0;//订单状态

    private boolean broadcastType = true;

    private IntentFilter intentFilter;
    //private MyBroadcastReciver receiver;

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_my_order_new;
    }

    @Override
    protected void initView() {
        //标题栏相关
        orderTitle.setText("我的订单");
        //返回
        orderBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        String is_jie = MyApplication.mApp.getUserBean().getIs_jie();
        broadcastType=getIntent().getBooleanExtra("isGrab",true);//判断显示 已发订单 还是 已抢订单

        if (is_jie.equals("2")) {
            //显示正常
            normalMyorder.setVisibility(View.VISIBLE);
            goVerify.setVisibility(View.GONE);

            showViewPager();

        } else if (is_jie.equals("1")) {
            //审核中
            showVierfy("您的会员实名认证信息正在审核中", "点击查看", "我的订单");
        } else if (is_jie.equals("3")) {
            //审核失败
            showVierfy("您的会员实名认证信息审核未通过", "重新认证", "我的订单");
        } else {
            showVierfy("您尚未开通指针会员", "马上开通", "我的订单");
        }
        ////注册广播
        //intentFilter = new IntentFilter();
        //intentFilter.addAction("刷新赚一赚");
        //receiver = new MyBroadcastReciver();
        //registerReceiver(receiver, intentFilter);


    }

    /**
     * 显示正常的Viewpager布局
     *
     */
    private void showViewPager() {

        orderBar.inflateMenu(R.menu.my_order_menu);

        grabOrderfragment = new GrabOrderfragment();
        publishOrderFragment = new PublishOrderFragment();
//        unpaidOrderFragment = new UnpaidOrderFragment();

        datas.add(grabOrderfragment);
        datas.add(publishOrderFragment);
//        datas.add(unpaidOrderFragment);

        adapter = new MyOrderNewAdapter(getSupportFragmentManager(), datas);
        myOrderViewpager.setAdapter(adapter);

        myOrderTab.addTab(myOrderTab.newTab());
        myOrderTab.addTab(myOrderTab.newTab());

        myOrderTab.setupWithViewPager(myOrderViewpager, false);//这句话顺序不能错（先ADDTab，然后再settext，否则tab数量会错）

        myOrderTab.getTabAt(0).setText("已抢订单");
        myOrderTab.getTabAt(1).setText("已发订单");
//        myOrderTab.getTabAt(2).setText("未支付订单");
        myOrderTab.setFillViewport(true);

        myOrderTab.setOnTabSelectedListener(this);

        if (broadcastType) {
            myOrderViewpager.setCurrentItem(0);
        } else {
            myOrderViewpager.setCurrentItem(1);
        }
        orderBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.order_all:
                        orderStatus = 0;
                        break;
                    case R.id.order_pending:
                        orderStatus = 1;
                        break;
                    case R.id.order_not_begin:
                        orderStatus = 2;
                        break;
                    case R.id.order_have_hand:
                        orderStatus = 3;
                        break;
                    case R.id.order_end://结束
                        orderStatus = 4;
                        break;
                    case R.id.order_overdue://过期
                        orderStatus = 5;
                        break;
                    case R.id.order_cancel://取消
                        orderStatus = 6;
                        break;
                    case R.id.order_unpaid://待付款
                        orderStatus = 7;
                        break;
                }
                //Intent intent = new Intent();
                if (broadcastType) {
                    //intent.setAction("changeGrabDataType");
                    EventBus.getDefault().post(new ChangTypeBean("changeGrabDataType", orderStatus));
                } else {
                    //intent.setAction("changePublisDataType");
                    EventBus.getDefault().post(new ChangTypeBean("changePublisDataType", orderStatus));
                }
                //intent.putExtra("statusType", orderStatus);
                //sendBroadcast(intent);
                return true;
            }
        });
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        if (position == 0) {
            broadcastType = true;//更新已抢订单
        } else if (position == 1) {
            broadcastType = false;//更新已发订单
        }
//        adapter.notifyDataSetChanged();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    /**
     * 显示认证的方法
     */
    private void showVierfy(String title, String buttontext, String note) {
        normalMyorder.setVisibility(View.GONE);
        goVerify.setVisibility(View.VISIBLE);
        verifyTitle.setText(title);
        buttonGoVerify.setText(buttontext);
        String text = String.format(getResources().getString(R.string.note_verify), note);
        noteGoVerify.setText(text);
        buttonGoVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyOrderNewActivity.this, VerifyActivity.class);
                intent.putExtra("goVerify", "myOrder");
                startActivity(intent);
            }
        });
    }

    ////广播接收内部类
    //private class MyBroadcastReciver extends BroadcastReceiver {
    //    @Override
    //    public void onReceive(Context context, Intent intent) {
    //        String action = intent.getAction();
    //        if (action.equals("orderNotify")) {
    //            String notifyFlag = intent.getStringExtra("notifyFlag");
    //            if (!StringUtil.isEmpty(notifyFlag)) {
    //                //刷新已发数据
    //                if (notifyFlag.equals("bill")) {
    //                    myOrderTab.getTabAt(1).select();
    //                    myOrderViewpager.setCurrentItem(1);
    //                } else if (notifyFlag.equals("grabOrder")) {
    //                    myOrderTab.getTabAt(0).select();
    //                    myOrderViewpager.setCurrentItem(0);
    //                }
    //            }
    //        } else if (action.equals("刷新赚一赚")) {
    //            initView();
    //        }
    //    }
    //}

    /**
     * bill grab等activity发送
     *
     * @param bean
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void orderNotify(OrderNotifyBean bean) {
        if (bean != null) {
            if (bean.getOrderNotify().equals("bill")) {
                myOrderTab.getTabAt(1).select();
                myOrderViewpager.setCurrentItem(1);
                EventBus.getDefault().post(new ChangTypeBean("changePublisDataType", orderStatus));
            } else if (bean.getOrderNotify().equals("grabOrder")) {
                myOrderTab.getTabAt(0).select();
                myOrderViewpager.setCurrentItem(0);
                EventBus.getDefault().post(new ChangTypeBean("changeGrabDataType", orderStatus));
            }         //刷新已发数据
        }
    }

    /**
     * Bill order ver 发送过来接收
     *
     * @param bean
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshEarningAProfit(RefreshEarningAProfitBean bean) {
        if (bean != null) {
            if (bean.getProfit().equals("刷新赚一赚")) {
                initView();
            }
        }
    }
}
