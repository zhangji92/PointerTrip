package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.CodeBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by moyan on 2018/1/11.
 */

public class ChangeBindingCodeEvent extends BaseHttpEvent<CodeBean> {
    @Override
    public Type getValueType() {

        return new TypeToken<CodeBean>() {
        }.getType();

    }
}
