package yc.pointer.trip.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import yc.pointer.trip.R;
import yc.pointer.trip.activity.LoginActivity;
import yc.pointer.trip.base.BaseFragment;
import yc.pointer.trip.untils.DensityUtil;
import yc.pointer.trip.untils.StatusBarUtils;
import yc.pointer.trip.view.ToolbarWrapper;

/**
 * Created by 刘佳伟
 * 2017/8/1
 * 10:17
 */
public class UnLoginGoTravel extends BaseFragment {
    @BindView(R.id.go_login)
    Button goLogin;//立即登录按钮
    @BindView(R.id.standard_toolbar)
    Toolbar standardToolbar;

    @Override
    protected int getContentViewLayout() {
        return R.layout.un_login_gotravel;
    }

    @Override
    protected void initView() {
//        StatusBarUtils.with(getActivity()).init();//设置沉浸式
        View decorView = getActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        DensityUtil.setStatusBarColor(getActivity(), R.color.colorTitle);

        int statusBarHeight = StatusBarUtils.getStatusBarHeight(getActivity());
        standardToolbar.setPadding(0, statusBarHeight, 0, 0);
        ToolbarWrapper toolbarWrapper = new ToolbarWrapper(this);
        toolbarWrapper.setCustomTitle(R.string.go_title);
        toolbarWrapper.setShowBack(false);
        goLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转登录
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra("logFlag", "unlogin");
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }


}
