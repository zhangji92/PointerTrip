package yc.pointer.trip.bean.eventbean;

/**
 * Created by 张继
 * 2018/7/12
 * 15:04
 * 公司：
 * 描述：使用eventbus替换广播OrderNotify
 */

public class OrderNotifyBean {

    private String mOrderNotify;

    public OrderNotifyBean(String mOrderNotify) {
        this.mOrderNotify = mOrderNotify;
    }

    public String getOrderNotify() {
        return mOrderNotify;
    }
}
