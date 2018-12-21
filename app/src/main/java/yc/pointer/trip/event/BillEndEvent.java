package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.BillBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Created by moyan on 2017/9/14.
 */
public class BillEndEvent extends BaseHttpEvent<BillBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<BillBean>(){}.getType();
    }
}
