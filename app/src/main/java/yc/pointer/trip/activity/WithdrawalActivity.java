package yc.pointer.trip.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import yc.pointer.trip.R;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.view.ToolbarWrapper;

/**
 * Created by moyan on 2018/1/12.
 */

public class WithdrawalActivity extends BaseActivity {
    @BindView(R.id.button)
    Button button;
    private UMShareAPI mShareAPI;

    @Override
    protected int getContentViewLayout() {
        return R.layout.withdrawal_layout;
    }

    @Override
    protected void initView() {
        new ToolbarWrapper(this).setCustomTitle(R.string.withdrawal_title);
        mShareAPI = UMShareAPI.get(WithdrawalActivity.this);
    }

    @Override
    protected boolean needEventBus() {

        return true;

    }


    @OnClick(R.id.button)
    public void onViewClicked() {
        //跳转微信
        boolean isHaveWeiXin = mShareAPI.isInstall(this, SHARE_MEDIA.WEIXIN);
        if (isHaveWeiXin) {
            IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
            msgApi.openWXApp();
        } else {

            Toast.makeText(this, "亲，您尚未安装微信客户端,请先前往应用商店下载", Toast.LENGTH_SHORT).show();

        }

    }
}
