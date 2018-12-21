package yc.pointer.trip.bean;

import yc.pointer.trip.base.BaseBean;

/**
 * Create by 张继
 * 2017/8/11
 * 13:59
 */
public class PayOrderBean extends BaseBean {


    /**
     * data : {"money":"80","uid":"5","oid":"162","saddress":"天津市","maddress":"天津市","pic":"/Images/log.png","nickname":"佚名"}
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
         * money : 80
         * uid : 5
         * oid : 162
         * saddress : 天津市
         * maddress : 天津市
         * pic : /Images/log.png
         * nickname : 佚名
         */

        private String money;
        private String uid;
        private String oid;
        private String saddress;
        private String maddress;
        private String pic;
        private String nickname;

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

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

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
    }
}
