package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.BookDetailsCollectionBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Create By 张继
 * 2017/7/27 0027
 * 下午 3:06
 */
public class BookDetailsCollectionEvent extends BaseHttpEvent<BookDetailsCollectionBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<BookDetailsCollectionBean>(){}.getType();
    }
}
