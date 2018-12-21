package yc.pointer.trip.bean;

import com.google.gson.annotations.SerializedName;
import yc.pointer.trip.base.BaseBean;

import java.util.List;

/**
 * Created by 刘佳伟
 * 2017/7/28
 * 18:33
 */
public class BookTypeBean extends BaseBean {

    /**
     * data : {"data_ad":[{"bid":"92","uid":"5","title":"1145","type":"人文","city":"","spot":"","b_pic":"/Images/no_photo.png","y_pic":"/Images/no_photo_y.png","is_order":"0","is_ad":"1","is_index":"1","look_num":"45","zan_num":"0","ord_num":"0","status":"1","nickname":"佚名","phone":"13230848209","pic":"http://139.196.106.89:1001/Images/log.png","cp":"指针旅游出品"}],"data_new":[{"bid":"92","uid":"5","title":"1145","type":"人文","city":"","spot":"","b_pic":"/Images/no_photo.png","y_pic":"/Images/no_photo_y.png","is_order":"0","is_ad":"1","is_index":"1","look_num":"45","zan_num":"0","ord_num":"0","status":"1","nickname":"佚名","phone":"13230848209","pic":"http://139.196.106.89:1001/Images/log.png","cp":"指针旅游出品"}],"data_all":[{"bid":"92","uid":"5","title":"1145","type":"人文","city":"","spot":"","b_pic":"/Images/no_photo.png","y_pic":"/Images/no_photo_y.png","is_order":"0","is_ad":"1","is_index":"1","look_num":"45","zan_num":"0","ord_num":"0","status":"1","nickname":"佚名","phone":"13230848209","pic":"http://139.196.106.89:1001/Images/log.png","cp":"指针旅游出品"}]}
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
        private List<DataNewBean> data_new;
        private List<DataAllBean> data_all;

        public List<DataAdBean> getData_ad() {
            return data_ad;
        }

        public void setData_ad(List<DataAdBean> data_ad) {
            this.data_ad = data_ad;
        }

        public List<DataNewBean> getData_new() {
            return data_new;
        }

        public void setData_new(List<DataNewBean> data_new) {
            this.data_new = data_new;
        }

        public List<DataAllBean> getData_all() {
            return data_all;
        }

        public void setData_all(List<DataAllBean> data_all) {
            this.data_all = data_all;
        }

        public static class DataAdBean {
            /**
             * bid : 92
             * uid : 5
             * title : 1145
             * type : 人文
             * city :
             * spot :
             * b_pic : /Images/no_photo.png
             * y_pic : /Images/no_photo_y.png
             * is_order : 0
             * is_ad : 1
             * is_index : 1
             * look_num : 45
             * zan_num : 0
             * ord_num : 0
             * status : 1
             * nickname : 佚名
             * phone : 13230848209
             * pic : http://139.196.106.89:1001/Images/log.png
             * cp : 指针旅游出品
             */

            private String bid;
            private String uid;
            private String title;
            private String type;
            private String city;
            private String spot;
            private String b_pic;
            private String y_pic;
            private String is_order;
            private String is_ad;
            private String is_index;
            private String look_num;
            private String zan_num;
            private String ord_num;
            @SerializedName("status")
            private String statusX;
            private String nickname;
            private String phone;
            private String pic;
            private String cp;

            public String getBid() {
                return bid;
            }

            public void setBid(String bid) {
                this.bid = bid;
            }

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getSpot() {
                return spot;
            }

            public void setSpot(String spot) {
                this.spot = spot;
            }

            public String getB_pic() {
                return b_pic;
            }

            public void setB_pic(String b_pic) {
                this.b_pic = b_pic;
            }

            public String getY_pic() {
                return y_pic;
            }

            public void setY_pic(String y_pic) {
                this.y_pic = y_pic;
            }

            public String getIs_order() {
                return is_order;
            }

            public void setIs_order(String is_order) {
                this.is_order = is_order;
            }

            public String getIs_ad() {
                return is_ad;
            }

            public void setIs_ad(String is_ad) {
                this.is_ad = is_ad;
            }

            public String getIs_index() {
                return is_index;
            }

            public void setIs_index(String is_index) {
                this.is_index = is_index;
            }

            public String getLook_num() {
                return look_num;
            }

            public void setLook_num(String look_num) {
                this.look_num = look_num;
            }

            public String getZan_num() {
                return zan_num;
            }

            public void setZan_num(String zan_num) {
                this.zan_num = zan_num;
            }

            public String getOrd_num() {
                return ord_num;
            }

            public void setOrd_num(String ord_num) {
                this.ord_num = ord_num;
            }

            public String getStatusX() {
                return statusX;
            }

            public void setStatusX(String statusX) {
                this.statusX = statusX;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getPic() {
                return pic;
            }

            public void setPic(String pic) {
                this.pic = pic;
            }

            public String getCp() {
                return cp;
            }

            public void setCp(String cp) {
                this.cp = cp;
            }
        }

        public static class DataNewBean {
            /**
             * bid : 92
             * uid : 5
             * title : 1145
             * type : 人文
             * city :
             * spot :
             * b_pic : /Images/no_photo.png
             * y_pic : /Images/no_photo_y.png
             * is_order : 0
             * is_ad : 1
             * is_index : 1
             * look_num : 45
             * zan_num : 0
             * ord_num : 0
             * status : 1
             * nickname : 佚名
             * phone : 13230848209
             * pic : http://139.196.106.89:1001/Images/log.png
             * cp : 指针旅游出品
             */

            private String bid;
            private String uid;
            private String title;
            private String type;
            private String city;
            private String spot;
            private String b_pic;
            private String y_pic;
            private String is_order;
            private String is_ad;
            private String is_index;
            private String look_num;
            private String zan_num;
            private String ord_num;
            @SerializedName("status")
            private String statusX;
            private String nickname;
            private String phone;
            private String pic;
            private String cp;

            public String getBid() {
                return bid;
            }

            public void setBid(String bid) {
                this.bid = bid;
            }

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getSpot() {
                return spot;
            }

            public void setSpot(String spot) {
                this.spot = spot;
            }

            public String getB_pic() {
                return b_pic;
            }

            public void setB_pic(String b_pic) {
                this.b_pic = b_pic;
            }

            public String getY_pic() {
                return y_pic;
            }

            public void setY_pic(String y_pic) {
                this.y_pic = y_pic;
            }

            public String getIs_order() {
                return is_order;
            }

            public void setIs_order(String is_order) {
                this.is_order = is_order;
            }

            public String getIs_ad() {
                return is_ad;
            }

            public void setIs_ad(String is_ad) {
                this.is_ad = is_ad;
            }

            public String getIs_index() {
                return is_index;
            }

            public void setIs_index(String is_index) {
                this.is_index = is_index;
            }

            public String getLook_num() {
                return look_num;
            }

            public void setLook_num(String look_num) {
                this.look_num = look_num;
            }

            public String getZan_num() {
                return zan_num;
            }

            public void setZan_num(String zan_num) {
                this.zan_num = zan_num;
            }

            public String getOrd_num() {
                return ord_num;
            }

            public void setOrd_num(String ord_num) {
                this.ord_num = ord_num;
            }

            public String getStatusX() {
                return statusX;
            }

            public void setStatusX(String statusX) {
                this.statusX = statusX;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getPic() {
                return pic;
            }

            public void setPic(String pic) {
                this.pic = pic;
            }

            public String getCp() {
                return cp;
            }

            public void setCp(String cp) {
                this.cp = cp;
            }
        }

        public static class DataAllBean {
            /**
             * bid : 92
             * uid : 5
             * title : 1145
             * type : 人文
             * city :
             * spot :
             * b_pic : /Images/no_photo.png
             * y_pic : /Images/no_photo_y.png
             * is_order : 0
             * is_ad : 1
             * is_index : 1
             * look_num : 45
             * zan_num : 0
             * ord_num : 0
             * status : 1
             * nickname : 佚名
             * phone : 13230848209
             * pic : http://139.196.106.89:1001/Images/log.png
             * cp : 指针旅游出品
             */

            private String bid;
            private String uid;
            private String title;
            private String type;
            private String city;
            private String spot;
            private String b_pic;
            private String y_pic;
            private String is_order;
            private String is_ad;
            private String is_index;
            private String look_num;
            private String zan_num;
            private String ord_num;
            @SerializedName("status")
            private String statusX;
            private String nickname;
            private String phone;
            private String pic;
            private String cp;

            public String getBid() {
                return bid;
            }

            public void setBid(String bid) {
                this.bid = bid;
            }

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getSpot() {
                return spot;
            }

            public void setSpot(String spot) {
                this.spot = spot;
            }

            public String getB_pic() {
                return b_pic;
            }

            public void setB_pic(String b_pic) {
                this.b_pic = b_pic;
            }

            public String getY_pic() {
                return y_pic;
            }

            public void setY_pic(String y_pic) {
                this.y_pic = y_pic;
            }

            public String getIs_order() {
                return is_order;
            }

            public void setIs_order(String is_order) {
                this.is_order = is_order;
            }

            public String getIs_ad() {
                return is_ad;
            }

            public void setIs_ad(String is_ad) {
                this.is_ad = is_ad;
            }

            public String getIs_index() {
                return is_index;
            }

            public void setIs_index(String is_index) {
                this.is_index = is_index;
            }

            public String getLook_num() {
                return look_num;
            }

            public void setLook_num(String look_num) {
                this.look_num = look_num;
            }

            public String getZan_num() {
                return zan_num;
            }

            public void setZan_num(String zan_num) {
                this.zan_num = zan_num;
            }

            public String getOrd_num() {
                return ord_num;
            }

            public void setOrd_num(String ord_num) {
                this.ord_num = ord_num;
            }

            public String getStatusX() {
                return statusX;
            }

            public void setStatusX(String statusX) {
                this.statusX = statusX;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getPic() {
                return pic;
            }

            public void setPic(String pic) {
                this.pic = pic;
            }

            public String getCp() {
                return cp;
            }

            public void setCp(String cp) {
                this.cp = cp;
            }
        }
    }
}
