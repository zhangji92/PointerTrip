package yc.pointer.trip.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import yc.pointer.trip.bean.eventbean.ReceiverBean;
import yc.pointer.trip.bean.SaveMesgBean;
import yc.pointer.trip.event.SaveMesgEvent;
import yc.pointer.trip.network.HttpCallBack;
import yc.pointer.trip.network.OkHttpUtils;
import yc.pointer.trip.network.URLUtils;
import yc.pointer.trip.untils.*;
import yc.pointer.trip.view.CustomCircleImage;
import yc.pointer.trip.view.LoadDialog;
import yc.pointer.trip.view.ToolbarWrapper;

import java.io.File;
import java.io.FileNotFoundException;

import yc.pointer.trip.untils.PermissionHelper.*;

/**
 * Created by 刘佳伟
 * 2017/7/24
 * 10:50
 * 个人信息页
 */
public class PersonMessageActivity extends BaseActivity implements PermissionHelper.OnAlterApplyPermission {

    private static final int REQUEST_CODE_CAPTURE_PICTURE = 1;
    private static final int REQUEST_CODE_CAPTURE_CAMEIA = 2;
    private static final int CROP_PHOTO = 3;
    @BindView(R.id.person_heada)
    CustomCircleImage personHeadImg;//头像
    @BindView(R.id.verify_result)
    ImageView verifyResult;//VIP标识
//    @BindView(R.id.user_invite)
//    TextView userInvite;//本人账户ID
    @BindView(R.id.person_nick)
    EditText personNick;//昵称
    @BindView(R.id.person_address)
    EditText personAddress;//地址
    @BindView(R.id.person_alipay_num)
    EditText personAlipayNum;//支付宝帐号
//    @BindView(R.id.other_invite)
//    EditText otherInvite;//其他人的邀请码
    @BindView(R.id.person_phone)
    EditText personPhone;//联系方式
    //    @BindView(R.id.person_save)
//    Button personSave;//保存信息
    @BindView(R.id.person_edit_autograph)
    EditText etAutograph;//个性签名
    @BindView(R.id.person_text_count)
    TextView textCount;//个性签名


    private Button btn_picture, btn_photo, btn_cancel;
    private String nickName;
    private String address;
    private String alipayNum;
    private String phone;
    private Bitmap bitmap;
    private String headBase64;//头像的Base64
    private String mDevcode;//设备识别码
    private long mTimestamp;//时间戳
    private String userID;//用户ID
    private boolean judgeTimeDev;
    private Boolean isSave = false;
    private Boolean isCameraHead = false;
    private PermissionHelper mHelper;
    private PermissionModel[] permissionModels = {
            new PermissionModel(1, Manifest.permission.CAMERA, "访问您的相机，否则无法拍照"),
            new PermissionHelper.PermissionModel(2, Manifest.permission.WRITE_EXTERNAL_STORAGE, "获取您的读写权限")
    };
    private boolean isPermission = false;//权限申请标志，防止一直TOAST
    private LoadDialog mLoadDialog;

    Uri imgFile = ImageUtils.createImageFile();
    private String autograph;//个性签名
    private Uri mImgUri;//解决小米不能裁剪问题

//    private String inviteCode = "";//填写的邀请码
//    private String code = "";//已经填写过的邀请码
//    private boolean isBinding;//判断是否绑定

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_person;
    }

    @Override
    protected void initView() {
        mImgUri=ImageUtils.getUri();
        //开启动画
        mLoadDialog = new LoadDialog(this, "正在加载...", R.drawable.load_animation);
        mLoadDialog.show();
        ToolbarWrapper toolbarWrapper = new ToolbarWrapper(this).setCustomTitle(R.string.person_message);
        toolbarWrapper.setRightText(R.string.write_text_commit);
        mDevcode = ((MyApplication) getApplication()).getDevcode();
        mTimestamp = ((MyApplication) getApplication()).getTimestamp();
        userID = ((MyApplication) getApplication()).getUserId();
//        isBinding = ((MyApplication) getApplication()).isUserIsBinding();
        //判断是否获取时间戳和设备识别码
        judgeTimeDev = APPUtils.judgeTimeDev(this, mDevcode, mTimestamp);
        //获取个人最新信息
        getServiceMine();
        mHelper = new PermissionHelper(this, this, permissionModels);
        etAutograph.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = etAutograph.getText().toString().trim();
                int length = text.length();
                if (length < 60) {
                    textCount.setText(String.valueOf(length));
                    textCount.setTextColor(Color.parseColor("#1FBB07"));
                } else if (length >= 60) {
                    textCount.setText("60");
                    textCount.setTextColor(Color.parseColor("#d7013a"));

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    @Override
    protected boolean needEventBus() {
        return true;
    }


    @OnClick({R.id.person_heada, R.id.satandard_toolbar_right})
    public void onViewClicked(View view) {
        getInfo();
        switch (view.getId()) {
            case R.id.person_heada://添加头像
                showDialog();
                break;
            case R.id.satandard_toolbar_right://保存信息
//                isBinding = ((MyApplication) getApplication()).isUserIsBinding();
                if (StringUtil.isEmpty(headBase64)) {
                    headBase64 = "";
                } else if (isCameraHead) {
                    headBase64 = "data:image/png;base64," + headBase64;
                }
//                if (StringUtil.isEmpty(headBase64)) {
//                    headBase64 = ""       ;
//                } else {
//                    headBase64 = "data:image/png;base64," + headBase64;
//                }
                //网络请求方法
                savePersonMesg();
//                if (!StringUtil.isEmpty(inviteCode)) {
//                    if (isBinding) {
//                        savePersonMesg();
//                    } else {
//                        new DialogSure(PersonMessageActivity.this, R.style.user_default_dialog, new DialogSure.CallBackListener() {
//                            @Override
//                            public void onClickListener(DialogSure dialogSure, boolean trueEnable) {
//                                if (trueEnable) {
//                                    Intent intent = new Intent(PersonMessageActivity.this, BindingPhoneActivity.class);
//                                    intent.putExtra("logFlag", "personMessage");
//                                    startActivity(intent);
//                                } else {
//                                    inviteCode = "";
//                                    otherInvite.setFocusableInTouchMode(true);
//                                    otherInvite.setFocusable(true);
//                                    otherInvite.setText("");
//                                    otherInvite.setHint("请输入邀请码");
//                                }
//                            }
//                        }).setTitle("温馨提示")
//                                .setMsg("您的账号并未完成手机绑定，无法填写用户邀请码，敬请谅解")
//                                .setPositiveButton("去绑定")
//                                .setNegativeButton("那算了")
//                                .show();
//                    }
//                } else {
//                    savePersonMesg();
//                }

                break;

        }
    }

    public void getInfo() {
        nickName = personNick.getText().toString().trim();
        address = personAddress.getText().toString();
        alipayNum = personAlipayNum.getText().toString();
        phone = personPhone.getText().toString();
        autograph = etAutograph.getText().toString();
//        inviteCode = otherInvite.getText().toString();
    }

    /**
     * 点击头像跳转弹框
     */
    private void showDialog() {
        View view = getLayoutInflater().inflate(R.layout.photo_choose_dialog, null);
        final Dialog dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        wl.width = LayoutParams.MATCH_PARENT;
        wl.height = LayoutParams.WRAP_CONTENT;
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
                if (Build.VERSION.SDK_INT < 23) {
                    //打开相机权限
                    openCamera();
                } else {
                    //申请权限
                    //Applypermission();
                    isPermission = true;
                    mHelper = new PermissionHelper(PersonMessageActivity.this, new OnAlterApplyPermission() {
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    /**
     * 打开相机
     */
    private void openCamera() {
        Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent2.putExtra(MediaStore.EXTRA_OUTPUT, imgFile);
        startActivityForResult(intent2, REQUEST_CODE_CAPTURE_CAMEIA);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (isPermission) {
            mHelper.onActivityResult(requestCode, resultCode, data);
        }
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_CAPTURE_PICTURE://图库返回
                Uri uriData = data.getData();
                cropPhoto(uriData);
//                Bitmap bitmapFromUri = ImageUtils.getBitmapFromUri(this, uriData);
//                personHeadImg.setImageURI(uriData);
//                headBase64 = ImageUtils.Bitmap2StrByBase64(bitmapFromUri);


                break;
            case REQUEST_CODE_CAPTURE_CAMEIA://拍照返回数据

                File temp = new File(imgFile.getPath());
                cropPhoto(Uri.fromFile(temp));
//                String absolutePath = imgFile.getAbsolutePath();
//                Bitmap bitmap = getimage(absolutePath);//根据path压缩图片
//                Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));
//                cropPhoto(uri);//剪切图片
//                Bundle bundleCameia = data.getExtras();
//                bitmap = (Bitmap) bundleCameia.get("data");
//                personHeadImg.setImageBitmap(bitmap);
//                headBase64 = ImageUtils.Bitmap2StrByBase64(bitmap);

                break;
            case CROP_PHOTO://裁剪后的图片
//                Bundle bundle = data.getExtras();
//                personHeadImg.setImageURI(mImgUri);
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mImgUri));
                    isCameraHead = true;//判断图片是否为本地的，true：本地 false：网络//设置个人生活照片
                    personHeadImg.setImageBitmap(bitmap);
                    headBase64 = ImageUtils.Bitmap2StrByBase64(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

//                bitmap = (Bitmap) bundle.get("data");
//                isCameraHead = true;//判断图片是否为本地的，true：本地 false：网络//设置个人生活照片
//                personHeadImg.setImageBitmap(bitmap);
//                headBase64 = ImageUtils.Bitmap2StrByBase64(bitmap);
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);


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
        intent.putExtra("outputX", 400);
        intent.putExtra("outputY", 300);
        intent.putExtra("scale", true);
        //uritempFile为Uri类变量，实例化uritempFile，转化为uri方式解决问题
        //裁剪之后，保存在裁剪文件中，关键
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImgUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, CROP_PHOTO);
    }

    private void savePersonMesg() {
        mLoadDialog.show();//显示动画
        isSave = true;
        if (!StringUtil.isEmpty(phone)) {
            Boolean mobileNo = StringUtil.isMobileNo(PersonMessageActivity.this, phone);
            if (mobileNo == false) {
                return;
            }
        }
//        if (!StringUtil.isEmpty(code)) {
//            inviteCode="";
//        }
        if (judgeTimeDev) {

            String sign = Md5Utils.createMD5("address=" + address + "alipay=" + alipayNum  + "contact=" + phone + "devcode=" + mDevcode + "nickname=" + nickName + "pic=" + headBase64 + "sig=" + autograph + "timestamp=" + mTimestamp + "uid=" + userID + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("address", address)
                    .add("alipay", alipayNum)
                    .add("contact", phone)
                    .add("devcode", mDevcode)
                    .add("nickname", nickName)
                    .add("pic", headBase64)
                    .add("sig", autograph)
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", userID)
                    .add("signature", sign)
                    .build();

            OkHttpUtils.getInstance().post(URLUtils.SAVE_PERSION_MESG, requestBody, new HttpCallBack(new SaveMesgEvent()));
        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void savePersonMesg(SaveMesgEvent saveMesgEvent) {
        if (saveMesgEvent.isTimeOut()) {
            mLoadDialog.dismiss();//取消动画
            Toast.makeText(this, "网络状态异常，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }
        SaveMesgBean saveMesgBean = saveMesgEvent.getData();
        if (saveMesgBean.getStatus() == 0) {
            if (isSave) {
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                //Intent intent1 = new Intent();
                //intent1.setAction("刷新");
                //intent1.putExtra("receiver", "1");
                //sendBroadcast(intent1);
                EventBus.getDefault().post(new ReceiverBean("1"));
                finish();
            }

            isCameraHead = false;
            ((MyApplication) getApplication()).setUserBean(saveMesgBean.getData());//保存信息成功
            String headimg = saveMesgBean.getData().getPic();
            String is_vip = saveMesgBean.getData().getIs_vip();
//            OkHttpUtils.displayImg(personHeadImg, headimg);//设置头像
            OkHttpUtils.displayGlideCircular(this,personHeadImg,headimg,verifyResult,is_vip);
            headBase64 = headimg;

            SharedPreferencesUtils.getInstance().putString(PersonMessageActivity.this, "headImg", headimg);
//            String is_jie = saveMesgBean.getData().getIs_jie();
//            if (!StringUtil.isEmpty(is_jie)){
//                if (is_jie.equals("2")){
//                    verifyResult.setVisibility(View.VISIBLE);
//                }else {
//                    verifyResult.setVisibility(View.GONE);
//                }
//            }else {
//                verifyResult.setVisibility(View.GONE);
//            }
            String nickname = saveMesgBean.getData().getNickname();
            String address = saveMesgBean.getData().getAddress();
            String alipayNum = saveMesgBean.getData().getAlipay();
            String phone = saveMesgBean.getData().getContact();
            String sig = saveMesgBean.getData().getSig();



            String invitation_code = saveMesgBean.getData().getInvitation_code();
            String otherInvitationCode = saveMesgBean.getData().getI_invitation_code();

//
//            if (!StringUtil.isEmpty(invitation_code)) {
//                userInvite.setText("个人邀请码：" + invitation_code);
//            } else {
//                userInvite.setText("个人邀请码：需绑定手机后获取");
//            }

            if (!StringUtil.isEmpty(nickname)) {
                personNick.setText(nickname);
            }

            if (!StringUtil.isEmpty(sig)) {
                etAutograph.setText(sig);
            } else {
                etAutograph.setHint("个性签名");
            }
//            if (!StringUtil.isEmpty(otherInvitationCode)) {
//                otherInvite.setText(otherInvitationCode);
//                code = otherInvitationCode;
//                otherInvite.setFocusable(false);
//                otherInvite.setFocusableInTouchMode(false);
//            } else {
//                otherInvite.setHint("请输入他人邀请码");
//                otherInvite.setFocusable(true);
//                otherInvite.setFocusableInTouchMode(true);
//            }

            if (!StringUtil.isEmpty(address)) {
                personAddress.setText(address);
            }

            if (!StringUtil.isEmpty(alipayNum)) {
                ((MyApplication) getApplication()).setAlipayNumber(alipayNum);
                personAlipayNum.setText(alipayNum);
//                personAlipayNum.setEnabled(false);
            } else {
                ((MyApplication) getApplication()).setAlipayNumber("");
            }
            if (!StringUtil.isEmpty(phone)) {
                personPhone.setText(phone);
            } else {
                personPhone.setText(saveMesgBean.getData().getPhone());
            }
            mLoadDialog.dismiss();//取消动画

            personNick.setSelection(personNick.getText().length());
            personAddress.setSelection(personAddress.getText().length());
            personAlipayNum.setSelection(personAlipayNum.getText().length());
            personPhone.setSelection(personPhone.getText().length());
            etAutograph.setSelection(etAutograph.getText().length());
        } else {
            mLoadDialog.dismiss();//取消动画
            Toast.makeText(this, saveMesgBean.getMsg(), Toast.LENGTH_SHORT).show();
            APPUtils.intentLogin(this, saveMesgBean.getStatus());

        }

    }

    private void getServiceMine() {
        isSave = false;//判断是保存信息还是获取信息 true：保存信息 false：获取信息
        if (judgeTimeDev) {

            String sign = Md5Utils.createMD5("devcode=" + mDevcode + "timestamp=" + mTimestamp + "uid=" + userID + URLUtils.WK_APP_KEY);
            RequestBody requestBody = new FormBody.Builder()
                    .add("devcode", mDevcode)
                    .add("signature", sign)
                    .add("timestamp", String.valueOf(mTimestamp))
                    .add("uid", userID)
                    .build();
            OkHttpUtils.getInstance().post(URLUtils.GET_PERSON_MESG, requestBody, new HttpCallBack(new SaveMesgEvent()));
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
