package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.UnpaidBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by moyan on 2018/4/23.
 * 已抢订单Event数据
 */

public class MyOrderGrabEvent extends BaseHttpEvent<UnpaidBean> {
    @Override
    public Type getValueType() {
        return  new TypeToken<UnpaidBean>(){}.getType();
    }
}
