package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.BookDetailsCollectionBean;
import yc.pointer.trip.bean.CollectionBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Created by 刘佳伟
 * 2017/7/27
 * 17:05
 */
public class CollectionEvent extends BaseHttpEvent<CollectionBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<CollectionBean>(){}.getType();
    }
}
