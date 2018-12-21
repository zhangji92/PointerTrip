package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.BillCommentBean;
import yc.pointer.trip.bean.BookReserveBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Created by moyan on 2017/9/5.
 * 预约
 */
public class BookReserveEvent extends BaseHttpEvent<BookReserveBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<BookReserveBean>(){}.getType();
    }
}
