package yc.pointer.trip.network;

import java.lang.reflect.Type;

import yc.pointer.trip.bean.PrasieMessageBean;

/**
 * Created by Lenovo
 * 2017/6/26
 * 14:35
 */
public abstract class BaseHttpEvent<V>
{
    private V data;

    private boolean isTimeOut;

    public V getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = (V) data;
    }

    public boolean isTimeOut() {
        return isTimeOut;
    }

    public void setTimeOut(boolean timeOut) {
        isTimeOut = timeOut;
    }

    /**
     * 子类实现只需要将 V 替换掉就好
     *  return new TypeToken<V>() {}.getType();
     * @return
     */
    public abstract Type getValueType();
}
