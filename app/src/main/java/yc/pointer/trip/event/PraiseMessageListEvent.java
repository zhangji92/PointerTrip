package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.PrasieMessageBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by moyan on 2018/6/14.
 */

public class PraiseMessageListEvent extends BaseHttpEvent<PrasieMessageBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<PrasieMessageBean>(){}.getType();
    }
}
