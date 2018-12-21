package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.WriteBookBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Created by 张继
 * 2017/7/19
 * 18:41
 */
public class WriteBookEvent extends BaseHttpEvent<WriteBookBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<WriteBookBean>() {
        }.getType();
    }
}
