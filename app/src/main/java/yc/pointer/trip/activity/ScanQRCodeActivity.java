package yc.pointer.trip.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.application.MyApplication;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.untils.AppDavikActivityMgr;
import yc.pointer.trip.untils.StatusBarUtils;

/**
 * Created by 张继
 * 2018/7/10
 * 16:11
 * 公司：
 * 描述：二维码扫描页
 */

public class ScanQRCodeActivity extends AppCompatActivity {
    /**
     * 条形码扫描视图
     */
    private DecoratedBarcodeView mBarcodeView;
    /**
     * 条形码扫描管理器
     */
    private CaptureManager mCaptureManager;
    //打开手电筒标志
    private boolean openLightFlag = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);
        StatusBarUtils.with(this).init();
        AppDavikActivityMgr.getScreenManager().addActivity(this);//当前的activity加入栈中
        mBarcodeView = findViewById(R.id.my_decorate_bracode_view);
        mCaptureManager = new CaptureManager(this, mBarcodeView);
        mCaptureManager.initializeFromIntent(getIntent(), savedInstanceState);
        mCaptureManager.decode();
        final ImageView imgLight = findViewById(R.id.code_light);
        imgLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!openLightFlag) {//打开
                    mBarcodeView.setTorchOn();
                    openLightFlag = true;
                    imgLight.setImageResource(R.mipmap.icon_light_on);
                } else {//关闭
                    mBarcodeView.setTorchOff();
                    openLightFlag = false;
                    imgLight.setImageResource(R.mipmap.icon_light);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBarcodeView != null) {//关闭闪光灯
            mBarcodeView.setTorchOff();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCaptureManager.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mCaptureManager.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCaptureManager.onDestroy();
        AppDavikActivityMgr.getScreenManager().removeActivity(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mCaptureManager.onSaveInstanceState(outState);
    }

    /**
     * 权限处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mCaptureManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 按键处理
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mBarcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }


}
