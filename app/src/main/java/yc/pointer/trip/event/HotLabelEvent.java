package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.HotLabelBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Create By 张继
 * 2017/7/30 0030
 * 下午 2:55
 */
public class HotLabelEvent extends BaseHttpEvent<HotLabelBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<HotLabelBean>(){}.getType();
    }
}
