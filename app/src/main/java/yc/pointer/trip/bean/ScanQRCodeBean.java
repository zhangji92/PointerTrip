package yc.pointer.trip.bean;

/**
 * Created by moyan on 2018/7/17.
 */

public class ScanQRCodeBean {

    private String uid;//接单人的Uid
    private String oid;//订单id
    private String ord_status;//订单状态


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getOrder_status() {
        return ord_status;
    }

    public void setOrder_status(String order_status) {
        this.ord_status = order_status;
    }


}
