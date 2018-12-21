package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.ActivityTripBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by 张继 on 2017/11/22.
 */

public class ActivityTripEvent extends BaseHttpEvent<ActivityTripBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<ActivityTripBean>(){}.getType();
    }
}
