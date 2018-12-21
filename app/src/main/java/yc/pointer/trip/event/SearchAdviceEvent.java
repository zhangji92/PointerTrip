package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.SearchAdviceBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Create By 张继
 * 2017/7/31 0031
 * 上午 11:51
 */
public class SearchAdviceEvent extends BaseHttpEvent<SearchAdviceBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<SearchAdviceBean>(){}.getType();
    }
}
