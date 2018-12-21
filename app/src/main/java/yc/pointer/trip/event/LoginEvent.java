package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.SaveMesgBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Created by Lenovo
 * 2017/6/26
 * 21:18
 */
public class LoginEvent extends BaseHttpEvent<SaveMesgBean>
{
    @Override
    public Type getValueType()
    {
        return new TypeToken<SaveMesgBean>(){
        }.getType();
    }
}
