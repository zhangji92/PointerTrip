package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.SaveMesgBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by 张继
 * 2018/5/18
 * 11:04
 * 公司：
 * 描述：
 */

public class HomeInvitationEvent extends BaseHttpEvent<SaveMesgBean> {

    @Override
    public Type getValueType() {
        return new TypeToken<SaveMesgBean>() {
        }.getType();
    }
}
