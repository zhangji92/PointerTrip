package yc.pointer.trip.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by moyan on 2018/4/24.
 */

public abstract class BaseViewPageFragment extends Fragment {


    private Unbinder mUnbinder;
    //是否可见
    protected boolean isVisble;
    // 标志位，标志Fragment已经初始化完成。
    public boolean isPrepared = false;

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater,
                                   @Nullable ViewGroup container,
                                   @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getContentViewLayout(), container, false);
        mUnbinder = ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public final void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (needEventBus()) {
            try {
                EventBus.getDefault().register(this);
            } catch (EventBusException e) {
//                Logger.e("该类中没有 接收 eventbus 回调的方法");
            }
        }
        initView();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        mUnbinder = null;
        if (needEventBus()) {
            try {
                EventBus.getDefault().unregister(this);
            } catch (Exception e) {
                // Logger.e("该类没有注册 eventbus");
            }
        }
    }


    @LayoutRes
    protected abstract int getContentViewLayout();

    protected abstract void initView();

    protected abstract boolean needEventBus();


}
