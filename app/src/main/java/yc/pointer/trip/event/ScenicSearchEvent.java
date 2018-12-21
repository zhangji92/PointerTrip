package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.ScenicSearchBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by 张继
 * 2017/8/2 0002
 * 下午 6:33
 * 景点搜索的event
 */

public class ScenicSearchEvent extends BaseHttpEvent<ScenicSearchBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<ScenicSearchBean>(){}.getType();
    }
}
