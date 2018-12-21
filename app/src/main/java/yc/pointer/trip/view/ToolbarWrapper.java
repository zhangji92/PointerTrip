package yc.pointer.trip.view;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import butterknife.ButterKnife;
import yc.pointer.trip.R;
import yc.pointer.trip.base.BaseActivity;
import yc.pointer.trip.base.BaseFragment;

/**
 * Created by Administrator on 2017/6/21.
 * 顶部标题栏ToolBar展示
 */
public class ToolbarWrapper {
    private BaseActivity mBaseActivity;

    private TextView mTvTitle;
    private TextView mTVRight;
    private Toolbar toolbar;

    public ToolbarWrapper(BaseActivity activity) {
        mBaseActivity = activity;
        toolbar = ButterKnife.findById(activity, R.id.standard_toolbar);
        init(toolbar);
        setShowBack(true);
        setShowTitle(false);
    }

    public ToolbarWrapper(BaseFragment fragment) {
        mBaseActivity = (BaseActivity) fragment.getActivity();
        //noinspection ConstantConditions
        Toolbar toolbar = ButterKnife.findById(fragment.getView(), R.id.standard_toolbar);
        init(toolbar);
        setShowBack(false);
        setShowTitle(false);

    }

    public ToolbarWrapper setShowTitle(boolean enable) {
        getSupportActionBar().setDisplayShowTitleEnabled(enable);
        return this;
    }

    public ToolbarWrapper setShowBack(boolean enable) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(enable);
        return this;
    }

    public ToolbarWrapper setCustomTitle(int resId) {
        if (mTvTitle == null) {
            throw new UnsupportedOperationException("No title textview in toolbar.");
        }
        mTvTitle.setText(resId);
        return this;
    }
    public ToolbarWrapper setCustomTitle(String title) {
        if (mTvTitle == null) {
            throw new UnsupportedOperationException("No title textview in toolbar.");
        }
        mTvTitle.setText(title);
        return this;
    }
    public ToolbarWrapper setTitle(String title) {
        if (mTvTitle == null) {
            throw new UnsupportedOperationException("No title textview in toolbar.");
        }
        mTvTitle.setText(title);
        return this;
    }

    /**
     * 设置toolbar右边文本
     * @param resId
     * @return
     */
    public ToolbarWrapper setRightText(int resId){
        if (mTVRight == null) {
            throw new UnsupportedOperationException("No right title textview in toolbar.");
        }
        mTVRight.setText(resId);
        return this;
    }

    private void init(Toolbar toolbar) {
        mTvTitle = ButterKnife.findById(toolbar, R.id.standard_toolbar_title);
        mTVRight = ButterKnife.findById(toolbar, R.id.satandard_toolbar_right);
        mBaseActivity.setSupportActionBar(toolbar);
    }

    private ActionBar getSupportActionBar() {
        return mBaseActivity.getSupportActionBar();
    }

}
