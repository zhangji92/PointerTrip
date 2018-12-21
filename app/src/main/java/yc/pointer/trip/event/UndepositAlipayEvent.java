package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.PayOrderPayTypeBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by moyan on 2018/1/26.
 */

public class UndepositAlipayEvent extends BaseHttpEvent<PayOrderPayTypeBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<PayOrderPayTypeBean>(){}.getType();
    }
}
