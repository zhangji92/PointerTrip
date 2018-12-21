package yc.pointer.trip.bean.eventbean;

/**
 * Created by 张继
 * 2018/7/11
 * 18:44
 * 公司：
 * 描述：EventBus 代替广播 刷新推送
 */

public class PushMsgBen {
    private boolean mPushFlag;

    public PushMsgBen(boolean psuhFlag) {
        this.mPushFlag = psuhFlag;
    }

    public boolean isPushFlag() {
        return mPushFlag;
    }
}
