package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.PersonalPageFollowBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by moyan on 2018/6/15.
 */

public class FansFollowEvent extends BaseHttpEvent<PersonalPageFollowBean> {

    @Override
    public Type getValueType() {
        return new TypeToken<PersonalPageFollowBean>(){}.getType();
    }
}
