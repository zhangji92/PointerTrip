package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.MakeMoneyBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Create by 张继
 * 2017/8/15
 * 17:40
 */
public class MakeMoneyEvent extends BaseHttpEvent<MakeMoneyBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<MakeMoneyBean>(){}.getType();
    }
}
