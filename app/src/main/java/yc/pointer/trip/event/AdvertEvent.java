package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.AdvertBean;
import yc.pointer.trip.bean.ApkUpdateBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Created by moyan on 2017/9/12.
 */
public class AdvertEvent extends BaseHttpEvent<AdvertBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<AdvertBean>(){}.getType();
    }
}
