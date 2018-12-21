package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.BillCommentBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Create by 张继
 * 2017/9/27
 * 12:50
 */
public class GrabCommentEvent  extends BaseHttpEvent<BillCommentBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<BillCommentBean>(){}.getType();
    }
}

