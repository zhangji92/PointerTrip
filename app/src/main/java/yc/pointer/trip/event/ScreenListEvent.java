package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.NewHomeVideoData;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by 张继
 * 2018/12/19
 * 14:46
 * 公司：
 * 描述：
 */

public class ScreenListEvent extends BaseHttpEvent<NewHomeVideoData> {
    @Override
    public Type getValueType() {
        return new TypeToken<NewHomeVideoData>(){
        }.getType();
    }
}
