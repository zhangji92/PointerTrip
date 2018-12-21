package yc.pointer.trip.bean;

import java.io.Serializable;
import java.util.List;

import yc.pointer.trip.base.BaseBean;

/**
 * Created by moyan on 2018/3/28.
 */

public class CommentMessageBean extends BaseBean implements Serializable{




    private CommentDataBean data;

    public CommentDataBean getData() {
        return data;
    }

    public void setData(CommentDataBean data) {
        this.data = data;
    }

    public static class CommentDataBean {

        /**
         * cid : 5
         * bid : 1280
         * nickname : 小七
         * pic : /Uploads/15174813996338.png
         * addtime : 1522223458
         * info : 美丽的地方
         * child : []
         */

        private String cid;
        private String bid;
        private String c_nickname;
        private String c_u_pic;
        private String addtime;
        private String c_info;
        private List<CommentsListBean.ListBean.ChildBean> child;
        private String c_num;

        public String getC_num() {
            return c_num;
        }

        public void setC_num(String c_num) {
            this.c_num = c_num;
        }

        public String getCid() {
            return cid;
        }

        public void setCid(String cid) {
            this.cid = cid;
        }

        public String getBid() {
            return bid;
        }

        public void setBid(String bid) {
            this.bid = bid;
        }

        public String getNickname() {
            return c_nickname;
        }

        public void setNickname(String c_nickname) {
            this.c_nickname = c_nickname;
        }

        public String getPic() {
            return c_u_pic;
        }

        public void setPic(String c_u_pic) {
            this.c_u_pic = c_u_pic;
        }

        public String getAddtime() {
            return addtime;
        }

        public void setAddtime(String addtime) {
            this.addtime = addtime;
        }

        public String getInfo() {
            return c_info;
        }

        public void setInfo(String c_info) {
            this.c_info = c_info;
        }


        public List<CommentsListBean.ListBean.ChildBean> getChildBeanList() {
            return child;
        }

        public void setChildBeanList(List<CommentsListBean.ListBean.ChildBean> child) {
            this.child = child;
        }




    }
}
