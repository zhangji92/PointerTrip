package yc.pointer.trip.bean;

import yc.pointer.trip.base.BaseBean;

import java.util.List;

/**
 * Create by 张继
 * 2017/8/29
 * 15:08
 */
public class MyTravelBean extends BaseBean {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * maddress : 天津市,天津市,天津市
         * ord_amount : 3
         * money : 960
         * type : 0
         * time : 2017-07
         */

        private String maddress;
        private String ord_amount;
        private String money;
        private String type;
        private String time;

        public String getMaddress() {
            return maddress;
        }

        public void setMaddress(String maddress) {
            this.maddress = maddress;
        }

        public String getOrd_amount() {
            return ord_amount;
        }

        public void setOrd_amount(String ord_amount) {
            this.ord_amount = ord_amount;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}
