package yc.pointer.trip.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by 张继
 * 2018/12/11
 * 11:41
 * 公司：
 * 描述：新版首页viewpager适配器
 */

public class HomeScreenPagerAdapter extends FragmentPagerAdapter {
    List<Fragment> mList;

    public HomeScreenPagerAdapter(FragmentManager fm, List<Fragment> lists) {
        super(fm);
        this.mList = lists;
    }

    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size() == 0 ? 0 : mList.size();
    }
}
