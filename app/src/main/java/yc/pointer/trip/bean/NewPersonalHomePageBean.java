package yc.pointer.trip.bean;

/**
 * Created by Trip on 2018/4/24.
 * 个人主页返回数据类型的bean删除慎重
 */

public class NewPersonalHomePageBean {

    private String uid;
    private int type;

    public NewPersonalHomePageBean() {
    }

    public NewPersonalHomePageBean(String uid, int type) {
        this.uid = uid;
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public int getType() {
        return type;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setType(int type) {
        this.type = type;
    }
}
