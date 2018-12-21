package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.SearchResultBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Create By 张继
 * 2017/7/30 0030
 * 下午 3:41
 */
public class SearchResultEvent extends BaseHttpEvent<SearchResultBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<SearchResultBean>(){}.getType();
    }
}
