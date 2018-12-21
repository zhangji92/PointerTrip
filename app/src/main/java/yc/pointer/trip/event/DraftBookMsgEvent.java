package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.CityBean;
import yc.pointer.trip.bean.DraftBookBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Created by 刘佳伟
 * 2017/8/3
 * 17:41
 */
public class DraftBookMsgEvent extends BaseHttpEvent<DraftBookBean> {
    @Override
    public Type getValueType() {
        return  new TypeToken<DraftBookBean>(){}.getType();
    }
}
