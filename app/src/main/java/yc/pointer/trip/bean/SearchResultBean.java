package yc.pointer.trip.bean;

import java.util.List;

/**
 * Create By 张继
 * 2017/7/30 0030
 * 下午 3:41
 * 搜索结果
 */
public class SearchResultBean {


    /**
     * status : 0
     * d_status : 1
     * msg : 未搜索到结果
     * data : [{"bid":"90","uid":"6","title":"天津故宫","type":"自驾","city":"天津","spot":"天津滨海新区","b_pic":"/Uploads/15011513567985.png","y_pic":"/Uploads/15011513569177.png","is_order":"1","is_ad":"1","is_index":"1","look_num":"264","zan_num":"0","ord_num":"0","status":"1","nickname":"佚名","phone":"18222396725","pic":"http://139.196.106.89:1001/Images/log.png","cp":"指针旅游出品"},{"bid":"93","uid":"6","title":"澳门","type":"自驾","city":"澳门","spot":"澳门滑梯","b_pic":"/Uploads/15012288106688.png","y_pic":"/Uploads/15012288107753.png","is_order":"1","is_ad":"1","is_index":"1","look_num":"120","zan_num":"2","ord_num":"0","status":"1","nickname":"佚名","phone":"18222396725","pic":"http://139.196.106.89:1001/Images/log.png","cp":"指针旅游出品"},{"bid":"91","uid":"5","title":"天津18:37","type":"自驾","city":"天津","spot":"天津水上公园","b_pic":"/Uploads/15011519269024.png","y_pic":"/Uploads/15011519270118.png","is_order":"0","is_ad":"1","is_index":"1","look_num":"219","zan_num":"0","ord_num":"0","status":"1","nickname":"佚名","phone":"13230848209","pic":"http://139.196.106.89:1001/Images/log.png","cp":"指针旅游出品"},{"bid":"92","uid":"5","title":"上海迪士尼","type":"人文","city":"","spot":"上海迪士尼","b_pic":"/Images/no_photo.png","y_pic":"/Images/no_photo_y.png","is_order":"0","is_ad":"1","is_index":"1","look_num":"69","zan_num":"0","ord_num":"0","status":"1","nickname":"佚名","phone":"13230848209","pic":"http://139.196.106.89:1001/Images/log.png","cp":"指针旅游出品"}]
     */

    private int status;
    private int d_status;
    private String msg;
    private List<DataBean> data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getD_status() {
        return d_status;
    }

    public void setD_status(int d_status) {
        this.d_status = d_status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean extends BookBean{

    }
}
