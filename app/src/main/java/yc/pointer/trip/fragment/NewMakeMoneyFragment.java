package yc.pointer.trip.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import yc.pointer.trip.R;
import yc.pointer.trip.activity.AllTaskActivity;
import yc.pointer.trip.activity.ExplainWebActivity;
import yc.pointer.trip.activity.InvitionTaskActivity;
import yc.pointer.trip.activity.LinkTaskActivity;
import yc.pointer.trip.activity.LoginActivity;
import yc.pointer.trip.activity.MakeMoneyActivity;
import yc.pointer.trip.activity.RecordVideoActivity;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseFragment;
import yc.pointer.trip.bean.NewMakeMoneyFragmentBean;
import yc.pointer.trip.bean.eventbean.RefreshEarningAProfitBean;
import yc.pointer.trip.event.NewMakeMoneyFragmentEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.DensityUtil;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.CustomCircleImage;
import yc.pointer.trip.view.CustomProgress;
import yc.pointer.trip.view.FadingScrollView;

/**
 * Created by 张继
 * 2018/9/18
 * 14:50
 * 公司：
 * 描述：新版赚一赚
 */
public class NewMakeMoneyFragment extends BaseFragment {

    @BindView(R.id.nac_layout)
    View nacLayout;
    @BindView(R.id.scroll_view)
    FadingScrollView nacRoot;
    @BindView(R.id.navigate_title)
    TextView navigateTitle;//导航栏标题
    @BindView(R.id.tool_bar_line)
    ImageView line;//导航栏底部线
    private View decorView;//状态栏布局


    @BindView(R.id.new_make_head)
    CustomCircleImage newMakeHead;//头像
    @BindView(R.id.new_make_head_rl)
    RelativeLayout headRelative;
    @BindView(R.id.new_make_login)
    Button newMakeLogin;//登陆按钮
    @BindView(R.id.new_make_money)
    TextView newMakeMoney;//总计收益
    @BindView(R.id.new_make_progress_start)
    TextView newMakeProgressStart;//百分比头
    @BindView(R.id.new_make_progress)
    CustomProgress newMakeProgress;//百分比
    @BindView(R.id.new_make_progress_end)
    TextView newMakeProgressEnd;//比分比尾
    @BindView(R.id.new_make_content)
    TextView newMakeContent;//总计收益内容
    @BindView(R.id.new_make_task)
    ImageView newMakeTask;//任务赚一赚
    @BindView(R.id.new_make_task_content)
    TextView newMakeTaskContent;//任务内容
    @BindView(R.id.new_make_invite)
    ImageView newMakeInvite;//邀请赚一赚
    @BindView(R.id.new_make_invite_content)
    TextView newMakeInviteContent;//邀请内容
    @BindView(R.id.new_make_video)
    ImageView newMakeVideo;//视频赚一赚
    @BindView(R.id.new_make_video_content)
    TextView newMakeVideoContent;//视频内容
    @BindView(R.id.new_make_local)
    ImageView newMakeLocal;//当地赚一赚
    @BindView(R.id.new_make_local_content)
    TextView newMakeLocalContent;//当地内容
    @BindView(R.id.new_make_nick)
    TextView newMakeNick;//登陆成功后的昵称
    @BindView(R.id.new_make_location)
    TextView newMakeLocation;//登陆成功后的地址
    @BindView(R.id.new_make_introduce)
    TextView newMakeIntroduce;//登陆成功后的个性签名
    @BindView(R.id.new_make_vip)
    ImageView newMakeVip;//vip标志
    @BindView(R.id.new_make_nick_group)
    LinearLayout newMakeNickGroup;//昵称个性签名组

    private String mUserId;
    private long mTimestamp;
    private String mDevCode;
    private boolean islogin;
    private static final int REQUEST_CAMERA = 0;//权限code
    private int mType;
    private String invitionWebUrl = "";//邀请赚一赚URL

    @Override
    protected int getContentViewLayout() {
        return R.layout.fragment_new_make;
    }

    @Override
    protected void initView() {
        decorView = getActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        DensityUtil.setStatusBarColor(getActivity(), R.color.transparent);

        nacLayout.setAlpha(0);
        navigateTitle.setVisibility(View.GONE);
        line.setVisibility(View.GONE);
        nacRoot.setFadingView(getActivity(), nacLayout);//绑定渐变的View视图
//        nacRoot.setFadingHeightView(getActivity(), headRelative);//设定滑动距离为背景图片的距离
        nacRoot.setFadingHeight(370);
        nacRoot.setListener(new FadingScrollView.AlphaListener() {
            @Override
            public void statusBarIsWhite(boolean isWhite) {
                if (isWhite) {
                    //状态栏白色
                    if (Build.VERSION.SDK_INT >= 21) {
                        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    }
                    line.setVisibility(View.VISIBLE);
                    navigateTitle.setVisibility(View.VISIBLE);
                } else {
                    //沉浸式
                    if (Build.VERSION.SDK_INT >= 21) {
                        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                    }
                    line.setVisibility(View.GONE);
                    navigateTitle.setVisibility(View.GONE);


                }
            }
        });


        mUserId = MyApplication.mApp.getUserId();//用户id
        if (StringUtil.isEmpty(mUserId)) {
            mUserId = "0";
        }
        islogin = MyApplication.mApp.isIslogin();
        mTimestamp = MyApplication.mApp.getTimestamp();//时间戳
        mDevCode = MyApplication.mApp.getDevcode();//设备识别码
        getMakeAProfit(mUserId, mTimestamp, mDevCode);
    }


    @Override
    protected boolean needEventBus() {
        return true;
    }


    @OnClick({R.id.new_make_login, R.id.new_make_task, R.id.new_make_invite, R.id.new_make_video, R.id.new_make_local})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.new_make_login://去登陆
                //跳转登录
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra("logFlag", "makemoney");
                startActivityForResult(intent, 0);
                break;
            case R.id.new_make_task://任务赚一赚
                if (islogin == true) {

                    if (mType == 3) {//任务结束
                        Intent intentAllTask = new Intent(getActivity(), AllTaskActivity.class);
                        startActivity(intentAllTask);
//                        Toast.makeText(getActivity(), "任务结束", Toast.LENGTH_SHORT).show();
                    } else {//任务开始
                        startActivity(new Intent(getActivity(), LinkTaskActivity.class));
                        //}
                    }
                } else {
                    //跳转登录
                    Intent intentLogin = new Intent(getActivity(), LoginActivity.class);
                    intentLogin.putExtra("logFlag", "makemoney");
                    startActivityForResult(intentLogin, 0);
                }
                break;
            case R.id.new_make_invite://邀请 赚一赚
                if (islogin == true) {
                    if (!StringUtil.isEmpty(invitionWebUrl)) {
                        Intent intentInviteTask = new Intent(getActivity(), InvitionTaskActivity.class);
                        intentInviteTask.putExtra("loadUrl",invitionWebUrl);
//                        intentInviteTask.putExtra("url",invitionWebUrl);
                        startActivity(intentInviteTask);
                    }
                } else {
                    //跳转登录
                    Intent intentLogin = new Intent(getActivity(), LoginActivity.class);
                    intentLogin.putExtra("logFlag", "makemoney");
                    startActivityForResult(intentLogin, 0);
                }
                break;
            case R.id.new_make_video://视频赚一赚
                checkCameraPermission();
                break;
            case R.id.new_make_local://当地赚一赚
                Intent intenMakeMoney = new Intent(getActivity(), MakeMoneyActivity.class);
                startActivity(intenMakeMoney);
                break;
        }
    }

    /**
     * 跳转拍视频
     */
    private void IntentRecord() {
        if (judgeLanding("lookWorld")) {
            //跳转登录
            Intent intent = new Intent(getActivity(), RecordVideoActivity.class);
            intent.putExtra("logFlag", "lookWorld");
            startActivity(intent);
        }
    }

    /**
     * 判断是否登陆
     *
     * @param value 登陆之后跳转的标志
     * @return 未登录true 登陆false
     */
    private boolean judgeLanding(String value) {
        if (islogin != true) {
            //跳转登录
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.putExtra("logFlag", value);
            startActivity(intent);
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == 4) {
                initView();
            }
        }
    }

    /**
     * 请求后台获取赚一赚页面数据
     *
     * @param userId    用户id
     * @param timestamp 时间戳
     * @param devcode   设备识别码
     */
    private void getMakeAProfit(String userId, long timestamp, String devcode) {
        if (APPUtils.judgeTimeDev(getActivity(), devcode, timestamp)) {//判断设备识别码是否为空
            String str = Md5Utils.createMD5("devcode=" + devcode + "timestamp=" + timestamp + "uid=" + userId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", String.valueOf(devcode))
                    .add("timestamp", String.valueOf(timestamp))
                    .add("signature", str)
                    .add("uid", userId)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.NEW_MAKE_MONEY_DATE, requestBody, new HttpCallBack(new NewMakeMoneyFragmentEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMakeAProfit(NewMakeMoneyFragmentEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(getActivity(), "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }

        NewMakeMoneyFragmentBean data = event.getData();
        if (data.getStatus() == 0) {
            mType = data.getData().getType();
            int user_count = data.getData().getUser_count();//总用户量
            String userCount = StringUtil.conversion(user_count, 2);
            newMakeProgress.setMax(user_count);//最大进度
            newMakeProgressEnd.setText(userCount);//百分比总数
            String task_count = data.getData().getTask_count();
            newMakeTaskContent.setText("已有" + task_count + "位用户正在做任务");
            String inv_count = data.getData().getInv_count();
            newMakeInviteContent.setText("已有" + inv_count + "位用户成功邀请好友");
            String book_count = data.getData().getBook_count();
            newMakeVideoContent.setText("已有" + book_count + "位用户上传视频");
            String ord_count = data.getData().getOrd_count();
            newMakeLocalContent.setText("已有" + ord_count + "位用户接到订单");

            if (!islogin) {//未登录状态
                newMakeContent.setVisibility(View.GONE);
                newMakeVip.setVisibility(View.INVISIBLE);//未登录隐藏会员标志
                newMakeLogin.setVisibility(View.VISIBLE);//登陆按钮显示
                newMakeNickGroup.setVisibility(View.GONE);//昵称个性签名隐藏
                newMakeMoney.setText("登录可见");//总计收益不可见
                newMakeMoney.setTextColor(Color.parseColor("#FFD3D6DA"));
                newMakeProgress.setProgress(0);
                newMakeProgressStart.setBackgroundResource(R.drawable.new_make_text_back_gray);
                newMakeProgressEnd.setBackgroundResource(R.drawable.new_make_text_back_gray);
                invitionWebUrl = "";
            } else {//登陆状态
                setUserMessage(data);
                String inv_url = data.getData().getInv_url();
                if (!StringUtil.isEmpty(inv_url)) {
                    invitionWebUrl = URLUtils.BASE_URL + inv_url + mUserId;
                } else {
                    invitionWebUrl = "";
                }
                newMakeProgressStart.setText("0");//比分比开始0
                int rank = data.getData().getRank();//已超过xx用户
                newMakeProgress.setProgress(rank);//当前进度
                String conversion = StringUtil.conversion(rank, 2);
                newMakeProgress.setProgressText(conversion);//进度条文本
                newMakeContent.setText("您当前总计收益已超过" + conversion + "个指针用户");
                newMakeProgressStart.setBackgroundResource(R.drawable.new_make_text_back);
                newMakeProgressEnd.setBackgroundResource(R.drawable.new_make_text_back);
            }

        } else {
            Toast.makeText(getActivity(), data.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(getActivity(), data.getStatus());
        }
    }

    /**
     * 设置用户信息
     *
     * @param data
     */
    private void setUserMessage(NewMakeMoneyFragmentBean data) {
        newMakeContent.setVisibility(View.VISIBLE);
        newMakeVip.setVisibility(View.VISIBLE);//会员标志显示
        newMakeLogin.setVisibility(View.GONE);//登陆按钮隐藏
        newMakeNickGroup.setVisibility(View.VISIBLE);//昵称个性签名显示
        String pic = MyApplication.mApp.getUserBean().getPic();//头像
        String is_vip = MyApplication.mApp.getUserBean().getIs_vip();//是否时会员
        OkHttpUtils.displayGlideCircular(getActivity(), newMakeHead, pic, newMakeVip, is_vip);
        String u_all_money = data.getData().getU_all_money();//总计收益
        newMakeMoney.setText(u_all_money + "元");
        newMakeMoney.setTextColor(Color.parseColor("#45b0ff"));
        String nickname = MyApplication.mApp.getUserBean().getNickname();//昵称
        newMakeNick.setText(nickname);
        String location = MyApplication.mApp.getUserBean().getLocation();//地址
        newMakeLocation.setText(location);
        String sig = MyApplication.mApp.getUserBean().getSig();//个性签名
        if (!StringUtil.isEmpty(sig)) {
            newMakeIntroduce.setText(sig);
        } else {
            newMakeIntroduce.setText("简介：这个人很懒,暂时没有填写个人简介");
        }
    }

    //视频录制需要的权限(相机，录音，外部存储)
    private String[] VIDEO_PERMISSION = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private List<String> NO_VIDEO_PERMISSION = new ArrayList<String>();

    /**
     * 检测摄像头权限，具备相关权限才能继续
     */
    private void checkCameraPermission() {
        NO_VIDEO_PERMISSION.clear();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < VIDEO_PERMISSION.length; i++) {
                if (ActivityCompat.checkSelfPermission(getActivity(), VIDEO_PERMISSION[i]) != PackageManager.PERMISSION_GRANTED) {
                    NO_VIDEO_PERMISSION.add(VIDEO_PERMISSION[i]);
                }
            }
            if (NO_VIDEO_PERMISSION.size() == 0) {
                //无权限直接录制
                IntentRecord();
            } else {
                requestPermissions(NO_VIDEO_PERMISSION.toArray(new String[NO_VIDEO_PERMISSION.size()]), REQUEST_CAMERA);
            }
        } else {
            //无权限直接录制
            IntentRecord();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            boolean flag = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    flag = true;
                } else {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                //权限申请完毕
                IntentRecord();
            } else {
                Toast.makeText(getActivity(), "授权失败", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    /**
     * 登录后从绑定手机号页面发送更新消息
     *
     * @param bean
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshMakeMoney(RefreshEarningAProfitBean bean) {
        if (bean != null) {
            if (bean.getProfit().equals("刷新赚一赚")) {
                initView();
            }
        }
    }


}
