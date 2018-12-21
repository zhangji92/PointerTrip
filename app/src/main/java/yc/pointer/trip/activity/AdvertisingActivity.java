package yc.pointer.trip.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.FormBody;
import okhttp3.RequestBody;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import yc.pointer.trip.R;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.activity.MainActivity;
import yc.pointer.trip.bean.AdvertBean;
import yc.pointer.trip.event.AdvertEvent;
import yc.pointer.trip.event.CouponUseEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.AppDavikActivityMgr;
import yc.pointer.trip.untils.DensityUtil;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.SharedPreferencesUtils;
import yc.pointer.trip.untils.StringUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 广告页面
 */
public class AdvertisingActivity extends AppCompatActivity {

    @BindView(R.id.pass_text)
    TextView passText;
    @BindView(R.id.pass)
    RelativeLayout pass;
    @BindView(R.id.advert_pic)
    ImageView advertPic;

    private int second = 3;
    private Unbinder bind;
    private long mTimestamp;//时间戳
    private String mDevcode;//手机识别码
    private Timer timer = new Timer();
    private boolean isAction = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppDavikActivityMgr.getScreenManager().addActivity(this);//当前的activity加入栈中
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_advertising);
        bind = ButterKnife.bind(this);
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();
        mDevcode = ((MyApplication) getApplication()).getDevcode();
        try {
            EventBus.getDefault().register(this);
        } catch (EventBusException e) {
//                Logger.e("该类中没有 接收 eventbus 回调的方法");
        }

        pass.setVisibility(View.GONE);
//        getAdvertPic();
        OkHttpUtils.getInstance().get(URLUtils.GET_ADVERT_PIC, new HttpCallBack(new AdvertEvent()));
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        second--;
//                        passText.setText("跳转 " + second);
//                        if (second <= 0) {
//                            timer.cancel();
//                            IntentMain();
//                        }
//                    }
//                });
//            }
//        }, 2000, 1000);
    }

    @OnClick(R.id.pass)
    public void onViewClicked() {
        timer.cancel();
        IntentMain();
    }

    /**
     * 跳转方法
     */
    private void IntentMain() {
        final boolean falg = SharedPreferencesUtils.getInstance().getBoolean(AdvertisingActivity.this, "BootPageActivity");
        if (falg) {
//            Intent intent = new Intent(AdvertisingActivity.this, MainActivity.class);
            Intent intent1 = new Intent(AdvertisingActivity.this, MainActivity.class);
            startActivity(intent1);
            finish();
        } else {
            startActivity(new Intent(AdvertisingActivity.this, BootPageActivity.class));
            finish();
        }
    }

    /**
     * 获取广告页数据
     */
    private void getAdvertPic() {
        boolean timeDev = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
        if (timeDev) {
            String signature = Md5Utils.createMD5("devcode=" + mDevcode + "timestamp=" + mTimestamp + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", mDevcode)
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("signature", signature)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.GET_ADVERT_PIC, requestBody, new HttpCallBack(new AdvertEvent()));

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getPic(AdvertEvent event) {
        if (event.isTimeOut()) {
            //TODO 加载缓存图片
            IntentMain();
//            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
        } else {
            AdvertBean data = event.getData();
            if (data.getStatus() == 0) {

                final String aid = data.getData().getAid();
                final String title = data.getData().getTitle();
                final String price = data.getData().getPrice();

//                Double height = Double.valueOf(data.getData().getHeight());
//                Double width = Double.valueOf(data.getData().getWidth());
//
//                int screenWidth = DensityUtil.getScreenWidth(this);
//                double imgHeight = screenWidth * (height / width);
//                RelativeLayout.LayoutParams layoutParamsImg = (RelativeLayout.LayoutParams) advertPic.getLayoutParams();
//                layoutParamsImg.height= (int) imgHeight;
//                layoutParamsImg.width=screenWidth;
//                advertPic.setLayoutParams(layoutParamsImg);
                OkHttpUtils.displayAdvertImg(advertPic, data.getData().getPic());
                advertPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final boolean falg = SharedPreferencesUtils.getInstance().getBoolean(AdvertisingActivity.this, "BootPageActivity");
                        if (falg) {
                            isAction = true;
                        } else {
                            isAction = false;
                        }
                    }
                });
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pass.setVisibility(View.VISIBLE);
                                second--;
                                passText.setText("跳转 " + second);
                                if (isAction) {
                                    timer.cancel();
                                    String userId = SharedPreferencesUtils.getInstance().getString(AdvertisingActivity.this, "useId");
                                    if (!StringUtil.isEmpty(price)&&!price.equals("0")) {
                                        if (!StringUtil.isEmpty(userId) && !userId.equals("not find")) {
                                            //TODO 拿到AId，传递到活动详情
                                            Intent intent=new Intent(AdvertisingActivity.this,ThemeActionActivity.class);
                                            intent.putExtra("backFlag","adverting");
                                            intent.putExtra("aid",aid);
                                            intent.putExtra("title",title);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Intent intentlogin = new Intent(AdvertisingActivity.this, LoginActivity.class);
                                            intentlogin.putExtra("logFlag", "action");
                                            intentlogin.putExtra("aid", aid);
                                            intentlogin.putExtra("title", title);
                                            startActivity(intentlogin);
                                            finish();
                                        }
                                    }else {
                                        Intent intentRed = new Intent(AdvertisingActivity.this, ExplainWebActivity.class);
                                        intentRed.putExtra("title", title);
                                        intentRed.putExtra("backFlag", "adverting");
                                        intentRed.putExtra("url", URLUtils.BASE_URL + "/Home/Book/hb?id=1");
                                        startActivity(intentRed);
                                    }
                                timer.cancel();
                                    finish();
                            }else{
                                if (second <= 0) {
                                    timer.cancel();
                                    IntentMain();
                                }
                            }
                        }
                    });
                }
            },2000, 1000);

        } else{
            IntentMain();
//                Toast.makeText(this, data.getMsg(), Toast.LENGTH_SHORT).show();
        }
    }


}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
        timer.cancel();
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            // Logger.e("该类没有注册 eventbus");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }
}
