package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.BillCommentBean;
import yc.pointer.trip.bean.ComplainTagBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Created by moyan on 2017/9/8.
 */
public class ComplainEvent extends BaseHttpEvent<ComplainTagBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<ComplainTagBean>(){}.getType();
    }
}
