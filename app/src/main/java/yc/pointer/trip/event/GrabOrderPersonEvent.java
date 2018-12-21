package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.OrderDetailBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Create by 张继
 * 2017/9/25
 * 16:04
 */
public class GrabOrderPersonEvent extends BaseHttpEvent<OrderDetailBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<OrderDetailBean>(){}.getType();
    }
}
