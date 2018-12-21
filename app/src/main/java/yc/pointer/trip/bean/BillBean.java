package yc.pointer.trip.bean;

import yc.pointer.trip.base.BaseBean;

/**
 * Create by 张继
 * 2017/8/16
 * 18:27
 */
public class BillBean extends BaseBean {


    /**
     * data : {"ord_status":2,"time_way":"2","ord_end":"2017-09-07 18:46:03"}
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
         * ord_status : 2
         * time_way : 2
         * ord_end : 2017-09-07 18:46:03
         */

        private String ord_status;
        private String time_way;
        private String ord_end;

        public String getOrd_status() {
            return ord_status;
        }

        public void setOrd_status(String ord_status) {
            this.ord_status = ord_status;
        }

        public String getTime_way() {
            return time_way;
        }

        public void setTime_way(String time_way) {
            this.time_way = time_way;
        }

        public String getOrd_end() {
            return ord_end;
        }

        public void setOrd_end(String ord_end) {
            this.ord_end = ord_end;
        }
    }
}
