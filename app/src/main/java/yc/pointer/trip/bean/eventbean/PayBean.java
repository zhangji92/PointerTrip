package yc.pointer.trip.bean.eventbean;

/**
 * Created by 张继
 * 2018/7/12
 * 10:18
 * 公司：
 * 描述：使用EventBus 代替广播刷新支付
 */

public class PayBean {
    private String mPay;

    public PayBean(String mPay) {
        this.mPay = mPay;
    }

    public String getPay(){
        return mPay;
    }
}
