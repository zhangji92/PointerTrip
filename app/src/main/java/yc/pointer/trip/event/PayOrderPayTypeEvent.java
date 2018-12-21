package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.PayOrderPayTypeBean;
import yc.pointer.trip.network.BaseHttpEvent;
import yc.pointer.trip.network.HttpCallBack;

import java.lang.reflect.Type;

/**
 * Create by 张继
 * 2017/8/11
 * 15:41
 */
public class PayOrderPayTypeEvent extends BaseHttpEvent<PayOrderPayTypeBean>{

    @Override
    public Type getValueType() {
        return new TypeToken<PayOrderPayTypeBean>(){}.getType();
    }
}
