package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.MyReserveBean;
import yc.pointer.trip.bean.UnpaidBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Created by moyan on 2017/9/6.
 */
public class MyRserveEvent extends BaseHttpEvent<MyReserveBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<MyReserveBean >(){}.getType();
    }
}
