package yc.pointer.trip.bean.eventbean;

/**
 * Created by 张继
 * 2018/9/19
 * 17:43
 * 公司：
 * 描述：
 */

public class NewMakeMoneyBusBean {
    private boolean loadData;

    public NewMakeMoneyBusBean(boolean loadData) {
        this.loadData = loadData;
    }

    public boolean isLoadData() {
        return loadData;
    }

    public void setLoadData(boolean loadData) {
        this.loadData = loadData;
    }
}
