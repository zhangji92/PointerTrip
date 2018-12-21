package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.GetAllTaskMoneyBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by moyan on 2018/10/11.
 */

public class GetAllTaskMoneyEvent extends BaseHttpEvent<GetAllTaskMoneyBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<GetAllTaskMoneyBean>(){}.getType();
    }
}
