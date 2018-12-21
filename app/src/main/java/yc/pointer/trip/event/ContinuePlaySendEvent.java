package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.ContinuePlaySendBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Create by 张继
 * 2017/9/5
 * 16:30
 */
public class ContinuePlaySendEvent extends BaseHttpEvent<ContinuePlaySendBean>{
    @Override
    public Type getValueType() {
        return new TypeToken<ContinuePlaySendBean>(){}.getType();
    }
}
