package yc.pointer.trip.base;


import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/6/21.
 * 通用的fragment
 */
public abstract class BaseFragment extends Fragment {
    private Unbinder mUnbinder;

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
                if (!EventBus.getDefault().isRegistered(this)) {
                    EventBus.getDefault().register(this);
                }
            } catch (EventBusException e) {
                Log.e("needEventBus","该类中没有 接收 eventbus 回调的方法");
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
                if (EventBus.getDefault().isRegistered(this)) {
                    EventBus.getDefault().unregister(this);
                }
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
