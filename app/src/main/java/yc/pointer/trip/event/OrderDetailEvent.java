package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.OrderDetailBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Create by 张继
 * 2017/8/15
 * 13:09
 */
public class OrderDetailEvent extends BaseHttpEvent<OrderDetailBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<OrderDetailBean>(){}.getType();
    }
}
