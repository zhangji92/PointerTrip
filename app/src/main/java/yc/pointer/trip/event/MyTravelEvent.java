package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.MyTravelBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Create by 张继
 * 2017/8/29
 * 15:08
 */
public class MyTravelEvent extends BaseHttpEvent<MyTravelBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<MyTravelBean>(){}.getType();
    }
}
