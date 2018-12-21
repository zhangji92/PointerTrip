package yc.pointer.trip.bean;

import java.io.Serializable;
import java.util.List;

import yc.pointer.trip.base.BaseBean;

/**
 * Created by 张继
 * 2018/1/4
 * 15:52
 */

public class PersonalPageBean  extends BaseBean{


    /**
     * data : [{"bid":"1339","uid":"1","title":"蓝月谷","type":"深度","city":"","spot":"","b_pic":"/Uploads/15137600068251.png","y_pic":"/Uploads/15137600068807.png","is_order":"0","is_ad":"0","is_index":"1","look_num":"2","zan_num":"0","ord_num":"0","status":"1","info":"","is_cao":"0","nickname":"佚名","phone":"13230848209","pic":"/Uploads/15058755422507.png","cp":"指针出品","video":"/Video/3290_15137600065995.mp4","col_num":"0","width":"540","height":"960","is_video":"1","location":"","is_del":"0","addtime":"2017-12-20","addtime1":"1513759977"},{"bid":"1331","uid":"1","title":"沪沽湖","type":"自驾","city":"","spot":"","b_pic":"/Uploads/15137572881826.png","y_pic":"/Uploads/15137572882295.png","is_order":"0","is_ad":"0","is_index":"1","look_num":"0","zan_num":"0","ord_num":"0","status":"1","info":"","is_cao":"0","nickname":"佚名","phone":"13230848209","pic":"/Uploads/15058755422507.png","cp":"指针出品","video":"/Video/1_15137572879834.mp4","col_num":"0","width":"540","height":"960","is_video":"1","location":"","is_del":"0","addtime":"2017-12-20","addtime1":"1513757173"},{"bid":"1316","uid":"1","title":"新疆","type":"深度","city":"","spot":"","b_pic":"/Uploads/15137485866672.png","y_pic":"/Uploads/1513748586715.png","is_order":"0","is_ad":"0","is_index":"1","look_num":"1","zan_num":"0","ord_num":"0","status":"1","info":"","is_cao":"0","nickname":"佚名","phone":"13230848209","pic":"/Uploads/15058755422507.png","cp":"指针出品","video":"/Video/8_15137485864455.mp4","col_num":"0","width":"544","height":"960","is_video":"1","location":"","is_del":"0","addtime":"2017-12-20","addtime1":"1513748526"},{"bid":"1205","uid":"1","title":"印度尼西亚","type":"深度","city":"","spot":"","b_pic":"/Uploads/15132256619064.png","y_pic":"/Uploads/15132256619474.png","is_order":"0","is_ad":"0","is_index":"1","look_num":"3","zan_num":"0","ord_num":"0","status":"1","info":"","is_cao":"0","nickname":"佚名","phone":"13230848209","pic":"/Uploads/15058755422507.png","cp":"指针出品","video":"/Video/3290_15132256616867.mp4","col_num":"0","width":"700","height":"946","is_video":"1","location":"","is_del":"0","addtime":"2017-12-14","addtime1":"1513225615"}]
     * user : {"uid":"1","phone":"13230848209","pic":"/Uploads/15058755422507.png","nickname":"佚名","real_name":"张继","sex":"男","age":"25","address":"","dpic":"","dcode":"","bank_num":"6217000180005646504","alipay":"","id_card":"13028119920317****","contact":"13230848209","is_order":"1","book_num":"47","col_num":"4","is_verify":"0","is_mz":"1","is_jie":"0","card_pic":"","id_card_pic":"","login_type":"","login_id":"","location":"","sig":"不一样的人生","att":"0","fans":"0","z_status":0,"m_status":0,"a_status":0}
     */

    private UserBean user;
    private List<DataBean> data;

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class UserBean extends SaveMesgBean.DataBean implements Serializable {


        /**
         * uid : 1
         * phone : 13230848209
         * pic : /Uploads/15058755422507.png
         * nickname : 佚名
         * real_name : 张继
         * sex : 男
         * age : 25
         * address :
         * dpic :
         * dcode :
         * bank_num : 6217000180005646504
         * alipay :
         * id_card : 13028119920317****
         * contact : 13230848209
         * is_order : 1
         * book_num : 48
         * col_num : 4
         * is_verify : 0
         * is_mz : 1
         * is_jie : 0
         * card_pic :
         * id_card_pic :
         * login_type :
         * login_id :
         * location :
         * sig : 不一样的人生
         * att : 0
         * fans : 0
         * is_bd : 1
         * z_status : 0
         * m_status : 0
         * a_status : 0
         * c_status : 0
         */

    }

    public static class DataBean extends BookBean{

        /**
         * is_hb : 0
         */

        private String is_hb;

        public String getIs_hb() {
            return is_hb;
        }

        public void setIs_hb(String is_hb) {
            this.is_hb = is_hb;
        }
    }
}
