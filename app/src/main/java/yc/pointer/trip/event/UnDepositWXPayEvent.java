package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.WXPayBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by moyan on 2018/1/26.
 */

public class UnDepositWXPayEvent extends BaseHttpEvent<WXPayBean> {
    @Override
    public Type getValueType() {
       return new TypeToken<WXPayBean>(){}.getType();
    }
}
