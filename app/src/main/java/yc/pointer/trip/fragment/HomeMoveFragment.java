package yc.pointer.trip.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import yc.pointer.trip.R;
import yc.pointer.trip.activity.BindingPhoneActivity;
import yc.pointer.trip.activity.ExplainWebActivity;
import yc.pointer.trip.activity.LinkTaskActivity;
import yc.pointer.trip.activity.LoginActivity;
import yc.pointer.trip.activity.MyMoneyActivity;
import yc.pointer.trip.activity.ScanQRCodeActivity;
import yc.pointer.trip.activity.SearchActivity;
import yc.pointer.trip.activity.ShareWebActivity;
import yc.pointer.trip.activity.ThemeActionActivity;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseFragment;
import yc.pointer.trip.bean.DataAdBean;
import yc.pointer.trip.bean.HomeVideoNewDataBean;
import yc.pointer.trip.bean.NewHomeVideoData;
import yc.pointer.trip.bean.SaveMesgBean;
import yc.pointer.trip.bean.eventbean.FollowDotEvent;
import yc.pointer.trip.bean.eventbean.UpdateRedDotBean;
import yc.pointer.trip.behavior.HomeHeaderPagerBehavior;
import yc.pointer.trip.event.HomeInvitationEvent;
import yc.pointer.trip.event.ItemRecycleToTopEvent;
import yc.pointer.trip.event.LoginEventRefresh;
import yc.pointer.trip.event.MainFragmentRefreshEvent;
import yc.pointer.trip.event.NewHomeVideoEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.DensityUtil;
import yc.pointer.trip.untils.LocationUtil;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.MySQLiteDataBase;
import yc.pointer.trip.untils.SharedPreferencesUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.DialogSure;
import yc.pointer.trip.view.InvitationDialog;
import yc.pointer.trip.view.LoadDialog;

/**
 * Created by 张继
 * 2018/4/10
 * 11:17
 * 公司:天津悦程嘉和网络科技有限公司
 * 描述:新版首页0809
 */

public class HomeMoveFragment extends BaseFragment implements TabLayout.OnTabSelectedListener, HomeHeaderPagerBehavior.OnPagerStateListener {

    @BindView(R.id.home_header_pager)
    ImageView homeHeaderPager;
    @BindView(R.id.home_move_tab)
    TabLayout mTableLayout;
    @BindView(R.id.home_move_pager)
    ViewPager mNewsPager;
    @BindView(R.id.home_move_logo)
    ImageView mImgLogo;
    @BindView(R.id.home_move_search)
    ImageView mImgSearch;
    @BindView(R.id.home_move_smart)
    SmartRefreshLayout mSmartRefresh;
    @BindView(R.id.home_header_look)
    TextView homeHeaderLook;
    @BindView(R.id.home_header_record)
    TextView homeHeaderRecord;
    @BindView(R.id.home_header_book)
    TextView homeHeaderBook;
    @BindView(R.id.home_header_mybook)//邀请码点击
    TextView homeHeaderMybook;

    //    private String[] mTitle = {"精选", "关注", "骑行", "自驾", "深度", "极限", "美食", "户外", "景点点评", "民间"};
    private String[] mTitle = {"关注", "精选", "民间", "景点点评", "美食", "极限", "骑行", "自驾", "深度", "户外"};

    private List<Fragment> mFragments = new ArrayList<>();
    private HomeHeaderPagerBehavior mPagerBehavior;
    private int position;
    private View decorView;
    private boolean statusFlag = true;// 头部收起打开的标志  true：打开状态   false:闭合状态  改变状态栏颜色的标志
    private long mTimestamp;
    private String mDevcode;
    private String mUserId;
    private LoadDialog mLoadDialog;
    private boolean islogin;


    private NewHomeVideoData data;

    private List<DataAdBean> mList = new ArrayList();//banner数据集合
    private boolean mRecyclerToTopFlag;
    private Button mPositiveButton;
    private LocationUtil locationUtil;
    private TextView mTabLayoutTitle;


    public boolean isStatusFlag() {
        return statusFlag;
    }

    @Override
    protected int getContentViewLayout() {
        return R.layout.fragment_home_move;
    }

    @Override
    protected void initView() {


//        mUserId = MyApplication.mApp.getUserId();
        mUserId = SharedPreferencesUtils.getInstance().getString(getActivity(), "useId");
        mDevcode = MyApplication.mApp.getDevcode();
        mTimestamp = MyApplication.mApp.getTimestamp();
//        if (StringUtil.isEmpty(mUserId)) {
//            mUserId = "0";
//        }

        if (StringUtil.isEmpty(mUserId) || "not find".equals(mUserId)) {
            mUserId = "0";
        }
        if (!OkHttpUtils.isNetworkAvailable(getActivity())) {
//            mLoadDialog.dismiss();
            mList = MySQLiteDataBase.findDataAdAll();
            if (mList.size() > 0) {
                OkHttpUtils.displayImg(homeHeaderPager, mList.get(0).getPic());
            }
        } else {
            mLoadDialog = new LoadDialog(getActivity(), "正在加载...", R.drawable.load_animation);
            mLoadDialog.show();
            requestHomePageData(mUserId, mDevcode, mTimestamp, 0);
        }

//        //关闭上拉加载
        mSmartRefresh.setEnableLoadmore(false);
        decorView = getActivity().getWindow().getDecorView();
        mPagerBehavior = (HomeHeaderPagerBehavior) ((CoordinatorLayout.LayoutParams) homeHeaderPager.getLayoutParams()).getBehavior();
        mPagerBehavior.setPagerStateListener(this);
        mFragments.add(NewItemHomeFragmentFollow.newInstance(1));//关注
        mFragments.add(NewItemHomeFragment.newInstance(0));//精选
        mFragments.add(NewItemHomeFragmentFolk.newInstance(9));//民间
        mFragments.add(NewItemHomeFragmentScenic.newInstance(8));//景点点评
        mFragments.add(NewItemHomeFragmentFood.newInstance(6));//美食
        mFragments.add(NewItemHomeFragmentLimit.newInstance(5));//极限
        mFragments.add(NewItemHomeFragmentRiding.newInstance(2));//骑行
        mFragments.add(NewItemHomeFragmentDrive.newInstance(3));//自驾
        mFragments.add(NewItemHomeFragmentDepth.newInstance(4));//深度
        mFragments.add(NewItemHomeFragmentOutDoors.newInstance(7));//户外

        mTableLayout.addTab(mTableLayout.newTab());
        mTableLayout.addTab(mTableLayout.newTab(), true);
        mTableLayout.addTab(mTableLayout.newTab());
        mTableLayout.addTab(mTableLayout.newTab());
        mTableLayout.addTab(mTableLayout.newTab());
        mTableLayout.addTab(mTableLayout.newTab());
        mTableLayout.addTab(mTableLayout.newTab());
        mTableLayout.addTab(mTableLayout.newTab());
        mTableLayout.addTab(mTableLayout.newTab());
        mTableLayout.addTab(mTableLayout.newTab());
//
//
        for (int i = 0; i < mTitle.length; i++) {
            TabLayout.Tab tab = mTableLayout.getTabAt(i);//获得每一个tab
            tab.setCustomView(R.layout.system_message_tab_item);//给每一个tab设置view
            mTabLayoutTitle = (TextView) tab.getCustomView().findViewById(R.id.tab_item_title);
            mTabLayoutTitle.setText(mTitle[i]);//设置tab上的文字
            ImageView imageView = (ImageView) tab.getCustomView().findViewById(R.id.red_action);
            imageView.setVisibility(View.GONE);
            if (i == 1) {
                mTabLayoutTitle.setTextColor(getResources().getColor(R.color.jingxuan));
            }
        }
        mTableLayout.addOnTabSelectedListener(this);
        //设置可以滑动
        mTableLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mNewsPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTableLayout));


        mNewsPager.setAdapter(new FragmentPagerAdapter(getFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size() == 0 ? 0 : mFragments.size();
            }
        });
        mNewsPager.setCurrentItem(1);
        mSmartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mUserId = ((MyApplication) getActivity().getApplication()).getUserId();
                if (StringUtil.isEmpty(mUserId)) {
                    mUserId = "0";
                }
                requestHomePageData(mUserId, mDevcode, mTimestamp, 0);
                //EventBus.getDefault().post(new MainFragmentRefreshTypeEvent(true, 0));
                //mNewsPager.setCurrentItem(1);
                mSmartRefresh.finishRefresh();
            }
        });


    }

    @Override
    protected boolean needEventBus() {
        return true;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        position = tab.getPosition();
        mNewsPager.setCurrentItem(position);
        if (tab.getCustomView() != null) {
            mTabLayoutTitle = (TextView) tab.getCustomView().findViewById(R.id.tab_item_title);
            tab.getCustomView().findViewById(R.id.red_action).setVisibility(View.GONE);
            mTabLayoutTitle.setTextColor(getResources().getColor(R.color.jingxuan));
        }
        if (mRecyclerToTopFlag) {//如果事打开状态，所有视图界面回到顶部
            //recyclerview回到顶部
            EventBus.getDefault().post(new ItemRecycleToTopEvent(true));
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        if (tab.getCustomView() != null) {
            mTabLayoutTitle = (TextView) tab.getCustomView().findViewById(R.id.tab_item_title);
            mTabLayoutTitle.setTextColor(getResources().getColor(R.color.history_search));
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onPagerClosed() {
        mRecyclerToTopFlag = false;
        mImgLogo.setImageResource(R.mipmap.img_more_top);
        mSmartRefresh.setEnableRefresh(false);
        statusFlag = false;
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            DensityUtil.setStatusBarColor(getActivity(), R.color.colorTitle);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    @Override
    public void onPagerOpened() {
        mRecyclerToTopFlag = true;
        mImgLogo.setImageResource(R.mipmap.img_logo_home);
        mSmartRefresh.setEnableRefresh(true);
        statusFlag = true;
        //recyclerview回到顶部
        EventBus.getDefault().post(new ItemRecycleToTopEvent(true));
        if (Build.VERSION.SDK_INT >= 21) {
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void mainFragment(MainFragmentRefreshEvent event) {
        if (statusFlag) {
            if (event.isFlag()) {
                mSmartRefresh.autoRefresh();//自动刷新
            }
        }
    }

    /**
     * 从网络上获取首页信息
     *
     * @param userId    用户id
     * @param devcode   设备识别码
     * @param timestamp 时间戳
     * @param page      分页页数
     */
    private void requestHomePageData(String userId, String devcode, Long timestamp, int page) {
        boolean timeFlag = APPUtils.judgeTimeDev(getActivity(), devcode, timestamp);
        if (timeFlag) {
            String str = Md5Utils.createMD5("devcode=" + devcode + "p=" + page + "timestamp=" + timestamp + "type=" + 0 + "uid=" + userId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", String.valueOf(devcode))
                    .add("p", String.valueOf(page))
                    .add("timestamp", String.valueOf(timestamp))
                    .add("type", String.valueOf(0))
                    .add("signature", str)
                    .add("uid", userId)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.HOME_VIDEO_NEW_DATA, requestBody, new HttpCallBack(new NewHomeVideoEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void requestHomePageData(NewHomeVideoEvent event) {
        mLoadDialog.dismiss();
        if (event.isTimeOut()) {
            mLoadDialog.dismiss();
            Toast.makeText(getActivity(), "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        data = event.getData();
        if (data.getStatus() == 0) {
            mLoadDialog.dismiss();
            String pic = data.getData().getData_ad().get(0).getPic();//首页第一张图片
            OkHttpUtils.displayImg(homeHeaderPager, pic);
            NewHomeVideoData.UnmsgBean unmsg = data.getUnmsg();
            String att_msg_num = unmsg.getAtt_msg_num();
            followDot(att_msg_num);
            MySQLiteDataBase.deleteAdBean();
            MySQLiteDataBase.saveAd(data);
        } else if (data.getStatus() == 201) {
            mLoadDialog.dismiss();
            ((MyApplication) getActivity().getApplication()).setUserBean(null);
            ((MyApplication) getActivity().getApplication()).setIslogin(false);
            ((MyApplication) getActivity().getApplication()).setUserId("");
            SharedPreferencesUtils.getInstance().remove(getActivity(), "phone");
            SharedPreferencesUtils.getInstance().remove(getActivity(), "pad");
            APPUtils.intentLogin(getActivity(), data.getStatus());
        } else {
            mLoadDialog.dismiss();
            Toast.makeText(getActivity(), data.getMsg(), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 关注小红点的逻辑
     *
     * @param att_msg_num 关注的最新发布数量
     */
    private void followDot(String att_msg_num) {
        View customView = mTableLayout.getTabAt(0).getCustomView();
        ImageView redImg = customView.findViewById(R.id.red_action);
        if (!StringUtil.isEmpty(att_msg_num)) {
            if (!att_msg_num.equals("0")) {
                redImg.setVisibility(View.VISIBLE);
            } else {
                redImg.setVisibility(View.GONE);
            }
        } else {
            redImg.setVisibility(View.GONE);
        }
    }

    /**
     * 保存用户信息
     *
     * @param loginBean
     */
    private void savePersonMsg(SaveMesgBean loginBean) {
        EventBus.getDefault().post(new LoginEventRefresh(true));
        String uid = loginBean.getData().getUid();
        String loginPhone = loginBean.getData().getPhone();
        String is_order = loginBean.getData().getIs_order();
        String login_type = loginBean.getData().getLogin_type();
        String is_bd = loginBean.getData().getIs_bd();
        String bank_num = loginBean.getData().getBank_num();
        String alipay_num = loginBean.getData().getAlipay();

        int msg_num = loginBean.getData().getMsg_num();
        SharedPreferencesUtils.getInstance().putString(getActivity(), "loginPhone", loginPhone);
        SharedPreferencesUtils.getInstance().putString(getActivity(), "useId", uid);
        SharedPreferencesUtils.getInstance().putString(getActivity(), "loginType", login_type);
        SharedPreferencesUtils.getInstance().putString(getActivity(), "isOrder", is_order);
        String pic = loginBean.getData().getPic();
        if (!StringUtil.isEmpty(pic)) {
            SharedPreferencesUtils.getInstance().putString(getActivity(), "headImg", pic);
        }
        if (!StringUtil.isEmpty(is_bd) && is_bd.equals("1")) {
            ((MyApplication) getActivity().getApplication()).setUserIsBinding(true);
            SharedPreferencesUtils.getInstance().putBoolean(getActivity(), "isBinding", true);
        } else if (is_bd.equals("0")) {
            ((MyApplication) getActivity().getApplication()).setUserIsBinding(false);
            SharedPreferencesUtils.getInstance().putBoolean(getActivity(), "isBinding", false);
        }

        if (!StringUtil.isEmpty(bank_num)) {
            ((MyApplication) getActivity().getApplication()).setUserIsVerify(true);
        } else {
            ((MyApplication) getActivity().getApplication()).setUserIsVerify(false);
        }
        if (!StringUtil.isEmpty(is_order) && is_order.equals("1")) {
//            ((MyApplication) getApplication()).setUserIsBinding(true);
            SharedPreferencesUtils.getInstance().putBoolean(getActivity(), "isPayDeposit", true);
        } else if (is_order.equals("0")) {
//            ((MyApplication) getApplication()).setUserIsBinding(false);
            SharedPreferencesUtils.getInstance().putBoolean(getActivity(), "isPayDeposit", false);
        }
        if (!StringUtil.isEmpty(alipay_num)) {
            ((MyApplication) getActivity().getApplication()).setAlipayNumber(alipay_num);
        } else {
            ((MyApplication) getActivity().getApplication()).setAlipayNumber("");
        }

        ((MyApplication) getActivity().getApplication()).setIslogin(true);
        ((MyApplication) getActivity().getApplication()).setUserBean(loginBean.getData());
        ((MyApplication) getActivity().getApplication()).setUserId(uid);
        //更新活动标识
        if (msg_num <= 0) {
            EventBus.getDefault().post(new UpdateRedDotBean(false));

            //Intent intent1 = new Intent();
            //intent1.setAction("更新活动标识");
            if (msg_num <= 0) {
                EventBus.getDefault().post(new UpdateRedDotBean(false));
                //intent1.putExtra("action", "gone");
            } else {
                //MainActivity 我的 RadioButton 上面的小红点
                EventBus.getDefault().post(new UpdateRedDotBean(true));
                //intent1.putExtra("action", "visible");
            }


        }
    }


    @OnClick({R.id.home_header_look, R.id.home_header_record, R.id.home_header_book,
            R.id.home_header_mybook, R.id.home_header_pager, R.id.home_move_logo, R.id.home_move_search})
    public void onViewClicked(View view) {
//        mUserId = SharedPreferencesUtils.getInstance().getString(getActivity(), "useId");
        mUserId = MyApplication.mApp.getUserId();

        if (!StringUtil.isEmpty(mUserId) && !mUserId.equals("not find")) {
            islogin = true;
        } else {
            islogin = false;
        }
        switch (view.getId()) {
            case R.id.home_move_logo://点击打开头部
                if (mPagerBehavior != null && mPagerBehavior.isClosed()) {
                    mPagerBehavior.openPager();
                }
                break;
            case R.id.home_move_search://跳转到搜索界面
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.home_header_look://旅赚训练营
                if (islogin == true) {
                    //startActivity(new Intent(getActivity(), MyMoneyActivity.class));
                    startActivity(new Intent(getActivity(), ShareWebActivity.class));
                } else {
                    //跳转登录
                    Intent intentlogin = new Intent(getActivity(), LoginActivity.class);
                    intentlogin.putExtra("logFlag", "shareweb");
                    startActivity(intentlogin);
                }

                break;
            case R.id.home_header_record://认证页面
                if (!islogin) {
                    mUserId = "0";
                }
                Intent intentVerify = new Intent(getActivity(), ExplainWebActivity.class);
                intentVerify.putExtra("title", "会员说明");
                intentVerify.putExtra("url", URLUtils.BASE_URL + "/Home/Book/agreement?uid=" + mUserId);
                startActivity(intentVerify);
                break;
            case R.id.home_header_book://扫一扫
                if (islogin == true) {
//                    startActivity(new Intent(getActivity(), MyReserveActivity.class));
                    // 创建IntentIntegrator对象
                    IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity());
                    intentIntegrator.setCaptureActivity(ScanQRCodeActivity.class);//设置扫描二维码的界面
                    /**
                     * 设置扫描的样式
                     *  IntentIntegrator.ALL_CODE_TYPES//全部类型
                     IntentIntegrator.PRODUCT_CODE_TYPES//商品码类型
                     IntentIntegrator.ONE_D_CODE_TYPES//一维码类型
                     IntentIntegrator.QR_CODE_TYPES//二维码类型
                     IntentIntegrator.DATA_MATRIX_TYPES//数据矩阵类型
                     *
                     */
//                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
//                intentIntegrator.setPrompt("请将二维码放入扫描框，即可完成扫描");//设置提示信息
                    intentIntegrator.setOrientationLocked(true);//设置方向锁（默认为true）
                    intentIntegrator.setCameraId(0);//设置摄像头Id  0：后置  1：前置
                    intentIntegrator.setBeepEnabled(false);//设置是否有声音
//                 开始扫描
                    intentIntegrator.initiateScan();
                } else {
                    //跳转登录
                    Intent intentlogin = new Intent(getActivity(), LoginActivity.class);
//                    intentlogin.putExtra("logFlag", "myReservation");
                    startActivity(intentlogin);
                }
                break;
            case R.id.home_header_mybook://跳转填写邀请码

                if (islogin == true) {
                    boolean isBinding = MyApplication.mApp.isUserIsBinding();
                    SaveMesgBean.DataBean userBean = MyApplication.mApp.getUserBean();
                    String pic = userBean.getPic();
                    String invitation_code = userBean.getInvitation_code();//本人邀请码
                    String i_invitation_code = userBean.getI_invitation_code();//朋友邀请码
                    String is_jie = userBean.getIs_jie();
                    String is_vip = userBean.getIs_vip();
                    if (isBinding) {
                        new InvitationDialog(getActivity(), R.style.user_default_dialog, new InvitationDialog.CallBackListener() {
                            @Override
                            public void onClickListener(InvitationDialog dialogSure, boolean trueEnable, String code, Button positiveButton) {
                                mPositiveButton = positiveButton;
                                if (trueEnable) {
                                    getInvitationCode(code, mUserId, mDevcode, mTimestamp);
                                }
                            }
                        }).setHeaderUrl(pic)
                                .setVerifyResult(is_jie)
                                .setWhoCode(invitation_code)
                                .setCode(i_invitation_code)
                                .setVipType(is_vip)
                                .show();
                    } else {
                        new DialogSure(getActivity(), R.style.user_default_dialog, new DialogSure.CallBackListener() {
                            @Override
                            public void onClickListener(DialogSure dialogSure, boolean trueEnable) {
                                if (trueEnable) {
                                    Intent intent = new Intent(getActivity(), BindingPhoneActivity.class);
                                    intent.putExtra("logFlag", "personMessage");
                                    intent.putExtra("uid", mUserId);
                                    startActivity(intent);
                                }
                            }
                        }).setTitle("温馨提示")
                                .setMsg("您的账号并未完成手机绑定，无法填写用户邀请码，敬请谅解")
                                .setPositiveButton("去绑定")
                                .setNegativeButton("那算了")
                                .show();
                    }

                    //startActivity(new Intent(getActivity(), LinkTaskActivity.class));
                } else {
                    //跳转登录
                    Intent intentlogin = new Intent(getActivity(), LoginActivity.class);
                    //intentlogin.putExtra("logFlag", "myCollection");
                    startActivity(intentlogin);
                }
                break;
            case R.id.home_header_pager://跳转活动详情
                if (!OkHttpUtils.isNetworkAvailable(getActivity())) {
                    Toast.makeText(getActivity(), "请检查网络", Toast.LENGTH_SHORT).show();
                } else {
                    if (data != null && data.getData().getData_ad().size() > 0) {
                        String aid = data.getData().getData_ad().get(0).getAid();
                        String title = data.getData().getData_ad().get(0).getTitle();
                        if (islogin == true) {
                            Intent intentAction = new Intent(getActivity(), ThemeActionActivity.class);
                            if (!StringUtil.isEmpty(aid)) {
                                intentAction.putExtra("aid", aid);
                                intentAction.putExtra("title", title);
                            }
                            startActivity(intentAction);
                        } else {
                            Intent intentAction = new Intent(getActivity(), LoginActivity.class);
                            if (!StringUtil.isEmpty(aid)) {
                                intentAction.putExtra("aid", aid);
                                intentAction.putExtra("title", title);
                            }
                            intentAction.putExtra("logFlag", "actionHome");
                            startActivity(intentAction);
                        }
                    }
                }
                break;
        }
    }


    private void getInvitationCode(String code, String userId, String devcode, long timestamp) {
        boolean timeFlag = APPUtils.judgeTimeDev(getActivity(), devcode, timestamp);
        if (timeFlag) {
            String str = Md5Utils.createMD5("code=" + code + "devcode=" + devcode + "timestamp=" + timestamp + "uid=" + userId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", String.valueOf(devcode))
                    .add("timestamp", String.valueOf(timestamp))
                    .add("signature", str)
                    .add("code", code)
                    .add("uid", userId)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.HOME_INVITATION_CODE, requestBody, new HttpCallBack(new HomeInvitationEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getInvitationCode(HomeInvitationEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(getActivity(), "网络状态异常", Toast.LENGTH_SHORT).show();
            return;
        }
        SaveMesgBean data = event.getData();
        if (data.getStatus() == 0) {
            String code = data.getData().getI_invitation_code();
            if (!StringUtil.isEmpty(code)) {//有邀请码
                mPositiveButton.setText("已填写");
                mPositiveButton.setBackgroundResource(R.drawable.invitation_dialog_not);
                mPositiveButton.setClickable(false);
                mPositiveButton.setEnabled(false);
            } else {//无邀请码
                mPositiveButton.setText("确定");
                mPositiveButton.setBackgroundResource(R.drawable.comment_button);
                mPositiveButton.setClickable(true);
                mPositiveButton.setEnabled(true);
            }
            savePersonMsg(data);
            Toast.makeText(getActivity(), data.getMsg(), Toast.LENGTH_SHORT).show();
        } else {
            mPositiveButton.setText("确定");
            mPositiveButton.setBackgroundResource(R.drawable.comment_button);
            mPositiveButton.setClickable(true);
            mPositiveButton.setEnabled(true);
            APPUtils.intentLogin(getActivity(), data.getStatus());
            Toast.makeText(getActivity(), data.getMsg(), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 保存数据
     *
     * @param bean
     */
    private ContentValues setAdValues(ContentValues values, HomeVideoNewDataBean.DataBean.DataAdBean bean) {
        values.put("aid", bean.getAid());
        values.put("title", bean.getTitle());
        values.put("pic", bean.getPic());
        return values;
    }

    /**
     * 查询缓存数据
     *
     * @param cursor
     */
    private HomeVideoNewDataBean.DataBean.DataAdBean selectAdColumn(Cursor cursor) {
        HomeVideoNewDataBean.DataBean.DataAdBean bean = new HomeVideoNewDataBean.DataBean.DataAdBean();
        String aid = cursor.getString(cursor.getColumnIndex("aid"));
        String title = cursor.getString(cursor.getColumnIndex("title"));
        String pic = cursor.getString(cursor.getColumnIndex("pic"));
        bean.setAid(aid);
        bean.setTitle(title);
        bean.setPic(pic);
        return bean;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            GSYVideoManager.releaseAllVideos();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void followDot(FollowDotEvent event) {
        if (event != null) {
            followDot(event.getAtt_Num_msg());
        }
    }
}
