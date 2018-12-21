package yc.pointer.trip.bean.eventbean;

/**
 * Created by 张继
 * 2018/7/12
 * 14:47
 * 公司：
 * 描述：使用EventBus 代替广播 广播action 刷新体现
 */

public class RefreshEmbodyBean {
    private String mEmbody;

    public RefreshEmbodyBean(String mEmbody) {
        this.mEmbody = mEmbody;
    }

    public String getEmbody() {
        return mEmbody;
    }
}
