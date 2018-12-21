package yc.pointer.trip.event;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.MessageListBean;
import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by 张继
 * 2018/4/2
 * 16:48
 * 公司:天津悦程嘉和网络科技有限公司
 * 描述:
 */

public class MessageListEvent extends BaseHttpEvent<MessageListBean> {
    @Override
    public Type getValueType() {
        return new TypeToken<MessageListBean>(){}.getType();
    }
}
