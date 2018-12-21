package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.BookDetailsCollectionBean;
import yc.pointer.trip.bean.MyBookBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Created by 刘佳伟
 * 2017/8/2
 * 16:59
 */
public class MyBookEvent extends BaseHttpEvent<MyBookBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<MyBookBean>(){}.getType();
    }
}
