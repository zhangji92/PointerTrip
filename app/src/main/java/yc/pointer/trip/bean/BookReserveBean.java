package yc.pointer.trip.bean;

import yc.pointer.trip.base.BaseBean;

/**
 * Created by moyan on 2017/9/5.
 */
public class BookReserveBean extends BaseBean {


    /**
     * data : {"uid":"6","city":"今天","spot":"低低的","is_verify":"0"}
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
         * uid : 6
         * city : 今天
         * spot : 低低的
         * is_verify : 0
         */

        private String uid;
        private String city;
        private String spot;
        private String is_verify;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getSpot() {
            return spot;
        }

        public void setSpot(String spot) {
            this.spot = spot;
        }

        public String getIs_verify() {
            return is_verify;
        }

        public void setIs_verify(String is_verify) {
            this.is_verify = is_verify;
        }
    }
}
