package yc.pointer.trip.bean;

/**
 * Created by 张继
 * 2017/8/2 0002
 * 下午 6:44
 */

public class BaseScenicBean {
    /**
     * sid : 411
     * title : 123123
     * pic : http://139.196.106.89:9999/Image/Scenery/水上公园.png
     * brief : 简介
     * price : 10
     * zz : 指针旅游协议价
     */

    private boolean isSelect=false;
    private String sid;
    private String title;
    private String pic;
    private String brief;
    private String price;
    private String zz;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getZz() {
        return zz;
    }

    public void setZz(String zz) {
        this.zz = zz;
    }
}
