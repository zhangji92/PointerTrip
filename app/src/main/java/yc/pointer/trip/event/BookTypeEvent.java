package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.BookTypeBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Created by 刘佳伟
 * 2017/7/28
 * 18:30
 */
public class BookTypeEvent extends BaseHttpEvent<BookTypeBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<BookTypeBean>() {
        }.getType();
    }
}
