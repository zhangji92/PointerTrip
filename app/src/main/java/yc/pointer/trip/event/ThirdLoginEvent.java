package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.SaveMesgBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by moyan on 2017/11/30.
 */

public class ThirdLoginEvent extends BaseHttpEvent<SaveMesgBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<SaveMesgBean>(){
        }.getType();
    }
}
