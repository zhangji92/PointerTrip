package yc.pointer.trip.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import yc.pointer.trip.base.BaseBean;

/**
 * Created by moyan on 2017/11/21.
 */

public class HomeVideoDataBean extends BaseBean {


    /**
     * data : {"data_ad":[{"bid":"1015","uid":"2","title":"我的Mac","type":"","city":"天津","spot":"","b_pic":"/Uploads/15113213555603.png","y_pic":"/Uploads/15113213556785.png","is_order":"1","is_ad":"1","is_index":"1","look_num":"1","zan_num":"0","ord_num":"0","status":"1","is_cao":"0","nickname":"佚名","phone":"18222396725","pic":"/Uploads/15058754861558.png","cp":"指针出品","video":"/Video/2_15113213554695.mp4","col_num":"0","width":"0","height":"0","is_video":"1","addtime":"2017-11-22","addtime1":"1511321355"}],"data_good":[{"bid":"1015","uid":"2","title":"我的Mac","type":"","city":"天津","spot":"","b_pic":"/Uploads/15113213555603.png","y_pic":"/Uploads/15113213556785.png","is_order":"1","is_ad":"1","is_index":"1","look_num":"1","zan_num":"0","ord_num":"0","status":"1","info":"我是小破哦婆婆哦","is_cao":"0","nickname":"佚名","phone":"18222396725","pic":"/Uploads/15058754861558.png","cp":"指针出品","video":"/Video/2_15113213554695.mp4","col_num":"0","width":"0","height":"0","is_video":"1","addtime":"2017-11-22","addtime1":"1511321355"}],"data_city":[{"bid":"1015","uid":"2","title":"我的Mac","type":"","city":"天津","spot":"","b_pic":"/Uploads/15113213555603.png","y_pic":"/Uploads/15113213556785.png","is_order":"1","is_ad":"1","is_index":"1","look_num":"1","zan_num":"0","ord_num":"0","status":"1","info":"我是小破哦婆婆哦","is_cao":"0","nickname":"佚名","phone":"18222396725","pic":"/Uploads/15058754861558.png","cp":"指针出品","video":"/Video/2_15113213554695.mp4","col_num":"0","width":"0","height":"0","is_video":"1","addtime":"2017-11-22","addtime1":"1511321355"}],"data_ad_index":[{"aid":"3","title":"精彩活动","pic2":""},{"aid":"2","title":"精彩活动","pic2":""}]}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {


//        private String is_hb;
        private List<DataAdBean> data_ad;//banner
        private List<DataGoodBean> data_good;//精选
        private List<DataCityBean> data_city;//同城
        private List<DataAdIndexBean> data_ad_index;//活动+主题
        private Hb hb;

        public Hb getHb() {
            return hb;
        }

        public void setHb(Hb hb) {
            this.hb = hb;
        }
        public List<DataAdBean> getData_ad() {
            return data_ad;
        }

        public void setData_ad(List<DataAdBean> data_ad) {
            this.data_ad = data_ad;
        }

        public List<DataGoodBean> getData_good() {
            return data_good;
        }

        public void setData_good(List<DataGoodBean> data_good) {
            this.data_good = data_good;
        }

        public List<DataCityBean> getData_city() {
            return data_city;
        }

        public void setData_city(List<DataCityBean> data_city) {
            this.data_city = data_city;
        }

        public List<DataAdIndexBean> getData_ad_index() {
            return data_ad_index;
        }

        public void setData_ad_index(List<DataAdIndexBean> data_ad_index) {
            this.data_ad_index = data_ad_index;
        }
//        public String getIs_hb() {
//            return is_hb;
//        }
//
//        public void setIs_hb(String is_hb) {
//            this.is_hb = is_hb;
//        }

        public static class DataAdBean extends BookBean {

        }

        public static class DataGoodBean extends BookBean implements Serializable {
        }

        public static class DataCityBean extends BookBean implements Serializable {

        }

        public static class DataAdIndexBean {
            /**
             * aid : 3
             * title : 精彩活动
             * pic2 :
             */
            private String aid;
            private String title;
            private String pic;

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

            public String getPic() {
                return pic;
            }

            public void setPic(String pic) {
                this.pic = pic;
            }
        }

        public  static class Hb{

            private String is_hb;
            private String pic;
            private String pic1;

            public String getIs_hb() {
                return is_hb;
            }

            public void setIs_hb(String is_hb) {
                this.is_hb = is_hb;
            }

            public String getPic() {
                return pic;
            }

            public void setPic(String pic) {
                this.pic = pic;
            }

            public String getPic1() {
                return pic1;
            }

            public void setPic1(String pic1) {
                this.pic1 = pic1;
            }
        }
    }
}
