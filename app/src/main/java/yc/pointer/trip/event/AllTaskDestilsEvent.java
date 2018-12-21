package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.AllTaskDestilsBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by moyan on 2018/10/11.
 * 总任务界面数据Event
 */

public class AllTaskDestilsEvent extends BaseHttpEvent<AllTaskDestilsBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<AllTaskDestilsBean>(){}.getType();
    }
}
