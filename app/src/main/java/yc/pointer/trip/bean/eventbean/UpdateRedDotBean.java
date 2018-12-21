package yc.pointer.trip.bean.eventbean;

/**
 * Created by 张继
 * 2018/7/11
 * 18:48
 * 公司：
 * 描述：使用eventBus 代替广播 刷新小红点
 */

public class UpdateRedDotBean {
    private boolean mDot;

    public boolean isDot() {
        return mDot;
    }

    public UpdateRedDotBean(boolean mDot) {
        this.mDot = mDot;
    }
}
