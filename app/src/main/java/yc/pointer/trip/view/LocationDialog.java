package yc.pointer.trip.view;

import android.app.Activity;
import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import yc.pointer.trip.R;

/**
 * Created by 张继
 * 2018/7/13
 * 16:18
 * 公司：
 * 描述：开启GPS
 */

public class LocationDialog extends Dialog implements View.OnClickListener {

    private String mMsg;
    private Fragment mFragment;
    private Activity mActivity;
    private int mRequestCode;

    public LocationDialog(@NonNull Context context, int themeResId, Fragment fragment, int requestCode) {
        super(context, themeResId);
        this.mFragment = fragment;
        this.mRequestCode = requestCode;
    }

    public LocationDialog(@NonNull Context context, int themeResId, Activity mActivity, int requestCode) {
        super(context, themeResId);
        this.mActivity = mActivity;
        this.mRequestCode = requestCode;
    }

    public LocationDialog setMsg(String msg) {
        this.mMsg = msg;
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_dialog);
        setCanceledOnTouchOutside(false);
        TextView locationMsg = findViewById(R.id.location_msg);
        locationMsg.setText(mMsg);
        ImageView imgClose = findViewById(R.id.location_close);
        imgClose.setOnClickListener(this);
        Button btnStart = findViewById(R.id.location_star);
        btnStart.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.location_star:
                if (mFragment != null) {
                    dismiss();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    mFragment.startActivityForResult(intent, mRequestCode); // 设置完成后返回到原来的界面

                } else if (mActivity != null) {
                    dismiss();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    mActivity.startActivityForResult(intent, mRequestCode); // 设置完成后返回到原来的界面

                }

                break;
            case R.id.location_close:
                dismiss();
                if (mLocationCallBack != null) {
                    mLocationCallBack.closeDialog();
                }

                break;
        }
    }

    private LocationCallBack mLocationCallBack;

    public void setLocationCallBack(LocationCallBack mLocationCallBack) {
        this.mLocationCallBack = mLocationCallBack;
    }

    public interface LocationCallBack {
        void closeDialog();

    }
}
