package yc.pointer.trip.bean;

import yc.pointer.trip.base.BaseBean;

/**
 * Create by 张继
 * 2017/9/5
 * 13:38
 */
public class ContinuePlayBean extends BaseBean {

    /**
     * data : {"send_time":"1504627200","time_way":"2","time_num":"1","is_dao":"0","quid":"6","end_time":"2017-09-07 00:00:00","price":"30","phone":"18222396725","pic":"/Uploads/15041904564067.png","nickname":"轩仔"}
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
         * send_time : 1504627200
         * time_way : 2
         * time_num : 1
         * is_dao : 0
         * quid : 6
         * end_time : 2017-09-07 00:00:00
         * price : 30
         * phone : 18222396725
         * pic : /Uploads/15041904564067.png
         * nickname : 轩仔
         */

        private String send_time;
        private String time_way;
        private String time_num;
        private String is_dao;
        private String quid;
        private String end_time;
        private String price;
        private String phone;
        private String pic;
        private String nickname;

        public String getSend_time() {
            return send_time;
        }

        public void setSend_time(String send_time) {
            this.send_time = send_time;
        }

        public String getTime_way() {
            return time_way;
        }

        public void setTime_way(String time_way) {
            this.time_way = time_way;
        }

        public String getTime_num() {
            return time_num;
        }

        public void setTime_num(String time_num) {
            this.time_num = time_num;
        }

        public String getIs_dao() {
            return is_dao;
        }

        public void setIs_dao(String is_dao) {
            this.is_dao = is_dao;
        }

        public String getQuid() {
            return quid;
        }

        public void setQuid(String quid) {
            this.quid = quid;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
    }
}
