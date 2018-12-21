package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.SaveMesgBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Created by 刘佳伟
 * 2017/8/8
 * 16:10
 */
public class SaveMesgEvent extends BaseHttpEvent<SaveMesgBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<SaveMesgBean>(){}.getType();
    }
}
