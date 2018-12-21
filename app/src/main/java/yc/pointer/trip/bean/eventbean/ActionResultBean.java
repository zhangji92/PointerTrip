package yc.pointer.trip.bean.eventbean;

/**
 * Created by 张继
 * 2018/7/12
 * 10:30
 * 公司：
 * 描述：使用eventBus 代替广播 广播接受字段 actionResult
 */

public class ActionResultBean {
    private String mActionResult;

    public ActionResultBean(String mActionResult) {
        this.mActionResult = mActionResult;
    }
    public String getActionResult(){
        return mActionResult;
    }
}
