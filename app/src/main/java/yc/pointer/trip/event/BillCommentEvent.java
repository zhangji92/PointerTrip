package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.BillCommentBean;
import yc.pointer.trip.bean.CityBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Create By 张继
 * 2017/7/31 0031
 * 上午 10:10
 */
public class BillCommentEvent extends BaseHttpEvent<BillCommentBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<BillCommentBean>(){}.getType();
    }
}
