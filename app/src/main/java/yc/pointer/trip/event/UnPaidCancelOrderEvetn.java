package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.base.BaseBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Create by 张继
 * 2017/9/27
 * 13:03
 */
public class UnPaidCancelOrderEvetn extends BaseHttpEvent<BaseBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<BaseBean>() {
        }.getType();
    }
}