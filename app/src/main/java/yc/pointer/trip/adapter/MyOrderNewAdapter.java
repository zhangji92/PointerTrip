package yc.pointer.trip.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moyan on 2018/4/20.
 * 我的订单ViewPager
 *
 */

public class MyOrderNewAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> datas=new ArrayList<>();

    public MyOrderNewAdapter(FragmentManager fm ,List<Fragment> list) {
        super(fm);
        this.datas=list;

    }

    @Override
    public int getCount() {

        return  datas.size()==0?0:datas.size();

    }

    @Override
    public Fragment getItem(int position) {
        return datas.get(position);
    }

}
