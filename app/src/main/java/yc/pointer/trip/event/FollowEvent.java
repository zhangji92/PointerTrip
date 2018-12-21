package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.FansBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by 张继
 * 2018/1/12
 * 18:46
 */

public class FollowEvent extends BaseHttpEvent<FansBean> {

    @Override
    public Type getValueType() {
        return new TypeToken<FansBean>() {
        }.getType();
    }
}
