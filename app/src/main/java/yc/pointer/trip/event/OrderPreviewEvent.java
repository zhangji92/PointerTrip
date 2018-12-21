package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.OrderPreviewBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Create by 张继
 * 2017/8/11
 * 11:06
 */
public class OrderPreviewEvent extends BaseHttpEvent<OrderPreviewBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<OrderPreviewBean>(){}.getType();
    }
}
