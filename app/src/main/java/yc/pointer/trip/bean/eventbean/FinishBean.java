package yc.pointer.trip.bean.eventbean;

/**
 * Created by 张继
 * 2018/7/12
 * 16:28
 * 公司：
 * 描述：eventbus 代替广播finish
 */

public class FinishBean {
    private String finish;

    public FinishBean(String finish) {
        this.finish = finish;
    }

    public String getFinish() {
        return finish;
    }
}
