package yc.pointer.trip.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.untils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by 张继
 * 2017/8/30
 * 13:02
 * 引导页
 */
public class BootPageActivity extends BaseActivity {
    @BindView(R.id.boot_viewPager)
    ViewPager bootViewPager;
    @BindView(R.id.boot_point)
    LinearLayout bootPoint;
    @BindView(R.id.boot_bnt)
    Button bootBnt;

    private int mPoistion;
    private List<ImageView> mListImage = new ArrayList<>();
    private int[] mImgIds = new int[]{R.mipmap.yindaoye_one,R.mipmap.yinyaoye_two,R.mipmap.yindaouye_three};


    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_boot_pager;
    }


    @Override
    protected void initView() {
        //隐藏状态栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        SharedPreferencesUtils.getInstance().putBoolean(this, "BootPageActivity", true);
        for (int imgId : mImgIds) {
            ImageView imageView = new ImageView(getApplicationContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(imgId);
            mListImage.add(imageView);
        }
        initViewPager(mListImage.size());
        bootViewPager.setAdapter(new PagerAdapter() {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {

                container.addView(mListImage.get(position));
                return mListImage.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                container.removeView(mListImage.get(position));
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public int getCount() {
                return mImgIds.length==0?0:mImgIds.length;
            }
        });
        bootViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == mImgIds.length-1) {
                    bootBnt.setVisibility(View.VISIBLE);
                    bootPoint.setVisibility(View.GONE);
                } else {
                    bootBnt.setVisibility(View.GONE);
                    bootPoint.setVisibility(View.VISIBLE);
                }
                int i = position % mListImage.size();
                bootPoint.getChildAt(i).setSelected(true);
                bootPoint.getChildAt(mPoistion).setSelected(false);
                mPoistion = i;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

                }
        });

        bootBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BootPageActivity.this, MainActivity.class));
                finish();
            }
        });
    }
    //初始化小点
    private void initViewPager(int image) {
        if (image == 0)
            return;
        for (int i = 0; i < image; i++) {
            ImageView point = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 0, 10, 0);
            point.setLayoutParams(params);
            point.setBackgroundResource(R.drawable.boot_selector);
            if (i == 0) {
                point.setSelected(true);
            } else {
                point.setSelected(false);
            }
            bootPoint.addView(point);
        }
    }
    @Override
    protected boolean needEventBus() {
        return true;
    }

}
