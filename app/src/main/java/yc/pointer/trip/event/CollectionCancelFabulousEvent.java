package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.BookDetailsCollectionBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Create by 张继
 * 2017/9/27
 * 12:53
 */
public class CollectionCancelFabulousEvent extends BaseHttpEvent<BookDetailsCollectionBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<BookDetailsCollectionBean>(){}.getType();
    }
}
