package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.BookDetailsBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Create By 张继
 * 2017/7/26 0026
 * 下午 6:59
 */
public class BookDetailsEvent extends BaseHttpEvent<BookDetailsBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<BookDetailsBean>(){}.getType();
    }
}
