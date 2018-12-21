package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.WXPayBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Created by moyan on 2017/8/22.
 * 调起微信支付参数
 */
public class WXPayEvent extends BaseHttpEvent<WXPayBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<WXPayBean>(){}.getType();
    }
}
