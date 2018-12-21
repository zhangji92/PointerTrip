package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.ReplyBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by moyan on 2018/5/8.
 */

public class ReplyMessageEvent extends BaseHttpEvent<ReplyBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<ReplyBean>(){}.getType();
    }
}
