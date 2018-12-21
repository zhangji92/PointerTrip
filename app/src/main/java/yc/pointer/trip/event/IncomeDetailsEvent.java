package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.IncomeDetailsBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by moyan on 2018/4/25.
 */

public class IncomeDetailsEvent extends BaseHttpEvent<IncomeDetailsBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<IncomeDetailsBean>(){}.getType();
    }
}
