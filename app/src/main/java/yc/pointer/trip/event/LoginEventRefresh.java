package yc.pointer.trip.event;

import yc.pointer.trip.network.BaseHttpEvent;

/**
 * Created by 张继
 * 2018/5/11
 * 14:44
 * 公司：
 * 描述：登陆刷新关注界面
 */

public class LoginEventRefresh  {

    private boolean flag;

    public boolean isFlag() {
        return flag;
    }

    public LoginEventRefresh(boolean flag) {
        this.flag = flag;
    }
}
