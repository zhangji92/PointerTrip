package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.base.BaseBean;
import yc.pointer.trip.bean.WXPayBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by moyan on 2018/4/2.
 */

public class DeleteCommentEvent extends BaseHttpEvent<BaseBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<BaseBean>(){}.getType();
    }
}
