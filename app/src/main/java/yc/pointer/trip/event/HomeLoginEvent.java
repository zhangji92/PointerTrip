package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.SaveMesgBean;
import yc.pointer.trip.bean.ScenicBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Created by moyan on 2017/8/30.
 */
public class HomeLoginEvent extends BaseHttpEvent<SaveMesgBean> {
    @Override
    public Type getValueType(){
        return new TypeToken<SaveMesgBean>(){
        }.getType();
    }
}
