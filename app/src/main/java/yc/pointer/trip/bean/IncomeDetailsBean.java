package yc.pointer.trip.bean;

import java.util.List;

import yc.pointer.trip.base.BaseBean;

/**
 * Created by moyan on 2018/4/25.
 */

public class IncomeDetailsBean extends BaseBean {

    /**
     * earnings : {"u_all_money":"20.00","u_money":"20.00"}
     * data : [{"info":"您的游记：\u201c芽庄延时摄影，这样你们喜欢吗？\u201d播放量2000奖励","addtime":"1524452681","money":"20.00"},{"info":"您的游记：\u201c芽庄延时摄影，这样你们喜欢吗？\u201d播放量1000奖励","addtime":"1524452681","money":"10.00"}]
     */

    private EarningsBean earnings;
    private List<DataBean> data;

    public EarningsBean getEarnings() {
        return earnings;
    }

    public void setEarnings(EarningsBean earnings) {
        this.earnings = earnings;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class EarningsBean {
        /**
         * u_all_money : 20.00
         * u_money : 20.00
         */

        private String u_all_money;
        private String u_money;


        public String getU_all_money() {
            return u_all_money;
        }

        public void setU_all_money(String u_all_money) {
            this.u_all_money = u_all_money;
        }

        public String getU_money() {
            return u_money;
        }

        public void setU_money(String u_money) {
            this.u_money = u_money;
        }
    }

    public static class DataBean {
        /**
         * info : 您的游记：“芽庄延时摄影，这样你们喜欢吗？”播放量2000奖励
         * addtime : 1524452681
         * money : 20.00
         */

        private String info;
        private String addtime;
        private String money;
        private String is_tx;//0:+   1:-


        public String getIs_tx() {
            return is_tx;
        }

        public void setIs_tx(String is_tx) {
            this.is_tx = is_tx;
        }


        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public String getAddtime() {
            return addtime;
        }

        public void setAddtime(String addtime) {
            this.addtime = addtime;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }
    }
}
