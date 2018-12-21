package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.ReadBookBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Created by 张继
 * 2017/7/20
 * 13:24
 */
public class ReadBookEvent extends BaseHttpEvent<ReadBookBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<ReadBookBean>(){
        }.getType();
    }
}
