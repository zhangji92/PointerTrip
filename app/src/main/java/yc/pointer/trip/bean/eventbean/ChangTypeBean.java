package yc.pointer.trip.bean.eventbean;

/**
 * Created by 张继
 * 2018/7/12
 * 16:01
 * 公司：
 * 描述：
 */

public class ChangTypeBean {
    private String mChangeType;
    private int statusType;

    public ChangTypeBean(String changeType,int status) {
        this.mChangeType = changeType;
        this.statusType=status;
    }

    public String getChangeType() {
        return mChangeType;
    }

    public int getStatusType() {
        return statusType;
    }
}
