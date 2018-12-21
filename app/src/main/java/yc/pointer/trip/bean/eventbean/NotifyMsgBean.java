package yc.pointer.trip.bean.eventbean;

import yc.pointer.trip.bean.BaseUnMsgBean;

/**
 * Created by 张继
 * 2018/7/12
 * 16:51
 * 公司：
 * 描述：代替广播NotifyMsg
 */

public class NotifyMsgBean {
    private BaseUnMsgBean bean;
    private String mNotifyMsg;

    public NotifyMsgBean(BaseUnMsgBean bean, String mNotifyMsg) {
        this.bean = bean;
        this.mNotifyMsg = mNotifyMsg;
    }

    public BaseUnMsgBean getBean() {
        return bean;
    }

    public void setBean(BaseUnMsgBean bean) {
        this.bean = bean;
    }

    public String getmNotifyMsg() {
        return mNotifyMsg;
    }

    public void setNotifyMsg(String mNotifyMsg) {
        this.mNotifyMsg = mNotifyMsg;
    }
}
