package yc.pointer.trip.bean.eventbean;

/**
 * Created by 张继
 * 2018/7/12
 * 11:36
 * 公司：
 * 描述：使用EventBus 代替广播 广播Action useCoupon
 */

public class UseCouponBean {
    private String mUseCoupon;

    public UseCouponBean(String mUseCoupon) {
        this.mUseCoupon = mUseCoupon;
    }

    public String getUseCoupon() {
        return mUseCoupon;
    }
}
