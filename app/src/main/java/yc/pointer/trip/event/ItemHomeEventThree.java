package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.HomeVideoNewDataBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by moyan on 2017/11/21.
 */

public class ItemHomeEventThree extends BaseHttpEvent<HomeVideoNewDataBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<HomeVideoNewDataBean>(){
        }.getType();
    }
}
