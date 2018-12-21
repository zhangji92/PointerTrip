package yc.pointer.trip.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.*;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import yc.pointer.trip.R;
import yc.pointer.trip.adapter.ComplainAdapter;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.base.BaseBean;
import yc.pointer.trip.bean.ComplainTagBean;
import yc.pointer.trip.event.CommintComplainEvent;
import yc.pointer.trip.event.ComplainEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.PermissionHelper;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.DialogKnow;
import yc.pointer.trip.view.ToolbarWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moyan on 2017/9/8.
 * 投诉界面
 */
public class ComplainActivity extends BaseActivity {
    @BindView(R.id.call_customer)
    LinearLayout callCustomer;
    @BindView(R.id.customer_phone)
    TextView customerNumber;
    @BindView(R.id.complain_tag)
    GridView complainTag;
    @BindView(R.id.edit_feedback)
    EditText editFeedback;
    @BindView(R.id.commint_complain)
    Button commintComplain;
    private PermissionHelper mHelper;
    private PermissionHelper.PermissionModel[] permissionModels = {new PermissionHelper.PermissionModel(1, Manifest.permission.CALL_PHONE, "拨打电话")
    };
    private boolean isCall = true;

    private List<ComplainTagBean.DataBean> data;

    private String mDevcode;//设备识别码
    private long mTimestamp;//时间戳
    private String userID;//用户ID
    private boolean judgeTimeDev;
    private ComplainAdapter adapter;
    private List<String> list = new ArrayList<>();
    private String infoTag;
    private String oid;


    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_complain;
    }

    @Override
    protected void initView() {
        new ToolbarWrapper(this).setCustomTitle(R.string.complain);

        mDevcode = ((MyApplication) getApplication()).getDevcode();
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();
        userID = ((MyApplication) getApplication()).getUserId();
        oid = getIntent().getStringExtra("oid");
        //判断是否获取时间戳和设备识别码
        judgeTimeDev = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
        if (!StringUtil.isEmpty(userID)) {
            //获取投诉标签以及投诉电话
            getMsg();
        }
        data = new ArrayList<>();
        adapter = new ComplainAdapter(this, data);
        complainTag.setAdapter(adapter);

        complainTag.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tagText = (TextView) view.findViewById(R.id.complain_choose_tag);
                data.get(position).setSelect(!data.get(position).isSelect());
                if (data.get(position).isSelect()) {
                    list.add(data.get(position).getTitle());
                    tagText.setSelected(true);
                    tagText.setTextColor(Color.parseColor("#d60337"));
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).equals(data.get(position).getTitle())) {
                            list.remove(i);
                            tagText.setSelected(false);
                            tagText.setTextColor(Color.parseColor("#000000"));
                        }
                    }
                }

            }
        });
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }


    @OnClick({R.id.call_customer, R.id.commint_complain})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.call_customer:
                if (Build.VERSION.SDK_INT < 23) {
                    call();
                } else {
                    //申请权限
                    //Applypermission();
                    mHelper = new PermissionHelper(this, new PermissionHelper.OnAlterApplyPermission() {
                        @Override
                        public void OnAlterApplyPermission() {
                            call();
                        }

                        @Override
                        public void cancelListener() {
                            //取消后，无操作
                        }
                    }, permissionModels);
                    mHelper.applyPermission();
                }
                break;
            case R.id.commint_complain://提交投诉
                if (list.size() > 0) {
                    StringBuffer stringBuffer = new StringBuffer();
                    for (String tag : list) {
                        stringBuffer.append(tag);
                    }
                    infoTag = stringBuffer.toString();
                }
                String concreteComplain = editFeedback.getText().toString().trim();

                String info = infoTag + concreteComplain;
                if (!StringUtil.isEmpty(infoTag) || !StringUtil.isEmpty(concreteComplain)) {

                    //请求网络
                    commintComplain(info, oid);
                    commintComplain.setBackgroundColor(Color.parseColor("#b8b8b8"));
                    commintComplain.setClickable(false);
                } else {
                    Toast.makeText(this, "投诉原因不能未空", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    /**
     * 打电话
     */
    private void call() {
        final String phoneNum = customerNumber.getText().toString().trim();
        if (!StringUtil.isEmpty(phoneNum)) {
            new DialogKnow(ComplainActivity.this, R.style.user_default_dialog, new DialogKnow.CallBackListener() {
                @SuppressLint("MissingPermission")
                @Override
                public void onClickListener() {
                    if (!StringUtil.isEmpty(phoneNum)) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        Uri data = Uri.parse("tel:" + phoneNum);
                        intent.setData(data);
                        startActivity(intent);
                    }
                }
            }).setTitle("联系我们").setMsg(phoneNum).setPositiveButton("呼叫").show();
        } else {
            return;
        }


    }


    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mHelper.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 获取标签
     */
    private void getMsg() {
        if (judgeTimeDev) {
            String sign = Md5Utils.createMD5("devcode=" + mDevcode + "timestamp=" + mTimestamp + "uid=" + userID + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", mDevcode)
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", userID)
                    .add("signature", sign)
                    .build();

            OkHttpUtils.getInstance().post(URLUtils.GET_COMPLAIN, requestBody, new HttpCallBack(new ComplainEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getComplainTag(ComplainEvent complainEvent) {

        if (complainEvent.isTimeOut()) {
            Toast.makeText(this, "网络状态异常，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }
        ComplainTagBean complainTagBean = complainEvent.getData();
        String kf_phone = complainTagBean.getKf_phone();
        if (!StringUtil.isEmpty(kf_phone)) {
            customerNumber.setText(kf_phone);
        } else {
            customerNumber.setText("");
        }
        if (complainTagBean.getStatus() == 0) {

            data.addAll(complainTagBean.getData());
            complainTag.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        } else {
            Toast.makeText(this, complainTagBean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(ComplainActivity.this, complainTagBean.getStatus());
        }


    }

    /**
     * 提交
     */
    private void commintComplain(String info, String oid) {
        if (judgeTimeDev) {
            String sign = Md5Utils.createMD5("devcode=" + mDevcode + "info=" + info + "oid=" + oid + "timestamp=" + mTimestamp + "uid=" + userID + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", mDevcode)
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", userID)
                    .add("info", info)
                    .add("oid", oid)
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.COMMINT_COMPLAIN, requestBody, new HttpCallBack(new CommintComplainEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void commintMsg(CommintComplainEvent commintComplainEvent) {
        if (commintComplainEvent.isTimeOut()) {
            Toast.makeText(this, "网络状态异常，请稍后重试", Toast.LENGTH_SHORT).show();
            commintComplain.setClickable(true);
            return;
        }
        BaseBean data = commintComplainEvent.getData();
        if (data.getStatus() == 0) {
            new DialogKnow(ComplainActivity.this, R.style.user_default_dialog, new DialogKnow.CallBackListener() {
                @Override
                public void onClickListener() {
                    finish();
                }
            }).setMsg("提交成功，我们会根据您的投诉建议进行处理，由此为您带来不便，敬请谅解！").setPositiveButton("我知道了").show();
        } else {
            Toast.makeText(this, data.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, data.getStatus());
            commintComplain.setClickable(true);
        }
    }

}
