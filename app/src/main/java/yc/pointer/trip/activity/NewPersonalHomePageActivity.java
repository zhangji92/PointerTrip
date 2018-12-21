package yc.pointer.trip.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import yc.pointer.trip.R;
import yc.pointer.trip.adapter.AttentionAdapter;
import yc.pointer.trip.adapter.FansAdapter;
import yc.pointer.trip.adapter.NewPersonalHomePageAdapter;
import yc.pointer.trip.adapter.PersonalPageItemDecoration;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.base.BaseBean;
import yc.pointer.trip.bean.FansBean;
import yc.pointer.trip.bean.NewPersonalHomePageBean;
import yc.pointer.trip.bean.PersonalPageBean;
import yc.pointer.trip.bean.PersonalPageFollowBean;
import yc.pointer.trip.bean.eventbean.ReceiverBean;
import yc.pointer.trip.bean.SaveMesgBean;
import yc.pointer.trip.event.FansEvent;
import yc.pointer.trip.event.FollowEvent;
import yc.pointer.trip.event.NewPersonalPageEventA;
import yc.pointer.trip.event.NewPersonalPageFollowEvent;
import yc.pointer.trip.event.RoadDeleteDataEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.DensityUtil;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.PermissionHelper;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.CustomCircleImage;
import yc.pointer.trip.view.DialogSure;
import yc.pointer.trip.view.LoadDialog;

/**
 * Created by 张继
 * 2018/4/18
 * 16:20
 * 公司:天津悦程嘉和网络科技有限公司
 * 描述: 新改版的个人主页
 */

public class NewPersonalHomePageActivity extends BaseActivity implements FansAdapter.FansCallBack, AttentionAdapter.AttentionAdapterCallBack
        , NewPersonalHomePageAdapter.PersonalPageOnCallBack, PermissionHelper.OnAlterApplyPermission {
    @BindView(R.id.user_nick)
    TextView pageTitle;//主页标题
    @BindView(R.id.new_personal_page_edit)
    ImageView newPersonalPageEdit;//编辑
    @BindView(R.id.new_personal_home_page_head)
    CustomCircleImage newPersonalHomePageHead;//头像
    @BindView(R.id.verify_result)
    ImageView verifyResult;//判断是否为VIP
    @BindView(R.id.verify_tags)
    TextView verifyTagText;//认证标签内容
    @BindView(R.id.new_personal_home_page_title)
    TextView newPersonalHomePageTitle;//标题
    @BindView(R.id.new_personal_home_page_address)
    TextView newPersonalHomePageAddress;//地址
    @BindView(R.id.new_personal_home_page_content)
    TextView newPersonalHomePageContent;//内容
    @BindView(R.id.new_personal_home_page_travels)
    TextView newPersonalHomePageTravels;//游记
    @BindView(R.id.new_personal_home_page_fans)
    TextView newPersonalHomePageFans;//粉丝
    @BindView(R.id.new_personal_home_page_attention)
    TextView newPersonalHomePageAttention;//关注
    @BindView(R.id.new_personal_home_page_recycler)
    RecyclerView newPersonalHomePageRecycler;//列表
    @BindView(R.id.personal_page_road)
    ImageView personalPageRoad;//拍摄
    @BindView(R.id.new_personal_home_page_smart)
    SmartRefreshLayout newPersonalHomePageSmart;//列表
    @BindView(R.id.adapter_empty)
    TextView adapterEmpty;
    @BindView(R.id.empty_img)
    ImageView emptyImg;
    @BindView(R.id.empty)
    LinearLayout empty;
    @BindView(R.id.new_personal_home_page_toolbar)
    Toolbar newPersonalHomePageToolbar;


    private int TYPE = 1;//类型

    private String mUUid;
    private String mUserId;
    private String mDevcode;
    private long mTimestamp;
    private List<PersonalPageBean.DataBean> mTravelList = new ArrayList<>();
    private List<FansBean.DataBean> mFansList = new ArrayList<>();
    private List<FansBean.DataBean> mAttentionList = new ArrayList<>();
    private List<NewPersonalHomePageBean> mUidList = new ArrayList<>();

    private NewPersonalHomePageAdapter mTravelAdapter;
    private FansAdapter mFansAdapter;//粉丝
    private AttentionAdapter mAttentionAdapter;//关注


    private boolean mWaterfallFlowSign = true;//游记刷新 加载 标识
    private boolean mFans = true;//粉丝刷新 加载 标识
    private boolean mAttentionSign = true;//关注刷新 加载 标识

    private int mTravelPage = 0;
    private int mFansPage = 0;
    private int mAttentionPage = 0;
    private LoadDialog mLoadDialog;
    private String c_status;//关注未关注的标志


    private PermissionHelper permissionHelper;
    private PermissionHelper.PermissionModel[] permissionModels = {
            new PermissionHelper.PermissionModel(1, Manifest.permission.CAMERA, "需要打开您的相机"),
            new PermissionHelper.PermissionModel(2, Manifest.permission.WRITE_EXTERNAL_STORAGE, "获取您的读写权限"),
            new PermissionHelper.PermissionModel(3, Manifest.permission.RECORD_AUDIO, "获取您的麦克风权限")
    };

    private boolean isPermission = false;
    private boolean mBackFlg;
    private int mBackType;
    private StaggeredGridLayoutManager layoutManager;


    @Override
    protected int getContentViewLayout() {
        return R.layout.new_personal_home_page;
    }

    @Override
    protected void initView() {

        //开启动画
        mLoadDialog = new LoadDialog(this, "正在加载...", R.drawable.load_animation);
        //mLoadDialog.show();
        mUserId = MyApplication.mApp.getUserId();//用户id
        if (StringUtil.isEmpty(mUserId)) {
            mUserId = "0";
        }
        //注册广播 在个人信息页面保存信息时，接受广播刷新信息
        //IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction("刷新");
        //receiver = new MyBroadcastReceiver();
        //registerReceiver(receiver, intentFilter);

        mDevcode = MyApplication.mApp.getDevcode();//设备识别码
        mTimestamp = MyApplication.mApp.getTimestamp();//时间戳
        mUUid = getIntent().getStringExtra("uid");//路数作者的id

        newPersonalHomePageToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//返回上一层数据
                empty.setVisibility(View.GONE);
                if (mUidList.size() > 0) {
                    NewPersonalHomePageBean bean = mUidList.get(mUidList.size() - 1);
                    mUUid = bean.getUid();
                    mBackFlg = true;
                    mBackType = bean.getType();
                    mWaterfallFlowSign = true;
                    mFans = true;
                    mAttentionSign = true;
                    mTravelPage = 0;
                    mFansPage = 0;
                    mAttentionPage = 0;
                    newPersonalHomePageRecycler.setLayoutManager(layoutManager);
                    newPersonalHomePageRecycler.setAdapter(mTravelAdapter);
                    //获取游记列表数据
                    getServiceMsg(mUserId, mUUid, mDevcode, mTimestamp, mTravelPage);
                    mUidList.remove(mUidList.size() - 1);
                } else {
                    finish();
                }
            }
        });

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        DensityUtil.setStatusBarColor(this, R.color.colorTitle);

        if (!StringUtil.isEmpty(mUserId) && mUserId.equals(mUUid)) {//是否显示拍摄视频按钮
            permissionHelper = new PermissionHelper(this, this, permissionModels);
            personalPageRoad.setVisibility(View.VISIBLE);
        } else {
            personalPageRoad.setVisibility(View.GONE);
        }
        newPersonalHomePageTravels.setTextColor(Color.parseColor("#fed921"));

        //获取游记列表数据
        getServiceMsg(mUserId, mUUid, mDevcode, mTimestamp, mTravelPage);

        newPersonalHomePageSmart.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                if (TYPE == 1) {
                    mWaterfallFlowSign = false;
                    ++mTravelPage;
                    getServiceMsg(mUserId, mUUid, mDevcode, mTimestamp, mTravelPage);
                } else if (TYPE == 2) {
                    mFans = false;
                    ++mFansPage;
                    getMsg(mUserId, mDevcode, mTimestamp, mFansPage, mUUid);
                } else if (TYPE == 3) {
                    mAttentionSign = false;
                    ++mAttentionPage;
                    getAttention(mUserId, mDevcode, mTimestamp, mAttentionPage, mUUid);
                }
                newPersonalHomePageSmart.finishLoadmore();
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if (TYPE == 1) {
                    mWaterfallFlowSign = true;
                    mTravelPage = 0;
                    getServiceMsg(mUserId, mUUid, mDevcode, mTimestamp, mTravelPage);
                } else if (TYPE == 2) {
                    mFans = true;
                    mFansPage = 0;
                    getMsg(mUserId, mDevcode, mTimestamp, mFansPage, mUUid);
                } else if (TYPE == 3) {
                    mAttentionSign = true;
                    mAttentionPage = 0;
                    getAttention(mUserId, mDevcode, mTimestamp, mAttentionPage, mUUid);
                }
                newPersonalHomePageSmart.finishRefresh();
            }
        });


    }

    /**
     * 获取个人主页信息
     *
     * @param userId
     * @param devcode
     * @param timestamp
     * @param page
     */
    private void getServiceMsg(String userId, String uuid, String devcode, long timestamp, int page) {
        if (APPUtils.judgeTimeDev(this, devcode, timestamp)) {
            String sign = Md5Utils.createMD5("devcode=" + devcode + "p=" + page + "timestamp=" + timestamp + "uid=" + userId + "uuid=" + uuid + URLUtils.WK_APP_KEY);
            RequestBody params = new FormBody.Builder()
                    .add("devcode", devcode)
                    .add("p", String.valueOf(page))
                    .add("timestamp", String.valueOf(timestamp))
                    .add("uid", userId)
                    .add("uuid", uuid)
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.PERSONAL_PAGE, params, new HttpCallBack(new NewPersonalPageEventA()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getServiceMsg(NewPersonalPageEventA event) {
        mLoadDialog.dismiss();
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        PersonalPageBean bean = event.getData();
        if (bean.getStatus() == 0) {
            if (mTravelList == null) {
                mTravelList = new ArrayList<>();
            }
            //设置用户信息资料
            setPersonMessage(bean.getUser());

            if (mWaterfallFlowSign) {//刷新
                if (bean.getData().size() == 0) {
                    if (TYPE == 1) {
                        //路书数据
                        newPersonalHomePageRecycler.setVisibility(View.GONE);
                        empty.setVisibility(View.VISIBLE);
                        adapterEmpty.setText("暂无游记数据");
                        emptyImg.setImageResource(R.mipmap.no_book);
                    }
                } else {
                    empty.setVisibility(View.GONE);
                    newPersonalHomePageRecycler.setVisibility(View.VISIBLE);
                }
                mTravelList.clear();
                mTravelList.addAll(bean.getData());
                layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                newPersonalHomePageRecycler.setLayoutManager(layoutManager);
                mTravelAdapter = new NewPersonalHomePageAdapter(this, mTravelList, this);
                newPersonalHomePageRecycler.setAdapter(mTravelAdapter);
                newPersonalHomePageSmart.finishRefresh();
                if (mBackFlg) {//返回的标识
                    mBackFlg = false;
                    mWaterfallFlowSign = true;
                    mTravelPage = 0;
                    determineTheFontColor(mBackType);
                }
            } else {
                //加载
                if (bean.getData().size() == 0) {
//                    Toast.makeText(this, "全部加载完成", Toast.LENGTH_SHORT).show();
                } else {
                    int oldSize = mTravelList.size();
                    mTravelList.addAll(bean.getData());
                    mTravelAdapter.notifyItemRangeInserted(oldSize, mTravelList.size());
                }
                newPersonalHomePageSmart.finishLoadmore();
            }
        } else {
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }

    /**
     * 设置用户信息资料
     *
     * @param bean
     */
    private void setPersonMessage(SaveMesgBean.DataBean bean) {

        String nickname = bean.getNickname();
        if (!StringUtil.isEmpty(nickname)) {
            pageTitle.setText(nickname);
        } else {
            pageTitle.setText("个人主页");
        }
        String pic = bean.getPic();
        String is_vip = bean.getIs_vip();
        OkHttpUtils.displayGlideCircular(this, newPersonalHomePageHead, pic, verifyResult, is_vip);//加载头像
        newPersonalHomePageTitle.setText(bean.getNickname());//昵称
        newPersonalHomePageTravels.setText("游记(" + bean.getBook_num() + ")");//游记数
        String location = bean.getLocation();
        if (!StringUtil.isEmpty(location)) {
            newPersonalHomePageAddress.setVisibility(View.VISIBLE);
            newPersonalHomePageAddress.setText(location);//地址
        } else {
            newPersonalHomePageAddress.setVisibility(View.GONE);
        }
        String tags = bean.getTags();//认证的标签内容
        if (!StringUtil.isEmpty(tags)) {
            verifyTagText.setVisibility(View.VISIBLE);
            String format = String.format(getResources().getString(R.string.verify_tagtext), tags);
            verifyTagText.setText(format);
        } else {
            verifyTagText.setVisibility(View.GONE);
        }
        c_status = bean.getC_status();
        if (!StringUtil.isEmpty(mUserId) && mUserId.equals(mUUid)) {
            newPersonalPageEdit.setImageResource(R.mipmap.icon_editdata);
        } else {
            if (bean.getC_status().equals("1")) {//1关注 0取消关注
                newPersonalPageEdit.setImageResource(R.mipmap.icon_focus_mine);
            } else {
                newPersonalPageEdit.setImageResource(R.mipmap.icon_addfocus_mine);
            }
        }
        String sig = bean.getSig();
        if (!StringUtil.isEmpty(sig)) {
            newPersonalHomePageContent.setText(sig);//个性签名
        } else {
            newPersonalHomePageContent.setText("暂无签名内容");//个性签名
        }
        newPersonalHomePageFans.setText("粉丝(" + bean.getFans() + ")");
        newPersonalHomePageAttention.setText("关注(" + bean.getAtt() + ")");
//        if (bean.getIs_jie().equals("2")) {//认证
//            verifyResult.setVisibility(View.VISIBLE);
//        } else {//未认证
//            verifyResult.setVisibility(View.GONE);
//        }
    }


    @Override
    protected boolean needEventBus() {
        return true;
    }

    @OnClick({R.id.new_personal_page_edit, R.id.new_personal_home_page_travels, R.id.personal_page_road,
            R.id.new_personal_home_page_fans, R.id.new_personal_home_page_attention})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.personal_page_road://跳转录制
                if (Build.VERSION.SDK_INT < 23) {
                    startActivity(new Intent(NewPersonalHomePageActivity.this, RecordVideoActivity.class));
                } else {
                    //申请权限
                    isPermission = true;
                    permissionHelper.setOnAlterApplyPermission(this);
                    permissionHelper.applyPermission();//申请权限
                }
                break;

            case R.id.new_personal_page_edit:
                if (!StringUtil.isEmpty(mUserId) && !mUserId.equals("0")) {
                    if (!StringUtil.isEmpty(mUserId) && mUserId.equals(mUUid)) {
                        startActivity(new Intent(this, PersonMessageActivity.class));
                    } else {
                        if (!StringUtil.isEmpty(c_status)) {
                            if (c_status.equals("0")) {//已关注
                                postFollow(mUserId, mDevcode, mTimestamp, mUUid, 1);
                            } else {//为关注
                                postFollow(mUserId, mDevcode, mTimestamp, mUUid, 0);
                            }
                        } else {
                            Toast.makeText(this, "操作失败，状态值为空", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    new DialogSure(this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
                        @Override
                        public void onClickListener(DialogSure dialogSure, boolean cancelable) {
                            if (cancelable) {//确定按钮
                                Intent intent = new Intent(NewPersonalHomePageActivity.this, LoginActivity.class);
                                intent.putExtra("logFlag", "NewPersonal");
                                intent.putExtra("uuid", mUUid);
                                startActivity(intent);
                            }
                        }
                    }).setMsg("未登录,无法关注,请去登录").setPositiveButton("确定").setNegativeButton("取消").show();

                }
                break;
            case R.id.new_personal_home_page_travels://游记
                empty.setVisibility(View.GONE);
                mWaterfallFlowSign = true;
                mTravelPage = 0;
                TYPE = 1;
                determineTheFontColor(TYPE);
                break;
            case R.id.new_personal_home_page_fans://粉丝
                mFans = true;
                mFansPage = 0;
                empty.setVisibility(View.GONE);
                TYPE = 2;
                determineTheFontColor(TYPE);
                break;
            case R.id.new_personal_home_page_attention://关注
                mAttentionSign = true;
                mAttentionPage = 0;
                empty.setVisibility(View.GONE);
                TYPE = 3;
                determineTheFontColor(TYPE);
                break;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (isPermission) {
            permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isPermission) {
            permissionHelper.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 根据type获取网络信息
     *
     * @param type
     */
    private void determineTheFontColor(int type) {
        TYPE=type;
        if (type == 1) {
            newPersonalHomePageTravels.setTextColor(Color.parseColor("#fed921"));
            newPersonalHomePageFans.setTextColor(Color.parseColor("#000000"));
            newPersonalHomePageAttention.setTextColor(Color.parseColor("#000000"));
            newPersonalHomePageRecycler.setVisibility(View.VISIBLE);
            newPersonalHomePageRecycler.setLayoutManager(layoutManager);
            newPersonalHomePageRecycler.setAdapter(mTravelAdapter);
            //获取游记列表数据
            getServiceMsg(mUserId, mUUid, mDevcode, mTimestamp, mTravelPage);

        } else if (type == 2) {
            newPersonalHomePageFans.setTextColor(Color.parseColor("#fed921"));
            newPersonalHomePageTravels.setTextColor(Color.parseColor("#000000"));
            newPersonalHomePageAttention.setTextColor(Color.parseColor("#000000"));
            newPersonalHomePageRecycler.setVisibility(View.VISIBLE);
            getMsg(mUserId, mDevcode, mTimestamp, mFansPage, mUUid);
            newPersonalHomePageRecycler.setLayoutManager(new LinearLayoutManager(this));
            mFansAdapter = new FansAdapter(mFansList, this);
            newPersonalHomePageRecycler.setAdapter(mFansAdapter);
        } else if (type == 3) {
            newPersonalHomePageRecycler.setVisibility(View.VISIBLE);
            newPersonalHomePageAttention.setTextColor(Color.parseColor("#fed921"));
            newPersonalHomePageTravels.setTextColor(Color.parseColor("#000000"));
            newPersonalHomePageFans.setTextColor(Color.parseColor("#000000"));
            getAttention(mUserId, mDevcode, mTimestamp, mAttentionPage, mUUid);
            newPersonalHomePageRecycler.setLayoutManager(new LinearLayoutManager(this));
            mAttentionAdapter = new AttentionAdapter(mAttentionList, this);
            newPersonalHomePageRecycler.setAdapter(mAttentionAdapter);
        }
        empty.setVisibility(View.GONE);
    }

    /**
     * 粉丝接口
     *
     * @param uid       用户id
     * @param devcode   设备识别码
     * @param timestamp 时间戳
     * @param page      分享
     * @param uuid      关注者id
     */
    private void getMsg(String uid, String devcode, long timestamp, int page, String uuid) {
        if (APPUtils.judgeTimeDev(this, devcode, timestamp)) {
            Log.e("aa", "uid=" + uid);
            String sign = Md5Utils.createMD5("devcode=" + devcode + "p=" + page + "timestamp=" + timestamp + "type=" + 2 + "uid=" + uid + "uuid=" + uuid + URLUtils.WK_APP_KEY);
            RequestBody params = new FormBody.Builder()
                    .add("devcode", devcode)
                    .add("p", String.valueOf(page))
                    .add("timestamp", String.valueOf(timestamp))
                    .add("type", String.valueOf(2))
                    .add("uid", uid)
                    .add("uuid", uuid)
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.PERSONAL_FOLLOW_LIST, params, new HttpCallBack(new FansEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMsg(FansEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        FansBean bean = event.getData();
        if (bean.getStatus() == 0) {
            //设置用户信息资料
            setPersonMessage(bean.getUser());
            if (mFans) {
                if (bean.getData().size() == 0) {
                    newPersonalHomePageRecycler.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                    emptyImg.setImageResource(R.mipmap.fans_no_data);
                    adapterEmpty.setText("这个人很惨，没有粉丝");
                } else {
                    empty.setVisibility(View.GONE);
                    newPersonalHomePageRecycler.setVisibility(View.VISIBLE);
                }
                mFansList.clear();
                mFansList.addAll(bean.getData());
                mFansAdapter.notifyDataSetChanged();
                newPersonalHomePageSmart.finishRefresh();
            } else {
                if (bean.getData().size() == 0) {
                    Toast.makeText(this, "全部加载完成", Toast.LENGTH_SHORT).show();
                } else {
                    int oldSize = mFansList.size();
                    mFansList.addAll(bean.getData());
                    mFansAdapter.notifyItemRangeChanged(oldSize, mFansList.size());
                }
                newPersonalHomePageSmart.finishLoadmore();
            }
        } else {
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }

    /**
     * 关注列表
     *
     * @param uid
     * @param devcode
     * @param timestamp
     * @param page
     * @param uuid
     */
    private void getAttention(String uid, String devcode, long timestamp, int page, String uuid) {
        if (APPUtils.judgeTimeDev(this, devcode, timestamp)) {
            String sign = Md5Utils.createMD5("devcode=" + devcode + "p=" + page + "timestamp=" + timestamp + "type=" + 1 + "uid=" + uid + "uuid=" + uuid + URLUtils.WK_APP_KEY);
            RequestBody params = new FormBody.Builder()
                    .add("devcode", devcode)
                    .add("p", String.valueOf(page))
                    .add("timestamp", String.valueOf(timestamp))
                    .add("type", String.valueOf(1))
                    .add("uid", uid)
                    .add("uuid", uuid)
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.PERSONAL_FOLLOW_LIST, params, new HttpCallBack(new FollowEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getAttention(FollowEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        FansBean bean = event.getData();
        if (bean.getStatus() == 0) {

            newPersonalHomePageTravels.setText("游记(" + bean.getUser().getBook_num() + ")");//游记数
            newPersonalHomePageFans.setText("粉丝(" + bean.getUser().getFans() + ")");//游记数
            newPersonalHomePageAttention.setText("关注(" + bean.getUser().getAtt() + ")");//游记数
            if (mAttentionSign) {
                if (bean.getData().size() == 0) {
                    newPersonalHomePageRecycler.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                    emptyImg.setImageResource(R.mipmap.fans_no_data);
                    adapterEmpty.setText("这个人很懒，没有关注");
                } else {
                    empty.setVisibility(View.GONE);
                    newPersonalHomePageRecycler.setVisibility(View.VISIBLE);
                }
                mAttentionList.clear();
                mAttentionList.addAll(bean.getData());
                mAttentionAdapter.notifyDataSetChanged();
                newPersonalHomePageSmart.finishRefresh();
            } else {
                if (bean.getData().size() == 0) {
//                    Toast.makeText(this, "全部加载完成", Toast.LENGTH_SHORT).show();
                } else {
                    int oldSize = mAttentionList.size();
                    mAttentionList.addAll(bean.getData());
                    mAttentionAdapter.notifyItemRangeChanged(oldSize, mAttentionList.size());
                }
                newPersonalHomePageSmart.finishLoadmore();
            }
        } else {
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());

        }
    }


    /**
     * 关注接口
     *
     * @param userId    用户id
     * @param devcode   设备识别码
     * @param timestamp 时间戳
     * @param status    关注标志
     * @param uuid      关注者id
     */
    private void postFollow(String userId, String devcode, long timestamp, String uuid, int status) {
        if (APPUtils.judgeTimeDev(this, devcode, timestamp)) {
            String sign = Md5Utils.createMD5("devcode=" + devcode + "status=" + status + "timestamp=" + timestamp + "uid=" + userId + "uuid=" + uuid + URLUtils.WK_APP_KEY);
            RequestBody params = new FormBody.Builder()
                    .add("devcode", devcode)
                    .add("status", String.valueOf(status))
                    .add("timestamp", String.valueOf(timestamp))
                    .add("uid", userId)
                    .add("uuid", uuid)
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.PERSONAL_FOLLOW, params, new HttpCallBack(new NewPersonalPageFollowEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void postFollow(NewPersonalPageFollowEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        PersonalPageFollowBean data = event.getData();
        if (data.getStatus() == 0) {
            if (data.getC_status() == 1) {
                c_status = String.valueOf(data.getC_status());
                newPersonalPageEdit.setImageResource(R.mipmap.icon_focus_mine);
            } else {
                c_status = String.valueOf(data.getC_status());
                newPersonalPageEdit.setImageResource(R.mipmap.icon_addfocus_mine);
            }
            Intent intent = new Intent();
            intent.putExtra("c_status", c_status);
            setResult(2, intent);

        } else {
            Toast.makeText(this, data.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, data.getStatus());
        }
    }

    @Override
    public void fans(String uid) {//粉丝界面跳转个人主页回调接口
        TYPE = 1;
        mLoadDialog.show();
        mUidList.add(new NewPersonalHomePageBean(mUUid, 2));
        mUUid = uid;
        newPersonalHomePageTravels.setTextColor(Color.parseColor("#fed921"));
        newPersonalHomePageFans.setTextColor(Color.parseColor("#000000"));
        newPersonalHomePageAttention.setTextColor(Color.parseColor("#000000"));
        //newPersonalHomePageRecyclerFans.setVisibility(View.GONE);
        newPersonalHomePageRecycler.setVisibility(View.VISIBLE);
        mWaterfallFlowSign = true;
        mTravelPage = 0;
        newPersonalHomePageRecycler.setLayoutManager(layoutManager);
        newPersonalHomePageRecycler.setAdapter(mTravelAdapter);
        getServiceMsg(mUserId, mUUid, mDevcode, mTimestamp, mTravelPage);
    }

    @Override
    public void attention(String uid) {//关注界面跳转个人主页回调接口

        TYPE = 1;
        mLoadDialog.show();
        mUidList.add(new NewPersonalHomePageBean(mUUid, 3));
        mUUid = uid;
        newPersonalHomePageTravels.setTextColor(Color.parseColor("#fed921"));
        newPersonalHomePageFans.setTextColor(Color.parseColor("#000000"));
        newPersonalHomePageAttention.setTextColor(Color.parseColor("#000000"));
        //newPersonalHomePageRecyclerFans.setVisibility(View.GONE);
        newPersonalHomePageRecycler.setVisibility(View.VISIBLE);
        mWaterfallFlowSign = true;
        mTravelPage = 0;
        newPersonalHomePageRecycler.setLayoutManager(layoutManager);
        newPersonalHomePageRecycler.setAdapter(mTravelAdapter);
        getServiceMsg(mUserId, mUUid, mDevcode, mTimestamp, mTravelPage);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            empty.setVisibility(View.GONE);
            if (mUidList.size() > 0) {
                NewPersonalHomePageBean bean = mUidList.get(mUidList.size() - 1);
                mUUid = bean.getUid();
                mBackFlg = true;
                mBackType = bean.getType();
                mWaterfallFlowSign = true;
                mFans = true;
                mAttentionSign = true;
                mTravelPage = 0;
                mFansPage = 0;
                mAttentionPage = 0;
                newPersonalHomePageRecycler.setLayoutManager(layoutManager);
                newPersonalHomePageRecycler.setAdapter(mTravelAdapter);
                //获取游记列表数据
                getServiceMsg(mUserId, mUUid, mDevcode, mTimestamp, mTravelPage);
                mUidList.remove(mUidList.size() - 1);
            } else {
                finish();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onClickVideo(PersonalPageBean.DataBean dataBean) {
        Intent intent = new Intent(NewPersonalHomePageActivity.this, VideoDetailsActivity.class);
        intent.putExtra("dataGoodBean", dataBean);
        startActivity(intent);
    }

    @Override
    public void onLongClick(final String bid) {
        new DialogSure(NewPersonalHomePageActivity.this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
            @Override
            public void onClickListener(DialogSure dialogSure, boolean cancelable) {
                if (cancelable) {
                    deleteBook(bid);
                }
            }
        }).setTitle("温馨提示").setMsg("删除路书？").setPositiveButton("确定").setNegativeButton("取消").show();
    }


    /**
     * 删除路数
     *
     * @param bid 路数的id
     */
    private void deleteBook(String bid) {
        boolean timeFlag = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
        if (timeFlag) {
            String sign = Md5Utils.createMD5("bid=" + bid + "devcode=" + mDevcode + "timestamp=" + mTimestamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", String.valueOf(mUserId))
                    .add("bid", String.valueOf(bid))
                    .add("signature", sign)
                    .add("devcode", mDevcode)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.DELETE_BOOK, requestBody, new HttpCallBack(new RoadDeleteDataEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void cancelCollect(RoadDeleteDataEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(this, "网络异常，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }
        BaseBean bean = event.getData();
        if (bean.getStatus() == 0) {
            mTravelPage = 0;
            mWaterfallFlowSign = true;
            mTravelPage = 0;
            newPersonalHomePageRecycler.setLayoutManager(layoutManager);
            newPersonalHomePageRecycler.setAdapter(mTravelAdapter);
            getServiceMsg(mUserId, mUUid, mDevcode, mTimestamp, mTravelPage);
        } else {
            Toast.makeText(this, bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, bean.getStatus());
        }
    }


    @Override
    public void OnAlterApplyPermission() {
        isPermission = false;
        startActivity(new Intent(NewPersonalHomePageActivity.this, RecordVideoActivity.class));
    }

    @Override
    public void cancelListener() {

    }

    /**
     * 从个人信息页面发送广播传递回来
     * 刷新头像 昵称之类的信息
     */
    //private class MyBroadcastReceiver extends BroadcastReceiver {
    //    @Override
    //    public void onReceive(Context context, Intent intent) {
    //        String action = intent.getAction();
    //        String receiver = intent.getStringExtra("receiver");
    //        if (action.equals("刷新")) {
    //            if (!StringUtil.isEmpty(receiver) && receiver.equals("1")) {
    //                //获取游记列表数据
    //                getServiceMsg(mUserId, mUUid, mDevcode, mTimestamp, mTravelPage);
    //            }
    //        }
    //
    //    }
    //}


    /**
     * bind change collection coupon fans follow login newPerson system ver 等activity发送过来
     *
     * @param receiverBean receiver 是1
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(ReceiverBean receiverBean) {
        if (receiverBean != null && receiverBean.getReceiver().equals("1")) {
            mWaterfallFlowSign = true;
            mTravelPage = 0;
            newPersonalHomePageRecycler.setLayoutManager(layoutManager);
            newPersonalHomePageRecycler.setAdapter(mTravelAdapter);
            //获取游记列表数据
            getServiceMsg(mUserId, mUUid, mDevcode, mTimestamp, mTravelPage);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //if (receiver != null) {//如果广播不为空
        //    unregisterReceiver(receiver);//解除广播的绑定
        //}
    }
}
