package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.base.BaseBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Created by moyan on 2017/9/27.
 */
public class OrderDetailGrabEvent extends BaseHttpEvent<BaseBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<BaseBean>() {
        }.getType();
    }
}
