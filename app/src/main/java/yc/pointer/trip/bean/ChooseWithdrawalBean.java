package yc.pointer.trip.bean;

import java.util.List;

import yc.pointer.trip.base.BaseBean;

/**
 * Created by moyan on 2018/4/26.
 */

public class ChooseWithdrawalBean  extends BaseBean{


    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * type : 0
         * money : 3.00
         * remark : 视频收益
         * is_order : 1
         */

        private String type;
        private String u_money;
        private String remark;
        private String is_order;
        private boolean isChoose=false;

        public boolean isChoose() {
            return isChoose;
        }

        public void setChoose(boolean choose) {
            isChoose = choose;
        }



        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getMoney() {
            return u_money;
        }

        public void setMoney(String u_money) {
            this.u_money = u_money;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getIs_order() {
            return is_order;
        }

        public void setIs_order(String is_order) {
            this.is_order = is_order;
        }
    }

}
