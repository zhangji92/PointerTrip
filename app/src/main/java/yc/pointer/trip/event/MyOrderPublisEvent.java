package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.UnpaidBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by moyan on 2018/4/23.
 */

public  class MyOrderPublisEvent extends BaseHttpEvent<UnpaidBean>{
    @Override
    public Type getValueType() {
        return new TypeToken<UnpaidBean>(){}.getType();
    }
}
