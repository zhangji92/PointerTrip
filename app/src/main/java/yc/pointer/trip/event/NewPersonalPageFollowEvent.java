package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.PersonalPageFollowBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by Trip on 2018/4/24.
 */

public class NewPersonalPageFollowEvent extends BaseHttpEvent<PersonalPageFollowBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<PersonalPageFollowBean>(){}.getType();
    }
}
