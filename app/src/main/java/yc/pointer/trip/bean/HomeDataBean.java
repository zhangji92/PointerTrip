package yc.pointer.trip.bean;

import com.google.gson.annotations.SerializedName;
import yc.pointer.trip.base.BaseBean;

import java.util.List;

/**
 * Created by 刘佳伟
 * 2017/7/24
 * 18:21
 */
public class HomeDataBean extends BaseBean {


    /**
     * data : {"data_ad":[{"bid":"265","uid":"2711","title":"天津最精华的街景\u2014\u2014海河","type":"人文","city":"天津","spot":"海河","b_pic":"/Uploads/15077837063325.png","y_pic":"/Uploads/15064161234307.png","is_order":"0","is_ad":"1","is_index":"1","look_num":"200","zan_num":"5","ord_num":"0","status":"1","nickname":"悦 悦 悦","phone":"13821984117","pic":"/Uploads/15078679496652.png","cp":"指针出品"}],"data_good":[{"bid":"217","uid":"4","title":"这样的河南，美极了！","type":"人文","city":"河南","spot":"龙门石窟","b_pic":"/Uploads/Project/image/20170920/20170920164629_84122.png","y_pic":"/Uploads/15058973145137.png","is_order":"0","is_ad":"1","is_index":"1","look_num":"178","zan_num":"7","ord_num":"2","status":"1","nickname":"秦","phone":"15822523867","pic":"/Uploads/1506582900512.png","cp":"指针出品"}],"data_new":[{"bid":"285","uid":"2711","title":"说说四川九寨沟","type":"深度","city":"四川","spot":"九寨沟","b_pic":"/Uploads/1509617422714.png","y_pic":"/Uploads/15096174228458.png","is_order":"0","is_ad":"0","is_index":"1","look_num":"12","zan_num":"0","ord_num":"0","status":"1","nickname":"悦 悦 悦","phone":"13821984117","pic":"/Uploads/15078679496652.png","cp":"指针出品"}],"data_ad_index":[{"b_pic":"/Uploads/15058802989724.png"}]}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<DataAdBean> data_ad;
        private List<DataGoodBean> data_good;
        private List<DataNewBean> data_new;
        private List<DataAdIndexBean> data_ad_index;

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

        public List<DataNewBean> getData_new() {
            return data_new;
        }

        public void setData_new(List<DataNewBean> data_new) {
            this.data_new = data_new;
        }

        public List<DataAdIndexBean> getData_ad_index() {
            return data_ad_index;
        }

        public void setData_ad_index(List<DataAdIndexBean> data_ad_index) {
            this.data_ad_index = data_ad_index;
        }

        public static class DataAdBean extends BookBean{

        }

        public static class DataGoodBean extends BookBean{

        }

        public static class DataNewBean extends BookBean{
            ///**
            // * bid : 285
            // * uid : 2711
            // * title : 说说四川九寨沟
            // * type : 深度
            // * city : 四川
            // * spot : 九寨沟
            // * b_pic : /Uploads/1509617422714.png
            // * y_pic : /Uploads/15096174228458.png
            // * is_order : 0
            // * is_ad : 0
            // * is_index : 1
            // * look_num : 12
            // * zan_num : 0
            // * ord_num : 0
            // * status : 1
            // * nickname : 悦 悦 悦
            // * phone : 13821984117
            // * pic : /Uploads/15078679496652.png
            // * cp : 指针出品
            // */
            //
            //private String bid;
            //private String uid;
            //private String title;
            //private String type;
            //private String city;
            //private String spot;
            //private String b_pic;
            //private String y_pic;
            //private String is_order;
            //private String is_ad;
            //private String is_index;
            //private String look_num;
            //private String zan_num;
            //private String ord_num;
            //@SerializedName("status")
            //private String statusX;
            //private String nickname;
            //private String phone;
            //private String pic;
            //private String cp;
            //
            //public String getBid() {
            //    return bid;
            //}
            //
            //public void setBid(String bid) {
            //    this.bid = bid;
            //}
            //
            //public String getUid() {
            //    return uid;
            //}
            //
            //public void setUid(String uid) {
            //    this.uid = uid;
            //}
            //
            //public String getTitle() {
            //    return title;
            //}
            //
            //public void setTitle(String title) {
            //    this.title = title;
            //}
            //
            //public String getType() {
            //    return type;
            //}
            //
            //public void setType(String type) {
            //    this.type = type;
            //}
            //
            //public String getCity() {
            //    return city;
            //}
            //
            //public void setCity(String city) {
            //    this.city = city;
            //}
            //
            //public String getSpot() {
            //    return spot;
            //}
            //
            //public void setSpot(String spot) {
            //    this.spot = spot;
            //}
            //
            //public String getB_pic() {
            //    return b_pic;
            //}
            //
            //public void setB_pic(String b_pic) {
            //    this.b_pic = b_pic;
            //}
            //
            //public String getY_pic() {
            //    return y_pic;
            //}
            //
            //public void setY_pic(String y_pic) {
            //    this.y_pic = y_pic;
            //}
            //
            //public String getIs_order() {
            //    return is_order;
            //}
            //
            //public void setIs_order(String is_order) {
            //    this.is_order = is_order;
            //}
            //
            //public String getIs_ad() {
            //    return is_ad;
            //}
            //
            //public void setIs_ad(String is_ad) {
            //    this.is_ad = is_ad;
            //}
            //
            //public String getIs_index() {
            //    return is_index;
            //}
            //
            //public void setIs_index(String is_index) {
            //    this.is_index = is_index;
            //}
            //
            //public String getLook_num() {
            //    return look_num;
            //}
            //
            //public void setLook_num(String look_num) {
            //    this.look_num = look_num;
            //}
            //
            //public String getZan_num() {
            //    return zan_num;
            //}
            //
            //public void setZan_num(String zan_num) {
            //    this.zan_num = zan_num;
            //}
            //
            //public String getOrd_num() {
            //    return ord_num;
            //}
            //
            //public void setOrd_num(String ord_num) {
            //    this.ord_num = ord_num;
            //}
            //
            //public String getStatusX() {
            //    return statusX;
            //}
            //
            //public void setStatusX(String statusX) {
            //    this.statusX = statusX;
            //}
            //
            //public String getNickname() {
            //    return nickname;
            //}
            //
            //public void setNickname(String nickname) {
            //    this.nickname = nickname;
            //}
            //
            //public String getPhone() {
            //    return phone;
            //}
            //
            //public void setPhone(String phone) {
            //    this.phone = phone;
            //}
            //
            //public String getPic() {
            //    return pic;
            //}
            //
            //public void setPic(String pic) {
            //    this.pic = pic;
            //}
            //
            //public String getCp() {
            //    return cp;
            //}
            //
            //public void setCp(String cp) {
            //    this.cp = cp;
            //}
        }

        public static class DataAdIndexBean {
            /**
             * b_pic : /Uploads/15058802989724.png
             */

            private String b_pic;

            public String getB_pic() {
                return b_pic;
            }

            public void setB_pic(String b_pic) {
                this.b_pic = b_pic;
            }
        }
    }
}
