package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.base.BaseBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Created by 刘佳伟
 * 2017/8/4
 * 10:34
 */
public class DeleteBookEvent extends BaseHttpEvent<BaseBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<BaseBean>() {
        }.getType();
    }
}
