package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.CommentsListBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by moyan on 2018/3/28.
 *
 */

public class CommentListEvent extends BaseHttpEvent<CommentsListBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<CommentsListBean>(){}.getType();
    }
}
