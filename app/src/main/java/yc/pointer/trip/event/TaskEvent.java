package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.TaskBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by 张继
 * 2018/9/25
 * 10:59
 * 公司：
 * 描述：
 */

public class TaskEvent extends BaseHttpEvent<TaskBean> {

    @Override
    public Type getValueType() {
        return new TypeToken<TaskBean>(){}.getType();
    }
}
