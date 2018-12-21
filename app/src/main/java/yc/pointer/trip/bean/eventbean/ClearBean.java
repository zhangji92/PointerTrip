package yc.pointer.trip.bean.eventbean;

/**
 * Created by 张继
 * 2018/7/12
 * 16:15
 * 公司：
 * 描述：eventBus 代替clear广播
 */

public class ClearBean {
    private String mClear;

    public ClearBean(String clear) {
        this.mClear = clear;
    }

    public String getClear() {
        return mClear;
    }
}
