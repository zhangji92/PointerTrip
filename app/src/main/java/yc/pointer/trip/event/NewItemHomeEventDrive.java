package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.NewHomeVideoData;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by moyan on 2018/8/14.
 */

public class NewItemHomeEventDrive extends BaseHttpEvent<NewHomeVideoData> {
    @Override
    public Type getValueType() {
        return new TypeToken<NewHomeVideoData>(){
        }.getType();
    }
}