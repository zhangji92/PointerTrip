package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.BookDetailsFabulousBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by moyan on 2018/8/13.
 */

public class NewHomeAddLikeEvent extends BaseHttpEvent<BookDetailsFabulousBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<BookDetailsFabulousBean >(){}.getType();
    }
}
