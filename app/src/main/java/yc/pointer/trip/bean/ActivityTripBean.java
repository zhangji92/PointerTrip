package yc.pointer.trip.bean;

import java.util.List;

import yc.pointer.trip.base.BaseBean;

/**
 * Created by 张继 on 2017/11/22.
 */

public class ActivityTripBean extends BaseBean {

    /**
     * data : {"data_ad":[{"aid":"3","pic":"/Images/ad1.jpg","title":"精彩活动"},{"aid":"2","pic":"/Images/ad1.jpg","title":"精彩活动"}],"data_good":{"aid":"3","title":"精彩活动","brief":"","addtime":"2017-11-22 11:19:37"},"data_all":[{"aid":"3","pic":"/Images/ad1.jpg","info":[{"b_pic":"/Images/banner1.jpg","title":""},{"b_pic":"/Uploads/Project/image/20171110/20171110154343_77796.jpg","title":""},{"b_pic":"/Images/banner3.jpg","title":""},{"b_pic":"/Images/banner4.jpg","title":""}]},{"aid":"2","pic":"/Images/ad1.jpg","info":[{"b_pic":"/Images/banner1.jpg","title":""},{"b_pic":"/Uploads/Project/image/20171110/20171110154343_77796.jpg","title":""},{"b_pic":"/Images/banner3.jpg","title":""},{"b_pic":"/Images/banner4.jpg","title":""}]}]}
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
         * data_ad : [{"aid":"3","pic":"/Images/ad1.jpg","title":"精彩活动"},{"aid":"2","pic":"/Images/ad1.jpg","title":"精彩活动"}]
         * data_good : {"aid":"3","title":"精彩活动","brief":"","addtime":"2017-11-22 11:19:37"}
         * data_all : [{"aid":"3","pic":"/Images/ad1.jpg","info":[{"b_pic":"/Images/banner1.jpg","title":""},{"b_pic":"/Uploads/Project/image/20171110/20171110154343_77796.jpg","title":""},{"b_pic":"/Images/banner3.jpg","title":""},{"b_pic":"/Images/banner4.jpg","title":""}]},{"aid":"2","pic":"/Images/ad1.jpg","info":[{"b_pic":"/Images/banner1.jpg","title":""},{"b_pic":"/Uploads/Project/image/20171110/20171110154343_77796.jpg","title":""},{"b_pic":"/Images/banner3.jpg","title":""},{"b_pic":"/Images/banner4.jpg","title":""}]}]
         */

        private DataGoodBean data_good;
        private List<DataAdBean> data_ad;
        private List<DataAllBean> data_all;

        public DataGoodBean getData_good() {
            return data_good;
        }

        public void setData_good(DataGoodBean data_good) {
            this.data_good = data_good;
        }

        public List<DataAdBean> getData_ad() {
            return data_ad;
        }

        public void setData_ad(List<DataAdBean> data_ad) {
            this.data_ad = data_ad;
        }

        public List<DataAllBean> getData_all() {
            return data_all;
        }

        public void setData_all(List<DataAllBean> data_all) {
            this.data_all = data_all;
        }

        public static class DataGoodBean {
            /**
             * aid : 3
             * title : 精彩活动
             * brief :
             * addtime : 2017-11-22 11:19:37
             */

            private String aid;
            private String title;
            private String brief;
            private String addtime;

            public String getAid() {
                return aid;
            }

            public void setAid(String aid) {
                this.aid = aid;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getBrief() {
                return brief;
            }

            public void setBrief(String brief) {
                this.brief = brief;
            }

            public String getAddtime() {
                return addtime;
            }

            public void setAddtime(String addtime) {
                this.addtime = addtime;
            }
        }

        public static class DataAdBean {
            /**
             * aid : 3
             * pic : /Images/ad1.jpg
             * title : 精彩活动
             */

            private String aid;
            private String pic;
            private String title;

            public String getAid() {
                return aid;
            }

            public void setAid(String aid) {
                this.aid = aid;
            }

            public String getPic() {
                return pic;
            }

            public void setPic(String pic) {
                this.pic = pic;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }
        }

        public static class DataAllBean {
            /**
             * aid : 3
             * pic : /Images/ad1.jpg
             * title: 标题
             * info : [{"b_pic":"/Images/banner1.jpg","title":""},{"b_pic":"/Uploads/Project/image/20171110/20171110154343_77796.jpg","title":""},{"b_pic":"/Images/banner3.jpg","title":""},{"b_pic":"/Images/banner4.jpg","title":""}]
             */

            private String aid;
            private String pic;
            private String title;
            private List<InfoBean> info;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }



            public String getAid() {
                return aid;
            }

            public void setAid(String aid) {
                this.aid = aid;
            }

            public String getPic() {
                return pic;
            }

            public void setPic(String pic) {
                this.pic = pic;
            }

            public List<InfoBean> getInfo() {
                return info;
            }

            public void setInfo(List<InfoBean> info) {
                this.info = info;
            }

            public static class InfoBean {
                /**
                 * b_pic : /Images/banner1.jpg
                 * title :
                 */

                private String b_pic;
                private String title;

                public String getB_pic() {
                    return b_pic;
                }

                public void setB_pic(String b_pic) {
                    this.b_pic = b_pic;
                }

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }
            }
        }
    }
}
