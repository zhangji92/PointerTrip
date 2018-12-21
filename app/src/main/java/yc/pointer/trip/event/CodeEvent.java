package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.CodeBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Created by 张继
 * 2017/7/13
 * 17:40
 */
public class CodeEvent extends BaseHttpEvent<CodeBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<CodeBean>() {
        }.getType();
    }
}
