package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.GoTravelPriceBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Created by moyan on 2017/8/10.
 */
public class GoTravelEvent extends BaseHttpEvent<GoTravelPriceBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<GoTravelPriceBean>(){}.getType();
    }
}
