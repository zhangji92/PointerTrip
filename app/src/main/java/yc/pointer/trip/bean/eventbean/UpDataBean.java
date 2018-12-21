package yc.pointer.trip.bean.eventbean;

/**
 * Created by 张继
 * 2018/7/12
 * 15:37
 * 公司：
 * 描述：使用EventBus 替换广播更新数据
 */

public class UpDataBean {
    private String mData;

    public UpDataBean(String mData) {
        this.mData = mData;
    }

    public String getData() {
        return mData;
    }
}
