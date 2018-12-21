package yc.pointer.trip.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moyan on 2018/6/12.
 * 站内信页面的ViewPager适配器（加载Fragment）
 */

public class SystemMessageViewpagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> datas=new ArrayList<>();

    public SystemMessageViewpagerAdapter(FragmentManager fm,List<Fragment> datas) {
        super(fm);
        this.datas=datas;
    }

    @Override
    public Fragment getItem(int position) {
        return datas.get(position);
    }

    @Override
    public int getCount() {
        return datas.size()==0?0:datas.size();
    }
}
