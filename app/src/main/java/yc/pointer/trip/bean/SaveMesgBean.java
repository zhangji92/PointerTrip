package yc.pointer.trip.bean;

import yc.pointer.trip.base.BaseBean;

/**
 * Created by 刘佳伟
 * 2017/8/8
 * 16:12
 * 保存个人信息实体类
 */
public class SaveMesgBean extends BaseBean {


    /**
     * info : 绑定手机号可以让更多用户关注您，发布路书通过审核后还能够获取红包奖励哦！
     * data : {"uid":"3286","phone":"18701408576","pic":"/Uploads/1512114388088.png","nickname":"waitting乀","real_name":"","sex":"","age":"0","address":"","dpic":"","dcode":"","bank_num":"","alipay":"","id_card":"","contact":"18622950188","is_order":"0","book_num":"30","col_num":"0","is_verify":"0","is_mz":"0","is_jie":"0","card_pic":"","id_card_pic":"","login_type":"qq","login_id":"073D14D7FE94EB2437AFD503638EC906","location":"天津市","sig":"","att":"0","fans":"3","is_bd":"0","z_status":"1","m_status":"0","a_status":0}
     */

    private String info;
    private DataBean data;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * uid : 3286
         * phone : 18701408576
         * pic : /Uploads/1512114388088.png
         * nickname : waitting乀
         * real_name :
         * sex :
         * age : 0
         * address :
         * dpic :
         * dcode :
         * bank_num :
         * alipay :
         * id_card :
         * contact : 18622950188
         * is_order : 0
         * book_num : 30
         * col_num : 0
         * is_verify : 0
         * is_mz : 0
         * is_jie : 0
         * card_pic :
         * id_card_pic :
         * login_type : qq
         * login_id : 073D14D7FE94EB2437AFD503638EC906
         * location : 天津市
         * sig :
         * att : 0
         * fans : 3
         * is_bd : 0
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
        private String is_mz;//判断是否已读出行须知  1：已读  0：未读
        private String is_jie; // 0：未提交信息  1：提交审核中  2：审核通过  3：审核未通过
        private String card_pic;
        private String id_card_pic;
        private String login_type;
        private String login_id;
        private String location;
        private String sig;
        private String att;
        private String fans;
        private String is_bd;
        private String z_status;
        private String m_status;
        private String a_status;
        private String c_status;//判断是否相互关注
        private String invitation_code;//本人邀请码
        private String i_invitation_code;//已经填写过的邀请码


        private String is_sm = "0";//判断是否已读《实名认证协议》 1：已读   0：未读
        private String is_yj = "0";//判断是否已读《押金协议》 1：已读   0：未读
        private String qi_time;//会员终止日期
        private String head_back_img;//我的页面背景图
        private int msg_num;//判断是否有消息
        private String att_msg_num;//判断是否有关注信息
        private String tags;//认证身份标签
        /**
         * is_vip : 0
         */

        private String is_vip;
        /**
         * yj_money : 0.01
         */

        private String yj_money;

        public String getTags() {
            return tags;
        }

        public void setTags(String tags) {
            this.tags = tags;
        }
        public String getC_status() {
            return c_status;
        }

        public void setC_status(String c_status) {
            this.c_status = c_status;
        }
        public String getHead_back_img() {
            return head_back_img;
        }

        public void setHead_back_img(String head_back_img) {
            this.head_back_img = head_back_img;
        }

        public int getMsg_num() {
            return msg_num;
        }

        public void setMsg_num(int msg_num) {
            this.msg_num = msg_num;
        }

        public String getQi_time() {
            return qi_time;
        }

        public void setQi_time(String qi_time) {
            this.qi_time = qi_time;
        }

        public String getIs_sm() {
            return is_sm;
        }

        public void setIs_sm(String is_sm) {
            this.is_sm = is_sm;
        }

        public String getIs_yj() {
            return is_yj;
        }

        public void setIs_yj(String is_yj) {
            this.is_yj = is_yj;
        }


        public String getInvitation_code() {
            return invitation_code;
        }

        public void setInvitation_code(String invitation_code) {
            this.invitation_code = invitation_code;
        }

        public String getI_invitation_code() {
            return i_invitation_code;
        }

        public void setI_invitation_code(String i_invitation_code) {
            this.i_invitation_code = i_invitation_code;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
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

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getReal_name() {
            return real_name;
        }

        public void setReal_name(String real_name) {
            this.real_name = real_name;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getDpic() {
            return dpic;
        }

        public void setDpic(String dpic) {
            this.dpic = dpic;
        }

        public String getDcode() {
            return dcode;
        }

        public void setDcode(String dcode) {
            this.dcode = dcode;
        }

        public String getBank_num() {
            return bank_num;
        }

        public void setBank_num(String bank_num) {
            this.bank_num = bank_num;
        }

        public String getAlipay() {
            return alipay;
        }

        public void setAlipay(String alipay) {
            this.alipay = alipay;
        }

        public String getId_card() {
            return id_card;
        }

        public void setId_card(String id_card) {
            this.id_card = id_card;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        public String getIs_order() {
            return is_order;
        }

        public void setIs_order(String is_order) {
            this.is_order = is_order;
        }

        public String getBook_num() {
            return book_num;
        }

        public void setBook_num(String book_num) {
            this.book_num = book_num;
        }

        public String getCol_num() {
            return col_num;
        }

        public void setCol_num(String col_num) {
            this.col_num = col_num;
        }

        public String getIs_verify() {
            return is_verify;
        }

        public void setIs_verify(String is_verify) {
            this.is_verify = is_verify;
        }

        public String getIs_mz() {
            return is_mz;
        }

        public void setIs_mz(String is_mz) {
            this.is_mz = is_mz;
        }

        public String getIs_jie() {
            return is_jie;
        }

        public void setIs_jie(String is_jie) {
            this.is_jie = is_jie;
        }

        public String getCard_pic() {
            return card_pic;
        }

        public void setCard_pic(String card_pic) {
            this.card_pic = card_pic;
        }

        public String getId_card_pic() {
            return id_card_pic;
        }

        public void setId_card_pic(String id_card_pic) {
            this.id_card_pic = id_card_pic;
        }

        public String getLogin_type() {
            return login_type;
        }

        public void setLogin_type(String login_type) {
            this.login_type = login_type;
        }

        public String getLogin_id() {
            return login_id;
        }

        public void setLogin_id(String login_id) {
            this.login_id = login_id;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getSig() {
            return sig;
        }

        public void setSig(String sig) {
            this.sig = sig;
        }

        public String getAtt() {
            return att;
        }

        public void setAtt(String att) {
            this.att = att;
        }

        public String getFans() {
            return fans;
        }

        public void setFans(String fans) {
            this.fans = fans;
        }

        public String getIs_bd() {
            return is_bd;
        }

        public void setIs_bd(String is_bd) {
            this.is_bd = is_bd;
        }

        public String getZ_status() {
            return z_status;
        }

        public void setZ_status(String z_status) {
            this.z_status = z_status;
        }

        public String getM_status() {
            return m_status;
        }

        public void setM_status(String m_status) {
            this.m_status = m_status;
        }

        public String getA_status() {
            return a_status;
        }

        public void setA_status(String a_status) {
            this.a_status = a_status;
        }

        public String getIs_vip() {
            return is_vip;
        }

        public void setIs_vip(String is_vip) {
            this.is_vip = is_vip;
        }

        public String getYj_money() {
            return yj_money;
        }

        public void setYj_money(String yj_money) {
            this.yj_money = yj_money;
        }

        public String getAtt_msg_num() {
            return att_msg_num;
        }

        public void setAtt_msg_num(String att_msg_num) {
            this.att_msg_num = att_msg_num;
        }

    }
}
