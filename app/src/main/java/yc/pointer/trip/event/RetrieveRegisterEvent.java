package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.RegisterBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Create by 张继
 * 2017/9/27
 * 13:05
 */
public class RetrieveRegisterEvent extends BaseHttpEvent<RegisterBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<RegisterBean>() {
        }.getType();
    }
}