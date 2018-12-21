package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.ActivityTripBean;
import yc.pointer.trip.bean.CommentMessageBean;
import yc.pointer.trip.bean.CommentsListBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by moyan on 2018/3/28.
 */

public class CommentsEvent extends BaseHttpEvent<CommentMessageBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<CommentMessageBean>(){}.getType();
    }
}
