package yc.pointer.trip.bean;

import com.google.gson.annotations.SerializedName;

import yc.pointer.trip.base.BaseBean;

/**
 * Created by moyan on 2017/9/12.
 */
public class AdvertBean extends BaseBean{


    /**
     * data : {"aid":"2","title":"精彩活动","pic":"/Images/ad1.jpg","url":"","status":"1","width":"1080","height":"1560","is_show":"1","price":"300","k_status":"0","pic1":"/Images/ad2.jpg","pic2":"/Images/ad2.jpg","brief":"","addtime":"1511320777"}
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
         * aid : 2
         * title : 精彩活动
         * pic : /Images/ad1.jpg
         * url :
         * status : 1
         * width : 1080
         * height : 1560
         * is_show : 1
         * price : 300
         * k_status : 0
         * pic1 : /Images/ad2.jpg
         * pic2 : /Images/ad2.jpg
         * brief :
         * addtime : 1511320777
         */

        private String aid;
        private String title;
        private String pic;
        private String url;
        @SerializedName("status")
        private String statusX;
        private String width;
        private String height;
        private String is_show;
        private String price;
        private String k_status;
        private String pic1;
        private String pic2;
        private String brief;
        private String addtime;

        public String getAid() {
            return aid;
        }

        public void setAid(String aid) {
            this.aid = aid;
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

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getStatusX() {
            return statusX;
        }

        public void setStatusX(String statusX) {
            this.statusX = statusX;
        }

        public String getWidth() {
            return width;
        }

        public void setWidth(String width) {
            this.width = width;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public String getIs_show() {
            return is_show;
        }

        public void setIs_show(String is_show) {
            this.is_show = is_show;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getK_status() {
            return k_status;
        }

        public void setK_status(String k_status) {
            this.k_status = k_status;
        }

        public String getPic1() {
            return pic1;
        }

        public void setPic1(String pic1) {
            this.pic1 = pic1;
        }

        public String getPic2() {
            return pic2;
        }

        public void setPic2(String pic2) {
            this.pic2 = pic2;
        }

        public String getBrief() {
            return brief;
        }

        public void setBrief(String brief) {
            this.brief = brief;
        }

        public String getAddtime() {
            return addtime;
        }

        public void setAddtime(String addtime) {
            this.addtime = addtime;
        }
    }
}
