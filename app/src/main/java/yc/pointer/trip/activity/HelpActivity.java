package yc.pointer.trip.activity;

import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;
//import org.apache.http.util.EncodingUtils;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import yc.pointer.trip.R;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.base.BaseBean;
import yc.pointer.trip.bean.eventbean.ReceiverBean;
import yc.pointer.trip.event.HelpEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.DialogKnow;
import yc.pointer.trip.view.ToolbarWrapper;

import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by moyan on 2017/9/1.
 */
public class HelpActivity extends BaseActivity {
    //    @BindView(R.id.back_button)
//    ImageView backButton;
    @BindView(R.id.standard_toolbar_title)
    TextView standardToolbarTitle;
    @BindView(R.id.satandard_toolbar_right)
    TextView satandardToolbarRight;
    @BindView(R.id.edit_feedback)
    EditText editFeedback;//输入框
    @BindView(R.id.text_count)
    TextView textCount;//输入字数统计
    @BindView(R.id.help_text)
    TextView helpcContent;//帮助说明

    private String feedback;

    private String mDevcode;//设备识别码
    private long mTimestamp;//时间戳
    private String userID;//用户ID
    private boolean judgeTimeDev;

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_help;
    }

    @Override
    protected void initView() {
        new ToolbarWrapper(this);
        standardToolbarTitle.setText("帮助反馈");
        satandardToolbarRight.setText("提交");
        mDevcode = ((MyApplication) getApplication()).getDevcode();
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();
        userID = ((MyApplication) getApplication()).getUserId();
        //判断是否获取时间戳和设备识别码
        judgeTimeDev = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
        editFeedback.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = editFeedback.getText().toString().trim();
                int length = text.length();
                if (length < 140) {
                    textCount.setText(String.valueOf(length));
                    textCount.setTextColor(Color.parseColor("#1FBB07"));
                } else if (length >= 140) {
                    textCount.setText("140");
                    textCount.setTextColor(Color.parseColor("#d7013a"));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        InputStream inputStream = null;
        inputStream = getResources().openRawResource(R.raw.helpresouse);//获取TXT文件
        //获取文件的字节数
//            int lenght = 0;
//            lenght = inputStream.available();
//            //创建byte数组
//            byte[]  buffer = new byte[lenght];
//            //将文件中的数据读到byte数组中
//            inputStream.read(buffer);
        String content = StringUtil.getString(inputStream);
        helpcContent.setText(content);

        //IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction("刷新");
        //registerReceiver(new MyBroadcastReciver(), intentFilter);
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }


    @OnClick({R.id.satandard_toolbar_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
//            case R.id.back_button://返回
//                finish();
//                break;
            case R.id.satandard_toolbar_right://提交
                feedback = editFeedback.getText().toString().trim();
                if (StringUtil.isEmpty(userID)) {
                    Intent intentPerson = new Intent(HelpActivity.this, LoginActivity.class);
                    intentPerson.putExtra("logFlag", "help");
                    startActivity(intentPerson);
                } else {
                    if (!StringUtil.isEmpty(feedback)) {
                        //提交网络
                        commitFeedback();
                    } else {
                        Toast.makeText(this, "反馈信息不能为空", Toast.LENGTH_SHORT).show();
                    }
                }

                break;
        }
    }



    /**
     * 提交信息至服务器
     */
    private void commitFeedback() {
        if (judgeTimeDev) {
            String sign = Md5Utils.createMD5("devcode=" + mDevcode + "info=" + feedback + "timestamp=" + mTimestamp + "uid=" + userID + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", mDevcode)
                    .add("info", feedback)
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", userID)
                    .add("signature", sign)
                    .build();

            OkHttpUtils.getInstance().post(URLUtils.COMMIT_FEEDBACK, requestBody, new HttpCallBack(new HelpEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void commitMsg(HelpEvent helpEvent) {
        if (helpEvent.isTimeOut()) {
            Toast.makeText(this, "网络状态异常，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }
        BaseBean bean = helpEvent.getData();
        if (bean.getStatus() == 0) {
            //我知道了
            new DialogKnow(HelpActivity.this, R.style.user_default_dialog, new DialogKnow.CallBackListener() {
                @Override
                public void onClickListener() {
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);
                }
            }).setMsg("提交成功，我们会根据您的问题尽快处理").setPositiveButton("我知道了").show();
        } else {
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }

    //广播接收内部类
//    private class MyBroadcastReciver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals("刷新")) {
//                String author = intent.getStringExtra("receiver");
//                if (!StringUtil.isEmpty(author) && author.equals("1")) {
//                    initView();
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
     * bind change collection coupon fans follow login newPerson system ver 等activity发送过来
     *
     * @param receiverBean receiver 是1
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(ReceiverBean receiverBean) {
        if (receiverBean != null && receiverBean.getReceiver().equals("1")) {
            //initView();
            userID = MyApplication.mApp.getUserId();
        }
    }
}
