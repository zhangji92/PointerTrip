package yc.pointer.trip.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import yc.pointer.trip.R;
import yc.pointer.trip.adapter.SystemMessageViewpagerAdapter;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.eventbean.ActionResultBean;
import yc.pointer.trip.bean.BaseUnMsgBean;
import yc.pointer.trip.bean.eventbean.NotifyMsgBean;
import yc.pointer.trip.bean.eventbean.ReceiverBean;
import yc.pointer.trip.fragment.CommentFragment;
import yc.pointer.trip.fragment.FansFragment;
import yc.pointer.trip.fragment.PraiseFragment;
import yc.pointer.trip.fragment.SystemFragment;
import yc.pointer.trip.view.ToolbarWrapper;

/**
 * Created by moyan on 2018/6/11.
 * 私信
 */

public class SystemMessageActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {


    @BindView(R.id.system_message_tab)
    TabLayout systemMessageTab;
    @BindView(R.id.system_message_viewpager)
    ViewPager systemMessageViewpager;
    @BindView(R.id.standard_toolbar)
    Toolbar standardToolbar;

    private TabLayout.Tab tabItem;//TabLayout子控件(通知,评论,点赞,粉丝)
    private TextView systemTitle;//tabItem的标题
    private List<Fragment> viewPagerFragments = new ArrayList<>();
    private SystemMessageViewpagerAdapter adapter;


    private SystemFragment systemFragment;//系统通知
    private CommentFragment commentFragment;//评论消息
    private PraiseFragment praiseFragment;//点赞消息
    private FansFragment fansFragment;//粉丝消息
    //private MyBroadcastReciver receiver;

    @Override
    protected int getContentViewLayout() {
        return R.layout.system_message_activity_layout;
    }

    @Override
    protected void initView() {
        //IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction("notifiMessage");
        //receiver = new MyBroadcastReciver();
        //registerReceiver(receiver, intentFilter);

        ToolbarWrapper toolbarWrapper = new ToolbarWrapper(this);
        toolbarWrapper.setTitle("消息列表");
        systemFragment = new SystemFragment();
        commentFragment = new CommentFragment();
        praiseFragment = new PraiseFragment();
        fansFragment = new FansFragment();

        viewPagerFragments.add(systemFragment);
        viewPagerFragments.add(commentFragment);
        viewPagerFragments.add(praiseFragment);
        viewPagerFragments.add(fansFragment);

        adapter = new SystemMessageViewpagerAdapter(getSupportFragmentManager(), viewPagerFragments);
        systemMessageViewpager.setAdapter(adapter);

        systemMessageTab.addTab(systemMessageTab.newTab(), true);
        systemMessageTab.addTab(systemMessageTab.newTab());
        systemMessageTab.addTab(systemMessageTab.newTab());
        systemMessageTab.addTab(systemMessageTab.newTab());
//这句话顺序不能错（先ADDTab，然后再settext，否则tab数量会错）
        systemMessageTab.setupWithViewPager(systemMessageViewpager);
        systemMessageTab.addOnTabSelectedListener(this);
        for (int i = 0; i < 4; i++) {
            tabItem = systemMessageTab.getTabAt(i);
            tabItem.setCustomView(R.layout.system_message_tab_item);
            systemTitle = (TextView) tabItem.getCustomView().findViewById(R.id.tab_item_title);
            switch (i) {
                case 0:
                    tabItem.getCustomView().findViewById(R.id.red_action).setVisibility(View.GONE);
                    systemTitle.setTextColor(getResources().getColor(R.color.new_my_money_button));
                    systemTitle.setText("通知");
                    break;
                case 1:
                    systemTitle.setText("评论");
                    break;
                case 2:
                    systemTitle.setText("点赞");
                    break;
                case 3:
                    systemTitle.setText("粉丝");
                    break;
            }
        }

//        systemMessageTab.getTabAt(0).setText("通知");
//        systemMessageTab.getTabAt(1).setText("评论");
//        systemMessageTab.getTabAt(2).setText("点赞");
//        systemMessageTab.getTabAt(3).setText("粉丝");
        systemMessageTab.getTabAt(0).getCustomView().setSelected(true);
//        systemMessageViewpager.setCurrentItem(0);
//        systemMessageTab.setFillViewport(true);
        standardToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //刷新个人信息，更新我的页面红点
                rufreshAndFinish();
            }
        });

    }

    /**
     * 发送刷新广播，更新个人信息以及上个页面红点
     */
    private void rufreshAndFinish() {
        //Intent intent = new Intent();
        //intent.setAction("刷新");
        //intent.putExtra("receiver", "1");
        //intent.putExtra("actionResult", "actionResult");
        //sendBroadcast(intent);
        EventBus.getDefault().post(new ReceiverBean("1"));
        EventBus.getDefault().post(new ActionResultBean("actionResult"));
        finish();
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (tab.getCustomView() != null) {
            systemTitle = (TextView) tab.getCustomView().findViewById(R.id.tab_item_title);
            tab.getCustomView().findViewById(R.id.red_action).setVisibility(View.GONE);
            systemTitle.setTextColor(getResources().getColor(R.color.new_my_money_button));
        }

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        if (tab.getCustomView() != null) {
            systemTitle = (TextView) tab.getCustomView().findViewById(R.id.tab_item_title);
            systemTitle.setTextColor(getResources().getColor(R.color.history_search));
        }

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            rufreshAndFinish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //广播接收内部类
    //private class MyBroadcastReciver extends BroadcastReceiver {
    //    @Override
    //    public void onReceive(Context context, Intent intent) {
    //        String action = intent.getAction();
    //        if (action.equals("notifiMessage")) {
    //            BaseUnMsgBean unMsgBean = (BaseUnMsgBean) intent.getSerializableExtra("UnMsgBean");
    //            for (int i = 0; i < 4; i++) {
    //                ImageView redAction = systemMessageTab.getTabAt(i).getCustomView().findViewById(R.id.red_action);
    //                switch (i) {
    //                    case 0:
    //                        if (unMsgBean.getNews().equals("0")) {
    //                            redAction.setVisibility(View.GONE);
    //                        } else {
    //                            redAction.setVisibility(View.VISIBLE);
    //                        }
    //                        break;
    //                    case 1:
    //                        if (unMsgBean.getComment().equals("0")) {
    //                            redAction.setVisibility(View.GONE);
    //                        } else {
    //                            redAction.setVisibility(View.VISIBLE);
    //                        }
    //                        break;
    //                    case 2:
    //                        if (unMsgBean.getZan().equals("0")) {
    //                            redAction.setVisibility(View.GONE);
    //                        } else {
    //                            redAction.setVisibility(View.VISIBLE);
    //                        }
    //                        break;
    //                    case 3:
    //                        if (unMsgBean.getFans().equals("0")) {
    //                            redAction.setVisibility(View.GONE);
    //                        } else {
    //                            redAction.setVisibility(View.VISIBLE);
    //                        }
    //                        break;
    //                }
    //            }
    //
    //        }
    //
    //    }
    //}

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void NotifyMsg(NotifyMsgBean bean) {
        if (bean != null && bean.getmNotifyMsg().equals("notifiMessage")) {
            BaseUnMsgBean unMsgBean = bean.getBean();
            for (int i = 0; i < 4; i++) {
                ImageView redAction = systemMessageTab.getTabAt(i).getCustomView().findViewById(R.id.red_action);
                switch (i) {
                    case 0:
                        if (unMsgBean.getNews().equals("0")) {
                            redAction.setVisibility(View.GONE);
                        } else {
                            redAction.setVisibility(View.VISIBLE);
                        }
                        break;
                    case 1:
                        if (unMsgBean.getComment().equals("0")) {
                            redAction.setVisibility(View.GONE);
                        } else {
                            redAction.setVisibility(View.VISIBLE);
                        }
                        break;
                    case 2:
                        if (unMsgBean.getZan().equals("0")) {
                            redAction.setVisibility(View.GONE);
                        } else {
                            redAction.setVisibility(View.VISIBLE);
                        }
                        break;
                    case 3:
                        if (unMsgBean.getFans().equals("0")) {
                            redAction.setVisibility(View.GONE);
                        } else {
                            redAction.setVisibility(View.VISIBLE);
                        }
                        break;
                }
            }
        }
    }

    /**
     * 以下的几个方法用来，让fragment能够监听touch事件
     */
    private ArrayList<MyOnTouchListener> onTouchListeners = new ArrayList<MyOnTouchListener>(
            10);

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (MyOnTouchListener listener : onTouchListeners) {
            listener.onTouch(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void registerMyOnTouchListener(MyOnTouchListener myOnTouchListener) {
        onTouchListeners.add(myOnTouchListener);
    }

    public void unregisterMyOnTouchListener(MyOnTouchListener myOnTouchListener) {
        onTouchListeners.remove(myOnTouchListener);
    }

    public interface MyOnTouchListener {
        boolean onTouch(MotionEvent ev);
    }


}

