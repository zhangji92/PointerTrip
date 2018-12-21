package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.WXPayBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Created by moyan on 2017/10/23.
 */
public class ActionWeixinPayEvent extends BaseHttpEvent<WXPayBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<WXPayBean>(){}.getType();
    }
}
