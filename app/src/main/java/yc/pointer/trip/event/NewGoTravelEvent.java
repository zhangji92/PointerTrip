package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.NewGotravelBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by moyan on 2018/7/3.
 * 2018.7.3.版出发吧数据接收
 */

public class NewGoTravelEvent extends BaseHttpEvent<NewGotravelBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<NewGotravelBean>(){}.getType();
    }
}
