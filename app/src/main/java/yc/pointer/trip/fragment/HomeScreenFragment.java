package yc.pointer.trip.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import yc.pointer.trip.R;
import yc.pointer.trip.adapter.HomeScreenPagerAdapter;
import yc.pointer.trip.base.BaseFragment;

/**
 * Created by 张继
 * 2018/12/10
 * 15:39
 * 公司：
 * 描述：防微视首页
 */

public class HomeScreenFragment extends BaseFragment {
    @BindView(R.id.screen_pager)
    ViewPager screenPager;
    List<Fragment> mList;

    @Override
    protected int getContentViewLayout() {
        return R.layout.home_screen;
    }

    @Override
    protected void initView() {
        mList = new ArrayList<>();
        ScreenListFragment listFragment = new ScreenListFragment();
        ScreenPersonFragment personFragment = new ScreenPersonFragment();
        mList.add(listFragment);
        mList.add(personFragment);
        screenPager.setAdapter(new HomeScreenPagerAdapter(getFragmentManager(), mList));
    }

    @Override
    protected boolean needEventBus() {
        return true;
    }
}
