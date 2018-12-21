package yc.pointer.trip.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import yc.pointer.trip.R;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.base.BaseBean;
import yc.pointer.trip.event.CodeEvent;
import yc.pointer.trip.event.RecomposePaswEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.ToolbarWrapper;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by moyan on 2017/9/1.
 * 修改密码界面
 */
public class RecomposeActivity extends BaseActivity {

    @BindView(R.id.old_pasw)
    EditText oldPasw;//旧密码
    @BindView(R.id.new_pasw)
    EditText newPasw;//新密码
    @BindView(R.id.sure_pasw)
    EditText surePasw;//确认新密码
    @BindView(R.id.button_recompose)
    Button buttonRecompose;//修改密码按钮

    private String mDevcode;//设备识别码
    private long mTimestamp;//时间戳
    private String userID;//用户ID
    private boolean judgeTimeDev;
    private String oldpasw;
    private String newpasw;
    private String surepasw;


    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_recompose;
    }

    @Override
    protected void initView() {
        ToolbarWrapper toolbarWrapper = new ToolbarWrapper(this);
        toolbarWrapper.setCustomTitle(R.string.recompose_pasw);
        mDevcode = ((MyApplication) getApplication()).getDevcode();
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();
        userID = ((MyApplication) getApplication()).getUserId();
        //判断是否获取时间戳和设备识别码
        judgeTimeDev = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

    @OnClick(R.id.button_recompose)
    public void onViewClicked() {
        //请求网络
        getInfo();
    }
    private void recomposepassword(){

        if (judgeTimeDev){
            oldpasw= Md5Utils.createMD5(oldpasw + URLUtils.WK_PWD_KEY);
            newpasw=Md5Utils.createMD5(newpasw + URLUtils.WK_PWD_KEY);
            String str = Md5Utils.createMD5("devcode=" + mDevcode + "newPwd=" + newpasw +"oldPwd="+oldpasw+ "timestamp=" + mTimestamp + "uid="+userID + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("oldPwd", oldpasw)
                    .add("newPwd", newpasw)
                    .add("devcode", mDevcode)
                    .add("signature", str)
                    .add("uid",userID )
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.RECOMPOSE_PASSWORD, requestBody, new HttpCallBack(new RecomposePaswEvent() ));
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getRecomposeResult(RecomposePaswEvent recomposePaswEvent){
        if (recomposePaswEvent.isTimeOut()){
            Toast.makeText(this, "网络连接超时，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }
        BaseBean bean = recomposePaswEvent.getData();
        if (bean.getStatus()==0){
            Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    finish();
                }
            },2000);
        }else {
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this,bean.getStatus());
        }
    }

    /**
     * 获取信息
     */
    private void getInfo(){
        oldpasw = oldPasw.getText().toString().trim();
        newpasw = newPasw.getText().toString().trim();
        surepasw = surePasw.getText().toString().trim();
        if (!StringUtil.isEmpty(oldpasw)){
            if (newpasw.length()<6){
                Toast.makeText(this, "密码不得少于6位数", Toast.LENGTH_SHORT).show();
            }else {
                if (!StringUtil.isEmpty(newpasw)&&!StringUtil.isEmpty(surepasw)){
                    if (newpasw.equals(surepasw)){
                        //请求网络
                        recomposepassword();
                    }else {
                        Toast.makeText(this, "两次输入不一致，请认真核对", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(this, "新密码不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        }else {
            Toast.makeText(this, "原始密码不能为空", Toast.LENGTH_SHORT).show();
        }
    }

}
