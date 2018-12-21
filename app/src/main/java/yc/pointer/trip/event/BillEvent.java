package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.BillBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Create by 张继
 * 2017/8/16
 * 18:27
 */
public class BillEvent extends BaseHttpEvent<BillBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<BillBean>(){}.getType();

    }
}
