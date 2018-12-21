package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.ActionAlipayBean;
import yc.pointer.trip.bean.PayOrderPayTypeBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Created by moyan on 2017/10/23.
 */
public class ActionAlipayEvent extends BaseHttpEvent<PayOrderPayTypeBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<PayOrderPayTypeBean>(){}.getType();
    }
}
