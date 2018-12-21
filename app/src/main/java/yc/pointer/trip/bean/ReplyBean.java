package yc.pointer.trip.bean;

import java.io.Serializable;
import java.util.List;

import yc.pointer.trip.base.BaseBean;

/**
 * Created by moyan on 2018/4/2.
 */

public class ReplyBean extends BaseBean {


    /**
     * data : {"b_nickname":"摸摸","bid":"2560","c_u_pic":"/Uploads/15174813996338.png","uid":"3","addtime":1522656211,"child":[],"c_nickname":"小七","c_info":"无忧","cid":"176","c_num":"10"}
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
         * b_nickname : 摸摸
         * bid : 2560
         * c_u_pic : /Uploads/15174813996338.png
         * uid : 3
         * addtime : 1522656211
         * child : []
         * c_nickname : 小七
         * c_info : 无忧
         * cid : 176
         * c_num : 10 //评论数
         */

        private String b_nickname;
        private String bid;
        private String c_u_pic;
        private String uid;
        private int addtime;
        private String c_nickname;
        private String c_info;
        private String cid;
        private String c_num;
        private List<?> child;

        public String getB_nickname() {
            return b_nickname;
        }

        public void setB_nickname(String b_nickname) {
            this.b_nickname = b_nickname;
        }

        public String getBid() {
            return bid;
        }

        public void setBid(String bid) {
            this.bid = bid;
        }

        public String getC_u_pic() {
            return c_u_pic;
        }

        public void setC_u_pic(String c_u_pic) {
            this.c_u_pic = c_u_pic;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public int getAddtime() {
            return addtime;
        }

        public void setAddtime(int addtime) {
            this.addtime = addtime;
        }

        public String getC_nickname() {
            return c_nickname;
        }

        public void setC_nickname(String c_nickname) {
            this.c_nickname = c_nickname;
        }

        public String getC_info() {
            return c_info;
        }

        public void setC_info(String c_info) {
            this.c_info = c_info;
        }

        public String getCid() {
            return cid;
        }

        public void setCid(String cid) {
            this.cid = cid;
        }

        public String getC_num() {
            return c_num;
        }

        public void setC_num(String c_num) {
            this.c_num = c_num;
        }

        public List<?> getChild() {
            return child;
        }

        public void setChild(List<?> child) {
            this.child = child;
        }
    }
}
