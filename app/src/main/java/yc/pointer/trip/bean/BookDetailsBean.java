package yc.pointer.trip.bean;

import yc.pointer.trip.base.BaseBean;

/**
 * Create By 张继
 * 2017/7/26 0026
 * 下午 6:59
 */
public class BookDetailsBean extends BaseBean {


    /**
     * data : {"bid":"2816","is_order":"0","title":"云南昆明","pic":"/Uploads/15163366526307.png","info":"","buid":"3364","zan_num":"3","look_num":"25","ord_num":"0","col_num":"1","c_status":0,"z_status":0,"url":"https://www.zhizhentrip.com/Home/Index/bookDetails?bid=2816","new_url":"https://www.zhizhentrip.com/Home/Book/bookDetails?bid=2816"}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * bid : 2816
         * is_order : 0
         * title : 云南昆明
         * pic : /Uploads/15163366526307.png
         * info :
         * buid : 3364
         * zan_num : 3
         * look_num : 25
         * ord_num : 0
         * col_num : 1
         * c_status : 0
         * z_status : 0
         * url : https://www.zhizhentrip.com/Home/Index/bookDetails?bid=2816
         * new_url : https://www.zhizhentrip.com/Home/Book/bookDetails?bid=2816
         */

        private String bid;
        private String is_order;
        private String title;
        private String pic;
        private String info;
        private String buid;
        private String zan_num;
        private String look_num;
        private String ord_num;
        private String col_num;
        private String c_num;//评论数
        private int c_status;
        private int z_status;
        private String url;
        private String new_url;
        private String is_vip;
        private String u_pic;

        public String getU_pic() {
            return u_pic;
        }

        public void setU_pic(String u_pic) {
            this.u_pic = u_pic;
        }

        public String getIs_vip() {
            return is_vip;
        }

        public void setIs_vip(String is_vip) {
            this.is_vip = is_vip;
        }
        public String getC_num() {
            return c_num;
        }

        public void setC_num(String c_num) {
            this.c_num = c_num;
        }

        public String getBid() {
            return bid;
        }

        public void setBid(String bid) {
            this.bid = bid;
        }

        public String getIs_order() {
            return is_order;
        }

        public void setIs_order(String is_order) {
            this.is_order = is_order;
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

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public String getBuid() {
            return buid;
        }

        public void setBuid(String buid) {
            this.buid = buid;
        }

        public String getZan_num() {
            return zan_num;
        }

        public void setZan_num(String zan_num) {
            this.zan_num = zan_num;
        }

        public String getLook_num() {
            return look_num;
        }

        public void setLook_num(String look_num) {
            this.look_num = look_num;
        }

        public String getOrd_num() {
            return ord_num;
        }

        public void setOrd_num(String ord_num) {
            this.ord_num = ord_num;
        }

        public String getCol_num() {
            return col_num;
        }

        public void setCol_num(String col_num) {
            this.col_num = col_num;
        }

        public int getC_status() {
            return c_status;
        }

        public void setC_status(int c_status) {
            this.c_status = c_status;
        }

        public int getZ_status() {
            return z_status;
        }

        public void setZ_status(int z_status) {
            this.z_status = z_status;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getNew_url() {
            return new_url;
        }

        public void setNew_url(String new_url) {
            this.new_url = new_url;
        }
    }
}
