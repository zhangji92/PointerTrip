package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.SystemMessageBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by moyan on 2018/6/13.
 */

public class SystemMessageListEvent extends BaseHttpEvent<SystemMessageBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<SystemMessageBean>(){}.getType();
    }
}
