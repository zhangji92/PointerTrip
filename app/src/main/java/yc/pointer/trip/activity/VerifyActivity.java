package yc.pointer.trip.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import yc.pointer.trip.R;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.bean.BookBean;
import yc.pointer.trip.bean.eventbean.PayBean;
import yc.pointer.trip.bean.eventbean.ReceiverBean;
import yc.pointer.trip.bean.SaveMesgBean;
import yc.pointer.trip.bean.eventbean.RefreshEarningAProfitBean;
import yc.pointer.trip.bean.eventbean.UpDataBean;
import yc.pointer.trip.event.SaveVerificationMesgEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.*;
import yc.pointer.trip.view.DialogKnow;
import yc.pointer.trip.view.DialogSure;
import yc.pointer.trip.view.LoadDialog;
import yc.pointer.trip.view.ToolbarWrapper;

import java.io.File;
import java.io.FileNotFoundException;

import yc.pointer.trip.untils.PermissionHelper.*;

/**
 * Created by moyan on 2017/8/31.
 * 认证界面
 */
public class VerifyActivity extends BaseActivity implements PermissionHelper.OnAlterApplyPermission {

    private static final int REQUEST_CODE_CAPTURE_PICTURE = 1;
    private static final int REQUEST_CODE_CAPTURE_CAMEIA = 2;
    private static final int CROP_PHOTO = 3;
    private static final int READRULES = 4;

    @BindView(R.id.standard_toolbar_title)
    TextView standardToolbarTitle;//页面标题
    //    @BindView(R.id.person_life_img)
//    RoundImageView personLifeImg;//个人生活照
    @BindView(R.id.person_name)
    EditText personName;//真实姓名
    @BindView(R.id.person_bank_num)
    EditText personBankNum;//银行卡号
    @BindView(R.id.person_id_num)
    EditText personIdNum;//身份证号
    @BindView(R.id.person_add_card)
    EditText personAddCard;//导游证号
    @BindView(R.id.person_idcard_photo)
    ImageView personIdcardPhoto;//添加身份证正面照
    @BindView(R.id.person_add_img)
    ImageView personAddImg;//添加导游证照片
    //    @BindView(R.id.back_button)
//    ImageView backButton;//返回按钮
//    @BindView(R.id.satandard_toolbar_right)
//    TextView satandardToolbarRight;//提交认证
    @BindView(R.id.bnt_commit)
    Button bntCommit;//提交按钮
    //    @BindView(R.id.guidecode_verify_rult)
//    TextView guidecodeVerifyRult;//导游证号认证结果
    @BindView(R.id.idcard_verify_result)
    TextView idcardVerifyResult;//身份证照片验证结果
    @BindView(R.id.guidephoto_verify_result)
    TextView guidephotoVerifyResult;//导游证照片验证结果
    @BindView(R.id.liner_phone)
    LinearLayout linerPhone;//联系方式
    @BindView(R.id.line)
    View liner;
    @BindView(R.id.person_add_phone)
    EditText editTextphone;//联系方式
//    @BindView(R.id.lifephoto_verify_rult)
//    TextView lifephotoVerifyRult;//生活照片验证结果

    @BindView(R.id.check_rules)
    ImageView checkRules;//判断是否阅读规则
    @BindView(R.id.rules)
    TextView readRules;//点击阅读《实名认证协议》

    @BindView(R.id.guidephoto_verify_result_explan)
    TextView guidephotoExplan;//导游证审核结果说明
    @BindView(R.id.idcard_verify_result_explan)
    TextView IdCardphotoExplan;//身份证证审核结果说明

    @BindView(R.id.verify_back)
    ImageView verifyBack;//头部底背景

    @BindView(R.id.verify_sign)
    ImageView verifySign;//会员标志


    private Button btn_picture, btn_photo, btn_cancel;
    private Bitmap bitmap;
    private String idcardBase64;//身份证的Base64
    private String GuideBase64;//导游证的Base64
    //    private String lifeImgBase64;//个人生活照的Base64
    private int flagImg;//判断照片作用0：身份证  1：导游证   2：个人生活照
    private boolean isFirstChange = true;//判断身份信息是否为第一次提交
    //    private Boolean isCameralife = false;
    private Boolean isCameraguide = false;
    private Boolean isCameraIdCard = false;
    private Boolean havephone = true;//判断是否是手机登录或者有无联系方式
    private Boolean isSave = false;

    private String mDevcode;//设备识别码
    private long mTimestamp;//时间戳
    private String userID;//用户ID
    private boolean judgeTimeDev;

    private String trueName;
    private String guideCardCode;
    private String bankNum;
    private String iDcardNum;
    private String phone;


    Uri imgFile = ImageUtils.createImageFile();//拍照的uri

    private PermissionHelper mHelper;
    private PermissionModel[] permissionModels = {new PermissionModel(1, Manifest.permission.CAMERA, "访问您的相机，否则无法拍照"),
            new PermissionHelper.PermissionModel(2, Manifest.permission.WRITE_EXTERNAL_STORAGE, "获取您的读写权限")
    };
    private boolean isPermission = false;//权限申请标志，防止一直TOAST
    private boolean isRead = false;//判断是否已读出行协议
    private LoadDialog mLoadDialog;
    private String payType;//付款页面交互
    private String videoHair;
    private String cash;
    private BookBean dataGoodBean;
    private String goVerify;


    private String isReadedVerifyRules = "";// 判断是否已读《实名认证协议》 1：已读   0：未读
    private Uri mImgUri;//解决小米不能裁剪问题 剪切的uri

    @Override
    protected int getContentViewLayout() {
//        return R.layout.activity_verify;
//        return R.layout.activity_verify_new;
        return R.layout.activity_verify_new_again;
    }

    @Override
    protected void initView() {
        mImgUri = ImageUtils.getUri();
        //开启动画
        new ToolbarWrapper(this);
        mLoadDialog = new LoadDialog(this, "正在加载...", R.drawable.load_animation);
//        mLoadDialog.show();
        standardToolbarTitle.setText("会员信息");
//        satandardToolbarRight.setText("认证");

        //视频播放详情界面
        videoHair = getIntent().getStringExtra("video");
        cash = getIntent().getStringExtra("money");
        dataGoodBean = (BookBean) getIntent().getSerializableExtra("dataBean");

        mDevcode = ((MyApplication) getApplication()).getDevcode();
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();
        userID = ((MyApplication) getApplication()).getUserId();

        isReadedVerifyRules = ((MyApplication) getApplication()).getUserBean().getIs_sm();//判断是否读过认证协议

        if (isReadedVerifyRules.equals("1")) {
//            checkRules.setChecked(true);
            isRead = true;
            checkRules.setImageResource(R.mipmap.icon_red_choose);
        } else {
//            checkRules.setChecked(false);
            isRead = false;
            checkRules.setImageResource(R.mipmap.icon_gray_choose);
        }
        checkRules.setClickable(false);


        payType = getIntent().getStringExtra("payOrder");
        goVerify = getIntent().getStringExtra("goVerify");//赚一赚跳转过来
        //判断是否获取时间戳和设备识别码
        judgeTimeDev = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
        //获取个人最新信息
        getServiceMine();
        mHelper = new PermissionHelper(this, this, permissionModels);
        //设置光标在末尾
        personName.setSelection(personName.getText().length());
        personBankNum.setSelection(personBankNum.getText().length());
        personIdNum.setSelection(personIdNum.getText().length());
        personAddCard.setSelection(personAddCard.getText().length());
        editTextphone.setSelection(editTextphone.getText().length());

    }

    /**
     * 获取所填信息内容
     */
    public void getInfo() {
        trueName = personName.getText().toString();
        guideCardCode = personAddCard.getText().toString();
        bankNum = personBankNum.getText().toString();
        iDcardNum = personIdNum.getText().toString();
        phone = editTextphone.getText().toString().trim();
    }

    /**
     * 点击头像跳转弹框
     */
    private void showDialog() {
        View view = getLayoutInflater().inflate(R.layout.photo_choose_dialog, null);
        final Dialog dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
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
                    openCamera();

                } else {
                    //申请权限
                    //Applypermission();
                    isPermission = true;
                    mHelper = new PermissionHelper(VerifyActivity.this, new OnAlterApplyPermission() {
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

        intent.putExtra("crop", "true");//圆形
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (isPermission) {
            mHelper.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_CAPTURE_PICTURE://图库返回
                if (resultCode != RESULT_OK) {
                    return;
                }
                Uri uriData = data.getData();
                cropPhoto(uriData);
                break;
            case REQUEST_CODE_CAPTURE_CAMEIA://拍照返回数据
//                Bundle bundleCameia = data.getExtras();
//                bitmap = (Bitmap) bundleCameia.get("data");
                if (resultCode != RESULT_OK) {
                    return;
                }
                //String absolutePath = imgFile.getPath();
//                Bitmap bitmap = getimage(absolutePath);//根据path压缩图片
//                Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));
//                File scal = ImageUtils.scal(Uri.fromFile(new File(absolutePath)));
                File scal = ImageUtils.scal(imgFile);
                cropPhoto(Uri.fromFile(new File(scal.getAbsolutePath())));//剪切图片
                break;
            case CROP_PHOTO://裁剪后的图片
                if (resultCode != RESULT_OK) {
                    return;
                }
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mImgUri));
                    if (flagImg == 0) {//身份证
                        isCameraIdCard = true;//判断图片是否为本地的，true：本地 false：网络//设置身份证照片
                        personIdcardPhoto.setImageBitmap(bitmap);
                        idcardBase64 = ImageUtils.Bitmap2StrByBase64(bitmap);
                    } else if (flagImg == 1) {//导游证
                        isCameraguide = true;//判断图片是否为本地的，true：本地 false：网络//设置导游证照片
//                    guideVerifyFlag.setVisibility(View.GONE);
                        personAddImg.setImageBitmap(bitmap);
                        GuideBase64 = ImageUtils.Bitmap2StrByBase64(bitmap);
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

//                Bundle bundle = data.getExtras();
//                bitmap = (Bitmap) bundle.get("data");
//                if (flagImg == 0) {//身份证
//                    isCameraIdCard = true;//判断图片是否为本地的，true：本地 false：网络//设置身份证照片
//                    personIdcardPhoto.setImageBitmap(bitmap);
//                    idcardBase64 = ImageUtils.Bitmap2StrByBase64(bitmap);
//                } else if (flagImg == 1) {//导游证
//                    isCameraguide = true;//判断图片是否为本地的，true：本地 false：网络//设置导游证照片
////                    guideVerifyFlag.setVisibility(View.GONE);
//                    personAddImg.setImageBitmap(bitmap);
//                    GuideBase64 = ImageUtils.Bitmap2StrByBase64(bitmap);
//                }
//                else if (flagImg == 2) {//个人生活照
//                    isCameralife = true;//判断图片是否为本地的，true：本地 false：网络//设置个人生活照片
////                    lifeVerifyFlag.setVisibility(View.GONE);
//                    personLifeImg.setImageBitmap(bitmap);
//                    lifeImgBase64 = ImageUtils.Bitmap2StrByBase64(bitmap);
//                }
                break;
            case READRULES:
                if (resultCode == 2) {
                    isReadedVerifyRules = data.getStringExtra("readedStatus");
                    if (!StringUtil.isEmpty(isReadedVerifyRules) && isReadedVerifyRules.equals("1")) {
//                        checkRules.setChecked(true);
                        isRead = true;
                        checkRules.setImageResource(R.mipmap.icon_red_choose);
                    } else {
//                        checkRules.setChecked(false);
                        isRead = false;
                        checkRules.setImageResource(R.mipmap.icon_gray_choose);
                    }
                }
                break;

        }


    }

    @Override
    protected boolean needEventBus() {
        return true;
    }


    @OnClick({R.id.bnt_commit, R.id.person_idcard_photo, R.id.person_add_img, R.id.rules})
    public void onViewClicked(View view) {
        //获取所填信息
        getInfo();
        switch (view.getId()) {
//            case R.id.satandard_toolbar_right://认证
//                if (StringUtil.isEmpty(GuideBase64)) {
//                    GuideBase64 = "";
//                } else if (isCameraguide) {
//                    GuideBase64 = "data:image/png;base64," + GuideBase64;
//                }
////                if (StringUtil.isEmpty(lifeImgBase64)) {
////                    lifeImgBase64 = "";
////                } else if (isCameralife) {
////                    lifeImgBase64 = "data:image/png;base64," + lifeImgBase64;
////                }
//                if (StringUtil.isEmpty(idcardBase64)) {
//                    idcardBase64 = "";
//                } else if (isCameraIdCard) {
//                    idcardBase64 = "data:image/png;base64," + idcardBase64;
//                }
//                //提交认证
//                verifyGuide();
//                break;
            case R.id.bnt_commit:
                if (isRead) {
                    if (StringUtil.isEmpty(GuideBase64)) {
                        GuideBase64 = "";
                    } else if (isCameraguide) {
                        GuideBase64 = "data:image/png;base64," + GuideBase64;
                    }
//                if (StringUtil.isEmpty(lifeImgBase64)) {
//                    lifeImgBase64 = "";
//                } else if (isCameralife) {
//                    lifeImgBase64 = "data:image/png;base64," + lifeImgBase64;
//                }
                    if (StringUtil.isEmpty(idcardBase64)) {
                        idcardBase64 = "";
                    } else if (isCameraIdCard) {
                        idcardBase64 = "data:image/png;base64," + idcardBase64;
                    }
                    if (!StringUtil.isEmpty(idcardBase64)) {
                        //提交认证
                        verifyGuide();
                    } else {
                        Toast.makeText(this, "请选择身份证正面照", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "在认证之前请仔细阅读《会员身份认证服务协议》", Toast.LENGTH_SHORT).show();
                }

                break;
//            case R.id.person_life_img://个人生活照
//                flagImg = 2;
//                showDialog();
//                break;
            case R.id.person_idcard_photo://身份证照片
                flagImg = 0;
                showDialog();
                break;
            case R.id.person_add_img://导游照片
                flagImg = 1;
                showDialog();
                break;
            case R.id.rules://阅读《实名认证服务协议》的结果
                Intent intent = new Intent(VerifyActivity.this, AgreementActivity.class);
                intent.putExtra("logFlag", "Verify");
                intent.putExtra("isReade", isReadedVerifyRules);
                startActivityForResult(intent, READRULES);
                break;
        }
    }

    /**
     * 验证身份信息，保存信息
     */
    private void verifyID() {
        if (!StringUtil.isEmpty(trueName) && !StringUtil.isEmpty(bankNum) && !StringUtil.isEmpty(iDcardNum)) {

            //验证身份信息，保存信息
            String msgDialog = "身份信息提交后不能更改，是否确认提交？";
            if (isFirstChange) {
                showChangeDialog(msgDialog);
            } else {
                savePersonMesg();
            }
        } else {
            Toast.makeText(this, "姓名、身份证、银行卡三者信息不全，请完善", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 根据手机号判断是否为手机号登录
     */
    private void verifyPhone() {
        if (havephone == false) {//第三方登录
            if (StringUtil.isMobileNo(VerifyActivity.this, phone)) {
                verifyID();
            }
        } else {//手机号登录
            verifyID();
        }
    }

    /**
     * 验证导游证及导游证照片
     */
    private void verifyGuide() {
        if (!StringUtil.isEmpty(guideCardCode) && !StringUtil.isEmpty(GuideBase64)) {
            verifyPhone();
        } else if (StringUtil.isEmpty(guideCardCode) && StringUtil.isEmpty(GuideBase64)) {
            verifyPhone();
        } else {
            Toast.makeText(this, "请完善导游证编号或导游证件", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 验证生活照和身份证正面照
     * <p>
     * /
     * //     private void verifyIlifeAndIDcar() {
     * //        if (!StringUtil.isEmpty(idcardBase64)) {
     * //            verifyGuide();
     * //        } else if (StringUtil.isEmpty(idcardBase64)) {
     * //            verifyGuide();
     * //        } else {
     * //            Toast.makeText(this, "身份证正面照不能为空", Toast.LENGTH_SHORT).show();
     * //        }
     * //    }
     * //
     * <p>
     * /**
     * 身份信息提交后不能修改提示弹框
     *
     * @param msg
     */
    private void showChangeDialog(String msg) {
        // 身份信息提交后不能更改，是否确认提交？ DialogSure
        new DialogSure(VerifyActivity.this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
            @Override
            public void onClickListener(DialogSure dialogSure, boolean cancelable) {
                if (cancelable) {
                    //网络请求方法
                    savePersonMesg();
                }
            }
        }).setTitle("温馨提示").setMsg(msg).setPositiveButton("确定").setNegativeButton("取消").show();
    }

    /**
     * 保存验证信息
     */
    private void savePersonMesg() {
        isSave = true;


//        if (!StringUtil.isEmpty(iDcardNum)) {
////            if (isIDCard(iDcardNum)) {
////            } else {
////                Toast.makeText(this, "请检查身份证号是否正确", Toast.LENGTH_SHORT).show();
////                return;
////            }
//            return;
//        }

//        card_pic个人生活照lifeImgBase64现在为空
        if (judgeTimeDev) {
            String sign = Md5Utils.createMD5("bank_num=" + bankNum + "card_pic=" + "" + "dcode=" + guideCardCode + "devcode=" + mDevcode + "dpic=" + GuideBase64 + "id_card=" + iDcardNum + "id_card_pic=" + idcardBase64 + "phone=" + phone + "real_name=" + trueName + "timestamp=" + mTimestamp + "uid=" + userID + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("bank_num", bankNum)
                    .add("card_pic", "")
                    .add("dcode", guideCardCode)
                    .add("devcode", mDevcode)
                    .add("dpic", GuideBase64)
                    .add("id_card", iDcardNum)
                    .add("real_name", trueName)
                    .add("id_card_pic", idcardBase64)
                    .add("phone", phone)
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", userID)
                    .add("signature", sign)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.SAVE_PERSION_MESG, requestBody, new HttpCallBack(new SaveVerificationMesgEvent()));
            mLoadDialog.show();//显示动画
        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void savePersonMesg(SaveVerificationMesgEvent saveMesgEvent) {
        if (saveMesgEvent.isTimeOut()) {
            mLoadDialog.dismiss();//取消动画
            Toast.makeText(this, "网络状态异常，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }
        SaveMesgBean saveMesgBean = saveMesgEvent.getData();
        if (saveMesgBean.getStatus() == 0) {
            String is_verify = saveMesgBean.getData().getIs_verify();
            String lifeImgverify = saveMesgBean.getData().getIs_jie();
            String is_order = saveMesgBean.getData().getIs_order();
            String isJie = saveMesgBean.getData().getIs_jie();
            String is_vip = saveMesgBean.getData().getIs_vip();
            if (isSave) {
                if (!StringUtil.isEmpty(is_order) && !is_order.equals("1")) {
                    //未交押金，跳转押金界面

                    Intent intent = new Intent(this, NewUnDepositActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    //交完押金后，正常跳转的方法
                    normalIntent(lifeImgverify);
                }

                //Intent intent1 = new Intent();
                //intent1.setAction("刷新");
                //intent1.putExtra("receiver", "1");
                //sendBroadcast(intent1);
                //Intent intentPay = new Intent();
                //intentPay.setAction("刷新支付");
                //sendBroadcast(intentPay);
                EventBus.getDefault().post(new ReceiverBean("1"));
                EventBus.getDefault().post(new PayBean("刷新支付"));

//                if (lifeImgverify.equals("1")) {
//                    new DialogKnow(VerifyActivity.this, R.style.user_default_dialog, new DialogKnow.CallBackListener() {
//                        @Override
//                        public void onClickListener() {
////                    setResult(1);
//                    finish();
//                        }
//                    }).setTitle("温馨提示")
//                            .setMsg("您的会员身份认证信息已提交人工审核，审核周期为1~2个工作日")
//                            .setPositiveButton("我知道了")
//                            .show();
//                }else {
//                    Toast.makeText(this, "提交成功", Toast.LENGTH_SHORT).show();
//                    finish();
//                }
            }
            //设置页面数据
            setVerifyData(saveMesgBean, is_verify, lifeImgverify, is_order, isJie,is_vip);

        } else {
            mLoadDialog.dismiss();//取消动画
            Toast.makeText(this, saveMesgBean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, saveMesgBean.getStatus());
        }
    }

    /**
     * 设置页面数据
     *
     * @param saveMesgBean
     * @param is_verify
     * @param lifeImgverify
     * @param is_order
     */
    private void setVerifyData(SaveMesgBean saveMesgBean, String is_verify, String lifeImgverify,
                               String is_order, String isJie,String isVip) {
        //            GuideBase64 = "";
        isCameraguide = false;
//        isCameralife = false;
        ((MyApplication) getApplication()).setUserBean(saveMesgBean.getData());//保存信息成功


        SharedPreferencesUtils.getInstance().putString(VerifyActivity.this, "isOrder", is_order);
        String trueNameNet = saveMesgBean.getData().getReal_name();
        String dcode = saveMesgBean.getData().getDcode();
        String bankNumNet = saveMesgBean.getData().getBank_num();
        String idCardNet = saveMesgBean.getData().getId_card();

        String phoneNum = saveMesgBean.getData().getPhone();
        String contact = saveMesgBean.getData().getContact();

        if (!StringUtil.isEmpty(bankNumNet)) {
            ((MyApplication) getApplication()).setUserIsVerify(true);
        } else {
            ((MyApplication) getApplication()).setUserIsVerify(false);
        }

        if (!StringUtil.isEmpty(is_order) && is_order.equals("1")) {
            SharedPreferencesUtils.getInstance().putBoolean(VerifyActivity.this, "isPayDeposit", true);
            bntCommit.setText("提交");
            if (isJie.equals("2")) {//审核通过
                verifySign.setVisibility(View.VISIBLE);
                if (isVip.equals("1")){//黄金
                    verifySign.setImageResource(R.mipmap.img_ninety_nine_mss);
                }else if (isVip.equals("2")){//白金
                    verifySign.setImageResource(R.mipmap.img_three_hundred_ninety_nine_mss);
                }
                verifyBack.setImageResource(R.mipmap.img_real_name_authentication_yrz);
                bntCommit.setText("修改信息");
                personBankNum.setTextColor(Color.parseColor("#777777"));
                personAddCard.setTextColor(Color.parseColor("#777777"));
            } else {//审核未通过
                verifySign.setVisibility(View.GONE);
                verifyBack.setImageResource(R.mipmap.img_real_name_authentication);
                bntCommit.setText("提交");
                personBankNum.setTextColor(Color.parseColor("#cdcdcd"));
                personAddCard.setTextColor(Color.parseColor("#cdcdcd"));
            }
        } else if (is_order.equals("0")) {
            SharedPreferencesUtils.getInstance().putBoolean(VerifyActivity.this, "isPayDeposit", false);
            bntCommit.setText("下一步");
        }

        if (StringUtil.isEmpty(contact) && StringUtil.isEmpty(phoneNum)) {
            havephone = false;
            linerPhone.setVisibility(View.VISIBLE);
            liner.setVisibility(View.VISIBLE);
        } else {
            havephone = true;
            phone = phoneNum;
            linerPhone.setVisibility(View.GONE);
            liner.setVisibility(View.GONE);
        }

        if (!StringUtil.isEmpty(trueNameNet)) {//设置真实姓名，验证完成后不可更改
            personName.setText(trueNameNet);
            personName.setFocusable(false);
            personName.setFocusableInTouchMode(false);//不可点击编辑
        } else {
            personName.setFocusable(true);
            personName.setFocusableInTouchMode(true);//不可点击编辑
        }

        String dpic = saveMesgBean.getData().getDpic();
        if (!StringUtil.isEmpty(dpic)) {

            GuideBase64 = saveMesgBean.getData().getDpic();
            OkHttpUtils.displayGlideRound(this, personAddImg, dpic, 15);

        }

        if (!StringUtil.isEmpty(dcode)) {
            personAddCard.setText(dcode);
        }


        if (is_verify.equals("1")) {
            guidephotoVerifyResult.setVisibility(View.VISIBLE);
//            guidecodeVerifyRult.setVisibility(View.VISIBLE);
//            guidecodeVerifyRult.setText("审核中");
//            guidecodeVerifyRult.setTextColor(Color.parseColor("#d7013a"));
            guidephotoVerifyResult.setText("审核中");
            guidephotoVerifyResult.setTextColor(Color.parseColor("#d7013a"));
            guidephotoExplan.setText(R.string.verfiy_checking_explan);

            personAddCard.setFocusable(false);
            personAddCard.setFocusableInTouchMode(false);//不可点击编辑
            personAddImg.setClickable(false);//不可更换
        } else if (is_verify.equals("2")) {//审核通过

            guidephotoVerifyResult.setVisibility(View.VISIBLE);
//            guidecodeVerifyRult.setVisibility(View.VISIBLE);
//            guidecodeVerifyRult.setText("已通过");
//            guidecodeVerifyRult.setTextColor(Color.parseColor("#1FBB07"));
            guidephotoVerifyResult.setText("已通过");
            guidephotoVerifyResult.setTextColor(Color.parseColor("#1FBB07"));
            guidephotoExplan.setText(R.string.verfiy_pass_explan);
            personAddCard.setFocusable(true);
            personAddCard.setFocusableInTouchMode(true);//可点击编辑
            personAddImg.setClickable(true);//可更换
        } else if (is_verify.equals("3")) {//未通过
            guidephotoVerifyResult.setVisibility(View.VISIBLE);
//            guidecodeVerifyRult.setVisibility(View.VISIBLE);
//            guidecodeVerifyRult.setText("未通过");
//            guidecodeVerifyRult.setTextColor(Color.parseColor("#d7013a"));
            guidephotoVerifyResult.setText("未通过");
            guidephotoVerifyResult.setTextColor(Color.parseColor("#d7013a"));
            guidephotoExplan.setText(R.string.verfiy_unpass_explan);

            personAddCard.setFocusable(true);
            personAddCard.setFocusableInTouchMode(true);//可点击编辑
            personAddImg.setClickable(true);//可更换
        } else {
            personBankNum.setTextColor(Color.parseColor("#cdcdcd"));
            personAddCard.setTextColor(Color.parseColor("#cdcdcd"));
//            guidecodeVerifyRult.setVisibility(View.GONE);
            guidephotoVerifyResult.setText("上传导游证正面照");
            personAddCard.setText("");
            personAddCard.setFocusable(true);
            personAddCard.setFocusableInTouchMode(true);//可点击编辑
            personAddImg.setClickable(true);//可更换
            personAddImg.setImageResource(R.mipmap.img_add_tour_guide);
        }
//        个人生活照
//        String card_pic = saveMesgBean.getData().getCard_pic();
//        if (!StringUtil.isEmpty(card_pic)) {
//            lifeImgBase64 = saveMesgBean.getData().getCard_pic();
//            OkHttpUtils.displayImg(personLifeImg, card_pic);
//        } else {
//
//        }
        String id_card_pic = saveMesgBean.getData().getId_card_pic();
        if (!StringUtil.isEmpty(id_card_pic)) {
            idcardBase64 = id_card_pic;
            OkHttpUtils.displayGlideRound(this, personIdcardPhoto, id_card_pic, 15);
        }
        if (lifeImgverify.equals("1")) {//审核中不允许更换
//                lifephotoVerifyRult.setVisibility(View.VISIBLE);
//                lifephotoVerifyRult.setText("审核中");
//                lifephotoVerifyRult.setTextColor(Color.parseColor("#d7013a"));
            idcardVerifyResult.setText("审核中");
            idcardVerifyResult.setTextColor(Color.parseColor("#d7013a"));
            IdCardphotoExplan.setText(R.string.verfiy_checking_explan);
//                personLifeImg.setClickable(false);
            personIdcardPhoto.setClickable(false);

        } else if (lifeImgverify.equals("2")) {//审核通过
//                lifephotoVerifyRult.setVisibility(View.VISIBLE);
//                lifephotoVerifyRult.setText("已通过");
//                lifephotoVerifyRult.setTextColor(Color.parseColor("#1FBB07"));
            idcardVerifyResult.setText("已通过");
            idcardVerifyResult.setTextColor(Color.parseColor("#1FBB07"));
            IdCardphotoExplan.setText(R.string.verfiy_pass_explan);
//                personLifeImg.setClickable(true);
            personIdcardPhoto.setClickable(true);
        } else if (lifeImgverify.equals("3")) {//审核未通过
//                lifephotoVerifyRult.setVisibility(View.VISIBLE);
//                lifephotoVerifyRult.setText("未通过");
//                lifephotoVerifyRult.setTextColor(Color.parseColor("#d7013a"));
            idcardVerifyResult.setText("未通过");
            idcardVerifyResult.setTextColor(Color.parseColor("#d7013a"));
            IdCardphotoExplan.setText(R.string.verfiy_unpass_explan);
//                personLifeImg.setClickable(true);
            personIdcardPhoto.setClickable(true);
        } else {
//                lifephotoVerifyRult.setVisibility(View.GONE);
            idcardVerifyResult.setText("上传身份证正面照");
//                personLifeImg.setClickable(true);
//                personLifeImg.setImageResource(R.mipmap.addlifephoto);
            personIdcardPhoto.setImageResource(R.mipmap.img_add_id);
        }

        if (!StringUtil.isEmpty(bankNumNet)) {
            personBankNum.setText(bankNumNet);
        }
        if (!StringUtil.isEmpty(idCardNet)) {
            isFirstChange = false;
            personIdNum.setText(idCardNet);
            personIdNum.setFocusable(false);
            personIdNum.setFocusableInTouchMode(false);//不可点击编辑
        } else {
            personIdNum.setFocusable(true);
            personIdNum.setFocusableInTouchMode(true);//可点击编辑
        }
        mLoadDialog.dismiss();//取消动画
    }

    /**
     * 正常跳转
     *
     * @param lifeImgverify
     */
    private void normalIntent(String lifeImgverify) {

        if (lifeImgverify.equals("1")) {
            new DialogKnow(VerifyActivity.this, R.style.user_default_dialog, new DialogKnow.CallBackListener() {
                @Override
                public void onClickListener() {
//                  //如果是视频详情跳转认证，认证之后跳转视频详情（预约认证）
                    if (!StringUtil.isEmpty(videoHair) && videoHair.equals("hair")) {
                        Intent intent = new Intent(VerifyActivity.this, VideoDetailsActivity.class);
                        intent.putExtra("dataGoodBean", dataGoodBean);
                        startActivity(intent);
                    }

                    //支付界面
//        if (!StringUtil.isEmpty(payType) && payType.equals("pay")) {
//            setResult(1);
//        }
                    //提现认证
//                if (!StringUtil.isEmpty(cash)&&cash.equals("money")){
//                    setResult(2);
//                }

                    if (!StringUtil.isEmpty(goVerify)) {
                        if (goVerify.equals("makemoney") || goVerify.equals("myOrder") || goVerify.equals("myReserve")) {
                            //Intent intentBroadcast = new Intent();
                            //intentBroadcast.setAction("刷新赚一赚");
                            //sendBroadcast(intentBroadcast);
                            EventBus.getDefault().post(new RefreshEarningAProfitBean("刷新赚一赚"));
                        } else if (goVerify.equals("unPaid")) {
                            //Intent intentBroadcast = new Intent();
                            //intentBroadcast.setAction("更新数据");
                            //sendBroadcast(intentBroadcast);
                            EventBus.getDefault().post(new UpDataBean("更新数据"));
                        }
                    }
                    finish();
                }
            }).setTitle("温馨提示")
                    .setMsg("您的会员身份认证信息已提交人工审核，审核周期为1~2个工作日")
                    .setPositiveButton("我知道了")
                    .show();
        } else {
            Toast.makeText(this, "提交成功", Toast.LENGTH_SHORT).show();
            //如果是视频详情跳转认证，认证之后跳转视频详情（预约认证）
            if (!StringUtil.isEmpty(videoHair) && videoHair.equals("hair")) {
                Intent intent = new Intent(VerifyActivity.this, VideoDetailsActivity.class);
                intent.putExtra("dataGoodBean", dataGoodBean);
                startActivity(intent);
            }

            //支付界面
//        if (!StringUtil.isEmpty(payType) && payType.equals("pay")) {
//            setResult(1);
//        }
            //提现认证
//                if (!StringUtil.isEmpty(cash)&&cash.equals("money")){
//                    setResult(2);
//                }

            if (!StringUtil.isEmpty(goVerify)) {
                if (goVerify.equals("makemoney") || goVerify.equals("myOrder") || goVerify.equals("myReserve")) {
                    //Intent intentBroadcast = new Intent();
                    //intentBroadcast.setAction("刷新赚一赚");
                    //sendBroadcast(intentBroadcast);
                    EventBus.getDefault().post(new RefreshEarningAProfitBean("刷新赚一赚"));
                } else if (goVerify.equals("unPaid")) {
                    //Intent intentBroadcast = new Intent();
                    //intentBroadcast.setAction("更新数据");
                    //sendBroadcast(intentBroadcast);
                    EventBus.getDefault().post(new UpDataBean("更新数据"));
                }
            }
            finish();
        }


    }


    private void getServiceMine() {
        isSave = false;//判断是保存信息还是获取信息 true：保存信息 false：获取信息
        mLoadDialog.show();
        if (judgeTimeDev) {
            String sign = Md5Utils.createMD5("devcode=" + mDevcode + "timestamp=" + mTimestamp + "uid=" + userID + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", mDevcode)
                    .add("signature", sign)
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", userID)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.GET_PERSON_MESG, requestBody, new HttpCallBack(new SaveVerificationMesgEvent()));
        }
    }


    @Override
    public void OnAlterApplyPermission() {
        openCamera();
    }

    @Override
    public void cancelListener() {

    }

}
