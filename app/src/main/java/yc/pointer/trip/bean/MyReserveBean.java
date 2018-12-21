package yc.pointer.trip.bean;

import yc.pointer.trip.base.BaseBean;

import java.util.List;

/**
 * Created by moyan on 2017/9/7.
 */
public class MyReserveBean extends BaseBean {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * oid : 303
         * number : 180702691
         * ord_status : 0
         * nickname : 悦/悦/悦
         * saddress : 天津市
         * maddress : 天津
         * pic : /Uploads/15063280968445.png
         * sex : 女
         * addtime : 1506334022
         * send_time : 2017-09-27
         * money : 40
         * tui_status : 0
         * spot : 渔阳古镇
         * amount : 1
         * time_way : 1
         * time_num : 1
         * money_y : 32.0
         */

        private String oid;
        private String number;
        private String ord_status;
        private String nickname;
        private String saddress;
        private String maddress;
        private String pic;
        private String sex;
        private String addtime;
        private String send_time;
        private String money;
        private String tui_status;
        private String spot;
        private String amount;
        private String time_way;
        private String time_num;
        private String money_y;

        public String getOid() {
            return oid;
        }

        public void setOid(String oid) {
            this.oid = oid;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getOrd_status() {
            return ord_status;
        }

        public void setOrd_status(String ord_status) {
            this.ord_status = ord_status;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getSaddress() {
            return saddress;
        }

        public void setSaddress(String saddress) {
            this.saddress = saddress;
        }

        public String getMaddress() {
            return maddress;
        }

        public void setMaddress(String maddress) {
            this.maddress = maddress;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getAddtime() {
            return addtime;
        }

        public void setAddtime(String addtime) {
            this.addtime = addtime;
        }

        public String getSend_time() {
            return send_time;
        }

        public void setSend_time(String send_time) {
            this.send_time = send_time;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getTui_status() {
            return tui_status;
        }

        public void setTui_status(String tui_status) {
            this.tui_status = tui_status;
        }

        public String getSpot() {
            return spot;
        }

        public void setSpot(String spot) {
            this.spot = spot;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
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

        public String getMoney_y() {
            return money_y;
        }

        public void setMoney_y(String money_y) {
            this.money_y = money_y;
        }
    }
}
