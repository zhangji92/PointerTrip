package yc.pointer.trip.bean;

import java.io.Serializable;
import java.util.List;

import yc.pointer.trip.base.BaseBean;

/**
 * Created by moyan on 2018/3/28.
 */

public class CommentsListBean extends BaseBean {


    private List<ListBean> data;

    public List<ListBean> getData() {
        return data;
    }

    public void setData(List<ListBean> data) {
        this.data = data;
    }

    public static class ListBean {
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
        private String is_vip;
        private List<ChildBean> child;

        public String getIs_vip() {
            return is_vip;
        }

        public void setIs_vip(String is_vip) {
            this.is_vip = is_vip;
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


        public List<ChildBean> getChildBeanList() {
            return child;
        }

        public void setChildBeanList(List<ChildBean> childBeanList) {
            this.child = childBeanList;
        }

        public static class ChildBean {
            private String cid;
            private String bid;
            private String c_nickname;
            private String b_nickname;
            private String c_u_pic;
            private String addtime;
            private String c_info;


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

            public String getB_nickname() {
                return b_nickname;
            }

            public void setB_nickname(String b_nickname) {
                this.b_nickname = b_nickname;
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
        }
    }
}
