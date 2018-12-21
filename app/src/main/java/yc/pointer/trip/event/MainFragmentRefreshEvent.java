package yc.pointer.trip.event;

/**
 * Created by 张继
 * 2018/3/8
 * 14:19
 * 公司:天津悦程嘉和网络科技有限公司
 * 描述: 首页双击刷新页面的event
 */

public class MainFragmentRefreshEvent {
    private boolean flag;

    public boolean isFlag() {
        return flag;
    }

    public MainFragmentRefreshEvent(boolean flag) {
        this.flag = flag;
    }
}
