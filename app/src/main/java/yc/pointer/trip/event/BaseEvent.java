package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.base.BaseBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by 张继
 * 2018/4/17
 * 16:10
 * 公司:天津悦程嘉和网络科技有限公司
 * 描述:
 */

public class BaseEvent extends BaseHttpEvent<BaseBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<BaseBean>(){}.getType();
    }
}
