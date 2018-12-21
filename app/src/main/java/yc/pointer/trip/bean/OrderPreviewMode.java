package yc.pointer.trip.bean;

/**
 * Created by 张继
 * 2017/7/10
 * 15:51
 */
public class OrderPreviewMode {
    /** 标题 */
    private String orderTitle;
    /** 内容 */
    private String orderContent;
    /**价钱*/
    private String orderPic;



    public OrderPreviewMode() {
    }


    public OrderPreviewMode(String orderTitle, String orderContent, String orderPic) {
        this.orderTitle = orderTitle;
        this.orderContent = orderContent;
        this.orderPic = orderPic;
    }

    public String getOrderTitle() {
        return orderTitle;
    }

    public void setOrderTitle(String orderTitle) {
        this.orderTitle = orderTitle;
    }

    public String getOrderContent() {
        return orderContent;
    }

    public void setOrderContent(String orderContent) {
        this.orderContent = orderContent;
    }

    public String getOrderPic() {
        return orderPic;
    }

    public void setOrderPic(String orderPic) {
        this.orderPic = orderPic;
    }
}
