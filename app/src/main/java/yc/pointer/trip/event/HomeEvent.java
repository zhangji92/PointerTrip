package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;
import yc.pointer.trip.bean.HomeDataBean;
import yc.pointer.trip.bean.ReadBookBean;
import yc.pointer.trip.network.BaseHttpEvent;

import java.lang.reflect.Type;

/**
 * Created by 刘佳伟
 * 2017/7/24
 * 18:20
 */
public class HomeEvent extends BaseHttpEvent<HomeDataBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<HomeDataBean>(){
        }.getType();
    }
}
