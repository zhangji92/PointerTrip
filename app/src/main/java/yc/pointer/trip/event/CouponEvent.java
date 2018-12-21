package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.CouponBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Create by 张继
 * 2017/9/8
 * 15:14
 */
public class CouponEvent extends BaseHttpEvent<CouponBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<CouponBean>(){}.getType();
    }
}
