package yc.pointer.trip.bean;

import yc.pointer.trip.base.BaseBean;

/**
 * Created by moyan on 2018/1/12.
 */

public class WithdrawalBean extends BaseBean {

    /**
     * data : {"book_num":"48","cash":"0","y_cash":"0","all_cash":"0"}
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
         * book_num : 48
         * cash : 0
         * y_cash : 0
         * all_cash : 0
         */

        private String book_num;
        private String cash;
        private String y_cash;
        private String all_cash;
        private String tips;

        public String getTips() {
            return tips;
        }

        public void setTips(String tips) {
            this.tips = tips;
        }
        public String getBook_num() {
            return book_num;
        }

        public void setBook_num(String book_num) {
            this.book_num = book_num;
        }

        public String getCash() {
            return cash;
        }

        public void setCash(String cash) {
            this.cash = cash;
        }

        public String getY_cash() {
            return y_cash;
        }

        public void setY_cash(String y_cash) {
            this.y_cash = y_cash;
        }

        public String getAll_cash() {
            return all_cash;
        }

        public void setAll_cash(String all_cash) {
            this.all_cash = all_cash;
        }
    }
}
