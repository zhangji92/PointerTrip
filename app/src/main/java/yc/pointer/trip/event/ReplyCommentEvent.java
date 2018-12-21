package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.ReplyBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by moyan on 2018/6/21.
 */

public class ReplyCommentEvent extends BaseHttpEvent<ReplyBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<ReplyBean>(){}.getType();
    }
}
