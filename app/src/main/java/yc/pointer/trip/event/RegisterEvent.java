package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.CodeBean;
import yc.pointer.trip.bean.RegisterBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Created by 张继
 * 2017/7/14
 * 14:12
 */
public class RegisterEvent extends BaseHttpEvent<RegisterBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<RegisterBean>() {
        }.getType();
    }
}
