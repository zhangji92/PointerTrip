package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.PersonalPageFollowBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by 张继
 * 2018/1/10
 * 19:00
 */

public class PersonalPageFollowEvent extends BaseHttpEvent<PersonalPageFollowBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<PersonalPageFollowBean>(){}.getType();
    }
}
