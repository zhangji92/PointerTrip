package yc.pointer.trip.bean.eventbean;

/**
 * Created by 张继
 * 2018/7/11
 * 18:52
 * 公司：
 * 描述：使用EventBus Receiver代替广播action刷新Receiver是1的
 */
public class ReceiverBean {
    private String mReceiver;

    public String getReceiver() {
        return mReceiver;
    }

    public ReceiverBean(String mReceiver) {
        this.mReceiver = mReceiver;
    }
}
