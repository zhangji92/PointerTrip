package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.UnpaidBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Create by 张继
 * 2017/9/27
 * 13:12
 */
public class MyOrderUNpaiEvent extends BaseHttpEvent<UnpaidBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<UnpaidBean>(){}.getType();
    }
}
