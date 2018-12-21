package yc.pointer.trip.bean;

import android.widget.TextView;

import butterknife.BindView;
import yc.pointer.trip.R;

/**
 * Created by moyan on 2018/7/9.
 */

public class NewOrderpreviewMode {

    private String orderPreviewItemTitle;//内容标题

    private String orderContent;//订单要求

    private String price;//价格信息


    private String travelType = "";// 出行类型  1代表小时 0代表天 2代表取消

    private int timeLength=0;//出行时长



    public int getTimeLength() {
        return timeLength;
    }

    public void setTimeLength(int timeLength) {
        this.timeLength = timeLength;
    }

    public String getTravelType() {
        return travelType;
    }

    public void setTravelType(String travelType) {
        this.travelType = travelType;
    }

    public String getOrderPreviewItemTitle() {
        return orderPreviewItemTitle;
    }

    public void setOrderPreviewItemTitle(String orderPreviewItemTitle) {
        this.orderPreviewItemTitle = orderPreviewItemTitle;
    }

    public String getOrderContent() {
        return orderContent;
    }

    public void setOrderContent(String orderContent) {
        this.orderContent = orderContent;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


}
