package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.SaveMesgBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by moyan on 2017/8/30.
 */
public class HomeMoveLoginEvent extends BaseHttpEvent<SaveMesgBean> {
    @Override
    public Type getValueType(){
        return new TypeToken<SaveMesgBean>(){
        }.getType();
    }
}
