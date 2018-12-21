package yc.pointer.trip.fragment;

import yc.pointer.trip.R;
import yc.pointer.trip.base.BaseViewPageFragment;

/**
 * Created by 张继
 * 2018/12/11
 * 11:34
 * 公司：
 * 描述：懒加载模式下的个人主页
 */

public class ScreenPersonFragment extends BaseViewPageFragment {
    @Override
    protected int getContentViewLayout() {
        return R.layout.fragment_person_screen;
    }

    @Override
    protected void initView() {

    }

    /**
     * 判断当前页面是否处于显示状态
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isVisble = true;
            onVisible();
        } else {
            isVisble = false;
            onInVisible();
        }
    }

    //隐藏状态
    protected void onInVisible() {

    }

    //显示状态
    protected void onVisible() {
        if (!isVisble || !isPrepared) {
            return;
        }
    }

    @Override
    protected boolean needEventBus() {
        return false;
    }
}
