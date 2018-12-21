package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.BookDetailsFabulousBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Create By 张继
 * 2017/7/27 0027
 * 下午 3:46
 */
public class BookDetailsFabulousEvent extends BaseHttpEvent<BookDetailsFabulousBean> {

    @Override
    public Type getValueType() {
        return new TypeToken<BookDetailsFabulousBean>(){}.getType();
    }
}
