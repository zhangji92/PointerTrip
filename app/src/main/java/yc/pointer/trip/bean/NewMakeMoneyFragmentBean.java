package yc.pointer.trip.bean;

import yc.pointer.trip.base.BaseBean;

/**
 * Created by 张继
 * 2018/9/19
 * 11:16
 * 公司：
 * 描述：
 */

public class NewMakeMoneyFragmentBean extends BaseBean{
    /**
     * data : {"type":3,"u_all_money":0,"rank":10216,"user_count":10425,"task_count":"7","inv_count":"4535","book_count":"1572","ord_count":"25","inv_url":"/Home/Task/invcode?uid="}
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
         * type : 3
         * u_all_money : 0
         * rank : 10216
         * user_count : 10425
         * task_count : 7
         * inv_count : 4535
         * book_count : 1572
         * ord_count : 25
         * inv_url : /Home/Task/invcode?uid=
         */

        private int type;
        private String u_all_money;
        private int rank;
        private int user_count;
        private String task_count;
        private String inv_count;
        private String book_count;
        private String ord_count;
        private String inv_url;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getU_all_money() {
            return u_all_money;
        }

        public void setU_all_money(String u_all_money) {
            this.u_all_money = u_all_money;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        public int getUser_count() {
            return user_count;
        }

        public void setUser_count(int user_count) {
            this.user_count = user_count;
        }

        public String getTask_count() {
            return task_count;
        }

        public void setTask_count(String task_count) {
            this.task_count = task_count;
        }

        public String getInv_count() {
            return inv_count;
        }

        public void setInv_count(String inv_count) {
            this.inv_count = inv_count;
        }

        public String getBook_count() {
            return book_count;
        }

        public void setBook_count(String book_count) {
            this.book_count = book_count;
        }

        public String getOrd_count() {
            return ord_count;
        }

        public void setOrd_count(String ord_count) {
            this.ord_count = ord_count;
        }

        public String getInv_url() {
            return inv_url;
        }

        public void setInv_url(String inv_url) {
            this.inv_url = inv_url;
        }
    }


    /**
     * data : {"type":3,"u_all_money":"9.80","rank":10205,"user_count":10383,"task_count":"2","inv_count":"4534","book_count":"1569","ord_count":"25"}
     */

//    private DataBean data;
//
//    public DataBean getData() {
//        return data;
//    }
//
//    public void setData(DataBean data) {
//        this.data = data;
//    }
//
//    public static class DataBean {
//        /**
//         * type : 3
//         * u_all_money : 9.80
//         * rank : 10205
//         * user_count : 10383
//         * task_count : 2
//         * inv_count : 4534
//         * book_count : 1569
//         * ord_count : 25
//         */
//
//        private int type;
//        private String u_all_money;
//        private int rank;
//        private int user_count;
//        private String task_count;
//        private String inv_count;
//        private String book_count;
//        private String ord_count;
//
//        public int getType() {
//            return type;
//        }
//
//        public void setType(int type) {
//            this.type = type;
//        }
//
//        public String getU_all_money() {
//            return u_all_money;
//        }
//
//        public void setU_all_money(String u_all_money) {
//            this.u_all_money = u_all_money;
//        }
//
//        public int getRank() {
//            return rank;
//        }
//
//        public void setRank(int rank) {
//            this.rank = rank;
//        }
//
//        public int getUser_count() {
//            return user_count;
//        }
//
//        public void setUser_count(int user_count) {
//            this.user_count = user_count;
//        }
//
//        public String getTask_count() {
//            return task_count;
//        }
//
//        public void setTask_count(String task_count) {
//            this.task_count = task_count;
//        }
//
//        public String getInv_count() {
//            return inv_count;
//        }
//
//        public void setInv_count(String inv_count) {
//            this.inv_count = inv_count;
//        }
//
//        public String getBook_count() {
//            return book_count;
//        }
//
//        public void setBook_count(String book_count) {
//            this.book_count = book_count;
//        }
//
//        public String getOrd_count() {
//            return ord_count;
//        }
//
//        public void setOrd_count(String ord_count) {
//            this.ord_count = ord_count;
//        }
//    }




}
