package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.ApkUpdateBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Create by 张继
 * 2017/9/7
 * 17:37
 */
public class ApkUpdateEvent extends BaseHttpEvent<ApkUpdateBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<ApkUpdateBean>(){}.getType();
    }
}
