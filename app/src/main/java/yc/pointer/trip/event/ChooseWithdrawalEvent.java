package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.ChooseWithdrawalBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by moyan on 2018/4/26.
 */

public class ChooseWithdrawalEvent extends BaseHttpEvent<ChooseWithdrawalBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<ChooseWithdrawalBean>(){}.getType();
    }
}
