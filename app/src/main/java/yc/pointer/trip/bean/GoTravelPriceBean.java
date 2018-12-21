package yc.pointer.trip.bean;

import yc.pointer.trip.base.BaseBean;

/**
 * Created by moyan on 2017/8/10.
 */
public class GoTravelPriceBean extends BaseBean {

    /**
     * data : {"id":"2","wu_h_price":"40","you_h_price":"50","wu_d_price":"200","you_d_price":"250","way_jj":"30","way_jz":"20","info":"asdasd","type":"2","jijie":"7,8,9,10,12,1,2"}
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
         * id : 2
         * wu_h_price : 40
         * you_h_price : 50
         * wu_d_price : 200
         * you_d_price : 250
         * way_jj : 30
         * way_jz : 20
         * info : asdasd
         * type : 2
         * jijie : 7,8,9,10,12,1,2
         */

        private String id;
        private String wu_h_price;
        private String you_h_price;
        private String wu_d_price;
        private String you_d_price;
        private String way_jj;
        private String way_jz;
        private String info;
        private String type;
        private String jijie;
        private String car_price;//用车价格(按小时)
        private String car_d_price;//用车价格（按天）




        public String getCar_d_price() {
            return car_d_price;
        }

        public void setCar_d_price(String car_d_price) {
            this.car_d_price = car_d_price;
        }

        public String getCar_price() {
            return car_price;
        }

        public void setCar_price(String car_price) {
            this.car_price = car_price;
        }


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getWu_h_price() {
            return wu_h_price;
        }

        public void setWu_h_price(String wu_h_price) {
            this.wu_h_price = wu_h_price;
        }

        public String getYou_h_price() {
            return you_h_price;
        }

        public void setYou_h_price(String you_h_price) {
            this.you_h_price = you_h_price;
        }

        public String getWu_d_price() {
            return wu_d_price;
        }

        public void setWu_d_price(String wu_d_price) {
            this.wu_d_price = wu_d_price;
        }

        public String getYou_d_price() {
            return you_d_price;
        }

        public void setYou_d_price(String you_d_price) {
            this.you_d_price = you_d_price;
        }

        public String getWay_jj() {
            return way_jj;
        }

        public void setWay_jj(String way_jj) {
            this.way_jj = way_jj;
        }

        public String getWay_jz() {
            return way_jz;
        }

        public void setWay_jz(String way_jz) {
            this.way_jz = way_jz;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getJijie() {
            return jijie;
        }

        public void setJijie(String jijie) {
            this.jijie = jijie;
        }
    }
}
