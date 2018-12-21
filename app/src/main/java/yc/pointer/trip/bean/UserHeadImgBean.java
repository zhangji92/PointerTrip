package yc.pointer.trip.bean;

import yc.pointer.trip.base.BaseBean;

/**
 * Created by moyan on 2017/9/20.
 */
public class UserHeadImgBean extends BaseBean {

    /**
     * pic : /Uploads/15058921001834.png
     */

    private String pic;//头像地址
    private String is_jie;//VIP认证标识

    public String getIs_jie() {
        return is_jie;
    }

    public void setIs_jie(String is_jie) {
        this.is_jie = is_jie;
    }
    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
