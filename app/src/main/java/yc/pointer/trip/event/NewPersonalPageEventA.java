package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.PersonalPageBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by 张继
 * 2018/1/4
 * 15:52
 */

public class NewPersonalPageEventA extends BaseHttpEvent<PersonalPageBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<PersonalPageBean>() {
        }.getType();
    }
}
