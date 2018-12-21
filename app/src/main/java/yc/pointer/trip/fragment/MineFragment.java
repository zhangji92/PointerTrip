package yc.pointer.trip.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import yc.pointer.trip.R;
import yc.pointer.trip.activity.CollectionActivity;
import yc.pointer.trip.activity.CouponActivity;
import yc.pointer.trip.activity.ExplainWebActivity;
import yc.pointer.trip.activity.LoginActivity;
import yc.pointer.trip.activity.MemberUpgradeActivity;
import yc.pointer.trip.activity.MyMoneyActivity;
import yc.pointer.trip.activity.MyOrderNewActivity;
import yc.pointer.trip.activity.MyReserveActivity;
import yc.pointer.trip.activity.MyTravelActivity;
import yc.pointer.trip.activity.NewDepositedActivity;
import yc.pointer.trip.activity.NewPersonalHomePageActivity;
import yc.pointer.trip.activity.NewUnDepositActivity;
import yc.pointer.trip.activity.SettingActivity;
import yc.pointer.trip.activity.SystemMessageActivity;
import yc.pointer.trip.activity.VerifyActivity;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseFragment;
import yc.pointer.trip.bean.eventbean.ActionResultBean;
import yc.pointer.trip.bean.eventbean.ReceiverBean;
import yc.pointer.trip.bean.SaveMesgBean;
import yc.pointer.trip.bean.eventbean.UpdateRedDotBean;
import yc.pointer.trip.event.MineSaveMesgEvent;
import yc.pointer.trip.event.UploadHeadBackEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.APPUtils;
import yc.pointer.trip.untils.ImageUtils;
import yc.pointer.trip.untils.Md5Utils;
import yc.pointer.trip.untils.PermissionHelper;
import yc.pointer.trip.untils.SharedPreferencesUtils;
import yc.pointer.trip.untils.StatusBarUtils;
import yc.pointer.trip.untils.StringUtil;
import yc.pointer.trip.view.CustomCircleImage;
import yc.pointer.trip.view.DialogKnow;
import yc.pointer.trip.view.MemberUpgradeWindow;

/**
 * 我的Fragment
 */
public class MineFragment extends BaseFragment {


    @BindView(R.id.liner_setting)
    ImageView linerSetting;
    @BindView(R.id.verify_result)
    ImageView verifyResult;//认证活动奖
    @BindView(R.id.comment_message)
    RelativeLayout commentMessage;//跳转评论列表
    //    @BindView(R.id.coupon_result)
//    ImageView couponResult;//指针大礼包活动奖
    @BindView(R.id.mine_message_remind)
    ImageView actionResult;//主题活动红点
    @BindView(R.id.head)
    CustomCircleImage head;
    @BindView(R.id.mine_nick)
    TextView mineNick;
    @BindView(R.id.user_location)
    TextView userLocation;
    @BindView(R.id.mine_introduce)
    TextView mineIntroduce;
    @BindView(R.id.liner_my_order)
    TextView linerMyOrder;
    @BindView(R.id.liner_my_reservation)
    TextView linerMyReservation;
    @BindView(R.id.liner_my_collect)
    TextView linerMyCollect;
    @BindView(R.id.liner_my_income)
    TextView linerMyIncome;
    @BindView(R.id.liner_my_foot)
    TextView linerMyFoot;
    @BindView(R.id.liner_verify)
    TextView linerVerify;
    @BindView(R.id.liner_coupon)
    TextView linerCoupon;
    //    @BindView(R.id.liner_action)
//    TextView linerAction;
    @BindView(R.id.layout_unlogin)
    LinearLayout layoutUnlogin;
    @BindView(R.id.logined_layout)
    LinearLayout layoutlogined;
    @BindView(R.id.mine_login)
    Button mineLogin;//立即登录
    @BindView(R.id.head_back_img)
    ImageView headBackImg;//主题背景
    @BindView(R.id.replace_head_back)
    ImageView replaceHeadBack;//点击更换主题背景
    @BindView(R.id.mine_toolbar)
    Toolbar mineToolbar;

    @BindView(R.id.liner_unnamed)
    TextView linerUnnamed;//未命名
    @BindView(R.id.liner_upgrade)
    TextView linerUpgrade;//升级


    private int msg_num;
    //private MyBroadcastReciver myBroadcastReciver;
    //点击更换封面的弹框
    private Button btn_picture, btn_photo, btn_cancel;
    private Bitmap bitmap;


    private String mUserId;//用户id
    private boolean loginFlag;//是否登录标志
    private long mTimestamp;//时间戳
    private String mDevcode;//手机识别码


    private static final int REQUEST_CODE_CAPTURE_PICTURE = 1;//打开相册
    private static final int REQUEST_CODE_CAPTURE_CAMEIA = 2;//打开相机
    private static final int CROP_PHOTO = 3;

    Uri imgFile = ImageUtils.createImageFile();//拍照返回的Uri
    private Uri mImgUri;//解决小米不能裁剪问题


    private PermissionHelper mHelper;
    private PermissionHelper.PermissionModel[] permissionModels = {
            new PermissionHelper.PermissionModel(1, Manifest.permission.CAMERA, "访问您的相机，否则无法拍照"),

    };
    private boolean isPermission = false;//权限申请标志，防止一直TOAST
    private String mIsVip;//VIP的类型
    private String mIsJie;//是否审核通过
    private boolean isPayDeposit;//判断是否交过押金


    public static MineFragment newInstance() {
        return new MineFragment();
    }

    @Override
    protected int getContentViewLayout() {
        return R.layout.fragment_mine_new;
    }

    @Override
    protected void initView() {
        mImgUri = ImageUtils.getUri();
        //非沉浸式
//        View decorView = getActivity().getWindow().getDecorView();
//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        DensityUtil.setStatusBarColor(getActivity(), R.color.colorTitle);


        StatusBarUtils.with(getActivity()).init();//设置沉浸式
//        int statusBarHeight = StatusBarUtils.getStatusBarHeight(getActivity());
//        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mineToolbar.getLayoutParams();
//        layoutParams.topMargin = statusBarHeight;
//        mineToolbar.setLayoutParams(layoutParams);

//        boolean isCash = SharedPreferencesUtils.getInstance().getBoolean(getContext(), "isCash");
//        if (isCash) {
//            linerCash.setVisibility(View.VISIBLE);
//            line.setVisibility(View.VISIBLE);
//        } else {
//            linerCash.setVisibility(View.GONE);
//            line.setVisibility(View.GONE);
//        }

        //正常加载
        showData();

        //IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction("刷新");
        //myBroadcastReciver = new MyBroadcastReciver();
        //getActivity().registerReceiver(myBroadcastReciver, intentFilter);
    }

    /**
     * 正常加载
     */
    private void showData() {
        loginFlag = MyApplication.mApp.isIslogin();
        mUserId = MyApplication.mApp.getUserId();
        mTimestamp = MyApplication.mApp.getTimestamp();
        mDevcode = MyApplication.mApp.getDevcode();

        if (StringUtil.isEmpty(mUserId)) {
            layoutlogined.setVisibility(View.GONE);
            verifyResult.setVisibility(View.GONE);
            layoutUnlogin.setVisibility(View.VISIBLE);
//            mUserId = "0";
            SaveMesgBean.DataBean userBean = MyApplication.mApp.getUserBean();
            if (userBean != null) {
                int msg_num = userBean.getMsg_num();
                if (msg_num <= 0) {
                    actionResult.setVisibility(View.GONE);
                } else {
                    actionResult.setVisibility(View.VISIBLE);
                }
            } else {
                actionResult.setVisibility(View.VISIBLE);
            }

        } else {
            layoutUnlogin.setVisibility(View.GONE);
//            verifyResult.setVisibility(View.VISIBLE);
            layoutlogined.setVisibility(View.VISIBLE);
            getServiceMine();//请求网络数据
        }
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }


    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 打开相机
     */
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgFile);
        startActivityForResult(intent, REQUEST_CODE_CAPTURE_CAMEIA);
    }

    /**
     * 裁剪图片
     *
     * @param uri
     */
    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        intent.putExtra("crop", "true");
//        aspectX aspectY 比例
        intent.putExtra("aspectX", 4);
        intent.putExtra("aspectY", 3);
//        outputX outputY 裁剪的宽高
        intent.putExtra("outputX", 400);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("scale", true);
        intent.putExtra("noFaceDetection", true);

        //裁剪之后，保存在裁剪文件中，关键
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImgUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, CROP_PHOTO);
    }


    /**
     * 点击头像跳转弹框
     */
    private void showDialog() {
        View view = getLayoutInflater().inflate(R.layout.photo_choose_dialog, null);
        final Dialog dialog = new Dialog(getActivity(), R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getActivity().getWindowManager().getDefaultDisplay().getHeight();
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.onWindowAttributesChanged(wl);
        dialog.setCanceledOnTouchOutside(true);

        btn_picture = (Button) window.findViewById(R.id.btn_picture);
        btn_photo = (Button) window.findViewById(R.id.btn_photo);
        btn_cancel = (Button) window.findViewById(R.id.btn_cancle);
        //图库
        btn_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_CAPTURE_PICTURE);
                dialog.dismiss();
            }
        });
        //拍照
        btn_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, REQUEST_CODE_CAPTURE_CAMEIA);
                if (Build.VERSION.SDK_INT < 23) {
                    //打开相机权限
//                    showDialog();
                    openCamera();

                } else {
                    //申请权限
                    //Applypermission();
                    isPermission = true;
                    mHelper = new PermissionHelper(getActivity(), new PermissionHelper.OnAlterApplyPermission() {
                        @Override
                        public void OnAlterApplyPermission() {
                            isPermission = false;
                            openCamera();
                        }

                        @Override
                        public void cancelListener() {

                        }
                    }, permissionModels);
                    mHelper.applyPermission();
                }
                dialog.dismiss();
            }
        });
        //取消
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (isPermission) {
            mHelper.onActivityResult(requestCode, resultCode, data);
        }
        switch (requestCode) {
            case 8:
                if (resultCode == 8) {
                    loginFlag = MyApplication.mApp.isIslogin();
                    mUserId = data.getStringExtra("uid");
                    getServiceMine();
                    layoutUnlogin.setVisibility(View.GONE);
                    layoutlogined.setVisibility(View.VISIBLE);
                } else if (resultCode == 9) {
                    mUserId = MyApplication.mApp.getUserId();
                    type();
                }


                break;

            case REQUEST_CODE_CAPTURE_PICTURE://图库返回
                if (resultCode != getActivity().RESULT_OK) {
                    return;
                }
                Uri uriData = data.getData();
                cropPhoto(uriData);
                break;
            case REQUEST_CODE_CAPTURE_CAMEIA://拍照返回数据
                if (resultCode != getActivity().RESULT_OK) {
                    return;
                }
                String absolutePath = imgFile.getPath();
                File scal = ImageUtils.scal(Uri.fromFile(new File(absolutePath)));
                cropPhoto(Uri.fromFile(new File(scal.getAbsolutePath())));//剪切图片
                break;
            case CROP_PHOTO://裁剪后的图片
                if (resultCode != getActivity().RESULT_OK) {
                    return;
                }
                try {
                    bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(mImgUri));
                    headBackImg.setImageBitmap(bitmap);

                    //请求网络，上传图片数据流
                    String path = mImgUri.getPath();
                    File headImgFile = new File(path);
                    uploadHeadBackImg(headImgFile);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


                break;

        }
    }

    /**
     * 上传封面图片
     */
    private void uploadHeadBackImg(File file) {
        boolean flag = APPUtils.judgeTimeDev(getActivity(), mDevcode, mTimestamp);
        if (flag) {
            String sign = Md5Utils.createMD5("devcode=" + mDevcode + "timestamp=" + mTimestamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/png"), file);
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            builder.addFormDataPart("devcode", mDevcode);
            builder.addFormDataPart("timestamp", String.valueOf(mTimestamp));
            builder.addFormDataPart("uid", mUserId);
            builder.addFormDataPart("signature", sign);
            builder.addFormDataPart("img", file.getName(), fileBody);
            OkHttpUtils.getInstance().post(URLUtils.UPLOAD_HEAD_BACK, builder.build(), new HttpCallBack(new UploadHeadBackEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getHeadBackImg(UploadHeadBackEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(getActivity(), "网络异常", Toast.LENGTH_SHORT).show();
            return;
        }
        SaveMesgBean data = event.getData();
        if (data.getStatus() == 0) {

        } else {
            String msg = data.getMsg();
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(getActivity(), data.getStatus());
        }

    }

    /**
     * 请求个人信息
     */
    private void getServiceMine() {
        boolean flag = APPUtils.judgeTimeDev(getActivity(), mDevcode, mTimestamp);
        if (flag) {
            String sign = Md5Utils.createMD5("devcode=" + mDevcode + "timestamp=" + mTimestamp + "uid=" + mUserId + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", mDevcode)
                    .add("signature", sign)
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", mUserId)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.MY_PERSON_SETTING, requestBody, new HttpCallBack(new MineSaveMesgEvent()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void personSetBean(MineSaveMesgEvent event) {
        if (event.isTimeOut()) {
            Toast.makeText(getActivity(), "网络出小差", Toast.LENGTH_SHORT).show();
            return;
        }
        SaveMesgBean bean = event.getData();
        if (bean.getStatus() == 0) {
            ((MyApplication) getActivity().getApplication()).setUserBean(bean.getData());
//            mLinear_mine.setVisibility(View.VISIBLE);
//            mLinear_nick.setVisibility(View.VISIBLE);
//            mText_login.setVisibility(View.GONE);
//            String m_status = bean.getData().getM_status();
//            String z_status = bean.getData().getZ_status();
//            String a_status = bean.getData().getA_status();//活动标识
            msg_num = bean.getData().getMsg_num();

            mIsJie = bean.getData().getIs_jie();
            String is_vip = bean.getData().getIs_vip();
            String is_order = bean.getData().getIs_order();
            String is_bd = bean.getData().getIs_bd();
            String bank_num = bean.getData().getBank_num();
            String alipay = bean.getData().getAlipay();
            String head_back_img = bean.getData().getHead_back_img();

            mIsVip = bean.getData().getIs_vip();
            String yj_money = bean.getData().getYj_money();
            if ("1".equals(mIsVip) && yj_money.equals("99.00")) {//会员 99 会员显示升级按钮，其余状态不显示升级按钮
                linerUpgrade.setVisibility(View.VISIBLE);
            } else {
                linerUpgrade.setVisibility(View.INVISIBLE);
            }

//            if (!StringUtil.isEmpty(m_status) && m_status.equals("1")) {
//                couponResult.setVisibility(View.VISIBLE);
//                couponResult.setImageResource(R.mipmap.gift);
//            } else {
//                couponResult.setVisibility(View.GONE);
//            }
//            if (!StringUtil.isEmpty(is_jie) && is_jie.equals("2")) {
////                verifyResult.setText("VIP");
////                verifyResult.setTextColor(Color.parseColor("#ffb400"));
////                Drawable drawable = getResources().getDrawable(R.drawable.icon_authorized);
////                verifyResult.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
//                verifyResult.setVisibility(View.VISIBLE);
//            } else if (!StringUtil.isEmpty(is_jie) && is_jie.equals("1")) {
////                verifyResult.setText("VIP认证中");
////                verifyResult.setTextColor(Color.parseColor("#ffb400"));
////                Drawable drawable = getResources().getDrawable(R.drawable.icon_unauthorized);
////                verifyResult.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
//                verifyResult.setVisibility(View.GONE);
//            } else {
////                verifyResult.setText("VIP");
////                verifyResult.setTextColor(Color.parseColor("#bebebe"));
////                Drawable drawable = getResources().getDrawable(R.drawable.icon_unauthorized);
////                verifyResult.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
//                verifyResult.setVisibility(View.GONE);
//            }

            if (!StringUtil.isEmpty(is_bd) && is_bd.equals("1")) {
                ((MyApplication) getActivity().getApplication()).setUserIsBinding(true);
                SharedPreferencesUtils.getInstance().putBoolean(getActivity(), "isBinding", true);
            } else if (is_bd.equals("0")) {
                ((MyApplication) getActivity().getApplication()).setUserIsBinding(false);
                SharedPreferencesUtils.getInstance().putBoolean(getActivity(), "isBinding", false);
            }

            if (!StringUtil.isEmpty(is_order) && is_order.equals("1")) {
//            ((MyApplication) getApplication()).setUserIsBinding(true);
                SharedPreferencesUtils.getInstance().putBoolean(getActivity(), "isPayDeposit", true);
            } else if (is_order.equals("0")) {
//            ((MyApplication) getApplication()).setUserIsBinding(false);
                SharedPreferencesUtils.getInstance().putBoolean(getActivity(), "isPayDeposit", false);
            }
            if (!StringUtil.isEmpty(bank_num)) {
                ((MyApplication) getActivity().getApplication()).setUserIsVerify(true);
            } else {
                ((MyApplication) getActivity().getApplication()).setUserIsVerify(false);
            }
            if (!StringUtil.isEmpty(alipay)) {
                ((MyApplication) getActivity().getApplication()).setAlipayNumber(alipay);
            } else {
                ((MyApplication) getActivity().getApplication()).setAlipayNumber("");
            }
            if (!StringUtil.isEmpty(head_back_img)) {

                OkHttpUtils.displayGlide(getActivity(), headBackImg, head_back_img);


            } else {

            }

            //Intent intent1 = new Intent();
            //intent1.setAction("更新活动标识");
            if (msg_num <= 0) {
                actionResult.setVisibility(View.GONE);
                EventBus.getDefault().post(new UpdateRedDotBean(false));
                //intent1.putExtra("action", "gone");
            } else {
                //MainActivity 我的 RadioButton 上面的小红点
                EventBus.getDefault().postSticky(new UpdateRedDotBean(true));
                //intent1.putExtra("action", "visible");
                actionResult.setVisibility(View.VISIBLE);
            }
            //getActivity().sendBroadcast(intent1);


            String pic = bean.getData().getPic();
//            OkHttpUtils.displayImg(head, pic);
            OkHttpUtils.displayGlideCircular(getActivity(),head,pic,verifyResult,is_vip);
//            Glide.with(getActivity())
//                    .load(URLUtils.BASE_URL + bean.getData().getPic())
//                    .placeholder(R.mipmap.gray_picture) //占位符 也就是加载中的图片，可放个gif
//                    .error(R.mipmap.gray_picture) //失败图片
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .centerCrop()
//                    .fitCenter()//缩放
//                    .bitmapTransform(new BlurTransformation(getActivity(), 85))
//                    .into(headBack);
            //OkHttpUtils.displayGlideVague(getActivity(), headBack, bean.getData().getPic());
            mineNick.setText(bean.getData().getNickname());
            String location = bean.getData().getLocation();
            if (!StringUtil.isEmpty(location)) {
                userLocation.setVisibility(View.VISIBLE);
                userLocation.setText(location);
            } else {
                userLocation.setVisibility(View.GONE);
            }
            String sig = bean.getData().getSig();
            if (!StringUtil.isEmpty(sig)) {

                mineIntroduce.setText(sig);

            } else {
                mineIntroduce.setText("简介：这个人很懒,暂时没有填写个人简介");
            }
//            collectrbCount.setText(bean.getData().getCol_num());
//            roadbookCount.setText(bean.getData().getBook_num());
        } else {
            Toast.makeText(getActivity(), bean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(getActivity(), bean.getStatus());
            if (bean.getStatus() == 201) {
                layoutlogined.setVisibility(View.GONE);
                verifyResult.setVisibility(View.GONE);
                layoutUnlogin.setVisibility(View.VISIBLE);
                SaveMesgBean.DataBean userBean = ((MyApplication) getActivity().getApplication()).getUserBean();
                if (userBean != null) {
                    int msg_num = userBean.getMsg_num();
                    if (msg_num <= 0) {
                        actionResult.setVisibility(View.GONE);
                    } else {
                        actionResult.setVisibility(View.VISIBLE);
                    }
                } else {
                    actionResult.setVisibility(View.VISIBLE);
                }
            }
        }
    }


    @OnClick({R.id.head, R.id.mine_login, R.id.comment_message, R.id.liner_my_order, R.id.liner_my_reservation, R.id.liner_my_collect,
            R.id.liner_my_income, R.id.liner_my_foot, R.id.liner_verify, R.id.liner_coupon, R.id.liner_setting, R.id.head_back_img,
            R.id.replace_head_back, R.id.liner_upgrade, R.id.liner_unnamed})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.replace_head_back:
                if (loginFlag) {
                    showDialog();
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.putExtra("logFlag", "mine");
                    startActivityForResult(intent, 8);
                }
                break;

            case R.id.head_back_img:
                if (loginFlag) {
                    Intent intent = new Intent(getActivity(), NewPersonalHomePageActivity.class);
                    intent.putExtra("uid", mUserId);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.putExtra("logFlag", "mine");
                    startActivityForResult(intent, 8);
                }
                break;
            case R.id.head:
                if (loginFlag) {
                    Intent intent = new Intent(getActivity(), NewPersonalHomePageActivity.class);
                    intent.putExtra("uid", mUserId);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.putExtra("logFlag", "mine");
                    startActivityForResult(intent, 8);
                }
                break;
            case R.id.mine_login://立即登录
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra("logFlag", "mine");
                startActivityForResult(intent, 8);
                break;
            case R.id.liner_my_order:
                if (StringUtil.isEmpty(mUserId)) {
                    Intent intentPerson = new Intent(getActivity(), LoginActivity.class);
                    intentPerson.putExtra("logFlag", "myOrder");
                    startActivity(intentPerson);
                } else {
                    Intent intent1 = new Intent(getActivity(), MyOrderNewActivity.class);
//                    intent1.putExtra("uid", "3");
                    startActivity(intent1);
                }
                break;
            case R.id.liner_my_reservation:
                if (!StringUtil.isEmpty(mUserId)) {
                    startActivity(new Intent(getActivity(), MyReserveActivity.class));
                } else {
                    //跳转登录
                    Intent intentlogin = new Intent(getActivity(), LoginActivity.class);
                    intentlogin.putExtra("logFlag", "myReservation");
                    startActivity(intentlogin);
                }
                break;
            case R.id.liner_my_collect:
                if (!StringUtil.isEmpty(mUserId)) {
                    startActivity(new Intent(getActivity(), CollectionActivity.class));
//                    Intent intent1=new Intent(getActivity(), NewPersonalHomePageActivity.class);
//                    intent1.putExtra("uid", "3");
//                    startActivity( intent1);
                } else {
                    //跳转登录
                    Intent intentlogin = new Intent(getActivity(), LoginActivity.class);
                    intentlogin.putExtra("logFlag", "myCollection");
                    startActivity(intentlogin);
                }
                break;
            case R.id.liner_my_income:
                if (StringUtil.isEmpty(mUserId)) {
                    //跳转登录
                    Intent intentlogin = new Intent(getActivity(), LoginActivity.class);
                    intentlogin.putExtra("logFlag", "myMoney");
                    startActivity(intentlogin);
                } else {
                    startActivity(new Intent(getActivity(), MyMoneyActivity.class));
                }
                break;
            case R.id.liner_my_foot:
                if (StringUtil.isEmpty(mUserId)) {
                    Intent intentPerson = new Intent(getActivity(), LoginActivity.class);
                    intentPerson.putExtra("logFlag", "myTravel");
                    startActivity(intentPerson);
                } else {
                    startActivity(new Intent(getActivity(), MyTravelActivity.class));
                }
                break;
            case R.id.liner_verify://跳转认证
                if (StringUtil.isEmpty(mUserId)) {
                    Intent intentPerson = new Intent(getActivity(), LoginActivity.class);
                    intentPerson.putExtra("logFlag", "verify");
                    startActivity(intentPerson);
                } else {
                    Intent intentverify = new Intent(getActivity(), VerifyActivity.class);
                    startActivity(intentverify);
                }
                break;
            case R.id.liner_coupon:
                if (StringUtil.isEmpty(mUserId)) {
                    //跳转登录
                    Intent intentlogin = new Intent(getActivity(), LoginActivity.class);
                    intentlogin.putExtra("logFlag", "coupon");
                    startActivity(intentlogin);

                } else {
                    Intent intentCoupon = new Intent(getActivity(), CouponActivity.class);
                    startActivity(intentCoupon);
                }
                break;
//            case R.id.liner_action:
//                Intent intentCoupon = new Intent(getActivity(), ActivityTrip.class);
//                startActivity(intentCoupon);
//                break;
            case R.id.liner_setting:
                Intent intentSetting = new Intent(getActivity(), SettingActivity.class);
                startActivity(intentSetting);
                break;
            case R.id.comment_message:
                if (!StringUtil.isEmpty(mUserId)) {
                    startActivity(new Intent(getActivity(), SystemMessageActivity.class));
                } else {
                    //跳转登录
                    Intent intentlogin = new Intent(getActivity(), LoginActivity.class);
                    intentlogin.putExtra("logFlag", "myComments");
                    startActivity(intentlogin);
                }
                break;
            case R.id.liner_upgrade://升级
                if (loginFlag) {//判断是否登陆 去升级界面
                    // MemberUpgradeActivity会员升级页面
                    getActivity().startActivity(new Intent(getActivity(), MemberUpgradeActivity.class));
                } else {//未登录
                    Intent intentLogin = new Intent(getActivity(), LoginActivity.class);
                    intentLogin.putExtra("logFlag", "mine");
                    startActivityForResult(intentLogin, 8);
                }
                break;
            case R.id.liner_unnamed://未命名
                if (loginFlag) {
                    type();
                } else {
                    Intent login = new Intent(getActivity(), LoginActivity.class);
                    login.putExtra("logFlag", "mine");
                    startActivityForResult(login, 9);
                }
                break;
        }
    }

    /**
     * 根据isVip跳转界面
     */
    private void type() {
        isPayDeposit = SharedPreferencesUtils.getInstance().getBoolean(getActivity(), "isPayDeposit");
        if ("2".equals(mIsJie)) {//已成会员
            if ("1".equals(mIsVip)) {//99会员权益
                Intent intent1 = new Intent(getActivity(), NewDepositedActivity.class);
                intent1.putExtra("title", "99元黄金特权");
                getActivity().startActivity(intent1);
            } else if ("2".equals(mIsVip)) {//399会员权益
                Intent intent1 = new Intent(getActivity(), NewDepositedActivity.class);
                intent1.putExtra("title", "399元白金特权");
                getActivity().startActivity(intent1);
            }
        } else if ("1".equals(mIsJie)) {//审核中
            if (isPayDeposit == false) {
                //没缴纳押金，跳转支付押金页面
                //未充值
                Intent intent = new Intent(getActivity(), NewUnDepositActivity.class);
                startActivity(intent);
            } else {
                new DialogKnow(getActivity(), R.style.user_default_dialog, new DialogKnow.CallBackListener() {
                    @Override
                    public void onClickListener() {

                    }
                }).setTitle("温馨提示")
                        .setMsg("您的指针会员身份信息正在加急审核认证中，请耐心等待！")
                        .setPositiveButton("我知道了")
                        .show();
            }
//            Toast.makeText(getActivity(), "您的指针会员正在认证中，请耐心等待", Toast.LENGTH_SHORT).show();
        } else {
            Intent intentVerify = new Intent(getActivity(), ExplainWebActivity.class);
            intentVerify.putExtra("title", "会员说明");
            intentVerify.putExtra("url", URLUtils.BASE_URL + "/Home/Book/agreement?uid=" + mUserId);
            startActivity(intentVerify);
        }
    }

    //广播接收内部类
    //private class MyBroadcastReciver extends BroadcastReceiver {
    //    @Override
    //    public void onReceive(Context context, Intent intent) {
    //        String action = intent.getAction();
    //        if (action.equals("刷新")) {
    //            String author = intent.getStringExtra("receiver");
    //            String actionResult = intent.getStringExtra("actionResult");
    //            if (!StringUtil.isEmpty(author) && author.equals("1")) {
    //                showData();
    //                if (!StringUtil.isEmpty(actionResult) && actionResult.equals("actionResult")) {
    //
    //                    Intent intent1 = new Intent();
    //                    intent1.setAction("更新活动标识");
    //                    if (msg_num<=0) {
    //                        intent1.putExtra("action", "gone");
    //                    } else {
    //                        intent1.putExtra("action", "visible");
    //                    }
    //                }
    //            }
    //
    //        }
    //    }
    //}

    /**
     * bind change collection coupon fans follow login newPerson system ver 等activity发送过来
     *
     * @param bean
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(ReceiverBean bean) {
        if (bean != null && bean.getReceiver().equals("1")) {
            head.setImageResource(R.mipmap.head);
            showData();
            //if (!StringUtil.isEmpty(actionResult) && actionResult.equals("actionResult")) {
            //
            //    Intent intent1 = new Intent();
            //    intent1.setAction("更新活动标识");
            //    if (msg_num<=0) {
            //        intent1.putExtra("action", "gone");
            //    } else {
            //        intent1.putExtra("action", "visible");
            //    }
            //}
        }
    }

    /**
     * system 等activity发送过来
     *
     * @param bean
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void actionResult(ActionResultBean bean) {
        if (bean != null && bean.getActionResult().equals("actionResult")) {
            Intent intent1 = new Intent();
            intent1.setAction("更新活动标识");
            if (msg_num <= 0) {
                intent1.putExtra("action", "gone");
            } else {
                intent1.putExtra("action", "visible");
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //在结束时可取消广播
        //getActivity().unregisterReceiver(myBroadcastReciver);
    }
}
