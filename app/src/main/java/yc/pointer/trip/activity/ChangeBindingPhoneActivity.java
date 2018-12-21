package yc.pointer.trip.activity;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import yc.pointer.trip.R;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.eventbean.ReceiverBean;
import yc.pointer.trip.untils.SharedPreferencesUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.ToolbarWrapper;

/**
 * Created by moyan on 2018/1/10.
 * 更改手机号
 */

public class ChangeBindingPhoneActivity extends BaseActivity {

    @BindView(R.id.standard_toolbar_title)
    TextView standardToolbarTitle;
    @BindView(R.id.satandard_toolbar_right)
    TextView satandardToolbarRight;
    @BindView(R.id.standard_toolbar)
    Toolbar standardToolbar;
    @BindView(R.id.now_binding_number)
    TextView nowBindingNumber;
    @BindView(R.id.now_binding_introduce)
    TextView bindingIntroduce;
    @BindView(R.id.change_commite)
    Button changeCommite;
    private String bindingNumber;

    @Override
    protected int getContentViewLayout() {
        return R.layout.change_binding_phone;
    }

    @Override
    protected void initView() {

        ToolbarWrapper toolbarWrapper = new ToolbarWrapper(this);
        toolbarWrapper.setCustomTitle(R.string.binding);
//        bindingNumber = getIntent().getStringExtra("bindingNumber");
        bindingNumber = SharedPreferencesUtils.getInstance().getString(this, "loginPhone");
        if (!StringUtil.isEmpty(bindingNumber)) {
            nowBindingNumber.setText("当前用户绑定手机号码：" + bindingNumber);
        }

        //IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction("刷新");
        //registerReceiver(new MyBroadcastReciver(), intentFilter);
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }


    @OnClick(R.id.change_commite)
    public void onViewClicked() {
        Intent intent = new Intent(this, ChangeFirstActivity.class);
        intent.putExtra("bindingNumber", bindingNumber);
        startActivity(intent);
    }

    //    //广播接收内部类
//    private class MyBroadcastReciver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals("刷新")) {
//                String author = intent.getStringExtra("receiver");
//                if (!StringUtil.isEmpty(author) && author.equals("1")) {
//                    initView();
//                }else {
//                    finish();
//                }
////            //在android端显示接收到的广播内容
////            Toast.makeText(getActivity(), author, 1).show();
//
//                //在结束时可取消广播
//                unregisterReceiver(this);
//            }
//        }
//    }

    /**
     * bind change collection coupon fans follow login system ver 等activity发送过来
     * @param receiverBean receiver 是1
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(ReceiverBean receiverBean) {
        if (receiverBean != null && receiverBean.getReceiver().equals("1")) {
            initView();
        } else {
            finish();
        }
    }
}
