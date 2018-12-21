package yc.pointer.trip.bean;

import java.io.Serializable;
import java.util.List;

import yc.pointer.trip.base.BaseBean;

/**
 * Created by 张继
 * 2018/1/12
 * 18:04
 */

public class FansBean extends BaseBean {


    /**
     * unMsg : {"news":"0","comment":"12","zan":"0","fans":"0"}
     * data : [{"uid":"5474","addtime":"1528251061","pic":"/Uploads/15281145502659.png","nickname":"旅游漫谈","location":"无锡市","att_status":0},{"uid":"5524","addtime":"1528271088","pic":"/Uploads/1528444573118.png","nickname":"芦山在线站长","location":"雅安市","att_status":0},{"uid":"5493","addtime":"1528880327","pic":"/Uploads/15288701998493.png","nickname":"恩铭行摄记","location":"衢州市","att_status":0},{"uid":"3560","addtime":"1528883232","pic":"/Uploads/15282783864363.png","nickname":"玫瑰王国","location":"天津市","att_status":0},{"uid":"4","addtime":"1528972121","pic":"/Uploads/15289721877206.png","nickname":"老骥伏枥","location":"天津市","att_status":0}]
     */

    private UnMsgBean unMsg;
    private List<DataBean> data;
    /**
     * uid : 1
     * phone : 13230848209
     * pic : /Uploads/15159882563416.png
     * nickname : 佚名
     * real_name : 张继
     * sex : 男
     * age : 25
     * address :
     * dpic : /Uploads/images/15290477291211.png
     * dcode : 1226
     * bank_num : 6217000180005646504
     * alipay : 13230848209
     * id_card : 130281199203170712
     * contact : 13230848209
     * is_order : 0
     * book_num : 19
     * col_num : 7
     * is_verify : 3
     * is_mz : 1
     * is_jie : 3
     * card_pic :
     * id_card_pic : /Uploads/15290480543347.png
     * login_type :
     * login_id :
     * location : 天津市
     * sig :
     * att : 2
     * fans : 2
     * is_bd : 1
     * cash : 0.00
     * y_cash : 0.00
     * all_cash : 0.00
     * yj_money : 0.00
     * invitation_code : 111111
     * i_invitation_code : 333333
     * is_sm : 1
     * is_yj : 1
     * is_admin : 1
     * qi_time :
     * z_status : 1
     * m_status : 0
     * a_status : 0
     */

    private String uid;
    private String phone;
    private String pic;
    private String nickname;
    private String real_name;
    private String sex;
    private String age;
    private String address;
    private String dpic;
    private String dcode;
    private String bank_num;
    private String alipay;
    private String id_card;
    private String contact;
    private String is_order;
    private String book_num;
    private String col_num;
    private String is_verify;
    private String is_mz;
    private String is_jie;
    private String card_pic;
    private String id_card_pic;
    private String login_type;
    private String login_id;
    private String location;
    private String sig;
    private String att;
    private String fans;
    private String is_bd;
    private String cash;
    private String y_cash;
    private String all_cash;
    private String yj_money;
    private String invitation_code;
    private String i_invitation_code;
    private String is_sm;
    private String is_yj;
    private String is_admin;
    private String qi_time;
    private String z_status;
    private String m_status;
    private int a_status;
    /**
     * user : {"uid":"1","phone":"13230848209","pic":"/Uploads/15159882563416.png","nickname":"佚名","real_name":"张继","sex":"男","age":"25","address":"","dpic":"/Uploads/images/15290477291211.png","dcode":"1226","bank_num":"6217000180005646504","alipay":"13230848209","id_card":"130281199203170712","contact":"13230848209","is_order":"0","book_num":"19","col_num":"7","is_verify":"3","is_mz":"1","is_jie":"3","card_pic":"","id_card_pic":"/Uploads/15290480543347.png","login_type":"","login_id":"","location":"天津市","sig":"","att":"2","fans":"2","is_bd":"1","cash":"0.00","y_cash":"0.00","all_cash":"0.00","yj_money":"0.00","invitation_code":"111111","i_invitation_code":"333333","is_sm":"1","is_yj":"1","is_admin":"1","qi_time":"","z_status":"1","m_status":"0","a_status":0}
     */

    private UserBean user;

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    /**
     * 用户信息
     */
    public static class UserBean extends SaveMesgBean.DataBean{

    }


    public UnMsgBean getUnMsg() {
        return unMsg;
    }

    public void setUnMsg(UnMsgBean unMsg) {
        this.unMsg = unMsg;
    }

    /**
     * 消息数量通知
     */
    public static class UnMsgBean extends BaseUnMsgBean implements Serializable {

    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    /**
     * 粉丝信息
     */
    public static class DataBean {
        /**
         * uid : 5474
         * addtime : 1528251061
         * pic : /Uploads/15281145502659.png
         * nickname : 旅游漫谈
         * location : 无锡市
         * att_status : 0
         */

        private String uid;
        private String addtime;
        private String pic;
        private String nickname;
        private String location;
        private int att_status;
        private String is_jie;//Vip标识
        private String is_vip;//VIP用户的身份类型  0：不是会员  1：黄金会员 2：白金会员


        public String getIs_vip() {
            return is_vip;
        }

        public void setIs_vip(String is_vip) {
            this.is_vip = is_vip;
        }

        public String getIs_jie() {
            return is_jie;
        }

        public void setIs_jie(String is_jie) {
            this.is_jie = is_jie;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getAddtime() {
            return addtime;
        }

        public void setAddtime(String addtime) {
            this.addtime = addtime;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public int getAtt_status() {
            return att_status;
        }

        public void setAtt_status(int att_status) {
            this.att_status = att_status;
        }
    }


}
