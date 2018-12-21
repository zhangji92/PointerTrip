package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.ScenicBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by 张继
 * 2017/8/1
 * 3：24
 * 景点event
 */

public class ScenicEvent extends BaseHttpEvent<ScenicBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<ScenicBean>(){}.getType();
    }
}
