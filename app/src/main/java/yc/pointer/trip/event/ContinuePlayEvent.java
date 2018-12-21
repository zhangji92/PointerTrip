package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.ContinuePlayBean;
import yc.pointer.trip.network.BaseHttpEvent;
import yc.pointer.trip.network.HttpCallBack;

import java.lang.reflect.Type;

/**
 * Create by 张继
 * 2017/9/5
 * 13:38
 */
public class ContinuePlayEvent extends BaseHttpEvent<ContinuePlayBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<ContinuePlayBean>(){}.getType();
    }
}
