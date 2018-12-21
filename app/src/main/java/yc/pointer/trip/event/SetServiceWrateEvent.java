package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.SetServiceWrateBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by 张继 on 2017/11/20.
 */

public class SetServiceWrateEvent extends BaseHttpEvent<SetServiceWrateBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<SetServiceWrateBean>(){}.getType();
    }
}
