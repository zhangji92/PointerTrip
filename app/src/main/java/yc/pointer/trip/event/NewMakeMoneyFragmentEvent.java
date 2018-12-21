package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.NewMakeMoneyFragmentBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by 张继
 * 2018/9/19
 * 11:16
 * 公司：
 * 描述：
 */

public class NewMakeMoneyFragmentEvent extends BaseHttpEvent<NewMakeMoneyFragmentBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<NewMakeMoneyFragmentBean>(){}.getType();
    }
}
