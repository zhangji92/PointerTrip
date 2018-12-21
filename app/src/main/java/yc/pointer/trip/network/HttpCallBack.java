package yc.pointer.trip.network;

/**
 * Created by Lenovo
 * 2017/6/26
 * 14:38
 */
public class HttpCallBack<T extends BaseHttpEvent>
{
    private T event;

    public HttpCallBack(T event) {
        this.event = event;
    }

    public T getEvent() {
        return event;
    }
}
