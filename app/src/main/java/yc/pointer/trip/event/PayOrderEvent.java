package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.PayOrderBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Create by 张继
 * 2017/8/11
 * 13:59
 */
public class PayOrderEvent extends BaseHttpEvent<PayOrderBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<PayOrderBean>(){}.getType();
    }
}
